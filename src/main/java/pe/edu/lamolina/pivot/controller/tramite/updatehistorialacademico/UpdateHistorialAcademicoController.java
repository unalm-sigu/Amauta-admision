package pe.edu.lamolina.pivot.controller.tramite.updatehistorialacademico;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.List;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import pe.albatross.zelpers.miscelanea.ExceptionHandler;
import pe.albatross.zelpers.miscelanea.JsonResponse;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.AlumnoCiclo;
import pe.edu.lamolina.model.academico.AlumnoCicloCurso;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Curso;
import pe.edu.lamolina.model.academico.SituacionAcademica;
import pe.edu.lamolina.pivot.zelper.constant.Constantine;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

@Controller
@RequestMapping("academico/alumno/updatehistorial")
public class UpdateHistorialAcademicoController {

    @Autowired
    UpdateHistorialAcademicoService service;

    @RequestMapping(value = "{idAlumno}", method = RequestMethod.GET)
    public String datoacademico(@PathVariable("idAlumno") Long idAlumno, Model model, HttpSession session) {
        Alumno alumno = service.allInfo(new Alumno(idAlumno));
        List<CicloAcademico> ciclosAcademico = service.allCicloAcademico();
        ObjectNode alumnoJson = alumno.toJsonInfoAcademico();
        model.addAttribute("datoAlumno", alumnoJson);
        model.addAttribute("ciclosAcademico", ciclosAcademico);
        return "academico/alumno/updatehistorialacademico/updateHistorialAcademico";
    }

    @ResponseBody
    @RequestMapping("update")
    public JsonResponse update(Alumno alumnoForm, HttpSession session) {
        JsonResponse response = new JsonResponse();
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        try {
            service.updateHistorialAcademico(alumnoForm, ds);
            response.setSuccess(true);
        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("notas")
    public JsonResponse notas(Alumno alumnoForm, Model model, HttpSession session) {
        JsonResponse response = new JsonResponse();

        try {

            List<AlumnoCiclo> notas = service.allPromediosByAlumno(alumnoForm);
            ArrayNode arrayNotas = new ArrayNode(JsonNodeFactory.instance);
            for (AlumnoCiclo nota : notas) {

                SituacionAcademica situacionAcademica = nota.getSituacionFinal();
                CicloAcademico cicloAcademico = nota.getCicloAcademico();

                ObjectNode alumnoCicloNode = service.toJson(nota);

                alumnoCicloNode.put("cicloAcademico", service.toJson(cicloAcademico));
                alumnoCicloNode.put("situacionAcademica", service.toJson(situacionAcademica));

                List<AlumnoCicloCurso> cursos = nota.getAlumnoCicloCurso();

                ArrayNode cursosArray = new ArrayNode(JsonNodeFactory.instance);

                for (AlumnoCicloCurso alumnoCicloCurso : cursos) {
                    ObjectNode alumnoCicloCursoNode = service.toJson(alumnoCicloCurso);
                    Curso curso = alumnoCicloCurso.getCurso();
                    alumnoCicloCursoNode.put("curso", service.toJson(curso));
                    cursosArray.add(alumnoCicloCursoNode);
                }

                alumnoCicloNode.set("alumnociclocursos", cursosArray);
                arrayNotas.add(alumnoCicloNode);
            }

            response.setData(arrayNotas);
            response.setTotal(arrayNotas.size());
            response.setSuccess(true);
        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }

        return response;
    }

    @ResponseBody
    @RequestMapping("searchcurso")
    public JsonResponse searchCurso(@RequestParam("nombre") String nombre, HttpSession session) {
        JsonNodeFactory jsonFactory = JsonNodeFactory.instance;
        JsonResponse response = new JsonResponse();
        try {
            List<Curso> cursos = service.allCursoByName(nombre);
            ArrayNode jCursos = new ArrayNode(jsonFactory);
            for (Curso curso : cursos) {
                jCursos.add(service.toJson(curso));
            }
            response.setData(jCursos);
            response.setTotal(jCursos.size());
            response.setSuccess(true);
        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

}
