package pe.edu.lamolina.pivot.controller.academico.infoacademico;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.Base64;
import java.util.List;
import javax.servlet.http.HttpSession;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import pe.albatross.zelpers.miscelanea.ExceptionHandler;
import pe.albatross.zelpers.miscelanea.JsonHelper;
import pe.albatross.zelpers.miscelanea.JsonResponse;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.AlumnoCiclo;
import pe.edu.lamolina.model.academico.AlumnoCicloCurso;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Curso;
import pe.edu.lamolina.model.academico.MatriculaCurso;
import pe.edu.lamolina.model.academico.MatriculaResumen;
import pe.edu.lamolina.model.academico.PlanCurricular;
import pe.edu.lamolina.model.academico.SituacionAcademica;
import pe.edu.lamolina.model.aporte.BoletaIngresante;
import pe.edu.lamolina.model.horario.Hora;
import pe.edu.lamolina.pivot.zelper.constant.Constantine;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

@Controller
@RequestMapping("academico/alumno")
public class infoAcademicoController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    infoAcademicoService service;

    @ResponseBody
    @RequestMapping(value = "{idAlumno}/avance", method = RequestMethod.GET)
    public JsonResponse alumnoAvance(@PathVariable("idAlumno") Long idAlumno, Model model, HttpSession session) {
        JsonResponse response = new JsonResponse();
        try {
            ObjectNode objectNode = service.allAvanaceCurricular(new Alumno(idAlumno));
            response.setData(objectNode);
        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);

        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }

        return response;
    }

    @ResponseBody
    @RequestMapping(value = "{idAlumno}/cursoMatri", method = RequestMethod.GET)
    public JsonResponse alumnoListCursoMatri(@PathVariable("idAlumno") Long idAlumno, Model model, HttpSession session) {
        JsonResponse response = new JsonResponse();
        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
            ObjectNode lst = service.allAlumnosByCursosMatri(new Alumno(idAlumno), ds.getCicloAcademico());
            response.setData(lst);
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
        JsonNodeFactory factory = JsonNodeFactory.instance;

        Alumno alumno = service.allInfo(new Alumno(idAlumno));
        ObjectNode alumnoJson = JsonHelper.createJson(alumno, factory, true, new String[]{
            "*",
            "carrera.codigo",
            "carrera.nombre",
            "carrera.orientacionCarrera.nombre",
            "carrera.facultad.codigo",
            "carrera.facultad.nombre",
            "cicloIngreso.descripcion",
            "cicloActivo.descripcion",
            "cicloActivoRegular.descripcion",
            "situacionAcademica.codigo",
            "situacionAcademica.nombre",
            "postulantePregrado.modalidadIngreso.nombre",
            "planCurricular.id",
            "planCurricular.carrera.nombre",
            "planCurricular.cicloInicioVigencia.descripcion",
            "modalidadEstudio.nombre",
            "persona.apellidosNombres",
            "persona.numeroDocIdentidad",
            "persona.rutaFoto",
            "persona.tipoFoto",
            "persona.tipoDocumento.simbolo"
        });

        ArrayNode planesJson = new ArrayNode(JsonNodeFactory.instance);

        List<PlanCurricular> planes = service.allPlanCurricularByCarrera(alumno.getCarrera());
        for (PlanCurricular plan : planes) {
            ObjectNode planJson = JsonHelper.createJson(plan, factory, true, new String[]{
                "*", "cicloInicioVigencia.*", "carrera.*"
            });
            planesJson.add(planJson);
        }

        ObjectNode cicloJson = JsonHelper.createJson(ds.getCicloAcademico(), JsonNodeFactory.instance, true, new String[]{
            "id", "descripcion", "descripcion2", "modalidadEstudio.*"
        });

        model.addAttribute("alumno", alumnoJson);
        model.addAttribute("ciclo", cicloJson);
        model.addAttribute("planes", planesJson);
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
    @RequestMapping(value = "{idAlumno}/historial", method = RequestMethod.GET)
    public JsonResponse alumnoHistorial(@PathVariable("idAlumno") Long idAlumno, Model model, HttpSession session) {
        JsonResponse response = new JsonResponse();

        try {
            List<AlumnoCiclo> promedios = service.allPromediosByAlumno(new Alumno(idAlumno));
            ArrayNode lstNode = new ArrayNode(JsonNodeFactory.instance);
            for (AlumnoCiclo promedio : promedios) {

                SituacionAcademica situacionAcademica = promedio.getSituacionFinal();

                ObjectNode objNode = new ObjectNode(JsonNodeFactory.instance);
                objNode.put("ciclo", promedio.getCicloAcademico().getDescripcion2());
                objNode.put("descripción", promedio.getCicloAcademico().getDescripcion());
                objNode.put("promedio", promedio.getPromedioCiclo());
                objNode.put("promedioPonderadoAcum", promedio.getPromedioAcumulado());
                objNode.put("CreditoCursadosCiclo", promedio.getCreditosCursadosCiclo());
                objNode.put("CreditoAprobadosAcu", promedio.getCreditosAprobadosAcumulados());
                objNode.put("CreditoAprobaCiclo", promedio.getCreditosAprobadosCiclo());
                objNode.put("creditoAcumulado", promedio.getCreditosAcumulados());
                objNode.put("situacionAcademica", situacionAcademica.getNombre());
                List<AlumnoCicloCurso> cursos = promedio.getAlumnoCicloCurso();

                ArrayNode lstCurso = new ArrayNode(JsonNodeFactory.instance);
                for (AlumnoCicloCurso cicloCurso : cursos) {
                    ObjectNode objCurso = new ObjectNode(JsonNodeFactory.instance);
                    Curso curso = cicloCurso.getCurso();
                    objCurso.put("curso", curso.getNombre());
                    objCurso.put("codigo", curso.getCodigo());
                    objCurso.put("creditos", cicloCurso.getCreditos());
                    objCurso.put("nota", cicloCurso.getNota());

                    lstCurso.add(objCurso);
                }
                objNode.set("cursos", lstCurso);
                lstNode.add(objNode);
                response.setSuccess(true);
            }
            response.setData(lstNode);
        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);

        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }

        return response;
    }

    @ResponseBody
    @RequestMapping(value = "{idAlumno}/cursosmatriculados", method = RequestMethod.GET)
    public JsonResponse cursosMatriculados(@PathVariable("idAlumno") Long idAlumno, Model model, HttpSession session) {
        JsonResponse response = new JsonResponse();
        JsonNodeFactory factory = JsonNodeFactory.instance;

        ArrayNode cursosJson = new ArrayNode(factory);

        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
            CicloAcademico ciclo = ds.getCicloAcademico();
            Alumno alumno = new Alumno(idAlumno);

            ObjectNode data = new ObjectNode(factory);
            List<MatriculaCurso> matriculaCursos = service.allCursosMatriculadosByAlumnoCiclo(alumno, ciclo);
            for (MatriculaCurso matriculaCurso : matriculaCursos) {
                ObjectNode matriCursoJson = JsonHelper.createJson(matriculaCurso, factory, true, new String[]{
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
                cursosJson.add(matriCursoJson);
            }
            data.set("cursos", cursosJson);

            MatriculaResumen matResum = service.findResumenMatricula(alumno, ciclo);
            data.set("resumen", JsonHelper.createJson(matResum, factory, true, new String[]{
                "creditosMatriculados",
                "cursosMatriculados"
            }));

            data.set("ciclo", JsonHelper.createJson(ciclo, factory));
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
    @RequestMapping(value = "{idAlumno}/generaravance", method = RequestMethod.GET)
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
    @RequestMapping(value = "{idAlumno}/listHistorial", method = RequestMethod.GET)
    public JsonResponse alumnoListHistorial(@PathVariable("idAlumno") Long idAlumno, Model model, HttpSession session) {
        JsonResponse response = new JsonResponse();
        try {
            List<AlumnoCicloCurso> alumnoCicloCurso = service.allPromediosByAlumnoOrderByCurso(new Alumno(idAlumno));

            ArrayNode lstCurso = new ArrayNode(JsonNodeFactory.instance);
            ObjectNode objNode = new ObjectNode(JsonNodeFactory.instance);

            if (alumnoCicloCurso.size() > 0) {
                objNode.put("alumnoCodigo", alumnoCicloCurso.get(0).getAlumnoCiclo().getAlumno().getCodigo());
                for (AlumnoCicloCurso curso : alumnoCicloCurso) {
                    ObjectNode objCurso = new ObjectNode(JsonNodeFactory.instance);
                    objCurso.put("curso", curso.getCurso().getNombre());
                    objCurso.put("codigo", curso.getCurso().getCodigo());
                    objCurso.put("creditos", curso.getCreditos());
                    objCurso.put("nota", curso.getNota());
                    lstCurso.add(objCurso);
                }
            }
            objNode.set("cursos", lstCurso);
            response.setData(objNode);
            response.setSuccess(true);
        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);

        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping(value = "{idAlumno}/{idPlan}/cambiarplan", method = RequestMethod.GET)
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
    @RequestMapping(value = "{idAlumno}/aportes", method = RequestMethod.GET)
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

}
