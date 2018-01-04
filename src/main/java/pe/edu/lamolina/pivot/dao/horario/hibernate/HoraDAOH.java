package pe.edu.lamolina.pivot.dao.horario.hibernate;

import java.util.List;
import pe.edu.lamolina.pivot.dao.horario.HoraDAO;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.horario.Hora;

@Repository
public class HoraDAOH extends AbstractEasyDAO<Hora> implements HoraDAO {

    public HoraDAOH() {
        super();
        setClazz(Hora.class);
    }

    @Override
    public List<Hora> allHora() {
        Octavia sql = Octavia.query()
                .from(Hora.class, "ho")
                .orderBy("ho.numero");

        return all(sql);
    }

}
