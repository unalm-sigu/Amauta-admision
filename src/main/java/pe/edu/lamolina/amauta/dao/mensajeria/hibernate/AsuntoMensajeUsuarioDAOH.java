package pe.edu.lamolina.amauta.dao.mensajeria.hibernate;

import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.amauta.dao.mensajeria.AsuntoMensajeUsuarioDAO;
import pe.edu.lamolina.model.social.AsuntoMensaje;
import pe.edu.lamolina.model.social.AsuntoMensajeUsuario;
import pe.edu.lamolina.model.social.UsuarioMensajeria;

@Repository
public class AsuntoMensajeUsuarioDAOH extends AbstractEasyDAO<AsuntoMensajeUsuario> implements AsuntoMensajeUsuarioDAO {

    public AsuntoMensajeUsuarioDAOH() {
        super();
        setClazz(AsuntoMensajeUsuario.class);
    }

    @Override
    public AsuntoMensajeUsuario findByAsuntoUsuario(AsuntoMensaje asunto, UsuarioMensajeria usuario) {
        Octavia sql = Octavia.query()
                .from(AsuntoMensajeUsuario.class, "amu")
                .join("asuntoMensaje am", "usuarioMensajeria um")
                .filter("am.id", asunto)
                .filter("um.id", usuario);

        return find(sql);
    }
}
