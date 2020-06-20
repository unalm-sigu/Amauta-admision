package pe.edu.lamolina.amauta.controller.escalafon;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableResponse;
import pe.albatross.zelpers.json.JaneHelper;
import pe.albatross.zelpers.miscelanea.ExceptionHandler;
import pe.albatross.zelpers.miscelanea.JsonHelper;
import pe.albatross.zelpers.miscelanea.JsonResponse;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;
import pe.edu.lamolina.model.constantines.GlobalConstantine;
import pe.edu.lamolina.model.enums.NivelIdiomaEnum;
import pe.edu.lamolina.model.escalafon.AreaInvestigacion;
import pe.edu.lamolina.model.escalafon.Escalafon;
import pe.edu.lamolina.model.escalafon.EscalafonConfirmBean;
import pe.edu.lamolina.model.escalafon.enums.GradoEscalafonEnum;
import pe.edu.lamolina.model.escalafon.enums.TipoDocenteEscaEnum;
import pe.edu.lamolina.model.escalafon.enums.TipoProduccionEscaEnum;
import pe.edu.lamolina.model.general.Idioma;

@Controller
@RequestMapping("escalafon")
public class EscalafonController {

    @Autowired
    EscalafonService service;

    @RequestMapping(method = RequestMethod.GET)
    public String index() {
        return "escalafon/lista";
    }

    @ResponseBody
    @RequestMapping("list")
    public DynatableResponse list(DynatableFilter filter, HttpSession session, HttpServletRequest request) {
        DynatableResponse json = new DynatableResponse();
        try {
            JsonNodeFactory jFactory = JsonNodeFactory.instance;
            ArrayNode array = new ArrayNode(jFactory);
            List<Escalafon> listEscalafon = service.allDynatable(filter);
            for (Escalafon item : listEscalafon) {
                array.add(JaneHelper.from(item).join("persona").json());
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
    public JsonResponse save(@RequestBody Escalafon escalafon, HttpSession session) {
        JsonResponse response = new JsonResponse();
        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
            Escalafon escalafonBD = service.save(escalafon, ds.getUsuario());
            response.setData(JsonHelper.createJson(escalafonBD, JsonNodeFactory.instance, new String[]{"*"}));
            response.setMessage("El registro fue creado satisfactoriamente");
            response.setSuccess(true);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("updateGeneral")
    public JsonResponse updateGeneral(@RequestBody Escalafon escalafon, HttpSession session) {
        JsonResponse response = new JsonResponse();
        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
            service.updateGeneral(escalafon, ds.getUsuario());
            Escalafon escalafonBD = service.findEscalafon(escalafon);
            response.setData(JsonHelper.createJson(escalafonBD, JsonNodeFactory.instance, new String[]{"*", "persona.id", "persona.apellidosNombres", "paisNacimiento.*"}));
            response.setMessage("El registro fue actualizado satisfactoriamente");
            response.setSuccess(true);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("eliminar")
    public JsonResponse eliminar(@RequestBody Escalafon escalafon, HttpSession session) {
        JsonResponse response = new JsonResponse();
        try {
            service.eliminar(escalafon);
            response.setMessage("El registro fue eliminado satisfactoriamente");
            response.setSuccess(true);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("confirmarEscalafon")
    public JsonResponse confirmarEscalafon(@RequestBody EscalafonConfirmBean escalafonConfirmBean, HttpSession session) {
        JsonResponse response = new JsonResponse();
        try {
            service.confirmarEscalafon(escalafonConfirmBean);
            response.setMessage("El registro fue confirmado satisfactoriamente");
            response.setSuccess(true);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @RequestMapping("update/{idEscalafon}")
    public String editor(@PathVariable Long idEscalafon, Model model) {
        Escalafon escalafon = service.loadEscalafon(idEscalafon);
        model.addAttribute("escalafon", this.createEscalafonJson(escalafon).toString());
        model.addAttribute("listNivelesEnum", this.createNivelesJson().toString());
        model.addAttribute("listGradoEscalafonEnum", this.createGradoJson().toString());
        model.addAttribute("listTipoDocenteEnum", this.createTipoDocenteJson().toString());
        model.addAttribute("listTipoProduccionEnum", this.createTipoProduccionJson().toString());
        model.addAttribute("listIdioma", this.createIdiomasJson(service.allIdioma()).toString());
        model.addAttribute("listAreaInvestigacion", this.createListAreaInvestigacionJson(service.allAreaInvestigacion()).toString());
        return "escalafon/editorEscalafon";
    }

    @RequestMapping("info/{idEscalafon}")
    public String info(@PathVariable Long idEscalafon, Model model) {
        Escalafon escalafon = service.loadEscalafon(idEscalafon);
        model.addAttribute("escalafon", this.createEscalafonJson(escalafon).toString());
        return "escalafon/infoEscalafon";
    }

    private ObjectNode createEscalafonJson(Escalafon escalafon) {
        return JsonHelper.createJson(escalafon, JsonNodeFactory.instance, new String[]{
            "*",
            "persona.id", "persona.apellidosNombres", "persona.apellidos", "persona.nombres", "persona.celular",
            "paisNacimiento.*",
            "idiomaEscalafon.*", "idiomaEscalafon.idioma.*", "idiomaEscalafon.escalafon.id",
            "distincionEscalafon.*", "distincionEscalafon.pais.*", "distincionEscalafon.escalafon.id",
            "academicoEscalafon.*", "academicoEscalafon.universidad.*", "academicoEscalafon.pais.*", "academicoEscalafon.escalafon.id",
            "experienciaEscalafon.*", "experienciaEscalafon.universidad.*", "experienciaEscalafon.escalafon.id",
            "experienciaAsesor.*", "experienciaAsesor.universidad.*", "experienciaAsesor.escalafon.id",
            "investigacionEscalafon.*", "investigacionEscalafon.area.*", "investigacionEscalafon.escalafon.id",
            "produccionEscalafon.*", "produccionEscalafon.escalafon.id"
        });
    }

    private ArrayNode createNivelesJson() {
        ArrayNode array = new ArrayNode(JsonNodeFactory.instance);
        for (NivelIdiomaEnum nivel : NivelIdiomaEnum.values()) {
            array.add(nivel.getValue());
        }
        return array;
    }

    private ArrayNode createTipoProduccionJson() {
        ArrayNode array = new ArrayNode(JsonNodeFactory.instance);
        for (TipoProduccionEscaEnum nivel : TipoProduccionEscaEnum.values()) {
            array.add(nivel.name());
        }
        return array;
    }

    private ArrayNode createGradoJson() {
        ArrayNode array = new ArrayNode(JsonNodeFactory.instance);
        for (GradoEscalafonEnum grado : GradoEscalafonEnum.values()) {
            ObjectNode node = new ObjectNode(JsonNodeFactory.instance);
            node.put("name", grado.name());
            node.put("descripcion", grado.getDescripcion());
            array.add(node);
        }
        return array;
    }

    private ArrayNode createTipoDocenteJson() {
        ArrayNode array = new ArrayNode(JsonNodeFactory.instance);
        for (TipoDocenteEscaEnum tipoDocente : TipoDocenteEscaEnum.values()) {
            ObjectNode node = new ObjectNode(JsonNodeFactory.instance);
            node.put("name", tipoDocente.name());
            node.put("descripcion", tipoDocente.getDescripcion());
            array.add(node);
        }
        return array;
    }

    private ArrayNode createIdiomasJson(List<Idioma> idiomas) {
        ArrayNode array = new ArrayNode(JsonNodeFactory.instance);
        for (Idioma idioma : idiomas) {
            array.add(JsonHelper.createJson(idioma, JsonNodeFactory.instance, new String[]{"id", "nombre"}));
        }
        return array;
    }

    private ArrayNode createListAreaInvestigacionJson(List<AreaInvestigacion> listAreaInvestigacion) {
        ArrayNode array = new ArrayNode(JsonNodeFactory.instance);
        for (AreaInvestigacion item : listAreaInvestigacion) {
            array.add(JsonHelper.createJson(item, JsonNodeFactory.instance, new String[]{"*"}));
        }
        return array;
    }

}
