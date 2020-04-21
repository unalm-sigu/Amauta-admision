package pe.edu.lamolina.amauta.controller.visores;

import java.math.BigDecimal;
import java.math.RoundingMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class RespositorVisor {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private enum EstadoEnum {
        LIBRE, INICIADO, OCUPADO, COMPLETO
    };
    private long inicioTime;

    private Integer contador;
    private Integer procesados;
    private Integer cantidadTotal;
    private Integer cantidadTotalTemp;
    private EstadoEnum estadoEnum;

    public RespositorVisor() {

    }

    public void iniciar(Integer cantidadTotal) {
        this.contador = 1;
        this.cantidadTotal = cantidadTotal;
        this.cantidadTotalTemp = cantidadTotal;
        this.estadoEnum = EstadoEnum.OCUPADO;
        this.inicioTime = System.currentTimeMillis();
    }

    public synchronized void incrementar() {
        if (this.contador != null) {
            this.contador = this.contador + 1;
            this.cantidadTotalTemp = this.cantidadTotalTemp - 1;
        }
    }

    public synchronized void completar() {
        this.estadoEnum = EstadoEnum.COMPLETO;
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

    public String getMessage() {
        if (this.contador != null) {
            return "Registro " + contador + " de " + cantidadTotal;
        }
        return "";
    }

    public Boolean isLibre() {
        if (this.estadoEnum == null) {
            return true;
        }
        Boolean res = this.estadoEnum == EstadoEnum.LIBRE;
        if (this.cantidadTotalTemp == 0) {
            this.estadoEnum = EstadoEnum.LIBRE;
        }
        return res;
    }

    public Boolean isCompleto() {
        return this.estadoEnum == EstadoEnum.COMPLETO;
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

    public Integer porcentajeAvance() {
        if (this.cantidadTotal == null || this.cantidadTotal == 0) {
            return 0;
        }
        int tope = this.cantidadTotal;
        int conta = this.contador;
        if (tope == 0) {
            return 0;
        }
        BigDecimal div = new BigDecimal(conta * 100).divide(new BigDecimal(tope), 0, RoundingMode.FLOOR);
        return div.intValue();
    }
}
