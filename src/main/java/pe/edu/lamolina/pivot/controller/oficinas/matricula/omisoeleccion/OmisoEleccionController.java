package pe.edu.lamolina.pivot.controller.oficinas.matricula.omisoeleccion;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.Arrays;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableResponse;
import pe.albatross.zelpers.miscelanea.ExceptionHandler;
import pe.albatross.zelpers.miscelanea.JsonHelper;
import pe.albatross.zelpers.miscelanea.JsonResponse;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.AlumnoOmisoEleccion;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.MatriculaResumen;
import pe.edu.lamolina.model.aporte.AporteAlumnoCiclo;
import pe.edu.lamolina.model.aporte.ResumenAporteAlumno;
import static pe.edu.lamolina.model.enums.EstadoAporteEnum.DEBE;
import static pe.edu.lamolina.model.enums.EstadoAporteEnum.PAGO;
import pe.edu.lamolina.model.enums.ModalidadEstudioEnum;
import pe.edu.lamolina.model.enums.MotivoOmisoEnum;
import pe.edu.lamolina.model.finanzas.DeudaAlumno;
import pe.edu.lamolina.model.general.Persona;
import pe.edu.lamolina.pivot.controller.matricula.matriculable.MatriculableService;
import pe.edu.lamolina.pivot.zelper.constant.Constantine;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

@Controller
@RequestMapping("oficinas/matricula/omisoeleccion")
public class OmisoEleccionController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final String rutaModulo = this.getClass().getAnnotation(RequestMapping.class).value()[0];

    @Autowired
    OmisoEleccionService service;
    @Autowired
    MatriculableService matriculableService;

    @RequestMapping(method = RequestMethod.GET)
    public String index(Model model, HttpSession session) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        List<CicloAcademico> cicloAcademicos = service.allCicloAcademico(ds.getCicloAcademico());
        ArrayNode arrayCiclo = new ArrayNode(JsonNodeFactory.instance);
        ArrayNode arrayEnum = new ArrayNode(JsonNodeFactory.instance);
        for (CicloAcademico cicloAcademico : cicloAcademicos) {
            arrayCiclo.add(JsonHelper.createJson(cicloAcademico, JsonNodeFactory.instance, new String[]{"*"}));
        }
        for (MotivoOmisoEnum enums : MotivoOmisoEnum.values()) {
            ObjectNode node = new ObjectNode(JsonNodeFactory.instance);
            node.put("name", enums.name());
            node.put("value", enums.getValue());
            arrayEnum.add(node);
        }
        model.addAttribute("motivos", arrayEnum);
        model.addAttribute("ciclos", arrayCiclo);
        model.addAttribute("rutaModulo", rutaModulo);
        return "oficinas/matricula/omisoeleccion/omisoeleccion";
    }

    @RequestMapping("load")
    public String load(Model model, HttpSession session) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        List<CicloAcademico> cicloAcademicos = service.allCicloAcademico(ds.getCicloAcademico());
        ArrayNode arrayCiclo = new ArrayNode(JsonNodeFactory.instance);

        for (CicloAcademico cicloAcademico : cicloAcademicos) {
            arrayCiclo.add(JsonHelper.createJson(cicloAcademico, JsonNodeFactory.instance, new String[]{"*"}));
        }

        model.addAttribute("ciclos", arrayCiclo);
        return "oficinas/matricula/omisoeleccion/loadOmisoEleccion";
    }

    @ResponseBody
    @RequestMapping("list")
    public DynatableResponse list(DynatableFilter filter, HttpSession session) {
        DynatableResponse json = new DynatableResponse();

        try {
            JsonNodeFactory factory = JsonNodeFactory.instance;
            ArrayNode array = new ArrayNode(factory);
            List<Alumno> alumnos = service.allDeudaAlumno(filter);

            for (Alumno alumno : alumnos) {
                ObjectNode node = JsonHelper.createJson(alumno, factory, new String[]{
                    "id",
                    "codigo",
                    "modalidadEstudio.*",
                    "persona.nombreCompleto",
                    "persona.tipoDocumento.*",
                    "persona.numeroDocIdentidad",
                    "persona.rutaFoto",
                    "carrera.*",
                    "carrera.facultad.*"});

                ArrayNode arrayOmiso = new ArrayNode(factory);
                for (AlumnoOmisoEleccion alumnoOmisoEleccion : alumno.getAlumnoOmisoEleccions()) {
                    ObjectNode nodeOmiso = JsonHelper.createJson(alumnoOmisoEleccion, factory, new String[]{
                        "*",
                        "alumno.id",
                        "cicloAcademico.*"});
                    arrayOmiso.add(nodeOmiso);
                }
                node.set("alumnoOmisoEleccions", arrayOmiso);
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
    @RequestMapping("saveOmision")
    public JsonResponse saveOmision(@RequestBody AlumnoOmisoEleccion omisoEleccion, HttpSession session) {
        JsonResponse response = new JsonResponse();
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);

        try {

            service.saveOmision(omisoEleccion, ds);
            service.modificarAporte(omisoEleccion.getAlumno(), ds);

            response.setSuccess(true);
            response.setMessage("Se realizó el registro.");
        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("anularOmision")
    public JsonResponse anularOmision(@RequestBody Alumno alumno, HttpSession session) {
        JsonResponse response = new JsonResponse();
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);

        try {
            alumno.getAlumnoOmisoEleccions().get(0).setMotivoAnulacion(alumno.getMotivoAnulacion());
            service.anularOmision(alumno, ds.getCicloAcademico(), ds);

            response.setSuccess(true);
            response.setMessage("Se realizó la actualización satisfactoriamente.");
        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("cargarDatos")
    public JsonResponse cargarDatos(@RequestParam("file") MultipartFile file,
            @RequestParam("cicloAcademico") String codigoCiclo,
            Model model, HttpSession session) {
        JsonResponse json = new JsonResponse();
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);

        try {
            List<String> observados = service.cargarDeudas(file, codigoCiclo, ds);
            if (observados.isEmpty()) {
                json.setData(null);
                json.setSuccess(true);
                json.setMessage("Deudas por elecciones guardadas");
            } else {
                logger.debug("Hay observaciones");
                JsonNodeFactory factory = JsonNodeFactory.instance;
                ArrayNode observaciones = new ArrayNode(factory);
                for (String observado : observados) {
                    observaciones.add(observado);
                }
                json.setData(observaciones);
                json.setSuccess(false);
                json.setMessage("Hay observaciones");
            }
        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, json);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, json);
        } finally {
            return json;
        }
    }

    @ResponseBody
    @RequestMapping("allAlumnoByNombre")
    public JsonResponse allAlumnoByNombre(@RequestParam("nombre") String nombre, HttpSession session) {
        JsonResponse response = new JsonResponse();
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        try {

            JsonNodeFactory jsonFactory = JsonNodeFactory.instance;
            List<Alumno> lista = service.allAlumnoByNombre(nombre, ds);
            ArrayNode jsonList = new ArrayNode(jsonFactory);

            for (Alumno alum : lista) {
                jsonList.add(JsonHelper.createJson(alum, jsonFactory, true,
                        new String[]{
                            "id",
                            "codigo",
                            "modalidadEstudio.id",
                            "modalidadEstudio.nombre",
                            "carrera.codigo",
                            "carrera.nombre",
                            "carrera.facultad.codigo",
                            "carrera.facultad.nombre",
                            "persona.numeroDocIdentidad",
                            "persona.apellidosNombres",
                            "persona.nombreCompleto",
                            "persona.rutaFoto",
                            "persona.tipoDocumento.*"}));
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

    @ResponseBody
    @RequestMapping("getInfoAportes/{idAlumno}")
    public JsonResponse getInfoAportes(@PathVariable("idAlumno") Long idAlumno, HttpSession session) {
        JsonResponse json = new JsonResponse();

        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
            JsonNodeFactory factory = JsonNodeFactory.instance;
            ObjectNode info = new ObjectNode(factory);
            ResumenAporteAlumno resumen = service.findResumenAporteAlumno(new Alumno(idAlumno), ds.getCicloAcademico());

            Alumno alumno = resumen.getMatriculaResumen().getAlumno();
            Persona persona = alumno.getPersona();

            info.put("nombre", persona.getNombreCompleto());
            info.put("codigo", alumno.getCodigo());
            info.put("carrera", alumno.getCarrera().getNombre());

            if (alumno.getModalidadEstudio().getCodigoEnum() == ModalidadEstudioEnum.EPG) {
                info.put("modalidadEstudio", alumno.getCarrera().getTipoEnum().getValue());
            } else {
                info.put("modalidadEstudio", alumno.getModalidadEstudio().getNombre());
            }
            ArrayNode array = new ArrayNode(factory);

            for (AporteAlumnoCiclo aporte : resumen.getAporteAlumnoCiclo()) {
                if (Arrays.asList(DEBE.name(), PAGO.name()).contains(aporte.getEstado())) {
                    ObjectNode node = JsonHelper.createJson(aporte, factory, true, new String[]{
                        "monto", "pagado", "saldo", "numeroCuota", "estadoEnum",
                        "aporteCiclo.aporte.nombre",
                        "aporteCiclo.cuentaBancaria.nombre",
                        "aporteCiclo.cuentaBancaria.numero"
                    });
                    array.add(node);
                }
            }

            info.set("aporteAlumnoCiclo", array);

            json.setData(info);
            json.setSuccess(Boolean.TRUE);
            json.setMessage("Datos cargados con éxito");

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, json);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, json);
        } finally {
            return json;
        }
    }

    @ResponseBody
    @RequestMapping("findBoleta/{idAlumno}")
    public JsonResponse findBoleta(@PathVariable("idAlumno") Long idAlumno, HttpSession session) {
        JsonResponse json = new JsonResponse();

        try {

            JsonNodeFactory factory = JsonNodeFactory.instance;
            ObjectNode response = new ObjectNode(factory);

            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
            MatriculaResumen resumen = service.findMatriculaResumen(new Alumno(idAlumno), ds.getCicloAcademico());

            Alumno alumno = resumen.getAlumno();

            response.put("nombre", alumno.getPersona().getNombreCompleto());
            response.put("carrera", alumno.getCarrera().getNombre());
            if (alumno.getModalidadEstudio().getCodigoEnum() == ModalidadEstudioEnum.EPG) {
                response.put("modalidadEstudio", alumno.getCarrera().getTipoEnum().getValue());
            } else {
                response.put("modalidadEstudio", alumno.getModalidadEstudio().getNombre());
            }
            ArrayNode array = new ArrayNode(factory);

            List<DeudaAlumno> boletas = matriculableService.allDeudasByAlumnoCiclo(alumno, resumen.getCicloAcademico());
            int numero = 1;
            for (DeudaAlumno boleta : boletas) {
                ObjectNode node = JsonHelper.createJson(boleta, factory, true, new String[]{
                    "monto", "estadoEnum", "estado", "fechaEmision", "fechaVencimiento", "numeroCuota",
                    "alumno.persona.numeroDocIdentidad",
                    "cuentaBancaria.numero",
                    "cuentaBancaria.nombre",
                    "cuentaBancaria.empresa",
                    "cuentaBancaria.banco"
                });
                node.put("numero", numero);
                node.put("acreencia", boleta.getAcreencia() != null ? boleta.getAcreencia().getId() : 0);
                array.add(node);
                numero++;
            }
            response.set("boletas", array);

            json.setSuccess(Boolean.TRUE);
            json.setData(response);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, json);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, json);
        } finally {
            return json;
        }
    }
}
