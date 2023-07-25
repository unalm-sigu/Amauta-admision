package pe.edu.lamolina.amauta.controller.consejeria.derivartutorado;

import lombok.extern.slf4j.Slf4j;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;

@Slf4j
public class DerivarTutoradoServiceTest {

    @Before
    public void initMocks() {
    }

    @Test
    public void saveDerivacion_test() {
        LocalDate viernes = new LocalDate("2023-07-14");
        int diaSemana = viernes.getDayOfWeek();
        log.info("fecha={} dia semana={}", viernes.toString("dd/MM/yyyy"), diaSemana);

    }

    private LocalDate siguientFecha(LocalDate fecha) {
        fecha.getDayOfWeek();
        return fecha;
    }
}
