package pe.edu.lamolina.pivot.dao.rolexamen;

import java.util.List;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.rolexamen.CursoMasivoExamen;
import pe.edu.lamolina.model.rolexamen.SeccionCursoMasivo;

public interface SeccionCursoMasivoDAO extends EasyDAO<SeccionCursoMasivo> {

    List<SeccionCursoMasivo> allSeccionByCursoMasivo(Long id); //Pendiente se debe cambiar

    List<SeccionCursoMasivo> allByCursosMasivos(List<CursoMasivoExamen> cursosMasivos);
    
}
