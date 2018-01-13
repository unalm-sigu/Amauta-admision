package pe.edu.lamolina.pivot.controller.academico.visitante;

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
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableResponse;
import pe.albatross.zelpers.miscelanea.ExceptionHandler;
import pe.albatross.zelpers.miscelanea.JsonResponse;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.AlumnoVisitante;
import pe.edu.lamolina.model.academico.Carrera;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.general.Persona;
import pe.edu.lamolina.model.general.TipoDocIdentidad;
import pe.edu.lamolina.pivot.zelper.constant.Constantine;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

@Controller
@SessionAttributes("alumnoVisitante")
@RequestMapping("academico/visitante/alumno")
public class AlumnosVisitanteController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    AlumnosVisitanteService service;

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

        AlumnoVisitante alumnoVisitante = new AlumnoVisitante();
        alumnoVisitante.setPersona(new Persona());
        List<TipoDocIdentidad> tiposDocIdentidad = service.allTiposDocIdentidad();
        List<CicloAcademico> ciclos = service.allCicloAcademico();

        model.addAttribute("alumnoVisitante", alumnoVisitante);
        model.addAttribute("tiposDocIdentidad", tiposDocIdentidad);
        model.addAttribute("ciclos", ciclos);
        model.addAttribute("helper", new AlumnoHelper());

        return "academico/visitante/alumnovisitante";

    }

    @ResponseBody
    @RequestMapping("list")
    public DynatableResponse list(DynatableFilter filter, HttpSession session) {

        DynatableResponse json = new DynatableResponse();

        try {

            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
            CicloAcademico cicloAcademico = ds.getCicloAcademico();
            logger.debug("cicloAcademico {} {}", cicloAcademico.getId(), cicloAcademico.getDescripcion());

            List<AlumnoVisitante> visitantes = service.allAlumnoVisitante(filter);
            Map<Long, Alumno> alumnoBecadoMap = service.allAlumnoByVisitante(visitantes);

            JsonNodeFactory jsonFactory = JsonNodeFactory.instance;
            ArrayNode array = new ArrayNode(jsonFactory);

            for (AlumnoVisitante visitante : visitantes) {

                ObjectNode node = new ObjectNode(jsonFactory);
                Alumno alumno = alumnoBecadoMap.get(visitante.getId());
                Persona persona = alumno.getPersona();
                TipoDocIdentidad tipoDoc = persona.getTipoDocumento();
                Carrera carrera = alumno.getCarrera();
                CicloAcademico ciclo = visitante.getCicloEstudia();

                node.put("id", visitante.getId());
                node.put("nombre", persona.getNombreCompleto());
                node.put("numeroMatricula", alumno.getCodigo());

                node.put("codigo", tipoDoc.getCodigo());
                node.put("documento", persona.getNumeroDocIdentidad());

                node.put("carrera", carrera.getNombre());
                node.put("facultad", carrera.getFacultad().getNombre());
                node.put("universidadExtranjera", visitante.getUniversidadExtranjera());
                node.put("ciclo", ciclo.getDescripcion());
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

    @RequestMapping("nuevo")
    public String nuevo(Model model, HttpSession session) {

        AlumnoVisitante alumnoVisitante = new AlumnoVisitante();
        alumnoVisitante.setPersona(new Persona());
        List<TipoDocIdentidad> tiposDocIdentidad = service.allTiposDocIdentidad();
        List<CicloAcademico> ciclos = service.allCicloAcademico();

        model.addAttribute("alumnoVisitante", alumnoVisitante);
        model.addAttribute("tiposDocIdentidad", tiposDocIdentidad);
        model.addAttribute("ciclos", ciclos);
        model.addAttribute("helper", new AlumnoHelper());

        return "academico/visitante/alumnovisitanteform";

    }

    @RequestMapping("update")
    public String update(Model model, HttpSession session) {

        AlumnoVisitante alumnoVisitante = new AlumnoVisitante();
        alumnoVisitante.setPersona(new Persona());
        List<TipoDocIdentidad> tiposDocIdentidad = service.allTiposDocIdentidad();
        List<CicloAcademico> ciclos = service.allCicloAcademico();

        model.addAttribute("alumnoVisitante", alumnoVisitante);
        model.addAttribute("tiposDocIdentidad", tiposDocIdentidad);
        model.addAttribute("ciclos", ciclos);
        model.addAttribute("helper", new AlumnoHelper());

        return "academico/visitante/alumnovisitanteform";

    }

    @ResponseBody
    @RequestMapping("save")
    public JsonResponse save(@ModelAttribute("alumnoVisitante") AlumnoVisitante alumnoVisitante, HttpSession session) {

        JsonResponse response = new JsonResponse();

        try {

            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
            service.save(alumnoVisitante, ds);
            response.setMessage("Alumno Visitante guardado satisfactoriamente");
            response.setSuccess(Boolean.TRUE);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }

        return response;
    }
}
