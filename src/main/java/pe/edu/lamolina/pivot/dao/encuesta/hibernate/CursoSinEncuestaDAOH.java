package pe.edu.lamolina.pivot.dao.encuesta.hibernate;

import java.util.List;
import org.hibernate.Query;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.academico.Curso;
import pe.edu.lamolina.model.encuestaestudiantil.CursoSinEncuesta;
import pe.edu.lamolina.model.encuestaestudiantil.EncuestaEstudiantil;
import pe.edu.lamolina.pivot.dao.encuesta.CursoSinEncuestaDAO;

@Repository
public class CursoSinEncuestaDAOH extends AbstractEasyDAO<CursoSinEncuesta> implements CursoSinEncuestaDAO {

    public CursoSinEncuestaDAOH() {
        super();
        setClazz(CursoSinEncuesta.class);
    }

    @Override
    public CursoSinEncuesta findByEncuestaEstudiantilCurso(EncuestaEstudiantil encuestaEstudiantil, Curso curso) {
        Octavia sql = Octavia.query()
                .from(CursoSinEncuesta.class, "ep")
                .join("encuestaEstudiantil ee", "curso cur")
                .filter("ee.id", encuestaEstudiantil)
                .filter("cur.id", curso);
        return find(sql);
    }

    @Override
    public List<CursoSinEncuesta> allByEncuestaEstudiantil(EncuestaEstudiantil encuestaEstudiantil) {
        Octavia sql = Octavia.query()
                .from(CursoSinEncuesta.class, "ep")
                .join("encuestaEstudiantil ee", "curso cur")
                .leftJoin("cur.carrera car", "cur.departamentoAcademico da", "da.facultad")
                .filter("ee.id", encuestaEstudiantil);
        return all(sql);
    }

    @Override
    public void deleteByEncuestaEstudiantil(EncuestaEstudiantil encuesta) {
        String strQuery = "delete from CursoSinEncuesta cse where cse.encuestaEstudiantil.id = :ENCUESTA ";
        Query query = getCurrentSession().createQuery(strQuery);
        query.setLong("ENCUESTA", encuesta.getId());
        query.executeUpdate();
    }

}
