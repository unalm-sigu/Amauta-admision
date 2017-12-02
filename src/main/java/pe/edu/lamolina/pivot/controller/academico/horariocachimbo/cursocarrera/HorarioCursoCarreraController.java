package pe.edu.lamolina.pivot.controller.academico.horariocachimbo.cursocarrera;

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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableResponse;
import pe.albatross.zelpers.miscelanea.ExceptionHandler;
import pe.albatross.zelpers.miscelanea.JsonResponse;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.edu.lamolina.pivot.model.academico.CicloAcademico;
import pe.edu.lamolina.pivot.model.academico.Curso;
import pe.edu.lamolina.pivot.model.academico.CursoCachimbos;
import pe.edu.lamolina.pivot.model.seguridad.Usuario;
import pe.edu.lamolina.pivot.zelper.constant.Constantine;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

@Controller
@RequestMapping("academico/horariocachimbo/curso")
public class HorarioCursoCarreraController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    HorarioCursoCarreraService service;

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
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        model.addAttribute("cicloAcademico", ds.getCicloAcademico());
        return "academico/horariocachimbo/cursocarrera/horariocursocarrera";
    }

    @ResponseBody
    @RequestMapping("list")
    public DynatableResponse list(DynatableFilter filter, HttpSession session) {
        DynatableResponse json = new DynatableResponse();
        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
            CicloAcademico cicloAcademico = ds.getCicloAcademico();
            List<CursoCachimbos> cursoCachimbos = service.allCursoCachimbos(filter, cicloAcademico);
            ArrayNode array = new ArrayNode(JsonNodeFactory.instance);
            for (CursoCachimbos cursoCachimbo : cursoCachimbos) {
                ObjectNode node = new ObjectNode(JsonNodeFactory.instance);
                node.put("id", cursoCachimbo.getId());
                node.put("codigo", cursoCachimbo.getCurso().getCodigo());
                node.put("nombre", cursoCachimbo.getCurso().getNombre());
                node.put("carrera", cursoCachimbo.getCarrera().getNombre());
                node.put("facultad", cursoCachimbo.getCarrera().getFacultad().getNombre());
                node.put("departamentoAcademico", cursoCachimbo.getCurso().getDepartamentoAcademico().getNombre());
                node.put("curso", cursoCachimbo.getCurso().getNombre());
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
    @RequestMapping("addCurso")
    public JsonResponse addCurso(CursoCachimbos cursoCachimbos, HttpSession session) {
        JsonResponse response = new JsonResponse();
        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
            CicloAcademico cicloAcademico = ds.getCicloAcademico();
            Usuario user = ds.getUsuario();
            cursoCachimbos.setIdUserCreacion(user.getId());
            cursoCachimbos.setCicloAcademico(cicloAcademico);
            service.addCurso(cursoCachimbos);
            response.setMessage("Curso agregado satisfactoriamente");
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
    public JsonResponse delete(CursoCachimbos cursoCachimbos) {
        JsonResponse response = new JsonResponse();
        try {
            service.delete(cursoCachimbos);
            response.setMessage("Curso eliminado satisfactoriamente");
            response.setSuccess(Boolean.TRUE);
        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("searchCurso")
    public JsonResponse searchCurso(@RequestParam("nombre") String nombre, HttpSession session) {
        JsonNodeFactory jsonFactory = JsonNodeFactory.instance;
        JsonResponse response = new JsonResponse();
        try {
            List<Curso> cursos = service.allCursoByName(nombre);
            ArrayNode jsonList = new ArrayNode(jsonFactory);
            for (Curso curso : cursos) {
                ObjectNode json = new ObjectNode(jsonFactory);
                json.put("id", curso.getId());
                json.put("nombre", curso.getNombre());
                json.put("codigo", curso.getCodigo());
                json.put("tpc", curso.getCodigo());
                json.put("departamentoAcademico", curso.getDepartamentoAcademico().getNombre());
                json.put("carrera",curso.getCarrera()!=null? curso.getCarrera().getNombre():"");
                json.put("facultad", curso.getCarrera()!=null?curso.getCarrera().getFacultad().getNombre():"");
                jsonList.add(json);
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

}
