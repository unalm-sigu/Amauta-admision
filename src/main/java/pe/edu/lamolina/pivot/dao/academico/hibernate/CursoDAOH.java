package pe.edu.lamolina.pivot.dao.academico.hibernate;

import java.util.List;
import org.hibernate.Query;
import pe.albatross.zelpers.dao.AbstractDAO;
import pe.edu.lamolina.pivot.dao.academico.CursoDAO;
import pe.edu.lamolina.pivot.model.academico.Curso;
import org.springframework.stereotype.Repository;

@Repository
public class CursoDAOH extends AbstractDAO<Curso> implements CursoDAO {

    public CursoDAOH() {
        super();
        setClazz(Curso.class);
    }

    @Override
    public List<Curso> allAutocomplete(String nombre) {
        nombre = "%" + nombre.replaceAll(" ", "%") + "%";
        StringBuilder sql = new StringBuilder();
        sql.append("  from ").append(Curso.class.getName()).append(" as cur ");
        sql.append(" inner join fetch a.departamentoAcademico da ");
        sql.append(" inner join fetch a.sistemaEvaluacion se ");
        sql.append(" where 1=1 ");
        sql.append("   or    cur.nombre like :NOMBRE ");
        sql.append(" order by cur.nombre ");

        Query query = getCurrentSession().createQuery(sql.toString());
        query.setString("NOMBRE", nombre);
        query.setMaxResults(15);

        return query.list();
    }

}
