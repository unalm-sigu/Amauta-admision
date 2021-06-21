package pe.edu.lamolina.amauta.controller.matricula.matriculable;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.lamolina.amauta.controller.bienestar.alumnoAporte.AporteAlumnoService;
import pe.edu.lamolina.amauta.dao.academico.AlumnoDAO;
import pe.edu.lamolina.amauta.dao.academico.CicloAcademicoDAO;
import pe.edu.lamolina.amauta.dao.academico.MatriculaResumenDAO;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.MatriculaResumen;

@Service
@Transactional(readOnly = true)
public class MatriculableLoteServiceImp implements MatriculableLoteService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    CicloAcademicoDAO cicloAcademicoDAO;

    @Autowired
    AlumnoDAO alumnoDAO;

    @Autowired
    MatriculaResumenDAO matriculaResumenDAO;

    @Autowired
    AporteAlumnoService aporteAlumnoService;

    @Override
    public void executeAporteCarnetLote(DataSessionPivot ds) {

        CicloAcademico cicloAcademico = cicloAcademicoDAO.find(ds.getCicloAcademico());

        List<Alumno> alumnos = alumnoDAO.allByCustomQuery(cicloAcademico);

        List<MatriculaResumen> matriculaResumenes = matriculaResumenDAO.allByAlumnosCiclo(alumnos,cicloAcademico);

        for (MatriculaResumen matriculaResumen : matriculaResumenes) {
            try {

                logger.debug("matricula resumen {} {}", matriculaResumen.getId(), matriculaResumen.getEstado());
                aporteAlumnoService.generarAporteCarnet(matriculaResumen.getCicloAcademico(), matriculaResumen, ds);
            } catch (Exception e) {
                logger.debug("error en matricula {} {} ", matriculaResumen.getId(), e.getLocalizedMessage());
            }
        }
    }

    @Override
    public void eliminarAporteCarnetLote(DataSessionPivot ds) {

        CicloAcademico cicloAcademico = cicloAcademicoDAO.find(ds.getCicloAcademico());

        List<Alumno> alumnos = alumnoDAO.allByCustomQuery(cicloAcademico);

        List<MatriculaResumen> matriculaResumenes = matriculaResumenDAO.allByAlumnosCiclo(alumnos,cicloAcademico);

        for (MatriculaResumen matriculaResumen : matriculaResumenes) {
            try {
                logger.debug("matricula resumen {} {}", matriculaResumen.getId(), matriculaResumen.getEstado());
                aporteAlumnoService.quitarAporteCarnet(matriculaResumen.getCicloAcademico(), matriculaResumen, ds);
            } catch (Exception e) {
                logger.debug("error en matricula {} {} ", matriculaResumen.getId(), e.getLocalizedMessage());
            }
        }
    }

}
