package pe.edu.lamolina.amauta.dao.nivelacioneegg.hibernate;

import java.util.List;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.amauta.dao.nivelacioneegg.CursoTemaExamenDAO;
import pe.edu.lamolina.model.academico.Curso;
import pe.edu.lamolina.model.nivelacioneegg.CursoTemaExamen;

@Repository
public class CursoTemaExamenDAOH extends AbstractEasyDAO<CursoTemaExamen> implements CursoTemaExamenDAO {

    public CursoTemaExamenDAOH() {
        super();
        setClazz(CursoTemaExamen.class);
    }

    @Override
    public List<CursoTemaExamen> allParents() {
        Octavia sql = Octavia.query()
                .from(CursoTemaExamen.class, "cte")
                .join("curso cur", "temaExamen te");
        return all(sql);

    }

    @Override
    public List<CursoTemaExamen> allByCurso(Curso curso) {
        Octavia sql = Octavia.query()
                .from(CursoTemaExamen.class, "cte")
                .join("curso cur", "temaExamen te")
                .filter("cur.id", curso);
        return all(sql);
    }

    @Override
    public List<CursoTemaExamen> allByCursos(List<Curso> cursosNivelacion) {
        Octavia sql = Octavia.query()
                .from(CursoTemaExamen.class, "cte")
                .join("curso cur", "temaExamen te")
                .in("cur.id", cursosNivelacion);
        return all(sql);
    }

}
