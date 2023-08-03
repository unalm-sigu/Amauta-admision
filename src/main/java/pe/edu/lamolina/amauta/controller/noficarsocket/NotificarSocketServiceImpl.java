package pe.edu.lamolina.amauta.controller.noficarsocket;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.lamolina.model.social.MensajeSistema;

@Slf4j
@Service
@AllArgsConstructor(onConstructor = @__(
        @Autowired))
@Transactional(readOnly = true)
public class NotificarSocketServiceImpl implements NotificarSocketService {

    private final SimpMessagingTemplate messagingTemplate;

    @Override
    public void enviarMensajeChat(MensajeSistema mensaje) {
        String destination = "/monitoreo/chatunalm/" + mensaje.getDestinatario().getUserWSCompleto();
        messagingTemplate.convertAndSend(destination, mensaje);
    }

}
