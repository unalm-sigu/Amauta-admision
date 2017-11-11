package pe.edu.lamolina.pivot.dao.horario.hibernate;

import pe.albatross.zelpers.dao.AbstractDAO;
import org.springframework.stereotype.Repository;
import pe.edu.lamolina.pivot.dao.horario.TipoGrupoHorasDAO;
import pe.edu.lamolina.pivot.model.horario.TipoGrupoHoras;

@Repository
public class TipoGrupoHorasDAOH extends AbstractDAO<TipoGrupoHoras> implements TipoGrupoHorasDAO {

    public TipoGrupoHorasDAOH() {
        super();
        setClazz(TipoGrupoHoras.class);
    }

}

