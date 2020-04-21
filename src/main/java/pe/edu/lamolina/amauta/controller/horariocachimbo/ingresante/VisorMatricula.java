package pe.edu.lamolina.amauta.controller.horariocachimbo.ingresante;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.model.academico.Alumno;

@Component
public class VisorMatricula {

    private List<String> mensajes;
    private String procesoActual;
    private List<Alumno> alumnos;
    private Map< Long, Alumno> mapAlumnos;
    private Integer procesados;
    private BigDecimal avance;
    private EstadoEnum estado;

    public VisorMatricula() {
        this.estado = EstadoEnum.LIBRE;
    }

    private enum EstadoEnum {
        LIBRE, INICIADO, PROCESANDO, ERROR, COMPLETO
    };

    public synchronized boolean iniciar() {
        if (this.estado != EstadoEnum.LIBRE) {
            if (this.estado != EstadoEnum.ERROR) {
                if (this.estado != EstadoEnum.COMPLETO) {
                    return false;
                }
            }
        }

        this.estado = EstadoEnum.INICIADO;
        this.alumnos = new ArrayList();
        this.mapAlumnos = TypesUtil.convertListToMap("id", this.alumnos);
        this.procesados = 0;
        this.avance = BigDecimal.ZERO;
        this.mensajes = new ArrayList();
        this.procesoActual = "Preparando información para iniciar el proceso Matrícula-Cachimbos.";

        return true;
    }

    public synchronized void sinData() {
        if (this.estado == EstadoEnum.INICIADO) {
            this.estado = EstadoEnum.ERROR;
            this.procesoActual = "No existe alumnos para procesar.";
        }
    }

    public synchronized boolean iniciarAlumnos(List<Alumno> alumnosNuevos) {
        if (this.estado != EstadoEnum.INICIADO) {
            return false;
        }

        if (alumnosNuevos.isEmpty()) {
            return false;
        }

        this.estado = EstadoEnum.PROCESANDO;
        this.alumnos = alumnosNuevos;
        this.mapAlumnos = TypesUtil.convertListToMap("id", alumnosNuevos);
        this.procesados = 0;
        this.avance = BigDecimal.ZERO;
        this.mensajes = new ArrayList();
        this.procesoActual = "Iniciando el proceso Matrícula-Cachimbos de " + alumnosNuevos.size() + " alumnos.";

        return true;
    }

    public synchronized void iniciarAlumno(Alumno alumno) {
        Alumno alu = mapAlumnos.get(alumno.getId());
        if (alu == null) {
            mapAlumnos.remove(alumno.getId());
            return;
        }
        this.procesoActual = "Se inicia la matrícula de " + alumno.getCodigo() + ".";
    }

    public synchronized void marcarAlumno(Alumno alumno) {
        Alumno alu = mapAlumnos.get(alumno.getId());
        if (alu == null) {
            mapAlumnos.remove(alumno.getId());
            return;
        }
        this.procesados++;

        BigDecimal proce = new BigDecimal(procesados * 100);
        BigDecimal total = new BigDecimal(alumnos.size());
        this.avance = proce.divide(total, 2, RoundingMode.HALF_DOWN);
        this.procesoActual = "Se terminó de procesar la matrícula de " + alumno.getCodigo() + ".";

        if (this.procesados == this.alumnos.size()) {
            this.estado = EstadoEnum.COMPLETO;
            this.procesoActual = "Se terminó de procesar la matrícula de " + procesados + " alumnos.";
        }
    }

    public synchronized List<String> getMensajes() {
        if (mensajes == null) {
            this.mensajes = new ArrayList();
        }
        return mensajes;
    }

    public String getProcesoActual() {
        if (this.estado == EstadoEnum.LIBRE) {
            return "No existe ningún proceso en matrícula en ejecución.";
        }
        return procesoActual;
    }

    public void setProcesoActual(String procesoActual) {
        this.procesoActual = procesoActual;
    }

    public List<Alumno> getAlumnos() {
        return alumnos;
    }

    public BigDecimal getAvance() {
        if (avance == null) {
            return BigDecimal.ZERO;
        }
        return avance;
    }

    public String getEstado() {
        return this.estado.name();
    }

    public boolean sigueProcesando() {
        if (this.estado == EstadoEnum.INICIADO) {
            return true;
        }
        if (this.estado == EstadoEnum.PROCESANDO) {
            return true;
        }
        return false;
    }

    public synchronized List<String> getErrores() {
        int cant = 0;
        Map<Integer, String> mapErrores = new HashMap();
        for (String msg : getMensajes()) {
            cant++;
            mapErrores.put(cant, msg);
        }
        List<String> errores = new ArrayList();
        for (int i = cant; i > 0; i--) {
            String msg = mapErrores.get(i);
            errores.add(msg);
        }
        return errores;
    }

}
