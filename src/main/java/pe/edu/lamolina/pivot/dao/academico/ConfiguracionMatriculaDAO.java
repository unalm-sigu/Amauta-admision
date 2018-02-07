/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.edu.lamolina.pivot.dao.academico;

import java.io.Serializable;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.academico.ConfiguracionTurnosAtencion;
/**
 *
 * @author AlbatrossCloud
 */
public interface ConfiguracionMatriculaDAO extends EasyDAO<ConfiguracionTurnosAtencion>{
    
    ConfiguracionTurnosAtencion findConfiguracion(ConfiguracionTurnosAtencion configuracionTurnosAtencion);
}
