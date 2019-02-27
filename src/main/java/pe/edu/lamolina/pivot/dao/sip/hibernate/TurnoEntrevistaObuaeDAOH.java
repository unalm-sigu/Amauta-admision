package pe.edu.lamolina.pivot.dao.sip.hibernate;

import org.springframework.stereotype.Repository;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.inscripcion.TurnoEntrevistaObuae;
import pe.edu.lamolina.pivot.dao.sip.TurnoEntrevistaObuaeDAO;

@Repository
public class TurnoEntrevistaObuaeDAOH extends AbstractEasyDAO<TurnoEntrevistaObuae> implements TurnoEntrevistaObuaeDAO {

    public TurnoEntrevistaObuaeDAOH() {
        super();
        setClazz(TurnoEntrevistaObuae.class);
    }

}
