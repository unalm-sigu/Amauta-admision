package pe.edu.lamolina.pivot.dao.academico.hibernate;

import java.util.List;
import pe.edu.lamolina.pivot.dao.academico.MatriculaSeccionDAO;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableSql;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.AlumnoHorario;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.GrupoSeccion;
import pe.edu.lamolina.model.academico.MatriculaResumen;
import pe.edu.lamolina.model.academico.MatriculaSeccion;
import pe.edu.lamolina.model.academico.ModalidadEstudio;
import pe.edu.lamolina.model.academico.Seccion;
import pe.edu.lamolina.model.enums.EstadoMatriculaEnum;
import static pe.edu.lamolina.model.enums.EstadoMatriculaEnum.MAT;

@Repository
public class MatriculaSeccionDAOH extends AbstractEasyDAO<MatriculaSeccion> implements MatriculaSeccionDAO {

    public MatriculaSeccionDAOH() {
        super();
        setClazz(MatriculaSeccion.class);
    }

    @Override
    public List<MatriculaSeccion> allBySeccion(Seccion seccion) {
        Octavia sql = Octavia.query()
                .from(MatriculaSeccion.class, "ms")
                .join("matriculaResumen mr", "seccion sec", "mr.alumno alu", "sec.grupoSeccion gs")
                .join("gs.curso cur", "alu.persona per", "per.tipoDocumento tdoc")
                .leftJoin("alu.carrera carr", "carr.facultad fac")
                .filter("ms.estado", EstadoMatriculaEnum.MAT)
                .filter("sec.id", seccion)
                .orderBy("per.paterno", "per.materno", "per.nombres");

        return all(sql);
    }

    @Override
    public MatriculaSeccion find(Long id) {
        Octavia sql = Octavia.query()
                .from(MatriculaSeccion.class, "ms")
                .join("matriculaResumen mr", "seccion sec", "mr.alumno alu", "sec.grupoSeccion gs")
                .join("gs.curso cur", "alu.persona per", "per.tipoDocumento tdoc")
                .filter("ms.id", id);

        return find(sql);
    }

    @Override
    public MatriculaSeccion findByAlumnoSeccion(Alumno alumno, Seccion seccion) {
        Octavia sql = Octavia.query()
                .from(MatriculaSeccion.class, "ms")
                .join("matriculaResumen mr", "seccion sec", "mr.alumno alu", "sec.grupoSeccion gs")
                .join("gs.curso cur", "alu.persona per", "per.tipoDocumento tdoc")
                .filter("sec.id", seccion)
                .filter("alu.id", alumno);

        return find(sql);
    }

    @Override
    public List<MatriculaSeccion> allByMatriculaSeccion(MatriculaResumen resumen) {
        Octavia sql = Octavia.query()
                .from(MatriculaSeccion.class, "ms")
                .join("matriculaResumen mr", "seccion sec", "mr.alumno alu", "sec.grupoSeccion gs", "gs.cicloAcademico ca")
                .join("gs.curso cur", "alu.persona per", "per.tipoDocumento tdoc")
                .filter("mr.id", resumen);

        return all(sql);
    }

    @Override
    public List<MatriculaSeccion> allByGpoSeccion(GrupoSeccion grupoSeccion, CicloAcademico ciclo) {
        Octavia sql = Octavia.query()
                .from(MatriculaSeccion.class, "ms")
                .join("matriculaResumen mr", "seccion sec", "mr.alumno alu", "sec.grupoSeccion gs", "gs.cicloAcademico ca")
                .join("gs.curso cur", "alu.persona per", "per.tipoDocumento tdoc")
                .filter("ms.estado", MAT)
                .filter("gs.id", grupoSeccion)
                .filter("ca.id", ciclo);

        return all(sql);
    }

    @Override
    public List<MatriculaSeccion> allByCiclo(CicloAcademico ciclo) {
        Octavia sql = Octavia.query()
                .from(MatriculaSeccion.class, "ms")
                .join("matriculaResumen mr", "seccion sec", "mr.alumno alu", "sec.grupoSeccion gs", "gs.cicloAcademico ca")
                .join("gs.curso cur", "alu.persona per", "per.tipoDocumento tdoc")
                .leftJoin("gs.planCalificacion")
                .filter("ms.estado", MAT)
                .filter("ca.id", ciclo);

        return all(sql);
    }

    @Override
    public List<MatriculaSeccion> allByAlumnoCiclo(Alumno alumno, CicloAcademico ciclo) {
        Octavia sqlUtil = Octavia.query()
                .from(MatriculaSeccion.class, "ms")
                .join("matriculaResumen mr", "mr.alumno alu", "mr.cicloAcademico ca")
                .join("seccion s", "s.grupoSeccion gs", "gs.curso")
                .left("s.aula")
                .filter("ca.id", ciclo)
                .filter("alu.id", alumno);
        return all(sqlUtil);
    }

    @Override
    public List<MatriculaSeccion> allMatByAlumnosCiclo(List<AlumnoHorario> alumnos, CicloAcademico ciclo) {
        Octavia sqlUtil = Octavia.query()
                .from(MatriculaSeccion.class, "ms")
                .join("matriculaResumen mr", "mr.alumno alu", "mr.cicloAcademico ca")
                .join("seccion sec", "sec.grupoSeccion gs", "gs.curso")
                .leftJoin("sec.seccionSuperior")
                .leftJoin("sec.aula", "sec.grupoHoras", "sec.aula")
                .filter("mr.estado", EstadoMatriculaEnum.MAT)
                .filter("ca.id", ciclo)
                .in("alu.id", alumnos);
        return all(sqlUtil);
    }

    @Override
    public List<MatriculaSeccion> allByAlumnoCicloEstados(Alumno alumno, CicloAcademico academico, List<String> estados) {
        Octavia sql = Octavia.query()
                .from(MatriculaSeccion.class, "ms")
                .join("matriculaResumen mr", "mr.alumno alu", "mr.cicloAcademico ca")
                .join("seccion s", "s.grupoSeccion gs", "gs.curso")
                .leftJoin("s.aula")
                .filter("ca.id", academico)
                .filter("alu.id", alumno)
                .in("ms.estado", estados);
        return sql.all(getCurrentSession());
    }

    @Override
    public Long countAllSeccionPrematriculado(CicloAcademico cicloAcademico) {
        Octavia sql = Octavia.query()
                .selectCountDistinct("ms.id")
                .from(MatriculaSeccion.class, "ms")
                .join("matriculaResumen mr", "mr.alumno alu", "mr.cicloAcademico ca")
                .join("seccion s", "s.grupoSeccion gs", "gs.curso")
                .left("s.aula")
                .filter("ca.id", cicloAcademico)
                .filter("ms.estado", EstadoMatriculaEnum.PMAT)
                .filter("mr.estado", EstadoMatriculaEnum.NMAT);
        return (Long) sql.find(getCurrentSession());
    }

    @Override
    public List<MatriculaSeccion> allPrematriculadoByMatriculaResumen(List<MatriculaResumen> matriculaResumens) {
        Octavia sql = Octavia.query()
                .from(MatriculaSeccion.class, "ms")
                .join("matriculaResumen mr", "seccion sec", "mr.alumno alu", "sec.grupoSeccion gs", "gs.cicloAcademico ca")
                .join("gs.curso cur", "alu.persona per", "per.tipoDocumento tdoc")
                .in("mr.id", matriculaResumens)
                .filter("ms.estado", EstadoMatriculaEnum.PMAT);
        return all(sql);
    }

    @Override
    public List<MatriculaSeccion> allByModalidadEstudioCiclo(ModalidadEstudio modalidad, CicloAcademico cicloAcademico) {
        Octavia sql = Octavia.query()
                .from(MatriculaSeccion.class, "ms")
                .join("seccion sec", "sec.grupoSeccion gs", "gs.curso cur", "cur.modalidadEstudio me", "gs.cicloAcademico ci")
                .join("matriculaResumen mr", "mr.alumno alu", "alu.modalidadEstudio mo", "alu.persona per", "mr.cicloAcademico ca")
                .filter("ms.estado", EstadoMatriculaEnum.MAT)
                .filter("ca.id", cicloAcademico)
                .filter("ci.id", cicloAcademico)
                .filter("mo.id", modalidad)
                .filter("me.id", modalidad)
                .orderBy("per.paterno", "per.materno", "per.nombres");
        return all(sql);
    }

}
