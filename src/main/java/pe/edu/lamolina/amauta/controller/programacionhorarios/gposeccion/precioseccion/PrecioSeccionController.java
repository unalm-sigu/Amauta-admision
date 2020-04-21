package pe.edu.lamolina.amauta.controller.programacionhorarios.gposeccion.precioseccion;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.beans.PropertyEditorSupport;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import javax.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import pe.albatross.zelpers.miscelanea.ExceptionHandler;
import pe.albatross.zelpers.miscelanea.JsonHelper;
import pe.albatross.zelpers.miscelanea.JsonResponse;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.edu.lamolina.model.academico.CursoCicloAcademico;
import pe.edu.lamolina.model.academico.DocenteSeccion;
import pe.edu.lamolina.model.academico.GrupoSeccion;
import pe.edu.lamolina.model.academico.Seccion;
import pe.edu.lamolina.model.finanzas.PagoHoraDocente;
import pe.edu.lamolina.model.general.TipoCarpeta;
import pe.edu.lamolina.model.constantines.GlobalConstantine;
import pe.edu.lamolina.model.constantines.GlobalMessages;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;

@Controller
@RequestMapping("academico/gposeccion")
public class PrecioSeccionController {

    @Autowired
    PrecioSeccionService service;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @InitBinder
    public void initBinder(WebDataBinder dataBinder) {

        dataBinder.registerCustomEditor(Date.class, new PropertyEditorSupport() {
            @Override
            public void setAsText(String value) {
                try {
                    setValue(new SimpleDateFormat("dd/MM/yyyy").parse(value));
                } catch (ParseException e) {
                    setValue(null);
                }
            }
        });

        dataBinder.registerCustomEditor(BigDecimal.class, new PropertyEditorSupport() {
            @Override
            public void setAsText(String value) {
                try {
                    setValue(new BigDecimal(value.replaceAll(",", "")));
                } catch (Exception e) {
                    setValue(null);
                }
            }
        });
    }

    @ResponseBody
    @RequestMapping("saveprecioseccion")
    public JsonResponse savePrecioSeccion(@RequestBody Seccion precioSeccion, HttpSession session) {

        JsonResponse response = new JsonResponse();
        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
            service.savePrecioSeccion(precioSeccion, ds);
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
    @RequestMapping("asignarHorasAdicionales")
    public JsonResponse asignarHorasAdicionales(Seccion seccion, HttpSession session) {
        JsonResponse response = new JsonResponse();
        try {

            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
            service.asignarHorasAdicionales(seccion, ds);
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
    @RequestMapping("allTipoCarpeta")
    public JsonResponse allTipoCarpeta(@RequestParam("nombre") String nombre, HttpSession session) {

        JsonResponse response = new JsonResponse();

        try {

            List<TipoCarpeta> carpetas = service.allTipoCarpetaByNombre(nombre);
            JsonNodeFactory jsonFactory = JsonNodeFactory.instance;
            ArrayNode jsonList = new ArrayNode(jsonFactory);

            for (TipoCarpeta carpeta : carpetas) {
                ObjectNode json = JsonHelper.createJson(carpeta, jsonFactory, true,
                        new String[]{"*", "tipoCarpetaSuperior.*"});
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
    @RequestMapping("saveTipoCarpetaSeccion")
    public JsonResponse saveTipoCarpetaSeccion(Seccion seccion, HttpSession session) {

        JsonResponse response = new JsonResponse();

        try {

            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
            service.saveTipoCarpetaSeccion(seccion, ds);
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
    @RequestMapping("findtipocarpeta")
    public JsonResponse findtipocarpeta(Seccion seccion, HttpSession session) {

        JsonResponse response = new JsonResponse();

        try {

            JsonNodeFactory jsonFactory = JsonNodeFactory.instance;
            TipoCarpeta tipoCarpeta = service.findTipoCarpetaSeccion(seccion);

            ObjectNode json = JsonHelper.createJson(tipoCarpeta, jsonFactory, true,
                    new String[]{"*", "tipoCarpetaSuperior.*"});

            response.setData(json);
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
    @RequestMapping("asignarGrupoSeccionModular")
    public JsonResponse asignarGrupoSeccionModular(GrupoSeccion grupoSeccion, HttpSession session) {

        JsonResponse response = new JsonResponse();

        try {

            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
            service.asignarGrupoSeccionModular(grupoSeccion, ds);
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
    @RequestMapping("generarPagoDocente")
    public JsonResponse generarPagoDocente(DocenteSeccion docenteSeccionForm, HttpSession session) {

        JsonResponse response = new JsonResponse();
        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
            List<PagoHoraDocente> pagosDocenteByHora = service.allPagosDocenteByCiclo(ds.getCicloAcademico());
            DocenteSeccion docenteSeccionBD = service.findDocenteSeccion(docenteSeccionForm);
            CursoCicloAcademico cursoCiclo = service.findCursoCiclo(docenteSeccionBD.getSeccion().getGrupoSeccion().getCurso(), ds.getCicloAcademico());
            String msg = service.generarPagoDocente(docenteSeccionBD, cursoCiclo, pagosDocenteByHora, ds.getCicloAcademico(), ds);
            response.setMessage(msg);
            response.setSuccess(true);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (RuntimeException e) {
            ExceptionHandler.handleSpecial(e, response, GlobalMessages.ERROR_GENERAL);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("generarPagoDocenteCiclo")
    public JsonResponse generarPagoDocenteCiclo(HttpSession session) {

        JsonResponse response = new JsonResponse();
        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
            service.generarPagoDocenteCiclo(ds.getCicloAcademico(), ds);
            response.setMessage("Se generó el pago de los docentes satisfactoriamente.");
            response.setSuccess(true);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (RuntimeException e) {
            ExceptionHandler.handleSpecial(e, response, GlobalMessages.ERROR_GENERAL);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

}
