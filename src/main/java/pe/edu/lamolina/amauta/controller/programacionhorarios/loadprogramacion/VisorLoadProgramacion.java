package pe.edu.lamolina.amauta.controller.programacionhorarios.loadprogramacion;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import pe.edu.lamolina.model.academico.Alumno;

@Component
public class VisorLoadProgramacion {

    private String ciclo;
    private boolean stop;
    private boolean ejecutando;
    private Date ultimaEjecucion;

    private Map<String, AlumnoBlocked> mapAlumnos;
    private List<String> acciones;
    private List<String> errores;
    private Map<String, ProcesoLoad> procesos;
    private Map<String, Integer> grupos;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public void iniciar() {
        this.acciones = new ArrayList();
        this.errores = new ArrayList();
        this.procesos = new LinkedHashMap();
        this.grupos = new LinkedHashMap();
        this.mapAlumnos = new LinkedHashMap();
        this.ciclo = null;
        this.stop = false;
        this.ejecutando = true;
        this.ultimaEjecucion = null;
    }

    public void setCiclo(String ciclo) {
        this.ciclo = ciclo;
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
        if (contar && proxexo != null) {
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
        this.ultimaEjecucion = new Date();
    }

    private void revisarEjecucion() {
        if (this.ultimaEjecucion == null) {
            return;
        }
        if (!this.ejecutando) {
            return;
        }

        Date hoy = new Date();
        if (hoy.getTime() - this.ultimaEjecucion.getTime() > 60 * 1500) {
            this.ejecutando = false;
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
        if (!this.ejecutando) {
            info.add("fin::::Proceso de carga ha finalizado");
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

        this.revisarEjecucion();

        return info;
    }

    public boolean isStop() {
        return stop;
    }

    public void setStop(boolean stop) {
        this.stop = stop;
    }

    public boolean isEjecutando() {
        return ejecutando;
    }

    public List<AlumnoBlocked> getAlumnos() {

        List<AlumnoBlocked> lista = new ArrayList();
        if (this.mapAlumnos != null) {
            limpiarLista();
            lista.addAll(this.mapAlumnos.values());

        }
        Collections.sort(lista, new AlumnoBlocked.CompareAlumno());
        return lista;
    }

    public void limpiarLista() {
        if (this.mapAlumnos == null) {
            return;
        }

        Map<String, String> mapVerificarlos = new LinkedHashMap();

        List<AlumnoBlocked> lista = new ArrayList(this.mapAlumnos.values());
        for (AlumnoBlocked alub : lista) {
            if (alub.getFechaDesbloqueo() == null) {
                String code = alub.getAlumno().getCodigo();
                mapVerificarlos.put(code, code);
            }
        }
        for (AlumnoBlocked alub : lista) {
            if (alub.getFechaDesbloqueo() == null) {
                continue;
            }
            if (new Date().getTime() - alub.getFechaDesbloqueo().getTime() > 10000) {
                String code = mapVerificarlos.get(alub.getAlumno().getCodigo());
                if (code == null) {
                    this.mapAlumnos.remove(alub.getAlumno().getCodigo() + "-" + alub.getSeccion());
                }
            }
        }

    }

    public synchronized void addAlumno(Alumno alumno, String seccion, long inicio) {
        Alumno aluTmo = new Alumno(alumno.getId(), alumno.getCodigo(), alumno.getPersona());
        AlumnoBlocked blok = new AlumnoBlocked(aluTmo, seccion, inicio);
        this.mapAlumnos.put(alumno.getCodigo() + "-" + seccion, blok);
    }

    public synchronized void removeAlumno(Alumno alumno, String seccion) {
        AlumnoBlocked alu = this.mapAlumnos.get(alumno.getCodigo() + "-" + seccion);
        alu.setEstado("DES-BLOQUEADO");
        alu.setFechaDesbloqueo(new Date());

        limpiarLista();
    }

    public synchronized void bloqueadoAlumno(Alumno alumno, String seccion) {
        AlumnoBlocked alu = this.mapAlumnos.get(alumno.getCodigo() + "-" + seccion);
        alu.setEstado("BLOQUEADO");
        alu.setFechaBloqueo(new Date());
    }

}
