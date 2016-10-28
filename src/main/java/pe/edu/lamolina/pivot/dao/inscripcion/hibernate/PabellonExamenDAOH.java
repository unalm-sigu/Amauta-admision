package pe.edu.lamolina.pivot.dao.inscripcion.hibernate;

import pe.albatross.zelpers.dao.AbstractDAO;
import pe.edu.lamolina.pivot.dao.inscripcion.PabellonExamenDAO;
import pe.edu.lamolina.pivot.model.inscripcion.PabellonExamen;
import org.springframework.stereotype.Repository;

@Repository
public class PabellonExamenDAOH extends AbstractDAO<PabellonExamen> implements PabellonExamenDAO {

    public PabellonExamenDAOH() {
        super();
        setClazz(PabellonExamen.class);
    }
}

