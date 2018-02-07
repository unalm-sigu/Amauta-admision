/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.edu.lamolina.pivot.controller.academico.matricula.configuracion;

import pe.edu.lamolina.model.academico.ConfiguracionTurnosAtencion;

/**
 *
 * @author AlbatrossCloud
 */
public interface ConfiguracionMatriculaService {
    
    ConfiguracionTurnosAtencion findConfiguracion(ConfiguracionTurnosAtencion configuracionTurnosAtencion);
    
    void saveConfiguracion(ConfiguracionTurnosAtencion configuracionTurnosAtencion);
}
