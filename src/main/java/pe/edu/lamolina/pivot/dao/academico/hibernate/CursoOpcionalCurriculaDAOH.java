package pe.edu.lamolina.pivot.dao.academico.hibernate;

import pe.albatross.zelpers.dao.AbstractDAO;
import pe.edu.lamolina.pivot.dao.academico.CursoOpcionalCurriculaDAO;
import pe.edu.lamolina.pivot.model.academico.CursoOpcionalCurricula;
import org.springframework.stereotype.Repository;

@Repository
public class CursoOpcionalCurriculaDAOH extends AbstractDAO<CursoOpcionalCurricula> implements CursoOpcionalCurriculaDAO {

    public CursoOpcionalCurriculaDAOH() {
        super();
        setClazz(CursoOpcionalCurricula.class);
    }
}

