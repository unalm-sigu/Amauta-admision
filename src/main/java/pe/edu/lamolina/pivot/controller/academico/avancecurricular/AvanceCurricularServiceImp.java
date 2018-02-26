package pe.edu.lamolina.pivot.controller.academico.avancecurricular;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.PlanCurricular;
import pe.edu.lamolina.pivot.dao.academico.AlumnoCicloCursoDAO;
import pe.edu.lamolina.pivot.dao.academico.AlumnoCicloDAO;
import pe.edu.lamolina.pivot.dao.academico.AlumnoCursoCurriculaDAO;
import pe.edu.lamolina.pivot.dao.academico.AlumnoCursoSimultaneoDAO;
import pe.edu.lamolina.pivot.dao.academico.AlumnoDAO;
import pe.edu.lamolina.pivot.dao.academico.CicloAcademicoDAO;
import pe.edu.lamolina.pivot.dao.academico.CursoCurriculaDAO;
import pe.edu.lamolina.pivot.dao.academico.PlanCurricularDAO;
import pe.edu.lamolina.pivot.dao.academico.RequisitoCursoCurriculaDAO;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

@Service
@Transactional(readOnly = true)
public class AvanceCurricularServiceImp implements AvanceCurricularService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    PlanCurricularDAO planCurricularDAO;

    @Autowired
    AlumnoDAO alumnoDAO;

    @Autowired
    AlumnoCicloDAO alumnoCicloDAO;

    @Autowired
    AlumnoCicloCursoDAO alumnoCicloCursoDAO;

    @Autowired
    AlumnoCursoCurriculaDAO alumnoCursoCurriculaDAO;

    @Autowired
    RequisitoCursoCurriculaDAO requisitoCursoCurriculaDAO;

    @Autowired
    CursoCurriculaDAO cursoCurriculaDAO;

    @Autowired
    CicloAcademicoDAO cicloAcademicoDAO;

    @Autowired
    AlumnoCursoSimultaneoDAO alumnoCursoSimultaneoDAO;

    @Autowired
    AvanceCurricularAsincronoService avanceCurricularAsincronoService;

    @Override
    @Transactional
    public void generarAvanceCurricularByPlanCurricular(PlanCurricular planCurricular, DataSessionPivot ds) {
        PlanCurricular planBD = planCurricularDAO.find(planCurricular.getId());
        List<Alumno> alumnos = alumnoDAO.allByPlanCurricular(planBD);

        logger.debug("Alumnos: {}", alumnos.size());
        
        for (Alumno alumno : alumnos) {
            avanceCurricularAsincronoService.deleteAllAlumnoCursoSimultaneoByAlumno(alumno);
        }
        for (Alumno alumno : alumnos) {
            avanceCurricularAsincronoService.procesarAlumno(alumno, ds);
        }
    }

    @Override
    @Transactional
    public void generarAvanceCurricularByAlumno(Alumno alumno, DataSessionPivot ds) {
        alumnoCursoSimultaneoDAO.deleteAllByAlumno(alumno);
        avanceCurricularAsincronoService.procesarAlumno(alumno, ds);
    }

}
