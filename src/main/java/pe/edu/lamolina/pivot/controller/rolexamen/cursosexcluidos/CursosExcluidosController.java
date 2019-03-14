package pe.edu.lamolina.pivot.controller.rolexamen.cursosexcluidos;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.Date;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
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
import pe.edu.lamolina.model.rolexamen.CursoExcluido;
import pe.edu.lamolina.model.rolexamen.CursoMasivoExamen;
import pe.edu.lamolina.model.rolexamen.RolExamenes;
import pe.edu.lamolina.pivot.zelper.constant.Constantine;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

@Controller
@RequestMapping("rolexamen/cursosexcluidos")
public class CursosExcluidosController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    CursosExcluidosService cursosExcluidosService;

    @RequestMapping(method = RequestMethod.GET)
    public String index(Model model, HttpSession session) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);

        List<RolExamenes> rolexamenes = cursosExcluidosService.allRolExamenesByCicloActivo(ds.getCicloAcademico());
        ArrayNode jRolexamenes = new ArrayNode(JsonNodeFactory.instance);
        for (RolExamenes rolexamen : rolexamenes) {
            ObjectNode rolExam = JsonHelper.createJson(rolexamen, JsonNodeFactory.instance, true,
                    new String[]{
                        "*",
                        "eventoCicloAcademico.cicloAcademico.descripcion",
                        "eventoCicloAcademico.fechaInicio", "eventoCicloAcademico.fechaFin",
                        "nombre", "estado", "fechaPublicacion"
                    });

            jRolexamenes.add(rolExam);
        }

        model.addAttribute("cicloAcademico", ds.getCicloAcademico());
        model.addAttribute("jRolexamenes", jRolexamenes.toString());
        return "rolexamen/cursosexcluidos/cursosexcluidos";
    }

    @ResponseBody
    @RequestMapping("excluirCurso")
    public JsonResponse excluirCurso(@RequestBody CursoExcluido cursoExcluido, HttpSession session) {
        JsonNodeFactory jsonFactory = JsonNodeFactory.instance;
        JsonResponse response = new JsonResponse();

        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
            ds.setFechaAccionAudit(new Date());
            cursosExcluidosService.excluirCurso(cursoExcluido, ds);
            response.setSuccess(true);
            response.setMessage("Curso excluido satisfactoriamnente");

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("listCursoExcluido")
    public JsonResponse listCursoExcluido(@RequestBody RolExamenes rolExamenes, HttpSession session, HttpServletRequest request) {
        JsonResponse response = new JsonResponse();
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        try {

            List<CursoExcluido> cursosExcluidos = cursosExcluidosService.allCursosExcluidosByRolExamenes(rolExamenes);

            ArrayNode jCursoMasivosByRolExamen = new ArrayNode(JsonNodeFactory.instance);
            for (CursoExcluido cursoExcluido : cursosExcluidos) {

                ObjectNode cursoMasivo = JsonHelper.createJson(cursoExcluido, JsonNodeFactory.instance, true,
                        new String[]{
                            "*",
                            "curso.*"
                        });

                jCursoMasivosByRolExamen.add(cursoMasivo);
            }
            response.setData(jCursoMasivosByRolExamen);
            response.setSuccess(Boolean.TRUE);
        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }

        return response;
    }

}
