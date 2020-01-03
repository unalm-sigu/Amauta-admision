package pe.edu.lamolina.pivot.controller.academico.promedio;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class ContadorComponent {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private long inicioTime;

//    private Integer contador;
    private Integer procesados;
    private int metaProcesados;
    private int cantidadAcumulada;

    public ContadorComponent() {

    }

    public synchronized void iniciarTotal() {
//        this.contador = 1;
        this.cantidadAcumulada = 0;
    }

    public synchronized void iniciar(Integer metaProcesados) {
//        this.contador = 1;
        this.procesados = 0;
        this.metaProcesados = metaProcesados;
        this.inicioTime = System.currentTimeMillis();
    }

//    public synchronized void incrementar() {
//        if (this.contador != null) {
//            this.contador = this.contador + 1;
//        }
//    }
    public synchronized void incrementarProcesados() {
        this.procesados = this.procesados + 1;
        this.cantidadAcumulada = this.cantidadAcumulada + 1;
    }

//    public void reporte() {
//        if (this.contador != null) {
//            logger.info("Registro {} de {}", contador, cantidadTotal);
//        }
//    }
    public Integer getProcesados() {
        return procesados;
    }

    public Integer getMetaProcesados() {
        return metaProcesados;
    }

    public Integer getCantidadAcumulada() {
        return cantidadAcumulada;
    }

//    public void setContador(Integer contador) {
//        this.contador = contador;
//    }
//    public Integer getCantidadTotal() {
//        return cantidadProceso;
//    }
//
//    public void setCantidadTotal(Integer cantidadTotal) {
//        this.cantidadProceso = cantidadTotal;
//    }
    public boolean finalizoProcesados() {
        return this.procesados >= this.metaProcesados;
    }

}
