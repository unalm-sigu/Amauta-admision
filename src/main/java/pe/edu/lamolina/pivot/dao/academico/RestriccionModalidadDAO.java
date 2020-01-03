package pe.edu.lamolina.pivot.dao.academico;

import java.util.List;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.RestriccionModalidad;
import pe.edu.lamolina.model.academico.Seccion;

public interface RestriccionModalidadDAO extends EasyDAO<RestriccionModalidad> {

    List<RestriccionModalidad> allBySeccion(Seccion seccion);

    List<RestriccionModalidad> allActivasBySeccion(Seccion seccion);

    List<RestriccionModalidad> allActivasBySecciones(List<Seccion> secciones);

    void updateEstadoFechaUsuario(RestriccionModalidad restriccionModalidad);

    void deleteAllByCiclo(CicloAcademico ciclo);

    int saveList(List<RestriccionModalidad> restricciones);

}
