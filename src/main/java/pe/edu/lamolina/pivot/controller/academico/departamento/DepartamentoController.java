package pe.edu.lamolina.pivot.controller.academico.departamento;

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
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pe.albatross.zelpers.dynatable.DynatableFilter;
import pe.albatross.zelpers.dynatable.DynatableResponse;
import pe.albatross.zelpers.miscelanea.ExceptionHandler;
import pe.albatross.zelpers.miscelanea.JsonResponse;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.albatross.zelpers.notify.Notificaciones;
import pe.edu.lamolina.pivot.model.academico.DepartamentoAcademico;
import pe.edu.lamolina.pivot.zelper.constant.Constantine;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;


@Controller
@RequestMapping("academico/departamento")
public class DepartamentoController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    DepartamentoService service;

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
        return "academico/departamento/departamento";
    }

    @ResponseBody
    @RequestMapping("list")
    public DynatableResponse list(DynatableFilter filter, HttpSession session) {
        DynatableResponse json = new DynatableResponse();
        try {

            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
            List<DepartamentoAcademico> departamentos = service.allDepartamentoAcademico(filter);

            ArrayNode array = new ArrayNode(JsonNodeFactory.instance);

            for (DepartamentoAcademico departamentoAcademico : departamentos) {
                ObjectNode node = new ObjectNode(JsonNodeFactory.instance);
                node.put("id", departamentoAcademico.getId());
                node.put("nombre", departamentoAcademico.getNombre());
                node.put("codigo", departamentoAcademico.getCodigo());
                node.put("nombreLargo", departamentoAcademico.getNombre());
                node.put("estado", departamentoAcademico.getEstado());
                node.put("motivoDesactivacion", departamentoAcademico.getMotivoDesactivacion());
                node.put("fecha", new DateTime(departamentoAcademico.getFechaDesactivacion()).toString("dd/MM/yyyy"));
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

    @RequestMapping("{departamento}/update")
    public String update(@PathVariable("departamento") Long idDepartamentoAcademico, Model model, HttpSession session) {

        DepartamentoAcademico departamento = service.findDepartamentoAcademico(idDepartamentoAcademico);
        logger.debug("{}",departamento);
        model.addAttribute("departamento", departamento);
        return "academico/departamento/departamentoForm";
    }
    
    @RequestMapping("nuevo")
    public String nuevo(Model model, HttpSession session) {

        model.addAttribute("departamento", new DepartamentoAcademico());
        return "academico/departamento/departamentoForm";
    }

    @RequestMapping("save")
    public String save(DepartamentoAcademico departamento, RedirectAttributes redirectAttr, HttpSession session) {

        try {

            if (departamento.getId() != null) {
                service.update(departamento);
                Notificaciones.crearMsg("Departamento académico actualizado satisfactoriamente", redirectAttr);
            } else {
                service.save(departamento);
                Notificaciones.crearMsg("Departamento académico creado satisfactoriamente", redirectAttr);
            }

        } catch (PhobosException ex) {
            ExceptionHandler.handleException(ex, redirectAttr);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, redirectAttr);
        }
        
        return "redirect:/academico/departamento";
    }
    
    
    @ResponseBody
    @RequestMapping("delete")
    public JsonResponse delete(DepartamentoAcademico departamento) {
        
        JsonResponse response = new JsonResponse();

        try {

            service.delete(departamento);
            response.setMessage("Departamento académico eliminado satisfactoriamente");
            response.setSuccess(Boolean.TRUE);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }

        return response;
    }
    
    
    @ResponseBody
    @RequestMapping("estado")
    public JsonResponse estado(DepartamentoAcademico departamento) {
        
        JsonResponse response = new JsonResponse();

        try {

            service.estado(departamento);
            response.setMessage("Departamento académico actualizado satisfactoriamente");
            response.setSuccess(Boolean.TRUE);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }

        return response;
    }
    
}
