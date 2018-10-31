package pe.edu.lamolina.pivot.controller.academico.gposeccion.clonarciclo;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import javax.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import pe.albatross.zelpers.miscelanea.ExceptionHandler;
import pe.albatross.zelpers.miscelanea.JsonHelper;
import pe.albatross.zelpers.miscelanea.JsonResponse;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.pivot.controller.academico.gposeccion.GpoSeccionResumen;
import pe.edu.lamolina.pivot.zelper.constant.Constantine;
import pe.edu.lamolina.pivot.zelper.constant.Messages;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

@Controller
@RequestMapping("academico/gposeccion")
public class ClonGpoSeccionController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    ClonGpoSeccionService service;

    @ResponseBody
    @RequestMapping("clonarciclo")
    public JsonResponse clonarCiclo(CicloAcademico ciclo, HttpSession session) {

        JsonResponse response = new JsonResponse();
        try {

            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);

            CicloAcademico cicloActivo = ds.getCicloAcademico();

            service.clonarCiclo(ciclo, cicloActivo, ds);

            response.setMessage("Ciclo de clonación satisfactoria");
            response.setSuccess(true);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }

        return response;

    }

    @ResponseBody
    @RequestMapping("cerrarciclo")
    public JsonResponse cantidadGrupo(HttpSession session) {

        JsonResponse response = new JsonResponse();
        try {

            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
            CicloAcademico ciclo = ds.getCicloAcademico();

            //Long cantidad = service.contarGpoSecc(ciclo);
            CicloAcademico cicloBD = service.findCiclo(ciclo);
            logger.debug("Contenido de cicloDB= {}", cicloBD.getDescripcion());
            service.cerrarCiclo(cicloBD);
            logger.debug("Contenido de cicloDB= {}", cicloBD.getDescripcion());
            
            ObjectNode cicloJson = JsonHelper.createJson(cicloBD, JsonNodeFactory.instance, true, new String[]{"*"});
            //ObjectNode info = new ObjectNode(JsonNodeFactory.instance);
            //info.set("ciclo", cicloJson);
            //cicloJson.put("ciclo", cantidad);

            response.setData(cicloJson);
            response.setMessage("Ciclo de clonación cerrado satisfactoriamente");
            response.setSuccess(true);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }

        return response;

    }

    @ResponseBody
    @RequestMapping("findresumen")
    public JsonResponse findResumen(HttpSession session) {

        JsonResponse response = new JsonResponse();

        try {

            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
            CicloAcademico ciclo = ds.getCicloAcademico();

            GpoSeccionResumen resumen = service.resumenByCiclo(ciclo);

            ObjectNode noderesumen = JsonHelper.createJson(resumen, JsonNodeFactory.instance, true, new String[]{"*"});

            response.setData(noderesumen);
            response.setMessage(Messages.UPDATED);
            response.setSuccess(true);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }

        return response;

    }

    @ResponseBody
    @RequestMapping("reordenar")
    public JsonResponse reordenar(HttpSession session) {

        JsonResponse response = new JsonResponse();
        try {

            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
            CicloAcademico ciclo = ds.getCicloAcademico();
            service.limpiarCodigo2(ciclo, ds);
            service.reordenar(ciclo, ds);

            response.setMessage(Messages.UPDATED);
            response.setSuccess(true);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }

        return response;

    }

    @ResponseBody
    @RequestMapping("limpiarciclo")
    public JsonResponse limpiarCiclo(HttpSession session) {

        JsonResponse response = new JsonResponse();
        try {

            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
            CicloAcademico ciclo = ds.getCicloAcademico();
            service.limpiarCiclo(ciclo);

            response.setMessage("Limpieza de clonación satisfactoria");
            response.setSuccess(true);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }

        return response;

    }
}
