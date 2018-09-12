package pe.edu.lamolina.pivot.dao.horario.hibernate;

import java.util.Arrays;
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
    public List<Hora> all() {
        Octavia sql = Octavia.query()
                .from(Hora.class, "ho")
                .orderBy("ho.numero");

        return all(sql);
    }

    @Override
    public Hora findByNumeroHora(Integer numero) {
        Octavia sql = Octavia.query()
                .from(Hora.class, "ho")
                .filter("numero", numero)
                .orderBy("ho.numero");

        return find(sql);
    }

    @Override
    public List<Hora> allHoraInitOcho() {
        Octavia sql = Octavia.query()
                .from(Hora.class, "ho")
                .notIn("ho.numero", Arrays.asList(6, 7))
                .orderBy("ho.numero");

        return all(sql);
    }

}
