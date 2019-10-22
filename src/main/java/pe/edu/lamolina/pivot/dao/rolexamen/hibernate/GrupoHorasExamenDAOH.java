package pe.edu.lamolina.pivot.dao.rolexamen.hibernate;

import java.util.List;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableSql;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.horario.GrupoHoras;
import pe.edu.lamolina.model.rolexamen.GrupoHorasExamen;
import pe.edu.lamolina.model.rolexamen.RolExamenes;
import pe.edu.lamolina.pivot.dao.rolexamen.GrupoHorasExamenDAO;

@Repository
public class GrupoHorasExamenDAOH extends AbstractEasyDAO<GrupoHorasExamen> implements GrupoHorasExamenDAO {

    public GrupoHorasExamenDAOH() {
        super();
        setClazz(GrupoHorasExamen.class);
    }

    @Override
    public GrupoHorasExamen find(long id) {
        Octavia sql = Octavia.query()
                .from(GrupoHorasExamen.class, "ghe")
                .join("rolExamenes re", "grupoHoras gh")
                .leftJoin("horaInicio", "horaFin")
                .filter("ghe.id", id);
        return find(sql);
    }

    @Override
    public List<GrupoHorasExamen> allByRolExamenes(RolExamenes rolExamenes) {
        Octavia sql = Octavia.query()
                .from(GrupoHorasExamen.class, "ghe")
                .join("rolExamenes re", "grupoHoras gh", "horaInicio", "horaFin")
                .filter("re.id", rolExamenes)
                .orderBy("gh.codigo");
        return all(sql);
    }

    @Override
    public List<GrupoHorasExamen> allByRolExamenesAndDyna(RolExamenes rolExamenes, DynatableFilter filter) {
        DynatableSql sql = new DynatableSql(filter)
                .from(GrupoHorasExamen.class, "ghe")
                .join("rolExamenes re", "grupoHoras gh")
                .leftJoin("horaInicio", "horaFin")
                .searchFields("gh.codigo", "gh.letra")
                .filter("re.id", rolExamenes)
                .orderBy("gh.letra");
        return all(sql);
    }

    @Override
    public GrupoHorasExamen findByRolExamenAndGrupoHoras(RolExamenes rolExamenes, GrupoHoras gruposHora) {
        Octavia sql = Octavia.query()
                .from(GrupoHorasExamen.class, "ghe")
                .join("rolExamenes re", "grupoHoras gh", "horaInicio", "horaFin")
                .filter("re.id", rolExamenes)
                .filter("gh.id", gruposHora);
        return find(sql);
    }

    @Override
    public void updateFechaExamen(GrupoHorasExamen grupoHorasExamen) {
        Octavia octavia = Octavia.update(GrupoHorasExamen.class);
        octavia.set(grupoHorasExamen, "fecha");
        octavia.set(grupoHorasExamen, "dia");
        octavia.set(grupoHorasExamen, "horaInicio");
        octavia.set(grupoHorasExamen, "horaFin");
        this.update(octavia);
    }

    @Override
    public void updateVerificado(GrupoHorasExamen grupoHorasExamen) {
        Octavia octavia = Octavia.update(GrupoHorasExamen.class);
        octavia.set(grupoHorasExamen, "verificado");
        this.update(octavia);
    }
}
