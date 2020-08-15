package pe.edu.lamolina.amauta.controller.fotoCarne;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.beans.PropertyEditorSupport;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import pe.albatross.zelpers.miscelanea.ExceptionHandler;
import pe.albatross.zelpers.miscelanea.JsonHelper;
import pe.albatross.zelpers.miscelanea.JsonResponse;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;
import pe.edu.lamolina.model.constantines.GlobalConstantine;

@Controller
@RequestMapping("fotos/carne")
public class FotoCarneController {

    @Autowired
    FotoCarneService service;

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

    @RequestMapping(method = RequestMethod.GET)
    public String index(Model model, HttpSession session) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);

        model.addAttribute("ciclo", ds.getCicloAcademico());
        return "fotosCarne/fotosCarne";
    }

    @ResponseBody
    @RequestMapping(value = "descargarFotos", method = RequestMethod.GET)
    public JsonResponse descargarFotos(HttpSession session) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
        JsonResponse response = new JsonResponse();
        try {
            service.activar(ds);
            service.descargarFotos(ds);
            response.setSuccess(Boolean.TRUE);
            return response;
        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (RuntimeException e) {
            ExceptionHandler.handleSpecial(e, response, e.getLocalizedMessage());
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }

        return response;
    }

    @ResponseBody
    @RequestMapping(value = "info", method = RequestMethod.GET)
    public JsonResponse info(HttpSession session) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
        JsonResponse response = new JsonResponse();
        FotosCarneComponent cant = service.info(ds);
        ObjectNode node = JsonHelper.createJson(cant, JsonNodeFactory.instance, new String[]{"*"});
        response.setData(node);
        response.setSuccess(Boolean.TRUE);
        return response;
    }

}
