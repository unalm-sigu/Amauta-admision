package pe.edu.lamolina.pivot.controller.academico.distanciapabellon;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.beans.PropertyEditorSupport;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
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
import pe.edu.lamolina.model.academico.DepartamentoAcademico;
import pe.edu.lamolina.model.academico.DistanciaPabellon;
import pe.edu.lamolina.pivot.zelper.constant.Constantine;
import pe.edu.lamolina.pivot.zelper.constant.Messages;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

@Controller
@RequestMapping("academico/distanciapabellon")
public class DistanciaPabellonController {

    @Autowired
    DistanciaPabellonService service;

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
        return "academico/distanciapabellon/distanciapabellon";
    }

    @ResponseBody
    @RequestMapping("list")
    public DynatableResponse list(DynatableFilter filter, HttpSession session, HttpServletRequest request) {

        DynatableResponse json = new DynatableResponse();

        try {

            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
            List<DepartamentoAcademico> departamentos = service.allDynatableFilter(filter, ds);
            JsonNodeFactory factory = JsonNodeFactory.instance;
            ArrayNode array = new ArrayNode(factory);

            for (DepartamentoAcademico departamentoAcademico : departamentos) {
                ObjectNode depajson = JsonHelper.createJson(departamentoAcademico, factory, true, new String[]{
                    "id",
                    "nombre",
                    "codigo",
                    "nombreLargo",
                    "facultad.codigo",
                    "facultad.nombre",});
                array.add(depajson);
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
    @RequestMapping("allDistancia")
    public JsonResponse allDistancia(DepartamentoAcademico departamentoAcademico) {

        JsonResponse response = new JsonResponse();

        try {

            List<DistanciaPabellon> distancias = service.allDistancia(departamentoAcademico);
            JsonNodeFactory factory = JsonNodeFactory.instance;
            ArrayNode array = new ArrayNode(factory);

            for (DistanciaPabellon distancia : distancias) {
                ObjectNode jDistancia = JsonHelper.createJson(distancia, factory, true, new String[]{
                    "*",
                    "pabellon.id",
                    "pabellon.codigo",
                    "pabellon.nombre",
                    "departamentoAcademico.id",});
                array.add(jDistancia);
            }

            response.setData(array);
            response.setSuccess(Boolean.TRUE);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }

        return response;
    }

    @ResponseBody
    @RequestMapping("saveDistancia")
    public JsonResponse saveDistancia(@RequestBody DepartamentoAcademico departamentoAcademico) {

        JsonResponse response = new JsonResponse();

        try {

            service.saveDistancia(departamentoAcademico);
            response.setMessage(GlobalMessages.UPDATED);
            response.setSuccess(Boolean.TRUE);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }

        return response;
    }

}
