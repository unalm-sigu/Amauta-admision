package pe.edu.lamolina.pivot.controller.academico.cargaadicional.factor1;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import java.util.List;
import javax.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
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
import pe.edu.lamolina.model.academico.Factor1CargaAdicional;
import pe.edu.lamolina.model.rrhh.CategoriaDocente;
import pe.edu.lamolina.model.rrhh.SituacionDocente;
import pe.edu.lamolina.model.constantines.AcademicoConstantine;
import pe.edu.lamolina.model.constantines.GlobalConstantine;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

@Controller
@RequestMapping("academico/cargaadicional/factor1")
public class CargaAdicionalFactor1Controller {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    CargaAdicionalFactor1Service service;

    @RequestMapping(method = RequestMethod.GET)
    public String index(Model model, HttpSession session) {

        List<CategoriaDocente> categorias = service.allCategoriaDocente();

        ArrayNode arrCategoria = new ArrayNode(JsonNodeFactory.instance);
        for (CategoriaDocente categoria : categorias) {
            arrCategoria.add(JsonHelper.createJson(categoria, JsonNodeFactory.instance));
        }

        ArrayNode arrSituacion = new ArrayNode(JsonNodeFactory.instance);
        List<SituacionDocente> situaciones = service.allSituacionDocente();
        for (SituacionDocente situacion : situaciones) {
            arrSituacion.add(JsonHelper.createJson(situacion, JsonNodeFactory.instance));
        }
        
        model.addAttribute("categorias", arrCategoria);
        model.addAttribute("situaciones", arrSituacion);
        
        return "academico/cargaadicional/cargaadicionalfactor1/cargaadicionalfactor1";
    }

    @ResponseBody
    @RequestMapping("list")
    public DynatableResponse list(DynatableFilter filter, HttpSession session) {

        DynatableResponse json = new DynatableResponse();
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);

        try {

            ArrayNode array = new ArrayNode(JsonNodeFactory.instance);

            List<Factor1CargaAdicional> list = service.allByDynatable(filter, ds.getCicloAcademico());

            for (Factor1CargaAdicional item : list) {
                array.add(JsonHelper.createJson(item, JsonNodeFactory.instance, new String[]{
                    "id",
                    "cicloAcademico.id",
                    "cicloAcademico.descripcion",
                    "situacionDocente.id",
                    "situacionDocente.nombre",
                    "situacionDocente.codigo",
                    "categoriaDocente.id",
                    "categoriaDocente.nombre",
                    "categoriaDocente.codigo",
                    "creditosMinimo",
                    "factor"
                }));
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
    @RequestMapping(value = "find/{id}", method = RequestMethod.GET)
    public JsonResponse find(@PathVariable Long id) {

        JsonResponse response = new JsonResponse();
        try {

            Factor1CargaAdicional fca = service.find(id);

            response.setData(JsonHelper.createJson(fca, JsonNodeFactory.instance, new String[]{
                "id",
                "cicloAcademico.id",
                "cicloAcademico.descripcion",
                "situacionDocente.id",
                "situacionDocente.nombre",
                "situacionDocente.codigo",
                "categoriaDocente.id",
                "categoriaDocente.nombre",
                "categoriaDocente.codigo",
                "creditosMinimo",
                "factor"
            }));

            response.setSuccess(true);
        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping(value = "save", method = RequestMethod.POST)
    public JsonResponse save(@RequestBody Factor1CargaAdicional factor1CargaAdicional, HttpSession session) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);

        JsonResponse response = new JsonResponse();
        try {
            if (factor1CargaAdicional.getId() == null) {
                service.save(factor1CargaAdicional, ds);
                response.setMessage("Condición registrada");
            } else {
                service.update(factor1CargaAdicional, ds);
                response.setMessage("Condición actualizada");
            }
            response.setSuccess(true);
        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping(value = "delete/{id}", method = RequestMethod.POST)
    public JsonResponse delete(@PathVariable Long id, HttpSession session) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);

        JsonResponse response = new JsonResponse();
        try {
            service.delete(id, ds);
            response.setMessage("Condición eliminada");
            response.setSuccess(true);
        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

}
