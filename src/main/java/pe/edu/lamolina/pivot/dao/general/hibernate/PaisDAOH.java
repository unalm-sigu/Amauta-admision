package pe.edu.lamolina.pivot.dao.general.hibernate;

import java.util.List;
import pe.albatross.zelpers.dao.AbstractDAO;
import pe.edu.lamolina.pivot.dao.general.PaisDAO;
import pe.edu.lamolina.pivot.model.general.Pais;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;

@Repository
public class PaisDAOH extends AbstractDAO<Pais> implements PaisDAO {

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
