package pe.edu.lamolina.pivot.controller.academico.tramitesacademicos.proceso;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.beans.PropertyEditorSupport;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.servlet.http.HttpSession;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import pe.albatross.zelpers.miscelanea.JsonHelper;
import pe.edu.lamolina.model.tramite.Tramite;
import pe.edu.lamolina.pivot.controller.academico.tramitesacademicos.TramitesAcademicosService;
import pe.edu.lamolina.pivot.zelper.constant.Constantine;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

@Controller
@RequestMapping("academico/procesar")
public class ProcesarTramiteController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    TramitesAcademicosService tramitesAcademicosService;

    @InitBinder
    public void initBinder(WebDataBinder dataBinder) {

        dataBinder.registerCustomEditor(Date.class, new PropertyEditorSupport() {
            @Override
            public void setAsText(String value) {
                try {
                    setValue(new SimpleDateFormat("dd/MM/yyyy").parse(value));
                } catch (ParseException e) {
                    setValue(null);
                }
            }
        });

        dataBinder.registerCustomEditor(BigDecimal.class, new PropertyEditorSupport() {
            @Override
            public void setAsText(String value) {
                try {
                    setValue(new BigDecimal(value.replaceAll(",", "")));
                } catch (Exception e) {
                    setValue(null);
                }
            }
        });
    }

    @RequestMapping("{tramite}")
    public String index(Model model, HttpSession session,
            @PathVariable("tramite") Long tramiteId) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        
        Tramite tramite = tramitesAcademicosService.findTramite(tramiteId);
        String[] mapperTramite = new String[]{
            "id",
            "*",
            "persona.*",
            "alumno.*",
            "alumno.carrera.*",
            "alumno.carrera.facultad.*",
            "alumno.planCurricular.id",
            "alumno.modalidadEstudio.id",
            "alumno.modalidadEstudio.nombre",
            "alumno.planCurricular.carrera.nombre",
            "alumno.planCurricular.cicloInicioVigencia.descripcion",
            "compania.*",
            "cicloAcademico.*",
            "tipoTramite.codigo",
            "tipoTramite.nombre",
            "tipoTramite.esTipoTramiteRei",
            "userRegistro.*",
            "userRegistro.persona.*",
            "userRespuesta.*",
            "accionesTramitesAcademico.*",
            "accionesTramitesAcademico.estadoTramiteFinal.*",
            "accionesTramitesAcademico.estadoTramiteInicio.*",
            "accionesTramitesDocumentos.*",
            "accionesTramitesDocumentos.estadoTramiteFinal.*",
            "accionesTramitesDocumentos.estadoTramite.*",
            "formularioEstadoTramite.*"
        };

        String[] mapperEstadoTramite = new String[]{
            "estadoTramite.nombre",
            "estadoTramite.id",
            "estadoTramite.nombre",
            "estadoTramite.esSolicitudReincorporacion",
            "estadoTramite.esSolicitudHistorialRevisado",
            "estadoTramite.esConsejoFacultad"
        };

        String[] mapperTramiteReunionConsejo = new String[]{
            "tramiteReunionConsejo.*",
            "tramiteReunionConsejo.reunionConsejo.*"
        };

        String[] mapperTramiteComplex = (String[]) ArrayUtils.addAll(mapperTramite, mapperEstadoTramite);
        mapperTramiteComplex = (String[]) ArrayUtils.addAll(mapperTramiteComplex, mapperTramiteReunionConsejo);

        model.addAttribute("tramite", JsonHelper.createJson(tramite, JsonNodeFactory.instance, true, mapperTramiteComplex));
        return "academico/tramitescademicos/proceso/procesarTramite";
    }

    @RequestMapping("procesarTramite")
    public String procesarTramite(Model model, @RequestParam("tramite") Long tramite, HttpSession session) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        //   return "academico/tramitescademicos/procesarTramite";
        return "redirect:/someOtherURL";
    }

}
