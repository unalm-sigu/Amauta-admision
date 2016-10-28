package pe.edu.lamolina.pivot.dao.horario.hibernate;

import pe.albatross.zelpers.dao.AbstractDAO;
import pe.edu.lamolina.pivot.dao.horario.HorarioSeccionDAO;
import pe.edu.lamolina.pivot.model.horario.HorarioSeccion;
import org.springframework.stereotype.Repository;

@Repository
public class HorarioSeccionDAOH extends AbstractDAO<HorarioSeccion> implements HorarioSeccionDAO {

    public HorarioSeccionDAOH() {
        super();
        setClazz(HorarioSeccion.class);
    }
}

