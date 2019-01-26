package pe.edu.lamolina.pivot.dao.rolexamen;

import java.util.List;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.rolexamen.AulaCursoMasivo;
import pe.edu.lamolina.model.rolexamen.CursoMasivoExamen;

public interface AulaCursoMasivoDAO extends EasyDAO<AulaCursoMasivo> {

    void deleteByCursoMasivo(CursoMasivoExamen cursoMasivoExamen);

    List<AulaCursoMasivo> allByCursoMasivo(CursoMasivoExamen cursoMasivo);

    List<AulaCursoMasivo> allByCursosMasivos(List<CursoMasivoExamen> cursosMasivosExamenes);
}
