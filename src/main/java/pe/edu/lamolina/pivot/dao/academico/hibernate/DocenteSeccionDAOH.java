package pe.edu.lamolina.pivot.dao.academico.hibernate;

import pe.albatross.zelpers.dao.AbstractDAO;
import pe.edu.lamolina.pivot.dao.academico.DocenteSeccionDAO;
import pe.edu.lamolina.pivot.model.academico.DocenteSeccion;
import org.springframework.stereotype.Repository;

@Repository
public class DocenteSeccionDAOH extends AbstractDAO<DocenteSeccion> implements DocenteSeccionDAO {

    public DocenteSeccionDAOH() {
        super();
        setClazz(DocenteSeccion.class);
    }
}

