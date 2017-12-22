package pe.edu.lamolina.pivot.dao.horario.hibernate;

import java.util.List;
import pe.albatross.zelpers.dao.AbstractDAO;
import pe.edu.lamolina.pivot.dao.horario.GrupoHorasDAO;
import pe.edu.lamolina.pivot.model.horario.GrupoHoras;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableSql;
import pe.albatross.zelpers.dao.SqlUtil;
import pe.edu.lamolina.pivot.model.academico.CicloAcademico;
import pe.edu.lamolina.pivot.model.academico.GrupoSeccion;
import pe.edu.lamolina.pivot.model.horario.DiaHoraGrupo;
import pe.edu.lamolina.pivot.model.horario.TipoGrupoHoras;
import pe.edu.lamolina.pivot.zelper.enums.TipoSeccionEnum;

@Repository
public class GrupoHorasDAOH extends AbstractDAO<GrupoHoras> implements GrupoHorasDAO {

    public GrupoHorasDAOH() {
        super();
        setClazz(GrupoHoras.class);
    }

    @Override
    public GrupoHoras findByCode(String codigo) {
        SqlUtil sqlUtil = SqlUtil.creaSqlUtil("gh")
                .filter("gh.codigo", codigo);
        return find(sqlUtil);
    }

    @Override
    public GrupoHoras findGrupoHorasByCode(String codigo) {
        Octavia sql = Octavia.query()
                .from(GrupoHoras.class, "grup")
                .filter("codigo", codigo);
        return (GrupoHoras) sql.find(getCurrentSession());
    }

    @Override
    public List<GrupoHoras> allGrupoHoras(DynatableFilter filter, Long idTipoGrupo) {

        DynatableSql sql = new DynatableSql(filter)
                .from(GrupoHoras.class, "gh")
                .leftJoin("tipoGrupoHoras tgh")
                .searchFields("codigo", "letra", "tipoCiclo", "tipoSeccion", "color")
                .filter("tgh.id", idTipoGrupo)
                .orderBy("gh.id desc");
        return sql.all(getCurrentSession());
    }

    @Override
    public List<GrupoHoras> allByTipoGrupoHora(TipoGrupoHoras tipoGrupoHoras, CicloAcademico cicloAcademico) {
        Octavia sql = Octavia.query()
                .selectDistinct("gh")
                .from(DiaHoraGrupo.class, "dhg")
                .join("grupoHorario gh", "gh.tipoGrupoHoras tgh", "cicloAcademico ca")
                .filter("tgh.id", tipoGrupoHoras)
                .filter("ca.id", cicloAcademico);
        return sql.all(getCurrentSession());
    }

    @Override
    public GrupoHoras find(GrupoHoras grupoHoras) {
        Octavia sql = Octavia.query()
                .from(GrupoHoras.class, "grup")
                .join("diaHoraGrupo dhg")
                .leftJoin("tipoGrupoHoras tgh")
                .filter("grup.id", grupoHoras);
        return (GrupoHoras) sql.find(getCurrentSession());
    }

    @Override
    public List<GrupoHoras> allZetasByDynatable(pe.albatross.octavia.dynatable.DynatableFilter filter,
            TipoGrupoHoras tipoGrupoHoras,
            CicloAcademico cicloAcademico) {

        DynatableSql sql = new DynatableSql(filter)
                .selectDistinct("gh")
                .from(DiaHoraGrupo.class, "dhg")
                .join("grupoHorario gh", "gh.tipoGrupoHoras tgh", "cicloAcademico ca")
                .filter("tgh.id", tipoGrupoHoras);
        //   .filter("ca.id", cicloAcademico)
        // .searchFields("cu.nombre")
        //  .orderBy("gs.id desc");
        sql.beginRelativeFilters();
        // this.setGrupoAnexo(filter, sql);
        return sql.all(getCurrentSession());
    }

}
