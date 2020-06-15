package pe.edu.lamolina.amauta.dao.escalafon.hibernate;

import org.springframework.stereotype.Repository;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.amauta.dao.escalafon.AreaInvestigacionDAO;
import pe.edu.lamolina.model.escalafon.AreaInvestigacion;

@Repository
public class AreaInvestigacionDAOH extends AbstractEasyDAO<AreaInvestigacion> implements AreaInvestigacionDAO {

    public AreaInvestigacionDAOH(){
        super();
        setClazz(AreaInvestigacion.class);
    }
}