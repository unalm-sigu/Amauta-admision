package pe.edu.lamolina.pivot.controller.academico.visitante;

import java.util.List;
import pe.edu.lamolina.pivot.model.academico.AlumnoVisitante;
import pe.edu.lamolina.pivot.model.academico.CicloAcademico;
import pe.edu.lamolina.pivot.model.general.TipoDocIdentidad;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

public interface AlumnosVisitanteService {

    List<TipoDocIdentidad> allTiposDocIdentidad();

    void save(AlumnoVisitante alumnoVisitante, DataSessionPivot ds);

    List<CicloAcademico> allCicloAcademico();

}
