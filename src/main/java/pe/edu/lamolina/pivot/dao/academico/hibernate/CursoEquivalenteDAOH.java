package pe.edu.lamolina.pivot.dao.academico.hibernate;

import org.springframework.stereotype.Repository;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.academico.CursoEquivalente;
import pe.edu.lamolina.pivot.dao.academico.CursoEquivalenteDAO;

@Repository
public class CursoEquivalenteDAOH extends AbstractEasyDAO<CursoEquivalente> implements CursoEquivalenteDAO {

    public CursoEquivalenteDAOH() {
        super();
        setClazz(CursoEquivalente.class);
    }

}
