package pe.edu.lamolina.pivot.dao.tramite.hibernate;

import pe.edu.lamolina.pivot.dao.tramite.RetiroCursoDAO;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.tramite.RetiroCurso;

@Repository
public class RetiroCursoDAOH extends AbstractEasyDAO<RetiroCurso> implements RetiroCursoDAO {

    public RetiroCursoDAOH() {
        super();
        setClazz(RetiroCurso.class);
    }
}
