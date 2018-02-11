/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.edu.lamolina.pivot.dao.academico;

import java.util.ArrayList;
import java.util.List;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.ConfiguracionTurnosAtencion;
import pe.edu.lamolina.model.academico.TurnoAtencion;

/**
 *
 * @author AlbatrossCloud
 */
public interface TurnoAtencionDAO extends EasyDAO<TurnoAtencion>{

     List<TurnoAtencion> findConfiguracion( ConfiguracionTurnosAtencion config);
}
