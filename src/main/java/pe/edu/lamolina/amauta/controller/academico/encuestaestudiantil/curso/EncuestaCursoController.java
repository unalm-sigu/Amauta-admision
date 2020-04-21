package pe.edu.lamolina.amauta.controller.academico.encuestaestudiantil.curso;

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
import pe.edu.lamolina.model.academico.DepartamentoAcademico;
import pe.edu.lamolina.model.academico.Facultad;
import pe.edu.lamolina.model.encuestaestudiantil.ConfiguraEncuesta;
import pe.edu.lamolina.model.encuestaestudiantil.EncuestaCurso;
import pe.edu.lamolina.model.encuestaestudiantil.EncuestaEstudiantil;
import pe.edu.lamolina.amauta.controller.seguridad.verificador.VerificadorService;
import pe.edu.lamolina.model.constantines.AcademicoConstantine;
import pe.edu.lamolina.model.constantines.GlobalConstantine;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;

@Controller
@RequestMapping("academico/encuestaestudiantil/curso")
public class EncuestaCursoController {

    @Autowired
    EncuestaCursoService service;

    @Autowired
    VerificadorService verificadorService;

    @Autowired
    VisorEncuestaCurso visorEncuestaCurso;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @RequestMapping(method = RequestMethod.GET)
    public String index(Model model, HttpSession session) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
        CicloAcademico cicloAcademico = ds.getCicloAcademico();

        model.addAttribute("cicloAcademico", cicloAcademico);
        model.addAttribute("visor", visorEncuestaCurso);
        model.addAttribute("facultadesJson", createFacultadesJson(cicloAcademico));
        model.addAttribute("departamentosJson", createDptosAcademicosJson(cicloAcademico));
        model.addAttribute("esEditorEncuestas", verificadorService.isEditorEncuestas(ds));
        return "academico/encuestaestudiantil/curso/encuestaCurso";
    }

    private ArrayNode createFacultadesJson(CicloAcademico cicloAcademico) {
        ArrayNode array = new ArrayNode(JsonNodeFactory.instance);
        List<Facultad> facultades = service.allFacultadesFromCursos(cicloAcademico);
        for (Facultad fac : facultades) {
            ObjectNode node = JsonHelper.createJson(fac, JsonNodeFactory.instance, new String[]{"id", "nombre", "codigo"});
            array.add(node);
        }
        return array;

    }

    private ArrayNode createDptosAcademicosJson(CicloAcademico cicloAcademico) {
        ArrayNode array = new ArrayNode(JsonNodeFactory.instance);
        List<DepartamentoAcademico> departamentos = service.allDepartamentosFromCursos(cicloAcademico);
        for (DepartamentoAcademico dpto : departamentos) {
            ObjectNode node = JsonHelper.createJson(dpto, JsonNodeFactory.instance,
                    new String[]{"id", "nombre", "codigo", "facultad.id", "facultad.nombre"}
            );
            node.put("nombreCodigo", dpto.getNombre() + " (" + dpto.getCodigo() + ")");
            array.add(node);
        }
        return array;

    }

    @ResponseBody
    @RequestMapping("list")
    public DynatableResponse list(DynatableFilter filter, HttpSession session) {
        DynatableResponse json = new DynatableResponse();
        try {

            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
            CicloAcademico ciclo = ds.getCicloAcademico();

            ConfiguraEncuesta cfg = service.findConfigEncuestaCurso(ciclo);
            boolean noEsSimultaneo = (cfg == null) ? false : cfg.getSimultaneo() != 1;
            List<EncuestaCurso> encuestaCursos = service.allEncuestaCurso(filter, ciclo, noEsSimultaneo);
            ArrayNode array = new ArrayNode(JsonNodeFactory.instance);

            for (EncuestaCurso enCurso : encuestaCursos) {

                ObjectNode node = JsonHelper.createJson(enCurso, JsonNodeFactory.instance, true,
                        new String[]{
                            "*",
                            "modalidadEstudio.codigo",
                            "modalidadEstudio.nombre",
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
                            "grupoSeccion.fechaInicioModular",
                            "grupoSeccion.fechaFinModular",
                            "grupoSeccion.tipoDictado",
                            "grupoSeccion.tipoDictadoEnum",
                            "grupoSeccion.curso.departamentoAcademico.nombre",
                            "grupoSeccion.curso.departamentoAcademico.facultad.nombre",
                            "encuestaDocente.docenteSeccion.principal",
                            "encuestaDocente.docenteSeccion.seccion.codigo2",
                            "encuestaDocente.docenteSeccion.seccion.tipoSeccion",
                            "encuestaDocente.docenteSeccion.seccion.grupoHoras.codigo",
                            "encuestaDocente.docenteSeccion.docente.codigo",
                            "encuestaDocente.docenteSeccion.docente.persona.apellidosNombres"
                        });
                node.put("conOpciones", noEsSimultaneo);
                node.put("esSimultaneo", !noEsSimultaneo);

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
    @RequestMapping("resumen")
    public JsonResponse resumen(HttpSession session) {

        JsonResponse response = new JsonResponse();

        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
            CicloAcademico cicloAcademico = ds.getCicloAcademico();
            EncuestaEstudiantil encuesta = service.findEncuestaCursoWithResumen(cicloAcademico);
            ObjectNode node = JsonHelper.createJson(encuesta, JsonNodeFactory.instance, true, new String[]{
                "*",
                "configuraEncuesta.*",
                "periodosEncuesta.*"
            });

            response.setData(node);
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
    @RequestMapping("generar")
    public JsonResponse generar(HttpSession session) {

        JsonResponse response = new JsonResponse();

        try {

            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
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

            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
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

            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
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
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
            CicloAcademico cicloAcademico = ds.getCicloAcademico();
            EncuestaEstudiantil encuesta = service.findEncuestaCursoWithResumen(cicloAcademico);

            ObjectNode encuJson = JsonHelper.createJson(encuesta, JsonNodeFactory.instance, true,
                    new String[]{
                        "id", "estado", "estadoEnum", "objetivosEncuesta", "objetivosEncuestados",
                        "encuestasActivas", "encuestasAnuladas", "encuestasSinPeriodo", "encuestasCerradas", "encuestasInnecesarias",
                        "encuestasProgramadas", "encuestasEjecutadas",
                        "periodosEncuesta.fechaInicio",
                        "periodosEncuesta.fechaFin",
                        "configuraEncuesta.cantidadMinimaAlumnosPregrado",
                        "configuraEncuesta.cantidadMinimaAlumnosPosgrado",
                        "configuraEncuesta.cantidadMaximaDocentes",
                        "configuraEncuesta.encuestaTeoriaPractica",
                        "configuraEncuesta.diasEncuesta",
                        "configuraEncuesta.simultaneo",
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

    @ResponseBody
    @RequestMapping("delete")
    public JsonResponse delete(@RequestBody EncuestaEstudiantil encuesta, HttpSession session) {
        JsonResponse response = new JsonResponse();
        try {
            service.delete(encuesta);
            response.setMessage("Encuesta Eliminada.");
            response.setSuccess(true);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("publicar")
    public JsonResponse publicar(@RequestBody EncuestaEstudiantil encuesta, HttpSession session) {
        JsonResponse response = new JsonResponse();
        try {
            service.publicar(encuesta);
            response.setMessage("Encuesta Publicada correctamente.");
            response.setSuccess(true);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

}
