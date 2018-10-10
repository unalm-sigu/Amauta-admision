package pe.edu.lamolina.pivot.controller.posgrado.alumnotarifa;

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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableResponse;
import pe.albatross.zelpers.miscelanea.JsonHelper;
import pe.edu.lamolina.model.posgrado.AlumnoTarifa;
import pe.edu.lamolina.pivot.zelper.constant.Constantine;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

@Controller
@RequestMapping("posgrado/alumnotarifa")
public class AlumnoTarifaController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    AlumnoTarifaService service;

    @RequestMapping(method = RequestMethod.GET)
    public String index(Model model, HttpSession session) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        return "posgrado/alumnotarifa/alumnotarifa";
    }

    @ResponseBody
    @RequestMapping("list")
    public DynatableResponse list(DynatableFilter filter, HttpSession session, HttpServletRequest request) {
        DynatableResponse json = new DynatableResponse();
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);

        try {
            List<AlumnoTarifa> alumnotarifa = service.allAlumnoTarifa(filter);
            ArrayNode array = new ArrayNode(JsonNodeFactory.instance);

            for (AlumnoTarifa alumntarifa : alumnotarifa) {
                ObjectNode node = JsonHelper.createJson(alumntarifa, JsonNodeFactory.instance, true,
                        new String[]{
                            "id",  "estado", "fechaAceptaTarifa",
                            "fechaActivacion", "fechaRegistro",
                            "tarifaCarrera.carrera.tipoEnum",
                            "tarifaCarrera.carrera.nombre",
                            "tarifaCarrera.cicloinicio.descripcion",
                            "tarifaCarrera.cicloinicio.descripcion2",
                            "alumno.codigo",
                            "alumno.carrera.tipoEnum",
                            "alumno.carrera.nombre",
                            "alumno.persona.apellidosNombres"
                            
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

}
