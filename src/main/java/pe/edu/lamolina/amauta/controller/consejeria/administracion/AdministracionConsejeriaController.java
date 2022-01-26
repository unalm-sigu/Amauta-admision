package pe.edu.lamolina.amauta.controller.consejeria.administracion;

import com.fasterxml.jackson.databind.node.ArrayNode;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import lombok.extern.slf4j.Slf4j;
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
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.consejeria.ConsejeriaHistorial;
import pe.edu.lamolina.model.constantines.GlobalConstantine;
import pe.edu.lamolina.model.constantines.GlobalMessages;

@Slf4j
@Controller
@RequestMapping("consejeria/administracion")
public class AdministracionConsejeriaController {

    @Autowired
    AdministracionConsejeriaService service;

    @RequestMapping(method = RequestMethod.GET)
    public String index(Model model, HttpSession session) {
        return "consejeria/administracion/administracion";
    }

    @ResponseBody
    @RequestMapping("all")
    public DynatableResponse all(DynatableFilter filter, HttpSession session, HttpServletRequest request) {

        DynatableResponse json = new DynatableResponse();
        json.setTotal(0);

        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);

        List<ConsejeriaHistorial> consejeriaHistoriales = service.allConsejeriaHistorialByDynatable(filter, ds.getCicloAcademico());

        ArrayNode array = JaneHelper.from(consejeriaHistoriales).
                join("cicloAcademico", "id,descripcion").
                array();

        json.setData(array);
        json.setFiltered(filter.getFiltered());
        json.setTotal(filter.getTotal());
        return json;
    }

    @ResponseBody
    @RequestMapping("ciclo/all")
    public ArrayNode allCiclo() {
        List<CicloAcademico> cicloAcademicos = service.allCiclo();
        return JaneHelper.from(cicloAcademicos)
                .only("id,descripcion,codigo").array();
    }

    @ResponseBody
    @RequestMapping("clonar")
    public String clonar(@RequestBody @Valid ClonarConsejerosDTO clonarDTO, HttpSession session) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
        service.clonar(clonarDTO, ds);
        return GlobalMessages.UPDATED;
    }

    @ResponseBody
    @RequestMapping("eliminar/{idConsejeriaHistorial}")
    public String cloneliminarar(@PathVariable  Long idConsejeriaHistorial, HttpSession session) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
        service.eliminar(idConsejeriaHistorial, ds);
        return GlobalMessages.DELETED;
    }

}
