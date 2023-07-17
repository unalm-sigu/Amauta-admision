package pe.edu.lamolina.amauta.controller.matricula.asignacionturno;

import com.fasterxml.jackson.databind.node.ArrayNode;
import javax.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableResponse;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;
import pe.edu.lamolina.model.constantines.GlobalConstantine;
import pe.edu.lamolina.model.matricula.MatriculaTurno;


@Controller
@RequestMapping("academico/matricula/asignacionturno")
public class AsignacionTurnoController {

    
    public static final Logger LOG = LoggerFactory.getLogger(AsignacionTurnoController.class);
    
    @Autowired
    AsignacionTurnoService asignacionTurnoService;   

    @RequestMapping(method = RequestMethod.GET)
    public String index(Model model, HttpSession session) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
        model.addAttribute("cicloAcademico", ds.getCicloAcademico());
        return "academico/matricula/asignacionturno/asignacionTurno";
    }    

    @ResponseBody
    @RequestMapping(path = {"list"})
    public DynatableResponse list(DynatableFilter filter, HttpSession session) {
        DynatableResponse json = asignacionTurnoService.findAllMatriculaTurnoByCiclo(filter, session);
        return json;        
    }
   
    @ResponseBody
    @RequestMapping(path = {"turnosAtencion/{id}"})
    public ResponseEntity<ArrayNode> listTurnosAtencion(@PathVariable(value = "id") Long idAlumno) {
        ArrayNode array = asignacionTurnoService.findAllTurnoAtencionByAlumno();
        return ResponseEntity.status(HttpStatus.OK).body(array);
    }    
    
    @RequestMapping(path = {"procesarTurnoMatricula"})
    public ResponseEntity<Void> procesarTurnoMatricula(@RequestBody MatriculaTurno matriculaTurno, HttpSession httpSession) {
        boolean registrado = asignacionTurnoService.nuevoTurno(matriculaTurno, httpSession);
        if (registrado) {
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}
