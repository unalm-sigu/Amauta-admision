package pe.edu.lamolina.pivot.controller.academico.promedio;

import pe.edu.lamolina.model.academico.MatriculaCurso;
import pe.edu.lamolina.model.seguridad.Usuario;

public interface PromedioService {

    void promedio(MatriculaCurso matriculaCurso, Usuario usuario);

}
