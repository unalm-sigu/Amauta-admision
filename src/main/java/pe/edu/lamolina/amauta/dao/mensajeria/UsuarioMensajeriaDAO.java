package pe.edu.lamolina.amauta.dao.mensajeria;

import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.Docente;
import pe.edu.lamolina.model.enums.mensajeria.TipoUserMensajeriaEnum;
import pe.edu.lamolina.model.general.Persona;
import pe.edu.lamolina.model.social.UsuarioMensajeria;

public interface UsuarioMensajeriaDAO extends EasyDAO<UsuarioMensajeria> {

    UsuarioMensajeria findByPersonaTipoUser(Persona persona, TipoUserMensajeriaEnum tipoUsuario);

    UsuarioMensajeria findByPersonaAlumno(Persona persona, Alumno alumno);

    UsuarioMensajeria findByPersonaDocente(Persona persona, Docente docente);

}
