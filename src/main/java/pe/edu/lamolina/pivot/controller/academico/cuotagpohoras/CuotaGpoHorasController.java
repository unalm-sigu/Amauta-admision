package pe.edu.lamolina.pivot.controller.academico.cuotagpohoras;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableResponse;
import pe.albatross.zelpers.miscelanea.JsonHelper;
import pe.edu.lamolina.model.academico.CuotaGpoHoras;
import pe.edu.lamolina.pivot.zelper.constant.Constantine;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

@Controller
@RequestMapping("academico/cuotagpohoras")
public class CuotaGpoHorasController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    CuotaGpoHorasService service;

    @RequestMapping(method = RequestMethod.GET)
    public String index(Model model, HttpSession session) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        model.addAttribute("ciclo", ds.getCicloAcademico());
        return "academico/cuotagpohoras/cuotagpohoras";
    }

    @ResponseBody
    @RequestMapping("list")
    public DynatableResponse list(DynatableFilter filter, HttpSession session, HttpServletRequest request) {
        DynatableResponse json = new DynatableResponse();
        

        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
            
            List<CuotaGpoHoras> cuotagpohoras = service.allCuotasGpoHoras(filter, ds.getCicloAcademico()); 
            ArrayNode array = new ArrayNode(JsonNodeFactory.instance);

            for (CuotaGpoHoras cuota : cuotagpohoras) {
                ObjectNode node = JsonHelper.createJson(cuota, JsonNodeFactory.instance, true,
                        new String[]{
                            "anaexoBoletin.id", "anaexoBoletin.nombre", "anaexoBoletin.codigo", "anaexoBoletin.estado",
                            "grupoHoras.codigo", "grupoHoras.letra", "grupoHoras.tipoCiclo",                           
                            "cicloAcademico.descripcion2", 
                            "cuota", "asignadasSistema", "totalUtilizadas"
                        });

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



}
