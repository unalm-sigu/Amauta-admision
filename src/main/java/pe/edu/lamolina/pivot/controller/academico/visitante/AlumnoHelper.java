package pe.edu.lamolina.pivot.controller.academico.visitante;

import java.util.List;
import pe.albatross.zelpers.miscelanea.ObjectUtil;
import pe.edu.lamolina.pivot.model.general.Pais;
import pe.edu.lamolina.pivot.model.inscripcion.CarreraPostula;
import pe.edu.lamolina.pivot.model.inscripcion.ModalidadIngreso;
import pe.edu.lamolina.pivot.model.inscripcion.OpcionCarrera;
import pe.edu.lamolina.pivot.model.inscripcion.Postulante;

public class AlumnoHelper {

    public CarreraPostula getCarreraOpcion(Postulante postulante, Integer prioridad) {
        if (postulante.getOpcionCarrera() == null) {
            return null;
        }

        List<OpcionCarrera> opciones = postulante.getOpcionCarrera();
        for (OpcionCarrera opcion : opciones) {
            if (prioridad == opcion.getPrioridad().intValue()) {
                return opcion.getCarreraPostula();
            }
        }
        return null;
    }

    public String getIdCarreraPostula(Postulante postulante, Integer prioridad) {
        if (postulante.getOpcionCarrera() == null) {
            return "";
        }

        List<OpcionCarrera> opciones = postulante.getOpcionCarrera();
        for (OpcionCarrera opcion : opciones) {
            if (prioridad == opcion.getPrioridad().intValue()) {
                return opcion.getCarreraPostula().getId() + "";
            }
        }
        return "";
    }

    public String showCarrera(ModalidadIngreso modalidad, Integer opcion) {

        return "";
    }

    public String showColegio(Pais paisColegio) {
        if (paisColegio == null) {
            return "hide";
        }
        if (paisColegio.getCodigo() == null) {
            return "hide";
        }
        if (paisColegio.getCodigo().equals("PE")) {
            return "";
        } else {
            return "hide";
        }
    }

    public String showCodigoPais(Pais pais) {
        if (pais == null) {
            return "";
        }
        return pais.getCodigo();
    }

    public String showColegioExtrangero(Pais paisColegio) {
        if (paisColegio == null) {
            return "hide";
        }
        if (!paisColegio.getCodigo().equals("PE")) {
            return "";
        } else {
            return "hide";
        }
    }

    public String getCarreraOpcionId(Postulante postulante, Integer prioridad) {
        List<OpcionCarrera> opcionesCarrera = postulante.getOpcionCarrera();
        if (opcionesCarrera == null) {
            return null;
        }
        for (OpcionCarrera opcion : opcionesCarrera) {
            if (prioridad == opcion.getPrioridad().intValue()) {
                return String.valueOf(opcion.getId());
            }
        }
        return null;

    }

    public Object getParentTree(Object obj, String attr) {
        return ObjectUtil.getParentTree(obj, attr);
    }
}
