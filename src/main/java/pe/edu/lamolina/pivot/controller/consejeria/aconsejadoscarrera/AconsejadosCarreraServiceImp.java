package pe.edu.lamolina.pivot.controller.consejeria.aconsejadoscarrera;

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
import pe.edu.lamolina.model.consejeria.AlumnoConsejero;
import pe.edu.lamolina.model.consejeria.ConsejeriaResumen;
import pe.edu.lamolina.model.enums.EstadoMatriculaEnum;
import pe.edu.lamolina.pivot.dao.academico.MatriculaResumenDAO;
import pe.edu.lamolina.pivot.dao.consejeria.AlumnoConsejeroDAO;
import pe.edu.lamolina.pivot.dao.consejeria.ConsejeriaResumenDAO;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

@Service
@Transactional(readOnly = false)
public class AconsejadosCarreraServiceImp implements AconsejadosCarreraService {

    @Autowired
    AlumnoConsejeroDAO alumnoConsejeroDAO;
    @Autowired
    MatriculaResumenDAO matriculaResumenDAO;
    @Autowired
    ConsejeriaResumenDAO consejeriaResumenDAO;

    @Override
    public List<AlumnoConsejero> allAconsejadoByDynatable(Carrera carrera, DynatableFilter filter, CicloAcademico cicloAcademico) {

        List<AlumnoConsejero> aconsejadosCarrera = alumnoConsejeroDAO.allByDynatableCarrera(carrera, filter, cicloAcademico);
        List<Alumno> alumnos = aconsejadosCarrera.stream().map(x -> x.getAlumno()).collect(Collectors.toList());
        List<MatriculaResumen> matriculaResumen = matriculaResumenDAO.allByAlumnosCiclo(alumnos, cicloAcademico);
        Map<Long, MatriculaResumen> mapMatriculaResumen = TypesUtil.convertListToMap("alumno.id", matriculaResumen);

        for (AlumnoConsejero alumnoTutor : aconsejadosCarrera) {
            MatriculaResumen matResumen = mapMatriculaResumen.get(alumnoTutor.getAlumno().getId());
            if (matResumen != null) {
                alumnoTutor.setEstadoMatriculableEnum(matResumen.getEstadoEnum());
                alumnoTutor.setCursosMatriculados(matResumen.getCursosMatriculados());
                alumnoTutor.setCreditosMatriculados(matResumen.getCreditosMatriculados());
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
    public ConsejeriaResumen getResumenByCarreraCiclo(Carrera carrera, CicloAcademico cicloAcademico) {
        ConsejeriaResumen resumen = consejeriaResumenDAO.findByCarreraCiclo(carrera, cicloAcademico);
        resumen = (resumen == null) ? new ConsejeriaResumen() : resumen;
        return resumen;
    }

}
