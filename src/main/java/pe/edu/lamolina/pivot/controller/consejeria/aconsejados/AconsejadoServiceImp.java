package pe.edu.lamolina.pivot.controller.consejeria.aconsejados;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.Carrera;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.MatriculaResumen;
import pe.edu.lamolina.model.bean.AconsejadoEstadoBean;
import pe.edu.lamolina.model.consejeria.AlumnoConsejero;
import pe.edu.lamolina.model.enums.EstadoMatriculaEnum;
import pe.edu.lamolina.pivot.dao.academico.MatriculaResumenDAO;
import pe.edu.lamolina.pivot.dao.consejeria.AlumnoConsejeroDAO;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

@Service
@Transactional(readOnly = false)
public class AconsejadoServiceImp implements AconsejadoService {

    @Autowired
    AlumnoConsejeroDAO alumnoConsejeroDAO;
    @Autowired
    MatriculaResumenDAO matriculaResumenDAO;

    @Override
    public List<AlumnoConsejero> allAconsejadoByDynatableCarrera(DynatableFilter filter, CicloAcademico cicloAcademico) {

        List<AlumnoConsejero> aconsejadosCarrera = alumnoConsejeroDAO.allByCarrera(filter);
        List<Alumno> alumnos = aconsejadosCarrera.stream().map(x -> x.getAlumno()).collect(Collectors.toList());
        List<MatriculaResumen> matriculaResumen = matriculaResumenDAO.allByAlumnosCiclo(alumnos, cicloAcademico);
        Map<Long, MatriculaResumen> mapMatriculaResumen = TypesUtil.convertListToMap("alumno.id", matriculaResumen);

        for (AlumnoConsejero alumnoTutor : aconsejadosCarrera) {
            MatriculaResumen matResumen = mapMatriculaResumen.get(alumnoTutor.getAlumno().getId());
            if (matResumen != null) {
                alumnoTutor.setEstadoMatriculableEnum(matResumen.getEstadoEnum());
            } else {
                alumnoTutor.setEstadoMatriculableEnum(EstadoMatriculaEnum.INH);
            }
        }
        return aconsejadosCarrera;
    }

    @Override
    @Transactional
    public void updateAlumnoConsejero(AlumnoConsejero alumnoConsejeroForm, DataSessionPivot ds) {
        AlumnoConsejero alumnoConsejero = alumnoConsejeroDAO.find(alumnoConsejeroForm.getId());
        alumnoConsejero.setConsejero(alumnoConsejeroForm.getConsejero());
        alumnoConsejero.setFechaAsigna(new Date());
        alumnoConsejeroDAO.update(alumnoConsejero);
    }

    @Override
    public AconsejadoEstadoBean allByCarrera(Carrera carrera, CicloAcademico cicloAcademico) {
        Long countSinConsejero = matriculaResumenDAO.allSinConsejero(carrera, cicloAcademico);
        Long countActivos = matriculaResumenDAO.allConConsejero(carrera, cicloAcademico);
        Long countConConsejeroNN = matriculaResumenDAO.allConConsejeroNN(carrera, cicloAcademico);

        AconsejadoEstadoBean aconsejadoEstadoBean = new AconsejadoEstadoBean();
        aconsejadoEstadoBean.setActivos(countActivos);
        aconsejadoEstadoBean.setSinConsejero(countConConsejeroNN);
        aconsejadoEstadoBean.setSinAsignar(countSinConsejero);
        return aconsejadoEstadoBean;
    }

}
