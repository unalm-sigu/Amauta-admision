package pe.edu.lamolina.pivot.dao.general.hibernate;

import java.util.Arrays;
import java.util.List;
import pe.edu.lamolina.pivot.dao.general.TipoDocIdentidadDAO;
import pe.edu.lamolina.pivot.model.general.TipoDocIdentidad;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.zelpers.dao.AbstractDAO;

@Repository
public class TipoDocIdentidadDAOH extends AbstractDAO<TipoDocIdentidad> implements TipoDocIdentidadDAO {

    public TipoDocIdentidadDAOH() {
        super();
        setClazz(TipoDocIdentidad.class);
    }

    @Override
    public List<TipoDocIdentidad> allForPersonaNatural() {
        Octavia sql = Octavia.query()
                .from(TipoDocIdentidad.class, "td")
                .in("td.simbolo", Arrays.asList("DNI", "CEX", "CE", "PAS"));
        return sql.all(getCurrentSession());
    }
}
