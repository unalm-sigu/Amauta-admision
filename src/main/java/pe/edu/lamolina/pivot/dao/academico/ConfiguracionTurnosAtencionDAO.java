package pe.edu.lamolina.pivot.dao.academico;

import java.math.BigDecimal;
import java.util.List;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.ConfiguracionTurnosAtencion;

public interface ConfiguracionTurnosAtencionDAO extends EasyDAO<ConfiguracionTurnosAtencion> {

    List<ConfiguracionTurnosAtencion> allByCiclo(CicloAcademico ciclo);

}
