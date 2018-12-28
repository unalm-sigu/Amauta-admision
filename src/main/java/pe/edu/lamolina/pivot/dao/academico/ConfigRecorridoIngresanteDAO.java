package pe.edu.lamolina.pivot.dao.academico;

import java.util.List;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.ConfigRecorridoIngresante;

public interface ConfigRecorridoIngresanteDAO extends EasyDAO<ConfigRecorridoIngresante> {

    List<ConfigRecorridoIngresante> allByCicloAcademico(CicloAcademico ciclo);

}
