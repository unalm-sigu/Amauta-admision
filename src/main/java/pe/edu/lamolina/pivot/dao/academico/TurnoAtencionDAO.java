package pe.edu.lamolina.pivot.dao.academico;

import java.math.BigDecimal;
import java.util.List;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.ConfiguracionTurnosAtencion;
import pe.edu.lamolina.model.academico.TurnoAtencion;
import pe.edu.lamolina.model.enums.EventoAcademicoEnum;

public interface TurnoAtencionDAO extends EasyDAO<TurnoAtencion> {

    List<TurnoAtencion> allByConfiguracion(ConfiguracionTurnosAtencion config);

    List<TurnoAtencion> allByIdTurno(ConfiguracionTurnosAtencion config, Long id);

    TurnoAtencion findById(Long Id);

    TurnoAtencion findLastByConfiguracion(ConfiguracionTurnosAtencion config);

    TurnoAtencion findByPrioridad(BigDecimal prioridad, CicloAcademico ciclo, EventoAcademicoEnum eventoEnum);

    List<TurnoAtencion> allByCicloEventoEnum(CicloAcademico ciclo, EventoAcademicoEnum eventoEnum);
}
