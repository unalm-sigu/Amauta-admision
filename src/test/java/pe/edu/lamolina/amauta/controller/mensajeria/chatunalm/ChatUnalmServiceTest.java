package pe.edu.lamolina.amauta.controller.mensajeria.chatunalm;

import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.junit.Test;
import pe.albatross.zelpers.miscelanea.TypesUtil;

@Slf4j
public class ChatUnalmServiceTest {

    @Test
    public void fecha_Test() {
        DateTime fecha = new DateTime();
        log.debug("fecha={}", TypesUtil.getStringDate(fecha.toDate(), "EEEE dd 'de' MMMM", "es"));

    }
}
