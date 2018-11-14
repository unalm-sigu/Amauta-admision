package pe.edu.lamolina.pivot.controller.rolexamen.cursomasivos;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import pe.albatross.zelpers.miscelanea.ExceptionHandler;
import pe.albatross.zelpers.miscelanea.JsonHelper;
import pe.albatross.zelpers.miscelanea.JsonResponse;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.edu.lamolina.model.academico.Curso;
import pe.edu.lamolina.model.rolexamen.CursoMasivoExamen;
import pe.edu.lamolina.model.rolexamen.RolExamenes;
import pe.edu.lamolina.pivot.zelper.constant.Constantine;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

@Controller
@RequestMapping("rolexamen/cursomasivos")
public class CursoMasivosController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    CursoMasivosService service;

    @RequestMapping(method = RequestMethod.GET)
    public String index(Model model, HttpSession session) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        model.addAttribute("cicloAcademico", ds.getCicloAcademico());

        List<RolExamenes> rolexamenes = service.allRolExamenesByCicloActivo(ds.getCicloAcademico());
        ArrayNode jRolexamenes = new ArrayNode(JsonNodeFactory.instance);
        for (RolExamenes rolexamen : rolexamenes) {
            logger.debug("Contenido de RolExamenes {}", rolexamen.getNombre());
            ObjectNode rolExam = JsonHelper.createJson(rolexamen, JsonNodeFactory.instance, true,
                    new String[]{
                        "*",
                        "eventoCicloAcademico.cicloAcademico.descripcion",
                        "eventoCicloAcademico.fechaInicio", "eventoCicloAcademico.fechaFin",
                        "nombre", "estado", "fechaPublicacion"
                    });

            jRolexamenes.add(rolExam);
        }

        model.addAttribute("jRolexamenes", jRolexamenes.toString());

        return "rolexamen/cursomasivos/cursomasivos";
    }

    @ResponseBody
    @RequestMapping("loadCursosMasivosByRoleExamen")
    public JsonResponse loadCursosMasivosByRoleExamen(@RequestParam("nombre") String nombre, HttpSession session, HttpServletRequest request) {
        JsonResponse response = new JsonResponse();
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        logger.debug("Contenido de RolExamenes {}", nombre);
        try {

//            //Cursos Masivos cuyo rol examen = rolExamenes
//            List<RolExamenes> cursoMasivosByRolExamenes = service.allRolExamenesByCicloActivo(ds.getCicloAcademico());
//            ArrayNode jRolexamenes = new ArrayNode(JsonNodeFactory.instance);
//            for (RolExamenes rolexamen : cursoMasivosByRolExamenes) {
//                logger.debug("Contenido de RolExamenes {}", rolexamen.getNombre());
//                ObjectNode rolExam = JsonHelper.createJson(rolexamen, JsonNodeFactory.instance, true,
//                        new String[]{
//                            "*",
//                            "eventoCicloAcademico.cicloAcademico.descripcion",
//                            "eventoCicloAcademico.fechaInicio", "eventoCicloAcademico.fechaFin",
//                            "nombre", "estado", "fechaPublicacion"
//                        });
//
//                jRolexamenes.add(rolExam);
//            }
//            response.setData(cursoMasivosByRolExamenes);
        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }

        return response;
    }

    @ResponseBody
    @RequestMapping("save")
    public JsonResponse save(@RequestBody CursoMasivoExamen cursoMasivosExamen, HttpSession session) {

        JsonNodeFactory jsonFactory = JsonNodeFactory.instance;
        JsonResponse response = new JsonResponse();

        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
            logger.debug("saveCursoMasivo");
            service.save(cursoMasivosExamen, ds.getCicloAcademico(), ds);

            response.setSuccess(true);
            response.setMessage("Guardado satisfactoriamnente");

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("list")
    public JsonResponse list(@RequestBody RolExamenes rolExamenes, HttpSession session, HttpServletRequest request) {
        JsonResponse response = new JsonResponse();
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        try {

            List<CursoMasivoExamen> cursoMasivosByRolExamenes = service.listCursosMasivosExamenes(rolExamenes);

            ArrayNode jCursoMasivosByRolExamen = new ArrayNode(JsonNodeFactory.instance);
            for (CursoMasivoExamen cursoMasivoByRolExamen : cursoMasivosByRolExamenes) {

                ObjectNode cursoMasivo = JsonHelper.createJson(cursoMasivoByRolExamen, JsonNodeFactory.instance, true,
                        new String[]{
                            "*",
                            "curso.*"
//                            "userRegistro.*",
//                            "userRegistro.persona.apellidosNombres",
//                            "alumnosCursosMasivos.*",
//                            "alumnosCursosMasivos.alumno.codigo",
//                            "alumnosCursosMasivos.alumno.persona.apellidosNombres",
//                            "seccionesCursosMasivos.*",
//                            "seccionesCursosMasivos.seccion.codigo",
//                            "seccionesCursosMasivos.seccion.codigo2",
//                            "aulasCursosMasivos.*",
//                            "aulasCursosMasivos.aula.codigo",
//                            "aulasCursosMasivos.aula.nombre"
                        });

                jCursoMasivosByRolExamen.add(cursoMasivo);
            }
            response.setData(jCursoMasivosByRolExamen);
            response.setSuccess(Boolean.TRUE);
        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }

        return response;
    }

    @ResponseBody
    @RequestMapping("{idRolExamen}/loadCurso")
    public JsonResponse loadCurso(
            @RequestParam("nombre") String nombre,
            @PathVariable("idRolExamen") Long idRolExamen,
            HttpSession session,
            HttpServletRequest request) {

        JsonNodeFactory jsonFactory = JsonNodeFactory.instance;
        JsonResponse response = new JsonResponse();

        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);

            List<Curso> cursos = service.allCursosByCiclo(nombre, new RolExamenes(idRolExamen), ds.getCicloAcademico());
            ArrayNode jCursos = new ArrayNode(JsonNodeFactory.instance);
            for (Curso curso : cursos) {
                ObjectNode cur = JsonHelper.createJson(curso, JsonNodeFactory.instance, false,
                        new String[]{
                            "*",
                            "departamentoAcademico.*"
                        });
                jCursos.add(cur);
            }
            response.setData(jCursos);

            response.setSuccess(true);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

}
