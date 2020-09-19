package pe.edu.lamolina.amauta.controller.tramite.tramiteBachiller;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.List;
import javax.servlet.http.HttpSession;
import org.apache.commons.lang3.ArrayUtils;
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
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;
import pe.edu.lamolina.model.constantines.GlobalConstantine;
import pe.edu.lamolina.model.tramite.TramiteBachiller;

@Controller
@RequestMapping("academico/tramiteacademico/tramitebachiller")
public class TramitesBachillerController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    TramitesBachillerService tramitesBachillerService;

    @RequestMapping(method = RequestMethod.GET)
    public String index(Model model, HttpSession session) {

        return "academico/tramitescademicos/tramitebachiller/tramitesbachiller";
    }

    @ResponseBody
    @RequestMapping("list")
    public DynatableResponse listTramites(DynatableFilter filter,
            HttpSession session) {
        DynatableResponse json = new DynatableResponse();
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
        try {
            ArrayNode array = new ArrayNode(JsonNodeFactory.instance);
            List<TramiteBachiller> tramitesBachiller = tramitesBachillerService.allTramitesByFilter(filter, ds);

            String[] mapperTramite = new String[]{
                "*",
                "tramite.*",
                "tramite.persona.*",
                "tramite.alumno.*",
                "tramite.alumno.planCurricular.*",
                "tramite.alumno.carrera.*",
                "tramite.alumno.carrera.facultad.*",
                "tramite.compania.*",
                "tramite.cicloAcademico.*",
                "tramite.tipoTramite.codigo",
                "tramite.tipoTramite.nombre",
                "tramite.tipoTramite.esReincorporacionPregrado",
                "tramite.tipoTramite.esCursoDirigido",
                "tramite.tipoTramite.oficina.*",
                "tramite.userRegistro.*",
                "tramite.userRegistro.persona.*",
                "tramite.userRespuesta.*",
                "tramite.formularioEstadoTramite.*"
            };

            String[] mapperEstadoTramite = new String[]{
                "tramite.estadoTramite.nombre",
                "tramite.estadoTramite.id",
                "tramite.estadoTramite.nombre"
            };

            String[] mapperTramiteComplex = (String[]) ArrayUtils.addAll(mapperTramite, mapperEstadoTramite);

            JsonNodeFactory jc = JsonNodeFactory.instance;
            for (TramiteBachiller tramite : tramitesBachiller) {
                ObjectNode tramiteJson = JsonHelper.createJson(tramite, jc, false, mapperTramiteComplex);

                array.add(tramiteJson);
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
