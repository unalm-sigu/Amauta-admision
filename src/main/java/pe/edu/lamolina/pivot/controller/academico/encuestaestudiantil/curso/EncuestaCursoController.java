package pe.edu.lamolina.pivot.controller.academico.encuestaestudiantil.curso;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.List;
import javax.servlet.http.HttpSession;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableResponse;
import pe.albatross.zelpers.miscelanea.ExceptionHandler;
import pe.albatross.zelpers.miscelanea.JsonResponse;
import pe.albatross.zelpers.miscelanea.ObjectUtil;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.encuestaestudiantil.EncuestaCurso;
import pe.edu.lamolina.pivot.zelper.constant.Constantine;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

@Controller
@RequestMapping("academico/encuestaestudiantil/curso")
public class EncuestaCursoController {

    @Autowired
    EncuestaCursoService service;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @RequestMapping(method = RequestMethod.GET)
    public String index(Model model, HttpSession session) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        CicloAcademico cicloAcademico = ds.getCicloAcademico();
        model.addAttribute("cicloAcademico", cicloAcademico);
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

                ObjectNode node = new ObjectNode(JsonNodeFactory.instance);
                node.put("id", enCurso.getId());
                node.put("estado", enCurso.getEstado());
                node.put("estadoEnum", enCurso.getEstadoEnum().getValue());
                node.put("fechaInicio", enCurso.getFechaInicio() != null ? new DateTime(enCurso.getFechaInicio()).toString("dd/MM/yyyy hh:mm") : "");
                node.put("fechaFin", enCurso.getFechaFin() != null ? new DateTime(enCurso.getFechaFin()).toString("dd/MM/yyyy hh:mm") : "");
                node.put("fechaEncuestaInicio", enCurso.getFechaEncuestaInicio() != null ? new DateTime(enCurso.getFechaEncuestaInicio()).toString("dd/MM/yyyy") : "");
                node.put("fechaEncuestaFin", enCurso.getFechaEncuestaFin() != null ? new DateTime(enCurso.getFechaEncuestaFin()).toString("dd/MM/yyyy") : "");
                node.put("alumnosInicio", enCurso.getAlumnosInicio());
                node.put("alumnoFin", enCurso.getAlumnoFin());
                node.put("alumnosEncuestados", enCurso.getAlumnosEncuestados());
                node.put("descripcion", enCurso.getDescripcion());

                node.put("grupoSeccion", (String) ObjectUtil.getParentTree(enCurso, "grupoSeccion.codigo"));
                node.put("curso", (String) ObjectUtil.getParentTree(enCurso, "grupoSeccion.curso.nombre"));
                node.put("cursoCodigo", (String) ObjectUtil.getParentTree(enCurso, "grupoSeccion.curso.codigo"));
                node.put("tpc", (String) ObjectUtil.getParentTree(enCurso, "grupoSeccion.curso.tpc"));

                node.put("facultad", (String) ObjectUtil.getParentTree(enCurso, "grupoSeccion.curso.departamentoAcademico.facultad.nombre"));
                node.put("departamentoAcademico", (String) ObjectUtil.getParentTree(enCurso, "grupoSeccion.curso.departamentoAcademico.nombre"));

                node.put("examen", (String) ObjectUtil.getParentTree(enCurso, "encuestaEstudiantil.encuesta.nombre"));

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
            service.generarEncuesta(ds);
            response.setMessage("Registro modificado satisfactoriamente");
            response.setSuccess(true);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
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

}
