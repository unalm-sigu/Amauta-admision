package pe.edu.lamolina.pivot.dao.vacantes.hibernate;

import pe.albatross.zelpers.dao.AbstractDAO;
import pe.edu.lamolina.pivot.dao.vacantes.ConfiguraVacanteModalidadDAO;
import pe.edu.lamolina.pivot.model.vacantes.ConfiguraVacanteModalidad;
import org.springframework.stereotype.Repository;

@Repository
public class ConfiguraVacanteModalidadDAOH extends AbstractDAO<ConfiguraVacanteModalidad> implements ConfiguraVacanteModalidadDAO {

    public ConfiguraVacanteModalidadDAOH() {
        super();
        setClazz(ConfiguraVacanteModalidad.class);
    }
}

