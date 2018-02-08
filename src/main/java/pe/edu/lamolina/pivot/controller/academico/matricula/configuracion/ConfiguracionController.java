package pe.edu.lamolina.pivot.controller.academico.matricula.configuracion;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.beans.PropertyEditorSupport;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import pe.albatross.zelpers.miscelanea.ExceptionHandler;
import pe.albatross.zelpers.miscelanea.JsonResponse;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.edu.lamolina.model.academico.ConfiguracionTurnosAtencion;
import pe.edu.lamolina.model.general.Persona;
import pe.edu.lamolina.pivot.zelper.constant.Constantine;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

@Controller
@RequestMapping("academico/matricula")
public class ConfiguracionController {

    @Autowired
    ConfiguracionMatriculaService configuracionMatriculaService;

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

    @RequestMapping("configuracion")
    public String index(Model model, HttpSession session) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        model.addAttribute("ciclo", ds.getCicloAcademico());
        return "academico/matricula/matriculaConfiguracion";
    }

    @ResponseBody
    @RequestMapping(value = "configuracion/{idConfiguracion}", method = RequestMethod.GET)
    public JsonResponse finConfiguracion(@PathVariable("idConfiguracion") Long idConfiguracion, Model model, HttpSession session) {
        JsonResponse response = new JsonResponse();

        ConfiguracionTurnosAtencion configuracionTurnosAtencion = new ConfiguracionTurnosAtencion();
        configuracionTurnosAtencion.setId(idConfiguracion);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");

        ConfiguracionTurnosAtencion atencion = configuracionMatriculaService.findConfiguracion(configuracionTurnosAtencion);
        ObjectNode objNode = new ObjectNode(JsonNodeFactory.instance);
        objNode.put("alumnos", atencion.getAlumnos());
        objNode.put("duracion", atencion.getDuracion());
        objNode.put("espera", atencion.getEspera());
        objNode.put("tipo", atencion.getTipo());
        objNode.put("turnosDia", atencion.getTurnosDia());

        String fechafin = sdf.format(atencion.getFechaFin());
        objNode.put("fechaFin", fechafin);
        String fechaInicio = sdf.format(atencion.getFechaFin());
        objNode.put("fechaInicio", fechaInicio);
        String horaInicio = sdf.format(atencion.getHoraInicio());
        objNode.put("horaInicio", horaInicio);
        objNode.put("envento", atencion.getEventoCicloAcademico().getEventoAcademico().getId());

        response.setData(objNode);
        return response;
    }


    @ResponseBody
    @RequestMapping(value = "configuracion", method = RequestMethod.POST )
    public JsonResponse saveConfiguracion(@RequestBody ConfiguracionTurnosAtencion config) {
        JsonResponse response = new JsonResponse();
        System.out.println("Alumno: ----> " + config.getAlumnos());
        try {
            configuracionMatriculaService.saveConfiguracion(config);
            response.setSuccess(true);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);

        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }
}
