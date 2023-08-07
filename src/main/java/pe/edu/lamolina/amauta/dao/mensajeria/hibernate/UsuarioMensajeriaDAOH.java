package pe.edu.lamolina.amauta.dao.mensajeria.hibernate;

import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.amauta.dao.mensajeria.UsuarioMensajeriaDAO;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.Docente;
import pe.edu.lamolina.model.enums.mensajeria.TipoUserMensajeriaEnum;
import pe.edu.lamolina.model.general.Persona;
import pe.edu.lamolina.model.social.UsuarioMensajeria;

@Repository
public class UsuarioMensajeriaDAOH extends AbstractEasyDAO<UsuarioMensajeria> implements UsuarioMensajeriaDAO {

    public UsuarioMensajeriaDAOH() {
        super();
        setClazz(UsuarioMensajeria.class);
    }

    @Override
    public UsuarioMensajeria findByPersonaTipoUser(Persona persona, TipoUserMensajeriaEnum tipoUsuario) {
        Octavia sql = Octavia.query()
                .from(UsuarioMensajeria.class, "um")
                .join("persona per")
                .leftJoin("alumno", "docente")
                .filter("per.id", persona)
                .filter("tipoUsuario", tipoUsuario);

        return find(sql);
    }

    @Override
    public UsuarioMensajeria findByPersonaAlumno(Persona persona, Alumno alumno) {
        Octavia sql = Octavia.query()
                .from(UsuarioMensajeria.class, "um")
                .join("persona per", "alumno alu")
                .leftJoin("docente")
                .filter("per.id", persona)
                .filter("alu.id", alumno);

        return find(sql);
    }

    @Override
    public UsuarioMensajeria findByPersonaDocente(Persona persona, Docente docente) {
        Octavia sql = Octavia.query()
                .from(UsuarioMensajeria.class, "um")
                .join("persona per", "docente doc")
                .leftJoin("alumno")
                .filter("per.id", persona)
                .filter("doc.id", docente);

        return find(sql);
    }
}
