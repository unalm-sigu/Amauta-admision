package pe.edu.lamolina.pivot.dao.academico.hibernate;

import pe.albatross.zelpers.dao.AbstractDAO;
import pe.edu.lamolina.pivot.dao.academico.AlumnoCicloCursoDAO;
import pe.edu.lamolina.pivot.model.academico.AlumnoCicloCurso;
import org.springframework.stereotype.Repository;

@Repository
public class AlumnoCicloCursoDAOH extends AbstractDAO<AlumnoCicloCurso> implements AlumnoCicloCursoDAO {

    public AlumnoCicloCursoDAOH() {
        super();
        setClazz(AlumnoCicloCurso.class);
    }
}

