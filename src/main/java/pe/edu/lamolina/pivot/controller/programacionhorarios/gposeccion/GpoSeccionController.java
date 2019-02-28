package pe.edu.lamolina.pivot.controller.programacionhorarios.gposeccion;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.beans.PropertyEditorSupport;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.servlet.http.HttpSession;
import org.apache.commons.lang3.StringUtils;
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
import pe.edu.lamolina.model.academico.CambioAulaGrupo;
import pe.edu.lamolina.model.academico.Carrera;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Curso;
import pe.edu.lamolina.model.academico.CursoCicloAcademico;
import pe.edu.lamolina.model.academico.Docente;
import pe.edu.lamolina.model.academico.DocenteSeccion;
import pe.edu.lamolina.model.academico.Facultad;
import pe.edu.lamolina.model.academico.GrupoSeccion;
import pe.edu.lamolina.model.academico.ModalidadEstudio;
import pe.edu.lamolina.model.academico.RestriccionCarrera;
import pe.edu.lamolina.model.academico.RestriccionFacultad;
import pe.edu.lamolina.model.academico.RestriccionModalidad;
import pe.edu.lamolina.model.academico.RestriccionRepitencia;
import pe.edu.lamolina.model.academico.Seccion;
import pe.edu.lamolina.model.academico.TipoRepitencia;
import pe.edu.lamolina.model.enums.SeccionEstadoEnum;
import pe.edu.lamolina.model.enums.TipoDictadoGrupoSeccionEnum;
import pe.edu.lamolina.model.enums.TipoGrupoHorasEnum;
import pe.edu.lamolina.model.general.Aula;
import pe.edu.lamolina.model.general.Dia;
import pe.edu.lamolina.model.general.Oficina;
import pe.edu.lamolina.model.general.Persona;
import pe.edu.lamolina.model.horario.DiaHoraGrupo;
import pe.edu.lamolina.model.horario.GrupoHoras;
import pe.edu.lamolina.model.horario.Hora;
import pe.edu.lamolina.model.horario.HorarioAula;
import pe.edu.lamolina.model.horario.HorarioSeccion;
import pe.edu.lamolina.model.horario.TipoGrupoHoras;
import pe.edu.lamolina.pivot.controller.general.oficina.OficinaService;
import pe.edu.lamolina.pivot.zelper.constant.Constantine;
import pe.edu.lamolina.pivot.zelper.constant.Messages;
import pe.edu.lamolina.pivot.zelper.enums.TipoRestriccionEnum;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

@Controller
@RequestMapping("academico/gposeccion")
public class GpoSeccionController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    GpoSeccionService service;

    @Autowired
    OficinaService oficinaService;

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
        CicloAcademico ciclo = service.findCiclo(ds.getCicloAcademico());
        Long cantidad = service.contarGpoSecc(ciclo);

        model.addAttribute("cantidad", cantidad);
        model.addAttribute("ciclo", ciclo);
        model.addAttribute("cicloJson", createCicloJson(ciclo).toString());
        model.addAttribute("resumenJson", createResumenJson(service.resumenByCiclo(ciclo)));
//        model.addAttribute("resumen", service.resumenByCiclo(ciclo));
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
                ObjectNode nodeGpoSecc = JsonHelper.createJson(gpoSeccion, JsonNodeFactory.instance, true, new String[]{
                    "id", "estado", "estadoEnum", "codigo2", "cursoDirigido",
                    "curso.codigo",
                    "curso.nombre",
                    "curso.tpc",
                    "curso.precioFormato",
                    "curso.precioTpcFormato",
                    "curso.departamentoAcademico.nombre",
                    "anexoBoletin.nombre",
                    "anexoBoletin.anexoSuperior.nombre",
                    "secciones.codigo2",
                    "secciones.precio",
                    "secciones.precioFormato",
                    "secciones.abonoVeranoFormato",
                    "secciones.precioBaseFormato",
                    "secciones.precioPersonalizado",
                    "secciones.tipoSeccionEnum",
                    "secciones.vacantes",
                    "secciones.matriculados",
                    "secciones.restriccionCapa",
                    "secciones.horasSemanales",
                    "secciones.estadoEnum",
                    "secciones.grupoHoras.codigo",
                    "secciones.aula.codigo",
                    "secciones.aula.nombre",
                    "secciones.restriccionesModalidad.modalidadEstudio.nombre",
                    "secciones.restriccionesModalidad.modalidadEstudio.codigo",
                    "secciones.restriccionesFacultad.facultad.nombre",
                    "secciones.restriccionesFacultad.facultad.codigo",
                    "secciones.restriccionesCarrera.carrera.nombre",
                    "secciones.restriccionesCarrera.carrera.codigo",
                    "secciones.restriccionesCarrera.carrera.tipoEnum",
                    "secciones.restriccionesCarrera.carrera.tipoMAE",
                    "secciones.restriccionesCarrera.carrera.tipoDOC",
                    "secciones.restriccionesRepitencia.tipoRepitencia.nombre",
                    "secciones.docenteSeccion.estadoEnum",
                    "secciones.docenteSeccion.principal",
                    "secciones.docenteSeccion.porcentajeCarga",
                    "secciones.docenteSeccion.docente.codigo",
                    "secciones.docenteSeccion.docente.persona.apellidosNombres"
                });

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
    public String editar(
            @PathVariable("gruposeccion") Long gpoSeccId,
            @RequestParam(value = "origen", required = false) String origen,
            @RequestParam(value = "ids", required = false) String ids,
            Model model, HttpSession session) {

        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        List<Date> fechas = service.allDatesEventoCicloAcademicoForPeriodo(ds.getCicloAcademico());
        String fechaMin = null;
        String fechaMax = null;
        if (!fechas.isEmpty()) {
            fechaMin = TypesUtil.getStringDate(fechas.get(0), "dd/MM/yyyy");
            fechaMax = TypesUtil.getStringDate(fechas.get(fechas.size() - 1), "dd/MM/yyyy");
        }

        GrupoSeccion gpoSeccion = service.findGpoSeccion(gpoSeccId);
        ObjectNode gpoSeccionJson = createGpoSeccionJson(gpoSeccion, fechaMin, fechaMax, ds);
        String ruta = getOrigen(origen);
        Persona persona = ds.getPersona();
        List<Oficina> oficinas = oficinaService.allOficinasMainByPersona(persona);
        List<GrupoSeccion> gpos = obtenerGruposSeccion(ids);
        if (!gpos.isEmpty()) {
            gpos.add(gpoSeccion);
        }

        model.addAttribute("cicloAcademico", ds.getCicloAcademico());
        model.addAttribute("cicloJson", createCicloJson(ds.getCicloAcademico()).toString());
        model.addAttribute("grupoSeccionJson", gpoSeccionJson.toString());
        model.addAttribute("navigationJson", createNavegationJson(ruta, gpos, gpoSeccId, ds.getCicloAcademico()).toString());
        model.addAttribute("origen", ruta);
        model.addAttribute("oficinasJson", createOficinasJson(oficinas).toString());
        model.addAttribute("oficinas", oficinas);

        return "academico/gposeccion/gpoSeccionForm";
    }

    @ResponseBody
    @RequestMapping("{gruposeccion}/get")
    public JsonResponse getGpoSeccion(@PathVariable("gruposeccion") Long gpoSeccId, HttpSession session) {
        JsonResponse response = new JsonResponse();
        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
            List<Date> fechas = service.allDatesEventoCicloAcademicoForPeriodo(ds.getCicloAcademico());
            String fechaMin = null;
            String fechaMax = null;
            if (!fechas.isEmpty()) {
                fechaMin = TypesUtil.getStringDate(fechas.get(0), "dd/MM/yyyy");
                fechaMax = TypesUtil.getStringDate(fechas.get(fechas.size() - 1), "dd/MM/yyyy");
            }

            GrupoSeccion gpoSeccion = service.findGpoSeccion(gpoSeccId);
            ObjectNode nodeGpoSecc = createGpoSeccionJson(gpoSeccion, fechaMin, fechaMax, ds);

            ObjectNode data = new ObjectNode(JsonNodeFactory.instance);
            data.set("grupoSeccion", nodeGpoSecc);

            response.setData(data);
            response.setSuccess(true);

        } catch (PhobosException e) {
            e.printStackTrace();
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            e.printStackTrace();
            ExceptionHandler.handleException(e, response);
        } finally {
            return response;
        }
    }

    @ResponseBody
    @RequestMapping("allData")
    public JsonResponse allData(Model model, HttpSession session) {
        JsonResponse response = new JsonResponse();
        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);

            List<AnexoBoletin> anexosSup = service.allAnexosSuperiores();
            List<AnexoBoletin> anexos = service.allAnexoBoletionHijos();

            ArrayNode anexosJson = new ArrayNode(JsonNodeFactory.instance);
            for (AnexoBoletin anexo : anexos) {
                ObjectNode anxJson = JsonHelper.createJson(anexo, JsonNodeFactory.instance, true, new String[]{
                    "id", "codigo", "nombre",
                    "departamentoAcademico.nombre",
                    "carrera.codigo",
                    "carrera.nombre",
                    "anexoSuperior.id",
                    "anexoSuperior.codigo",
                    "anexoSuperior.nombre"
                });
                anexosJson.add(anxJson);
            }
            ArrayNode anexosSupJson = new ArrayNode(JsonNodeFactory.instance);
            for (AnexoBoletin anexoSup : anexosSup) {
                if (anexoSup.getId() > 5) {
                    continue;
                }
                ObjectNode anxJson = JsonHelper.createJson(anexoSup, JsonNodeFactory.instance, true, new String[]{
                    "id", "codigo", "nombre"
                });
                anexosSupJson.add(anxJson);
            }

            ObjectNode data = new ObjectNode(JsonNodeFactory.instance);
            data.set("anexos", anexosJson);
            data.set("anexosSup", anexosSupJson);

            response.setData(data);
            response.setSuccess(true);

        } catch (PhobosException e) {
            e.printStackTrace();
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            e.printStackTrace();
            ExceptionHandler.handleException(e, response);
        } finally {
            return response;
        }
    }

    @ResponseBody
    @RequestMapping("{gruposeccion}/activarGrupo")
    public JsonResponse activarGrupo(@PathVariable("gruposeccion") Long gruposeccionId, Model model, HttpSession session) {
        JsonResponse response = new JsonResponse();
        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
            service.cambiarEstadoGpoSeccion(SeccionEstadoEnum.ACT, new GrupoSeccion(gruposeccionId), ds.getUsuario());
            response.setMessage("Activado correctamente.");
            response.setSuccess(true);
        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        } finally {
            return response;
        }
    }

    @ResponseBody
    @RequestMapping("{gruposeccion}/desactivarGrupo")
    public JsonResponse desactivarGrupo(@PathVariable("gruposeccion") Long gruposeccionId, Model model, HttpSession session) {
        JsonResponse response = new JsonResponse();
        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
            service.cambiarEstadoGpoSeccion(SeccionEstadoEnum.ANU, new GrupoSeccion(gruposeccionId), ds.getUsuario());
            response.setMessage("Desactivado correctamente.");
            response.setSuccess(true);
        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        } finally {
            return response;
        }
    }

    @ResponseBody
    @RequestMapping("{gruposeccion}/loadGpoSeccionForm")
    public JsonResponse loadGpoSeccionForm(@PathVariable("gruposeccion") Long gruposeccionId, HttpSession session) {
        JsonResponse jsonResponse = new JsonResponse();
        JsonNodeFactory nc = JsonNodeFactory.instance;
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);

        /*
        List<Date> fechas = service.allDatesEventoCicloAcademicoForPeriodo(ds.getCicloAcademico());

        ArrayNode fechasJson = new ArrayNode(nc);
        for (Date fecha : fechas) {
            fechasJson.add(TypesUtil.getStringDate(fecha, "dd/MM/yyyy"));
        }
         */
        ObjectNode result = new ObjectNode(nc);
        /*   result.set("fechasPeriodos", fechasJson);
        if (fechasJson.size() != 0) {
            result.set("minFechaPeriodo", fechasJson.get(0));
            result.set("maxFechaPeriodo", fechasJson.get(fechasJson.size() - 1));
        } else {
            result.set("minFechaPeriodo", null);
            result.set("maxFechaPeriodo", null);
        }*/
        jsonResponse.setSuccess(true);
        jsonResponse.setData(result);
        return jsonResponse;
    }

    @ResponseBody
    @RequestMapping("{seccion}/loadCambiarAulaGrupo")
    public JsonResponse loadCambiarAulaGrupo(@PathVariable("seccion") Long seccionId, HttpSession session) {
        JsonResponse jsonResponse = new JsonResponse();
        JsonNodeFactory nc = JsonNodeFactory.instance;
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);

        ObjectNode result = new ObjectNode(nc);

        Seccion seccion = service.findSeccion(seccionId);

        JsonHelper.createJson(new CambioAulaGrupo(), nc, false, new String[]{"*"});

        jsonResponse.setSuccess(true);
        jsonResponse.setData(result);
        return jsonResponse;
    }

    @ResponseBody
    @RequestMapping("{gruposeccion}/findSecciones")
    public JsonResponse findSecciones(@PathVariable("gruposeccion") Long gruposeccionId, HttpSession session) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        JsonResponse jsonResponse = new JsonResponse();
        JsonNodeFactory jsonFactory = JsonNodeFactory.instance;
        ArrayNode array = new ArrayNode(jsonFactory);
        List<DocenteSeccion> docentesSeccion = service.analizedDocenteSeccion(new GrupoSeccion(gruposeccionId), ds.getCicloAcademico());
        List<Seccion> secciones = service.allSeccionesByGrupo(new GrupoSeccion(gruposeccionId), docentesSeccion);
        for (Seccion seccion : secciones) {
            ObjectNode node = JsonHelper.createJson(seccion, jsonFactory, true, new String[]{
                "*",
                "grupoHoras.id",
                "grupoHoras.codigo",
                "aula.id",
                "aula.codigo",
                "aula.capacidadAula",
                "restriccionesCarrera.id",
                "restriccionesCarrera.carrera.codigo",
                "restriccionesCarrera.carrera.nombre",
                "restriccionesFacultad.id",
                "restriccionesFacultad.facultad.codigo",
                "restriccionesFacultad.facultad.nombre",
                "restriccionesModalidad.id",
                "restriccionesModalidad.modalidadEstudio.codigo",
                "restriccionesModalidad.modalidadEstudio.nombre",
                "restriccionesRepitencia.id",
                "restriccionesRepitencia.tipoRepitencia.*"
            });

            node.put("tipoSeccionEvaluacionValue", seccion.getTipoSeccionEnum().getTipoSeccionEvalEnum().getValue());
            node.put("editVacantes", Boolean.FALSE);
            node.put("editRestriccionCapa", Boolean.FALSE);

            BigDecimal porcentajeAvance = BigDecimal.ZERO;
            for (DocenteSeccion docSeccion : seccion.getDocenteSeccion()) {
                if (docSeccion.getPorcentajeCarga() != null) {
                    porcentajeAvance = porcentajeAvance.add(docSeccion.getPorcentajeCarga());
                }
            }
            node.put("porcentajeAvance", porcentajeAvance);

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
            @RequestParam("seccion") String seccionId,
            HttpSession session) {

        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        JsonResponse jsonResponse = new JsonResponse();
        ArrayNode array = new ArrayNode(JsonNodeFactory.instance);
        List<DocenteSeccion> docentesSeccion = service.allDocentesSeccionBySeccion(new Seccion(seccionId));

        List<Date> fechas = service.allDatesEventoCicloAcademicoForPeriodo(ds.getCicloAcademico());
        String fechaMin = null;
        String fechaMax = null;
        if (!fechas.isEmpty()) {
            fechaMin = TypesUtil.getStringDate(fechas.get(0), "dd/MM/yyyy");
            fechaMax = TypesUtil.getStringDate(fechas.get(fechas.size() - 1), "dd/MM/yyyy");
        }

        for (DocenteSeccion docSeccion : docentesSeccion) {
            ObjectNode node = JsonHelper.createJson(docSeccion, JsonNodeFactory.instance, true, new String[]{
                "*",
                "docente.codigo",
                "docente.persona.id",
                "docente.persona.apellidosNombres"
            });
            node.put("docenteNN", docSeccion.getDocente().getCodigo().equals(Constantine.DOCENTE_INDETERMINADO));
            node.put("fechaInicioMin", fechaMin);
            node.put("fechaFinMax", fechaMax);
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
        return "academico/gposeccion/gpoSeccionNuevo";
    }

    @ResponseBody
    @RequestMapping("allCursos")
    public JsonResponse allCursos(
            @RequestParam("nombre") String nombre,
            HttpSession session) {

        JsonNodeFactory jsonFactory = JsonNodeFactory.instance;
        JsonResponse response = new JsonResponse();

        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);

            List<Curso> cursos = service.allCursosForProgramacion(nombre);
            ArrayNode jsonList = new ArrayNode(jsonFactory);
            for (Curso cur : cursos) {
                ObjectNode json = JsonHelper.createJson(cur, jsonFactory, true, new String[]{
                    "id", "codigo", "nombre", "tpc", "creditos",
                    "modalidadEstudio.id",
                    "modalidadEstudio.codigo",
                    "modalidadEstudio.nombre",
                    "carrera.id",
                    "carrera.nombre",
                    "carrera.codigo",
                    "departamentoAcademico.id",
                    "departamentoAcademico.codigo",
                    "departamentoAcademico.nombre",
                    "departamentoAcademico.facultad.nombre"
                });
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
    public JsonResponse saveGpoHeader(@RequestBody GrupoSeccion grupoSeccion, HttpSession session) {

        JsonResponse response = new JsonResponse();
        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
            ObjectNode node = new ObjectNode(JsonNodeFactory.instance);

            String ids = "";
            List<GrupoSeccion> gpoSecciones = service.saveGpoSeccionHeader(grupoSeccion, ds.getCicloAcademico(), ds);
            for (GrupoSeccion gpoSecc : gpoSecciones) {
                if (ids.equals("")) {
                    node.put("primero", gpoSecc.getId());
                }

                ids += ids.equals("") ? "" : ",";
                ids += gpoSecc.getId();
            }
            node.put("lista", ids);
            response.setData(node);
            response.setSuccess(true);
            response.setMessage("Grupos de secciones creados satisfactoriamente");

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
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

            service.addDocenteSeccion(new Seccion(seccionId), ds.getCicloAcademico());

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
    @RequestMapping("updateFechaInicio")
    public JsonResponse updateFechaInicioDocSec(@RequestBody DocenteSeccion docenteSeccion,
            Model model,
            HttpSession session) {
        JsonResponse response = new JsonResponse();
        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);

            //service.addDocenteSeccion(new Seccion(seccionId), ds.getCicloAcademico());
            service.updateDocenteSecFechaInicio(docenteSeccion, ds.getCicloAcademico());
            String message = "Fecha inicio actualizada.";
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
    @RequestMapping("updateFechaFin")
    public JsonResponse updateFechaFin(@RequestBody DocenteSeccion docenteSeccion,
            Model model,
            HttpSession session) {
        JsonResponse response = new JsonResponse();
        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);

            //service.addDocenteSeccion(new Seccion(seccionId), ds.getCicloAcademico());
            service.updateDocenteSecFechaFin(docenteSeccion, ds.getCicloAcademico());
            String message = "Fecha final actualizada.";
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
    @RequestMapping("activarSeccion")
    public JsonResponse activarSeccion(@RequestParam("seccion") Long seccionId,
            Model model,
            HttpSession session) {
        JsonResponse response = new JsonResponse();
        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
            service.activarSeccion(new Seccion(seccionId), ds.getUsuario());
            String message = "Sección activada.";
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
    @RequestMapping("bloquearSeccion")
    public JsonResponse bloquearSeccion(@RequestParam("seccion") Long seccionId,
            Model model,
            HttpSession session) {
        JsonResponse response = new JsonResponse();
        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
            service.bloquearSeccion(new Seccion(seccionId), ds.getUsuario());
            String message = "Sección bloqueada.";
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
    @RequestMapping("anularSeccion")
    public JsonResponse anularSeccion(@RequestParam("seccion") Long seccionId,
            Model model,
            HttpSession session) {
        JsonResponse response = new JsonResponse();
        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);

            GrupoSeccion grupoSeccion = service.anularSeccion(new Seccion(seccionId), ds.getUsuario());
            String message = "redirect";
            response.setData(grupoSeccion.getCurso().getId());
            if (grupoSeccion.getId() != null) {
                message = "Sección anulada.";
            }
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

    @RequestMapping("deleteGrupoSeccion")
    public String deleteGrupoSeccion(RedirectAttributes redirectAttr, Model model, @RequestParam("curso") Long cursoId, HttpSession session) {
        Curso curso = service.findCurso(cursoId);
        String message = String.format("Se eliminó el grupo seccion del curso %s", curso.getCodigo() + "-" + curso.getNombre());
        Notificaciones.crearMsg(message, redirectAttr);
        return "redirect:/academico/gposeccion";
    }

    @ResponseBody
    @RequestMapping("deleteDocSeccion")
    public JsonResponse deleteDocSeccion(@RequestParam("docSeccion") Long docSeccionId,
            Model model,
            HttpSession session) {
        JsonResponse response = new JsonResponse();
        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);

            service.deleteDocSeccion(new DocenteSeccion(docSeccionId), ds.getCicloAcademico());
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
            @RequestParam(name = "codigoDep", required = false) String codigoDep,
            HttpSession session) {

        JsonNodeFactory jsonFactory = JsonNodeFactory.instance;
        JsonResponse response = new JsonResponse();

        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);

            ArrayNode jsonList = new ArrayNode(jsonFactory);

            List<Docente> docentes = service.allDocenterByNombre(nombre, codigoDep);

            for (Docente doc : docentes) {
                ObjectNode json = new ObjectNode(jsonFactory);
                json.put("id", doc.getId());
                json.put("apellidosNombres", StringUtils.isBlank(doc.getPersona().getApellidosNombres()) ? "" : doc.getPersona().getApellidosNombres());
                json.put("personaNombre", doc.getPersona().getNombres());
                json.put("personaPaterno", doc.getPersona().getPaterno());
                json.put("personaMaterno", doc.getPersona().getMaterno());
                json.put("codigo", doc.getCodigo());
                json.put("departamento", doc.getDepartamentoAcademico().getNombre());
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
            @RequestParam("seccion") Long seccionId,
            HttpSession session) {

        JsonNodeFactory jsonFactory = JsonNodeFactory.instance;
        JsonResponse response = new JsonResponse();

        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
            CicloAcademico ciclo = ds.getCicloAcademico();
            List<Aula> aulas = service.searchAulaByName(nombre, seccionId, ciclo);
            ArrayNode jsonList = new ArrayNode(jsonFactory);

            for (Aula aula : aulas) {
                ObjectNode json = JsonHelper.createJson(aula, jsonFactory, true, new String[]{"*", "aulaSuperior.nombre"});
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
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        try {
            service.actualizarDocente(docSeccion, docente, ds.getCicloAcademico());
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
        JsonResponse response = new JsonResponse();
        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
            logger.debug("seccion {}, vacantes {}", seccionId, vacantes);

            Seccion seccion = new Seccion(seccionId);
            seccion.setVacantes(vacantes);
            service.actualizarSeccionVacantes(seccion, ds.getUsuario());

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
    @RequestMapping("cambiarRestriccionCapa")
    public JsonResponse cambiarRestriccionCapa(
            @RequestParam("seccion") Long seccionId,
            @RequestParam("capa") Integer capa,
            HttpSession session) {
        JsonNodeFactory jsonFactory = JsonNodeFactory.instance;
        JsonResponse response = new JsonResponse();
        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
            logger.debug("seccion {}, capa {}", seccionId, capa);

            Seccion seccion = new Seccion(seccionId);
            seccion.setRestriccionCapa(capa);
            service.actualizarSeccionResctriccionCapa(seccion, ds.getUsuario());

            response.setSuccess(Boolean.TRUE);
            response.setMessage("Restricción CAPA actualizada.");
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
        JsonResponse response = new JsonResponse();
        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
            DocenteSeccion docenteSeccion = new DocenteSeccion(docSeccion);
            docenteSeccion.setPorcentajeCarga(porcentajeAvance);
            service.updatePorcentajeAvance(docenteSeccion, ds.getCicloAcademico());

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
    @RequestMapping("cambiarAulaDirect")
    public JsonResponse cambiarAulaDirect(
            @RequestParam("seccion") Long seccionId,
            @RequestParam("aula") String codigoAula,
            HttpSession session) {

        JsonResponse response = new JsonResponse();
        try {

            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
            Aula aula = new Aula();
            aula.setCodigo(codigoAula);
            service.saveAula(new Seccion(seccionId), aula, ds);

            response.setSuccess(Boolean.TRUE);
            response.setMessage("Aula cambiada correctamente");
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
    @RequestMapping("cambiarGrupoHorDirect")
    public JsonResponse cambiarGrupoHorDirect(
            @RequestParam("seccion") Long seccionId,
            @RequestParam("grupoHor") String codigoGrupoHor,
            HttpSession session) {

        JsonResponse response = new JsonResponse();
        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
            Seccion seccion = service.findSeccion(seccionId);
            GrupoHoras grupoHoras = service.findGrupoHorasForDirectUpdate(codigoGrupoHor, ds.getCicloAcademico(), seccion);
            service.saveSeccionGrupoHorario(seccion, grupoHoras, ds.getCicloAcademico());

            response.setSuccess(Boolean.TRUE);
            response.setMessage("Grupo horario cambiado correctamente");
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
            response.setData(JsonHelper.createJson(seccion, jsonFactory, true, new String[]{"*"}));
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
    public JsonResponse loadModalGrupo(
            @RequestParam("seccion") Long seccionId,
            @RequestParam(value = "grupoHoras", required = false) Long grupoHorasId,
            Model model,
            HttpSession session) {
        JsonNodeFactory jsonFactory = JsonNodeFactory.instance;
        JsonResponse response = new JsonResponse();

        try {
            long t1 = System.currentTimeMillis();

            Seccion seccion = service.findSeccion(seccionId);
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
            CicloAcademico cicloAcademico = ds.getCicloAcademico();

            ObjectNode node = new ObjectNode(jsonFactory);

            node.set("seccion", JsonHelper.createJson(seccion, jsonFactory, true, new String[]{
                "id", "codigo2", "vacantes", "horasSemanales", "horasAdicionales", "totalHorasSemanales",
                "tieneRestriccion", "tieneRestriccionCarrera", "tieneRestriccionFacultad", "tieneRestriccionModalidad", "tieneRestriccionRepitencia",
                "aula.id",
                "aula.codigo",
                "aula.nombre",
                "aula.capacidadAula",
                "aula.aforo",
                "grupoHoras.codigo",
                "grupoSeccion.curso.codigo",
                "grupoSeccion.curso.nombre",
                "grupoSeccion.curso.departamentoAcademico.nombre"
            }));
            node.put("tipoGrupoHorasSeleccionado", "");

            String[] mapperGrupoHorarioSel = new String[]{
                "*",
                "tipoGrupoHoras.*",
                "diaHoraGrupo.dia.*",
                "diaHoraGrupo.hora.*"
            };
            if (grupoHorasId != null) {
                GrupoHoras gpoHora = service.findGrupoHorasWithHorario(seccion, new GrupoHoras(grupoHorasId), ds.getCicloAcademico());
                node.set("grupoHorarioSel", JsonHelper.createJson(gpoHora, jsonFactory, true, mapperGrupoHorarioSel));
            } else {
                if (seccion.getGrupoHoras() != null) {
                    GrupoHoras gpoHora = service.findGrupoHorasWithHorario(seccion, ds.getCicloAcademico());
                    node.set("grupoHorarioSel", JsonHelper.createJson(gpoHora, jsonFactory, true, mapperGrupoHorarioSel));
                }
            }

            List<TipoGrupoHoras> tiposGrupoHoras = service.allGrupoHorasActivosTipoAndCiclo(cicloAcademico, TipoGrupoHorasEnum.REGULAR);

            ArrayNode jTiposGrupoHoras = new ArrayNode(jsonFactory);
            for (TipoGrupoHoras tiposGrupoHoraEach : tiposGrupoHoras) {
                ObjectNode tipoGrupoHorasNode = JsonHelper.createJson(tiposGrupoHoraEach, jsonFactory, true, new String[]{"*"});
                tipoGrupoHorasNode.put("texto", tiposGrupoHoraEach.getCodigo() + " - " + tiposGrupoHoraEach.getDescripcion());
                jTiposGrupoHoras.add(tipoGrupoHorasNode);
            }
            node.set("tiposGruposHorasOpt", jTiposGrupoHoras);

            response.setData(node);
            response.setSuccess(Boolean.TRUE);

            long t2 = System.currentTimeMillis();

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
            Oficina oficinaOERA = service.findOficinaOera();
            List<Oficina> oficinas = oficinaContenida(ds.getOficinas(), oficinaOERA) ? service.allOficinas(ds.getCompania()) : ds.getOficinas();

            Seccion seccion = service.findSeccion(seccionId);
            List<Aula> modulosOera = service.allPabellonesByOficina(oficinaOERA);

            List<Aula> modulosNoOera = service.allPabellonesByOficinasNoOera(oficinas);

            ArrayNode modulosOeraJson = new ArrayNode(jsonFactory);
            for (Aula moduloEach : modulosOera) {
                modulosOeraJson.add(JsonHelper.createJson(moduloEach, jsonFactory));
            }

            ArrayNode oficinasAulasSuperioresJson = new ArrayNode(jsonFactory);
            for (Aula moduloEach : modulosNoOera) {
                oficinasAulasSuperioresJson.add(JsonHelper.createJson(moduloEach, jsonFactory));
            }

            ObjectNode nodeResult = new ObjectNode(jsonFactory);
            nodeResult.putPOJO("seccion", JsonHelper.createJson(seccion, jsonFactory, true, new String[]{
                "id", "codigo2", "vacantes", "horasSemanales", "horasAdicionales", "totalHorasSemanales",
                "tieneRestriccion", "tieneRestriccionCarrera", "tieneRestriccionFacultad", "tieneRestriccionModalidad", "tieneRestriccionRepitencia",
                "aula.id",
                "aula.codigo",
                "aula.nombre",
                "aula.capacidadAula",
                "aula.aforo",
                "grupoHoras.codigo",
                "grupoSeccion.curso.codigo",
                "grupoSeccion.curso.nombre",
                "grupoSeccion.curso.departamentoAcademico.nombre"
            }));
            //combo
            nodeResult.set("modulosOera", modulosOeraJson);
            //combo
            nodeResult.set("oficinasDisponibles", oficinasAulasSuperioresJson);

            //  nodeResult.put("modulosOeraSel", "");
            //   nodeResult.put("oficinaSel", "");
            //  nodeResult.put("aulaSel", "");
            if (ObjectUtil.getParentTree(seccion, "aula.id") != null) {
                Aula aula = seccion.getAula();
                ObjectNode aulaNode = JsonHelper.createJson(aula, jsonFactory, true, new String[]{
                    "id", "codigo", "nombre"
                });

                if (aula.getOficinaSupervisora().getId().equals(Constantine.ID_OFICINA_OERA)) {
                    //OERA
                    Aula moduloOera = modulosOera.stream().filter(req -> req.getId().equals(aula.getAulaSuperior().getId())).findFirst().orElse(null);
                    aulaNode.put("tabAula", "oera");
                    //  aulaNode.put("seleccionado", Boolean.TRUE);
                    nodeResult.putPOJO("modulosOeraSel", JsonHelper.createJson(moduloOera, jsonFactory));
                } else if (aulaContenida(modulosNoOera, aula.getAulaSuperior())) {
//                } else if (modulosNoOera.contains(aula.getAulaSuperior())) {
                    //oficinas
                    aulaNode.put("tabAula", "oficina");
                    nodeResult.putPOJO("oficinaSel", JsonHelper.createJson(aula.getAulaSuperior(), jsonFactory));
                } else {
                    //especificos
                    aulaNode.put("tabAula", "especifica");
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
    @RequestMapping("loadModalRestricciones")
    public JsonResponse loadModalRestricciones(
            @RequestParam("seccion") Long seccionId,
            HttpSession session,
            Model model) {
        JsonNodeFactory jsonFactory = JsonNodeFactory.instance;
        JsonResponse response = new JsonResponse();
        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
            ObjectNode nodeResult = new ObjectNode(jsonFactory);

            Seccion seccion = service.findSeccionWithRestriccions(seccionId);
            List<Facultad> facultades = service.allFacultadesActivas();
            List<ModalidadEstudio> modalidadesEstudio = service.allModalidadesEstudioActivas();

            ArrayNode facultadesJson = new ArrayNode(jsonFactory);
            for (Facultad facultad : facultades) {
                facultadesJson.add(JsonHelper.createJson(facultad, jsonFactory, true, new String[]{"id", "codigo", "nombre"}));
            }
            ArrayNode modalidadesJson = new ArrayNode(jsonFactory);
            for (ModalidadEstudio modalidadEstudioEach : modalidadesEstudio) {
                modalidadesJson.add(JsonHelper.createJson(modalidadEstudioEach, jsonFactory, true, new String[]{"id", "codigo", "nombre"}));
            }
            ArrayNode tiposRestriccionesJson = new ArrayNode(jsonFactory);
            for (TipoRestriccionEnum tipo : TipoRestriccionEnum.values()) {
                if (tipo == TipoRestriccionEnum.NREP) {
                    continue;
                }
                ObjectNode tipoRestJson = new ObjectNode(jsonFactory);
                tipoRestJson.put("codigo", tipo.name());
                tipoRestJson.put("nombre", tipo.getValue());
                tiposRestriccionesJson.add(tipoRestJson);
            }

            ObjectNode seccionJson = JsonHelper.createJson(seccion, jsonFactory, true, new String[]{
                "id", "codigo2", "vacantes", "horasSemanales",
                "tieneRestriccion", "tieneRestriccionCarrera", "tieneRestriccionFacultad", "tieneRestriccionModalidad", "tieneRestriccionRepitencia",
                "aula.codigo",
                "aula.nombre",
                "grupoSeccion.curso.codigo",
                "grupoSeccion.curso.nombre",
                "grupoSeccion.curso.tpc",
                "grupoSeccion.curso.departamentoAcademico.nombre"
            });

            nodeResult.set("seccion", seccionJson);
            nodeResult.set("facultades", facultadesJson);
            nodeResult.set("modalidades", modalidadesJson);
            nodeResult.set("tiposRestriccion", tiposRestriccionesJson);

            ObjectNode tipoRestriccionSel = null;
            if (seccion.isTieneRestriccionCarrera()) {
                tipoRestriccionSel = new ObjectNode(jsonFactory);
                tipoRestriccionSel.put("codigo", TipoRestriccionEnum.ESP.name());
                tipoRestriccionSel.put("nombre", TipoRestriccionEnum.ESP.getValue());
            } else if (seccion.isTieneRestriccionFacultad()) {
                tipoRestriccionSel = new ObjectNode(jsonFactory);
                tipoRestriccionSel.put("codigo", TipoRestriccionEnum.FAC.name());
                tipoRestriccionSel.put("nombre", TipoRestriccionEnum.FAC.getValue());
            } else if (seccion.isTieneRestriccionModalidad()) {
                tipoRestriccionSel = new ObjectNode(jsonFactory);
                tipoRestriccionSel.put("codigo", TipoRestriccionEnum.MOD.name());
                tipoRestriccionSel.put("nombre", TipoRestriccionEnum.MOD.getValue());
            }
            nodeResult.set("tipoRestriccionSel", tipoRestriccionSel);

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
    @RequestMapping("loadModalRepitenciaRestriccion")
    public JsonResponse loadModalRepitenciaRestriccion(
            @RequestParam("seccion") Long seccionId,
            HttpSession session,
            Model model) {
        JsonNodeFactory jsonFactory = JsonNodeFactory.instance;
        JsonResponse response = new JsonResponse();
        try {
            ObjectNode result = new ObjectNode(jsonFactory);
            Seccion seccion = service.findSeccion(seccionId);
            List<TipoRepitencia> tiposRepitencia = service.allTipoRepitencia();

            ArrayNode repitenciasJson = null;
            if (seccion.getRestriccionesRepitencia() != null && !seccion.getRestriccionesRepitencia().isEmpty()) {
                repitenciasJson = new ArrayNode(jsonFactory);

                for (RestriccionRepitencia repitencia : seccion.getRestriccionesRepitencia()) {
                    ObjectNode repitenciaJson = JsonHelper.createJson(repitencia.getTipoRepitencia(), jsonFactory, true, new String[]{
                        "id", "codigo", "nombre"
                    });

                    repitenciasJson.add(repitenciaJson);
                }
            }

            ArrayNode tiposRepitenciaJson = new ArrayNode(jsonFactory);
            for (TipoRepitencia tipoRepitencia : tiposRepitencia) {
                tiposRepitenciaJson.add(JsonHelper.createJson(tipoRepitencia, jsonFactory, true, new String[]{"id", "codigo", "nombre"}));
            }

            ObjectNode seccionJson = JsonHelper.createJson(seccion, jsonFactory, true, new String[]{
                "id", "codigo2", "vacantes", "horasSemanales",
                "tieneRestriccion", "tieneRestriccionCarrera", "tieneRestriccionFacultad", "tieneRestriccionModalidad", "tieneRestriccionRepitencia",
                "aula.codigo",
                "aula.nombre",
                "grupoSeccion.curso.codigo",
                "grupoSeccion.curso.nombre",
                "grupoSeccion.curso.tpc",
                "grupoSeccion.curso.departamentoAcademico.nombre"
            });

            result.set("seccion", seccionJson);
            result.set("restriccionesRepitencia", repitenciasJson);
            result.set("tiposRepitenciaJson", tiposRepitenciaJson);

            response.setData(result);
            response.setSuccess(Boolean.TRUE);
        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("cambiarTipoRestriccion")
    public JsonResponse cambiarTipoRestriccion(
            @RequestParam("seccion") Long seccionId,
            @RequestParam("tipoRestriccion") String tipoRestriccion,
            HttpSession session,
            Model model) {
        JsonNodeFactory jsonFactory = JsonNodeFactory.instance;
        JsonResponse response = new JsonResponse();
        ObjectNode nodeResult = new ObjectNode(jsonFactory);
        try {
            logger.debug("entro a cambiarTipoRestriccion");

            TipoRestriccionEnum tipoRestriccionEnum = TipoRestriccionEnum.valueOf(tipoRestriccion);
            Seccion seccion = service.findSeccionWithRestriccions(seccionId);

            nodeResult.put("esEspecialidad", Boolean.FALSE);
            nodeResult.put("esFacultad", Boolean.FALSE);
            nodeResult.put("esModalidad", Boolean.FALSE);

            ArrayNode facultadesJson = null;
            ArrayNode modalidadesJson = null;
            ArrayNode carrerasJson = null;
            ArrayNode restriccionesSeleccionadas = null;

            if (tipoRestriccionEnum.equals(TipoRestriccionEnum.ESP)) {
                nodeResult.put("esEspecialidad", Boolean.TRUE);
                carrerasJson = new ArrayNode(jsonFactory);
                List<Carrera> carreras = service.allCarrerasActivasPrePost();
                for (Carrera carreraeEach : carreras) {
                    ObjectNode carreraJson = JsonHelper.createJson(carreraeEach, jsonFactory, new String[]{
                        "id", "codigo", "nombre", "tipoDOC", "tipoMAE", "tipoEnum",
                        "modalidadEstudio.id",
                        "modalidadEstudio.codigo",
                        "modalidadEstudio.nombre"
                    });
//                    carreraJson.put("tipoDescripcion", carreraeEach.getTipoEnum().getValue());
//                    carreraJson.put("esTipoDOC", carreraeEach.isTipoDOC());
//                    carreraJson.put("esTipoMAE", carreraeEach.isTipoMAE());
                    carrerasJson.add(carreraJson);
                }
                if (seccion.getRestriccionesCarrera() != null && !seccion.getRestriccionesCarrera().isEmpty()) {
                    restriccionesSeleccionadas = new ArrayNode(jsonFactory);
                    for (RestriccionCarrera restriccionCarreraEach : seccion.getRestriccionesCarrera()) {
                        restriccionesSeleccionadas.add(JsonHelper.createJson(restriccionCarreraEach.getCarrera(), jsonFactory, new String[]{
                            "id", "codigo", "nombre", "tipoDOC", "tipoMAE", "tipoEnum",
                            "modalidadEstudio.id",
                            "modalidadEstudio.codigo",
                            "modalidadEstudio.nombre"
                        }));
                    }
                }
            } else if (tipoRestriccionEnum.equals(TipoRestriccionEnum.FAC)) {
                nodeResult.put("esFacultad", Boolean.TRUE);
                facultadesJson = new ArrayNode(jsonFactory);
                List<Facultad> facultades = service.allFacultadesActivas();
                for (Facultad facultadEach : facultades) {
                    facultadesJson.add(JsonHelper.createJson(facultadEach, jsonFactory, new String[]{"id", "codigo", "nombre"}));
                }
                if (seccion.getRestriccionesFacultad() != null && !seccion.getRestriccionesFacultad().isEmpty()) {
                    restriccionesSeleccionadas = new ArrayNode(jsonFactory);
                    for (RestriccionFacultad restriccionFacultadEach : seccion.getRestriccionesFacultad()) {
                        restriccionesSeleccionadas.add(JsonHelper.createJson(restriccionFacultadEach.getFacultad(), jsonFactory, new String[]{
                            "id", "codigo", "nombre"
                        }));
                    }
                }
            } else if (tipoRestriccionEnum.equals(TipoRestriccionEnum.MOD)) {
                nodeResult.put("esModalidad", Boolean.TRUE);
                modalidadesJson = new ArrayNode(jsonFactory);
                List<ModalidadEstudio> modalidades = service.allModalidadesEstudioActivas();
                for (ModalidadEstudio modalidadEach : modalidades) {
                    modalidadesJson.add(JsonHelper.createJson(modalidadEach, jsonFactory, new String[]{"id", "codigo", "nombre"}));
                }
                if (seccion.getRestriccionesModalidad() != null && !seccion.getRestriccionesModalidad().isEmpty()) {
                    restriccionesSeleccionadas = new ArrayNode(jsonFactory);
                    for (RestriccionModalidad restriccionModalidadEach : seccion.getRestriccionesModalidad()) {
                        restriccionesSeleccionadas.add(JsonHelper.createJson(restriccionModalidadEach.getModalidadEstudio(), jsonFactory, new String[]{
                            "id", "codigo", "nombre"
                        }));
                    }
                }
            }
            nodeResult.set("restriccionesSeleccionadas", restriccionesSeleccionadas);
            if (facultadesJson != null) {
                nodeResult.set("tblRestricciones", facultadesJson);
            }
            if (modalidadesJson != null) {
                nodeResult.set("tblRestricciones", modalidadesJson);
            }
            if (carrerasJson != null) {
                nodeResult.set("tblRestricciones", carrerasJson);
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
    @RequestMapping("saveRestriccion")
    public JsonResponse saveRestriccion(
            @RequestBody ObjectNode restriccionJson,
            RedirectAttributes redirectAttr,
            HttpSession session) {

        JsonResponse response = new JsonResponse();
        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);

            ObjectNode node = new ObjectNode(JsonNodeFactory.instance);
            Seccion seccion = new Seccion(TypesUtil.getLong(restriccionJson.get("seccion")));
            String tipoRestriccion = restriccionJson.get("tipoRestriccion").get("codigo").asText();
            TipoRestriccionEnum tipoRestriccionEnum = TipoRestriccionEnum.valueOf(tipoRestriccion);
            ArrayNode restriccionesJson = (ArrayNode) restriccionJson.get("restriccionesArr");
            List<Long> restriccionesList = new ArrayList<>();

            for (JsonNode restriccionEach : restriccionesJson) {
                restriccionesList.add(restriccionEach.get("id").asLong());
            }

            service.saveRestriccion(seccion, ds.getUsuario(), tipoRestriccionEnum, restriccionesList);
            String message = "Restriccion asignada correctamente.";

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
    @RequestMapping("loadModalAulaHorario")
    public JsonResponse loadModalAulaHorario(
            @RequestParam("aula") Long aulaId,
            HttpSession session,
            Model model) {
        JsonNodeFactory jsonFactory = JsonNodeFactory.instance;
        JsonResponse response = new JsonResponse();
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        try {
            Aula aula = service.findAulaFull(aulaId, ds.getCicloAcademico());

            List<Dia> dias = service.allDia();
            List<Hora> horasEncontradas = new ArrayList<>();
            for (HorarioAula horarioAula : aula.getHorariosAula()) {
                if (!horasEncontradas.contains(horarioAula.getHora())) {
                    horasEncontradas.add(horarioAula.getHora());
                }
            }
            Collections.sort(horasEncontradas, (p1, p2) -> p1.getNumero().compareTo(p2.getNumero()));

            JsonNodeFactory factory = JsonNodeFactory.instance;
            ObjectNode data = new ObjectNode(factory);

            ArrayNode diasJson = new ArrayNode(factory);
            for (Dia dia : dias) {
                diasJson.add(JsonHelper.createJson(dia, factory));
            }
            ArrayNode horasJson = new ArrayNode(factory);
            for (Hora horasEncontrada : horasEncontradas) {
                horasJson.add(JsonHelper.createJson(horasEncontrada, factory));
            }

            ObjectNode jsonHorarioAula = new ObjectNode(factory);
            if (aula.getHorariosAula() != null) {
                for (HorarioAula horarioAulaEach : aula.getHorariosAula()) {
                    jsonHorarioAula.putPOJO(horarioAulaEach.getIdDiaHora(),
                            JsonHelper.createJson(horarioAulaEach, jsonFactory, true,
                                    new String[]{
                                        "id",
                                        "seccion.codigo2",
                                        "seccion.sizeDocente",
                                        "seccion.grupoSeccion.curso.codigo",
                                        "seccion.grupoSeccion.curso.nombre",
                                        "seccion.docenteSeccion.docente.codigo",
                                        "seccion.docenteSeccion.docente.persona.nomPaternoMat",
                                        "seccion.grupoHoras.codigo"})
                    );
                }
            }

            data.putPOJO("aula", JsonHelper.createJson(aula, jsonFactory, true, new String[]{
                "id",
                "codigo",
                "nombre",
                "capacidadAula",
                "tipoAmbienteEnum",
                "tipoAula.id",
                "tipoAula.nombre",
                "oficinaSupervisora.id",
                "oficinaSupervisora.nombre",
                "aulaSuperior.id",
                "aulaSuperior.nombre"}));

            data.set("dias", diasJson);
            data.set("horas", horasJson);
            data.set("jsonHorarioAula", jsonHorarioAula);

            response.setData(data);
            response.setSuccess(true);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("horariosRegulares")
    public JsonResponse horariosRegulares(
            @RequestParam(name = "tipoGrupoHorasId", required = false) Long tipoGrupoHorasId,
            @RequestParam(name = "seccionId", required = true) Long seccionId,
            @RequestParam(name = "aulaId", required = false) Long aulaId,
            Model model, HttpSession session) {
        //cargar horarios de acuerdo a la seccion o al tipo grupo horas
        JsonResponse response = new JsonResponse();
        try {
            long t1 = System.currentTimeMillis();
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
            CicloAcademico cicloAcademico = ds.getCicloAcademico();

            JsonNodeFactory factory = JsonNodeFactory.instance;

            List<Dia> dias = service.allDia();
            Seccion seccion = service.findSeccion(seccionId);
            TipoGrupoHoras tipoGrupo = service.findTipoGrupoHoras(tipoGrupoHorasId);
            List<GrupoHoras> gruposHoras = service.allGrupoHorasBySeccionAndTipoGrupoHoras(seccion, tipoGrupo, cicloAcademico);
            List<HorarioAula> horariosAulasByCiclo = null;

            if (aulaId != null) {
                Aula aula = service.findAula(aulaId);
                horariosAulasByCiclo = service.allHorariosAula(aula, ds.getCicloAcademico());
            } else {
                if (seccion.getAula() != null) {
                    horariosAulasByCiclo = service.allHorariosAula(seccion.getAula(), ds.getCicloAcademico());
                }
            }

            List<DiaHoraGrupo> diaHoraGrupos = new ArrayList();
            for (GrupoHoras grupoHoraEach : gruposHoras) {
                for (DiaHoraGrupo diaHoraGrupo : grupoHoraEach.getDiaHoraGrupo()) {
                    diaHoraGrupos.add(diaHoraGrupo);
                }
            }

            Map<Long, Hora> mapHoras = TypesUtil.convertListToMap("hora.id", "hora", diaHoraGrupos);
            List<Hora> horasEncontradas = new ArrayList(mapHoras.values());

            Collections.sort(horasEncontradas, (p1, p2) -> p1.getNumero().compareTo(p2.getNumero()));

            Map<String, HorarioSeccion> mapHorarioBySeccion = TypesUtil.convertListToMap("idDiaHora", service.allHorarioSeccion(seccion));

            //Dias Horas Grupos de todos los grupos, de acuerdo al tipo seleccionado y se marcan los dia hora grupo de la seccion
            ObjectNode jsonDiaHoraGrupo = new ObjectNode(factory);
            for (DiaHoraGrupo diaHoraGrupo : diaHoraGrupos) {
                ObjectNode jsonDiaHoraGrupoEach = JsonHelper.createJson(diaHoraGrupo, factory, true,
                        new String[]{"id",
                            "dia.id", "dia.nombre",
                            "hora.id", "hora.codigo", "hora.descripcion",
                            "grupoHorario.codigo", "grupoHorario.id",
                            "grupoHorario.tipoGrupoHoras.*"});
                jsonDiaHoraGrupoEach.put("seleccionado", Boolean.FALSE);
                String diaHora = diaHoraGrupo.getIdDiaHora();
                HorarioSeccion hSecc = mapHorarioBySeccion.get(diaHora);
                if (hSecc != null) {
                    jsonDiaHoraGrupoEach.put("seleccionado", Boolean.TRUE);
                }
                jsonDiaHoraGrupo.putPOJO(diaHoraGrupo.getIdDiaHora(), jsonDiaHoraGrupoEach);
            }
            //
            ObjectNode jsonHorarioAula = new ObjectNode(factory);
            if (horariosAulasByCiclo != null) {
                for (HorarioAula horarioAulaEach : horariosAulasByCiclo) {
                    if (!horarioAulaEach.getSeccion().getId().equals(seccion.getId())) {

                        HorarioSeccion hSeccCruce = mapHorarioBySeccion.get(horarioAulaEach.getIdDiaHora());
                        if (hSeccCruce != null) {
                            horarioAulaEach.setTieneCruce(Boolean.TRUE);
                        }

                        jsonHorarioAula.putPOJO(horarioAulaEach.getIdDiaHora(),
                                JsonHelper.createJson(horarioAulaEach, factory, true,
                                        new String[]{"id",
                                            "tieneCruce",
                                            "dia.id", "dia.nombre",
                                            "hora.id", "hora.codigo", "hora.descripcion",
                                            "seccion.id",
                                            "seccion.codigo2",
                                            "aula.id", "aula.codigo", "aula.nombre"}));
                    }
                }
            }

            ObjectNode data = new ObjectNode(factory);

            ArrayNode diasJson = new ArrayNode(factory);
            for (Dia dia : dias) {
                diasJson.add(JsonHelper.createJson(dia, factory, true, new String[]{"*"}));
            }
            ArrayNode horasJson = new ArrayNode(factory);
            for (Hora horasEncontrada : horasEncontradas) {
                horasJson.add(JsonHelper.createJson(horasEncontrada, factory, true, new String[]{"*"}));
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
    public JsonResponse horariosEspeciales(
            @RequestParam(name = "grupoHorario", required = false) Long grupoHorarioId,
            @RequestParam(name = "aula", required = false) Long aulaId,
            @RequestParam(name = "seccion", required = false) Long seccionId, Model model, HttpSession session) {
        JsonResponse response = new JsonResponse();
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        try {
            JsonNodeFactory factory = JsonNodeFactory.instance;

            GrupoHoras grupoHoras = service.findGrupoHorasFull(new GrupoHoras(grupoHorarioId), ds.getCicloAcademico());
            Seccion seccion = service.findSeccion(seccionId);
            List<Dia> dias = service.allDia();

            List<HorarioAula> horariosAulas = null;
            if (aulaId == null) {
                if (ObjectUtil.getParentTree(seccion, "aula.id") != null) {
                    horariosAulas = service.allHorariosAula(seccion.getAula(), ds.getCicloAcademico());
                }
            } else {
                Aula aula = service.findAula(aulaId);
                horariosAulas = service.allHorariosAula(aula, ds.getCicloAcademico());
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

                ObjectNode jsonDiaHoraGrupoEach = JsonHelper.createJson(diaHoraGrupoEach, factory, true,
                        new String[]{"*",
                            "dia.id", "dia.nombre",
                            "hora.id", "hora.codigo", "hora.numero", "hora.descripcion",
                            "grupoHorario.id", "grupoHorario.codigo",
                            "grupoHorario.tipoGrupoHoras.*"});
                jsonDiaHoraGrupoEach.put("seleccionado", Boolean.FALSE);

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
                jsonDiaHoraGrupo.putPOJO(diaHoraGrupoEach.getIdDiaHora(), jsonDiaHoraGrupoEach);
            }

            ObjectNode jsonHorarioAula = new ObjectNode(factory);
            if (horariosAulas != null) {
                for (HorarioAula horarioAulaEach : horariosAulas) {
                    if (!horarioAulaEach.getSeccion().getId().equals(seccion.getId())) {
                        jsonHorarioAula.putPOJO(horarioAulaEach.getIdDiaHora(),
                                JsonHelper.createJson(horarioAulaEach, factory, true,
                                        new String[]{"*", "dia.*", "hora.*", "aula.*", "seccion.id", "seccion.codigo2"}));
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
                horasJson.add(JsonHelper.createJson(horasEncontrada, factory, true, new String[]{"*"}));
            }

            data.set("dias", diasJson);
            data.set("horas", horasJson);
            data.set("jsonDiaHoraGrupo", jsonDiaHoraGrupo);
            data.set("jsonHorarioAula", jsonHorarioAula);
            data.putPOJO("grupoHorasSeleccionado",
                    JsonHelper.createJson(grupoHoras, factory, true, new String[]{"*", "tipoGrupoHoras.*"}));

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

            GrupoHoras grupoHoras = service.findGrupoHoras(new GrupoHoras(grupoHorarioId), ds.getCicloAcademico());
            Seccion seccion = service.findSeccion(seccionId);

            List<Dia> dias = service.allDia();
            List<Hora> horasEncontradas = service.allHora();

            List<HorarioAula> horariosAulas = null;
            if (ObjectUtil.getParentTree(seccion, "aula.id") != null) {
                horariosAulas = service.allHorariosAula(seccion.getAula(), ds.getCicloAcademico());
            }

            ObjectNode jsonDiaHoraGrupo = new ObjectNode(factory);

            ObjectNode jspnGrupoHoras = JsonHelper.createJson(grupoHoras, factory, true, new String[]{"*", "tipoGrupoHoras.*"});

            for (Dia diaEach : dias) {
                for (Hora horaEach : horasEncontradas) {
                    Long diaId = diaEach.getId();
                    Long horaId = horaEach.getId();

                    ObjectNode jsonDiaHoraGrupoEach = new ObjectNode(factory);
                    jsonDiaHoraGrupoEach.putPOJO("dia", JsonHelper.createJson(diaEach, factory));
                    jsonDiaHoraGrupoEach.putPOJO("hora", JsonHelper.createJson(horaEach, factory));
                    jsonDiaHoraGrupoEach.put("seleccionado", Boolean.FALSE);

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
                    if (!horarioAulaEach.getSeccion().getId().equals(seccion.getId())) {
                        jsonHorarioAula.putPOJO(horarioAulaEach.getIdDiaHora(),
                                JsonHelper.createJson(horarioAulaEach, factory, true,
                                        new String[]{"id",
                                            "dia.id", "dia.nombre",
                                            "hora.id", "hora.codigo", "hora.descripcion",
                                            "aula.id", "aula.nombre", "aula.codigo",
                                            "seccion.id", "seccion.codigo2"}));
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
            data.putPOJO("grupoHorasSeleccionado", jspnGrupoHoras);

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
    public DynatableResponse listGrupoHorariosZetas(DynatableFilter filter, HttpSession session) {
        DynatableResponse json = new DynatableResponse();

        try {
            long t1 = System.currentTimeMillis();

            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
            List<GrupoHoras> gruposHoras = service.allGrupoHorasZetasDyna(filter, ds.getCicloAcademico());

            JsonNodeFactory nf = JsonNodeFactory.instance;
            ArrayNode array = new ArrayNode(nf);
            for (GrupoHoras grupoHoraEach : gruposHoras) {
                ObjectNode node = new ObjectNode(JsonNodeFactory.instance);
                node.put("id", grupoHoraEach.getId());
                node.put("codigo", grupoHoraEach.getCodigo());
                node.put("letra", grupoHoraEach.getLetra());
                node.put("tipoCiclo", grupoHoraEach.getTipoCiclo());
                node.put("tipoGrupoHoras", grupoHoraEach.getTipoGrupoHoras().getCodigo());
                node.put("tipoSeccion", grupoHoraEach.getTipoSeccion());
                node.put("color", grupoHoraEach.getColor());
                node.put("horas", grupoHoraEach.getDiaHoraGrupo().size());
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
    @RequestMapping("listGrupoHorariosEspeciales")
    public DynatableResponse listGrupoHorariosEspeciales(DynatableFilter filter,
            @RequestParam(name = "tipoGrupoHora", required = false) String tipoGrupoHora,
            HttpSession session) {

        DynatableResponse json = new DynatableResponse();

        try {
            long t1 = System.currentTimeMillis();
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
            CicloAcademico cicloAcademico = ds.getCicloAcademico();

            JsonNodeFactory nf = JsonNodeFactory.instance;
            ArrayNode array = new ArrayNode(nf);

            Seccion seccion = null;
            if ((filter.getQueries() != null && !filter.getQueries().isEmpty())
                    && filter.getQueries().get("seccion") != null) {
                seccion = service.findSeccion(TypesUtil.getLong(filter.getQueries().get("seccion")));
                long t3 = System.currentTimeMillis();
            }
            if (seccion != null) {
                TipoGrupoHoras tipoGrupoHoras = service.findTipoGpoByEnumCiclo(TipoGrupoHorasEnum.ESPECIAL, cicloAcademico);
                List<GrupoHoras> gruposHoras = service.allGrupoByTipoGpoSeccionDynatable(filter, tipoGrupoHoras, cicloAcademico, seccion);

                for (GrupoHoras grupoHoraEach : gruposHoras) {
                    ObjectNode node = new ObjectNode(JsonNodeFactory.instance);
                    node.put("id", grupoHoraEach.getId());
                    node.put("codigo", grupoHoraEach.getCodigo());
                    node.put("letra", grupoHoraEach.getLetra());
                    node.put("tipoCiclo", grupoHoraEach.getTipoCiclo());
                    node.put("tipoGrupoHoras", grupoHoraEach.getTipoGrupoHoras().getCodigo());
                    node.put("tipoSeccion", grupoHoraEach.getTipoSeccion());
                    node.put("color", grupoHoraEach.getColor());
                    node.put("horas", grupoHoraEach.getDiaHoraGrupo().size());

                    array.add(node);
                }
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
    @RequestMapping("{seccion}/saveSeccionGrupo")
    public JsonResponse saveSeccionGrupo(
            @PathVariable("seccion") Long seccionId,
            @RequestBody GrupoHoras grupoHoras, HttpSession session) {

        JsonResponse response = new JsonResponse();
        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);

            String message = "Grupo hora asignado correctamente.";
            Seccion seccion = service.findSeccion(seccionId);
            service.saveSeccionGrupoHorario(seccion, grupoHoras, ds.getCicloAcademico());

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
    @RequestMapping("{seccion}/saveTipoRepRestriccion")
    public JsonResponse saveTipoRepRestriccion(
            @PathVariable("seccion") Long seccionId,
            @RequestBody ArrayNode tiposRepitencias,
            RedirectAttributes redirectAttr,
            HttpSession session) {
        JsonResponse response = new JsonResponse();
        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
            List<TipoRepitencia> tiposRepitencia = new ArrayList();
            for (JsonNode it : tiposRepitencias) {
                ObjectNode tipoRepitencia = (ObjectNode) it;
                tiposRepitencia.add((TipoRepitencia) JsonHelper.fromJson(tipoRepitencia.toString(), TipoRepitencia.class));
            }
            service.saveTipoRepitenciaRestriccion(new Seccion(seccionId), tiposRepitencia, ds);

            String message = "Tipo repitencia asignada correctamente.";

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
            if (aulaId == null) {
                message = "Aula removida correctamente.";
            }
            service.saveAula(new Seccion(seccionId), new Aula(aulaId), ds);

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
            @RequestParam(name = "aula", required = false) Long pabellonId,
            RedirectAttributes redirectAttr,
            HttpSession session) {

        JsonResponse response = new JsonResponse();
        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);

            Oficina oficinaOERA = service.findOficinaOera();
            List<Oficina> oficinas = oficinaContenida(ds.getOficinas(), oficinaOERA) ? service.allOficinas(ds.getCompania()) : ds.getOficinas();
            List<Aula> oficinasAulasSuperiores = service.allPabellonesByOficinasNoOera(oficinas);

            JsonNodeFactory nc = JsonNodeFactory.instance;
            ObjectNode nodeData = new ObjectNode(nc);
            Seccion seccion = service.findSeccion(seccionId);
            Aula aula = seccion.getAula();

            ArrayNode argAulas = new ArrayNode(nc);
            List<Aula> aulas = service.allAulasByPabellon(seccion, new Aula(pabellonId), ds.getCicloAcademico());
            for (Aula aulaEach : aulas) {
//                logger.debug("aula {}, disponible {}, secciones {}", aulaEach.getId(), aulaEach.isDisponible(), aulaEach.getSeccion() == null ? 0 : aulaEach.getSeccion().size());
                ObjectNode aulaJson = JsonHelper.createJson(aulaEach, nc, true, new String[]{"*"});
                aulaJson.put("seleccionado", Boolean.FALSE);

                if (aulaEach.getOficinaSupervisora().getId().equals(Constantine.ID_OFICINA_OERA)) {
                    aulaJson.put("tabAula", "oera");
                } else if (oficinasAulasSuperiores.contains(aulaEach.getAulaSuperior())) {
                    aulaJson.put("tabAula", "oficina");
                } else {
                    aulaJson.put("tabAula", "especifica");
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
    @RequestMapping("generarpagodocente")
    public JsonResponse generarpagodocente(DocenteSeccion docenteSeccion,
            RedirectAttributes redirectAttr,
            HttpSession session) {

        JsonResponse response = new JsonResponse();
        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
            service.generarpagodocente(docenteSeccion, ds);
            response.setMessage(Messages.UPDATED);
            response.setSuccess(true);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (RuntimeException e) {
            ExceptionHandler.handleSpecial(e, response, Messages.ERROR_GENERAL);
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

    @ResponseBody
    @RequestMapping("{gruposeccion}/clonar/{veces}")
    public JsonResponse clonar(@PathVariable("gruposeccion") Long gruposeccionId, @PathVariable Integer veces, Model model, HttpSession session) {
        JsonResponse response = new JsonResponse();
        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
            List<GrupoSeccion> gps = service.clonar(new GrupoSeccion(gruposeccionId), veces, ds);
            ArrayNode arr = new ArrayNode(JsonNodeFactory.instance);
            String ids = String.join(",", gps.stream().map(x -> x.getId().toString()).collect(Collectors.toList()));

            for (GrupoSeccion gp : gps) {
                arr.add(gp.getId());
            }
            response.setData(ids);
            response.setMessage(String.format("%d nuevos registros agregados", veces));
            response.setSuccess(true);
        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        } finally {
            return response;
        }
    }

    private ObjectNode createGpoSeccionJson(GrupoSeccion gpoSeccion, String fechaMin, String fechaMax, DataSessionPivot ds) {
        ObjectNode nodeGpoSecc = JsonHelper.createJson(gpoSeccion, JsonNodeFactory.instance, true, new String[]{
            "id", "estado", "estadoEnum", "codigo2", "cursoDirigido",
            "tipoDictado","fechaInicioModular","fechaFinModular",
            "curso.id",
            "curso.codigo",
            "curso.nombre",
            "curso.tpc",
            "curso.precioFormato",
            "curso.precioTpcFormato",
            "curso.tipoCursoEnum",
            "curso.departamentoAcademico.codigo",
            "curso.departamentoAcademico.nombre",
            "curso.tipoCursoTEO",
            "curso.tipoCursoPRA",
            "curso.tipoCursoTEOPRA",
            "anexoBoletin.codigo",
            "anexoBoletin.nombre",
            "anexoBoletin.anexoSuperior.codigo",
            "anexoBoletin.anexoSuperior.nombre"
        });
        
        nodeGpoSecc.put("tipoDictadoCheck",TipoDictadoGrupoSeccionEnum.MOD.name().equalsIgnoreCase(gpoSeccion.getTipoDictado()));
        

        CursoCicloAcademico cca = service.findCursoCicloAcademico(gpoSeccion.getCurso(), ds.getCicloAcademico());

        ArrayNode arraySecciones = new ArrayNode(JsonNodeFactory.instance);
        List<Seccion> secciones = gpoSeccion.getSecciones();
        for (Seccion seccion : secciones) {
            ObjectNode nodeSecc = JsonHelper.createJson(seccion, JsonNodeFactory.instance, true, new String[]{
                "*",
                "grupoHoras.id",
                "grupoHoras.codigo",
                "aula.id",
                "aula.codigo",
                "aula.capacidadAula",
                "restriccionesCarrera.id",
                "restriccionesCarrera.carrera.codigo",
                "restriccionesCarrera.carrera.nombre",
                "restriccionesFacultad.id",
                "restriccionesFacultad.facultad.codigo",
                "restriccionesFacultad.facultad.nombre",
                "restriccionesModalidad.id",
                "restriccionesModalidad.modalidadEstudio.codigo",
                "restriccionesModalidad.modalidadEstudio.nombre",
                "restriccionesRepitencia.id",
                "restriccionesRepitencia.tipoRepitencia.codigo",
                "restriccionesRepitencia.tipoRepitencia.id",
                "docenteSeccion.estadoEnum",
                "docenteSeccion.principal",
                "docenteSeccion.porcentajeCarga",
                "docenteSeccion.docente.codigo",
                "docenteSeccion.docente.persona.apellidosNombres",
                "ampliacionesVacantes.*",
                "ampliacionesVacantes.seccion.id",
                "ampliacionesVacantes.colaborador.id",
                "ampliacionesVacantes.colaborador.cargo.nombre",
                "ampliacionesVacantes.colaborador.persona.nombreCompleto",
                "ampliacionesVacantes.oficina.id",
                "ampliacionesVacantes.oficina.nombre",
                "cambioAulaGrupos.*",
                "cambioAulaGrupos.seccion.id",
                "cambioAulaGrupos.colaborador.id",
                "cambioAulaGrupos.colaborador.cargo.nombre",
                "cambioAulaGrupos.colaborador.persona.nombreCompleto",
                "cambioAulaGrupos.oficina.id",
                "cambioAulaGrupos.oficina.nombre",
                "cambioAulaGrupos.aulaInicio.id",
                "cambioAulaGrupos.aulaInicio.codigo",
                "cambioAulaGrupos.aulaFin.id",
                "cambioAulaGrupos.aulaFin.codigo",
                "cambioAulaGrupos.grupoHorasInicio.id",
                "cambioAulaGrupos.grupoHorasInicio.codigo",
                "cambioAulaGrupos.grupoHorasFin.id",
                "cambioAulaGrupos.grupoHorasFin.codigo"});

            BigDecimal porcentajeAvance = BigDecimal.ZERO;
            for (DocenteSeccion docSeccion : seccion.getDocenteSeccion()) {
                if (docSeccion.getPorcentajeCarga() != null) {
                    porcentajeAvance = porcentajeAvance.add(docSeccion.getPorcentajeCarga());
                }
            }
            nodeSecc.put("porcentajeAvance", porcentajeAvance);
            nodeSecc.put("tipoSeccionEvaluacionValue", seccion.getTipoSeccionEnum().getTipoSeccionEvalEnum().getValue());
            nodeSecc.put("editVacantes", Boolean.FALSE);
            nodeSecc.put("editRestriccionCapa", Boolean.FALSE);

            List<DocenteSeccion> docentesSeccion = seccion.getDocenteSeccion();
            ArrayNode arrayProfeSecc = new ArrayNode(JsonNodeFactory.instance);

            for (DocenteSeccion docSeccion : docentesSeccion) {
                ObjectNode nodeProfe = JsonHelper.createJson(docSeccion, JsonNodeFactory.instance, true, new String[]{
                    "*",
                    "docente.codigo",
                    "docente.persona.id",
                    "docente.persona.apellidosNombres"
                });
                nodeProfe.put("docenteNN", docSeccion.getDocente().getCodigo().equals(Constantine.DOCENTE_INDETERMINADO));
                nodeProfe.put("fechaInicioMin", fechaMin);
                nodeProfe.put("fechaFinMax", fechaMax);
                arrayProfeSecc.add(nodeProfe);

            }

            nodeSecc.set("docenteSeccion", arrayProfeSecc);
            nodeSecc.put("minimoAlumnos", cca.getMinimoAlumnos());
            arraySecciones.add(nodeSecc);
        }
        nodeGpoSecc.set("secciones", arraySecciones);
        return nodeGpoSecc;
    }

    private ObjectNode createCicloJson(CicloAcademico ciclo) {
        ObjectNode nodeJson = JsonHelper.createJson(ciclo, JsonNodeFactory.instance, true, new String[]{
            "id", "codigo", "descripcion", "descripcion2", "tipo",
            "modalidadEstudio.codigo",
            "modalidadEstudio.nombre"
        });
        return nodeJson;
    }

    private ObjectNode createNavegationJson(String ruta, List<GrupoSeccion> ids, Long idGpoSeccEdit, CicloAcademico ciclo) {
        ObjectNode nodeJson = new ObjectNode(JsonNodeFactory.instance);
        Integer position = -1;
        Long next = null;
        Long prev = null;

        DynatableFilter filter = createFilter(ruta);
        List<GrupoSeccion> gpoSecciones;
        if (ids.size() > 0) {
            gpoSecciones = service.allCleanByDynatableGruposSeccion(filter, ciclo, ids);
        } else {
            gpoSecciones = service.allCleanByDynatable(filter, ciclo);
        }

        ArrayNode arrayGpoSeccJson = new ArrayNode(JsonNodeFactory.instance);

        Integer loop = 0;
        for (GrupoSeccion gpoSecc : gpoSecciones) {
            Long idGpoSecc = gpoSecc.getId();
            ObjectNode nodeGpoSecc = new ObjectNode(JsonNodeFactory.instance);
            arrayGpoSeccJson.add(nodeGpoSecc.put("id", idGpoSecc));

            if (position + 1 == loop && position >= 0) {
                next = idGpoSecc;
            }

            if (idGpoSecc.longValue() == idGpoSeccEdit) {
                position = loop;
            }
            if (position < 0) {
                prev = idGpoSecc;
            }
            loop++;
        }

        nodeJson.set("arrayGpoSecciones", arrayGpoSeccJson);
        nodeJson.put("position", position);
        nodeJson.put("current", idGpoSeccEdit);
        nodeJson.put("next", next);
        nodeJson.put("prev", prev);
        nodeJson.put("origen", ruta);
        return nodeJson;
    }

    private String getOrigen(String origen) {
        if (StringUtils.isEmpty(origen)) {
            return "/academico/gposeccion";
        }
        byte[] decoded = Base64.getMimeDecoder().decode(origen);
        String output = new String(decoded);
        return output;
    }

    private DynatableFilter createFilter(String ruta) {
        DynatableFilter filter = new DynatableFilter();
        filter.setPage(1);
        filter.setOffset(0);
        filter.setPerPage(1000000);

        try {
            ruta = java.net.URLDecoder.decode(ruta, "UTF-8");
        } catch (UnsupportedEncodingException ex) {
            ex.printStackTrace();
        }

        int inicio = ruta.indexOf("?");
        if (inicio < 0) {
            return filter;
        }

        String analizar = ruta.substring(inicio + 1);
        String[] tramos = analizar.split("&");
        Map<String, Object> queries = new HashMap();

        for (String tramo : tramos) {
            if (tramo.startsWith("queries")) {
                String[] partes = tramo.split("=");
                String key = getQuerieFilter(partes[0]);
                String value = partes[1];
                queries.put(key, value);
            }
        }

        filter.setQueries(queries);

        return filter;
    }

    private String getQuerieFilter(String param) {
        String resto = param.substring(0, param.length() - 1);
        resto = resto.substring(8);
        return resto;
    }

    private boolean oficinaContenida(List<Oficina> oficinas, Oficina ofi) {
        for (Oficina oficina : oficinas) {
            if (ofi.getId() == oficina.getId().longValue()) {
                return true;
            }
        }
        return false;
    }

    private boolean aulaContenida(List<Aula> aulas, Aula ambiente) {
        if (ambiente == null) {
            return false;
        }
        for (Aula aula : aulas) {
            if (ambiente.getId() == aula.getId().longValue()) {
                return true;
            }
        }
        return false;
    }

    private List<GrupoSeccion> obtenerGruposSeccion(String ids) {
        if (StringUtils.isEmpty(ids)) {
            return new ArrayList();
        }
        byte[] decoded = Base64.getMimeDecoder().decode(ids);
        String output = new String(decoded);
        String[] str = output.split(",");
        List<GrupoSeccion> gpos = new ArrayList<>();
        for (String string : str) {
            gpos.add(new GrupoSeccion(Long.parseLong(string)));
        }
        return gpos;
    }

    private ObjectNode createResumenJson(GpoSeccionResumen resumen) {
        ObjectNode nodeJson = JsonHelper.createJson(resumen, JsonNodeFactory.instance, true, new String[]{"*"});
        return nodeJson;
    }

    private ArrayNode createOficinasJson(List<Oficina> oficinas) {
        ArrayNode array = new ArrayNode(JsonNodeFactory.instance);
        for (Oficina oficina : oficinas) {
            ObjectNode node = JsonHelper.createJson(oficina, JsonNodeFactory.instance, true, new String[]{
                "*"
            });
            array.add(node);
        }
        return array;
    }

    @ResponseBody
    @RequestMapping("recrearVacanteAlumno")
    public JsonResponse recrearVacanteAlumno(Model model, HttpSession session) {
        JsonResponse response = new JsonResponse();
        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
            CicloAcademico ciclo = ds.getCicloAcademico();

            service.recrearVacanteAlumno(ciclo, ds);

            response.setMessage("Se recreo los VacanteAlumno satisfactoriamente");
            response.setSuccess(true);
        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        } finally {
            return response;
        }
    }

}
