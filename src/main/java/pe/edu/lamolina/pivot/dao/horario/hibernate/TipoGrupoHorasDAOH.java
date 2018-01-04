package pe.edu.lamolina.pivot.dao.horario.hibernate;

import java.util.List;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableSql;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.horario.TipoGrupoHoras;
import pe.edu.lamolina.pivot.dao.horario.TipoGrupoHorasDAO;

@Repository
public class TipoGrupoHorasDAOH extends AbstractEasyDAO<TipoGrupoHoras> implements TipoGrupoHorasDAO {

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

        return all(sql);

    }

    @Override
    public TipoGrupoHoras findByCode(String codigo) {
        Octavia sql = Octavia.query()
                .from(TipoGrupoHoras.class, "tipo")
                .filter("codigo", codigo);

        return find(sql);
    }

}
