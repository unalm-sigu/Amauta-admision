package pe.edu.lamolina.pivot.controller.academico.asistenciaacademica;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.beans.PropertyEditorSupport;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import javax.servlet.http.HttpSession;
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
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableResponse;
import pe.albatross.zelpers.miscelanea.ExceptionHandler;
import pe.albatross.zelpers.miscelanea.JsonHelper;
import pe.albatross.zelpers.miscelanea.JsonResponse;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Curso;
import pe.edu.lamolina.model.academico.Docente;
import pe.edu.lamolina.model.academico.DocenteSeccion;
import pe.edu.lamolina.model.academico.GrupoSeccion;
import pe.edu.lamolina.model.academico.MatriculaSeccion;
import pe.edu.lamolina.model.academico.Seccion;
import pe.edu.lamolina.model.academico.TemaLeccion;
import pe.edu.lamolina.model.general.Aula;
import pe.edu.lamolina.model.horario.HorarioSeccion;
import pe.edu.lamolina.model.horario.LeccionReprogramada;
import pe.edu.lamolina.pivot.controller.academico.notasacademicas.CargaAcademicaService;
import pe.edu.lamolina.pivot.zelper.constant.Constantine;
import pe.edu.lamolina.pivot.zelper.constant.Messages;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

@Controller
@RequestMapping("academico/docente/asistenciaacademica")
public class AsistenciaAcademicaController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    CargaAcademicaService cargaAcademicaService;

    @Autowired
    AsistenciaAcademicaService asistenciaAcademicaService;

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
        model.addAttribute("docente", ds.getDocente());
        model.addAttribute("cicloAcademico", ds.getCicloAcademico());
        logger.debug("el docente logeado es {}", ds.getDocente().getId());
        //    cargaAcademicaService.createEvaluacionSeccionPorDocente(ds.getDocente(), ds);

        model.addAttribute("dptoAcad", ds.getDepartamentoAcademico());

        return "academico/docente/asistenciaacademica/asistenciaAcademica";
    }

    @ResponseBody
    @RequestMapping("listGruposSecciones")
    public DynatableResponse listGruposSecciones(DynatableFilter filter, HttpSession session) {
        DynatableResponse json = new DynatableResponse();
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);

        try {

            ArrayNode array = new ArrayNode(JsonNodeFactory.instance);
            CicloAcademico ciclo = ds.getCicloAcademico();
            Docente docente = ds.getDocente();

            List<GrupoSeccion> gruposSeccion = cargaAcademicaService.allGrupoByDocente(docente, ciclo, ds);
            logger.debug(this.getClass() + " Lista grupos por docente {}", gruposSeccion.size());

            for (GrupoSeccion grupoSeccion : gruposSeccion) {
                for (Seccion seccionEach : grupoSeccion.getSecciones()) {
                    seccionEach.setVerInformacion(Boolean.FALSE);
                    for (DocenteSeccion docenteSeccionEach : seccionEach.getDocenteSeccion()) {
                        if (docenteSeccionEach.getDocente().getId().equals(docente.getId())) {
                            seccionEach.setVerInformacion(Boolean.TRUE);
                        }
                    }
                }
                ObjectNode node = grupoSeccion.toJson();

                node.put("estadoGrupoEnum", grupoSeccion.getEstadoGrupoEnum().getValue());
                node.put("estadoGrupoCerrado", grupoSeccion.isEstadoGrupoCerrado());
                node.put("estadoGrupoCerrado", grupoSeccion.isEstadoGrupoCerrado());

                array.add(node);
            }

            json.setData(array);
            json.setTotal(gruposSeccion.size());
            json.setFiltered(gruposSeccion.size());

        } catch (Exception e) {
            e.printStackTrace();
            json.setTotal(0);
        }
        return json;
    }

    @ResponseBody
    @RequestMapping("listLeccionesAcademicas")
    public DynatableResponse listLeccionesAcademicas(DynatableFilter filter,
            @RequestParam(name = "seccion", required = true) Long seccionId,
            HttpSession session) {
        DynatableResponse json = new DynatableResponse();
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);

        try {

            ArrayNode array = new ArrayNode(JsonNodeFactory.instance);
            CicloAcademico ciclo = ds.getCicloAcademico();
            Docente docente = ds.getDocente();
            DateTime dateTime = new DateTime();

            List<TemaLeccion> lecciones = asistenciaAcademicaService.allTemaLeccionBySeccionDocenteDyna(
                    new Seccion(seccionId), docente, filter
            );
            logger.debug(this.getClass() + " Cantidad de lecciones {}", lecciones.size());

            for (TemaLeccion leccion : lecciones) {
                DateTime editLimitDATE = new DateTime().plusDays(pe.edu.lamolina.model.miscelaneo.Constantine.DAYS_EDIT_TEMA_CICLO * -1);
                DateTime fechaRegistro = new DateTime(leccion.getFechaRegistro());
                ObjectNode node = leccion.toJson();
                node.put("allowEdit", leccion.isAllowEdit());
                array.add(node);
            }

            json.setData(array);
            json.setTotal(lecciones.size());
            json.setFiltered(lecciones.size());

        } catch (Exception e) {
            e.printStackTrace();
            json.setTotal(0);
        }
        return json;
    }

    @RequestMapping("{seccion}/lecciones")
    public String lecciones(
            @PathVariable("seccion") Long idSeccion,
            Model model, HttpSession session) {
        logger.debug("la seccion es {}", idSeccion);
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        DateTime today = new DateTime();

        Seccion seccion = asistenciaAcademicaService.findSeccionDia(new Seccion(idSeccion), today);
        logger.debug("Seccion {}, Grupo Seccion {}", seccion.getId(), seccion.getGrupoSeccion().getId());
        GrupoSeccion grupoSeccion = cargaAcademicaService.findGrupo(seccion.getGrupoSeccion().getId());
        Curso curso = grupoSeccion.getCurso();

        model.addAttribute("seccion", seccion);
        model.addAttribute("seccionJson", seccion.toJson().toString());
        model.addAttribute("grupoSeccion", grupoSeccion);
        model.addAttribute("curso", curso);
        model.addAttribute("cicloAcademico", ds.getCicloAcademico());
        model.addAttribute("docente", ds.getDocente());
        model.addAttribute("dptoAcad", ds.getDepartamentoAcademico());
        return "academico/docente/asistenciaacademica/leccionesAcademicas";
    }

    @RequestMapping("{leccion}/editar")
    public String editar(
            @PathVariable("leccion") Long idTemaLeccion,
            Model model, HttpSession session) {
        logger.debug("la tema leccion es {}", idTemaLeccion);
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        DateTime today = new DateTime();

        TemaLeccion temaLeccion = asistenciaAcademicaService.findTemaLeccion(idTemaLeccion);
        if (temaLeccion == null) {
            temaLeccion = new TemaLeccion();
        }

        Seccion seccion = asistenciaAcademicaService.findSeccionDia(temaLeccion.getSeccion(), new DateTime(temaLeccion.getFecha()));
        logger.debug("Seccion {}, Grupo Seccion {}", seccion.getId(), seccion.getGrupoSeccion().getId());
        GrupoSeccion grupoSeccion = cargaAcademicaService.findGrupo(seccion.getGrupoSeccion().getId());
        Curso curso = grupoSeccion.getCurso();

        model.addAttribute("seccion", seccion);
        model.addAttribute("seccionJson", seccion.toJson().toString());
        model.addAttribute("temaLeccionJson", temaLeccion.toJson().toString());
        model.addAttribute("grupoSeccion", grupoSeccion);
        model.addAttribute("curso", curso);

        return "academico/docente/asistenciaacademica/controlAsistenciaForm";
    }

    @RequestMapping("{seccion}/control")
    public String control(
            @PathVariable("seccion") Long idSeccion,
            Model model, HttpSession session) {
        logger.debug("la seccion es {}", idSeccion);
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        DateTime today = new DateTime();

        TemaLeccion temaLeccion = asistenciaAcademicaService.findTemaLeccionSeccionDocenteFecha(new Seccion(idSeccion), ds.getDocente(), today);
        if (temaLeccion == null) {
            temaLeccion = new TemaLeccion();
        }

        Seccion seccion = asistenciaAcademicaService.findSeccionDia(new Seccion(idSeccion), today);
        logger.debug("Seccion {}, Grupo Seccion {}", seccion.getId(), seccion.getGrupoSeccion().getId());
        GrupoSeccion grupoSeccion = cargaAcademicaService.findGrupo(seccion.getGrupoSeccion().getId());
        Curso curso = grupoSeccion.getCurso();

        model.addAttribute("seccion", seccion);
        model.addAttribute("seccionJson", seccion.toJson().toString());
        model.addAttribute("temaLeccionJson", temaLeccion.toJson().toString());
        model.addAttribute("grupoSeccion", grupoSeccion);
        model.addAttribute("curso", curso);

        return "academico/docente/asistenciaacademica/controlAsistenciaForm";
    }

    @ResponseBody
    @RequestMapping("loadAsistenciaAcademicaForm")
    public JsonResponse loadGpoSeccionForm(@RequestParam(name = "seccion") Long seccionId, HttpSession session) {
        JsonResponse jsonResponse = new JsonResponse();
        JsonNodeFactory nc = JsonNodeFactory.instance;
        ObjectNode data = new ObjectNode(nc);

        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);

        jsonResponse.setSuccess(true);
        jsonResponse.setData(data);
        return jsonResponse;
    }

    @ResponseBody
    @RequestMapping("listMatriculasSeccionDyna")
    public DynatableResponse listMatriculasSeccionDyna(
            DynatableFilter filter,
            @RequestParam(name = "seccion", required = true) Long seccionId,
            @RequestParam(name = "temaLeccion", required = false) Long temaLeccionId,
            HttpSession session) {
        DynatableResponse json = new DynatableResponse();
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);

        try {
            DateTime today = new DateTime();
            if (temaLeccionId != null) {
                TemaLeccion temaLeccion = asistenciaAcademicaService.findTemaLeccion(temaLeccionId);
                today = new DateTime(temaLeccion.getFecha());
            }
            List<MatriculaSeccion> matriculasSeccionByFilter = asistenciaAcademicaService.allMatriculaSeccionBySeccion(new Seccion(seccionId), ds.getDocente(), today);
            ArrayNode array = new ArrayNode(JsonNodeFactory.instance);

            for (MatriculaSeccion matriculaSeccionEach : matriculasSeccionByFilter) {
                array.addPOJO(matriculaSeccionEach.toJson());
            }

            json.setData(array);

            json.setTotal(matriculasSeccionByFilter.size());
            json.setFiltered(matriculasSeccionByFilter.size());

        } catch (Exception e) {
            e.printStackTrace();
            json.setTotal(0);
        }
        return json;
    }

    @ResponseBody
    @RequestMapping("saveAsistencia")
    public JsonResponse saveAsistencia(
            @RequestBody TemaLeccion temaLeccion,
            Model model,
            HttpSession session) {
        JsonResponse response = new JsonResponse();
        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);

            for (MatriculaSeccion matriculaSeccion : temaLeccion.getSeccion().getMatriculaSeccion()) {

                List<HorarioSeccion> horariosSeccion = matriculaSeccion.getSeccion().getHorarioSeccion().stream().filter(x -> !x.isSeleccionado()).collect(Collectors.toList());
                matriculaSeccion.getSeccion().setHorarioSeccion(horariosSeccion);
            }

            if (temaLeccion.getId() == null) {
                asistenciaAcademicaService.saveInasistencia(temaLeccion, ds.getDocente(), ds.getUsuario(), ds.getCicloAcademico());
            } else {
                asistenciaAcademicaService.updateInasistencia(temaLeccion, ds.getDocente(), ds.getUsuario(), ds.getCicloAcademico());
            }
            String message = "Asistencia guardada.";
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
    @RequestMapping("saveReprogramacion")
    public JsonResponse saveReprogramacion(
            @RequestBody LeccionReprogramada leccionReprogramada,
            Model model,
            HttpSession session) {
        JsonResponse response = new JsonResponse();
        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
            asistenciaAcademicaService.saveReprogramacion(leccionReprogramada, ds.getUsuario(), ds.getDocente(), ds.getCicloAcademico());
            String message = "Lección reprogramada.";
            response.setSuccess(true);
            response.setMessage(message);

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
    @RequestMapping("loadModalReprogramacion")
    public JsonResponse loadModalReprogramacion(
            @RequestParam(name = "seccion", required = true) Long seccionId,
            Model model,
            HttpSession session) {
        JsonResponse response = new JsonResponse();
        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
            List<Date> fechaInicioFin = asistenciaAcademicaService.findStartEndDateReschedule(
                    new Seccion(seccionId), ds.getDocente(), ds.getCicloAcademico());

            JsonNodeFactory jc = JsonNodeFactory.instance;

            Seccion seccion = asistenciaAcademicaService.findSeccion(seccionId);

            ArrayNode disabledDaysArg = new ArrayNode(jc);
            for (int i = 0; i < 7; i++) {
                boolean found = false;
                for (HorarioSeccion horSec : seccion.getHorarioSeccion()) {
                    int dia = horSec.getDia().getNumeroDia();
                    if (dia == i) {
                        found = true;
                    }
                }
                if (!found) {
                    disabledDaysArg.add(i);
                }
            }

            ArrayNode disabledDatesArg = new ArrayNode(jc);
            List<TemaLeccion> temasLeccionBySeccion = asistenciaAcademicaService.allTemaLeccionBySeccion(seccion);
            for (TemaLeccion temaLeccion : temasLeccionBySeccion) {
                disabledDatesArg.add(TypesUtil.getStringDate(temaLeccion.getFecha(), "dd/MM/yyyy"));
            }

            response.setSuccess(Boolean.TRUE);

            Date fechaInicio = fechaInicioFin.get(0);
            Date fechaFin = fechaInicioFin.get(1);

            ObjectNode data = new ObjectNode(jc);
            data.put("fechaInicio", TypesUtil.getStringDate(fechaInicio, "dd/MM/yyyy"));
            data.put("fechaFin", TypesUtil.getStringDate(fechaFin, "dd/MM/yyyy"));
            data.set("disabledDaysArg", disabledDaysArg);
            data.set("disabledDatesArg", disabledDatesArg);

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
    @RequestMapping("asyncFindAulas")
    public JsonResponse asyncFindAulas(
            @RequestParam("nombre") String nombre,
            HttpSession session) {

        JsonNodeFactory jsonFactory = JsonNodeFactory.instance;
        JsonResponse response = new JsonResponse();

        try {
            List<Aula> aulas = asistenciaAcademicaService.searchAulaByName(nombre);
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

}
