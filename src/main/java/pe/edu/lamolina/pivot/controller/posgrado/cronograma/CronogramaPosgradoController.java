package pe.edu.lamolina.pivot.controller.posgrado.cronograma;

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
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.posgrado.CronogramaCuota;
import pe.edu.lamolina.pivot.zelper.constant.Constantine;
import pe.edu.lamolina.pivot.zelper.constant.Messages;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

@Controller
@RequestMapping("posgrado/cronograma")
public class CronogramaPosgradoController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    CronogramaPosgradoService service;

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
    public String index(HttpSession session, Model model) {
        return "posgrado/cronograma/cronograma";
    }

    @ResponseBody
    @RequestMapping("all")
    public JsonResponse all(HttpSession session) {

        JsonResponse response = new JsonResponse();

        try {

            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
            CicloAcademico ciclo = ds.getCicloAcademico();
            
            List<CronogramaCuota> cronogramas = service.allByCiclo(ciclo);
            
            JsonNodeFactory jFactory = JsonNodeFactory.instance;
            ArrayNode array = new ArrayNode(jFactory);
            for (CronogramaCuota cronograma : cronogramas) {
                ObjectNode node = JsonHelper.createJson(cronograma, jFactory, true,
                        new String[]{
                            "*",
                            "cicloAcademico.id"
                        });
                array.add(node);
            }

            response.setData(array);
            response.setTotal(array.size());
            response.setSuccess(true);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("generar")
    public JsonResponse generar(CronogramaCuota cronograma,HttpSession session) {

        JsonResponse response = new JsonResponse();

        try {

            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
            service.generar(cronograma,ds);
            
            response.setSuccess(true);
            response.setMessage(GlobalMessages.CREATED);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("delete")
    public JsonResponse delete(CicloAcademico ciclo,HttpSession session) {

        JsonResponse response = new JsonResponse();

        try {

            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
            service.deleteAll(ciclo,ds);
            
            response.setMessage(GlobalMessages.DELETED);
            response.setSuccess(true);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("update")
    public JsonResponse update(CronogramaCuota cronograma,HttpSession session) {

        JsonResponse response = new JsonResponse();

        try {

            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
            service.update(cronograma,ds);
            
            response.setMessage(GlobalMessages.UPDATED);
            response.setSuccess(true);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

}
