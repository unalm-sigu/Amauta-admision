package pe.edu.lamolina.amauta.controller.academico.facultad;

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
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableResponse;
import pe.albatross.zelpers.miscelanea.ExceptionHandler;
import pe.albatross.zelpers.miscelanea.JsonResponse;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.albatross.zelpers.notify.Notificaciones;
import pe.edu.lamolina.model.academico.Carrera;
import pe.edu.lamolina.model.academico.Facultad;
import pe.edu.lamolina.model.enums.TipoOficinaEnum;
import pe.edu.lamolina.model.general.Compania;
import pe.edu.lamolina.amauta.controller.seguridad.verificador.VerificadorService;
import pe.edu.lamolina.model.constantines.AcademicoConstantine;
import pe.edu.lamolina.model.constantines.GlobalConstantine;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;

@Controller
@RequestMapping("academico/facultad")
public class FacultadController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    FacultadService service;

    @Autowired
    VerificadorService verificadorService;

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
        return "academico/facultad/facultad";
    }

    @ResponseBody
    @RequestMapping("list")
    public DynatableResponse list(DynatableFilter filter, HttpSession session, HttpServletRequest request) {
        DynatableResponse json = new DynatableResponse();
        try {

            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
            List<Facultad> facultadUser = verificadorService.allInstanciasByMenuRol(TipoOficinaEnum.ESP, request, ds);

            List<Facultad> facultades = service.allFacultad(filter, facultadUser);
            ArrayNode array = new ArrayNode(JsonNodeFactory.instance);

            for (Facultad facultad : facultades) {
                ObjectNode node = new ObjectNode(JsonNodeFactory.instance);
                node.put("id", facultad.getId());
                node.put("nombre", facultad.getNombre());
                node.put("codigo", facultad.getCodigo());
                node.put("simbolo", facultad.getSimbolo());
                node.put("codigoCurso", facultad.getCodigoCurso());
                node.put("estado", facultad.getEstado());
                node.put("estadoEnum", facultad.getEstadoEnum().getValue());
                node.put("consejeriaRequerida", facultad.getConsejeriaRequerida());
                node.put("motivoDesactivacion", facultad.getMotivoDesactivacion());
                node.put("fecha", new DateTime(facultad.getFechaRegistro()).toString("dd/MM/yyyy"));
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

    @RequestMapping("{facultad}/update")
    public String update(@PathVariable("facultad") Long idFacultad, Model model, HttpSession session) {

        Facultad facultad = service.findFacultad(idFacultad);
        model.addAttribute("facultad", facultad);
        return "academico/facultad/facultadForm";
    }

    @RequestMapping("nuevo")
    public String nuevo(Model model, HttpSession session) {

        model.addAttribute("facultad", new Facultad());
        return "academico/facultad/facultadForm";
    }

    @RequestMapping("save")
    public String save(Facultad facultad, RedirectAttributes redirectAttr, HttpSession session) {

        try {

            if (facultad.getId() != null) {
                service.update(facultad);
                Notificaciones.crearMsg("Facultad Actualizado", redirectAttr);
            } else {
                service.save(facultad);
                Notificaciones.crearMsg("Facultad Creada", redirectAttr);
            }

        } catch (PhobosException ex) {
            ExceptionHandler.handleException(ex, redirectAttr);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, redirectAttr);
        }

        return "redirect:/academico/facultad";
    }

    @ResponseBody
    @RequestMapping("delete")
    public JsonResponse delete(Facultad facultad) {

        JsonResponse response = new JsonResponse();

        try {

            service.delete(facultad);
            response.setMessage("Facultad eliminada satisfactoriamente");
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
    public JsonResponse estado(Facultad facultad) {

        JsonResponse response = new JsonResponse();

        try {

            service.estado(facultad);
            response.setMessage("Facultad eliminada satisfactoriamente");
            response.setSuccess(Boolean.TRUE);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }

        return response;
    }

    @ResponseBody
    @RequestMapping("consejeroRequeridoEstado")
    public JsonResponse consejeroRequeridoEstado(@RequestBody Facultad facultadForm, HttpSession session) {

        JsonResponse response = new JsonResponse();

        try {

            logger.debug("Estado consejero requerido  {}", facultadForm.getConsejeriaRequerida());
            logger.debug("facultad  {}", facultadForm.getId());

            service.updateConsejeroRequerido(facultadForm);
            response.setMessage("La facultad seleccionada fue modificada satisfactoriamente");
            response.setSuccess(Boolean.TRUE);
        } catch (PhobosException ex) {
            ExceptionHandler.handleException(ex, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }

        return response;
    }

}
