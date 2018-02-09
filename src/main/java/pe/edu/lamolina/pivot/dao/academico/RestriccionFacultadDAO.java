package pe.edu.lamolina.pivot.dao.academico;

import java.util.List;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.academico.RestriccionFacultad;
import pe.edu.lamolina.model.academico.Seccion;
import pe.edu.lamolina.model.seguridad.Usuario;

public interface RestriccionFacultadDAO extends EasyDAO<RestriccionFacultad> {

    List<RestriccionFacultad> allActivasBySeccion(Seccion seccion);

    void updateEstadoFechaUsuario(RestriccionFacultad restriccionFacultad);

}
