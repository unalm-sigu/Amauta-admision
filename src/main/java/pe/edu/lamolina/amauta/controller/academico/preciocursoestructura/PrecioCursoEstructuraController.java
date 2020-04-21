package pe.edu.lamolina.amauta.controller.academico.preciocursoestructura;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
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
import pe.albatross.zelpers.miscelanea.ExceptionHandler;
import pe.albatross.zelpers.miscelanea.JsonHelper;
import pe.albatross.zelpers.miscelanea.JsonResponse;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.edu.lamolina.model.academico.PrecioCursoEstructura;
import pe.edu.lamolina.model.constantines.AcademicoConstantine;
import pe.edu.lamolina.model.constantines.GlobalConstantine;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;

@Controller
@RequestMapping("academico/preciocursoestructura")
public class PrecioCursoEstructuraController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    PrecioCursoEstructuraService service;

    @RequestMapping(method = RequestMethod.GET)
    public String index(Model model, HttpSession session) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);

        model.addAttribute("cicloAcademico", ds.getCicloAcademico());

        return "academico/preciocursoestructura/preciocursoestructura";
    }

    @ResponseBody
    @RequestMapping(method = RequestMethod.GET, value = "/list")
    public JsonResponse list(HttpSession session) {
        JsonResponse response = new JsonResponse();
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);

        try {
            List<PrecioCursoEstructura> list = service.allByCicloAcademico(ds.getCicloAcademico());
            ArrayNode array = new ArrayNode(JsonNodeFactory.instance);
            for (PrecioCursoEstructura precioCursoEstructura : list) {
                array.add(JsonHelper.createJson(precioCursoEstructura, JsonNodeFactory.instance, new String[]{
                    "id",
                    "precio",
                    "tpc",
                    "creditos",
                    "estado",
                    "cantidadGrupos"
                }));
            }
            response.setData(array);
        } catch (PhobosException pex) {
            ExceptionHandler.handlePhobosEx(pex, response);
        } catch (Exception ex) {
            ExceptionHandler.handleException(ex, response);
        }

        return response;
    }

    @ResponseBody
    @RequestMapping(method = RequestMethod.POST, value = "/save")
    public JsonResponse save(HttpSession session, @RequestBody List<PrecioCursoEstructura> listForm) {
        JsonResponse response = new JsonResponse();
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
        try {
            service.saveAll(listForm, ds.getCicloAcademico(), ds);
            response.setMessage("Precios actualizados");
            response.setSuccess(true);
        } catch (PhobosException pex) {
            ExceptionHandler.handlePhobosEx(pex, response);
        } catch (Exception ex) {
            ExceptionHandler.handleException(ex, response);
        }

        return response;
    }

}
