package pe.edu.lamolina.pivot.dao.academico.hibernate;

import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.pivot.dao.academico.CursoCachimbosDAO;
import pe.edu.lamolina.pivot.model.academico.CursoCachimbos;
import org.springframework.stereotype.Repository;
import pe.edu.lamolina.pivot.dao.academico.CursoCachimbosDAO;

@Repository
public class CursoCachimbosDAOH extends AbstractEasyDAO<CursoCachimbos> implements CursoCachimbosDAO {

    public CursoCachimbosDAOH() {
        super();
        setClazz(CursoCachimbos.class);
    }
}

