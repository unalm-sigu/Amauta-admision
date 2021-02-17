package pe.edu.lamolina.amauta.controller.academico.egresado;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;
import pe.edu.lamolina.model.academico.Carrera;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Egresado;
import pe.edu.lamolina.model.academico.Facultad;
import pe.edu.lamolina.model.bean.BusquedaBean;

public interface EgresadoService {

    List<Egresado> allEgresadoByDynatable(DynatableFilter filter, List<Carrera> carreras, String todo);

    EgresadoResumen findResumenEgresado(List<Carrera> carreras, String name);

    public List<CicloAcademico> allCiclosByNombre(String nombre, DataSessionPivot ds);

    public List<Facultad> allFacultadByNombre(String nombre, DataSessionPivot ds);

    public List<Carrera> allCarreraByNombre(String nombre, DataSessionPivot ds);

    public String downloadReporte(BusquedaBean busquedaBean, DataSessionPivot ds);

}
