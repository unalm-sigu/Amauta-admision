package pe.edu.lamolina.pivot.controller.academico.encuestaestudiantil.curso;

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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableResponse;
import pe.albatross.zelpers.miscelanea.ExceptionHandler;
import pe.albatross.zelpers.miscelanea.JsonHelper;
import pe.albatross.zelpers.miscelanea.JsonResponse;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.encuestaestudiantil.EncuestaCurso;
import pe.edu.lamolina.model.encuestaestudiantil.EncuestaDocente;
import pe.edu.lamolina.model.encuestaestudiantil.EncuestaEstudiantil;
import pe.edu.lamolina.model.encuestaestudiantil.PuntajeEncuestaDocente;
import pe.edu.lamolina.model.encuestaestudiantil.ResumenEncuestaDocente;
import pe.edu.lamolina.pivot.zelper.constant.Constantine;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

@Controller
@RequestMapping("academico/encuestaestudiantil/curso")
public class EncuestaCursoController {

    @Autowired
    EncuestaCursoService service;
    @Autowired
    VisorEncuestaCurso visorEncuestaCurso;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @RequestMapping(method = RequestMethod.GET)
    public String index(Model model, HttpSession session) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        CicloAcademico cicloAcademico = ds.getCicloAcademico();
        EncuestaEstudiantil encuesta = service.findEncuestaCurso(cicloAcademico);

        model.addAttribute("cicloAcademico", cicloAcademico);
        model.addAttribute("visor", visorEncuestaCurso);
        model.addAttribute("encuesta", JsonHelper.createJson(encuesta, JsonNodeFactory.instance, true, new String[]{"*"}));
        return "academico/encuestaestudiantil/curso/encuestaCurso";
    }

    @ResponseBody
    @RequestMapping("list")
    public DynatableResponse list(DynatableFilter filter, HttpSession session) {
        DynatableResponse json = new DynatableResponse();
        try {

            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
            CicloAcademico ciclo = ds.getCicloAcademico();

            List<EncuestaCurso> encuestaCursos = service.allEncuestaCurso(filter, ciclo);
            ArrayNode array = new ArrayNode(JsonNodeFactory.instance);

            for (EncuestaCurso enCurso : encuestaCursos) {

                ObjectNode node = JsonHelper.createJson(enCurso, JsonNodeFactory.instance, true,
                        new String[]{
                            "*",
                            "grupoSeccion.secciones.codigo2",
                            "grupoSeccion.secciones.docenteSeccion.principal",
                            "grupoSeccion.secciones.docenteSeccion.seccion.codigo2",
                            "grupoSeccion.secciones.docenteSeccion.seccion.tipoSeccion",
                            "grupoSeccion.secciones.docenteSeccion.docente.codigo",
                            "grupoSeccion.secciones.docenteSeccion.docente.persona.apellidosNombres",
                            "grupoSeccion.secciones.grupoHoras.codigo",
                            "grupoSeccion.curso.codigo",
                            "grupoSeccion.curso.nombre",
                            "grupoSeccion.curso.tpc",
                            "grupoSeccion.curso.departamentoAcademico.nombre",
                            "grupoSeccion.curso.departamentoAcademico.facultad.nombre"
                        });

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
    @RequestMapping("generar")
    public JsonResponse generar(HttpSession session) {

        JsonResponse response = new JsonResponse();

        try {

            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
            String msg = service.generarEncuesta(ds.getCicloAcademico(), ds);
            response.setSuccess(msg == null);
            response.setMessage(msg == null ? "Se inició proceso de generación en encuestas" : msg);

        } catch (PhobosException e) {
            visorEncuestaCurso.cancelarProceso();
            ExceptionHandler.handlePhobosEx(e, response);

        } catch (Exception e) {
            visorEncuestaCurso.cancelarProceso();
            ExceptionHandler.handleException(e, response);
        }
        return response;

    }

    @ResponseBody
    @RequestMapping("estado")
    public JsonResponse estado(EncuestaCurso encuesta, HttpSession session) {

        JsonResponse response = new JsonResponse();

        try {

            service.cambiarEstadoEncuesta(encuesta);
            response.setMessage("Registro actualizado satisfactoriamente.");
            response.setSuccess(true);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("activar")
    public JsonResponse activar(HttpSession session) {

        JsonResponse response = new JsonResponse();

        try {

            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
            service.activarEncuesta(ds.getCicloAcademico(), ds);
            response.setMessage("Encuesta activada satisfactoriamente");
            response.setSuccess(true);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;

    }

    @ResponseBody
    @RequestMapping("saveConfigEncuesta")
    public JsonResponse saveConfigEncuesta(@RequestBody EncuestaEstudiantil encuestaEstudiantil, HttpSession session) {

        JsonResponse response = new JsonResponse();

        try {

            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
            CicloAcademico ciclo = ds.getCicloAcademico();
            service.saveDetalleConfigEncuesta(encuestaEstudiantil, ciclo, ds);
            response.setMessage("Encuesta configurada satisfactoriamente");
            response.setSuccess(true);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;

    }

    @ResponseBody
    @RequestMapping("estadoGenerarEncuestas")
    public JsonResponse estadoGenerarEncuestas(HttpSession session) {
        JsonResponse json = new JsonResponse();

        try {
            json.setData(visorEncuestaCurso.getPorcentaje());
            json.setSuccess(visorEncuestaCurso.estaProcesando());
            json.setMessage(visorEncuestaCurso.getEstado());

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, json);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, json);

        } finally {
            return json;
        }

    }

    @ResponseBody
    @RequestMapping("encuestaCurso")
    public JsonResponse encuestaCurso(HttpSession session) {

        JsonResponse response = new JsonResponse();
        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
            CicloAcademico cicloAcademico = ds.getCicloAcademico();
            EncuestaEstudiantil encuesta = service.findEncuestaCurso(cicloAcademico);

            ObjectNode encuJson = JsonHelper.createJson(encuesta, JsonNodeFactory.instance, true,
                    new String[]{
                        "id", "estado", "estadoEnum", "objetivosEncuesta", "objetivosEncuestados",
                        "encuestasActivas", "encuestasAnuladas", "encuestasSinPeriodo", "encuestasCerradas", "encuestasInnecesarias",
                        "encuestasProgramadas", "encuestasEjecutadas",
                        "periodosEncuesta.fechaInicio",
                        "periodosEncuesta.fechaFin",
                        "configuraEncuesta.cantidadMinimaAlumnos",
                        "configuraEncuesta.cantidadMaximaDocentes",
                        "configuraEncuesta.encuestaTeoriaPractica",
                        "configuraEncuesta.diasEncuesta",
                        "cursosNoEncuestar.curso.codigo",
                        "cursosNoEncuestar.curso.nombre",
                        "cursosNoEncuestar.curso.tpc"
                    });

            response.setData(encuJson);
            response.setSuccess(true);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

//    @ResponseBody
//    @RequestMapping(value = "/{id}/resumen/preguntas", method = RequestMethod.GET)
//    public JsonResponse resumenPreguntas(@PathVariable Long id) {
//
//        JsonResponse response = new JsonResponse();
//        try {
//
//            List<ResumenEncuestaDocente> resumenes = service.resumenPreguntasLikert(new EncuestaDocente(id));
//            ArrayNode arr = new ArrayNode(JsonNodeFactory.instance);
//
//            for (ResumenEncuestaDocente resumene : resumenes) {
//                arr.add(JsonHelper.createJson(resumene, JsonNodeFactory.instance, new String[]{
//                    "id",
//                    "puntaje",
//                    "opcionLikert.id",
//                    "opcionLikert.opcion",
//                    "pregunta.id",
//                    "pregunta.texto"
//                }));
//            }
//            response.setData(arr);
//            response.setSuccess(true);
//        } catch (PhobosException e) {
//            ExceptionHandler.handlePhobosEx(e, response);
//        } catch (Exception e) {
//            ExceptionHandler.handleException(e, response);
//        }
//        return response;
//    }
//
//    @ResponseBody
//    @RequestMapping(value = "/{id}/resumen/comentarios", method = RequestMethod.GET)
//    public JsonResponse resumenComentarios(@PathVariable Long id) {
//
//        JsonResponse response = new JsonResponse();
//        try {
//            List<String> resumenes = service.resumenComentarios(new EncuestaDocente(id));
//            ArrayNode arr = new ArrayNode(JsonNodeFactory.instance);
//            for (String resumen : resumenes) {
//                arr.add(resumen);
//            }
//            response.setData(arr);
//            response.setSuccess(true);
//        } catch (PhobosException e) {
//            ExceptionHandler.handlePhobosEx(e, response);
//        } catch (Exception e) {
//            ExceptionHandler.handleException(e, response);
//        }
//        return response;
//    }
//
//    @ResponseBody
//    @RequestMapping(value = "/{id}/resumen/temas", method = RequestMethod.GET)
//    public JsonResponse resumenTemas(@PathVariable Long id) {
//
//        JsonResponse response = new JsonResponse();
//        try {
//            List<PuntajeEncuestaDocente> resumenes = service.resumenPuntajeTemas(new EncuestaDocente(id));
//            ArrayNode arr = new ArrayNode(JsonNodeFactory.instance);
//            for (PuntajeEncuestaDocente resumen : resumenes) {
//                arr.add(JsonHelper.createJson(resumen, JsonNodeFactory.instance, new String[]{
//                    "puntaje",
//                    "desviacionStandar",
//                    "temaEncuesta.nombre"
//                }));
//            }
//            response.setData(arr);
//            response.setSuccess(true);
//        } catch (PhobosException e) {
//            ExceptionHandler.handlePhobosEx(e, response);
//        } catch (Exception e) {
//            ExceptionHandler.handleException(e, response);
//        }
//        return response;
//    }

}
