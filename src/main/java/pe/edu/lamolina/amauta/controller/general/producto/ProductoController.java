package pe.edu.lamolina.amauta.controller.general.producto;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpSession;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableResponse;
import pe.albatross.zelpers.miscelanea.ExceptionHandler;
import pe.albatross.zelpers.miscelanea.JsonHelper;
import pe.albatross.zelpers.miscelanea.JsonResponse;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.edu.lamolina.amauta.controller.general.inventarioaula.InventarioAulaService;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;
import pe.edu.lamolina.model.almacen.Inventario;
import pe.edu.lamolina.model.constantines.GlobalConstantine;
import pe.edu.lamolina.model.general.Archivo;
import pe.edu.lamolina.model.general.InventarioTraslado;

import java.util.List;

@Controller
@RequestMapping("general/aula/producto")
public class ProductoController {

    @Autowired
    ProductoService service;

    @Autowired
    InventarioAulaService inventarioAulaService;



    @RequestMapping(method = RequestMethod.GET)
    public String index(Model model, HttpSession session){
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
//        List<Producto> allProductos = service.allProductosByAulas();
//        model.addAttribute("productos",allProductos);
        return "general/aula/producto/producto";
    }

    @ResponseBody
    @RequestMapping("list")
    public DynatableResponse allByDynatableee(DynatableFilter filter, HttpSession session) {
        DynatableResponse json = new DynatableResponse();
        try {
            List<Inventario> inventarios=inventarioAulaService.allByDynatable(filter);
            JsonNodeFactory jsonNodeFactory = JsonNodeFactory.instance;
            ArrayNode array=new ArrayNode(jsonNodeFactory);
            for(Inventario inventario: inventarios){
                ObjectNode node = JsonHelper.createJson(inventario, jsonNodeFactory,true,new String[]{
                        "*","almacen.aula.nombre","almacen.aula.id","producto.nombre","producto.codigo"
                });
                array.add(node);
            }
            json.setData(array);
            json.setTotal(filter.getTotal());
            json.setFiltered(filter.getFiltered());
        }catch (Exception e){
            e.printStackTrace();
            json.setTotal(0);
        }
        return json;
    }

    @ResponseBody
    @RequestMapping("traslados")
    public JsonResponse productosTraslados(@RequestParam("id") Integer id, HttpSession session){
        JsonNodeFactory jsonFactory = JsonNodeFactory.instance;
        JsonResponse response = new JsonResponse();

        try{
            List<InventarioTraslado> productosTraslados=inventarioAulaService.productosTraslado(id);
            ArrayNode array = new ArrayNode(jsonFactory);
            for (InventarioTraslado productos : productosTraslados ){
                ObjectNode node= JsonHelper.createJson(productos,jsonFactory,true,new String[]{
                        "*","inventario.producto.nombre","aulaInicio.nombre","aulaFin.nombre","userRegistro.persona.nombres","userRegistro.persona.paterno","userRegistro.persona.materno"
                });
                array.add(node);
            }
            response.setData(array);
//            response.setTotal(array.size());
            response.setSuccess(true);
        }catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }
}
