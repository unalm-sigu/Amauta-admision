package pe.edu.lamolina.pivot.dao.academico;

import java.util.List;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.academico.RestriccionModalidad;
import pe.edu.lamolina.model.academico.Seccion;

public interface RestriccionModalidadDAO extends EasyDAO<RestriccionModalidad> {

    List<RestriccionModalidad> allActivasBySeccion(Seccion seccion);

    void updateEstadoFechaUsuario(RestriccionModalidad restriccionModalidad);

}
