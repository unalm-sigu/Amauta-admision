package pe.edu.lamolina.amauta.controller.academico.graduado;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.model.academico.Carrera;
import pe.edu.lamolina.model.tramite.ObtencionGrado;

public interface GraduadoService {

    List<ObtencionGrado> allEgresadoByDynatable(DynatableFilter filter, List<Carrera> carreras, String todo);

    GraduadoResumen findResumenEgresado(List<Carrera> carreras, String name);

}
