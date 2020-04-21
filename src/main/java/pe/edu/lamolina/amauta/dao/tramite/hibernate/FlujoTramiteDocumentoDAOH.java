package pe.edu.lamolina.amauta.dao.tramite.hibernate;

import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.tramite.FlujoTramiteBienestar;
import pe.edu.lamolina.model.tramite.FlujoTramiteDocumento;
import pe.edu.lamolina.model.tramite.Tramite;
import pe.edu.lamolina.amauta.dao.tramite.FlujoTramiteDocumentoDAO;

@Repository
public class FlujoTramiteDocumentoDAOH extends AbstractEasyDAO<FlujoTramiteDocumento> implements FlujoTramiteDocumentoDAO {

    public FlujoTramiteDocumentoDAOH() {
        super();
        setClazz(FlujoTramiteDocumento.class);
    }

    @Override
    public FlujoTramiteDocumento findByTramite(Tramite tramite) {
        Octavia sql = new Octavia()
                .from(FlujoTramiteDocumento.class, "ftb")
                .join("tramite tr")
                .filter("tr.id", tramite);

        return find(sql);
    }

}
