package pe.edu.lamolina.amauta.controller.consejeria.aconsejadostutor;

import java.util.Arrays;
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
import pe.edu.lamolina.amauta.dao.general.PersonaDAO;
import pe.edu.lamolina.amauta.dao.tramite.TramiteBachillerDAO;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;
import pe.edu.lamolina.model.academico.Carrera;
import pe.edu.lamolina.model.consejeria.Consejero;
import static pe.edu.lamolina.model.constantines.GlobalConstantine.ID_CONSEJERO_NN;
import static pe.edu.lamolina.model.enums.EstadoMatriculaEnum.MAT;
import static pe.edu.lamolina.model.enums.EstadoMatriculaEnum.NMAT;
import pe.edu.lamolina.model.tramite.TramiteBachiller;

@Service
@Transactional(readOnly = true)
public class AconsejadosTutorServiceImpl implements AconsejadosTutorService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    AlumnoConsejeroDAO alumnoConsejeroDAO;
    @Autowired
    MatriculaResumenDAO matriculaResumenDAO;

    @Autowired
    PersonaDAO personaDAO;
    @Autowired
    TramiteBachillerDAO tramiteBachillerDAO;

    @Override
    public List<AlumnoConsejero> allByDynatable(DynatableFilter filter, CicloAcademico cicloAcademico, Persona tutor) {
        List<AlumnoConsejero> alumnoConsejeros = alumnoConsejeroDAO.allByDynatablePersonaTutor(filter, cicloAcademico, tutor);
        List<Alumno> alumnos = alumnoConsejeros.stream().map(x -> x.getAlumno()).collect(Collectors.toList());
        List<MatriculaResumen> matriculaResumen = matriculaResumenDAO.allByAlumnosCiclo(alumnos, cicloAcademico);
        Map<Long, MatriculaResumen> mapMatriculaResumen = TypesUtil.convertListToMap("alumno.id", matriculaResumen);
        logger.debug("alumno consejero {}", alumnoConsejeros.size());

        List<TramiteBachiller> tramBachiller = tramiteBachillerDAO.allByAlumnosAct(alumnos);

        List<Alumno> alumnosConTramBachiller = tramBachiller.stream().map(x -> x.getTramite().getAlumno()).collect(Collectors.toList());

        logger.debug("INICIO {}", alumnoConsejeros.size());
        List<AlumnoConsejero> alumnoConsejerosDepurado = alumnoConsejeros.stream().filter(x -> !alumnosConTramBachiller.contains(x.getAlumno())).collect(Collectors.toList());

        logger.debug("DEPURADO {}", alumnoConsejerosDepurado.size());

        for (AlumnoConsejero alumnoTutor : alumnoConsejerosDepurado) {
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
        return alumnoConsejerosDepurado;
    }

    @Override
    public List<AlumnoConsejero> allByDynatableByCarrera(DynatableFilter filter, CicloAcademico cicloAcademico, Persona tutor, Carrera carrera) {
        List<AlumnoConsejero> alumnoConsejeros = alumnoConsejeroDAO.allByDynatablePersonaTutorCarrera(filter, cicloAcademico, tutor, carrera);
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
        return alumnoConsejeros.stream().filter(x -> Arrays.asList(MAT, NMAT).contains(x.getEstadoMatriculableEnum())).collect(Collectors.toList());
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

    @Override
    public Persona findPersona(Long idPersona) {
        return personaDAO.find(idPersona);
    }

    @Override
    public AconsejadoEstadoBean allByPersonaCarrera(Persona persona, CicloAcademico cicloAcademico, Carrera carrera) {
        DynatableFilter filter = new DynatableFilter();
        filter.setPage(1);
        filter.setOffset(0);
        filter.setPerPage(10000000);

        Long countMatriculable = matriculaResumenDAO.countMatriculablesByConsejeroCarrera(persona, cicloAcademico, carrera);
        Long countNoMatriculados = matriculaResumenDAO.countNoMatriculablesByConsejeroCarrera(persona, cicloAcademico, carrera);
        Long countRetiroCiclo = matriculaResumenDAO.countRetiroCicloByConsejeroCarrera(persona, cicloAcademico, carrera);
        List<AlumnoConsejero> alumnosTutor = this.allByDynatableByCarrera(filter, cicloAcademico, persona, carrera);
        alumnosTutor = alumnosTutor.stream().filter(x -> Arrays.asList(MAT, NMAT).contains(x.getEstadoMatriculableEnum())).collect(Collectors.toList());
        AconsejadoEstadoBean aconsejadoEstadoBean = new AconsejadoEstadoBean();
        aconsejadoEstadoBean.setMatriculados(countMatriculable);
        aconsejadoEstadoBean.setNoMatriculados(countNoMatriculados);
        aconsejadoEstadoBean.setRetiroCiclo(countRetiroCiclo);
        aconsejadoEstadoBean.setAlumnosConsejeros(alumnosTutor);
        return aconsejadoEstadoBean;
    }

    @Override
    @Transactional
    public void eliminarAlumnoConsejero(Long idAlumnoConsejero) {
        alumnoConsejeroDAO.delete(idAlumnoConsejero);
    }

    @Override
    @Transactional
    public void quitarTutor(Long idAlumnoConsejero) {
        AlumnoConsejero alumnoConsejero= alumnoConsejeroDAO.findAll(idAlumnoConsejero);
        alumnoConsejero.setConsejero(new Consejero(ID_CONSEJERO_NN));
        alumnoConsejeroDAO.updateColumns(alumnoConsejero,"consejero");
    }

}
