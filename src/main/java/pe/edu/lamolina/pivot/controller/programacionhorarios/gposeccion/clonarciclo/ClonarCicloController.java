package pe.edu.lamolina.pivot.controller.programacionhorarios.gposeccion.clonarciclo;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import javax.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import pe.albatross.zelpers.miscelanea.ExceptionHandler;
import pe.albatross.zelpers.miscelanea.JsonHelper;
import pe.albatross.zelpers.miscelanea.JsonResponse;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.enums.ModalidadEstudioEnum;
import pe.edu.lamolina.pivot.controller.programacionhorarios.gposeccion.GpoSeccionResumen;
import pe.edu.lamolina.model.constantines.AcademicoConstantine;
import pe.edu.lamolina.model.constantines.GlobalConstantine;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

@Controller
@RequestMapping("academico/gposeccion")
public class ClonarCicloController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    ClonarCicloService service;

    @ResponseBody
    @RequestMapping("clonarciclo")
    public JsonResponse clonarCiclo(@RequestBody CicloClonacionBean cicloClonacionBean, HttpSession session) {

        JsonResponse response = new JsonResponse();
        try {

            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
            cicloClonacionBean.setCicloDestino(ds.getCicloAcademico());
            service.clonarCiclo(cicloClonacionBean, ds);

            response.setMessage("Se clonó satisfactoriamente la programación de horarios a este ciclo");
            response.setSuccess(true);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);

        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }

        return response;

    }

    @ResponseBody
    @RequestMapping("cerrarClonacion")
    public JsonResponse cerrarClonacion(HttpSession session) {

        JsonResponse response = new JsonResponse();
        try {

            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
            CicloAcademico ciclo = ds.getCicloAcademico();
            service.cerrarClonacion(ciclo);

            response.setMessage("Se dio por finalizada satisfactoriamente la clonación a este ciclo");
            response.setSuccess(true);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }

        return response;

    }

    @ResponseBody
    @RequestMapping("verBoletinPregrado")
    public JsonResponse verBoletinPregrado(HttpSession session) {

        JsonResponse response = new JsonResponse();
        try {

            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
            CicloAcademico ciclo = ds.getCicloAcademico();
            service.verBoletin(ciclo, ModalidadEstudioEnum.PRE);

            response.setMessage("Ver boletin se ejecutó satisfactoria");
            response.setSuccess(true);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }

        return response;

    }

    @ResponseBody
    @RequestMapping("verBoletinPosgrado")
    public JsonResponse verBoletinPosgrado(HttpSession session) {

        JsonResponse response = new JsonResponse();
        try {

            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
            CicloAcademico ciclo = ds.getCicloAcademico();
            service.verBoletin(ciclo, ModalidadEstudioEnum.EPG);

            response.setMessage("Ver boletin se ejecutó satisfactoria");
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

            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
            CicloAcademico ciclo = ds.getCicloAcademico();
            service.limpiarCodigo2(ciclo, ds);
            service.reordenar(ciclo, ds);

            response.setMessage("Ordenamiento satisfactorio de códigos");
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

            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
            service.limpiarCiclo(ds.getCicloAcademico());

            response.setMessage("Se eliminó satisfactoriamente la programación de horarios");
            response.setSuccess(true);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }

        return response;

    }

    @ResponseBody
    @RequestMapping("cerrarorden")
    public JsonResponse cerrarOrden(HttpSession session) {

        JsonResponse response = new JsonResponse();
        try {

            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
            CicloAcademico ciclo = ds.getCicloAcademico();
            service.cerrarOrden(ciclo);

            response.setMessage("Se dio por finalizado satisfactoriamente el ordenamiento de códigos");
            response.setSuccess(true);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }

        return response;

    }

    @ResponseBody
    @RequestMapping("findDataCiclo")
    public JsonResponse findDataCiclo(HttpSession session) {

        JsonResponse response = new JsonResponse();
        try {

            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
            CicloAcademico cicloPregrado = service.findCicloPregrado(ds.getCicloAcademico());
            CicloAcademico cicloPosgrado = service.findCicloPosgrado(ds.getCicloAcademico());

            ObjectNode node = new ObjectNode(JsonNodeFactory.instance);
            node.set("ciclo", createCicloJson(cicloPregrado));
            node.set("cicloPosgrado", createCicloJson(cicloPosgrado));
            node.set("resumen", createResumenJson(service.resumenByCiclo(cicloPregrado, ds)));

            response.setData(node);
            response.setSuccess(true);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }

        return response;

    }

    private ObjectNode createCicloJson(CicloAcademico ciclo) {
        ObjectNode nodeJson = JsonHelper.createJson(ciclo, JsonNodeFactory.instance, true, new String[]{
            "*",
            "modalidadEstudio.codigo",
            "modalidadEstudio.nombre"
        });
        return nodeJson;
    }

    private ObjectNode createResumenJson(GpoSeccionResumen resumen) {
        ObjectNode nodeJson = JsonHelper.createJson(resumen, JsonNodeFactory.instance, true, new String[]{"*"});
        return nodeJson;
    }
}
