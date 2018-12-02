package pe.edu.lamolina.pivot.dao.academico;

import java.util.List;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.RestriccionRepitencia;
import pe.edu.lamolina.model.academico.Seccion;

public interface RestriccionRepitenciaDAO extends EasyDAO<RestriccionRepitencia> {

    List<RestriccionRepitencia> allBySeccion(Seccion seccion);

    List<RestriccionRepitencia> allActivasBySeccion(Seccion seccion);

    List<RestriccionRepitencia> allActivasBySecciones(List<Seccion> secciones);

    void updateEstadoFechaUsuario(RestriccionRepitencia restriccionRepitencia);

    void deleteAllByCiclo(CicloAcademico ciclo);

}
