package pe.edu.lamolina.pivot.dao.horario.hibernate;

import java.util.List;
import pe.albatross.zelpers.dao.AbstractDAO;
import pe.edu.lamolina.pivot.dao.horario.DiaHoraGrupoDAO;
import pe.edu.lamolina.pivot.model.horario.DiaHoraGrupo;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.edu.lamolina.pivot.model.academico.CicloAcademico;
import pe.edu.lamolina.pivot.model.horario.GrupoHoras;
import pe.edu.lamolina.pivot.model.horario.TipoGrupoHoras;

@Repository
public class DiaHoraGrupoDAOH extends AbstractDAO<DiaHoraGrupo> implements DiaHoraGrupoDAO {
    
    public DiaHoraGrupoDAOH() {
        super();
        setClazz(DiaHoraGrupo.class);
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
        return (DiaHoraGrupo) sql.find(getCurrentSession());
    }
    
    @Override
    public List<DiaHoraGrupo> allDiaHoraGrupoByGrupo(GrupoHoras grupoHoras, CicloAcademico cicloAcademico) {
        Octavia sql = Octavia.query()
                .from(DiaHoraGrupo.class, "dhg")
                .join("grupoHorario gh", "cicloAcademico ciclo", "dia dia", "hora hora")
                .filter("gh.id", grupoHoras)
                .filter("ciclo.id", cicloAcademico);
        return sql.all(getCurrentSession());
    }
    
    @Override
    public List<DiaHoraGrupo> allDiaHoraGrupo(List<GrupoHoras> grupos) {
        Octavia sql = Octavia.query()
                .from(DiaHoraGrupo.class, "dhg")
                .join("grupoHorario gh", "cicloAcademico ciclo", "dia dia", "hora hora")
                .in("gh.id", grupos);
        return sql.all(getCurrentSession());
    }
    
    @Override
    public List<DiaHoraGrupo> allDiaHoraGrupo(List<GrupoHoras> grupos, CicloAcademico cicloAcademico) {
        Octavia sql = Octavia.query()
                .from(DiaHoraGrupo.class, "dhg")
                .join("grupoHorario gh", "cicloAcademico ciclo", "dia dia", "hora hora")
                .in("gh.id", grupos)
                .filter("ciclo.id", cicloAcademico)
                .orderBy("dia.numeroDia", "hora.numero");
        return sql.all(getCurrentSession());
    }
    
    @Override
    public List<DiaHoraGrupo> allDiaHoraGrupoByTipo(TipoGrupoHoras tipoGrupoHoras, CicloAcademico cicloAcademico) {
        Octavia sql = Octavia.query()
                .from(DiaHoraGrupo.class, "dhg")
                .join("grupoHorario gh", "cicloAcademico ciclo", "dia dia", "hora hora", "gh.tipoGrupoHoras tgh")
                .filter("tgh.id", tipoGrupoHoras)
                .filter("ciclo.id", cicloAcademico);
        return sql.all(getCurrentSession());
    }
}
