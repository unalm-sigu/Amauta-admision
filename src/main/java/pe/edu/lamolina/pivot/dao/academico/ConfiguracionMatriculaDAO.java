package pe.edu.lamolina.pivot.dao.academico;

import java.util.List;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.ConfiguracionTurnosAtencion;

public interface ConfiguracionMatriculaDAO extends EasyDAO<ConfiguracionTurnosAtencion> {

    List<ConfiguracionTurnosAtencion> findEventoByConfTurnoAten(CicloAcademico cicloAcademico);

     List<ConfiguracionTurnosAtencion> allByCicloAcad(CicloAcademico cicloAcademico);
}
