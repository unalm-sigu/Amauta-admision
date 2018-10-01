package pe.edu.lamolina.pivot.dao.posgrado;

import java.util.List;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.posgrado.TarifaCarrera;
import pe.edu.lamolina.model.posgrado.TarifaConcepto;

public interface TarifaConceptoDAO extends EasyDAO<TarifaConcepto> {

    List<TarifaConcepto> allByTarifaCarrera(TarifaCarrera tarifaCarrera);

    void deleteAllByTarifaCarrera(TarifaCarrera tarifaCarrera);

}
