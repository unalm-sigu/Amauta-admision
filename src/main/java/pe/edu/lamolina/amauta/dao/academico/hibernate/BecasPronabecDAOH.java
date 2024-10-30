package pe.edu.lamolina.amauta.dao.academico.hibernate;

import org.hibernate.Hibernate;
import org.hibernate.Query;
import org.hibernate.transform.Transformers;
import org.hibernate.type.LongType;
import org.hibernate.type.StringType;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableSql;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.amauta.controller.academico.pronabec.BecadosFilterBean;
import pe.edu.lamolina.amauta.controller.academico.pronabec.MatriculadosBecadosBean;
import pe.edu.lamolina.amauta.dao.academico.BecasPronabecDAO;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.ModalidadEstudio;
import pe.edu.lamolina.model.enums.persona.PersonaEstadoEnum;
import pe.edu.lamolina.model.general.Persona;
import pe.edu.lamolina.model.pronabec.InformacionBeca;
import pe.edu.lamolina.model.pronabec.TipoBeca;

import java.util.Arrays;
import java.util.List;

import static pe.edu.lamolina.model.enums.ModalidadEstudioEnum.*;
import static pe.edu.lamolina.model.enums.ModalidadEstudioEnum.ESP;

@Repository
public class BecasPronabecDAOH extends AbstractEasyDAO<InformacionBeca> implements BecasPronabecDAO {

    public BecasPronabecDAOH(){
        super();
        setClazz(InformacionBeca.class);
    }

    @Override
    public List<InformacionBeca> allByFilter(DynatableFilter filter){
        DynatableSql sql = new DynatableSql(filter)
                .from(InformacionBeca.class, "be")
                .join("tipoBeca tb", "persona per")
                .searchFields("tb.nombre")
                .searchFields("per.numeroDocIdentidad", "per.telefono", "per.celular")
                .searchComplexField("concat(coalesce(per.paterno,''),' ',coalesce(per.materno,''),' ',coalesce(per.nombres,''))")
                .searchComplexField("concat(coalesce(per.nombres,''),' ',coalesce(per.paterno,''),' ',coalesce(per.materno,''))")
                .orderBy("be.id desc");
        return all(sql);
    }

    @Override
    public List<Persona> allByName(String nombre) {
        nombre = "%" + nombre.replaceAll(" ", "%") + "%";
        Octavia sql = Octavia.query()
                .selectDistinct("per")
                .from(Alumno.class, "alu")
                .join("persona per", "carrera car", "car.facultad fa", "modalidadEstudio me")
                .leftJoin("per.tipoDocumento td")
                .filter("per.estado", PersonaEstadoEnum.ACT)
                .in("me.codigo", Arrays.asList(EPG, PRE, VIS, ESP))
                .beginBlock()
                .__().complexFilter("concat(coalesce(per.paterno,''),' ',coalesce(per.materno,''),' ',coalesce(per.nombres,''))", "like", nombre)
                .__().complexFilter("concat(coalesce(per.nombres,''),' ',coalesce(per.paterno,''),' ',coalesce(per.materno,''))", "like", nombre)
                .__().filter("per.numeroDocIdentidad", "like", nombre)
                .__().filter("alu.codigo", "like", nombre)
                .endBlock()
                .limit(15);
        return sql.all(getCurrentSession());
    }

    @Override
    public List<MatriculadosBecadosBean> allMatriculadosBecadosPregrado(CicloAcademico cicloAcademico) {
        StringBuilder sql = new StringBuilder();
        sql.append(" select distinct f.numero_doc_identidad dni, e.codigo as codigo_estudiante, concat(f.paterno,'  ',f.materno,'   ',f.nombres) as apellidos_nombres, ");
        sql.append(" ptb.nombre tipo_beca, pi2.year_convocatoria year_convocatoria, ");
        sql.append(" concat('UNIVERSIDAD AGRARIA LA MOLINA') nombre_institucion, ");
        sql.append(" g.nombre carrera, d.codigo_anterior periodo_academico, ");
        sql.append(" case when z3.codigo_anterior=d.codigo_anterior then z3.cantidad end ciclo, ");
        sql.append(" CONCAT_WS('   ', b.codigo,b.nombre) curso_matriculado, a.nota nota ,a.veces_cursado_regular veces_desaprobado, ");
        sql.append(" truncate(c.promedio_ciclo ,2) promedio_ponderado, ");
        sql.append(" case when c.promedio_ciclo  >= '11' then 'Aprobado' when c.promedio_ciclo <'11' then 'Desaprobado' end condicion ");
        sql.append(" from aca_alumno_ciclo_curso a ");
        sql.append(" left join aca_curso b on b.id =a.id_curso and b.creditos =a.creditos ");
        sql.append(" join aca_alumno_ciclo c on c.id =a.id_alumno_ciclo ");
        sql.append(" join aca_ciclo_academico d on d.id =c.id_ciclo_academico ");
        sql.append(" join aca_alumno e on e.id =c.id_alumno  and e.id_modalidad_estudio ='1' ");
        sql.append(" join gen_persona f on f.id =e.id_persona  ");
        sql.append(" join aca_carrera g on g.id =e.id_carrera ");
        sql.append(" join aca_facultad h on h.id =g.id_facultad ");
        sql.append(" join pronabec_informacion pi2 on pi2.id_persona =f.id ");
        sql.append(" join pronabec_tipo_beca ptb on ptb.id =pi2.id_tipo_beca ");
        sql.append(" left join (select distinct z.id_alumno,z1.codigo_anterior,ROW_NUMBER() over(PARTITION by z.id_alumno order by z1.codigo_anterior)as cantidad ");
        sql.append("        from aca_alumno_ciclo z ");
        sql.append("        join aca_ciclo_academico z1 on z1.id =z.id_ciclo_academico ");
        sql.append("        where z.estado ='MAT' ");
        sql.append("        and z1.tipo ='REG') z3 on z3.id_alumno=e.id and z3.codigo_anterior=d.codigo_anterior ");
        sql.append(" left join (select distinct y1.id_alumno,y.id_curso,ac.codigo,ac.nombre,(y.veces_cursado_regular + y5.item1) as item2,y5.ciclo ");
        sql.append("            from aca_alumno_ciclo_curso y ");
        sql.append("            join aca_alumno_ciclo y1 on y1.id =y.id_alumno_ciclo ");
        sql.append("            join aca_ciclo_academico y3 on y3.id =y1.id_ciclo_academico  and y3.tipo ='REG' ");
        sql.append("            join aca_alumno y4 on y4.id =y1.id_alumno and y4.id_modalidad_estudio ='1' ");
        sql.append("            join aca_curso ac on ac.id =y.id_curso ");
        sql.append("            inner join (select a1.id_alumno ,amc.id_curso,aca.codigo_anterior as ciclo,count(*) item1 ");
        sql.append("                    from aca_matricula_resumen a1 ");
        sql.append("                    join aca_ciclo_academico aca on aca.id =a1.id_ciclo_academico and aca.tipo ='REG' and aca.codigo_anterior ='20242' ");
        sql.append("                    join aca_matricula_curso amc on amc.id_matricula_resumen =a1.id WHERE a1.estado ='MAT' and amc.estado ='MAT' ");
        sql.append("                    group by a1.id_alumno ,amc.id_curso ) y5 on y5.id_curso =y.id_curso and y5.id_alumno =y4.id  where y.veces_cursado_regular ='2')h on h.id_alumno =e.id and h.id_curso=a.id_curso ");
        sql.append(" where d.codigo_anterior = :CICLO ");
        sql.append(" and a.estado <>'NELI' ");
        sql.append(" order by 3,1,8; ");

        Query query = getCurrentSession().createSQLQuery(sql.toString())
                .addScalar("dni", StringType.INSTANCE)
                .addScalar("codigo_estudiante", StringType.INSTANCE)
                .addScalar("apellidos_nombres", StringType.INSTANCE)
                .addScalar("tipo_beca", StringType.INSTANCE )
                .addScalar("year_convocatoria", StringType.INSTANCE)
                .addScalar("nombre_institucion", StringType.INSTANCE)
                .addScalar("carrera", StringType.INSTANCE)
                .addScalar("periodo_academico", StringType.INSTANCE)
                .addScalar("ciclo", StringType.INSTANCE)
                .addScalar("curso_matriculado", StringType.INSTANCE)
                .addScalar("nota", StringType.INSTANCE)
                .addScalar("veces_desaprobado", LongType.INSTANCE)
                .addScalar("promedio_ponderado", LongType.INSTANCE)
                .addScalar("condicion", StringType.INSTANCE)
                .setResultTransformer(Transformers.aliasToBean(MatriculadosBecadosBean.class));
        query.setParameter("CICLO", cicloAcademico.getCodigoAnterior());


        return (List<MatriculadosBecadosBean>) query.list();
    }

    @Override
    public List<BecadosFilterBean> allBecadosFilterExcel(CicloAcademico cicloAcademico, ModalidadEstudio modalidadEstudio,BecadosFilterBean becadosFilterBean) {
        StringBuilder sql = new StringBuilder();
        sql.append(" select distinct f.numero_doc_identidad dni, e.codigo as codigo_estudiante, concat(f.paterno,'  ',f.materno,'   ',f.nombres) as apellidos_nombres, ");
        sql.append(" pi2.year_convocatoria year_convocatoria, ");
        sql.append(" ptb.*, d.*, ");
        sql.append(" concat('UNIVERSIDAD AGRARIA LA MOLINA') nombre_institucion, ");
        sql.append(" g.nombre carrera, d.codigo_anterior periodo_academico, ");
        sql.append(" case when z3.codigo_anterior=d.codigo_anterior then z3.cantidad end ciclo_academico, ");
//        sql.append(" z3.* ciclo_academico, ");
        sql.append(" CONCAT_WS('   ', b.codigo,b.nombre) curso_matriculado, ");
        sql.append(" a.nota nota ,a.veces_cursado_regular veces_desaprobado, ");
        sql.append(" truncate(c.promedio_ciclo ,2) promedio_ponderado, ");
        sql.append(" case when c.promedio_ciclo  >= '11' then 'Aprobado' when c.promedio_ciclo <'11' then 'Desaprobado' end condicion, ");
        sql.append(" g.nombre cambio_carrera ");
        sql.append(" from aca_alumno_ciclo_curso a ");
        sql.append(" left join aca_curso b on b.id =a.id_curso and b.creditos =a.creditos ");
        sql.append(" join aca_alumno_ciclo c on c.id =a.id_alumno_ciclo ");
        sql.append(" join aca_ciclo_academico d on d.id =c.id_ciclo_academico ");
        sql.append(" join aca_alumno e on e.id =c.id_alumno  and e.id_modalidad_estudio ='1' ");
        sql.append(" join gen_persona f on f.id =e.id_persona  ");
        sql.append(" join aca_carrera g on g.id =e.id_carrera ");
        sql.append(" join aca_facultad h on h.id =g.id_facultad ");
        sql.append(" join pronabec_informacion pi2 on pi2.id_persona =f.id ");
        sql.append(" join pronabec_tipo_beca ptb on ptb.id =pi2.id_tipo_beca ");
        sql.append(" left join (select distinct z.id_alumno,z1.codigo_anterior,ROW_NUMBER() over(PARTITION by z.id_alumno order by z1.codigo_anterior)as cantidad ");
        sql.append("        from aca_alumno_ciclo z ");
        sql.append("        join aca_ciclo_academico z1 on z1.id =z.id_ciclo_academico ");
        sql.append("        where z.estado ='MAT' ");
        sql.append("        and z1.tipo ='REG') z3 on z3.id_alumno=e.id and z3.codigo_anterior=d.codigo_anterior ");
        sql.append(" left join (select distinct y1.id_alumno,y.id_curso,ac.codigo,ac.nombre,(y.veces_cursado_regular + y5.item1) as item2,y5.ciclo ");
        sql.append("            from aca_alumno_ciclo_curso y ");
        sql.append("            join aca_alumno_ciclo y1 on y1.id =y.id_alumno_ciclo ");
        sql.append("            join aca_ciclo_academico y3 on y3.id =y1.id_ciclo_academico  and y3.tipo ='REG' ");
        sql.append("            join aca_alumno y4 on y4.id =y1.id_alumno and y4.id_modalidad_estudio = :MODALIDAD ");
        sql.append("            join aca_curso ac on ac.id =y.id_curso ");
        sql.append("            inner join (select a1.id_alumno ,amc.id_curso,aca.codigo_anterior as ciclo,count(*) item1 ");
        sql.append("                    from aca_matricula_resumen a1 ");
        sql.append("                    join aca_ciclo_academico aca on aca.id =a1.id_ciclo_academico and aca.tipo ='REG' and aca.id = :CICLO_ACTUAL ");
        sql.append("                    join aca_matricula_curso amc on amc.id_matricula_resumen =a1.id WHERE a1.estado ='MAT' and amc.estado ='MAT' ");
        sql.append("                    group by a1.id_alumno ,amc.id_curso ) y5 on y5.id_curso =y.id_curso and y5.id_alumno =y4.id  where y.veces_cursado_regular ='2')h on h.id_alumno =e.id and h.id_curso=a.id_curso ");
        sql.append(" where a.estado <>'NELI' ");
        if(becadosFilterBean.getTipo_beca() != null){
            sql.append(" and ptb.id = :TIPOBECA");
        }
        if(becadosFilterBean.getCiclo_academico() != null){
            sql.append(" and d.id = :CICLOACADEMICO ");
        }
//        else {
//            sql.append(" and d.id = :CICLO_ACTUAL ");
//        }
        sql.append(" order by 3,1,8; ");

        Query query = getCurrentSession().createSQLQuery(sql.toString())
                .addScalar("dni", StringType.INSTANCE)
                .addScalar("codigo_estudiante", StringType.INSTANCE)
                .addScalar("apellidos_nombres", StringType.INSTANCE)
                .addEntity("tipo_beca", TipoBeca.class)
                .addScalar("year_convocatoria", StringType.INSTANCE)
                .addScalar("nombre_institucion", StringType.INSTANCE)
                .addScalar("carrera", StringType.INSTANCE)
                .addScalar("periodo_academico", StringType.INSTANCE)
                .addEntity("ciclo_academico", CicloAcademico.class)
                .addScalar("curso_matriculado", StringType.INSTANCE)
                .addScalar("nota", StringType.INSTANCE)
                .addScalar("veces_desaprobado", LongType.INSTANCE)
                .addScalar("promedio_ponderado", LongType.INSTANCE)
                .addScalar("condicion", StringType.INSTANCE)
                .addScalar("cambio_carrera", StringType.INSTANCE)
                .setResultTransformer(Transformers.aliasToBean(BecadosFilterBean.class));
        //query.setParameter("CICLO", cicloAcademico.getCodigoAnterior());
        query.setParameter("CICLO_ACTUAL",cicloAcademico.getId());
        query.setParameter("MODALIDAD", modalidadEstudio.getId());
        //query.setParameter("TIPOBECA",9);
        if(becadosFilterBean.getTipo_beca() != null){
            query.setParameter("TIPOBECA", becadosFilterBean.getTipo_beca().getId());
        }
        //query.setParameter("CICADEMICO", becadosFilterBean.getCiclo());
        if(becadosFilterBean.getCiclo_academico() != null){
            query.setParameter("CICLOACADEMICO", becadosFilterBean.getCiclo_academico().getId());
        }
//        else {
//            query.setParameter("CICLO_ACTUAL", cicloAcademico.getId());
//        }



            //query.setParameter("CICLOACADEMICO", "20241");

        //String cica = becadosFilterBean.getCiclo();

        return (List<BecadosFilterBean>) query.list();

    }

    @Override
    public List<BecadosFilterBean> filterActualBecados(CicloAcademico cicloAcademico, ModalidadEstudio modalidadEstudio, BecadosFilterBean becadosFilterBean) {
        StringBuilder sql = new StringBuilder();
        sql.append(" select p.numero_doc_identidad dni, concat(ifnull(p.paterno,''),' ',ifnull(p.materno,''),', ',ifnull(p.nombres,'')) apellidos_nombres, ");
        sql.append("        pi.year_convocatoria, ");
        sql.append(" tb.*, ");
        sql.append("         'Universidad Agraria La Molina' nombre_institucion,car.nombre carrera, ca.descripcion periodo_academico, ");
        sql.append("         (x.ciclos + 1) ciclos, concat_ws(' ', cu.codigo,cu.nombre) curso_matriculado, cu.creditos, ");
        sql.append(" if(z.veces_cursado_regular > 0,z.veces_cursado_regular,'Matriculado en el ciclo actual') veces_cursado, ");
        sql.append(" if(w.id_alumno is not null,'SI','NO') retiro_ciclo ");
        sql.append(" from aca_matricula_resumen mr ");
        sql.append(" join aca_ciclo_academico ca on mr.id_ciclo_academico = ca.id ");
        sql.append(" join aca_alumno a on mr.id_alumno = a.id ");
        sql.append(" join gen_persona p on a.id_persona = p.id ");
        sql.append(" join aca_carrera car on a.id_carrera = car.id ");
        sql.append(" join aca_matricula_curso mc on mc.id_matricula_resumen = mr.id ");
        sql.append(" join aca_curso cu on mc.id_curso = cu.id ");
        sql.append(" join pronabec_informacion pi on pi.id_persona = p.id and pi.year_convocatoria = ca.year ");
        sql.append(" join pronabec_tipo_beca tb on pi.id_tipo_beca = tb.id ");
        sql.append(" left join ( ");
        sql.append("            select a2.id id_alumno,count(ac2.id) ciclos ");
        sql.append("            from aca_alumno_ciclo ac2 ");
        sql.append("            join aca_ciclo_academico ca2 on ac2.id_ciclo_academico = ca2.id ");
        sql.append("            join aca_alumno a2 on ac2.id_alumno = a2.id ");
        sql.append("            where ac2.estado ='MAT' ");
        sql.append("            and ca2.tipo ='REG' and ca2.codigo <= :CICLO_ACTUAL ");
        sql.append("            group by a2.id ");
        sql.append("            ) x on a.id = x.id_alumno ");
        sql.append(" left join ( ");
        sql.append("            select ac3.id_alumno, acc3.id_curso id_curso,max(acc3.veces_cursado_regular) veces_cursado_regular ");
        sql.append("            from aca_alumno_ciclo_curso acc3 ");
        sql.append("            join aca_alumno_ciclo ac3 on acc3.id_alumno_ciclo = ac3.id ");
        sql.append("            where acc3.registro_activo = 1 ");
        sql.append("            group by ac3.id_alumno, acc3.id_curso ");
        sql.append("            ) z on a.id = z.id_alumno and cu.id = z.id_curso ");
        sql.append(" left join ( ");
        sql.append("            select ca4.id id_ciclo, a4.id id_alumno ");
        sql.append("            from tram_retiro_ciclo rc4 ");
        sql.append("            join aca_ciclo_academico ca4 on rc4.id_ciclo_academico = ca4.id ");
        sql.append("            join aca_alumno a4 on rc4.id_alumno = a4.id ");
        sql.append("            where rc4.estado = 'ACEP' ");
        sql.append("            ) w on w.id_ciclo = ca.id and w.id_alumno = a.id ");
        sql.append(" where ca.codigo = :CICLO_ACTUAL and ");
        sql.append(" mr.estado = 'MAT' and mc.estado = 'MAT' ");
        if(becadosFilterBean.getTipo_beca() != null){
            sql.append(" and tb.id = :TIPOBECA");
        }

        Query query = getCurrentSession().createSQLQuery(sql.toString())
                .addScalar("dni", StringType.INSTANCE)
                .addScalar("apellidos_nombres", StringType.INSTANCE)
                .addScalar("year_convocatoria", StringType.INSTANCE)
                .addEntity("tipo_beca", TipoBeca.class)
                .addScalar("nombre_institucion", StringType.INSTANCE)
                .addScalar("carrera", StringType.INSTANCE)
                .addScalar("periodo_academico", StringType.INSTANCE)
                .addScalar("ciclos", StringType.INSTANCE)
                .addScalar("curso_matriculado", StringType.INSTANCE)
                .addScalar("creditos", StringType.INSTANCE)
                .addScalar("veces_cursado", StringType.INSTANCE)
                .addScalar("retiro_ciclo", StringType.INSTANCE)
                .setResultTransformer(Transformers.aliasToBean(BecadosFilterBean.class));
        query.setParameter("CICLO_ACTUAL",cicloAcademico.getCodigo());
        if(becadosFilterBean.getTipo_beca() != null){
            query.setParameter("TIPOBECA", becadosFilterBean.getTipo_beca().getId());
        }

        return (List<BecadosFilterBean>) query.list();
    }

    @Override
    public List<BecadosFilterBean> filterAnteriorBecados(CicloAcademico cicloAcademico, ModalidadEstudio modalidadEstudio, BecadosFilterBean becadosFilterBean) {
        StringBuilder sql = new StringBuilder();
        sql.append(" select p.numero_doc_identidad dni, concat(ifnull(p.paterno,''),' ',ifnull(p.materno,''),', ',ifnull(p.nombres,'')) apellidos_nombres, pi.year_convocatoria, ");
        sql.append(" tb.*, ca.*, ");
        sql.append("         'Universidad Agraria La Molina' nombre_institucion,car.nombre carrera, ");
        sql.append("         x.ciclos, concat_ws(' ', cu.codigo,cu.nombre) curso_matriculado,acc.nota,acc.creditos,acc.veces_cursado_regular veces_cursado, ");
        sql.append("         case tcc.codigo ");
        sql.append("         when 'ELC' then acc.creditos ");
        sql.append("         else 0 end electivo_matriculado, ");
        sql.append("         case when acc.veces_cursado_regular > 2 then 'SI' else 'NO' end tercera_vez, ");
        sql.append("         case ");
        sql.append("                 when ac.promedio_ciclo >= 11 then 'APROBADO' ");
        sql.append("                 when ac.promedio_ciclo < 11 then 'DESAPROBADO' ");
        sql.append("                 else '' ");
        sql.append(" end condicion, ac.promedio_ciclo promedio_ponderado ");
        sql.append(" from aca_alumno_ciclo_curso acc ");
        sql.append(" join aca_alumno_ciclo ac on acc.id_alumno_ciclo = ac.id ");
        sql.append(" join aca_curso cu on acc.id_curso = cu.id ");
        sql.append(" join aca_ciclo_academico ca on ac.id_ciclo_academico = ca.id ");
        sql.append(" join aca_alumno a on ac.id_alumno = a.id ");
        sql.append(" join gen_persona p on a.id_persona = p.id ");
        sql.append(" join aca_carrera car on a.id_carrera = car.id ");
        sql.append(" left join aca_tipo_curso_curricula tcc on acc.id_tipo_curso_curricula = tcc.id ");
        sql.append(" join pronabec_informacion pi on pi.id_persona = p.id and pi.year_convocatoria = ca.year ");
        sql.append(" join pronabec_tipo_beca tb on pi.id_tipo_beca = tb.id ");
        sql.append(" left join ( ");
        sql.append("            select a2.id id_alumno,count(ac2.id) ciclos ");
        sql.append("            from aca_alumno_ciclo ac2 ");
        sql.append("            join aca_ciclo_academico ca2 on ac2.id_ciclo_academico = ca2.id ");
        sql.append("            join aca_alumno a2 on ac2.id_alumno = a2.id ");
        sql.append("            where ac2.estado ='MAT' ");
        sql.append("            and ca2.tipo ='REG' and ca2.codigo <= :CICLOACADEMICO ");
        sql.append("            group by a2.id ");
        sql.append("            ) x on a.id = x.id_alumno ");
        sql.append(" where ca.codigo = :CICLOACADEMICO and ");
        sql.append(" acc.registro_activo = true ");
        if(becadosFilterBean.getTipo_beca() != null){
            sql.append(" and tb.id = :TIPOBECA");
        }

        Query query = getCurrentSession().createSQLQuery(sql.toString())
                .addScalar("dni", StringType.INSTANCE)
                .addScalar("apellidos_nombres", StringType.INSTANCE)
                .addScalar("year_convocatoria", StringType.INSTANCE)
                .addEntity("tipo_beca", TipoBeca.class)
                .addScalar("nombre_institucion", StringType.INSTANCE)
                .addScalar("carrera", StringType.INSTANCE)
                //.addScalar("periodo_academico", StringType.INSTANCE)
                .addEntity("ciclo_academico", CicloAcademico.class)
                .addScalar("ciclos", StringType.INSTANCE)
                .addScalar("curso_matriculado", StringType.INSTANCE)
                .addScalar("nota",StringType.INSTANCE)
                .addScalar("creditos", StringType.INSTANCE)
                .addScalar("veces_cursado", StringType.INSTANCE)
                .addScalar("electivo_matriculado", StringType.INSTANCE)

                .addScalar("tercera_vez", StringType.INSTANCE)

                .addScalar("condicion", StringType.INSTANCE)
                .addScalar("promedio_ponderado", LongType.INSTANCE)
                .setResultTransformer(Transformers.aliasToBean(BecadosFilterBean.class));
;
        query.setParameter("CICLOACADEMICO", becadosFilterBean.getCiclo_academico().getCodigo());
        if(becadosFilterBean.getTipo_beca() != null){
            query.setParameter("TIPOBECA", becadosFilterBean.getTipo_beca().getId());
        }

        return (List<BecadosFilterBean>) query.list();
    }


}
