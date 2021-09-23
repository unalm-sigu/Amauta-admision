package pe.edu.lamolina.amauta.controller.tramite.bolsainvestigacion;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
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
import org.springframework.web.bind.annotation.ResponseBody;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableResponse;
import pe.albatross.zelpers.json.JaneHelper;
import pe.albatross.zelpers.miscelanea.ExceptionHandler;
import pe.albatross.zelpers.miscelanea.JsonHelper;
import pe.albatross.zelpers.miscelanea.JsonResponse;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Facultad;
import pe.edu.lamolina.model.enums.TipoOficinaEnum;
import pe.edu.lamolina.model.general.Colaborador;
import pe.edu.lamolina.model.tramite.AlumnoBolsaInvestigacion;
import pe.edu.lamolina.model.tramite.BolsaInvestigacion;
import pe.edu.lamolina.amauta.controller.seguridad.verificador.VerificadorService;
import pe.edu.lamolina.model.constantines.GlobalConstantine;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;
import pe.edu.lamolina.model.zelper.util.ModelUtils;

@Controller
@RequestMapping("tramite/bolsainvestigacion")
public class BolsaInvestigacionController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    BolsaInvestigacionService service;

    @Autowired
    VerificadorService verificadorService;

    @RequestMapping(method = RequestMethod.GET)
    public String index(Model model, HttpSession session, HttpServletRequest request) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
        String codeRequest = verificadorService.generateCodeRequest();

        Facultad facultad = this.getFacultad(request, ds, codeRequest);
        if (facultad == null) {
            return "redirect:/";
        }

        model.addAttribute("facultad", facultad);
        model.addAttribute("ciclo", ds.getCicloAcademico().getDescripcion());

        return "tramite/bolsainvestigacion/bolsainvestigacion";
    }

    @ResponseBody
    @RequestMapping(value = "find", method = RequestMethod.GET)
    public JsonResponse find(HttpSession session, HttpServletRequest request) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
        JsonResponse response = new JsonResponse();
        String codeRequest = verificadorService.generateCodeRequest();

        try {
            response.setSuccess(Boolean.FALSE);

            Facultad facultad = this.getFacultad(request, ds, codeRequest);
            if (facultad != null) {
                BolsaInvestigacion bi = service.findByFacultadCicloAcademico(facultad, ds.getCicloAcademico());
                ObjectNode json = JaneHelper
                        .from(bi)
                        .only("id,estado,estadoEnum,becados,postulantes")
                        .join("facultad", "nombre")
                        .join("cicloAcademico", "descripcion")
                        .json();

                response.setData(json);
                response.setSuccess(Boolean.TRUE);
            }

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }

        return response;
    }

    @ResponseBody
    @RequestMapping(value = "list", method = RequestMethod.GET)
    public DynatableResponse list(DynatableFilter filter, HttpSession session, HttpServletRequest request) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
        String codeRequest = verificadorService.generateCodeRequest();
        Facultad facultad = this.getFacultad(request, ds, codeRequest);

        logger.info("facultad = {}", ModelUtils.toString(facultad, "id", "codigo", "nombre"));
        logger.info("ciclo = {}", ModelUtils.toString(ds.getCicloAcademico(), "id", "codigo", "descripcion"));

        List<AlumnoBolsaInvestigacion> alumnos = new ArrayList();
        if (facultad != null) {
            alumnos = service.allByDynatable(filter, facultad, ds.getCicloAcademico());
        }

        DynatableResponse json = new DynatableResponse();
        ArrayNode array = JaneHelper
                .from(alumnos)
                .only("id,estado,estadoEnum,nombreInvestigacion")
                .join("alumno", "id,codigo")
                .join("alumno.carrera", "codigo,nombre")
                .join("alumno.carrera.facultad", "codigo,nombre")
                .join("alumno.persona", "id,nombreCompleto,numeroDocIdentidad")
                .join("alumno.persona.tipoDocumento", "id,simbolo")
                .join("supervisor", "id")
                .join("supervisor.persona", "id,nombreCompleto,numeroDocIdentidad")
                .join("supervisor.persona.tipoDocumento", "id,simbolo")
                .join("supervisor.cargo", "nombre")
                .join("supervisor.oficina", "nombre")
                .join("supervisor.oficina.tipoOficina", "nivel")
                .join("supervisor.oficina.oficinaSuperior", "nombre")
                .join("supervisor.oficina.oficinaSuperior.tipoOficina", "nivel")
                .array();

        json.setData(array);
        json.setTotal(filter.getTotal());
        json.setFiltered(filter.getFiltered());

        return json;
    }

    @ResponseBody
    @RequestMapping(value = "alumnos", method = RequestMethod.POST)
    public JsonResponse alumnos(@RequestBody String nombre, HttpSession session, HttpServletRequest request) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
        JsonResponse response = new JsonResponse();
        String codeRequest = verificadorService.generateCodeRequest();

        try {
            CicloAcademico ciclo = ds.getCicloAcademico();
            List<Facultad> facultades = this.getFacultades(request, ds, codeRequest);
            List<Alumno> alumnos = service.searchAlumnosByFacultadNombre(facultades, nombre, ciclo);
            ArrayNode arr = new ArrayNode(JsonNodeFactory.instance);
            for (Alumno alumno : alumnos) {
                ObjectNode node = JsonHelper.createJson(alumno, JsonNodeFactory.instance, true,
                        new String[]{
                            "id", "codigo",
                            "carrera.nombre",
                            "carrera.codigo",
                            "carrera.facultad.nombre",
                            "carrera.facultad.codigo",
                            "orientacionCarrera.nombre",
                            "situacionAcademica.nombre",
                            "modalidadEstudio.nombre",
                            "modalidadEstudio.nombre",
                            "persona.id",
                            "persona.nombreCompleto",
                            "persona.numeroDocIdentidad",
                            "persona.tipoDocumento.simbolo",
                            "matriculaResumen.estadoEnum",
                            "matriculaResumen.creditosMatriculados"
                        });
                arr.add(node);
            }
            response.setData(arr);
            response.setSuccess(Boolean.TRUE);
        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping(value = "supervisores", method = RequestMethod.POST)
    public JsonResponse supervisores(@RequestBody String nombre, HttpSession session, HttpServletRequest request) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
        JsonResponse response = new JsonResponse();
        String codeRequest = verificadorService.generateCodeRequest();

        try {
            Facultad facultad = this.getFacultad(request, ds, codeRequest);
            List<Colaborador> colaboradores = service.searchColaboradoresByFacultadNombre(facultad, nombre);
            ArrayNode array = JaneHelper
                    .from(colaboradores)
                    .only("id")
                    .join("persona", "id,nombreCompleto,numeroDocIdentidad")
                    .join("persona.tipoDocumento", "id,simbolo")
                    .join("cargo", "nombre")
                    .join("oficina", "nombre")
                    .join("oficina.tipoOficina", "nivel")
                    .join("oficina.oficinaSuperior", "nombre")
                    .join("oficina.oficinaSuperior.tipoOficina", "nivel")
                    .array();

            response.setData(array);
            response.setSuccess(Boolean.TRUE);
        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping(value = "alumnos/save", method = RequestMethod.POST)
    public JsonResponse saveAlumno(@RequestBody AlumnoBolsaInvestigacion alumnoBolsa, HttpSession session, HttpServletRequest request) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
        JsonResponse response = new JsonResponse();
        String codeRequest = verificadorService.generateCodeRequest();

        try {
            Facultad facultad = this.getFacultad(request, ds, codeRequest);
            if (alumnoBolsa.getId() != null) {
                service.updateAlumno(facultad, ds.getCicloAcademico(), alumnoBolsa, ds);
                response.setMessage("Alumno actualizado");

            } else {
                service.agregarAlumno(facultad, ds.getCicloAcademico(), alumnoBolsa, ds);
                response.setMessage("Alumno agregado");
            }
            response.setSuccess(Boolean.TRUE);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping(value = "alumnos/{id}/find", method = RequestMethod.GET)
    public JsonResponse findAlumno(@PathVariable Long id, HttpSession session) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
        JsonResponse response = new JsonResponse();
        try {
            AlumnoBolsaInvestigacion abi = service.findAlumnoBolsaInvestigacion(id);

            ObjectNode node = JsonHelper.createJson(abi, JsonNodeFactory.instance, new String[]{
                "id",
                "nombreInvestigacion",
                "estado",
                "alumno.id",
                "alumno.codigo",
                "alumno.carrera.codigo",
                "alumno.carrera.nombre",
                "alumno.carrera.facultad.codigo",
                "alumno.persona.id",
                "alumno.persona.nombreCompleto",
                "alumno.persona.numeroDocIdentidad",
                "alumno.persona.tipoDocumento.id",
                "alumno.persona.tipoDocumento.simbolo",
                "supervisor.id",
                "supervisor.persona.id",
                "supervisor.persona.nombreCompleto",
                "supervisor.persona.numeroDocIdentidad",
                "supervisor.persona.tipoDocumento.id",
                "supervisor.persona.tipoDocumento.simbolo",
                "supervisor.cargo.nombre",
                "supervisor.oficina.nombre"
            });

            response.setData(node);
            response.setSuccess(Boolean.TRUE);
        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping(value = "alumnos/{id}/eliminar", method = RequestMethod.POST)
    public JsonResponse eliminarAlumno(@PathVariable Long id, HttpSession session, HttpServletRequest request) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
        JsonResponse response = new JsonResponse();
        String codeRequest = verificadorService.generateCodeRequest();

        try {
            Facultad facultad = this.getFacultad(request, ds, codeRequest);
            service.eliminarAlumno(new AlumnoBolsaInvestigacion(id), ds.getCicloAcademico(), facultad, ds);
            response.setMessage("Alumno eliminado");
            response.setSuccess(Boolean.TRUE);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping(value = "alumnos/{id}/checkear", method = RequestMethod.POST)
    public JsonResponse checkearAlumno(@PathVariable Long id, HttpSession session, HttpServletRequest request) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
        JsonResponse response = new JsonResponse();
        String codeRequest = verificadorService.generateCodeRequest();

        try {
            Facultad facultad = this.getFacultad(request, ds, codeRequest);
            List<String> errores = service.checkearAlumno(new Alumno(id), ds.getCicloAcademico(), facultad);
            ObjectNode erroresNode = new ObjectNode(JsonNodeFactory.instance);
            ArrayNode arr = new ArrayNode(JsonNodeFactory.instance);
            for (String error : errores) {
                arr.add(error);
            }
            erroresNode.set("errores", arr);
            response.setData(errores);
            response.setSuccess(Boolean.TRUE);
        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping(value = "enviarinvitaciones", method = RequestMethod.POST)
    public JsonResponse enviarInvitaciones(HttpSession session, HttpServletRequest request) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
        JsonResponse response = new JsonResponse();
        String codeRequest = verificadorService.generateCodeRequest();

        try {
            Facultad facultad = this.getFacultad(request, ds, codeRequest);
            service.enviarInvitaciones(facultad, ds.getCicloAcademico(), ds);
            response.setMessage("Invitaciones enviadas");
            response.setSuccess(Boolean.TRUE);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    private Facultad getFacultad(HttpServletRequest request, DataSessionPivot ds, String codeResquest) {
        List<Facultad> facultades = verificadorService.allInstanciasByMenuRol(TipoOficinaEnum.FAC, request, ds, codeResquest);
        if (facultades.size() == 1) {
            return facultades.get(0);
        }
        return null;
    }

    private List<Facultad> getFacultades(HttpServletRequest request, DataSessionPivot ds, String codeRequest) {
        List<Facultad> facultades = verificadorService.allInstanciasByMenuRol(TipoOficinaEnum.FAC, request, ds, codeRequest);
        if (facultades.size() == 1) {
            return facultades;
        }
        return new ArrayList();
    }
}
