package pe.edu.lamolina.pivot.controller.academico.cargaacademica;

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
import pe.albatross.zelpers.dynatable.DynatableFilter;
import pe.albatross.zelpers.dynatable.DynatableResponse;
import pe.edu.lamolina.pivot.model.academico.Evaluacion;
import pe.edu.lamolina.pivot.model.academico.GrupoSeccion;
import pe.edu.lamolina.pivot.model.academico.Seccion;
import pe.edu.lamolina.pivot.model.academico.TipoEvaluacion;
import pe.edu.lamolina.pivot.zelper.constant.Constantine;
import pe.edu.lamolina.pivot.zelper.model.DataSession;

@Controller
@RequestMapping("academico/docente/cargaacademica")
public class CargaAcademicaController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    CargaAcademicaService cargaAcademicaService;

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
        DataSession ds = (DataSession) session.getAttribute(Constantine.SESSION_USUARIO);

        return "app/academico/docente/cargaacademica/cargaAcademica";
    }

    @RequestMapping("sistemaCurso")
    public String sistemaCurso(Model model, HttpSession session) {
        DataSession ds = (DataSession) session.getAttribute(Constantine.SESSION_USUARIO);

        return "app/academico/docente/cargaacademica/sistemaCurso";
    }

    @ResponseBody
    @RequestMapping("list")
    public DynatableResponse list(DynatableFilter filter, HttpSession session) {

        DynatableResponse json = new DynatableResponse();
        DataSession ds = (DataSession) session.getAttribute(Constantine.SESSION_USUARIO);

        try {

            ArrayNode array = new ArrayNode(JsonNodeFactory.instance);
            List<Seccion> lista = cargaAcademicaService.allByCargaAcademica(filter);
            logger.debug("Lista {}", lista.size());
            for (Seccion seccion : lista) {
                ObjectNode node = new ObjectNode(JsonNodeFactory.instance);

                node.put("id", seccion.getId());
                node.put("idCurso", seccion.getGrupoSeccion().getCurso().getId());
                node.put("idSistemaCalificacion", seccion.getGrupoSeccion().getCurso().getPlanCalificacion() != null ? seccion.getGrupoSeccion().getCurso().getPlanCalificacion().getId().toString() : "");
                node.put("sistemaCalificacion", seccion.getGrupoSeccion().getCurso().getPlanCalificacion() != null ? seccion.getGrupoSeccion().getCurso().getPlanCalificacion().getCodigo() : "");
                node.put("nombre", seccion.getGrupoSeccion().getCurso().getNombre());
                node.put("codigo", seccion.getGrupoSeccion().getCurso().getCodigo());
                node.put("tpc", seccion.getGrupoSeccion().getCurso().getTpc());
                node.put("seccion", seccion.getCodigo());
                node.put("aula", seccion.getAula().getNombre());
                node.put("tipoSeccion", seccion.getTipoSeccion());
                node.put("alumnos", 35);
                node.put("horasSemanales", 3);
                node.put("estado", "DIC");
                node.put("estadoEnum", "Dictando");
                node.put("estadoSistema", seccion.getGrupoSeccion().getCurso().getPlanCalificacion() != null ? seccion.getGrupoSeccion().getCurso().getPlanCalificacion().getEstado() : "");
                node.put("estadoSistemaEnum", seccion.getGrupoSeccion().getCurso().getPlanCalificacion() != null ? seccion.getGrupoSeccion().getCurso().getPlanCalificacion().getEstadoEnum().getValue() : "");
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
    @RequestMapping("listEvaluacionPlan")
    public DynatableResponse listEvaluacionPlan(DynatableFilter filter,
            @RequestParam("planCalificacion") Long planCalificacion,
            HttpSession session) {
        try {

        } catch (Exception e) {
        }
        return null;
    }

    @RequestMapping("{seccion}/detalleSistemaCalificacion")
    public String detalleSistemaCalificacion(@PathVariable("seccion") Long idSeccion, Model model, HttpSession session) {
        DataSession ds = (DataSession) session.getAttribute(Constantine.SESSION_USUARIO);
        Seccion seccion = cargaAcademicaService.findSeccion(idSeccion);
        model.addAttribute("seccion", seccion);
        model.addAttribute("planCalificacion", seccion.getGrupoSeccion().getCurso().getPlanCalificacion());
        model.addAttribute("curso", seccion.getGrupoSeccion().getCurso());
        return "app/academico/docente/cargaacademica/detalleSistemaCalificacion";
    }

    @RequestMapping("expandir/{seccion}")
    public String expandir(Model model, HttpSession session, @PathVariable("seccion") Long idSeccion) {
        DataSession ds = (DataSession) session.getAttribute(Constantine.SESSION_USUARIO);
        Seccion seccion = cargaAcademicaService.findSeccion(idSeccion);
        GrupoSeccion grupoSeccion = cargaAcademicaService.findGrupo(seccion.getGrupoSeccion().getId());

        StringBuilder claves = new StringBuilder();
        for (Seccion sec : grupoSeccion.getSecciones()) {
            claves.append(sec.getCodigo());
            claves.append(",");

        }

        model.addAttribute("planCalificacion", seccion.getGrupoSeccion().getCurso().getPlanCalificacion());
        model.addAttribute("curso", seccion.getGrupoSeccion().getCurso());
        model.addAttribute("claves", claves.substring(0, claves.length() - 1));
        return "app/academico/docente/cargaacademica/expandirSistemaCalificacion";
    }

    @RequestMapping("detalleExpandirEvaluacion")
    public String detalleExapandirEva(Model model, HttpSession session) {
        DataSession ds = (DataSession) session.getAttribute(Constantine.SESSION_USUARIO);
        model.addAttribute("tipoEvaluaciones", cargaAcademicaService.allTipoEvaluacion());
        return "app/academico/docente/cargaacademica/detalleExpandirEvaluacion";
    }

    @RequestMapping("{cargaAcademica}/notasAcademicas")
    public String notasAcademicas(@PathVariable("cargaAcademica") Long idCargaAcademica, Model model, HttpSession session) {
        DataSession ds = (DataSession) session.getAttribute(Constantine.SESSION_USUARIO);

        return "app/academico/docente/cargaacademica/notasAcademicas";
    }

    @RequestMapping("{evaluacion}/evaluacion")
    public String evaluacion(@PathVariable("evaluacion") Long idEvaluacion, Model model, HttpSession session) {
        DataSession ds = (DataSession) session.getAttribute(Constantine.SESSION_USUARIO);

        Evaluacion eval = new Evaluacion();
        eval.setTipoEvaluacion(new TipoEvaluacion());
        eval.getTipoEvaluacion().setCodigo("PC1");
        model.addAttribute("evaluacion", eval);

        return "app/academico/docente/cargaacademica/notasAcademicas";
    }

    @RequestMapping("nuevo")
    public String nuevo(Model model, HttpSession session) {
        DataSession ds = (DataSession) session.getAttribute(Constantine.SESSION_USUARIO);

        return "app/academico/docente/cargaacademica/nuevoSistemaCalificacion";
    }

    @RequestMapping("detalleCambioNota")
    public String detalleCambioNota(Model model, HttpSession session) {
        DataSession ds = (DataSession) session.getAttribute(Constantine.SESSION_USUARIO);

        return "app/academico/docente/cargaacademica/detalleCambioNota";
    }

    @RequestMapping("unalm")
    public String unalm() {

        return "app/unalm/unalm";
    }
}
