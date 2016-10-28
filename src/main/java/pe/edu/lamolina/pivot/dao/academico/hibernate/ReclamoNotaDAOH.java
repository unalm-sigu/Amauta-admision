package pe.edu.lamolina.pivot.dao.academico.hibernate;

import pe.albatross.zelpers.dao.AbstractDAO;
import pe.edu.lamolina.pivot.dao.academico.ReclamoNotaDAO;
import pe.edu.lamolina.pivot.model.academico.ReclamoNota;
import org.springframework.stereotype.Repository;

@Repository
public class ReclamoNotaDAOH extends AbstractDAO<ReclamoNota> implements ReclamoNotaDAO {

    public ReclamoNotaDAOH() {
        super();
        setClazz(ReclamoNota.class);
    }
}

