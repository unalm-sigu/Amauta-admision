package pe.edu.lamolina.amauta.controller.matricula.tutorsolicitud;

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
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.consejeria.TutorSolicitud;
import pe.edu.lamolina.model.constantines.GlobalConstantine;
import pe.edu.lamolina.model.seguridad.Usuario;

@Controller
@RequestMapping("academico/tutorsolicitud")
public class TutorSolicitudController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    TutorSolicitudService service;

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
        CicloAcademico cicloAcademico = ds.getCicloAcademico();

        model.addAttribute("ciclo", cicloAcademico);
        return "academico/tutorsolicitud/tutorsolicitud";
    }

    @ResponseBody
    @RequestMapping("list")
    public DynatableResponse list(DynatableFilter filter, HttpSession session) {
        DynatableResponse json = new DynatableResponse();

        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
            CicloAcademico cicloAcademico = ds.getCicloAcademico();
            JsonNodeFactory factory = JsonNodeFactory.instance;
            ArrayNode array = new ArrayNode(factory);

            List<TutorSolicitud> solicitudes = service.allTutorSolicitudByFilter(filter, cicloAcademico);

            for (TutorSolicitud solicitud : solicitudes) {
                ObjectNode node = JsonHelper.createJson(solicitud, factory, new String[]{
                    "id",
                    "tipoSolicitud",
                    "estado",
                    "estadoEnum",
                    "alumnoConsejero.alumno.codigo",
                    "alumnoConsejero.alumno.persona.apellidosNombres",
                    "alumnoConsejero.alumno.persona.tipoDocumento.*",
                    "alumnoConsejero.alumno.persona.numeroDocIdentidad",
                    "alumnoConsejero.consejero.colaborador.persona.apellidosNombres",
                    "alumnoConsejero.alumno.persona.rutaFoto",
                    "alumnoConsejero.alumno.carrera.*",
                    "alumnoConsejero.alumno.carrera.facultad.*",
                    "alumnoConsejero.alumno.modalidadEstudio.nombre"});
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
    @RequestMapping("updateEstado")
    public JsonResponse updateEstado(@RequestBody TutorSolicitud solicitud, HttpSession session) {

        JsonResponse response = new JsonResponse();
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
        Usuario usuario = ds.getUsuario();
        response.setSuccess(false);

        try {
            service.updateEstado(solicitud,usuario);
            response.setMessage("Se actualizó correctamente.");
            response.setSuccess(true);

        } catch (PhobosException e) {
            response.setSuccess(false);
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            response.setSuccess(false);
            ExceptionHandler.handleException(e, response);
        }
        return response;

    }
}
