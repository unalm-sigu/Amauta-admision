package pe.edu.lamolina.pivot.dao.general.hibernate;

import org.springframework.stereotype.Repository;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.tramite.ConfiguracionFirmaDocumento;
import pe.edu.lamolina.pivot.dao.general.ConfiguracionFirmaDocumentoDAO;

@Repository
public class ConfiguracionFirmaDocumentoDAOH extends AbstractEasyDAO<ConfiguracionFirmaDocumento> implements ConfiguracionFirmaDocumentoDAO {

    public ConfiguracionFirmaDocumentoDAOH() {
        super();
        setClazz(ConfiguracionFirmaDocumento.class);
    }

}
