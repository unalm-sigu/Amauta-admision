package pe.edu.lamolina.pivot.controller.academico.encuestaestudiantil.docente;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.encuestaestudiantil.EncuestaDocente;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

public interface EncuestaDocenteService {

    List<EncuestaDocente> allEncuestaDocente(DynatableFilter filter, CicloAcademico ciclo);

    void generarEncuesta(DataSessionPivot ds);

    void cambiarEstadoEncuesta(EncuestaDocente encuesta);

}
