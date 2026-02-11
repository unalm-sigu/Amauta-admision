package pe.edu.lamolina.amauta.dao.nivelacioneegg.hibernate;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.Query;
import org.hibernate.transform.Transformers;
import org.hibernate.type.BigDecimalType;
import org.hibernate.type.DateType;
import org.hibernate.type.IntegerType;
import org.hibernate.type.StringType;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Insecto;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableSql;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.amauta.controller.nivelacioneegg.matriculables.dto.MatriculablesResumen;
import pe.edu.lamolina.amauta.controller.nivelacioneegg.reportes.ExcelData.Bean.*;
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
                .leftJoin("per.tipoDocumento", "temaCiclo teci")
                .leftJoin("cursoNivelacion cn", "cn.grupoNivelacion", "cn.aula", "cn.plantilla")
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
                .leftJoin("per.tipoDocumento", "temaCiclo teci", "cn.aula", "cn.plantilla")
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
    public List<NotaAlumnoNivelacion> allByCursoNivelacion2(CursoNivelacion cursoNiv) {
        Octavia sql = Octavia.query()
                .from(NotaAlumnoNivelacion.class, "nan")
                .join("alumnoNivelacion an", "temaExamen te", "curso cur")
                .join("an.alumno alu", "alu.carrera car", "car.facultad fac")
                .join("alu.situacionAcademica", "alu.modalidadEstudio", "alu.persona per")
                .join("alu.postulantePregrado pp", "pp.modalidadIngreso mi", "pp.cicloPostula cp", "cp.cicloAcademico cai")
                .join("an.cicloAcademico ci", "cursoNivelacion cn")
                .leftJoin("per.tipoDocumento", "temaCiclo teci", "cn.aula", "cn.plantilla")
                .filter("cn.id", cursoNiv)
                .orderBy("per.paterno", "per.materno", "per.nombres");

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
                .leftJoin("temaCiclo tc", "tc.temaExamen te", "te.temaSuperior", "curso")
                .leftJoin("an.prelamolina", "an.evaluado")
                .in("an.id", alumnosNiv)
                .orderBy("an.id", "nan.id");

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
        sql.append("  select ROW_NUMBER() OVER (PARTITION BY cu.nombre, cn.codigo ORDER BY cu.nombre, cn.codigo, pe.id) AS correlativo,a.codigo matricula,pe.numero_doc_identidad dni,   ");
        sql.append("  concat(ifnull(pe.paterno,''), ' ',ifnull(pe.materno,''),', ', ifnull(pe.nombres,''))apellidosNombre,   ");
        sql.append("  mi.nombre modalidadIngreso,car.nombre carrera,fa.nombre facultad,pe.email correoPersonal,pe.email_corporativo correoOutlook,pe.email_compania correoGmail,pe.celular,pe.telefono,   ");
        sql.append("  cu.nombre curso,te.nombre temaCurso,nan.puntaje_examen puntajeCurso,cn.codigo seccion,aus.nombre moduloAula, au.codigo aula,   ");
        sql.append("  COALESCE(  ");
        sql.append("    NULLIF(  ");
        sql.append("      CONCAT_WS(', ',  ");
        sql.append("        NULLIF(TRIM(CONCAT_WS(' ', per.paterno, per.materno)), ''),   ");
        sql.append("        NULLIF(TRIM(per.nombres), '')                                ");
        sql.append("      ),   ");
        sql.append("      ''  ");
        sql.append("    ),   ");
        sql.append("    ''  ");
        sql.append("  ) docente,per.email_compania correoDocente,   ");
        sql.append("  case nan.estado  ");
        sql.append("  when 'MAT' then 'Matriculado'  ");
        sql.append("  when 'NMAT' then 'No Matriculado'  ");
        sql.append("  else 'Sin estado'   ");
        sql.append("  end estadoCursoNivelacion,  ");
        sql.append("  case an.estado  ");
        sql.append("  when 'MAT' then 'Matriculado'  ");
        sql.append("  when 'NMAT' then 'No Matriculado'  ");
        sql.append("  when 'INH' then 'Inhabilitado'  ");
        sql.append("  else 'Sin estado'   ");
        sql.append("  end estado,         ");
        sql.append("  case   ");
        sql.append("  when uu.id is null then 'NO TIENE USUARIO'   ");
        sql.append("  else 'SI TIENE USUARIO'   ");
        sql.append("  end usuario  ");
        sql.append("  from eegg_nota_alumno_nivelacion nan   ");
        sql.append("  join eegg_alumno_nivelacion an on an.id = nan.id_alumno_nivelacion   ");
        sql.append("  join aca_alumno a on a.id = an.id_alumno   ");
        sql.append("  join aca_carrera car on a.id_carrera = car.id   ");
        sql.append("  join aca_facultad fa on car.id_facultad = fa.id   ");
        sql.append("  join gen_persona pe on pe.id = a.id_persona   ");
        sql.append("  left join seg_usuario uu on uu.id_persona = pe.id and uu.estado = 'ACT'   ");
        sql.append("  join sip_postulante po on po.id = a.id_postulante_pregrado   ");
        sql.append("  join sip_modalidad_ingreso mi on mi.id = po.id_modalidad_ingreso   ");
        sql.append("  join aca_ciclo_academico ci on ci.id = an.id_ciclo_academico   ");
        sql.append("  join sce_tema_examen te on te.id = nan.id_tema_examen   ");
        sql.append("  left join eegg_curso_nivelacion cn on cn.id = nan.id_curso_nivelacion  ");
        sql.append("  left join gen_aula au on cn.id_aula = au.id   ");
        sql.append("  left join gen_aula aus on au.id_aula_superior = aus.id   ");
        sql.append("  left join aca_docente doc on cn.id_docente = doc.id   ");
        sql.append("  left join gen_persona per on doc.id_persona = per.id   ");
        sql.append("  left join aca_curso_ciclo_academico cc on cc.id = cn.id_curso_ciclo_academico   ");
        sql.append("  left join aca_curso cu on cu.id = cc.id_curso   ");
        sql.append("  join eegg_modalidad_tema_ciclo mtc   ");
        sql.append("     on mtc.id_ciclo_academico = an.id_ciclo_academico   ");
        sql.append("     and mtc.id_tema_examen = nan.id_tema_examen   ");
        sql.append("     and mtc.otras_modalidades = case po.id_modalidad_ingreso when 16 then 0 else 1 end   ");
        sql.append("  where ci.id = :CICLO  ");
        sql.append("  and te.id_tema_superior is null   ");
        sql.append("  and nan.tema_aprobado = false;   ");

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
                .addScalar("estadoCursoNivelacion", StringType.INSTANCE)
                .addScalar("estado", StringType.INSTANCE)
                .addScalar("usuario", StringType.INSTANCE)
                .setResultTransformer(Transformers.aliasToBean(ResultadoReporteView.class));

        query.setParameter("CICLO", cicloAcademico.getId());
        return (List<ResultadoReporteView>) query.list();
    }

    @Override
    public List<ResultadoReporteView> ingresantesDesaprobadosMoodleByCiclo(CicloAcademico cicloAcademico) {
        StringBuilder sql = new StringBuilder();
        sql.append(" select ROW_NUMBER() OVER (PARTITION BY cu.nombre, cn.codigo ORDER BY cu.nombre, cn.codigo, pe.paterno,pe.materno,pe.nombres) AS correlativo,ci.descripcion ciclo, ");
        sql.append(" cu.nombre curso,cn.codigo seccion,concat(ifnull(per.paterno,''), ' ',ifnull(per.materno,''),', ', ifnull(per.nombres,'')) docente,per.email_compania correoDocente, ");
        sql.append(" a.codigo matricula,car.nombre carrera,concat(ifnull(pe.paterno,''), ' ',ifnull(pe.materno,''),', ', ifnull(pe.nombres,'')) apellidosNombre,pe.email_corporativo correoOutlook,pe.email_compania correoGmail ");
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
                .addScalar("ciclo", StringType.INSTANCE)
                .addScalar("curso", StringType.INSTANCE)
                .addScalar("seccion", StringType.INSTANCE)
                .addScalar("docente", StringType.INSTANCE)
                .addScalar("correoDocente", StringType.INSTANCE)
                .addScalar("matricula", StringType.INSTANCE)
                .addScalar("carrera", StringType.INSTANCE)
                .addScalar("apellidosNombre", StringType.INSTANCE)
                .addScalar("correoOutlook", StringType.INSTANCE)
                .addScalar("correoGmail", StringType.INSTANCE)
                .setResultTransformer(Transformers.aliasToBean(ResultadoReporteView.class));

        query.setParameter("CICLO", cicloAcademico.getId());
        return (List<ResultadoReporteView>) query.list();
    }

    @Override
    public List<IngresantesExamenAdmisionDTO> allExamenAdmisionByCiclo(CicloAcademico cicloAcademico) {
        StringBuilder sql = new StringBuilder();
        sql.append(" WITH datos AS ( ");
        sql.append("    SELECT ");
        sql.append("        ifnull(pre.puntaje_rm,e.puntaje_rm) puntaje_rm, ");
        sql.append("        ifnull(pre.puntaje_matematicas,(e.puntaje_algebra + e.puntaje_aritmetica + e.puntaje_geometria + e.puntaje_trignometria)) puntaje_matematicas, ");
        sql.append("        ifnull(pre.puntaje_fisica,e.puntaje_fisica) puntaje_fisica, ");
        sql.append("        ifnull(pre.puntaje_quimica,e.puntaje_quimica) puntaje_quimica, ");
        sql.append("        ifnull(pre.puntaje_biologia,e.puntaje_biologia) puntaje_biologia, ");
        sql.append("        ifnull(pre.puntaje_economia,e.puntaje_economia) puntaje_economia, ");
        sql.append("        ifnull(pre.puntaje_historia,e.puntaje_historia) puntaje_historia, ");
        sql.append("        ifnull(pre.puntaje_geografia,e.puntaje_geografia) puntaje_geografia, ");
        sql.append("        ifnull(pre.puntaje_rv,e.puntaje_rv) puntaje_rv ");
        sql.append("    FROM aca_alumno a ");
        sql.append("    JOIN aca_ciclo_academico cai ON a.id_ciclo_ingreso = cai.id ");
        sql.append("    JOIN sip_postulante po ON a.id_postulante_pregrado = po.id ");
        sql.append("    JOIN sip_ingresante i ON i.id_postulante = po.id ");
        sql.append("    LEFT JOIN sip_prelamolina pre ON i.id_prelamolina = pre.id ");
        sql.append("    LEFT JOIN sip_evaluado e ON i.id_evaluado = e.id ");
        sql.append("    WHERE cai.id = :CICLO AND po.estado = 'ING' ");
        sql.append(" ) ");
        sql.append("  ");
        sql.append(" SELECT ");
        sql.append("    'puntaje_rm' AS materia, ");
        sql.append("    13.125 AS puntaje_minimo, ");
        sql.append("    SUM(CASE WHEN puntaje_rm >= 13.125 THEN 1 ELSE 0 END) AS aprobados, ");
        sql.append("    ROUND(SUM(CASE WHEN puntaje_rm >= 13.125 THEN 1 ELSE 0 END) * 100.0 / COUNT(*), 2) AS porcAprobados, ");
        sql.append("    SUM(CASE WHEN puntaje_rm < 13.125 THEN 1 ELSE 0 END) AS desaprobados, ");
        sql.append("    ROUND(SUM(CASE WHEN puntaje_rm < 13.125 THEN 1 ELSE 0 END) * 100.0 / COUNT(*), 2) AS porcDesaprobados ");
        sql.append(" FROM datos ");
        sql.append("  ");
        sql.append(" UNION ALL ");
        sql.append("  ");
        sql.append(" SELECT ");
        sql.append("    'puntaje_matematicas' AS materia, ");
        sql.append("    9.4 AS puntaje_minimo, ");
        sql.append("    SUM(CASE WHEN puntaje_matematicas >= 9.4 THEN 1 ELSE 0 END) AS aprobados, ");
        sql.append("    ROUND(SUM(CASE WHEN puntaje_matematicas >= 9.4 THEN 1 ELSE 0 END) * 100.0 / COUNT(*), 2) AS porcAprobados, ");
        sql.append("    SUM(CASE WHEN puntaje_matematicas < 9.4 THEN 1 ELSE 0 END) AS desaprobados, ");
        sql.append("    ROUND(SUM(CASE WHEN puntaje_matematicas < 9.4 THEN 1 ELSE 0 END) * 100.0 / COUNT(*), 2) AS porcDesaprobados ");
        sql.append(" FROM datos ");
        sql.append("  ");
        sql.append(" UNION ALL ");
        sql.append("  ");
        sql.append(" SELECT ");
        sql.append("    'puntaje_fisica' AS materia, ");
        sql.append("    3.15 AS puntaje_minimo, ");
        sql.append("    SUM(CASE WHEN puntaje_fisica >= 3.15 THEN 1 ELSE 0 END) AS aprobados, ");
        sql.append("    ROUND(SUM(CASE WHEN puntaje_fisica >= 3.15 THEN 1 ELSE 0 END) * 100.0 / COUNT(*), 2) AS porcAprobados, ");
        sql.append("    SUM(CASE WHEN puntaje_fisica < 3.15 THEN 1 ELSE 0 END) AS desaprobados, ");
        sql.append("    ROUND(SUM(CASE WHEN puntaje_fisica < 3.15 THEN 1 ELSE 0 END) * 100.0 / COUNT(*), 2) AS porcDesaprobados ");
        sql.append(" FROM datos ");
        sql.append("  ");
        sql.append(" UNION ALL ");
        sql.append("  ");
        sql.append(" SELECT ");
        sql.append("    'puntaje_quimica' AS materia, ");
        sql.append("    3.15 AS puntaje_minimo, ");
        sql.append("    SUM(CASE WHEN puntaje_quimica >= 3.15 THEN 1 ELSE 0 END) AS aprobados, ");
        sql.append("    ROUND(SUM(CASE WHEN puntaje_quimica >= 3.15 THEN 1 ELSE 0 END) * 100.0 / COUNT(*), 2) AS porcAprobados, ");
        sql.append("    SUM(CASE WHEN puntaje_quimica < 3.15 THEN 1 ELSE 0 END) AS desaprobados, ");
        sql.append("    ROUND(SUM(CASE WHEN puntaje_quimica < 3.15 THEN 1 ELSE 0 END) * 100.0 / COUNT(*), 2) AS porcDesaprobados ");
        sql.append(" FROM datos ");
        sql.append("  ");
        sql.append(" UNION ALL ");
        sql.append("  ");
        sql.append(" SELECT ");
        sql.append("    'puntaje_biologia' AS materia, ");
        sql.append("    3.15 AS puntaje_minimo, ");
        sql.append("    SUM(CASE WHEN puntaje_biologia >= 3.15 THEN 1 ELSE 0 END) AS aprobados, ");
        sql.append("    ROUND(SUM(CASE WHEN puntaje_biologia >= 3.15 THEN 1 ELSE 0 END) * 100.0 / COUNT(*), 2) AS porcAprobados, ");
        sql.append("    SUM(CASE WHEN puntaje_biologia < 3.15 THEN 1 ELSE 0 END) AS desaprobados, ");
        sql.append("    ROUND(SUM(CASE WHEN puntaje_biologia < 3.15 THEN 1 ELSE 0 END) * 100.0 / COUNT(*), 2) AS porcDesaprobados ");
        sql.append(" FROM datos ");
        sql.append("  ");
        sql.append(" UNION ALL ");
        sql.append("  ");
        sql.append(" SELECT ");
        sql.append("    'puntaje_economia' AS materia, ");
        sql.append("    2.10 AS puntaje_minimo, ");
        sql.append("    SUM(CASE WHEN puntaje_economia >= 2.10 THEN 1 ELSE 0 END) AS aprobados, ");
        sql.append("    ROUND(SUM(CASE WHEN puntaje_economia >= 2.10 THEN 1 ELSE 0 END) * 100.0 / COUNT(*), 2) AS porcAprobados, ");
        sql.append("    SUM(CASE WHEN puntaje_economia < 2.10 THEN 1 ELSE 0 END) AS desaprobados, ");
        sql.append("    ROUND(SUM(CASE WHEN puntaje_economia < 2.10 THEN 1 ELSE 0 END) * 100.0 / COUNT(*), 2) AS porcDesaprobados ");
        sql.append(" FROM datos ");
        sql.append("  ");
        sql.append(" UNION ALL ");
        sql.append("  ");
        sql.append(" SELECT ");
        sql.append("    'puntaje_historia' AS materia, ");
        sql.append("    2.625 AS puntaje_minimo, ");
        sql.append("    SUM(CASE WHEN puntaje_historia >= 2.625 THEN 1 ELSE 0 END) AS aprobados, ");
        sql.append("    ROUND(SUM(CASE WHEN puntaje_historia >= 2.625 THEN 1 ELSE 0 END) * 100.0 / COUNT(*), 2) AS porcAprobados, ");
        sql.append("    SUM(CASE WHEN puntaje_historia < 2.625 THEN 1 ELSE 0 END) AS desaprobados, ");
        sql.append("    ROUND(SUM(CASE WHEN puntaje_historia < 2.625 THEN 1 ELSE 0 END) * 100.0 / COUNT(*), 2) AS porcDesaprobados ");
        sql.append(" FROM datos ");
        sql.append("  ");
        sql.append(" UNION ALL ");
        sql.append("  ");
        sql.append(" SELECT ");
        sql.append("    'puntaje_geografia' AS materia, ");
        sql.append("    2.625 AS puntaje_minimo, ");
        sql.append("    SUM(CASE WHEN puntaje_geografia >= 2.625 THEN 1 ELSE 0 END) AS aprobados, ");
        sql.append("    ROUND(SUM(CASE WHEN puntaje_geografia >= 2.625 THEN 1 ELSE 0 END) * 100.0 / COUNT(*), 2) AS porcAprobados, ");
        sql.append("    SUM(CASE WHEN puntaje_geografia < 2.625 THEN 1 ELSE 0 END) AS desaprobados, ");
        sql.append("    ROUND(SUM(CASE WHEN puntaje_geografia < 2.625 THEN 1 ELSE 0 END) * 100.0 / COUNT(*), 2) AS porcDesaprobados ");
        sql.append(" FROM datos ");
        sql.append("  ");
        sql.append(" UNION ALL ");
        sql.append("  ");
        sql.append(" SELECT ");
        sql.append("    'puntaje_rv' AS materia, ");
        sql.append("    13.125 AS puntaje_minimo, ");
        sql.append("    SUM(CASE WHEN puntaje_rv >= 13.125 THEN 1 ELSE 0 END) AS aprobados, ");
        sql.append("    ROUND(SUM(CASE WHEN puntaje_rv >= 13.125 THEN 1 ELSE 0 END) * 100.0 / COUNT(*), 2) AS porcAprobados, ");
        sql.append("    SUM(CASE WHEN puntaje_rv < 13.125 THEN 1 ELSE 0 END) AS desaprobados, ");
        sql.append("    ROUND(SUM(CASE WHEN puntaje_rv < 13.125 THEN 1 ELSE 0 END) * 100.0 / COUNT(*), 2) AS porcDesaprobados ");
        sql.append(" FROM datos ");
        sql.append("  ");
        sql.append(" ORDER BY materia; ");

        Query query = getCurrentSession().createSQLQuery(sql.toString())
                .addScalar("materia", StringType.INSTANCE)
                .addScalar("aprobados", IntegerType.INSTANCE)
                .addScalar("porcAprobados", BigDecimalType.INSTANCE)
                .addScalar("desaprobados", IntegerType.INSTANCE)
                .addScalar("porcDesaprobados", BigDecimalType.INSTANCE)
                .setResultTransformer(Transformers.aliasToBean(IngresantesExamenAdmisionDTO.class));

        query.setParameter("CICLO", cicloAcademico.getId());
        return (List<IngresantesExamenAdmisionDTO>) query.list();

    }

    @Override
    public List<IngresantesInscritosNivelacionDTO> allInscritosNivelacionByCiclo(CicloAcademico cicloAcademico) {
        StringBuilder sql = new StringBuilder();
        sql.append(" SELECT  ");
        sql.append(" caa.descripcion ciclo,   ");
        sql.append(" car.nombre carrera,  ");
        sql.append(" COUNT(CASE WHEN nan.aprobado = 1 THEN 1 END) AS aprobados,  ");
        sql.append(" COUNT(CASE WHEN nan.aprobado = 0 THEN 1 END) AS desaprobados,  ");
        sql.append(" COUNT(CASE WHEN nan.aprobado is null THEN 1 END) AS sinNota,  ");
        sql.append(" count(nan.id) total  ");
        sql.append(" FROM eegg_nota_alumno_nivelacion nan  ");
        sql.append(" JOIN eegg_alumno_nivelacion an ON an.id = nan.id_alumno_nivelacion  ");
        sql.append(" JOIN aca_alumno a ON a.id = an.id_alumno  ");
        sql.append(" JOIN aca_carrera car ON a.id_carrera = car.id  ");
        sql.append(" JOIN gen_persona pe ON pe.id = a.id_persona  ");
        sql.append(" JOIN eegg_curso_nivelacion cn ON cn.id = nan.id_curso_nivelacion  ");
        sql.append(" LEFT JOIN aca_docente doc ON cn.id_docente = doc.id  ");
        sql.append(" LEFT JOIN gen_persona per ON doc.id_persona = per.id  ");
        sql.append(" JOIN aca_curso_ciclo_academico cc ON cc.id = cn.id_curso_ciclo_academico  ");
        sql.append(" JOIN aca_ciclo_academico caa ON cc.id_ciclo_academico = caa.id  ");
        sql.append(" JOIN aca_curso cu ON cu.id = cc.id_curso  ");
        sql.append(" WHERE caa.id = :CICLO  ");
        sql.append("  AND an.estado IN ('MAT')  ");
        sql.append("  AND nan.estado IN ('MAT')  ");
        sql.append("  AND nan.tema_aprobado = false  ");
        sql.append("  and cn.estado = 'ACT'  ");
        sql.append(" GROUP BY  ");
        sql.append("  caa.descripcion,  ");
        sql.append("  car.nombre  ");
        sql.append("  ORDER BY car.nombre;  ");

        Query query = getCurrentSession().createSQLQuery(sql.toString())
                .addScalar("carrera", StringType.INSTANCE)
                .addScalar("aprobados", IntegerType.INSTANCE)
                .addScalar("desaprobados", IntegerType.INSTANCE)
                .addScalar("sinNota", IntegerType.INSTANCE)
                .addScalar("total", IntegerType.INSTANCE)
                .setResultTransformer(Transformers.aliasToBean(IngresantesInscritosNivelacionDTO.class));

        query.setParameter("CICLO", cicloAcademico.getId());
        return (List<IngresantesInscritosNivelacionDTO>) query.list();

    }

    @Override
    public List<IngresantesMateriasNivelacionDTO> allMateriasNivelacion(CicloAcademico cicloAcademico) {
        StringBuilder sql = new StringBuilder();
        sql.append(" SELECT  ");
        sql.append(" REPLACE(cu.nombre, 'Nivelación en ', '') curso,  ");
        sql.append(" count(nan.id) inscritos,  ");
        sql.append(" COUNT(CASE WHEN nan.aprobado = 1 THEN 1 END) AS aprobados,  ");
        sql.append(" COUNT(CASE WHEN nan.aprobado = 0 THEN 1 END) AS desaprobados,  ");
        sql.append(" COUNT(CASE WHEN nan.aprobado is null THEN 1 END) AS sinNota  ");
        sql.append(" FROM eegg_nota_alumno_nivelacion nan  ");
        sql.append(" JOIN eegg_alumno_nivelacion an ON an.id = nan.id_alumno_nivelacion  ");
        sql.append(" JOIN aca_alumno a ON a.id = an.id_alumno  ");
        sql.append(" JOIN aca_carrera car ON a.id_carrera = car.id  ");
        sql.append(" JOIN gen_persona pe ON pe.id = a.id_persona  ");
        sql.append(" JOIN eegg_curso_nivelacion cn ON cn.id = nan.id_curso_nivelacion  ");
        sql.append(" LEFT JOIN aca_docente doc ON cn.id_docente = doc.id  ");
        sql.append(" LEFT JOIN gen_persona per ON doc.id_persona = per.id  ");
        sql.append(" JOIN aca_curso_ciclo_academico cc ON cc.id = cn.id_curso_ciclo_academico  ");
        sql.append(" JOIN aca_ciclo_academico caa ON cc.id_ciclo_academico = caa.id  ");
        sql.append(" JOIN aca_curso cu ON cu.id = cc.id_curso  ");
        sql.append(" WHERE caa.id = :CICLO  ");
        sql.append("  AND an.estado IN ('MAT')  ");
        sql.append("  AND nan.estado IN ('MAT')  ");
        sql.append("  AND nan.tema_aprobado = false  ");
        sql.append("  and cn.estado = 'ACT'  ");
        sql.append(" GROUP BY  ");
        sql.append("  cu.nombre  ");
        sql.append("  ORDER BY cu.nombre;  ");

        Query query = getCurrentSession().createSQLQuery(sql.toString())
                .addScalar("curso", StringType.INSTANCE)
                .addScalar("inscritos", IntegerType.INSTANCE)
                .addScalar("aprobados", IntegerType.INSTANCE)
                .addScalar("desaprobados", IntegerType.INSTANCE)
                .addScalar("sinNota", IntegerType.INSTANCE)
                .setResultTransformer(Transformers.aliasToBean(IngresantesMateriasNivelacionDTO.class));

        query.setParameter("CICLO", cicloAcademico.getId());
        return (List<IngresantesMateriasNivelacionDTO>) query.list();

    }

    @Override
    public List<IngresantesAsistenciaInscritosDTO> allAsistenciasByCiclo(CicloAcademico cicloAcademico) {
        StringBuilder sql = new StringBuilder();
        sql.append(" select t.ciclo,REPLACE(t.curso, 'Nivelación en ', '') curso,sum(t.total) total, sum(t.mayorIgual50Asistencia) mayorIgual50Asistencia, sum(t.menora50Asistencia) menora50Asistencia, sum(t.zeroAsistencia) zeroAsistencia ");
        sql.append(" from ( ");
        sql.append("           WITH asistencias_alumno AS ( ");
        sql.append("            SELECT ");
        sql.append("                caa.descripcion AS ciclo, ");
        sql.append("                cu.codigo AS cod_curso, ");
        sql.append("                cu.nombre AS curso, ");
        sql.append("                cn.codigo AS seccion, ");
        sql.append("                a.codigo AS matricula, ");
        sql.append("                COUNT(CASE WHEN asn.estado = 'ASISTIO' THEN 1 END) AS clases_asistidas, ");
        sql.append("                COUNT(asn.id) AS clases_totales, ");
        sql.append("                cn.horas_dictado ");
        sql.append("            FROM eegg_alumno_nivelacion an ");
        sql.append("            JOIN aca_alumno a ON a.id = an.id_alumno ");
        sql.append("            JOIN eegg_asistencia_nivelacion asn ON asn.id_alumno_nivelacion = an.id ");
        sql.append("            JOIN eegg_tema_asistencia ta ON asn.id_tema_asistencia = ta.id ");
        sql.append("            JOIN eegg_curso_nivelacion cn ON cn.id = ta.id_curso_nivelacion ");
        sql.append("            JOIN aca_curso_ciclo_academico cc ON cc.id = cn.id_curso_ciclo_academico ");
        sql.append("            JOIN aca_ciclo_academico caa ON cc.id_ciclo_academico = caa.id ");
        sql.append("            JOIN aca_curso cu ON cu.id = cc.id_curso ");
        sql.append("            WHERE caa.id = :CICLO ");
        sql.append("              AND an.estado IN ('MAT') ");
        sql.append("              AND cn.estado = 'ACT' ");
        sql.append("            GROUP BY caa.descripcion, cu.codigo, cu.nombre, cn.codigo, a.codigo, cn.horas_dictado ");
        sql.append("        ), ");
        sql.append("        porcentajes AS ( ");
        sql.append("            SELECT ");
        sql.append("                ciclo, ");
        sql.append("                cod_curso, ");
        sql.append("                curso, ");
        sql.append("                seccion, ");
        sql.append("                matricula,  ");
        sql.append("                horas_dictado, ");
        sql.append("                clases_asistidas, ");
        sql.append("                clases_totales, ");
        sql.append("                CASE WHEN clases_totales > 0 THEN (clases_asistidas * 100.0 / clases_totales) ELSE 0 END AS porcentaje_asistencia ");
        sql.append("            FROM asistencias_alumno ");
        sql.append("        ) ");
        sql.append("        SELECT ");
        sql.append("            ciclo, ");
        sql.append("            cod_curso, ");
        sql.append("            curso, ");
        sql.append("            seccion, ");
        sql.append("            COUNT(matricula) AS total, ");
        sql.append("            COUNT(CASE WHEN porcentaje_asistencia >= 50 THEN 1 END) AS mayorIgual50Asistencia, ");
        sql.append("            COUNT(CASE WHEN porcentaje_asistencia < 50 AND porcentaje_asistencia > 0 THEN 1 END) AS menora50Asistencia, ");
        sql.append("            COUNT(CASE WHEN porcentaje_asistencia = 0 THEN 1 END) AS zeroAsistencia ");
        sql.append("        FROM porcentajes ");
        sql.append("        GROUP BY ciclo, cod_curso, curso, seccion ");
        sql.append("        )t ");
        sql.append(" group by  t.ciclo,t.curso ");
        sql.append(" order by  curso; ");

        Query query = getCurrentSession().createSQLQuery(sql.toString())
                .addScalar("curso", StringType.INSTANCE)
                .addScalar("total", IntegerType.INSTANCE)
                .addScalar("mayorIgual50Asistencia", IntegerType.INSTANCE)
                .addScalar("menora50Asistencia", IntegerType.INSTANCE)
                .addScalar("zeroAsistencia", IntegerType.INSTANCE)
                .setResultTransformer(Transformers.aliasToBean(IngresantesAsistenciaInscritosDTO.class));

        query.setParameter("CICLO", cicloAcademico.getId());
        return (List<IngresantesAsistenciaInscritosDTO>) query.list();

    }

    @Override
    public List<ResultadoReporteView> resultadoAdmisionByCiclo(CicloAcademico cicloAcademico) {
        StringBuilder sql = new StringBuilder();
        sql.append(" select a.codigo matricula,p.numero_doc_identidad dni,concat(ifnull(p.paterno,''),' ',ifnull(p.materno,''),', ',ifnull(p.nombres,'')) apellidosNombre, ");
        sql.append(" mi.nombre modalidadIngreso,car.nombre carrera, fa.nombre facultad,p.email correoPersonal, p.email_corporativo correoOutlook,p.telefono,p.celular, ");
        sql.append(" ifnull(pre.puntaje_algebra,e.puntaje_algebra) puntajeAlgebra, ");
        sql.append(" ifnull(pre.puntaje_aritmetica,e.puntaje_aritmetica) puntajeAritmetica, ");
        sql.append(" ifnull(pre.puntaje_geometria,e.puntaje_geometria) puntajeGeometrica, ");
        sql.append(" ifnull(pre.puntaje_trigonometria,e.puntaje_trignometria) puntajeTrigonometria, ");
        sql.append(" ifnull(pre.puntaje_matematicas,e.puntaje_matematicas) puntajeMatematica, ");
        sql.append(" ifnull(pre.puntaje_quimica,e.puntaje_quimica) puntajeQuimica, ");
        sql.append(" ifnull(pre.puntaje_rm,e.puntaje_rm) puntajeRm, ");
        sql.append(" ifnull(pre.puntaje_rv,e.puntaje_rv) puntajeRv, ");
        sql.append(" ifnull(pre.puntaje_biologia,e.puntaje_biologia) puntajeBiologia, ");
        sql.append(" ifnull(pre.puntaje_economia,e.puntaje_economia) puntajeEconomia, ");
        sql.append(" ifnull(pre.puntaje_fisica,e.puntaje_fisica) puntajeFisica, ");
        sql.append(" ifnull(pre.puntaje_historia,e.puntaje_historia) puntajeHistoria, ");
        sql.append(" ifnull(pre.puntaje_geografia,e.puntaje_geografia) puntajeGeografia, ");
        sql.append(" ifnull(pre.puntaje_final,e.puntaje_final) puntajeFinal, ");
        sql.append(" i.fecha_registro fechaIngreso, ");
        sql.append(" ca.descripcion cicloIngresoAdmision ");
        sql.append(" from aca_alumno a ");
        sql.append(" join aca_ciclo_academico cai on a.id_ciclo_ingreso = cai.id ");
        sql.append(" join sip_postulante po on a.id_postulante_pregrado = po.id ");
        sql.append(" join sip_ingresante i on i.id_postulante = po.id ");
        sql.append(" join sip_modalidad_ingreso mi on po.id_modalidad_ingreso = mi.id ");
        sql.append(" join aca_carrera car on a.id_carrera = car.id ");
        sql.append(" join aca_facultad fa on car.id_facultad = fa.id ");
        sql.append(" join gen_persona p on a.id_persona = p.id ");
        sql.append(" join sip_ciclo_postula cp on po.id_ciclo_postula = cp.id ");
        sql.append(" join aca_ciclo_academico ca on cp.id_ciclo_academico = ca.id ");
        sql.append(" left join sip_prelamolina pre on i.id_prelamolina = pre.id ");
        sql.append(" left join sip_evaluado e on i.id_evaluado = e.id ");
        sql.append(" where cai.id = :CICLO and po.estado = 'ING'; ");

        Query query = getCurrentSession().createSQLQuery(sql.toString())
                .addScalar("matricula", StringType.INSTANCE)
                .addScalar("dni", StringType.INSTANCE)
                .addScalar("apellidosNombre", StringType.INSTANCE)
                .addScalar("modalidadIngreso", StringType.INSTANCE)
                .addScalar("carrera", StringType.INSTANCE)
                .addScalar("facultad", StringType.INSTANCE)
                .addScalar("correoPersonal", StringType.INSTANCE)
                .addScalar("correoOutlook", StringType.INSTANCE)
                .addScalar("telefono", StringType.INSTANCE)
                .addScalar("celular", StringType.INSTANCE)
                .addScalar("puntajeAlgebra", BigDecimalType.INSTANCE)
                .addScalar("puntajeAritmetica", BigDecimalType.INSTANCE)
                .addScalar("puntajeGeometrica", BigDecimalType.INSTANCE)
                .addScalar("puntajeTrigonometria", BigDecimalType.INSTANCE)
                .addScalar("puntajeMatematica", BigDecimalType.INSTANCE)
                .addScalar("puntajeQuimica", BigDecimalType.INSTANCE)
                .addScalar("puntajeRm", BigDecimalType.INSTANCE)
                .addScalar("puntajeRv", BigDecimalType.INSTANCE)
                .addScalar("puntajeBiologia", BigDecimalType.INSTANCE)
                .addScalar("puntajeEconomia", BigDecimalType.INSTANCE)
                .addScalar("puntajeFisica", BigDecimalType.INSTANCE)
                .addScalar("puntajeHistoria", BigDecimalType.INSTANCE)
                .addScalar("puntajeGeografia", BigDecimalType.INSTANCE)
                .addScalar("puntajeFinal", BigDecimalType.INSTANCE)
                .addScalar("fechaIngreso", DateType.INSTANCE)
                .addScalar("cicloIngresoAdmision", StringType.INSTANCE)
                .setResultTransformer(Transformers.aliasToBean(ResultadoReporteView.class));

        query.setParameter("CICLO", cicloAcademico.getId());
        return (List<ResultadoReporteView>) query.list();
    }

    @Override
    public List<ResultadoReporteView> ingresantesGeneraByCiclo(CicloAcademico cicloAcademico) {
        StringBuilder sql = new StringBuilder();
        sql.append(" select    ");
        sql.append(" ROW_NUMBER() OVER (PARTITION BY cu.nombre, te.nombre ORDER BY cu.nombre, te.nombre, pe.id) AS correlativo,   ");
        sql.append(" a.codigo matricula, pe.numero_doc_identidad dni,concat(ifnull(pe.paterno,''), ' ',ifnull(pe.materno,''),', ', ifnull(pe.nombres,'')) apellidosNombre, mi.nombre modalidadIngreso, car.nombre carrera, fa.nombre facultad,   ");
        sql.append(" pe.email correoPersonal, pe.email_corporativo correoOutlook, pe.telefono, pe.celular, cu.nombre curso, te.nombre temaCurso, nan.puntaje_examen puntajeCurso,    ");
        sql.append(" case nan.estado   ");
        sql.append(" when 'MAT' then 'Matriculado'   ");
        sql.append(" when 'NMAT' then 'No Matriculado'   ");
        sql.append(" when 'INH' then 'Inhabilitado'   ");
        sql.append(" else 'VALIDAR ESTADO' end estadoCursoNivelacion,   ");
        sql.append(" case nan.tema_aprobado    ");
        sql.append(" when true then 'Si'    ");
        sql.append(" else 'No' end temaAprobado,   ");
        sql.append(" case an.estado   ");
        sql.append(" when 'MAT' then 'Matriculado'   ");
        sql.append(" when 'NMAT' then 'No Matriculado'   ");
        sql.append(" when 'INH' then 'Inhabilitado'   ");
        sql.append(" else 'VALIDAR ESTADO' end estado,   ");
        sql.append(" case   ");
        sql.append(" when uu.id is null then 'NO TIENE USUARIO'   ");
        sql.append(" else 'SI TIENE USUARIO' end usuario,   ");
        sql.append(" jt.motivo AS motivoReserva  ");
        sql.append(" from eegg_nota_alumno_nivelacion nan   ");
        sql.append(" join eegg_alumno_nivelacion an on an.id = nan.id_alumno_nivelacion   ");
        sql.append(" LEFT JOIN JSON_TABLE(  ");
        sql.append("     an.cambios,  ");
        sql.append("     '$[*]' COLUMNS (  ");
        sql.append("         estado VARCHAR(10) PATH '$.estado',  ");
        sql.append("         motivo VARCHAR(255) PATH '$.motivo'  ");
        sql.append("     )  ");
        sql.append(" ) AS jt ON jt.estado = 'INH'  ");
        sql.append(" join aca_alumno a on a.id = an.id_alumno   ");
        sql.append(" join aca_carrera car on a.id_carrera = car.id   ");
        sql.append(" join aca_facultad fa on car.id_facultad = fa.id   ");
        sql.append(" join gen_persona pe on pe.id = a.id_persona   ");
        sql.append(" left join seg_usuario uu on uu.id_persona = pe.id and uu.estado = 'ACT'   ");
        sql.append(" join sip_postulante po on po.id = a.id_postulante_pregrado   ");
        sql.append(" join sip_modalidad_ingreso mi on mi.id = po.id_modalidad_ingreso   ");
        sql.append(" join aca_ciclo_academico ci on ci.id = an.id_ciclo_academico   ");
        sql.append(" join sce_tema_examen te on te.id = nan.id_tema_examen   ");
        sql.append(" left join eegg_curso_nivelacion cn on cn.id = nan.id_curso_nivelacion   ");
        sql.append(" left join gen_aula au on cn.id_aula = au.id   ");
        sql.append(" left join gen_aula aus on au.id_aula_superior = aus.id   ");
        sql.append(" left join aca_docente doc on cn.id_docente = doc.id   ");
        sql.append(" left join gen_persona per on doc.id_persona = per.id   ");
        sql.append(" left join aca_curso_ciclo_academico cc on cc.id = cn.id_curso_ciclo_academico   ");
        sql.append(" left join aca_curso cu on cu.id = cc.id_curso   ");
        sql.append(" join eegg_modalidad_tema_ciclo mtc   ");
        sql.append("    on mtc.id_ciclo_academico = an.id_ciclo_academico   ");
        sql.append("    and mtc.id_tema_examen = nan.id_tema_examen   ");
        sql.append("    and mtc.otras_modalidades = case po.id_modalidad_ingreso when 16 then 0 else 1 end   ");
        sql.append(" where ci.id = :CICLO   ");
        sql.append(" and te.id_tema_superior is null;   ");
//        sql.append(" and nan.tema_aprobado = false; ");

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
                .addScalar("telefono", StringType.INSTANCE)
                .addScalar("celular", StringType.INSTANCE)
                .addScalar("curso", StringType.INSTANCE)
                .addScalar("temaCurso", StringType.INSTANCE)
                .addScalar("puntajeCurso", BigDecimalType.INSTANCE)
                .addScalar("estadoCursoNivelacion", StringType.INSTANCE)
                .addScalar("temaAprobado", StringType.INSTANCE)
                .addScalar("estado", StringType.INSTANCE)
                .addScalar("motivoReserva", StringType.INSTANCE)
                .addScalar("usuario", StringType.INSTANCE)
                .setResultTransformer(Transformers.aliasToBean(ResultadoReporteView.class));

        query.setParameter("CICLO", cicloAcademico.getId());
        return (List<ResultadoReporteView>) query.list();

    }

    @Override
    public List<ResultadoReporteView> cursoNivelacionFormadoByCiclo(CicloAcademico cicloAcademico) {
        StringBuilder sql = new StringBuilder();
        sql.append("  select   ");
        sql.append("    ca.descripcion ciclo,  ");
        sql.append("    cu.nombre curso,  ");
        sql.append("    cn.codigo seccion,  ");
        sql.append("    COALESCE(  ");
        sql.append("      NULLIF(  ");
        sql.append("        CONCAT_WS(', ',  ");
        sql.append("          NULLIF(TRIM(CONCAT_WS(' ', per.paterno, per.materno)), ''),   ");
        sql.append("          NULLIF(TRIM(per.nombres), '')                                ");
        sql.append("        ),   ");
        sql.append("        ''  ");
        sql.append("      ),   ");
        sql.append("      ''  ");
        sql.append("    ) docente,   ");
        sql.append("    per.email_compania correoDocente,  ");
        sql.append("    doc.codigo codDocente,   ");
        sql.append("    au.codigo aula,   ");
        sql.append("    aus.nombre moduloAula,  ");
        sql.append("    hn.codigo plantilla,  ");
        sql.append("    hc.semana,  ");
        sql.append("    d.nombre dia,  ");
        sql.append("    CONCAT(  ");
        sql.append("      DATE_FORMAT(MIN(STR_TO_DATE(h.descripcion, '%h:%i %p')), '%h:%i %p'),  ");
        sql.append("      ' a ',  ");
        sql.append("      DATE_FORMAT(MAX(STR_TO_DATE(h.descripcion_fin, '%h:%i %p')), '%h:%i %p')  ");
        sql.append("    ) AS horaDictado,  ");
        sql.append("    concat((select count(*) from eegg_tema_asistencia ta where ta.id_curso_nivelacion = cn.id),' / ',  ");
        sql.append("    ((select count(distinct haa.fecha_ini,haa.id_dia) from hor_horario_aula haa where haa.id_curso_nivelacion = cn.id) - 1)) controlAsistencia,  ");
        sql.append("    concat((select count(*) from eegg_examen_curso_nivelacion ecn where ecn.id_curso_nivelacion = cn.id),' / ',  ");
        sql.append("    (select count(*) from eegg_examen_curso_nivelacion ecn where ecn.id_curso_nivelacion = cn.id and ecn.estado = 'CER')) actasEntregadas,  ");
        sql.append("    concat(cn.vacantes, ' /',cn.matriculados) vacMat,   ");
        sql.append("    case cn.estado  ");
        sql.append("    when 'ACT' then 'Activo'  ");
        sql.append("    when 'R_ACT' then 'Reactivado'  ");
        sql.append("    when 'INA' then 'Inactivo'  ");
        sql.append("    when 'CRE' then 'Creado'  ");
        sql.append("    when 'ANU' then 'Anulado'  ");
        sql.append("    when 'BLO' then 'Bloqueado'  ");
        sql.append("    when 'D_BLO' then 'Desbloqueado'  ");
        sql.append("    when 'CAN' then 'Cancelada'  ");
        sql.append("    else 'SIN_ESTADO' end estado    ");
        sql.append("  from eegg_curso_nivelacion cn   ");
        sql.append("  join aca_curso_ciclo_academico cc on cc.id = cn.id_curso_ciclo_academico  ");
        sql.append("  join aca_ciclo_academico ca on cc.id_ciclo_academico = ca.id  ");
        sql.append("  join hor_plantilla_nivelacion hn on cn.id_plantilla = hn.id  ");
        sql.append("  join hor_horario_curso hc on hc.id_curso_ciclo = cc.id and hc.id_plantilla = hn.id  ");
        sql.append("  join gen_dia d on hc.id_dia = d.id  ");
        sql.append("  join hor_hora h on hc.id_hora = h.id  ");
        sql.append("  left join gen_aula au on cn.id_aula = au.id   ");
        sql.append("  left join gen_aula aus on au.id_aula_superior = aus.id   ");
        sql.append("  left join aca_docente doc on cn.id_docente = doc.id   ");
        sql.append("  left join gen_persona per on doc.id_persona = per.id   ");
        sql.append("  left join aca_curso cu on cu.id = cc.id_curso  ");
        sql.append("  where ca.id = :CICLO  ");
        sql.append("  group by ca.descripcion,cu.nombre,cn.codigo,cn.id,docente,  ");
        sql.append("  per.email_compania,  ");
        sql.append("  doc.codigo, au.codigo, aus.nombre,hn.codigo,hc.semana,d.nombre,cn.estado;  ");

        Query query = getCurrentSession().createSQLQuery(sql.toString())
                .addScalar("ciclo", StringType.INSTANCE)
                .addScalar("curso", StringType.INSTANCE)
                .addScalar("seccion", StringType.INSTANCE)
                .addScalar("docente", StringType.INSTANCE)
                .addScalar("correoDocente", StringType.INSTANCE)
                .addScalar("codDocente", StringType.INSTANCE)
                .addScalar("aula", StringType.INSTANCE)
                .addScalar("moduloAula", StringType.INSTANCE)
                .addScalar("plantilla", StringType.INSTANCE)
                .addScalar("semana", DateType.INSTANCE)
                .addScalar("dia", StringType.INSTANCE)
                .addScalar("horaDictado", StringType.INSTANCE)
                .addScalar("controlAsistencia", StringType.INSTANCE)
                .addScalar("actasEntregadas", StringType.INSTANCE)
                .addScalar("vacMat", StringType.INSTANCE)
                .addScalar("estado", StringType.INSTANCE)
                .setResultTransformer(Transformers.aliasToBean(ResultadoReporteView.class));

        query.setParameter("CICLO", cicloAcademico.getId());
        return (List<ResultadoReporteView>) query.list();
    }

    @Override
    public List<IngresantesInscritosNivelacionDTO> allInscritosNivelacionByCicloAndCarrera(CicloAcademico cicloAcademico, Long idCarrera) {
        StringBuilder sql = new StringBuilder();
        sql.append(" SELECT    ");
        sql.append(" REPLACE(cu.nombre, 'Nivelación en ', '') curso,  ");
        sql.append(" COUNT(CASE WHEN nan.aprobado = 1 THEN 1 END) AS aprobados,    ");
        sql.append(" COUNT(CASE WHEN nan.aprobado = 0 THEN 1 END) AS desaprobados,    ");
        sql.append(" COUNT(CASE WHEN nan.aprobado is null THEN 1 END) AS sinNota,    ");
        sql.append(" count(nan.id) total    ");
        sql.append(" FROM eegg_nota_alumno_nivelacion nan    ");
        sql.append(" JOIN eegg_alumno_nivelacion an ON an.id = nan.id_alumno_nivelacion    ");
        sql.append(" JOIN aca_alumno a ON a.id = an.id_alumno    ");
        sql.append(" JOIN aca_carrera car ON a.id_carrera = car.id    ");
        sql.append(" JOIN gen_persona pe ON pe.id = a.id_persona    ");
        sql.append(" JOIN eegg_curso_nivelacion cn ON cn.id = nan.id_curso_nivelacion    ");
        sql.append(" LEFT JOIN aca_docente doc ON cn.id_docente = doc.id    ");
        sql.append(" LEFT JOIN gen_persona per ON doc.id_persona = per.id    ");
        sql.append(" JOIN aca_curso_ciclo_academico cc ON cc.id = cn.id_curso_ciclo_academico    ");
        sql.append(" JOIN aca_ciclo_academico caa ON cc.id_ciclo_academico = caa.id    ");
        sql.append(" JOIN aca_curso cu ON cu.id = cc.id_curso    ");
        sql.append(" WHERE caa.id = :CICLO  ");
        sql.append("  AND an.estado IN ('MAT')    ");
        sql.append("  AND nan.estado IN ('MAT')    ");
        sql.append("  AND nan.tema_aprobado = false    ");
        sql.append("  and cn.estado = 'ACT'    ");
        sql.append("  and car.id = :CARRERA   ");
        sql.append(" GROUP BY  cu.nombre  ");
        sql.append("  ORDER BY cu.nombre;  ");

        Query query = getCurrentSession().createSQLQuery(sql.toString())
                .addScalar("curso", StringType.INSTANCE)
                .addScalar("aprobados", IntegerType.INSTANCE)
                .addScalar("desaprobados", IntegerType.INSTANCE)
                .addScalar("sinNota", IntegerType.INSTANCE)
                .addScalar("total", IntegerType.INSTANCE)
                .setResultTransformer(Transformers.aliasToBean(IngresantesInscritosNivelacionDTO.class));

        query.setParameter("CICLO", cicloAcademico.getId());
        query.setParameter("CARRERA", idCarrera);
        return (List<IngresantesInscritosNivelacionDTO>) query.list();
    }

    @Override
    public List<IngresantesNivelacionCarreraDTO> allIngresantesNivelacionByCicloCarrera(CicloAcademico cicloAcademico, Long idCarrera) {
        StringBuilder sql = new StringBuilder();
        sql.append("   select car.nombre carrera, fac.nombre facultad,   ");
        sql.append("   a2.codigo matricula, CONCAT(IFNULL(p2.paterno,''), ' ', IFNULL(p2.materno,''), ', ', IFNULL(p2.nombres,'')) ingresante,cu2.nombre curso,   ");
        sql.append("   nan2.nota_examen notaInicial,nan2.nota_curso notaFinal,  ");
        sql.append("   case   ");
        sql.append("   when nan2.aprobado then 'Aprobado'  ");
        sql.append("   when !nan2.aprobado then 'Desaprobado'  ");
        sql.append("   else 'NULL' end estadoCurso,  ");
        sql.append("   CASE  ");
        sql.append("         WHEN doc2.id_persona IS NULL THEN 'DESCONOCIDO'  ");
        sql.append("         ELSE CONCAT(IFNULL(per2.paterno,''), ' ', IFNULL(per2.materno,''), ', ', IFNULL(per2.nombres,''))   ");
        sql.append("     END docente,  ");
        sql.append("   IFNULL(  ");
        sql.append("     ROUND(  ");
        sql.append("       100.0 *   ");
        sql.append("       SUM(CASE WHEN asn2.estado = 'ASISTIO' AND ta2.id IS NOT NULL THEN 1 ELSE 0 END)   ");
        sql.append("       / NULLIF(COUNT(ta2.id), 0),  ");
        sql.append("       2  ");
        sql.append("     ),  ");
        sql.append("     0.00  ");
        sql.append("   ) AS porcentajeAsistencia  ");
        sql.append("   FROM eegg_nota_alumno_nivelacion nan2  ");
        sql.append("   JOIN eegg_alumno_nivelacion an2 ON an2.id = nan2.id_alumno_nivelacion  ");
        sql.append("   JOIN aca_alumno a2 ON a2.id = an2.id_alumno  ");
        sql.append("   JOIN aca_carrera car on a2.id_carrera = car.id  ");
        sql.append("   join aca_facultad fac on car.id_facultad = fac.id  ");
        sql.append("   join gen_persona p2 on a2.id_persona = p2.id  ");
        sql.append("   JOIN eegg_curso_nivelacion cn2 ON cn2.id = nan2.id_curso_nivelacion  ");
        sql.append("   LEFT JOIN eegg_asistencia_nivelacion asn2   ");
        sql.append("       ON asn2.id_alumno_nivelacion = an2.id  ");
        sql.append("   LEFT JOIN eegg_tema_asistencia ta2   ");
        sql.append("       ON asn2.id_tema_asistencia = ta2.id   ");
        sql.append("       AND ta2.id_curso_nivelacion = cn2.id  ");
        sql.append("   JOIN aca_docente doc2 ON cn2.id_docente = doc2.id  ");
        sql.append("   LEFT JOIN gen_persona per2 ON doc2.id_persona = per2.id  ");
        sql.append("   JOIN aca_curso_ciclo_academico cc2 ON cc2.id = cn2.id_curso_ciclo_academico  ");
        sql.append("   JOIN aca_ciclo_academico caa2 ON cc2.id_ciclo_academico = caa2.id  ");
        sql.append("   join aca_curso cu2 on cc2.id_curso = cu2.id  ");
        sql.append("   WHERE caa2.id = :CICLO  ");
        sql.append("   AND an2.estado IN ('NMAT','MAT')  ");
        sql.append("   AND nan2.estado IN ('NMAT','MAT')  ");
        sql.append("   AND nan2.tema_aprobado = false  ");
        sql.append("   AND cn2.estado = 'ACT'  ");
        sql.append("   AND car.id = :CARRERA  ");
        sql.append("   GROUP BY car.nombre, fac.nombre,a2.codigo,p2.paterno,p2.materno,p2.nombres,cu2.nombre,nan2.nota_examen,nan2.nota_curso,  ");
        sql.append("   nan2.aprobado,doc2.id_persona, per2.paterno,per2.materno,per2.nombres  ");
        sql.append("   order by 4;  ");

        Query query = getCurrentSession().createSQLQuery(sql.toString())
                .addScalar("carrera", StringType.INSTANCE)
                .addScalar("facultad", StringType.INSTANCE)
                .addScalar("matricula", StringType.INSTANCE)
                .addScalar("ingresante", StringType.INSTANCE)
                .addScalar("curso", StringType.INSTANCE)
                .addScalar("notaInicial", StringType.INSTANCE)
                .addScalar("notaFinal", StringType.INSTANCE)
                .addScalar("estadoCurso", StringType.INSTANCE)
                .addScalar("docente", StringType.INSTANCE)
                .addScalar("porcentajeAsistencia", StringType.INSTANCE)
                .setResultTransformer(Transformers.aliasToBean(IngresantesNivelacionCarreraDTO.class));

        query.setParameter("CICLO", cicloAcademico.getId());
        query.setParameter("CARRERA", idCarrera);
        return (List<IngresantesNivelacionCarreraDTO>) query.list();
    }

    @Override
    public List<IngresantesAsistenciaInscritosDTO> allAsistenciasByCicloCarrera(CicloAcademico cicloAcademico, Long idCarrera) {
        StringBuilder sql = new StringBuilder();
        sql.append("  select t.ciclo,REPLACE(t.curso, 'Nivelación en ', '') curso,  ");
        sql.append("  sum(t.total) total, sum(t.mayorIgual50Asistencia) mayorIgual50Asistencia,   ");
        sql.append("  sum(t.menora50Asistencia) menora50Asistencia, sum(t.zeroAsistencia) zeroAsistencia   ");
        sql.append("  from (   ");
        sql.append("            WITH asistencias_alumno AS (   ");
        sql.append("            SELECT   ");
        sql.append("              caa.descripcion AS ciclo,   ");
        sql.append("              cu.codigo AS cod_curso,   ");
        sql.append("              cu.nombre AS curso,   ");
        sql.append("              cn.codigo AS seccion,   ");
        sql.append("              a.codigo AS matricula,   ");
        sql.append("              COALESCE(asistencias.clases_asistidas, 0) AS clases_asistidas,   ");
        sql.append("              COALESCE(asistencias.clases_totales, 0) AS clases_totales,   ");
        sql.append("              cn.horas_dictado   ");
        sql.append("            FROM eegg_nota_alumno_nivelacion nan   ");
        sql.append("            JOIN eegg_alumno_nivelacion an ON nan.id_alumno_nivelacion = an.id   ");
        sql.append("            JOIN aca_alumno a ON a.id = an.id_alumno   ");
        sql.append("            JOIN aca_carrera car ON a.id_carrera = car.id    ");
        sql.append("            JOIN eegg_curso_nivelacion cn ON cn.id = nan.id_curso_nivelacion    ");
        sql.append("            JOIN aca_curso cu ON cu.id = nan.id_curso   ");
        sql.append("            JOIN aca_ciclo_academico caa ON an.id_ciclo_academico = caa.id   ");
        sql.append("            LEFT JOIN (   ");
        sql.append("              SELECT   ");
        sql.append("                asn.id_alumno_nivelacion,   ");
        sql.append("                ta.id_curso_nivelacion,   ");
        sql.append("                COUNT(DISTINCT CASE WHEN asn.estado = 'ASISTIO' THEN asn.id END) AS clases_asistidas,   ");
        sql.append("                COUNT(DISTINCT asn.id) AS clases_totales   ");
        sql.append("              FROM eegg_asistencia_nivelacion asn   ");
        sql.append("              JOIN eegg_tema_asistencia ta ON asn.id_tema_asistencia = ta.id   ");
        sql.append("              GROUP BY asn.id_alumno_nivelacion, ta.id_curso_nivelacion   ");
        sql.append("            ) asistencias ON asistencias.id_alumno_nivelacion = an.id AND asistencias.id_curso_nivelacion = cn.id   ");
        sql.append("            WHERE caa.id = :CICLO   ");
        sql.append("              AND car.id = :CARRERA   ");
        sql.append("              AND an.estado IN ('MAT')   ");
        sql.append("              AND nan.estado IN ('MAT')   ");
        sql.append("              AND cn.estado = 'ACT'   ");
        sql.append("            GROUP BY cu.codigo, cu.nombre, cn.codigo, a.codigo, cn.horas_dictado, asistencias.clases_asistidas, asistencias.clases_totales   ");
        sql.append("         ),   ");
        sql.append("         porcentajes AS (   ");
        sql.append("             SELECT   ");
        sql.append("                 ciclo,   ");
        sql.append("                 cod_curso,   ");
        sql.append("                 curso,   ");
        sql.append("                 seccion,   ");
        sql.append("                 matricula,    ");
        sql.append("                 horas_dictado,   ");
        sql.append("                 clases_asistidas,   ");
        sql.append("                 clases_totales,   ");
        sql.append("                 CASE WHEN clases_totales > 0 THEN (clases_asistidas * 100.0 / clases_totales) ELSE 0 END AS porcentaje_asistencia   ");
        sql.append("             FROM asistencias_alumno   ");
        sql.append("         )   ");
        sql.append("         SELECT   ");
        sql.append("             ciclo,   ");
        sql.append("             cod_curso,   ");
        sql.append("             curso,   ");
        sql.append("             seccion,   ");
        sql.append("             COUNT(matricula) AS total,   ");
        sql.append("             COUNT(CASE WHEN porcentaje_asistencia >= 50 THEN 1 END) AS mayorIgual50Asistencia,   ");
        sql.append("             COUNT(CASE WHEN porcentaje_asistencia < 50 AND porcentaje_asistencia > 0 THEN 1 END) AS menora50Asistencia,   ");
        sql.append("             COUNT(CASE WHEN porcentaje_asistencia = 0 THEN 1 END) AS zeroAsistencia   ");
        sql.append("         FROM porcentajes   ");
        sql.append("         GROUP BY ciclo, cod_curso, curso, seccion   ");
        sql.append("         )t   ");
        sql.append("  group by  t.ciclo,t.curso   ");
        sql.append("  order by  curso;   ");

        Query query = getCurrentSession().createSQLQuery(sql.toString())
                .addScalar("curso", StringType.INSTANCE)
                .addScalar("total", IntegerType.INSTANCE)
                .addScalar("mayorIgual50Asistencia", IntegerType.INSTANCE)
                .addScalar("menora50Asistencia", IntegerType.INSTANCE)
                .addScalar("zeroAsistencia", IntegerType.INSTANCE)
                .setResultTransformer(Transformers.aliasToBean(IngresantesAsistenciaInscritosDTO.class));

        query.setParameter("CICLO", cicloAcademico.getId());
        query.setParameter("CARRERA", idCarrera);
        return (List<IngresantesAsistenciaInscritosDTO>) query.list();
    }
}
