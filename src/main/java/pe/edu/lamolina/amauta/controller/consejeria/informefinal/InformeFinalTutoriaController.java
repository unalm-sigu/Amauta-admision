package pe.edu.lamolina.amauta.controller.consejeria.informefinal;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.List;
import javax.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import pe.albatross.zelpers.json.JaneHelper;
import pe.albatross.zelpers.miscelanea.JsonResponse;
import pe.edu.lamolina.amauta.controller.consejeria.plantutoria.PlanTutoriaService;
import pe.edu.lamolina.amauta.controller.seguridad.verificador.VerificadorService;
import pe.edu.lamolina.model.constantines.GlobalConstantine;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.consejeria.Consejero;
import pe.edu.lamolina.model.tutoria.InformeFinalTutoria;
import pe.edu.lamolina.model.tutoria.ItemInformeFinalTutoria;

@Slf4j
@Controller
@AllArgsConstructor(onConstructor = @__(
        @Autowired))
@RequestMapping("consejeria/aconsejadostutor")
public class InformeFinalTutoriaController {

    public final String rutaModulo = this.getClass().getAnnotation(RequestMapping.class).value()[0];

    private final InformeFinalTutoriaService service;
    private final PlanTutoriaService planTutoriaService;
    private final VerificadorService verificadorService;

    @RequestMapping("{idConsejero}/informefinal")
    public String informefinal(
            @PathVariable("idConsejero") Long idConsejero,
            @RequestParam("origen") String origen,
            Model model, HttpSession session) {

        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
        CicloAcademico ciclo = ds.getCicloAcademico();
        Consejero consejero = service.findConsejero(new Consejero(idConsejero));

        model.addAttribute("consejeroJson", this.createConsejeroJson(consejero));
        model.addAttribute("cicloJson", this.createCicloJson(ciclo));
        model.addAttribute("tienePermiso", service.tienePermiso(consejero, ciclo, ds));
        model.addAttribute("esConsejero", service.verificarConsejero(ciclo, ds));
        model.addAttribute("rutaModulo", rutaModulo);
        model.addAttribute("origen", verificadorService.getOrigen(origen, "/consejeria/aconsejadostutor"));

        return "consejeria/informefinal/informeFinal";
    }

    @ResponseBody
    @RequestMapping("{idConsejero}/findInforme")
    public JsonResponse findInforme(@PathVariable("idConsejero") Long idConsejero, HttpSession session) {

        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
        InformeFinalTutoria informe = service.findInforme(new Consejero(idConsejero), ds.getCicloAcademico(), ds);
        ObjectNode informeJson = this.createInformeJson(informe);

        JsonResponse json = new JsonResponse();
        json.setSuccess(Boolean.TRUE);
        json.setData(informeJson);

        return json;
    }

    @ResponseBody
    @RequestMapping("calcularCantidadesInforme")
    public JsonResponse calcularCantidadesInforme(@RequestBody InformeFinalTutoria informe, HttpSession session) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
        service.calcularCantidadesInforme(informe, ds.getCicloAcademico(), ds);

        JsonResponse json = new JsonResponse();
        json.setMessage("Se calculó las cantidades de actividades de la tutoría");
        json.setSuccess(Boolean.TRUE);

        return json;
    }

    @ResponseBody
    @RequestMapping("dificultadesInforme")
    public JsonResponse dificultadeesInforme(@RequestBody InformeFinalTutoria informe, HttpSession session) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
        service.dificultadesInforme(informe, ds.getCicloAcademico(), ds);

        JsonResponse json = new JsonResponse();
        json.setMessage("Se calculó las cantidades de actividades de la tutoría");
        json.setSuccess(Boolean.TRUE);

        return json;
    }

    @ResponseBody
    @RequestMapping("sugerenciasInforme")
    public JsonResponse sugerenciasInforme(@RequestBody InformeFinalTutoria informe, HttpSession session) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
        service.sugerenciasInforme(informe, ds.getCicloAcademico(), ds);

        JsonResponse json = new JsonResponse();
        json.setMessage("Se calculó las cantidades de actividades de la tutoría");
        json.setSuccess(Boolean.TRUE);

        return json;
    }

    @ResponseBody
    @RequestMapping("conclusionesInforme")
    public JsonResponse conclusionesInforme(@RequestBody InformeFinalTutoria informe, HttpSession session) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
        service.conclusionesInforme(informe, ds.getCicloAcademico(), ds);

        JsonResponse json = new JsonResponse();
        json.setMessage("Se calculó las cantidades de actividades de la tutoría");
        json.setSuccess(Boolean.TRUE);

        return json;
    }

    @ResponseBody
    @RequestMapping("enviarInforme")
    public JsonResponse enviarInforme(@RequestBody InformeFinalTutoria informe, HttpSession session) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
        service.enviarInforme(informe, ds.getCicloAcademico(), ds);

        JsonResponse json = new JsonResponse();
        json.setMessage("Se envió el Informe a la Coordinación de Consejería");
        json.setSuccess(Boolean.TRUE);

        return json;
    }

    private ObjectNode createConsejeroJson(Consejero consejero) {
        return JaneHelper
                .from(consejero)
                .only("id,estado,fechaInicio,fechaFin")
                .join("carrera", "codigo,nombre")
                .join("carrera.facultad", "codigo,nombre")
                .join("colaborador", "id")
                .join("colaborador.persona", "apellidosNombres,numeroDocIdentidad,tipoFoto,rutaFoto")
                .join("colaborador.persona.tipoDocumento", "simbolo")
                .json();
    }

    private ObjectNode createInformeJson(InformeFinalTutoria informe) {
        ArrayNode itemsJson = this.createItemsInformeJson(informe.getItemsInforme());

        ObjectNode node = JaneHelper
                .from(informe)
                .only("id,estado,estadoEnum,serie,numero,fecha,fechaEmision,dificultades,sugerencias,conclusiones,comentarioInforme")
                .join("cicloAcademico", "id")
                .join("carrera", "id")
                .join("consejero", "id")
                .join("tipoDocumento", "id")
                .json();

        node.set("itemsInforme", itemsJson);
        return node;
    }

    private ArrayNode createItemsInformeJson(List<ItemInformeFinalTutoria> items) {
        return JaneHelper
                .from(items)
                .only("id,cantidad,orden")
                .join("parteInformeTutoria", "id,nombre")
                .array();
    }

    private ObjectNode createCicloJson(CicloAcademico ciclo) {
        return JaneHelper
                .from(ciclo)
                .only("id,descripcion,descripcion2")
                .json();
    }

}
