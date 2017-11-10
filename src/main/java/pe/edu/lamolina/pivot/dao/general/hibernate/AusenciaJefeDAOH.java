package pe.edu.lamolina.pivot.dao.general.hibernate;

import pe.albatross.zelpers.dao.AbstractDAO;
import org.springframework.stereotype.Repository;
import pe.edu.lamolina.pivot.dao.general.AusenciaJefeDAO;
import pe.edu.lamolina.pivot.model.general.AusenciaJefe;

@Repository
public class AusenciaJefeDAOH extends AbstractDAO<AusenciaJefe> implements AusenciaJefeDAO {

    public AusenciaJefeDAOH() {
        super();
        setClazz(AusenciaJefe.class);
    }
}
