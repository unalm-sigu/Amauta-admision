package pe.edu.lamolina.amauta.dao.admision;

import java.util.List;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.calificacion.TemaCiclo;
import pe.edu.lamolina.model.inscripcion.CicloPostula;

public interface TemaCicloDAO extends EasyDAO<TemaCiclo> {

    List<TemaCiclo> allByCiclo(CicloPostula ciclo);

}
