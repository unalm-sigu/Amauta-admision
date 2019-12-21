package pe.edu.lamolina.pivot.dao.academico;

import java.util.List;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.RestriccionFacultad;
import pe.edu.lamolina.model.academico.Seccion;

public interface RestriccionFacultadDAO extends EasyDAO<RestriccionFacultad> {

    List<RestriccionFacultad> allBySeccion(Seccion seccion);

    List<RestriccionFacultad> allActivasBySeccion(Seccion seccion);

    List<RestriccionFacultad> allActivasBySecciones(List<Seccion> secciones);

    void updateEstadoFechaUsuario(RestriccionFacultad restriccionFacultad);

    void deleteAllByCiclo(CicloAcademico ciclo);

    int saveList(List<RestriccionFacultad> restricciones);

}
