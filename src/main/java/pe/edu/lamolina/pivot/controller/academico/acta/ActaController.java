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
import org.apache.commons.lang3.StringUtils;
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
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableResponse;
import pe.albatross.zelpers.miscelanea.ExceptionHandler;
import pe.albatross.zelpers.miscelanea.JsonResponse;
import pe.albatross.zelpers.miscelanea.ObjectUtil;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.DepartamentoAcademico;
import pe.edu.lamolina.model.academico.Docente;
import pe.edu.lamolina.model.academico.DocenteSeccion;
import pe.edu.lamolina.model.academico.GrupoSeccion;
import pe.edu.lamolina.model.academico.Seccion;
import pe.edu.lamolina.model.enums.EstadoEnum;
import pe.edu.lamolina.pivot.controller.academico.acta.reporte.RecordDeActasExcelView;
import pe.edu.lamolina.pivot.zelper.constant.Constantine;
import pe.edu.lamolina.pivot.zelper.constant.Messages;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

@Controller
@RequestMapping("academico/acta")
public class ActaController {

    @Autowired
    ActaService service;

    @Autowired
    RecordDeActasExcelView recordDeActasExcelView;

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
        //   model.addAttribute("docente", ds.getDocente());
        model.addAttribute("cicloAcademico", ds.getCicloAcademico());
        //    model.addAttribute("dptoAcad", ds.getDepartamentoAcademico());
        return "academico/acta/acta";
    }

    @ResponseBody
    @RequestMapping("list")
    public DynatableResponse list(DynatableFilter filter, HttpSession session) {

        DynatableResponse json = new DynatableResponse();

        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
            CicloAcademico ciclo = ds.getCicloAcademico();

            List<DepartamentoAcademico> departamentosAcaActivos = service.allActiveDepartamentosAcademicos(filter, ds.getDepartamentos(), ciclo);

            List<Long> departamentos = new ArrayList<>();
            for (DepartamentoAcademico departamento : departamentosAcaActivos) {
                departamentos.add(departamento.getId());
            }

            List<DepartamentoAcademico> counts = new ArrayList<>();
            if (!departamentos.isEmpty()) {
                logger.debug("Departamentos {}", StringUtils.join(departamentos, ","));
                logger.debug("Ciclo Academico {}", ds.getCicloAcademico().getId());
                counts = service.countGroupsByFilter(departamentos, ds.getCicloAcademico(), null);
                logger.debug("Cantidad de resumen de cantidades {}", counts.size());
            }

            ArrayNode array = new ArrayNode(JsonNodeFactory.instance);
            for (DepartamentoAcademico dep : departamentosAcaActivos) {

                ObjectNode node = new ObjectNode(JsonNodeFactory.instance);

                node.put("idDep", dep.getId());
                node.put("nombreDep", dep.getNombre());

                if (counts.contains(dep)) {

                    DepartamentoAcademico countDep = counts.stream().filter(x -> x.equals(dep)).findFirst().get();

                    node.put("cantidadCerrados", countDep.getCantidadGruposCerrados());
                    node.put("cantidadAbiertos", countDep.getCantidadGruposAbiertos());
                    node.put("cantidadTotal", countDep.getTotalGrupos());
                } else {

                    node.put("cantidadCerrados", 0);
                    node.put("cantidadAbiertos", 0);
                    node.put("cantidadTotal", 0);
                }
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

    @RequestMapping("{departamento}/departamento")
    public String departamento(@PathVariable("departamento") Long idDepartamento, Model model, HttpSession session, RedirectAttributes redirect) {

        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        DepartamentoAcademico depAcademico = service.findDepartamento(idDepartamento);
        List<GrupoSeccion> allGruposSeccion = service.allGrupoSeccionByFilter(ds.getCicloAcademico(), new DepartamentoAcademico(idDepartamento), EstadoEnum.ACT);
        ActaResumen resumen = service.findResumenByDepartamento(ds.getCicloAcademico(), depAcademico);

        model.addAttribute("resumen", resumen);
        model.addAttribute("cicloAcademico", ds.getCicloAcademico());
        model.addAttribute("departamentoAcademico", depAcademico);
        model.addAttribute("gruposSecciones", allGruposSeccion);

        return "academico/acta/actaDepartamento";

    }

    @ResponseBody
    @RequestMapping("listGrupo")
    public DynatableResponse listGrupo(DynatableFilter filter, @RequestParam("departamento") Long idDepartamento, HttpSession session) {

        DynatableResponse json = new DynatableResponse();

        try {

            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
            DepartamentoAcademico dpto = new DepartamentoAcademico(idDepartamento);
            CicloAcademico ciclo = ds.getCicloAcademico();

            List<GrupoSeccion> allGruposSeccion = service.allGrupoSeccionByFilterDyna(ciclo, dpto, filter);
            ArrayNode array = new ArrayNode(JsonNodeFactory.instance);

            for (GrupoSeccion grupo : allGruposSeccion) {
                ObjectNode node = new ObjectNode(JsonNodeFactory.instance);

                node.put("idGrupo", grupo.getId());
                node.put("codigoGrupo", grupo.getCodigo());

                node.put("idCurso", grupo.getCurso().getId());
                node.put("nombreCurso", grupo.getCurso().getNombre());
                node.put("codigo", grupo.getCurso().getCodigo());
                node.put("estructura", grupo.getCurso().getTpc());

                node.put("estado", grupo.getEstado());
                node.put("estadoValue", "");
                if (!StringUtils.isEmpty(grupo.getEstado())) {
                    node.put("estadoValue", EstadoEnum.valueOf(grupo.getEstado()).getValue());
                }
                //    node.put("version", grupo.getVersion());

                node.put("version", grupo.getVersion());
                node.put("estadoPlan", "");
                node.put("estadoPlanValue", "");
                if (grupo.getEstadoPlanEnum() != null) {
                    node.put("estadoPlan", grupo.getEstadoPlanEnum().name());
                    node.put("estadoPlanValue", grupo.getEstadoPlanEnum().getValue());
                }
                node.put("estadoGrupo", "");
                node.put("estadoGrupoValue", "");
                if (grupo.getEstadoGrupoEnum() != null) {
                    node.put("estadoGrupo", grupo.getEstadoGrupoEnum().name());
                    node.put("estadoGrupoValue", grupo.getEstadoGrupoEnum().getValue());
                }

                node.put("estadoGrupoCerrado", grupo.isEstadoGrupoCerrado());
                node.put("estadoPlanAceptado", grupo.isEstadoAceptado());

                Long idSeccion = 0L;
                String secciones = "";
                String grupoHoras = "";
                List<DocenteSeccion> docentesSeccion = null;
                List<Docente> docentesPrincipal = new ArrayList<>();

                for (Seccion sec : grupo.getSecciones()) {

                    if (sec.isTipoSeccionPRA() || sec.isTipoSeccionTCUR() || sec.isTipoSeccionTEO()) {
                        idSeccion = sec.getId();
                        secciones += sec.getId() + "|" + sec.getCodigo() + ",";
                        if (ObjectUtil.getParentTree(sec, "grupoHoras") != null) {
                            grupoHoras += sec.getGrupoHoras().getId() + "|" + sec.getGrupoHoras().getCodigo() + ",";
                        }

//                        docentesSeccion = service.allDocenteSeccionByFilter(null, sec);
                        docentesSeccion = sec.getDocenteSeccion();
                        for (DocenteSeccion docentesSeccionEach : docentesSeccion) {
                            if (docentesSeccionEach.getEstadoEnum().equals(EstadoEnum.ACT)) {
                                if (docentesSeccionEach.esDocentePrincipal()) {
                                    docentesPrincipal.add(docentesSeccionEach.getDocente());
                                }
                            }
                        }

                    }

                }
                node.put("seccion", idSeccion);
                node.put("secciones", "");

                if (!StringUtils.isEmpty(secciones)) {
                    node.put("secciones", secciones.substring(0, secciones.length() - 1));
                }
                if (grupoHoras != "") {
                    grupoHoras = grupoHoras.substring(0, grupoHoras.length() - 1);
                }
                node.put("grupoHoras", grupoHoras);

                node.put("docenteNombre", "");
                //    node.put("idDocente", "");
                if (!docentesPrincipal.isEmpty()) {
                    String docentes = "";
                    for (Docente doc : docentesPrincipal) {
                        docentes += doc.getPersona().getApellidosNombres() + " - ";
                    }
                    if (!StringUtils.isEmpty(docentes)) {
                        docentes = docentes.substring(0, docentes.length() - 3);
                    }
                    node.put("docenteNombre", docentes);
                    //    node.put("idDocente", docentePrincipal.getId());
                }
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
    @RequestMapping("reabrir")
    public JsonResponse reabrir(@RequestParam("grupo") Long idGrupo, HttpSession session) {
        JsonResponse response = new JsonResponse();
        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
            logger.debug("El grupo seleccionado es {}", idGrupo);

            service.reabrirGrupo(new GrupoSeccion(idGrupo), ds.getUsuario());

            response.setMessage("Registro reabierto");
            response.setSuccess(true);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (RuntimeException e) {
            ExceptionHandler.handleSpecial(e, response, Messages.FK_ERROR);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @RequestMapping("exportExcel/recordActas")
    public ModelAndView recordActas(HttpSession session, Model model, RedirectAttributes redirectAttr) {
        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);

            List<GrupoSeccion> gpoSecciones = service.allGrupoSeccionByCiclo(ds.getCicloAcademico());
            model.addAttribute("gruposSecciones", gpoSecciones);
            model.addAttribute("cantidadAlumnosByGrupo", service.mapCantidadAlumnoByGrupo(gpoSecciones));
            model.addAttribute("cantidadAlumnosByGrupoNF", service.mapCantidadAlumnoByGrupoNF(gpoSecciones));
            model.addAttribute(RecordDeActasExcelView.TIPO, RecordDeActasExcelView.PRE_GRADO);

        } catch (PhobosException e) {
            e.printStackTrace();
            return new ModelAndView("redirect:/");

        } catch (Exception e) {
            e.printStackTrace();
            return new ModelAndView("redirect:/");

        }

        return new ModelAndView(recordDeActasExcelView);
    }

    @RequestMapping("exportExcel/raPostGrado")
    public ModelAndView postGrado(HttpSession session, Model model, RedirectAttributes redirectAttr) {
        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);

            List<GrupoSeccion> gpoSecciones = service.allGrupoSeccionByCiclo(ds.getCicloAcademico());
            model.addAttribute("gruposSecciones", gpoSecciones);
            model.addAttribute("cantidadAlumnosByGrupo", service.mapCantidadAlumnoByGrupo(gpoSecciones));
            model.addAttribute("cantidadAlumnosByGrupoNF", service.mapCantidadAlumnoByGrupoNF(gpoSecciones));
            model.addAttribute(RecordDeActasExcelView.TIPO, RecordDeActasExcelView.POST_GRADO);

        } catch (PhobosException e) {
            e.printStackTrace();
            return new ModelAndView("redirect:/");

        } catch (Exception e) {
            e.printStackTrace();
            return new ModelAndView("redirect:/");

        }

        return new ModelAndView(recordDeActasExcelView);
    }

}
