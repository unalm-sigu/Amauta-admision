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
    private Map<String, ProcesoLoad> procesos;
    private Map<String, Integer> grupos;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public void iniciar() {

        this.acciones = new ArrayList();
        this.procesos = new HashMap();
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

    public synchronized void agregarLog(String grupo, String proceso, String descripcion, boolean contar) {
        ProcesoLoad proxexo = procesos.get(proceso);
        if (proxexo == null) {
            Integer cant = grupos.get(grupo);
            proxexo = new ProcesoLoad(proceso, cant);
            procesos.put(proceso, proxexo);
        }
        if (contar) {
            proxexo.incrementer();
        }

        DateTime hoy = new DateTime();
        String log = hoy.toString("yyMMdd HH:mm:ss.SSS") + " " + proceso + " " + descripcion;
        this.acciones.add(log);
        if (acciones.size() > 20) {
            this.acciones.remove(0);
        }
    }

    public void reporte() {

    }
}
