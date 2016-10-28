package pe.edu.lamolina.pivot.dao.inscripcion.hibernate;

import pe.albatross.zelpers.dao.AbstractDAO;
import pe.edu.lamolina.pivot.dao.inscripcion.ProspectoDAO;
import pe.edu.lamolina.pivot.model.inscripcion.Prospecto;
import org.springframework.stereotype.Repository;

@Repository
public class ProspectoDAOH extends AbstractDAO<Prospecto> implements ProspectoDAO {

    public ProspectoDAOH() {
        super();
        setClazz(Prospecto.class);
    }
}

