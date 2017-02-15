package pe.edu.lamolina.pivot.controller.academico.acta;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.beans.PropertyEditorSupport;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pe.albatross.zelpers.dynatable.DynatableFilter;
import pe.albatross.zelpers.dynatable.DynatableResponse;
import pe.albatross.zelpers.miscelanea.ObjectUtil;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.pivot.model.academico.CicloAcademico;
import pe.edu.lamolina.pivot.model.academico.Curso;
import pe.edu.lamolina.pivot.model.academico.DepartamentoAcademico;
import pe.edu.lamolina.pivot.model.academico.GrupoSeccion;
import pe.edu.lamolina.pivot.model.academico.PlanCalificacion;
import pe.edu.lamolina.pivot.zelper.constant.Constantine;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

@Controller
@RequestMapping("academico/acta")
public class ActaController {

    @Autowired
    ActaService actaService;

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
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        model.addAttribute("docente", ds.getDocente());
        model.addAttribute("cicloAcademico", ds.getCicloAcademico());
        model.addAttribute("dptoAcad", ds.getDepartamentoAcademico());
        return "app/academico/acta/acta";
    }

    @ResponseBody
    @RequestMapping("list")
    public DynatableResponse list(DynatableFilter filter, HttpSession session) {

        DynatableResponse json = new DynatableResponse();

        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
            CicloAcademico periodo = ds.getCicloAcademico();

            List<DepartamentoAcademico> departamentosAcaActivos = actaService.allActiveDepartamentosAcademicos(filter);

            ArrayNode array = new ArrayNode(JsonNodeFactory.instance);

            for (DepartamentoAcademico dep : departamentosAcaActivos) {

                ObjectNode node = new ObjectNode(JsonNodeFactory.instance);

                node.put("idDep", dep.getId());
                node.put("nombreDep", dep.getNombre());
                array.add(node);
            }

            json.setData(array);
            json.setTotal(filter.getTotal());
            json.setFiltered(filter.getFiltered());

        } catch (Exception e) {
            json.setTotal(0);
        }

        return json;
    }

    @RequestMapping("{departamento}/departamento")
    public String departamento(@PathVariable("departamento") Long idDepartamento, Model model, HttpSession session, RedirectAttributes redirect) {

        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        DepartamentoAcademico depAcademico = actaService.findDepartamento(idDepartamento);
        logger.debug("departamento academico id {}", depAcademico.getId());
        List<GrupoSeccion> allGruposSeccion = actaService.allGrupoSeccionByFilter(ds.getCicloAcademico(), new DepartamentoAcademico(idDepartamento));
        logger.debug("cantidad de grupos secciones {}", allGruposSeccion.size());

        model.addAttribute("docente", ds.getDocente());
        model.addAttribute("cicloAcademico", ds.getCicloAcademico());
        model.addAttribute("departamentoAcademico", depAcademico);
        model.addAttribute("gruposSecciones", allGruposSeccion);

        return "app/academico/acta/actaDepartamento";

    }
    /*
    @ResponseBody
    @RequestMapping("listGrupo")
    public DynatableResponse listGrupo(DynatableFilter filter, HttpSession session) {

        DynatableResponse json = new DynatableResponse();
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);

        try {
            DepartamentoAcademico dpto = ds.getDepartamentoAcademico();

        //    List<PlanCalificacion> lstPLanCalificacion = sistemaService.allPlanesCalificacionByDynatable(filter, dpto);

            ArrayNode array = new ArrayNode(JsonNodeFactory.instance);

            for (PlanCalificacion planCalificacion : lstPLanCalificacion) {
                ObjectNode node = new ObjectNode(JsonNodeFactory.instance);

                node.put("id", planCalificacion.getId());
                node.put("codigo", planCalificacion.getCodigo());
                node.put("formula", planCalificacion.getFormula());
                node.put("descripcion", planCalificacion.getDescripcion());
                node.put("origen", planCalificacion.getOrigenEnum().getValue());
                node.put("fechaReg", TypesUtil.getStringDate(planCalificacion.getFechaRegistro(), "dd/MM/yyyy"));
                node.put("estado", planCalificacion.getEstado());
                node.put("estadoEnum", planCalificacion.getEstadoEnum().getValue());
                node.put("verSolicitud", planCalificacion.isEstadoSolicitado());
                node.put("verActivar", planCalificacion.isEstadoCreado());
                node.put("verInactivar", planCalificacion.isEstadoCreado() || planCalificacion.isEstadoActivado());
                node.put("verAprobar", planCalificacion.isEstadoSolicitado() || planCalificacion.isEstadoReenviado());

                node.put("verRechazar", planCalificacion.isEstadoSolicitado() || planCalificacion.isEstadoReenviado());
                node.put("verObservar", planCalificacion.isEstadoSolicitado() || planCalificacion.isEstadoReenviado());
                node.put("verReenviar", planCalificacion.isEstadoObservado());
                node.put("verAsignarCursos", planCalificacion.isEstadoActivado());
                List<Curso> cursos = new ArrayList<>();
                if (ObjectUtil.getParentTree(planCalificacion, "curso") != null) {
                    for (Curso cur : planCalificacion.getCurso()) {
                        if (cur.isEstadoActive()) {
                            cursos.add(cur);
                        }
                    }
                }
                node.put("cantidadCursos", cursos.size());
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
    }*/

}
