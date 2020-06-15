package pe.edu.lamolina.amauta.dao.escalafon.hibernate;

import org.springframework.stereotype.Repository;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.amauta.dao.escalafon.IdiomaEscalafonDAO;
import pe.edu.lamolina.model.escalafon.IdiomaEscalafon;

@Repository
public class IdiomaEscalafonDAOH extends AbstractEasyDAO<IdiomaEscalafon> implements IdiomaEscalafonDAO {

    public IdiomaEscalafonDAOH(){
        super();
        setClazz(IdiomaEscalafon.class);
    }
}