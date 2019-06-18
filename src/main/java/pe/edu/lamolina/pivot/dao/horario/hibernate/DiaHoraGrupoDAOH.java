package pe.edu.lamolina.pivot.dao.horario.hibernate;

import java.util.List;
import org.hibernate.Query;
import pe.edu.lamolina.pivot.dao.horario.DiaHoraGrupoDAO;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.enums.TipoGrupoHorasEnum;
import pe.edu.lamolina.model.general.Dia;
import pe.edu.lamolina.model.horario.DiaHoraGrupo;
import pe.edu.lamolina.model.horario.GrupoHoras;
import pe.edu.lamolina.model.horario.Hora;
import pe.edu.lamolina.model.horario.TipoGrupoHoras;

@Repository
public class DiaHoraGrupoDAOH extends AbstractEasyDAO<DiaHoraGrupo> implements DiaHoraGrupoDAO {

    public DiaHoraGrupoDAOH() {
        super();
        setClazz(DiaHoraGrupo.class);
    }

    @Override
    public void deleteAllByNotInList(List<DiaHoraGrupo> horarios) {
        StringBuilder sql = new StringBuilder();

        sql.append("delete DiaHoraGrupo dag where dag not in :HORARIOS");

        Query query = getCurrentSession().createQuery(sql.toString());

        query.setParameter("HORARIOS", horarios);

        query.executeUpdate();
    }

    @Override
    public void deleteAllInList(List<DiaHoraGrupo> diaHoraGrupos) {
        if (diaHoraGrupos.isEmpty()) {
            return;
        }

        String sql = "delete DiaHoraGrupo dh where dh in :GRUPOS";
        Query query = getCurrentSession().createQuery(sql);
        query.setParameterList("GRUPOS", diaHoraGrupos);
        query.executeUpdate();
    }

    @Override
    public DiaHoraGrupo findByCicloGrupoDiaHora(CicloAcademico cicloAcademico, GrupoHoras grupo, Dia dia, Hora hora) {
        Octavia sql = Octavia.query()
                .from(DiaHoraGrupo.class, "dhg")
                .join("grupoHorario gh", "cicloAcademico ciclo", "dia dia", "hora hora")
                .filter("dia.id", dia)
                .filter("hora.id", hora)
                .filter("gh.id", grupo)
                .filter("ciclo.id", cicloAcademico);

        return find(sql);
    }

    @Override
    public DiaHoraGrupo findByDiaHoraCiclo(DiaHoraGrupo diaHoraGrupo) {
        Octavia sql = Octavia.query()
                .from(DiaHoraGrupo.class, "dhg")
                .join("grupoHorario gh", "cicloAcademico ciclo", "dia dia", "hora hora")
                .filter("dia.id", diaHoraGrupo.getDia())
                .filter("hora.id", diaHoraGrupo.getHora())
                .filter("gh.id", diaHoraGrupo.getGrupoHorario())
                .filter("ciclo.id", diaHoraGrupo.getCicloAcademico());

        return find(sql);
    }

    @Override
    public List<DiaHoraGrupo> allByGrupoCiclo(GrupoHoras grupoHoras, CicloAcademico cicloAcademico) {
        Octavia sql = Octavia.query()
                .from(DiaHoraGrupo.class, "dhg")
                .join("grupoHorario gh", "cicloAcademico ciclo", "dia dia", "hora hora")
                .filter("gh.id", grupoHoras)
                .filter("ciclo.id", cicloAcademico);

        return all(sql);
    }

    @Override
    public List<DiaHoraGrupo> allByTipoGpoCiclo(TipoGrupoHoras tipoGrupoHoras, CicloAcademico cicloAcademico) {
        Octavia sql = Octavia.query()
                .from(DiaHoraGrupo.class, "dhg")
                .join("grupoHorario gh", "cicloAcademico ciclo", "dia dia", "hora hora", "gh.tipoGrupoHoras tgh")
                .filter("tgh.id", tipoGrupoHoras)
                .filter("ciclo.id", cicloAcademico);

        return all(sql);
    }

    @Override
    public List<DiaHoraGrupo> allByGruposCiclo(List<GrupoHoras> grupos, CicloAcademico cicloAcademico) {
        Octavia sql = Octavia.query()
                .from(DiaHoraGrupo.class, "dhg")
                .join("grupoHorario gh", "gh.tipoGrupoHoras tgh", "cicloAcademico ciclo", "dia dia", "hora hora")
                .in("gh.id", grupos)
                .filter("ciclo.id", cicloAcademico)
                .orderBy("dia.numeroDia", "hora.numero");

        return all(sql);
    }

    public void adad(CicloAcademico cicloAcademico) {
        Octavia sql = Octavia.query()
                .select("pc.id", "count(cc)")
                .from(DiaHoraGrupo.class, "dhg")
                .join("grupoHorario gh", "cicloAcademico ca", "dia d", "hora h")
                .filter("ca.id", cicloAcademico)
                .groupBy("gh.id", "d.id");

    }

//    @Override
//    public List<DiaHoraGrupo> allByGrupo(GrupoHoras grupo, CicloAcademico ciclo) {
//        Octavia sql = Octavia.query()
//                .from(DiaHoraGrupo.class, "dhg")
//                .join("grupoHorario gh", "gh.tipoGrupoHoras tgh", "cicloAcademico ciclo", "dia dia", "hora hora")
//                .filter("gh.id", grupo)
//                .filter("ciclo.id", ciclo);
//
//        return all(sql);
//    }
//    
    @Override
    public List<DiaHoraGrupo> allByCiclo(CicloAcademico ciclo) {
        Octavia sql = Octavia.query()
                .from(DiaHoraGrupo.class, "dhg")
                .join("grupoHorario gh", "gh.tipoGrupoHoras tgh", "cicloAcademico ciclo", "dia dia", "hora hora")
                .filter("ciclo.id", ciclo);

        return all(sql);
    }

    @Override
    public List<DiaHoraGrupo> allByCicloAndTipoCiclo(CicloAcademico cicloDestino) {
        Octavia sql = Octavia.query()
                .from(DiaHoraGrupo.class, "dhg")
                .join("grupoHorario gh", "gh.tipoGrupoHoras tgh", "cicloAcademico ciclo", "dia dia", "hora hora")
                .filter("ciclo.id", cicloDestino);

        return all(sql);
    }

    @Override
    public List<DiaHoraGrupo> allByDiaHoraGrupo(List<DiaHoraGrupo> diaHoraGrupo) {
        Octavia sql = Octavia.query()
                .from(DiaHoraGrupo.class, "dhg")
                .join("grupoHorario gh", "cicloAcademico ciclo", "dia dia", "hora hora")
                .in("dhg.id", diaHoraGrupo);

        return all(sql);
    }

    @Override
    public List<DiaHoraGrupo> allByTipoGpoEnumCiclo(TipoGrupoHorasEnum tipoGrupoHorasEnum, CicloAcademico cicloAcademico) {
        Octavia sql = Octavia.query()
                .from(DiaHoraGrupo.class, "dhg")
                .join("grupoHorario gh", "cicloAcademico ciclo", "dia dia", "hora hora", "gh.tipoGrupoHoras tgh")
                .filter("tgh.tipo", tipoGrupoHorasEnum)
                .filter("ciclo.id", cicloAcademico);

        return all(sql);

    }

}
