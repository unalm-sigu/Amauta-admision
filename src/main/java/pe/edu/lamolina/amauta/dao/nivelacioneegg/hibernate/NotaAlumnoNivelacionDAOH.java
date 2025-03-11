package pe.edu.lamolina.amauta.dao.nivelacioneegg.hibernate;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Query;
import org.hibernate.transform.Transformers;
import org.hibernate.type.BigDecimalType;
import org.hibernate.type.StringType;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Insecto;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableSql;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.amauta.controller.nivelacioneegg.matriculables.dto.MatriculablesResumen;
import pe.edu.lamolina.amauta.controller.nivelacioneegg.reportes.ExcelData.Bean.ResultadoReporteView;
import pe.edu.lamolina.amauta.dao.nivelacioneegg.NotaAlumnoNivelacionDAO;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.enums.EstadoGrupoSeccionEnum;
import static pe.edu.lamolina.model.enums.EstadoMatriculaEnum.INH;
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
    public List<NotaAlumnoNivelacion> allByDynatableCiclo(DynatableFilter filter, CicloAcademico ciclo) {
        DynatableSql sql = new DynatableSql(filter)
                .from(NotaAlumnoNivelacion.class, "nan")
                .join("alumnoNivelacion an", "temaExamen te", "curso cur")
                .join("an.alumno alu", "alu.carrera car", "car.facultad fac")
                .join("alu.postulantePregrado pp", "pp.modalidadIngreso mi", "pp.cicloPostula cp", "cp.cicloAcademico cai")
                .join("alu.situacionAcademica", "alu.modalidadEstudio", "alu.persona per")
                .join("an.cicloAcademico ci")
                .leftJoin("per.tipoDocumento", "cursoNivelacion cn", "temaCiclo teci", "cn.aula", "cn.grupoHoras")
                .filter("ci.id", ciclo)
                .filter("an.estado", "<>", INH)
                .filter("nan.esMatriculable", 1)
                .searchFields("car.nombre", "fac.nombre", "per.numeroDocIdentidad", "alu.codigo", "cur.codigo", "cur.nombre", "cn.codigo", "cai.codigoAnterior", "mi.nombre", "nan.estado", "an.estado")
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
    public List<NotaAlumnoNivelacion> allByDynatableSeccion(DynatableFilter filter, CursoNivelacion cursoNiv) {
        DynatableSql sql = new DynatableSql(filter)
                .from(NotaAlumnoNivelacion.class, "nan")
                .join("alumnoNivelacion an", "temaExamen te", "curso cur")
                .join("an.alumno alu", "alu.carrera car", "car.facultad fac")
                .join("alu.situacionAcademica", "alu.modalidadEstudio", "alu.persona per")
                .join("alu.postulantePregrado pp", "pp.modalidadIngreso mi", "pp.cicloPostula cp", "cp.cicloAcademico cai")
                .join("an.cicloAcademico ci", "cursoNivelacion cn")
                .leftJoin("per.tipoDocumento", "temaCiclo teci", "cn.aula", "cn.grupoHoras")
                .filter("cn.id", cursoNiv)
                .filter("an.estado", MAT)
                .filter("nan.estado", MAT)
                .searchFields("car.nombre", "fac.nombre", "per.numeroDocIdentidad", "alu.codigo", "cur.codigo", "cur.nombre", "cn.codigo", "cai.codigoAnterior", "mi.nombre")
                .searchComplexField("concat(coalesce(per.paterno,''),' ',coalesce(per.materno,''),' ',coalesce(per.nombres,''))")
                .searchComplexField("concat(coalesce(per.nombres,''),' ',coalesce(per.paterno,''),' ',coalesce(per.materno,''))")
                .orderBy("per.paterno", "per.materno", "per.nombres");

        return all(sql);
    }

    @Override
    public List<NotaAlumnoNivelacion> allBySeccion(CursoNivelacion cursoNiv) {
        Octavia sql = Octavia.query()
                .from(NotaAlumnoNivelacion.class, "nan")
                .join("alumnoNivelacion an", "temaExamen te", "curso cur")
                .join("an.alumno alu", "alu.carrera car", "car.facultad fac")
                .join("alu.situacionAcademica", "alu.modalidadEstudio", "alu.persona per")
                .join("alu.postulantePregrado pp", "pp.modalidadIngreso mi", "pp.cicloPostula cp", "cp.cicloAcademico cai")
                .join("an.cicloAcademico ci", "cursoNivelacion cn")
                .leftJoin("per.tipoDocumento", "temaCiclo teci", "cn.aula", "cn.grupoHoras")
                .filter("cn.id", cursoNiv)
                .filter("an.estado", MAT)
                .filter("nan.estado", MAT)
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
                .filter("nan.esMatriculable", 1)
                .in("an.estado", Arrays.asList(NMAT, MAT))
                .filter("nan.estado", NMAT)
                .filter("ci.id", ciclo)
                .orderBy("car.nombre", "per.paterno", "per.materno", "per.nombres");

        return all(sql);
    }

    @Override
    public List<NotaAlumnoNivelacion> allMatriculadosByCiclo(CicloAcademico ciclo) {
        Octavia sql = Octavia.query()
                .from(NotaAlumnoNivelacion.class, "nan")
                .join("alumnoNivelacion an", "temaExamen te", "curso")
                .join("an.alumno alu", "alu.carrera car", "car.facultad fac")
                .join("alu.situacionAcademica", "alu.modalidadEstudio", "alu.persona per")
                .join("an.cicloAcademico ci", "cursoNivelacion cn")
                .leftJoin("per.tipoDocumento", "temaCiclo teci")
                .filter("nan.esMatriculable", 1)
                .filter("an.estado", MAT)
                .filter("nan.estado", MAT)
                .filter("ci.id", ciclo);

        return all(sql);
    }

    @Override
    public List<NotaAlumnoNivelacion> allConNotaByAlumno(Alumno alumno) {
        Octavia sql = Octavia.query()
                .from(NotaAlumnoNivelacion.class, "nan")
                .join("alumnoNivelacion an", "temaExamen te", "curso")
                .join("an.alumno alu", "alu.carrera car", "car.facultad fac")
                .join("alu.situacionAcademica", "alu.modalidadEstudio", "alu.persona per")
                .join("an.cicloAcademico ci", "cursoNivelacion cn")
                .leftJoin("per.tipoDocumento", "temaCiclo teci")
                .isNotNull("nan.notaCurso")
                .filter("cn.estadoNotas", EstadoGrupoSeccionEnum.CER)
                .filter("nan.esMatriculable", 1)
                .filter("an.estado", MAT)
                .filter("nan.estado", MAT)
                .filter("alu.id", alumno)
                .orderBy("ci.codigo DESC");

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
                .filter("nan.esMatriculable", 1)
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
                .leftJoin("an.prelamolina", "an.evaluado", "cursoNivelacion cn", "cn.cursoCiclo")
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
                .in("an.id", alumnosNiv)
                .orderBy("an.id", "nan.id");

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

    @Override
    public List<ResultadoReporteView> allResultadoNotaSeccionByCicloAndSeccion(CicloAcademico cicloAcademico, String seccion) {
        StringBuilder sql = new StringBuilder();
        sql.append(" SELECT ");
        sql.append("    cu.nombre curso, ");
        sql.append("    CASE ");
        sql.append("        WHEN doc.id_persona IS NULL THEN 'DESCONOCIDO' ");
        sql.append("        ELSE CONCAT(IFNULL(per.paterno,''), ' ', IFNULL(per.materno,''), ', ', IFNULL(per.nombres,''))  ");
        sql.append("    END AS docente, ");
        sql.append("    cn.codigo AS seccion, ");
        sql.append("    caa.descripcion AS ciclo, ");
        sql.append("    a.codigo AS matricula, ");
        sql.append("    CONCAT(IFNULL(pe.paterno,''), ' ', IFNULL(pe.materno,''), ', ', IFNULL(pe.nombres,'')) AS apellidosNombre, ");
        sql.append("    MAX(CASE WHEN ten.codigo = 'EVA1' THEN round(ifnull(ean.nota_examen,'Sin Nota'),0) END) AS evaluacionParcial1, ");
        sql.append("    MAX(CASE WHEN ten.codigo = 'EVA2' THEN round(ifnull(ean.nota_examen,'Sin Nota'),0) END) AS evaluacionParcial2, ");
        sql.append("    MAX(CASE WHEN ten.codigo = 'EF' THEN round(ifnull(ean.nota_examen,'Sin Nota'),0) END) AS examenFinal, ");
        sql.append("    nan.nota_curso AS promedioFinal, ");
        sql.append("    CASE WHEN nan.aprobado THEN 'Aprobado' ELSE 'Desaprobado' END AS condicion ");
        sql.append(" FROM eegg_nota_alumno_nivelacion nan ");
        sql.append(" JOIN eegg_alumno_nivelacion an ON an.id = nan.id_alumno_nivelacion ");
        sql.append(" JOIN aca_alumno a ON a.id = an.id_alumno ");
        sql.append(" JOIN aca_carrera car ON a.id_carrera = car.id ");
        sql.append(" JOIN gen_persona pe ON pe.id = a.id_persona ");
        sql.append(" JOIN eegg_examen_alumno_nivelacion ean ON ean.id_nota_alumno_nivelacion = nan.id ");
        sql.append(" JOIN eegg_examen_curso_nivelacion ecn ON ean.id_examen_curso_nivelacion = ecn.id ");
        sql.append(" JOIN eegg_tipo_examen_nivelacion ten ON ecn.id_tipo_examen_nivelacion = ten.id ");
        sql.append(" JOIN eegg_curso_nivelacion cn ON cn.id = nan.id_curso_nivelacion ");
        sql.append(" LEFT JOIN aca_docente doc ON cn.id_docente = doc.id ");
        sql.append(" LEFT JOIN gen_persona per ON doc.id_persona = per.id ");
        sql.append(" JOIN aca_curso_ciclo_academico cc ON cc.id = cn.id_curso_ciclo_academico ");
        sql.append(" JOIN aca_ciclo_academico caa ON cc.id_ciclo_academico = caa.id ");
        sql.append(" JOIN aca_curso cu ON cu.id = cc.id_curso ");
        sql.append(" WHERE caa.id = :CICLO ");
        sql.append("    AND an.estado IN ('NMAT','MAT') ");
        sql.append("    AND nan.estado IN ('NMAT','MAT') ");
        if (seccion != null) {
            sql.append("    AND cn.codigo = :SECCION ");
        }
        sql.append("    AND nan.tema_aprobado = false ");
        sql.append(" GROUP BY ");
        sql.append("   cu.nombre, cn.codigo, caa.descripcion, a.codigo, pe.paterno, pe.materno, pe.nombres, nan.nota_curso, nan.aprobado, doc.id_persona, per.paterno, per.materno, per.nombres ");
        sql.append(" ORDER BY  4, 1, 3, 6; ");

        Query query = getCurrentSession().createSQLQuery(sql.toString())
                .addScalar("curso", StringType.INSTANCE)
                .addScalar("docente", StringType.INSTANCE)
                .addScalar("seccion", StringType.INSTANCE)
                .addScalar("ciclo", StringType.INSTANCE)
                .addScalar("matricula", StringType.INSTANCE)
                .addScalar("apellidosNombre", StringType.INSTANCE)
                .addScalar("evaluacionParcial1", BigDecimalType.INSTANCE)
                .addScalar("evaluacionParcial2", BigDecimalType.INSTANCE)
                .addScalar("examenFinal", BigDecimalType.INSTANCE)
                .addScalar("promedioFinal", BigDecimalType.INSTANCE)
                .addScalar("condicion", StringType.INSTANCE)
                .setResultTransformer(Transformers.aliasToBean(ResultadoReporteView.class));

        query.setParameter("CICLO", cicloAcademico.getId());
        if (seccion != null) {
            query.setParameter("SECCION", seccion);
        }
        return (List<ResultadoReporteView>) query.list();

    }

    @Override
    public List<ResultadoReporteView> allIngresantesDesaprobadosByCiclo(CicloAcademico cicloAcademico) {
        StringBuilder sql = new StringBuilder();
        sql.append(" select ROW_NUMBER() OVER (PARTITION BY cu.nombre, cn.codigo ORDER BY cu.nombre, cn.codigo, pe.id) AS correlativo,a.codigo matricula,pe.numero_doc_identidad dni, ");
        sql.append(" concat(ifnull(pe.paterno,''), ' ',ifnull(pe.materno,''),', ', ifnull(pe.nombres,''))apellidosNombre, ");
        sql.append(" mi.nombre modalidadIngreso,car.nombre carrera,fa.nombre facultad,pe.email correoPersonal,pe.email_corporativo correoOutlook,pe.email_compania correoGmail,pe.celular,pe.telefono, ");
        sql.append(" cu.nombre curso,te.nombre temaCurso,nan.puntaje_examen puntajeCurso,cn.codigo seccion,aus.nombre moduloAula, au.codigo aula, ");
        sql.append(" concat(ifnull(per.paterno,''), ' ',ifnull(per.materno,''),', ', ifnull(per.nombres,'')) docente,per.email_compania correoDocente, ");
        sql.append(" case ");
        sql.append(" when uu.id is null then 'NO TIENE USUARIO' ");
        sql.append(" else 'SI TIENE USUARIO' ");
        sql.append(" end usuario ");
        sql.append(" from eegg_nota_alumno_nivelacion nan ");
        sql.append(" join eegg_alumno_nivelacion an on an.id = nan.id_alumno_nivelacion ");
        sql.append(" join aca_alumno a on a.id = an.id_alumno ");
        sql.append(" join aca_carrera car on a.id_carrera = car.id ");
        sql.append(" join aca_facultad fa on car.id_facultad = fa.id ");
        sql.append(" join gen_persona pe on pe.id = a.id_persona ");
        sql.append(" left join seg_usuario uu on uu.id_persona = pe.id and uu.estado = 'ACT' ");
        sql.append(" join sip_postulante po on po.id = a.id_postulante_pregrado ");
        sql.append(" join sip_modalidad_ingreso mi on mi.id = po.id_modalidad_ingreso ");
        sql.append(" join aca_ciclo_academico ci on ci.id = an.id_ciclo_academico ");
        sql.append(" join sce_tema_examen te on te.id = nan.id_tema_examen ");
        sql.append(" join eegg_curso_nivelacion cn on cn.id = nan.id_curso_nivelacion ");
        sql.append(" left join gen_aula au on cn.id_aula = au.id ");
        sql.append(" left join gen_aula aus on au.id_aula_superior = aus.id ");
        sql.append(" left join aca_docente doc on cn.id_docente = doc.id ");
        sql.append(" left join gen_persona per on doc.id_persona = per.id ");
        sql.append(" left join aca_curso_ciclo_academico cc on cc.id = cn.id_curso_ciclo_academico ");
        sql.append(" left join aca_curso cu on cu.id = cc.id_curso ");
        sql.append(" join eegg_modalidad_tema_ciclo mtc ");
        sql.append("    on mtc.id_ciclo_academico = an.id_ciclo_academico ");
        sql.append("    and mtc.id_tema_examen = nan.id_tema_examen ");
        sql.append("    and mtc.otras_modalidades = case po.id_modalidad_ingreso when 16 then 0 else 1 end ");
        sql.append(" where ci.id = :CICLO ");
        sql.append(" and an.estado in ('NMAT','MAT') ");
        sql.append(" and nan.estado in ('NMAT','MAT') ");
        sql.append(" and te.id_tema_superior is null ");
        sql.append(" and nan.tema_aprobado = false; ");

        Query query = getCurrentSession().createSQLQuery(sql.toString())
                .addScalar("correlativo", StringType.INSTANCE)
                .addScalar("matricula", StringType.INSTANCE)
                .addScalar("dni", StringType.INSTANCE)
                .addScalar("apellidosNombre", StringType.INSTANCE)
                .addScalar("modalidadIngreso", StringType.INSTANCE)
                .addScalar("carrera", StringType.INSTANCE)
                .addScalar("facultad", StringType.INSTANCE)
                .addScalar("correoPersonal", StringType.INSTANCE)
                .addScalar("correoOutlook", StringType.INSTANCE)
                .addScalar("celular", StringType.INSTANCE)
                .addScalar("telefono", StringType.INSTANCE)
                .addScalar("curso", StringType.INSTANCE)
                .addScalar("temaCurso", StringType.INSTANCE)
                .addScalar("puntajeCurso", BigDecimalType.INSTANCE)
                .addScalar("seccion", StringType.INSTANCE)
                .addScalar("moduloAula", StringType.INSTANCE)
                .addScalar("aula", StringType.INSTANCE)
                .addScalar("docente", StringType.INSTANCE)
                .addScalar("correoDocente", StringType.INSTANCE)
                .addScalar("usuario", StringType.INSTANCE)
                .setResultTransformer(Transformers.aliasToBean(ResultadoReporteView.class));

        query.setParameter("CICLO", cicloAcademico.getId());
        return (List<ResultadoReporteView>) query.list();
    }

}
