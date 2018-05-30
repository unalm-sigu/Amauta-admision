package pe.edu.lamolina.pivot.dao.tramite.hibernate;

import org.springframework.stereotype.Repository;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.tramite.VariablePlantilla;
import pe.edu.lamolina.pivot.dao.tramite.VariablePlantillaDAO;

@Repository
public class VariablePlantillaDAOH extends AbstractEasyDAO<VariablePlantilla> implements VariablePlantillaDAO {

    public VariablePlantillaDAOH() {
        super();
        setClazz(VariablePlantilla.class);
    }

}
