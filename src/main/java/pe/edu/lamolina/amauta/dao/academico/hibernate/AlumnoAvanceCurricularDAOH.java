package pe.edu.lamolina.amauta.dao.academico.hibernate;

import java.util.List;
import org.hibernate.Query;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.TipoCursoCurricula;
import pe.edu.lamolina.model.matricula.AlumnoAvanceCurricular;
import pe.edu.lamolina.amauta.dao.academico.AlumnoAvanceCurricularDAO;

@Repository
public class AlumnoAvanceCurricularDAOH extends AbstractEasyDAO<AlumnoAvanceCurricular> implements AlumnoAvanceCurricularDAO {

    public AlumnoAvanceCurricularDAOH() {
        super();
        setClazz(AlumnoAvanceCurricular.class);
    }

    @Override
    public List<AlumnoAvanceCurricular> allByAlumno(Alumno alumno) {
        Octavia sql = Octavia.query()
                .from(AlumnoAvanceCurricular.class, "ac")
                .join("alumno al", "tipoCursoCurricula tcc")
                .filter("al.id", alumno)
                .orderBy("tcc.orden");

        return sql.all(getCurrentSession());
    }

    @Override
    public AlumnoAvanceCurricular findByAlumnoTipoCursoCurricula(Alumno alumno, TipoCursoCurricula tipoCursoCurricula) {
        Octavia sql = Octavia.query()
                .from(AlumnoAvanceCurricular.class, "ac")
                .join("alumno al")
                .filter("al.id", alumno)
                .filter("tipoCursoCurricula", tipoCursoCurricula);

        return (AlumnoAvanceCurricular) sql.find(getCurrentSession());
    }

    @Override
    public void deleteAllByAlumno(Alumno alumno) {
        StringBuilder sql = new StringBuilder();
        sql.append("delete AlumnoAvanceCurricular aac where aac.alumno.id = :ALUMNO ");

        Query query = getCurrentSession().createQuery(sql.toString());
        query.setParameter("ALUMNO", alumno.getId());
        query.executeUpdate();
    }

    @Override
    public List<AlumnoAvanceCurricular> allByAlumnos(List<Alumno> alumnos) {
        Octavia sql = Octavia.query()
                .from(AlumnoAvanceCurricular.class, "ac")
                .join("alumno al", "tipoCursoCurricula")
                .in("al.id", alumnos);

        return sql.all(getCurrentSession());
    }

    @Override
    public void updateColumns(AlumnoAvanceCurricular avance, String... columns) {
        Octavia octavia = Octavia.update(AlumnoAvanceCurricular.class);
        octavia.set(avance, columns);
        this.update(octavia);
    }

}
