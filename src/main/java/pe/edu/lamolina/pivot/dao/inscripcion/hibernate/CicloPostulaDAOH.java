package pe.edu.lamolina.pivot.dao.inscripcion.hibernate;

import pe.albatross.zelpers.dao.AbstractDAO;
import pe.edu.lamolina.pivot.dao.inscripcion.CicloPostulaDAO;
import pe.edu.lamolina.pivot.model.inscripcion.CicloPostula;
import org.springframework.stereotype.Repository;

@Repository
public class CicloPostulaDAOH extends AbstractDAO<CicloPostula> implements CicloPostulaDAO {

    public CicloPostulaDAOH() {
        super();
        setClazz(CicloPostula.class);
    }
}

