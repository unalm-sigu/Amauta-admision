package pe.edu.lamolina.pivot.dao.academico.hibernate;

import java.util.List;
import org.hibernate.Query;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.CursoCurricula;
import pe.edu.lamolina.model.matricula.AlumnoCursoCurricula;
import pe.edu.lamolina.pivot.dao.academico.AlumnoCursoCurriculaDAO;

@Repository
public class AlumnoCursoCurriculaDAOH extends AbstractEasyDAO<AlumnoCursoCurricula> implements AlumnoCursoCurriculaDAO {

    public AlumnoCursoCurriculaDAOH() {
        super();
        setClazz(AlumnoCursoCurricula.class);
    }

    @Override
    public List<AlumnoCursoCurricula> allByAlumnoCursosCurricula(Alumno alumno, List<CursoCurricula> cursosCurricula) {
        Octavia sql = Octavia.query()
                .from(AlumnoCursoCurricula.class, "acc")
                .isNull("cursoOpcional")
                .join("alumno alu", "curso cur", "cursoCurricula ccur")
                .filter("alumno", alumno)
                .in("ccur.id", cursosCurricula)
                .orderBy("acc.numeroCiclo");

        return all(sql);
    }

    @Override
    public List<AlumnoCursoCurricula> allNoOpcionalByAlumno(Alumno alumno) {
        Octavia sql = Octavia.query()
                .from(AlumnoCursoCurricula.class, "acc")
                .isNull("cursoOpcional")
                .join("alumno alu", "curso cur")
                .filter("alumno", alumno)
                .orderBy("acc.numeroCiclo");

        return all(sql);
    }

    @Override
    public List<AlumnoCursoCurricula> allByAlumno(Alumno alumno, Long numeroCiclo) {
        Octavia sql = Octavia.query()
                .from(AlumnoCursoCurricula.class, "acc")
                .join("curso", "cursoCurricula cc")
                .join("cc.tipoCursoCurricula")
                .leftJoin("acc.cicloAprobado")
                .filter("acc.alumno", alumno)
                .filter("acc.numeroCiclo", numeroCiclo);
        return all(sql);
    }

    @Override
    public List<AlumnoCursoCurricula> allCiclosAlumno(Alumno alumno) {
        Octavia sql = Octavia.query()
                .from(AlumnoCursoCurricula.class, "acc")
                .filter("acc.alumno", alumno)
                .orderBy("acc.numeroCiclo");
        return all(sql);
    }

    @Override
    public void deleteAllByAlumno(Alumno alumno) {
        StringBuilder sql = new StringBuilder();
        sql.append("delete AlumnoCursoCurricula acs where acs.alumno.id =:ALUMNO ");
        
        Query query = getCurrentSession().createQuery(sql.toString());
        query.setParameter("ALUMNO", alumno.getId());
        query.executeUpdate();
    }

}
