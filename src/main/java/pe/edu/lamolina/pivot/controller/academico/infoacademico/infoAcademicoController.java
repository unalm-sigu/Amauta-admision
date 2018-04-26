package pe.edu.lamolina.pivot.controller.academico.infoacademico;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.List;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import pe.albatross.zelpers.miscelanea.ExceptionHandler;
import pe.albatross.zelpers.miscelanea.JsonHelper;
import pe.albatross.zelpers.miscelanea.JsonResponse;
import pe.albatross.zelpers.miscelanea.ObjectUtil;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.AlumnoCiclo;
import pe.edu.lamolina.model.academico.AlumnoCicloCurso;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Curso;
import pe.edu.lamolina.model.academico.DocenteSeccion;
import pe.edu.lamolina.model.academico.MatriculaCurso;
import pe.edu.lamolina.model.academico.MatriculaSeccion;
import pe.edu.lamolina.model.academico.PlanCurricular;
import pe.edu.lamolina.model.academico.SituacionAcademica;
import pe.edu.lamolina.model.horario.Hora;
import pe.edu.lamolina.pivot.zelper.constant.Constantine;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

@Controller
@RequestMapping("academico/alumno")
public class infoAcademicoController {

    @Autowired
    infoAcademicoService service;

    @ResponseBody
    @RequestMapping(value = "{idAlumno}/{numeroCiclo}/avance", method = RequestMethod.GET)
    public JsonResponse alumnoListHistorial(@PathVariable("idAlumno") Long idAlumno, @PathVariable("numeroCiclo") Long numeroCiclo, Model model, HttpSession session) {
        JsonResponse response = new JsonResponse();
        try {
            ObjectNode objectNode = service.allAlumnosByCiclo(new Alumno(idAlumno), numeroCiclo);
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
    public String infoAcademico(@PathVariable("idAlumno") Long idAlumno, Model model, HttpSession session) {

        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        Alumno alumno = service.allInfo(new Alumno(idAlumno));
        ObjectNode alumnoJson = alumno.toJsonInfoAcademico();
        ArrayNode planesJson = new ArrayNode(JsonNodeFactory.instance);

        List<PlanCurricular> planes = service.allPlanCurricularByCarrera(alumno.getCarrera());
        for (PlanCurricular plan : planes) {
            planesJson.add(plan.toJson());
        }

        model.addAttribute("datoAlumno", alumnoJson);
        model.addAttribute("ciclo", ds.getCicloAcademico().toJson());
        model.addAttribute("planes", planesJson);

        ArrayNode horasJson = new ArrayNode(JsonNodeFactory.instance);
        List<Hora> horas = service.allHoras();
        for (Hora hora : horas) {
            horasJson.add(hora.toJson());
        }
        model.addAttribute("horasBD", horasJson);
        return "academico/alumno/infoAcademico";
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
        ObjectNode data = new ObjectNode(factory);
        ArrayNode cursosJson = new ArrayNode(factory);

        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
            CicloAcademico ciclo = ds.getCicloAcademico();
            Alumno alumno = new Alumno(idAlumno);
            List<MatriculaCurso> matriculaCursos = service.allCursosMatriculadosByAlumnoCiclo(alumno, ciclo);
            for (MatriculaCurso matriculaCurso : matriculaCursos) {
                ObjectNode matriculaCursoNode = matriculaCurso.toJson();
                ArrayNode detalle = new ArrayNode(factory);
                List<MatriculaSeccion> matriculaSeccions = matriculaCurso.getMatriculaSeccion();
                if (matriculaSeccions == null) {
                    continue;
                }
                for (MatriculaSeccion matriculaSeccion : matriculaSeccions) {
                    ObjectNode node = new ObjectNode(factory);
                    node.put("tipo", (String) ObjectUtil.getParentTree(matriculaSeccion, "seccion.tipoSeccion"));
                    node.put("codigo", (String) ObjectUtil.getParentTree(matriculaSeccion, "seccion.codigo"));
                    node.put("grupo", (String) ObjectUtil.getParentTree(matriculaSeccion, "seccion.grupoHoras.codigo"));
                    node.put("aula", (String) ObjectUtil.getParentTree(matriculaSeccion, "seccion.aula.codigo"));
                    DocenteSeccion docenteSeccion = matriculaSeccion.getSeccion().getDocenteSeccion().get(0);
                    node.put("docente", (String) ObjectUtil.getParentTree(docenteSeccion, "docente.persona.nombreCompleto"));
                    node.put("docenteCodigo", (String) ObjectUtil.getParentTree(docenteSeccion, "docente.codigo"));
                    detalle.add(node);
                }
                matriculaCursoNode.set("detalle", detalle);
                cursosJson.add(matriculaCursoNode);

            }
            data.set("cursos", cursosJson);
            data.set("ciclo", JsonHelper.createJson(ciclo, factory));
            response.setData(data);

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
            service.cambiarPlan(new Alumno(idAlumno), new PlanCurricular(idPlan));
            response.setSuccess(true);
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
        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);

        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("updateinfoacademica")
    public JsonResponse updateinfoacademica(AlumnoCicloForm alumnoCicloForm, HttpSession session) {
        JsonResponse response = new JsonResponse();
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        try {
            service.updateInfoAcademica(alumnoCicloForm, ds);
            response.setSuccess(true);
        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);

        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @RequestMapping("{idAlumno}/datoacademico")
    public String datoacademico(@PathVariable("idAlumno") Long idAlumno, Model model, HttpSession session) {
        Alumno alumno = service.allInfo(new Alumno(idAlumno));
        List<CicloAcademico> ciclosAcademico = service.allCicloAcademico();
        ObjectNode alumnoJson = alumno.toJsonInfoAcademico();
        model.addAttribute("datoAlumno", alumnoJson);
        model.addAttribute("ciclosAcademico", ciclosAcademico);
        return "academico/alumno/datosAcademico";
    }
}
