package pe.edu.lamolina.amauta.dao.horario.hibernate;

import java.util.List;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableSql;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.amauta.dao.horario.GrupoNivelacionDAO;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.horario.GrupoNivelacion;
import pe.edu.lamolina.model.horario.HorarioGrupoNivelacion;

@Repository
public class GrupoNivelacionDAOH extends AbstractEasyDAO<GrupoNivelacion> implements GrupoNivelacionDAO {

    public GrupoNivelacionDAOH() {
        super();
        setClazz(GrupoNivelacion.class);
    }

    @Override
    public List<GrupoNivelacion> allByDynatable(DynatableFilter filter) {
        DynatableSql sql = new DynatableSql(filter)
                .from(GrupoNivelacion.class, "gn")
                .searchFields("gn.codigo")
                .orderBy("gn.orden", "gn.codigo");

        return all(sql);
    }

    @Override
    public GrupoNivelacion findByCodigo(String codigo) {
        Octavia sql = Octavia.query()
                .from(GrupoNivelacion.class, "gn")
                .filter("gn.codigo", codigo);
        return find(sql);
    }

    @Override
    public List<GrupoNivelacion> allByCiclo(CicloAcademico ciclo) {
        Octavia sql = Octavia.query()
                .selectDistinct("gn")
                .from(HorarioGrupoNivelacion.class, "hgn")
                .join("cicloAcademico ci", "grupoNivelacion gn")
                .filter("ci.id", ciclo)
                .orderBy("gn.orden", "gn.codigo");

        return all(sql);
    }
}
