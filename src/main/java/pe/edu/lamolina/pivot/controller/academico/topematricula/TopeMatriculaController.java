package pe.edu.lamolina.pivot.controller.academico.topematricula;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableResponse;
import pe.albatross.zelpers.miscelanea.ExceptionHandler;
import pe.albatross.zelpers.miscelanea.JsonHelper;
import pe.albatross.zelpers.miscelanea.JsonResponse;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.edu.lamolina.model.academico.TopeMatricula;
import pe.edu.lamolina.model.enums.TipoAlumnoEnum;
import pe.edu.lamolina.pivot.zelper.constant.Constantine;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

@Controller
@RequestMapping("academico/topematricula")
public class TopeMatriculaController {

    @Autowired
    TopeMatriculaService service;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @RequestMapping(method = RequestMethod.GET)
    public String index(Model model, HttpSession session) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        model.addAttribute("ciclo", ds.getCicloAcademico());
        return "academico/topematricula/topematricula";
    }

    @ResponseBody
    @RequestMapping("list")
    public DynatableResponse list(DynatableFilter filter, HttpSession session, HttpServletRequest request) {
        DynatableResponse json = new DynatableResponse();
        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);

            List<TopeMatricula> topesMatricula = service.allTopeMatricula(filter, ds.getCicloAcademico());

            ArrayNode array = new ArrayNode(JsonNodeFactory.instance);

            for (TopeMatricula topMatricula : topesMatricula) {
                ObjectNode node = JsonHelper.createJson(topMatricula, JsonNodeFactory.instance, true,
                        new String[]{
                            "cicloAcademico.descripcion2",
                            "id", "tipoAlumno", "tipoAlumnoEnum", "creditos"
                        });

                array.add(node);
            }
            json.setData(array);
            json.setTotal(filter.getTotal());
            json.setFiltered(filter.getFiltered());

        } catch (Exception e) {
            json.setTotal(0);
        }
        return json;
    }

    @ResponseBody
    @RequestMapping("allTipoAlumnos")
    public JsonResponse allTipoAlumnos(HttpSession session) {

        JsonResponse response = new JsonResponse();

        try {
            ArrayNode arrayTipoAlumnos = JsonHelper.enumToJson(TipoAlumnoEnum.values());

            response.setData(arrayTipoAlumnos);
            response.setSuccess(true);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("save")
    public JsonResponse save(@RequestBody List<TopeMatricula> topesMatricula, HttpSession session) {

        JsonNodeFactory jsonFactory = JsonNodeFactory.instance;
        JsonResponse response = new JsonResponse();

        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
            service.save(topesMatricula, ds.getCicloAcademico(), ds);

            response.setSuccess(true);
            response.setMessage("Guardado satisfactoriamnente");

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }
}
