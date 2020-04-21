package pe.edu.lamolina.amauta.controller.matricula.configuracionturno;

import java.text.ParseException;
import java.util.List;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.ConfiguracionTurnosAtencion;
import pe.edu.lamolina.model.academico.EventoCicloAcademico;
import pe.edu.lamolina.model.academico.TurnoAtencion;

public interface ConfiguracionMatriculaService {

    Long saveConfiguracion(ConfiguracionTurnosAtencion configuracionTurnosAtencion) throws ParseException;

    List<EventoCicloAcademico> allEventoCiclo(CicloAcademico cicloAcademico);

    List<ConfiguracionTurnosAtencion> allConfiguraciones(CicloAcademico cicloAcademico);

    List<TurnoAtencion> allTurnosByConfiguracion(ConfiguracionTurnosAtencion config);

    ConfiguracionTurnosAtencion updateTurnos(Long id, String pk);

    void deleteConfiguracion(ConfiguracionTurnosAtencion config);

}
