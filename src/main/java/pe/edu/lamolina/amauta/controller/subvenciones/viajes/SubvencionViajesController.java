package pe.edu.lamolina.amauta.controller.subvenciones.viajes;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.List;
import javax.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableResponse;
import pe.albatross.zelpers.json.JaneHelper;
import pe.albatross.zelpers.miscelanea.ExceptionHandler;
import pe.albatross.zelpers.miscelanea.JsonResponse;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.edu.lamolina.amauta.config.DespliegueConfig;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.Curso;
import pe.edu.lamolina.model.academico.DepartamentoAcademico;
import pe.edu.lamolina.model.academico.Docente;
import pe.edu.lamolina.model.academico.Seccion;
import pe.edu.lamolina.model.bienestar.ViajeCurso;
import pe.edu.lamolina.model.constantines.GlobalConstantine;

@Slf4j
@Controller
@AllArgsConstructor(onConstructor = @__(
        @Autowired))
@RequestMapping("subvenciones/viajes")
public class SubvencionViajesController {

    private final SubvencionViajesService service;
    private final DespliegueConfig despliegueConfig;

    private final String rutaModulo = this.getClass().getAnnotation(RequestMapping.class).value()[0];

    @RequestMapping(method = RequestMethod.GET)
    public String index(Model model, HttpSession session) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
        Docente docenteUser = ds.getDocente();

        model.addAttribute("isDocenteUser", docenteUser != null);
        model.addAttribute("isProduccion", despliegueConfig.isProduccion());
        model.addAttribute("ciclo", ds.getCicloAcademico());
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
            Docente docenteUser = ds.getDocente();

            List<ViajeCurso> viajes = service.allDynatbleByDocente(docenteUser, dptos, ds.getCicloAcademico(), filter);

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
            List<Curso> cursos = service.allCursos(ds.getDocente(), ds.getCicloAcademico(), ds);

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
            List<Seccion> secciones = service.allSecciones(curso, ds.getDocente(), ds.getCicloAcademico(), ds);

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
            if (viajeCurso.getId() == null) {
                service.saveViaje(viajeCurso, ds.getCicloAcademico(), ds);
            } else {
                service.updateViaje(viajeCurso, ds.getCicloAcademico(), ds);
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

}
