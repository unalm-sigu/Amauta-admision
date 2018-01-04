package pe.edu.lamolina.pivot.dao.general.hibernate;

import pe.edu.lamolina.pivot.dao.general.IdiomaDAO;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.general.Idioma;

@Repository
public class IdiomaDAOH extends AbstractEasyDAO<Idioma> implements IdiomaDAO {

    public IdiomaDAOH() {
        super();
        setClazz(Idioma.class);
    }
}
