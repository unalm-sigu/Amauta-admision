package pe.edu.lamolina.amauta.controller.noficarsocket;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.lamolina.model.comedor.ResumenServicioComedor;
import pe.edu.lamolina.model.comedor.dto.ResumenAtencionServicio;

@Slf4j
@Service
@AllArgsConstructor(onConstructor = @__(
        @Autowired))
@Transactional(readOnly = true)
public class NotificarSocketServiceImpl implements NotificarSocketService {

    private final SimpMessagingTemplate messagingTemplate;

    //@Override
    public void enviarResumen(ResumenServicioComedor resumen, UUID uuid) {
        ResumenAtencionServicio resumenSend = new ResumenAtencionServicio(resumen, uuid.toString());
        messagingTemplate.convertAndSend("/broken/comedor/resumen", resumenSend);
    }

}
