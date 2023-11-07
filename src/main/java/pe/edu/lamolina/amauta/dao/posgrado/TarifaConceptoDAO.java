package pe.edu.lamolina.amauta.dao.posgrado;

import java.util.List;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.posgrado.TarifaCarrera;
import pe.edu.lamolina.model.posgrado.TarifaConcepto;
import pe.edu.lamolina.model.posgrado.concepto.ConceptoPosgrado;

public interface TarifaConceptoDAO extends EasyDAO<TarifaConcepto> {

    List<TarifaConcepto> allByTarifaCarrera(TarifaCarrera tarifaCarrera);

    TarifaConcepto findByConceptoPosgrado(ConceptoPosgrado conceptoPosgrado);

}
