package pe.edu.lamolina.pivot.dao.horario.hibernate;

import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.pivot.dao.horario.HorarioCachimbosDAO;
import pe.edu.lamolina.pivot.model.horario.HorarioCachimbos;
import org.springframework.stereotype.Repository;

@Repository
public class HorarioCachimbosDAOH extends AbstractEasyDAO<HorarioCachimbos> implements HorarioCachimbosDAO {

    public HorarioCachimbosDAOH() {
        super();
        setClazz(HorarioCachimbos.class);
    }
}

