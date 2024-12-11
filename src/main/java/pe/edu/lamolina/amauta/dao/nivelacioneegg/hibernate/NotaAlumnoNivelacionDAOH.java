package pe.edu.lamolina.amauta.dao.nivelacioneegg.hibernate;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Query;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Insecto;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableSql;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.amauta.controller.nivelacioneegg.matriculables.dto.MatriculablesResumen;
import pe.edu.lamolina.amauta.dao.nivelacioneegg.NotaAlumnoNivelacionDAO;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.CicloAcademico;
import static pe.edu.lamolina.model.enums.EstadoMatriculaEnum.MAT;
import static pe.edu.lamolina.model.enums.EstadoMatriculaEnum.NMAT;
import pe.edu.lamolina.model.enums.SeccionEstadoEnum;
import pe.edu.lamolina.model.nivelacioneegg.AlumnoNivelacion;
import pe.edu.lamolina.model.nivelacioneegg.CursoNivelacion;
import pe.edu.lamolina.model.nivelacioneegg.NotaAlumnoNivelacion;

@Slf4j
@Repository
public class NotaAlumnoNivelacionDAOH extends AbstractEasyDAO<NotaAlumnoNivelacion> implements NotaAlumnoNivelacionDAO {

    public NotaAlumnoNivelacionDAOH() {
        super();
        setClazz(NotaAlumnoNivelacion.class);
    }

    @Override
    public NotaAlumnoNivelacion find(long id) {
        Octavia sql = Octavia.query()
                .from(NotaAlumnoNivelacion.class, "nan")
                .join("alumnoNivelacion an", "temaExamen te")
                .join("an.alumno alu", "alu.carrera car", "car.facultad fac")
                .join("alu.situacionAcademica", "alu.modalidadEstudio", "alu.persona per")
                .join("an.cicloAcademico ci")
                .leftJoin("per.tipoDocumento", "cursoNivelacion cn", "temaCiclo teci", "curso cur")
                .filter("nan.id", id);

        return find(sql);
    }

    @Override
    public List<NotaAlumnoNivelacion> allByDynatable(DynatableFilter filter, CicloAcademico ciclo) {
        DynatableSql sql = new DynatableSql(filter)
                .from(NotaAlumnoNivelacion.class, "nan")
                .join("alumnoNivelacion an", "temaExamen te", "curso cur")
                .join("an.alumno alu", "alu.carrera car", "car.facultad fac")
                .join("alu.situacionAcademica", "alu.modalidadEstudio", "alu.persona per")
                .join("an.cicloAcademico ci")
                .leftJoin("per.tipoDocumento", "cursoNivelacion cn", "temaCiclo teci", "cn.aula", "cn.grupoHoras")
                .filter("ci.id", ciclo)
                .filter("nan.esMatriculable", 1)
                .searchFields("car.nombre", "fac.nombre", "per.numeroDocIdentidad", "alu.codigo", "cur.codigo", "cur.nombre", "cn.codigo")
                .searchComplexField("concat(coalesce(per.paterno,''),' ',coalesce(per.materno,''),' ',coalesce(per.nombres,''))")
                .searchComplexField("concat(coalesce(per.nombres,''),' ',coalesce(per.paterno,''),' ',coalesce(per.materno,''))")
                .orderBy("nan.id DESC");

        sql.beginRelativeFilters();
        this.setCondicionEstado(filter, sql);

        return all(sql);
    }

    private void setCondicionEstado(DynatableFilter filter, DynatableSql sql) {
        Map<String, Object> queries = filter.getQueries();
        if (queries == null) {
            return;
        }

        for (String key : queries.keySet()) {
            if (!key.equals("situacion")) {
                continue;
            }
            String values = (String) queries.get(key);
            if (values.equals("inscritos")) {
                sql.filter("nan.estado", MAT);
            } else if (values.equals("pendientes")) {
                sql.filter("nan.estado", NMAT);
            }
        }

    }

    @Override
    public List<NotaAlumnoNivelacion> allSeccionByDynatable(DynatableFilter filter, CursoNivelacion cursoNiv) {
        DynatableSql sql = new DynatableSql(filter)
                .from(NotaAlumnoNivelacion.class, "nan")
                .join("alumnoNivelacion an", "temaExamen te", "curso cur")
                .join("an.alumno alu", "alu.carrera car", "car.facultad fac")
                .join("alu.situacionAcademica", "alu.modalidadEstudio", "alu.persona per")
                .join("an.cicloAcademico ci", "cursoNivelacion cn")
                .leftJoin("per.tipoDocumento", "temaCiclo teci", "cn.aula", "cn.grupoHoras")
                .filter("cn.id", cursoNiv)
                .filter("an.estado", MAT)
                .filter("nan.estado", MAT)
                .searchFields("car.nombre", "fac.nombre", "per.numeroDocIdentidad", "alu.codigo", "cur.codigo", "cur.nombre", "cn.codigo")
                .searchComplexField("concat(coalesce(per.paterno,''),' ',coalesce(per.materno,''),' ',coalesce(per.nombres,''))")
                .searchComplexField("concat(coalesce(per.nombres,''),' ',coalesce(per.paterno,''),' ',coalesce(per.materno,''))")
                .orderBy("per.paterno", "per.materno", "per.nombres");

        return all(sql);
    }

    @Override
    public MatriculablesResumen findResumen(CicloAcademico ciclo) {
        StringBuilder sql = new StringBuilder();

        sql.append("select new ").append(MatriculablesResumen.class.getName());
        sql.append(" (   ");
        sql.append("   sum(case nan.estado when 'MAT' then 1 else 0 end),   ");
        sql.append("   sum(case nan.estado when 'NMAT' then 1 else 0 end)   ");
        sql.append(" )   ");
        sql.append("  from ").append(NotaAlumnoNivelacion.class.getName()).append(" as nan ");
        sql.append(" inner join nan.curso cu ");
        sql.append(" inner join nan.alumnoNivelacion an ");
        sql.append(" inner join an.cicloAcademico ci ");
        sql.append(" where ci.id = :CICLO ");
        sql.append("   and nan.esMatriculable = 1 ");
        sql.append("   and an.estado in ('NMAT','MAT') ");
        sql.append("   and nan.estado in ('NMAT','MAT') ");

        Query query = getCurrentSession().createQuery(sql.toString());
        query.setParameter("CICLO", ciclo.getId());

        return (MatriculablesResumen) query.uniqueResult();
    }

    @Override
    public List<NotaAlumnoNivelacion> allByCiclo(CicloAcademico ciclo) {
        Octavia sql = Octavia.query()
                .from(NotaAlumnoNivelacion.class, "nan")
                .join("alumnoNivelacion an", "temaExamen te")
                .join("an.alumno alu", "alu.carrera car", "car.facultad fac")
                .join("alu.situacionAcademica", "alu.modalidadEstudio", "alu.persona per")
                .join("an.cicloAcademico ci")
                .leftJoin("per.tipoDocumento", "cursoNivelacion cn", "temaCiclo teci")
                .filter("ci.id", ciclo);

        return all(sql);
    }

    @Override
    public List<NotaAlumnoNivelacion> allActivosByCiclo(CicloAcademico ciclo) {
        Octavia sql = Octavia.query()
                .from(NotaAlumnoNivelacion.class, "nan")
                .join("alumnoNivelacion an", "temaExamen te", "curso")
                .join("an.alumno alu", "alu.carrera car", "car.facultad fac")
                .join("alu.situacionAcademica", "alu.modalidadEstudio", "alu.persona per")
                .join("an.cicloAcademico ci")
                .leftJoin("per.tipoDocumento", "cursoNivelacion cn", "temaCiclo teci")
                .isNull("cn.id")
                .in("an.estado", Arrays.asList(NMAT, MAT))
                .in("nan.estado", Arrays.asList(NMAT, MAT))
                .filter("ci.id", ciclo);

        return all(sql);
    }

    @Override
    public List<NotaAlumnoNivelacion> allSinCursoByCiclo(CicloAcademico ciclo) {
        Octavia sql = Octavia.query()
                .from(NotaAlumnoNivelacion.class, "nan")
                .join("alumnoNivelacion an", "temaExamen te")
                .join("an.alumno alu", "alu.carrera car", "car.facultad fac")
                .join("alu.situacionAcademica", "alu.modalidadEstudio", "alu.persona per")
                .join("an.cicloAcademico ci")
                .leftJoin("per.tipoDocumento", "cursoNivelacion cn", "temaCiclo teci")
                .leftJoin("curso cur")
                .isNull("cur.id")
                .in("an.estado", Arrays.asList(NMAT, MAT))
                .filter("nan.estado", NMAT)
                .filter("ci.id", ciclo);

        return all(sql);
    }

    @Override
    public List<NotaAlumnoNivelacion> allConCursoByCiclo(CicloAcademico ciclo) {
        Octavia sql = Octavia.query()
                .from(NotaAlumnoNivelacion.class, "nan")
                .join("alumnoNivelacion an", "temaExamen te", "curso")
                .join("an.alumno alu", "alu.carrera car", "car.facultad fac")
                .join("alu.situacionAcademica", "alu.modalidadEstudio", "alu.persona per")
                .join("an.cicloAcademico ci")
                .leftJoin("per.tipoDocumento", "cursoNivelacion cn", "temaCiclo teci")
                .isNull("cn.id")
                .in("an.estado", Arrays.asList(NMAT, MAT))
                .filter("nan.estado", NMAT)
                .filter("ci.id", ciclo);

        return all(sql);
    }

    @Override
    public List<NotaAlumnoNivelacion> allInscritosByCursoNivelacion(CursoNivelacion cursoNiv) {
        Octavia sql = Octavia.query()
                .from(NotaAlumnoNivelacion.class, "nan")
                .join("alumnoNivelacion an", "temaExamen te", "curso")
                .join("an.alumno alu", "alu.carrera car", "car.facultad fac")
                .join("alu.situacionAcademica", "alu.modalidadEstudio", "alu.persona per")
                .join("an.cicloAcademico ci", "cursoNivelacion cn")
                .leftJoin("per.tipoDocumento", "temaCiclo teci")
                .filter("an.estado", MAT)
                .filter("nan.estado", MAT)
                .filter("cn.estado", SeccionEstadoEnum.ACT)
                .filter("cn.id", cursoNiv);

        return all(sql);
    }

    @Override
    public List<NotaAlumnoNivelacion> allByAlumnosCiclo(List<Alumno> alumnos, CicloAcademico ciclo) {
        Octavia sql = Octavia.query()
                .from(NotaAlumnoNivelacion.class, "nan")
                .join("alumnoNivelacion an", "an.cicloAcademico ci", "an.alumno alu")
                .join("temaCiclo tc", "tc.temaExamen")
                .leftJoin("an.prelamolina", "an.evaluado")
                .in("alu.id", alumnos)
                .filter("ci.id", ciclo);

        return all(sql);
    }

    @Override
    public List<NotaAlumnoNivelacion> allByAlumnoNivelacion(AlumnoNivelacion alumnoNiv) {
        Octavia sql = Octavia.query()
                .from(NotaAlumnoNivelacion.class, "nan")
                .join("alumnoNivelacion an", "an.cicloAcademico ci", "an.alumno alu")
                .join("temaExamen")
                .leftJoin("temaCiclo tc", "tc.temaExamen te", "te.temaSuperior")
                .leftJoin("an.prelamolina", "an.evaluado", "cursoNivelacion cn")
                .filter("an.id", alumnoNiv);

        return all(sql);
    }

    @Override
    public List<NotaAlumnoNivelacion> allByAlumnosNivelacion(List<AlumnoNivelacion> alumnosNiv) {
        Octavia sql = Octavia.query()
                .from(NotaAlumnoNivelacion.class, "nan")
                .join("alumnoNivelacion an", "an.cicloAcademico ci", "an.alumno alu")
                .join("temaExamen")
                .leftJoin("temaCiclo tc", "tc.temaExamen te", "te.temaSuperior")
                .leftJoin("an.prelamolina", "an.evaluado")
                .in("an.id", alumnosNiv);

        return all(sql);
    }

    @Override
    public List<NotaAlumnoNivelacion> allByCursoNivelacion(CursoNivelacion cursoNiv) {
        Octavia sql = Octavia.query()
                .from(NotaAlumnoNivelacion.class, "nan")
                .join("alumnoNivelacion an", "temaExamen te", "curso cur")
                .join("an.alumno alu", "alu.carrera car", "car.facultad fac")
                .join("alu.situacionAcademica", "alu.modalidadEstudio", "alu.persona per")
                .join("an.cicloAcademico ci", "cursoNivelacion cn")
                .leftJoin("per.tipoDocumento", "temaCiclo teci", "cn.aula", "cn.grupoHoras")
                .filter("cn.id", cursoNiv)
                .filter("an.estado", MAT)
                .filter("nan.estado", MAT)
                .orderBy("per.paterno", "per.materno", "per.nombres");

        return all(sql);
    }

    @Override
    public int saveList(List<NotaAlumnoNivelacion> notasAlumnos) {
        if (notasAlumnos.isEmpty()) {
            return 0;
        }

        long t1 = System.currentTimeMillis();
        Insecto sql = Insecto.createInsert()
                .into(NotaAlumnoNivelacion.class)
                .columns("estado", "notaExamen", "puntajeExamen", "temaAprobado",
                        "notaCurso", "esMatriculable", "fechaRegistro",
                        "alumnoNivelacion", "temaCiclo", "temaExamen", "curso", "cursoNivelacion", "userRegistro")
                .values(notasAlumnos);

        Query query = getCurrentSession().createSQLQuery(sql.toString());
        int rows = query.executeUpdate();

        long t2 = System.currentTimeMillis();
        log.info("{} NotaAlumnoNivelacion's insertados en {} mseg....", rows, (t2 - t1));
        return rows;
    }

    @Override
    public int updateList(List<NotaAlumnoNivelacion> notasAlumnos, String... columnas) {
        if (notasAlumnos.isEmpty()) {
            return 0;
        }

        long t1 = System.currentTimeMillis();
        Insecto sql = Insecto.createUpdate(NotaAlumnoNivelacion.class)
                .set(columnas)
                .with(notasAlumnos);

        Query query = getCurrentSession().createSQLQuery(sql.toString());
        int rows = query.executeUpdate();

        long t2 = System.currentTimeMillis();
        log.info("{} NotaAlumnoNivelacion's actualizados en {} mseg....", rows, (t2 - t1));
        return rows;
    }

}
