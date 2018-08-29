package pe.edu.lamolina.pivot.controller.academico.encuestaestudiantil.docente;

import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

public interface GeneradorEncuestaDocenteService {

    void generarEncuesta(CicloAcademico cicloAcademico, DataSessionPivot ds);

}
