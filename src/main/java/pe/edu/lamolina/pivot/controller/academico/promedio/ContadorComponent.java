package pe.edu.lamolina.pivot.controller.academico.promedio;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class ContadorComponent {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private long inicioTime;

    private Integer contador;
    private Integer procesados;
    private Integer cantidadTotal;

    public ContadorComponent() {

    }

    public void iniciar(Integer cantidadTotal) {
        this.contador = 1;
        this.cantidadTotal = cantidadTotal;
        this.inicioTime = System.currentTimeMillis();
    }

    public synchronized void incrementar() {
        if (this.contador != null) {
            this.contador = this.contador + 1;
        }
    }

    public synchronized void incrementarProcesados() {
        if (this.procesados != null) {
            this.procesados = this.procesados + 1;
        }
    }

    public void reporte() {
        if (this.contador != null) {
            logger.info("Registro {} de {}", contador, cantidadTotal);
        }
    }

    public Integer getContador() {
        return contador;
    }

    public void setContador(Integer contador) {
        this.contador = contador;
    }

    public Integer getCantidadTotal() {
        return cantidadTotal;
    }

    public void setCantidadTotal(Integer cantidadTotal) {
        this.cantidadTotal = cantidadTotal;
    }

}
