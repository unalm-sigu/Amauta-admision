package pe.edu.lamolina.amauta.controller.programacionhorarios.gposeccion.ampliavacantes;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.List;
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
import pe.edu.lamolina.model.academico.AmpliacionVacantes;
import pe.edu.lamolina.model.academico.Seccion;
import pe.edu.lamolina.model.constantines.GlobalConstantine;
import pe.edu.lamolina.model.constantines.GlobalMessages;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;

@Controller
@RequestMapping("academico/gposeccion")
public class AmpliaVacantesController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    AmpliaVacantesService service;

    @ResponseBody
    @RequestMapping("allampliacionvacante")
    public JsonResponse allAmpliacionVacante(Seccion seccion, HttpSession session) {

        JsonResponse response = new JsonResponse();

        try {

            List<AmpliacionVacantes> ampliaciones = service.allAmpliacionVacante(seccion);
            JsonNodeFactory jFactory = JsonNodeFactory.instance;
            ArrayNode array = new ArrayNode(jFactory);

            for (AmpliacionVacantes ampliacion : ampliaciones) {
                ObjectNode node = JsonHelper.createJson(ampliacion, jFactory, true,
                        new String[]{
                            "*",
                            "colaborador.id",
                            "colaborador.cargo.nombre",
                            "oficina.id",
                            "oficina.nombre",
                            "seccion.id",
                            "colaborador.persona.id",
                            "colaborador.persona.nombreCompleto"
                        });
                array.add(node);
            }

            response.setData(array);
            response.setMessage(GlobalMessages.UPDATED);
            response.setSuccess(true);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }

        return response;

    }

    @ResponseBody
    @RequestMapping("saveampliacionvacante")
    public JsonResponse saveAmpliacionVacante(AmpliacionVacantes ampliacionVacante, HttpSession session) {

        JsonResponse response = new JsonResponse();
        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
            service.saveAmpliacionVacante(ampliacionVacante, ds);
            response.setMessage("Solictud de ampliación registrada satisfactoriamente");
            response.setSuccess(true);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }

        return response;
    }

    @ResponseBody
    @RequestMapping("updateampliacionvacante")
    public JsonResponse updateAmpliacionVacante(AmpliacionVacantes ampliacionVacanteForm, HttpSession session) {

        JsonResponse response = new JsonResponse();

        try {

            AmpliacionVacantes ampliacion = service.findAmpliacionVacante(ampliacionVacanteForm);
            JsonNodeFactory jFactory = JsonNodeFactory.instance;
            ObjectNode node = JsonHelper.createJson(ampliacion, jFactory, true, new String[]{"*", "seccion.id", "oficina.id"});

            response.setData(node);
            response.setMessage(GlobalMessages.UPDATED);
            response.setSuccess(true);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }

        return response;
    }

    @ResponseBody
    @RequestMapping("deleteampliacionvacante")
    public JsonResponse deleteAmpliacionVacante(AmpliacionVacantes ampliacionVacante, HttpSession session) {

        JsonResponse response = new JsonResponse();

        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
            service.deleteAmpliacionVacante(ampliacionVacante, ds);
            response.setMessage("Ampliación anulada");
            response.setSuccess(true);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }

        return response;
    }

    @ResponseBody
    @RequestMapping("aceptarampliacionvacante")
    public JsonResponse aceptarampliacionvacante(AmpliacionVacantes ampliacionVacante, HttpSession session) {

        JsonResponse response = new JsonResponse();

        try {

            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);

            service.aceptarAmpliacionVacante(ampliacionVacante, ds);
            response.setMessage("Ampliación aceptada");
            response.setSuccess(true);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }

        return response;
    }

    @ResponseBody
    @RequestMapping("rechazarampliacionvacante")
    public JsonResponse rechazarampliacionvacante(AmpliacionVacantes ampliacionVacante, HttpSession session) {

        JsonResponse response = new JsonResponse();

        try {

            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);

            service.rechazarAmpliacionVacante(ampliacionVacante, ds);
            response.setMessage("Ampliación rechazada");
            response.setSuccess(true);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }

        return response;
    }

}
