package pe.edu.lamolina.pivot.controller.academico.carrera;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.beans.PropertyEditorSupport;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
import org.springframework.web.bind.annotation.PathVariable;
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
import pe.edu.lamolina.pivot.model.academico.Carrera;
import pe.edu.lamolina.pivot.model.academico.CicloAcademico;
import pe.edu.lamolina.pivot.model.academico.OrientacionCarrera;
import pe.edu.lamolina.pivot.zelper.constant.Constantine;
import pe.edu.lamolina.pivot.zelper.enums.EstadoEnum;
import pe.edu.lamolina.pivot.zelper.enums.ModalidadEstudioEnum;
import pe.edu.lamolina.pivot.zelper.enums.TipoCarreraEnum;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

@Controller
@RequestMapping("academico/carrera")
public class CarreraController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    CarreraService service;

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
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        CicloAcademico ciclo = ds.getCicloAcademico();
        model.addAttribute("ciclo", ciclo);
        model.addAttribute("tiposEstudio", ModalidadEstudioEnum.values());
        return "academico/carrera/carrera";
    }

    @ResponseBody
    @RequestMapping("list")
    public DynatableResponse allByDynatable(DynatableFilter filter, HttpSession session) {
        DynatableResponse json = new DynatableResponse();
        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);

            List<Carrera> carreras = service.allByDynatable(filter);

            ArrayNode array = new ArrayNode(JsonNodeFactory.instance);
            logger.debug("size carreras {}", carreras.size());
            for (Carrera carrera : carreras) {
                ObjectNode node = new ObjectNode(JsonNodeFactory.instance);

                node.put("id", carrera.getId());
                node.put("nombre", carrera.getNombre());
                node.put("codigo", carrera.getCodigo());
                node.put("facultad", carrera.getFacultad().getNombre());
                node.put("modalidad", carrera.getModalidadEstudio().getNombre());
                node.put("tipo", carrera.getTipo());
                node.put("tipoEnum", !"".equals(this.getTipoEstudio(carrera.getTipo())) ? carrera.getTipoEnum().getValue() : "");
                ArrayNode arrayOriCarrera = new ArrayNode(JsonNodeFactory.instance);
                for (OrientacionCarrera oriCarrera : carrera.getOrientacionCarrera()) {
                    ObjectNode node2 = new ObjectNode(JsonNodeFactory.instance);
                    node2.put("nombre", oriCarrera.getNombre());
                    arrayOriCarrera.add(node2);
                }
                node.set("oriCarreras", arrayOriCarrera);
                node.put("estado", carrera.getEstado());
                node.put("estadoEnum", carrera.getEstadoEnum().getValue());
                node.put("motivo", carrera.getMotivoAnulacion());

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

    public String getTipoEstudio(String tipo) {
        if (tipo.equals(TipoCarreraEnum.SEM.name()) || tipo.equals(TipoCarreraEnum.PMA.name())) {
            return "";
        }
        return tipo;
    }

    @ResponseBody
    @RequestMapping("cambiarEstadoCarrera")
    public JsonResponse cambiarEstadoCarrera(Carrera carrera) {
        JsonResponse response = new JsonResponse();
        response.setSuccess(false);

        try {
            service.cambiarEstadoCarrera(carrera);

            response.setMessage("Se cambio de estado satisfactoriamente.");
            response.setSuccess(true);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @RequestMapping("nuevo")
    public String nuevoRol(Model model) {
        Carrera carrera = new Carrera();
        carrera.setOrientacionCarrera(new ArrayList());
        model.addAttribute("carrera", carrera);
        model.addAttribute("modalidades", service.allModalidades());
        model.addAttribute("facultades", service.allFacultades());
        model.addAttribute("tipos", TipoCarreraEnum.values());

        return "academico/carrera/carreraForm";
    }

    @RequestMapping("save")
    public String save(Carrera carrera, RedirectAttributes redirectAttr, HttpSession session) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        try {
            String mensaje = carrera.getId() != null ? "Carrera Actualizado" : "Carrera Agregado";
            service.save(carrera, ds.getUsuario());
            Notificaciones.crearMsg(mensaje, redirectAttr);

        } catch (PhobosException ex) {
            ExceptionHandler.handleException(ex, redirectAttr);

        } catch (Exception e) {
            ExceptionHandler.handleException(e, redirectAttr);

        }
        return "redirect:/academico/carrera/editar/" + carrera.getId();
    }

    @ResponseBody
    @RequestMapping("listOrientacion/{idCarrera}")
    public DynatableResponse allByIdCarreraDynatable(DynatableFilter filter, @PathVariable("idCarrera") Long idCarrera, HttpSession session) {
        DynatableResponse json = new DynatableResponse();
        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);

            List<OrientacionCarrera> orientaciones = service.allByIdCarreraDynatable(filter, idCarrera);

            ArrayNode array = new ArrayNode(JsonNodeFactory.instance);
            if (!orientaciones.isEmpty()) {

                for (OrientacionCarrera orientacion : orientaciones) {
                    ObjectNode node = new ObjectNode(JsonNodeFactory.instance);

                    node.put("id", orientacion.getId());
                    node.put("idCarrera", orientacion.getCarrera().getId());
                    node.put("codigo", orientacion.getCodigo());
                    node.put("nombre", orientacion.getNombre());
                    node.put("carrera", orientacion.getCarrera().getNombre());
                    node.put("estado", orientacion.getEstado());
                    node.put("estadoName", EstadoEnum.valueOf(orientacion.getEstado()).getValue());
                    node.put("motivo", orientacion.getMotivoAnulacion());

                    array.add(node);
                }
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

    @RequestMapping("editar/{id}")
    public String editarCarrera(@PathVariable("id") Long id, Model model) {

        Carrera carrera = service.find(id);
        model.addAttribute("modalidades", service.allModalidades());
        model.addAttribute("facultades", service.allFacultades());
        model.addAttribute("tipos", TipoCarreraEnum.values());

        model.addAttribute("carrera", carrera);
        return "academico/carrera/carreraForm";
    }

    @ResponseBody
    @RequestMapping("deleteOrientacion")
    public JsonResponse deleteOrientacion(@RequestParam("idOrientacion") Long idOrientacion,
            @RequestParam("idCarrera") Long idCarrera, RedirectAttributes redirectAttr) {

        JsonResponse response = new JsonResponse();
        response.setSuccess(false);

        try {
            service.deleteOrientacion(idOrientacion);
            response.setMessage("Registro eliminado.");
            response.setSuccess(true);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("saveOrientacion")
    public JsonResponse saveOrientacion(@RequestParam("nombreOrientacion") String nombreOrientacion,
            @RequestParam("idCarrera") Long idCarrera,
            @RequestParam(required = false, value = "idOrientacion") Long idOrientacion,
            Model model, HttpSession session) {

        JsonResponse response = new JsonResponse();
        response.setSuccess(false);
        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
            service.saveOrientacion(idCarrera, idOrientacion, nombreOrientacion, ds.getUsuario());
            if (idOrientacion == null) {
                response.setMessage("Orientacion ingresado satisfactoriamente.");
            } else {
                response.setMessage("Orientacion actualizada satisfactoriamente.");
            }
            response.setSuccess(true);
        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("editarOrientacion")
    public JsonResponse editarOrientacion(@RequestParam("id") Long id) {
        JsonNodeFactory jsonFactory = JsonNodeFactory.instance;
        JsonResponse response = new JsonResponse();
        response.setSuccess(false);

        try {
            OrientacionCarrera orientacion = service.editarOrientacion(id);

            ObjectNode json = new ObjectNode(jsonFactory);

            json.put("id", orientacion.getId());
            json.put("nombreOrientacion", orientacion.getNombre());

            response.setData(json);
            response.setSuccess(true);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("cambioEstadoOrientacion")
    public JsonResponse cambioEstadoOrientacionCarrera(OrientacionCarrera orientacion) {
        JsonResponse response = new JsonResponse();
        response.setSuccess(false);

        try {
            service.cambioEstado(orientacion);

            response.setMessage("Se cambio de estado satisfactoriamente.");
            response.setSuccess(true);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

}
