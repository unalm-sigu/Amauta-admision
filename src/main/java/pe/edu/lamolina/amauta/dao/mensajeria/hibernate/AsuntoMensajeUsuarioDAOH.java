package pe.edu.lamolina.amauta.dao.mensajeria.hibernate;

import java.util.List;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.amauta.dao.mensajeria.AsuntoMensajeUsuarioDAO;
import pe.edu.lamolina.model.academico.Docente;
import pe.edu.lamolina.model.general.Persona;
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

    @Override
    public List<AsuntoMensajeUsuario> allByAsuntos(List<AsuntoMensaje> asuntos) {
        Octavia sql = Octavia.query()
                .from(AsuntoMensajeUsuario.class, "amu")
                .join("asuntoMensaje am")
                .in("am.id", asuntos);

        return all(sql);
    }

    @Override
    public List<AsuntoMensajeUsuario> allByAsuntosDocente(List<AsuntoMensaje> asuntos, Docente docente) {
        Octavia sql = Octavia.query()
                .from(AsuntoMensajeUsuario.class, "amu")
                .join("asuntoMensaje am", "usuarioMensajeria um", "um.docente doc")
                .filter("doc.id", docente)
                .in("am.id", asuntos);

        return all(sql);
    }

    @Override
    public List<AsuntoMensajeUsuario> allByAsuntosPersona(List<AsuntoMensaje> asuntos, Persona persona) {
        Octavia sql = Octavia.query()
                .from(AsuntoMensajeUsuario.class, "amu")
                .join("asuntoMensaje am", "usuarioMensajeria um", "um.persona per")
                .leftJoin("um.docente", "um.alumno")
                .isNull("um.docente")
                .isNull("um.alumno")
                .filter("per.id", persona)
                .in("am.id", asuntos);

        return all(sql);
    }

}
