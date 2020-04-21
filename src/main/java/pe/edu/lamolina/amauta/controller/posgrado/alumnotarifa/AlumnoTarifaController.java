package pe.edu.lamolina.amauta.controller.posgrado.alumnotarifa;

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
import pe.edu.lamolina.model.posgrado.AlumnoTarifa;
import pe.edu.lamolina.model.posgrado.TarifaCarrera;
import pe.edu.lamolina.model.constantines.AcademicoConstantine;
import pe.edu.lamolina.model.constantines.GlobalConstantine;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;

@Controller
@RequestMapping("posgrado/alumnotarifa")
public class AlumnoTarifaController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    AlumnoTarifaService service;

    @RequestMapping(method = RequestMethod.GET)
    public String index(Model model, HttpSession session) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
        return "posgrado/alumnotarifa/alumnotarifa";
    }

    @ResponseBody
    @RequestMapping("list")
    public DynatableResponse list(DynatableFilter filter, HttpSession session, HttpServletRequest request) {
        DynatableResponse json = new DynatableResponse();
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);

        try {
            List<AlumnoTarifa> alumnotarifa = service.allAlumnoTarifa(filter);
            ArrayNode array = new ArrayNode(JsonNodeFactory.instance);

            for (AlumnoTarifa alumntarifa : alumnotarifa) {
                ObjectNode node = JsonHelper.createJson(alumntarifa, JsonNodeFactory.instance, true,
                        new String[]{
                            "id", "estado", "estadoEnum", "fechaAceptaTarifa",
                            "fechaActivacion", "fechaRegistro",
                            "tarifaCarrera.nombre",
                            "tarifaCarrera.ambitoEnum",
                            "tarifaCarrera.carrera.tipoEnum",
                            "tarifaCarrera.carrera.nombre",
                            "tarifaCarrera.cicloInicio.descripcion",
                            "tarifaCarrera.cicloInicio.descripcion2",
                            "alumno.id",
                            "alumno.codigo",
                            "alumno.carrera.tipoEnum",
                            "alumno.carrera.nombre",
                            "alumno.persona.apellidosNombres",
                            "alumno.persona.rutaFoto",
                            "alumno.persona.tipoFoto"

                        });

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
    public JsonResponse save(@RequestBody AlumnoTarifa alumnotarifa, HttpSession session) {

        JsonNodeFactory jsonFactory = JsonNodeFactory.instance;
        JsonResponse response = new JsonResponse();

        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);

            service.save(alumnotarifa, ds);

            response.setSuccess(true);
            response.setMessage("Guardado satisfactoriamnente");

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("allOtrasTarifas")
    public JsonResponse allOtrasTarifas(@RequestBody Alumno alumno, HttpSession session) {

        JsonNodeFactory jsonFactory = JsonNodeFactory.instance;
        JsonResponse response = new JsonResponse();

        try {
            List<TarifaCarrera> tarifas = service.allOtrasTarifas(alumno);

            ArrayNode arrayTarifas = new ArrayNode(jsonFactory);
            for (TarifaCarrera tarifa : tarifas) {
                ObjectNode json = createTarifaJson(tarifa);
                arrayTarifas.add(json);
            }

            response.setData(arrayTarifas);
            response.setSuccess(true);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    private ObjectNode createTarifaJson(TarifaCarrera tarifa) {
        ObjectNode json = JsonHelper.createJson(tarifa, JsonNodeFactory.instance, true, new String[]{
            "id", "nombre"
        });
        return json;
    }

}
