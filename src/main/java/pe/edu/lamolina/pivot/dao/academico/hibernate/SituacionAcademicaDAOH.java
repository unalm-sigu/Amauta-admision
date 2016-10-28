package pe.edu.lamolina.pivot.dao.academico.hibernate;

import pe.albatross.zelpers.dao.AbstractDAO;
import pe.edu.lamolina.pivot.dao.academico.SituacionAcademicaDAO;
import pe.edu.lamolina.pivot.model.academico.SituacionAcademica;
import org.springframework.stereotype.Repository;

@Repository
public class SituacionAcademicaDAOH extends AbstractDAO<SituacionAcademica> implements SituacionAcademicaDAO {

    public SituacionAcademicaDAOH() {
        super();
        setClazz(SituacionAcademica.class);
    }
}

