package pe.edu.lamolina.pivot.controller.academico.promedio;

import java.util.List;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.AlumnoCiclo;
import pe.edu.lamolina.model.academico.AlumnoCicloCurso;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Egresado;
import pe.edu.lamolina.model.tramite.Reincorporacion;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

public interface PromedioReviewService {

    void promediarAllCicloAsync(
            Alumno alumno,
            CicloAcademico cicloActivo,
            Egresado egresado,
            List<CicloAcademico> ciclos,
            List<AlumnoCiclo> alumnoCiclos,
            List<AlumnoCicloCurso> allOperativesByModalidadEstudio,
            List<AlumnoCicloCurso> allAlumnoCicloCurso,
            List<Reincorporacion> allReincorporacionesByAlumno,
            DataSessionPivot ds);

}
