package pe.edu.lamolina.amauta.controller.queue;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pe.edu.lamolina.amauta.controller.noficarsocket.NotificarSocketService;
import pe.edu.lamolina.model.social.MensajeSistema;

@Component
public class ReceptorQueue {

    @Autowired
    private NotificarSocketService notificarSocketService;

    public void handleMessageChatUnalm(Object message) {

        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

            MensajeSistema mensajeChat = mapper.readValue((String) message, MensajeSistema.class);
            notificarSocketService.enviarMensajeChat(mensajeChat);

        } catch (Exception ex) {
            JsonNodeFactory jsonNodeFactory = JsonNodeFactory.instance;
            ObjectNode objNode = new ObjectNode(jsonNodeFactory);
            objNode.put("data", (String) message);
            ex.printStackTrace();
        }

    }

}
