package pe.edu.lamolina.pivot.controller.docente.horario;

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
import pe.albatross.zelpers.miscelanea.JsonHelper;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.horario.Hora;
import pe.edu.lamolina.pivot.controller.academico.infoacademico.InfoAcademicoService;
import pe.edu.lamolina.pivot.zelper.constant.Constantine;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

@Controller
@RequestMapping("docente/horario")
public class HorarioDocenteController {

    @Autowired
    InfoAcademicoService infoAcademicoService;

    @RequestMapping(method = RequestMethod.GET)
    public String index(Model model, HttpSession session) {

        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);

        model.addAttribute("docente", ds.getDocente());
        model.addAttribute("ciclo", ds.getCicloAcademico());
        model.addAttribute("dptoAcad", ds.getDepartamentoAcademico());

        ArrayNode horasJson = new ArrayNode(JsonNodeFactory.instance);
        List<Hora> horas = infoAcademicoService.allHoras();
        for (Hora hora : horas) {
            horasJson.add(JsonHelper.createJson(hora, JsonNodeFactory.instance, true, new String[]{"*"}));
        }

        model.addAttribute("horasBD", horasJson.toString());

        ObjectNode jCiclo = JsonHelper.createJson(ds.getCicloAcademico(), JsonNodeFactory.instance, true, new String[]{"*"});
        ObjectNode jDocente = JsonHelper.createJson(ds.getDocente(), JsonNodeFactory.instance, true, new String[]{"*"});

        model.addAttribute("jCiclo", jCiclo.toString());
        model.addAttribute("jDocente", jDocente.toString());
        return "academico/docente/horario/horario";

    }

}
