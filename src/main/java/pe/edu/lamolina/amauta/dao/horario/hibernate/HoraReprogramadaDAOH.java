package pe.edu.lamolina.amauta.dao.horario.hibernate;

import org.springframework.stereotype.Repository;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.horario.HoraReprogramada;
import pe.edu.lamolina.amauta.dao.horario.HoraReprogramadaDAO;

@Repository
public class HoraReprogramadaDAOH extends AbstractEasyDAO<HoraReprogramada> implements HoraReprogramadaDAO {

    public HoraReprogramadaDAOH() {
        super();
        setClazz(HoraReprogramada.class);
    }

}
