package pe.edu.lamolina.amauta.controller.academico.encuestaestudiantil.curso;

import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;

public interface GeneradorEncuestaCursoService {

    void generarEncuesta(CicloAcademico cicloAcademico, DataSessionPivot ds);

}
