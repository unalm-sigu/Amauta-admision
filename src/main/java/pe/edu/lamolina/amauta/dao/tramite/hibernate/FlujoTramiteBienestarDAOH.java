package pe.edu.lamolina.amauta.dao.tramite.hibernate;

import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.tramite.FlujoTramiteBienestar;
import pe.edu.lamolina.model.tramite.Tramite;
import pe.edu.lamolina.amauta.dao.tramite.FlujoTramiteBienestarDAO;

@Repository
public class FlujoTramiteBienestarDAOH extends AbstractEasyDAO<FlujoTramiteBienestar> implements FlujoTramiteBienestarDAO {

    public FlujoTramiteBienestarDAOH() {
        super();
        setClazz(FlujoTramiteBienestar.class);
    }

    @Override
    public FlujoTramiteBienestar findByTramite(Tramite tramite) {
        Octavia sql = new Octavia()
                .from(FlujoTramiteBienestar.class, "ftb")
                .join("tramite tr")
                .filter("tr.id", tramite);

        return find(sql);
    }

}
