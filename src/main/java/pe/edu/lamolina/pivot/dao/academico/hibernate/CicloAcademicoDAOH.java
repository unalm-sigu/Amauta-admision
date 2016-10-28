package pe.edu.lamolina.pivot.dao.academico.hibernate;

import pe.albatross.zelpers.dao.AbstractDAO;
import pe.edu.lamolina.pivot.dao.academico.CicloAcademicoDAO;
import pe.edu.lamolina.pivot.model.academico.CicloAcademico;
import org.springframework.stereotype.Repository;

@Repository
public class CicloAcademicoDAOH extends AbstractDAO<CicloAcademico> implements CicloAcademicoDAO {

    public CicloAcademicoDAOH() {
        super();
        setClazz(CicloAcademico.class);
    }
}

