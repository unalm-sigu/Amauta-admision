package pe.edu.lamolina.pivot.dao.rolexamen.hibernate;

import java.util.List;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.rolexamen.FechaHoraGrupoExamen;
import pe.edu.lamolina.model.rolexamen.GrupoHorasExamen;
import pe.edu.lamolina.model.rolexamen.RolExamenes;
import pe.edu.lamolina.model.rolexamen.SemanaExamen;
import pe.edu.lamolina.pivot.dao.rolexamen.FechaHoraGrupoExamenDAO;

@Repository
public class FechaHoraGrupoExamenDAOH extends AbstractEasyDAO<FechaHoraGrupoExamen> implements FechaHoraGrupoExamenDAO {

    public FechaHoraGrupoExamenDAOH() {
        super();
        setClazz(FechaHoraGrupoExamen.class);
    }

    @Override
    public FechaHoraGrupoExamen find(long id) {
        Octavia sql = Octavia.query()
                .from(FechaHoraGrupoExamen.class, "fhg")
                .join("grupoHorasExamen ghe", "semanaExamen se", "dia d", "hora h")
                .filter("fhg.id", id);
        return find(sql);
    }

    @Override
    public List<FechaHoraGrupoExamen> allByGrupoHorasExamen(GrupoHorasExamen grupoHorasExamen) {
        Octavia sql = Octavia.query()
                .from(FechaHoraGrupoExamen.class, "fhg")
                .join("grupoHorasExamen ghe", "semanaExamen se", "dia d", "hora h")
                .filter("ghe.id", grupoHorasExamen);
        return all(sql);
    }

    @Override
    public List<FechaHoraGrupoExamen> allByGrupoHorasExamen(List<GrupoHorasExamen> gruposHorasExamen) {
        Octavia sql = Octavia.query()
                .from(FechaHoraGrupoExamen.class, "fhg")
                .join("grupoHorasExamen ghe", "semanaExamen se", "dia d", "hora h")
                .in("ghe.id", gruposHorasExamen);
        return all(sql);
    }

    @Override
    public List<FechaHoraGrupoExamen> allByGrupoHorasExamenOrderByDiaHora(List<GrupoHorasExamen> gruposHorasExamen) {
        Octavia sql = Octavia.query()
                .from(FechaHoraGrupoExamen.class, "fhg")
                .join("grupoHorasExamen ghe", "semanaExamen se", "dia d", "hora h")
                .orderBy("d.numeroDia", "h.numero")
                .in("ghe.id", gruposHorasExamen);
        return all(sql);
    }

    @Override
    public List<FechaHoraGrupoExamen> allByGrupoHorasExamenOrderByDiaHora(GrupoHorasExamen grupoHorasExamen) {
        Octavia sql = Octavia.query()
                .from(FechaHoraGrupoExamen.class, "fhg")
                .join("grupoHorasExamen ghe", "semanaExamen se", "dia d", "hora h")
                .orderBy("d.numeroDia", "h.numero")
                .filter("ghe.id", grupoHorasExamen);
        return all(sql);
    }

    @Override
    public List<FechaHoraGrupoExamen> allBySemanaExamen(SemanaExamen semanaExamen) {
        Octavia sql = Octavia.query()
                .from(FechaHoraGrupoExamen.class, "fhg")
                .join("grupoHorasExamen ghe", "semanaExamen se", "dia d", "hora h")
                .join("ghe.grupoHoras gh", "gh.tipoGrupoHoras tgh")
                .filter("se.id", semanaExamen);
        return all(sql);
    }

    @Override
    public List<FechaHoraGrupoExamen> allBySemanasExamen(List<SemanaExamen> semanasExamen) {
        Octavia sql = Octavia.query()
                .from(FechaHoraGrupoExamen.class, "fhg")
                .join("grupoHorasExamen ghe", "semanaExamen se", "dia d", "hora h")
                .join("ghe.grupoHoras gh", "gh.tipoGrupoHoras tgh")
                .in("se.id", semanasExamen);
        return all(sql);
    }

    @Override
    public List<FechaHoraGrupoExamen> allByRolExamens(RolExamenes rolExamenes) {
        Octavia sql = Octavia.query()
                .from(FechaHoraGrupoExamen.class, "fhg")
                .join("grupoHorasExamen ghe", "semanaExamen se", "dia d", "hora h")
                .join("se.rolExamenes rex")
                .filter("rex.id", rolExamenes);
        return all(sql);
    }

    @Override
    public List<FechaHoraGrupoExamen> allBySemanaExamenAndGrupoHoraSecc(SemanaExamen semanaExamen, List<Long> ids) {
        Octavia sql = Octavia.query()
                .from(FechaHoraGrupoExamen.class, "fhg")
                .join("grupoHorasExamen ghe", "semanaExamen se", "dia d", "hora h")
                .join("ghe.grupoHoras gh", "gh.tipoGrupoHoras tgh")
                .in("ghe.id", ids)
                .filter("se.id", semanaExamen);
        return all(sql);
    }
}
