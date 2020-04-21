package pe.edu.lamolina.amauta.dao.academico;

import java.util.List;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.academico.CambioAulaGrupo;
import pe.edu.lamolina.model.academico.Seccion;

public interface CambioAulaGrupoDAO extends EasyDAO<CambioAulaGrupo> {

    CambioAulaGrupo find(CambioAulaGrupo cambioAulaGrupo);

//   void save(CambioAulaGrupo cambioAulaGrupo);
    List<CambioAulaGrupo> allBySeccion(Seccion seccion);

    List<CambioAulaGrupo> allBySecciones(List<Seccion> secciones);

}
