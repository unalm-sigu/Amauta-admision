package pe.edu.lamolina.amauta.controller.general.producto;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import pe.albatross.zelpers.miscelanea.JsonHelper;
import pe.albatross.zelpers.miscelanea.JsonResponse;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;
import pe.edu.lamolina.model.almacen.Producto;
import pe.edu.lamolina.model.constantines.GlobalConstantine;

import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
@RequestMapping("general/aula/producto/resumen")
public class ProductoResumenController {

    @Autowired
    ProductoService service;

    @RequestMapping(method = RequestMethod.GET)
    public String index(Model model, HttpSession session) {

        return "general/aula/producto/resumen";
    }

    @ResponseBody
    @RequestMapping("resumen")
    public JsonResponse resumen(HttpSession session){
        JsonResponse response= new JsonResponse();
        JsonNodeFactory jsonNodeFactory = JsonNodeFactory.instance;
        List<Producto> productos=service.allProductosByAulas();
        ArrayNode array = new ArrayNode(JsonNodeFactory.instance);
//        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
        for(Producto producto : productos){
            ObjectNode jProducto = JsonHelper.createJson(producto, jsonNodeFactory,true,new String[]{
                    "*"
            });
            array.add(jProducto);
        }
        response.setData(array);
        response.setSuccess(Boolean.TRUE);
        return response;
    }
}
