package pe.edu.lamolina.amauta.controller.academico.encuestaestudiantil.docente;

import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.DocenteSeccion;
import pe.edu.lamolina.model.encuestaestudiantil.EncuestaDocente;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;

public interface GeneradorEncuestaDocenteService {

    void generarEncuesta(CicloAcademico cicloAcademico, DataSessionPivot ds);

    void generarEncuestaDocente(DocenteSeccion docenteSeccion, CicloAcademico ciclo, DataSessionPivot ds);

    void desactivarEncuestaDocente(EncuestaDocente encuestaForm, CicloAcademico ciclo, DataSessionPivot ds);

    void activarEncuestaDocente(EncuestaDocente encuestaBD, CicloAcademico ciclo, DataSessionPivot ds);

}
