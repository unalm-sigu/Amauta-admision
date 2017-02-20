package pe.edu.lamolina.pivot.dao.auditoria.hibernate;

import org.springframework.stereotype.Repository;
import pe.albatross.zelpers.dao.AbstractDAO;
import pe.edu.lamolina.pivot.dao.auditoria.ControlDeActasDAO;
import pe.edu.lamolina.pivot.model.auditoria.ControlDeActas;

@Repository
public class ControlDeActasDAOH extends AbstractDAO<ControlDeActas> implements ControlDeActasDAO {

    public ControlDeActasDAOH() {
        super();
        setClazz(ControlDeActas.class);
    }

}
