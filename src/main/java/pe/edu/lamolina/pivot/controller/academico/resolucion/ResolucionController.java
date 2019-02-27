package pe.edu.lamolina.pivot.controller.academico.resolucion;

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
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableResponse;
import pe.albatross.zelpers.miscelanea.ExceptionHandler;
import pe.albatross.zelpers.miscelanea.JsonHelper;
import pe.albatross.zelpers.miscelanea.JsonResponse;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.albatross.zelpers.notify.Notificaciones;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.general.Oficina;
import pe.edu.lamolina.model.tramite.Reincorporacion;
import pe.edu.lamolina.model.tramite.TramiteReunionConsejo;
import pe.edu.lamolina.model.tramite.Resolucion;
import pe.edu.lamolina.model.tramite.ReunionConsejo;
import pe.edu.lamolina.model.tramite.TipoResolucion;
import pe.edu.lamolina.model.tramite.Tramite;
import pe.edu.lamolina.pivot.zelper.constant.Constantine;
import pe.edu.lamolina.pivot.zelper.constant.Messages;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

@Controller
@RequestMapping("academico/resolucion")
public class ResolucionController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    ResolucionService service;

    private MultipartFile resolucionFile;

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
        return "academico/resolucion/resolucion";
    }

    @RequestMapping("nuevo")
    public String nuevo(Model model, HttpSession session) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        model.addAttribute("resolucion", new Resolucion());
        model.addAttribute("cicloAcademico", ds.getCicloAcademico());
        return "academico/resolucion/resolucionForm";
    }

    @RequestMapping("{resolucion}/editar")
    public String editar(@PathVariable("resolucion") Long resolucionId, Model model, HttpSession session) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        model.addAttribute("cicloAcademico", ds.getCicloAcademico());
        Resolucion resolucion = new Resolucion(resolucionId);
        ObjectNode resolucionJson = JsonHelper.createJson(resolucion, JsonNodeFactory.instance);
        model.addAttribute("resolucionJson", resolucionJson.toString());

        resolucion = service.findResolucion(resolucionId);
        model.addAttribute("resolucion", resolucion);
        return "academico/resolucion/resolucionForm";
    }

    @ResponseBody
    @RequestMapping("loadModalResolucion")
    public JsonResponse loadModalResolucion(@RequestParam(name = "resolucion", required = false) Long resolucionId,
            Model model,
            HttpSession session) {
        JsonResponse response = new JsonResponse();
        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);

            response.setSuccess(Boolean.TRUE);

            JsonNodeFactory jc = JsonNodeFactory.instance;
            Resolucion resolucion = service.findResolucion(resolucionId);
            List<CicloAcademico> ciclos = service.allCiclosToReincorporacion();

            ObjectNode resolucionJson = JsonHelper.createJson(resolucion, jc, true,
                    new String[]{"*",
                        "oficina.id",
                        "cicloReincorporacion.*",
                        "oficina.nombre",
                        "reunionConsejo.*",
                        "tipoResolucion.*",
                        "userRegistro.*",
                        "userRegistro.persona.*",
                        "userActualizacion.*",
                        "userActualizacion.persona.*"});

            ArrayNode ciclosJson = new ArrayNode(jc);
            for (CicloAcademico ciclo : ciclos) {
                ciclosJson.add(JsonHelper.createJson(ciclo, jc, new String[]{"*"}));
            }

            ObjectNode data = new ObjectNode(jc);
            data.set("resolucion", resolucionJson);
            data.set("ciclosToReincorporacion", ciclosJson);

            response.setData(data);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (RuntimeException e) {
            ExceptionHandler.handleSpecial(e, response, e.getLocalizedMessage());
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("loadFormResolucion")
    public JsonResponse loadFormResolucion(
            @RequestParam(name = "resolucion", required = false) Long resolucionId,
            Model model,
            HttpSession session) {
        JsonResponse response = new JsonResponse();
        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);

            response.setSuccess(Boolean.TRUE);

            JsonNodeFactory jc = JsonNodeFactory.instance;
            Resolucion resolucion = new Resolucion();
            if (resolucionId != null) {
                resolucion = service.findResolucion(resolucionId);
            } else {
                resolucionFile = null;
                resolucion.setFecha(new Date());
            }

            ObjectNode resolucionJson = JsonHelper.createJson(resolucion, jc, true,
                    new String[]{"*",
                        "oficina.id",
                        "oficina.nombre",
                        "reunionConsejo.*",
                        "tipoResolucion.*",
                        "userRegistro.*",
                        "userRegistro.persona.*",
                        "userActualizacion.*",
                        "userActualizacion.persona.*"});
            if (resolucionId != null) {
                resolucionJson.put("esEdicion", true);
            }
            List<TipoResolucion> tiposResoluciones = service.allTiposResolucion();
            ArrayNode tiposResolucionesJson = new ArrayNode(JsonNodeFactory.instance);
            for (TipoResolucion tipoResolucion : tiposResoluciones) {
                tiposResolucionesJson.add(JsonHelper.createJson(tipoResolucion, jc, false, new String[]{
                    "*"
                }));
            }

            if (StringUtils.isBlank(resolucionJson.get("id").asText())) {
                //       resolucionJson.remove("id");
            }

            ObjectNode data = new ObjectNode(jc);
            data.set("resolucionJson", resolucionJson);
            data.set("tiposResolucionesJson", tiposResolucionesJson);

            ArrayNode oficinasJson = new ArrayNode(jc);
            List<Oficina> oficinas = service.allOFicinasByUser(ds);
            for (Oficina oficina : oficinas) {
                ObjectNode oficinaJson = JsonHelper.createJson(oficina, jc, true, new String[]{"*"});
                oficinasJson.add(oficinaJson);
            }

            data.set("oficinasJson", oficinasJson);

            response.setData(data);
        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (RuntimeException e) {
            ExceptionHandler.handleSpecial(e, response, e.getLocalizedMessage());
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("listResoluciones")
    public DynatableResponse listResoluciones(DynatableFilter filter,
            HttpSession session) {
        DynatableResponse json = new DynatableResponse();
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        try {
            ArrayNode array = new ArrayNode(JsonNodeFactory.instance);
            CicloAcademico ciclo = ds.getCicloAcademico();
            DateTime today = new DateTime();

            List<Resolucion> resoluciones = service.allResolucionesByFilter(filter);
            logger.debug("cantidad de resoluciones " + resoluciones.size());

            for (Resolucion resolucionEach : resoluciones) {

                ObjectNode resolucionJson = JsonHelper.createJson(resolucionEach, JsonNodeFactory.instance,
                        new String[]{
                            "*",
                            "oficina.*",
                            "tipoResolucion.*",
                            "userRegistro.persona.*"
                        });
                array.add(resolucionJson);
            }

            json.setData(array);
            json.setTotal(resoluciones.size());
            json.setFiltered(resoluciones.size());

        } catch (Exception e) {
            e.printStackTrace();
            json.setTotal(0);
        }
        return json;
    }

    @ResponseBody
    @RequestMapping("listTramitesToConfirm")
    public DynatableResponse listTramitesToConfirm(DynatableFilter filter,
            @RequestParam(name = "resolucion", required = false) Long resolucionId,
            HttpSession session) {
        DynatableResponse json = new DynatableResponse();
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        try {
            ArrayNode array = new ArrayNode(JsonNodeFactory.instance);
            CicloAcademico ciclo = ds.getCicloAcademico();
            DateTime today = new DateTime();
            if (resolucionId == null) {
                json.setTotal(0);
                return json;
            }
            List<Reincorporacion> reincorporaciones = service.allReincorporacionByFilter(filter, new Resolucion(resolucionId));
            logger.debug("cantidad de reincorporaciones " + reincorporaciones.size());

            for (Reincorporacion reincorporacion : reincorporaciones) {

                ObjectNode reincorporacionJson = JsonHelper.createJson(reincorporacion, JsonNodeFactory.instance,
                        new String[]{
                            "*",
                            "tramite.*",
                            "tramite.tipoTramite.*",
                            "tramite.persona.*",
                            "estadoTramite.*",
                            "cicloReincorporacion.*",
                            "resolucion.*",
                            "userRegistro.*",
                            "userRegistro.persona.*"
                        });
                array.add(reincorporacionJson);
            }

            json.setData(array);
            json.setTotal(reincorporaciones.size());
            json.setFiltered(reincorporaciones.size());

        } catch (Exception e) {
            e.printStackTrace();
            json.setTotal(0);
        }
        return json;
    }

    @ResponseBody
    @RequestMapping("listTramiteReunionConsejo")
    public DynatableResponse listTramiteReunionConsejo(DynatableFilter filter,
            @RequestParam(name = "reunionConsejo", required = false) Long reunionConsejoId,
            @RequestParam(name = "tipoResolucion", required = false) Long tipoResolucionId,
            @RequestParam(name = "resolucion", required = false) Long resolucionId,
            HttpSession session) {
        DynatableResponse json = new DynatableResponse();
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        logger.debug(" Reunion Consejo Id {} ", reunionConsejoId);
        if (reunionConsejoId == null && tipoResolucionId == null) {
            json.setTotal(0);
            return json;
        }
        try {
            ArrayNode array = new ArrayNode(JsonNodeFactory.instance);
            CicloAcademico ciclo = ds.getCicloAcademico();
            DateTime today = new DateTime();

            List<TramiteReunionConsejo> alumnosReunionConsejo = service.allTramiteReunionConsejoByReunion(new ReunionConsejo(reunionConsejoId), new TipoResolucion(tipoResolucionId));
            logger.debug("alumnos reunion consejo " + alumnosReunionConsejo.size());
            //reunionConsejo alumno

            String[] mapperTramite = new String[]{
                "*",
                "reunionConsejo.*",
                "tramite.*",
                "tramite.alumno.*",
                "tramite.tipoTramite.*",
                "tramite.alumno.persona.*",
                "userRegistro.*",
                "userActualizacion.*"
            };

            String[] mapperReincorporacion = new String[]{
                "estadoTramite.nombre",
                "estadoTramite.id",
                "estadoTramite.nombre",
                "estadoTramite.esSolicitudReincorporacion",
                "estadoTramite.esSolicitudHistorialRevisado",
                "estadoTramite.esConsejoFacultad"
            };

            for (TramiteReunionConsejo tramiteReunionConsejo : alumnosReunionConsejo) {
                if (resolucionId == null) {
                    tramiteReunionConsejo.setSeleccionado(Boolean.TRUE);
                } else {
                    Tramite tramite = service.findTramite(tramiteReunionConsejo.getTramite().getId());
                    Reincorporacion reincorporacion = tramite.getReincorporaciones().get(0);
                    tramiteReunionConsejo.setSeleccionado(reincorporacion.getAceptado() == 1 ? true : false);
                }
                ArrayNode reincorporaciones = null;
                if (tramiteReunionConsejo.getTramite().getReincorporaciones() != null && !tramiteReunionConsejo.getTramite().getReincorporaciones().isEmpty()) {
                    reincorporaciones = new ArrayNode(JsonNodeFactory.instance);
                    for (Reincorporacion reincorporacionEach : tramiteReunionConsejo.getTramite().getReincorporaciones()) {
                        reincorporaciones.addPOJO(JsonHelper.createJson(reincorporacionEach, JsonNodeFactory.instance, false, mapperReincorporacion));
                    }
                }

                ObjectNode reunionConseJson = JsonHelper.createJson(tramiteReunionConsejo, JsonNodeFactory.instance, false, mapperTramite);
                ObjectNode tramiteJson = (ObjectNode) reunionConseJson.get("tramite");
                if (reincorporaciones != null) {
                    tramiteJson.set("reincorporaciones", reincorporaciones);
                }
                reunionConseJson.replace("tramite", tramiteJson);
                array.add(reunionConseJson);
            }

            json.setData(array);
            json.setTotal(alumnosReunionConsejo.size());
            json.setFiltered(alumnosReunionConsejo.size());

        } catch (Exception e) {
            e.printStackTrace();
            json.setTotal(0);
        }
        return json;
    }

    @ResponseBody
    @RequestMapping("saveResolucion")
    public JsonResponse saveResolucion(
            @RequestBody Resolucion resolucion,
            Model model,
            HttpSession session) {
        JsonResponse response = new JsonResponse();
        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);

            ObjectNode node = new ObjectNode(JsonNodeFactory.instance);
            if (resolucion.getId() == null) {
                service.saveResolucion(resolucion, ds, ds.getCicloAcademico());
                node.put("operation", "s");
                // node.put("planCurricular", planCurricular.getId());

            } else {
                node.put("operation", "u");
                service.updateResolucion(resolucion, ds);
                response.setMessage("Resolución actualizada correctamente.");
            }

            response.setData(node);
            response.setSuccess(true);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (RuntimeException e) {
            ExceptionHandler.handleSpecial(e, response, Messages.FK_ERROR);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @RequestMapping("succesSave")
    public String succesSave(RedirectAttributes redirectAttr, HttpSession session) {
        Notificaciones.crearMsg("Resolución guardada correctamente.", redirectAttr);
        return "redirect:/academico/resolucion";
    }

    @ResponseBody
    @RequestMapping("saveConfirmarResVB")
    public JsonResponse saveConfirmarResVB(
            @RequestBody Resolucion resolucion,
            Model model,
            HttpSession session) {
        JsonResponse response = new JsonResponse();
        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);

            ObjectNode node = new ObjectNode(JsonNodeFactory.instance);

            node.put("operation", "s");
            service.saveConfirmarResVB(resolucion, ds, ds.getCicloAcademico());

            response.setData(node);
            response.setSuccess(true);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (RuntimeException e) {
            ExceptionHandler.handleSpecial(e, response, Messages.FK_ERROR);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @RequestMapping("succesSaveResVB")
    public String succesSaveResVB(RedirectAttributes redirectAttr, HttpSession session) {
        Notificaciones.crearMsg("Visto Bueno registrado correctamente.", redirectAttr);
        return "redirect:/academico/deleteGrupoSeccion";
    }

    @ResponseBody
    @RequestMapping("addFile")
    public JsonResponse addFile(@RequestParam("resolucionId") Long resolucionId,
            @RequestParam("file") MultipartFile file,
            HttpSession session) {
        JsonResponse response = new JsonResponse();
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        try {
            logger.debug("file {}, content type {}, size {}", file.getOriginalFilename(), file.getContentType(), file.getSize());

            service.uploadResolucionFile(new Resolucion(resolucionId), file, ds);

            //    response.setData(name);
            response.setMessage("Archivo cargado.");
            response.setSuccess(true);
        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception ex) {
            ExceptionHandler.handleException(ex, response);
        }
        return response;

    }

    @ResponseBody
    @RequestMapping("cambiarOficina")
    public JsonResponse cambiarOficina(
            @RequestParam("oficina") Long oficinaId,
            HttpSession session,
            Model model) {
        JsonNodeFactory jsonFactory = JsonNodeFactory.instance;
        JsonResponse response = new JsonResponse();
        ObjectNode nodeResult = new ObjectNode(jsonFactory);
        try {
            logger.debug("entro a cambiarOficina");

            ArrayNode reunionesConsejoJson = new ArrayNode(jsonFactory);
            List<ReunionConsejo> reunionesConsejo = service.allReunionesConsejoByOficina(new Oficina(oficinaId));
            for (ReunionConsejo reunionConsejo : reunionesConsejo) {
                reunionesConsejoJson.add(JsonHelper.createJson(reunionConsejo, jsonFactory, false, new String[]{
                    "*",}));
            }

            nodeResult.set("reunionesConsejo", reunionesConsejoJson);

            response.setData(nodeResult);
            response.setSuccess(Boolean.TRUE);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("saveConfirmar")
    public JsonResponse saveConfirmarSubirDocumento(
            @RequestBody Resolucion resolucion,
            Model model,
            HttpSession session) {
        JsonResponse response = new JsonResponse();
        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);

            ObjectNode node = new ObjectNode(JsonNodeFactory.instance);

            service.saveConfirmarSubirDocumento(resolucion, ds);

            response.setMessage("Resolucion confirmada.");

            response.setData(node);
            response.setSuccess(true);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (RuntimeException e) {
            ExceptionHandler.handleSpecial(e, response, Messages.FK_ERROR);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

}
