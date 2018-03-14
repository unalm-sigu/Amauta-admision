package pe.edu.lamolina.pivot.controller.academico.horariocachimbo.generar;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableResponse;
import pe.albatross.zelpers.miscelanea.ExceptionHandler;
import pe.albatross.zelpers.miscelanea.JsonResponse;
import pe.albatross.zelpers.miscelanea.ObjectUtil;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.AlumnoHorario;
import pe.edu.lamolina.model.academico.Carrera;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Curso;
import pe.edu.lamolina.model.academico.DocenteSeccion;
import pe.edu.lamolina.model.academico.Facultad;
import pe.edu.lamolina.model.academico.GrupoSeccion;
import pe.edu.lamolina.model.academico.ModalidadEstudio;
import pe.edu.lamolina.model.academico.Seccion;
import pe.edu.lamolina.model.enums.EstadoAlumnoHorarioEnum;
import pe.edu.lamolina.model.enums.EstadoEnum;
import pe.edu.lamolina.model.general.Dia;
import pe.edu.lamolina.model.general.Persona;
import pe.edu.lamolina.model.horario.Hora;
import pe.edu.lamolina.model.horario.HorarioCachimbos;
import pe.edu.lamolina.model.horario.HorarioSeccion;
import pe.edu.lamolina.model.horario.SeccionHorarioCachimbos;
import pe.edu.lamolina.model.seguridad.Usuario;
import pe.edu.lamolina.pivot.zelper.constant.Constantine;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

@Controller
@RequestMapping("academico/horariocachimbo/generar")
public class HorarioCachimboGenerarController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    HorarioCachimboGenerarService service;

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
        model.addAttribute("cicloAcademico", ds.getCicloAcademico());
        return "academico/horariocachimbo/generar/horarioCachimboGenerar";
    }

    @ResponseBody
    @RequestMapping("list")
    public DynatableResponse list(DynatableFilter filter, HttpSession session) {
        DynatableResponse json = new DynatableResponse();
        try {

            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
            CicloAcademico cicloAcademico = ds.getCicloAcademico();
            List<HorarioCachimbos> horarioCachimbos = service.allHorarioCachimbos(filter, cicloAcademico);
            ArrayNode array = new ArrayNode(JsonNodeFactory.instance);

            for (HorarioCachimbos horarioCachimbo : horarioCachimbos) {
                ObjectNode node = new ObjectNode(JsonNodeFactory.instance);
                node.put("id", horarioCachimbo.getId());
                node.put("codigo", horarioCachimbo.getCodigo());
                node.put("carrera", horarioCachimbo.getCarrera().getNombre());
                node.put("cursos", horarioCachimbo.getCursos());
                node.put("capacidad", horarioCachimbo.getCapacidad());
                node.put("suscritos", horarioCachimbo.getSuscritos());
                node.put("matriculados", horarioCachimbo.getMatriculados());
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
    @RequestMapping("delete")
    public JsonResponse delete(HorarioCachimbos horarioCachimbos, HttpSession session) {
        JsonResponse response = new JsonResponse();
        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
            Usuario usuario = ds.getUsuario();
            service.delete(horarioCachimbos, ds.getCicloAcademico(), usuario);
            response.setMessage("Horario eliminado satisfactoriamente");
            response.setSuccess(Boolean.TRUE);
        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("deletegrupo")
    public JsonResponse deleteGrupo(HorarioCachimboForm form, HttpSession session) {
        JsonResponse response = new JsonResponse();
        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
            Usuario usuario = ds.getUsuario();
            service.delete(form, ds.getCicloAcademico(), usuario);
            response.setMessage("Horarios eliminado satisfactoriamente");
            response.setSuccess(Boolean.TRUE);
        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @RequestMapping("generador")
    public String generador(Model model, HttpSession session) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);

        ModalidadEstudio modalidadEstudio = new ModalidadEstudio(1);
        CicloAcademico cicloAcademico = ds.getCicloAcademico();
        List<Carrera> carreras = service.allCarrera(modalidadEstudio);
        model.addAttribute("cicloAcademico", cicloAcademico);
        model.addAttribute("carreras", carreras);
        return "academico/horariocachimbo/generador/horarioCachimboGenerador";
    }

    @ResponseBody
    @RequestMapping("allhorario")
    public DynatableResponse allHorario(DynatableFilter filter, Carrera carrera, HttpSession session) {

        DynatableResponse json = new DynatableResponse();
        try {

            JsonNodeFactory jsonFactory = JsonNodeFactory.instance;
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
            CicloAcademico cicloAcademico = ds.getCicloAcademico();
            ArrayNode array = new ArrayNode(jsonFactory);

            List<Curso> cursos = service.allCursoCachimbosByCicloAcademico(cicloAcademico, carrera);
            List<HorarioCachimbos> horarioCachimbos = service.allHorarioCachimbosByCicloAcademico(cicloAcademico, carrera);
            List<SeccionHorarioCachimbos> seccionHorarioCachimbos = service.allSeccionHorarioCachimbosByCursoHora(carrera, cursos, cicloAcademico);
            Map<Long, List<SeccionHorarioCachimbos>> seccionHorarioCachimbosMap = TypesUtil.convertListToMapList("seccion.grupoSeccion.curso.id", seccionHorarioCachimbos);

            for (Curso curso : cursos) {

                ObjectNode node = new ObjectNode(jsonFactory);

                node.put("curso", curso.getNombre());
                List<SeccionHorarioCachimbos> seccionHorarioCachimboLIst = seccionHorarioCachimbosMap.get(curso.getId());

                if (seccionHorarioCachimboLIst == null) {
                    seccionHorarioCachimboLIst = new ArrayList();
                }

                Map<Long, List<SeccionHorarioCachimbos>> horarioCachimbosMap = TypesUtil.convertListToMapList("horarioCachimbos.id", seccionHorarioCachimboLIst);

                ArrayNode arrayHorario = new ArrayNode(jsonFactory);

                for (HorarioCachimbos horarioCachimbo : horarioCachimbos) {

                    ObjectNode hora = new ObjectNode(jsonFactory);
                    hora.put("codigo", horarioCachimbo.getCodigo());

                    List<SeccionHorarioCachimbos> shcHorario = horarioCachimbosMap.get(horarioCachimbo.getId());
                    ArrayNode horarios = new ArrayNode(jsonFactory);
                    if (shcHorario != null) {
                        for (SeccionHorarioCachimbos shc : shcHorario) {
                            ObjectNode horaSeccion = new ObjectNode(jsonFactory);
                            horaSeccion.put("hora", service.getHoraSeccion(shc));
                            horaSeccion.put("seccion", ObjectUtil.getParentTree(shc, "seccion.codigo2").toString());
                            horaSeccion.put("grupo", ObjectUtil.getParentTree(shc, "seccion.grupoHoras.codigo").toString());
                            horarios.add(horaSeccion);
                        }
                    }

                    hora.put("horarios", horarios);
                    arrayHorario.add(hora);
                }

                node.set("horario", arrayHorario);
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

    @ResponseBody
    @RequestMapping("searchalumno")
    public JsonResponse searchAlumno(@RequestParam("nombre") String nombre, @RequestParam("horario") Long horario, HttpSession session) {
        JsonResponse response = new JsonResponse();

        try {

            JsonNodeFactory jsonFactory = JsonNodeFactory.instance;
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
            CicloAcademico cicloAcademico = ds.getCicloAcademico();
            List<AlumnoHorario> alumnos = service.allAlumnoHorarioByName(nombre, cicloAcademico, horario);
            ArrayNode jsonList = new ArrayNode(jsonFactory);

            for (AlumnoHorario alumnoHorario : alumnos) {
                ObjectNode json = new ObjectNode(jsonFactory);
                Alumno alumno = alumnoHorario.getAlumno();
                Persona persona = alumno.getPersona();

                json.put("id", alumnoHorario.getId());
                json.put("nombre", persona.getNombreCompleto());
                json.put("codigoMatricula", alumnoHorario.getAlumno().getCodigo());
                json.put("carrera", alumno.getCarrera().getNombre());
                json.put("facultad", alumno.getCarrera().getFacultad().getNombre());
                json.put("tipo", persona.getTipoDocumento().getSimbolo());
                json.put("numero", persona.getNumeroDocIdentidad());
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
    @RequestMapping("generar")
    public JsonResponse generar(HttpSession session) {
        JsonResponse response = new JsonResponse();
        try {
            JsonNodeFactory jsonFactory = JsonNodeFactory.instance;
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
            CicloAcademico cicloAcademico = ds.getCicloAcademico();
            ModalidadEstudio modalidad = service.findModalidadPregrado();
            service.generar(cicloAcademico, modalidad, ds);
            ArrayNode node = new ArrayNode(jsonFactory);
            response.setData(node);
            response.setMessage("Horario generado satisfactoriamente");
            response.setSuccess(true);
        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("allhorarioheader")
    public JsonResponse allHorarioHeader(Carrera carrera, HttpSession session) {

        JsonResponse response = new JsonResponse();

        try {

            JsonNodeFactory jsonFactory = JsonNodeFactory.instance;
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
            CicloAcademico cicloAcademico = ds.getCicloAcademico();

            List<HorarioCachimbos> horarioCachimbos = service.allHorarioCachimbosByCicloAcademico(cicloAcademico, carrera);
            ArrayNode jsonList = new ArrayNode(jsonFactory);

            for (HorarioCachimbos alumnoHorario : horarioCachimbos) {
                ObjectNode json = new ObjectNode(jsonFactory);
                json.put("id", alumnoHorario.getId());
                json.put("codigo", alumnoHorario.getCodigo());
                json.put("capacidad", alumnoHorario.getCapacidad());
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
    @RequestMapping("openhorario")
    public JsonResponse openHorario(HorarioCachimbos horario, HttpSession session) {

        JsonResponse response = new JsonResponse();

        try {

            JsonNodeFactory jsonFactory = JsonNodeFactory.instance;

            List<HorarioSeccion> seccionHorario = service.allSeccionHorarioCachimbosByHorarioCachimbos(horario);

            Map<Long, List<HorarioSeccion>> seccionHorarioHoras = TypesUtil.convertListToMapList("hora.id", seccionHorario);
            Map<Long, Hora> seccionHorarioHorasMap = TypesUtil.convertListToMap("hora.id", "hora", seccionHorario);

            List<Dia> dias = service.allDia();

            List<Hora> horas = new ArrayList();//service.allHora();
            for (Hora hora : seccionHorarioHorasMap.values()) {
                horas.add(hora);
            }
            horas = horas.isEmpty() ? service.allHora() : horas;
            Collections.sort(horas, new Hora.CompareCodigo());

            ObjectNode dataObject = new ObjectNode(jsonFactory);
            ArrayNode horaArray = new ArrayNode(jsonFactory);

            for (Hora hora : horas) {

                logger.debug("****hora {}", hora.getDescripcion());

                ObjectNode horaNode = new ObjectNode(jsonFactory);
                horaNode.put("hora", hora.getDescripcion());

                List<HorarioSeccion> seccionHorarioHora = seccionHorarioHoras.get(hora.getId());

                if (seccionHorarioHora == null) {
                    seccionHorarioHora = new ArrayList();
                }

                Map<Long, List<HorarioSeccion>> seccionHorarioDias = TypesUtil.convertListToMapList("dia.id", seccionHorarioHora);

                ArrayNode arrayDia = new ArrayNode(jsonFactory);

                for (Dia dia : dias) {

                    ObjectNode diaNode = new ObjectNode(jsonFactory);
                    diaNode.put("hora", hora.getDescripcion());

                    logger.debug("*******dia {}", dia.getNombre());
                    diaNode.put("dia", dia.getNombre());

                    List<HorarioSeccion> seccionHorarioDia = seccionHorarioDias.get(dia.getId());
                    if (seccionHorarioDia == null) {
                        seccionHorarioDia = new ArrayList();
                    }

                    ArrayNode arraySeccion = new ArrayNode(jsonFactory);

                    for (HorarioSeccion horarioSeccion : seccionHorarioDia) {
                        ObjectNode seccionNode = new ObjectNode(jsonFactory);
                        seccionNode.put("seccion", horarioSeccion.getSeccion().getCodigo2());
                        seccionNode.put("codigoCurso", horarioSeccion.getSeccion().getGrupoSeccion().getCurso().getCodigo());
                        seccionNode.put("curso", horarioSeccion.getSeccion().getGrupoSeccion().getCurso().getNombre());
                        seccionNode.put("grupo", (String) ObjectUtil.getParentTree(horarioSeccion, "seccion.grupoHoras.codigo"));
                        logger.debug("********seccion {}", horarioSeccion.getSeccion().getCodigo());
                        arraySeccion.add(seccionNode);
                    }

                    diaNode.put("secciones", arraySeccion);
                    arrayDia.add(diaNode);

                }

                horaNode.put("dias", arrayDia);
                horaArray.add(horaNode);

            }

            ArrayNode diasArray = new ArrayNode(jsonFactory);

            for (Dia dia : dias) {
                ObjectNode diaObjectNode = new ObjectNode(jsonFactory);
                diaObjectNode.put("dia", dia.getNombre());
                diasArray.add(diaObjectNode);
            }

            dataObject.put("horarios", horaArray);
            dataObject.put("dias", diasArray);

            response.setData(dataObject);
            response.setTotal(horaArray.size());
            response.setSuccess(true);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }

        return response;
    }

    @ResponseBody
    @RequestMapping("addalumno")
    public JsonResponse addAlumno(AlumnoHorario alumno, HttpSession session) {
        JsonResponse response = new JsonResponse();
        try {

            service.addAlumno(alumno);
            response.setMessage("Alumno agregado satisfactoriamente");
            response.setSuccess(Boolean.TRUE);
        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("veralumno")
    public JsonResponse verAlumno(HorarioCachimbos horario, HttpSession session) {
        JsonResponse response = new JsonResponse();
        try {

            List<AlumnoHorario> alumnosHorario = service.allAlumnoHorarioByHorario(horario);
            ArrayNode array = new ArrayNode(JsonNodeFactory.instance);

            for (AlumnoHorario alumHorario : alumnosHorario) {

                ObjectNode node = new ObjectNode(JsonNodeFactory.instance);
                Alumno alumno = alumHorario.getAlumno();
                Persona persona = alumno.getPersona();
                Carrera carrera = alumno.getCarrera();
                Facultad facultad = carrera.getFacultad();
                HorarioCachimbos hc = alumHorario.getHorarioCachimbos();

                node.put("id", alumHorario.getId());
                node.put("estudiante", persona.getApellidosNombres());
                node.put("carrera", carrera.getNombre());
                node.put("facultad", facultad.getNombre());
                node.put("showfacultad", !facultad.getCodigo().equals(carrera.getCodigo()));

                node.put("codigo", alumno.getCodigo());
                node.put("horario", hc != null ? hc.getCodigo() : "");
                node.put("numCurso", hc != null ? hc.getCursos() : 0);
                node.put("estado", alumHorario.getEstado());
                node.put("estadoName", EstadoAlumnoHorarioEnum.valueOf(alumHorario.getEstado()).getValue());
                array.add(node);
            }

            response.setData(array);
            response.setSuccess(Boolean.TRUE);
        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("vercurso")
    public JsonResponse verCurso(HorarioCachimbos horario, HttpSession session) {
        JsonResponse response = new JsonResponse();
        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
            CicloAcademico cicloAcademico = ds.getCicloAcademico();
            List<GrupoSeccion> gpoSecciones = service.allGrupoSeccionByHorario(horario, cicloAcademico);
            ArrayNode arrayGpoSecc = new ArrayNode(JsonNodeFactory.instance);

            for (GrupoSeccion gpoSeccion : gpoSecciones) {
                int loop = 0;
                for (Seccion seccion : gpoSeccion.getSecciones()) {
                    ObjectNode node = new ObjectNode(JsonNodeFactory.instance);
                    node.put("id", gpoSeccion.getId());
                    node.put("loop", loop);

                    if (loop == 0) {
                        node.put("curso", gpoSeccion.getCurso().getNombre());
                        node.put("codigoCurso", gpoSeccion.getCurso().getCodigo());
                        node.put("tpc", gpoSeccion.getCurso().getTpc());
                        node.put("anexo", gpoSeccion.getAnexoBoletin().getNombre());
                        node.put("estado", gpoSeccion.getEstado());
                        node.put("estadoValue", gpoSeccion.getEstado() != null ? EstadoEnum.valueOf(gpoSeccion.getEstado()).getValue() : "");
                        node.put("cantSecciones", gpoSeccion.getSecciones().size());
                    }

                    node.put("tipo", seccion.getTipoSeccion());
                    node.put("tipoValue", seccion.getTipoSeccionEnum().getTipoSeccionEvalEnum().getValue());
                    node.put("codigoSeccion", seccion.getCodigo());
                    node.put("codigoSeccion2", seccion.getCodigo2());
                    node.put("vacantes", seccion.getVacantes());
                    node.put("matriculados", seccion.getMatriculados());
                    node.put("aula", (String) ObjectUtil.getParentTree(seccion, "aula.codigo"));
                    node.put("grupo", (String) ObjectUtil.getParentTree(seccion, "grupoHoras.codigo"));
                    node.put("estadoSec", seccion.getEstado());
                    node.put("estadoValueSec", seccion.getEstadoEnum().getValue());

                    for (DocenteSeccion docSeccion : seccion.getDocenteSeccion()) {
                        node.put("principal", docSeccion.getPrincipal());
                        node.put("codigoDocente", docSeccion.getDocente().getCodigo());
                        node.put("docente", (String) ObjectUtil.getParentTree(docSeccion, "docente.persona.nombrePaterno"));
                    }

                    if (seccion.getDocenteSeccion().isEmpty()) {
                        node.put("principal", 0);
                        node.put("codigoDocente", "");
                        node.put("docente", "");
                    }

                    arrayGpoSecc.add(node);
                    loop++;
                }
            }

            response.setData(arrayGpoSecc);
            response.setSuccess(Boolean.TRUE);
        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("verhorario")
    public JsonResponse verHorario(HorarioCachimbos horario, HttpSession session) {
        JsonResponse response = new JsonResponse();
        try {

            JsonNodeFactory jsonFactory = JsonNodeFactory.instance;
            List<HorarioSeccion> seccionesHorarios = service.allSeccionHorarioCachimbosByHorarioCachimbos(horario);
            Map<Long, List<HorarioSeccion>> mapHorariosSeccionHora = TypesUtil.convertListToMapList("hora.id", seccionesHorarios);
            Map<Long, Hora> seccionHorarioHorasMap = TypesUtil.convertListToMap("hora.id", "hora", seccionesHorarios);
            List<Dia> dias = service.allDia();
            List<Hora> horas = new ArrayList();
            for (Hora hora : seccionHorarioHorasMap.values()) {
                horas.add(hora);
            }
            horas = horas.isEmpty() ? service.allHora() : horas;
            Collections.sort(horas, new Hora.CompareCodigo());

            ObjectNode dataObject = new ObjectNode(jsonFactory);
            ArrayNode horaArray = new ArrayNode(jsonFactory);

            for (Hora hora : horas) {
                ObjectNode horaNode = new ObjectNode(jsonFactory);
                horaNode.put("hora", hora.getDescripcion());
                List<HorarioSeccion> horariosSeccionesHora = mapHorariosSeccionHora.get(hora.getId());
                horariosSeccionesHora = (horariosSeccionesHora == null) ? new ArrayList() : horariosSeccionesHora;

                Map<Long, List<HorarioSeccion>> mapHorarioSeccionDia = TypesUtil.convertListToMapList("dia.id", horariosSeccionesHora);
                ArrayNode arrayDia = new ArrayNode(jsonFactory);
                for (Dia dia : dias) {
                    ObjectNode diaNode = new ObjectNode(jsonFactory);
                    diaNode.put("hora", hora.getDescripcion());
                    diaNode.put("dia", dia.getNombre());
                    List<HorarioSeccion> horariosSeccionesDia = mapHorarioSeccionDia.get(dia.getId());
                    horariosSeccionesDia = (horariosSeccionesDia == null) ? new ArrayList() : horariosSeccionesDia;

                    ArrayNode arraySeccion = new ArrayNode(jsonFactory);
                    for (HorarioSeccion horarioSeccion : horariosSeccionesDia) {
                        ObjectNode seccionNode = new ObjectNode(jsonFactory);
                        seccionNode.put("seccion", horarioSeccion.getSeccion().getCodigo2());
                        seccionNode.put("codigoCurso", horarioSeccion.getSeccion().getGrupoSeccion().getCurso().getCodigo());
                        seccionNode.put("curso", horarioSeccion.getSeccion().getGrupoSeccion().getCurso().getNombre());
                        seccionNode.put("grupo", (String) ObjectUtil.getParentTree(horarioSeccion, "seccion.grupoHoras.codigo"));
                        arraySeccion.add(seccionNode);
                    }
                    diaNode.put("secciones", arraySeccion);
                    arrayDia.add(diaNode);
                }
                horaNode.put("dias", arrayDia);
                horaArray.add(horaNode);
            }
            ArrayNode diasArray = new ArrayNode(jsonFactory);
            for (Dia dia : dias) {
                ObjectNode diaObjectNode = new ObjectNode(jsonFactory);
                diaObjectNode.put("dia", dia.getNombre());
                diasArray.add(diaObjectNode);
            }
            dataObject.put("horarios", horaArray);
            dataObject.put("dias", diasArray);
            response.setData(dataObject);
            response.setTotal(horaArray.size());
            response.setSuccess(true);
            response.setSuccess(Boolean.TRUE);
        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

}
