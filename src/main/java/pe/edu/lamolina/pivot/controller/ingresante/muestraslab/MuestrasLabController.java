package pe.edu.lamolina.pivot.controller.ingresante.muestraslab;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.List;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableResponse;
import pe.albatross.zelpers.miscelanea.JsonHelper;
import pe.albatross.zelpers.miscelanea.JsonResponse;
import pe.edu.lamolina.model.academico.RecorridoIngresante;
import pe.edu.lamolina.model.inscripcion.TurnoEntrevistaObuae;
import pe.edu.lamolina.model.medico.HistoriaLaboratorio;
import pe.edu.lamolina.pivot.zelper.constant.Constantine;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

@Controller
@RequestMapping("ingresante/muestraslab")
public class MuestrasLabController {

    @Autowired
    MuestrasLabService service;

    @RequestMapping(method = RequestMethod.GET)
    public String postulante(Model model, HttpSession session) {

        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        model.addAttribute("ciclo", ds.getCicloAcademico());
        return "ingresante/muestraslab/muestraslab";
    }

    @ResponseBody
    @RequestMapping("list/{idTurno}")
    public DynatableResponse listIngresantes(@PathVariable Long idTurno,DynatableFilter filter, HttpSession session) {
        DynatableResponse json = new DynatableResponse();
        try {

            TurnoEntrevistaObuae turno = new TurnoEntrevistaObuae(idTurno);

            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);

            List<RecorridoIngresante> lista = service.laboratorioDynatableTurno(filter, turno, ds.getCicloAcademico());
            ArrayNode array = new ArrayNode(JsonNodeFactory.instance);

            for (RecorridoIngresante reco : lista) {
                HistoriaLaboratorio lab = service.findLaboratorioByRecorridoIngresante(reco);
                if (lab == null) {
                    lab = new HistoriaLaboratorio();
                    //lab.setRecorridoIngresante(reco);
                }
                reco.setLaboratorio(lab);

                ObjectNode node = JsonHelper.createJson(reco, JsonNodeFactory.instance, true,
                        new String[]{
                            "*",
                            "alumno.*",
                            "alumno.carrera.nombre",
                            "alumno.persona.*",
                            "alumno.persona.tipoDocumento.simbolo",
                            "turnoEntrevistaObuae.*",
                            "laboratorio.*"
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
    @RequestMapping("turnos")
    public JsonResponse turnos(HttpSession session) {
        JsonResponse json = new JsonResponse();
        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
            List<TurnoEntrevistaObuae> turnos = service.allTurnos(ds.getCicloAcademico());
            ArrayNode array = new ArrayNode(JsonNodeFactory.instance);

            for (TurnoEntrevistaObuae elem : turnos) {

                ObjectNode node = JsonHelper.createJson(elem, JsonNodeFactory.instance, true,
                        new String[]{
                            "*",
                        });
                array.add(node);
            }

            json.setData(array);
            json.setSuccess(Boolean.TRUE);

        } catch (Exception e) {
            e.printStackTrace();
            json.setTotal(0);
        }
        return json;
    }

}
