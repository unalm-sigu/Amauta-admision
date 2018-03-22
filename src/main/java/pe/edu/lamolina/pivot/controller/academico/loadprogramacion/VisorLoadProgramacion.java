package pe.edu.lamolina.pivot.controller.academico.loadprogramacion;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class VisorLoadProgramacion {

    private String ciclo;
    private String proceso;

    private List<String> acciones;
    private List<String> errores;
    private Map<String, ProcesoLoad> procesos;
    private Map<String, Integer> grupos;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public void iniciar() {
        this.acciones = new ArrayList();
        this.errores = new ArrayList();
        this.procesos = new HashMap();
        this.grupos = new HashMap();
        this.ciclo = null;
    }

    public void setCiclo(String ciclo) {
        this.ciclo = ciclo;
    }

    public synchronized void setProceso(String proceso) {
        this.proceso = proceso;
    }

    public synchronized void inicializar(String grupo, int cantidad) {
        grupos.put(grupo, cantidad);
    }

    public synchronized void agregarLog(String grupo, String proceso, String descripcion, boolean contar, String tipo) {
        ProcesoLoad proxexo = procesos.get(proceso);
        if (proxexo == null) {
            Integer cant = grupos.get(grupo);
            if (cant != null) {
                proxexo = new ProcesoLoad(proceso, cant);
                procesos.put(proceso, proxexo);
            }
        }
        if (contar) {
            proxexo.incrementer();
        }

        if (grupo.equals("ciclo")) {
            this.ciclo = descripcion;
        }

        DateTime hoy = new DateTime();
        String log = tipo.toLowerCase() + "::::" + hoy.toString("yyMMdd HH:mm:ss.SSS") + " " + proceso + " " + descripcion;
        if (tipo.equalsIgnoreCase("error-proceso")) {
            this.errores.add(log);
        } else {
            this.acciones.add(log);
            if (acciones.size() > 20) {
                this.acciones.remove(0);
            }
        }

    }

    public List<String> reporte() {
        List<String> info = new ArrayList();
        if (this.ciclo != null) {
            info.add(this.ciclo);
        }
        info.add("Procesos ejecutados o en ejecución");
        for (Map.Entry<String, ProcesoLoad> entry : procesos.entrySet()) {
            ProcesoLoad proxexo = entry.getValue();
            String log = proxexo.getProceso() + " ";
            log += new DateTime(proxexo.getHoraInicio()).toString("HH:mm:ss.SSS") + " ";
            if (proxexo.getHoraFinal() != null) {
                log += new DateTime(proxexo.getHoraFinal()).toString("HH:mm:ss.SSS");
            }
            log += " " + proxexo.getAvance() + " de " + proxexo.getCantidad();
            info.add(log);
        }
        info.add("");

        for (String rev : this.acciones) {
            info.add(rev);
        }

        if (!this.errores.isEmpty()) {
            info.add("");
            info.add("Errores");
            for (String err : this.errores) {
                info.add(err);
            }
        }

        return info;
    }
}
