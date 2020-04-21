package pe.edu.lamolina.pivot.controller.tramite.bolsainvestigacion;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
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
import pe.edu.lamolina.pivot.controller.seguridad.verificador.VerificadorService;
import pe.edu.lamolina.model.constantines.AcademicoConstantine;
import pe.edu.lamolina.model.constantines.GlobalConstantine;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

@Controller
@RequestMapping("tramite/bolsainvestigacion")
public class BolsaInvestigacionController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    BolsaInvestigacionService service;

    @Autowired
    VerificadorService verificadorService;

    @RequestMapping(method = RequestMethod.GET)
    public String index(Model model, HttpSession session) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
        model.addAttribute("ciclo", ds.getCicloAcademico().getDescripcion());
        return "tramite/bolsainvestigacion/bolsainvestigacion";
    }

    @ResponseBody
    @RequestMapping(value = "/find", method = RequestMethod.GET)
    public JsonResponse find(HttpSession session, HttpServletRequest request) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
        List<Facultad> facultades = verificadorService.allInstanciasByMenuRol(TipoOficinaEnum.FAC, request, ds);
        JsonResponse response = new JsonResponse();
        try {
            Facultad facultad = new Facultad(6L);
            BolsaInvestigacion bi = service.findByFacultadCicloAcademico(facultad, ds.getCicloAcademico());
            response.setData(JsonHelper.createJson(bi, JsonNodeFactory.instance));
            response.setSuccess(Boolean.TRUE);
        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }

        return response;
    }

    @ResponseBody
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public DynatableResponse list(DynatableFilter filter, HttpSession session) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
        Facultad facultad = new Facultad(6L);
        DynatableResponse json = new DynatableResponse();

        List<AlumnoBolsaInvestigacion> alumnos = service.allByDynatableFacultadCicloAcademico(filter, facultad, ds.getCicloAcademico());
        ArrayNode array = new ArrayNode(JsonNodeFactory.instance);

        for (AlumnoBolsaInvestigacion alumno : alumnos) {
            array.add(JsonHelper.createJson(alumno, JsonNodeFactory.instance, new String[]{
                "id",
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
                "supervisor.oficina.nombre",
                "nombreInvestigacion",
                "estado"
            }));
        }

        json.setData(array);
        json.setTotal(filter.getTotal());
        json.setFiltered(filter.getFiltered());

        return json;
    }

    @ResponseBody
    @RequestMapping(value = "/alumnos", method = RequestMethod.POST)
    public JsonResponse alumnos(@RequestBody String nombre, HttpSession session) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
        JsonResponse response = new JsonResponse();
        try {
            CicloAcademico ciclo = ds.getCicloAcademico();
            List<Facultad> facultades = ds.getFacultades();
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
//                node.put("id", alumno.getId());
//                node.put("nombre", (String) ObjectUtil.getParentTree(alumno, "persona.nombreCompleto"));
//                node.put("codigo", alumno.getCodigo());
//                node.set("persona", JsonHelper.createJson(alumno.getPersona(), JsonNodeFactory.instance, new String[]{"*"}));
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
    @RequestMapping(value = "/supervisores", method = RequestMethod.POST)
    public JsonResponse supervisores(@RequestBody String nombre, HttpSession session) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
        JsonResponse response = new JsonResponse();
        try {
            Facultad facultad = new Facultad(6L);
            List<Colaborador> colaboradores = service.searchColaboradoresByFacultadNombre(facultad, nombre);
            ArrayNode arr = new ArrayNode(JsonNodeFactory.instance);
            for (Colaborador colaborador : colaboradores) {
                ObjectNode node = JsonHelper.createJson(colaborador, JsonNodeFactory.instance, true,
                        new String[]{
                            "id",
                            "persona.id",
                            "persona.nombreCompleto",
                            "persona.numeroDocIdentidad",
                            "persona.tipoDocumento.id",
                            "persona.tipoDocumento.simbolo",
                            "cargo.nombre",
                            "oficina.nombre"
                        });
//                node.put("id", colaborador.getId());
//                node.put("nombre", (String) ObjectUtil.getParentTree(colaborador, "persona.nombreCompleto"));
//                node.put("codigo", colaborador.getCodigo());
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
    @RequestMapping(value = "/alumnos/save", method = RequestMethod.POST)
    public JsonResponse saveAlumno(@RequestBody AlumnoBolsaInvestigacion alumnoBolsa, HttpSession session) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
        JsonResponse response = new JsonResponse();
        try {
            Facultad facultad = new Facultad(6L);
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
    @RequestMapping(value = "/alumnos/{id}/find", method = RequestMethod.GET)
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

//            ObjectNode nodeAlumno = new ObjectNode(JsonNodeFactory.instance);
//            nodeAlumno.put("id", abi.getAlumno().getId());
//            nodeAlumno.put("nombre", (String) ObjectUtil.getParentTree(abi, "alumno.persona.nombreCompleto"));
//            nodeAlumno.put("codigo", (String) ObjectUtil.getParentTree(abi, "alumno.codigo"));
//            node.set("alumno", nodeAlumno);
//
//            if (abi.getSupervisor() != null) {
//                ObjectNode nodeSupervisor = new ObjectNode(JsonNodeFactory.instance);
//                nodeSupervisor.put("id", abi.getSupervisor().getId());
//                nodeSupervisor.put("nombre", (String) ObjectUtil.getParentTree(abi, "supervisor.persona.nombreCompleto"));
//                nodeSupervisor.put("codigo", (String) ObjectUtil.getParentTree(abi, "sueprvisor.codigo"));
//                node.set("supervisor", nodeSupervisor);
//            }
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
    @RequestMapping(value = "/alumnos/{id}/eliminar", method = RequestMethod.POST)
    public JsonResponse eliminarAlumno(@PathVariable Long id, HttpSession session) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
        JsonResponse response = new JsonResponse();
        try {
            Facultad facultad = new Facultad(6L);
            service.eliminarAlumno(id, ds.getCicloAcademico(), facultad);
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
    @RequestMapping(value = "/alumnos/{id}/checkear", method = RequestMethod.POST)
    public JsonResponse checkearAlumno(@PathVariable Long id, HttpSession session) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
        JsonResponse response = new JsonResponse();
        try {
            List<String> errores = service.checkearAlumno(new Alumno(id), ds.getCicloAcademico());
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
    @RequestMapping(value = "/enviarinvitaciones", method = RequestMethod.POST)
    public JsonResponse enviarInvitaciones(HttpSession session) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
        JsonResponse response = new JsonResponse();
        try {
            Facultad facultad = new Facultad(6L);
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
}
