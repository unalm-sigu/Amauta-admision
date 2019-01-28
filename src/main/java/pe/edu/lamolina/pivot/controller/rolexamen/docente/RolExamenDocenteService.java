package pe.edu.lamolina.pivot.controller.rolexamen.docente;

import java.util.List;
import pe.edu.lamolina.model.academico.Docente;
import pe.edu.lamolina.model.bean.RolExamenDocente;
import pe.edu.lamolina.model.rolexamen.FechaHoraGrupoExamen;
import pe.edu.lamolina.model.rolexamen.GrupoHorasExamen;
import pe.edu.lamolina.model.rolexamen.SemanaExamen;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

public interface RolExamenDocenteService {

    public List<RolExamenDocente> listExamenDocente(Docente docente, DataSessionPivot ds);

    List<FechaHoraGrupoExamen> allFechaHoraGrupoExamenBySemanaExamen(SemanaExamen semanaExamen, List<GrupoHorasExamen> grupoHorasExamens);
}
