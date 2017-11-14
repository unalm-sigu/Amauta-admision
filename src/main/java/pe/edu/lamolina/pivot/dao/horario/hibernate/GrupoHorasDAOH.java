package pe.edu.lamolina.pivot.dao.horario.hibernate;

import java.util.List;
import pe.albatross.zelpers.dao.AbstractDAO;
import pe.edu.lamolina.pivot.dao.horario.GrupoHorasDAO;
import pe.edu.lamolina.pivot.model.horario.GrupoHoras;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableSql;
import pe.albatross.zelpers.dao.SqlUtil;

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
    public List<GrupoHoras> allGrupoHoras(DynatableFilter filter) {
        DynatableSql sql = new DynatableSql(filter)
                .from(GrupoHoras.class, "gh")
                .leftJoin("tipoGrupoHoras tgh")
                .searchFields("codigo", "letra", "tipoCiclo", "tipoSeccion", "color")
                .orderBy("gh.id desc");
        return sql.all(getCurrentSession());
    }
}
