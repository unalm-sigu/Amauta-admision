package pe.edu.lamolina.amauta.controller.academico.promedio;

import java.util.Date;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.AlumnoCiclo;
import pe.edu.lamolina.model.academico.AlumnoCicloCurso;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Egresado;
import pe.edu.lamolina.model.tramite.Reincorporacion;
import pe.edu.lamolina.amauta.controller.academico.alumno.AlumnoService;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;

@Service
@Transactional(readOnly = true)
public class PromedioReviewServiceImp implements PromedioReviewService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    ContadorComponent contadorComponent;
    @Autowired
    PromedioService promedioService;
    @Autowired
    AlumnoService alumnoService;

    @Async
    @Override
    public void promediarAllCicloAsync(
            Alumno alumno,
            CicloAcademico cicloActivo,
            Egresado egresado,
            List<CicloAcademico> ciclos,
            List<AlumnoCiclo> alumnoCiclos,
            List<AlumnoCicloCurso> allOperativesCicloCurso,
            List<AlumnoCicloCurso> allAlumnoCicloCurso,
            List<Reincorporacion> allReincorporacionesByAlumno,
            DataSessionPivot ds) {

        if (ds.getFechaAccionAudit() == null) {
            ds.setFechaAccionAudit(new Date());
        }

        alumno.setConError(Boolean.FALSE);
        alumnoService.marcarFalla(alumno);

        if (alumno.getSituacionAcademica().isIngresanteRenunciante()) {
            contadorComponent.incrementarProcesados();
            return;
        }

        int rpta;
        try {
            rpta = promedioService.promediarAllCicloSync(
                    alumno,
                    cicloActivo,
                    egresado,
                    ciclos,
                    alumnoCiclos,
                    allOperativesCicloCurso,
                    allAlumnoCicloCurso,
                    allReincorporacionesByAlumno, ds, false, false);
        } catch (Exception e) {
            rpta = 0;
        }
        if (rpta == 0) {
            alumno.setConError(Boolean.TRUE);
            alumnoService.marcarFalla(alumno);
        }
        contadorComponent.incrementarProcesados();
    }

}
