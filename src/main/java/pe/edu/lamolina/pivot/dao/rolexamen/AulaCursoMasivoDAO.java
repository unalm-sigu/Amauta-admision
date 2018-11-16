package pe.edu.lamolina.pivot.dao.rolexamen;

import java.util.List;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.rolexamen.AulaCursoMasivo;

public interface AulaCursoMasivoDAO extends EasyDAO<AulaCursoMasivo> {

    public List<AulaCursoMasivo> allAulaByCursoMasivo(Long id);


}
