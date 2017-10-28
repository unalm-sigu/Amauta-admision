package pe.edu.lamolina.pivot.controller.general.oficina;

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
import java.util.Map;
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
import pe.albatross.zelpers.dynatable.DynatableFilter;
import pe.albatross.zelpers.dynatable.DynatableResponse;
import pe.albatross.zelpers.miscelanea.ExceptionHandler;
import pe.albatross.zelpers.miscelanea.JsonResponse;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.albatross.zelpers.notify.Notificaciones;
import pe.edu.lamolina.pivot.model.academico.Carrera;
import pe.edu.lamolina.pivot.model.academico.DepartamentoAcademico;
import pe.edu.lamolina.pivot.model.academico.Facultad;
import pe.edu.lamolina.pivot.model.general.Colaborador;
import pe.edu.lamolina.pivot.model.general.Compania;
import pe.edu.lamolina.pivot.model.general.Oficina;
import pe.edu.lamolina.pivot.zelper.constant.Constantine;
import pe.edu.lamolina.pivot.zelper.enums.TipoOficinaEnum;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

@Controller
@RequestMapping("general/oficina")
public class OficinaController {

    @Autowired
    OficinaService service;

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
        return "general/oficina/oficina";
    }

    @ResponseBody
    @RequestMapping("all")
    public DynatableResponse all(DynatableFilter filter, HttpSession session) {

        DynatableResponse json = new DynatableResponse();

        try {

            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
            Compania compania = ds.getCompania();

            List<Oficina> oficinas = service.allByDynatable(filter, compania);
            List<Colaborador> colaboradores = service.allColaborador(oficinas);

            Map<Long, List<Colaborador>> colaboradoresMap = TypesUtil.convertListToMap("oficina.id", colaboradores);
            ArrayNode array = new ArrayNode(JsonNodeFactory.instance);

            for (Oficina oficina : oficinas) {

                List<Colaborador> colaboradorMap = colaboradoresMap.get(oficina.getId());

                if (colaboradorMap == null) {
                    colaboradorMap = new ArrayList();
                }

                ObjectNode node = new ObjectNode(JsonNodeFactory.instance);
                node.put("id", oficina.getId());
                node.put("nombre", oficina.getNombre());
                node.put("codigo", oficina.getCodigo());
                //node.put("tipo", TipoOficinaEnum.valueOf(oficina.getTipoOficina()).getValue());
                node.put("estado", oficina.getEsJefeEncargado());
                node.put("dependencia", oficina.getEsJefeEncargado());
                node.put("jefatura", oficina.getEsJefeEncargado());
                node.put("estado", oficina.getEstado());

                node.put("colaboradores", colaboradorMap.size());
                node.put("colaboradoresMap", oficina.getEsJefeEncargado());

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
        Compania compania = ds.getCompania();
        model.addAttribute("oficina", new Oficina());
        model.addAttribute("tipos", TipoOficinaEnum.values());
        return "general/oficina/oficinaForm";
    }

    @RequestMapping("{oficina}/update")
    public String update(@PathVariable("oficina") Long idOficina, Model model, HttpSession session) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        Compania compania = ds.getCompania();
        Oficina oficina = service.find(new Oficina(idOficina));
        model.addAttribute("oficina", oficina);
        model.addAttribute("tipos", TipoOficinaEnum.values());
        return "general/oficina/oficinaForm";
    }

    @RequestMapping("save")
    public String save(Oficina oficina, HttpSession session, RedirectAttributes redirectAttr) {
        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
            Compania compania = ds.getCompania();
            oficina.setCompania(compania);
            if (oficina.getId() != null) {
                service.update(oficina);
                Notificaciones.crearMsg("Oficina Actualizado", redirectAttr);
            } else {
                service.save(oficina);
                Notificaciones.crearMsg("Oficina Creada", redirectAttr);
            }
        } catch (PhobosException ex) {
            ExceptionHandler.handleException(ex, redirectAttr);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, redirectAttr);
        }
        return "redirect:/general/oficina";
    }

    @ResponseBody
    @RequestMapping("delete")
    public JsonResponse delete(Oficina oficina) {
        JsonResponse response = new JsonResponse();
        try {
            service.delete(oficina);
            response.setMessage("Oficina eliminada satisfactoriamente");
            response.setSuccess(Boolean.TRUE);
        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("allUnidadSuperior")
    public JsonResponse allUnidadSuperior(@RequestParam("nombre") String nombre, HttpSession session) {

        JsonNodeFactory jsonFactory = JsonNodeFactory.instance;
        JsonResponse response = new JsonResponse();

        try {

            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
            Compania compania = ds.getCompania();

            List<Oficina> oficinas = service.allUnidadSuperior(nombre, compania);
            ArrayNode array = new ArrayNode(jsonFactory);
            for (Oficina oficina : oficinas) {
                ObjectNode a = new ObjectNode(jsonFactory);
                a.put("id", oficina.getId());
                a.put("codigo", oficina.getCodigo());
                a.put("nombre", oficina.getNombre());
                array.add(a);
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
    @RequestMapping("allReferencia")
    public JsonResponse allReferencia(@RequestParam("tipo") String tipo, HttpSession session) {

        JsonNodeFactory jsonFactory = JsonNodeFactory.instance;
        JsonResponse response = new JsonResponse();

        try {

            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
            Compania compania = ds.getCompania();
            ArrayNode array = new ArrayNode(jsonFactory);

            if (TipoOficinaEnum.DEP.name().equalsIgnoreCase(tipo)) {
                List<DepartamentoAcademico> departamentos = service.allDepartamento(compania);
                for (DepartamentoAcademico departamento : departamentos) {
                    ObjectNode a = new ObjectNode(jsonFactory);
                    a.put("id", departamento.getId());
                    a.put("codigo", departamento.getCodigo());
                    a.put("nombre", departamento.getNombre());
                    array.add(a);
                }
            }
            if (TipoOficinaEnum.ESP.name().equalsIgnoreCase(tipo)) {
                List<Carrera> carreras = service.allCarrera(compania);
                for (Carrera carrera : carreras) {
                    ObjectNode a = new ObjectNode(jsonFactory);
                    a.put("id", carrera.getId());
                    a.put("codigo", carrera.getCodigo());
                    a.put("nombre", carrera.getNombre());
                    array.add(a);
                }
            }
            if (TipoOficinaEnum.FAC.name().equalsIgnoreCase(tipo)) {
                List<Facultad> facultades = service.allFacultad(compania);
                for (Facultad facultad : facultades) {
                    ObjectNode a = new ObjectNode(jsonFactory);
                    a.put("id", facultad.getId());
                    a.put("codigo", facultad.getCodigo());
                    a.put("nombre", facultad.getNombre());
                    array.add(a);
                }
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
    @RequestMapping("estado")
    public JsonResponse estado(Oficina oficina) {

        JsonResponse response = new JsonResponse();

        try {

            service.estado(oficina);
            response.setMessage("Oficina actualizado satisfactoriamente");
            response.setSuccess(Boolean.TRUE);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }

        return response;
    }
}
