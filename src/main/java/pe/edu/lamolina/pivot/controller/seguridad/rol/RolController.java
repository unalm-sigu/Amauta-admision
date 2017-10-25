package pe.edu.lamolina.pivot.controller.seguridad.rol;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.thymeleaf.spring4.SpringTemplateEngine;
import pe.albatross.zelpers.dynatable.DynatableFilter;
import pe.albatross.zelpers.dynatable.DynatableResponse;
import pe.albatross.zelpers.miscelanea.ExceptionHandler;
import pe.albatross.zelpers.miscelanea.JsonResponse;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.edu.lamolina.pivot.model.seguridad.Menu;
import pe.edu.lamolina.pivot.model.seguridad.Rol;
import pe.edu.lamolina.pivot.model.seguridad.Sistema;

@Controller
@RequestMapping("seguridad/rol")
public class RolController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    RolService service;

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

        return "seguridad/rol/rolSistema";

    }

    @ResponseBody
    @RequestMapping("list")
    public DynatableResponse list(DynatableFilter filter, HttpSession session) {
        DynatableResponse json = new DynatableResponse();
        try {
            List<Rol> roles = service.allRol();

            ArrayNode array = new ArrayNode(JsonNodeFactory.instance);
            for (Rol rol : roles) {
                ObjectNode node = new ObjectNode(JsonNodeFactory.instance);

                node.put("id", rol.getId());
                node.put("nombre", rol.getNombre());
                node.put("codigo", rol.getCodigo());

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
    @RequestMapping("listRol")
    public JsonResponse listRol(@RequestParam("rol") Long idRol, HttpSession session) {

        JsonResponse response = new JsonResponse();

        try {

            List<Menu> menus = service.allMenuSystemByRol(new Sistema(1L), idRol);
            logger.debug("SIZE OF MENU {}", menus.size());
            ArrayNode array = createNodes(menus);
            response.setData(array);
            response.setSuccess(true);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        } finally {
            return response;
        }

    }

    private ArrayNode createNodes(List<Menu> menus) {
        JsonNodeFactory jsonFactory = JsonNodeFactory.instance;
        ArrayNode array = new ArrayNode(jsonFactory);
        for (Menu menu : menus) {
            ObjectNode jmenu = new ObjectNode(jsonFactory);
            jmenu.put("id", menu.getId());
            jmenu.put("nombre", menu.getNombre());
            jmenu.put("icono", menu.getIcono());
            jmenu.put("tipo", menu.getTipo());

            if (!menu.getMenus().isEmpty()) {
                ArrayNode arrayNode = createNodes(menu.getMenus());
                jmenu.set("nodes", arrayNode);
            }

            array.add(jmenu);
        }
        return array;
    }

    @ResponseBody
    @RequestMapping("save")
    public JsonResponse save(Rol rol, HttpSession session) {

        JsonResponse response = new JsonResponse();

        try {

            if (rol.getId() == null) {
                service.save(rol);
                response.setMessage("Registro creado satisfactoriamente");
            } else {
                service.update(rol);
                response.setMessage("Registro actualizado satisfactoriamente");
            }

            response.setSuccess(true);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        } finally {
            return response;
        }

    }

    @ResponseBody
    @RequestMapping("delete")
    public JsonResponse delete(Rol rol, HttpSession session) {

        JsonResponse response = new JsonResponse();

        try {
            service.delete(rol);
            response.setMessage("Registro eliminado satisfactoriamente");
            response.setSuccess(true);
        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (RuntimeException e) {
            ExceptionHandler.handleSpecial(e, response, "Este registro se encuentra relacionado a otros objetos del Sistema.");
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        } finally {
            return response;
        }
    }

    @RequestMapping("editarRol")
    public String editarRol(Rol rol, Model model) {

        Rol rolInfo = service.findRol(rol);

        model.addAttribute("rol", rolInfo);
        return "seguridad/rol/rolModal";
    }

    @RequestMapping("nuevoRol")
    public String nuevoRol(Model model) {

        return "seguridad/rol/rolModal";
    }
}
