package pe.edu.lamolina.pivot.dao.general.hibernate;

import java.util.List;
import pe.edu.lamolina.pivot.dao.general.UniversidadDAO;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.general.Universidad;

@Repository
public class UniversidadDAOH extends AbstractEasyDAO<Universidad> implements UniversidadDAO {

    public UniversidadDAOH() {
        super();
        setClazz(Universidad.class);
    }

    @Override
    public List<Universidad> allUniversidadByName(String nombre) {
        Octavia sql = Octavia.query()
                .from(Universidad.class, "uni")
                .beginBlock()
                .__().filter("uni.nombre", "like", nombre)
                .__().filter("uni.siglas", "like", nombre)
                .endBlock()
                .limit(15);
        return sql.all(getCurrentSession());
    }
}
