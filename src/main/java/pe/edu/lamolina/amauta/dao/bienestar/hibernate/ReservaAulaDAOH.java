package pe.edu.lamolina.amauta.dao.bienestar.hibernate;

import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableSql;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.tramite.ReservaAula;
import pe.edu.lamolina.amauta.dao.bienestar.ReservaAulaDAO;
import static pe.edu.lamolina.model.enums.EstadoMatriculaEnum.MAT;
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
    public void updateColumns(ReservaAula reservaAula, String... columns) {
        Octavia sql = Octavia.update(ReservaAula.class, "re");
        for (String column : columns) {
            sql.set(reservaAula, column);
        }
        this.update(sql);
    }
}
