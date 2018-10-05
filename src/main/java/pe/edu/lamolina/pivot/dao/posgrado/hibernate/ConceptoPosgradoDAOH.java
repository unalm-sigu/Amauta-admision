package pe.edu.lamolina.pivot.dao.posgrado.hibernate;

import java.math.BigDecimal;
import java.util.List;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.posgrado.ConceptoPosgrado;
import pe.edu.lamolina.pivot.dao.posgrado.ConceptoPosgradoDAO;

@Repository
public class ConceptoPosgradoDAOH extends AbstractEasyDAO<ConceptoPosgrado> implements ConceptoPosgradoDAO {

    public ConceptoPosgradoDAOH() {
        super();
        setClazz(ConceptoPosgrado.class);
    }

    @Override
    public List<ConceptoPosgrado> allMatricula() {
        Octavia sql = Octavia.query()
                .from(ConceptoPosgrado.class, "cp")
                .filter("cp.grupoMatricula", BigDecimal.ONE.intValue());
        return all(sql);
    }

}
