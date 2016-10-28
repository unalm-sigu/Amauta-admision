package pe.edu.lamolina.pivot.dao.calificacion.hibernate;

import pe.albatross.zelpers.dao.AbstractDAO;
import pe.edu.lamolina.pivot.dao.calificacion.TemaCicloDAO;
import pe.edu.lamolina.pivot.model.calificacion.TemaCiclo;
import org.springframework.stereotype.Repository;

@Repository
public class TemaCicloDAOH extends AbstractDAO<TemaCiclo> implements TemaCicloDAO {

    public TemaCicloDAOH() {
        super();
        setClazz(TemaCiclo.class);
    }
}

