package pe.edu.lamolina.pivot.dao.horario.hibernate;

import java.util.List;
import pe.albatross.zelpers.dao.AbstractDAO;
import pe.edu.lamolina.pivot.dao.horario.DiaHoraGrupoDAO;
import pe.edu.lamolina.pivot.model.horario.DiaHoraGrupo;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.edu.lamolina.pivot.model.academico.CicloAcademico;
import pe.edu.lamolina.pivot.model.horario.GrupoHoras;

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
                .left("cicloAcademico ciclo")
                .join("dia dia")
                .join("hora hora")
                .filter("dia.id", diaHoraGrupo.getDia())
                .filter("hora.id", diaHoraGrupo.getHora())
                .filter("ciclo.id", diaHoraGrupo.getCicloAcademico());
        return (DiaHoraGrupo) sql.find(getCurrentSession());
    }

    @Override
    public List<DiaHoraGrupo> allDiaHoraGrupo(GrupoHoras grupoHoras, CicloAcademico cicloAcademico) {
        Octavia sql = Octavia.query()
                .from(DiaHoraGrupo.class, "dhg")
                .join("dia dia")
                .join("hora hora")
                .join("grupoHorario gh")
                .join("cicloAcademico ciclo")
                .filter("gh.id", grupoHoras);
        return sql.all(getCurrentSession());
    }
}
