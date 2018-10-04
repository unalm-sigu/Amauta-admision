package pe.edu.lamolina.pivot.dao.posgrado;

import java.util.List;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.posgrado.ConceptoPosgrado;

public interface ConceptoPosgradoDAO extends EasyDAO<ConceptoPosgrado> {

    List<ConceptoPosgrado> allMatricula();

}
