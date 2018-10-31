package pe.edu.lamolina.pivot.controller.test;

import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class VisorCalculoNotas {

    private Integer cantidad;
    private Integer procesados;
    private Integer cantidadTotal;
    private long inicio;
    private Boolean activo;
    private List<String> errores;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public VisorCalculoNotas() {
        activo = false;
    }

    public void iniciar() {
        cantidad = 0;
        procesados = 0;
        cantidadTotal = 0;
        inicio = System.currentTimeMillis();
        errores = new ArrayList<>();
    }

    public synchronized void incrementarCantidad() {
        cantidad++;
    }

    public synchronized void incrementarProcesados() {
        procesados++;
    }

    public synchronized void agregarError(String error) {
        errores.add(error);
    }

    public void reporte() {
        logger.info("Procesados {} de {}, Cantidad Total {}", procesados, cantidad, cantidadTotal);

        if (procesados >= cantidad) {
            long fin = System.currentTimeMillis();
            logger.info("Finalizó procesos de recalculo de notas en {} mseg", (fin - inicio));
            if (!errores.isEmpty()) {
                logger.info("Cantidad de Errores {}", errores.size());
            }
        }
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }

    public void setCantidadTotal(Integer cantidadTotal) {
        this.cantidadTotal = cantidadTotal;
    }

}
