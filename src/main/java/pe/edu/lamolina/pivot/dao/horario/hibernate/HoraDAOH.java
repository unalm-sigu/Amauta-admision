package pe.edu.lamolina.pivot.dao.horario.hibernate;

import java.util.List;
import pe.albatross.zelpers.dao.AbstractDAO;
import pe.edu.lamolina.pivot.dao.horario.HoraDAO;
import pe.edu.lamolina.pivot.model.horario.Hora;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;

@Repository
public class HoraDAOH extends AbstractDAO<Hora> implements HoraDAO {

    public HoraDAOH() {
        super();
        setClazz(Hora.class);
    }

    @Override
    public List<Hora> allHora() {
        Octavia sql = Octavia.query()
                .from(Hora.class, "ho")
                .orderBy("ho.numero");
        return sql.all(getCurrentSession());
    }
    
}
