package pe.edu.lamolina.pivot.dao.academico.hibernate;

import java.util.List;
import pe.albatross.zelpers.dao.AbstractDAO;
import pe.edu.lamolina.pivot.dao.academico.NombreCursoDAO;
import pe.edu.lamolina.pivot.model.academico.NombreCurso;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.edu.lamolina.pivot.model.academico.Curso;

@Repository
public class NombreCursoDAOH extends AbstractDAO<NombreCurso> implements NombreCursoDAO {

    public NombreCursoDAOH() {
        super();
        setClazz(NombreCurso.class);
    }

    @Override
    public List<NombreCurso> allByCurso(Curso curso) {
        Octavia sql = Octavia.query()
                .from(NombreCurso.class, "nc")
                .join("curso cu", "idioma id")
                .filter("cu.id", curso);
        return sql.all(getCurrentSession());
    }
}
