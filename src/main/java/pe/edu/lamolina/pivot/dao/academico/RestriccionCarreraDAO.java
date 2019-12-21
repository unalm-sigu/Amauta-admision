package pe.edu.lamolina.pivot.dao.academico;

import java.util.List;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.RestriccionCarrera;
import pe.edu.lamolina.model.academico.Seccion;

public interface RestriccionCarreraDAO extends EasyDAO<RestriccionCarrera> {

    List<RestriccionCarrera> allBySeccion(Seccion seccion);

    List<RestriccionCarrera> allActivasBySeccion(Seccion seccion);

    List<RestriccionCarrera> allActivasBySecciones(List<Seccion> secciones);

    void updateEstadoFechaUsuario(RestriccionCarrera restriccionCarrera);

    void deleteAllByCiclo(CicloAcademico ciclo);

    int saveList(List<RestriccionCarrera> restricciones);

}
