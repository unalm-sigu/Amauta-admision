package pe.edu.lamolina.amauta.dao.academico.hibernate;

import pe.edu.lamolina.amauta.dao.academico.FormatoCursoDAO;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.academico.FormatoCurso;

@Repository
public class FormatoCursoDAOH extends AbstractEasyDAO<FormatoCurso> implements FormatoCursoDAO {

    public FormatoCursoDAOH() {
        super();
        setClazz(FormatoCurso.class);
    }
}
