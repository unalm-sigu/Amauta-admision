package pe.edu.lamolina.amauta.dao.academico.hibernate;

import java.util.Arrays;
import java.util.List;
import org.hibernate.Query;
import pe.edu.lamolina.amauta.dao.academico.MatriculaCursoDAO;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Insecto;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Curso;
import pe.edu.lamolina.model.academico.CursoCicloAcademico;
import pe.edu.lamolina.model.academico.MatriculaCurso;
import pe.edu.lamolina.model.academico.MatriculaResumen;
import pe.edu.lamolina.model.enums.CicloAcademicoEstadoEnum;
import pe.edu.lamolina.model.enums.EstadoMatriculaEnum;
import static pe.edu.lamolina.model.enums.EstadoMatriculaEnum.MAT;
import static pe.edu.lamolina.model.enums.EstadoMatriculaEnum.PMAT;
import static pe.edu.lamolina.model.enums.EstadoMatriculaEnum.RCI;
import static pe.edu.lamolina.model.enums.EstadoMatriculaEnum.RCU;
import static pe.edu.lamolina.model.enums.EstadoMatriculaEnum.RET;
import pe.edu.lamolina.model.matricula.MatriculaSimultaneo;

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
                .filter("ca.codigo", ciclo.getCodigo())
                .filter("alu.id", alumno)
                .filter("cu.id", curso);

        return find(sql);
    }

    @Override
    public List<MatriculaCurso> allMatriculadosByCursoCiclo(Curso curso, CicloAcademico ciclo) {
        Octavia sql = Octavia.query()
                .from(MatriculaCurso.class, "mc")
                .join("matriculaResumen mr", "mr.alumno alu", "mr.cicloAcademico ca", "curso cu")
                .filter("mc.estado", MAT)
                .filter("ca.codigo", ciclo.getCodigo())
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
    public List<MatriculaCurso> allByMatriculaResumenCurso(List<MatriculaResumen> resumenes, Curso curso) {
        Octavia sql = Octavia.query()
                .from(MatriculaCurso.class, "mc")
                .join("matriculaResumen mr", "mr.alumno alu", "mr.cicloAcademico ca", "curso cu")
                .filter("cu.id", curso)
                .in("mr.id", resumenes);
        return all(sql);
    }

    @Override
    public List<MatriculaCurso> allActivosByMatriculaResumenCurso(List<MatriculaResumen> resumenes, Curso curso) {
        Octavia sql = Octavia.query()
                .from(MatriculaCurso.class, "mc")
                .join("matriculaResumen mr", "curso cu")
                .join("mr.alumno alu", "mr.cicloAcademico ca")
                .join("alu.modalidadEstudio", "alu.carrera car", "car.facultad")
                .leftJoin("alu.orientacionCarrera", "alu.situacionAcademica", "alu.cicloIngreso")
                .filter("cu.id", curso)
                .in("mr.id", resumenes)
                .filter("mc.estado", MAT);
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
                .filter("ca.codigo", ciclo.getCodigo());

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
                .join("alu.persona per", "alu.carrera")
                .leftJoin("per.tipoDocumento", "alu.orientacionCarrera")
                .orderBy("ca.codigo asc")
                .exists(sqlSubquery)
                .linkedBy("alu.id", "alu1.id");

        return all(sql);
    }

    @Override
    public List<MatriculaCurso> allActivosByCiclo(CicloAcademico ciclo) {

        Octavia sql = Octavia.query()
                .from(MatriculaCurso.class, "mc")
                .join("matriculaResumen mr", "mr.alumno alu", "mr.cicloAcademico ca", "curso cu")
                .join("alu.persona per", "alu.carrera")
                .leftJoin("alu.orientacionCarrera")
                .filter("mc.estado", MAT)
                .filter("mr.estado", MAT)
                .filter("mc.porcentajeAvanceNota", 100)
                .filter("ca.codigo", ciclo.getCodigo());

        return all(sql);
    }

    @Override
    public List<MatriculaCurso> allByMatriculaResumenFull(MatriculaResumen matriculaResumen) {
        Octavia sql = Octavia.query()
                .from(MatriculaCurso.class, "mc")
                .join("matriculaResumen mr", "mr.alumno alu", "mr.cicloAcademico ca", "curso cu")
                .join("alu.persona per")
                .leftJoin("per.tipoDocumento")
                .filter("mr.id", matriculaResumen)
                .orderBy("ca.codigo asc");

        return all(sql);
    }

    @Override
    public List<MatriculaCurso> allMatriculadosByAlumnoCiclo(Alumno alumno, CicloAcademico ciclo) {
        Octavia sql = Octavia.query()
                .from(MatriculaCurso.class, "mc")
                .join("matriculaResumen mr", "mr.alumno alu", "mr.cicloAcademico ca")
                .join("curso cu", "cu.departamentoAcademico")
                .filter("mc.estado", EstadoMatriculaEnum.MAT)
                .filter("ca.codigo", ciclo.getCodigo())
                .filter("alu.id", alumno);

        return all(sql);
    }

    @Override
    public List<MatriculaCurso> allActivoByAlumnoCiclo(Alumno alumno, CicloAcademico ciclo) {
        Octavia sql = Octavia.query(MatriculaCurso.class, "mc")
                .join("matriculaResumen mr", "mr.alumno alu", "mr.cicloAcademico ca", "curso cu")
                .in("mc.estado", Arrays.asList(EstadoMatriculaEnum.PMAT.name(), EstadoMatriculaEnum.MAT.name(), EstadoMatriculaEnum.RCI.name(), EstadoMatriculaEnum.RCU.name()))
                .filter("ca.codigo", ciclo.getCodigo())
                .filter("alu.id", alumno);
        return sql.all(getCurrentSession());
    }

    @Override
    public List<MatriculaCurso> allActivoByAlumnosCiclo(List<Alumno> alumnos, CicloAcademico ciclo) {
        Octavia sql = Octavia.query(MatriculaCurso.class, "mc")
                .join("matriculaResumen mr", "mr.alumno alu", "mr.cicloAcademico ca", "curso cu")
                .filter("mr.estado", MAT)
                .filter("mc.estado", MAT)
                .filter("ca.codigo", ciclo.getCodigo())
                .in("alu.id", alumnos);
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
                .in("mc.estado", Arrays.asList(PMAT.name(), MAT.name(), RCI.name(), RCU.name(), RET.name()))
                .in("alu.id", alumnos);
        return sql.all(getCurrentSession());

    }

    @Override
    public List<MatriculaCurso> allByAlumnosCicloActivo(List<Alumno> alumnos) {
        Octavia sql = Octavia.query(MatriculaCurso.class, "mc")
                .join("matriculaResumen mr", "mr.alumno alu", "mr.cicloAcademico ca", "curso cu")
                .filter("ca.estado", CicloAcademicoEstadoEnum.ACT)
                .in("alu.id", alumnos);
        return sql.all(getCurrentSession());
    }

    @Override
    public List<MatriculaCurso> allActivoByAlumnoCicloActivo(Alumno alumno) {
        Octavia sql = Octavia.query(MatriculaCurso.class, "mc")
                .join("matriculaResumen mr", "mr.alumno alu", "mr.cicloAcademico ca", "curso cu")
                .filter("ca.estado", CicloAcademicoEstadoEnum.ACT)
                .in("mc.estado", Arrays.asList(PMAT.name(), MAT.name()))
                .filter("alu.id", alumno);

        return sql.all(getCurrentSession());

    }

    @Override
    public MatriculaCurso findByMatriculaCurso(MatriculaResumen matriculaResumen, Curso curso) {
        Octavia sql = Octavia.query()
                .from(MatriculaCurso.class, "mc")
                .join("matriculaResumen mr", "mr.alumno alu", "mr.cicloAcademico ca", "curso cu")
                .filter("mr.id", matriculaResumen)
                .filter("cu.id", curso);

        return find(sql);
    }

    @Override
    public MatriculaCurso findByMatriculaCursoAndNotEstado(MatriculaResumen matriculaResumen, Curso curso, EstadoMatriculaEnum... estadoMatriculaEnum) {
        Octavia sql = Octavia.query()
                .from(MatriculaCurso.class, "mc")
                .join("matriculaResumen mr", "mr.alumno alu", "mr.cicloAcademico ca", "curso cu")
                .filter("mr.id", matriculaResumen)
                .filter("cu.id", curso)
                .notIn("mc,estado", Arrays.asList(estadoMatriculaEnum));

        return find(sql);
    }

    @Override
    public List<MatriculaCurso> allByCicloAcademico(CicloAcademico cicloAcademico) {
        Octavia sqlSub = new Octavia()
                .from(MatriculaSimultaneo.class, "ms")
                .join("matriculaCurso mcs");

        Octavia sql = new Octavia()
                .from(MatriculaCurso.class, "mc")
                .left("tipoCursoCurricula tc", "curso cu")
                .join("matriculaResumen mr", "mr.alumno alu", "mr.cicloAcademico ca")
                .notExists(sqlSub)
                .linkedBy("mc.id", "mcs.id")
                .filter("ca.id", cicloAcademico)
                .filter("mc.estado", EstadoMatriculaEnum.PMAT)
                .orderBy("mr.prioridad");
        return all(sql);
    }

    @Override
    public List<MatriculaCurso> allByMatriculaSimResumenes(List<MatriculaResumen> resumenes) {
        Octavia sqlSub = new Octavia()
                .from(MatriculaSimultaneo.class, "ms")
                .join("matriculaCurso mcs");

        Octavia sql = new Octavia()
                .from(MatriculaCurso.class, "mc")
                .left("tipoCursoCurricula tc", "curso cu")
                .join("matriculaResumen mr", "mr.alumno alu", "mr.cicloAcademico ca")
                .exists(sqlSub)
                .linkedBy("mc.id", "mcs.id")
                .in("mr.id", resumenes)
                .filter("mc.estado", EstadoMatriculaEnum.PMAT)
                .orderBy("mr.prioridad");
        return all(sql);
    }

    @Override
    public void updateInasistencias(MatriculaCurso matCurso) {
        Octavia updateSql = Octavia.update(MatriculaCurso.class);
        updateSql.set(matCurso, "inasistencias");
        this.update(updateSql);
    }

    public void updateColumns(MatriculaCurso matriculaCurso, String... columns) {
        Octavia sql = Octavia.update(MatriculaCurso.class, "se");
        for (String column : columns) {
            sql.set(matriculaCurso, column);
        }
        this.update(sql);
    }

    @Override
    public List<MatriculaCurso> allByCiclosFull(List<CicloAcademico> ciclos) {
        Octavia sqlSubquery = Octavia.query()
                .from(MatriculaResumen.class, "mr1")
                .join("alumno alu1", "cicloAcademico ca1")
                .in("ca1.id", ciclos);

        Octavia sql = Octavia.query()
                .from(MatriculaCurso.class, "mc")
                .join("matriculaResumen mr", "mr.alumno alu", "mr.cicloAcademico ca", "curso cu")
                .join("alu.persona per", "alu.carrera")
                .leftJoin("per.tipoDocumento", "alu.orientacionCarrera")
                .orderBy("ca.codigo asc")
                .exists(sqlSubquery)
                .linkedBy("alu.id", "alu1.id");

        return all(sql);
    }

    @Override
    public List<MatriculaCurso> allByMatriculaResumenFull(List<MatriculaResumen> matr) {

        Octavia sql = Octavia.query()
                .from(MatriculaCurso.class, "mc")
                .join("matriculaResumen mr", "mr.alumno alu", "mr.cicloAcademico ca", "curso cu")
                .join("alu.persona per", "alu.carrera")
                .leftJoin("per.tipoDocumento", "alu.orientacionCarrera")
                .orderBy("ca.codigo asc")
                .in("mr.id", matr);

        return all(sql);
    }

    @Override
    public List<MatriculaCurso> allByOcultoMaipi(CicloAcademico cicloAcademico) {
        Octavia sql = Octavia.query()
                .from(MatriculaCurso.class, "mc")
                .join("matriculaResumen mr", "mr.alumno alu", "mr.cicloAcademico ca", "curso cu")
                .filter("ca.id", cicloAcademico)
                .filter("mc.ocultoMaipi", 1);
        return all(sql);
    }

    @Override
    public List<MatriculaCurso> allActivosByAlumnoCicloCursos(Alumno alumno, CicloAcademico ciclo, List<Curso> cursos) {
        Octavia sql = Octavia.query()
                .from(MatriculaCurso.class, "mc")
                .join("matriculaResumen mr", "mr.alumno alu", "mr.cicloAcademico ca", "curso cu")
                .filter("alu.id", alumno)
                .filter("ca.id", ciclo)
                .filter("mc.estado", EstadoMatriculaEnum.MAT)
                .in("cu.id", cursos);
        return all(sql);
    }

    @Override
    public int updateList(List<MatriculaCurso> matriculaCursos, String... columnas) {
        if (matriculaCursos.isEmpty()) {
            return 0;
        }

        long t1 = System.currentTimeMillis();
        Insecto sql = Insecto.createUpdate(CursoCicloAcademico.class)
                .set(columnas)
                .with(matriculaCursos);

        Query query = getCurrentSession().createSQLQuery(sql.toString());
        int rows = query.executeUpdate();

        return rows;
    }

    @Override
    public List<MatriculaCurso> allmatriculadoByCiclo(CicloAcademico cicloAcademico) {
        Octavia sql = Octavia.query()
                .from(MatriculaCurso.class, "mc")
                .join("matriculaResumen mr", "curso cu")
                .join("mr.alumno alu", "mr.cicloAcademico ca")
                .join("alu.modalidadEstudio", "alu.carrera car", "car.facultad")
                .leftJoin("alu.orientacionCarrera", "alu.situacionAcademica", "alu.cicloIngreso")
                .filter("ca.id", cicloAcademico)
                .filter("mc.estado", MAT);
        return all(sql);
    }

}
