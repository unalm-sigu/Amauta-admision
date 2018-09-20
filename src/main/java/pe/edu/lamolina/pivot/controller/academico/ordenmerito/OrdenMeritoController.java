package pe.edu.lamolina.pivot.controller.academico.ordenmerito;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableResponse;
import pe.albatross.zelpers.miscelanea.ExceptionHandler;
import pe.albatross.zelpers.miscelanea.JsonHelper;
import pe.albatross.zelpers.miscelanea.JsonResponse;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.ControlOrdenMerito;
import pe.edu.lamolina.pivot.zelper.constant.Constantine;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

@Controller
@RequestMapping("academico/ordenmerito")
public class OrdenMeritoController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    OrdenMeritoService service;

    @RequestMapping(method = RequestMethod.GET)
    public String index(Model model, HttpSession session) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);

        CicloAcademico ciclo = findCicloAcademico(ds, session);
        CicloAcademico cicloActivo = service.findCicloActivo();
        List<CicloAcademico> ciclos = service.allCicloAcademicoForSelect();

        model.addAttribute("ciclo", ciclo);
        model.addAttribute("ciclos", ciclos);
        model.addAttribute("esCicloActivo", cicloActivo.getId().equals(ciclo.getId()));

        return "academico/ordenmerito/ordenmerito";
    }

    @ResponseBody
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public DynatableResponse list(DynatableFilter filter, HttpSession session) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        DynatableResponse json = new DynatableResponse();

        List<ControlOrdenMerito> list = service.allByDynatable(filter, findCicloAcademico(ds, session));
        ArrayNode array = new ArrayNode(JsonNodeFactory.instance);

        for (ControlOrdenMerito item : list) {
            array.add(JsonHelper.createJson(item, JsonNodeFactory.instance, new String[]{
                "escalaEnum",
                "estadoEnum",
                "facultad.nombre",
                "carrera.nombre",
                "totalAlumnos",
                "alumnosComputados",
                "noComputados",
                "alumnosCompletos",
                "alumnosIncompletos"
            }));
        }

        json.setData(array);
        json.setTotal(filter.getTotal());
        json.setFiltered(filter.getFiltered());

        return json;
    }

    @ResponseBody
    @RequestMapping(value = "/generardatos", method = RequestMethod.POST)
    public JsonResponse generarDatos(@RequestBody CicloAcademico cicloAcademico, HttpSession session) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        JsonResponse response = new JsonResponse();
        try {
            service.generarDatos(findCicloAcademico(ds, session), ds);
            response.setMessage("Datos generados");
            response.setSuccess(Boolean.TRUE);
        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping(value = "/calcularmeritos", method = RequestMethod.POST)
    public JsonResponse calcularMeritos(@RequestBody CicloAcademico cicloAcademico, HttpSession session) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        JsonResponse response = new JsonResponse();
        try {
            service.calcularMeritos(findCicloAcademico(ds, session), ds);
            response.setMessage("Orden de mérito generado");
            response.setSuccess(Boolean.TRUE);
        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("changeciclo")
    public JsonResponse changeCiclo(@RequestParam("ciclo") Long idCiclo, HttpSession session) {

        JsonResponse json = new JsonResponse();
        try {
            CicloAcademico ciclo = service.findCicloAcademico(new CicloAcademico(idCiclo));
            session.setAttribute(Constantine.CICLO_ORDEN_MERITO, ciclo);
            json.setSuccess(true);
            json.setMessage("Se cambio el ciclo académico satisfactoriamente");
        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, json);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, json);
        }
        return json;
    }

    private CicloAcademico findCicloAcademico(DataSessionPivot ds, HttpSession session) {
        CicloAcademico ciclo = (CicloAcademico) session.getAttribute(Constantine.CICLO_ORDEN_MERITO);
        if (ciclo == null) {
            ciclo = service.findCicloAcademico(ds.getCicloAcademico());
            session.setAttribute(Constantine.CICLO_ORDEN_MERITO, ciclo);
        }
        return ciclo;
    }
}
