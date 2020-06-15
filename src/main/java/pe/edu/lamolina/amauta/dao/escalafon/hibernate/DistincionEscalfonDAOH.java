package pe.edu.lamolina.amauta.dao.escalafon.hibernate;

import org.springframework.stereotype.Repository;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.amauta.dao.escalafon.DistincionEscalfonDAO;
import pe.edu.lamolina.model.escalafon.DistincionEscalfon;

@Repository
public class DistincionEscalfonDAOH extends AbstractEasyDAO<DistincionEscalfon> implements DistincionEscalfonDAO {

    public DistincionEscalfonDAOH(){
        super();
        setClazz(DistincionEscalfon.class);
    }
}