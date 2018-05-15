package pe.edu.lamolina.pivot.controller.academico.resolucion;

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
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableResponse;
import pe.albatross.zelpers.file.system.FileHelper;
import pe.albatross.zelpers.miscelanea.ExceptionHandler;
import pe.albatross.zelpers.miscelanea.JsonHelper;
import pe.albatross.zelpers.miscelanea.JsonResponse;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.tramite.Resolucion;
import pe.edu.lamolina.model.tramite.TipoResolucion;
import pe.edu.lamolina.pivot.zelper.constant.Constantine;
import pe.edu.lamolina.pivot.zelper.constant.Messages;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

@Controller
@RequestMapping("academico/resolucion")
public class ResolucionController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    ResolucionService resolucionService;

    private MultipartFile resolucionFile;

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
        model.addAttribute("ciclo", ds.getCicloAcademico());
        return "academico/resolucion/resolucion";
    }

    @ResponseBody
    @RequestMapping("listResoluciones")
    public DynatableResponse listTramites(DynatableFilter filter,
            HttpSession session) {
        DynatableResponse json = new DynatableResponse();
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        try {
            ArrayNode array = new ArrayNode(JsonNodeFactory.instance);
            CicloAcademico ciclo = ds.getCicloAcademico();
            DateTime today = new DateTime();

            List<Resolucion> resoluciones = resolucionService.allTramitesByFilter(filter);
            logger.debug("cantidad de resoluciones " + resoluciones.size());

            for (Resolucion resolucionEach : resoluciones) {

                ObjectNode resolucionJson = JsonHelper.createJson(resolucionEach, JsonNodeFactory.instance,
                        new String[]{
                            "*",
                            "oficina.*",
                            "tipoResolucion.*",
                            "userRegistro.persona.*"
                        });
                array.add(resolucionJson);
            }

            json.setData(array);
            json.setTotal(resoluciones.size());
            json.setFiltered(resoluciones.size());

        } catch (Exception e) {
            e.printStackTrace();
            json.setTotal(0);
        }
        return json;
    }

    @ResponseBody
    @RequestMapping("loadModalResolucion")
    public JsonResponse loadModalResolucion(
            @RequestParam(name = "resolucion", required = false) Long resolucionId,
            Model model,
            HttpSession session) {
        JsonResponse response = new JsonResponse();
        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);

            response.setSuccess(Boolean.TRUE);

            JsonNodeFactory jc = JsonNodeFactory.instance;
            Resolucion resolucion = new Resolucion();

            resolucionFile = null;
            //resolucion.setFecha(new Date());
            ObjectNode resolucionJson = JsonHelper.createJson(resolucion, jc, true,
                    new String[]{"*",
                        "oficina.*",
                        "tipoResolucion.*",
                        "userRegistro.*",
                        "userRegistro.persona.*"});

            List<TipoResolucion> tiposResoluciones = resolucionService.allTiposResolucion();
            ArrayNode tiposResolucionesJson = new ArrayNode(JsonNodeFactory.instance);
            for (TipoResolucion tipoResolucion : tiposResoluciones) {
                tiposResolucionesJson.addPOJO(tipoResolucion);
            }

            if (StringUtils.isBlank(resolucionJson.get("id").asText())) {
                resolucionJson.remove("id");
            }

            ObjectNode data = new ObjectNode(jc);
            data.set("resolucionJson", resolucionJson);
            data.set("tiposResolucionesJson", tiposResolucionesJson);

            response.setData(data);
        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (RuntimeException e) {
            ExceptionHandler.handleSpecial(e, response, e.getLocalizedMessage());
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("saveResolucion")
    public JsonResponse saveResolucion(
            @RequestBody Resolucion resolucion,
            Model model,
            HttpSession session) {
        JsonResponse response = new JsonResponse();
        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
            if (StringUtils.isBlank(resolucion.getRutaUrl())) {
                throw new PhobosException("Seleccion su archivo de resolucion.");
            }

            resolucionService.saveResolucion(resolucion, ds.getUsuario(), ds.getCicloAcademico(), ds.getOficinaMain());

            String message = "Resolución guardada correctamente.";
            response.setSuccess(true);
            response.setMessage(message);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (RuntimeException e) {
            ExceptionHandler.handleSpecial(e, response, Messages.FK_ERROR);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("addFile")
    public JsonResponse addFile(@RequestParam("file") MultipartFile file) {
        JsonResponse response = new JsonResponse();

        try {
            logger.debug("file {}, content type {}, size {}", file.getOriginalFilename(), file.getContentType(), file.getSize());

            String name = TypesUtil.getUnixTime() + file.getOriginalFilename();
            String absoluteName = Constantine.TMP_DIR + name;

            FileHelper.saveToDisk(file, absoluteName);

            response.setData(name);
            response.setMessage("Archivo cargado.");
            response.setSuccess(true);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception ex) {
            ExceptionHandler.handleException(ex, response);
        }
        return response;

    }

}
