package pe.edu.lamolina.amauta.dao.escalafon.hibernate;

import org.springframework.stereotype.Repository;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.amauta.dao.escalafon.ProduccionEscalafonDAO;
import pe.edu.lamolina.model.escalafon.ProduccionEscalafon;

@Repository
public class ProduccionEscalafonDAOH extends AbstractEasyDAO<ProduccionEscalafon> implements ProduccionEscalafonDAO {

    public ProduccionEscalafonDAOH(){
        super();
        setClazz(ProduccionEscalafon.class);
    }
}