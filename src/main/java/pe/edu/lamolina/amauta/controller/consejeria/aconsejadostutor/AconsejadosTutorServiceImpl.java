package pe.edu.lamolina.amauta.controller.consejeria.aconsejadostutor;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.MatriculaResumen;
import pe.edu.lamolina.model.bean.AconsejadoEstadoBean;
import pe.edu.lamolina.model.consejeria.AlumnoConsejero;
import pe.edu.lamolina.model.enums.EstadoMatriculaEnum;
import pe.edu.lamolina.model.general.Persona;
import pe.edu.lamolina.amauta.dao.academico.MatriculaResumenDAO;
import pe.edu.lamolina.amauta.dao.consejeria.AlumnoConsejeroDAO;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;

@Service
@Transactional(readOnly = true)
public class AconsejadosTutorServiceImpl implements AconsejadosTutorService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    AlumnoConsejeroDAO alumnoConsejeroDAO;
    @Autowired
    MatriculaResumenDAO matriculaResumenDAO;

    @Override
    public List<AlumnoConsejero> allByDynatable(DynatableFilter filter, CicloAcademico cicloAcademico, Persona tutor) {
        List<AlumnoConsejero> alumnoConsejeros = alumnoConsejeroDAO.allByPersonaTutor(filter, cicloAcademico, tutor);
        List<Alumno> alumnos = alumnoConsejeros.stream().map(x -> x.getAlumno()).collect(Collectors.toList());
        List<MatriculaResumen> matriculaResumen = matriculaResumenDAO.allByAlumnosCiclo(alumnos, cicloAcademico);
        Map<Long, MatriculaResumen> mapMatriculaResumen = TypesUtil.convertListToMap("alumno.id", matriculaResumen);
        logger.debug("alumno consejero {}", alumnoConsejeros.size());

        for (AlumnoConsejero alumnoTutor : alumnoConsejeros) {
            MatriculaResumen matResumen = mapMatriculaResumen.get(alumnoTutor.getAlumno().getId());
            if (matResumen != null) {
                alumnoTutor.setEstadoMatriculableEnum(matResumen.getEstadoEnum());
                alumnoTutor.setEstadoMatriculaAutorizacion(matResumen.getAutorizacionMatricula());
                alumnoTutor.setCursosMatriculados(matResumen.getCursosMatriculados());
                alumnoTutor.setCreditosMatriculados(matResumen.getCreditosMatriculados());
            } else {
                alumnoTutor.setEstadoMatriculableEnum(EstadoMatriculaEnum.INH);
            }
        }
        return alumnoConsejeros;
    }

    @Override
    public AconsejadoEstadoBean allByPersona(Persona persona, CicloAcademico cicloAcademico) {
        Long countMatriculable = matriculaResumenDAO.countMatriculablesByConsejero(persona, cicloAcademico);
        Long countNoMatriculados = matriculaResumenDAO.countNoMatriculablesByConsejero(persona, cicloAcademico);
        Long countRetiroCiclo = matriculaResumenDAO.countRetiroCicloByConsejero(persona, cicloAcademico);
        AconsejadoEstadoBean aconsejadoEstadoBean = new AconsejadoEstadoBean();
        aconsejadoEstadoBean.setMatriculados(countMatriculable);
        aconsejadoEstadoBean.setNoMatriculados(countNoMatriculados);
        aconsejadoEstadoBean.setRetiroCiclo(countRetiroCiclo);
        return aconsejadoEstadoBean;
    }

    @Override
    @Transactional
    public void matriculaAutorizacion(MatriculaResumen matriculaResumen, DataSessionPivot ds) {
        MatriculaResumen matriculaResumenBD = matriculaResumenDAO.findByAlumnoCiclo(matriculaResumen.getAlumno(), ds.getCicloAcademico());
        matriculaResumenBD.setAutorizacionMatricula(matriculaResumen.getAutorizacionMatricula());
        matriculaResumenBD.setFechaAutorizacionMatricula(new Date());
        matriculaResumenBD.setUserConsejero(ds.getUsuario());
        matriculaResumenDAO.update(matriculaResumenBD);
    }
}
