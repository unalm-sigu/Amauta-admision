package pe.edu.lamolina.pivot.dao.academico.hibernate;

import pe.albatross.zelpers.dao.AbstractDAO;
import pe.edu.lamolina.pivot.dao.academico.NotaLetraDAO;
import pe.edu.lamolina.pivot.model.academico.NotaLetra;
import org.springframework.stereotype.Repository;

@Repository
public class NotaLetraDAOH extends AbstractDAO<NotaLetra> implements NotaLetraDAO {

    public NotaLetraDAOH() {
        super();
        setClazz(NotaLetra.class);
    }
    
    
    
}

