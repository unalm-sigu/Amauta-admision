package pe.edu.lamolina.pivot.dao.bienestar.hibernate;

import java.util.List;
import org.hibernate.Query;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.bienestar.AulaReservada;
import pe.edu.lamolina.model.bienestar.ReservaAula;
import pe.edu.lamolina.pivot.dao.bienestar.AulaReservadaDAO;

@Repository
public class AulaReservadaDAOH extends AbstractEasyDAO<AulaReservada> implements AulaReservadaDAO {

    public AulaReservadaDAOH() {
        super();
        setClazz(AulaReservada.class);
    }

    @Override
    public List<AulaReservada> allByReservaAula(ReservaAula reservaAula) {
        Octavia sql = Octavia.query()
                .from(AulaReservada.class, "ar")
                .join("reservaAula ra", "aula au")
                .leftJoin("dia d", "hora h")
                .filter("ra.id", reservaAula);
        return all(sql);
    }

    @Override
    public void deleteAllByReservaAula(ReservaAula reservaAula) {
        String strQuery = "delete from AulaReservada ar where ar.reservaAula.id=:IDRESERVAAULA";
        Query query = getCurrentSession().createQuery(strQuery);
        query.setLong("IDRESERVAAULA", reservaAula.getId());
        query.executeUpdate();
    }

    @Override
    public List<AulaReservada> allByReservaAulas(List<ReservaAula> reservaAulas) {
        Octavia sql = Octavia.query()
                .from(AulaReservada.class, "ar")
                .join("reservaAula ra", "aula au")
                .leftJoin("dia d", "hora h")
                .in("ra.id", reservaAulas);
        return all(sql);
    }

}
