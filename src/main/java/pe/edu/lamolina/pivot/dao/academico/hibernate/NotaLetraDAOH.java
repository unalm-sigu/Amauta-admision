package pe.edu.lamolina.pivot.dao.academico.hibernate;

import pe.edu.lamolina.pivot.dao.academico.NotaLetraDAO;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.academico.NotaLetra;

@Repository
public class NotaLetraDAOH extends AbstractEasyDAO<NotaLetra> implements NotaLetraDAO {

    public NotaLetraDAOH() {
        super();
        setClazz(NotaLetra.class);
    }

}
