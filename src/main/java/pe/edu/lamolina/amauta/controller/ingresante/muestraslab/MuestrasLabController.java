package pe.edu.lamolina.amauta.controller.ingresante.muestraslab;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.List;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableResponse;
import pe.albatross.zelpers.miscelanea.ExceptionHandler;
import pe.albatross.zelpers.miscelanea.JsonHelper;
import pe.albatross.zelpers.miscelanea.JsonResponse;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.edu.lamolina.model.academico.ActividadIngresante;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.RecorridoIngresante;
import pe.edu.lamolina.model.constantines.GlobalMessages;
import pe.edu.lamolina.model.inscripcion.TurnoEntrevistaObuae;
import pe.edu.lamolina.model.medico.HistoriaLaboratorio;
import pe.edu.lamolina.amauta.controller.reporte.view.AtendidosMuestraLabView;
import pe.edu.lamolina.amauta.controller.reporte.view.IngresanteMuestraLabView;
import pe.edu.lamolina.amauta.controller.reporte.view.IngresanteTurnoMuestraLabView;
import pe.edu.lamolina.model.constantines.AcademicoConstantine;
import pe.edu.lamolina.model.constantines.GlobalConstantine;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;

@Controller
@RequestMapping("ingresante/muestraslab")
public class MuestrasLabController {

    @Autowired
    MuestrasLabService service;

    @Autowired
    VisorMuestrasLab visorMuestrasLab;

    @Autowired
    IngresanteTurnoMuestraLabView ingresanteTurnoMuestraLabView;

    @Autowired
    IngresanteMuestraLabView ingresanteMuestraLabView;

    @Autowired
    AtendidosMuestraLabView atendidosMuestraLabView;

    @RequestMapping(method = RequestMethod.GET)
    public String index(Model model, HttpSession session) {

        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);

        ObjectNode jsonLab = new ObjectNode(JsonNodeFactory.instance);
        jsonLab.put("numero", visorMuestrasLab.getNumeroLab());
        jsonLab.put("ciclo", visorMuestrasLab.getCicloAcademico().getDescripcion());

        model.addAttribute("laboratorioActual", jsonLab);
        return "ingresante/muestraslab/muestraslab";
    }

    @ResponseBody
    @RequestMapping("list/todos")
    public DynatableResponse listTodos(DynatableFilter filter, HttpSession session) {
        DynatableResponse json = new DynatableResponse();
        try {

            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
            CicloAcademico ciclo = service.findCicloActivoAdmision();

            List<RecorridoIngresante> recorridos = service.allRecorridosByDynatable(filter, ciclo, ds);
            ArrayNode array = createRecorridoJson(recorridos);

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
    @RequestMapping("list/turno/{idTurno}")
    public DynatableResponse listTurno(@PathVariable Long idTurno, DynatableFilter filter, HttpSession session) {
        DynatableResponse json = new DynatableResponse();
        try {

            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
            TurnoEntrevistaObuae turno = new TurnoEntrevistaObuae(idTurno);
            CicloAcademico ciclo = service.findCicloActivoAdmision();

            List<RecorridoIngresante> recorridos = service.allRecorridosByDynatableTurno(filter, turno, ciclo, ds);
            ArrayNode array = createRecorridoJson(recorridos);

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
    @RequestMapping("list/atendidos/{idTurno}")
    public DynatableResponse listAtendidos(@PathVariable Long idTurno, DynatableFilter filter, HttpSession session) {
        DynatableResponse json = new DynatableResponse();
        try {

            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
            TurnoEntrevistaObuae turno = service.findTurno(idTurno);
            CicloAcademico ciclo = service.findCicloActivoAdmision();

            List<RecorridoIngresante> recorridos = service.allAtendidosByDynatableTurno(filter, turno, ciclo, ds);
            ArrayNode array = createRecorridoJson(recorridos);

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
            CicloAcademico ciclo = service.findCicloActivoAdmision();
            List<TurnoEntrevistaObuae> turnos = service.allTurnos(ciclo);
            ArrayNode array = createTurnosJson(turnos);

            json.setData(array);
            json.setSuccess(Boolean.TRUE);

        } catch (Exception e) {
            e.printStackTrace();
            json.setTotal(0);
        }
        return json;
    }

    @ResponseBody
    @RequestMapping("saveLaboratorio")
    public JsonResponse saveLaboratorio(@RequestBody HistoriaLaboratorio laboratorio, HttpSession session) {

        JsonResponse response = new JsonResponse();
        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
            service.saveLaboratorio(laboratorio, ds);
            ObjectNode json = JsonHelper.createJson(laboratorio, JsonNodeFactory.instance, new String[]{"*"});

            response.setData(json);
            response.setMessage(GlobalMessages.CREATED);
            response.setSuccess(true);
        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("borrarLaboratorio")
    public JsonResponse borrarLaboratorio(@RequestBody HistoriaLaboratorio laboratorio, HttpSession session) {

        JsonResponse response = new JsonResponse();
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
        try {

            service.deleteLaboratorio(laboratorio);
            ObjectNode json = JsonHelper.createJson(laboratorio, JsonNodeFactory.instance, new String[]{"*"});

            response.setData(json);
            response.setMessage(GlobalMessages.DELETED);
            response.setSuccess(true);
        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @RequestMapping("listaExcel")
    public ModelAndView listaExcel(Model model, HttpSession session) {

        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
        CicloAcademico ciclo = service.findCicloActivoAdmision();
        List<RecorridoIngresante> ingresantes = service.allIngresantesConTurno(ciclo);

        InputStream formato = this.getClass().getResourceAsStream("/templates/excel/formatoIngresanteMuestraLab.xlsx");

        model.addAttribute("formato", formato);
        model.addAttribute("ingresantes", ingresantes);
        model.addAttribute("ciclo", ciclo);

        return new ModelAndView(ingresanteMuestraLabView);
    }

    @RequestMapping("listaExcelTurno")
    public ModelAndView listaExcelTurno(@RequestParam("turno") Long idTurno, Model model, HttpSession session) {

        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
        TurnoEntrevistaObuae turno = service.findTurno(idTurno);
        CicloAcademico ciclo = service.findCicloActivoAdmision();
        List<RecorridoIngresante> ingresantes = service.allIngresantesConTurno(turno, ciclo);

        InputStream formato = this.getClass().getResourceAsStream("/templates/excel/formatoIngresanteTurnoMuestraLab.xlsx");

        model.addAttribute("formato", formato);
        model.addAttribute("ingresantes", ingresantes);
        model.addAttribute("ciclo", ciclo);
        model.addAttribute("turno", turno);

        return new ModelAndView(ingresanteTurnoMuestraLabView);
    }

    @RequestMapping("listaExcelAtendidos")
    public ModelAndView listaExcelAtendidos(@RequestParam("turno") Long idTurno, Model model, HttpSession session) {

        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
        TurnoEntrevistaObuae turno = service.findTurno(idTurno);
        CicloAcademico ciclo = service.findCicloActivoAdmision();
        List<RecorridoIngresante> ingresantes = service.allAtendidos(turno, ciclo);

        InputStream formato = this.getClass().getResourceAsStream("/templates/excel/formatoAtendidosMuestraLab.xlsx");

        model.addAttribute("formato", formato);
        model.addAttribute("ingresantes", ingresantes);
        model.addAttribute("ciclo", ciclo);
        model.addAttribute("turno", turno);

        return new ModelAndView(atendidosMuestraLabView);
    }

    private ArrayNode createTurnosJson(List<TurnoEntrevistaObuae> turnos) {
        ArrayNode array = new ArrayNode(JsonNodeFactory.instance);

        for (TurnoEntrevistaObuae elem : turnos) {
            ObjectNode node = JsonHelper.createJson(elem, JsonNodeFactory.instance, true, new String[]{"*"});
            array.add(node);
        }
        return array;
    }

    private ArrayNode createRecorridoJson(List<RecorridoIngresante> recorridos) {
        ArrayNode array = new ArrayNode(JsonNodeFactory.instance);
        for (RecorridoIngresante reco : recorridos) {
            ObjectNode node = JsonHelper.createJson(reco, JsonNodeFactory.instance, true,
                    new String[]{
                        "*",
                        "alumno.*",
                        "alumno.carrera.nombre",
                        "alumno.persona.*",
                        "alumno.persona.tipoDocumento.simbolo",
                        "turnoEntrevistaObuae.*",
                        "laboratorio.id",
                        "laboratorio.numeroMuestra",
                        "laboratorio.fechaMuestra",
                        "laboratorio.idRecorridoIngresante",
                        "laboratorio.historiaClinica.id",
                        "tieneRiesgo",
                        "actividadIngresante.estado",
                        "actividadIngresante.tipoActividadIngresante",
                        "actividadIngresante.tipoActividadIngresante.tipoEXAMED",
                        "actividadIngresante.estadoAct"
                    });
//            ArrayNode jActividadIngresante = new ArrayNode(JsonNodeFactory.instance);
//            for (ActividadIngresante actividadIngresante : reco.getActividadIngresante()) {
//                ObjectNode actIngresante = JsonHelper.createJson(actividadIngresante, JsonNodeFactory.instance, true,
//                        new String[]{
//                            "estado",
//                            "tipoActividadIngresante.tipoEXAMED",
//                            "estadoAct"
//                        });
//            }

            ActividadIngresante actividadIngRECEP = reco.getActividadIngresante().stream().filter(x -> x.getTipoActividadIngresante().isTipoRECEP()).findFirst().orElse(null);
            node.put("tieneActividadRECEP", (actividadIngRECEP != null && actividadIngRECEP.isEstadoAct()));
            array.add(node);
        }
        return array;
    }

}
