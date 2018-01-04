package pe.edu.lamolina.pivot.controller.academico.horariocachimbo.ingresante;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.beans.PropertyEditorSupport;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableResponse;
import pe.albatross.zelpers.miscelanea.ExceptionHandler;
import pe.albatross.zelpers.miscelanea.JsonResponse;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.AlumnoHorario;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.enums.EstadoAlumnoHorarioEnum;
import pe.edu.lamolina.model.horario.HorarioCachimbos;
import pe.edu.lamolina.pivot.zelper.constant.Constantine;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

@Controller
@RequestMapping("academico/horariocachimbo/ingresante")
public class HorarioIngresanteController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    HorarioIngresanteService service;

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
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        model.addAttribute("cicloAcademico", ds.getCicloAcademico());
        return "academico/horariocachimbo/ingresante/horarioingresante";
    }

    @ResponseBody
    @RequestMapping("list")
    public DynatableResponse list(DynatableFilter filter, HttpSession session) {
        DynatableResponse json = new DynatableResponse();
        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
            CicloAcademico cicloAcademico = ds.getCicloAcademico();
            List<AlumnoHorario> alumnosHorario = service.allAlumnoHorario(filter, cicloAcademico);
            ArrayNode array = new ArrayNode(JsonNodeFactory.instance);

            for (AlumnoHorario alumHorario : alumnosHorario) {

                ObjectNode node = new ObjectNode(JsonNodeFactory.instance);
                Alumno alumno = alumHorario.getAlumno();
                HorarioCachimbos hc = alumHorario.getHorarioCachimbos();

                node.put("id", alumHorario.getId());
                node.put("estudiante", alumno.getPersona().getApellidosNombres());
                node.put("carrera", alumno.getCarrera().getNombre());
                node.put("facultad", alumno.getCarrera().getFacultad().getNombre());
                node.put("horario", hc != null ? hc.getCodigo() : "");
                node.put("numCurso", hc != null ? hc.getCursos() : 0);
                node.put("estado", alumHorario.getEstado());
                node.put("estadoName", EstadoAlumnoHorarioEnum.valueOf(alumHorario.getEstado()).getValue());

                node.put("codigoMatricula", alumno.getCodigo());
                node.put("tipo", alumno.getPersona().getTipoDocumento().getSimbolo());
                node.put("numero", alumno.getPersona().getNumeroDocIdentidad());

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
    @RequestMapping("addAlumno")
    public JsonResponse addAlumno(Alumno alumno, HttpSession session) {
        JsonResponse response = new JsonResponse();
        try {

            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
            CicloAcademico cicloAcademico = ds.getCicloAcademico();
            service.addAlumno(alumno, cicloAcademico);
            response.setMessage("Alumno agregado satisfactoriamente");
            response.setSuccess(Boolean.TRUE);
        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("activarMatricula")
    public JsonResponse activarMatricula(AlumnoHorario alumnoHorario) {
        JsonResponse response = new JsonResponse();
        try {
            service.activarMatricula(alumnoHorario);
            response.setMessage("Matrícula activada satisfactoriamente");
            response.setSuccess(Boolean.TRUE);
        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("suspenderMatricula")
    public JsonResponse suspenderMatricula(AlumnoHorario alumnoHorario) {
        JsonResponse response = new JsonResponse();
        try {
            service.suspenderMatricula(alumnoHorario);
            response.setMessage("Matrícula suspendida satisfactoriamente");
            response.setSuccess(Boolean.TRUE);
        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("asignarHorario")
    public JsonResponse asignarHorario(AlumnoHorario alumnoHorario, HttpSession session) {
        JsonResponse response = new JsonResponse();
        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
            service.asignarHorario(alumnoHorario, ds);
            response.setMessage("Horario asignado satisfactoriamente");
            response.setSuccess(Boolean.TRUE);
        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("retirarHorario")
    public JsonResponse retirarHorario(AlumnoHorario alumnoHorario) {
        JsonResponse response = new JsonResponse();
        try {
            service.retirarHorario(alumnoHorario);
            response.setMessage("Horario retirado satisfactoriamente");
            response.setSuccess(Boolean.TRUE);
        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("buscarHorario")
    public JsonResponse buscarHorario(Alumno alumno, HttpSession session) {
        JsonResponse response = new JsonResponse();
        try {

            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
            CicloAcademico cicloAcademico = ds.getCicloAcademico();
            service.buscarHorario(alumno, cicloAcademico);
            response.setMessage("Alumno creado satisfactoriamente");
            response.setSuccess(Boolean.TRUE);
        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("searchAlumno")
    public JsonResponse searchAlumno(@RequestParam("nombre") String nombre, HttpSession session) {
        JsonNodeFactory jsonFactory = JsonNodeFactory.instance;
        JsonResponse response = new JsonResponse();
        try {

            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
            CicloAcademico cicloAcademico = ds.getCicloAcademico();
            List<Alumno> alumnos = service.allAlumnoByName(nombre);
            ArrayNode jsonList = new ArrayNode(jsonFactory);

            for (Alumno alumno : alumnos) {

                ObjectNode json = new ObjectNode(jsonFactory);
                json.put("id", alumno.getId());
                json.put("nombre", alumno.getPersona().getNombreCompleto());
                json.put("codigoMatricula", alumno.getCodigo());
                json.put("carrera", alumno.getCarrera().getNombre());
                json.put("facultad", alumno.getCarrera().getFacultad().getNombre());
                json.put("tipo", alumno.getPersona().getTipoDocumento().getSimbolo());
                json.put("numero", alumno.getPersona().getNumeroDocIdentidad());
                jsonList.add(json);
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

}
