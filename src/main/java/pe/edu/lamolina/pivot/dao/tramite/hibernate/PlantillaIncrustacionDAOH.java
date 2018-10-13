package pe.edu.lamolina.pivot.dao.tramite.hibernate;

import org.springframework.stereotype.Repository;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.tramite.PlantillaDocumentoAcademico;
import pe.edu.lamolina.pivot.dao.tramite.PlantillaIncrutacionDAO;

@Repository
public class PlantillaIncrustacionDAOH extends AbstractEasyDAO<PlantillaDocumentoAcademico> implements PlantillaIncrutacionDAO {

    public PlantillaIncrustacionDAOH() {
        super();
        setClazz(PlantillaDocumentoAcademico.class);
    }


}
