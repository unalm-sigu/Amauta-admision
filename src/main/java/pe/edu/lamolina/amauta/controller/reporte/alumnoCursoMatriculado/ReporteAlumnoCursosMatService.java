package pe.edu.lamolina.amauta.controller.reporte.alumnoCursoMatriculado;

import java.util.List;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;
import pe.edu.lamolina.model.academico.MatriculaSeccion;

public interface ReporteAlumnoCursosMatService {

    List<MatriculaSeccion> downloadReporte(String seccion, DataSessionPivot ds);

    List<AlumnoPersonalizadoDTO> downloadReporteAlumnoPersonalizado();

}
