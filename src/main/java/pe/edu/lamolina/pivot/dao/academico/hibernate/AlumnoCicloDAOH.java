package pe.edu.lamolina.pivot.dao.academico.hibernate;

import pe.albatross.zelpers.dao.AbstractDAO;
import pe.edu.lamolina.pivot.dao.academico.AlumnoCicloDAO;
import org.springframework.stereotype.Repository;
import pe.edu.lamolina.model.academico.AlumnoCiclo;

@Repository
public class AlumnoCicloDAOH extends AbstractDAO<AlumnoCiclo> implements AlumnoCicloDAO {

    public AlumnoCicloDAOH() {
        super();
        setClazz(AlumnoCiclo.class);
    }
}
