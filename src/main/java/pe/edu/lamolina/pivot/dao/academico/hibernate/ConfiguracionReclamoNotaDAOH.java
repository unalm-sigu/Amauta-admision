package pe.edu.lamolina.pivot.dao.academico.hibernate;

import pe.albatross.zelpers.dao.AbstractDAO;
import pe.edu.lamolina.pivot.dao.academico.ConfiguracionReclamoNotaDAO;
import org.springframework.stereotype.Repository;
import pe.edu.lamolina.model.academico.ConfiguracionReclamoNota;

@Repository
public class ConfiguracionReclamoNotaDAOH extends AbstractDAO<ConfiguracionReclamoNota> implements ConfiguracionReclamoNotaDAO {

    public ConfiguracionReclamoNotaDAOH() {
        super();
        setClazz(ConfiguracionReclamoNota.class);
    }
}
