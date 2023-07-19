package pe.edu.lamolina.amauta.controller.mensajeria.chatunalm;

import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.Docente;
import pe.edu.lamolina.model.enums.mensajeria.TipoAsuntoMensajeEnum;
import pe.edu.lamolina.model.social.UsuarioMensajeria;
import pe.edu.lamolina.model.tutoria.CitaConsejeroAlumno;

public interface ChatUnalmService {

    void enviarMensaje(String asunto, String contenido, Docente docente, Alumno alumno, DataSessionPivot ds);

    UsuarioMensajeria getUsuario(Docente docente, DataSessionPivot ds);

    UsuarioMensajeria getUsuario(Alumno alumno, DataSessionPivot ds);

    String crearContenido(TipoAsuntoMensajeEnum tipoAsunto, CitaConsejeroAlumno cita);

}
