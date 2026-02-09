package pe.edu.lamolina.amauta.controller.nivelacioneegg.programacionnivelacion.notificarcambio;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pe.albatross.zelpers.miscelanea.TypesUtil;

import pe.edu.lamolina.amauta.controller.mensajeria.chatunalm.ChatUnalmService;
import pe.edu.lamolina.amauta.dao.nivelacioneegg.NotaAlumnoNivelacionDAO;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;
import pe.edu.lamolina.model.academico.*;
import pe.edu.lamolina.model.enums.*;
import pe.edu.lamolina.model.enums.mensajeria.TipoAsuntoMensajeEnum;
import pe.edu.lamolina.model.nivelacioneegg.CursoNivelacion;
import pe.edu.lamolina.model.nivelacioneegg.NotaAlumnoNivelacion;
import pe.edu.lamolina.model.social.AsuntoMensaje;
import pe.edu.lamolina.model.social.MensajeSistema;

import java.util.*;

@Slf4j
@Service
@AllArgsConstructor(onConstructor = @__(
        @Autowired))
@Transactional(readOnly = true)
public class NotificarCambioServiceImpl implements NotificarCambioService {

    private final NotaAlumnoNivelacionDAO notaAlumnoNivelacionDAO;

    private final ChatUnalmService chatUnalmService;

    @Async
    @Override
    @Transactional
    public void notificaCambioAula(CursoNivelacion cursoNiv, DataSessionPivot ds) {
        TypesUtil.delay(200);
        List<NotaAlumnoNivelacion> inscritos = notaAlumnoNivelacionDAO.allInscritosByCursoNivelacion(cursoNiv);

        TipoAsuntoMensajeEnum tipoAsunto = TipoAsuntoMensajeEnum.AVISO_EEGG_CAMBIO_AULA;
        for (NotaAlumnoNivelacion nota : inscritos) {
            Alumno alumno = nota.getAlumnoNivelacion().getAlumno();
            String contenido = chatUnalmService.crearContenido(tipoAsunto, nota);
            AsuntoMensaje asunto = new AsuntoMensaje(
                    tipoAsunto.getValue(),
                    NombreTablasEnum.EEGG_CURSO_NIVELACION,
                    cursoNiv.getId());
            MensajeSistema mensaje = chatUnalmService.crearMensaje(asunto, contenido, ds.getPersona(), alumno, ds);
            chatUnalmService.enviarMensajeChat(mensaje);
        }
    }

}
