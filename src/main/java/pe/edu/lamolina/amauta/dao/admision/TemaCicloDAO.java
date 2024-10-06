package pe.edu.lamolina.amauta.dao.admision;

import java.util.List;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.calificacion.TemaCiclo;

public interface TemaCicloDAO extends EasyDAO<TemaCiclo> {

    List<TemaCiclo> allByCiclo(CicloAcademico ciclo);

}
