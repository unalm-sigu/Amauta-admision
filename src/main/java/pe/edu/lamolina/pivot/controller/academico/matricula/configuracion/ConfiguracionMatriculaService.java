package pe.edu.lamolina.pivot.controller.academico.matricula.configuracion;

import pe.edu.lamolina.model.academico.ConfiguracionTurnosAtencion;

public interface ConfiguracionMatriculaService {

    ConfiguracionTurnosAtencion findConfiguracion(ConfiguracionTurnosAtencion configuracionTurnosAtencion);

    void saveConfiguracion(ConfiguracionTurnosAtencion configuracionTurnosAtencion);
}
