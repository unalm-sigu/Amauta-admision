package pe.edu.lamolina.pivot.dao.horario.hibernate;

import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.pivot.dao.horario.SeccionHorarioCachimbosDAO;
import pe.edu.lamolina.pivot.model.horario.SeccionHorarioCachimbos;
import org.springframework.stereotype.Repository;

@Repository
public class SeccionHorarioCachimbosDAOH extends AbstractEasyDAO<SeccionHorarioCachimbos> implements SeccionHorarioCachimbosDAO {

    public SeccionHorarioCachimbosDAOH() {
        super();
        setClazz(SeccionHorarioCachimbos.class);
    }
}

