package pe.edu.lamolina.pivot.dao.calificacion.hibernate;

import pe.albatross.zelpers.dao.AbstractDAO;
import pe.edu.lamolina.pivot.dao.calificacion.TemaExamenModalidadDAO;
import pe.edu.lamolina.pivot.model.calificacion.TemaExamenModalidad;
import org.springframework.stereotype.Repository;

@Repository
public class TemaExamenModalidadDAOH extends AbstractDAO<TemaExamenModalidad> implements TemaExamenModalidadDAO {

    public TemaExamenModalidadDAOH() {
        super();
        setClazz(TemaExamenModalidad.class);
    }
}

