package pe.edu.lamolina.pivot.controller.general.inventarioaula;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.base.Strings;
import java.beans.PropertyEditorSupport;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import javax.servlet.http.HttpSession;
import org.apache.commons.io.FilenameUtils;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableResponse;
import pe.albatross.zelpers.file.system.FileHelper;
import pe.albatross.zelpers.miscelanea.ExceptionHandler;
import pe.albatross.zelpers.miscelanea.JsonHelper;
import pe.albatross.zelpers.miscelanea.JsonResponse;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.model.almacen.Inventario;
import pe.edu.lamolina.model.almacen.Producto;
import pe.edu.lamolina.model.almacen.ResumenInventario;
import pe.edu.lamolina.model.enums.CondicionInventarioEnum;
import pe.edu.lamolina.model.enums.TipoArticuloEnum;
import pe.edu.lamolina.model.general.Aula;
import pe.edu.lamolina.model.seguridad.Usuario;
import pe.edu.lamolina.pivot.zelper.constant.Constantine;
import pe.edu.lamolina.pivot.zelper.constant.Messages;
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
        model.addAttribute("condiciones", CondicionInventarioEnum.values());
        model.addAttribute("tipos", TipoArticuloEnum.values());
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
                jInventario.put("codeEdit", Strings.isNullOrEmpty(inventario.getCodigo()));
                array.add(jInventario);
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
    @RequestMapping("{idaula}/allresumen")
    public DynatableResponse allResumenByDynatable(@PathVariable("idaula") Long aula, DynatableFilter filter, HttpSession session) {

        DynatableResponse json = new DynatableResponse();
        try {

            JsonNodeFactory jFactory = JsonNodeFactory.instance;
            List<ResumenInventario> resumenes = service.allResumenByDynatable(filter, new Aula(aula));
            ArrayNode array = new ArrayNode(JsonNodeFactory.instance);

            for (ResumenInventario resumen : resumenes) {
                ObjectNode jInventario = JsonHelper.createJson(resumen, jFactory, true, new String[]{
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
            e.printStackTrace();
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
            Usuario user = ds.getUsuario();
            if (inventario.getId() == null) {
                service.save(inventario, user);
                response.setMessage("Inventario agregado satisfactoriamente");
            } else {
                service.update(inventario, user);
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
    @RequestMapping("updateresumen")
    public JsonResponse updateResumen(ResumenInventario resumen, HttpSession session) {
        JsonResponse response = new JsonResponse();
        try {
            if (resumen.getId() == null) {
                throw new PhobosException(Messages.ERROR_GENERAL);
            } else {
                service.updateResumen(resumen);
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

    @ResponseBody
    @RequestMapping("updateCode")
    public JsonResponse updateInventarioCode(@RequestBody List<Inventario> inventarios, HttpSession session) {
        JsonResponse response = new JsonResponse();
        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
            Usuario user = ds.getUsuario();
            service.updateInventarioCode(inventarios, user);
            response.setMessage(Messages.UPDATED);
            response.setSuccess(Boolean.TRUE);
        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("upload")
    public JsonResponse upload(@RequestParam("file") MultipartFile archivo, HttpSession session) {

        JsonResponse response = new JsonResponse();

        try {

            JsonNodeFactory jsonFactory = JsonNodeFactory.instance;
            ObjectNode json = new ObjectNode(jsonFactory);

            String fileExt = TypesUtil.getClean(FilenameUtils.getExtension(archivo.getOriginalFilename())).toLowerCase();
            String fileName = TypesUtil.getUnixTime() + "." + fileExt;
            String absoluteName = Constantine.TMP_DIR + fileName;
            FileHelper.saveToDisk(archivo, absoluteName);

            json.put("name", archivo.getOriginalFilename());
            json.put("ruta", fileName);
            json.put("mime", TypesUtil.getClean(FilenameUtils.getExtension(archivo.getOriginalFilename())));
            json.put("size", archivo.getSize());

            response.setData(json);
            response.setSuccess(true);
            response.setMessage("Carga satisfactoria del archivo");

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

}
