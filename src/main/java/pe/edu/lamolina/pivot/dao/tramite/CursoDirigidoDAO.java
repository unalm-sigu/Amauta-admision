package pe.edu.lamolina.pivot.dao.tramite;

import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.tramite.CursoDirigido;
import pe.edu.lamolina.model.tramite.Tramite;

public interface CursoDirigidoDAO extends EasyDAO<CursoDirigido> {

    CursoDirigido findByTramite(Tramite tramite);

}
