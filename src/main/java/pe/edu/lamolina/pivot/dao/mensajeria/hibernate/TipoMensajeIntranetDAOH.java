package pe.edu.lamolina.pivot.dao.mensajeria.hibernate;

import java.util.List;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableSql;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.academico.TipoMensajeIntranet;
import pe.edu.lamolina.pivot.dao.mensajeria.TipoMensajeIntranetDAO;

@Repository
public class TipoMensajeIntranetDAOH extends AbstractEasyDAO<TipoMensajeIntranet> implements TipoMensajeIntranetDAO {

    public TipoMensajeIntranetDAOH() {
        super();
        setClazz(TipoMensajeIntranet.class);
    }

    @Override
    public List<TipoMensajeIntranet> allByDynatable(DynatableFilter filter) {
        DynatableSql sql = new DynatableSql(filter)
                .from(TipoMensajeIntranet.class, "tmi")
                .searchFields("tmi.codigo", "tmi.nombre", "tmi.contenido", "tmi.tipoPantalla", "tmi.boton")
                .orderBy("tmi.id desc");
        return sql.all(getCurrentSession());

    }
}
