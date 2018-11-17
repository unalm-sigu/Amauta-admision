package pe.edu.lamolina.pivot.dao.rolexamen;

import java.util.List;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.rolexamen.AlumnoCursoMasivo;
import pe.edu.lamolina.model.rolexamen.CursoMasivoExamen;

public interface AlumnoCursoMasivoDAO extends EasyDAO<AlumnoCursoMasivo> {

    List<AlumnoCursoMasivo> allAlumnoByCursoMasivo(CursoMasivoExamen cursoMasivo);
}
