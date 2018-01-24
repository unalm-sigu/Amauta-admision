package pe.edu.lamolina.pivot.controller.academico.gposeccion;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.beans.PropertyEditorSupport;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpSession;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.thymeleaf.spring4.SpringTemplateEngine;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableResponse;
import pe.albatross.zelpers.miscelanea.ExceptionHandler;
import pe.albatross.zelpers.miscelanea.JsonHelper;
import pe.albatross.zelpers.miscelanea.JsonResponse;
import pe.albatross.zelpers.miscelanea.ObjectUtil;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.albatross.zelpers.notify.Notificaciones;
import pe.edu.lamolina.model.academico.AnexoBoletin;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Curso;
import pe.edu.lamolina.model.academico.Docente;
import pe.edu.lamolina.model.academico.DocenteSeccion;
import pe.edu.lamolina.model.academico.GrupoSeccion;
import pe.edu.lamolina.model.academico.Seccion;
import pe.edu.lamolina.model.enums.EstadoEnum;
import pe.edu.lamolina.model.enums.TipoGrupoHorasEnum;
import pe.edu.lamolina.model.general.Aula;
import pe.edu.lamolina.model.general.Dia;
import pe.edu.lamolina.model.general.Oficina;
import pe.edu.lamolina.model.horario.DiaHoraGrupo;
import pe.edu.lamolina.model.horario.GrupoHoras;
import pe.edu.lamolina.model.horario.Hora;
import pe.edu.lamolina.model.horario.HorarioAula;
import pe.edu.lamolina.model.horario.HorarioSeccion;
import pe.edu.lamolina.model.horario.TipoGrupoHoras;
import pe.edu.lamolina.pivot.zelper.constant.Constantine;
import pe.edu.lamolina.pivot.zelper.constant.Messages;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

@Controller
@RequestMapping("academico/gposeccion")
public class GpoSeccionController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    GpoSeccionService service;

    @Autowired
    SpringTemplateEngine springHtml;

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
        CicloAcademico ciclo = ds.getCicloAcademico();
        model.addAttribute("ciclo", ciclo);
        model.addAttribute("resumen", service.resumen());
        return "academico/gposeccion/gpoSeccion";
    }

    @ResponseBody
    @RequestMapping("list")
    public DynatableResponse allByDynatable(DynatableFilter filter, HttpSession session) {
        DynatableResponse json = new DynatableResponse();
        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);

            List<GrupoSeccion> gpoSecciones = service.allByDynatable(filter, ds.getCicloAcademico());

            ArrayNode arrayGpoSecc = new ArrayNode(JsonNodeFactory.instance);

            for (GrupoSeccion gpoSeccion : gpoSecciones) {
                ObjectNode nodeGpoSecc = new ObjectNode(JsonNodeFactory.instance);

                nodeGpoSecc.put("id", gpoSeccion.getId());
                nodeGpoSecc.put("curso", gpoSeccion.getCurso().getNombre());
                nodeGpoSecc.put("codigo", gpoSeccion.getCurso().getCodigo());
                nodeGpoSecc.put("teoria", gpoSeccion.getCurso().getHorasTeoria());
                nodeGpoSecc.put("practica", gpoSeccion.getCurso().getHorasPractica());
                nodeGpoSecc.put("creditos", gpoSeccion.getCurso().getCreditos());
                nodeGpoSecc.put("anexo", gpoSeccion.getAnexoBoletin().getNombre());
                nodeGpoSecc.put("estado", gpoSeccion.getEstado());
                nodeGpoSecc.put("estadoValue", gpoSeccion.getEstado() != null ? EstadoEnum.valueOf(gpoSeccion.getEstado()).getValue() : "");

                ArrayNode arraySecc = new ArrayNode(JsonNodeFactory.instance);
                for (Seccion seccion : gpoSeccion.getSecciones()) {
                    ObjectNode nodeSecc = new ObjectNode(JsonNodeFactory.instance);
                    nodeSecc.put("tipo", seccion.getTipoSeccion());
                    nodeSecc.put("tipoValue", seccion.getTipoSeccionEnum().getTipoSeccionEvalEnum().getValue());
                    nodeSecc.put("codigo", seccion.getCodigo());
                    nodeSecc.put("codigo2", seccion.getCodigo2());
                    nodeSecc.put("vacantes", seccion.getVacantes());
                    nodeSecc.put("matriculados", seccion.getMatriculados());
                    nodeSecc.put("aula", (String) ObjectUtil.getParentTree(seccion, "aula.codigo"));
                    nodeSecc.put("grupo", (String) ObjectUtil.getParentTree(seccion, "grupoHoras.codigo"));
                    nodeSecc.put("estadoSec", seccion.getEstado());
                    nodeSecc.put("estadoValueSec", seccion.getEstadoEnum().getValue());

                    ArrayNode arrayDoc = new ArrayNode(JsonNodeFactory.instance);
                    for (DocenteSeccion docSeccion : seccion.getDocenteSeccion()) {
                        ObjectNode nodeDoc = new ObjectNode(JsonNodeFactory.instance);
                        nodeDoc.put("principal", docSeccion.getPrincipal());
                        nodeDoc.put("codigo", docSeccion.getDocente().getCodigo());
                        nodeDoc.put("docente", (String) ObjectUtil.getParentTree(docSeccion, "docente.persona.apellidosNombres"));
                        arrayDoc.add(nodeDoc);
                    }

                    if (seccion.getDocenteSeccion().isEmpty()) {
                        ObjectNode nodeDoc = new ObjectNode(JsonNodeFactory.instance);
                        nodeDoc.put("principal", 0);
                        nodeDoc.put("codigo", "");
                        nodeDoc.put("docente", "");
                        arrayDoc.add(nodeDoc);
                    }

                    nodeSecc.set("docentes", arrayDoc);
                    arraySecc.add(nodeSecc);
                }

                nodeGpoSecc.set("secciones", arraySecc);
                arrayGpoSecc.add(nodeGpoSecc);
            }

            json.setData(arrayGpoSecc);
            json.setTotal(filter.getTotal());
            json.setFiltered(filter.getFiltered());

        } catch (Exception e) {
            e.printStackTrace();
            json.setTotal(0);
        }
        return json;
    }

    @RequestMapping("{gruposeccion}/editar")
    public String editar(@PathVariable("gruposeccion") Long gruposeccionId, Model model, HttpSession session) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);

        GrupoSeccion gpoSeccion = service.findGpoSeccion(gruposeccionId);
        ObjectNode gpoSeccionJson = JsonHelper.createJson(gpoSeccion, JsonNodeFactory.instance);

        model.addAttribute("grupoSeccion", gpoSeccion);
        model.addAttribute("cicloAcademico", ds.getCicloAcademico());
        model.addAttribute("grupoSeccionJson", gpoSeccionJson.toString());

        return "academico/gposeccion/editarGpoSeccion";
    }

    @ResponseBody
    @RequestMapping("{gruposeccion}/findSecciones")
    public JsonResponse findSecciones(@PathVariable("gruposeccion") Long gruposeccionId) {
        JsonResponse jsonResponse = new JsonResponse();
        JsonNodeFactory nc = JsonNodeFactory.instance;
        ArrayNode array = new ArrayNode(nc);
        List<Seccion> secciones = service.allSeccionesByGrupo(new GrupoSeccion(gruposeccionId));
        for (Seccion seccion : secciones) {
            ObjectNode node = new ObjectNode(nc);
            node.put("seccionId", seccion.getId());
            node.put("seccionCodigo", seccion.getCodigo());
            node.put("tipoSeccionValue", seccion.getTipoSeccionEnum().getValue());
            //       node.put("aula", ObjectUtil.getParentTree(seccion, "aula.id") != null ? seccion.getAula().getCodigo() : "");
            if (ObjectUtil.getParentTree(seccion, "aula.id") != null) {
                node.putPOJO("aula", JsonHelper.createJson(seccion.getAula(), JsonNodeFactory.instance));
            } else {
                node.put("aula", "");
            }

            node.put("grupoHoras", ObjectUtil.getParentTree(seccion, "grupoHoras.id") != null ? seccion.getGrupoHoras().getCodigo() : "");
            node.put("vacantes", seccion.getVacantes());
            node.put("matriculados", seccion.getMatriculados());
            node.put("esTipoSeccionTcur", seccion.isTipoSeccionTCUR());
            node.put("esTipoSeccionPcur", seccion.isTipoSeccionPCUR());

            node.put("cantidadDocentes", seccion.getDocentesCant());
            BigDecimal porcentajeAvance = BigDecimal.ZERO;
            for (DocenteSeccion docSeccion : seccion.getDocenteSeccion()) {
                if (docSeccion.getPorcentajeCarga() != null) {
                    porcentajeAvance = porcentajeAvance.add(docSeccion.getPorcentajeCarga());
                }
            }
            node.put("porcentajeAvance", porcentajeAvance);
            node.put("estadoEnumValue", seccion.getEstadoEnum().getValue());
            node.put("estadoEnumCode", seccion.getEstadoEnum().name());
            node.put("editVacantes", Boolean.FALSE);
            array.add(node);
        }
        jsonResponse.setSuccess(true);
        jsonResponse.setData(array);
        return jsonResponse;
    }

    @ResponseBody
    @RequestMapping("findTiposGruposHoras")
    public JsonResponse findTiposGruposHoras(HttpSession session) {
        JsonResponse jsonResponse = new JsonResponse();
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);

        ArrayNode array = new ArrayNode(JsonNodeFactory.instance);
        List<TipoGrupoHoras> tiposGrupoHoras = service.allGrupoHorasActivosTipoAndCiclo(ds.getCicloAcademico(), TipoGrupoHorasEnum.REGULAR);

        for (TipoGrupoHoras tipoGrupoHora : tiposGrupoHoras) {
            ObjectNode node = new ObjectNode(JsonNodeFactory.instance);

            node.put("tipoGrupoHoraId", tipoGrupoHora.getId());
            node.put("tipoGrupoHoraCodigo", tipoGrupoHora.getCodigo());
            node.put("tipoGrupoHoraDescripcion", tipoGrupoHora.getDescripcion());
            array.add(node);
        }
        jsonResponse.setSuccess(true);
        jsonResponse.setData(array);
        return jsonResponse;
    }

    @ResponseBody
    @RequestMapping("findDocentesSecciones")
    public JsonResponse findDocentesSecciones(
            @RequestParam("seccion") String seccionId) {
        JsonResponse jsonResponse = new JsonResponse();
        ArrayNode array = new ArrayNode(JsonNodeFactory.instance);
        List<DocenteSeccion> docentesSeccion = service.allDocentesSeccionBySeccion(new Seccion(seccionId));
        for (DocenteSeccion docSeccion : docentesSeccion) {
            ObjectNode node = new ObjectNode(JsonNodeFactory.instance);
            node.put("docSeccionId", docSeccion.getId());
            node.put("docenteId", docSeccion.getDocente().getId());
            node.put("personaApeNombres", ObjectUtil.getParentTree(docSeccion.getDocente(), "persona.id") == null ? "" : docSeccion.getDocente().getPersona().getApellidosNombres());
            node.put("docSecFechaInicio", TypesUtil.getStringDate(docSeccion.getFechaInicio(), "dd/MM/yyyy"));
            node.put("docSecFechaFin", TypesUtil.getStringDate(docSeccion.getFechaFin(), "dd/MM/yyyy"));
            node.put("estadoEnumVal", docSeccion.getEstadoEnum().getValue());
            node.put("estadoEnumCode", docSeccion.getEstadoEnum().name());
            node.put("principal", docSeccion.getPrincipal());
            node.put("porcentajeCarga", docSeccion.getPorcentajeCarga());
            node.put("docenteNN", docSeccion.getDocente().getCodigo().equals(Constantine.DOCENTE_INDETERMINADO));
            array.add(node);
        }
        jsonResponse.setSuccess(true);
        jsonResponse.setData(array);
        return jsonResponse;
    }

    @RequestMapping("nuevo")
    public String nuevo(Model model, HttpSession session) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        model.addAttribute("anexosHijos", service.allAnexoBoletionHijos());
        return "academico/gposeccion/nuevoGpoSeccion";
    }

    @ResponseBody
    @RequestMapping("buscarCursos")
    public JsonResponse buscarCursos(
            @RequestParam("nombre") String nombre,
            HttpSession session) {

        JsonNodeFactory jsonFactory = JsonNodeFactory.instance;
        JsonResponse response = new JsonResponse();

        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);

            List<Curso> cursos = service.allCursosForProgramacion(nombre);
            ArrayNode jsonList = new ArrayNode(jsonFactory);
            for (Curso cur : cursos) {
                ObjectNode json = new ObjectNode(jsonFactory);
                json.put("id", cur.getId());
                json.put("cursoNombre", cur.getCodigo());
                json.put("cursoCodigo", cur.getNombre());
                json.put("cursoTpc", cur.getTpc());
                json.put("cursoCreditos", cur.getCreditos());
                json.put("departamentoNombre", ObjectUtil.getParentTree(cur, "departamentoAcademico.nombre") != null ? cur.getDepartamentoAcademico().getNombre() : "");
                json.put("facultadNombre", ObjectUtil.getParentTree(cur, "departamentoAcademico.facultad.nombre") != null ? cur.getDepartamentoAcademico().getFacultad().getNombre() : "");
                jsonList.add(json);
            }

            response.setData(jsonList);
            response.setTotal(jsonList.size());
            response.setSuccess(true);

        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }

        return response;
    }

    @ResponseBody
    @RequestMapping("{anexo}/cambiarAnexo")
    public JsonResponse cambiarAnexo(@PathVariable("anexo") Long anexo,
            Model model, HttpSession session) {
        JsonResponse response = new JsonResponse();
        ObjectNode node = new ObjectNode(JsonNodeFactory.instance);
        AnexoBoletin anexoBoletin = service.findAnexoBoletin(anexo);
        node.put("anexoId", anexoBoletin.getId());
        node.put("anexoCodigo", anexoBoletin.getAnexoSuperior().getCodigo());
        node.put("anexoNombre", anexoBoletin.getAnexoSuperior().getNombre());
        response.setData(node);
        response.setSuccess(Boolean.TRUE);

        return response;
    }

    @ResponseBody
    @RequestMapping("{curso}/findCurso")
    public JsonResponse findCurso(@PathVariable("curso") Long anexo,
            Model model, HttpSession session) {
        JsonResponse response = new JsonResponse();
        ObjectNode node = new ObjectNode(JsonNodeFactory.instance);
        Curso curso = service.findCurso(anexo);
        node.put("cursoId", curso.getId());
        node.put("cursoNombre", curso.getNombre());
        node.put("cursoCodigo", curso.getCodigo());
        node.put("cursoTpc", curso.getTpc());
        response.setData(node);
        response.setSuccess(Boolean.TRUE);

        return response;
    }

    @ResponseBody
    @RequestMapping("saveGpoHeader")
    public JsonResponse saveGpoHeader(
            @ModelAttribute("grupoSeccion") GrupoSeccion grupoSeccion,
            RedirectAttributes redirectAttr,
            HttpSession session) {

        JsonResponse response = new JsonResponse();
        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);

            ObjectNode node = new ObjectNode(JsonNodeFactory.instance);

            String message = "Creado exitosamente.";
            grupoSeccion = service.saveGpoSeccionHeader(grupoSeccion, ds.getCicloAcademico());
            node.put("gruposeccion", grupoSeccion.getId());
            response.setData(node);
            response.setSuccess(true);
            response.setMessage(message);
        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (RuntimeException e) {
            ExceptionHandler.handleSpecial(e, response, Messages.FK_ERROR);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("addSeccion")
    public JsonResponse addSeccion(@RequestParam("grupoSeccion") Long grupoSeccion,
            Model model,
            HttpSession session) {
        JsonResponse response = new JsonResponse();
        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);

            service.addSeccion(new GrupoSeccion(grupoSeccion));

            String message = "Sección agregada.";
            response.setSuccess(true);
            response.setMessage(message);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (RuntimeException e) {
            e.printStackTrace();
            ExceptionHandler.handleSpecial(e, response, Messages.FK_ERROR);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("addDocSeccion")
    public JsonResponse addDocSeccion(@RequestParam("seccion") Long seccionId,
            Model model,
            HttpSession session) {
        JsonResponse response = new JsonResponse();
        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);

            service.addDocenteSeccion(new Seccion(seccionId));

            String message = "Docente Sección agregada.";
            response.setSuccess(true);
            response.setMessage(message);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (RuntimeException e) {
            ExceptionHandler.handleSpecial(e, response, Messages.FK_ERROR);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("deleteSeccion")
    public JsonResponse deleteSeccion(@RequestParam("seccion") Long seccionId,
            Model model,
            HttpSession session) {
        JsonResponse response = new JsonResponse();
        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
            service.deleteSeccion(new Seccion(seccionId));
            String message = "Sección eliminada.";
            response.setSuccess(true);
            response.setMessage(message);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (RuntimeException e) {
            ExceptionHandler.handleSpecial(e, response, Messages.FK_ERROR);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("deleteDocSeccion")
    public JsonResponse deleteDocSeccion(@RequestParam("docSeccion") Long docSeccionId,
            Model model,
            HttpSession session) {
        JsonResponse response = new JsonResponse();
        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);

            service.deleteDocSeccion(new DocenteSeccion(docSeccionId));
            logger.debug("Docente Seccion {}", docSeccionId);

            String message = "Docente eliminado.";
            response.setSuccess(true);
            response.setMessage(message);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (RuntimeException e) {
            ExceptionHandler.handleSpecial(e, response, Messages.FK_ERROR);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @RequestMapping("{gruposeccion}/succesSave")
    public String succesSave(@PathVariable("gruposeccion") Long grupoSeccionId,
            RedirectAttributes redirectAttr,
            Model model, HttpSession session) {
        Notificaciones.crearMsg(Messages.CREATED, redirectAttr);
        return "redirect:/academico/gposeccion/" + grupoSeccionId + "/editar";
    }

    @ResponseBody
    @RequestMapping("buscarDocentes")
    public JsonResponse buscarDocentes(
            @RequestParam("nombre") String nombre,
            HttpSession session) {

        JsonNodeFactory jsonFactory = JsonNodeFactory.instance;
        JsonResponse response = new JsonResponse();

        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);

            ArrayNode jsonList = new ArrayNode(jsonFactory);

            List<Docente> docentes = service.allDocenterByNombre(nombre);

            for (Docente doc : docentes) {
                ObjectNode json = new ObjectNode(jsonFactory);
                json.put("id", doc.getId());
                json.put("apellidosNombres", StringUtils.isBlank(doc.getPersona().getApellidosNombres()) ? "" : doc.getPersona().getApellidosNombres());
                json.put("personaNombre", doc.getPersona().getNombres());
                json.put("personaPaterno", doc.getPersona().getPaterno());
                json.put("personaMaterno", doc.getPersona().getMaterno());
                jsonList.add(json);
            }

            response.setData(jsonList);
            response.setTotal(jsonList.size());
            response.setSuccess(true);

        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }

        return response;
    }

    @ResponseBody
    @RequestMapping("asyncFindAulas")
    public JsonResponse asyncFindAulas(
            @RequestParam("nombre") String nombre,
            HttpSession session) {

        JsonNodeFactory jsonFactory = JsonNodeFactory.instance;
        JsonResponse response = new JsonResponse();

        try {
            List<Aula> aulas = service.searchAulaByName(nombre);
            ArrayNode jsonList = new ArrayNode(jsonFactory);

            for (Aula aula : aulas) {
                ObjectNode json = JsonHelper.createJson(aula, jsonFactory);
                json.put("esEspecifica", Boolean.TRUE);
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
    @RequestMapping("cambiarDocPrincipal")
    public JsonResponse cambiarDocPrincipal(
            @RequestParam("docSeccion") Long docSeccion,
            HttpSession session) {
        JsonNodeFactory jsonFactory = JsonNodeFactory.instance;
        JsonResponse response = new JsonResponse();
        try {
            service.cambiarDocentePrincipal(new DocenteSeccion(docSeccion));
            response.setSuccess(Boolean.TRUE);
            response.setMessage("Docente principal actualizado");
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("cambiarDocenteSeccion")
    public JsonResponse cambiarDocPrincipal(
            @RequestParam("docSeccion") Long docSeccion,
            @RequestParam("docente") Long docente,
            HttpSession session) {
        JsonNodeFactory jsonFactory = JsonNodeFactory.instance;
        JsonResponse response = new JsonResponse();
        try {
            service.actualizarDocente(docSeccion, docente);
            response.setSuccess(Boolean.TRUE);
            response.setMessage("Docente actualizado");
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("cambiarVacantesSeccion")
    public JsonResponse cambiarVacantesSeccion(
            @RequestParam("seccion") Long seccionId,
            @RequestParam("vacantes") Integer vacantes,
            HttpSession session) {
        JsonNodeFactory jsonFactory = JsonNodeFactory.instance;
        JsonResponse response = new JsonResponse();
        try {
            logger.debug("seccion {}, vacantes {}", seccionId, vacantes);

            Seccion seccion = new Seccion(seccionId);
            seccion.setVacantes(vacantes);
            service.actualizarSeccionVacantes(seccion);

            response.setSuccess(Boolean.TRUE);
            response.setMessage("Vacantes actualizadas");
        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (RuntimeException e) {
            ExceptionHandler.handleSpecial(e, response, Messages.FK_ERROR);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("cambiarPorcentajeAvance")
    public JsonResponse cambiarPorcentajeAvance(
            @RequestParam("docSeccion") Long docSeccion,
            @RequestParam("porcentajeAvance") BigDecimal porcentajeAvance,
            HttpSession session) {
        JsonNodeFactory jsonFactory = JsonNodeFactory.instance;
        JsonResponse response = new JsonResponse();
        try {
            DocenteSeccion docenteSeccion = new DocenteSeccion(docSeccion);
            docenteSeccion.setPorcentajeCarga(porcentajeAvance);
            service.updatePorcentajeAvance(docenteSeccion);
            response.setSuccess(Boolean.TRUE);
            response.setMessage("Porcentaje de avance actualizado");
        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (RuntimeException e) {
            ExceptionHandler.handleSpecial(e, response, Messages.FK_ERROR);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("findSeccion")
    public JsonResponse findSeccion(
            @RequestParam("seccion") Long seccionId,
            HttpSession session) {
        JsonNodeFactory jsonFactory = JsonNodeFactory.instance;
        JsonResponse response = new JsonResponse();
        try {
            Seccion seccion = service.findSeccion(seccionId);
            response.setData(seccion.toJson());
            response.setSuccess(Boolean.TRUE);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("loadModalGrupo")
    public JsonResponse loadModalGrupo(@RequestParam("seccion") Long seccionId,
            Model model,
            HttpSession session) {
        JsonNodeFactory jsonFactory = JsonNodeFactory.instance;
        JsonResponse response = new JsonResponse();

        try {
            Seccion seccion = service.findSeccion(seccionId);
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
            CicloAcademico cicloAcademico = ds.getCicloAcademico();

            ObjectNode node = new ObjectNode(jsonFactory);

            node.putPOJO("seccion", seccion.toJson());
            //    node.put("grupoHorarioSel", "");
            node.put("tipoGrupoHorasSeleccionado", "");
            if (ObjectUtil.getParentTree(seccion, "grupoHoras.id") != null) {
                GrupoHoras grupoHoras = service.findGrupoHoras(seccion.getGrupoHoras());

                ObjectNode grupoHorasNode = grupoHoras.toJson();

                if (grupoHoras.getTipoGrupoHoras().isTipoGrupoRegular()) {
                    grupoHorasNode.put("esTipoGrupoRegular", true);
                } else if (grupoHoras.getTipoGrupoHoras().isTipoGrupoZeta()) {
                    grupoHorasNode.put("esTipoGrupoZeta", true);
                    grupoHorasNode.put("esTipoGrupoCodZeta", grupoHoras.getTipoGrupoHoras().isCodigoZeta());
                    grupoHorasNode.put("esTipoGrupoCodZetaAsterisk", grupoHoras.getTipoGrupoHoras().isCodigoZetaAsterisk());
                } else if (grupoHoras.getTipoGrupoHoras().isTipoGrupoEspecial()) {
                    grupoHorasNode.put("isTipoGrupoEspecial", true);
                }
                node.putPOJO("grupoHorarioSel", grupoHorasNode);
            }

            List<TipoGrupoHoras> tiposGrupoHoras = service.allGrupoHorasActivosTipoAndCiclo(cicloAcademico, TipoGrupoHorasEnum.REGULAR);

            ArrayNode tiposGrupoaHoras = new ArrayNode(jsonFactory);
            for (TipoGrupoHoras tiposGrupoHoraEach : tiposGrupoHoras) {
                ObjectNode tipoGrupoHorasNode = JsonHelper.createJson(tiposGrupoHoraEach, jsonFactory);
                tipoGrupoHorasNode.put("texto", tiposGrupoHoraEach.getCodigo() + " - " + tiposGrupoHoraEach.getDescripcion());
                tiposGrupoaHoras.addPOJO(tipoGrupoHorasNode);
            }
            node.set("tiposGruposHorasOpt", tiposGrupoaHoras);

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
    @RequestMapping("loadModalAula")
    public JsonResponse loadModalAula(
            @RequestParam("seccion") Long seccionId,
            HttpSession session,
            Model model) {
        JsonNodeFactory jsonFactory = JsonNodeFactory.instance;
        JsonResponse response = new JsonResponse();
        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
            Seccion seccion = service.findSeccion(seccionId);
            List<Aula> modulosOera = service.allAulasSuperiorByOficina(new Oficina(Constantine.ID_OFICINA_OERA));
            //       List<Oficina> oficinasDisponibless = service.allOficinasWithAula(ds.getOficinas());
            List<Aula> oficinasAulasSuperiores = service.allAulaSuperiorByOficinasWithAula(ds.getOficinas());

            ArrayNode modulosOeraJson = new ArrayNode(jsonFactory);
            for (Aula aulaEach : modulosOera) {
                modulosOeraJson.add(JsonHelper.createJson(aulaEach, jsonFactory));
            }

            ArrayNode oficinasAulasSuperioresJson = new ArrayNode(jsonFactory);
            for (Aula aulaEach : oficinasAulasSuperiores) {
                oficinasAulasSuperioresJson.add(JsonHelper.createJson(aulaEach, jsonFactory));
            }

            ObjectNode nodeResult = new ObjectNode(jsonFactory);
            nodeResult.putPOJO("seccion", seccion.toJson());
            //combo
            nodeResult.set("modulosOera", modulosOeraJson);
            //combo
            nodeResult.set("oficinasDisponibles", oficinasAulasSuperioresJson);

            //  nodeResult.put("modulosOeraSel", "");
            //   nodeResult.put("oficinaSel", "");
            //  nodeResult.put("aulaSel", "");
            if (ObjectUtil.getParentTree(seccion, "aula.id") != null) {
                Aula aula = service.findAula(seccion.getAula().getId());
                ObjectNode aulaNode = JsonHelper.createJson(aula, jsonFactory);

                if (aula.getOficinaSupervisora().getId().equals(Constantine.ID_OFICINA_OERA)) {
                    //OERA
                    Aula moduloOera = modulosOera.stream().filter(req -> req.getId().equals(aula.getAulaSuperior().getId())).findFirst().orElse(null);
                    aulaNode.put("esOera", Boolean.TRUE);
                    //  aulaNode.put("seleccionado", Boolean.TRUE);
                    nodeResult.putPOJO("modulosOeraSel", JsonHelper.createJson(moduloOera, jsonFactory));
                } else if (oficinasAulasSuperiores.contains(aula.getAulaSuperior())) {
                    //oficinas
                    aulaNode.put("esOficina", Boolean.TRUE);
                    nodeResult.putPOJO("oficinaSel", JsonHelper.createJson(aula.getAulaSuperior(), jsonFactory));
                } else {
                    //especificos
                    aulaNode.put("esEspecifica", Boolean.TRUE);
                }
                nodeResult.putPOJO("aulaSel", aulaNode);
            }

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
    @RequestMapping("horario")
    public JsonResponse horario(@RequestParam(name = "tipoGrupoHorasId", required = false) Long tipoGrupoHorasId,
            @RequestParam(name = "seccionId", required = false) Long seccionId, Model model, HttpSession session) {
        JsonResponse response = new JsonResponse();
        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
            CicloAcademico cicloAcademico = ds.getCicloAcademico();

            JsonNodeFactory factory = JsonNodeFactory.instance;

            List<Dia> dias = service.allDia();
            Seccion seccion = service.findSeccion(seccionId);
            TipoGrupoHoras tipoGrupoDb = service.findTipoGrupoHoras(tipoGrupoHorasId);
            List<GrupoHoras> gruposHoras = service.allGrupoHorasBySeccionAndTipoGrupoHoras(seccion, tipoGrupoDb, cicloAcademico);
            List<HorarioAula> horariosAulas = null;
            if (ObjectUtil.getParentTree(seccion, "aula.id") != null) {
                horariosAulas = service.allHorariosAula(seccion.getAula(), ds.getCicloAcademico());
            }

            List<Hora> horasEncontradas = new ArrayList<>();
            List<DiaHoraGrupo> diaHoraGrupos = new ArrayList();

            for (GrupoHoras grupoHoraEach : gruposHoras) {
                for (DiaHoraGrupo diaHoraGrupo : grupoHoraEach.getDiaHoraGrupo()) {
                    diaHoraGrupos.add(diaHoraGrupo);
                    Hora horaFound = horasEncontradas.stream().filter(req -> req.getId().equals(diaHoraGrupo.getHora().getId())).findFirst().orElse(null);
                    if (horaFound == null) {
                        horasEncontradas.add(diaHoraGrupo.getHora());
                    }
                }
            }
            Collections.sort(horasEncontradas, (p1, p2) -> p1.getNumero().compareTo(p2.getNumero()));

            ObjectNode jsonDiaHoraGrupo = new ObjectNode(factory);
            for (DiaHoraGrupo diaHoraGrupo : diaHoraGrupos) {
                Long diaId = diaHoraGrupo.getDia().getId();
                Long horaId = diaHoraGrupo.getHora().getId();

                ObjectNode jsonDiaHoraGrupoEach = JsonHelper.createJson(diaHoraGrupo, factory);
                ObjectNode grupoHorarioJson = JsonHelper.createJson(diaHoraGrupo.getGrupoHorario(), factory);

                if (diaHoraGrupo.getGrupoHorario().getTipoGrupoHoras().isTipoGrupoRegular()) {
                    grupoHorarioJson.put("esTipoGrupoRegular", Boolean.TRUE);
                } else if (diaHoraGrupo.getGrupoHorario().getTipoGrupoHoras().isTipoGrupoZeta()) {
                    grupoHorarioJson.put("esTipoGrupoZeta", Boolean.TRUE);
                }

                jsonDiaHoraGrupoEach.putPOJO("grupoHorario", grupoHorarioJson);
                jsonDiaHoraGrupoEach.put("seleccionado", Boolean.FALSE);
                jsonDiaHoraGrupoEach.putPOJO("dia", JsonHelper.createJson(diaHoraGrupo.getDia(), factory));
                jsonDiaHoraGrupoEach.putPOJO("hora", JsonHelper.createJson(diaHoraGrupo.getHora(), factory));

                if (ObjectUtil.getParentTree(seccion, "grupoHoras.id") != null) {
                    if (seccion.getGrupoHoras().getId().equals(diaHoraGrupo.getGrupoHorario().getId())) {
                        jsonDiaHoraGrupoEach.put("seleccionado", Boolean.TRUE);
                    }
                }
                jsonDiaHoraGrupo.putPOJO(diaId + "_" + horaId, jsonDiaHoraGrupoEach);
            }

            ObjectNode jsonHorarioAula = new ObjectNode(factory);
            if (horariosAulas != null) {
                for (HorarioAula horarioAulaEach : horariosAulas) {
                    Long diaId = horarioAulaEach.getDia().getId();
                    Long horaId = horarioAulaEach.getHora().getId();
                    if (!horarioAulaEach.getSeccion().getId().equals(seccion.getId())) {
                        jsonHorarioAula.putPOJO(diaId + "_" + horaId, horarioAulaEach.toJson());
                    }
                }
            }

            ObjectNode data = new ObjectNode(factory);

            ArrayNode diasJson = new ArrayNode(factory);
            for (Dia dia : dias) {
                diasJson.add(JsonHelper.createJson(dia, factory));
            }
            ArrayNode horasJson = new ArrayNode(factory);
            for (Hora horasEncontrada : horasEncontradas) {
                horasJson.add(JsonHelper.createJson(horasEncontrada, factory));
            }

            data.set("dias", diasJson);
            data.set("horas", horasJson);
            data.set("jsonDiaHoraGrupo", jsonDiaHoraGrupo);
            data.set("jsonHorarioAula", jsonHorarioAula);

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
    @RequestMapping("horariosEspeciales")
    public JsonResponse horariosEspeciales(@RequestParam(name = "grupoHorario", required = false) Long grupoHorarioId,
            @RequestParam(name = "seccion", required = false) Long seccionId, Model model, HttpSession session) {
        JsonResponse response = new JsonResponse();
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        try {
            JsonNodeFactory factory = JsonNodeFactory.instance;

            GrupoHoras grupoHoras = service.findGrupoHoras(new GrupoHoras(grupoHorarioId));
            Seccion seccion = service.findSeccion(seccionId);
            List<Dia> dias = service.allDia();

            List<HorarioAula> horariosAulas = null;
            if (ObjectUtil.getParentTree(seccion, "aula.id") != null) {
                horariosAulas = service.allHorariosAula(seccion.getAula(), ds.getCicloAcademico());
            }

            List<Hora> horasEncontradas = new ArrayList<>();

            for (DiaHoraGrupo diaHoraGrupo : grupoHoras.getDiaHoraGrupo()) {
                Hora horaFound = horasEncontradas.stream().filter(req -> req.getId().equals(diaHoraGrupo.getHora().getId())).findFirst().orElse(null);
                if (horaFound == null) {
                    horasEncontradas.add(diaHoraGrupo.getHora());
                }
            }
            Collections.sort(horasEncontradas, (p1, p2) -> p1.getNumero().compareTo(p2.getNumero()));

            ObjectNode jsonDiaHoraGrupo = new ObjectNode(factory);
            for (DiaHoraGrupo diaHoraGrupoEach : grupoHoras.getDiaHoraGrupo()) {
                Long diaId = diaHoraGrupoEach.getDia().getId();
                Long horaId = diaHoraGrupoEach.getHora().getId();

                ObjectNode jsonDiaHoraGrupoEach = JsonHelper.createJson(diaHoraGrupoEach, factory);
                ObjectNode grupoHorarioJson = JsonHelper.createJson(diaHoraGrupoEach.getGrupoHorario(), factory);

                if (diaHoraGrupoEach.getGrupoHorario().getTipoGrupoHoras().isTipoGrupoRegular()) {
                    grupoHorarioJson.put("esTipoGrupoRegular", Boolean.TRUE);
                } else if (diaHoraGrupoEach.getGrupoHorario().getTipoGrupoHoras().isTipoGrupoZeta()) {
                    grupoHorarioJson.put("esTipoGrupoZeta", Boolean.TRUE);
                } else if (diaHoraGrupoEach.getGrupoHorario().getTipoGrupoHoras().isTipoGrupoEspecial()) {
                    grupoHorarioJson.put("esTipoGrupoEspecial", Boolean.TRUE);
                }

                jsonDiaHoraGrupoEach.putPOJO("grupoHorario", grupoHorarioJson);
                jsonDiaHoraGrupoEach.put("seleccionado", Boolean.FALSE);
                jsonDiaHoraGrupoEach.putPOJO("dia", JsonHelper.createJson(diaHoraGrupoEach.getDia(), factory));
                jsonDiaHoraGrupoEach.putPOJO("hora", JsonHelper.createJson(diaHoraGrupoEach.getHora(), factory));

                if (ObjectUtil.getParentTree(seccion, "grupoHoras.id") != null) {
                    if (seccion.getGrupoHoras().getId().compareTo(grupoHoras.getId()) == 0) {
                        if (seccion.getHorarioSeccion() != null && !seccion.getHorarioSeccion().isEmpty()) {
                            for (HorarioSeccion horarioSeccionEach : seccion.getHorarioSeccion()) {
                                if (horarioSeccionEach.getDia().getId().compareTo(diaId) == 0
                                        && horarioSeccionEach.getHora().getId().compareTo(horaId) == 0) {
                                    jsonDiaHoraGrupoEach.put("seleccionado", Boolean.TRUE);
                                }
                            }
                        }
                    }
                }
                jsonDiaHoraGrupo.putPOJO(diaId + "_" + horaId, jsonDiaHoraGrupoEach);
            }

            ObjectNode jsonHorarioAula = new ObjectNode(factory);
            if (horariosAulas != null) {
                for (HorarioAula horarioAulaEach : horariosAulas) {
                    Long diaId = horarioAulaEach.getDia().getId();
                    Long horaId = horarioAulaEach.getHora().getId();
                    if (!horarioAulaEach.getSeccion().getId().equals(seccion.getId())) {
                        jsonHorarioAula.putPOJO(diaId + "_" + horaId, horarioAulaEach.toJson());
                    }
                }
            }

            ObjectNode data = new ObjectNode(factory);

            ArrayNode diasJson = new ArrayNode(factory);
            for (Dia dia : dias) {
                diasJson.add(JsonHelper.createJson(dia, factory));
            }
            ArrayNode horasJson = new ArrayNode(factory);
            for (Hora horasEncontrada : horasEncontradas) {
                horasJson.add(JsonHelper.createJson(horasEncontrada, factory));
            }

            data.set("dias", diasJson);
            data.set("horas", horasJson);
            data.set("jsonDiaHoraGrupo", jsonDiaHoraGrupo);
            data.set("jsonHorarioAula", jsonHorarioAula);

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
    @RequestMapping("horariosZeta")
    public JsonResponse horariosZeta(@RequestParam(name = "grupoHorario", required = false) Long grupoHorarioId,
            @RequestParam(name = "seccion", required = false) Long seccionId, Model model, HttpSession session) {

        JsonResponse response = new JsonResponse();
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        try {

            JsonNodeFactory factory = JsonNodeFactory.instance;

            GrupoHoras grupoHoras = service.findGrupoHoras(new GrupoHoras(grupoHorarioId));
            Seccion seccion = service.findSeccion(seccionId);

            List<Dia> dias = service.allDia();
            List<Hora> horasEncontradas = service.allHora();

            List<HorarioAula> horariosAulas = null;
            if (ObjectUtil.getParentTree(seccion, "aula.id") != null) {
                horariosAulas = service.allHorariosAula(seccion.getAula(), ds.getCicloAcademico());
            }

            ObjectNode jsonDiaHoraGrupo = new ObjectNode(factory);

            for (Dia diaEach : dias) {
                for (Hora horaEach : horasEncontradas) {
                    Long diaId = diaEach.getId();
                    Long horaId = horaEach.getId();

                    ObjectNode jsonDiaHoraGrupoEach = new ObjectNode(factory);
                    jsonDiaHoraGrupoEach.putPOJO("dia", JsonHelper.createJson(diaEach, factory));
                    jsonDiaHoraGrupoEach.putPOJO("hora", JsonHelper.createJson(horaEach, factory));
                    jsonDiaHoraGrupoEach.put("seleccionado", Boolean.FALSE);

                    ObjectNode jspnGrupoHoras = JsonHelper.createJson(grupoHoras, factory);
                    if (grupoHoras.getTipoGrupoHoras().isTipoGrupoRegular()) {
                        jspnGrupoHoras.put("esTipoGrupoRegular", Boolean.TRUE);
                    } else if (grupoHoras.getTipoGrupoHoras().isTipoGrupoZeta()) {
                        jspnGrupoHoras.put("esTipoGrupoZeta", Boolean.TRUE);
                    } else if (grupoHoras.getTipoGrupoHoras().isTipoGrupoEspecial()) {
                        jspnGrupoHoras.put("esTipoGrupoEspecial", Boolean.TRUE);
                    }

                    jsonDiaHoraGrupoEach.putPOJO("grupoHorario", jspnGrupoHoras);
                    if (seccion.getHorarioSeccion() != null && !seccion.getHorarioSeccion().isEmpty()) {
                        if (ObjectUtil.getParentTree(seccion, "grupoHoras.id") != null) {
                            if (seccion.getGrupoHoras().getId().compareTo(grupoHoras.getId()) == 0) {
                                for (HorarioSeccion horarioSeccionEach : seccion.getHorarioSeccion()) {
                                    if (horarioSeccionEach.getDia().getId().compareTo(diaEach.getId()) == 0
                                            && horarioSeccionEach.getHora().getId().compareTo(horaEach.getId()) == 0) {
                                        jsonDiaHoraGrupoEach.put("seleccionado", Boolean.TRUE);
                                    }
                                }
                            }
                        }
                    }

                    jsonDiaHoraGrupo.putPOJO(diaId + "_" + horaId, jsonDiaHoraGrupoEach);
                }
            }
            ObjectNode jsonHorarioAula = new ObjectNode(factory);
            if (horariosAulas != null) {
                for (HorarioAula horarioAulaEach : horariosAulas) {
                    Long diaId = horarioAulaEach.getDia().getId();
                    Long horaId = horarioAulaEach.getHora().getId();
                    if (!horarioAulaEach.getSeccion().getId().equals(seccion.getId())) {
                        jsonHorarioAula.putPOJO(diaId + "_" + horaId, horarioAulaEach.toJson());
                    }
                }
            }

            ObjectNode data = new ObjectNode(factory);

            ArrayNode diasJson = new ArrayNode(factory);
            for (Dia dia : dias) {
                diasJson.add(JsonHelper.createJson(dia, factory));
            }
            ArrayNode horasJson = new ArrayNode(factory);
            for (Hora horasEncontrada : horasEncontradas) {
                horasJson.add(JsonHelper.createJson(horasEncontrada, factory));
            }

            data.set("dias", diasJson);
            data.set("horas", horasJson);
            data.set("jsonDiaHoraGrupo", jsonDiaHoraGrupo);
            data.set("jsonHorarioAula", jsonHorarioAula);

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
    @RequestMapping("listGrupoHorariosZetas")
    public DynatableResponse listGrupoHorariosZetas(pe.albatross.octavia.dynatable.DynatableFilter filter,
            HttpSession session) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        DynatableResponse json = new DynatableResponse();

        try {
            TipoGrupoHoras tipoGrupoHoras = service.findTipoGrupoHoraByTipo(TipoGrupoHorasEnum.ZETA);
            List<GrupoHoras> gruposHoras = service.allGrupoHorasZetasDyna(filter, tipoGrupoHoras, ds.getCicloAcademico());

            List<DiaHoraGrupo> horas = service.allDiaHoraGrupo(gruposHoras);
            Map<Long, List<DiaHoraGrupo>> mapGrupohoras = TypesUtil.convertListToMapList("grupoHorario.id", horas);

            JsonNodeFactory nf = JsonNodeFactory.instance;
            ArrayNode array = new ArrayNode(nf);
            for (GrupoHoras grupoHoraEach : gruposHoras) {
                ObjectNode node = new ObjectNode(JsonNodeFactory.instance);
                node.put("id", grupoHoraEach.getId());
                node.put("codigo", grupoHoraEach.getCodigo());
                node.put("letra", grupoHoraEach.getLetra());
                node.put("tipoCiclo", grupoHoraEach.getTipoCiclo());
                node.put("tipoGrupoHoras", grupoHoraEach.getTipoGrupoHoras() != null ? grupoHoraEach.getTipoGrupoHoras().getCodigo() : "");
                node.put("tipoSeccion", grupoHoraEach.getTipoSeccion());
                node.put("color", grupoHoraEach.getColor());
                List<DiaHoraGrupo> mapGrupohora = mapGrupohoras.get(grupoHoraEach.getId());
                node.put("horas", 0);
                if (mapGrupohora != null) {
                    node.put("horas", mapGrupohora.size());
                }
                array.add(node);
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
    @RequestMapping("listGrupoHorariosByTipoEspecial")
    public DynatableResponse listGrupoHorariosByTipoEspecial(pe.albatross.octavia.dynatable.DynatableFilter filter,
            @RequestParam(name = "tipoGrupoHora", required = false) String tipoGrupoHora,
            HttpSession session) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        DynatableResponse json = new DynatableResponse();

        try {
            JsonNodeFactory nf = JsonNodeFactory.instance;
            ArrayNode array = new ArrayNode(nf);

            //    Seccion seccion = new Seccion(TypesUtil.getLong(filter.getQueries().get("seccion")));
            TipoGrupoHoras tipoGrupoHoras = service.findTipoGrupoHoraByTipoAndCiclo(TipoGrupoHorasEnum.valueOf(tipoGrupoHora), ds.getCicloAcademico());
            List<GrupoHoras> gruposHoras = service.allGrupoHoraByTipoGrupoHoraDyna(filter, tipoGrupoHoras, ds.getCicloAcademico(), null);

            List<DiaHoraGrupo> horas = service.allDiaHoraGrupo(gruposHoras);
            Map<Long, List<DiaHoraGrupo>> mapGrupohoras = TypesUtil.convertListToMapList("grupoHorario.id", horas);

            array = new ArrayNode(nf);
            for (GrupoHoras grupoHoraEach : gruposHoras) {
                ObjectNode node = new ObjectNode(JsonNodeFactory.instance);
                node.put("id", grupoHoraEach.getId());
                node.put("codigo", grupoHoraEach.getCodigo());
                node.put("letra", grupoHoraEach.getLetra());
                node.put("tipoCiclo", grupoHoraEach.getTipoCiclo());
                node.put("tipoGrupoHoras", grupoHoraEach.getTipoGrupoHoras() != null ? grupoHoraEach.getTipoGrupoHoras().getCodigo() : "");
                node.put("tipoSeccion", grupoHoraEach.getTipoSeccion());
                node.put("color", grupoHoraEach.getColor());
                List<DiaHoraGrupo> mapGrupohora = mapGrupohoras.get(grupoHoraEach.getId());
                node.put("horas", 0);
                if (mapGrupohora != null) {
                    node.put("horas", mapGrupohora.size());
                }
                array.add(node);
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

    /*
    @ResponseBody
    @RequestMapping("listGrupoHorariosByTipoZeta")
    public DynatableResponse listGrupoHorariosByTipoZeta(pe.albatross.octavia.dynatable.DynatableFilter filter,
            @RequestParam(name = "tipoGrupoHora", required = false) String tipoGrupoHora,
            HttpSession session) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        DynatableResponse json = new DynatableResponse();

        try {
            TipoGrupoHoras tipoGrupoHoras = service.findTipoGrupoHoraByTipoAndCiclo(TipoGrupoHorasEnum.valueOf(tipoGrupoHora), ds.getCicloAcademico());
            List<GrupoHoras> gruposHoras = service.allGrupoHoraByTipoGrupoHoraDyna(filter, tipoGrupoHoras, ds.getCicloAcademico());

            List<DiaHoraGrupo> horas = service.allDiaHoraGrupo(gruposHoras);
            Map<Long, List<DiaHoraGrupo>> mapGrupohoras = TypesUtil.convertListToMapList("grupoHorario.id", horas);

            JsonNodeFactory nf = JsonNodeFactory.instance;
            ArrayNode array = new ArrayNode(nf);
            for (GrupoHoras grupoHoraEach : gruposHoras) {
                ObjectNode node = new ObjectNode(JsonNodeFactory.instance);
                node.put("id", grupoHoraEach.getId());
                node.put("codigo", grupoHoraEach.getCodigo());
                node.put("letra", grupoHoraEach.getLetra());
                node.put("tipoCiclo", grupoHoraEach.getTipoCiclo());
                node.put("tipoGrupoHoras", grupoHoraEach.getTipoGrupoHoras() != null ? grupoHoraEach.getTipoGrupoHoras().getCodigo() : "");
                node.put("tipoSeccion", grupoHoraEach.getTipoSeccion());
                node.put("color", grupoHoraEach.getColor());
                List<DiaHoraGrupo> mapGrupohora = mapGrupohoras.get(grupoHoraEach.getId());
                node.put("horas", 0);
                if (mapGrupohora != null) {
                    node.put("horas", mapGrupohora.size());
                }
                array.add(node);
            }

            json.setData(array);
            json.setTotal(filter.getTotal());
            json.setFiltered(filter.getFiltered());

        } catch (Exception e) {
            e.printStackTrace();
            json.setTotal(0);
        }

        return json;
    }*/
    @ResponseBody
    @RequestMapping("{seccion}/saveSeccionGrupo")
    public JsonResponse saveSeccionGrupo(
            @PathVariable("seccion") Long seccionId,
            @RequestBody List<DiaHoraGrupo> diasHorasGrupo,
            RedirectAttributes redirectAttr,
            HttpSession session) {

        JsonResponse response = new JsonResponse();
        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);

            ObjectNode node = new ObjectNode(JsonNodeFactory.instance);
            String message = "Grupo hora asignado correctamente.";
            service.saveSeccionGrupoHorario(seccionId, diasHorasGrupo, ds.getCicloAcademico());

            response.setSuccess(true);
            response.setMessage(message);
        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (RuntimeException e) {
            e.printStackTrace();
            ExceptionHandler.handleSpecial(e, response, Messages.FK_ERROR);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("saveAula")
    public JsonResponse saveAula(
            @RequestParam(name = "seccion", required = false) Long seccionId,
            @RequestParam(name = "aula", required = false) Long aulaId,
            RedirectAttributes redirectAttr,
            HttpSession session) {

        JsonResponse response = new JsonResponse();
        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);

            ObjectNode node = new ObjectNode(JsonNodeFactory.instance);

            String message = "Aula asignado correctamente.";
            service.saveAula(seccionId, aulaId, ds.getCicloAcademico());

            response.setSuccess(true);
            response.setMessage(message);
        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (RuntimeException e) {
            ExceptionHandler.handleSpecial(e, response, Messages.FK_ERROR);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("aulas")
    public JsonResponse aulas(
            @RequestParam(name = "seccion", required = false) Long seccionId,
            @RequestParam(name = "aula", required = false) Long aulaId,
            RedirectAttributes redirectAttr,
            HttpSession session) {

        JsonResponse response = new JsonResponse();
        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
            JsonNodeFactory nc = JsonNodeFactory.instance;
            ObjectNode nodeData = new ObjectNode(nc);
            Seccion seccion = service.findSeccion(seccionId);
            Aula aula = null;

            List<Aula> oficinasAulasSuperiores = service.allAulaSuperiorByOficinasWithAula(ds.getOficinas());

            if (ObjectUtil.getParentTree(seccion, "aula.id") != null) {
                aula = seccion.getAula();
                /*
                ObjectNode aulaNode = JsonHelper.createJson(aula, nc);
                if (aula.getOficinaSupervisora().getId().equals(Constantine.ID_OFICINA_OERA)) {
                    aulaNode.put("esOera", Boolean.TRUE);
                } else if (oficinasAulasSuperiores.contains(aula.getAulaSuperior())) {
                    aulaNode.put("esOficina", Boolean.TRUE);
                } else {
                    aulaNode.put("esEspecifico", Boolean.TRUE);
                }
                nodeData.putPOJO("aulaSel", aulaNode);
                 */
            }

            List<Aula> aulas = service.allAulasBySuperior(seccion, new Aula(aulaId), ds.getCicloAcademico());
            ArrayNode argAulas = new ArrayNode(nc);
            for (Aula aulaEach : aulas) {
                logger.debug("aula {}, disponible {}, secciones {}", aulaEach.getId(), aulaEach.isDisponible(), aulaEach.getSeccion() == null ? 0 : aulaEach.getSeccion().size());
                ObjectNode aulaJson = JsonHelper.createJson(aulaEach, nc);
                aulaJson.put("seleccionado", Boolean.FALSE);

                if (aulaEach.getOficinaSupervisora().getId().equals(Constantine.ID_OFICINA_OERA)) {
                    aulaJson.put("esOera", Boolean.TRUE);
                } else if (oficinasAulasSuperiores.contains(aulaEach.getAulaSuperior())) {
                    aulaJson.put("esOficina", Boolean.TRUE);
                } else {
                    aulaJson.put("esEspecifica", Boolean.TRUE);
                }

                if (aula != null && aula.getId().equals(aulaEach.getId())) {
                    aulaJson.put("seleccionado", Boolean.TRUE);
                    nodeData.putPOJO("aulaSel", aulaJson);
                }
                argAulas.add(aulaJson);
            }
            /*
            List<HorarioAula> horariosAulas = service.allHorarioAulaByAulaCiclo(new Aula(aulaId), new Seccion(seccionId),
                    ds.getCicloAcademico());*/
            nodeData.set("aulas", argAulas);
            response.setData(nodeData);
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

    @ResponseBody
    @RequestMapping("seleccionarAula")
    public JsonResponse seleccionarAula(
            @RequestParam(name = "seccion", required = false) Long seccionId,
            @RequestParam(name = "aula", required = false) Long aulaId,
            RedirectAttributes redirectAttr,
            HttpSession session) {

        JsonResponse response = new JsonResponse();
        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
            JsonNodeFactory nc = JsonNodeFactory.instance;
            ObjectNode nodeData = new ObjectNode(nc);

            Seccion seccion = service.findSeccion(seccionId);
            Aula aula = service.findAula(aulaId);

            ArrayNode arg = new ArrayNode(nc);
            if (seccion.getVacantes() != null) {
                if (aula.getAforo().intValue() < seccion.getVacantes().intValue()) {
                    arg.add("Capacidad del aula menor que las vacantes.");
                }
            }
            String message = "";
            if (arg.size() == 0) {
                response.setSuccess(true);
                ObjectNode aulaNode = JsonHelper.createJson(aula, nc);
                aulaNode.put("esEspecifica", Boolean.TRUE);
                response.setData(aulaNode);
            } else {
                response.setSuccess(false);
                message = "Aula seleccionada con errores, verifique.";
                response.setData(arg);
            }
            response.setTotal(arg.size());
            response.setMessage(message);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (RuntimeException e) {
            ExceptionHandler.handleSpecial(e, response, Messages.FK_ERROR);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("allAnexos")
    public JsonResponse allAnexos(@RequestParam("anexoSuperior") String anexoSuperior, HttpSession session) {
        JsonResponse jsonResponse = new JsonResponse();

        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);

        ArrayNode array = new ArrayNode(JsonNodeFactory.instance);
        List<AnexoBoletin> anexos = service.allAnexosBySuperiorCiclo(anexoSuperior, ds.getCicloAcademico());

        for (AnexoBoletin anexo : anexos) {
            ObjectNode node = new ObjectNode(JsonNodeFactory.instance);
            node.put("id", anexo.getId());
            node.put("nombre", anexo.getNombre());
            node.put("superior", anexo.getAnexoSuperior().getNombre());

            array.add(node);
        }

        jsonResponse.setSuccess(true);
        jsonResponse.setData(array);
        return jsonResponse;
    }

}
