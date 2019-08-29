package pe.edu.lamolina.pivot.controller.matricula.matriculable;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.beans.PropertyEditorSupport;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableResponse;
import pe.albatross.zelpers.miscelanea.Assert;
import pe.albatross.zelpers.miscelanea.ExceptionHandler;
import pe.albatross.zelpers.miscelanea.JsonHelper;
import pe.albatross.zelpers.miscelanea.JsonResponse;
import pe.albatross.zelpers.miscelanea.NumberFormat;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.AlumnoCiclo;
import pe.edu.lamolina.model.academico.Carrera;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.ConfiguracionTurnosAtencion;
import pe.edu.lamolina.model.academico.Facultad;
import pe.edu.lamolina.model.academico.MatriculaResumen;
import pe.edu.lamolina.model.academico.ModalidadEstudio;
import pe.edu.lamolina.model.aporte.AporteAlumnoCiclo;
import pe.edu.lamolina.model.aporte.ResumenAporteAlumno;
import pe.edu.lamolina.model.constantines.GlobalMessages;
import pe.edu.lamolina.model.enums.AmbienteAplicacionEnum;
import static pe.edu.lamolina.model.enums.EstadoAporteEnum.DEBE;
import static pe.edu.lamolina.model.enums.EstadoAporteEnum.PAGO;
import pe.edu.lamolina.model.enums.ModalidadEstudioEnum;
import pe.edu.lamolina.model.enums.ParametrosSistemasEnum;
import pe.edu.lamolina.model.enums.RolEnum;
import static pe.edu.lamolina.model.enums.RolEnum.FAC;
import static pe.edu.lamolina.model.enums.RolEnum.MOD;
import static pe.edu.lamolina.model.enums.RolEnum.TODO;
import pe.edu.lamolina.model.enums.TipoCicloEnum;
import pe.edu.lamolina.model.enums.TipoCondicionalEnum;
import pe.edu.lamolina.model.finanzas.DeudaAlumno;
import pe.edu.lamolina.model.general.Parametro;
import pe.edu.lamolina.model.general.Persona;
import pe.edu.lamolina.model.seguridad.TokenIngresante;
import pe.edu.lamolina.model.seguridad.Usuario;
import pe.edu.lamolina.pivot.controller.academico.alumno.AlumnoResumen;
import pe.edu.lamolina.pivot.controller.academico.alumno.AlumnoService;
import pe.edu.lamolina.pivot.controller.academico.tramitesacademicos.tramiteRetiroCiclo.ResponseRestService;
import pe.edu.lamolina.pivot.controller.visores.RespositorVisor;
import pe.edu.lamolina.pivot.zelper.constant.Constantine;
import pe.edu.lamolina.pivot.zelper.constant.Messages;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

@Controller
@RequestMapping("academico/matriculable")
public class MatriculableController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    MatriculableService service;

    @Autowired
    RespositorVisor repositorVisor;

    @Autowired
    AlumnoService serviceAlumno;

    @Autowired
    ResponseRestService responseRestService;

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

        ArrayNode array = new ArrayNode(JsonNodeFactory.instance);

        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        CicloAcademico cicloAcademico = service.findCicloAcademico(ds.getCicloAcademico());
        AlumnoResumen resumen = service.allResumenAlumnosByCicloRol(ds.getCicloAcademico(), null, null);

        for (TipoCondicionalEnum value : TipoCondicionalEnum.values()) {
            if (value == TipoCondicionalEnum.OTRO) {

                ObjectNode obj = new ObjectNode(JsonNodeFactory.instance);
                obj.put("name", value.name());
                obj.put("value", value.getValue());
                array.add(obj);
            }
        }

        boolean puedeCalcular = service.usuarioPuedeCalcular(ds);

        model.addAttribute("resumen", JsonHelper.createJson(resumen, JsonNodeFactory.instance, new String[]{"*"}));
        model.addAttribute("ciclo", JsonHelper.createJson(cicloAcademico, JsonNodeFactory.instance, new String[]{"*"}));
        model.addAttribute("tipoCondicional", array);
        model.addAttribute("puedeCalcular", puedeCalcular);

        return "academico/matriculable/matriculable";
    }

    @ResponseBody
    @RequestMapping("list")
    public DynatableResponse list(DynatableFilter filter, HttpSession session) {

        DynatableResponse json = new DynatableResponse();
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);

        logger.debug("Rol activo {}", ds.getRolActivo().getCodigo());

        List<Long> filtros = new ArrayList();

        switch (RolEnum.valueOf(ds.getRolActivo().getCodigo())) {
            case TODO:
                break;
            case MOD:
                for (ModalidadEstudio modalidad : ds.getModalidades()) {
                    filtros.add(modalidad.getId());
                }
                break;
            case FAC:
                for (Facultad fac : ds.getFacultades()) {
                    filtros.add(fac.getId());
                }
                break;
            case ESP:
                for (Carrera carrera : ds.getCarreras()) {
                    filtros.add(carrera.getId());
                }
                break;
            default:
                break;
        }

        try {

            ArrayNode array = new ArrayNode(JsonNodeFactory.instance);

            List<MatriculaResumen> matriculables = service.allAlumnosByCicloRolDynatable(filter, ds.getCicloAcademico(), ds.getRolActivo().getCodigo(), filtros);
            for (MatriculaResumen matriculable : matriculables) {
                ObjectNode node = JsonHelper.createJson(matriculable, JsonNodeFactory.instance, true,
                        new String[]{
                            "id", "prioridad", "puntajePrioridad", "cursosMatriculados", "cursosRetirados", "motivoMatriculable", "esBeneficiadoUltimoCiclo", "esCondicional", "aporteCarnet", "boletaPendiente",
                            "prioridadAnterior", "alumno.persona.rutaFoto", "alumno.persona.tipoFoto", "alumno.persona.emailCompania", "alumno.persona.numeroDocIdentidad",
                            "creditosMatriculados", "creditosRetirados", "estado", "estadoEnum", "alumno.codigo",
                            "promedioSemestral",
                            "creditosAcumulados",
                            "creditosCursadosCiclo",
                            "creditosAprobadosCiclo",
                            "creditosAprobadosAcumulados",
                            "cicloAcademicoInfo.*",
                            "alumno.id", "alumno.persona.apellidosNombres", "alumno.carrera.id", "alumno.carrera.codigo",
                            "alumno.carrera.nombre", "alumno.carrera.facultad.id", "alumno.carrera.facultad.nombre",
                            "alumno.carrera.facultad.codigo", "situacionInicio.id", "situacionFinal.id", "situacionFinal.nombre",
                            "situacionInicio.nombre",
                            "turnoAtencion.fecha",
                            "turnoAtencion.fechaHoraInicio",
                            "alumno.situacionAcademica.codigo",
                            "alumno.situacionAcademica.nombre",
                            "alumno.situacionAcademica.descripcion",
                            "resumenesAportes.id",
                            "resumenesAportes.montoTotal"
                        });
                if (matriculable.getPuntajePrioridad() != null) {
                    node.put("puntajePrioridad", NumberFormat.notaDecimalXDecimals(matriculable.getPuntajePrioridad(), 6));
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
    @RequestMapping("generar")
    public JsonResponse generar(Model model, HttpSession session) {

        JsonResponse response = new JsonResponse();
        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
            if (ds.getCicloAcademico().getTipoEnum() == TipoCicloEnum.NIV) {
                service.generarVerano(ds.getCicloAcademico(), ds);
            } else {
                service.generar(ds.getCicloAcademico(), ds);
            }
            AlumnoResumen resumen = service.allResumenAlumnosByCicloRol(ds.getCicloAcademico(), null, null);
            response.setData(JsonHelper.createJson(resumen, JsonNodeFactory.instance, new String[]{"*"}));
            response.setMessage("Matriculables generados satisfactoriamente");
            response.setSuccess(true);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("ciclo")
    public JsonResponse ciclo(Model model, HttpSession session) {

        JsonResponse response = new JsonResponse();
        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
            CicloAcademico c = service.findCicloAcademico(ds.getCicloAcademico());
            response.setData(JsonHelper.createJson(c, JsonNodeFactory.instance, new String[]{
                "*"
            }));
            response.setMessage("Matriculables generados satisfactoriamente");
            response.setSuccess(true);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("generarPrioridad")
    public JsonResponse generarPrioridad(Model model, HttpSession session) {

        JsonResponse response = new JsonResponse();
        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);

            //service.revisarSituacionesAcademicas(ds.getCicloAcademico(), ds);
            service.generarPrioridad(ds.getCicloAcademico());
            response.setMessage("Prioridad generadas correctamente");
            response.setSuccess(true);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("eliminarPrioridad")
    public JsonResponse eliminarPrioridad(Model model, HttpSession session) {

        JsonResponse response = new JsonResponse();
        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);

            service.eliminarPrioridad(ds.getCicloAcademico());
            AlumnoResumen resumen = service.allResumenAlumnosByCicloRol(ds.getCicloAcademico(), null, null);
            response.setData(JsonHelper.createJson(resumen, JsonNodeFactory.instance, new String[]{"*"}));
            response.setMessage("Prioridad eliminada correctamente");
            response.setSuccess(true);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("finalizarPrioridad")
    public JsonResponse finalizarPrioridad(HttpSession session) {

        JsonResponse response = new JsonResponse();
        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);

            service.finalizarPrioridad(ds.getCicloAcademico());
            AlumnoResumen resumen = service.allResumenAlumnosByCicloRol(ds.getCicloAcademico(), null, null);
            response.setData(JsonHelper.createJson(resumen, JsonNodeFactory.instance, new String[]{"*"}));
            response.setMessage("Prioridad finalizada correctamente");
            response.setSuccess(true);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("finalizarMatriculable")
    public JsonResponse finalizarMatriculable(HttpSession session) {

        JsonResponse response = new JsonResponse();
        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);

            service.finalizarMatriculable(ds.getCicloAcademico());
            AlumnoResumen resumen = service.allResumenAlumnosByCicloRol(ds.getCicloAcademico(), null, null);
            response.setData(JsonHelper.createJson(resumen, JsonNodeFactory.instance, new String[]{"*"}));
            response.setMessage("Matriculable finalizada correctamente");
            response.setSuccess(true);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("limpiarMatriculable")
    public JsonResponse limpiarMatriculable(HttpSession session) {

        JsonResponse response = new JsonResponse();
        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
            service.limpiarMatriculable(ds.getCicloAcademico());
            AlumnoResumen resumen = service.allResumenAlumnosByCicloRol(ds.getCicloAcademico(), null, null);
            response.setData(JsonHelper.createJson(resumen, JsonNodeFactory.instance, new String[]{"*"}));
            response.setMessage("Matriculable eliminada correctamente");
            response.setSuccess(true);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("procesarTipoMatricula")
    public JsonResponse procesarTipoMatricula(
            @RequestParam("confTurnoAtencion") Long confTurnoAtencion,
            RedirectAttributes redirectAttr, HttpSession session) {

        JsonResponse response = new JsonResponse();
        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);

            logger.debug("confTurnoAtencion {}", confTurnoAtencion);
            String message = "Rechazado correctamente.";
            service.procesarTurnoMatricula(ds.getCicloAcademico(), confTurnoAtencion);
            ObjectNode node = new ObjectNode(JsonNodeFactory.instance);
            response.setData(node);
            response.setSuccess(true);
            response.setMessage(message);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (RuntimeException e) {
            e.printStackTrace();
            ExceptionHandler.handleSpecial(e, response, Messages.FK_ERROR);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("configuracionesTurno")
    public JsonResponse modalAsignarTurno(HttpSession session) {
        JsonResponse response = new JsonResponse();
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        ArrayNode an = new ArrayNode(JsonNodeFactory.instance);
        try {
            List<ConfiguracionTurnosAtencion> configuracionesTurnoAtencion = service.allConfiguracionTurnoByCiclo(ds.getCicloAcademico());
            for (ConfiguracionTurnosAtencion configuracionTurnosAtencion : configuracionesTurnoAtencion) {
                an.add(JsonHelper.createJson(configuracionTurnosAtencion, JsonNodeFactory.instance, new String[]{
                    "*",
                    "eventoCicloAcademico.*",
                    "eventoCicloAcademico.eventoAcademico.*"
                }));
            }
            response.setData(an);
            response.setSuccess(Boolean.TRUE);
        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }

        return response;
    }

    @RequestMapping("modalSubirEgresados")
    public String modalSubirEgresados(Model model, HttpSession session) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);

        return "academico/matriculable/modalSubirEgresados";
    }

    @ResponseBody
    @RequestMapping("subirEgresados")
    public JsonResponse subirEgresados(@RequestParam("file") MultipartFile file,
            Model model, HttpSession session) {
        JsonResponse json = new JsonResponse();
        try {
            logger.debug("File {}", file.getBytes().length);
            service.loadEgresados(file);
            json.setSuccess(Boolean.TRUE);
        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, json);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, json);
        } finally {
            return json;
        }
    }

    @RequestMapping("estadoVisor")
    public String estadoVisor(Model model, HttpSession session) {

        return "academico/matriculable/estadoVisor";
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
                            "id",
                            "codigo",
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
    @RequestMapping("saveMatriculable/{tipoCondicional}")
    public JsonResponse saveMatriculable(@PathVariable String tipoCondicional, @RequestBody Alumno alumno, HttpSession session) {
        JsonResponse response = new JsonResponse();
        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
//            service.revisarSituacionAcademica(alumno, ds);
            service.saveMatriculable(alumno, tipoCondicional, ds);

            response.setMessage("Se agregó al alumno satisfactoriamente.");
            response.setSuccess(true);
        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("inhabilitar")
    public JsonResponse inhabilitar(@RequestBody MatriculaResumen matriculaResumen, HttpSession session) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        JsonResponse response = new JsonResponse();

        try {

            service.inhabilitarMatriculable(matriculaResumen, ds);
            response.setMessage("Se actualizo el matriculable satisfactoriamente.");
            response.setSuccess(true);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;

    }

    @ResponseBody
    @RequestMapping("verificarAlumnosNmat")
    public JsonResponse verificarAlumnosNmat(HttpSession session) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        JsonResponse response = new JsonResponse();

        try {
            if (repositorVisor.isLibre()) {
                List<AlumnoCiclo> alumnoCiclos = service.allAlumnosCicloNmat(ds.getCicloAcademico());

                service.verificarAlumnosNmat(ds, alumnoCiclos);
            }
            response.setData(repositorVisor.porcentajeAvance());
            response.setMessage(repositorVisor.getMessage());
            response.setSuccess(repositorVisor.isLibre());
        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;

    }

    @ResponseBody
    @RequestMapping("verificarAvance")
    public JsonResponse verificarAvance(HttpSession session) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        JsonResponse response = new JsonResponse();

        try {
            response.setData(repositorVisor.porcentajeAvance());
            response.setMessage(repositorVisor.getMessage());
            response.setSuccess(repositorVisor.isLibre());
        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;

    }

    @ResponseBody
    @RequestMapping("beneficiar")
    public JsonResponse beneficiar(@RequestBody MatriculaResumen matriculaResumen, HttpSession session) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        JsonResponse response = new JsonResponse();

        try {

            service.beneficiar(matriculaResumen, ds);
            response.setMessage("Se actualizó satisfactoriamente.");
            response.setSuccess(true);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;

    }

    @ResponseBody
    @RequestMapping("agregarAporteCarnet")
    public JsonResponse agregarAporteCarnet(@RequestBody MatriculaResumen matriculaResumen, HttpSession session) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        JsonResponse response = new JsonResponse();

        try {

            service.agregarAporteCarnet(matriculaResumen, ds);
            response.setMessage("Se actualizó satisfactoriamente.");
            response.setSuccess(true);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;

    }

    @ResponseBody
    @RequestMapping("quitarAporteCarnet")
    public JsonResponse quitarAporteCarnet(@RequestBody MatriculaResumen matriculaResumen, HttpSession session) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        JsonResponse response = new JsonResponse();

        try {

            service.quitarAporteCarnet(matriculaResumen, ds);
            response.setMessage("Se actualizó satisfactoriamente.");
            response.setSuccess(true);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;

    }

    @ResponseBody
    @RequestMapping("actualizarPrioridadCero")
    public JsonResponse actualizarPrioridadCero(HttpSession session) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        JsonResponse response = new JsonResponse();

        try {

            service.actualizarPrioridadCero(ds);
            response.setMessage("Se actualizó satisfactoriamente.");
            response.setSuccess(true);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;

    }

    @ResponseBody
    @RequestMapping("getInfoAportes/{id}")
    public JsonResponse getInfoAportes(@PathVariable("id") Long idResumen) {
        JsonResponse json = new JsonResponse();

        try {

            JsonNodeFactory factory = JsonNodeFactory.instance;
            ObjectNode info = new ObjectNode(factory);
            ResumenAporteAlumno resumen = service.findResumenAporteAlumno(new ResumenAporteAlumno(idResumen));

            if (resumen == null) {
                throw new PhobosException(GlobalMessages.UNKNOWN);
            }

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
    @RequestMapping("findBoleta/{idMatriculaResumen}")
    public JsonResponse findBoleta(@PathVariable("idMatriculaResumen") Long idMatriculaResumen, HttpSession session) {
        JsonResponse json = new JsonResponse();

        try {

            JsonNodeFactory factory = JsonNodeFactory.instance;
            ObjectNode response = new ObjectNode(factory);

            MatriculaResumen resumen = service.findMatriculaResumen(new MatriculaResumen(idMatriculaResumen));

            Alumno alumno = resumen.getAlumno();

            response.put("nombre", alumno.getPersona().getNombreCompleto());
            response.put("carrera", alumno.getCarrera().getNombre());
            if (alumno.getModalidadEstudio().getCodigoEnum() == ModalidadEstudioEnum.EPG) {
                response.put("modalidadEstudio", alumno.getCarrera().getTipoEnum().getValue());
            } else {
                response.put("modalidadEstudio", alumno.getModalidadEstudio().getNombre());
            }
            ArrayNode array = new ArrayNode(factory);

            List<DeudaAlumno> boletas = service.allByAlumnoCiclo(alumno, resumen.getCicloAcademico());
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

    @RequestMapping("{idAlumno}/histrialPdf")
    public String goMaipi(@PathVariable("idAlumno") Long idAlumno, Model model, HttpSession session) throws InterruptedException {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        Usuario usuario = ds.getUsuario();
        serviceAlumno.goMaipi(idAlumno, usuario);
        Parametro paramRutaMatricula = serviceAlumno.findParametroByEnum(ParametrosSistemasEnum.SALTO_PIVOT_INTRA);
        JsonResponse jsonResponse = responseRestService.downloadHistorial(new Alumno(idAlumno), usuario, ds.getCicloAcademico(), paramRutaMatricula);

        Assert.isTrue(jsonResponse.getSuccess(), "Se produjo un error al modificar secciones matricula. Comuniquese con mesa de ayuda.");

        return "redirect:/";
    }

}
