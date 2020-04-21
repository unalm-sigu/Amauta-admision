package pe.edu.lamolina.amauta.dao.academico.hibernate;

import java.util.List;
import pe.edu.lamolina.amauta.dao.academico.NombreCursoDAO;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.academico.Curso;
import pe.edu.lamolina.model.academico.NombreCurso;

@Repository
public class NombreCursoDAOH extends AbstractEasyDAO<NombreCurso> implements NombreCursoDAO {

    public NombreCursoDAOH() {
        super();
        setClazz(NombreCurso.class);
    }

    @Override
    public NombreCurso find(long id) {
        Octavia sql = Octavia.query()
                .from(NombreCurso.class, "nc")
                .join("curso cu", "idioma id")
                .filter("nc.id", id);

        return find(sql);
    }

    @Override
    public List<NombreCurso> allByCurso(Curso curso) {
        Octavia sql = Octavia.query()
                .from(NombreCurso.class, "nc")
                .join("curso cu", "idioma id")
                .filter("cu.id", curso);

        return all(sql);
    }
}
