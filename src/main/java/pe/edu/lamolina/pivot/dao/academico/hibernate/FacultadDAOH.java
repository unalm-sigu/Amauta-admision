package pe.edu.lamolina.pivot.dao.academico.hibernate;

import pe.albatross.zelpers.dao.AbstractDAO;
import pe.edu.lamolina.pivot.dao.academico.FacultadDAO;
import pe.edu.lamolina.pivot.model.academico.Facultad;
import org.springframework.stereotype.Repository;

@Repository
public class FacultadDAOH extends AbstractDAO<Facultad> implements FacultadDAO {

    public FacultadDAOH() {
        super();
        setClazz(Facultad.class);
    }
}

