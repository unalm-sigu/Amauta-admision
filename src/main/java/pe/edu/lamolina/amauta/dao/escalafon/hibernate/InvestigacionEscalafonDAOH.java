package pe.edu.lamolina.amauta.dao.escalafon.hibernate;

import org.springframework.stereotype.Repository;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.amauta.dao.escalafon.InvestigacionEscalafonDAO;
import pe.edu.lamolina.model.escalafon.InvestigacionEscalafon;

@Repository
public class InvestigacionEscalafonDAOH extends AbstractEasyDAO<InvestigacionEscalafon> implements InvestigacionEscalafonDAO {

    public InvestigacionEscalafonDAOH(){
        super();
        setClazz(InvestigacionEscalafon.class);
    }
}