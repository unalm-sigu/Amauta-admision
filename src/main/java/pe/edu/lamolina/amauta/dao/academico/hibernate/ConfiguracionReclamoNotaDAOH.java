package pe.edu.lamolina.amauta.dao.academico.hibernate;

import pe.edu.lamolina.amauta.dao.academico.ConfiguracionReclamoNotaDAO;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.academico.ConfiguracionReclamoNota;

@Repository
public class ConfiguracionReclamoNotaDAOH extends AbstractEasyDAO<ConfiguracionReclamoNota> implements ConfiguracionReclamoNotaDAO {

    public ConfiguracionReclamoNotaDAOH() {
        super();
        setClazz(ConfiguracionReclamoNota.class);
    }
}
