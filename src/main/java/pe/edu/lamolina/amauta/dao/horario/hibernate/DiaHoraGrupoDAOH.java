package pe.edu.lamolina.amauta.dao.horario.hibernate;

import java.util.ArrayList;
import java.util.List;
import org.hibernate.Query;
import pe.edu.lamolina.amauta.dao.horario.DiaHoraGrupoDAO;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.AlumnoCiclo;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.SituacionAcademica;
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
    public List<DiaHoraGrupo> allByIdsDiasHoras(List<String> idsDiasHoras, CicloAcademico ciclo) {
        StringBuilder sql = new StringBuilder();
        sql.append(" select {dh.*},{dd.*},{hh.*},{ci.*},{gh.*} ");
        sql.append("   from hor_dia_hora_grupo as dh ");
        sql.append("   join gen_dia as dd on dd.id = dh.id_dia ");
        sql.append("   join hor_hora as hh on hh.id = dh.id_hora ");
        sql.append("   join hor_grupo_horas gh on gh.id = dh.id_grupo_horario ");
        sql.append("   join aca_ciclo_academico ci on ci.id = dh.id_ciclo_academico ");
        sql.append("   where concat(dd.id,'-',hh.id) in :IDS_DIA_HORA ");
        sql.append("     and ci.id = :CICLO ");

        Query query = getCurrentSession().createSQLQuery(sql.toString())
                .addEntity("dh", DiaHoraGrupo.class)
                .addEntity("dd", Dia.class)
                .addEntity("hh", Hora.class)
                .addEntity("ci", CicloAcademico.class)
                .addEntity("gh", GrupoHoras.class);

        query.setParameter("CICLO", ciclo.getId());
        query.setParameterList("IDS_DIA_HORA", idsDiasHoras);

        List<DiaHoraGrupo> diasHorasGrupo = new ArrayList();
        List<Object[]> rows = query.list();
        for (Object[] row : rows) {
            DiaHoraGrupo dh = (DiaHoraGrupo) row[0];
            Dia dd = (Dia) row[1];
            Hora hh = (Hora) row[2];
            CicloAcademico ci = (CicloAcademico) row[3];
            GrupoHoras gh = (GrupoHoras) row[4];

            dh.setDia(dd);
            dh.setHora(hh);
            dh.setCicloAcademico(ci);
            dd.setGrupohoras(gh);
            diasHorasGrupo.add(dh);
        }

        return diasHorasGrupo;
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
