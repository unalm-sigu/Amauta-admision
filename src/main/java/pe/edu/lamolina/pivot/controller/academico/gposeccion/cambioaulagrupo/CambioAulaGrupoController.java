package pe.edu.lamolina.pivot.controller.academico.gposeccion.cambioaulagrupo;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.List;
import javax.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import pe.albatross.zelpers.miscelanea.ExceptionHandler;
import pe.albatross.zelpers.miscelanea.JsonHelper;
import pe.albatross.zelpers.miscelanea.JsonResponse;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.edu.lamolina.model.academico.CambioAulaGrupo;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Seccion;
import pe.edu.lamolina.model.general.Aula;
import pe.edu.lamolina.model.horario.GrupoHoras;
import pe.edu.lamolina.pivot.zelper.constant.Constantine;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

@Controller
@RequestMapping("academico/gposeccion")
public class CambioAulaGrupoController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    CambioAulaGrupoService service;

    @ResponseBody
    @RequestMapping("allcambioaulagrupo")
    public JsonResponse allCambioAulaGrupo(Seccion seccion, HttpSession session) {

        JsonResponse response = new JsonResponse();

        try {

            List<CambioAulaGrupo> aulaGrupos = service.allAulaGrupos(seccion);
            JsonNodeFactory jFactory = JsonNodeFactory.instance;
            ArrayNode array = new ArrayNode(jFactory);

            for (CambioAulaGrupo aulaGrupo : aulaGrupos) {
                ObjectNode node = JsonHelper.createJson(aulaGrupo, jFactory, true,
                        new String[]{
                            "*",});
                array.add(node);
            }

            response.setData(array);
            response.setMessage("Aula Grupos");
            response.setSuccess(true);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }

        return response;

    }

    @ResponseBody
    @RequestMapping("savecambioaulagrupo")
    public JsonResponse savecambioaulagrupo(@RequestBody CambioAulaGrupo cambioAulaGrupo, HttpSession session) {

        logger.debug("Estoy en save CambioAulaGrupo ");
        
        JsonResponse response = new JsonResponse();
        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
            service.saveCambioAulaGrupo(cambioAulaGrupo, ds);
            response.setMessage("Cambio de aula / grupo fue registrada satisfactoriamente");
            response.setSuccess(true);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }

        return response;
    }

    @ResponseBody
    @RequestMapping("aceptarcambioaulagrupo")
    public JsonResponse aceptarcambioaulagrupo(@RequestBody CambioAulaGrupo cambioAulaGrupo, HttpSession session) {

        JsonResponse response = new JsonResponse();

        try {

            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);

            service.aceptarCambioAulaGrupo(cambioAulaGrupo, ds);
            response.setMessage("Cambio de aula / grupo aceptada");
            response.setSuccess(true);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }

        return response;
    }

    @ResponseBody
    @RequestMapping("rechazarcambioaulagrupo")
    public JsonResponse rechazarcambioaulagrupo(@RequestBody CambioAulaGrupo cambioAulaGrupo, HttpSession session) {

        JsonResponse response = new JsonResponse();

        try {

            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);

            service.rechazarCambioAulaGrupo(cambioAulaGrupo, ds);
            response.setMessage("Cambio de aula / grupo rechazada");
            response.setSuccess(true);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }

        return response;
    }
    
    @ResponseBody
    @RequestMapping("deletecambioaulagrupo")
    public JsonResponse deletecambioaulagrupo(CambioAulaGrupo cambioAulaGrupo, HttpSession session) {

        JsonResponse response = new JsonResponse();

        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
            service.deleteCambioAulaGrupo(cambioAulaGrupo, ds);
            response.setMessage("Cambio de aula / grupo anulada");
            response.setSuccess(true);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }

        return response;
    }
    
    @ResponseBody
    @RequestMapping("asyncFindCambioAulas")
    public JsonResponse asyncFindCambioAulas(
            @RequestParam("nombre") String nombre,
            HttpSession session) {

        JsonNodeFactory jsonFactory = JsonNodeFactory.instance;
        JsonResponse response = new JsonResponse();

        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
            CicloAcademico ciclo = ds.getCicloAcademico();
            List<Aula> aulas = service.searchCambioAulaByName(nombre, ciclo);
            ArrayNode jsonList = new ArrayNode(jsonFactory);

            for (Aula aula : aulas) {
                ObjectNode json = JsonHelper.createJson(aula, jsonFactory, true, new String[]{"*", "aulaSuperior.nombre"});
                json.put("esEspecifica", Boolean.TRUE);
                jsonList.add(json);
            }

            response.setData(jsonList);
            response.setTotal(jsonList.size());
            response.setSuccess(true);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("asyncFindCambioGrupos")
    public JsonResponse asyncFindCambioGrupos(
            @RequestParam("nombre") String nombre,
            HttpSession session) {

        JsonNodeFactory jsonFactory = JsonNodeFactory.instance;
        JsonResponse response = new JsonResponse();

        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
            CicloAcademico ciclo = ds.getCicloAcademico();
            List<GrupoHoras> gruposHoras = service.searchCambioGrupoByName(nombre, ciclo);
            ArrayNode jsonList = new ArrayNode(jsonFactory);

            for (GrupoHoras gruposHora : gruposHoras) {
                ObjectNode json = JsonHelper.createJson(gruposHora, jsonFactory, true, new String[]{"*"});
                jsonList.add(json);
            }

            response.setData(jsonList);
            response.setTotal(jsonList.size());
            response.setSuccess(true);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

}
