package pe.edu.lamolina.pivot.controller.programacionhorarios.horario.tipo;

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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.thymeleaf.spring4.SpringTemplateEngine;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableResponse;
import pe.albatross.zelpers.miscelanea.ExceptionHandler;
import pe.albatross.zelpers.miscelanea.JsonHelper;
import pe.albatross.zelpers.miscelanea.JsonResponse;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.edu.lamolina.model.enums.TipoCicloEnum;
import pe.edu.lamolina.model.horario.TipoGrupoHoras;

@Controller
@RequestMapping("academico/horario")
public class TipoGrupoHorasController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    TipoGrupoHorasService service;

    @Autowired
    SpringTemplateEngine springHtml;

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
        model.addAttribute("tiposCicloJson", JsonHelper.enumToJson(TipoCicloEnum.values()).toString());
        return "academico/horario/tipo/tipoGrupo";
    }

    @ResponseBody
    @RequestMapping("list")
    public DynatableResponse list(DynatableFilter filter, HttpSession session) {

        DynatableResponse json = new DynatableResponse();

        try {

            ArrayNode array = new ArrayNode(JsonNodeFactory.instance);
            List<TipoGrupoHoras> grupos = service.allTipoGrupoHoras(filter);

            for (TipoGrupoHoras grupo : grupos) {
                ObjectNode node = JsonHelper.createJson(grupo, JsonNodeFactory.instance, true, new String[]{"*"});
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
    @RequestMapping("save")
    public JsonResponse save(@RequestBody TipoGrupoHoras tipoGrupo, HttpSession session) {
        JsonResponse response = new JsonResponse();
        try {
            ObjectNode data = new ObjectNode(JsonNodeFactory.instance);
            if (tipoGrupo.getId() != null) {
                service.updateTipoGpo(tipoGrupo);
                response.setMessage("Tipo Grupos actualizado satisfactoriamente");
                response.setSuccess(Boolean.TRUE);
            } else {
                service.saveTipogpo(tipoGrupo);
                response.setMessage("Tipo Grupos creado satisfactoriamente");
                response.setSuccess(Boolean.TRUE);
            }

            response.setData(data);
            response.setSuccess(Boolean.TRUE);
        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("delete")
    public JsonResponse delete(TipoGrupoHoras tipoGrupo) {
        JsonResponse response = new JsonResponse();
        try {
            service.deleteTipoGpo(tipoGrupo);
            response.setMessage("Tipo Grupos eliminado satisfactoriamente");
            response.setSuccess(Boolean.TRUE);
        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (RuntimeException e) {
            ExceptionHandler.handleSpecial(e, response, "Este Tipo-Grupo esta relacionado a otros objetos del sistema");
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("estado")
    public JsonResponse estado(TipoGrupoHoras tipoGrupo) {
        JsonResponse response = new JsonResponse();
        try {
            service.changeEstado(tipoGrupo);
            response.setMessage("Tipo de Grupos actualizado satisfactoriamente");
            response.setSuccess(Boolean.TRUE);
        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

}
