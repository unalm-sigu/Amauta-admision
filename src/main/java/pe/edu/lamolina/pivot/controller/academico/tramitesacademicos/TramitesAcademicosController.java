package pe.edu.lamolina.pivot.controller.academico.tramitesacademicos;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.beans.PropertyEditorSupport;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import javax.servlet.http.HttpSession;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableResponse;
import pe.albatross.zelpers.miscelanea.ExceptionHandler;
import pe.albatross.zelpers.miscelanea.JsonHelper;
import pe.albatross.zelpers.miscelanea.JsonResponse;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Docente;
import pe.edu.lamolina.model.enums.EstadoTramiteEnum;
import pe.edu.lamolina.model.general.Oficina;
import pe.edu.lamolina.model.tramite.Reincorporacion;
import pe.edu.lamolina.model.tramite.ReunionConsejo;
import pe.edu.lamolina.model.tramite.Tramite;
import pe.edu.lamolina.pivot.zelper.constant.Constantine;
import pe.edu.lamolina.pivot.zelper.constant.Messages;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

@Controller
@RequestMapping("academico/tramiteacademico")
public class TramitesAcademicosController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    Oficina oficinaAux = new Oficina(8L);

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

    @RequestMapping(method = RequestMethod.GET)
    public String index(Model model, HttpSession session) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        model.addAttribute("ciclo", ds.getCicloAcademico());
        return "academico/tramitescademicos/tramitesAcademicos";
    }

    @ResponseBody
    @RequestMapping("listTramites")
    public DynatableResponse listTramites(DynatableFilter filter,
            HttpSession session) {
        DynatableResponse json = new DynatableResponse();
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        try {
            ArrayNode array = new ArrayNode(JsonNodeFactory.instance);
            CicloAcademico ciclo = ds.getCicloAcademico();
            Docente docente = ds.getDocente();
            DateTime dateTime = new DateTime();

            List<Tramite> tramites = tramitesAcademicosService.allTramitesByFilter(filter);
            logger.debug(this.getClass() + " Cantidad de tramites {}", tramites.size());

            String[] mapperTramite = new String[]{
                "*",
                "persona.*",
                "alumno.*",
                "compania.*",
                "cicloAcademico.*",
                "tipoTramite.codigo",
                "tipoTramite.nombre",
                "tipoTramite.esTipoTramiteRei",
                "userRegistro.*",
                "userRespuesta.*"
            };
            String[] mapperReincorporacion = new String[]{
                "estadoTramite.nombre",
                "estadoTramite.id",
                "estadoTramite.nombre",
                "estadoTramite.esSolicitudReincorporacion",
                "estadoTramite.esSolicitudHistorialRevisado",
                "estadoTramite.esConsejoFacultad"
            };
            JsonNodeFactory jc = JsonNodeFactory.instance;
            for (Tramite tramite : tramites) {
                ObjectNode tramiteJson = JsonHelper.createJson(tramite, jc, false, mapperTramite);
                ArrayNode reincorporaciones = null;
                if (tramite.getReincorporaciones() != null && !tramite.getReincorporaciones().isEmpty()) {
                    reincorporaciones = new ArrayNode(jc);
                    for (Reincorporacion reincorporacionEach : tramite.getReincorporaciones()) {
                        reincorporaciones.addPOJO(JsonHelper.createJson(reincorporacionEach, jc, false, mapperReincorporacion));
                    }
                }
                tramiteJson.set("reincorporaciones", reincorporaciones);
                array.add(tramiteJson);
            }

            json.setData(array);
            json.setTotal(tramites.size());
            json.setFiltered(tramites.size());

        } catch (Exception e) {
            e.printStackTrace();
            json.setTotal(0);
        }
        return json;
    }

    @ResponseBody
    @RequestMapping("cambiarEstadoReincorporacion")
    public JsonResponse cambiarAulaDirect(
            @RequestParam("tramite") Long tramiteId,
            @RequestParam("estado") String estadoDestino,
            HttpSession session) {
        JsonNodeFactory jsonFactory = JsonNodeFactory.instance;
        JsonResponse response = new JsonResponse();
        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
            if (EstadoTramiteEnum.REV_HIS.name().equals(estadoDestino)) {
                tramitesAcademicosService.aceptarSolReincorporacion(new Tramite(tramiteId), ds.getUsuario());
                response.setMessage("Solicitud aceptada.");
            }

            response.setSuccess(Boolean.TRUE);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (RuntimeException e) {
            ExceptionHandler.handleSpecial(e, response, Messages.FK_ERROR);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @RequestMapping("agendareuniones")
    public String agendareuniones(Model model, HttpSession session) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);

        return "academico/reunionconsejo/reunionconsejo";
    }

    @ResponseBody
    @RequestMapping("loadModalAgendar")
    public JsonResponse loadModalAgendar(
            @RequestBody Tramite tramite,
            Model model,
            HttpSession session) {
        JsonNodeFactory jsonFactory = JsonNodeFactory.instance;
        JsonResponse response = new JsonResponse();

        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
            CicloAcademico cicloAcademico = ds.getCicloAcademico();

            ObjectNode node = new ObjectNode(jsonFactory);
            /*
            ReunionConsejo reunionConsejo = reunionConsejoService.findReunionConsejoByFechaAndOficina(fechaReunion, oficinaAux);
            if (reunionConsejo == null) {
                reunionConsejo = new ReunionConsejo();
                reunionConsejo.setEsOrdinario(BigDecimal.ONE.intValue());
            }

            node.set("reunionConsejo", JsonHelper.createJson(reunionConsejo, jsonFactory, true, new String[]{
                "*",
                "usuarioRegistro.*",
                "usuarioActualizacion.*",}));
             */
            response.setData(node);
            response.setSuccess(Boolean.TRUE);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }

        return response;
    }

    @ResponseBody
    @RequestMapping("listReunionesConsejo")
    public DynatableResponse listReunionesConsejo(DynatableFilter filter, HttpSession session) {
        DynatableResponse json = new DynatableResponse();

        try {

            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);

            List<ReunionConsejo> reunionesConsejo = tramitesAcademicosService.allReunionConsejoByDyna(filter, oficinaAux);

            JsonNodeFactory nf = JsonNodeFactory.instance;
            ArrayNode array = new ArrayNode(nf);
            for (ReunionConsejo reunionConsejo : reunionesConsejo) {
                array.add(JsonHelper.createJson(reunionConsejo, nf, new String[]{
                    "*",
                    "oficina.*"
                }));
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
    @RequestMapping("saveAgendar")
    public JsonResponse saveAgendar(
            @RequestParam("reunionConsejo") Long reunionConsejoId,
            @RequestParam("tramite") Long tramiteId,
            Model model,
            HttpSession session) {
        JsonNodeFactory jsonFactory = JsonNodeFactory.instance;
        JsonResponse response = new JsonResponse();

        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
            CicloAcademico cicloAcademico = ds.getCicloAcademico();

            tramitesAcademicosService.agendarSolicitud(new Tramite(tramiteId), new ReunionConsejo(reunionConsejoId), ds.getUsuario());
            response.setMessage("Agendado correctamente.");
            /*
            ObjectNode node = new ObjectNode(jsonFactory);

      
            node.set("reunionConsejo", JsonHelper.createJson(reunionConsejo, jsonFactory, true, new String[]{
                "*",
                "usuarioRegistro.*",
                "usuarioActualizacion.*",}));
             */
            //  response.setData(node);
            response.setSuccess(Boolean.TRUE);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }

        return response;
    }

}
