package pe.edu.lamolina.amauta.dao.admision;

import java.util.List;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.calificacion.TemaExamen;

public interface TemaExamenDAO extends EasyDAO<TemaExamen> {

    List<TemaExamen> allNotTemaSuperior();

}
