package pe.edu.lamolina.pivot.dao.general.hibernate;

import pe.edu.lamolina.pivot.dao.general.PaisDAO;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.general.Pais;

@Repository
public class PaisDAOH extends AbstractEasyDAO<Pais> implements PaisDAO {

    public PaisDAOH() {
        super();
        setClazz(Pais.class);
    }
}
