package pe.edu.lamolina.amauta.dao.tramite;

import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.tramite.AutorizacionRegistro;
import pe.edu.lamolina.model.tramite.Tramite;

public interface AutorizacionRegistroDAO extends EasyDAO<AutorizacionRegistro> {

    AutorizacionRegistro findByTramite(Tramite tramite);

    public void updateColumns(AutorizacionRegistro autorizacionRegistro, String... params);

}
