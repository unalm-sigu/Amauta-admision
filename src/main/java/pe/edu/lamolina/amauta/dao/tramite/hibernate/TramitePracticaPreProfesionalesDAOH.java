package pe.edu.lamolina.amauta.dao.tramite.hibernate;

import pe.albatross.octavia.easydao.AbstractEasyDAO;
import org.springframework.stereotype.Repository;
import pe.edu.lamolina.amauta.dao.tramite.TramitePracticaPreProfesionalesDAO;
import pe.edu.lamolina.model.tramite.PracticasPreProfesionales;

@Repository
public class TramitePracticaPreProfesionalesDAOH extends AbstractEasyDAO<PracticasPreProfesionales> implements TramitePracticaPreProfesionalesDAO {

    public TramitePracticaPreProfesionalesDAOH() {
        super();
        setClazz(PracticasPreProfesionales.class);
    }
}
