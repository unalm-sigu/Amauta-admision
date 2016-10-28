package pe.edu.lamolina.pivot.dao.academico.hibernate;

import pe.albatross.zelpers.dao.AbstractDAO;
import pe.edu.lamolina.pivot.dao.academico.CursoCurriculaDAO;
import pe.edu.lamolina.pivot.model.academico.CursoCurricula;
import org.springframework.stereotype.Repository;

@Repository
public class CursoCurriculaDAOH extends AbstractDAO<CursoCurricula> implements CursoCurriculaDAO {

    public CursoCurriculaDAOH() {
        super();
        setClazz(CursoCurricula.class);
    }
}

