package pe.edu.lamolina.pivot.dao.horario.hibernate;

import pe.albatross.octavia.easydao.AbstractEasyDAO;
import org.springframework.stereotype.Repository;
import pe.edu.lamolina.model.horario.SeccionCursoCachimbos;
import pe.edu.lamolina.pivot.dao.horario.SeccionCursoCachimbosDAO;

@Repository
public class SeccionCursoCachimbosDAOH extends AbstractEasyDAO<SeccionCursoCachimbos> implements SeccionCursoCachimbosDAO {

    public SeccionCursoCachimbosDAOH() {
        super();
        setClazz(SeccionCursoCachimbos.class);
    }

}
