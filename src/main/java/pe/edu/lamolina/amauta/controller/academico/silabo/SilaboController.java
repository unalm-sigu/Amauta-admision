package pe.edu.lamolina.amauta.controller.academico.silabo;

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
import pe.edu.lamolina.model.academico.SilaboCurso;
import pe.edu.lamolina.model.constantines.AcademicoConstantine;
import pe.edu.lamolina.model.constantines.GlobalConstantine;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;

@Controller
@RequestMapping("academico/silabo")
public class SilaboController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    SilaboService service;

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
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
        return "academico/silabo/silabo";
    }

    @ResponseBody
    @RequestMapping("list")
    public DynatableResponse allByDynatable(DynatableFilter filter, HttpSession session) {
        DynatableResponse json = new DynatableResponse();

        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
            ArrayNode array = new ArrayNode(JsonNodeFactory.instance);

            List<SilaboCurso> silabos = service.allSilabo(filter);

            for (SilaboCurso silabo : silabos) {

                ObjectNode node = JsonHelper.createJson(silabo, JsonNodeFactory.instance, true,
                        new String[]{
                            "*",
                            "curso.id",
                            "curso.codigo",
                            "curso.nombre",
                            "curso.departamentoAcademico.id",
                            "curso.departamentoAcademico.nombre",
                            "curso.departamentoAcademico.codigo",
                            "curso.departamentoAcademico.nombreLargo",
                            "curso.departamentoAcademico.facultad.nombre",
                            "curso.departamentoAcademico.facultad.codigo",
                            "cicloVigenciaInicio.id",
                            "cicloVigenciaInicio.descripcion",
                            "cicloVigenciaInicio.descripcion2",
                            "cicloVigenciaFin.id",
                            "cicloVigenciaFin.descripcion",
                            "cicloVigenciaFin.descripcion2",
                            "curso.modalidadEstudio.id",
                            "curso.modalidadEstudio.nombre"
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
    public JsonResponse save(@RequestBody SilaboCurso silabo, HttpSession session) {

        JsonResponse response = new JsonResponse();

        try {

            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
            silabo.setUserRegistro(ds.getUsuario());
            if (silabo.getId() == null) {
                response.setMessage("Silabo agregado satisfactoriamente");
            } else {
                response.setMessage("Silabo actualizado satisfactoriamente");
            }
            service.save(silabo);
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
    public JsonResponse delete(@RequestBody SilaboCurso silabo, HttpSession session) {

        JsonResponse response = new JsonResponse();

        try {

            service.delete(silabo);
            response.setMessage("Silabo eliminado correctamente.");
            response.setSuccess(true);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;

    }

    @ResponseBody
    @RequestMapping("revision")
    public JsonResponse revision(@RequestBody SilaboCurso silabo, HttpSession session) {

        JsonResponse response = new JsonResponse();

        try {

            service.revision(silabo, response);
            response.setSuccess(true);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;

    }
}
