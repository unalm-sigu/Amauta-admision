package pe.edu.lamolina.pivot.controller.academico.encuestaestudiantil.curso;

import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

public interface GeneradorEncuestaCursoService {

    void generarEncuesta(CicloAcademico cicloAcademico, DataSessionPivot ds);

}
