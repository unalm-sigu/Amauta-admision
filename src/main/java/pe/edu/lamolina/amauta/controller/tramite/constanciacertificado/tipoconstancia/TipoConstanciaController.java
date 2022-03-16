package pe.edu.lamolina.amauta.controller.tramite.constanciacertificado.tipoconstancia;

import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.List;
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
import pe.edu.lamolina.model.enums.TipoConstanciaEnum;
import pe.edu.lamolina.model.tramite.TipoDocumentoAcademico;
import pe.edu.lamolina.model.constantines.GlobalConstantine;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;
import pe.edu.lamolina.model.constantines.GlobalMessages;

@Controller
@RequestMapping("tramite/tipoconstancia")
public class TipoConstanciaController {

    @Autowired
    TipoConstanciaService service;

    @RequestMapping(method = RequestMethod.GET)
    public String index(Model model) {
        model.addAttribute("tipos", TipoConstanciaEnum.getJsonValues());
        model.addAttribute("oficinas", JaneHelper
                .from(service.allOficinaEmisora())
                .only("id,nombre")
                .array());
        return "tramite/tipoConstancia/tipoConstancia";
    }

    @ResponseBody
    @RequestMapping("all")
    public DynatableResponse all(DynatableFilter filter, HttpSession session) {

        DynatableResponse json = new DynatableResponse();
        json.setTotal(0);

        List<TipoDocumentoAcademico> tiposDocumentos = service.allDynatable(filter);

        json.setData(JaneHelper.from(tiposDocumentos)
                .join("oficinaEmisora","id,nombre")
                .array());

        json.setTotal(filter.getTotal());
        json.setFiltered(filter.getFiltered());

        return json;

    }

    @ResponseBody
    @RequestMapping("save")
    public String save(@RequestBody TipoDocumentoAcademico tramiteDocumentoAcademico, HttpSession session) {

        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);

        if (tramiteDocumentoAcademico.getId() == null) {

            service.save(tramiteDocumentoAcademico, ds.getUsuario());
            return GlobalMessages.CREATED;

        } else {

            service.update(tramiteDocumentoAcademico, ds.getUsuario());
            return GlobalMessages.UPDATED;
        }

    }

    @ResponseBody
    @RequestMapping("delete")
    public String delete(TipoDocumentoAcademico tipoDocumento, HttpSession session) {

        service.delete(tipoDocumento);
        return GlobalMessages.DELETED;
    }

    @ResponseBody
    @RequestMapping("find/{idTipoDocumentoAcademico}")
    public ObjectNode find(@PathVariable Long idTipoDocumentoAcademico) {

        TipoDocumentoAcademico tipoDocumentoAcademico = service.findTipoDocumentoAcademico(new TipoDocumentoAcademico(idTipoDocumentoAcademico));

        ObjectNode jTipoDocumento = JaneHelper.from(tipoDocumentoAcademico)
                .join("oficinaEmisora").json();

        return jTipoDocumento;
    }

}
