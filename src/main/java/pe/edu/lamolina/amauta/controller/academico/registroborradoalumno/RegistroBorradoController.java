package pe.edu.lamolina.amauta.controller.academico.registroborradoalumno;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jfree.util.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableResponse;
import pe.albatross.zelpers.json.JaneHelper;
import pe.albatross.zelpers.miscelanea.*;
import pe.albatross.zelpers.miscelanea.ExceptionHandler;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;
import pe.edu.lamolina.model.academico.*;
import pe.edu.lamolina.model.constantines.GlobalConstantine;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.lang.reflect.Array;
import java.util.List;

@Slf4j
@Controller
@RequestMapping("academico/registroborradoalu")
@AllArgsConstructor(onConstructor = @__(
        @Autowired))
public class RegistroBorradoController {

    public final String rutaModulo = this.getClass().getAnnotation(RequestMapping.class).value()[0];


    private final RegistroBorradoService service;


    @RequestMapping(method = RequestMethod.GET)
    public String index(Model model, HttpSession session) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
        model.addAttribute("rutaModulo", rutaModulo);
        return "academico/registroborrado/registroborrado";
    }

    @ResponseBody
    @RequestMapping("list")
    public DynatableResponse allBorrados(DynatableFilter filter, HttpSession session, HttpServletRequest request) {
        DynatableResponse json = new DynatableResponse();
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);

        try {

            List<RegistroBorradoAlumno> borrados = service.allByDynatable(filter);
            log.debug("Número de registros borrados de alumnos: " + borrados.size());

            ArrayNode array = this.registrosBorradosAlumnos(borrados);

            json.setData(array);
            json.setTotal(filter.getTotal());
            json.setFiltered(filter.getFiltered());

            return json;
        } catch (Exception e) {
            e.printStackTrace();
            json.setTotal(0);
        }

        return json;
    }

    private ArrayNode registrosBorradosAlumnos(List<RegistroBorradoAlumno> borrados) {
        ArrayNode array = new ArrayNode(JsonNodeFactory.instance);
        for (RegistroBorradoAlumno borrado : borrados) {
            ObjectNode node = JaneHelper
                    .from(borrado)
                    .only("motivo")
                    .join("cicloAfectado", "id,descripcion")
                    .join("alumno", "id,codigo")
                    .join("alumno.situacionAcademica", "id,nombre")
                    .join("alumno.modalidadEstudio", "id,codigo,nombre")
                    .join("alumno.carrera", "id,codigo,nombre,tipo,estadoEnum")
                    .join("alumno.carrera.facultad", "id,codigo,nombre")
                    .join("alumno.persona", "id,apellidosNombres,numeroDocIdentidad,tipoFoto,rutaFoto")
                    .join("alumno.persona.tipoDocumento", "simbolo")
                    .join("alumno.cicloIngreso.cicloAcademico", "id,descripcion")
                    .join("userRegistra.persona","apellidosNombres")
                    .json();

            array.add(node);
        }
        return array;
    }

    @ResponseBody
    @RequestMapping(value = "alumnosByFilter")
    public JsonResponse alumnosByFilter(@RequestParam("filter") String filter, HttpSession session) {
        JsonResponse response = new JsonResponse();
        try {

            List<Alumno> alumnos = service.allActivoPregradoByNombre(filter);
            ArrayNode array = new ArrayNode(JsonNodeFactory.instance);

            for (Alumno alumno : alumnos) {
                ObjectNode node = JaneHelper.from(alumno)
                        .only("id,codigo")
                        .join("persona", "apellidosNombres,numeroDocIdentidad")
                        .join("persona.tipoDocumento", "simbolo,nombre")
                        .join("modalidadEstudio", "id,nombre")
                        .join("cicloIngreso", "id,descripcion")
                        .join("carrera", "codigo,nombre")
                        .join("carrera.facultad", "codigo,nombre")
                        .join("situacionAcademica", "nombre").json();
                array.add(node);
            }

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
    @RequestMapping("getCiclosEstudiados")
    public JsonResponse getCiclosEstudiados(@RequestBody(required = false) Alumno alumno) {
        JsonResponse response = new JsonResponse();
        response.setSuccess(Boolean.FALSE);
        try {
            if (alumno != null) {
                ArrayNode ciclos = service.allCiclosEstudiadosByAlumno(alumno);
                response.setData(ciclos);
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
    @RequestMapping("getInfoacademico")
    public JsonResponse getInfoacademico(@RequestBody InfoAlumno infoAlumno) {
        JsonResponse response = new JsonResponse();
        response.setSuccess(Boolean.FALSE);
        try {
            ObjectNode historialAlumnoJson = service.allHistorialByInfoAlumno(infoAlumno);
            response.setData(historialAlumnoJson);
            response.setSuccess(Boolean.TRUE);
        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;

    }

@ResponseBody
    @RequestMapping("getInfoacademicoEliminado")
    public JsonResponse getInfoacademicoEliminado(@RequestBody InfoAlumno infoAlumno) {
        JsonResponse response = new JsonResponse();
        response.setSuccess(Boolean.FALSE);
        try {
            ObjectNode historialEliminadoAlumnoJson = service.allHistorialEliminadoByInfoAlumno(infoAlumno);
            response.setData(historialEliminadoAlumnoJson);
            response.setSuccess(Boolean.TRUE);
        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;

    }

    @ResponseBody
    @RequestMapping("save")
    public JsonResponse save(@RequestBody RegistroBorradoAlumno registro, HttpSession session) {
        JsonResponse response = new JsonResponse();
        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
            service.save(registro, ds);
            response.setMessage("Se anulo el historial satisfactoriamente");
            response.setSuccess(true);
        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }

        return response;
    }
}
