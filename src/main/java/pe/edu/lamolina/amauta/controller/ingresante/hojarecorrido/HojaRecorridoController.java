package pe.edu.lamolina.amauta.controller.ingresante.hojarecorrido;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.List;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableResponse;
import pe.albatross.zelpers.miscelanea.ExceptionHandler;
import pe.albatross.zelpers.miscelanea.JsonHelper;
import pe.albatross.zelpers.miscelanea.JsonResponse;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.edu.lamolina.model.academico.RecorridoIngresante;
import pe.edu.lamolina.model.academico.TipoActividadIngresante;
import pe.edu.lamolina.model.constantines.AcademicoConstantine;
import pe.edu.lamolina.model.constantines.GlobalConstantine;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;

@Controller
@RequestMapping("ingresante/hojarecorrido")
public class HojaRecorridoController {

    @Autowired
    HojaRecorridoService service;

    @RequestMapping(method = RequestMethod.GET)
    public String postulante(Model model, HttpSession session) {

        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
        model.addAttribute("ciclo", ds.getCicloAcademico());
        return "ingresante/hojarecorrido/hojarecorrido";
    }

    @ResponseBody
    @RequestMapping("list")
    public DynatableResponse listIngresantes(DynatableFilter filter, HttpSession session) {
        DynatableResponse json = new DynatableResponse();
        try {

            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
            List<RecorridoIngresante> recoIngresantes = service.allRecorridoIngresante(filter, ds.getCicloAcademico());
            ArrayNode array = new ArrayNode(JsonNodeFactory.instance);

            for (RecorridoIngresante reco : recoIngresantes) {
                ObjectNode node = JsonHelper.createJson(reco, JsonNodeFactory.instance, true,
                        new String[]{
                            "*",
                            "turnoEntrevistaObuae.*",
                            "alumno.codigo",
                            "alumno.persona.tipoFoto",
                            "alumno.persona.rutaFoto",
                            "alumno.persona.apellidosNombres",
                            "alumno.persona.numeroDocIdentidad",
                            "alumno.persona.tipoDocumento.simbolo",
                            "alumno.carrera.nombre",
                            "actividadIngresante.*",
                            "actividadIngresante.tipoActividadIngresante.*"
                        });
                node.put("descripcion", "");
                node.put("ocultar", true);
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
    @RequestMapping("allTipoActividad")
    public JsonResponse allTipoActividad() {
        JsonResponse response = new JsonResponse();
        try {

            List<TipoActividadIngresante> actividades = service.allTipoActividadIngresante();

            JsonNodeFactory jsonFactory = JsonNodeFactory.instance;
            ArrayNode array = new ArrayNode(jsonFactory);
            for (TipoActividadIngresante actividad : actividades) {

                ObjectNode activNode = JsonHelper.createJson(actividad, JsonNodeFactory.instance, true,
                        new String[]{
                            "*"
                        });
                array.add(activNode);
            }

            response.setData(array);
            response.setTotal(array.size());
            response.setSuccess(true);
        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

}
