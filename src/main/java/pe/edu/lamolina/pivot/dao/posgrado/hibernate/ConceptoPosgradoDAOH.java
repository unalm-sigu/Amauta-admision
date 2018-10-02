package pe.edu.lamolina.pivot.dao.posgrado.hibernate;

import org.springframework.stereotype.Repository;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.posgrado.ConceptoPosgrado;
import pe.edu.lamolina.pivot.dao.posgrado.ConceptoPosgradoDAO;

@Repository
public class ConceptoPosgradoDAOH extends AbstractEasyDAO<ConceptoPosgrado> implements ConceptoPosgradoDAO {

    public ConceptoPosgradoDAOH() {
        super();
        setClazz(ConceptoPosgrado.class);
    }

}
