package pe.edu.lamolina.amauta.dao.mensajeria.hibernate;

import org.springframework.stereotype.Repository;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.amauta.dao.mensajeria.MensajeSistemaDAO;
import pe.edu.lamolina.model.social.MensajeSistema;

@Repository
public class MensajeSistemaDAOH extends AbstractEasyDAO<MensajeSistema> implements MensajeSistemaDAO {

    public MensajeSistemaDAOH() {
        super();
        setClazz(MensajeSistema.class);
    }
}
