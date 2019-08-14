package pe.edu.lamolina.pivot.controller.matricula.matricular;

import java.util.List;
import pe.edu.lamolina.model.academico.MatriculaCurso;
import pe.edu.lamolina.model.academico.MatriculaResumen;
import pe.edu.lamolina.model.academico.MatriculaSeccion;
import pe.edu.lamolina.model.academico.Seccion;
import pe.edu.lamolina.model.vacantes.VacanteAlumno;

public interface VacanteService {

    void enviarSeccion(Seccion seccion);

    void enviarMatriculaResuemn(MatriculaResumen matri);

    void enviarMatriculaSeccion23(MatriculaSeccion matSecc);

    void enviarMatriculaCurso23(MatriculaCurso matriculaCurso);

    void enviarMatriculaResumen23(MatriculaResumen resumene);

    void enviarVacanteAlumno(List<VacanteAlumno> vacantesAlumnoTemp);

    void enviarMatSeccionEstado(List<MatriculaSeccion> matriculaSeccionMatTemp);

    void enviarMatSeccionEstadoNVAC(List<MatriculaSeccion> matriculaSeccionNvacTemp);

}
