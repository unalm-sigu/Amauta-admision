package pe.edu.lamolina.amauta.controller.academico.tramitesacademicos;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.beans.PropertyEditorSupport;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
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
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.thymeleaf.context.Context;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableResponse;
import pe.albatross.zelpers.json.JaneHelper;
import pe.albatross.zelpers.miscelanea.ExceptionHandler;
import pe.albatross.zelpers.miscelanea.JsonHelper;
import pe.albatross.zelpers.miscelanea.JsonResponse;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.albatross.zelpers.notify.Notificaciones;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.AlumnoCiclo;
import pe.edu.lamolina.model.academico.AlumnoCicloCurso;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Curso;
import pe.edu.lamolina.model.academico.Docente;
import pe.edu.lamolina.model.enums.TipoOficinaEnum;
import pe.edu.lamolina.model.general.Oficina;
import pe.edu.lamolina.model.horario.Hora;
import pe.edu.lamolina.model.tramite.AccionTramiteAcademico;
import pe.edu.lamolina.model.tramite.AccionTramiteDocumento;
import pe.edu.lamolina.model.tramite.CursoDirigido;
import pe.edu.lamolina.model.tramite.ReunionConsejo;
import pe.edu.lamolina.model.tramite.TipoTramite;
import pe.edu.lamolina.model.tramite.Tramite;
import pe.edu.lamolina.model.tramite.TramiteReunionConsejo;
import pe.edu.lamolina.amauta.controller.academico.reunionconsejo.ReunionConsejoService;
import pe.edu.lamolina.model.constantines.GlobalConstantine;
import pe.edu.lamolina.model.constantines.GlobalMessages;
import pe.edu.lamolina.amauta.controller.academico.infoacademico.InfoAcademicoService;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;
import pe.edu.lamolina.amauta.zelper.pdf.PdfHtml;
import pe.edu.lamolina.amauta.controller.general.oficina.util.OficinaService;
import pe.edu.lamolina.model.enums.oficina.OficinaEnum;

@Slf4j
@Controller
@AllArgsConstructor(onConstructor = @__(
        @Autowired))
@RequestMapping("academico/tramiteacademico")
public class TramitesAcademicosController {

    private final TramitesAcademicosService service;
    private final ReunionConsejoService reunionConsejoService;
    private final InfoAcademicoService infoAcademicoService;
    private final OficinaService oficinaService;
    private final PdfHtml pdfHtml;

    private final String[] alumnoCicloMapper = new String[]{"*",
        "alumno.id",
        "alumno.codigo",
        "alumno.persona.id",
        "alumno.persona.nombres",
        "alumno.persona.paterno",
        "alumno.persona.materno",
        "cicloAcademico.*",
        "carrera.*",
        "carrera.facultad.*",
        "controlMeritoCiclo.*",
        "orientacionCarrera.*",
        "situacionInicio.id",
        "situacionInicio.codigo",
        "situacionInicio.nombre",
        "situacionFinal.id",
        "situacionFinal.codigo",
        "situacionFinal.nombre",
        //  "alumnoCicloCurso.*",
        "alumnoCicloCurso.id",
        "alumnoCicloCurso.estado",
        "alumnoCicloCurso.creditos",
        "alumnoCicloCurso.nota",
        "alumnoCicloCurso.estaAprobado",
        "alumnoCicloCurso.registroActivo",
        "alumnoCicloCurso.vecesCursado",
        "alumnoCicloCurso.estadoEnum",
        "alumnoCicloCurso.curso.id",
        "alumnoCicloCurso.curso.codigo",
        "alumnoCicloCurso.curso.nombre",
        "alumnoCicloCurso.isEstadoMatriculado",
        "alumnoCicloCurso.isEstadoNotaModificada",
        "alumnoCicloCurso.estaActivo",
        "alumnoCicloCurso.autorizacionRegistro.id",
        "alumnoCicloCurso.autorizacionRegistro.estado",
        "alumnoCicloCurso.isHijo"
    };

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
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
        List<Oficina> oficinas = oficinaService.allOficinasMainByPersona(ds.getPersona());
        ArrayNode oficinasJson = JaneHelper.from(oficinas).array();
        model.addAttribute("oficinas", oficinasJson);
        model.addAttribute("ciclo", ds.getCicloAcademico());
        return "academico/tramitescademicos/tramitesAcademicos";
    }

    @ResponseBody
    @RequestMapping("listTramites")
    public DynatableResponse listTramites(DynatableFilter filter,
            HttpSession session) {
        DynatableResponse json = new DynatableResponse();
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
        try {
            ArrayNode array = new ArrayNode(JsonNodeFactory.instance);
            List<Tramite> tramites = service.allTramitesByFilter(filter, ds);

            String[] mapperTramite = new String[]{
                "*",
                "persona.*",
                "alumno.*",
                "alumno.planCurricular.*",
                "alumno.carrera.*",
                "alumno.carrera.facultad.*",
                "compania.*",
                "cicloAcademico.*",
                "tipoTramite.codigo",
                "tipoTramite.nombre",
                "tipoTramite.esReincorporacionPregrado",
                "tipoTramite.esCursoDirigido",
                "tipoTramite.oficina.*",
                "userRegistro.*",
                "userRegistro.persona.*",
                "userRespuesta.*",
                "formularioEstadoTramite.*"
            };

            String[] mapperEstadoTramite = new String[]{
                "estadoTramite.nombre",
                "estadoTramite.id",
                "estadoTramite.nombre",
                "estadoTramite.esSolicitudReincorporacion",
                "estadoTramite.esSolicitudHistorialRevisado",
                "estadoTramite.esConsejoFacultad",
                "estadoTramite.isVisibleBandeja"
            };

            String[] mapperTramiteComplex = (String[]) ArrayUtils.addAll(mapperTramite, mapperEstadoTramite);

            String[] mapperReunionConsejo = new String[]{
                "*",
                "reunionConsejo.*"
            };

            JsonNodeFactory jc = JsonNodeFactory.instance;
            for (Tramite tramite : tramites) {
                ObjectNode tramiteJson = JsonHelper.createJson(tramite, jc, false, mapperTramiteComplex);

                if (tramite.isTipoCursoDirigido()) {
                    CursoDirigido cursoDirigido = tramite.getCursoDirigido().get(0);
                    tramiteJson.set("cursodirigido", JsonHelper.createJson(cursoDirigido, jc, false, new String[]{
                        "*",
                        "curso.nombre",
                        "docenteAsignado.*",
                        "docenteAsignado.persona.*"
                    }));
                }
                if (tramite.getTramitesReunionConsejo() != null && !tramite.getTramitesReunionConsejo().isEmpty()) {
                    TramiteReunionConsejo tramiteReunionConsejo = tramite.getTramitesReunionConsejo().get(0);
                    tramiteJson.set("tramiteReunionConsejo", JsonHelper.createJson(tramiteReunionConsejo, jc, false, mapperReunionConsejo));
                }
                ArrayNode accionesTramiteJson = new ArrayNode(jc);

                for (AccionTramiteAcademico accionTramiteAcademico : tramite.getAccionesTramitesAcademico()) {

                    accionesTramiteJson.add(JsonHelper.createJson(accionTramiteAcademico, jc, new String[]{
                        "*",
                        "estadoTramiteInicio.id",
                        "estadoTramiteInicio.nombre",
                        "estadoTramiteFinal.id",
                        "estadoTramiteFinal.nombre"
                    }));
                }
                tramiteJson.set("accionesTramite", accionesTramiteJson);
                array.add(tramiteJson);
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

    @RequestMapping("agendareuniones")
    public String agendareuniones(Model model, HttpSession session) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
        List<Oficina> oficinas = new ArrayList();
        oficinas = findOficina(oficinas, ds);
        if (oficinas.isEmpty()) {
            return "redirect:/academico/tramiteacademico";
        }
        model.addAttribute("oficinas", JaneHelper.from(oficinas).array().toString());
        return "academico/reunionconsejo/reunionconsejo";
    }

    @ResponseBody
    @RequestMapping("listReunionesConsejo/{idOficina}")
    public DynatableResponse listReunionesConsejo(@PathVariable Long idOficina, DynatableFilter filter, HttpSession session) {
        DynatableResponse json = new DynatableResponse();

        try {

            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
            List<Oficina> oficinas = new ArrayList();
            oficinas = findOficina(oficinas, ds);
            if (idOficina != 0) {
                oficinas.clear();
                oficinas.add(new Oficina(idOficina));
            }

            List<ReunionConsejo> reunionesConsejo = service.allReunionConsejoByDyna(filter, oficinas);

            ArrayNode array = JaneHelper.from(reunionesConsejo)
                    .join("oficina").array();

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
    @RequestMapping("revertirEstadoTramite")
    public JsonResponse revertirEstadoTramite(
            @RequestBody Tramite tramite,
            Model model,
            HttpSession session) {

        JsonResponse response = new JsonResponse();

        try {

            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
            service.revertTramiteAcademico(tramite, ds);

            response.setMessage("Reversión completada");
            response.setSuccess(Boolean.TRUE);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }

        return response;
    }

    @RequestMapping("procesar/{tramite}")
    public String procesarTramite(Model model,
            @PathVariable("tramite") Long tramiteId,
            HttpSession session) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);

        ObjectNode tramiteJson = JsonHelper.createJson(new Tramite(tramiteId), JsonNodeFactory.instance);

        model.addAttribute("tramiteJson", tramiteJson.toString());

        ArrayNode horasJson = new ArrayNode(JsonNodeFactory.instance);
        List<Hora> horas = infoAcademicoService.allHoras();
        for (Hora hora : horas) {
            horasJson.add(JsonHelper.createJson(hora, JsonNodeFactory.instance, true, new String[]{"*"}));
        }
        List<Oficina> oficinas = new ArrayList();
        oficinas = findOficina(oficinas, ds);
        if (oficinas.isEmpty()) {
            return "redirect:/academico/tramiteacademico";
        }
        model.addAttribute("oficinas", JaneHelper.from(oficinas).array().toString());
        model.addAttribute("horasBD", horasJson.toString());

        return "academico/tramitescademicos/proceso/procesarTramite";
    }

    @RequestMapping("procesarNota")
    public String procesarNota(Model model,
            HttpSession session) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);

        ArrayNode horasJson = new ArrayNode(JsonNodeFactory.instance);
        List<Hora> horas = infoAcademicoService.allHoras();
        for (Hora hora : horas) {
            horasJson.add(JsonHelper.createJson(hora, JsonNodeFactory.instance, true, new String[]{"*"}));
        }
        List<Oficina> oficinas = new ArrayList();
        oficinas = findOficina(oficinas, ds);
        if (oficinas.isEmpty()) {
            return "redirect:/academico/tramiteacademico";
        }
        model.addAttribute("oficinas", JaneHelper.from(oficinas).array().toString());
        model.addAttribute("horasBD", horasJson.toString());

        return "academico/tramitescademicos/proceso/procesarNotas";
    }

    @ResponseBody
    @RequestMapping("{tramite}/loadFormProcesar")
    public JsonResponse loadGpoSeccionForm(@PathVariable("tramite") Long tramiteId, HttpSession session) {

        JsonResponse response = new JsonResponse();

        try {

            JsonNodeFactory jsonFactory = JsonNodeFactory.instance;
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);

            ObjectNode node = new ObjectNode(jsonFactory);
            Tramite tramite = service.findTramite(tramiteId);

            String[] mapperTramite = new String[]{
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
                "tipoTramite.esReincorporacionPregrado",
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

            node.set("tramite", JsonHelper.createJson(tramite, jsonFactory, true, mapperTramiteComplex));

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
    @RequestMapping("cambiarEstadoReincorporacion")
    public JsonResponse cambiarAulaDirect(
            @RequestParam("tramite") Long tramiteId,
            @RequestParam("accionTramite") Long accionTramiteId,
            HttpSession session) {
        JsonResponse response = new JsonResponse();
        try {

            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
            service.aceptarSolReincorporacion(new Tramite(tramiteId), new AccionTramiteAcademico(accionTramiteId), ds);
            response.setMessage("Solicitud Procesada.");

            response.setSuccess(Boolean.TRUE);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (RuntimeException e) {
            ExceptionHandler.handleSpecial(e, response, GlobalMessages.FK_ERROR_UPDATE);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("procesarTramite")
    public JsonResponse procesarTramite(@RequestBody ObjectNode tramiteNode,
            Model model,
            HttpSession session) {
        JsonResponse response = new JsonResponse();
        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);

            Tramite tramite = new Tramite(tramiteNode.get("tramite").asLong());
            AccionTramiteAcademico accionTramiteAcademico = null;
            AccionTramiteDocumento accionTramiteDocumento = null;

            accionTramiteAcademico = service.findAccionTramiteAcademico(new AccionTramiteAcademico(tramiteNode.get("accionTramite").asLong()));
            if (accionTramiteAcademico == null) {
                accionTramiteDocumento = service.findAccionTramiteDocumento(new AccionTramiteDocumento(tramiteNode.get("accionTramiteDoc").asLong()));
            }

            if (tramiteNode.get("motivo") != null) {
                tramite.setObservacion(tramiteNode.get("motivo").asText());
            }
            tramite.setTramiteReunionConsejo(new TramiteReunionConsejo());
            if (tramiteNode.get("reunionConsejo") != null) {
                tramite.getTramiteReunionConsejo().setReunionConsejo(new ReunionConsejo(tramiteNode.get("reunionConsejo").asText()));
            }

            service.procesarTramite(tramite, accionTramiteAcademico, accionTramiteDocumento, ds);
            response.setSuccess(true);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (RuntimeException e) {
            e.printStackTrace();
            ExceptionHandler.handleSpecial(e, response, GlobalMessages.FK_ERROR_UPDATE);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @RequestMapping("{id}/successProcess")
    public String successProcess(@PathVariable Long id, RedirectAttributes redirectAttr, HttpSession session) {
        TipoTramite tipoTramite = service.findTipoTramite(id);
        Notificaciones.crearMsg("Tramite procesado correctamente.", redirectAttr);
        switch (tipoTramite.getCodigoEnum()) {
            case CERT:
            case CONS:
                return "redirect:/tramite/solicitudconstancia";

            case CORR_HISTO:
                return "redirect:/tramite/updateHistorial";
            default:
                return "redirect:/academico/tramiteacademico";
        }

    }

    @RequestMapping("cursodirigido/{id}/reporte")
    public ModelAndView cursoDirigidoReporte(Model model, HttpSession session, HttpServletResponse response, @PathVariable Long id) {

        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
        Context ctx = service.cursoDirigidoReporte(new Tramite(id), ds);
        model.addAllAttributes(ctx.getVariables());
        return new ModelAndView(pdfHtml);
    }

    @ResponseBody
    @RequestMapping("{alumno}/loadRevisarHistorialComponent")
    public JsonResponse loadRevisarHistorialComponent(@PathVariable("alumno") Long alumnoId, HttpSession session) {
        JsonResponse response = new JsonResponse();

        try {
            List<Curso> cursos = service.allCursos();

            ObjectNode data = new ObjectNode(JsonNodeFactory.instance);

            ArrayNode cursosJson = new ArrayNode(JsonNodeFactory.instance);

            cursos = cursos.subList(0, 10);
            cursos.forEach(x -> cursosJson.add(JsonHelper.createJson(x, JsonNodeFactory.instance, new String[]{"*"})));

            data.set("cursos", cursosJson);

            response.setData(data);
            response.setSuccess(Boolean.TRUE);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);

        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }

        return response;
    }

    @ResponseBody
    @RequestMapping("{alumno}/{tramite}/loadConfirmarHistorialComponent")
    public JsonResponse loadConfirmarHistorialComponent(@PathVariable("alumno") Long alumnoId, @PathVariable("tramite") Long tramiteId, HttpSession session) {
        JsonResponse response = new JsonResponse();
        try {
            List<Curso> cursos = service.allCursos();

            ObjectNode data = new ObjectNode(JsonNodeFactory.instance);

            ArrayNode cursosJson = new ArrayNode(JsonNodeFactory.instance);

            cursos = cursos.subList(0, 10);
            cursos.forEach(x -> cursosJson.add(JsonHelper.createJson(x, JsonNodeFactory.instance, new String[]{"*"})));

            data.set("cursos", cursosJson);

            response.setData(data);
            response.setSuccess(Boolean.TRUE);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);

        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }

        return response;
    }

    @ResponseBody
    @RequestMapping("asyncFindCursos")
    public JsonResponse asyncFindCursos(
            @RequestParam("nombreCurso") String nombreCurso,
            HttpSession session) {

        JsonNodeFactory jsonFactory = JsonNodeFactory.instance;
        JsonResponse response = new JsonResponse();

        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
            List<Curso> cursos = service.allCursosByName(nombreCurso, 10);
            ArrayNode jsonList = new ArrayNode(jsonFactory);

            for (Curso curso : cursos) {
                ObjectNode json = JsonHelper.createJson(curso, jsonFactory, true, new String[]{"*"});
                jsonList.add(json);
            }

            response.setData(jsonList);
            response.setTotal(jsonList.size());
            response.setSuccess(true);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("asyncFindCiclosAcad")
    public JsonResponse asyncFindCiclosAcad(
            @RequestParam("nombreCiclo") String nombreCiclo,
            @RequestParam("alumno") Long alumnoId,
            HttpSession session) {

        JsonNodeFactory jsonFactory = JsonNodeFactory.instance;
        JsonResponse response = new JsonResponse();

        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
            List<CicloAcademico> ciclos = service.allCiclosAcademicosByName(nombreCiclo, new Alumno(alumnoId));
            ArrayNode jsonList = new ArrayNode(jsonFactory);

            for (CicloAcademico ciclo : ciclos) {
                ObjectNode json = JsonHelper.createJson(ciclo, jsonFactory, true, new String[]{"*"});
                jsonList.add(json);
            }

            response.setData(jsonList);
            response.setTotal(jsonList.size());
            response.setSuccess(true);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("{idTramite}/{idAlumnoCiclo}/historial")
    public JsonResponse alumnoHistorial(@PathVariable("idTramite") Long idTramite, @PathVariable("idAlumnoCiclo") Long idAlumnoCiclo, Model model, HttpSession session) {
        JsonResponse response = new JsonResponse();
        try {
            AlumnoCiclo alumnoCiclo = service.findAlumnoCiclo(new AlumnoCiclo(idAlumnoCiclo), new Tramite(idTramite));

            ObjectNode data = new ObjectNode(JsonNodeFactory.instance);
            ObjectNode alumnoCicloJson = JsonHelper.createJson(alumnoCiclo, JsonNodeFactory.instance,
                    alumnoCicloMapper
            );
            data.set("promedios", alumnoCicloJson);
            response.setData(data);
            response.setSuccess(Boolean.TRUE);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);

        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("{idTramite}/{idAlumno}/historialAll")
    public JsonResponse alumnoHistorialAll(@PathVariable("idTramite") Long idTramite, @PathVariable("idAlumno") Long idAlumno, Model model, HttpSession session) {
        JsonResponse response = new JsonResponse();
        try {
            List<AlumnoCiclo> alumnoCiclos = service.allAlumnoCicloByAlumno(new Alumno(idAlumno), new Tramite(idTramite));

            ObjectNode data = new ObjectNode(JsonNodeFactory.instance);

            ArrayNode array = new ArrayNode(JsonNodeFactory.instance);

            for (AlumnoCiclo alumnoCiclo : alumnoCiclos) {
                ObjectNode alumnoCicloJson = JsonHelper.createJson(alumnoCiclo, JsonNodeFactory.instance,
                        alumnoCicloMapper
                );
                array.add(alumnoCicloJson);
            }

            data.set("promedios", array);
            response.setData(data);
            response.setSuccess(Boolean.TRUE);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);

        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("{idTramite}/saveRevAlumnoCiclo")
    public JsonResponse saveRevAlumnoCiclo(
            @PathVariable("idTramite") Long idTramite,
            @RequestBody AlumnoCiclo alumnoCiclo,
            HttpSession session) {

        JsonResponse response = new JsonResponse();
        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
            String message = "Save Seccion Grupo.";

            service.saveAlumnoCicloFromRevision(alumnoCiclo, idTramite, ds);

            response.setSuccess(true);
            response.setMessage(message);
        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (RuntimeException e) {
            e.printStackTrace();
            ExceptionHandler.handleSpecial(e, response, GlobalMessages.FK_ERROR_UPDATE);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("revertirCambioHistorial")
    public JsonResponse revertirCambioHistorial(
            @RequestBody AlumnoCiclo alumnoCiclo,
            HttpSession session) {

        JsonResponse response = new JsonResponse();
        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
            String message = "Se actualizó el historial.";

            service.revertirCambioHistorial(alumnoCiclo, ds);

            response.setSuccess(true);
            response.setMessage(message);
        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (RuntimeException e) {
            e.printStackTrace();
            ExceptionHandler.handleSpecial(e, response, GlobalMessages.FK_ERROR_UPDATE);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("{idTramite}/deleteCicloCurso")
    public JsonResponse deleteCicloCurso(
            @PathVariable("idTramite") Long idTramite,
            @RequestBody AlumnoCicloCurso alumnoCicloCurso,
            HttpSession session) {

        JsonResponse response = new JsonResponse();
        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
            String message = "Se actualizó el historial.";

            service.deleteCicloCurso(alumnoCicloCurso, idTramite, ds);

            response.setSuccess(true);
            response.setMessage(message);
        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (RuntimeException e) {
            e.printStackTrace();
            ExceptionHandler.handleSpecial(e, response, GlobalMessages.FK_ERROR_UPDATE);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    private List<Oficina> findOficina(List<Oficina> oficinas, DataSessionPivot ds) {
        List<Oficina> oficinasMain = oficinaService.allOficinasMainByPersona(ds.getPersona());

        for (Oficina oficina : oficinasMain) {
            log.debug("codigo oficina es {}", oficina.getCodigo());
            log.debug("tipo oficina es {} ", oficina.getTipoOficina().getCodigo());

            if (oficina.getCodigoEnum() == OficinaEnum.OERA) {
                oficinas.addAll(reunionConsejoService.allOficinaFac());
                break;
            }
            if (oficina.getTipoOficina().getCodigoEnum() == TipoOficinaEnum.FAC) {
                oficinas.add(oficina);
            }

        }
        return oficinas;
    }

    @ResponseBody
    @RequestMapping("findDocente")
    public ArrayNode findAlumno(@RequestParam("nombre") String nombre, HttpSession session) {

        List<Docente> docentes = service.allByNombre(nombre);
        return JaneHelper.from(docentes)
                .join("persona").array();

    }

}
