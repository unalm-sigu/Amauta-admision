package pe.edu.lamolina.amauta.controller.tramite.tramiteTitulo;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import static com.helger.commons.io.stream.StreamHelper.close;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.joda.time.DateTime;
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
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;
import pe.edu.lamolina.model.constantines.GlobalConstantine;
import pe.edu.lamolina.model.tramite.Tramite;
import pe.edu.lamolina.model.tramite.TramiteTitulo;

@Controller
@RequestMapping("academico/tramiteacademico/tramitetitulo")
public class TramitesTituloController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    TramitesTituloService tramitesTituloService;

    @RequestMapping(method = RequestMethod.GET)
    public String index(Model model, HttpSession session) {

        return "academico/tramitescademicos/tramiteTitulo/tramitesTitulo";
    }

    @ResponseBody
    @RequestMapping("list")
    public DynatableResponse listTramites(DynatableFilter filter,
            HttpSession session) {
        DynatableResponse json = new DynatableResponse();
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
        try {
            ArrayNode array = new ArrayNode(JsonNodeFactory.instance);
            List<TramiteTitulo> tramitesTitulos = tramitesTituloService.allTramitesByFilter(filter, ds);

            String[] mapperTramite = new String[]{
                "*",
                "tramite.*",
                "tramite.persona.*",
                "tramite.alumno.*",
                "tramite.alumno.planCurricular.*",
                "tramite.alumno.carrera.*",
                "tramite.alumno.carrera.facultad.*",
                "tramite.compania.*",
                "tramite.cicloAcademico.*",
                "tramite.tipoTramite.codigo",
                "tramite.tipoTramite.nombre",
                "tramite.tipoTramite.esReincorporacionPregrado",
                "tramite.tipoTramite.esCursoDirigido",
                "tramite.tipoTramite.oficina.*",
                "tramite.userRegistro.*",
                "tramite.userRegistro.persona.*",
                "tramite.userRespuesta.*",
                "tramite.formularioEstadoTramite.*"
            };

            String[] mapperEstadoTramite = new String[]{
                "tramite.estadoTramite.nombre",
                "tramite.estadoTramite.id",
                "tramite.estadoTramite.nombre"
            };

            String[] mapperTramiteComplex = (String[]) ArrayUtils.addAll(mapperTramite, mapperEstadoTramite);

            JsonNodeFactory jc = JsonNodeFactory.instance;
            for (TramiteTitulo tramite : tramitesTitulos) {
                ObjectNode tramiteJson = JsonHelper.createJson(tramite, jc, false, mapperTramiteComplex);

                array.add(tramiteJson);
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
    public JsonResponse saveTitulo(@RequestBody TramiteTitulo tramiteTitulo, HttpSession session) {

        JsonResponse response = new JsonResponse();

        try {

            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);

            tramitesTituloService.saveTitulo(tramiteTitulo, ds);
            response.setMessage("Tramite registrado correctamente");
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
    public JsonResponse anularTitulo(@RequestBody TramiteTitulo tramiteTitulo, HttpSession session) {

        JsonResponse response = new JsonResponse();

        try {

            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);

            tramitesTituloService.anularTitulo(tramiteTitulo, ds);
            response.setMessage("Tramite anulado correctamente");
            response.setSuccess(Boolean.TRUE);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }

        return response;
    }

    @RequestMapping("{id}/reporte")
    public void bachillerReporte(Model model, HttpSession session, HttpServletResponse response, @PathVariable Long id) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);

        try {
            Tramite tramite = tramitesTituloService.findByTramite(id);
            String fileName = tramitesTituloService.TituloReporte(tramite, ds);
            String name = "Información Titulo " + tramite.getAlumno().getPersona().getPaterno() + " - " + tramite.getNumero() + ".pdf";
            pdfResponse(fileName, name, response);
        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, model);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, model);
        }
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
            response.setBufferSize(GlobalConstantine.DEFAULT_BUFFER_SIZE_DOWNLOAD);
            response.setContentType("application/octet-stream");
            response.setHeader("Content-Disposition", "inline; filename=\"" + outputFile + "\"");

            BufferedInputStream input = null;
            BufferedOutputStream output = null;

            try {
                input = new BufferedInputStream(new FileInputStream(filex), GlobalConstantine.DEFAULT_BUFFER_SIZE_DOWNLOAD);
                output = new BufferedOutputStream(response.getOutputStream(), GlobalConstantine.DEFAULT_BUFFER_SIZE_DOWNLOAD);
                IOUtils.copy(input, output);
                response.flushBuffer();
            } finally {
                close(output);
                close(input);
            }
        }
    }
}
