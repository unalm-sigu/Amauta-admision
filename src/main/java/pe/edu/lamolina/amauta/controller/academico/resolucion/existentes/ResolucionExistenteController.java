package pe.edu.lamolina.amauta.controller.academico.resolucion.existentes;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import javax.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import pe.albatross.zelpers.json.JaneHelper;
import pe.albatross.zelpers.miscelanea.ExceptionHandler;
import pe.albatross.zelpers.miscelanea.JsonResponse;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.edu.lamolina.amauta.zelper.model.TramitesAcademicos;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.bean.AlumnoCicloCursoBean;

import pe.edu.lamolina.model.enums.TramiteEstadoEnum;
import pe.edu.lamolina.model.tramite.*;
import pe.edu.lamolina.amauta.controller.matricula.matriculable.MatriculableService;
import pe.edu.lamolina.model.constantines.GlobalConstantine;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;
import pe.edu.lamolina.model.constantines.GlobalMessages;
import pe.edu.lamolina.model.enums.TipoResolucionEnum;
import pe.edu.lamolina.model.enums.oficina.OficinaEnum;

import static pe.edu.lamolina.model.enums.TipoResolucionEnum.*;
import static pe.edu.lamolina.model.enums.tramite.TipoTramiteEnum.ING_HIS;
import static pe.edu.lamolina.model.enums.tramite.TipoTramiteEnum.INTES;

@Slf4j
@Controller
@AllArgsConstructor(onConstructor = @__(
        @Autowired))
@RequestMapping("academico/resolucion/existentes")
public class ResolucionExistenteController {

    private final ResolucionExistenteService service;
    private final MatriculableService matriculableService;
    private final static List<TipoResolucionEnum> TIPOS_RESOLUCIONES = Arrays.asList(
            TipoResolucionEnum.READMISION,
            TipoResolucionEnum.CAMBIO_PLAN_CURRICULAR,
            TipoResolucionEnum.TRAS_INT,
            TipoResolucionEnum.RCI,
            TipoResolucionEnum.ANCI,
            TipoResolucionEnum.REIC,
            TipoResolucionEnum.CAM_NOTA,
            TipoResolucionEnum.CURDIR,
            TipoResolucionEnum.TRAS,
            TipoResolucionEnum.INTES,
            TipoResolucionEnum.NOTA_BAJA,
            TipoResolucionEnum.BACHI,
            TipoResolucionEnum.TITUL,
            TipoResolucionEnum.PRACTICAS,
//            TipoResolucionEnum.ING_HIS,
            TipoResolucionEnum.BACHIFAC,
            TipoResolucionEnum.TITULBAC,
            TipoResolucionEnum.ALUMRENUNCIA,
            TipoResolucionEnum.RENUNCIA_CAR,
            TipoResolucionEnum.SUSP_DISCIPLI,
            TipoResolucionEnum.DOCENTE_RESOL_CU,
            TipoResolucionEnum.DOCENTE_RESOL_FC
    );

    @RequestMapping(method = RequestMethod.GET)
    public String index(Model model, HttpSession session) {

        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);

        ArrayNode tipoResolucionJson = JaneHelper
                .from(service.allTipoResolucionByCodigo(TIPOS_RESOLUCIONES))
                .array();

        ArrayNode oficinasJson = JaneHelper
                .from(service.allOficinasResolucion(ds))
                .only("id,nombre,codigo,codigoDocumento,instanciaOficina")
                .array();

        ArrayNode ciclosJson = JaneHelper
                .from(service.allCicloAplica(ds))
                .only("id,codigo,descripcion")
                .array();

        ArrayNode carrerasJson = JaneHelper
                .from(service.allCarrera())
                .only("id,nombre,codigo")
                .array();

        model.addAttribute("carreras", carrerasJson);
        model.addAttribute("oficinas", oficinasJson);
        model.addAttribute("tiposResolucion", tipoResolucionJson);
        model.addAttribute("ciclos", ciclosJson);
        model.addAttribute("IS_EDICION", FALSE);
        model.addAttribute("IS_ANULAR", FALSE);

        return "academico/resolucion/resolucionexistentes/resolucionExistentes";
    }

    @RequestMapping(value = "/{idResolucion}", method = RequestMethod.GET)
    public String updateresolucionExistentes(@PathVariable(value = "idResolucion") Long idResolucion, Model model, HttpSession session) {

        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);

        ArrayNode tipoResolucionJson = JaneHelper
                .from(service.allTipoResolucionByCodigo(TIPOS_RESOLUCIONES))
                .array();

        ArrayNode oficinasJson = JaneHelper
                .from(service.allOficinasResolucion(ds))
                .only("id,nombre,codigo,codigoDocumento,instanciaOficina")
                .array();

        ArrayNode ciclosJson = JaneHelper
                .from(service.ciclosAnteriores(40))
                .only("id,codigo,descripcion")
                .array();

        ArrayNode carrerasJson = JaneHelper
                .from(service.allCarrera())
                .only("id,nombre,codigo")
                .array();

        Resolucion resolucionDB = service.findByResolucion(idResolucion, ds);

        ObjectNode objectNode = this.findDataResolucion(resolucionDB);

        model.addAttribute("carreras", carrerasJson);
        model.addAttribute("oficinas", oficinasJson);
        model.addAttribute("tiposResolucion", tipoResolucionJson);
        model.addAttribute("ciclos", ciclosJson);
        model.addAttribute("resolucion", objectNode);
        model.addAttribute("IS_EDICION", TRUE);
        return "academico/resolucion/resolucionexistentes/resolucionExistentes";
    }

    @RequestMapping(value = "/{idResolucion}/anularTramite", method = RequestMethod.GET)
    public String anularTramiteTituloResolucionExistente(@PathVariable(value = "idResolucion") Long idResolucion, Model model, HttpSession session) {

        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);

        ArrayNode tipoResolucionJson = JaneHelper
                .from(service.allTipoResolucionByCodigo(TIPOS_RESOLUCIONES))
                .array();

        ArrayNode oficinasJson = JaneHelper
                .from(service.allOficinasResolucion(ds))
                .only("id,nombre,codigo,codigoDocumento,instanciaOficina")
                .array();

        ArrayNode ciclosJson = JaneHelper
                .from(service.ciclosAnteriores(40))
                .only("id,codigo,descripcion")
                .array();

        ArrayNode carrerasJson = JaneHelper
                .from(service.allCarrera())
                .only("id,nombre,codigo")
                .array();

        Resolucion resolucionDB = service.findByResolucion(idResolucion, ds);

        ObjectNode objectNode = this.findDataResolucion(resolucionDB);

        model.addAttribute("carreras", carrerasJson);
        model.addAttribute("oficinas", oficinasJson);
        model.addAttribute("tiposResolucion", tipoResolucionJson);
        model.addAttribute("ciclos", ciclosJson);
        model.addAttribute("resolucion", objectNode);
        model.addAttribute("IS_ANULAR", TRUE);
        return "academico/resolucion/resolucionexistentes/resolucionExistentes";

    }

    @ResponseBody
    @RequestMapping("findAlumno")
    public JsonResponse findAlumno(
            @RequestParam("nombre") String nombre,
            @RequestParam(name = "instanciaOficina", required = false) Long instanciaOficina,
            HttpSession session) {

        JsonResponse response = new JsonResponse();

        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);

        List<Alumno> alumnos = service.allAlumnoByOficina(nombre, instanciaOficina);

        ArrayNode data = JaneHelper.from(alumnos).only("id,codigo")
                .join("persona", "nombreCompleto,apellidosNombres,numeroDocIdentidad")
                .join("persona.tipoDocumento")
                .join("carrera")
                .join("carrera.facultad")
                .array();

        response.setSuccess(Boolean.TRUE);
        response.setData(data);

        return response;
    }

    @ResponseBody
    @RequestMapping("findAlumnoSancionados")
    public JsonResponse findAlumnoSancionados(
            @RequestParam("nombre") String nombre,
            @RequestParam(name = "instanciaOficina", required = false) Long instanciaOficina,
            HttpSession session) {

        JsonResponse response = new JsonResponse();

        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);

        List<Alumno> alumnosSancionados = service.findAlumnosSancionados(nombre, instanciaOficina);

        ArrayNode data = JaneHelper.from(alumnosSancionados).only("id,codigo")
                .join("persona", "nombreCompleto,apellidosNombres,numeroDocIdentidad")
                .join("persona.tipoDocumento")
                .join("carrera")
                .join("carrera.facultad")
                .array();

        response.setSuccess(Boolean.TRUE);
        response.setData(data);

        return response;
    }

    @ResponseBody
    @RequestMapping("save")
    public JsonResponse save(@RequestBody Resolucion resolucion, HttpSession session) {

        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);

        TipoResolucionEnum tipo = resolucion.getTipoResolucion().getTipoEnum();
        List<String> respuesta = service.saveResolucion(resolucion, ds);

        JsonResponse response = new JsonResponse();
        response.setSuccess(Boolean.TRUE);
        response.setMessage(GlobalMessages.CREATED);

        //    log.debug("respuesta.get(0) {} ", respuesta.get(0).length());
        //    log.debug("respuesta.mensaje {} ", respuesta.get(0));
        if (!respuesta.isEmpty() && respuesta.get(0).length() == 32 && respuesta.get(0).substring(0, 32).equalsIgnoreCase("Ya fue registrado una resolución")) {
            response.setSuccess(Boolean.FALSE);
            response.setMessage(respuesta.get(0));
            return response;
        }

        switch (tipo) {
            case REIC:
                if (this.contieneMensaje(respuesta)) {
                    response.setSuccess(Boolean.FALSE);
                    response.setMessage(respuesta.get(0));
                    return response;
                }
                break;
            case RCI:
            case ANCI:
                if (this.contieneMensaje(respuesta)) {
                    response.setSuccess(Boolean.FALSE);
                    response.setMessage(respuesta.get(0));
                    return response;
                }
                break;
            case CAM_NOTA:
            case NOTA_BAJA:
            case READMISION:
                if (this.contieneMensaje(respuesta)) {
                    matriculableService.calcularPromedios(respuesta.get(0), ds);
                    matriculableService.revisarCurriculaAlumnos(ds, respuesta.get(0));
                    matriculableService.revisarMatriculables(ds, respuesta.get(0));
                    matriculableService.generarAportes(ds, respuesta.get(0));
                }
                break;
            case TRAS_INT:
//                msg = respuesta;
                if (this.contieneMensaje(respuesta)) {
                    response.setSuccess(Boolean.FALSE);
                    response.setMessage(respuesta.get(0));
                    return response;
                }
//                service.generarNuevoPlan(resolucion, ds);
                break;
            case TRAS:
            case INTES:
            case ING_HIS:
            case BACHI:
            case BACHIFAC:
                if (this.contieneMensaje(respuesta)) {
                    response.setSuccess(Boolean.FALSE);
                    response.setMessage(respuesta.get(0));
                    return response;
                }

            case TITUL:
                if (this.contieneMensaje(respuesta)) {
                    response.setSuccess(Boolean.FALSE);
                    response.setMessage(respuesta.get(0));
                    return response;
                }
                break;
            case TITULBAC:
                if (this.contieneMensaje(respuesta)) {
                    response.setSuccess(Boolean.FALSE);
                    response.setMessage(respuesta.get(0));
                    return response;
                }
                break;
            case CAMBIO_PLAN_CURRICULAR:
                break;
            case CURDIR:
                if (this.contieneMensaje(respuesta)) {
                    response.setSuccess(Boolean.FALSE);
                    response.setMessage(respuesta.get(0));
                    return response;
                }
                break;
            case PRACTICAS:
                if (this.contieneMensaje(respuesta)) {
                    response.setSuccess(Boolean.FALSE);
                    response.setMessage(respuesta.get(0));
                    return response;
                }

                //matriculableService.calcularPromedios(respuesta.get(0), ds);
                //matriculableService.revisarCurriculaAlumnos(ds, respuesta.get(0));
                break;
            case ALUMRENUNCIA:
                if (this.contieneMensaje(respuesta)) {
                    response.setSuccess(Boolean.FALSE);
                    response.setMessage(respuesta.get(0));
                    return response;
                }
                break;
            case RENUNCIA_CAR:
                if (this.contieneMensaje(respuesta)) {
                    response.setSuccess(Boolean.FALSE);
                    response.setMessage(respuesta.get(0));
                    return response;
                }
                break;
            case SUSP_DISCIPLI:
                if (this.contieneMensaje(respuesta)) {
                    response.setSuccess(Boolean.FALSE);
                    response.setMessage(respuesta.get(0));
                    return response;
                }
                break;
            case DOCENTE_RESOL_FC:
            case DOCENTE_RESOL_CU:
                if (this.contieneMensaje(respuesta)) {
                    response.setSuccess(Boolean.FALSE);
                    response.setMessage(respuesta.get(0));
                    return response;
                }
                break;
            default:
                throw new PhobosException("Tipo de trámite no soportado");
        }

        return response;
    }

    public boolean contieneMensaje(List<String> respuesta) {
        for (String rpta : respuesta) {
            if (!rpta.isEmpty() && rpta.length() > 0) {
                return true;
            }
        }
        return false;
    }

    @ResponseBody
    @RequestMapping("update")
    public JsonResponse update(@RequestBody Resolucion resolucion, HttpSession session) {

        if (!(resolucion.isTipoTramiteBachiller()
                || resolucion.isTipoTramiteTitulo()
                || resolucion.isTipoTramiteTituloFacultad()
                || resolucion.isTipoTramitePracticas()
                || resolucion.isTipoTrasladoInterno()
                || resolucion.isTipoRetiroCiclo()
                || resolucion.isTipoTramiteBachillerFacultad()
                || resolucion.isTipoCursoDirigido()
                || resolucion.isTipoSancionDisciplina())) {
            throw new PhobosException("Solo se permite editar la resolución de bachiller, título, prácticas y retiro de ciclo");
        }

        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);

        List<String> msg = new ArrayList();

        TipoResolucionEnum tipo = resolucion.getTipoResolucion().getTipoEnum();

        List<String> respuestas = service.updateResolucion(resolucion, ds.getUsuario(), ds);

        JsonResponse response = new JsonResponse();
        response.setSuccess(Boolean.TRUE);
        response.setMessage(GlobalMessages.UPDATED);

        switch (tipo) {
            case REIC:
            case RCI:
            case ANCI:
            case CAM_NOTA:
            case NOTA_BAJA:
            case READMISION:
                String token = respuestas.get(0);
                matriculableService.calcularPromedios(token, ds);
                matriculableService.revisarCurriculaAlumnos(ds, token);
                matriculableService.revisarMatriculables(ds, token);
                matriculableService.generarAportes(ds, token);
                break;
            case TRAS_INT:
                //service.generarNuevoPlan(resolucion, ds);
                break;
            case TRAS:
            case INTES:
            case ING_HIS:
            case BACHI:
            case BACHIFAC:
            case TITUL:
            case TITULBAC:
            case CAMBIO_PLAN_CURRICULAR:
            case SUSP_DISCIPLI:
                break;
            case CURDIR:
                msg = respuestas;
                if (!msg.isEmpty()) {
                    response.setSuccess(Boolean.FALSE);
                }
                response.setData(msg);
                break;
            case PRACTICAS:
                //String token1 = respuestas.get(0);
                //matriculableService.calcularPromedios(token1, ds);
                //matriculableService.revisarCurriculaAlumnos(ds, token1);
                break;
            default:
                throw new PhobosException("Tipo de trámite no soportado");
        }

        return response;
    }

    @ResponseBody
    @RequestMapping(value = "anularTramiteTitulo")
    public JsonResponse anularTramiteTitulo(@RequestBody ResolucionesExistentesDTO resolucionesExistentesDTO, HttpSession session) {
        JsonResponse response = new JsonResponse();
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
        TramiteTitulo tramiteTitulo = resolucionesExistentesDTO.getTramiteTitulo();
        Resolucion resolucion = resolucionesExistentesDTO.getResolucion();
        boolean respuesta = service.anularAlumnoDeResolucionTitulo(resolucion, tramiteTitulo, ds.getUsuario(), ds);
        if (respuesta) {
            response.setSuccess(Boolean.TRUE);
            response.setMessage(TramitesAcademicos.TRAMITE_TITULO_ANULADO);
        } else {
            response.setSuccess(Boolean.FALSE);
            response.setMessage(TramitesAcademicos.ERROR_ANULAR_RESOLUCION_TITULO);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping(value = "anularTramiteBachiller")
    public JsonResponse anularTramiteBachiller(@RequestBody ResolucionesExistentesDTO resolucionesExistentesDTO, HttpSession session) {
        JsonResponse response = new JsonResponse();
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
        TramiteBachiller tramiteBachiller = resolucionesExistentesDTO.getTramiteBachiller();
        Resolucion resolucion = resolucionesExistentesDTO.getResolucion();
        Alumno alumno = resolucionesExistentesDTO.getAlumno();
        boolean respuesta = service.anularAlumnoDeResolucionBachiller(alumno, resolucion, tramiteBachiller, ds.getUsuario(), ds);
        if (respuesta) {
            response.setSuccess(Boolean.TRUE);
            response.setMessage(TramitesAcademicos.TRAMITE_BACHILLER_ANULADO);
        } else {
            response.setSuccess(Boolean.FALSE);
            response.setMessage(TramitesAcademicos.ERROR_ANULAR_RESOLUCION_BACHILLER);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping(value = "anularTramiteCursoDirigido")
    public JsonResponse anularTramiteCursoDirigido(@RequestBody ResolucionesExistentesDTO resolucionesExistentesDTO, HttpSession session) {
        JsonResponse response = new JsonResponse();
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
        CursoDirigido cursoDirigido = resolucionesExistentesDTO.getCursoDirigido();
        Resolucion resolucion = resolucionesExistentesDTO.getResolucion();
        Alumno alumno = resolucionesExistentesDTO.getAlumno();
        boolean respuesta = service.anularAlumnoDeResolucionCursoDirigido(alumno, resolucion, cursoDirigido, ds);
        if (respuesta) {
            response.setSuccess(Boolean.TRUE);
            response.setMessage(TramitesAcademicos.TRAMITE_CURSO_DIRIGIDO);
        } else {
            response.setSuccess(Boolean.FALSE);
            response.setMessage(TramitesAcademicos.ERROR_ANULAR_RESOLUCION_CURSO_DIRIGIDO);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping(value = "anularTramiteTrasladoInterno")
    public JsonResponse anularTramiteTrasladoInterno(@RequestBody ResolucionesExistentesDTO resolucionesExistentesDTO, HttpSession session) {
        JsonResponse response = new JsonResponse();
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
        TramiteTraslado tramiteTraslado = resolucionesExistentesDTO.getTramiteTraslado();
        Resolucion resolucion = resolucionesExistentesDTO.getResolucion();
        Alumno alumno = resolucionesExistentesDTO.getAlumno();
        boolean respuesta = service.anularAlumnoDeResolucionTramiteTraslado(alumno, resolucion, tramiteTraslado, ds);
        if (respuesta) {
            response.setSuccess(Boolean.TRUE);
            response.setMessage(TramitesAcademicos.TRAMITE_TRASLADO_INTERNO);
        } else {
            response.setSuccess(Boolean.FALSE);
            response.setMessage(TramitesAcademicos.ERROR_ANULAR_RESOLUCION_TRASLADO_INTERNO);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("alumnos")
    public ArrayNode alumnos(@RequestBody Resolucion resolucion) {

        TipoResolucionEnum tipoResolucionEnum = resolucion.getTipoResolucion().getTipoEnum();

        if (tipoResolucionEnum == REIC) {
            List<Reincorporacion> reincorporados = service.allReincorporacionByResolucion(resolucion);
            return JaneHelper.from(reincorporados)
                    .join("facultad")
                    .join("alumno")
                    .join("alumno.persona")
                    .join("alumno.persona.tipoDocumento")
                    .join("cicloReincorporacion")
                    .array();

        } else if (tipoResolucionEnum == RCI) {
            List<RetiroCiclo> retiradosCiclos = service.allRetiroCicloByResolucion(resolucion);
            return JaneHelper.from(retiradosCiclos)
                    .join("curso")
                    .join("alumno")
                    .join("alumno.persona")
                    .join("alumno.persona.tipoDocumento")
                    .join("cicloAcademico")
                    .array();

        } else if (tipoResolucionEnum == CAM_NOTA) {
            List<CambioNota> cambiosNotas = service.allCambioNota(resolucion);
            return JaneHelper.from(cambiosNotas)
                    .join("curso")
                    .join("alumno")
                    .join("alumno.persona")
                    .join("alumno.persona.tipoDocumento")
                    .join("cicloAcademico")
                    .array();

        } else if (tipoResolucionEnum == CURDIR) {
            List<CursoDirigido> cursosDirigidos = service.allCursodirigidoPregrado(resolucion);
            return JaneHelper.from(cursosDirigidos)
                    .join("curso")
                    .join("tramite.alumno")
                    .join("tramite.alumno.persona")
                    .join("tramite.alumno.persona.tipoDocumento")
                    .array();

        } else if (Arrays.asList(TRAS.name(), INTES.name(), ING_HIS.name(), TRAS_INT.name()).contains(tipoResolucionEnum.name())) {
            List<TramiteTraslado> trasladados = service.allTramiteTraslado(resolucion);
            return JaneHelper.from(trasladados)
                    .join("carrera")
                    .join("carreraOrigen")
                    .join("tramite.cicloAcademico", "id,descripcion")
                    .join("tramite.alumno")
                    .join("tramite.alumno.persona")
                    .join("tramite.alumno.persona.tipoDocumento")
                    .join("resolucion.cicloAplica")
                    .array();
        } else if (Arrays.asList(BACHI, OBTE_GRADO).contains(tipoResolucionEnum)) {
            if (resolucion.getOficina().getCodigoEnum() == OficinaEnum.UNA) {
                List<ObtencionGrado> graduados = service.allObtencionGrado(resolucion);
                return JaneHelper.from(graduados)
                        .join("cicloAcademico", "id,descripcion")
                        .join("gradoAcademico", "nombre")
                        .join("alumno", "codigo")
                        .join("alumno.persona", "id,numeroDocIdentidad,nombreCompleto,apellidosNombres")
                        .join("alumno.persona.tipoDocumento", "id,simbolo")
                        .array();

            } else {
                List<TramiteBachiller> tramiteBachiller = service.allResulucionFacultad(resolucion);
                return JaneHelper.from(tramiteBachiller)
                        .join("tramite.alumno", "codigo")
                        .join("tramite.cicloAcademico", "descripcion")
                        .join("tramite.persona", "paterno,materno,nombres")
                        .join("resolucion.oficina", "codigo,id")
                        .join("resolucion")
                        .array();
            }
        } else if (tipoResolucionEnum == BACHIFAC) {
            List<TramiteBachiller> tramiteBachillerx = service.allResulucionFacultad(resolucion);
            List<TramiteBachiller> tramiteBachillers = tramiteBachillerx.stream().filter(x ->!x.getEstado().equalsIgnoreCase(TramiteEstadoEnum.ANU.name())
                    && !x.getEstadofacultad().equalsIgnoreCase(TramiteEstadoEnum.ANU.name())).collect(Collectors.toList());
            return JaneHelper.from(tramiteBachillers)
                    .join("tramite.alumno", "codigo")
                    .join("tramite.cicloAcademico", "descripcion")
                    .join("tramite.persona", "paterno,materno,nombres,apellidosNombres")
                    .join("resolucion.oficina", "codigo,id")
                    .join("resolucionFacultad")
                    .array();
        } else if (tipoResolucionEnum == TITUL) {
            List<TramiteTitulo> tramiteTitulo = service.allResulucionTituloFacultadRes(resolucion);
            return JaneHelper.from(tramiteTitulo)
                    .join("tramite.alumno", "codigo")
                    .join("tramite.cicloAcademico", "descripcion")
                    .join("tramite.persona", "paterno,materno,nombres,apellidosNombres")
                    .join("resolucion.oficina", "codigo,id")
                    .join("resolucion")
                    //                        .join("cicloAcademico", "id,descripcion,nombre")
                    .array();
        } else if (tipoResolucionEnum == TITULBAC) {
            List<TramiteTitulo> tramiteTitulo = service.allResulucionTituloFacultad(resolucion);
            return JaneHelper.from(tramiteTitulo)
                    .join("tramite.alumno", "codigo")
                    .join("tramite.cicloAcademico", "descripcion")
                    .join("tramite.persona", "paterno,materno,nombres")
                    .join("resolucion.oficina", "codigo,id")
                    .join("resolucionFacultad")
                    //                        .join("cicloAcademico", "id,descripcion,nombre")
                    .array();
        } else if (tipoResolucionEnum == ALUMRENUNCIA) {
            /* aqi buscamos mostrar no editar*/
            List<TramiteRenunciaAlumno> tramiteRenuncia = service.allResolucionRenunciaAlumno(resolucion);
            return JaneHelper.from(tramiteRenuncia)
                    .join("tramite.alumno", "codigo")
                    .join("tramite.persona", "paterno,materno,nombres,numeroDocIdentidad")
                    .join("resolucion.oficina", "codigo,id")
                    .join("resolucion")
                    .array();
        } else if (tipoResolucionEnum == RENUNCIA_CAR) {
            /* aqi buscamos mostrar no editar*/
            List<TramiteRenunciaAlumno> tramiteRenuncia = service.allResolucionRenunciaAlumno(resolucion);
            return JaneHelper.from(tramiteRenuncia)
                    .join("tramite.alumno", "codigo")
                    .join("tramite.persona", "paterno,materno,nombres,numeroDocIdentidad")
                    .join("resolucion.oficina", "codigo,id")
                    .join("resolucion")
                    .array();
        } else if (tipoResolucionEnum == PRACTICAS) {
            List<PracticasPreProfesional> practicasPre = service.allPracticasPreProfesionales(resolucion);
            return JaneHelper.from(practicasPre)
                    .join("curso")
                    .join("tramite.alumno")
                    .join("tramite.alumno.persona")
                    .join("tramite.persona", "paterno,materno,nombres,apellidosNombres")
                    .join("tramite.alumno.persona.tipoDocumento")
                    .array();

        } else if (tipoResolucionEnum == READMISION) {
            List<Readmision> readmisiones = service.allReadmisionByResolucion(resolucion);
            return JaneHelper.from(readmisiones)
                    .join("facultad")
                    .join("alumno")
                    .join("alumno.persona")
                    .join("alumno.persona.tipoDocumento")
                    .join("cicloReadmitido")
                    .array();
        } else if (tipoResolucionEnum == CAMBIO_PLAN_CURRICULAR) {
            List<CambioPlanCurricular> cambioPlanCurriculares = service.allCambioPlanCurricularByResolucion(resolucion);
            return JaneHelper.from(cambioPlanCurriculares)
                    .join("facultad")
                    .join("alumno")
                    .join("alumno.persona")
                    .join("alumno.persona.tipoDocumento")
                    .join("cicloAcademico")
                    .array();
        } else if (tipoResolucionEnum == SUSP_DISCIPLI){
//            List<SancionDisciplina> sancionDisciplinas = service.allSancionDisciplinaByResolucion(resolucion);
            List<SancionDisciplinaCiclo> sancionDisciplinaCiclos = service.allSancioCicloAlumnoByResolucion(resolucion);
            return JaneHelper.from(sancionDisciplinaCiclos)
                    .join("ciclo")
                    .join("sancionDisciplina")
                    .join("sancionDisciplina.alumno")
                    .join("sancionDisciplina.alumno.persona")
                    .join("sancionDisciplina. alumno.persona.tipoDocumento")
                    .array();
        } else if (tipoResolucionEnum == DOCENTE_RESOL_CU){
            List<DocenteResolucion> docenteResolucions = service.allDocenteResolucionConsejoByResolucion(resolucion);
            return JaneHelper.from(docenteResolucions)
                    .join("tramite")
                    .join("ciclo")
                    .join("docente")
                    .join("docente.persona")
                    .join("docente.persona.tipoDocumento")
                    .array();
        } else if (tipoResolucionEnum == DOCENTE_RESOL_FC){
            List<DocenteResolucion> docenteResolucions = service.allDocenteResolucionFacultadByResolucion(resolucion);
            return JaneHelper.from(docenteResolucions)
                    .join("tramite")
                    .join("ciclo")
                    .join("docente")
                    .join("docente.persona")
                    .join("docente.persona.tipoDocumento")
                    .array();
        }

        return new ArrayNode(JsonNodeFactory.instance);
    }

    private ObjectNode findDataResolucion(Resolucion resolucion) {

        ObjectNode objectNode = JaneHelper.from(resolucion)
                .join("tipoResolucion")
                .join("oficina")
                .join("cicloAplica")
                .json();

        if (resolucion.getTipoResolucion().getCodigo().equals(BACHI.name())) {

            List<TramiteBachiller> bachillersCUTotal = service.allTramiteBachiller(resolucion);
//            List<TramiteBachiller> bachillers = bachillersCUTotal.stream().filter(x ->!x.getEstado().equalsIgnoreCase(TramiteEstadoEnum.ANU.name()) && !x.getEstadofacultad().equalsIgnoreCase(TramiteEstadoEnum.ANU.name())).collect(Collectors.toList());
            List<TramiteBachiller> bachillers = bachillersCUTotal.stream()
                    .filter(x -> (x.getEstado() == null || !x.getEstado().equalsIgnoreCase(TramiteEstadoEnum.ANU.name())) &&
                            (x.getEstadofacultad() == null || !x.getEstadofacultad().equalsIgnoreCase(TramiteEstadoEnum.ANU.name())))
                    .collect(Collectors.toList());

            for (TramiteBachiller bachiller : bachillers) {
                bachiller.setAlumno(bachiller.getTramite().getAlumno());
            }

            ArrayNode array = JaneHelper.from(bachillers).only("id")
                    .join("alumno", "id,codigo")
                    .join("alumno.carrera", "id,nombre")
                    .join("alumno.persona", "id,apellidosNombres,numeroDocIdentidad")
                    .join("alumno.persona.tipoDocumento", "id,simbolo")
                    .join("tramite", "id")
                    .array();

            objectNode.set("tramiteBachiller", array);

        } else if (resolucion.getTipoResolucion().getCodigo().equals(BACHIFAC.name())) {
            List<TramiteBachiller> bachillersFAC = service.allTramiteBachillerFacultad(resolucion);

            List<TramiteBachiller> bachillers = bachillersFAC.stream().filter(x ->!x.getEstado().equalsIgnoreCase(TramiteEstadoEnum.ANU.name()) && !x.getEstadofacultad().equalsIgnoreCase(TramiteEstadoEnum.ANU.name())).collect(Collectors.toList());

//            List<TramiteBachiller> bachillers = bachillersFAC.stream()
//                    .filter(x -> !TramiteEstadoEnum.ANU.equals(x.getEstado()) &&
//                            !TramiteEstadoEnum.ANU.equals(x.getEstadofacultad()))
//                    .collect(Collectors.toList());


            for (TramiteBachiller bachiller : bachillers) {
                bachiller.setAlumno(bachiller.getTramite().getAlumno());
            }
            ArrayNode array = JaneHelper.from(bachillers).only("id")
                    .join("alumno", "id,codigo")
                    .join("alumno.carrera", "id,nombre")
                    .join("alumno.persona", "id,apellidosNombres,numeroDocIdentidad")
                    .join("alumno.persona.tipoDocumento", "id,simbolo")
                    .join("tramite", "id")
                    .array();

            objectNode.set("tramiteBachiller", array);

        } else if (resolucion.getTipoResolucion().getCodigo().equals(ALUMRENUNCIA.name())) {
            /* para editar la renuncia*/
            List<TramiteRenunciaAlumno> renunciaAlumno = service.allResolucionRenunciaAlumno(resolucion);

            for (TramiteRenunciaAlumno renuncia : renunciaAlumno) {
                renuncia.setAlumno(renuncia.getTramite().getAlumno());
            }
            ArrayNode array = JaneHelper.from(renunciaAlumno).only("id")
                    .join("alumno", "id,codigo")
                    .join("alumno.carrera", "id,nombre")
                    .join("alumno.persona", "id,apellidosNombres,numeroDocIdentidad")
                    .join("alumno.persona.tipoDocumento", "id,simbolo")
                    .join("tramite", "id")
                    .array();
            objectNode.set("tramiteRenunciaAlumno", array);

        } else if (resolucion.getTipoResolucion().getCodigo().equals(TITULBAC.name())) {
            List<TramiteTitulo> tramiteTitulos = service.allTramiteTituloFacultad(resolucion);

            for (TramiteTitulo tit : tramiteTitulos) {
                tit.setAlumno(tit.getTramite().getAlumno());
            }
            ArrayNode array = JaneHelper.from(tramiteTitulos).only("id")
                    .join("alumno", "id,codigo")
                    .join("alumno.carrera", "id,nombre")
                    .join("alumno.persona", "id,apellidosNombres,numeroDocIdentidad")
                    .join("alumno.persona.tipoDocumento", "id,simbolo")
                    .join("tramite", "id")
                    .array();

            objectNode.set("tramiteTitulos", array);
        } else if (resolucion.getTipoResolucion().getCodigo().equals(TITUL.name())) {

            List<TramiteTitulo> tramiteTitulos = service.allTramiteTitulo(resolucion);

            for (TramiteTitulo tit : tramiteTitulos) {
                tit.setAlumno(tit.getTramite().getAlumno());
            }

            ArrayNode array = JaneHelper.from(tramiteTitulos).only("id")
                    .join("alumno", "id,codigo")
                    .join("alumno.carrera", "id,nombre")
                    .join("alumno.persona", "id,apellidosNombres,numeroDocIdentidad")
                    .join("alumno.persona.tipoDocumento", "id,simbolo")
                    .join("tramite", "id")
                    .array();

            objectNode.set("tramiteTitulos", array);

        } else if (resolucion.getTipoResolucion().getCodigo().equals(PRACTICAS.name())) {

            List<PracticasPreProfesional> practicasPreProfesionals = service.allPracticasPreProfesionales(resolucion);

            for (PracticasPreProfesional prac : practicasPreProfesionals) {
                prac.setAlumno(prac.getTramite().getAlumno());
            }

            ArrayNode array = JaneHelper.from(practicasPreProfesionals).only("id,creditos")
                    .join("alumno", "id,codigo")
                    .join("alumno.carrera", "id,nombre")
                    .join("alumno.persona", "id,apellidosNombres,numeroDocIdentidad")
                    .join("alumno.persona.tipoDocumento", "id,simbolo")
                    .join("tramite", "id")
                    .array();

            objectNode.set("tramitePracticasPreProfesionales", array);

        } else if (resolucion.getTipoResolucion().getCodigo().equals(RCI.name())) {

            List<RetiroCiclo> retiroCiclos = service.allRetiroCicloByResolucion(resolucion);

            ArrayNode array = JaneHelper.from(retiroCiclos).only("id")
                    .join("alumno", "id,codigo")
                    .join("alumno.carrera", "id,nombre")
                    .join("alumno.persona", "id,apellidosNombres,numeroDocIdentidad")
                    .join("alumno.persona.tipoDocumento", "id,simbolo")
                    .join("tramite", "id")
                    .join("cicloAcademico")
                    .array();

            objectNode.set("retiroCiclo", array);

        } else if (resolucion.getTipoResolucion().getCodigo().equals(TRAS_INT.name())) {

            List<TramiteTraslado> tramiteTraslados = service.allTramiteTrasladoByResolucion(resolucion);

            ArrayNode array = JaneHelper.from(tramiteTraslados)
                    .join("alumno", "id,codigo")
                    .join("alumno.carrera", "id,nombre")
                    .join("alumno.persona", "id,apellidosNombres,numeroDocIdentidad")
                    .join("alumno.persona.tipoDocumento", "id,simbolo")
                    .join("tramite", "id")
                    .join("cicloAcademico", "id,descripcion,codigo")
                    .array();

            objectNode.set("tramiteTraslado", array);

        } else if (resolucion.getTipoResolucion().getCodigo().equals(CURDIR.name())) {

            List<CursoDirigido> cursosDirigido = service.allCursodirigido(resolucion);
            for (CursoDirigido cursoDirigido : cursosDirigido) {
                cursoDirigido.setAlumno(cursoDirigido.getTramite().getAlumno());
                cursoDirigido.setSeleccionado(true);
            }
            ArrayNode array = JaneHelper.from(cursosDirigido)
                    .join("alumno", "id,codigo")
                    .join("alumno.carrera", "id,nombre")
                    .join("alumno.persona", "id,apellidosNombres,numeroDocIdentidad")
                    .join("alumno.persona.tipoDocumento", "id,simbolo")
                    .join("docenteAsignado", "id, codigo, estado")
                    .join("docenteAsignado.persona", "id, apellidosNombres, numeroDocIdentidad, estado")
                    .join("tramite", "id")
                    //.join("cicloAcademico", "id,descripcion,codigo")
                    .array();

            objectNode.set("cursoDirigido", array);

        } else if (resolucion.getTipoResolucion().getCodigo().equals(SUSP_DISCIPLI.name())) {
            List<SancionDisciplina> sancionados = service.allSancionDisciplinaByResolucion(resolucion);

            for (SancionDisciplina sancion : sancionados) {
                sancion.setAlumno(sancion.getTramite().getAlumno());
                sancion.setSeleccionado(true);
            }

            ArrayNode array = JaneHelper.from(sancionados).only("id")
                    .join("alumno", "id,codigo")
                    .join("alumno.carrera", "id,nombre")
                    .join("alumno.persona", "id,apellidosNombres,numeroDocIdentidad")
                    .join("alumno.persona.tipoDocumento", "id,simbolo")
                    .join("tramite", "id")
                    //.join("cicloAcademico", "id,descripcion,codigo")
                    .array();

            objectNode.set("sancionDisciplina", array);
        } else if (resolucion.getTipoResolucion().getCodigo().equals(DOCENTE_RESOL_CU.name())) {
            List<DocenteResolucion> docenteResolucions = service.allDocenteResolucionConsejoByResolucion(resolucion);

            for (DocenteResolucion docenteResolucion : docenteResolucions) {
                docenteResolucion.setDocente(docenteResolucion.getTramite().getDocente());
            }

            ArrayNode array = JaneHelper.from(docenteResolucions).only("id")
                    .join("docente","id,codigo")
                    .join("docente.persona","id,apellidosNombres,numeroDocIdentidad")
                    .join("docente.persona.tipoDocumento", "id,simbolo")
                    .join("tramite", "id")
                    .array();

            objectNode.set("docenteResolucion",array);
        } else if (resolucion.getTipoResolucion().getCodigo().equals(DOCENTE_RESOL_FC.name())) {
            List<DocenteResolucion> docenteResolucions = service.allDocenteResolucionFacultadByResolucion(resolucion);

            for (DocenteResolucion docenteResolucion : docenteResolucions) {
                docenteResolucion.setDocente(docenteResolucion.getTramite().getDocente());
            }

            ArrayNode array = JaneHelper.from(docenteResolucions).only("id")
                    .join("docente","id,codigo")
                    .join("docente.persona","id,apellidosNombres,numeroDocIdentidad")
                    .join("docente.persona.tipoDocumento", "id,simbolo")
                    .join("tramite", "id")
                    .array();

            objectNode.set("docenteResolucion",array);
        }

        return objectNode;
    }

    @ResponseBody
    @RequestMapping("allCiclosRepetido/{idAlumno}")
    public JsonResponse allCiclosRepetido(@PathVariable(value = "idAlumno") Long idAlumno,
            HttpSession session) {
        JsonResponse response = new JsonResponse();

        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);

        List<AlumnoCicloCursoBean> alumnoCicloCursoBeans = service.allCiclosRepetido(idAlumno, ds);

        ArrayNode array = JaneHelper.from(alumnoCicloCursoBeans)
                .join("alumno", "id")
                .join("curso", "id,nombre,codigo")
                .join("cicloAcademico", "id,descripcion")
                .array();

        response.setSuccess(Boolean.TRUE);
        response.setData(array);
        return response;
    }

    @ResponseBody
    @RequestMapping("allBachiller")
    public ArrayNode allBachiller(HttpSession session) {

        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);

        List<TramiteBachiller> tramitesBachiller = service.allBachiller(ds);

        for (TramiteBachiller tramiteBachiller : tramitesBachiller) {
            tramiteBachiller.setAlumno(tramiteBachiller.getTramite().getAlumno());
            tramiteBachiller.setSeleccionado(Boolean.FALSE);
        }

        return JaneHelper.from(tramitesBachiller)
                .join("tramite")
                .join("alumno", "id,codigo")
                .join("alumno.carrera", "id,codigo,nombre")
                .join("alumno.carrera.facultad", "id,codigo,nombre,simbolo")
                .join("alumno.persona", "id,apellidosNombres")
                .join("alumno.persona.tipoDocumento")
                .array();

    }

    @ResponseBody
    @RequestMapping("allBachillerFacultad")
    public ArrayNode allBachillerFacultad(HttpSession session) {

        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);

        List<TramiteBachiller> tramitesBachiller = service.allBachillerFacultad(ds);

        for (TramiteBachiller tramiteBachiller : tramitesBachiller) {
            tramiteBachiller.setAlumno(tramiteBachiller.getTramite().getAlumno());
            tramiteBachiller.setSeleccionado(Boolean.FALSE);
        }

        return JaneHelper.from(tramitesBachiller)
                .join("tramite")
                .join("alumno", "id,codigo")
                .join("alumno.carrera", "id,codigo,nombre")
                .join("alumno.carrera.facultad", "id,codigo,nombre,simbolo")
                .join("alumno.persona", "id,apellidosNombres")
                .join("alumno.persona.tipoDocumento")
                .array();

    }

    @ResponseBody
    @RequestMapping("allTitulo")
    public ArrayNode allTitulo(HttpSession session) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);

        List<TramiteTitulo> tramiteTitulos = service.allTitulos(ds);

        for (TramiteTitulo tramiteTitulo : tramiteTitulos) {
            tramiteTitulo.setAlumno(tramiteTitulo.getTramite().getAlumno());
            tramiteTitulo.setSeleccionado(Boolean.FALSE);
        }

        return JaneHelper.from(tramiteTitulos)
                .join("alumno")
                .join("alumno.carrera")
                .join("alumno.carrera.facultad","id,codigo,nombre,simbolo")
                .join("alumno.persona")
                .join("alumno.persona.tipoDocumento")
                .array();
    }

    @ResponseBody
    @RequestMapping("allRenunciaAlumno")
    public ArrayNode allRenunciaAlumno(HttpSession session) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);

        List<TramiteRenunciaAlumno> tramiteRenunciaAlumno = service.allRenunciaSolicitados(ds);

        for (TramiteRenunciaAlumno renunciaAlumno : tramiteRenunciaAlumno) {
            renunciaAlumno.setAlumno(renunciaAlumno.getTramite().getAlumno());
            renunciaAlumno.setSeleccionado(Boolean.FALSE);
        }
        return JaneHelper.from(tramiteRenunciaAlumno)
                .join("alumno")
                .join("alumno.carrera")
                .join("alumno.carrera.facultad")
                .join("alumno.persona")
                .join("alumno.persona.tipoDocumento")
                .array();
    }

    @ResponseBody
    @RequestMapping("allRenunciaAlumnoCarrera")
    public ArrayNode allRenunciaAlumnoCarrera(HttpSession session) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);

        List<TramiteRenunciaAlumno> tramiteRenunciaAlumno = service.allRenunciaSolicitadosCarrera(ds);

        for (TramiteRenunciaAlumno renunciaAlumno : tramiteRenunciaAlumno) {
            renunciaAlumno.setAlumno(renunciaAlumno.getTramite().getAlumno());
            renunciaAlumno.setSeleccionado(Boolean.FALSE);
        }
        return JaneHelper.from(tramiteRenunciaAlumno)
                .join("alumno")
                .join("alumno.carrera")
                .join("alumno.carrera.facultad")
                .join("alumno.persona")
                .join("alumno.persona.tipoDocumento")
                .array();
    }

    @ResponseBody
    @RequestMapping("allTituloFacultad")
    public ArrayNode allTituloFacultad(@RequestParam(value = "facultad", required = false) String facultad, HttpSession session) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);

        List<TramiteTitulo> tramiteTitulosFacultad = service.allTitulosFacultad(ds, facultad);

        for (TramiteTitulo tramiteTitulo : tramiteTitulosFacultad) {
            tramiteTitulo.setAlumno(tramiteTitulo.getTramite().getAlumno());
            tramiteTitulo.setSeleccionado(Boolean.FALSE);
        }
        return JaneHelper.from(tramiteTitulosFacultad)
                .join("alumno","id,codigo")
                .join("alumno.carrera","id,codigo,nombre")
                .join("alumno.carrera.facultad","id,codigo,nombre,simbolo")
                .join("alumno.persona","id,apellidosNombres")
                .join("alumno.persona.tipoDocumento")
                .array();
    }

    @ResponseBody
    @RequestMapping("allPracticas")
    public ArrayNode allPracticas(HttpSession session) {

        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);

        List<PracticasPreProfesional> practicasPreProfesionals = service.allPracticas(ds);

        for (PracticasPreProfesional practicasPreProfesional : practicasPreProfesionals) {
            practicasPreProfesional.setAlumno(practicasPreProfesional.getTramite().getAlumno());
            practicasPreProfesional.setSeleccionado(Boolean.FALSE);
        }

        return JaneHelper.from(practicasPreProfesionals)
                .join("alumno")
                .join("alumno.carrera")
                .join("alumno.carrera.facultad")
                .join("alumno.persona")
                .join("alumno.persona.tipoDocumento")
                .array();
    }

    @ResponseBody
    @RequestMapping("allRetiroCiclo")
    public ArrayNode allRetiroCiclo(HttpSession session) {

        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);

        List<RetiroCiclo> retiroCiclos = service.allRetiroCiclo(ds);

        for (RetiroCiclo retiroCiclo : retiroCiclos) {
            retiroCiclo.setAlumno(retiroCiclo.getTramite().getAlumno());
            retiroCiclo.setSeleccionado(Boolean.FALSE);
        }

        return JaneHelper.from(retiroCiclos)
                .join("alumno")
                .join("alumno.carrera")
                .join("alumno.carrera.facultad")
                .join("alumno.persona")
                .join("alumno.persona.tipoDocumento")
                .array();
    }

    @ResponseBody
    @RequestMapping("allSancion")
    public ArrayNode allSancionDisc(HttpSession session) {

        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);

        List<SancionDisciplina> sancionados = service.allSancionDisciplina();

        for (SancionDisciplina sancionDisc : sancionados) {
            sancionDisc.setAlumno(sancionDisc.getTramite().getAlumno());
            sancionDisc.setSeleccionado(Boolean.FALSE);
        }

        return JaneHelper.from(sancionados)
                .join("alumno")
                .join("alumno.carrera")
                .join("alumno.carrera.facultad")
                .join("alumno.persona")
                .join("alumno.persona.tipoDocumento")
                .array();
    }

    @ResponseBody
    @RequestMapping("allDocenteResolucionConsejo")
    public ArrayNode allDocenteResolucionConsejo(HttpSession session) {

        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);

        List<DocenteResolucion> docentes = service.allDocenteResolucionConsejo();

        for (DocenteResolucion docente : docentes) {
            docente.setDocente(docente.getTramite().getDocente());
            docente.setSeleccionado(Boolean.FALSE);
        }

        return JaneHelper.from(docentes)
                .join("docente")
                .join("docente.persona")
                .join("docente.persona.tipoDocumento")
                .array();
    }

    @ResponseBody
    @RequestMapping("allDocenteResolucionFacultad")
    public ArrayNode allDocenteResolucionFacultad(HttpSession session) {

        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);

        List<DocenteResolucion> docentes = service.allDocenteResolucionFacultad();

        for (DocenteResolucion docente : docentes) {
            docente.setDocente(docente.getTramite().getDocente());
            docente.setSeleccionado(Boolean.FALSE);
        }

        return JaneHelper.from(docentes)
                .join("docente")
                .join("docente.persona")
                .join("docente.persona.tipoDocumento")
                .array();
    }

    @ResponseBody
    @RequestMapping("allReincorporacion")
    public ArrayNode allReincorporacion(HttpSession session) {

        List<Reincorporacion> reincorporaciones = service.allReincorporacion();

        return JaneHelper.from(reincorporaciones)
                .join("cicloReincorporacion")
                .join("alumno")
                .join("alumno.carrera")
                .join("alumno.carrera.facultad")
                .join("alumno.persona")
                .join("alumno.persona.tipoDocumento")
                .array();
    }

    @ResponseBody
    @RequestMapping("allReadmision")
    public ArrayNode allReadmision(HttpSession session) {

        List<Readmision> readmisiones = service.allReadmision();

        return JaneHelper.from(readmisiones)
                .join("cicloReadmitido")
                .join("alumno")
                .join("alumno.carrera")
                .join("alumno.carrera.facultad")
                .join("alumno.persona")
                .join("alumno.persona.tipoDocumento")
                .array();
    }

    @ResponseBody
    @RequestMapping("allCambioPlanCurricular")
    public ArrayNode allCambioPlanCurricular(HttpSession session) {

        List<CambioPlanCurricular> cambioPlanCurriculares = service.allCambioPlanCurricular();

        return JaneHelper.from(cambioPlanCurriculares)
                .join("planCurricularOrigen")
                .join("planCurricularOrigen.cicloInicioVigencia")
                .join("planCurricularDestino")
                .join("planCurricularDestino.cicloInicioVigencia")
                .join("cicloAcademico")
                .join("alumno")
                .join("alumno.carrera")
                .join("alumno.carrera.facultad")
                .join("alumno.persona")
                .join("alumno.persona.tipoDocumento")
                .array();
    }

    @ResponseBody
    @RequestMapping("allTrasladoInterno")
    public ArrayNode allTrasladoInterno(HttpSession session) {

        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);

        List<TramiteTraslado> tramiteTraslados = service.allTrasladoInterno(ds.getCicloAcademico());

        return JaneHelper.from(tramiteTraslados)
                .join("carrera", "id,nombre")
                .join("carreraOrigen", "id,nombre")
                .join("cicloAcademico", "id,year,codigo,descripcion,descripcion2")
                .join("alumno", "id,codigo")
                .join("alumno.carrera", "id,nombre")
                .join("alumno.carrera.facultad", "id,nombre")
                .join("alumno.persona", "id,apellidosNombres")
                .join("alumno.persona.tipoDocumento")
                .array();

    }

    @ResponseBody
    @RequestMapping("/anularSancion/{sancionId}")
    public JsonResponse anularSancionDisciplina(@PathVariable(value = "sancionId") Long sancionId) {
        JsonResponse response = new JsonResponse();
        try {

            service.anularSancionDisciplina(sancionId);

            response.setMessage("Sancion anulada correctamente.");
            response.setSuccess(true);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (RuntimeException e) {
            ExceptionHandler.handleSpecial(e, response, GlobalMessages.FK_ERROR_UPDATE);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

}
