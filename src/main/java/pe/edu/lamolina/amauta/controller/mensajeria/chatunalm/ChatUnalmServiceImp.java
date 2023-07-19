package pe.edu.lamolina.amauta.controller.mensajeria.chatunalm;

import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.zelpers.miscelanea.Assert;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.amauta.dao.academico.AlumnoDAO;
import pe.edu.lamolina.amauta.dao.academico.DocenteDAO;
import pe.edu.lamolina.amauta.dao.consejeria.ConsejeroDAO;
import pe.edu.lamolina.amauta.dao.general.ContenidoCartaDAO;
import pe.edu.lamolina.amauta.dao.mensajeria.MensajeSistemaDAO;
import pe.edu.lamolina.amauta.dao.mensajeria.UsuarioMensajeriaDAO;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.Docente;
import pe.edu.lamolina.model.consejeria.Consejero;
import pe.edu.lamolina.model.enums.SexoEnum;
import pe.edu.lamolina.model.enums.mensajeria.EstadoMensajeEnum;
import pe.edu.lamolina.model.enums.mensajeria.TipoAsuntoMensajeEnum;
import pe.edu.lamolina.model.enums.mensajeria.TipoSistemaEnum;
import pe.edu.lamolina.model.enums.mensajeria.TipoUserMensajeriaEnum;
import pe.edu.lamolina.model.general.Persona;
import pe.edu.lamolina.model.inscripcion.ContenidoCarta;
import pe.edu.lamolina.model.social.MensajeSistema;
import pe.edu.lamolina.model.social.UsuarioMensajeria;
import pe.edu.lamolina.model.tutoria.CitaConsejeroAlumno;

@Slf4j
@Service
@AllArgsConstructor(onConstructor = @__(
        @Autowired))
@Transactional(readOnly = true)
public class ChatUnalmServiceImp implements ChatUnalmService {

    private final AlumnoDAO alumnoDAO;
    private final ConsejeroDAO consejeroDAO;
    private final ContenidoCartaDAO contenidoCartaDAO;
    private final DocenteDAO docenteDAO;
    private final MensajeSistemaDAO mensajeSistemaDAO;
    private final UsuarioMensajeriaDAO usuarioMensajeriaDAO;

    private final RabbitTemplate rabbitTemplate;

    @Override
    @Transactional
    public void enviarMensaje(String asunto, String contenido, Docente docente, Alumno alumno, DataSessionPivot ds) {
        UsuarioMensajeria userDocente = this.getUsuario(docente, ds);
        UsuarioMensajeria userAlumno = this.getUsuario(alumno, ds);

        MensajeSistema mensaje = new MensajeSistema();
        mensaje.setAsunto(asunto);
        mensaje.setMensaje(contenido);
        mensaje.setDestinatario(userAlumno);
        mensaje.setRemitente(userDocente);
        mensaje.setEstadoEnum(EstadoMensajeEnum.ENVIADO);
        mensaje.setSistemaOrigenEnum(TipoSistemaEnum.AMAUTA);
        mensaje.setSistemaDestinoEnum(TipoSistemaEnum.MAIPI);
        mensaje.setUserRegistro(ds.getUsuario());
        mensaje.setFechaRegistro(new Date());
        mensajeSistemaDAO.save(mensaje);
    }

    @Override
    @Transactional
    public UsuarioMensajeria getUsuario(Docente docenteForm, DataSessionPivot ds) {
        Docente docente = docenteDAO.find(docenteForm.getId());
        Assert.isNotNull(docente, "No se ha ubicado el registro del docente");
        TipoUserMensajeriaEnum tipoUsuario = TipoUserMensajeriaEnum.DOCENTE;
        return this.createUsuario(null, docente, docente.getPersona(), tipoUsuario, ds);
    }

    @Override
    @Transactional
    public UsuarioMensajeria getUsuario(Alumno alumnoForm, DataSessionPivot ds) {
        Alumno alumno = alumnoDAO.findAllInfo(alumnoForm.getId());
        Assert.isNotNull(alumno, "No se ha ubicado el registro del alumno");
        TipoUserMensajeriaEnum tipoUsuario = TipoUserMensajeriaEnum.ALUMNO;
        return this.createUsuario(alumno, null, alumno.getPersona(), tipoUsuario, ds);
    }

    private UsuarioMensajeria createUsuario(Alumno alumno, Docente docente, Persona persona, TipoUserMensajeriaEnum tipoUsuario, DataSessionPivot ds) {
        UsuarioMensajeria user = usuarioMensajeriaDAO.findByPersonaTipoUser(persona, tipoUsuario);
        if (user != null) {
            return user;
        }

        user = new UsuarioMensajeria();
        user.setTipoUsuarioEnum(tipoUsuario);
        user.setPersona(persona);
        user.setAlumno(alumno);
        user.setDocente(docente);
        user.setPendientesLeer(0);
        user.setFechaRegistro(new Date());
        user.setUserRegistro(ds.getUsuario());
        usuarioMensajeriaDAO.save(user);

        return user;
    }

    @Override
    public String crearContenido(TipoAsuntoMensajeEnum tipoAsunto, CitaConsejeroAlumno cita) {
        ContenidoCarta carta = contenidoCartaDAO.findByCodigo(tipoAsunto.name());
        if (carta == null) {
            return null;
        }

        String contenido = carta.getContenido();
        if (tipoAsunto == TipoAsuntoMensajeEnum.CITA_CONSEJERO) {
            Assert.isNotNull(cita.getFecha(), "No indicó la fecha de la cita");
            Assert.isNotNull(cita.getHora(), "No indicó la hora de la cita");

            Assert.isNotNull(cita.getAlumno(), "No indicó el alumno");
            Assert.isNotNull(cita.getAlumno().getId(), "No indicó el alumno");
            Assert.isNotNull(cita.getConsejero(), "No indicó el consejero");
            Assert.isNotNull(cita.getConsejero().getId(), "No indicó el consejero");

            Alumno alumnoBD = alumnoDAO.findAllInfo(cita.getAlumno().getId());
            Consejero consejero = consejeroDAO.find(cita.getConsejero().getId());

            contenido = contenido.replaceAll("PRM_ESTIMADO", this.getEstimado(alumnoBD.getPersona()));
            contenido = contenido.replaceAll("PRM_CONSEJERO", consejero.getColaborador().getPersona().getNombreCompleto());
            contenido = contenido.replaceAll("PRM_FECHA", TypesUtil.getStringDate(cita.getFecha(), "EEEE dd 'de' MMMM", "es"));
            contenido = contenido.replaceAll("PRM_HORA", cita.getHora());
            return contenido;
        }

        return null;
    }

    private String getEstimado(Persona persona) {
        if (persona.getSexoEnum() == SexoEnum.M) {
            return "Estimado";
        }
        if (persona.getSexoEnum() == SexoEnum.F) {
            return "Estimada";
        }
        return "Estimado(a)";
    }

}
