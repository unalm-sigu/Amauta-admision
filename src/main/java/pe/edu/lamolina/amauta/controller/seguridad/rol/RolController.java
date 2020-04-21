package pe.edu.lamolina.amauta.controller.seguridad.rol;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.beans.PropertyEditorSupport;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
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
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableResponse;
import pe.albatross.zelpers.miscelanea.ExceptionHandler;
import pe.albatross.zelpers.miscelanea.JsonHelper;
import pe.albatross.zelpers.miscelanea.JsonResponse;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.model.enums.FuncionRolEstadoEnum;
import pe.edu.lamolina.model.enums.TipoPerfilCompaniaEnum;
import pe.edu.lamolina.model.general.Compania;
import pe.edu.lamolina.model.general.PerfilCompania;
import pe.edu.lamolina.model.seguridad.FuncionRol;
import pe.edu.lamolina.model.seguridad.Menu;
import pe.edu.lamolina.model.seguridad.Rol;
import pe.edu.lamolina.model.seguridad.Sistema;
import pe.edu.lamolina.model.seguridad.Usuario;
import pe.edu.lamolina.amauta.config.DespliegueConfig;
import pe.edu.lamolina.model.constantines.GlobalConstantine;
import pe.edu.lamolina.model.constantines.GlobalMessages;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;

@Controller
@RequestMapping("seguridad/rol")
public class RolController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    RolService service;

    @Autowired
    SpringTemplateEngine springHtml;

    @Autowired
    DespliegueConfig despliegueConfig;

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

            JsonNodeFactory jsonFactory = JsonNodeFactory.instance;
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
            List<Rol> roles = service.allRolByDynatable(filter, new Sistema(despliegueConfig.getSistema()));
            List<FuncionRol> funcionesRol = service.allFuncionRol(roles);
            Map<Long, List<FuncionRol>> funcionesRolMap = TypesUtil.convertListToMapList("rol.id", funcionesRol);

            ArrayNode array = new ArrayNode(jsonFactory);

            for (Rol rol : roles) {

                ObjectNode node = JsonHelper.createJson(rol, jsonFactory, true, new String[]{
                    "*",
                    "rolSuperior.*",
                    "rolSistema.id",
                    "rolSistema.sistema.*"
                });

                node.set("funciones", createFuncionesJson(rol, funcionesRolMap, TipoPerfilCompaniaEnum.FUNCION));
                node.set("cargos", createFuncionesJson(rol, funcionesRolMap, TipoPerfilCompaniaEnum.CARGO));
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

            List<Menu> menus = service.allMenuSystemByRol(new Sistema(despliegueConfig.getSistema()), idRol);
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
                service.save(rol, new Sistema(despliegueConfig.getSistema()));
                response.setMessage(GlobalMessages.CREATED);
            } else {
                service.update(rol);
                response.setMessage(GlobalMessages.UPDATED);
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
    @RequestMapping("delete")
    public JsonResponse delete(Rol rol, HttpSession session) {

        JsonResponse response = new JsonResponse();

        try {
            service.delete(rol, new Sistema(despliegueConfig.getSistema()));
            response.setMessage(GlobalMessages.DELETED);
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

    @ResponseBody
    @RequestMapping("editar")
    public JsonResponse editar(Rol rol, HttpSession session) {

        JsonResponse response = new JsonResponse();

        try {

            Rol rolDb = service.findRol(rol);

            JsonNodeFactory jsonFactory = JsonNodeFactory.instance;

            ObjectNode node = JsonHelper.createJson(rolDb, jsonFactory, true,
                    new String[]{
                        "*", "rolSuperior.*"
                    });

            response.setSuccess(true);
            response.setData(node);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("allperfilcompania")
    public JsonResponse allPerfilCompania(PerfilCompania perfilCompaniaForm, HttpSession session) {

        JsonResponse response = new JsonResponse();

        try {

            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
            Compania compania = ds.getCompania();

            List<PerfilCompania> perfilesCompania = service.allPerfilCompaniaByTipo(perfilCompaniaForm, compania);
            JsonNodeFactory jsonFactory = JsonNodeFactory.instance;
            ArrayNode array = new ArrayNode(jsonFactory);

            for (PerfilCompania perfilCompania : perfilesCompania) {

                ObjectNode node = JsonHelper.createJson(perfilCompania, jsonFactory, true,
                        new String[]{
                            "*"
                        });

                array.add(node);
            }

            response.setData(array);
            response.setSuccess(Boolean.TRUE);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        } finally {
            return response;
        }

    }

    @ResponseBody
    @RequestMapping("allfuncionrol")
    public JsonResponse allFuncionRol(FuncionRol funcionRolForm) {

        JsonResponse response = new JsonResponse();

        try {

            List<FuncionRol> funciones = service.allFuncionRolTipoPerfil(funcionRolForm);

            JsonNodeFactory jsonFactory = JsonNodeFactory.instance;
            ArrayNode array = new ArrayNode(jsonFactory);

            for (FuncionRol funcionRol : funciones) {

                ObjectNode node = JsonHelper.createJson(funcionRol, jsonFactory, true,
                        new String[]{
                            "*", "rol.*", "perfilCompania.*"
                        });

                array.add(node);
            }

            response.setData(array);
            response.setSuccess(Boolean.TRUE);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        } finally {
            return response;
        }

    }

    @ResponseBody
    @RequestMapping("savefuncionrol")
    public JsonResponse saveFuncionRol(FuncionRol funcionRol, HttpSession session) {

        JsonResponse response = new JsonResponse();

        try {

            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
            Usuario usuario = ds.getUsuario();

            service.saveFuncionRol(funcionRol, usuario);
            response.setMessage(GlobalMessages.CREATED);
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
    @RequestMapping("cambiarEstado")
    public JsonResponse cambiarEstado(FuncionRol funcionRol, HttpSession session) {

        JsonResponse response = new JsonResponse();

        try {

            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
            Usuario usuario = ds.getUsuario();

            service.cambiarEstado(funcionRol, usuario);
            response.setMessage(GlobalMessages.UPDATED);
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
    @RequestMapping("allRolSuperior")
    public JsonResponse allRolSuperior(@RequestParam("nombre") String nombre, HttpSession session) {

        JsonNodeFactory jsonFactory = JsonNodeFactory.instance;
        JsonResponse response = new JsonResponse();

        try {

            List<Rol> roles = service.allRolSuperior(nombre);
            ArrayNode jsonList = new ArrayNode(jsonFactory);

            for (Rol rol : roles) {
                ObjectNode node = JsonHelper.createJson(rol, jsonFactory, true,
                        new String[]{
                            "*"
                        });
                jsonList.add(node);
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

    private ArrayNode createFuncionesJson(Rol rol, Map<Long, List<FuncionRol>> mapFunciones, TipoPerfilCompaniaEnum tipoPerfilEnum) {

        JsonNodeFactory jsonFactory = JsonNodeFactory.instance;
        ArrayNode array = new ArrayNode(jsonFactory);

        List<FuncionRol> funcionesRoll = mapFunciones.get(rol.getId());
        if (funcionesRoll == null || funcionesRoll.isEmpty()) {
            return array;
        }

        for (FuncionRol funcionRol : funcionesRoll) {

            if (FuncionRolEstadoEnum.ACT.name().equalsIgnoreCase(funcionRol.getEstado())) {
                if (tipoPerfilEnum.name().equalsIgnoreCase(funcionRol.getPerfilCompania().getTipo())) {
                    ObjectNode node = JsonHelper.createJson(funcionRol.getPerfilCompania(), jsonFactory, true, new String[]{"*"});
                    array.add(node);
                }
            }
        }

        return array;
    }
}
