package pe.edu.lamolina.pivot.dao.academico;

import java.io.Serializable;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.academico.ConfiguracionTurnosAtencion;

public interface ConfiguracionMatriculaDAO extends EasyDAO<ConfiguracionTurnosAtencion> {

    ConfiguracionTurnosAtencion findConfiguracion(ConfiguracionTurnosAtencion configuracionTurnosAtencion);
}
