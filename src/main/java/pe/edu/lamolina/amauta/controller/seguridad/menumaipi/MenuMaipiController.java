package pe.edu.lamolina.amauta.controller.seguridad.menumaipi;

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
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;
import pe.albatross.zelpers.miscelanea.ExceptionHandler;
import pe.albatross.zelpers.miscelanea.JsonResponse;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.edu.lamolina.model.enums.MenuTipoEnum;
import pe.edu.lamolina.model.seguridad.Menu;
import pe.edu.lamolina.model.seguridad.Rol;
import pe.edu.lamolina.model.seguridad.Sistema;
import pe.edu.lamolina.model.constantines.GlobalConstantine;
import pe.edu.lamolina.model.enums.EstadoEnum;

@Slf4j
@Controller
@AllArgsConstructor(onConstructor = @__(
        @Autowired))
@RequestMapping("seguridad/menumaipi")
public class MenuMaipiController {

    private final String rutaModulo = this.getClass().getAnnotation(RequestMapping.class).value()[0];

    private final MenuMaipiService service;
    private final SpringTemplateEngine springHtml;

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
        model.addAttribute("rutaModulo", rutaModulo);
        return "seguridad/menumaipi/menuMaipi";
    }

    @ResponseBody
    @RequestMapping("list")
    public JsonResponse list(HttpSession session) {

        JsonResponse response = new JsonResponse();

        try {

            List<Menu> menus = service.allMenuSystem(new Sistema(GlobalConstantine.ID_SISTEMA_MAIPI));
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
            jmenu.put("estado", menu.getEstado());
            jmenu.put("bucleActivar", menu.getBucleActivar());
            jmenu.put("modalidades", "[" + menu.getModalidades().replaceAll(",", " / ") + "]");

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
    public JsonResponse save(Menu menu, HttpSession session) {

        String message = "Registro creado satisfactoriamente";
        if (menu.getId() == null) {
            service.save(menu);
            service.reloadMenusMaipi();

        } else {
            service.update(menu);
            service.reloadMenusMaipi();
            message = "Registro actualizado satisfactoriamente";
        }

        JsonResponse response = new JsonResponse();
        response.setSuccess(true);
        response.setMessage(message);
        return response;

    }

    @ResponseBody
    @RequestMapping("update")
    public JsonResponse update(Menu menu, HttpSession session) {

        JsonResponse response = new JsonResponse();

        try {

            Menu menuBD = service.find(menu);
            boolean editable = menuBD.getMenus().size() > 0;

            log.debug("CANTIDAD {}", menuBD.getMenus().size());
            log.debug("EDITABLE {}", editable);

            List<MenuTipoEnum> tipos = service.allTiposMenuBySuperior(menuBD.getMenuSuperior());

            Context ctx = new Context();
            ctx.setVariable("editable", editable);
            ctx.setVariable("menu", menuBD);
            ctx.setVariable("menuSuperior", menuBD.getMenuSuperior());
            ctx.setVariable("tipos", tipos);

            String htmlContent = springHtml.process("seguridad/menumaipi/menuNuevoMaipi", ctx);

            response.setData(htmlContent);
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
    @RequestMapping("nuevo")
    public JsonResponse nuevo(@RequestParam("menuSuperior") Long idMenuSuperior, HttpSession session) {

        JsonResponse response = new JsonResponse();
        try {

            Menu menuNuevo = new Menu();
            menuNuevo.setPregrado(EstadoEnum.ACT.name());
            menuNuevo.setPosgrado(EstadoEnum.ACT.name());
            menuNuevo.setEspecial(EstadoEnum.ACT.name());
            menuNuevo.setVisitante(EstadoEnum.ACT.name());
            menuNuevo.setBucleActivar("NO");

            Menu menuSuperior = service.getMenu(new Menu(idMenuSuperior));
            List<MenuTipoEnum> tipos = service.allTiposMenuBySuperior(menuSuperior);

            Context ctx = new Context();
            ctx.setVariable("menu", menuNuevo);
            ctx.setVariable("menuSuperior", menuSuperior);
            ctx.setVariable("tipos", tipos);

            String htmlContent = springHtml.process("seguridad/menumaipi/menuNuevoMaipi", ctx);

            response.setData(htmlContent);
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
    public JsonResponse delete(Menu menu, HttpSession session) {

        JsonResponse response = new JsonResponse();
        try {
            service.delete(menu);
            response.setMessage("Registro eliminado satisfactoriamente");
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
    @RequestMapping("perfiles")
    public JsonResponse perfiles(Menu menu, HttpSession session) {

        JsonResponse response = new JsonResponse();

        try {

            Menu menuDb = service.getMenu(menu);
            List<Rol> rolesMenu = service.allRolMenu(menu);
            log.debug("rolesMenu {}", rolesMenu.size());
            List<Rol> roles = service.allRol(rolesMenu);
            log.debug("roles {}", roles.size());

            Context ctx = new Context();
            ctx.setVariable("roles", roles);
            ctx.setVariable("rolesMenu", rolesMenu);
            ctx.setVariable("menu", menuDb);
            String htmlContent = springHtml.process("seguridad/menumaipi/menuRol", ctx);
            response.setData(htmlContent);
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
    @RequestMapping("asignarRol")
    public JsonResponse asignarRol(Menu menu, HttpSession session) {

        JsonResponse response = new JsonResponse();

        try {

            service.asignarRol(menu);
            response.setMessage("Registro actualizado satisfactoriamente.");
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
    @RequestMapping("desAsignarRol")
    public JsonResponse desAsignarRol(Menu menu, HttpSession session) {

        JsonResponse response = new JsonResponse();

        try {

            service.desAsignarRol(menu);
            response.setMessage("Registro actualizado satisfactoriamente.");
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
    @RequestMapping("itemMenuUp")
    public JsonResponse itemMenuUp(Menu menu, HttpSession session) {

        JsonResponse response = new JsonResponse();

        try {

            service.itemMenuUp(menu);
            response.setMessage("Registro actualizado satisfactoriamente.");
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
    @RequestMapping("itemMenuDown")
    public JsonResponse itemMenuDown(Menu menu, HttpSession session) {

        JsonResponse response = new JsonResponse();

        try {

            service.itemMenuDown(menu);
            response.setMessage("Registro actualizado satisfactoriamente.");
            response.setSuccess(true);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        } finally {
            return response;
        }

    }

}
