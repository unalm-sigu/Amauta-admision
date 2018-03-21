package pe.edu.lamolina.pivot.dao.encuesta.hibernate;

import org.springframework.stereotype.Repository;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.encuesta.ConfiguraEncuesta;
import pe.edu.lamolina.pivot.dao.encuesta.ConfiguraEncuestaDAO;

@Repository
public class ConfiguraEncuestaDAOH extends AbstractEasyDAO<ConfiguraEncuesta> implements ConfiguraEncuestaDAO {

    public ConfiguraEncuestaDAOH() {
        super();
        setClazz(ConfiguraEncuesta.class);
    }

}
