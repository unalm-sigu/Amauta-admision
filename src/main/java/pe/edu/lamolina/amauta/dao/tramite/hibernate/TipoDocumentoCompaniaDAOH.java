package pe.edu.lamolina.amauta.dao.tramite.hibernate;

import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.enums.TipoDocumentoCompaniaEnum;
import pe.edu.lamolina.model.general.TipoDocumentoCompania;
import pe.edu.lamolina.amauta.dao.tramite.TipoDocumentoCompaniaDAO;

@Repository
public class TipoDocumentoCompaniaDAOH extends AbstractEasyDAO<TipoDocumentoCompania> implements TipoDocumentoCompaniaDAO {

    public TipoDocumentoCompaniaDAOH() {
        super();
        setClazz(TipoDocumentoCompania.class);
    }

    @Override
    public TipoDocumentoCompania findByCodigo(TipoDocumentoCompaniaEnum codigo) {
        Octavia sql = Octavia.query()
                .from(TipoDocumentoCompania.class, "tdc")
                .filter("tdc.codigo", codigo);
        return find(sql);
    }

}
