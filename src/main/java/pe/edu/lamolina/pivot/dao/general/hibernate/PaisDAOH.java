package pe.edu.lamolina.pivot.dao.general.hibernate;

import java.util.List;
import pe.edu.lamolina.pivot.dao.general.PaisDAO;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.general.Pais;

@Repository
public class PaisDAOH extends AbstractEasyDAO<Pais> implements PaisDAO {

    public PaisDAOH() {
        super();
        setClazz(Pais.class);
    }

    @Override
    public List<Pais> allPaisesByName(String nombre) {
        Octavia sql = Octavia.query()
                .from(Pais.class, "pa")
                .beginBlock()
                .__().filter("pa.codigo", "like", nombre)
                .__().filter("pa.nombre", "like", nombre)
                .endBlock()
                .limit(15);
        return sql.all(getCurrentSession());
    }

}
