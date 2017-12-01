package pe.edu.lamolina.pivot.controller.academico.gposeccion;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableResponse;
import pe.albatross.zelpers.miscelanea.ExceptionHandler;
import pe.albatross.zelpers.miscelanea.JsonHelper;
import pe.albatross.zelpers.miscelanea.JsonResponse;
import pe.albatross.zelpers.miscelanea.ObjectUtil;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.albatross.zelpers.notify.Notificaciones;
import pe.edu.lamolina.pivot.model.academico.AnexoBoletin;
import pe.edu.lamolina.pivot.model.academico.CicloAcademico;
import pe.edu.lamolina.pivot.model.academico.Curso;
import pe.edu.lamolina.pivot.model.academico.Docente;
import pe.edu.lamolina.pivot.model.academico.DocenteSeccion;
import pe.edu.lamolina.pivot.model.academico.GrupoSeccion;
import pe.edu.lamolina.pivot.model.academico.Seccion;
import pe.edu.lamolina.pivot.zelper.constant.Constantine;
import pe.edu.lamolina.pivot.zelper.constant.Messages;
import pe.edu.lamolina.pivot.zelper.enums.EstadoEnum;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

@Controller
@RequestMapping("academico/gposeccion")
public class GpoSeccionController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    GpoSeccionService service;

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

            ArrayNode array = new ArrayNode(JsonNodeFactory.instance);

            for (GrupoSeccion gpoSeccion : gpoSecciones) {
                ObjectNode node = new ObjectNode(JsonNodeFactory.instance);

                node.put("id", gpoSeccion.getId());
                node.put("curso", gpoSeccion.getCurso().getNombre());
                node.put("codigo", gpoSeccion.getCurso().getCodigo());
                node.put("teoria", gpoSeccion.getCurso().getHorasTeoria());
                node.put("practica", gpoSeccion.getCurso().getHorasPractica());
                node.put("creditos", gpoSeccion.getCurso().getCreditos());
                node.put("anexo", gpoSeccion.getAnexoBoletin().getNombre());
                node.put("estado", gpoSeccion.getEstado());
                node.put("estadoValue", gpoSeccion.getEstado() != null ? EstadoEnum.valueOf(gpoSeccion.getEstado()).getValue() : "");

                ArrayNode secciones = new ArrayNode(JsonNodeFactory.instance);
                if (gpoSeccion.getSecciones() != null && !gpoSeccion.getSecciones().isEmpty()) {
                    for (Seccion seccion : gpoSeccion.getSecciones()) {
                        ObjectNode node2 = new ObjectNode(JsonNodeFactory.instance);
                        node2.put("tipo", seccion.getTipoSeccion());
                        node2.put("tipoValue", seccion.getTipoSeccionEnum().getTipoSeccionEvalEnum().getValue());
                        node2.put("codigo", seccion.getCodigo());
                        node2.put("vacantes", seccion.getVacantes());
                        node2.put("matriculados", seccion.getMatriculados());
                        node2.put("aula", (String) ObjectUtil.getParentTree(seccion, "aula.codigo"));
                        node2.put("grupo", (String) ObjectUtil.getParentTree(seccion, "grupoHoras.codigo"));
                        node2.put("estadoSec", seccion.getEstado());
                        node2.put("estadoValueSec", seccion.getEstadoEnum().getValue());
                        secciones.add(node2);

                        ArrayNode docentes = new ArrayNode(JsonNodeFactory.instance);
                        for (DocenteSeccion docSeccion : seccion.getDocenteSeccion()) {
                            ObjectNode node3 = new ObjectNode(JsonNodeFactory.instance);
                            node3.put("principal", docSeccion.getPrincipal());
                            node3.put("codigo", docSeccion.getDocente().getCodigo());
                            node3.put("docente", (String) ObjectUtil.getParentTree(docSeccion, "docente.persona.apellidosNombres"));
                            docentes.add(node3);
                        }
                        node2.set("docentes", docentes);
                    }
                    node.set("secciones", secciones);
                } else {

                    ObjectNode node2 = new ObjectNode(JsonNodeFactory.instance);
                    node2.put("tipo", "");
                    node2.put("tipoValue", "");
                    node2.put("codigo", "");
                    node2.put("vacantes", "");
                    node2.put("matriculados", "");
                    node2.put("aula", "");
                    node2.put("grupo", "");
                    node2.put("estadoSec", "");
                    node2.put("estadoValueSec", "");
                    secciones.add(node2);

                    ArrayNode docentes = new ArrayNode(JsonNodeFactory.instance);

                    ObjectNode node3 = new ObjectNode(JsonNodeFactory.instance);
                    node3.put("principal", "");
                    node3.put("codigo", "");
                    node3.put("docente", "");
                    docentes.add(node3);

                    node2.put("docentes", "");

                    node.put("secciones", "");
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

    @RequestMapping("{gruposeccion}/editar")
    public String editar(Model model, HttpSession session, @PathVariable("gruposeccion") Long gruposeccionId) {
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

        ArrayNode array = new ArrayNode(JsonNodeFactory.instance);
        List<Seccion> secciones = service.allSeccionesByGrupo(new GrupoSeccion(gruposeccionId));
        for (Seccion seccion : secciones) {
            ObjectNode node = new ObjectNode(JsonNodeFactory.instance);
            node.put("seccionId", seccion.getId());
            node.put("seccionCodigo", seccion.getCodigo());
            node.put("tipoSeccionValue", seccion.getTipoSeccionEnum().getValue());
            node.put("aula", ObjectUtil.getParentTree(seccion, "aula.id") != null ? seccion.getAula().getNombre() : "");
            node.put("grupoHoras", ObjectUtil.getParentTree(seccion, "grupoHoras.id") != null ? seccion.getGrupoHoras().getCodigo() : "");
            node.put("vacantes", seccion.getVacantes());
            node.put("matriculados", seccion.getMatriculados());
            node.put("cantidadDocentes", seccion.getDocentesCant());
            node.put("estadoEnumValue", seccion.getEstadoEnum().getValue());
            node.put("estadoEnumCode", seccion.getEstadoEnum().name());

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

}
