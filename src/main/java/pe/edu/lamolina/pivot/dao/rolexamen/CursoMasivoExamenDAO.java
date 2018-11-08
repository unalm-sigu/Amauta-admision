package pe.edu.lamolina.pivot.dao.rolexamen;

import java.util.List;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.rolexamen.CursoMasivoExamen;
import pe.edu.lamolina.model.rolexamen.RolExamenes;

public interface CursoMasivoExamenDAO extends EasyDAO<CursoMasivoExamen> {

    List<CursoMasivoExamen> allActiveByRolExamen(RolExamenes rolExamenes);

}
