package pe.edu.lamolina.pivot.dao.horario.hibernate;

import pe.albatross.zelpers.dao.AbstractDAO;
import pe.edu.lamolina.pivot.dao.horario.DiaHoraGrupoDAO;
import pe.edu.lamolina.pivot.model.horario.DiaHoraGrupo;
import org.springframework.stereotype.Repository;

@Repository
public class DiaHoraGrupoDAOH extends AbstractDAO<DiaHoraGrupo> implements DiaHoraGrupoDAO {

    public DiaHoraGrupoDAOH() {
        super();
        setClazz(DiaHoraGrupo.class);
    }
}

