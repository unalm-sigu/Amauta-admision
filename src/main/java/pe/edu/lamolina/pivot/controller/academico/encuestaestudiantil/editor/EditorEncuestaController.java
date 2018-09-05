package pe.edu.lamolina.pivot.controller.academico.encuestaestudiantil.editor;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.List;
import javax.servlet.http.HttpSession;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableResponse;
import pe.albatross.zelpers.miscelanea.ExceptionHandler;
import pe.albatross.zelpers.miscelanea.JsonResponse;
import pe.albatross.zelpers.miscelanea.ObjectUtil;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.albatross.zelpers.notify.Notificaciones;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Curso;
import pe.edu.lamolina.model.encuestaestudiantil.CursoSinEncuesta;
import pe.edu.lamolina.model.encuestaestudiantil.EncuestaEstudiantil;
import pe.edu.lamolina.model.examen.ExamenVirtual;
import pe.edu.lamolina.model.examen.PreguntaExamen;
import pe.edu.lamolina.model.examen.TipoExamenVirtual;
import pe.edu.lamolina.model.inscripcion.CicloPostula;
import pe.edu.lamolina.pivot.zelper.constant.Constantine;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

@Controller
@RequestMapping("academico/encuestaestudiantil/editor")
public class EditorEncuestaController {

    @Autowired
    EditorEncuestaService service;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @RequestMapping(method = RequestMethod.GET)
    public String index(Model model, HttpSession session) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        CicloAcademico cicloAcademico = ds.getCicloAcademico();
        model.addAttribute("cicloAcademico", cicloAcademico);
        return "academico/encuestaestudiantil/editor/encuestaEditor";
    }

    @ResponseBody
    @RequestMapping("list")
    public DynatableResponse list(DynatableFilter filter, HttpSession session) {
        DynatableResponse json = new DynatableResponse();
        try {

            List<ExamenVirtual> encuestas = service.allEncuesta(filter);
            ArrayNode array = new ArrayNode(JsonNodeFactory.instance);

            for (ExamenVirtual encuesta : encuestas) {
                ObjectNode node = new ObjectNode(JsonNodeFactory.instance);
                node.put("id", encuesta.getId());
                node.put("nombre", encuesta.getNombre());
                node.put("estado", encuesta.getEstado());
                node.put("estadoEnum", encuesta.getEstadoEnum().getValue());
                node.put("codigo", encuesta.getCodigo());
                node.put("preguntasDisponibles", encuesta.getPreguntasDisponibles());
                node.put("preguntasVisibles", encuesta.getPreguntasVisibles());
//                node.put("cicloInicio", (String) ObjectUtil.getParentTree(encuesta, "cicloAcademicoInicio.descripcion"));
//                node.put("cicloFin", (String) ObjectUtil.getParentTree(encuesta, "cicloAcademicoFin.descripcion"));
                node.put("tipoName", (String) ObjectUtil.getParentTree(encuesta, "tipoExamen.nombre"));
                node.put("tipoCodigo", (String) ObjectUtil.getParentTree(encuesta, "tipoExamen.codigo"));

                ArrayNode arrayEncus = new ArrayNode(JsonNodeFactory.instance);
                List<EncuestaEstudiantil> encusEstudiantes = encuesta.getEncuestasEstudiantiles();
                int loop = 1;
                for (EncuestaEstudiantil encuEst : encusEstudiantes) {
                    ObjectNode nodeEncu = new ObjectNode(JsonNodeFactory.instance);
                    if (encusEstudiantes.size() > 8 && loop == 7) {
                        nodeEncu.put("ciclo", "...");
                        loop++;
                        continue;
                    }

                    if (encusEstudiantes.size() > 8 && loop == 8) {
                        EncuestaEstudiantil encuEstFinal = encusEstudiantes.get(encusEstudiantes.size() - 1);
                        nodeEncu.put("ciclo", encuEstFinal.getCicloAcademico().getDescripcion());
                        break;
                    }

                    nodeEncu.put("ciclo", encuEst.getCicloAcademico().getDescripcion());
                    arrayEncus.add(nodeEncu);
                    loop++;
                }
                node.set("cicloEncuestas", arrayEncus);

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

    @RequestMapping("nuevo")
    public String nuevo(Model model, HttpSession session) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        CicloAcademico cicloAcademico = ds.getCicloAcademico();
        List<TipoExamenVirtual> tipos = service.allTipoEncuesta();
        model.addAttribute("cicloAcademico", cicloAcademico);
        model.addAttribute("encuesta", new ExamenVirtual());
        model.addAttribute("tipos", tipos);
        return "academico/encuestaestudiantil/editor/encuestaEditorForm";
    }

    @RequestMapping("{encuesta}/update")
    public String update(@PathVariable("encuesta") Long idEncuesta, Model model, HttpSession session) {
        ExamenVirtual evaluacionVirtual = service.findEncuesta(idEncuesta);
        List<TipoExamenVirtual> tipos = service.allTipoEncuesta();
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        CicloAcademico cicloAcademico = ds.getCicloAcademico();
        model.addAttribute("cicloAcademico", cicloAcademico);
        model.addAttribute("encuesta", evaluacionVirtual);
        model.addAttribute("tipos", tipos);
        return "academico/encuestaestudiantil/editor/encuestaEditorForm";
    }

    @RequestMapping("{encuesta}/preview")
    public String preview(@PathVariable("encuesta") Long idEncuesta, Model model, HttpSession session) {
        CicloPostula ciclo = service.findCicloActivo();
        ExamenVirtual encuesta = service.findEncuesta(idEncuesta);
        List<PreguntaExamen> preguntas = service.allPreguntasByEncuesta(encuesta);

        model.addAttribute("preguntas", preguntas);
        model.addAttribute("encuesta", encuesta);
        model.addAttribute("ciclo", ciclo);

        return "academico/encuestaestudiantil/preview/encuestaPreview";
    }

    @RequestMapping("save")
    public String save(ExamenVirtual encuesta, RedirectAttributes redirectAttr, Model model, HttpSession session) {
        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
            if (encuesta.getId() == null) {
                service.saveEncuesta(encuesta, ds);
                Notificaciones.crearMsg("Registro creado", redirectAttr);
            } else {
                service.updateEncuesta(encuesta);
                Notificaciones.crearMsg("Registro actualizado", redirectAttr);
            }

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, redirectAttr);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, redirectAttr);
        }

        return "redirect:/academico/encuestaestudiantil/editor";
    }

    @ResponseBody
    @RequestMapping("delete")
    public JsonResponse delete(ExamenVirtual encuesta, HttpSession session) {

        JsonResponse response = new JsonResponse();
        try {

            service.delete(encuesta);
            response.setMessage("Registro eliminado satisfactoriamente");
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
    @RequestMapping("duplicar")
    public JsonResponse duplicar(ExamenVirtual encuesta, HttpSession session) {

        JsonResponse response = new JsonResponse();
        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
            service.duplicar(encuesta, ds);
            response.setMessage("Registro eliminado satisfactoriamente");
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
    @RequestMapping("estado")
    public JsonResponse estado(ExamenVirtual encuesta, HttpSession session) {

        JsonResponse response = new JsonResponse();

        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
            service.cambiarEstadoEncuesta(encuesta, ds);
            response.setMessage("Registro actualizado satisfactoriamente.");
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
    @RequestMapping("searchcurso")
    public JsonResponse searchCurso(@RequestParam("nombre") String nombre, HttpSession session) {
        JsonNodeFactory jsonFactory = JsonNodeFactory.instance;
        JsonResponse response = new JsonResponse();
        try {
            List<Curso> cursos = service.allCursoByName(nombre);
            ArrayNode jsonList = new ArrayNode(jsonFactory);
            for (Curso curso : cursos) {
                ObjectNode json = new ObjectNode(jsonFactory);
                json.put("id", curso.getId());
                json.put("curso", curso.getNombre());
                json.put("codigo", curso.getCodigo());
                json.put("tpc", curso.getTpc());
                json.put("creditos", curso.getCreditos());
                json.put("departamento", (String) ObjectUtil.getParentTree(curso, "departamentoAcademico.nombre"));
                json.put("facultad", (String) ObjectUtil.getParentTree(curso, "departamentoAcademico.facultad.nombre"));
                json.put("especialidad", (String) ObjectUtil.getParentTree(curso, "carrera.nombre"));
                json.put("tipoEspecialidad", (String) ObjectUtil.getParentTree(curso, "carrera.tipoEnum.value"));
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
    @RequestMapping("allcursosinencuesta")
    public JsonResponse allCursoSinEncuesta(ExamenVirtual encuesta, HttpSession session) {
        JsonNodeFactory jsonFactory = JsonNodeFactory.instance;
        JsonResponse response = new JsonResponse();
        try {

            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
            List<Curso> cursos = service.allCursoSinEncuesta(encuesta, ds);

            ArrayNode jsonList = new ArrayNode(jsonFactory);
            for (Curso curso : cursos) {
                ObjectNode json = new ObjectNode(jsonFactory);
                json.put("id", curso.getId());
                json.put("curso", curso.getNombre());
                json.put("codigo", curso.getCodigo());
                json.put("tpc", curso.getTpc());
                json.put("creditos", curso.getCreditos());
                json.put("departamento", (String) ObjectUtil.getParentTree(curso, "departamentoAcademico.nombre"));
                json.put("facultad", (String) ObjectUtil.getParentTree(curso, "departamentoAcademico.facultad.nombre"));
                json.put("especialidad", (String) ObjectUtil.getParentTree(curso, "carrera.nombre"));
                json.put("tipoEspecialidad", (String) ObjectUtil.getParentTree(curso, "carrera.tipoEnum.value"));
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
    @RequestMapping("addcursosinencuesta")
    public JsonResponse addcursosinencuesta(CursoSinEncuesta cursoSinEncuesta, HttpSession session) {
        JsonResponse response = new JsonResponse();
        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
            service.addCursoSinEncuesta(cursoSinEncuesta, ds);
            response.setMessage("Registro creado satisfactoriamente");
            response.setSuccess(true);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("removecursosinencuesta")
    public JsonResponse removeCursoSinEncuesta(CursoSinEncuesta cursoSinEncuesta, HttpSession session) {
        JsonResponse response = new JsonResponse();
        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
            service.removeCursoSinEncuesta(cursoSinEncuesta, ds);
            response.setMessage("Registro removido");
            response.setSuccess(true);
        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

}
