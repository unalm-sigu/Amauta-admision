package pe.edu.lamolina.pivot.dao.general.hibernate;

import org.springframework.stereotype.Repository;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.general.EstadoCivil;
import pe.edu.lamolina.pivot.dao.general.EstadoCivilDAO;

@Repository
public class EstadoCivilDAOH extends AbstractEasyDAO<EstadoCivil> implements EstadoCivilDAO {

    public EstadoCivilDAOH() {
        super();
        setClazz(EstadoCivil.class);
    }
}
