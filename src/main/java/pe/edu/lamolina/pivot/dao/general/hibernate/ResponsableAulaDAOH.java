package pe.edu.lamolina.pivot.dao.general.hibernate;

import org.springframework.stereotype.Repository;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.general.ResponsableAula;
import pe.edu.lamolina.pivot.dao.general.ResponsableAulaDAO;

@Repository
public class ResponsableAulaDAOH extends AbstractEasyDAO<ResponsableAula> implements ResponsableAulaDAO {

    public ResponsableAulaDAOH() {
        this.setClazz(ResponsableAula.class);
    }

}
