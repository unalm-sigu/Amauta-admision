package pe.edu.lamolina.pivot.controller.academico.cargaadicional.docente;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
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
import org.joda.time.DateTime;
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
import pe.albatross.zelpers.miscelanea.ExceptionHandler;
import pe.albatross.zelpers.miscelanea.JsonHelper;
import pe.albatross.zelpers.miscelanea.JsonResponse;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.edu.lamolina.model.academico.ConfiguraCargaAdicional;
import pe.edu.lamolina.model.academico.DocenteCiclo;
import pe.edu.lamolina.pivot.zelper.constant.Constantine;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

@Controller
@RequestMapping("academico/cargaadicional/docente")
public class CargaAdicionalDocenteController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    CargaAdicionalDocenteService service;

    @RequestMapping(method = RequestMethod.GET)
    public String index(Model model, HttpSession session) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        model.addAttribute("cicloAcademico", ds.getCicloAcademico());
        return "academico/cargaadicional/cargaadicionaldocente/cargaadicionaldocente";
    }

    @ResponseBody
    @RequestMapping("list")
    public DynatableResponse list(DynatableFilter filter, HttpSession session) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        DynatableResponse json = new DynatableResponse();

        try {
            ArrayNode array = new ArrayNode(JsonNodeFactory.instance);

            List<DocenteCiclo> list = service.allByDynatable(filter, ds.getCicloAcademico());

            for (DocenteCiclo item : list) {
                array.add(JsonHelper.createJson(item, JsonNodeFactory.instance, new String[]{
                    "id",
                    "docente.id",
                    "docente.codigo",
                    "docente.persona.id",
                    "docente.persona.apellidosNombres",
                    "docente.persona.numeroDocIdentidad",
                    "docente.persona.tipoDocumento.id",
                    "docente.persona.tipoDocumento.simbolo",
                    "docente.departamentoAcademico.id",
                    "docente.departamentoAcademico.nombre",
                    "docente.departamentoAcademico.facultad.id",
                    "docente.departamentoAcademico.facultad.nombre",
                    "promedioAlumnos",
                    "creditosTotal",
                    "creditosExceso",
                    "factor1",
                    "factor2",
                    "monto"
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
    @RequestMapping(value = "configuracion", method = RequestMethod.GET)
    public JsonResponse findConfiguracion(HttpSession session) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        JsonResponse response = new JsonResponse();

        try {
            ConfiguraCargaAdicional configuraCargaAdicional = service.findConfiguracionByCicloAcademico(ds.getCicloAcademico());
            response.setData(JsonHelper.createJson(configuraCargaAdicional, JsonNodeFactory.instance, new String[]{
                "estado",
                "rca",
                "minimoAlumnos"
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
    @RequestMapping(value = "configuracion/save", method = RequestMethod.POST)
    public JsonResponse findConfiguracion(@RequestBody ConfiguraCargaAdicional configuraCargaAdicional, HttpSession session) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        JsonResponse response = new JsonResponse();

        try {
            service.saveConfiguracion(configuraCargaAdicional, ds.getCicloAcademico(), ds);
            response.setMessage("Configuración actualizada");
            response.setSuccess(true);
        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }

        return response;
    }

    @ResponseBody
    @RequestMapping(value = "generar/carga", method = RequestMethod.POST)
    public JsonResponse generarCarga(HttpSession session) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        JsonResponse response = new JsonResponse();

        try {
            service.generarCarga(ds.getCicloAcademico(), ds);
            response.setMessage("Carga adicional generada");
            response.setSuccess(true);
        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }

        return response;
    }

    @ResponseBody
    @RequestMapping(value = "generar/montos", method = RequestMethod.POST)
    public JsonResponse generarMontos(HttpSession session) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        JsonResponse response = new JsonResponse();

        try {
            service.generarMontos(ds.getCicloAcademico(), ds);
            response.setMessage("Montos generados");
            response.setSuccess(true);
        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }

        return response;
    }

    @ResponseBody
    @RequestMapping(value = "eliminar/carga", method = RequestMethod.POST)
    public JsonResponse eliminarCarga(HttpSession session) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        JsonResponse response = new JsonResponse();

        try {
            service.eliminarCarga(ds.getCicloAcademico(), ds);
            response.setMessage("Carga adicional eliminada");
            response.setSuccess(true);
        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }

        return response;
    }

    @ResponseBody
    @RequestMapping(value = "eliminar/montos", method = RequestMethod.POST)
    public JsonResponse eliminarMontos(HttpSession session) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        JsonResponse response = new JsonResponse();

        try {
            service.eliminarMontos(ds.getCicloAcademico(), ds);
            response.setMessage("Montos eliminados");
            response.setSuccess(true);
        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }

        return response;
    }

    @ResponseBody
    @RequestMapping(value = "cerrar", method = RequestMethod.POST)
    public JsonResponse cerrar(HttpSession session) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        JsonResponse response = new JsonResponse();

        try {
            service.cerrar(ds.getCicloAcademico(), ds);
            response.setMessage("Carga adicional cerrada");
            response.setSuccess(true);
        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }

        return response;
    }

    @RequestMapping("reporte")
    public void reporteTodos(Model model, HttpSession session, HttpServletResponse response) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);

        try {
            String fileName = service.reporte(ds.getCicloAcademico());
            pdfResponse(fileName, String.format("Subvención por carga académica adicional %s.pdf", ds.getCicloAcademico().getDescripcion()), response);
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
