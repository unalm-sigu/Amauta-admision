package pe.edu.lamolina.amauta.dao.nivelacioneegg.hibernate;

import java.util.List;
import org.hibernate.Query;
import org.hibernate.transform.Transformers;
import org.hibernate.type.BigDecimalType;
import org.hibernate.type.StringType;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableSql;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.amauta.controller.nivelacioneegg.reportes.ExcelData.Bean.ResultadoReporteView;
import pe.edu.lamolina.amauta.dao.nivelacioneegg.AsistenciaNivelacionDAO;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.nivelacioneegg.AsistenciaNivelacion;
import pe.edu.lamolina.model.nivelacioneegg.TemaAsistencia;

@Repository
public class AsistenciaNivelacionDAOH extends AbstractEasyDAO<AsistenciaNivelacion> implements AsistenciaNivelacionDAO {

    public AsistenciaNivelacionDAOH() {
        super();
        setClazz(AsistenciaNivelacion.class);
    }

    @Override
    public AsistenciaNivelacion find(long id) {
        Octavia sql = Octavia.query()
                .from(AsistenciaNivelacion.class, "asn")
                .join("alumnoNivelacion an", "temaAsistencia tas")
                .filter("asn.id", id);

        return find(sql);
    }

    @Override
    public List<AsistenciaNivelacion> allLeccionByDynatable(DynatableFilter filter, TemaAsistencia leccion) {
        DynatableSql sql = new DynatableSql(filter)
                .from(AsistenciaNivelacion.class, "asn")
                .join("alumnoNivelacion an", "temaAsistencia tas", "hora hora")
                .join("an.alumno alu", "alu.carrera car", "car.facultad fac")
                .join("alu.situacionAcademica", "alu.modalidadEstudio", "alu.persona per")
                .join("an.cicloAcademico ci")
                .leftJoin("per.tipoDocumento")
                .filter("tas.id", leccion)
                .filter("hora.id", leccion.getHoraInicio())
                .searchFields("car.nombre", "fac.nombre", "per.numeroDocIdentidad", "alu.codigo")
                .searchComplexField("concat(coalesce(per.paterno,''),' ',coalesce(per.materno,''),' ',coalesce(per.nombres,''))")
                .searchComplexField("concat(coalesce(per.nombres,''),' ',coalesce(per.paterno,''),' ',coalesce(per.materno,''))")
                .orderBy("per.paterno", "per.materno", "per.nombres");

        return all(sql);
    }

    @Override
    public List<AsistenciaNivelacion> allByLeccion(TemaAsistencia leccion) {
        Octavia sql = Octavia.query()
                .from(AsistenciaNivelacion.class, "asn")
                .join("alumnoNivelacion an", "temaAsistencia tas", "hora hora")
                .join("an.alumno alu", "alu.carrera car", "car.facultad fac")
                .join("alu.situacionAcademica", "alu.modalidadEstudio", "alu.persona per")
                .join("an.cicloAcademico ci")
                .leftJoin("per.tipoDocumento")
                .filter("tas.id", leccion);

        return all(sql);
    }

    @Override
    public List<ResultadoReporteView> allByCicloAndSeccion(CicloAcademico cicloAcademico, String codSeccion) {
        StringBuilder sql = new StringBuilder();
        sql.append(" SELECT ");
        sql.append("    caa2.descripcion ciclo, ");
        sql.append("     cu2.codigo codCurso, ");
        sql.append("     cu2.nombre curso, ");
        sql.append("     CASE ");
        sql.append("        WHEN doc2.id_persona IS NULL THEN 'DESCONOCIDO' ");
        sql.append("        ELSE CONCAT(IFNULL(per2.paterno,''), ' ', IFNULL(per2.materno,''), ', ', IFNULL(per2.nombres,''))  ");
        sql.append("    END docente, ");
        sql.append("    CONCAT(IFNULL(p2.paterno,''), ' ', IFNULL(p2.materno,''), ', ', IFNULL(p2.nombres,'')) apellidosNombre,  ");
        sql.append("    a2.codigo matricula, ");
        sql.append("    cn2.codigo seccion, ");
        sql.append("    ROUND(SUM(CASE WHEN asn2.estado = 'ASISTIO' THEN 1 ELSE 0 END) * 100.0 / COUNT(*), 2) porcentajeAsistencia ");
        sql.append(" FROM eegg_nota_alumno_nivelacion nan2 ");
        sql.append(" JOIN eegg_alumno_nivelacion an2 ON an2.id = nan2.id_alumno_nivelacion ");
        sql.append(" JOIN aca_alumno a2 ON a2.id = an2.id_alumno ");
        sql.append(" join gen_persona p2 on a2.id_persona = p2.id ");
        sql.append(" JOIN eegg_asistencia_nivelacion asn2 ON asn2.id_alumno_nivelacion = an2.id ");
        sql.append(" JOIN eegg_tema_asistencia ta2 ON asn2.id_tema_asistencia = ta2.id ");
        sql.append(" JOIN eegg_curso_nivelacion cn2 ON cn2.id = nan2.id_curso_nivelacion AND ta2.id_curso_nivelacion = cn2.id ");
        sql.append(" JOIN aca_docente doc2 ON cn2.id_docente = doc2.id ");
        sql.append(" LEFT JOIN gen_persona per2 ON doc2.id_persona = per2.id ");
        sql.append(" JOIN aca_curso_ciclo_academico cc2 ON cc2.id = cn2.id_curso_ciclo_academico ");
        sql.append(" JOIN aca_ciclo_academico caa2 ON cc2.id_ciclo_academico = caa2.id ");
        sql.append(" join aca_curso cu2 on cc2.id_curso = cu2.id ");
        sql.append(" WHERE caa2.id = :CICLO ");
        sql.append("    AND an2.estado IN ('NMAT','MAT') ");
        sql.append("    AND nan2.estado IN ('NMAT','MAT') ");
        sql.append("    AND nan2.tema_aprobado = false ");
        if (codSeccion != null) {
            sql.append("    AND cn2.codigo = :SECCION ");
        }
        sql.append("    AND cn2.estado = 'ACT' ");
        sql.append(" GROUP BY cu2.codigo,cu2.nombre,cn2.codigo,doc2.id_persona, per2.paterno,per2.materno,per2.nombres,p2.paterno,p2.materno,p2.nombres, caa2.descripcion, a2.codigo;  ");

        Query query = getCurrentSession().createSQLQuery(sql.toString())
                .addScalar("ciclo", StringType.INSTANCE)
                .addScalar("codCurso", StringType.INSTANCE)
                .addScalar("curso", StringType.INSTANCE)
                .addScalar("docente", StringType.INSTANCE)
                .addScalar("apellidosNombre", StringType.INSTANCE)
                .addScalar("matricula", StringType.INSTANCE)
                .addScalar("seccion", StringType.INSTANCE)
                .addScalar("porcentajeAsistencia", BigDecimalType.INSTANCE)
                .setResultTransformer(Transformers.aliasToBean(ResultadoReporteView.class));

        query.setParameter("CICLO", cicloAcademico.getId());
        if (codSeccion != null) {
            query.setParameter("SECCION", codSeccion);
        }
        return (List<ResultadoReporteView>) query.list();
    }

    @Override
    public List<AsistenciaNivelacion> allByCicloSeccion(CicloAcademico cicloAcademico, String codSeccion) {
        Octavia sql = Octavia.query()
                .from(AsistenciaNivelacion.class, "asn")
                .join("alumnoNivelacion an", "temaAsistencia tas", "hora hora")
                .join("an.alumno alu", "an.cicloAcademico ca")
                .join("tas.cursoNivelacion cn")
                .filter("ca.id", cicloAcademico)
                .filter("cn.codigo", codSeccion);

        return all(sql);
    }

}
