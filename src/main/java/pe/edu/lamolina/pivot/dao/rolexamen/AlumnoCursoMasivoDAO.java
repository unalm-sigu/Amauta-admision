package pe.edu.lamolina.pivot.dao.rolexamen;

import java.util.List;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.rolexamen.AlumnoCursoMasivo;

public interface AlumnoCursoMasivoDAO extends EasyDAO<AlumnoCursoMasivo> {

    public List<AlumnoCursoMasivo> allAlumnoByCursoMasivo(Long id);


}
