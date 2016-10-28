package pe.edu.lamolina.pivot.dao.academico.hibernate;

import pe.albatross.zelpers.dao.AbstractDAO;
import pe.edu.lamolina.pivot.dao.academico.ConfiguracionReclamoNotaDAO;
import pe.edu.lamolina.pivot.model.academico.ConfiguracionReclamoNota;
import org.springframework.stereotype.Repository;

@Repository
public class ConfiguracionReclamoNotaDAOH extends AbstractDAO<ConfiguracionReclamoNota> implements ConfiguracionReclamoNotaDAO {

    public ConfiguracionReclamoNotaDAOH() {
        super();
        setClazz(ConfiguracionReclamoNota.class);
    }
}

