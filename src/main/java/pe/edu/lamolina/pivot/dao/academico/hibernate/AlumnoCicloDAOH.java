package pe.edu.lamolina.pivot.dao.academico.hibernate;

import pe.albatross.zelpers.dao.AbstractDAO;
import pe.edu.lamolina.pivot.dao.academico.AlumnoCicloDAO;
import pe.edu.lamolina.pivot.model.academico.AlumnoCiclo;
import org.springframework.stereotype.Repository;

@Repository
public class AlumnoCicloDAOH extends AbstractDAO<AlumnoCiclo> implements AlumnoCicloDAO {

    public AlumnoCicloDAOH() {
        super();
        setClazz(AlumnoCiclo.class);
    }
}

