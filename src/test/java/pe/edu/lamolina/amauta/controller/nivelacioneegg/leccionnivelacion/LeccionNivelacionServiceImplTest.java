package pe.edu.lamolina.amauta.controller.nivelacioneegg.leccionnivelacion;

import java.util.Date;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.junit.Test;

@Slf4j
public class LeccionNivelacionServiceImplTest {

    @Test
    public void test_verificar_mismo_dia() {
        LocalDate hoy = new LocalDate();
        LocalDate fecha = new LocalDate(new Date());

        log.info("hoy == fecha => {}", hoy.equals(fecha));
        LocalDate ayer = hoy.plusDays(-3);
        log.info("ayer == fecha => {}", ayer.equals(fecha));

    }

    @Test
    public void test_verificar_hora_hoy() {
        int horaMin = 14;
        int horaHoy = new DateTime().getHourOfDay();

        log.info("horaMin={} horaHoy={}", horaMin, horaHoy);

    }

}
