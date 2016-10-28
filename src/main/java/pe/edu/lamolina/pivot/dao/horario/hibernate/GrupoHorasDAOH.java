package pe.edu.lamolina.pivot.dao.horario.hibernate;

import pe.albatross.zelpers.dao.AbstractDAO;
import pe.edu.lamolina.pivot.dao.horario.GrupoHorasDAO;
import pe.edu.lamolina.pivot.model.horario.GrupoHoras;
import org.springframework.stereotype.Repository;

@Repository
public class GrupoHorasDAOH extends AbstractDAO<GrupoHoras> implements GrupoHorasDAO {

    public GrupoHorasDAOH() {
        super();
        setClazz(GrupoHoras.class);
    }
}

