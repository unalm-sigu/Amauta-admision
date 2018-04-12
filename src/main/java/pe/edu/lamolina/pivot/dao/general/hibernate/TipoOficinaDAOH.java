package pe.edu.lamolina.pivot.dao.general.hibernate;

import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.general.TipoOficina;
import pe.edu.lamolina.pivot.dao.general.TipoOficinaDAO;

@Repository
public class TipoOficinaDAOH extends AbstractEasyDAO<TipoOficina> implements TipoOficinaDAO {

    public TipoOficinaDAOH() {
        super();
        setClazz(TipoOficina.class);
    }

    @Override
    public TipoOficina findByCodigo(String codigo) {
        Octavia sql = Octavia.query()
                .from(TipoOficina.class, "ofi")
                .filter("codigo", codigo);
        return find(sql);
    }
}
