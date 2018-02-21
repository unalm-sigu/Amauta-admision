package pe.edu.lamolina.pivot.dao.academico;

import java.util.List;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.academico.RestriccionCarrera;
import pe.edu.lamolina.model.academico.Seccion;

public interface RestriccionCarreraDAO extends EasyDAO<RestriccionCarrera> {

    List<RestriccionCarrera> allActivasBySeccion(Seccion seccion);

    void updateEstadoFechaUsuario(RestriccionCarrera restriccionCarrera);
}
