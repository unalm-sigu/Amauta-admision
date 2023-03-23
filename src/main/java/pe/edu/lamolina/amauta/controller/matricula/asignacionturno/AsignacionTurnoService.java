/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package pe.edu.lamolina.amauta.controller.matricula.asignacionturno;

import com.fasterxml.jackson.databind.node.ArrayNode;
import javax.servlet.http.HttpSession;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableResponse;
import pe.edu.lamolina.model.matricula.MatriculaTurno;

/**
 *
 * @author Carlos Buitron
 */
public interface AsignacionTurnoService {
    
    DynatableResponse findAllMatriculaTurnoByCiclo(DynatableFilter filter, HttpSession httpSession);

    boolean nuevoTurno(MatriculaTurno matriculaTurno, HttpSession httpSession);      

    ArrayNode findAllTurnoAtencionByAlumno();

}
