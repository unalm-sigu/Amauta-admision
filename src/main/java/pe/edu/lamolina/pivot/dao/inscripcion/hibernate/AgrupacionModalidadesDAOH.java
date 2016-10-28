package pe.edu.lamolina.pivot.dao.inscripcion.hibernate;

import pe.albatross.zelpers.dao.AbstractDAO;
import pe.edu.lamolina.pivot.dao.inscripcion.AgrupacionModalidadesDAO;
import pe.edu.lamolina.pivot.model.inscripcion.AgrupacionModalidades;
import org.springframework.stereotype.Repository;

@Repository
public class AgrupacionModalidadesDAOH extends AbstractDAO<AgrupacionModalidades> implements AgrupacionModalidadesDAO {

    public AgrupacionModalidadesDAOH() {
        super();
        setClazz(AgrupacionModalidades.class);
    }
}

