package pe.edu.lamolina.pivot.dao.general.hibernate;

import pe.albatross.zelpers.dao.AbstractDAO;
import pe.edu.lamolina.pivot.dao.general.DiaDAO;
import pe.edu.lamolina.pivot.model.general.Dia;
import org.springframework.stereotype.Repository;

@Repository
public class DiaDAOH extends AbstractDAO<Dia> implements DiaDAO {

    public DiaDAOH() {
        super();
        setClazz(Dia.class);
    }
}

