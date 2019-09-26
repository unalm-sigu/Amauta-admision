package pe.edu.lamolina.pivot.controller.tramite.plantillaConstancia;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.beans.PropertyEditorSupport;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
import pe.edu.lamolina.model.general.Idioma;
import pe.edu.lamolina.model.tramite.PlantillaDocumentoAcademico;
import pe.edu.lamolina.model.tramite.TipoDocumentoAcademico;
import pe.edu.lamolina.model.tramite.VariableGenerica;
import pe.edu.lamolina.model.tramite.VariablePlantilla;
import pe.edu.lamolina.pivot.controller.tramite.tipoConstancia.TipoConstanciaService;
import pe.edu.lamolina.pivot.zelper.constant.Constantine;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;
import pe.edu.lamolina.pivot.zelper.pdf.pdfHtml.PdfHtmlView;

@Controller
@RequestMapping("tramite/plantillaconstancia")
public class PlantillaConstanciaController {

    @Autowired
    PlantillaConstanciaService service;

    @Autowired
    TipoConstanciaService tipoConstanciaService;

    @Autowired
    PdfHtmlView pdfHtmlView;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

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

    @RequestMapping(value = "{id}", method = RequestMethod.GET)
    public String editarContenido(@PathVariable("id") Long idPlantilla, Model model) {
        PlantillaDocumentoAcademico documentoAcademico = service.find(new PlantillaDocumentoAcademico(idPlantilla));
        List<VariablePlantilla> variablePlantilla = service.allVariablePlantilla(documentoAcademico);
        List<VariableGenerica> variableGeneral = service.allVariableGeneral();
        ArrayNode arrayNode = new ArrayNode(JsonNodeFactory.instance);
        for (VariablePlantilla variablePlant : variablePlantilla) {
            arrayNode.add(JsonHelper.createJson(variablePlant, JsonNodeFactory.instance, new String[]{
                "*",
                "variableGenerica.*"
            }));
        }
        ArrayNode arrayVariable = new ArrayNode(JsonNodeFactory.instance);
        for (VariableGenerica variablePlant : variableGeneral) {
            arrayVariable.add(JsonHelper.createJson(variablePlant, JsonNodeFactory.instance, new String[]{
                "*"
            }));
        }

        model.addAttribute("id", documentoAcademico.getId());
        model.addAttribute("variables", arrayVariable.toString());
        model.addAttribute("variablePlantilla", arrayNode.toString());
        model.addAttribute("contenido", documentoAcademico.getContenido());
        model.addAttribute("tipoDocumentoNombre", documentoAcademico.getTipoDocumentoAcademico() != null ? documentoAcademico.getTipoDocumentoAcademico().getNombre() : "");
        model.addAttribute("idioma", documentoAcademico.getIdioma().getNombre());

        return "tramite/editarContenido/contenido";
    }

    @RequestMapping(method = RequestMethod.GET)
    public String index(Model model) {
        List<TipoDocumentoAcademico> list = tipoConstanciaService.all();
        List<Idioma> listIdioma = service.allIdioma();

        model.addAttribute("tipoDocumento", list.isEmpty() ? list : new TipoDocumentoAcademico().toArrayJson(list));
        model.addAttribute("idiomas", listIdioma.isEmpty() ? listIdioma : new Idioma().toArrayJson(listIdioma));
        return "tramite/plantillaConstancia/plantillaConstancia";
    }

    @ResponseBody
    @RequestMapping("updateContenido")
    public JsonResponse updateContenido(PlantillaDocumentoAcademico documentoAcademico, HttpSession session) {
        JsonResponse response = new JsonResponse();
        try {

            JsonNodeFactory jsonFactory = JsonNodeFactory.instance;
            response.setSuccess(false);
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
            PlantillaDocumentoAcademico psa = service.updateContenido(documentoAcademico, ds.getUsuario());

            ObjectNode jPlantillaDocumentoAcademico = JsonHelper.createJson(psa, jsonFactory, true, new String[]{
                "id",
                "variablePlantilla.*",
                "variablePlantilla.variableGenerica.*"
            });

            response.setData(jPlantillaDocumentoAcademico);
            response.setMessage("Registro actualizado satisfactoriamente");
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
    public JsonResponse update(@RequestBody PlantillaDocumentoAcademico plantillaDocumentoAcademico, HttpSession session) {
        JsonResponse response = new JsonResponse();
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        response.setSuccess(false);
        try {

            service.update(plantillaDocumentoAcademico, ds.getUsuario());
            response.setMessage("Se actualizó");
            response.setSuccess(true);
        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            e.printStackTrace();
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("updateVariable")
    public JsonResponse updateVariable(@RequestBody VariablePlantilla variablePlantilla, HttpSession session) {
        JsonResponse response = new JsonResponse();
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        response.setSuccess(false);
        try {
            if (variablePlantilla.getId() != null) {
                service.updateVariable(variablePlantilla, ds.getUsuario());
                response.setMessage("Se actualizó satisfactoriamente");
            }
            response.setSuccess(true);
        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            e.printStackTrace();
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("saveVariable")
    public JsonResponse saveVariable(@RequestBody VariablePlantilla variablePlantilla, HttpSession session) {
        JsonResponse response = new JsonResponse();
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        response.setSuccess(false);
        try {
            service.saveVariable(variablePlantilla, ds.getUsuario());
            response.setMessage("Se guardó satisfactoriamente");
            response.setSuccess(true);
        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            e.printStackTrace();
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("{idPlantilla}/allVariable")
    public JsonResponse allVariable(@PathVariable("idPlantilla") Long idPlantilla, HttpSession session) {
        JsonResponse response = new JsonResponse();
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        response.setSuccess(false);
        try {
            List<VariablePlantilla> variablePlantilla = service.allVariablePlantilla(new PlantillaDocumentoAcademico(idPlantilla));
            ArrayNode arrayNode = new ArrayNode(JsonNodeFactory.instance);
            for (VariablePlantilla variablePlant : variablePlantilla) {
                arrayNode.add(JsonHelper.createJson(variablePlant, JsonNodeFactory.instance, new String[]{
                    "*",
                    "variableGenerica.*"
                }));
            }
            response.setData(arrayNode);
            response.setSuccess(true);
        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            e.printStackTrace();
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("{idVariablePlantilla}/deleteVariable")
    public JsonResponse delete(@PathVariable("idVariablePlantilla") Integer idVariablePlantilla, HttpSession session) {
        JsonResponse response = new JsonResponse();
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        response.setSuccess(false);
        try {
            service.deleteVariable(idVariablePlantilla);
            response.setMessage("Se eliminó satisfactoriamente");

            response.setSuccess(true);
        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            e.printStackTrace();
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("save")
    public JsonResponse save(@RequestBody PlantillaDocumentoAcademico plantillaDocumentoAcademico, HttpSession session) {
        JsonResponse response = new JsonResponse();
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        try {
            service.save(plantillaDocumentoAcademico, ds.getUsuario());
            response.setMessage("Se guardó");
            response.setSuccess(Boolean.TRUE);
        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("list")
    public DynatableResponse all(DynatableFilter filter, HttpSession session) {
        DynatableResponse json = new DynatableResponse();
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        try {
            List<PlantillaDocumentoAcademico> list = service.all(filter);
            json.setData(new PlantillaDocumentoAcademico().toArrayJson(list));
            json.setTotal(filter.getTotal());
            json.setFiltered(filter.getFiltered());
        } catch (Exception e) {
            e.printStackTrace();
            json.setTotal(0);
        }
        return json;
    }

    @ResponseBody
    @RequestMapping("{id}/find")
    public JsonResponse find(@PathVariable("id") Long id, HttpSession session) {
        JsonResponse response = new JsonResponse();
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        try {
            PlantillaDocumentoAcademico documentoAcademico = service.find(new PlantillaDocumentoAcademico(id));
            response.setData(documentoAcademico);
            response.setSuccess(Boolean.TRUE);
        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("preview")
    public JsonResponse preview(PlantillaDocumentoAcademico plantillaForm, Long idalumno) {
        JsonResponse response = new JsonResponse();
        try {

            PlantillaGenerica plantillaGenerica = service.fillPlantilla(plantillaForm);
            String contenido = plantillaGenerica.getContenido();

            response.setData(contenido);
            response.setSuccess(Boolean.TRUE);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

}
