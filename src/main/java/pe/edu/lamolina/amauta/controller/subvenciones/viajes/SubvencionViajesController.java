package pe.edu.lamolina.amauta.controller.subvenciones.viajes;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.Arrays;
import java.util.List;
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
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableResponse;
import pe.albatross.zelpers.json.JaneHelper;
import pe.albatross.zelpers.miscelanea.ExceptionHandler;
import pe.albatross.zelpers.miscelanea.JsonResponse;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.edu.lamolina.amauta.config.DespliegueConfig;
import pe.edu.lamolina.amauta.controller.seguridad.verificador.VerificadorService;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Curso;
import pe.edu.lamolina.model.academico.DepartamentoAcademico;
import pe.edu.lamolina.model.academico.Docente;
import pe.edu.lamolina.model.academico.Seccion;
import pe.edu.lamolina.model.bienestar.AlumnoViajeCurso;
import pe.edu.lamolina.model.bienestar.CronogramaEventoSubvencionado;
import pe.edu.lamolina.model.bienestar.ObjecionViajeEvento;
import pe.edu.lamolina.model.bienestar.ProformaEventoSubvencionado;
import pe.edu.lamolina.model.bienestar.ViajeCurso;
import pe.edu.lamolina.model.constantines.GlobalConstantine;
import pe.edu.lamolina.model.contabilidad.ItemJustificacionGasto;
import pe.edu.lamolina.model.contabilidad.JustificacionGasto;
import pe.edu.lamolina.model.contabilidad.JustificacionGastoAlumno;
import static pe.edu.lamolina.model.enums.subvenciones.ViajeCursoEstadoEnum.JUSTIFICADO;

@Slf4j
@Controller
@AllArgsConstructor(onConstructor = @__(
        @Autowired))
@RequestMapping("subvenciones/viajes")
public class SubvencionViajesController {

    private final SubvencionViajesService service;
    private final DespliegueConfig despliegueConfig;
    private final VerificadorService verificadorService;

    private final String rutaModulo = this.getClass().getAnnotation(RequestMapping.class).value()[0];

    @RequestMapping(method = RequestMethod.GET)
    public String index(Model model, HttpSession session) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
        Docente docenteUser = ds.getDocente();

        CicloAcademico ciclo = service.findCicloSubvenciones();

        model.addAttribute("isDocenteUser", docenteUser != null);
        model.addAttribute("isProduccion", despliegueConfig.isProduccion());
        model.addAttribute("ciclo", ciclo);
        model.addAttribute("rutaModulo", rutaModulo);

        return "subvenciones/viajes/subvencionViajes";
    }

    @ResponseBody
    @RequestMapping("list")
    public DynatableResponse list(HttpSession session, DynatableFilter filter) {

        DynatableResponse json = new DynatableResponse();
        try {

            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
            List<DepartamentoAcademico> dptos = service.allDptosAcademicos(ds);
            CicloAcademico ciclo = service.findCicloSubvenciones();
            Docente docenteUser = ds.getDocente();

            List<ViajeCurso> viajes = service.allDynatbleByDocente(docenteUser, dptos, ciclo, filter);

            ArrayNode array = new ArrayNode(JsonNodeFactory.instance);
            for (ViajeCurso viajeCurso : viajes) {
                ObjectNode node = JaneHelper
                        .from(viajeCurso)
                        .only("id,estadoViaje,estadoSubvencion,estadoViajeEnum,estadoSubvencionEnum")
                        .join("curso", "id,codigo,nombre,tpc")
                        .join("curso.departamentoAcademico", "nombre")
                        .join("seccion", "id,codigo2,tipoSeccionEnum")
                        .join("seccion.grupoHoras", "id,codigo")
                        .join("docenteCreador", "codigo")
                        .join("docenteCreador.persona", "apellidosNombres")
                        .join("alumnoDelegado", "id,codigo")
                        .join("alumnoDelegado.persona", "id,apellidosNombres,numeroDocIdentidad")
                        .join("alumnoDelegado.persona.tipoDocumento", "simbolo")
                        .json();

                boolean esDocente = Boolean.FALSE;
                if (docenteUser != null) {
                    Docente docenteViaje = viajeCurso.getDocenteCreador();
                    esDocente = docenteViaje.getId().equals(docenteUser.getId());
                }

                boolean esJefeDpto = Boolean.FALSE;
                if (!dptos.isEmpty()) {
                    DepartamentoAcademico dptoCurso = viajeCurso.getCurso().getDepartamentoAcademico();
                    DepartamentoAcademico existe = dptos.stream()
                            .filter(dpto -> dpto.getId().equals(dptoCurso.getId()))
                            .findAny()
                            .orElse(null);
                    esJefeDpto = existe != null;
                }

                node.put("esDocente", esDocente);
                node.put("esJefeDpto", esJefeDpto);
                array.add(node);
            }

            json.setData(array);
            json.setTotal(viajes.size());
            json.setFiltered(viajes.size());

        } catch (Exception e) {
            json.setTotal(0);
        }
        return json;
    }

    @ResponseBody
    @RequestMapping("allCursos")
    public JsonResponse allCursos(HttpSession session) {
        JsonResponse response = new JsonResponse();
        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
            CicloAcademico ciclo = service.findCicloSubvenciones();
            List<Curso> cursos = service.allCursos(ds.getDocente(), ciclo, ds);

            ArrayNode cursosJson = JaneHelper
                    .from(cursos)
                    .only("id,codigo,nombre,tpc")
                    .join("departamentoAcademico", "id,codigo,nombre")
                    .array();

            response.setData(cursosJson);
            response.setSuccess(true);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }

        return response;
    }

    @ResponseBody
    @RequestMapping("allSecciones")
    public JsonResponse allSecciones(@RequestBody Curso curso, HttpSession session) {
        JsonResponse response = new JsonResponse();
        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
            CicloAcademico ciclo = service.findCicloSubvenciones();
            List<Seccion> secciones = service.allSecciones(curso, ds.getDocente(), ciclo, ds);

            ArrayNode seccionesJson = JaneHelper
                    .from(secciones)
                    .only("id,codigo2,tipoSeccionEnum")
                    .join("grupoHoras", "id,codigo")
                    .array();

            response.setData(seccionesJson);
            response.setSuccess(true);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }

        return response;
    }

    @ResponseBody
    @RequestMapping("allAlumnos")
    public JsonResponse allAlumnos(@RequestBody Seccion seccion, HttpSession session) {
        JsonResponse response = new JsonResponse();
        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
            List<Alumno> alumnos = service.allAlumnos(seccion, ds);

            ArrayNode alumnosJson = new ArrayNode(JsonNodeFactory.instance);
            alumnos.forEach(alu -> {
                ObjectNode node = JaneHelper
                        .from(alu)
                        .only("id,codigo")
                        .join("carrera", "codigo,descripcionCarreraFacultad")
                        .join("persona", "apellidosNombres")
                        .json();

                node.put("apellidosNombres", alu.getPersona().getApellidosNombres());
                alumnosJson.add(node);
            });

            response.setData(alumnosJson);
            response.setSuccess(true);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }

        return response;
    }

    @ResponseBody
    @RequestMapping("saveViaje")
    public JsonResponse saveViaje(@RequestBody ViajeCurso viajeCurso, HttpSession session) {
        JsonResponse response = new JsonResponse();
        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
            CicloAcademico ciclo = service.findCicloSubvenciones();
            if (viajeCurso.getId() == null) {
                service.saveViaje(viajeCurso, ciclo, ds);
            } else {
                service.updateViaje(viajeCurso, ciclo, ds);
            }

            response.setSuccess(true);
            response.setMessage("Se aprobó satisfactoriamente el informe");

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }

        return response;
    }

    @ResponseBody
    @RequestMapping("solicitarAprobarViaje")
    public JsonResponse solicitarAprobarViaje(@RequestBody ViajeCurso viajeCurso, HttpSession session) {
        JsonResponse response = new JsonResponse();
        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
            service.solicitarAprobarViaje(viajeCurso, ds);

            response.setSuccess(true);
            response.setMessage("Se aprobó satisfactoriamente el informe");

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }

        return response;
    }

    @ResponseBody
    @RequestMapping("aprobarViaje")
    public JsonResponse aprobarViaje(@RequestBody ViajeCurso viajeCurso, HttpSession session) {
        JsonResponse response = new JsonResponse();
        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
            service.aprobarViaje(viajeCurso, ds);

            response.setSuccess(true);
            response.setMessage("Se aprobó satisfactoriamente el informe");

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }

        return response;
    }

    @ResponseBody
    @RequestMapping("aprobarJustificacion")
    public JsonResponse aprobarJustificacion(@RequestBody ViajeCurso viajeCurso, HttpSession session) {
        JsonResponse response = new JsonResponse();
        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
            service.aprobarJustificacion(viajeCurso, ds);

            response.setSuccess(true);
            response.setMessage("Se aprobó satisfactoriamente el informe");

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }

        return response;
    }

    @ResponseBody
    @RequestMapping("observaJustificacion")
    public JsonResponse observaJustificacion(@RequestBody ViajeCurso viajeCurso, HttpSession session) {
        JsonResponse response = new JsonResponse();
        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
            service.observaJustificacion(viajeCurso, ds);

            response.setSuccess(true);
            response.setMessage("Se registró la observación satisfactoriamente");

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }

        return response;
    }

    @RequestMapping("{idViajeCurso}/configurar")
    public String configurar(
            @RequestParam("origen") String origen,
            @PathVariable("idViajeCurso") Long idViajeCurso, Model model, HttpSession session) {

        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);

        ViajeCurso viajeCurso = service.findViaje(new ViajeCurso(idViajeCurso), ds);

        Boolean aprobable = Arrays.asList(JUSTIFICADO).contains(viajeCurso.getEstadoViajeEnum());
        Boolean esDocenteCreador = this.esDocenteViaje(viajeCurso, ds);

        ObjectNode viajeCursoJson = this.createViajeCursoJson(viajeCurso);

        JustificacionGasto justificacion = service.findJustificacion(viajeCurso, ds);
        ObjectNode justificacionJson = this.createJustificacionJson(justificacion);

        List<AlumnoViajeCurso> alumnosviaje = service.allAlumnosByViaje(viajeCurso);
        ArrayNode alumnosViajeJson = this.createAlumnosViajeJson(alumnosviaje);

        List<ObjecionViajeEvento> objeciones = service.allObjecionesActivas(viajeCurso, ds);
        ArrayNode objecionesJson = this.createObjecionesJson(objeciones);

        CicloAcademico ciclo = service.findCicloSubvenciones();

        model.addAttribute("esDocenteCreador", esDocenteCreador);
        model.addAttribute("viajeCurso", viajeCurso);
        model.addAttribute("viajeCursoJson", viajeCursoJson.toString());
        model.addAttribute("ciclo", ciclo);
        model.addAttribute("aprobable", aprobable);
        model.addAttribute("justificacionJson", justificacionJson.toString());
        model.addAttribute("alumnosViajeJson", alumnosViajeJson.toString());
        model.addAttribute("objecionesJson", objecionesJson.toString());
        model.addAttribute("rutaModulo", rutaModulo);
        model.addAttribute("origen", verificadorService.getOrigen(origen, "/subvenciones/viajes"));

        return "subvenciones/viajes/configuraViajeCurso";
    }

    private Boolean esDocenteViaje(ViajeCurso viajeCurso, DataSessionPivot ds) {
        Docente docenteUser = ds.getDocente();
        if (docenteUser == null) {
            return false;
        }

        Docente docenteCreador = viajeCurso.getDocenteCreador();
        return docenteCreador.getId().equals(docenteUser.getId());
    }

    private ArrayNode createAlumnosViajeJson(List<AlumnoViajeCurso> alumnosByViaje) {

        ArrayNode array = new ArrayNode(JsonNodeFactory.instance);

        for (AlumnoViajeCurso alumnoViaje : alumnosByViaje) {
            ViajeCurso viaje = alumnoViaje.getViajeCurso();

            Boolean aprobable = Arrays.asList(JUSTIFICADO).contains(viaje.getEstadoViajeEnum());

            ObjectNode node = JaneHelper
                    .from(alumnoViaje)
                    .only("id,estado,estadoSubvencion,estadoSalud,estadoAsistencia,estadoEnum,estadoSubvencionEnum,estadoSaludEnum,estadoAsistenciaEnum,importeAsignado,importeJustificado,importeDevuelto")
                    .join("alumno", "id,codigo")
                    .join("alumno.persona", "apellidosNombres,emailCompania,numeroDocIdentidad")
                    .join("viajeCurso", "id,fechaFinRegistroAlumnos,estadoViaje,estadoSubvencion,estadoViajeEnum,estadoSubvencionEnum")
                    .join("viajeCurso.curso", "id,codigo,nombre,tpc")
                    .join("viajeCurso.curso.departamentoAcademico", "nombre")
                    .join("viajeCurso.seccion", "id,codigo2,tipoSeccionEnum")
                    .join("viajeCurso.seccion.grupoHoras", "id,codigo")
                    .join("viajeCurso.docenteCreador", "codigo")
                    .join("viajeCurso.docenteCreador.persona", "apellidosNombres,emailCompania")
                    .join("viajeCurso.alumnoDelegado", "id,codigo")
                    .join("viajeCurso.alumnoDelegado.persona", "id,apellidosNombres,numeroDocIdentidad")
                    .join("viajeCurso.alumnoDelegado.persona.tipoDocumento", "simbolo")
                    .join("personaCuentaBancaria", "id,numeroCuenta,cuentaInterbancaria,esBcp")
                    .join("personaCuentaBancaria.banco", "nombre")
                    .join("personaCuentaBancaria.banco.empresa", "razonSocial")
                    .json();
            node.put("aprobable", aprobable);
            array.add(node);
        }
        return array;
    }

    private ObjectNode createJustificacionJson(JustificacionGasto justifica) {
        ObjectNode node = JaneHelper
                .from(justifica)
                .json();

        List<ItemJustificacionGasto> itemsJustica = justifica.getItemsJustificacion();

        ArrayNode arrayItems = new ArrayNode(JsonNodeFactory.instance);
        for (ItemJustificacionGasto item : itemsJustica) {
            ObjectNode nodeItem = JaneHelper
                    .from(item)
                    .only("id,estadoJustificacion,estadoEnum,descripcion,tipoGrupoAlumnos,tipoGrupoAlumnosEnum,observaciones,cantidadAlumnos,importe,importeAlumno,fechaAnulacion")
                    .join("factura", "id,ruta,nombre")
                    .json();

            List<JustificacionGastoAlumno> gastosAlumnos = item.getJustificacionesAlumnos();
            ArrayNode arrayAlumnos = JaneHelper
                    .from(gastosAlumnos)
                    .only("id,importeJustificado,fechaAnulacion")
                    .join("alumno", "codigo")
                    .join("alumno.persona", "apellidosNombres")
                    .array();

            nodeItem.set("justificacionesAlumnos", arrayAlumnos);

            arrayItems.add(nodeItem);
        }

        node.set("itemsJustificacion", arrayItems);
        return node;
    }

    private ObjectNode createViajeCursoJson(ViajeCurso viajeCurso) {

        Boolean aprobable = Arrays.asList(JUSTIFICADO)
                .contains(viajeCurso.getEstadoViajeEnum());

        ObjectNode viajeCursoJson = JaneHelper
                .from(viajeCurso)
                .only("id,rutaViaje,descripcionViaje,importeSolicitado,importeProforma,importeAlumno,estadoViaje,estadoSubvencion,estadoViajeEnum,estadoSubvencionEnum,cantidadAlumnosMatriculados,cantidadAlumnosRegistrados")
                .join("curso", "id,codigo,nombre,tpc")
                .join("curso.departamentoAcademico", "nombre")
                .join("seccion", "id,codigo2,tipoSeccionEnum")
                .join("seccion.grupoHoras", "id,codigo")
                .join("docenteCreador", "codigo")
                .join("docenteCreador.persona", "apellidosNombres")
                .join("alumnoDelegado", "id,codigo")
                .join("alumnoDelegado.persona", "id,apellidosNombres,numeroDocIdentidad")
                .join("alumnoDelegado.persona.tipoDocumento", "simbolo")
                .json();

        viajeCursoJson.put("aprobable", aprobable);

        ArrayNode cronogramasJson = new ArrayNode(JsonNodeFactory.instance);
        List<CronogramaEventoSubvencionado> cronogramas = viajeCurso.getCronogramasViaje();
        cronogramas.forEach(crono -> {
            ObjectNode cronoJson = JaneHelper
                    .from(crono)
                    .only("id,orden,descripcion,fecha,obligatorio")
                    .json();

            cronogramasJson.add(cronoJson);
        });

        ArrayNode proformasJson = new ArrayNode(JsonNodeFactory.instance);
        List<ProformaEventoSubvencionado> proformas = viajeCurso.getProformasViaje();
        proformas.forEach(crono -> {
            ObjectNode itemJson = JaneHelper
                    .from(crono)
                    .only("id,orden,descripcion,importe")
                    .join("folleto", "id,ruta,nombre")
                    .json();

            proformasJson.add(itemJson);
        });

        viajeCursoJson.set("cronogramasViaje", cronogramasJson);
        viajeCursoJson.set("proformasViaje", proformasJson);
        return viajeCursoJson;
    }

    @ResponseBody
    @RequestMapping("findJustificacion")
    public JsonResponse findJustificacion(@RequestBody ViajeCurso viajeCurso, HttpSession session) {
        JsonResponse response = new JsonResponse();
        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);

            JustificacionGasto justificacion = service.findJustificacion(viajeCurso, ds);
            ObjectNode justificacionJson = this.createJustificacionJson(justificacion);

            response.setData(justificacionJson);
            response.setSuccess(true);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }

        return response;
    }

    @ResponseBody
    @RequestMapping("findViaje")
    public JsonResponse findViaje(@RequestBody ViajeCurso viajeCursoForm, HttpSession session) {
        JsonResponse response = new JsonResponse();
        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);

            ViajeCurso viajeCursoBD = service.findViaje(viajeCursoForm, ds);
            ObjectNode viajeCursoJson = this.createViajeCursoJson(viajeCursoBD);

            response.setData(viajeCursoJson);
            response.setSuccess(true);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }

        return response;
    }

    @ResponseBody
    @RequestMapping("allObjecionesViaje")
    public JsonResponse allObjecionesViaje(@RequestBody ViajeCurso viajeCursoForm, HttpSession session) {
        JsonResponse response = new JsonResponse();
        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
            List<ObjecionViajeEvento> objeciones = service.allObjecionesActivas(viajeCursoForm, ds);

            response.setData(this.createObjecionesJson(objeciones));
            response.setSuccess(true);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }

        return response;
    }

    private ArrayNode createObjecionesJson(List<ObjecionViajeEvento> objeciones) {
        ArrayNode node = JaneHelper
                .from(objeciones)
                .join("viajeCurso", "id")
                .join("objecionOrigen", "id")
                .join("userInspeccion.persona", "nombreCompleto")
                .join("userCorrecion.persona", "nombreCompleto")
                .join("userAceptacion.persona", "nombreCompleto")
                .array();
        return node;
    }

    @ResponseBody
    @RequestMapping("addObjecion")
    public JsonResponse addObjecion(@RequestBody ObjecionViajeEvento objecion, HttpSession session) {
        JsonResponse response = new JsonResponse();
        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
            service.addObjecion(objecion, ds);

            response.setSuccess(true);
            response.setMessage("Se agregó satisfactoriamente la observación");

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }

        return response;
    }

    @ResponseBody
    @RequestMapping("deleteObjecion")
    public JsonResponse deleteObjecion(@RequestBody ObjecionViajeEvento objecion, HttpSession session) {
        JsonResponse response = new JsonResponse();
        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
            service.deleteObjecion(objecion, ds);

            response.setSuccess(true);
            response.setMessage("Se eliminó satisfactoriamente la observación");

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }

        return response;
    }

    @ResponseBody
    @RequestMapping("enviarObservacion")
    public JsonResponse enviarObservacion(@RequestBody ViajeCurso viajeCurso, HttpSession session) {
        JsonResponse response = new JsonResponse();
        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
            service.enviarObservacion(viajeCurso, ds);

            response.setSuccess(true);
            response.setMessage("Se envió las observaciones satisfactoriamente");

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }

        return response;
    }

    @ResponseBody
    @RequestMapping("aprobarRespuestaObjecion")
    public JsonResponse aprobarRespuestaObjecion(@RequestBody ObjecionViajeEvento objecion, HttpSession session) {
        JsonResponse response = new JsonResponse();
        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
            service.aprobarRespuestaObjecion(objecion, ds);

            response.setSuccess(true);
            response.setMessage("Se aprobó satisfactoriamente la respuesta de la observación");

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }

        return response;
    }

}
