package pe.edu.lamolina.pivot.dao.academico.hibernate;

import java.util.Arrays;
import java.util.List;
import org.hibernate.Query;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Curso;
import pe.edu.lamolina.model.academico.CursoCurricula;
import static pe.edu.lamolina.model.enums.CursoCurriculaEstadoEnum.APR;
import static pe.edu.lamolina.model.enums.CursoCurriculaEstadoEnum.CONV;
import static pe.edu.lamolina.model.enums.CursoCurriculaEstadoEnum.EQUIV;
import static pe.edu.lamolina.model.enums.TipoCursoCurriculaEnum.EEP;
import static pe.edu.lamolina.model.enums.TipoCursoCurriculaEnum.ELE;
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
                .isNotNull("cursoCurricula")
                .isNull("cursoOpcional")
                .join("alumno alu", "curso cur")
                .left("cursoCurricula ccur", "cursoOpcional")
                .filter("alumno", alumno)
                .in("ccur.id", cursosCurricula)
                .orderBy("acc.numeroCiclo");

        return all(sql);
    }

    @Override
    public List<AlumnoCursoCurricula> allByAlumnoCursosOpcional(Alumno alumno) {
        Octavia sql = Octavia.query()
                .from(AlumnoCursoCurricula.class, "acc")
                .isNull("cursoCurricula")
                .isNotNull("cursoOpcional")
                .left("alumno alu", "curso cur", "tipoCursoCurricula tc")
                .left("cursoCurricula ccur", "cursoOpcional")
                .filter("alumno", alumno)
                .filter("tc.codigo", "!=", EEP.name())
                .orderBy("acc.numeroCiclo");

        return all(sql);
    }

    @Override
    public List<AlumnoCursoCurricula> allByAlumnoComodin(Alumno alumno) {
        Octavia sql = Octavia.query()
                .from(AlumnoCursoCurricula.class, "acc")
                .isNull("cursoCurricula")
                .isNull("cursoOpcional")
                .left("alumno alu", "curso cur", "tipoCursoCurricula tc")
                .left("cursoCurricula ccur", "cursoOpcional")
                .filter("alumno", alumno)
                .orderBy("acc.numeroCiclo");

        return all(sql);
    }

    @Override
    public List<AlumnoCursoCurricula> allObligatoriosByAlumno(Alumno alumno) {
        Octavia sql = Octavia.query()
                .from(AlumnoCursoCurricula.class, "acc")
                .isNotNull("cursoCurricula")
                .isNull("cursoOpcional")
                .join("alumno alu", "curso cur")
                .filter("alumno", alumno)
                .orderBy("acc.numeroCiclo");

        return all(sql);
    }

    @Override
    public List<AlumnoCursoCurricula> allByAlumnoAprob(Alumno alumno, CicloAcademico ciclo) {
        Octavia sql = Octavia.query()
                .from(AlumnoCursoCurricula.class, "acc")
                .join("curso", "cursoCurricula cc")
                .join("cc.tipoCursoCurricula")
                .leftJoin("acc.cicloAprobado ca")
                .filter("acc.alumno", alumno)
                .filter("ca.id", ciclo);
        return all(sql);
    }

    @Override
    public List<AlumnoCursoCurricula> allCiclosAlumno(Alumno alumno) {
        Octavia sql = Octavia.query()
                .from(AlumnoCursoCurricula.class, "acc")
                .join("tipoCursoCurricula tc")
                .filter("acc.alumno", alumno)
                .notIn("tc.codigo", Arrays.asList(EEP.name(), ELE.name()))
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

    @Override
    public AlumnoCursoCurricula findByAlumnoCurso(Alumno alumno, Curso curso) {
        Octavia sql = Octavia.query()
                .from(AlumnoCursoCurricula.class, "acc")
                .join("alumno al", "curso cu")
                .leftJoin("cicloAprobado ci", "cursoCurricula cc", "cursoOpcional co", "tipoCursoCurriculaOrigen")
                .filter("al.id", alumno)
                .filter("cu.id", curso)
                .orderBy("cu.nombre");
        return (AlumnoCursoCurricula) sql.find(getCurrentSession());
    }

    @Override
    public void updateEstado(AlumnoCursoCurricula alumnoCursoCurricula) {
        Octavia octavia = Octavia.update(AlumnoCursoCurricula.class);
        octavia.set(alumnoCursoCurricula, "estado");
        octavia.set(alumnoCursoCurricula, "estadoMatricula");
        if (alumnoCursoCurricula.getTipoCursoCurricula() != null) {
            octavia.set(alumnoCursoCurricula, "tipoCursoCurricula");
        }
        this.update(octavia);
    }

    @Override
    public List<AlumnoCursoCurricula> allByAlumnoCicloRegularAct(Alumno alumno, CicloAcademico cicloAcademico) {
        Octavia sql = Octavia.query()
                .from(AlumnoCursoCurricula.class, "acc")
                .join("cicloAprobado ca", "curso cu")
                .filter("acc.alumno", alumno)
                .filter("ca.id", cicloAcademico)
                .orderBy("acc.numeroCiclo");
        return all(sql);
    }

    @Override
    public List<AlumnoCursoCurricula> allByAlumnosCurso(List<Alumno> alumnos, Curso curso) {
        Octavia sql = Octavia.query()
                .from(AlumnoCursoCurricula.class, "acc")
                .isNotNull("cursoCurricula")
                .isNull("cursoOpcional")
                .join("alumno alu", "curso cur")
                .in("alu.id", alumnos)
                .filter("cur.id", curso)
                .orderBy("acc.numeroCiclo");
        return all(sql);
    }

    @Override
    public List<AlumnoCursoCurricula> allByAlumnosApr(List<Alumno> alumnos) {
        Octavia sql = Octavia.query()
                .from(AlumnoCursoCurricula.class, "acc")
                .join("alumno alu", "curso cur")
                .in("alu.id", alumnos)
                .in("acc.estado", Arrays.asList(APR, EQUIV, CONV))
                .orderBy("acc.numeroCiclo");
        return all(sql);
    }

    @Override
    public List<AlumnoCursoCurricula> allByAlumnoApro(Alumno alumnoBD) {
        Octavia sql = Octavia.query()
                .from(AlumnoCursoCurricula.class, "acc")
                .join("alumno alu", "curso cur")
                .filter("alu.id", alumnoBD)
                .orderBy("acc.numeroCiclo");
        return all(sql);
    }

    @Override
    public List<AlumnoCursoCurricula> allByAlumnos(List<Alumno> alumnos) {
        Octavia sql = Octavia.query()
                .from(AlumnoCursoCurricula.class, "acc")
                .join("alumno alu", "curso cur")
                .in("alu.id", alumnos)
                .orderBy("acc.numeroCiclo");
        return all(sql);
    }

}
