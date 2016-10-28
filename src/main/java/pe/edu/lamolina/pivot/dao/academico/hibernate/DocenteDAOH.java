package pe.edu.lamolina.pivot.dao.academico.hibernate;

import pe.albatross.zelpers.dao.AbstractDAO;
import pe.edu.lamolina.pivot.dao.academico.DocenteDAO;
import pe.edu.lamolina.pivot.model.academico.Docente;
import org.springframework.stereotype.Repository;

@Repository
public class DocenteDAOH extends AbstractDAO<Docente> implements DocenteDAO {

    public DocenteDAOH() {
        super();
        setClazz(Docente.class);
    }
}

