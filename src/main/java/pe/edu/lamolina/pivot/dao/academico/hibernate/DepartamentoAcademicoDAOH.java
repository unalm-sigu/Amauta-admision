package pe.edu.lamolina.pivot.dao.academico.hibernate;

import pe.albatross.zelpers.dao.AbstractDAO;
import pe.edu.lamolina.pivot.dao.academico.DepartamentoAcademicoDAO;
import pe.edu.lamolina.pivot.model.academico.DepartamentoAcademico;
import org.springframework.stereotype.Repository;

@Repository
public class DepartamentoAcademicoDAOH extends AbstractDAO<DepartamentoAcademico> implements DepartamentoAcademicoDAO {

    public DepartamentoAcademicoDAOH() {
        super();
        setClazz(DepartamentoAcademico.class);
    }
}

