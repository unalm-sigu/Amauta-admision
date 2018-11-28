package pe.edu.lamolina.pivot.controller.general.inventarioaula;

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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableResponse;
import pe.albatross.zelpers.miscelanea.ExceptionHandler;
import pe.albatross.zelpers.miscelanea.JsonHelper;
import pe.albatross.zelpers.miscelanea.JsonResponse;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.edu.lamolina.model.almacen.Inventario;
import pe.edu.lamolina.model.almacen.Producto;
import pe.edu.lamolina.model.enums.CondicionInventarioEnum;
import pe.edu.lamolina.model.enums.TipoArticuloEnum;
import pe.edu.lamolina.model.general.Aula;
import pe.edu.lamolina.model.seguridad.Usuario;
import pe.edu.lamolina.pivot.zelper.constant.Constantine;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

@Controller
@RequestMapping("general/aula/inventario")
public class InventarioAulaController {

    @Autowired
    InventarioAulaService service;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

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

    @RequestMapping("{idaula}")
    public String index(@PathVariable("idaula") Long idaula, Model model, HttpSession session) {
        model.addAttribute("aula", service.findAula(idaula));
        model.addAttribute("condiciones", CondicionInventarioEnum.values());
        model.addAttribute("tipos", TipoArticuloEnum.values());
        return "general/inventarioaula/inventarioaula";
    }

    @RequestMapping("{idaula}/resumen")
    public String resumen(@PathVariable("idaula") Long idaula, Model model, HttpSession session) {
        model.addAttribute("aula", service.findAula(idaula));
        return "general/inventarioaula/inventarioaularesumen";
    }

    @ResponseBody
    @RequestMapping("allProducto")
    public JsonResponse allProducto(HttpSession session) {
        JsonResponse response = new JsonResponse();
        try {

            JsonNodeFactory jFactory = JsonNodeFactory.instance;
            List<Producto> productos = service.allProducto();
            ArrayNode array = new ArrayNode(JsonNodeFactory.instance);

            for (Producto producto : productos) {
                ObjectNode jInventario = JsonHelper.createJson(producto, jFactory, true, new String[]{
                    "*",
                    "tipoProducto.*",
                    "productoSuperior.*",
                    "unidadPrincipal.*",
                    "productos.*"
                });
                array.add(jInventario);
            }
            response.setData(array);
            response.setSuccess(Boolean.TRUE);
        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("{idaula}/all")
    public DynatableResponse allByDynatable(@PathVariable("idaula") Long aula, DynatableFilter filter, HttpSession session) {

        DynatableResponse json = new DynatableResponse();
        try {

            JsonNodeFactory jFactory = JsonNodeFactory.instance;
            List<Inventario> inventarios = service.allByDynatable(filter, new Aula(aula));
            ArrayNode array = new ArrayNode(JsonNodeFactory.instance);

            for (Inventario inventario : inventarios) {
                ObjectNode jInventario = JsonHelper.createJson(inventario, jFactory, true, new String[]{
                    "*",
                    "almacen.*",
                    "producto.*",
                    "producto.productoSuperior.*",
                    "producto.unidadPrincipal.*"
                });
                array.add(jInventario);
            }

            json.setData(array);
            json.setTotal(filter.getTotal());
            json.setFiltered(filter.getFiltered());

        } catch (Exception e) {
            json.setTotal(0);
        }
        return json;
    }

    @ResponseBody
    @RequestMapping("save")
    public JsonResponse save(Inventario inventario, HttpSession session) {
        JsonResponse response = new JsonResponse();
        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
            if (inventario.getId() == null) {
                Usuario user = ds.getUsuario();
                service.save(inventario, user);
                response.setMessage("Inventario agregado satisfactoriamente");
            } else {
                service.update(inventario);
                response.setMessage("Inventario actualizado satisfactoriamente");
            }
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
    public JsonResponse delete(Inventario inventario) {
        JsonResponse response = new JsonResponse();
        try {
            service.delete(inventario);
            response.setMessage("Inventario eliminado satisfactoriamente");
            response.setSuccess(Boolean.TRUE);
        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("update")
    public JsonResponse update(Inventario inventarioForm, HttpSession session) {
        JsonResponse response = new JsonResponse();
        try {

            JsonNodeFactory jFactory = JsonNodeFactory.instance;
            Inventario inventario = service.find(inventarioForm);
            ObjectNode jInventario = JsonHelper.createJson(inventario, jFactory, true, new String[]{
                "*",
                "almacen.*",
                "producto.*",
                "producto.productoSuperior.*",
                "producto.unidadPrincipal.*"
            });

            response.setData(jInventario);
            response.setSuccess(true);
        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("saveproducto")
    public JsonResponse saveProducto(Producto producto, HttpSession session) {
        JsonResponse response = new JsonResponse();
        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
            if (producto.getId() == null) {
                Usuario user = ds.getUsuario();
                service.saveProducto(producto, user);
                response.setMessage("Artículo agregado satisfactoriamente");
                JsonNodeFactory jFactory = JsonNodeFactory.instance;
                ObjectNode jProducto = JsonHelper.createJson(producto, jFactory, true, new String[]{
                    "*"
                });
                response.setData(jProducto);
            }
            response.setSuccess(Boolean.TRUE);
        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

}
