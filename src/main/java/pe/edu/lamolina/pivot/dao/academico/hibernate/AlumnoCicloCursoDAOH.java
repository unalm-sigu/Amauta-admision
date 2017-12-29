package pe.edu.lamolina.pivot.dao.academico.hibernate;

import pe.albatross.zelpers.dao.AbstractDAO;
import pe.edu.lamolina.pivot.dao.academico.AlumnoCicloCursoDAO;
import org.springframework.stereotype.Repository;
import pe.edu.lamolina.model.academico.AlumnoCicloCurso;

@Repository
public class AlumnoCicloCursoDAOH extends AbstractDAO<AlumnoCicloCurso> implements AlumnoCicloCursoDAO {

    public AlumnoCicloCursoDAOH() {
        super();
        setClazz(AlumnoCicloCurso.class);
    }
}

