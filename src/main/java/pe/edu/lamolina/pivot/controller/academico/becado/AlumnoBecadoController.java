package pe.edu.lamolina.pivot.controller.academico.becado;

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
import org.springframework.web.bind.annotation.SessionAttributes;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableResponse;
import pe.albatross.zelpers.miscelanea.ExceptionHandler;
import pe.albatross.zelpers.miscelanea.JsonResponse;
import pe.albatross.zelpers.miscelanea.ObjectUtil;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.AlumnoBecado;
import pe.edu.lamolina.model.academico.Carrera;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.general.Persona;
import pe.edu.lamolina.model.general.TipoDocIdentidad;
import pe.edu.lamolina.model.seguridad.Usuario;
import pe.edu.lamolina.pivot.controller.academico.visitante.AlumnoHelper;
import pe.edu.lamolina.pivot.zelper.constant.Constantine;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

@Controller
@SessionAttributes("alumnoBecado")
@RequestMapping("academico/becado/alumno")
public class AlumnoBecadoController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    AlumnoBecadoService service;

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
        CicloAcademico cicloAcademico = ds.getCicloAcademico();

        List<TipoDocIdentidad> tiposDocIdentidad = service.allTiposDocIdentidad();
        List<CicloAcademico> ciclos = service.allCicloAcademico();

        model.addAttribute("cicloAcademico", cicloAcademico);
        model.addAttribute("tiposDocIdentidad", tiposDocIdentidad);
        model.addAttribute("ciclos", ciclos);
        model.addAttribute("helper", new AlumnoHelper());

        return "academico/becado/alumnobecado";

    }

    @ResponseBody
    @RequestMapping("list")
    public DynatableResponse list(DynatableFilter filter, HttpSession session) {

        DynatableResponse json = new DynatableResponse();

        try {

            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
            CicloAcademico cicloAcademico = ds.getCicloAcademico();
            logger.debug("cicloAcademico {} {}", cicloAcademico.getId(), cicloAcademico.getDescripcion());
            List<AlumnoBecado> becados = service.allAlumnoBecado(filter, cicloAcademico);
            JsonNodeFactory jsonFactory = JsonNodeFactory.instance;
            ArrayNode array = new ArrayNode(jsonFactory);

            for (AlumnoBecado becado : becados) {
                ObjectNode node = new ObjectNode(jsonFactory);

                Alumno alumno = becado.getAlumno();
                Persona persona = alumno.getPersona();
                Carrera carrera = alumno.getCarrera();
                CicloAcademico ciclo = becado.getCicloBeca();

                node.put("id", becado.getId());
                node.put("nombre", persona.getNombreCompleto());
                node.put("codigo", alumno.getCodigo());
                node.put("tipoDoc", (String) ObjectUtil.getParentTree(persona, "tipoDocumento.simbolo"));
                node.put("nroDocumento", persona.getNumeroDocIdentidad());
                node.put("carrera", carrera.getNombre());
                node.put("codigoCarrera", carrera.getCodigo());
                node.put("facultad", carrera.getFacultad().getNombre());
                node.put("codigoFacultad", carrera.getFacultad().getCodigo());
                node.put("monto", becado.getMonto());
                node.put("facultadDestino", becado.getFacultadDestino());
                node.put("nombreUniversidadDestino", becado.getNombreUniversidadDestino());
                node.put("universidadDestino", (String) ObjectUtil.getParentTree(becado, "universidadDestino.nombre"));
                node.put("ciclo", ciclo.getDescripcion());
                node.put("estado", becado.getEstado());
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
    @RequestMapping("save")
    public JsonResponse save(AlumnoBecado alumnoBecado, HttpSession session) {
        JsonResponse response = new JsonResponse();
        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
            Usuario user = ds.getUsuario();
            if (alumnoBecado.getId() == null) {
                service.save(alumnoBecado, user);
                response.setMessage("Alumno becado agregado satisfactoriamente");
            } else {
                service.update(alumnoBecado, user);
                response.setMessage("Alumno becado  actualizado satisfactoriamente");
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
    @RequestMapping("delete")
    public JsonResponse delete(AlumnoBecado alumnoBecado) {
        JsonResponse response = new JsonResponse();
        try {
            service.delete(alumnoBecado);
            response.setMessage("Alumno becado eliminado satisfactoriamente");
            response.setSuccess(Boolean.TRUE);
        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("update")
    public JsonResponse update(AlumnoBecado alumnoBecado, HttpSession session) {

        JsonResponse response = new JsonResponse();
        try {

            AlumnoBecado becado = service.find(alumnoBecado);
            ObjectNode json = becado.toJson();
            response.setData(json);
            response.setSuccess(true);

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

        JsonResponse response = new JsonResponse();

        try {

            JsonNodeFactory jsonFactory = JsonNodeFactory.instance;
            List<Alumno> alumnos = service.allAlumnoByName(nombre);
            ArrayNode jsonList = new ArrayNode(jsonFactory);

            for (Alumno alumno : alumnos) {

                Persona persona = alumno.getPersona();

                ObjectNode json = new ObjectNode(jsonFactory);
                json.put("id", alumno.getId());
                json.put("nombre", alumno.getPersona().getNombreCompleto());
                json.put("codigo", alumno.getCodigo());
                json.put("carrera", alumno.getCarrera().getNombre());
                json.put("codigoCarrera", alumno.getCarrera().getCodigo());
                json.put("facultad", alumno.getCarrera().getFacultad().getNombre());
                json.put("codigoFacultad", alumno.getCarrera().getFacultad().getCodigo());
                json.put("tipoDoc", (String) ObjectUtil.getParentTree(persona, "tipoDocumento.simbolo"));
                json.put("nroDocumento", alumno.getPersona().getNumeroDocIdentidad());
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
