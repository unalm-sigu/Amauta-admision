package pe.edu.lamolina.pivot.dao.academico.hibernate;

import java.util.Arrays;
import java.util.List;
import pe.edu.lamolina.pivot.dao.academico.MatriculaCursoDAO;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Curso;
import pe.edu.lamolina.model.academico.MatriculaCurso;
import pe.edu.lamolina.model.academico.MatriculaResumen;
import pe.edu.lamolina.model.enums.CicloAcademicoEstadoEnum;
import pe.edu.lamolina.model.enums.EstadoMatriculaEnum;
import static pe.edu.lamolina.model.enums.EstadoMatriculaEnum.MAT;
import static pe.edu.lamolina.model.enums.EstadoMatriculaEnum.PMAT;
import static pe.edu.lamolina.model.enums.EstadoMatriculaEnum.RCI;
import static pe.edu.lamolina.model.enums.EstadoMatriculaEnum.RCU;

@Repository
public class MatriculaCursoDAOH extends AbstractEasyDAO<MatriculaCurso> implements MatriculaCursoDAO {

    public MatriculaCursoDAOH() {
        super();
        setClazz(MatriculaCurso.class);
    }

    @Override
    public MatriculaCurso findByAlumnoCursoCiclo(Alumno alumno, Curso curso, CicloAcademico ciclo) {
        Octavia sql = Octavia.query()
                .from(MatriculaCurso.class, "mc")
                .join("matriculaResumen mr", "mr.alumno alu", "mr.cicloAcademico ca", "curso cu")
                .filter("ca.id", ciclo)
                .filter("alu.id", alumno)
                .filter("cu.id", curso);

        return find(sql);
    }

    @Override
    public List<MatriculaCurso> findByCursoCiclo(Curso curso, CicloAcademico ciclo) {
        Octavia sql = Octavia.query()
                .from(MatriculaCurso.class, "mc")
                .join("matriculaResumen mr", "mr.alumno alu", "mr.cicloAcademico ca", "curso cu")
                .filter("ca.id", ciclo)
                .filter("cu.id", curso);

        return all(sql);
    }

    @Override
    public List<MatriculaCurso> allByMatriculaResumen(MatriculaResumen resumen) {
        Octavia sql = Octavia.query()
                .from(MatriculaCurso.class, "mc")
                .join("matriculaResumen mr", "mr.alumno alu", "mr.cicloAcademico ca", "curso cu")
                .filter("mr.id", resumen);

        return all(sql);
    }

    @Override
    public List<MatriculaCurso> allByMatriculaResumen(List<MatriculaResumen> resumenes) {
        Octavia sql = Octavia.query()
                .from(MatriculaCurso.class, "mc")
                .join("matriculaResumen mr", "mr.alumno alu", "mr.cicloAcademico ca", "curso cu")
                .in("mr.id", resumenes);
        return all(sql);
    }

    @Override
    public List<MatriculaCurso> allByMatriculaResumenCurso(List<MatriculaResumen> resumenes, Curso curso) {
        Octavia sql = Octavia.query()
                .from(MatriculaCurso.class, "mc")
                .join("matriculaResumen mr", "mr.alumno alu", "mr.cicloAcademico ca", "curso cu")
                .filter("cu.id", curso)
                .in("mr.id", resumenes);
        return all(sql);
    }

    @Override
    public List<MatriculaCurso> allByAlumno(Long idAlumno) {
        Octavia sql = Octavia.query()
                .from(MatriculaCurso.class, "macu")
                .join("matriculaResumen mare", "curso")
                .join("mare.alumno al")
                .filter("al.id", idAlumno);

        return all(sql);
    }

    @Override
    public List<MatriculaCurso> allByCursoCiclo(Curso curso, CicloAcademico ciclo) {
        Octavia sql = Octavia.query()
                .from(MatriculaCurso.class, "mc")
                .join("matriculaResumen mr", "mr.alumno alu", "mr.cicloAcademico ca", "curso cu")
                .filter("ca.id", ciclo)
                .filter("cu.id", curso);

        return all(sql);
    }

    @Override
    public List<MatriculaCurso> allByCiclo(CicloAcademico ciclo) {
        Octavia sql = Octavia.query()
                .from(MatriculaCurso.class, "mc")
                .join("matriculaResumen mr", "mr.alumno alu", "mr.cicloAcademico ca", "curso cu")
                .join("alu.persona per")
                .leftJoin("per.tipoDocumento")
                .filter("ca.id", ciclo);

        return all(sql);
    }

    @Override
    public List<MatriculaCurso> allByCicloFull(CicloAcademico ciclo) {
        Octavia sqlSubquery = Octavia.query()
                .from(MatriculaResumen.class, "mr1")
                .join("alumno alu1", "cicloAcademico ca1")
                .filter("ca1.id", ciclo);

        Octavia sql = Octavia.query()
                .from(MatriculaCurso.class, "mc")
                .join("matriculaResumen mr", "mr.alumno alu", "mr.cicloAcademico ca", "curso cu")
                .join("alu.persona per")
                .leftJoin("per.tipoDocumento")
                .orderBy("ca.codigo asc")
                .exists(sqlSubquery)
                .linkedBy("alu.id", "alu1.id");

        return all(sql);
    }

    @Override
    public List<MatriculaCurso> allByMatriculaResumenFull(MatriculaResumen matriculaResumen) {
        Octavia sqlSubquery = Octavia.query()
                .from(MatriculaResumen.class, "mr1")
                .join("alumno alu1")
                .filter("mr1.id", matriculaResumen);

        Octavia sql = Octavia.query()
                .from(MatriculaCurso.class, "mc")
                .join("matriculaResumen mr", "mr.alumno alu", "mr.cicloAcademico ca", "curso cu")
                .join("alu.persona per")
                .leftJoin("per.tipoDocumento")
                .orderBy("ca.codigo asc")
                .exists(sqlSubquery)
                .linkedBy("alu.id", "alu1.id");

        return all(sql);
    }

    @Override
    public List<MatriculaCurso> allMatriculadosByAlumnoCiclo(Alumno alumno, CicloAcademico ciclo) {
        Octavia sql = Octavia.query()
                .from(MatriculaCurso.class, "mc")
                .join("matriculaResumen mr", "mr.alumno alu", "mr.cicloAcademico ca")
                .join("curso cu", "cu.departamentoAcademico")
                .filter("mc.estado", EstadoMatriculaEnum.MAT)
                .filter("ca.id", ciclo)
                .filter("alu.id", alumno);

        return all(sql);
    }

    @Override
    public List<MatriculaCurso> allActivoByAlumnoCiclo(Alumno alumno, CicloAcademico ciclo) {
        Octavia sql = Octavia.query(MatriculaCurso.class, "mc")
                .join("matriculaResumen mr", "mr.alumno alu", "mr.cicloAcademico ca", "curso cu")
                .in("mc.estado", Arrays.asList(EstadoMatriculaEnum.PMAT.name(), EstadoMatriculaEnum.MAT.name(), EstadoMatriculaEnum.RCI.name(), EstadoMatriculaEnum.RCU.name()))
                .filter("ca.id", ciclo)
                .filter("alu.id", alumno);
        return sql.all(getCurrentSession());
    }

    @Override
    public Long countAllAlumnoPrematriculado(CicloAcademico cicloAcademico) {
        Octavia sql = Octavia.query()
                .selectCountDistinct("alu")
                .from(MatriculaCurso.class, "mc")
                .join("matriculaResumen mr", "mr.alumno alu", "mr.cicloAcademico ca")
                .join("curso cu", "cu.departamentoAcademico")
                .filter("ca.id", cicloAcademico)
                .in("mc.estado", Arrays.asList(EstadoMatriculaEnum.PMAT.name(), EstadoMatriculaEnum.NMAT.name()));
        return (Long) sql.find(getCurrentSession());
    }

    @Override
    public List<MatriculaCurso> allPrematriculadoByMatriculaResumen(List<MatriculaResumen> matriculaResumens) {
        Octavia sql = Octavia.query()
                .from(MatriculaCurso.class, "mc")
                .join("matriculaResumen mr", "mr.alumno alu", "mr.cicloAcademico ca", "curso cu")
                .in("mr.id", matriculaResumens)
                .filter("mc.estado", EstadoMatriculaEnum.PMAT);
        return all(sql);
    }

    @Override
    public List<MatriculaCurso> allPrematriculadoByCiclo(CicloAcademico cicloAcademico) {
        Octavia sql = Octavia.query()
                .from(MatriculaCurso.class, "mc")
                .join("matriculaResumen mr", "mr.alumno alu", "mr.cicloAcademico ca", "curso cu")
                .filter("ca.id", cicloAcademico)
                .filter("mc.estado", EstadoMatriculaEnum.PMAT);
        return all(sql);
    }

    @Override
    public List<MatriculaCurso> allByAlumnosCursosCiclo(List<Alumno> alumnos, List<Curso> cursos, CicloAcademico ciclo) {
        Octavia sql = Octavia.query()
                .from(MatriculaCurso.class, "mc")
                .join("matriculaResumen mr", "mr.alumno alu", "mr.cicloAcademico ca")
                .join("curso cu", "cu.departamentoAcademico")
                .filter("ca.id", ciclo)
                .in("cu.id", cursos)
                .in("alu.id", alumnos);

        return all(sql);
    }

    @Override
    public List<MatriculaCurso> allActivoByAlumnosCicloActivo(List<Alumno> alumnos) {
        Octavia sql = Octavia.query(MatriculaCurso.class, "mc")
                .join("matriculaResumen mr", "mr.alumno alu", "mr.cicloAcademico ca", "curso cu")
                .filter("ca.estado", CicloAcademicoEstadoEnum.ACT)
                .in("mc.estado", Arrays.asList(PMAT.name(), MAT.name(), RCI.name(), RCU.name()))
                .in("alu.id", alumnos);
        return sql.all(getCurrentSession());

    }

    @Override
    public List<MatriculaCurso> allActivoByAlumnoCicloActivo(Alumno alumno) {
        Octavia sql = Octavia.query(MatriculaCurso.class, "mc")
                .join("matriculaResumen mr", "mr.alumno alu", "mr.cicloAcademico ca", "curso cu")
                .filter("ca.estado", CicloAcademicoEstadoEnum.ACT)
                .in("mc.estado", Arrays.asList(PMAT.name(), MAT.name(), RCI.name(), RCU.name()))
                .filter("alu.id", alumno);

        return sql.all(getCurrentSession());

    }
}
