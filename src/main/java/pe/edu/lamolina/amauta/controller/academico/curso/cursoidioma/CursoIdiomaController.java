package pe.edu.lamolina.amauta.controller.academico.curso.cursoidioma;

import com.fasterxml.jackson.databind.node.ArrayNode;
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
import pe.albatross.zelpers.json.JaneHelper;
import pe.albatross.zelpers.miscelanea.ExceptionHandler;
import pe.albatross.zelpers.miscelanea.JsonResponse;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.edu.lamolina.model.academico.NombreCurso;
import pe.edu.lamolina.model.constantines.GlobalConstantine;
import pe.edu.lamolina.model.constantines.GlobalMessages;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;

@Controller
@RequestMapping("academico/curso/idioma")
public class CursoIdiomaController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    CursoIdiomaService service;

    @RequestMapping(method = RequestMethod.GET)
    public String index(Model model, HttpSession session) {
        return "academico/curso/cursoidioma/cursoidioma";
    }

    @ResponseBody
    @RequestMapping("list")
    public DynatableResponse allByDynatable(DynatableFilter filter) {
        
        DynatableResponse json = new DynatableResponse();

        try {

            List<NombreCurso> nombreCursos = service.allByDynatable(filter);
            
            ArrayNode array = JaneHelper.from(nombreCursos)
                    .join("curso")
                    .join("idioma")
                    .array();

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
    public JsonResponse save(@RequestBody NombreCurso nombreCurso, HttpSession session) {

        JsonResponse response = new JsonResponse();

        try {

            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
            service.save(nombreCurso, ds.getUsuario());
            response.setMessage(GlobalMessages.CREATED);
            response.setSuccess(true);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("update")
    public JsonResponse update(@RequestBody NombreCurso nombreCurso) {

        JsonResponse response = new JsonResponse();

        try {

            service.update(nombreCurso);
            response.setMessage(GlobalMessages.UPDATED);
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
    public JsonResponse delete(@RequestBody NombreCurso nombreCurso, HttpSession session) {
        
        JsonResponse response = new JsonResponse();
        
        try {
            
            service.delete(nombreCurso);
            response.setMessage(GlobalMessages.DELETED);
            response.setSuccess(true);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

}
