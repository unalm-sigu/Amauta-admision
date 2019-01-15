package pe.edu.lamolina.pivot.dao.bienestar.hibernate;

import org.springframework.stereotype.Repository;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.bienestar.AulaReservada;
import pe.edu.lamolina.pivot.dao.bienestar.AulaReservadaDAO;

@Repository
public class AulaReservadaDAOH extends AbstractEasyDAO<AulaReservada> implements AulaReservadaDAO {

    public AulaReservadaDAOH() {
        super();
        setClazz(AulaReservada.class);
    }

}
