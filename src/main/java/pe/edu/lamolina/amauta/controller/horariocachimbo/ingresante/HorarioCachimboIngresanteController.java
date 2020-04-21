package pe.edu.lamolina.amauta.controller.horariocachimbo.ingresante;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.beans.PropertyEditorSupport;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
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
import pe.albatross.zelpers.miscelanea.ObjectUtil;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.AlumnoHorario;
import pe.edu.lamolina.model.academico.Carrera;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Facultad;
import pe.edu.lamolina.model.academico.RecorridoIngresante;
import pe.edu.lamolina.model.enums.EstadoAlumnoHorarioEnum;
import pe.edu.lamolina.model.horario.HorarioCachimbos;
import pe.edu.lamolina.model.seguridad.Usuario;
import pe.edu.lamolina.model.constantines.AcademicoConstantine;
import pe.edu.lamolina.model.constantines.GlobalConstantine;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;

@Controller
@RequestMapping("academico/horariocachimbo/ingresante")
public class HorarioCachimboIngresanteController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    HorarioCachimboIngresanteService service;
    @Autowired
    VisorMatricula visorMatricula;

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
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
        model.addAttribute("cicloAcademico", ds.getCicloAcademico());
        model.addAttribute("estados", EstadoAlumnoHorarioEnum.values());
        return "academico/horariocachimbo/ingresante/horarioCachimboIngresante";
    }

    @ResponseBody
    @RequestMapping("list")
    public DynatableResponse list(DynatableFilter filter, HttpSession session) {
        DynatableResponse json = new DynatableResponse();
        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
            CicloAcademico cicloAcademico = ds.getCicloAcademico();
            List<AlumnoHorario> alumnosHorario = service.allAlumnoHorario(filter, cicloAcademico);
            List<RecorridoIngresante> recorridoIngresantes = service.allRecorridoIngresante(cicloAcademico);
            Map<Long, RecorridoIngresante> map = TypesUtil.convertListToMap("alumno.id", recorridoIngresantes);

            ArrayNode array = new ArrayNode(JsonNodeFactory.instance);
            for (AlumnoHorario alumHorario : alumnosHorario) {

                ObjectNode node = new ObjectNode(JsonNodeFactory.instance);
                Alumno alumno = alumHorario.getAlumno();
                Carrera carrera = alumno.getCarrera();
                Facultad facultad = carrera.getFacultad();
                HorarioCachimbos hc = alumHorario.getHorarioCachimbos();

                node.put("id", alumHorario.getId());
                node.put("estudiante", alumno.getPersona().getApellidosNombres());
                node.put("carrera", alumno.getCarrera().getNombre());
                node.put("facultad", alumno.getCarrera().getFacultad().getNombre());
                node.put("horario", hc != null ? hc.getCodigo() : "");
                node.put("numCurso", hc != null ? hc.getCursos() : 0);
                node.put("cursosMat", alumHorario.getCursosMatriculados().size());
                node.put("estado", alumHorario.getEstado());
                node.put("estadoName", EstadoAlumnoHorarioEnum.valueOf(alumHorario.getEstado()).getValue());

                node.put("codigoMatricula", alumno.getCodigo());
                node.put("tipo", alumno.getPersona().getTipoDocumento().getSimbolo());
                node.put("numero", alumno.getPersona().getNumeroDocIdentidad());
                node.put("errores", alumHorario.getErrores());
                RecorridoIngresante recorridoIngresante = map.get(alumno.getId());
                if (recorridoIngresante != null) {
                    node.put("actividadesEjecutadas", recorridoIngresante.getActividadesEjecutadas());
                    node.put("totalActividades", recorridoIngresante.getTotalActividades());

                }

                node.put("showfacultad", !facultad.getCodigo().equals(carrera.getCodigo()));
                node.put("horarioCachimbo", (Long) ObjectUtil.getParentTree(alumHorario, "horarioCachimbos.id"));

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
    @RequestMapping("addalumno")
    public JsonResponse addAlumno(Alumno alumno, HttpSession session) {
        JsonResponse response = new JsonResponse();
        try {

            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
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
    @RequestMapping("activarmatricula")
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
    @RequestMapping("suspendermatricula")
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
    @RequestMapping("asignarhorario")
    public JsonResponse asignarHorario(AlumnoHorario alumnoHorario, HttpSession session) {
        JsonResponse response = new JsonResponse();
        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
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
    @RequestMapping("retirarhorario")
    public JsonResponse retirarHorario(AlumnoHorario alumnoHorario, HttpSession session) {
        JsonResponse response = new JsonResponse();
        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
            Usuario user = ds.getUsuario();
            service.retirarHorario(alumnoHorario, ds);
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
    @RequestMapping("buscarhorario")
    public JsonResponse buscarHorario(Alumno alumno, HttpSession session) {
        JsonResponse response = new JsonResponse();
        try {

            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
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
    @RequestMapping("searchalumno")
    public JsonResponse searchAlumno(@RequestParam("nombre") String nombre, HttpSession session) {
        JsonNodeFactory jsonFactory = JsonNodeFactory.instance;
        JsonResponse response = new JsonResponse();
        try {

            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
            CicloAcademico cicloAcademico = ds.getCicloAcademico();
            List<Alumno> alumnos = service.allAlumnoIngresantePregradoByNameCiclo(nombre, cicloAcademico);
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

    @ResponseBody
    @RequestMapping("cargaringresantes")
    public JsonResponse cargarIngresantes(HttpSession session) {

        JsonResponse response = new JsonResponse();

        try {

            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
            CicloAcademico cicloAcademico = ds.getCicloAcademico();
            Usuario user = ds.getUsuario();
            service.cargarIngresantes(cicloAcademico, user);
            response.setMessage("Ingresantes cargado satisfactoriamente");
            response.setSuccess(true);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("eliminarhorarios")
    public JsonResponse eliminarHorarios(HttpSession session) {

        JsonResponse response = new JsonResponse();

        try {

            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
            CicloAcademico cicloAcademico = ds.getCicloAcademico();
            Usuario user = ds.getUsuario();
            service.eliminarHorarios(cicloAcademico, user);
            response.setMessage("Horarios de ingresantes eliminado satisfactoriamente");
            response.setSuccess(true);
        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("ingresantecantidad")
    public JsonResponse ingresanteCantidad(HttpSession session) {
        JsonResponse response = new JsonResponse();
        try {

            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
            CicloAcademico cicloAcademico = ds.getCicloAcademico();
            List<IngresanteCantidad> cantidad = service.allIngresanteCantidad(cicloAcademico);

            response.setData(cantidad);
            response.setSuccess(true);
        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("delete")
    public JsonResponse delete(AlumnoHorario alumnoHorario, HttpSession session) {
        JsonResponse response = new JsonResponse();
        response.setSuccess(false);
        try {

            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
            CicloAcademico cicloAcademico = ds.getCicloAcademico();
            service.deleteIngresante(alumnoHorario, cicloAcademico);
            response.setMessage("Registro eliminado");
            response.setSuccess(true);
        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("matricular")
    public JsonResponse matricular(HttpSession session) {
        JsonResponse response = new JsonResponse();
        try {

            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
            CicloAcademico cicloAcademico = ds.getCicloAcademico();
            boolean ok = visorMatricula.iniciar();
            response.setMessage(visorMatricula.getProcesoActual());
            if (ok) {
                service.matricular(cicloAcademico, ds);
            }
            response.setSuccess(ok);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("getAvanceMatricula")
    public JsonResponse getAvanceMatricula(HttpSession session) {
        JsonResponse response = new JsonResponse();
        try {

            ObjectNode node = new ObjectNode(JsonNodeFactory.instance);
            node.put("porcentaje", visorMatricula.getAvance());
            node.put("mensaje", visorMatricula.getProcesoActual());
            node.put("estado", visorMatricula.getEstado());
            node.put("procesando", visorMatricula.sigueProcesando());

            ArrayNode array = new ArrayNode(JsonNodeFactory.instance);
            List<String> errores = visorMatricula.getErrores();
            for (String error : errores) {
                ObjectNode nodeError = new ObjectNode(JsonNodeFactory.instance);
                nodeError.put("msg", error);
                array.add(nodeError);
            }

            node.set("errores", array);

            response.setData(node);
            response.setSuccess(visorMatricula.sigueProcesando());

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("revisarActividad")
    public JsonResponse revisarActividad(HttpSession session) {
        JsonResponse response = new JsonResponse();
        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
            service.revisarActividad(ds);
            response.setMessage("Se verificó las actividades del ingresante.");
            response.setSuccess(true);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

}
