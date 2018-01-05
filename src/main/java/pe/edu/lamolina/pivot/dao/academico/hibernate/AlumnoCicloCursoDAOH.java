package pe.edu.lamolina.pivot.dao.academico.hibernate;

import pe.edu.lamolina.pivot.dao.academico.AlumnoCicloCursoDAO;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.academico.AlumnoCicloCurso;

@Repository
public class AlumnoCicloCursoDAOH extends AbstractEasyDAO<AlumnoCicloCurso> implements AlumnoCicloCursoDAO {

    public AlumnoCicloCursoDAOH() {
        super();
        setClazz(AlumnoCicloCurso.class);
    }
}
