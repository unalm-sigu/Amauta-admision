package pe.edu.lamolina.amauta.dao.academico.hibernate;

import org.hibernate.Query;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.Curso;
import pe.edu.lamolina.model.matricula.AlumnoCursoCurricula;
import pe.edu.lamolina.model.matricula.AlumnoCursoSimultaneo;
import pe.edu.lamolina.amauta.dao.academico.AlumnoCursoSimultaneoDAO;

@Repository
public class AlumnoCursoSimultaneoDAOH extends AbstractEasyDAO<AlumnoCursoSimultaneo> implements AlumnoCursoSimultaneoDAO {

    public AlumnoCursoSimultaneoDAOH() {
        super();
        setClazz(AlumnoCursoSimultaneo.class);
    }


    @Override
    public AlumnoCursoSimultaneo findByAlumnoCursoCurriculaCurso(AlumnoCursoCurricula alumnoCursoCurricula, Curso curso) {
        Octavia sql = Octavia.query()
                .from(AlumnoCursoSimultaneo.class, "acs")
                .join("curso c", "alumnoCursoCurricula a")
                .filter("alumnoCursoCurricula", alumnoCursoCurricula)
                .filter("curso", curso);

        return find(sql);
    }

    @Override
    public void deleteAllByAlumno(Alumno alumno) {
        StringBuilder sql = new StringBuilder();
        sql.append("delete AlumnoCursoSimultaneo acs ");
        sql.append(" where exists (select 1 ");
        sql.append("                 from AlumnoCursoCurricula acc ");
        sql.append("                 join acc.alumno a ");
        sql.append("                where acc.id = acs.alumnoCursoCurricula.id ");
        sql.append("                  and a.id =:ALUMNO ) ");
        
        Query query =   getCurrentSession().createQuery(sql.toString());
        query.setParameter("ALUMNO", alumno.getId());
        query.executeUpdate();
    }
}
