package pe.edu.lamolina.amauta.dao.mensajeria;

import java.util.List;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.social.AsuntoMensaje;
import pe.edu.lamolina.model.social.AsuntoMensajeUsuario;
import pe.edu.lamolina.model.social.UsuarioMensajeria;

public interface AsuntoMensajeUsuarioDAO extends EasyDAO<AsuntoMensajeUsuario> {

    AsuntoMensajeUsuario findByAsuntoUsuario(AsuntoMensaje asunto, UsuarioMensajeria usuario);

    List<AsuntoMensajeUsuario> allByAsuntos(List<AsuntoMensaje> asuntos);

}
