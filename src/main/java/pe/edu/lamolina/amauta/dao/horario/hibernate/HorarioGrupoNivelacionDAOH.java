package pe.edu.lamolina.amauta.dao.horario.hibernate;

import java.util.List;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.amauta.dao.horario.HorarioGrupoNivelacionDAO;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.horario.GrupoNivelacion;
import pe.edu.lamolina.model.horario.HorarioGrupoNivelacion;

@Repository
public class HorarioGrupoNivelacionDAOH extends AbstractEasyDAO<HorarioGrupoNivelacion> implements HorarioGrupoNivelacionDAO {

    public HorarioGrupoNivelacionDAOH() {
        super();
        setClazz(HorarioGrupoNivelacion.class);
    }

    @Override
    public List<HorarioGrupoNivelacion> allByGrupo(GrupoNivelacion grupo) {
        Octavia sql = Octavia.query()
                .from(HorarioGrupoNivelacion.class, "hgn")
                .join("dia", "hora", "grupoNivelacion gn", "cicloAcademico ci")
                .filter("gn.id", grupo);
        return all(sql);
    }

    @Override
    public List<HorarioGrupoNivelacion> allByGrupoCiclo(GrupoNivelacion grupo, CicloAcademico ciclo) {
        Octavia sql = Octavia.query()
                .from(HorarioGrupoNivelacion.class, "hgn")
                .join("dia", "hora", "grupoNivelacion gn", "cicloAcademico ci")
                .filter("ci.id", ciclo)
                .filter("gn.id", grupo);
        return all(sql);
    }

    @Override
    public List<HorarioGrupoNivelacion> allByGruposCiclo(List<GrupoNivelacion> grupos, CicloAcademico ciclo) {
        Octavia sql = Octavia.query()
                .from(HorarioGrupoNivelacion.class, "hgn")
                .join("dia", "hora", "grupoNivelacion gn", "cicloAcademico ci")
                .filter("ci.id", ciclo)
                .in("gn.id", grupos);
        return all(sql);
    }

    @Override
    public List<HorarioGrupoNivelacion> allRegularByCiclo(CicloAcademico ciclo) {
        Octavia sql = Octavia.query()
                .from(HorarioGrupoNivelacion.class, "hgn")
                .join("dia", "hora", "grupoNivelacion gn", "cicloAcademico ci")
                .filter("ci.id", ciclo)
                .filter("gn.tipo","REGULAR");
        return all(sql);
    }

    @Override
    public List<HorarioGrupoNivelacion> allByCiclo(CicloAcademico ciclo) {
        Octavia sql = Octavia.query()
                .from(HorarioGrupoNivelacion.class, "hgn")
                .join("dia", "hora", "grupoNivelacion gn", "cicloAcademico ci")
                .filter("ci.id", ciclo);
        return all(sql);
    }
}
