package pe.edu.lamolina.pivot.controller.rolexamen.cursomasivos;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.Date;
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
import pe.edu.lamolina.model.general.Aula;
import pe.edu.lamolina.model.general.Oficina;
import pe.edu.lamolina.model.rolexamen.CursoMasivoExamen;
import pe.edu.lamolina.model.rolexamen.RolExamenes;
import pe.edu.lamolina.pivot.zelper.constant.Constantine;
import pe.edu.lamolina.pivot.zelper.constant.Messages;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

@Controller
@RequestMapping("rolexamen/cursomasivos")
public class CursoMasivosController {
    
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    
    @Autowired
    CursoMasivosService service;
    
    private enum TipoAccion {
        CURSO,
        SECCION
    }
    
    @RequestMapping(method = RequestMethod.GET)
    public String index(Model model, HttpSession session) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        model.addAttribute("cicloAcademico", ds.getCicloAcademico());
        
        List<RolExamenes> rolexamenes = service.allRolExamenesByCicloActivo(ds.getCicloAcademico());
        ArrayNode jRolexamenes = new ArrayNode(JsonNodeFactory.instance);
        for (RolExamenes rolexamen : rolexamenes) {
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
    @RequestMapping("save")
    public JsonResponse save(@RequestBody CursoMasivoExamen cursoMasivosExamen, HttpSession session) {
        
        JsonNodeFactory jsonFactory = JsonNodeFactory.instance;
        JsonResponse response = new JsonResponse();
        
        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
            service.save(cursoMasivosExamen, ds.getCicloAcademico(), ds);
            response.setSuccess(true);
            response.setMessage("Curso guardado satisfactoriamnente");
            
        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }
    
    @ResponseBody
    @RequestMapping("saveAulas")
    public JsonResponse saveAulas(@RequestBody CursoMasivoExamen cursoMasivosExamen, HttpSession session) {
        
        JsonNodeFactory jsonFactory = JsonNodeFactory.instance;
        JsonResponse response = new JsonResponse();
        
        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
            service.saveAula(cursoMasivosExamen, ds.getCicloAcademico(), ds);
            response.setSuccess(true);
            response.setMessage("Aula guardado satisfactoriamnente");
            
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
                            "curso.*",
                            "aulasCursosMasivos.aula.*",
                            "seccionesCursosMasivos.seccion.*"
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
    
    @ResponseBody
    @RequestMapping("allModulos")
    public JsonResponse allModulos(HttpSession session) {
        
        JsonNodeFactory jsonFactory = JsonNodeFactory.instance;
        JsonResponse response = new JsonResponse();
        
        try {
            Oficina oficinaOERA = service.findOficinaOera();
            List<Aula> pabellones = service.allPabellonesByOficina(oficinaOERA);
            
            ArrayNode arrayPabellones = new ArrayNode(jsonFactory);
            for (Aula pabellon : pabellones) {
                ObjectNode json = createPabellonesJson(pabellon);
                arrayPabellones.add(json);
            }
            
            response.setData(arrayPabellones);
            response.setSuccess(true);
            
        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }
    
    private ObjectNode createPabellonesJson(Aula pabellon) {
        ObjectNode json = JsonHelper.createJson(pabellon, JsonNodeFactory.instance, true, new String[]{
            "id",
            "codigo",
            "nombre"
        });
        return json;
    }
    
    @ResponseBody
    @RequestMapping("eliminar")
    public JsonResponse eliminar(CursoMasivoExamen cursoMasivoExamen, HttpSession session) {
        JsonResponse response = new JsonResponse();
        response.setSuccess(false);
        
        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
            service.eliminarCursoMasivoExamen(cursoMasivoExamen, ds);
            
            response.setMessage("Curso eliminado satisfactoriamente.");
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
    @RequestMapping("allAulasModulo")
    public JsonResponse allAulasModulo(
            @RequestBody Aula modulo, HttpSession session) {
        JsonNodeFactory jsonFactory = JsonNodeFactory.instance;
        JsonResponse response = new JsonResponse();
        
        try {
            Oficina oficinaOERA = service.findOficinaOera();
            List<Aula> aulas = service.allAulasByOficinaModulo(oficinaOERA, modulo);
            
            ArrayNode arrayAulas = new ArrayNode(jsonFactory);
            for (Aula aula : aulas) {
                ObjectNode json = createAulasJson(aula);
                arrayAulas.add(json);
            }
            
            response.setData(arrayAulas);
            response.setSuccess(true);
            
        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }
    
    @ResponseBody
    @RequestMapping(value = "{tipoAccion}/excluir", method = RequestMethod.POST)
    public JsonResponse excluir(
            @PathVariable("tipoAccion") String tipoAccion,
            @RequestBody ObjectNode objeto,
            HttpSession session, HttpServletRequest request) {
        JsonResponse response = new JsonResponse();
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        ds.setFechaAccionAudit(new Date());
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            
            if (CursoMasivosController.TipoAccion.CURSO.name().equals(tipoAccion)) {
                CursoMasivoExamen cursoMasivoExamen = (CursoMasivoExamen) mapper.readValue(objeto.toString(), CursoMasivoExamen.class);
                service.excluirCursoMasivo(cursoMasivoExamen, ds);
            }
            response.setMessage("Excluido corretamente.");
            response.setSuccess(Boolean.TRUE);
        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }
    
    private ObjectNode createAulasJson(Aula aula) {
        ObjectNode json = JsonHelper.createJson(aula, JsonNodeFactory.instance, true, new String[]{
            "*",
            "id",
            "codigo",});
        return json;
    }
}
