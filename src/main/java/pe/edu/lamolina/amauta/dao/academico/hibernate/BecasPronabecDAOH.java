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
import pe.edu.lamolina.model.tramite.Tramite;

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
    public List<InformacionBeca> finByPersonaIds(InformacionBeca infoBeca) {
        Octavia sql = Octavia.query()
                .from(InformacionBeca.class, "ib")
                .join("persona per")
                .filter("per.numeroDocIdentidad", infoBeca.getId())
                .orderBy("ib.yearConvocatoria desc");
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
    public void updateEstado(InformacionBeca informacionBeca) {
        Octavia octavia = Octavia.update(InformacionBeca.class);
        octavia.set(informacionBeca, "estado");
        octavia.set(informacionBeca, "usuario");
        this.update(octavia);
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
        sql.append(" select distinct a.numero_doc_identidad as dni, concat(ifnull(a.paterno,''),' ',ifnull(a.materno,''),', ',ifnull(a.nombres,'')) apellidos_nombres,  ");
        sql.append("         'Universidad Agraria La Molina' nombre_institucion,  ");
        sql.append("         e.nombre as carrera, IF(c.estado = 'MAT', 'Si', 'No') as se_matriculo,  ");
        sql.append(" case when x.num_ciclo =1  then concat(x.descripcion,'  - ','Primer ciclo')  ");
        sql.append(" when x.num_ciclo =2  then concat(x.descripcion,'  - ','Segundo ciclo')  ");
        sql.append(" when x.num_ciclo =3  then concat(x.descripcion,'  - ','Tercer ciclo')  ");
        sql.append(" when x.num_ciclo =4  then concat(x.descripcion,'  - ','Cuarto ciclo')  ");
        sql.append(" when x.num_ciclo =5  then concat(x.descripcion,'  - ','Quinto ciclo')  ");
        sql.append(" when x.num_ciclo =6  then concat(x.descripcion,'  - ','Sexto ciclo')  ");
        sql.append(" when x.num_ciclo =7  then concat(x.descripcion,'  - ','Septimo ciclo')  ");
        sql.append(" when x.num_ciclo =8  then concat(x.descripcion,'  - ','Octavo ciclo')  ");
        sql.append(" when x.num_ciclo =9  then concat(x.descripcion,'  - ','Noveno ciclo')  ");
        sql.append(" when x.num_ciclo =10 then concat(x.descripcion,' - ','Decimo ciclo')  ");
        sql.append(" when x.num_ciclo =11 then concat(x.descripcion,' - ','Onceavo ciclo')  ");
        sql.append(" when x.num_ciclo =12 then concat(x.descripcion,' - ','Doceavo ciclo')  ");
	    sql.append(" else 'No Matriculado'end as periodo_academico,  ");
        sql.append("         c.cursos_matriculados as curso_matriculado, c.creditos_matriculados as creditos,z.item2 as electivo_matriculado,  ");
        sql.append(" IF(t1.tipo_traslado = 'TRAS_INT', 'Si', 'No') as cambio_carrera,  ");
        sql.append(" IF(h.item2 = '3', 'Si', 'No') as tercera_vez  ");
        sql.append(" from gen_persona a  ");
        sql.append(" join aca_alumno b on b.id_persona =a.id  ");
        sql.append(" join aca_matricula_resumen c on c.id_alumno =b.id  ");
        sql.append(" join aca_ciclo_academico d on d.id =c.id_ciclo_academico  ");
        sql.append(" join aca_carrera e on e.id =b.id_carrera  ");
        sql.append(" join pronabec_informacion pi on pi.id_persona= a.id  ");
        sql.append(" join pronabec_tipo_beca tb on pi.id_tipo_beca = tb.id ");
        sql.append(" left join(select p4.codigo,p.id_alumno AS alu,p3.nombre as itemca ,p1.id ,p1.codigo_anterior ,p2.estado as estado1 ,p.estado as estado2,p2.tipo_traslado,p1.descripcion  ");
        sql.append(" from tram_tramite p  ");
        sql.append(" join aca_ciclo_academico p1 on p1.id =p.id_ciclo_academico  ");
        sql.append(" join tram_tramite_traslado p2 ON p2.id_tramite =p.id  ");
        sql.append(" join aca_carrera p3 on p3.id =p2.id_carrera  ");
        sql.append(" join aca_alumno p4 on p4.id =p.id_alumno  ");
        sql.append(" where p2.estado= 'ACEP' AND p.estado='ACEP') t1 on t1.alu= b.id and  t1.id=b.id_ciclo_activo_regular  ");
        sql.append(" left join (select ROW_NUMBER() over(PARTITION by z2.codigo order by z3.codigo_anterior)  as num_ciclo,z2.codigo  ");
        sql.append(" as matricula,gp3.numero_doc_identidad  as DNI,z2.id,z.id_alumno,z3.id as item ,z3.codigo_anterior,z.estado,z3.descripcion,z2.id_carrera item2  ");
        sql.append(" from aca_matricula_resumen z  ");
        sql.append(" join aca_alumno z2 on z2.id =z.id_alumno  ");
        sql.append(" join aca_ciclo_academico z3 on z3.id = z.id_ciclo_academico and z3.tipo ='REG'  ");
        sql.append(" join gen_persona gp3 on gp3.id =z2.id_persona  ");
        sql.append(" where z.estado ='MAT' ) x on x.id_alumno=b.id and x.id_alumno=c.id_alumno and x.item=d.id and x.item2=e.id  ");
        sql.append(" left join (select amr.id_alumno,x.codigo,aca.id  as item,count(distinct ac.id) as item2  ");
        sql.append(" from aca_matricula_curso amc  ");
        sql.append(" join aca_matricula_resumen amr on amr.id =amc.id_matricula_resumen  ");
        sql.append(" join aca_ciclo_academico aca on aca.id =amr.id_ciclo_academico  ");
        sql.append(" join aca_curso ac on ac.id =amc.id_curso  ");
        sql.append(" join aca_tipo_curso_curricula x on x.id =amc.id_tipo_curso_curricula  ");
        sql.append(" join aca_alumno aa on aa.id =amr.id_alumno  ");
        sql.append(" where  amc.id_tipo_curso_curricula ='3'  ");
        sql.append(" and ac.nivel <>'7' and amc.estado ='MAT'  ");
        sql.append(" group by amr.id_alumno,x.codigo,aca.id ) z on z.id_alumno=b.id and z.item=d.id  ");
        sql.append(" left join (select distinct y1.id_alumno,y.id_curso,ac.codigo,ac.nombre,(y.veces_cursado_regular + y5.item1) as  item2,y5.ciclo  ");
        sql.append(" from aca_alumno_ciclo_curso y  ");
        sql.append(" join aca_alumno_ciclo y1 on y1.id =y.id_alumno_ciclo  ");
        sql.append(" join aca_ciclo_academico y3 on y3.id =y1.id_ciclo_academico  and y3.tipo ='REG'  ");
        sql.append(" join aca_alumno y4 on y4.id =y1.id_alumno and y4.id_modalidad_estudio ='1'  ");
        sql.append(" join aca_curso ac on ac.id =y.id_curso  ");
        sql.append(" inner join (select a1.id_alumno ,amc.id_curso,aca.codigo_anterior as ciclo,count(*) item1  ");
        sql.append(" from aca_matricula_resumen a1  ");
        sql.append(" join aca_ciclo_academico aca on aca.id =a1.id_ciclo_academico and aca.tipo ='REG' and aca.codigo_anterior = :CICLO_ACTUAL   ");
        sql.append(" join aca_matricula_curso amc on amc.id_matricula_resumen =a1.id WHERE a1.estado ='MAT' and amc.estado ='MAT'  ");
        sql.append(" group by a1.id_alumno ,amc.id_curso ) y5 on y5.id_curso =y.id_curso and y5.id_alumno =y4.id  ");
        sql.append(" where y.veces_cursado_regular ='2') h on h.id_alumno =c.id_alumno and h.id_alumno=b.id  ");
        sql.append(" where d.codigo_anterior = :CICLO_ACTUAL  ");
        if(becadosFilterBean.getTipo_beca() != null){
            sql.append(" and tb.id = :TIPOBECA");
        }
        sql.append(" order by 2,6  ");

        Query query = getCurrentSession().createSQLQuery(sql.toString())
                .addScalar("dni", StringType.INSTANCE)
                .addScalar("apellidos_nombres", StringType.INSTANCE)
                .addScalar("nombre_institucion", StringType.INSTANCE)
                .addScalar("carrera", StringType.INSTANCE)
                .addScalar("se_matriculo", StringType.INSTANCE)
                .addScalar("periodo_academico", StringType.INSTANCE)
                .addScalar("curso_matriculado", StringType.INSTANCE)
                .addScalar("creditos", StringType.INSTANCE)
                .addScalar("electivo_matriculado", StringType.INSTANCE)
                .addScalar("cambio_carrera", StringType.INSTANCE)
                .addScalar("tercera_vez", StringType.INSTANCE)
                .setResultTransformer(Transformers.aliasToBean(BecadosFilterBean.class));
        query.setParameter("CICLO_ACTUAL",cicloAcademico.getCodigoAnterior());
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

        query.setParameter("CICLOACADEMICO", becadosFilterBean.getCiclo_academico().getCodigo());
        if(becadosFilterBean.getTipo_beca() != null){
            query.setParameter("TIPOBECA", becadosFilterBean.getTipo_beca().getId());
        }

        return (List<BecadosFilterBean>) query.list();
    }


}
