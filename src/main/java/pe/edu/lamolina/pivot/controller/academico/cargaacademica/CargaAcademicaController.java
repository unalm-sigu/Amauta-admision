package pe.edu.lamolina.pivot.controller.academico.cargaacademica;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.beans.PropertyEditorSupport;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import pe.albatross.zelpers.dynatable.DynatableFilter;
import pe.albatross.zelpers.dynatable.DynatableResponse;
import pe.edu.lamolina.pivot.model.academico.Evaluacion;
import pe.edu.lamolina.pivot.model.academico.TipoEvaluacion;
import pe.edu.lamolina.pivot.zelper.constant.Constantine;
import pe.edu.lamolina.pivot.zelper.model.DataSession;

@Controller
@RequestMapping("academico/docente/cargaacademica")
public class CargaAcademicaController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

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

            {
                ObjectNode node = new ObjectNode(JsonNodeFactory.instance);

                node.put("id", 233);
                node.put("idSistemaCalificacion", 74002);
                node.put("sistemaCalificacion", "SC-0025");
                node.put("nombre", "Fitotecnía Avanzada");
                node.put("codigo", "FI5210");
                node.put("tpc", "3-2-4");
                node.put("seccion", "2030");
                node.put("aula", "B30");
                node.put("tipoSeccion", "Teoría");
                node.put("alumnos", 35);
                node.put("horasSemanales", 3);
                node.put("estado", "DIC");
                node.put("estadoEnum", "Dictando");
                node.put("estadoSistema", "PEND");
                node.put("estadoSistemaEnum", "Pendiente");
                array.add(node);
            }
            {
                ObjectNode node = new ObjectNode(JsonNodeFactory.instance);

                node.put("id", 552);
                node.put("idSistemaCalificacion", 20);
                node.put("sistemaCalificacion", "SC-0512");
                node.put("nombre", "Genética de Hongos");
                node.put("codigo", "GE8541");
                node.put("tpc", "3-0-3");
                node.put("seccion", "4120");
                node.put("aula", "S42");
                node.put("tipoSeccion", "Teoría");
                node.put("alumnos", 28);
                node.put("horasSemanales", 3);
                node.put("estado", "DIC");
                node.put("estadoEnum", "Dictando");
                node.put("estadoSistema", "ACEP");
                node.put("estadoSistemaEnum", "Aceptado");
                array.add(node);
            }
            {
                ObjectNode node = new ObjectNode(JsonNodeFactory.instance);

                node.put("id", 8541);
                node.put("idSistemaCalificacion", 544);
                node.put("sistemaCalificacion", "SC-0842");
                node.put("nombre", "Fitopatología");
                node.put("codigo", "RT8455");
                node.put("tpc", "0-4-2");
                node.put("seccion", "8511");
                node.put("aula", "L88");
                node.put("tipoSeccion", "Práctica");
                node.put("alumnos", 28);
                node.put("horasSemanales", 4);
                node.put("estado", "DIC");
                node.put("estadoEnum", "Dictando");
                node.put("estadoSistema", "CER");
                node.put("estadoSistemaEnum", "Cerrado");
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

    @RequestMapping("{sistema}/detalleSistemaCalificacion")
    public String detalleSistemaCalificacion(@PathVariable("sistema") Long idSistema, Model model, HttpSession session) {
        DataSession ds = (DataSession) session.getAttribute(Constantine.SESSION_USUARIO);

        return "app/academico/docente/cargaacademica/detalleSistemaCalificacion";
    }

    @RequestMapping("expandir")
    public String expandir(Model model, HttpSession session) {
        DataSession ds = (DataSession) session.getAttribute(Constantine.SESSION_USUARIO);

        return "app/academico/docente/cargaacademica/expandirSistemaCalificacion";
    }

    @RequestMapping("detalleExpandirEvaluacion")
    public String detalleExapandirEva(Model model, HttpSession session) {
        DataSession ds = (DataSession) session.getAttribute(Constantine.SESSION_USUARIO);

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
