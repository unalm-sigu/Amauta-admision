package pe.edu.lamolina.pivot.controller.academico.plancurricular;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.stereotype.Component;
import pe.edu.lamolina.model.academico.Carrera;

@Component
public class VisorAsignaCurricula {

    private enum EstadoEnum {
        LIBRE, INICIADO, OCUPADO, COMPLETO
    };

    public enum AccionEnum {
        DESVINCULA, GENERA
    };

    private Map<Long, Carrera> mapCarreras = new LinkedHashMap();
    private Map<Long, Integer> mapContadorAlumnos = new LinkedHashMap();
    private Map<Long, Integer> mapTopeAlumnos = new LinkedHashMap();
    private Map<Long, Integer> mapPlanAsignado = new LinkedHashMap();
    private Map<Long, EstadoEnum> mapEstados = new LinkedHashMap();
    private Map<Long, AccionEnum> mapAcciones = new LinkedHashMap();

    public synchronized boolean addCarrera(Carrera carr, AccionEnum accion) {
        if (mapCarreras.isEmpty()) {
            createDataCarrera(carr, accion);
            return true;
        }

        Carrera carrera = mapCarreras.get(carr.getId());
        if (carrera != null) {
            EstadoEnum estado = mapEstados.get(carr.getId());
            if (estado == EstadoEnum.COMPLETO || estado == EstadoEnum.LIBRE) {
                createDataCarrera(carr, accion);
                return true;
            }
            return false;
        }

        createDataCarrera(carr, accion);
        return true;
    }

    public synchronized boolean procesoMitadCarrera(Carrera carr) {
        if (mapCarreras.isEmpty()) {
            return false;
        }
        Carrera carrera = mapCarreras.get(carr.getId());
        if (carrera != null) {
            int tope = mapTopeAlumnos.get(carr.getId());
            int conta = mapContadorAlumnos.get(carr.getId());
            int asignado = mapPlanAsignado.get(carr.getId());
            if (tope == conta * 2 && asignado == 0) {
                mapPlanAsignado.put(carr.getId(), 1);
                return true;
            }
        }
        return false;
    }

    public boolean existeCarrera(Carrera carr) {
        if (mapCarreras.isEmpty()) {
            return false;
        }

        Carrera carrera = mapCarreras.get(carr.getId());
        if (carrera != null) {
            EstadoEnum estado = mapEstados.get(carr.getId());
            if (estado == EstadoEnum.COMPLETO || estado == EstadoEnum.LIBRE) {
                return false;
            }
            return true;
        }
        return false;
    }

    private void createDataCarrera(Carrera carr, AccionEnum accion) {
        mapCarreras.put(carr.getId(), carr);
        mapContadorAlumnos.put(carr.getId(), 0);
        mapTopeAlumnos.put(carr.getId(), 0);
        mapEstados.put(carr.getId(), VisorAsignaCurricula.EstadoEnum.INICIADO);
        mapAcciones.put(carr.getId(), accion);
        mapPlanAsignado.put(carr.getId(), 0);

    }

    public synchronized void putTope(Carrera carr, int alumnos) {
        if (mapCarreras.isEmpty()) {
            return;
        }
        if (alumnos <= 0) {
            return;
        }

        Carrera carrera = mapCarreras.get(carr.getId());
        if (carrera == null) {
            return;
        }
        mapTopeAlumnos.put(carr.getId(), alumnos);
        mapEstados.put(carr.getId(), VisorAsignaCurricula.EstadoEnum.OCUPADO);
    }

    public synchronized void incrementar(Carrera carr) {
        if (mapCarreras.isEmpty()) {
            return;
        }

        Carrera carrera = mapCarreras.get(carr.getId());
        if (carrera == null) {
            return;
        }

        int tope = mapTopeAlumnos.get(carr.getId());
        int conta = mapContadorAlumnos.get(carr.getId());
        conta++;
        mapContadorAlumnos.put(carr.getId(), conta);
        if (tope <= conta) {
            mapEstados.put(carr.getId(), VisorAsignaCurricula.EstadoEnum.COMPLETO);
        }
    }

    public Integer porcentajeAvance(Carrera carr) {
        if (!existeCarrera(carr)) {
            return 0;
        }
        int tope = mapTopeAlumnos.get(carr.getId());
        int conta = mapContadorAlumnos.get(carr.getId());
        if (tope == 0) {
            return 0;
        }
        BigDecimal div = new BigDecimal(conta * 100).divide(new BigDecimal(tope), 0, RoundingMode.FLOOR);
        return div.intValue();
    }

    public String reporte(Carrera carr) {
        if (!existeCarrera(carr)) {
            return "No hay proceso de asignación masiva de planes curriculares en esta especialidad";
        }
        int tope = mapTopeAlumnos.get(carr.getId());
        int conta = mapContadorAlumnos.get(carr.getId());
        EstadoEnum estado = mapEstados.get(carr.getId());
        if (estado == EstadoEnum.OCUPADO) {
            Integer mitad = new BigDecimal(tope).divide(new BigDecimal(2), 0, RoundingMode.HALF_DOWN).intValue();
            if (conta * 2 < tope) {
                return "Eliminaddo avance curricular previo a " + conta + " alumnos de un total de " + mitad;
            } else {
                return "Generando avance curricular a " + (conta - mitad) + " alumnos de un total de " + mitad;
            }
        }
        if (estado == EstadoEnum.INICIADO) {
            return "Información está siendo preparada";
        }
        if (estado == EstadoEnum.COMPLETO) {
            return "Proceso ha finalizado";
        }
        return "";
    }

    public Carrera getCarreraActiva() {

        Carrera carrera = null;
        for (Map.Entry<Long, EstadoEnum> entry : mapEstados.entrySet()) {
            EstadoEnum estado = entry.getValue();
            if (estado == EstadoEnum.INICIADO || estado == EstadoEnum.OCUPADO) {
                carrera = mapCarreras.get(entry.getKey());
            }
        }

        return carrera;
    }
}
