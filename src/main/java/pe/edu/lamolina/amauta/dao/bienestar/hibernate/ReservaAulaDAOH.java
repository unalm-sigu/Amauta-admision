package pe.edu.lamolina.amauta.dao.bienestar.hibernate;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.Query;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StringType;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableSql;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.amauta.controller.programacionhorarios.tramiteaula.ReservaAulaBean;
import pe.edu.lamolina.model.tramite.ReservaAula;
import pe.edu.lamolina.amauta.dao.bienestar.ReservaAulaDAO;
import pe.edu.lamolina.model.enums.TipoSolicitanteEnum;
import pe.edu.lamolina.model.tramite.AulaReservada;

@Repository
public class ReservaAulaDAOH extends AbstractEasyDAO<ReservaAula> implements ReservaAulaDAO {

    public ReservaAulaDAOH() {
        super();
        setClazz(ReservaAula.class);
    }

    @Override
    public List<ReservaAula> allDynatableFilter(DynatableFilter filter) {

        DynatableSql sql = new DynatableSql(filter)
                .from(ReservaAula.class, "ra")
                .join("tramite tra", "tra.tipoTramite", "tra.cicloAcademico ca")
                .leftJoin("tra.compania cia", "tra.empresa em", "tra.oficina ofi", "tra.docente doc")
                .leftJoin("tra.alumno al", "al.persona per", "doc.persona perr")
                .searchFields("motivo","tra.numero")
                .orderBy("ra.id desc");

        this.setFilterAula(filter, sql);
        this.setFilterSolicitante(filter, sql);

        return all(sql);
    }

    private void setFilterAula(DynatableFilter filter, DynatableSql sql) {

        Map<String, Object> queries = filter.getQueries();

        if (queries == null) {
            return;
        }

        String strAula = (String) queries.get("aula");

        if (StringUtils.isBlank(strAula)) {
            return;
        }

        Long idAula = new Long(strAula);

        if (idAula != null) {

            Octavia subQuery = new Octavia()
                    .from(AulaReservada.class, "ar")
                    .join("reservaAula rau", "aula au")
                    .filter("au.id", idAula);

            sql.__().exists(subQuery)
                    .__().linkedBy("ra.id", "rau.id");

        }
    }

    private void setFilterSolicitante(DynatableFilter filter, DynatableSql sql) {

        Map<String, Object> queries = filter.getQueries();
        if (queries == null) {
            return;
        }

        String tipoSolicitante = (String) queries.get("tipo");

        if (StringUtils.isBlank(tipoSolicitante)) {
            return;
        }

        Long idSolicitante = new Long((String) queries.get("solicitante"));

        if (tipoSolicitante.equals(TipoSolicitanteEnum.ALU.name())) {
            sql.filter("al.id", idSolicitante);
        } else if (tipoSolicitante.equals(TipoSolicitanteEnum.DOC.name())) {
            sql.filter("doc.id", idSolicitante);
        } else if (tipoSolicitante.equals(TipoSolicitanteEnum.OFI.name())) {
            sql.filter("ofi.id", idSolicitante);
        } else if (tipoSolicitante.equals(TipoSolicitanteEnum.EMP.name())) {
            sql.filter("em.id", idSolicitante);
        }

    }

    @Override
    public ReservaAula find(ReservaAula reservaAula) {

        Octavia sql = Octavia.query()
                .from(ReservaAula.class, "ra")
                .join("tramite tra", "tra.tipoTramite", "tra.cicloAcademico ca")
                .leftJoin("tra.compania cia", "tra.empresa em", "tra.docente doc")
                .leftJoin("tra.alumno al", "al.persona per", "doc.persona perr")
                .filter("ra.id", reservaAula);

        return find(sql);
    }

    @Override
    public List<ReservaAulaBean> allReservaTipoAmbiente() {
        StringBuilder sql = new StringBuilder();
        sql.append(" select ");
        sql.append(" concat(tt.serie, '-',tt.numero) codigo, tra.fecha_inicio, tra.fecha_fin, ");
        sql.append("         group_concat(distinct au.codigo, '(', au.capacidad_aula, ')' separator ', ') aula, ");
        sql.append(" case when tt.tipo_solicitante = 'DOC' then concat(perso.nombres, ' ', perso.paterno, ' ', perso.materno) ");
        sql.append(" when tt.tipo_solicitante = 'OFI' then ofi.nombre ");
        sql.append(" when tt.tipo_solicitante = 'ALU' then concat(perso1.nombres, ' ', perso1.paterno, ' ', perso1.materno) end solicitante, ");
        sql.append(" tra.motivo, ");
        sql.append("         coalesce(tra.comentario, '') comentario, ");
        sql.append(" case when tra.estado = 'RES' then 'Reservado' ");
        sql.append(" when tra.estado = 'ANU' then 'Anulado' ");
        sql.append(" when tra.estado = 'PEN' then 'Pendiente' end estado, ");
        sql.append(" case when tra.tipo_reserva_ambiente = 'PRE' then 'Pregrado' ");
        sql.append(" when tra.tipo_reserva_ambiente = 'EPG' then 'Posgrado' ");
        sql.append(" when tra.tipo_reserva_ambiente = 'LIB' then 'Libre' end tipo_reserva, ");
        sql.append(" acad.descripcion2 ciclo ");
        sql.append(" from tram_reserva_aula tra ");
        sql.append(" left join tram_aula_reservada tar on tar.id_tramite_reserva_aula = tra.id ");
        sql.append(" left join gen_dia d on tar.id_dia = d.id ");
        sql.append(" left join gen_aula au on tar.id_aula = au.id ");
        sql.append(" left join hor_hora h on tar.id_hora = h.id ");
        sql.append(" join tram_tramite tt on tra.id_tramite = tt.id ");
        sql.append(" left join aca_alumno al on al.id = tt.id_alumno ");
        sql.append(" left join aca_docente doc on doc.id = tt.id_docente ");
        sql.append(" left join gen_persona perso on perso.id = doc.id_persona ");
        sql.append(" left join gen_persona perso1 on al.id_persona = perso1.id ");
        sql.append(" left join gen_oficina ofi on tt.id_oficina = ofi.id ");
        sql.append(" join aca_ciclo_academico acad on acad.id = tt.id_ciclo_academico ");
        sql.append(" group by tra.id ");
        sql.append(" order by tt.serie desc, tt.numero desc; ");

        Query query = getCurrentSession().createSQLQuery(sql.toString())
                .addScalar("codigo", StringType.INSTANCE)
                .addScalar("fecha_inicio", StringType.INSTANCE)
                .addScalar("fecha_fin", StringType.INSTANCE)
                .addScalar("aula", StringType.INSTANCE)
                .addScalar("solicitante", StringType.INSTANCE)
                .addScalar("motivo", StringType.INSTANCE)
                .addScalar("comentario", StringType.INSTANCE)
                .addScalar("estado", StringType.INSTANCE)
                .addScalar("tipo_reserva", StringType.INSTANCE)
                .addScalar("ciclo", StringType.INSTANCE)
                .setResultTransformer(Transformers.aliasToBean(ReservaAulaBean.class));

        return (List<ReservaAulaBean>) query.list();
    }
}
