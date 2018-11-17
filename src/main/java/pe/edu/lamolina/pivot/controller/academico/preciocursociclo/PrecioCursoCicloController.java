package pe.edu.lamolina.pivot.controller.academico.preciocursociclo;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.List;
import javax.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableResponse;
import pe.albatross.zelpers.miscelanea.ExceptionHandler;
import pe.albatross.zelpers.miscelanea.JsonHelper;
import pe.albatross.zelpers.miscelanea.JsonResponse;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.edu.lamolina.model.academico.CursoCicloAcademico;
import pe.edu.lamolina.pivot.zelper.constant.Constantine;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

@Controller
@RequestMapping("academico/preciocursociclo")
public class PrecioCursoCicloController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    PrecioCursoCicloService service;

    @RequestMapping(method = RequestMethod.GET)
    public String index(Model model, HttpSession session) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);

        model.addAttribute("ciclo", ds.getCicloAcademico());

        return "academico/preciocursociclo/precioCursoCiclo";
    }

    @ResponseBody
    @RequestMapping("list")
    public DynatableResponse list(DynatableFilter filter, HttpSession session) {
        DynatableResponse json = new DynatableResponse();

        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);

            List<CursoCicloAcademico> cursosCiclo = service.allCursoCiclo(filter, ds.getCicloAcademico());
            System.out.println(cursosCiclo);
            ArrayNode array = new ArrayNode(JsonNodeFactory.instance);

            for (CursoCicloAcademico cursoCiclo : cursosCiclo) {
                ObjectNode node = JsonHelper.createJson(cursoCiclo, JsonNodeFactory.instance, true,
                        new String[]{
                            "curso.id", "curso.estado", "curso.codigo", "curso.nombre", "curso.tpc", "curso.departamentoAcademico.nombre",
                            "cicloAcademico.descripcion",
                            "id", "cantidadGpoSecc", "estado", "precio", "precioPersonalizado", "precioFormato", "precioAdicional", "precioAdicionalFormato", "minimoAlumnos"
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

    @ResponseBody
    @RequestMapping("save")
    public JsonResponse save(@RequestBody List<CursoCicloAcademico> precioCursoCiclos, HttpSession session) {
        JsonResponse response = new JsonResponse();
        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
            service.save(precioCursoCiclos, ds.getCicloAcademico(), ds);
            response.setMessage("Guardado satisfactoriamente");
            response.setSuccess(true);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

}
