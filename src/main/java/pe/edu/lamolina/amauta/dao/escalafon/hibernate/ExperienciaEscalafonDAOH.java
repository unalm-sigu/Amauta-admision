package pe.edu.lamolina.amauta.dao.escalafon.hibernate;

import org.springframework.stereotype.Repository;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.amauta.dao.escalafon.ExperienciaEscalafonDAO;
import pe.edu.lamolina.model.escalafon.ExperienciaEscalafon;

@Repository
public class ExperienciaEscalafonDAOH extends AbstractEasyDAO<ExperienciaEscalafon> implements ExperienciaEscalafonDAO {

    public ExperienciaEscalafonDAOH(){
        super();
        setClazz(ExperienciaEscalafon.class);
    }
}