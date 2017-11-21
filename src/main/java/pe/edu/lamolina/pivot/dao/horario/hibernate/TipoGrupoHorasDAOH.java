package pe.edu.lamolina.pivot.dao.horario.hibernate;

import java.util.List;
import pe.albatross.zelpers.dao.AbstractDAO;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableSql;
import pe.edu.lamolina.pivot.dao.horario.TipoGrupoHorasDAO;
import pe.edu.lamolina.pivot.model.horario.TipoGrupoHoras;

@Repository
public class TipoGrupoHorasDAOH extends AbstractDAO<TipoGrupoHoras> implements TipoGrupoHorasDAO {

    public TipoGrupoHorasDAOH() {
        super();
        setClazz(TipoGrupoHoras.class);
    }

    @Override
    public List<TipoGrupoHoras> allTipoGrupoHoras(DynatableFilter filter) {

        DynatableSql sql = new DynatableSql(filter)
                .from(TipoGrupoHoras.class, "tgh")
                .searchFields("codigo", "tipo", "estadoGrupos", "estado")
                .orderBy("tgh.id desc");
        return sql.all(getCurrentSession());

    }

    @Override
    public TipoGrupoHoras findByCode(String codigo) {
        Octavia sql = Octavia.query()
                .from(TipoGrupoHoras.class, "tipo")
                .filter("codigo", codigo);
        return (TipoGrupoHoras) sql.find(getCurrentSession());
    }

}
