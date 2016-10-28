package pe.edu.lamolina.pivot.dao.calificacion.hibernate;

import pe.albatross.zelpers.dao.AbstractDAO;
import pe.edu.lamolina.pivot.dao.calificacion.InfoVacanteModalidadDAO;
import pe.edu.lamolina.pivot.model.calificacion.InfoVacanteModalidad;
import org.springframework.stereotype.Repository;

@Repository
public class InfoVacanteModalidadDAOH extends AbstractDAO<InfoVacanteModalidad> implements InfoVacanteModalidadDAO {

    public InfoVacanteModalidadDAOH() {
        super();
        setClazz(InfoVacanteModalidad.class);
    }
}

