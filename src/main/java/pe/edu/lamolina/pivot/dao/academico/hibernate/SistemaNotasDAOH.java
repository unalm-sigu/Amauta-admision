package pe.edu.lamolina.pivot.dao.academico.hibernate;

import pe.albatross.zelpers.dao.AbstractDAO;
import pe.edu.lamolina.pivot.dao.academico.SistemaNotasDAO;
import pe.edu.lamolina.pivot.model.academico.SistemaNotas;
import org.springframework.stereotype.Repository;

@Repository
public class SistemaNotasDAOH extends AbstractDAO<SistemaNotas> implements SistemaNotasDAO {

    public SistemaNotasDAOH() {
        super();
        setClazz(SistemaNotas.class);
    }
}

