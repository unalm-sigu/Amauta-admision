package pe.edu.lamolina.amauta.controller.mensajeria.chatunalm;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.zelpers.miscelanea.Assert;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.amauta.dao.academico.AlumnoDAO;
import pe.edu.lamolina.amauta.dao.academico.DocenteDAO;
import pe.edu.lamolina.amauta.dao.consejeria.ConsejeroDAO;
import pe.edu.lamolina.amauta.dao.general.ContenidoCartaDAO;
import pe.edu.lamolina.amauta.dao.mensajeria.AsuntoMensajeDAO;
import pe.edu.lamolina.amauta.dao.mensajeria.AsuntoMensajeUsuarioDAO;
import pe.edu.lamolina.amauta.dao.mensajeria.MensajeSistemaDAO;
import pe.edu.lamolina.amauta.dao.mensajeria.UsuarioMensajeriaDAO;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.Docente;
import pe.edu.lamolina.model.consejeria.Consejero;
import pe.edu.lamolina.model.enums.SexoEnum;
import pe.edu.lamolina.model.enums.mensajeria.EstadoMensajeEnum;
import pe.edu.lamolina.model.enums.mensajeria.TipoAsuntoMensajeEnum;
import static pe.edu.lamolina.model.enums.mensajeria.TipoAsuntoMensajeEnum.DERIVA_TUTOR_AAEE;
import static pe.edu.lamolina.model.enums.mensajeria.TipoAsuntoMensajeEnum.DERIVA_TUTOR_PSICOLOGIA;
import static pe.edu.lamolina.model.enums.mensajeria.TipoAsuntoMensajeEnum.DERIVA_TUTOR_PSICOPEDAGOGIA;
import pe.edu.lamolina.model.enums.mensajeria.TipoSistemaEnum;
import pe.edu.lamolina.model.enums.mensajeria.TipoUserMensajeriaEnum;
import pe.edu.lamolina.model.general.Persona;
import pe.edu.lamolina.model.inscripcion.ContenidoCarta;
import pe.edu.lamolina.model.social.AsuntoMensaje;
import pe.edu.lamolina.model.social.AsuntoMensajeUsuario;
import pe.edu.lamolina.model.social.MensajeSistema;
import pe.edu.lamolina.model.social.UsuarioMensajeria;
import pe.edu.lamolina.model.tutoria.AlumnoDerivadoAtencion;
import pe.edu.lamolina.model.tutoria.CitaConsejeroAlumno;

@Slf4j
@Service
@AllArgsConstructor(onConstructor = @__(
        @Autowired))
@Transactional(readOnly = true)
public class ChatUnalmServiceImp implements ChatUnalmService {

    private final AlumnoDAO alumnoDAO;
    private final AsuntoMensajeDAO asuntoMensajeDAO;
    private final AsuntoMensajeUsuarioDAO asuntoMensajeUsuarioDAO;
    private final ConsejeroDAO consejeroDAO;
    private final ContenidoCartaDAO contenidoCartaDAO;
    private final DocenteDAO docenteDAO;
    private final MensajeSistemaDAO mensajeSistemaDAO;
    private final UsuarioMensajeriaDAO usuarioMensajeriaDAO;

    @Override
    @Transactional
    public MensajeSistema enviarMensaje(AsuntoMensaje asunto, String contenido, Docente docente, Alumno alumno, DataSessionPivot ds) {
        UsuarioMensajeria userDocente = this.getUsuario(docente, ds);
        UsuarioMensajeria userAlumno = this.getUsuario(alumno, ds);

        AsuntoMensaje asuntoBD = this.getAsunto(asunto, ds);

        MensajeSistema mensaje = new MensajeSistema();
        mensaje.setAsuntoMensaje(asuntoBD);
        mensaje.setMensaje(contenido);
        mensaje.setDestinatario(userAlumno);
        mensaje.setRemitente(userDocente);
        mensaje.setEstadoEnum(EstadoMensajeEnum.ENVIADO);
        mensaje.setSistemaOrigenEnum(TipoSistemaEnum.AMAUTA);
        mensaje.setSistemaDestinoEnum(TipoSistemaEnum.MAIPI);
        mensaje.setUserRegistro(ds.getUsuario());
        mensaje.setFechaRegistro(new Date());
        mensajeSistemaDAO.save(mensaje);

        this.createAsuntoUsuario(asuntoBD, userAlumno, ds);

        userAlumno.setPendientesLeer(userAlumno.getPendientesLeer() + 1);
        usuarioMensajeriaDAO.update(userAlumno);

        return mensaje;
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
        UsuarioMensajeria user = null;
        if (tipoUsuario == TipoUserMensajeriaEnum.ALUMNO) {
            user = usuarioMensajeriaDAO.findByPersonaAlumno(persona, alumno);
        } else if (tipoUsuario == TipoUserMensajeriaEnum.DOCENTE) {
            user = usuarioMensajeriaDAO.findByPersonaDocente(persona, docente);
        } else if (tipoUsuario == TipoUserMensajeriaEnum.GENERICO) {
            user = usuarioMensajeriaDAO.findByPersonaTipoUser(persona, tipoUsuario);
        }

        if (user != null) {
            return user;
        }

        user = new UsuarioMensajeria();
        user.setTipoUsuarioEnum(tipoUsuario);
        user.setPersona(persona);
        user.setAlumno(alumno);
        user.setDocente(docente);
        user.setUserWebsocket(user.generarUserWS());
        user.setPendientesLeer(0);
        user.setFechaRegistro(new Date());
        user.setUserRegistro(ds.getUsuario());
        usuarioMensajeriaDAO.save(user);

        return user;
    }

    private AsuntoMensaje getAsunto(AsuntoMensaje asunto, DataSessionPivot ds) {
        AsuntoMensaje asuntoBD = asuntoMensajeDAO.findByTablaInstancia(asunto.getNombreTabla(), asunto.getInstanciaTabla());
        if (asuntoBD != null) {
            return asuntoBD;
        }

        asunto.setUserRegistro(ds.getUsuario());
        asunto.setFechaRegistro(new Date());
        asuntoMensajeDAO.save(asunto);

        return asunto;
    }

    private void createAsuntoUsuario(AsuntoMensaje asunto, UsuarioMensajeria usuario, DataSessionPivot ds) {
        boolean yaExiste = true;
        AsuntoMensajeUsuario asuntoUsuario = asuntoMensajeUsuarioDAO.findByAsuntoUsuario(asunto, usuario);
        if (asuntoUsuario == null) {
            asuntoUsuario = new AsuntoMensajeUsuario();
            asuntoUsuario.setAsuntoMensaje(asunto);
            asuntoUsuario.setUsuarioMensajeria(usuario);
            asuntoUsuario.setTotalMensajes(0);
            asuntoUsuario.setPendientesLeer(0);
            asuntoUsuario.setUserRegistro(ds.getUsuario());
            asuntoUsuario.setFechaRegistro(new Date());
            asuntoMensajeUsuarioDAO.save(asuntoUsuario);

            yaExiste = false;
        }

        asuntoUsuario.setTotalMensajes(asuntoUsuario.getTotalMensajes() + 1);
        asuntoUsuario.setPendientesLeer(asuntoUsuario.getPendientesLeer() + 1);

        if (yaExiste) {
            asuntoUsuario.setUserModificacion(ds.getUsuario());
            asuntoUsuario.setFechaModificacion(new Date());
        }

        asuntoMensajeUsuarioDAO.update(asuntoUsuario);
    }

    @Override
    public String crearContenido(TipoAsuntoMensajeEnum tipoAsunto, CitaConsejeroAlumno cita) {
        ContenidoCarta carta = contenidoCartaDAO.findByCodigo(tipoAsunto.name());
        if (carta == null) {
            return null;
        }

        String contenido = carta.getContenido();
        if (tipoAsunto == TipoAsuntoMensajeEnum.CITA_TUTOR) {
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

    @Override
    public String crearContenido(TipoAsuntoMensajeEnum tipoAsunto, AlumnoDerivadoAtencion derivacionTutor) {
        ContenidoCarta carta = contenidoCartaDAO.findByCodigo(tipoAsunto.name());
        if (carta == null) {
            return null;
        }

        String contenido = carta.getContenido();
        List<TipoAsuntoMensajeEnum> asuntos = Arrays.asList(DERIVA_TUTOR_PSICOLOGIA, DERIVA_TUTOR_PSICOPEDAGOGIA, DERIVA_TUTOR_AAEE);
        if (asuntos.contains(tipoAsunto)) {
            Alumno alumnoBD = alumnoDAO.findAllInfo(derivacionTutor.getAlumno().getId());
            contenido = contenido.replaceAll("PRM_ESTIMADO", this.getEstimado(alumnoBD.getPersona()));
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
