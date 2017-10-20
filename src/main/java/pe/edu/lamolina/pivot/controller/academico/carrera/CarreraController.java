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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableResponse;
import pe.albatross.zelpers.miscelanea.ExceptionHandler;
import pe.albatross.zelpers.miscelanea.JsonResponse;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.edu.lamolina.pivot.model.academico.Carrera;
import pe.edu.lamolina.pivot.model.academico.CicloAcademico;
import pe.edu.lamolina.pivot.model.academico.OrientacionCarrera;
import pe.edu.lamolina.pivot.zelper.constant.Constantine;
import pe.edu.lamolina.pivot.zelper.enums.EstadoEnum;
import pe.edu.lamolina.pivot.zelper.enums.ModalidadEstudioEnum;
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
    public DynatableResponse listCarrerasByModalidadEstudio(DynatableFilter filter, HttpSession session) {
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
                node.put("modalidad", carrera.getModalidadEstudio().getNombre());
                node.put("tipo", carrera.getTipo());
                node.put("tipoEnum", carrera.getTipoEnum().getValue());
                ArrayNode arrayOriCarrera = new ArrayNode(JsonNodeFactory.instance);
                for (OrientacionCarrera oriCarrera : carrera.getOrientacionCarrera()) {
                    ObjectNode node2 = new ObjectNode(JsonNodeFactory.instance);
                    node2.put("nombre", oriCarrera.getNombre());
                    arrayOriCarrera.add(node2);
                }
                node.set("oriCarreras", arrayOriCarrera);
                node.put("estado", carrera.getEstado());
                node.put("estadoEnum", carrera.getEstadoEnum().getValue());

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
    @RequestMapping("desactivar")
    public JsonResponse desactivar(Carrera carrera) {
        JsonResponse response = new JsonResponse();
        response.setSuccess(false);

        try {
            service.desactivar(carrera);

            response.setMessage("Registro desactivado.");
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

        return "sorteo/roles/rolesForm";
    }

}
