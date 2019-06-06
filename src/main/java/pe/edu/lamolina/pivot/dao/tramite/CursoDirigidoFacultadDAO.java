package pe.edu.lamolina.pivot.dao.tramite;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.academico.Facultad;
import pe.edu.lamolina.model.tramite.CursoDirigidoFacultad;

public interface CursoDirigidoFacultadDAO extends EasyDAO<CursoDirigidoFacultad> {

    List<CursoDirigidoFacultad> allByDynatable(DynatableFilter filter);

    List<CursoDirigidoFacultad> allByFacultad(Facultad facultad);

}
