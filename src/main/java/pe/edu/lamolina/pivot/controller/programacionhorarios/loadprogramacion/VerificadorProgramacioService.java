package pe.edu.lamolina.pivot.controller.programacionhorarios.loadprogramacion;

import pe.edu.lamolina.model.academico.MatriculaSeccion;

public interface VerificadorProgramacioService {

    MatriculaSeccion findMatriculaSeccion(long id);

}
