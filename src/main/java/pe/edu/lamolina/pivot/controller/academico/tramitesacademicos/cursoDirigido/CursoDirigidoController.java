package pe.edu.lamolina.pivot.controller.academico.tramitesacademicos.cursoDirigido;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import static com.helger.commons.io.stream.StreamHelper.close;
import java.beans.PropertyEditorSupport;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.commons.io.IOUtils;
import org.joda.time.DateTime;
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
import pe.edu.lamolina.model.academico.Facultad;
import pe.edu.lamolina.model.enums.TipoOficinaEnum;
import pe.edu.lamolina.model.session.DataSessionMaipi;
import pe.edu.lamolina.model.tramite.AccionTramiteAcademico;
import pe.edu.lamolina.model.tramite.CursoDirigido;
import pe.edu.lamolina.model.tramite.Tramite;
import pe.edu.lamolina.pivot.controller.academico.tramitesacademicos.TramitesAcademicosService;
import pe.edu.lamolina.pivot.controller.seguridad.verificador.VerificadorService;
import pe.edu.lamolina.pivot.zelper.constant.Constantine;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

@Controller
@RequestMapping("academico/cursodirigido")
public class CursoDirigidoController {

    @Autowired
    CursoDirigidoService service;

    @Autowired
    VerificadorService verificadorService;

    @Autowired
    TramitesAcademicosService tramitesAcademicosService;

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
    public String index(Model model, HttpSession session, HttpServletRequest request) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        List<Facultad> facultades = verificadorService.allInstanciasByMenuRol(TipoOficinaEnum.FAC, request, ds);
        model.addAttribute("facultades", createFacultadesJson(facultades).toString());
        model.addAttribute("ciclo", ds.getCicloAcademico());
        return "tramite/cursoDirigido/cursoDirigido";
    }

    @ResponseBody
    @RequestMapping("list")
    public DynatableResponse list(DynatableFilter filter,
            HttpSession session, HttpServletRequest request) {
        DynatableResponse json = new DynatableResponse();
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        try {
            ArrayNode array = new ArrayNode(JsonNodeFactory.instance);
            List<CursoDirigido> cursoDirigidos = service.allByFacultades(filter, ds.getCicloAcademico());

            for (CursoDirigido cursoDirigido : cursoDirigidos) {
                ObjectNode node = JsonHelper.createJson(cursoDirigido, JsonNodeFactory.instance, new String[]{
                    "*",
                    "estado.*",
                    "curso.*",
                    "facultad.*",
                    "tramite.*",
                    "tramite.alumno.*",
                    "tramite.alumno.carrera.*",
                    "tramite.alumno.carrera.facultad.*",
                    "tramite.alumno.orientacionCarrera.*",
                    "tramite.alumno.situacionAcademica.*",
                    "tramite.alumno.persona.*",
                    "tramite.alumno.persona.tipoDocumento.*"
                });
                ArrayNode arrayAccion = new ArrayNode(JsonNodeFactory.instance);
//                for (AccionTramiteAcademico accionTramiteAcademico : cursoDirigido.getAccionTramiteAcademicos()) {
//                    ObjectNode objectNode = JsonHelper.createJson(accionTramiteAcademico, JsonNodeFactory.instance, new String[]{
//                        "*",
//                        "estadoTramiteInicio.*",
//                        "estadoTramiteFinal.*",});
//                    arrayAccion.add(objectNode);
//                }
                node.set("accionTramite", arrayAccion);
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
    @RequestMapping("update")
    public JsonResponse update(@RequestBody CursoDirigido cursoDirigido, HttpSession session) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        JsonResponse response = new JsonResponse();
        try {
            service.update(cursoDirigido, ds);
            response.setMessage("Se Actualizó el registro");
            response.setSuccess(Boolean.TRUE);
        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("anular")
    public JsonResponse anular(@RequestBody CursoDirigido cursoDirigido, HttpSession session) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        JsonResponse response = new JsonResponse();
        try {
            service.anular(cursoDirigido, ds);
            response.setMessage("Se Actualizó el registro");
            response.setSuccess(Boolean.TRUE);
        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @RequestMapping("repFacDirigido/{id}/reporte")
    public void cursoDirigidoReporte(Model model, HttpSession session, HttpServletResponse response, @PathVariable Long id) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);

        try {

            String fileName = tramitesAcademicosService.allcursoDirigidoFac(new Facultad(id), ds);

            pdfResponse(fileName, "Reporte Facultad " + id + ".pdf", response);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, model);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, model);
        }
    }

    @RequestMapping("listFacDirigido/{id}/reporte")
    public void listFacDirigido(Model model, HttpSession session, HttpServletResponse response, @PathVariable Long id) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);

        try {

            String fileName = tramitesAcademicosService.alllistCursoDirigidoFac(new Facultad(id), ds);

            pdfResponse(fileName, "Reporte Facultad " + id + ".pdf", response);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, model);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, model);
        }
    }

    private ArrayNode createFacultadesJson(List<Facultad> facultades) {
        ArrayNode array = new ArrayNode(JsonNodeFactory.instance);
        for (Facultad facultad : facultades) {
            ObjectNode node = JsonHelper.createJson(facultad, JsonNodeFactory.instance, true, new String[]{
                "id", "nombre", "codigo"
            });
            array.add(node);
        }
        return array;
    }

    private void pdfResponse(String name, String outputFile, HttpServletResponse response) throws IOException {
        if (!name.isEmpty()) {
            File filex = new File(name);
            if (!filex.exists()) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }
            DateTime hoy = new DateTime();

            response.reset();
            response.setBufferSize(Constantine.DEFAULT_BUFFER_SIZE_DOWNLOAD);
            response.setContentType("application/octet-stream");
            response.setHeader("Content-Disposition", "inline; filename=\"" + outputFile + "\"");

            BufferedInputStream input = null;
            BufferedOutputStream output = null;

            try {
                input = new BufferedInputStream(new FileInputStream(filex), Constantine.DEFAULT_BUFFER_SIZE_DOWNLOAD);
                output = new BufferedOutputStream(response.getOutputStream(), Constantine.DEFAULT_BUFFER_SIZE_DOWNLOAD);
                IOUtils.copy(input, output);
                response.flushBuffer();
            } finally {
                close(output);
                close(input);
            }
        }
    }
}
