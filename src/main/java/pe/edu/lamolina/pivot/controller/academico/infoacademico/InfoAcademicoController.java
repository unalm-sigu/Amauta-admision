package pe.edu.lamolina.pivot.controller.academico.infoacademico;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpSession;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import pe.albatross.zelpers.miscelanea.ExceptionHandler;
import pe.albatross.zelpers.miscelanea.JsonHelper;
import pe.albatross.zelpers.miscelanea.JsonResponse;
import pe.albatross.zelpers.miscelanea.NumberFormat;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.AlumnoCicloCurso;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Curso;
import pe.edu.lamolina.model.academico.CursoCurricula;
import pe.edu.lamolina.model.academico.Docente;
import pe.edu.lamolina.model.academico.MatriculaCurso;
import pe.edu.lamolina.model.academico.MatriculaResumen;
import pe.edu.lamolina.model.academico.OrientacionCarrera;
import pe.edu.lamolina.model.academico.PlanCurricular;
import pe.edu.lamolina.model.academico.RequisitoCursoCurricula;
import pe.edu.lamolina.model.aporte.BoletaIngresante;
import pe.edu.lamolina.model.horario.Hora;
import pe.edu.lamolina.model.horario.HorarioSeccion;
import pe.edu.lamolina.pivot.controller.academico.plancurricular.PlanCurricularService;
import pe.edu.lamolina.pivot.zelper.constant.Constantine;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

@Controller
@RequestMapping("academico/alumno")
public class InfoAcademicoController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    InfoAcademicoService service;

    @Autowired
    PlanCurricularService planCurricularService;

    @ResponseBody
    @RequestMapping("{idAlumno}/avance")
    public JsonResponse alumnoAvance(
            @PathVariable("idAlumno") Long idAlumno, Model model, HttpSession session) {
        JsonResponse response = new JsonResponse();
        try {
            ObjectNode avanceCurrInfoJson = service.allAvanceCurricular(new Alumno(idAlumno));
            response.setData(avanceCurrInfoJson);
            response.setSuccess(Boolean.TRUE);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);

        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }

        return response;
    }

    @RequestMapping("{idAlumno}/infoacademico")
    public String infoAcademico(
            @PathVariable("idAlumno") Long idAlumno,
            @RequestParam(value = "origen", required = false) String origen,
            Model model, HttpSession session) {

        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        CicloAcademico ciclo = ds.getCicloAcademico();
        JsonNodeFactory factory = JsonNodeFactory.instance;

        Alumno alumno = service.findWithallInfo(new Alumno(idAlumno));
        ObjectNode alumnoJson = createAlumnoJson(alumno);

        ArrayNode planesJson = new ArrayNode(JsonNodeFactory.instance);

        List<PlanCurricular> planes = service.allPlanCurricularByAlumno(alumno);
        for (PlanCurricular plan : planes) {
            ObjectNode planJson = JsonHelper.createJson(plan, factory, true, new String[]{
                "*", "cicloInicioVigencia.descripcion", "carrera.nombre", "orientacionCarrera.nombre"
            });
            planesJson.add(planJson);
        }

        ObjectNode cicloJson = createCicloJson(ciclo);
        boolean puedeCalcular = service.usuarioPuedeCalcular(ds);

        model.addAttribute("alumno", alumnoJson);
        model.addAttribute("ciclo", cicloJson);
        model.addAttribute("planes", planesJson);
        model.addAttribute("puedeCalcular", puedeCalcular);
        model.addAttribute("origen", getOrigen(origen));

        ArrayNode horasJson = new ArrayNode(JsonNodeFactory.instance);
        List<Hora> horas = service.allHoras();
        for (Hora hora : horas) {
            horasJson.add(JsonHelper.createJson(hora, JsonNodeFactory.instance, true, new String[]{"*"}));
        }

        model.addAttribute("horasBD", horasJson);
        return "academico/alumno/infoAcademico";
    }

    private String getOrigen(String origen) {
        if (StringUtils.isEmpty(origen)) {
            return "/academico/alumno";
        }
        byte[] decoded = Base64.getMimeDecoder().decode(origen);
        String output = new String(decoded);
        return output;
    }

    @ResponseBody
    @RequestMapping("{idAlumno}/planes")
    public JsonResponse planes(@PathVariable("idAlumno") Long idAlumno, Model model, HttpSession session) {
        JsonResponse response = new JsonResponse();

        try {
            ArrayNode planesJson = new ArrayNode(JsonNodeFactory.instance);

            Alumno alumno = service.findAlumno(idAlumno);
            List<PlanCurricular> planes = service.allPlanCurricularByAlumno(alumno);
            for (PlanCurricular plan : planes) {
                ObjectNode planJson = JsonHelper.createJson(plan, JsonNodeFactory.instance, true, new String[]{
                    "*", "cicloInicioVigencia.descripcion", "carrera.nombre", "orientacionCarrera.nombre"
                });
                planesJson.add(planJson);
            }
            response.setData(planesJson);
            response.setSuccess(Boolean.TRUE);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);

        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }

        return response;
    }

    @ResponseBody
    @RequestMapping("{idAlumno}/historial")
    public JsonResponse alumnoHistorial(@PathVariable("idAlumno") Long idAlumno, Model model, HttpSession session) {
        JsonResponse response = new JsonResponse();

        try {
            List<AlumnoCicloCurso> cursosHisto = service.allHistorialAlumno(new Alumno(idAlumno));
            ArrayNode promediosJson = service.allPromediosJson(cursosHisto);
            ArrayNode cursosJson = service.allCursosJson(cursosHisto);

            ObjectNode data = new ObjectNode(JsonNodeFactory.instance);
            data.set("promedios", promediosJson);
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
    @RequestMapping("{idAlumno}/cursosmatriculados")
    public JsonResponse cursosMatriculados(@PathVariable("idAlumno") Long idAlumno, Model model, HttpSession session) {
        JsonResponse response = new JsonResponse();
        JsonNodeFactory factory = JsonNodeFactory.instance;

        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
            CicloAcademico ciclo = ds.getCicloAcademico();
            Alumno alumno = new Alumno(idAlumno);

            ObjectNode data = new ObjectNode(factory);
            ArrayNode cursosJson = new ArrayNode(factory);

            List<MatriculaCurso> matriculaCursos = service.allCursosMatriculadosByAlumnoCiclo(alumno, ciclo);
            for (MatriculaCurso matriculaCurso : matriculaCursos) {
                ObjectNode matriCursoJson = createMatriculaCursoJson(matriculaCurso);
                cursosJson.add(matriCursoJson);
            }
            data.set("cursos", cursosJson);

            MatriculaResumen matResum = service.findResumenMatricula(alumno, ciclo, matriculaCursos);
            data.set("resumen", JsonHelper.createJson(matResum, factory, true, new String[]{
                "creditosMatriculados",
                "cursosMatriculados"
            }));

            data.set("ciclo", createCicloJson(ciclo));
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
    @RequestMapping("{idAlumno}/generaravance")
    public JsonResponse generarAvance(@PathVariable("idAlumno") Long idAlumno, Model model, HttpSession session) {
        JsonResponse response = new JsonResponse();
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);

        try {
            service.generarAvance(new Alumno(idAlumno), ds);
            response.setSuccess(true);
        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);

        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("{idAlumno}/{idPlan}/cambiarplan")
    public JsonResponse cambiarPlan(@PathVariable("idAlumno") Long idAlumno, @PathVariable("idPlan") Long idPlan, Model model, HttpSession session) {
        JsonResponse response = new JsonResponse();
        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
            service.cambiarPlan(new Alumno(idAlumno), new PlanCurricular(idPlan), ds);
            response.setSuccess(true);
            response.setMessage("Se actualizó satisfactoriamente el plan curricular del alumno");
        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);

        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("calcularpromedio")
    public JsonResponse calcularpromedio(Alumno alumnoForm, HttpSession session) {
        JsonResponse response = new JsonResponse();
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        try {
            service.calcularPromedio(alumnoForm, ds);
            response.setSuccess(true);
            response.setMessage("Se calculó el promedio satisfactoriamente");
        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("{idAlumno}/aportes")
    public JsonResponse aportes(@PathVariable("idAlumno") Long idAlumno, Model model, HttpSession session) {
        JsonResponse response = new JsonResponse();
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        response.setSuccess(false);
        try {
            List<BoletaIngresante> aportes = service.allAportesAlumno(new Alumno(idAlumno), ds.getCicloAcademico());
            ArrayNode aportesArray = new ArrayNode(JsonNodeFactory.instance);
            for (BoletaIngresante aporte : aportes) {
                aportesArray.add(aporte.toJson());
            }
            response.setSuccess(true);
            response.setData(aportesArray);
        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);

        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("{id}/horario")
    public JsonResponse alumnoHorario(
            @PathVariable("id") Long id,
            @RequestParam(value = "tipo", required = false) String tipo,
            Model model, HttpSession session) {
        JsonResponse response = new JsonResponse();
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        CicloAcademico cicloAcademico = ds.getCicloAcademico();
        try {
            List<HorarioSeccion> seccionesHorarios = null;
            if (tipo.equals("ALU")) {
                seccionesHorarios = service.allSeccionHorarioAlumnoByAlumnoCicloACademico(new Alumno(id), cicloAcademico);
            } else if (tipo.equals("DOC")) {
                seccionesHorarios = service.allSeccionHorarioAlumnoByDocenteCicloACademico(new Docente(id), cicloAcademico);
            }

            ObjectNode horarios = service.findHorarioBySeccionesHorarios(seccionesHorarios);
            response.setData(horarios);
        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);

        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }

        return response;
    }

    @ResponseBody
    @RequestMapping("{idAlumno}/data")
    public JsonResponse alumnoData(@PathVariable("idAlumno") Long idAlumno, Model model, HttpSession session) {
        JsonResponse response = new JsonResponse();
        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
            Alumno alumno = service.findWithallInfo(new Alumno(idAlumno));
            ObjectNode alumnoJson = createAlumnoJson(alumno);
            response.setData(alumnoJson);
            response.setSuccess(Boolean.TRUE);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);

        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }

        return response;
    }

    @ResponseBody
    @RequestMapping("{numero}/hora")
    public JsonResponse getHoraByNroHora(@PathVariable("numero") Integer numero, Model model, HttpSession session) {
        JsonResponse response = new JsonResponse();
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        try {
            Hora hora = service.getHoraByNroHora(numero);
            response.setData(JsonHelper.createJson(hora, JsonNodeFactory.instance, true, new String[]{"*"}));
        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);

        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }

        return response;
    }

    @ResponseBody
    @RequestMapping("dataCurricula")
    public JsonResponse dataCurricula(PlanCurricular plan, Model model, HttpSession session) {
        JsonResponse response = new JsonResponse();
        try {
            ArrayNode array = new ArrayNode(JsonNodeFactory.instance);
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);

            if (plan.getId() != null) {
                PlanCurricular planBD = planCurricularService.findPlanCurricularById(plan);

                List<CursoCurricula> cursosCurr = planBD.getCursoCurricula();
                Map<Integer, List<CursoCurricula>> mapCursosCurr = TypesUtil.convertListToMapList("numeroCiclo", cursosCurr);

                for (Map.Entry<Integer, List<CursoCurricula>> entry : mapCursosCurr.entrySet()) {
                    Integer nroCiclo = entry.getKey();
                    if (nroCiclo == 0) {
                        continue;
                    }

                    ObjectNode node = new ObjectNode(JsonNodeFactory.instance);

                    node.put("numeroCiclo", nroCiclo);
                    node.put("numeroRomano", NumberFormat.roman(nroCiclo));

                    ArrayNode arrayCursos = new ArrayNode(JsonNodeFactory.instance);
                    List<CursoCurricula> cursosCiclo = entry.getValue();
                    for (CursoCurricula cursoCurr : cursosCiclo) {
                        Curso curso = cursoCurr.getCurso();
                        ObjectNode nodeCurso = new ObjectNode(JsonNodeFactory.instance);
                        nodeCurso.put("id", cursoCurr.getId());
                        nodeCurso.put("tipo", cursoCurr.getTipoCursoCurricula().getCodigo());
                        nodeCurso.put("curso", curso.getNombre());
                        nodeCurso.put("codigo", curso.getCodigo());
                        nodeCurso.put("creditos", cursoCurr.getCreditos());
                        nodeCurso.put("numeroCurso", cursoCurr.getNumeroCurso());
                        nodeCurso.put("creditosRequisito", cursoCurr.getCreditosRequisito());

                        ArrayNode arrayRequisitos = new ArrayNode(JsonNodeFactory.instance);
                        List<RequisitoCursoCurricula> requisitos = cursoCurr.getRequisitosCursoCurricula();
                        for (RequisitoCursoCurricula requisito : requisitos) {
                            CursoCurricula cursoReq = requisito.getCursoRequisito();
                            ObjectNode nodeReq = new ObjectNode(JsonNodeFactory.instance);
                            nodeReq.put("idReq", cursoReq.getId());
                            nodeReq.put("simultaneo", requisito.getSimultaneo());
                            arrayRequisitos.add(nodeReq);
                        }

                        nodeCurso.set("requisitos", arrayRequisitos);
                        arrayCursos.add(nodeCurso);
                    }

                    node.set("cursos", arrayCursos);
                    array.add(node);
                }
            }
            response.setData(array);
            response.setSuccess(true);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }

        return response;
    }

    @ResponseBody
    @RequestMapping("{idAlumno}/{idOrientacion}/saveOrientacion")
    public JsonResponse saveOrientacion(@PathVariable("idAlumno") Long idAlumno, @PathVariable("idOrientacion") Long idOrientacion, Model model, HttpSession session) {
        JsonResponse response = new JsonResponse();
        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
            service.cambiarOrientacion(new Alumno(idAlumno), new OrientacionCarrera(idOrientacion), ds);
            response.setSuccess(true);
            response.setMessage("Se actualizó satisfactoriamente la orientación ");
        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);

        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    private ObjectNode createAlumnoJson(Alumno alumno) {
        ObjectNode alumnoJson = JsonHelper.createJson(alumno, JsonNodeFactory.instance, true, new String[]{
            "*",
            "carrera.codigo",
            "carrera.nombre",
            "carrera.tipoEnum",
            "carrera.orientacionCarrera.id",
            "carrera.orientacionCarrera.nombre",
            "carrera.facultad.codigo",
            "carrera.facultad.nombre",
            "orientacionCarrera.id",
            "orientacionCarrera.nombre",
            "cicloIngreso.descripcion",
            "cicloActivo.descripcion",
            "cicloActivoRegular.descripcion",
            "situacionAcademica.codigo",
            "situacionAcademica.nombre",
            "postulantePregrado.modalidadIngreso.nombre",
            "planCurricular.id",
            "planCurricular.carrera.nombre",
            "planCurricular.orientacionCarrera.id",
            "planCurricular.orientacionCarrera.nombre",
            "planCurricular.cicloInicioVigencia.descripcion",
            "modalidadEstudio.codigo",
            "modalidadEstudio.nombre",
            "postulantePregrado.modalidadIngreso.nombre",
            "persona.apellidos",
            "persona.paterno",
            "persona.materno",
            "persona.nombres",
            "persona.sexo",
            "persona.sexoEnum",
            "persona.nombreCompleto",
            "persona.apellidosNombres",
            "persona.numeroDocIdentidad",
            "persona.rutaFoto",
            "persona.tipoFoto",
            "persona.tipoDocumento.simbolo"
        });
        return alumnoJson;
    }

    private ObjectNode createCicloJson(CicloAcademico ciclo) {
        ObjectNode cicloJson = JsonHelper.createJson(ciclo, JsonNodeFactory.instance, true, new String[]{
            "id", "codigo", "tipo", "descripcion", "descripcion2",
            "modalidadEstudio.id",
            "modalidadEstudio.codigo",
            "modalidadEstudio.nombre"
        });
        return cicloJson;
    }

    private ObjectNode createMatriculaCursoJson(MatriculaCurso matriculaCurso) {
        ObjectNode matriCursoJson = JsonHelper.createJson(matriculaCurso, JsonNodeFactory.instance, true, new String[]{
            "id", "creditos", "creditosAprobados", "estado", "estadoEnum", "notaFinal", "notaAvance",
            "curso.codigo",
            "curso.nombre",
            "curso.tpc",
            "curso.tipoCurso",
            "curso.departamentoAcademico.nombre",
            "matriculaSeccion.seccion.codigo2",
            "matriculaSeccion.seccion.tipoSeccion",
            "matriculaSeccion.seccion.grupoHoras.codigo",
            "matriculaSeccion.seccion.aula.codigo",
            "matriculaSeccion.seccion.docenteSeccion.docente.codigo",
            "matriculaSeccion.seccion.docenteSeccion.docente.persona.nombreCompleto"
        });
        return matriCursoJson;
    }
}
