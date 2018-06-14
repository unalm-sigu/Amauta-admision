package pe.edu.lamolina.pivot.dao.tramite.hibernate;

import org.springframework.stereotype.Repository;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.general.SerieDocumento;
import pe.edu.lamolina.pivot.dao.tramite.ConstanciaPlantillaDAO;

@Repository
public class ConstanciaPlantillaDAOH extends AbstractEasyDAO<SerieDocumento> implements ConstanciaPlantillaDAO {

    public ConstanciaPlantillaDAOH() {
        super();
        setClazz(SerieDocumento.class);
    }

}
