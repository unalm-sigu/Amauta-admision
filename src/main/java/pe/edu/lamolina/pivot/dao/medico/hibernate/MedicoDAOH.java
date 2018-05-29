package pe.edu.lamolina.pivot.dao.medico.hibernate;

import org.springframework.stereotype.Repository;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.medico.Medico;
import pe.edu.lamolina.pivot.dao.medico.MedicoDAO;

@Repository
public class MedicoDAOH extends AbstractEasyDAO<Medico> implements MedicoDAO {

    public MedicoDAOH() {
        super();
        setClazz(Medico.class);
    }
}
