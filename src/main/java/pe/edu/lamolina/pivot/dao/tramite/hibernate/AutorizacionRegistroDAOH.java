package pe.edu.lamolina.pivot.dao.tramite.hibernate;

import pe.edu.lamolina.pivot.dao.tramite.AutorizacionRegistroDAO;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.tramite.AutorizacionRegistro;
import pe.edu.lamolina.model.tramite.Tramite;

@Repository
public class AutorizacionRegistroDAOH extends AbstractEasyDAO<AutorizacionRegistro> implements AutorizacionRegistroDAO {

    public AutorizacionRegistroDAOH() {
        super();
        setClazz(AutorizacionRegistro.class);
    }

    @Override
    public AutorizacionRegistro findByTramite(Tramite tramite) {
        Octavia sql = Octavia.query()
                .from(AutorizacionRegistro.class, "sec")
                .join("tramite tra")
                .filter("tra.id", tramite);
        return find(sql);
    }
}
