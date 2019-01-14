package pe.edu.lamolina.pivot.dao.bienestar.hibernate;

import org.springframework.stereotype.Repository;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.bienestar.ReservaAula;
import pe.edu.lamolina.pivot.dao.bienestar.ReservaAulaDAO;

@Repository
public class ReservaAulaDAOH extends AbstractEasyDAO<ReservaAula> implements ReservaAulaDAO {

    public ReservaAulaDAOH() {
        super();
        setClazz(ReservaAula.class);
    }

}
