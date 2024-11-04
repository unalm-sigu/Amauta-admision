package pe.edu.lamolina.amauta.dao.academico.hibernate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.hibernate.Query;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StringType;
import pe.edu.lamolina.amauta.dao.academico.DocenteDAO;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableSql;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.amauta.controller.academico.profesor.DocenteCicloBean;
import pe.edu.lamolina.amauta.controller.academico.profesor.DocenteCicloCargaBean;
import pe.edu.lamolina.amauta.controller.programacionhorarios.gposeccion.reporte.dto.HorarioDocenteDTO;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.DepartamentoAcademico;
import pe.edu.lamolina.model.academico.Docente;
import pe.edu.lamolina.model.academico.ModalidadEstudio;
import pe.edu.lamolina.model.enums.ColaboradorEstadoEnum;
import static pe.edu.lamolina.model.enums.DocenteEstadoEnum.ACT;
import pe.edu.lamolina.model.enums.EnteAcademicoEstadoEnum;
import pe.edu.lamolina.model.enums.EstadoEnum;
import pe.edu.lamolina.model.enums.ModalidadEstudioEnum;
import pe.edu.lamolina.model.enums.PerfilColaboradorEnum;
import pe.edu.lamolina.model.enums.TipoOficinaEnum;
import pe.edu.lamolina.model.enums.persona.PersonaEstadoEnum;
import pe.edu.lamolina.model.general.Colaborador;
import pe.edu.lamolina.model.general.Persona;

@Repository
public class DocenteDAOH extends AbstractEasyDAO<Docente> implements DocenteDAO {

    public DocenteDAOH() {
        super();
        setClazz(Docente.class);
    }

    @Override
    public List<Docente> allByFacultadesDyantable(DynatableFilter filter, List<DepartamentoAcademico> departamento, String activo) {
        DynatableSql sql = new DynatableSql(filter)
                .from(Docente.class, "doc")
                .join("persona per", "departamentoAcademico da", "da.facultad fa")
                .leftJoin("per.tipoDocumento tdoc")
                .in("da.id", departamento)
                .searchFields("per.numeroDocIdentidad", "per.telefono", "per.celular", "per.emailCompania", "tdoc.simbolo", "doc.codigo")
                .searchFields("da.nombre", "fa.nombre")
                .searchComplexField("concat(coalesce(per.paterno,''),' ',coalesce(per.materno,''),' ',coalesce(per.nombres,''))")
                .searchComplexField("concat(coalesce(per.nombres,''),' ',coalesce(per.paterno,''),' ',coalesce(per.materno,''))")
                .orderBy("doc.id desc");
        if ("activos".equals(activo)) {
            sql.filter("doc.estado", "ACT");
        }
        return all(sql);
    }

    @Override
    public Docente find(long id) {
        Octavia sql = Octavia.query()
                .from(Docente.class, "doc")
                .join("persona per")
                .filter("doc.id", id);

        return find(sql);
    }

    @Override
    public Docente findByCode(String codigo) {
        Octavia sql = Octavia.query()
                .from(Docente.class, "doc")
                .leftJoin("persona per", "modalidadEstudio", "departamentoAcademico")
                .filter("doc.codigo", codigo);

        return find(sql);
    }

    @Override
    public List<Docente> allByPersona(Persona persona) {
        Octavia sql = Octavia.query()
                .from(Docente.class, "doc")
                .join("persona per")
                .leftJoin("modalidadEstudio me", "departamentoAcademico da", "da.facultad")
                .filter("per.id", persona)
                .in("me.codigo", Arrays.asList(ModalidadEstudioEnum.PRE, ModalidadEstudioEnum.EPG));

        return all(sql);
    }

    @Override
    public List<Docente> allByPersonas(List<Persona> personas) {
        Octavia sql = Octavia.query()
                .from(Docente.class, "doc")
                .join("persona per")
                .leftJoin("modalidadEstudio", "departamentoAcademico")
                .in("per.id", personas);

        return all(sql);
    }

    @Override
    public List<Docente> allActivos(ModalidadEstudio modalidad) {
        Octavia sql = Octavia.query()
                .from(Docente.class, "doc")
                .join("persona per", "modalidadEstudio me")
                .leftJoin("departamentoAcademico")
                .filter("doc.estado", EstadoEnum.ACT)
                .filter("me.id", modalidad);

        return all(sql);
    }

    @Override
    public List<Docente> allByFilter(DynatableFilter filter, List<DepartamentoAcademico> dptos) {
        DynatableSql sql = new DynatableSql(filter)
                .from(Docente.class, "doc")
                .join("persona per", "departamentoAcademico da", "da.facultad fa")
                .leftJoin("per.tipoDocumento tdoc")
                .in("da.id", dptos)
                .searchFields("per.numeroDocIdentidad", "per.telefono", "per.celular", "per.emailCompania", "tdoc.simbolo")
                .searchFields("da.nombre", "fa.nombre")
                .searchComplexField("concat(coalesce(per.paterno,''),' ',coalesce(per.materno,''),' ',coalesce(per.nombres,''))")
                .searchComplexField("concat(coalesce(per.nombres,''),' ',coalesce(per.paterno,''),' ',coalesce(per.materno,''))")
                .orderBy("doc.id desc");

        return all(sql);
    }

    @Override
    public Docente findByDocente(Docente docente) {
        Octavia sql = Octavia.query()
                .from(Docente.class, "doc")
                .join("persona per")
                .leftJoin("modalidadEstudio me", "departamentoAcademico", "per.paisDomicilio")
                .filter("doc.id", docente);

        return find(sql);
    }

    @Override
    public List<Docente> allCoordinadoresByIdDptoName(Long idDpto, String nombre) {
        nombre = "%" + nombre.replaceAll(" ", "%") + "%";
        Octavia sql = Octavia.query()
                .from(Docente.class, "do")
                .join("persona per", "departamentoAcademico dpto")
                .filter("dpto.id", idDpto)
                .__().complexFilter("concat(per.paterno,' ',per.materno,' ',per.nombres)", "like", nombre)
                .__().complexFilter("concat(per.nombres,' ',per.paterno,' ',per.materno)", "like", nombre)
                .limit(15);
        return sql.all(getCurrentSession());
    }

    @Override
    public List<Docente> allByNombreFilter(String nombre, Integer cantidad, String codigoDep) {
        nombre = "%" + nombre.replaceAll(" ", "%") + "%";
        System.out.println("nombre = <<" + nombre + ">>");
        Octavia sql = Octavia.query()
                .from(Docente.class, "doc")
                .join("persona per", "departamentoAcademico da")
                .beginBlock()
                .__().complexFilter("concat(coalesce(per.paterno,''),' ',coalesce(per.materno,''),' ',coalesce(per.nombres,''))", "like", nombre)
                .__().complexFilter("concat(coalesce(per.nombres,''),' ',coalesce(per.paterno,''),' ',coalesce(per.materno,''))", "like", nombre)
                .__().filter("doc.codigo", "like", nombre)
                .__().filter("da.nombre", "like", nombre)
                .endBlock()
                .limit(cantidad);

        if (codigoDep != null) {
            sql.filter("da.codigo", codigoDep);
        }

        return all(sql);
    }

    @Override
    public List<Docente> allByDptoEstado(Long idDpto, String estado) {
        Octavia sql = Octavia.query()
                .from(Docente.class, "doc")
                .join("persona per", "departamentoAcademico dpto")
                .filter("dpto.id", idDpto)
                .filter("doc.estado", estado);
        return all(sql);
    }

    @Override
    public List<Docente> allByDepartamentosAcademicoEstado(List<DepartamentoAcademico> departamentos, EnteAcademicoEstadoEnum estado) {
        Octavia sql = Octavia.query()
                .from(Docente.class, "doc")
                .join("persona per", "departamentoAcademico dpto")
                .leftJoin("dpto.facultad fa")
                .in("dpto.id", departamentos)
                .filter("doc.estado", estado)
                .orderBy("fa.id", "dpto.id", "per.paterno");
        return all(sql);
    }

    @Override
    public List<Docente> allByNombreDepartamento(String nombre, DepartamentoAcademico departamento, int limit) {
        nombre = "%" + nombre.replaceAll(" ", "%") + "%";
        Octavia sql = Octavia.query()
                .from(Docente.class, "doc")
                .join("persona per", "departamentoAcademico da")
                .leftJoin("per.tipoDocumento")
                .filter("da.id", departamento)
                .beginBlock()
                .__().complexFilter("concat(coalesce(per.paterno,''),' ',coalesce(per.materno,''),' ',coalesce(per.nombres,''))", "like", nombre)
                .__().complexFilter("concat(coalesce(per.nombres,''),' ',coalesce(per.paterno,''),' ',coalesce(per.materno,''))", "like", nombre)
                .__().filter("doc.codigo", "like", nombre)
                .endBlock()
                .limit(limit);

        return sql.all(getCurrentSession());
    }

    @Override
    public List<Docente> allByName(String nombre) {
        nombre = "%" + nombre.replaceAll(" ", "%") + "%";
        Octavia sql = Octavia.query()
                .from(Docente.class, "doc")
                .join("persona per", "departamentoAcademico da", "da.facultad fa")
                .leftJoin("per.tipoDocumento tdoc")
                .filter("per.estado", PersonaEstadoEnum.ACT)
                .filter("doc.estado", EstadoEnum.ACT)
                .beginBlock()
                .__().complexFilter("concat(coalesce(per.paterno,''),' ',coalesce(per.materno,''),' ',coalesce(per.nombres,''))", "like", nombre)
                .__().complexFilter("concat(coalesce(per.nombres,''),' ',coalesce(per.paterno,''),' ',coalesce(per.materno,''))", "like", nombre)
                .__().filter("per.numeroDocIdentidad", "like", nombre)
                .__().filter("doc.codigo", "like", nombre)
                .endBlock()
                .limit(15);
        return sql.all(getCurrentSession());
    }

    @Override
    public List<Docente> allByNombreDepartamentos(String nombre, List<DepartamentoAcademico> departamentos) {
        nombre = "%" + nombre.replaceAll(" ", "%") + "%";
        Octavia subQuery = new Octavia()
                .from(Colaborador.class, "col")
                .join("persona perc", "oficina ofi", "ofi.tipoOficina tip", "cargo carg")
                .in("estado", Arrays.asList(ColaboradorEstadoEnum.ACT, ColaboradorEstadoEnum.DSC, ColaboradorEstadoEnum.PER, ColaboradorEstadoEnum.VAC))
                .filter("carg.codigo", PerfilColaboradorEnum.DOC)
                .filter("tip.codigo", TipoOficinaEnum.DPTO)
                .in("ofi.instanciaOficina", departamentos);

        Octavia sql = Octavia.query()
                .from(Docente.class, "doc")
                .join("persona per", "departamentoAcademico da", "da.facultad fa")
                .leftJoin("per.tipoDocumento")
                .filter("per.estado", PersonaEstadoEnum.ACT)
                .filter("doc.estado", EstadoEnum.ACT)
                .in("da.id", departamentos)
                .beginBlock()
                .__().complexFilter("concat(coalesce(per.paterno,''),' ',coalesce(per.materno,''),' ',coalesce(per.nombres,''))", "like", nombre)
                .__().complexFilter("concat(coalesce(per.nombres,''),' ',coalesce(per.paterno,''),' ',coalesce(per.materno,''))", "like", nombre)
                .__().filter("per.numeroDocIdentidad", "like", nombre)
                .__().filter("doc.codigo", "like", nombre)
                .endBlock()
                .exists(subQuery)
                .linkedBy("per.id", "perc.id")
                .linkedBy("da.id", "ofi.instanciaOficina")
                .limit(15);

        return all(sql);
    }

    @Override
    public List<Docente> allByNombreActivoFilter(String nombre, Integer cantidad, String codigoDep) {
        nombre = "%" + nombre.replaceAll(" ", "%") + "%";
        System.out.println("nombre = <<" + nombre + ">>");
        Octavia sql = Octavia.query()
                .from(Docente.class, "doc")
                .join("persona per", "departamentoAcademico da")
                .beginBlock()
                .__().complexFilter("concat(coalesce(per.paterno,''),' ',coalesce(per.materno,''),' ',coalesce(per.nombres,''))", "like", nombre)
                .__().complexFilter("concat(coalesce(per.nombres,''),' ',coalesce(per.paterno,''),' ',coalesce(per.materno,''))", "like", nombre)
                .__().filter("doc.codigo", "like", nombre)
                .__().filter("da.nombre", "like", nombre)
                .endBlock()
                .filter("doc.estado", ACT)
                .limit(cantidad);

        if (codigoDep != null) {
            sql.filter("da.codigo", codigoDep);
        }

        return all(sql);
    }

    @Override
    public List<DocenteCicloBean> AllDocentecicloAcademico(List<CicloAcademico> cicloAcademicos) {
        StringBuilder sql = new StringBuilder();

        sql.append("    SELECT d.codigo AS cod_docente2,  ");
        sql.append("     ca.descripcion,  ");
        sql.append("     ca.codigo_anterior,  ");
        sql.append("     CONCAT(IFNULL(p.paterno, ''), ' ', IFNULL(p.materno, ''), ' ', IFNULL(p.nombres, '')) AS nombreDocente,  ");
        sql.append("     (SELECT count(a.id) ");
        sql.append("         FROM aca_anexo_boletin a  ");
        sql.append("         JOIN aca_grupo_seccion ags ON ags.id_anexo_boletin = a.id  ");
        sql.append("         join aca_ciclo_academico caaa on ags.id_ciclo = caaa.id  ");
        sql.append("         JOIN aca_seccion as2 ON as2.id_grupo_seccion = ags.id  ");
        sql.append("         JOIN aca_docente_seccion ads ON ads.id_seccion = as2.id  ");
        sql.append("         WHERE a.estado = 'ACT'   ");
        sql.append("         AND a.id_anexo_superior <> 4  ");
        sql.append("         AND caaa.id = ca.id  ");
        sql.append("         AND ads.id_docente = d.id  ");
        sql.append("     ) AS CargaPregrado, ");
        sql.append("     (SELECT count(a.id) ");
        sql.append("         FROM aca_anexo_boletin a  ");
        sql.append("         JOIN aca_grupo_seccion ags ON ags.id_anexo_boletin = a.id  ");
        sql.append("         join aca_ciclo_academico caaa on ags.id_ciclo = caaa.id  ");
        sql.append("         JOIN aca_seccion as2 ON as2.id_grupo_seccion = ags.id  ");
        sql.append("         JOIN aca_docente_seccion ads ON ads.id_seccion = as2.id  ");
        sql.append("         WHERE a.estado = 'ACT'   ");
        sql.append("         AND a.id_anexo_superior = 4  ");
        sql.append("         AND caaa.id = ca.id  ");
        sql.append("         AND ads.id_docente = d.id  ");
        sql.append("     ) AS CargaPost,  ");
        sql.append("     max(cod_dedica2) as cod_dedica2,  max(zz.nombre) nombre,max(pro_cad2) as pro_cad2,max(categ_nombre2) as categ_nombre2,  ");
        sql.append("     max(pro_situac2) as pro_situac2, max(situacionDocentez) as situac_nombre2,  ");
        sql.append("     ada.nombre_largo  ");
        sql.append(" FROM aca_grupo_seccion gs  ");
        sql.append(" JOIN aca_seccion s ON s.id_grupo_seccion = gs.id  ");
        sql.append(" JOIN aca_anexo_boletin aab ON aab.id = gs.id_anexo_boletin   ");
        sql.append(" JOIN aca_ciclo_academico ca ON gs.id_ciclo = ca.id  ");
        sql.append(" JOIN aca_docente_seccion ds ON ds.id_seccion = s.id  ");
        sql.append(" JOIN aca_docente d ON ds.id_docente = d.id  ");
        sql.append(" JOIN gen_persona p ON d.id_persona = p.id  ");
        sql.append(" join aca_departamento_academico ada ON ada.id = d.id_departamento_academico  ");
        sql.append(" left join (SELECT fz.id ,  ");
        sql.append("             dz.codigo cod_dedica2,  ");
        sql.append("             dz.nombre AS nombre,  ");
        sql.append("             cz.codigo pro_situac2,  ");
        sql.append("             cz.nombre AS situacionDocentez,  ");
        sql.append("             bz.nombre AS categ_nombre2,  ");
        sql.append("             bz.codigo pro_cad2,  ");
        sql.append("             ez.id AS id_ciclo_contrato  ");
        sql.append("         FROM rrhh_contrato_docente a   ");
        sql.append("         JOIN rrhh_categoria_docente bz ON a.id_categoria = bz.id  ");
        sql.append("         JOIN rrhh_situacion_docente cz ON a.id_situacion = cz.id  ");
        sql.append("         JOIN rrhh_dedicacion_docente dz ON a.id_dedicacion = dz.id  ");
        sql.append("         JOIN aca_ciclo_academico ez ON  ez.id = a.id_ciclo_inicio_contrato   ");
        sql.append("         JOIN aca_docente fz ON fz.id = a.id_docente   ");
        sql.append(" )  zz on zz.id=ds.id_docente and zz.id_ciclo_contrato = ca.id  ");
        sql.append(" WHERE ca.id = :CICLO   ");
        sql.append(" AND gs.estado = 'ACT'  ");
        sql.append(" AND ds.estado = 'ACT'  ");
        sql.append(" AND s.estado = 'ACT'  ");
        sql.append(" AND s.matriculados > 0  ");
        sql.append(" GROUP BY d.id, d.codigo, ca.id, ca.descripcion, ca.codigo_anterior, CONCAT(IFNULL(p.paterno, ''), ' ', IFNULL(p.materno, ''), ' ', IFNULL(p.nombres, ''))  ");
        sql.append(" ORDER BY 4 ");

//        sql.append("  select distinct d.codigo as cod_docente2,ca.descripcion,ca.codigo_anterior ,CONCAT(IFNULL(p.paterno,''),' ',IFNULL(p.materno,''),' ',IFNULL(p.nombres,'')) as nombreDocente, ");
//        sql.append("  cad.codigo pro_cad2, ");
//        sql.append("  cad.nombre categ_nombre2,sid.codigo pro_situac2,sid.nombre situac_nombre2, ");
//        sql.append("  ded.codigo cod_dedica2,ded.nombre nombre,cad2.descripcion2,ada.nombre_largo  ");
//        sql.append("  from aca_grupo_seccion gs  ");
//        sql.append("  join aca_seccion s on s.id_grupo_seccion = gs.id ");
//        sql.append("  join aca_ciclo_academico ca on gs.id_ciclo = ca.id ");
//        sql.append("  join aca_docente_seccion ds on ds.id_seccion = s.id ");
//        sql.append("  join aca_docente d on ds.id_docente = d.id ");
//        sql.append("  join gen_persona p on d.id_persona = p.id ");
//        sql.append("  join rrhh_contrato_docente cd on cd.id_docente = d.id ");
//        sql.append("  join rrhh_categoria_docente cad on cd.id_categoria = cad.id ");
//        sql.append("  join rrhh_situacion_docente sid on cd.id_situacion = sid.id ");
//        sql.append("  join rrhh_dedicacion_docente ded on cd.id_dedicacion = ded.id ");
//        sql.append("  join aca_ciclo_academico cad2 on cd.id_ciclo_inicio_contrato = cad2.id and ca.id = cad2.id ");
//        sql.append("  join aca_departamento_academico ada on ada.id =d.id_departamento_academico  ");
//        sql.append("  where ca.id  = :CICLO ");
//        sql.append("  and gs.estado = 'ACT' ");
//        sql.append("  and s.estado = 'ACT' ");
//        sql.append("  and s.matriculados > 0 ");
//        sql.append("  order by 3 ");
        Query query = getCurrentSession().createSQLQuery(sql.toString())
                .addScalar("descripcion", StringType.INSTANCE)
                .addScalar("codigo_anterior", StringType.INSTANCE)
                .addScalar("cod_docente2", StringType.INSTANCE)
                .addScalar("nombreDocente", StringType.INSTANCE)
                .addScalar("pro_cad2", StringType.INSTANCE)
                .addScalar("categ_nombre2", StringType.INSTANCE)
                .addScalar("pro_situac2", StringType.INSTANCE)
                .addScalar("situac_nombre2", StringType.INSTANCE)
                .addScalar("cod_dedica2", StringType.INSTANCE)
                .addScalar("nombre", StringType.INSTANCE)
                .addScalar("nombre_largo", StringType.INSTANCE)
                .addScalar("CargaPregrado", StringType.INSTANCE)
                .addScalar("CargaPost", StringType.INSTANCE)
                .setResultTransformer(Transformers.aliasToBean(DocenteCicloBean.class));
        List<Long> cicloIds = new ArrayList<>();
        for (CicloAcademico cicloAcademico : cicloAcademicos) {
            cicloIds.add(cicloAcademico.getId());
        }
        query.setParameterList("CICLO", cicloIds);
        return (List<DocenteCicloBean>) query.list();

    }

    @Override
    public List<DocenteCicloCargaBean> AllDocentecicloCargaAcademico(Long docente) {
        StringBuilder sql = new StringBuilder();

        sql.append(" SELECT ca.descripcion,abss.nombre anexo, cu.codigo as codigoCurso,cu.nombre curso,cu.horas_teoria as horasTeoria,cu.horas_practica as horasPractica,cu.creditos, s.codigo2 seccion, ");
        sql.append(" case s.tipo_seccion ");
        sql.append(" when 'TCUR' then 'Teoria' ");
        sql.append(" when 'PCUR' then 'Práctica' ");
        sql.append(" when 'PRA' then 'Práctica' ");
        sql.append(" when 'TEO' then 'Teoria' ");
        sql.append(" else 'NO DEFINIDO' ");
        sql.append(" end as 'tiposeccion', ");
        sql.append(" ds.porcentaje_carga as porcentajeCarga,  ");
        sql.append(" ds.fecha_inicio as fechaInicio, ds.fecha_fin fechaFin, ");
        sql.append(" di.nombre dia, ");
        sql.append(" case ");
        sql.append(" when (s.tipo_seccion = 'TCUR' or s.tipo_seccion = 'TEO') ");
        sql.append(" then ROUND((cu.creditos_teoria * ds.porcentaje_carga) / 100) ");
        sql.append(" when s.tipo_seccion = 'PCUR' or s.tipo_seccion = 'PRA' ");
        sql.append(" then ROUND((cu.creditos_practica * ds.porcentaje_carga) / 100) end credProfe,s.matriculados, ");
        sql.append(" group_concat(ho.descripcion,' - ',ho.descripcion_fin) as Horario,d.codigo as codDocente ");
        sql.append(" FROM aca_grupo_seccion gs  ");
        sql.append(" JOIN aca_seccion s ON s.id_grupo_seccion = gs.id ");
        sql.append(" join aca_curso cu on gs.id_curso = cu.id ");
        sql.append(" JOIN aca_anexo_boletin aab ON aab.id = gs.id_anexo_boletin   ");
        sql.append(" left join aca_anexo_boletin abss on aab.id_anexo_superior = abss.id ");
        sql.append(" JOIN aca_ciclo_academico ca ON gs.id_ciclo = ca.id  ");
        sql.append(" JOIN aca_docente_seccion ds ON ds.id_seccion = s.id  ");
        sql.append(" JOIN aca_docente d ON ds.id_docente = d.id  ");
        sql.append(" JOIN gen_persona p ON d.id_persona = p.id  ");
        sql.append(" join aca_departamento_academico ada ON ada.id = d.id_departamento_academico  ");
        sql.append(" left join hor_horario_seccion hs on hs.id_seccion = s.id ");
        sql.append(" left join gen_dia di on hs.id_dia = di.id ");
        sql.append(" left join hor_hora ho on hs.id_hora = ho.id ");
        sql.append(" left join hor_grupo_horas ghh on s.id_grupo_horas = ghh.id  ");
        sql.append(" where gs.estado = 'ACT'  ");
        sql.append(" AND ds.estado = 'ACT'  ");
        sql.append(" AND s.estado = 'ACT'  ");
        sql.append(" AND s.matriculados > 0  ");
        sql.append(" and d.id = :CODIGO ");
        sql.append(" group by ");
        sql.append(" ca.descripcion,abss.nombre, ");
        sql.append(" cu.codigo,cu.nombre,  ");
        sql.append(" cu.horas_teoria,cu.horas_practica,cu.creditos,s.codigo2, ");
        sql.append(" s.tipo_seccion, ");
        sql.append(" ds.porcentaje_carga,  ");
        sql.append(" ds.fecha_inicio, ds.fecha_fin, ");
        sql.append(" di.nombre, ");
        sql.append(" s.matriculados, ");
        sql.append(" cu.creditos_teoria, ");
        sql.append(" cu.creditos_practica order by 1");
        Query query = getCurrentSession().createSQLQuery(sql.toString())
                .addScalar("descripcion", StringType.INSTANCE)
                .addScalar("anexo", StringType.INSTANCE)
                .addScalar("codigoCurso", StringType.INSTANCE)
                .addScalar("curso", StringType.INSTANCE)
                .addScalar("horasTeoria", StringType.INSTANCE)
                .addScalar("horasPractica", StringType.INSTANCE)
                .addScalar("creditos", StringType.INSTANCE)
                .addScalar("seccion", StringType.INSTANCE)
                .addScalar("tiposeccion", StringType.INSTANCE)
                .addScalar("porcentajeCarga", StringType.INSTANCE)
                .addScalar("fechaInicio", StringType.INSTANCE)
                .addScalar("fechaFin", StringType.INSTANCE)
                .addScalar("dia", StringType.INSTANCE)
                .addScalar("credProfe", StringType.INSTANCE)
                .addScalar("credProfe", StringType.INSTANCE)
                .addScalar("matriculados", StringType.INSTANCE)
                .addScalar("horario", StringType.INSTANCE)
                .addScalar("codDocente", StringType.INSTANCE)
                .setResultTransformer(Transformers.aliasToBean(DocenteCicloCargaBean.class));

        query.setParameter("CODIGO", docente);
        return (List<DocenteCicloCargaBean>) query.list();

    }

    @Override
    public List<HorarioDocenteDTO> horarioDocente(CicloAcademico cicloAcademico, String id) {
        StringBuilder sql = new StringBuilder();
        sql.append("  SELECT     ");
        sql.append("  max(ciclo ) as ciclo,    ");
        sql.append("       max(nombres) as nombres,    ");
        sql.append("       max(codigoDocente) as codigoDocente,    ");
        sql.append("      max(nombreDepartamento) as nombreDepartamento,    ");
        sql.append("      hora,max(hora2) as hora2,numero,    ");
        sql.append("      COALESCE(MAX(CASE WHEN dia = 'Lunes' THEN curso end),'') AS curso_lunes,    ");
        sql.append("      COALESCE(MAX(CASE WHEN dia = 'Martes' THEN curso END),'') AS curso_martes,    ");
        sql.append("      COALESCE(MAX(CASE WHEN dia = 'Miércoles' THEN curso END),'') AS curso_miercoles,    ");
        sql.append("      COALESCE(MAX(CASE WHEN dia = 'Jueves' THEN curso END),'') AS curso_jueves,    ");
        sql.append("      COALESCE(MAX(CASE WHEN dia = 'Viernes' THEN curso END),'') AS curso_viernes,    ");
        sql.append("      COALESCE(MAX(CASE WHEN dia = 'Sabado' THEN curso END),'') AS curso_sabado,    ");
        sql.append("          COALESCE(MAX(CASE WHEN dia = 'Lunes' THEN CONCAT('Grupo : ',letra ) END), '') AS grupo_lunes,    ");
        sql.append("        COALESCE(MAX(CASE WHEN dia = 'Martes' THEN CONCAT(' Grupo : ',letra ) END), '') AS grupo_martes,    ");
        sql.append("        COALESCE(MAX(CASE WHEN dia = 'Miércoles' THEN CONCAT(' Grupo : ',letra ) END), '') AS grupo_miercoles,    ");
        sql.append("        COALESCE(MAX(CASE WHEN dia = 'Jueves' THEN CONCAT(' Grupo : ',letra ) END), '') AS grupo_jueves,    ");
        sql.append("        COALESCE(MAX(CASE WHEN dia = 'Viernes' THEN CONCAT(' Grupo : ',letra ) END), '') AS grupo_viernes,    ");
        sql.append("          COALESCE(MAX(CASE WHEN dia = 'Sabado' THEN CONCAT(' Grupo : ',letra ) END), '') AS grupo_sabado,    ");
        sql.append("     COALESCE(MAX(CASE WHEN dia = 'Lunes' THEN CONCAT('Aula : ',codigo ) END), '') AS aula_lunes,    ");
        sql.append("     COALESCE(MAX(CASE WHEN dia = 'Martes' THEN CONCAT('Aula : ',codigo ) END), '') AS aula_martes,    ");
        sql.append("     COALESCE(MAX(CASE WHEN dia = 'Miércoles' THEN CONCAT('Aula : ',codigo ) END), '') AS aula_miercoles,    ");
        sql.append("     COALESCE(MAX(CASE WHEN dia = 'Jueves' THEN CONCAT('Aula : ',codigo ) END), '') AS aula_jueves,    ");
        sql.append("     COALESCE(MAX(CASE WHEN dia = 'Viernes' THEN CONCAT('Aula : ',codigo ) END), '') AS aula_viernes,    ");
        sql.append("     COALESCE(MAX(CASE WHEN dia = 'Sabado' THEN CONCAT('Aula : ',codigo ) END), '') AS aula_sabado          ");
        sql.append("  FROM (    ");
        sql.append("      SELECT     ");
        sql.append("        ghh.letra as letra ,    ");
        sql.append("        aca.descripcion as ciclo,    ");
        sql.append("        Concat(gp.paterno,' ',gp.materno,' ',gp.nombres) as nombres,    ");
        sql.append("         ad2.codigo as codigoDocente,    ");
        sql.append("         ada.nombre  as nombreDepartamento,    ");
        sql.append("          hh2.descripcion AS hora,    ");
        sql.append("          hh2.descripcion AS hora2,    ");
        sql.append("          gd2.nombre AS dia,    ");
        sql.append("          ac2.nombre AS curso,    ");
        sql.append("          hh2.numero AS numero,    ");
        sql.append("          ga.codigo  as codigo    ");
        sql.append("      FROM     ");
        sql.append("          aca_seccion as3     ");
        sql.append("          JOIN aca_docente_seccion ads2 ON as3.id = ads2.id_seccion             ");
        sql.append("          JOIN aca_docente ad2 ON ad2.id = ads2.id_docente     ");
        sql.append("          join aca_departamento_academico ada on ada.id =ad2.id_departamento_academico     ");
        sql.append("          join gen_persona gp on gp.id =ad2.id_persona    ");
        sql.append("          JOIN aca_grupo_seccion ags2 ON ags2.id = as3.id_grupo_seccion     ");
        sql.append("          join aca_ciclo_academico aca on aca.id =ags2.id_ciclo    ");
        sql.append("          JOIN aca_curso ac2 ON ac2.id = ags2.id_curso     ");
        sql.append("          JOIN hor_horario_seccion hhs ON hhs.id_seccion = as3.id      ");
        sql.append("          JOIN gen_dia gd2 ON gd2.id = hhs.id_dia     ");
        sql.append("          JOIN hor_hora hh2 ON hh2.id = hhs.id_hora     ");
        sql.append("          left join gen_aula ga on ga.id =as3.id_aula    ");
        sql.append("          left join hor_grupo_horas ghh on as3.id_grupo_horas = ghh.id     ");
        sql.append("      WHERE     ");
        sql.append("          ad2.codigo  = :CODIGO AND ags2.id_ciclo = :IDCICLO    ");
        sql.append("          AND ac2.estado = 'ACT' AND ad2.estado = 'ACT' AND as3.estado = 'ACT'    ");
        sql.append("      ORDER BY     ");
        sql.append("          numero    ");
        sql.append("  ) AS subconsulta    ");
        sql.append("  GROUP BY     ");
        sql.append("      numero,hora    ");
        sql.append("  ORDER BY     ");
        sql.append("  numero,hora;   ");
        Query query = getCurrentSession().createSQLQuery(sql.toString())
                .addScalar("ciclo", StringType.INSTANCE)
                .addScalar("nombres", StringType.INSTANCE)
                .addScalar("codigoDocente", StringType.INSTANCE)
                .addScalar("nombreDepartamento", StringType.INSTANCE)
                .addScalar("hora", StringType.INSTANCE)
                .addScalar("hora2", StringType.INSTANCE)
                .addScalar("numero", StringType.INSTANCE)
                .addScalar("curso_lunes", StringType.INSTANCE)
                .addScalar("curso_martes", StringType.INSTANCE)
                .addScalar("curso_miercoles", StringType.INSTANCE)
                .addScalar("curso_jueves", StringType.INSTANCE)
                .addScalar("curso_viernes", StringType.INSTANCE)
                .addScalar("curso_sabado", StringType.INSTANCE)
                .addScalar("grupo_lunes", StringType.INSTANCE)
                .addScalar("grupo_martes", StringType.INSTANCE)
                .addScalar("grupo_miercoles", StringType.INSTANCE)
                .addScalar("grupo_jueves", StringType.INSTANCE)
                .addScalar("grupo_viernes", StringType.INSTANCE)
                .addScalar("grupo_sabado", StringType.INSTANCE)
                .addScalar("aula_lunes", StringType.INSTANCE)
                .addScalar("aula_martes", StringType.INSTANCE)
                .addScalar("aula_miercoles", StringType.INSTANCE)
                .addScalar("aula_jueves", StringType.INSTANCE)
                .addScalar("aula_viernes", StringType.INSTANCE)
                .addScalar("aula_sabado", StringType.INSTANCE)
                .setResultTransformer(Transformers.aliasToBean(HorarioDocenteDTO.class));
        query.setParameter("CODIGO", id);
        query.setParameter("IDCICLO", cicloAcademico.getId());
        return (List<HorarioDocenteDTO>) query.list();
//        return query.list();
    }

}
