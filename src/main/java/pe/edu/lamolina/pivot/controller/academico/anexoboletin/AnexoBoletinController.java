package pe.edu.lamolina.pivot.controller.academico.anexoboletin;

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
import pe.edu.lamolina.pivot.model.academico.AnexoBoletin;
import pe.edu.lamolina.pivot.model.academico.Carrera;
import pe.edu.lamolina.pivot.model.academico.CicloAcademico;
import pe.edu.lamolina.pivot.zelper.constant.Constantine;
import pe.edu.lamolina.pivot.zelper.constant.Messages;
import pe.edu.lamolina.pivot.zelper.enums.EstadoEnum;
import pe.edu.lamolina.pivot.zelper.enums.TipoCarreraEnum;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

@Controller
@RequestMapping("academico/anexo")
public class AnexoBoletinController {

    @Autowired
    AnexoBoletinService service;

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
    public String index(Model model) {
        model.addAttribute("resumen", service.resumen());
        return "academico/anexoboletin/anexo";
    }

    @ResponseBody
    @RequestMapping("list")
    public DynatableResponse allByDynatable(DynatableFilter filter, HttpSession session) {
        DynatableResponse json = new DynatableResponse();
        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);

            List<AnexoBoletin> anexos = service.allByDynatable(filter);

            ArrayNode array = new ArrayNode(JsonNodeFactory.instance);

            for (AnexoBoletin anexo : anexos) {
                ObjectNode node = new ObjectNode(JsonNodeFactory.instance);

                node.put("id", anexo.getId());
                node.put("codigo", anexo.getCodigo());
                node.put("nombre", anexo.getNombre());
                node.put("departamento", anexo.getDepartamentoAcademico() != null ? anexo.getDepartamentoAcademico().getNombre() : "");
                node.put("carrera", anexo.getCarrera() != null ? anexo.getCarrera().getNombre() : "");
                node.put("anexoSuperior", anexo.getAnexoSuperior() != null ? anexo.getAnexoSuperior().getNombre() : "");
                node.put("orden", anexo.getOrden());
                node.put("estado", anexo.getEstado());
                node.put("estadoName", EstadoEnum.valueOf(anexo.getEstado()).getValue());
                node.put("motivo", anexo.getMotivoAnulacion());
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

    @RequestMapping("nuevo")
    public String nuevo(Model model, HttpSession session) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        CicloAcademico ciclo = ds.getCicloAcademico();
        AnexoBoletin anexo = new AnexoBoletin();

        model.addAttribute("anexo", anexo);
        model.addAttribute("ciclo", ciclo);
        model.addAttribute("anexos", service.allAnexosSuperiores());
        return "academico/anexoboletin/anexoForm";
    }

    @RequestMapping("save")
    public String save(AnexoBoletin anexo, RedirectAttributes redirectAttr, HttpSession session) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        try {
            String mensaje = anexo.getId() != null ? Messages.UPDATED : Messages.CREATED;
            service.save(anexo, ds.getUsuario());
            Notificaciones.crearMsg(mensaje, redirectAttr);

        } catch (PhobosException ex) {
            ExceptionHandler.handleException(ex, redirectAttr);

        } catch (Exception e) {
            ExceptionHandler.handleException(e, redirectAttr);

        }
        return "redirect:/academico/anexo";
    }

    @RequestMapping("editar/{id}")
    public String editar(@PathVariable("id") Long id, Model model, HttpSession session) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        CicloAcademico ciclo = ds.getCicloAcademico();
        AnexoBoletin anexo = service.find(id);

        model.addAttribute("anexo", anexo);
        model.addAttribute("ciclo", ciclo);
        model.addAttribute("anexos", service.allAnexosSuperiores());
        return "academico/anexoboletin/anexoForm";
    }

    @ResponseBody
    @RequestMapping("cambiarEstado")
    public JsonResponse cambiarEstadoCarrera(AnexoBoletin anexo) {
        JsonResponse response = new JsonResponse();
        response.setSuccess(false);

        try {
            service.cambiarEstado(anexo);

            response.setMessage("Se cambio de estado satisfactoriamente.");
            response.setSuccess(true);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("allCarreras")
    public JsonResponse allCarreras(@RequestParam("nombre") String nombre, HttpSession session) {

        JsonNodeFactory jsonFactory = JsonNodeFactory.instance;
        JsonResponse response = new JsonResponse();

        try {
            List<Carrera> carreras = service.allCarrerasByNombre(nombre);
            ArrayNode jsonList = new ArrayNode(jsonFactory);

            for (Carrera carrera : carreras) {
                ObjectNode json = new ObjectNode(jsonFactory);

                json.put("id", carrera.getId());
                json.put("nombre", carrera.getNombre());
                json.put("codigo", carrera.getCodigo());
                json.put("tipoEstudio", !"".equals(this.getTipoEstudio(carrera.getTipo())) ? TipoCarreraEnum.valueOf(carrera.getTipo()).getValue() : "");
                json.put("modalidadEstudio", carrera.getModalidadEstudio().getNombre());

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

    public String getTipoEstudio(String tipo) {
        if (tipo.equals(TipoCarreraEnum.SEM.name()) || tipo.equals(TipoCarreraEnum.PMA.name())) {
            return "";
        }
        return tipo;
    }

}
