package pe.edu.lamolina.amauta.controller.mensajeria.chatunalm;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.zelpers.json.JaneHelper;
import pe.albatross.zelpers.miscelanea.Assert;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.amauta.dao.academico.AlumnoDAO;
import pe.edu.lamolina.amauta.dao.academico.CursoDAO;
import pe.edu.lamolina.amauta.dao.academico.DocenteDAO;
import pe.edu.lamolina.amauta.dao.consejeria.ConsejeroDAO;
import pe.edu.lamolina.amauta.dao.general.ContenidoCartaDAO;
import pe.edu.lamolina.amauta.dao.mensajeria.AsuntoMensajeDAO;
import pe.edu.lamolina.amauta.dao.mensajeria.AsuntoMensajeUsuarioDAO;
import pe.edu.lamolina.amauta.dao.mensajeria.MensajeSistemaDAO;
import pe.edu.lamolina.amauta.dao.mensajeria.UsuarioMensajeriaDAO;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.Curso;
import pe.edu.lamolina.model.academico.Docente;
import pe.edu.lamolina.model.consejeria.Consejero;
import pe.edu.lamolina.model.constantines.BienestarConstantine;
import pe.edu.lamolina.model.enums.SexoEnum;
import pe.edu.lamolina.model.enums.mensajeria.EstadoMensajeEnum;
import static pe.edu.lamolina.model.enums.mensajeria.EstadoMensajeEnum.ENVIADO;
import static pe.edu.lamolina.model.enums.mensajeria.EstadoMensajeEnum.LEIDO;
import pe.edu.lamolina.model.enums.mensajeria.TipoAsuntoMensajeEnum;
import static pe.edu.lamolina.model.enums.mensajeria.TipoAsuntoMensajeEnum.CITA_TUTOR;
import static pe.edu.lamolina.model.enums.mensajeria.TipoAsuntoMensajeEnum.CITA_TUTOR_ANULADA;
import static pe.edu.lamolina.model.enums.mensajeria.TipoAsuntoMensajeEnum.CITA_TUTOR_POSTERGADA;
import static pe.edu.lamolina.model.enums.mensajeria.TipoAsuntoMensajeEnum.DERIVA_TUTOR_AAEE;
import static pe.edu.lamolina.model.enums.mensajeria.TipoAsuntoMensajeEnum.DERIVA_TUTOR_ASESORIA_CURSO;
import static pe.edu.lamolina.model.enums.mensajeria.TipoAsuntoMensajeEnum.DERIVA_TUTOR_DEPORTES;
import static pe.edu.lamolina.model.enums.mensajeria.TipoAsuntoMensajeEnum.DERIVA_TUTOR_PSICOLOGIA;
import static pe.edu.lamolina.model.enums.mensajeria.TipoAsuntoMensajeEnum.DERIVA_TUTOR_PSICOPEDAGOGIA;
import static pe.edu.lamolina.model.enums.mensajeria.TipoAsuntoMensajeEnum.DERIVA_TUTOR_SEMINARIO;
import static pe.edu.lamolina.model.enums.mensajeria.TipoAsuntoMensajeEnum.DERIVA_TUTOR_TALLER_CULTURAL;
import static pe.edu.lamolina.model.enums.mensajeria.TipoAsuntoMensajeEnum.DERIVA_TUTOR_TALLER_VIVENCIAL;
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
    private final CursoDAO cursoDAO;
    private final ConsejeroDAO consejeroDAO;
    private final ContenidoCartaDAO contenidoCartaDAO;
    private final DocenteDAO docenteDAO;
    private final MensajeSistemaDAO mensajeSistemaDAO;
    private final UsuarioMensajeriaDAO usuarioMensajeriaDAO;

    private final RabbitTemplate rabbitTemplate;

    @Override
    public List<AsuntoMensaje> allAsuntos(DataSessionPivot ds) {
        List<MensajeSistema> mensajesAll;
        if (ds.getDocente() != null) {
            mensajesAll = mensajeSistemaDAO.allPendientesByDocente(ds.getDocente());
        } else {
            mensajesAll = mensajeSistemaDAO.allPendientesByPersona(ds.getPersona());
        }

        Map<Long, List<MensajeSistema>> mapMensajes = mensajesAll.stream()
                .collect(Collectors.groupingBy(msg -> msg.getAsuntoMensaje().getId()));

        List<AsuntoMensaje> asuntos = mensajesAll.stream()
                .map(msg -> msg.getAsuntoMensaje())
                .collect(Collectors.toList());

        List<AsuntoMensajeUsuario> resumenesAsuntosUser;
        if (ds.getDocente() != null) {
            resumenesAsuntosUser = asuntoMensajeUsuarioDAO.allByAsuntosDocente(asuntos, ds.getDocente());
        } else {
            resumenesAsuntosUser = asuntoMensajeUsuarioDAO.allByAsuntosPersona(asuntos, ds.getPersona());
        }

        Map<Long, AsuntoMensajeUsuario> mapResumen = resumenesAsuntosUser.stream()
                .collect(Collectors.toMap(amu -> amu.getAsuntoMensaje().getId(), Function.identity()));

        asuntos.forEach(asunto -> {
            List<MensajeSistema> mensajes = mapMensajes.get(asunto.getId());
            AsuntoMensajeUsuario resumen = mapResumen.get(asunto.getId());
            asunto.setMensajes(mensajes);
            asunto.setMensajePrincipal(mensajes.get(0));
            asunto.setResumenUsuario(resumen);
        });

        return asuntos;
    }

    @Override
    @Transactional
    public void marcarMensaje(MensajeSistema mensajeForm, DataSessionPivot ds) {
        boolean esDocente = ds.getDocente() != null;
        MensajeSistema mensaje = mensajeSistemaDAO.find(mensajeForm.getId());
        Assert.isNotNull(mensaje, "No se pudo ubicar el registro que desea marcar");

        Docente docente = mensaje.getDestinatario().getDocente();
        if (esDocente) {
            Assert.isTrue(docente.equals(ds.getDocente()), "Este mensaje pertenece a otro docente");
        }

        Persona persona = mensaje.getDestinatario().getPersona();
        Assert.isTrue(persona.equals(ds.getPersona()), "Este mensaje pertenece a otra persona");
        Assert.isTrue(mensaje.getEstadoEnum() == ENVIADO, "Este mensaje ya fue leido");

        mensaje.setEstadoEnum(LEIDO);
        mensaje.setUserLectura(ds.getUsuario());
        mensaje.setFechaLectura(new Date());
        mensajeSistemaDAO.update(mensaje);

        AsuntoMensaje asunto = mensaje.getAsuntoMensaje();
        AsuntoMensajeUsuario resumenUser = asuntoMensajeUsuarioDAO.findByAsuntoUsuario(asunto, mensaje.getDestinatario());
        Assert.isNotNull(resumenUser, "No se pudo ubicar el resumen del usuario para este asunto del mensaje");

        resumenUser.setPendientesLeer(resumenUser.getPendientesLeer() - 1);
        resumenUser.setUserModificacion(ds.getUsuario());
        resumenUser.setFechaModificacion(new Date());
        asuntoMensajeUsuarioDAO.update(resumenUser);
    }

    @Override
    @Transactional
    public MensajeSistema crearMensaje(AsuntoMensaje asunto, String contenido, Docente docente, Alumno alumno, DataSessionPivot ds) {
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

        List<TipoAsuntoMensajeEnum> asuntosTutor = Arrays.asList(CITA_TUTOR, CITA_TUTOR_ANULADA, CITA_TUTOR_POSTERGADA);

        String contenido = carta.getContenido();
        if (asuntosTutor.contains(tipoAsunto)) {
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
        List<TipoAsuntoMensajeEnum> asuntosSimples = Arrays.asList(
                DERIVA_TUTOR_AAEE,
                DERIVA_TUTOR_PSICOLOGIA, DERIVA_TUTOR_PSICOPEDAGOGIA, DERIVA_TUTOR_TALLER_VIVENCIAL,
                DERIVA_TUTOR_DEPORTES, DERIVA_TUTOR_TALLER_CULTURAL
        );

        List<TipoAsuntoMensajeEnum> asuntosCursos = Arrays.asList(
                DERIVA_TUTOR_ASESORIA_CURSO, DERIVA_TUTOR_SEMINARIO
        );

        if (asuntosSimples.contains(tipoAsunto)) {
            Alumno alumnoBD = alumnoDAO.findAllInfo(derivacionTutor.getAlumno().getId());
            contenido = contenido.replaceAll("PRM_ESTIMADO", this.getEstimado(alumnoBD.getPersona()));
            return contenido;

        } else if (asuntosCursos.contains(tipoAsunto)) {
            Alumno alumnoBD = alumnoDAO.findAllInfo(derivacionTutor.getAlumno().getId());
            contenido = contenido.replaceAll("PRM_ESTIMADO", this.getEstimado(alumnoBD.getPersona()));

            Curso curso = cursoDAO.find(derivacionTutor.getCurso().getId());
            contenido = contenido.replaceAll("PRM_CURSO", curso.getNombre());
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

    @Async
    @Override
    public void enviarMensajeChat(MensajeSistema mensaje) {
        this.enviarChat(mensaje);
    }

    @Override
    public void enviarMensajeChatDelay(MensajeSistema mensaje, int milisegundos) {
        TypesUtil.delay(milisegundos);
        this.enviarChat(mensaje);
    }

    private void enviarChat(MensajeSistema mensaje) {
        mensaje.setHoraPrefija(mensaje.getHora());

        String msg = JaneHelper
                .from(mensaje)
                .only("id,mensaje,estado,fechaRegistro,horaPrefija")
                .join("asuntoMensaje", "id,asunto")
                .join("remitente", "id,userWebsocket")
                .join("remitente.persona", "nombres,paterno,materno")
                .join("destinatario", "id,userWebsocket")
                .join("destinatario.persona", "nombres,paterno,materno")
                .json().toString();

        rabbitTemplate.convertAndSend(BienestarConstantine.QUEUE_CHAT_MAIPI, msg);
    }

}
