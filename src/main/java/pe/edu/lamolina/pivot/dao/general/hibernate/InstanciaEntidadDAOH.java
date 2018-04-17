package pe.edu.lamolina.pivot.dao.general.hibernate;

import java.util.List;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.general.InstanciaEntidad;
import pe.edu.lamolina.pivot.dao.general.InstanciaEntidadDAO;

@Repository
public class InstanciaEntidadDAOH extends AbstractEasyDAO<InstanciaEntidad> implements InstanciaEntidadDAO {

    public InstanciaEntidadDAOH() {
        super();
        setClazz(InstanciaEntidad.class);
    }

    @Override
    public List<InstanciaEntidad> all() {
        Octavia sql = Octavia.query(InstanciaEntidad.class, "ine")
                .join("oficina ofi");
        return all(sql);
    }
}
