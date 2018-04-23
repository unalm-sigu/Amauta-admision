package pe.edu.lamolina.pivot.controller.academico.asistenciaacademica;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.beans.PropertyEditorSupport;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import javax.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableResponse;
import pe.albatross.zelpers.miscelanea.JsonResponse;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Curso;
import pe.edu.lamolina.model.academico.Docente;
import pe.edu.lamolina.model.academico.DocenteSeccion;
import pe.edu.lamolina.model.academico.GrupoSeccion;
import pe.edu.lamolina.model.academico.MatriculaSeccion;
import pe.edu.lamolina.model.academico.Seccion;
import pe.edu.lamolina.pivot.controller.academico.notasacademicas.CargaAcademicaService;
import pe.edu.lamolina.pivot.zelper.constant.Constantine;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

@Controller
@RequestMapping("academico/docente/asistenciaacademica")
public class AsistenciaAcademicaController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    CargaAcademicaService cargaAcademicaService;

    @Autowired
    AsistenciaAcademicaService asistenciaAcademicaServiceImp;

    @InitBinder
    public void initBinder(WebDataBinder dataBinder) {
        dataBinder.registerCustomEditor(Date.class, new PropertyEditorSupport() {
            @Override
            public void setAsText(String value) {
                try {
                    setValue(new SimpleDateFormat("dd/MM/yyyy").parse(value));
                } catch (ParseException e) {
                    setValue(null);
                }
            }
        });
        dataBinder.registerCustomEditor(BigDecimal.class, new PropertyEditorSupport() {
            @Override
            public void setAsText(String value) {
                try {
                    setValue(new BigDecimal(value.replaceAll(",", "")));
                } catch (Exception e) {
                    setValue(null);
                }
            }
        });
    }

    @RequestMapping(method = RequestMethod.GET)
    public String index(Model model, HttpSession session) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        model.addAttribute("docente", ds.getDocente());
        model.addAttribute("cicloAcademico", ds.getCicloAcademico());
        logger.debug("el docente logeado es {}", ds.getDocente().getId());
        //    cargaAcademicaService.createEvaluacionSeccionPorDocente(ds.getDocente(), ds);

        model.addAttribute("dptoAcad", ds.getDepartamentoAcademico());
        return "academico/docente/asistenciaacademica/asistenciaAcademica";
    }

    @ResponseBody
    @RequestMapping("listGruposSecciones")
    public DynatableResponse listGruposSecciones(DynatableFilter filter, HttpSession session) {
        DynatableResponse json = new DynatableResponse();
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);

        try {

            ArrayNode array = new ArrayNode(JsonNodeFactory.instance);
            CicloAcademico ciclo = ds.getCicloAcademico();
            Docente docente = ds.getDocente();

            List<GrupoSeccion> gruposSeccion = cargaAcademicaService.allGrupoByDocente(docente, ciclo, ds);
            logger.debug(this.getClass() + " Lista grupos por docente {}", gruposSeccion.size());

            for (GrupoSeccion grupoSeccion : gruposSeccion) {
                for (Seccion seccionEach : grupoSeccion.getSecciones()) {
                    seccionEach.setVerInformacion(Boolean.FALSE);
                    for (DocenteSeccion docenteSeccionEach : seccionEach.getDocenteSeccion()) {
                        if (docenteSeccionEach.getDocente().getId().equals(docente.getId())) {
                            seccionEach.setVerInformacion(Boolean.TRUE);
                        }
                    }
                }
                ObjectNode node = grupoSeccion.toJson();

                node.put("estadoGrupoEnum", grupoSeccion.getEstadoGrupoEnum().getValue());
                node.put("estadoGrupoCerrado", grupoSeccion.isEstadoGrupoCerrado());
                node.put("estadoGrupoCerrado", grupoSeccion.isEstadoGrupoCerrado());

                array.add(node);
            }

            json.setData(array);
            json.setTotal(gruposSeccion.size());
            json.setFiltered(gruposSeccion.size());

        } catch (Exception e) {
            e.printStackTrace();
            json.setTotal(0);
        }
        return json;
    }

    @RequestMapping("{seccion}/control")
    public String control(
            @PathVariable("seccion") Long idSeccion,
            Model model, HttpSession session) {
        logger.debug("la seccion es {}", idSeccion);
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);

        Seccion seccion = cargaAcademicaService.findSeccion(idSeccion);
        logger.debug("Seccion {}, Grupo Seccion {}", seccion.getId(), seccion.getGrupoSeccion().getId());
        GrupoSeccion grupoSeccion = cargaAcademicaService.findGrupo(seccion.getGrupoSeccion().getId());
        Curso curso = grupoSeccion.getCurso();

        model.addAttribute("seccion", seccion);
        model.addAttribute("seccionJson", seccion.toJson().toString());
        model.addAttribute("grupoSeccion", grupoSeccion);
        model.addAttribute("curso", curso);

        return "academico/docente/asistenciaacademica/controlAsistenciaForm";
    }

    @ResponseBody
    @RequestMapping("loadAsistenciaAcademicaForm")
    public JsonResponse loadGpoSeccionForm(@RequestParam(name = "seccion") Long seccionId, HttpSession session) {
        JsonResponse jsonResponse = new JsonResponse();
        JsonNodeFactory nc = JsonNodeFactory.instance;
        ObjectNode data = new ObjectNode(nc);

        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);

//todo
        jsonResponse.setSuccess(true);
        jsonResponse.setData(data);
        return jsonResponse;
    }

    @ResponseBody
    @RequestMapping("listMatriculasSeccionDyna")
    public DynatableResponse listMatriculasSeccionDyna(
            DynatableFilter filter,
            @RequestParam(name = "seccion", required = true) Long seccionId,
            HttpSession session) {
        DynatableResponse json = new DynatableResponse();
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);

        try {
            List<MatriculaSeccion> matriculasSeccionByFilter = asistenciaAcademicaServiceImp.allMatriculaSeccionBySeccion(new Seccion(seccionId));
            ArrayNode array = new ArrayNode(JsonNodeFactory.instance);

            for (MatriculaSeccion matriculaSeccionEach : matriculasSeccionByFilter) {
                array.addPOJO(matriculaSeccionEach.toJson());
            }

            json.setData(array);
            json.setTotal(matriculasSeccionByFilter.size());
            json.setFiltered(matriculasSeccionByFilter.size());

        } catch (Exception e) {
            e.printStackTrace();
            json.setTotal(0);
        }
        return json;
    }

}
