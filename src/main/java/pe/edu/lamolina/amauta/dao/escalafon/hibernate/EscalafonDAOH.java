package pe.edu.lamolina.amauta.dao.escalafon.hibernate;

import org.springframework.stereotype.Repository;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.amauta.dao.escalafon.EscalafonDAO;
import pe.edu.lamolina.model.escalafon.Escalafon;

@Repository
public class EscalafonDAOH extends AbstractEasyDAO<Escalafon> implements EscalafonDAO {

    public EscalafonDAOH(){
        super();
        setClazz(Escalafon.class);
    }
}