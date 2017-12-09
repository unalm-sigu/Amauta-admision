package pe.edu.lamolina.pivot.dao.horario.hibernate;

import java.util.List;
import pe.albatross.zelpers.dao.AbstractDAO;
import pe.edu.lamolina.pivot.dao.horario.HorarioSeccionDAO;
import pe.edu.lamolina.pivot.model.horario.HorarioSeccion;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.edu.lamolina.pivot.model.academico.Seccion;

@Repository
public class HorarioSeccionDAOH extends AbstractDAO<HorarioSeccion> implements HorarioSeccionDAO {

    public HorarioSeccionDAOH() {
        super();
        setClazz(HorarioSeccion.class);
    }

    @Override
    public List<HorarioSeccion> allBySeccion(List<Seccion> secciones) {
        Octavia sql = Octavia.query()
                .from(HorarioSeccion.class, "hs")
                .join("dia di","hora ho","seccion sec")
                .in("sec.id", secciones);
        return sql.all(getCurrentSession());
    }
}
