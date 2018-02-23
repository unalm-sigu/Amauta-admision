package pe.edu.lamolina.pivot.controller.oficinas.matricula.restriccionmatricula;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.List;
import javax.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableResponse;
import pe.albatross.zelpers.miscelanea.ExceptionHandler;
import pe.albatross.zelpers.miscelanea.JsonResponse;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.DeudaAlumno;
import pe.edu.lamolina.model.academico.TipoDeudaAlumno;
import pe.edu.lamolina.model.enums.ModalidadEstudioEnum;
import pe.edu.lamolina.model.general.Persona;
import pe.edu.lamolina.model.misc.FotoHelper;
import pe.edu.lamolina.pivot.zelper.constant.Constantine;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

@Controller
@RequestMapping("oficinas/matricula/restriccionmatricula")
public class RestriccionMatriculaController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    RestriccionMatriculaService service;

    @RequestMapping(method = RequestMethod.GET)
    public String index(Model model, HttpSession session) {
        List<TipoDeudaAlumno> tiposDeuda = service.allTipoDeudaAlumno();
        model.addAttribute("tiposDeuda", tiposDeuda);
        return "oficinas/matricula/restriccionmatricula/restriccionMatricula";
    }

    @ResponseBody
    @RequestMapping("list")
    public DynatableResponse list(DynatableFilter filter, HttpSession session) {
        DynatableResponse json = new DynatableResponse();

        try {
            FotoHelper helper = new FotoHelper();
            JsonNodeFactory factory = JsonNodeFactory.instance;
            ArrayNode array = new ArrayNode(factory);
            List<DeudaAlumno> deudas = service.allDeudaAlumno(filter);

            for (DeudaAlumno deuda : deudas) {
                ObjectNode node = new ObjectNode(factory);
                Alumno alumno = deuda.getAlumno();
                Persona persona = alumno.getPersona();

                node.put("id", deuda.getId());
                node.put("nombre", persona.getNombreCompleto());
                node.put("codigo", alumno.getCodigo());
                node.put("tipoDoc", persona.getTipoDocumento().getSimbolo());
                node.put("numeroDoc", persona.getNumeroDocIdentidad());
                node.put("carrera", alumno.getCarrera().getNombre());
                node.put("telefono", persona.getTelefono());
                node.put("rutaIcono", persona.getFoto());
                node.put("rutaFoto", helper.getRutaFoto(persona.getFoto(), persona.getSexo()));
                node.put("descripcion", deuda.getDescripcion());
                node.put("estado", deuda.getEstadoEnum().getValue());
                node.put("tipo", deuda.getTipoDeuda().getNombre());
                node.put("respNombre", deuda.getTipoDeuda().getResponsable().getPersona().getNombreCompleto());
                node.put("respTelefono", deuda.getTipoDeuda().getResponsable().getTelefono());
                if (alumno.getModalidadEstudio().getCodigoEnum() == ModalidadEstudioEnum.EPG) {
                    node.put("modalidadEstudio", alumno.getCarrera().getTipoEnum().getValue());
                } else {
                    node.put("modalidadEstudio", alumno.getModalidadEstudio().getNombre());
                }
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
    @RequestMapping("levantar")
    public JsonResponse levantar(@RequestParam("id") Long id, HttpSession session) {
        JsonResponse response = new JsonResponse();
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);

        try {
            DeudaAlumno deuda = new DeudaAlumno(id);
            service.levantarDeuda(deuda, ds);
            response.setSuccess(true);
            response.setMessage("Restricción levantada");
        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("anular")
    public JsonResponse deuda(DeudaAlumno deuda, HttpSession session) {
        JsonResponse response = new JsonResponse();
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);

        try {
            service.anularDeuda(deuda, ds);
            response.setSuccess(true);
            response.setMessage("Restricción anulada");
        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("guardar")
    public JsonResponse guardar(DeudaAlumno deuda) {
        JsonResponse response = new JsonResponse();
        try {
            service.guardarDeuda(deuda);
            response.setSuccess(true);
            response.setMessage("Restricción actualizada");
        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @RequestMapping("upload")
    public String upload(Model model, HttpSession session) {

        List<TipoDeudaAlumno> tiposDeuda = service.allTipoDeudaAlumno();

        model.addAttribute("tiposDeuda", tiposDeuda);

        return "oficinas/matricula/restriccionmatricula/restriccionMatriculaUpload";

    }

    @ResponseBody
    @RequestMapping("cargarDatos")
    public JsonResponse cargarDatos(@RequestParam("file") MultipartFile file,
            @RequestParam("idTipoDeudaAlumno") Long idTipoDeudaAlumno,
            Model model, HttpSession session) {
        JsonResponse json = new JsonResponse();
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);

        try {
            List<String> observados = service.cargarDeudas(file, new TipoDeudaAlumno(idTipoDeudaAlumno), ds);
            if (observados.isEmpty()) {
                json.setData(null);
            } else {
                logger.debug("Hay observaciones");
                JsonNodeFactory factory = JsonNodeFactory.instance;
                ArrayNode observaciones = new ArrayNode(factory);
                for (String observado : observados) {
                    observaciones.add(observado);
                }
                json.setData(observaciones);
            }
            json.setSuccess(true);
            json.setMessage("Restricciones de matrícula guardadas");
        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, json);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, json);
        } finally {
            return json;
        }
    }
}
