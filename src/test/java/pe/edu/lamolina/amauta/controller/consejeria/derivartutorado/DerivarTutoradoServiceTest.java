package pe.edu.lamolina.amauta.controller.consejeria.derivartutorado;

import java.util.Arrays;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.LocalDate;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;

@Slf4j
public class DerivarTutoradoServiceTest {

    @Before
    public void initMocks() {
    }

    @Test
    public void verificarViernes_test() {
        LocalDate viernes = new LocalDate("2023-07-14");
        int diaSemana = viernes.getDayOfWeek();
        log.info("fecha={} dia semana={}", viernes.toString("dd/MM/yyyy"), diaSemana);
        assertEquals(5, diaSemana);
    }

    @Test
    public void saveDerivacion_test() {
        LocalDate fecha = new LocalDate("2023-07-13");
        int diaSemana = fecha.getDayOfWeek();
        LocalDate fechaDos = this.plusDays(fecha, 2);
        int diaSemana2 = fechaDos.getDayOfWeek();

        log.info("fecha={} dia-semana={}", fecha.toString("dd/MM/yyyy"), diaSemana);
        log.info("fechaDos={} dia-semana={}", fechaDos.toString("dd/MM/yyyy"), diaSemana2);

        assertEquals(4, diaSemana);
        assertEquals(1, diaSemana2);
        assertEquals("17/07/2023", fechaDos.toString("dd/MM/yyyy"));
    }

    private LocalDate plusDays(LocalDate fecha, int dias) {
        LocalDate siguiente = new LocalDate(fecha.toDate().getTime());
        for (int i = 0; i < dias; i++) {
            siguiente = this.siguienteFecha(siguiente);
        }
        return siguiente;
    }

    private LocalDate siguienteFecha(LocalDate fecha) {
        LocalDate siguiente = fecha.plusDays(1);
        int diaSemana = siguiente.getDayOfWeek();
        if (Arrays.asList(6, 7).contains(diaSemana)) {
            return siguienteFecha(siguiente);
        }
        return siguiente;
    }
}
