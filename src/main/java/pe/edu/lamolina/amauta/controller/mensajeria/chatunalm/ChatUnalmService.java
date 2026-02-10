package pe.edu.lamolina.amauta.controller.mensajeria.chatunalm;

import java.util.List;

import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.Docente;
import pe.edu.lamolina.model.enums.mensajeria.TipoAsuntoMensajeEnum;
import pe.edu.lamolina.model.general.Persona;
import pe.edu.lamolina.model.nivelacioneegg.NotaAlumnoNivelacion;
import pe.edu.lamolina.model.social.AsuntoMensaje;
import pe.edu.lamolina.model.social.MensajeSistema;
import pe.edu.lamolina.model.social.UsuarioMensajeria;
import pe.edu.lamolina.model.tutoria.AlumnoDerivadoAtencion;
import pe.edu.lamolina.model.tutoria.CitaConsejeroAlumno;

public interface ChatUnalmService {

    List<AsuntoMensaje> allAsuntos(DataSessionPivot ds);

    void marcarMensaje(MensajeSistema mensaje, DataSessionPivot ds);

    MensajeSistema crearMensaje(AsuntoMensaje asunto, String contenido, Docente docente, Alumno alumno, DataSessionPivot ds);

    MensajeSistema crearMensaje(AsuntoMensaje asunto, String contenido, Persona persona, Alumno alumno, DataSessionPivot ds);

    UsuarioMensajeria getUsuario(Docente docente, DataSessionPivot ds);

    UsuarioMensajeria getUsuario(Alumno alumno, DataSessionPivot ds);

    UsuarioMensajeria getUsuario(Persona persona, DataSessionPivot ds);

    String crearContenido(TipoAsuntoMensajeEnum tipoAsunto, CitaConsejeroAlumno cita);

    String crearContenido(TipoAsuntoMensajeEnum tipoAsunto, AlumnoDerivadoAtencion derivacionTutor);

    String crearContenido(TipoAsuntoMensajeEnum tipoAsunto, NotaAlumnoNivelacion inscrito);

    void enviarMensajeChat(MensajeSistema mensaje);

    void enviarMensajeChatDelay(MensajeSistema mensaje, int milisegundos);

}
