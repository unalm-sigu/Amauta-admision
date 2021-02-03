package pe.edu.lamolina.amauta.dao.tramite.hibernate;

import pe.albatross.octavia.easydao.AbstractEasyDAO;
import org.springframework.stereotype.Repository;
import pe.edu.lamolina.amauta.dao.tramite.TramitePracticaPreProfesionalesDAO;
import pe.edu.lamolina.model.tramite.PracticasPreProfesional;

@Repository
public class TramitePracticaPreProfesionalesDAOH extends AbstractEasyDAO<PracticasPreProfesional> implements TramitePracticaPreProfesionalesDAO {

    public TramitePracticaPreProfesionalesDAOH() {
        super();
        setClazz(PracticasPreProfesional.class);
    }
}
