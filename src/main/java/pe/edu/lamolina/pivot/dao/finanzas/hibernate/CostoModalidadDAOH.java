package pe.edu.lamolina.pivot.dao.finanzas.hibernate;

import pe.albatross.zelpers.dao.AbstractDAO;
import pe.edu.lamolina.pivot.dao.finanzas.CostoModalidadDAO;
import pe.edu.lamolina.pivot.model.finanzas.CostoModalidad;
import org.springframework.stereotype.Repository;

@Repository
public class CostoModalidadDAOH extends AbstractDAO<CostoModalidad> implements CostoModalidadDAO {

    public CostoModalidadDAOH() {
        super();
        setClazz(CostoModalidad.class);
    }
}

