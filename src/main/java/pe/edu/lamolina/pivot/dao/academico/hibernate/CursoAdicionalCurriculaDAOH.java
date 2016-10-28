package pe.edu.lamolina.pivot.dao.academico.hibernate;

import pe.albatross.zelpers.dao.AbstractDAO;
import pe.edu.lamolina.pivot.dao.academico.CursoAdicionalCurriculaDAO;
import pe.edu.lamolina.pivot.model.academico.CursoAdicionalCurricula;
import org.springframework.stereotype.Repository;

@Repository
public class CursoAdicionalCurriculaDAOH extends AbstractDAO<CursoAdicionalCurricula> implements CursoAdicionalCurriculaDAO {

    public CursoAdicionalCurriculaDAOH() {
        super();
        setClazz(CursoAdicionalCurricula.class);
    }
}

