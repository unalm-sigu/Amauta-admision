package pe.edu.lamolina.amauta.dao.academico.hibernate;

import java.util.List;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableSql;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.academico.ConvenioBeca;
import pe.edu.lamolina.amauta.dao.academico.ConvenioBecaDAO;

@Repository
public class ConvenioBecaDAOH extends AbstractEasyDAO<ConvenioBeca> implements ConvenioBecaDAO {

    public ConvenioBecaDAOH() {
        super();
        setClazz(ConvenioBeca.class);
    }

    @Override
    public List<ConvenioBeca> allByDynatable(DynatableFilter filter) {
        DynatableSql sql = new DynatableSql(filter)
                .from(ConvenioBeca.class, "cb")
                .join("institucion ins", "pais pa")
                .searchFields("cb.nombre", "pa.nombre", "ins.razonSocial", "cb.codigo", "cb.descripcion")
                .orderBy("cb.id desc");
        return sql.all(getCurrentSession());
    }

}
