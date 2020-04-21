package pe.edu.lamolina.amauta.dao.academico;

import java.util.List;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.academico.CursoConvalidado;
import pe.edu.lamolina.model.tramite.TramiteTraslado;

public interface CursoConvalidadoDAO extends EasyDAO<CursoConvalidado> {

    List<CursoConvalidado> allInTramiteTraslado(List<TramiteTraslado> listTramiteTraslado);

    void updateColumns(CursoConvalidado cursoConvalidado, String... params);

}
