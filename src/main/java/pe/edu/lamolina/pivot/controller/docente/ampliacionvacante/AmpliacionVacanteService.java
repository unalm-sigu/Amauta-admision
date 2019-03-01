package pe.edu.lamolina.pivot.controller.docente.ampliacionvacante;

import java.util.List;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Docente;
import pe.edu.lamolina.model.academico.GrupoSeccion;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

public interface AmpliacionVacanteService {

    List<GrupoSeccion> allGrupoByDocente(Docente docente, CicloAcademico ciclo, DataSessionPivot ds);

}
