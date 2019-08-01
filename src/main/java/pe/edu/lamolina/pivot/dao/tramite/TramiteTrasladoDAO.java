package pe.edu.lamolina.pivot.dao.tramite;

import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.tramite.Resolucion;
import pe.edu.lamolina.model.tramite.TramiteTraslado;

public interface TramiteTrasladoDAO extends EasyDAO<TramiteTraslado> {

    TramiteTraslado findByResolucion(Resolucion resolucionDB);

}
