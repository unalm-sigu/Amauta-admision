package pe.edu.lamolina.pivot.controller.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class VisorCalculoNotas {

    private Integer cantidad;
    private Integer procesados;
    private long inicio;
    private Boolean activo;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public VisorCalculoNotas() {
        activo = false;
    }

    public void iniciar() {
        cantidad = 0;
        procesados = 0;
        inicio = System.currentTimeMillis();
    }

    public synchronized void incrementarCantidad() {
        cantidad++;
    }

    public synchronized void incrementarProcesados() {
        procesados++;
    }

    public void reporte() {
        logger.info("Procesados {} de {}", procesados, cantidad);
        if (procesados >= cantidad) {
            long fin = System.currentTimeMillis();
            logger.info("Finalizó procesos de recalculo de notas en {} mseg", (fin - inicio));
        }
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }

}
