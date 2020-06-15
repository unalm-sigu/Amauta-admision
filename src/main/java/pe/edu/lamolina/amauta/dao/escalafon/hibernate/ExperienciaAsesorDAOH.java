package pe.edu.lamolina.amauta.dao.escalafon.hibernate;

import org.springframework.stereotype.Repository;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.amauta.dao.escalafon.ExperienciaAsesorDAO;
import pe.edu.lamolina.model.escalafon.ExperienciaAsesor;

@Repository
public class ExperienciaAsesorDAOH extends AbstractEasyDAO<ExperienciaAsesor> implements ExperienciaAsesorDAO {

    public ExperienciaAsesorDAOH(){
        super();
        setClazz(ExperienciaAsesor.class);
    }
}