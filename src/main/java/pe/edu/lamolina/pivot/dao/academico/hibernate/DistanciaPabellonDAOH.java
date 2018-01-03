package pe.edu.lamolina.pivot.dao.academico.hibernate;

import pe.edu.lamolina.pivot.dao.academico.DistanciaPabellonDAO;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.academico.DistanciaPabellon;

@Repository
public class DistanciaPabellonDAOH extends AbstractEasyDAO<DistanciaPabellon> implements DistanciaPabellonDAO {

    public DistanciaPabellonDAOH() {
        super();
        setClazz(DistanciaPabellon.class);
    }
}
