package pe.edu.lamolina.pivot.controller.academico.encuestaestudiantil.docente;

import java.math.BigDecimal;
import java.math.RoundingMode;
import org.springframework.stereotype.Component;
import pe.albatross.zelpers.miscelanea.NumberFormat;

@Component
public class VisorEncuestaDocente {

    private Integer maximo;
    private Integer actual;
    private String mensaje;
    private EstadoEnum estado;

    public enum EstadoEnum {
        LIBRE, INICIADO, OCUPADO, COMPLETO
    };

    public VisorEncuestaDocente() {
        estado = EstadoEnum.LIBRE;
    }

    public synchronized boolean iniciar() {
        if (estado == EstadoEnum.INICIADO) {
            return false;
        }
        if (estado == EstadoEnum.OCUPADO) {
            return false;
        }
        estado = EstadoEnum.INICIADO;
        maximo = 1;
        actual = 1;

        return true;
    }

    public synchronized void cancelarProceso() {
        estado = EstadoEnum.LIBRE;
    }

    public synchronized boolean iniciarConteo(Integer cantidad) {
        if (estado != EstadoEnum.INICIADO) {
            return false;
        }

        if (cantidad < 1) {
            estado = EstadoEnum.COMPLETO;
            return true;
        }

        estado = EstadoEnum.OCUPADO;
        maximo = cantidad;
        actual = 0;

        return true;
    }

    public boolean estaProcesando() {
        return estado == EstadoEnum.OCUPADO || estado == EstadoEnum.INICIADO;
    }

    public synchronized boolean incrementar() {
        if (estado == EstadoEnum.LIBRE) {
            return false;
        }
        actual++;

        if (actual >= maximo) {
            estado = EstadoEnum.COMPLETO;
        }
        return true;
    }

    public Integer getPorcentaje() {
        if (estado == EstadoEnum.LIBRE) {
            return 0;
        }
        if (estado == EstadoEnum.INICIADO) {
            return 0;
        }

        if (estado == EstadoEnum.COMPLETO) {
            return 100;
        }

        Integer porc = new BigDecimal(actual).multiply(new BigDecimal("100")).divide(new BigDecimal(maximo), 0, RoundingMode.FLOOR).intValue();
        return porc;
    }

    public String getEstadoEnum() {
        return estado.name();
    }

    public synchronized void setEstado(String message) {
        if (estado == EstadoEnum.LIBRE) {
            return;
        }
        if (estado == EstadoEnum.COMPLETO) {
            return;
        }
        mensaje = message;

    }

    public String getEstado() {
        if (estado == EstadoEnum.LIBRE) {
            return "No está creando encuestas";
        }

        if (estado == EstadoEnum.INICIADO) {
            if (mensaje == null) {
                return "Procesando información";
            } else {
                return mensaje;
            }
        }

        if (estado == EstadoEnum.COMPLETO) {
            return "Proceso finalizado. Se han creado  " + NumberFormat.medida(actual) + " encuestas.";
        }

        return "Ya se ha creado " + NumberFormat.medida(actual) + " encuestas de " + NumberFormat.medida(maximo);
    }
}
