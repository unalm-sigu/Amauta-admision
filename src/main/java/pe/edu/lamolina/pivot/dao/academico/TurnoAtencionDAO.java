package pe.edu.lamolina.pivot.dao.academico;

import java.util.List;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.academico.ConfiguracionTurnosAtencion;
import pe.edu.lamolina.model.academico.TurnoAtencion;

public interface TurnoAtencionDAO extends EasyDAO<TurnoAtencion> {

    List<TurnoAtencion> allByConfiguracion(ConfiguracionTurnosAtencion config);

    List<TurnoAtencion> allByIdTurno(ConfiguracionTurnosAtencion config, Long id);

    TurnoAtencion findById(Long Id);

    TurnoAtencion findLastByConfiguracion(ConfiguracionTurnosAtencion config);

}
