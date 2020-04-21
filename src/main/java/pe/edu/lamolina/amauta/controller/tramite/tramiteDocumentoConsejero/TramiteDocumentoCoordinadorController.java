package pe.edu.lamolina.amauta.controller.tramite.tramiteDocumentoConsejero;

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
import org.springframework.web.bind.annotation.ResponseBody;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableResponse;
import pe.albatross.zelpers.miscelanea.JsonHelper;
import pe.edu.lamolina.model.tramite.AccionTramiteDocumento;
import pe.edu.lamolina.model.tramite.TramiteDocumentoAcademico;
import pe.edu.lamolina.amauta.controller.tramite.constanciaSolicitud.ConstanciaSolicitudService;
import pe.edu.lamolina.model.constantines.AcademicoConstantine;
import pe.edu.lamolina.model.constantines.GlobalConstantine;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;

@Controller
@RequestMapping("tramite/documentocoordinador")
public class TramiteDocumentoCoordinadorController {

    @Autowired
    TramiteDocumentoCoordinadorService service;

    @Autowired
    ConstanciaSolicitudService serviceSolicitud;

    @RequestMapping(method = RequestMethod.GET)
    public String index(Model model, HttpSession session) {
        return "tramite/tramiteConstancia/documentoconsejero";
    }

    @ResponseBody
    @RequestMapping("list")
    public DynatableResponse allByDynatable(DynatableFilter filter, HttpSession session) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
        DynatableResponse json = new DynatableResponse();
        try {
            List<TramiteDocumentoAcademico> tipos = service.allTramiteDocumentoAcademico(filter, ds);

            ArrayNode array = new ArrayNode(JsonNodeFactory.instance);
            String[] mapperTramite = new String[]{
                "*",
                "persona.*",
                "alumno.*",
                "alumno.carrera.*",
                "alumno.carrera.facultad.*",
                "alumno.persona.*",
                "alumno.persona.tipoDocumento.*",
                "compania.*",
                "cicloAcademico.*",
                "tipoTramite.codigo",
                "tipoTramite.nombre",
                "tipoTramite.esTipoTramiteRei",
                "tipoTramite.esTipoTramiteCurDir",
                "tipoTramite.oficina.*",
                "userRegistro.*",
                "userRegistro.persona.*",
                "userRespuesta.*",
                "formularioEstadoTramite.*"
            };

            for (TramiteDocumentoAcademico tramiteDoc : tipos) {

                ObjectNode node = JsonHelper.createJson(tramiteDoc, JsonNodeFactory.instance, new String[]{
                    "*",
                    "idioma.*",
                    "estadoTramite.*",
                    "tipoDocumentoAcademico.*"});

                List<AccionTramiteDocumento> acciones = serviceSolicitud.findEstadoByEstadoInicio(tramiteDoc.getTipoDocumentoAcademico(), tramiteDoc.getEstadoTramite());
                ArrayNode arrayAcciones = new ArrayNode(JsonNodeFactory.instance);
                for (AccionTramiteDocumento accion : acciones) {
                    if (!accion.getEstadoTramiteFinal().getCodigo().equals("PAG")) {
                        arrayAcciones.add(JsonHelper.createJson(accion, JsonNodeFactory.instance, new String[]{
                            "*",
                            "estadoTramiteFinal.*"}));
                    }
                }
                ObjectNode tramiteJson = JsonHelper.createJson(tramiteDoc.getTramite(), JsonNodeFactory.instance, false, mapperTramite);
                node.set("estados", arrayAcciones);
                node.set("tramite", tramiteJson);
                array.add(node);
            }
            json.setData(array);
            json.setTotal(array.size());
            json.setFiltered(array.size());
        } catch (Exception e) {
            e.printStackTrace();
            json.setTotal(0);
        }
        return json;
    }
}
