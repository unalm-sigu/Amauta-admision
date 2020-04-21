package pe.edu.lamolina.amauta.dao.tramite;

import java.util.List;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.tramite.Resolucion;
import pe.edu.lamolina.model.tramite.TramiteTraslado;

public interface TramiteTrasladoDAO extends EasyDAO<TramiteTraslado> {

    List<TramiteTraslado> allByResolucion(Resolucion resolucion);

    List<TramiteTraslado> allByAlumno(Alumno alumno);
}
