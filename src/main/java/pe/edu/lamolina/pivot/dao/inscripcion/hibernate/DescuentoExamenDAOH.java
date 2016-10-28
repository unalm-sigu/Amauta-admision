package pe.edu.lamolina.pivot.dao.inscripcion.hibernate;

import pe.albatross.zelpers.dao.AbstractDAO;
import pe.edu.lamolina.pivot.dao.inscripcion.DescuentoExamenDAO;
import pe.edu.lamolina.pivot.model.inscripcion.DescuentoExamen;
import org.springframework.stereotype.Repository;

@Repository
public class DescuentoExamenDAOH extends AbstractDAO<DescuentoExamen> implements DescuentoExamenDAO {

    public DescuentoExamenDAOH() {
        super();
        setClazz(DescuentoExamen.class);
    }
}

