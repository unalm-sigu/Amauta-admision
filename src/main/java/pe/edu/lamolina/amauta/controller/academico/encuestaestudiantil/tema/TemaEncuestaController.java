package pe.edu.lamolina.amauta.controller.academico.encuestaestudiantil.tema;

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
import org.springframework.web.bind.annotation.ResponseBody;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring4.SpringTemplateEngine;
import pe.albatross.zelpers.miscelanea.ExceptionHandler;
import pe.albatross.zelpers.miscelanea.JsonResponse;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.enums.EstadoBloqueEnum;
import pe.edu.lamolina.model.enums.EstadoSubTituloEnum;
import pe.edu.lamolina.model.enums.EstadoTemaEnum;
import pe.edu.lamolina.model.examen.BloquePreguntas;
import pe.edu.lamolina.model.examen.ExamenVirtual;
import pe.edu.lamolina.model.examen.SubTituloExamen;
import pe.edu.lamolina.model.examen.TemaExamenVirtual;
import pe.edu.lamolina.model.constantines.AcademicoConstantine;
import pe.edu.lamolina.model.constantines.GlobalConstantine;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;

@Controller
@RequestMapping("academico/encuestaestudiantil/editor/tema")
public class TemaEncuestaController {

    @Autowired
    TemaEncuestaService service;

    @Autowired
    SpringTemplateEngine springHtml;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @RequestMapping(method = RequestMethod.GET, path = "{encuesta}")
    public String index(Model model, @PathVariable("encuesta") Long idEncuesta, HttpSession session) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
        CicloAcademico cicloAcademico = ds.getCicloAcademico();
        ExamenVirtual encuesta = service.findEncuesta(idEncuesta);
        model.addAttribute("encuesta", encuesta);
        model.addAttribute("cicloAcademico", cicloAcademico);
        return "academico/encuestaestudiantil/tema/temaEncuesta";
    }

    @ResponseBody
    @RequestMapping("list")
    public JsonResponse list(HttpSession session, Long idEncuesta) {

        JsonResponse response = new JsonResponse();

        try {

            List<TemaExamenVirtual> temas = service.allTema(new ExamenVirtual(idEncuesta));
            ArrayNode array = this.createNodes(temas);
            response.setData(array);
            response.setSuccess(true);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        } finally {
            return response;
        }

    }

    private ArrayNode createNodes(List<TemaExamenVirtual> temas) {

        JsonNodeFactory jsonFactory = JsonNodeFactory.instance;
        ArrayNode array = new ArrayNode(jsonFactory);

        for (TemaExamenVirtual tema : temas) {

            ObjectNode jtema = new ObjectNode(jsonFactory);

            jtema.put("id", tema.getId());
            jtema.put("nombre", tema.getNombre());
            jtema.put("desactivado", EstadoTemaEnum.ACT.name().equalsIgnoreCase(tema.getEstado()));
            jtema.put("activado", EstadoTemaEnum.INA.name().equalsIgnoreCase(tema.getEstado()));
            jtema.put("tipo", "TEMA");
            jtema.put("isSortable", true);
            jtema.put("preguntasVisibles", tema.getPreguntasVisibles());
            jtema.put("subtitulosVisibles", tema.getSubtitulosVisibles());
            jtema.put("bloquesVisibles", 0);

            if (tema.getSubTituloEvaluacionVirtual().size() > 0) {

                ArrayNode arraySubTitle = new ArrayNode(jsonFactory);

                for (SubTituloExamen subtitulo : tema.getSubTituloEvaluacionVirtual()) {

                    ObjectNode jtitle = new ObjectNode(jsonFactory);

                    jtitle.put("id", subtitulo.getId());
                    jtitle.put("nombre", subtitulo.getNombre());
                    jtitle.put("desactivado", EstadoSubTituloEnum.ACT.name().equalsIgnoreCase(subtitulo.getEstado()));
                    jtitle.put("activado", EstadoSubTituloEnum.INA.name().equalsIgnoreCase(subtitulo.getEstado()));
                    jtitle.put("tipo", "SUBTITULO");
                    jtitle.put("isSortable", true);
                    jtitle.put("preguntasVisibles", subtitulo.getPreguntasVisibles());
                    jtitle.put("bloquesVisibles", subtitulo.getBloquesVisibles());
                    jtitle.put("subtitulosVisibles", 0);

                    if (subtitulo.getBloquePreguntas().size() > 0) {
                        ArrayNode arrayBloque = new ArrayNode(jsonFactory);
                        for (BloquePreguntas bloque : subtitulo.getBloquePreguntas()) {

                            ObjectNode jbloque = new ObjectNode(jsonFactory);

                            jbloque.put("id", bloque.getId());
                            jbloque.put("nombre", bloque.getNombre());
                            jbloque.put("desactivado", EstadoBloqueEnum.ACT.name().equalsIgnoreCase(bloque.getEstado()));
                            jbloque.put("activado", EstadoBloqueEnum.INA.name().equalsIgnoreCase(bloque.getEstado()));
                            jbloque.put("tipo", "BLOQUE");
                            jbloque.put("isSortable", false);
                            jbloque.put("preguntasVisibles", bloque.getPreguntasVisibles());
                            jbloque.put("bloquesVisibles", 0);
                            jbloque.put("subtitulosVisibles", 0);

                            arrayBloque.add(jbloque);
                        }
                        jtitle.set("nodes", arrayBloque);
                    }
                    arraySubTitle.add(jtitle);
                }
                jtema.set("nodes", arraySubTitle);
            }
            array.add(jtema);
        }
        return array;
    }

    @ResponseBody
    @RequestMapping("saveTema")
    public JsonResponse saveTema(TemaExamenVirtual tema, HttpSession session) {

        JsonResponse response = new JsonResponse();

        try {

            if (tema.getId() == null) {
                service.saveTema(tema);
                response.setMessage("Registro creado satisfactoriamente");
            } else {
                service.updateTema(tema);
                response.setMessage("Registro actualizado satisfactoriamente");
            }

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
    @RequestMapping("saveSubTitulo")
    public JsonResponse saveSubTitulo(SubTituloExamen subtitulo, HttpSession session) {

        JsonResponse response = new JsonResponse();

        try {

            if (subtitulo.getId() == null) {
                service.saveSubTitulo(subtitulo);
                response.setMessage("Registro creado satisfactoriamente");
            } else {
                service.updateSubTitulo(subtitulo);
                response.setMessage("Registro actualizado satisfactoriamente");
            }

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
    @RequestMapping("saveBloque")
    public JsonResponse saveBloque(BloquePreguntas bloque, HttpSession session) {

        JsonResponse response = new JsonResponse();

        try {

            if (bloque.getId() == null) {
                service.saveBloque(bloque);
                response.setMessage("Registro creado satisfactoriamente");
            } else {
                service.updateBloque(bloque);
                response.setMessage("Registro actualizado satisfactoriamente");
            }

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
    @RequestMapping("deleteSubTitulo")
    public JsonResponse deleteSubTitulo(SubTituloExamen subtitulo, HttpSession session) {

        JsonResponse response = new JsonResponse();
        try {
            service.deleteSubTitulo(subtitulo);
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
    @RequestMapping("deleteTema")
    public JsonResponse deleteTema(TemaExamenVirtual tema, HttpSession session) {

        JsonResponse response = new JsonResponse();
        try {
            service.deleteTema(tema);
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
    @RequestMapping("deleteBloque")
    public JsonResponse deleteBloque(BloquePreguntas bloque, HttpSession session) {

        JsonResponse response = new JsonResponse();
        try {
            service.deleteBloque(bloque);
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
    @RequestMapping("updateTema")
    public JsonResponse updateTema(TemaExamenVirtual tema, HttpSession session) {

        JsonResponse response = new JsonResponse();

        try {

            TemaExamenVirtual temaExamenVirtual = service.findTema(tema);
            Context ctx = new Context();

            ctx.setVariable("temaExamenVirtual", temaExamenVirtual);
            String htmlContent = springHtml.process("academico/encuestaestudiantil/tema/temaFormEdit", ctx);

            response.setData(htmlContent);
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
    @RequestMapping("updateSubTitulo")
    public JsonResponse updateSubTitulo(SubTituloExamen subtitulo, HttpSession session) {

        JsonResponse response = new JsonResponse();

        try {

            SubTituloExamen subTituloExamen = service.findSubTitulo(subtitulo);
            Context ctx = new Context();

            ctx.setVariable("subTituloExamen", subTituloExamen);
            String htmlContent = springHtml.process("evaluacionvirtual/evaluacion/subTituloFormEdit", ctx);

            response.setData(htmlContent);
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
    @RequestMapping("updateBloque")
    public JsonResponse updateBloque(BloquePreguntas bloque, HttpSession session) {

        JsonResponse response = new JsonResponse();

        try {

            BloquePreguntas bloquePreguntas = service.findBloque(bloque);
            Context ctx = new Context();

            ctx.setVariable("bloquePreguntas", bloquePreguntas);
            String htmlContent = springHtml.process("evaluacionvirtual/evaluacion/bloqueFormEdit", ctx);

            response.setData(htmlContent);
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
    @RequestMapping("itemSort")
    public JsonResponse itemSort(Integer itemSort, Long instancia, String tipo, HttpSession session) {

        JsonResponse response = new JsonResponse();

        try {

            service.itemSort(itemSort, instancia, tipo);
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
    @RequestMapping("estado")
    public JsonResponse estado(Long instancia, String tipo, HttpSession session) {

        JsonResponse response = new JsonResponse();

        try {

            service.estado(instancia, tipo);
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

}
