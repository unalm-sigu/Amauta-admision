package pe.edu.lamolina.amauta.dao.mensajeria;

import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.social.AsuntoMensaje;

public interface AsuntoMensajeDAO extends EasyDAO<AsuntoMensaje> {

    AsuntoMensaje findByTablaInstancia(String nombreTabla, Long instanciaTabla);

}
