package pe.edu.lamolina.amauta.controller.academico.egresado;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.model.academico.Carrera;
import pe.edu.lamolina.model.academico.Egresado;

public interface EgresadoService {

    List<Egresado> allEgresadoByDynatable(DynatableFilter filter, List<Carrera> carreras, String todo);

    EgresadoResumen findResumenEgresado(List<Carrera> carreras, String name);

}
