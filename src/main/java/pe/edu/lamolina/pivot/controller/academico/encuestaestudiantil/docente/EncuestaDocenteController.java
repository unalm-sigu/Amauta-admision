package pe.edu.lamolina.pivot.controller.academico.encuestaestudiantil.docente;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.List;
import javax.persistence.Column;
import javax.servlet.http.HttpSession;
import org.joda.time.DateTime;
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
import pe.albatross.zelpers.miscelanea.ObjectUtil;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.encuestaestudiantil.ConfiguraEncuesta;
import pe.edu.lamolina.model.encuestaestudiantil.EncuestaDocente;
import pe.edu.lamolina.model.encuestaestudiantil.EncuestaEstudiantil;
import pe.edu.lamolina.pivot.zelper.constant.Constantine;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

@Controller
@RequestMapping("academico/encuestaestudiantil/docente")
public class EncuestaDocenteController {

    @Autowired
    EncuestaDocenteService service;
    @Autowired
    VisorEncuestaDocente visorEncuestaDocente;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @RequestMapping(method = RequestMethod.GET)
    public String index(Model model, HttpSession session) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        CicloAcademico cicloAcademico = ds.getCicloAcademico();
        EncuestaEstudiantil encuesta = service.findEncuestaDocente(cicloAcademico);

        model.addAttribute("cicloAcademico", cicloAcademico);
        model.addAttribute("visor", visorEncuestaDocente);
        model.addAttribute("encuesta", JsonHelper.createJson(encuesta, JsonNodeFactory.instance, true, new String[]{"*"}));
        return "academico/encuestaestudiantil/docente/encuestaDocente";
    }

    @ResponseBody
    @RequestMapping("list")
    public DynatableResponse list(DynatableFilter filter, HttpSession session) {
        DynatableResponse json = new DynatableResponse();
        try {

            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
            CicloAcademico ciclo = ds.getCicloAcademico();

            List<EncuestaDocente> encuestaDocentes = service.allEncuestaDocente(filter, ciclo);
            ArrayNode array = new ArrayNode(JsonNodeFactory.instance);

            for (EncuestaDocente enDocente : encuestaDocentes) {

                ObjectNode node = JsonHelper.createJson(enDocente, JsonNodeFactory.instance, true,
                        new String[]{
                            "*",
                            "docenteSeccion.docente.codigo",
                            "docenteSeccion.docente.departamentoAcademico.nombre",
                            "docenteSeccion.docente.departamentoAcademico.facultad.nombre",
                            "docenteSeccion.docente.persona.apellidosNombres",
                            "docenteSeccion.docente.persona.tipoDocumento.simbolo",
                            "docenteSeccion.docente.persona.numeroDocIdentidad",
                            "docenteSeccion.seccion.codigo2",
                            "docenteSeccion.seccion.tipoSeccion",
                            "docenteSeccion.seccion.docenteSeccion.*",
                            "docenteSeccion.seccion.grupoHoras.codigo",
                            "docenteSeccion.seccion.grupoSeccion.curso.nombre",
                            "docenteSeccion.seccion.grupoSeccion.curso.codigo",
                            "docenteSeccion.seccion.grupoSeccion.curso.tpc"
                        });
                node.put("esTeoriaPractica", enDocente.getEsTeoriaPractica() == 1 ? "Teo/Prac" : "");

                /**
                 * node.put("id", enDocente.getId());
                 * node.put("esTeoriaPractica", enDocente.getEsTeoriaPractica()
                 * == 1 ? "Teo/Prac" : ""); node.put("estado",
                 * enDocente.getEstado()); node.put("estadoEnum",
                 * enDocente.getEstadoEnum().getValue());
                 * node.put("fechaInicio", enDocente.getFechaInicio() != null ?
                 * new DateTime(enDocente.getFechaInicio()).toString("dd/MM/yyyy
                 * hh:mm") : ""); node.put("fechaFin", enDocente.getFechaFin()
                 * != null ? new
                 * DateTime(enDocente.getFechaFin()).toString("dd/MM/yyyy
                 * hh:mm") : ""); node.put("fechaEncuestaInicio",
                 * enDocente.getFechaEncuestaInicio() != null ? new
                 * DateTime(enDocente.getFechaEncuestaInicio()).toString("dd/MM/yyyy")
                 * : ""); node.put("fechaEncuestaFin",
                 * enDocente.getFechaEncuestaFin() != null ? new
                 * DateTime(enDocente.getFechaEncuestaFin()).toString("dd/MM/yyyy")
                 * : ""); node.put("alumnosInicio",
                 * enDocente.getAlumnosInicio()); node.put("alumnoFin",
                 * enDocente.getAlumnosFin()); node.put("alumnosEncuestados",
                 * enDocente.getAlumnosEncuestados()); node.put("descripcion",
                 * enDocente.getDescripcion());
                 *
                 * node.put("nombre", (String)
                 * ObjectUtil.getParentTree(enDocente,
                 * "docenteSeccion.docente.persona.apellidosNombres"));
                 * node.put("tipoDoc", (String)
                 * ObjectUtil.getParentTree(enDocente,
                 * "docenteSeccion.docente.persona.tipoDocumento.simbolo"));
                 * node.put("nroDocumento", (String)
                 * ObjectUtil.getParentTree(enDocente,
                 * "docenteSeccion.docente.persona.numeroDocIdentidad"));
                 * node.put("codigo", (String)
                 * ObjectUtil.getParentTree(enDocente,
                 * "docenteSeccion.docente.codigo")); node.put("seccion",
                 * (String) ObjectUtil.getParentTree(enDocente,
                 * "docenteSeccion.seccion.codigo")); node.put("seccionCodigo2",
                 * (String) ObjectUtil.getParentTree(enDocente,
                 * "docenteSeccion.seccion.codigo2")); node.put("grupoSeccion",
                 * (String) ObjectUtil.getParentTree(enDocente,
                 * "docenteSeccion.seccion.grupoSeccion.codigo"));
                 * node.put("grupoHoras", (String)
                 * ObjectUtil.getParentTree(enDocente,
                 * "docenteSeccion.seccion.grupoHoras.codigo"));
                 * node.put("curso", (String)
                 * ObjectUtil.getParentTree(enDocente,
                 * "docenteSeccion.seccion.grupoSeccion.curso.nombre"));
                 * node.put("cursoCodigo", (String)
                 * ObjectUtil.getParentTree(enDocente,
                 * "docenteSeccion.seccion.grupoSeccion.curso.codigo"));
                 * node.put("tpc", (String) ObjectUtil.getParentTree(enDocente,
                 * "docenteSeccion.seccion.grupoSeccion.curso.tpc"));
                 *
                 * node.put("examen", (String)
                 * ObjectUtil.getParentTree(enDocente,
                 * "encuestaEstudiantil.encuesta.nombre")); node.put("facultad",
                 * (String) ObjectUtil.getParentTree(enDocente,
                 * "docenteSeccion.seccion.grupoSeccion.curso.departamentoAcademico.facultad.nombre"));
                 * node.put("departamentoAcademico", (String)
                 * ObjectUtil.getParentTree(enDocente,
                 * "docenteSeccion.seccion.grupoSeccion.curso.departamentoAcademico.nombre"));
                 *
                 */
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
    public JsonResponse saveConfigEncuesta2(@RequestBody EncuestaEstudiantil encuestaEstudiantil, HttpSession session) {

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
    @RequestMapping("generar")
    public JsonResponse generar(HttpSession session) {

        JsonResponse response = new JsonResponse();

        try {

            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
            String msg = service.generarEncuesta(ds.getCicloAcademico(), ds);
            response.setSuccess(msg == null);
            response.setMessage(msg == null ? "Se inició proceso de generación en encuestas" : msg);

        } catch (PhobosException e) {
            visorEncuestaDocente.cancelarProceso();
            ExceptionHandler.handlePhobosEx(e, response);

        } catch (Exception e) {
            visorEncuestaDocente.cancelarProceso();
            ExceptionHandler.handleException(e, response);
        }
        return response;

    }

    @ResponseBody
    @RequestMapping("estadoGenerarEncuestas")
    public JsonResponse estadoGenerarEncuestas(HttpSession session) {
        JsonResponse json = new JsonResponse();

        try {
            json.setData(visorEncuestaDocente.getPorcentaje());
            json.setSuccess(visorEncuestaDocente.estaProcesando());
            json.setMessage(visorEncuestaDocente.getEstado());

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, json);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, json);

        } finally {
            return json;
        }

    }

    @ResponseBody
    @RequestMapping("estado")
    public JsonResponse estado(EncuestaDocente encuesta, HttpSession session) {

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
    @RequestMapping("encuestaDocente")
    public JsonResponse encuestaDocente(HttpSession session) {

        JsonResponse response = new JsonResponse();
        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
            CicloAcademico cicloAcademico = ds.getCicloAcademico();
            EncuestaEstudiantil encuesta = service.findEncuestaDocente(cicloAcademico);

            ObjectNode encuJson = JsonHelper.createJson(encuesta, JsonNodeFactory.instance, true,
                    new String[]{
                        "id", "estado", "estadoEnum", "objetivosEncuesta", "objetivosEncuestados",
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

}
