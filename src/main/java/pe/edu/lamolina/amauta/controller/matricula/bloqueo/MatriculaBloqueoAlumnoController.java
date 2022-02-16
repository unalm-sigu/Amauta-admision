package pe.edu.lamolina.amauta.controller.matricula.bloqueo;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.List;
import javax.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableResponse;
import pe.albatross.zelpers.json.JaneHelper;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;
import pe.edu.lamolina.model.academico.Carrera;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.MatriculaBloqueoAlumno;
import pe.edu.lamolina.model.academico.SituacionAcademica;
import pe.edu.lamolina.model.constantines.GlobalConstantine;
import pe.edu.lamolina.model.constantines.GlobalMessages;

@Slf4j
@Controller
@RequestMapping("academico/matricula/bloqueo")
public class MatriculaBloqueoAlumnoController {

    @Autowired
    MatriculaBloqueoAlumnoService service;

    @RequestMapping(method = RequestMethod.GET)
    public String index() {
        return "academico/matricula/bloqueo/bloqueo";
    }

    @ResponseBody
    @RequestMapping(value = "all", method = RequestMethod.GET)
    public DynatableResponse all(DynatableFilter filter, HttpSession session) {

        DynatableResponse dynatable = new DynatableResponse();
        dynatable.setTotal(0);
        List<MatriculaBloqueoAlumno> matriculaBloqueoAlumnos = service.all(filter);
        ArrayNode array = JaneHelper.from(matriculaBloqueoAlumnos).only("id")
                .join("carrera", "nombre")
                .join("situacionAcademica", "nombre,descripcion")
                .join("cicloAplica", "descripcion")
                .array();
        dynatable.setData(array);
        dynatable.setTotal(filter.getTotal());
        dynatable.setFiltered(filter.getFiltered());
        return dynatable;
    }

    @ResponseBody
    @RequestMapping(method = RequestMethod.POST)
    public String save(HttpSession session, @RequestBody MatriculaBloqueoAlumnoDTO matriculaBloqueoAlumno) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
        service.save(matriculaBloqueoAlumno, ds);
        return GlobalMessages.CREATED;
    }

    @ResponseBody
    @RequestMapping(method = RequestMethod.PUT)
    public String update(@RequestBody MatriculaBloqueoAlumno matriculaBloqueoAlumno) {
        service.update(matriculaBloqueoAlumno);
        return GlobalMessages.UPDATED;
    }

    @ResponseBody
    @RequestMapping(value = "{idMatriculaBloqueoAlumno}", method = RequestMethod.DELETE)
    public String eliminar(@PathVariable Long idMatriculaBloqueoAlumno) {
        service.eliminar(idMatriculaBloqueoAlumno);
        return GlobalMessages.DELETED;
    }

    @ResponseBody
    @RequestMapping(value = "{idMatriculaBloqueoAlumno}", method = RequestMethod.GET)
    public ObjectNode find(@PathVariable Long idMatriculaBloqueoAlumno) {
        MatriculaBloqueoAlumno matriculaBloqueoAlumno = service.find(idMatriculaBloqueoAlumno);
        return JaneHelper.from(matriculaBloqueoAlumno)
                .join("carrera", "id,nombre")
                .join("situacionAcademica", "id,nombre,descripcion,codigo")
                .join("cicloAplica", "id,codigo,descripcion")
                .json();
    }

    @ResponseBody
    @RequestMapping(value = "allCarrera", method = RequestMethod.GET)
    public ArrayNode allCarrera() {
        List<Carrera> carreras = service.allCarrera();
        return JaneHelper.from(carreras)
                .only("id,nombre")
                .array();
    }

    @ResponseBody
    @RequestMapping(value = "allSituacion", method = RequestMethod.GET)
    public ArrayNode allSituacion() {
        List<SituacionAcademica> situacionAcademicas = service.allSituacionAcademica();
        return JaneHelper.from(situacionAcademicas)
                .only("id,nombre")
                .array();
    }

    @ResponseBody
    @RequestMapping(value = "allCiclo", method = RequestMethod.GET)
    public ArrayNode allCiclo(HttpSession session) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
        List<CicloAcademico> cicloAcademicos = service.allCicloAcademico(ds);
        return JaneHelper.from(cicloAcademicos)
                .only("id,descripcion")
                .array();
    }

}
