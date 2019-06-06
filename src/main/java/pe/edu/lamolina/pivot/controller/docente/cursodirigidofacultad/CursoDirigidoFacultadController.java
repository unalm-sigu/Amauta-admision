package pe.edu.lamolina.pivot.controller.docente.cursodirigidofacultad;

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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableResponse;
import pe.albatross.zelpers.miscelanea.ExceptionHandler;
import pe.albatross.zelpers.miscelanea.JsonHelper;
import pe.albatross.zelpers.miscelanea.JsonResponse;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.edu.lamolina.model.academico.Curso;
import pe.edu.lamolina.model.academico.Facultad;
import pe.edu.lamolina.model.enums.TipoOficinaEnum;
import pe.edu.lamolina.model.tramite.CursoDirigidoFacultad;
import pe.edu.lamolina.pivot.controller.seguridad.verificador.VerificadorService;
import pe.edu.lamolina.pivot.zelper.constant.Constantine;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

@Controller
@RequestMapping("docente/cursodirigidofacultad")
public class CursoDirigidoFacultadController {

    @Autowired
    CursoDirigidoFacultadService service;

    @Autowired
    VerificadorService verificadorService;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @RequestMapping(method = RequestMethod.GET)
    public String index(Model model, HttpSession session, HttpServletRequest request) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);

        List<Facultad> facultades = verificadorService.allInstanciasByMenuRol(TipoOficinaEnum.FAC, request, ds);

        logger.debug("******** facultades {}", facultades.size());

        model.addAttribute("facultades", createFacultadesJson(facultades).toString());
        return "docente/cursodirigidofacultad/cursoDirigidoFacultad";
    }

    @ResponseBody
    @RequestMapping("list/{idFacultad}")
    public DynatableResponse list(@PathVariable("idFacultad") Long idFacultad, DynatableFilter filter, HttpSession session) {

        DynatableResponse json = new DynatableResponse();
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);

        try {

            List<CursoDirigidoFacultad> cursosDirigidosFaculta = service.allByDocenteFacultadDynatable(new Facultad(idFacultad), filter);

            ArrayNode array = new ArrayNode(JsonNodeFactory.instance);

            for (CursoDirigidoFacultad cursoDirigidoFacultad : cursosDirigidosFaculta) {
                ObjectNode node = JsonHelper.createJson(cursoDirigidoFacultad, JsonNodeFactory.instance, true,
                        new String[]{
                            "id", "estado", "fechaRegistro",
                            "facultad.id",
                            "facultad.codigo",
                            "facultad.nombre",
                            "facultad.estado",
                            "facultad.simbolo",
                            "curso.id",
                            "curso.codigo",
                            "curso.nombre",
                            "curso.tpc"
                        });

                array.add(node);
            }

            json.setData(array);
            json.setTotal(filter.getTotal());
            json.setFiltered(filter.getFiltered());

        } catch (Exception e) {
            e.printStackTrace();
            json.setTotal(0);
        }
        return json;
    }

    @ResponseBody
    @RequestMapping("allLikeCurso")
    public JsonResponse allLikeCurso(@RequestParam("parametro") String parametro, HttpSession session) {

        JsonNodeFactory jsonFactory = JsonNodeFactory.instance;
        JsonResponse response = new JsonResponse();

        try {
            ArrayNode jsonList = new ArrayNode(jsonFactory);
            List<Curso> cursos = service.allCursoLikeParam(parametro); //searhc for nombrey codigo
            for (Curso curso : cursos) {
                jsonList.add(JsonHelper.createJson(curso, JsonNodeFactory.instance, new String[]{
                    "id", "nombre", "estado", "codigo"
                }));
            }
            response.setData(jsonList);
            response.setTotal(jsonList.size());
            response.setSuccess(true);

        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }

        return response;
    }

    @ResponseBody
    @RequestMapping("save")
    public JsonResponse save(@RequestBody CursoDirigidoFacultad cursoDirigidoFacultad, HttpSession session) {
        JsonResponse response = new JsonResponse();

        try {

            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);

            service.save(cursoDirigidoFacultad, ds);
            response.setMessage("El curso fue agregado satisfactoriamente");
            response.setSuccess(true);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    private ArrayNode createFacultadesJson(List<Facultad> facultades) {
        ArrayNode array = new ArrayNode(JsonNodeFactory.instance);
        for (Facultad facultad : facultades) {
            ObjectNode node = JsonHelper.createJson(facultad, JsonNodeFactory.instance, true, new String[]{
                "id", "nombre", "codigo"
            });
            array.add(node);
        }
        return array;
    }

}
