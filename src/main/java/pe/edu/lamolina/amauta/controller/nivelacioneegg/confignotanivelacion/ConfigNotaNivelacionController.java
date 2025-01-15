package pe.edu.lamolina.amauta.controller.nivelacioneegg.confignotanivelacion;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableResponse;
import pe.albatross.zelpers.json.JaneHelper;
import pe.albatross.zelpers.miscelanea.JsonResponse;
import pe.edu.lamolina.model.constantines.GlobalConstantine;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.nivelacioneegg.ModalidadTemaCiclo;

@Slf4j
@Controller
@AllArgsConstructor(onConstructor = @__(
        @Autowired))
@RequestMapping("nivelacioneegg/confignotanivelacion")
public class ConfigNotaNivelacionController {

    public final String rutaModulo = this.getClass().getAnnotation(RequestMapping.class).value()[0];

    private final ConfigNotaNivelacionService service;

    @RequestMapping(method = RequestMethod.GET)
    public String index(Model model, HttpSession session) {

        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
        CicloAcademico ciclo = ds.getCicloAcademico();
        service.revisarNotasExamen(ciclo, ds);
        service.revisarDatos(ciclo, ds);

        model.addAttribute("cicloJson", this.createCicloJson(ciclo));
        model.addAttribute("rutaModulo", rutaModulo);

        return "nivelacioneegg/confignotanivelacion/configNotaNivelacion";
    }

    @ResponseBody
    @RequestMapping("list")
    public DynatableResponse list(DynatableFilter filter, HttpSession session, HttpServletRequest request) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
        CicloAcademico ciclo = ds.getCicloAcademico();
        List<ModalidadTemaCiclo> configs = service.allConfiguracionsByDynatable(filter, ciclo);

        ArrayNode array = JaneHelper
                .from(configs)
                .join("modalidadIngreso", "id,codigo,nombre")
                .join("temaCiclo", "id,orden,preguntas")
                .join("temaCiclo", "puntajeMinimo,puntajeMaximo,notaMinima,notaMaxima")
                .join("temaCiclo", "puntajeCepreMinimo,puntajeCepreMaximo,notaCepreMinima,notaCepreMaxima")
                .join("temaExamen", "id,nombre")
                .join("cicloAcademico", "id,descripcion")
                .array();

        DynatableResponse json = new DynatableResponse();
        json.setData(array);
        json.setTotal(filter.getTotal());
        json.setFiltered(filter.getFiltered());
        return json;
    }

    @ResponseBody
    @RequestMapping("saveConfig")
    public JsonResponse saveConfig(@RequestBody ModalidadTemaCiclo configuracion, HttpSession session) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
        service.saveConfig(configuracion, ds);

        JsonResponse json = new JsonResponse();
        json.setMessage("Se guardó la nueva configuración satisfactoriamente");
        json.setSuccess(Boolean.TRUE);

        return json;
    }

    @ResponseBody
    @RequestMapping("activarTodos")
    public JsonResponse activarTodos(HttpSession session) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
        int cambios = service.activarTodos(ds.getCicloAcademico(), ds);

        JsonResponse json = new JsonResponse();
        json.setMessage("Se activó " + cambios + " configuraciones satisfactoriamente");
        json.setSuccess(Boolean.TRUE);

        return json;
    }

    @ResponseBody
    @RequestMapping("activar")
    public JsonResponse activar(@RequestBody ModalidadTemaCiclo config, HttpSession session) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
        service.activar(config, ds);

        JsonResponse json = new JsonResponse();
        json.setMessage("Se activó la configuración satisfactoriamente");
        json.setSuccess(Boolean.TRUE);

        return json;
    }

    @ResponseBody
    @RequestMapping("desactivar")
    public JsonResponse desactivar(@RequestBody ModalidadTemaCiclo config, HttpSession session) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
        service.desactivar(config, ds);

        JsonResponse json = new JsonResponse();
        json.setMessage("Se activó la configuración satisfactoriamente");
        json.setSuccess(Boolean.TRUE);

        return json;
    }

    private ObjectNode createCicloJson(CicloAcademico ciclo) {
        return JaneHelper
                .from(ciclo)
                .only("id,descripcion,descripcion2")
                .json();
    }

}
