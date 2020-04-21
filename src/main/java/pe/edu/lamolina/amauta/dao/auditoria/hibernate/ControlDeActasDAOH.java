package pe.edu.lamolina.amauta.dao.auditoria.hibernate;

import org.springframework.stereotype.Repository;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.auditoria.ControlDeActas;
import pe.edu.lamolina.amauta.dao.auditoria.ControlDeActasDAO;

@Repository
public class ControlDeActasDAOH extends AbstractEasyDAO<ControlDeActas> implements ControlDeActasDAO {

    public ControlDeActasDAOH() {
        super();
        setClazz(ControlDeActas.class);
    }

}
