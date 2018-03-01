package pe.edu.lamolina.pivot.dao.academico.hibernate;

import java.math.BigDecimal;
import java.util.Map;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.academico.SituacionConfig;
import pe.edu.lamolina.pivot.dao.academico.SituacionConfigDAO;

@Repository
public class SituacionConfigDAOH extends AbstractEasyDAO<SituacionConfig> implements SituacionConfigDAO {

    @Override
    public SituacionConfig findForSituacionFinal(SituacionConfig situacionConfig) {
        Octavia sql = Octavia.query()
                .from(SituacionConfig.class, "sec")
                .join("situacionInicial si")
                .leftJoin("situacionFinal sf")
                .filter("si.id", situacionConfig.getSituacionInicial())
                .filter("sec.aprobado", situacionConfig.getAprobado())
                .filter("sec.ciclosEstudiados", situacionConfig.getCiclosEstudiados())
                .filter("sec.autorizado", situacionConfig.getAutorizado())
                .filter("sec.capa", situacionConfig.getCapa())
                // .filter("sec.capaMayore", situacionConfig.getCapaMayore())
                .filter("sec.siguienteCiclo", BigDecimal.ONE.intValue())
                .filter("sec.tramite", situacionConfig.getTramite())
                .filter("sec.cicloRegular", situacionConfig.getCicloRegular());
        return find(sql);
    }

    @Override
    public SituacionConfig findsSituacionConfig(SituacionConfig situacionConfig) {
        StringBuilder strbQuery = new StringBuilder();
        strbQuery.append(" SELECT ");
        strbQuery.append(" situacionc0_.id_situacion_inicial as situacionInicial, situacionc0_.id_situacion_final as situacionFinal ");
        strbQuery.append(" FROM ");
        strbQuery.append(" aca_situacion_config situacionc0_  ");
        strbQuery.append(" INNER JOIN aca_situacion_academica situaciona1_  ON situacionc0_.id_situacion_inicial=situaciona1_.id ");
        strbQuery.append(" LEFT OUTER JOIN aca_situacion_academica situaciona2_  ON situacionc0_.id_situacion_final=situaciona2_.id  ");
        strbQuery.append(" WHERE ");
        strbQuery.append(" situaciona1_.id=:PRM_INICIAL AND ");
        strbQuery.append(" case situacionc0_.aprobado when -1 then :APROBADO else situacionc0_.aprobado end = :APROBADO  ");
        strbQuery.append(" AND case situacionc0_.ciclos_estudiados when -1 then :CICLO_EST else situacionc0_.ciclos_estudiados end = :CICLO_EST  ");
        strbQuery.append(" AND case situacionc0_.autorizado when -1 then :AUTORIZADO else situacionc0_.autorizado end = :AUTORIZADO  ");
        strbQuery.append(" AND case when (situacionc0_.capa > 0 and situacionc0_.capa_mayore = 0) then situacionc0_.capa > :CAPA ");
        strbQuery.append(" when (situacionc0_.capa > 0 and situacionc0_.capa_mayore = 1) then situacionc0_.capa <= :CAPA ");
        strbQuery.append(" else (1=1) end AND case situacionc0_.siguiente_ciclo when -1 then :SGTE_CICLO else situacionc0_.siguiente_ciclo end = :SGTE_CICLO ");
        strbQuery.append(" AND case situacionc0_.tramite when -1 then :TRAMITE else situacionc0_.tramite end = :TRAMITE ");
        strbQuery.append(" AND case situacionc0_.ciclo_regular when -1 then :CICLO_REG else situacionc0_.ciclo_regular end = :CICLO_REG ");
        Query query = getCurrentSession().createSQLQuery(strbQuery.toString());

        query.setResultTransformer(Criteria.ALIAS_TO_ENTITY_MAP);
        query.setParameter("PRM_INICIAL", situacionConfig.getSituacionInicial().getId());
        query.setParameter("APROBADO", situacionConfig.getAprobado());
        query.setParameter("CICLO_EST", situacionConfig.getCiclosEstudiados());
        query.setParameter("AUTORIZADO", situacionConfig.getAprobado());
        query.setParameter("CAPA", situacionConfig.getCapa());
        query.setParameter("SGTE_CICLO", situacionConfig.getSiguienteCiclo());
        query.setParameter("TRAMITE", situacionConfig.getTramite());
        query.setParameter("CICLO_REG", situacionConfig.getCicloRegular());
        Map result = (Map) query.uniqueResult();

        SituacionConfig situacionConfigResult = null;
        if (result != null) {
            situacionConfigResult = new SituacionConfig(result.get("situacionInicial"), result.get("situacionFinal"));
        }
        return situacionConfigResult;
    }

}
