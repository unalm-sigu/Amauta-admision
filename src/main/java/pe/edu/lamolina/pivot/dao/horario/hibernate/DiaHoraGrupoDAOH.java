package pe.edu.lamolina.pivot.dao.horario.hibernate;

import java.util.List;
import org.hibernate.Query;
import pe.edu.lamolina.pivot.dao.horario.DiaHoraGrupoDAO;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.academico.CicloAcademico;
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
    public DiaHoraGrupo findByCicloAcademicoGrupoHorasDiaHora(CicloAcademico cicloAcademico, GrupoHoras grupo, Dia dia, Hora hora) {
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
    public List<DiaHoraGrupo> allDiaHoraGrupoByGrupo(GrupoHoras grupoHoras, CicloAcademico cicloAcademico) {
        Octavia sql = Octavia.query()
                .from(DiaHoraGrupo.class, "dhg")
                .join("grupoHorario gh", "cicloAcademico ciclo", "dia dia", "hora hora")
                .filter("gh.id", grupoHoras)
                .filter("ciclo.id", cicloAcademico);

        return all(sql);
    }

    @Override
    public List<DiaHoraGrupo> allDiaHoraGrupo(List<GrupoHoras> grupos) {
        Octavia sql = Octavia.query()
                .from(DiaHoraGrupo.class, "dhg")
                .join("grupoHorario gh", "cicloAcademico ciclo", "dia dia", "hora hora")
                .in("gh.id", grupos);

        return all(sql);
    }

    @Override
    public List<DiaHoraGrupo> allDiaHoraGrupoByTipo(TipoGrupoHoras tipoGrupoHoras, CicloAcademico cicloAcademico) {
        Octavia sql = Octavia.query()
                .from(DiaHoraGrupo.class, "dhg")
                .join("grupoHorario gh", "cicloAcademico ciclo", "dia dia", "hora hora", "gh.tipoGrupoHoras tgh")
                .filter("tgh.id", tipoGrupoHoras)
                .filter("ciclo.id", cicloAcademico);

        return all(sql);
    }

    @Override
    public List<DiaHoraGrupo> allDiaHoraGrupo(List<GrupoHoras> grupos, CicloAcademico cicloAcademico) {
        Octavia sql = Octavia.query()
                .from(DiaHoraGrupo.class, "dhg")
                .join("grupoHorario gh", "gh.tipoGrupoHoras tgh", "cicloAcademico ciclo", "dia dia", "hora hora")
                .in("gh.id", grupos)
                .filter("ciclo.id", cicloAcademico)
                .orderBy("dia.numeroDia", "hora.numero");

        return all(sql);
    }

}
