package pe.edu.lamolina.amauta.dao.horario.hibernate;

import org.springframework.stereotype.Repository;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.horario.PlantillaNivelacion;
import pe.edu.lamolina.amauta.dao.horario.PlantillaNivelacionDAO;

@Repository
public class PlantillaNivelacionDAOH extends AbstractEasyDAO<PlantillaNivelacion> implements PlantillaNivelacionDAO {

    public PlantillaNivelacionDAOH() {
        super();
        setClazz(PlantillaNivelacion.class);
    }

}
