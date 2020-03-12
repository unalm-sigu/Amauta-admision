package pe.edu.lamolina.pivot.controller.academico.plancurricular;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.beans.PropertyEditorSupport;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.apache.commons.lang3.RandomStringUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableResponse;
import pe.albatross.zelpers.miscelanea.ExceptionHandler;
import pe.albatross.zelpers.miscelanea.JsonHelper;
import pe.albatross.zelpers.miscelanea.JsonResponse;
import pe.albatross.zelpers.miscelanea.NumberFormat;
import pe.albatross.zelpers.miscelanea.ObjectUtil;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.albatross.zelpers.notify.Notificaciones;
import pe.edu.lamolina.model.academico.Carrera;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Curso;
import pe.edu.lamolina.model.academico.CursoAdicionalCurricula;
import pe.edu.lamolina.model.academico.CursoCurricula;
import pe.edu.lamolina.model.academico.CursoEquivalente;
import pe.edu.lamolina.model.academico.CursoEquivalenteElectivo;
import pe.edu.lamolina.model.academico.CursoOpcionalCurricula;
import pe.edu.lamolina.model.academico.ModalidadEstudio;
import pe.edu.lamolina.model.academico.OrientacionCarrera;
import pe.edu.lamolina.model.academico.PlanCurricular;
import pe.edu.lamolina.model.academico.RequisitoCursoCurricula;
import pe.edu.lamolina.model.academico.RequisitoCursoOpcional;
import pe.edu.lamolina.model.academico.ResumenPlanCurricular;
import pe.edu.lamolina.model.academico.TipoCursoCurricula;
import pe.edu.lamolina.model.enums.EstadoEnum;
import pe.edu.lamolina.model.enums.TipoCurriculaEnum;
import static pe.edu.lamolina.model.enums.TipoCursoCurriculaEnum.CULT;
import static pe.edu.lamolina.model.enums.TipoCursoCurriculaEnum.DEP;
import static pe.edu.lamolina.model.enums.TipoCursoCurriculaEnum.ELC;
import static pe.edu.lamolina.model.enums.TipoCursoCurriculaEnum.ELE;
import static pe.edu.lamolina.model.enums.TipoCursoCurriculaEnum.GEN;
import static pe.edu.lamolina.model.enums.TipoCursoCurriculaEnum.OBL;
import static pe.edu.lamolina.model.enums.TipoCursoCurriculaEnum.PROD;
import static pe.edu.lamolina.model.enums.TipoCursoCurriculaEnum.TECIND;
import pe.edu.lamolina.model.enums.TipoOficinaEnum;
import pe.edu.lamolina.pivot.config.DespliegueConfig;
import pe.edu.lamolina.pivot.controller.seguridad.verificador.VerificadorService;
import pe.edu.lamolina.pivot.controller.seguridad.verificador.VerificadorServiceImp;
import pe.edu.lamolina.pivot.zelper.constant.Constantine;
import pe.edu.lamolina.pivot.zelper.constant.Messages;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

@Controller
@RequestMapping("academico/planCurricular")
public class PlanCurricularController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    PlanCurricularService service;

    @Autowired
    VerificadorService verificadorService;

    @Autowired
    VisorAsignaCurricula visorAsignaCurricula;

    @Autowired
    DespliegueConfig despliegueConfig;

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
    public String index(Model model, HttpSession session, HttpServletRequest request) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        List<Carrera> carreras = service.filtrarByPlanes(verificadorService.allInstanciasByMenuRol(TipoOficinaEnum.ESP, request, ds));
        ArrayNode carrerasJson = this.createCarrerasJson(carreras);
        model.addAttribute("ambiente", despliegueConfig.getAmbiente());
        model.addAttribute("carrerasJson", carrerasJson.toString());
        model.addAttribute("editor", verificadorService.isEditorCurriculas(ds));
        model.addAttribute("editorAll", verificadorService.isEditorCurriculasAll(ds));
        model.addAttribute("editorEpg", verificadorService.isEditorCurriculasEpg(ds));
        model.addAttribute("revisor", verificadorService.isRevisorCurriculas(ds));
        return "academico/plancurricular/planCurricular";
    }

    @ResponseBody
    @RequestMapping("list")
    public DynatableResponse list(DynatableFilter filter, HttpSession session, HttpServletRequest request) {
        DynatableResponse json = new DynatableResponse();

        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
            List<Carrera> carreras = new ArrayList();
            List<PlanCurricular> curriculas = new ArrayList();
            VerificadorServiceImp.CantidadItemsEnum cantidadEnum = verificadorService.verificarCantidad(TipoOficinaEnum.ESP, request, ds);
            boolean tienePermiso = verificadorService.isRevisorCurriculas(ds);
            logger.info("Acceso alumnos {}", cantidadEnum.name());
            if (tienePermiso && cantidadEnum == VerificadorServiceImp.CantidadItemsEnum.PARCIAL) {
                carreras = verificadorService.allInstanciasByMenuRol(TipoOficinaEnum.ESP, request, ds);
                logger.info("Acceso a {} carreras", carreras.size());
            }
            if (tienePermiso && cantidadEnum != VerificadorServiceImp.CantidadItemsEnum.SIN_PERMISO) {
                curriculas = service.allByDynatable(filter, carreras);
                logger.info("Se extrajeron {} curriculas", curriculas.size());
            }

            //List<Carrera> carreras = verificadorService.allInstanciasByMenuRol(TipoOficinaEnum.ESP, request, ds);
            //List<PlanCurricular> curriculas = service.allByDynatable(filter, carreras);
            ArrayNode array = new ArrayNode(JsonNodeFactory.instance);
            for (PlanCurricular curricula : curriculas) {
                ObjectNode node = new ObjectNode(JsonNodeFactory.instance);

                node.put("id", curricula.getId());
                node.put("cicloInicioVigencia", curricula.getCicloInicioVigencia().getDescripcion());
                node.put("estado", curricula.getEstado());
                node.put("fechaAprobacion", getFechaString(curricula.getFechaAprobado()));
                node.put("codigoCarrera", curricula.getCarrera().getCodigo());
                node.put("carrera", curricula.getCarrera().getNombre());
                node.put("tipoCarrera", curricula.getCarrera().getTipoEnum().getValue());
                node.put("orientacion", curricula.getOrientacionCarrera() == null ? null : curricula.getOrientacionCarrera().getNombre());
                node.put("facultad", curricula.getCarrera().getFacultad().getNombre());
                node.put("modalidad", curricula.getCarrera().getModalidadEstudio().getNombre());
                node.put("codigoModalidad", curricula.getCarrera().getModalidadEstudio().getCodigo());
                node.put("estado", curricula.getEstado());
                node.put("estadoEnum", curricula.getEstadoEnum().getValue());
                node.put("ciclos", curricula.getCiclos());

                node.put("cantObl", curricula.getCantidadCursosCurricula());
                node.put("cantOpc", curricula.getCantidadCursosOpcionales());
                node.put("cantAdc", curricula.getCantidadCursosAdicionales());
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

    private String getFechaString(Date fecha) {
        if (fecha == null) {
            return null;
        } else {
            return new DateTime(fecha).toString("dd/MM/yyyy");
        }
    }

    @ResponseBody
    @RequestMapping("cursosObligatorios")
    public DynatableResponse cursosObligatorios(DynatableFilter filter, HttpSession session) {
        DynatableResponse json = new DynatableResponse();

        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
            List<CursoCurricula> cursosCurricula = service.allCursosOblByDynatable(filter);

            Integer total = 0;
            ArrayNode array = new ArrayNode(JsonNodeFactory.instance);
            for (CursoCurricula cursoCurricula : cursosCurricula) {
                ObjectNode node = new ObjectNode(JsonNodeFactory.instance);
                System.out.println("idCurso :::: " + cursoCurricula.getCurso().getId());
                Integer esRequisitoDe = cursoCurricula.getRequisitosCursoCurricula().size()
                        + cursoCurricula.getRequisitosCursoOpcional().size();
                node.put("id", cursoCurricula.getId());
                node.put("tipoCurso", cursoCurricula.getTipoCursoCurricula().getNombre());
                node.put("curso", cursoCurricula.getCurso().getNombre());
                node.put("tpc", cursoCurricula.getCurso().getTpc());
                node.put("tipoDictadoCurso", cursoCurricula.getCurso().getTipoCursoEnum().getValue());
                node.put("numeroCiclo", cursoCurricula.getNumeroCiclo());
                node.put("numeroRomano", NumberFormat.roman(cursoCurricula.getNumeroCiclo()));
                node.put("codigo", cursoCurricula.getCurso().getCodigo());
                node.put("codigo2", cursoCurricula.getCurso().getCodigoAnterior1());
                node.put("creditos", cursoCurricula.getCreditos());
                node.put("creditosRequisito", cursoCurricula.getCreditosRequisito());
                node.put("cursosRequisito", cursoCurricula.getCursosCurricula().size());
                node.put("esRequisitoDe", esRequisitoDe);
                node.put("requisitosOr", cursoCurricula.getRequisitosOr());
                node.put("estado", cursoCurricula.getEstado());
                node.put("strEstado", cursoCurricula.getEstadoEnum().getValue());

                ArrayNode arrayPreRequisitos = new ArrayNode(JsonNodeFactory.instance);
                List<RequisitoCursoCurricula> cursosRequisitos = cursoCurricula.getCursosCurricula();
                for (RequisitoCursoCurricula requisito : cursosRequisitos) {
                    ObjectNode nodeRequisito = new ObjectNode(JsonNodeFactory.instance);
                    nodeRequisito.put("curso", requisito.getCursoRequisito().getCurso().getNombre());
                    nodeRequisito.put("codigo", requisito.getCursoRequisito().getCurso().getCodigo());
                    nodeRequisito.put("codigo2", requisito.getCursoRequisito().getCurso().getCodigoAnterior1());
                    nodeRequisito.put("simultaneo", requisito.getSimultaneo());
                    nodeRequisito.put("tipoCurso", requisito.getCursoRequisito().getTipoCursoCurricula().getNombre());
                    nodeRequisito.put("numeroRomano", NumberFormat.roman(requisito.getCursoRequisito().getNumeroCiclo()));
                    nodeRequisito.put("tpc", requisito.getCursoRequisito().getCurso().getTpc());
                    nodeRequisito.put("tipoDictadoCurso", requisito.getCursoRequisito().getCurso().getTipoCursoEnum().getValue());

                    arrayPreRequisitos.add(nodeRequisito);
                }
                node.set("prerrequisitos", arrayPreRequisitos);

                ArrayNode arrayPostRequisitos = new ArrayNode(JsonNodeFactory.instance);
                List<RequisitoCursoCurricula> cursosPostRequisitos = cursoCurricula.getRequisitosCursoCurricula();
                for (RequisitoCursoCurricula postrequisito : cursosPostRequisitos) {
                    ObjectNode nodePostRequisito = new ObjectNode(JsonNodeFactory.instance);
                    nodePostRequisito.put("curso", postrequisito.getCursoCurricula().getCurso().getNombre());
                    nodePostRequisito.put("codigo", postrequisito.getCursoCurricula().getCurso().getCodigo());
                    nodePostRequisito.put("codigo2", postrequisito.getCursoCurricula().getCurso().getCodigoAnterior1());
                    nodePostRequisito.put("simultaneo", postrequisito.getSimultaneo());
                    nodePostRequisito.put("tipoCurso", postrequisito.getCursoCurricula().getTipoCursoCurricula().getNombre());
                    nodePostRequisito.put("numeroRomano", NumberFormat.roman(postrequisito.getCursoCurricula().getNumeroCiclo()));
                    nodePostRequisito.put("tpc", postrequisito.getCursoCurricula().getCurso().getTpc());
                    nodePostRequisito.put("tipoDictadoCurso", postrequisito.getCursoCurricula().getCurso().getTipoCursoEnum().getValue());

                    arrayPostRequisitos.add(nodePostRequisito);
                }
                node.set("postrrequisitos", arrayPostRequisitos);

                ArrayNode arrayPostRequisitosOpc = new ArrayNode(JsonNodeFactory.instance);
                List<RequisitoCursoOpcional> cursosPostRequisitosOpc = cursoCurricula.getRequisitosCursoOpcional();
                for (RequisitoCursoOpcional postrequisito : cursosPostRequisitosOpc) {
                    ObjectNode nodePostRequisito = new ObjectNode(JsonNodeFactory.instance);
                    nodePostRequisito.put("curso", postrequisito.getCursoOpcional().getCurso().getNombre());
                    nodePostRequisito.put("codigo", postrequisito.getCursoOpcional().getCurso().getCodigo());
                    nodePostRequisito.put("codigo2", postrequisito.getCursoOpcional().getCurso().getCodigoAnterior1());
                    nodePostRequisito.put("simultaneo", postrequisito.getSimultaneo());
                    nodePostRequisito.put("tipoCurso", postrequisito.getCursoOpcional().getTipoCursoCurricula().getNombre());
                    nodePostRequisito.put("tpc", postrequisito.getCursoOpcional().getCurso().getTpc());
                    nodePostRequisito.put("tipoDictadoCurso", postrequisito.getCursoOpcional().getCurso().getTipoCursoEnum().getValue());

                    arrayPostRequisitosOpc.add(nodePostRequisito);
                }
                node.set("postrrequisitosOpc", arrayPostRequisitosOpc);

                ArrayNode arrayGruposEquivalentes = new ArrayNode(JsonNodeFactory.instance);
                List<CursoEquivalente> cursosEquivalentes = cursoCurricula.getCursosEquivalentes();
                HashMap<Integer, ArrayNode> grupos = new HashMap<>();

                for (CursoEquivalente cursoEquivalente : cursosEquivalentes) {
                    Integer grupo = cursoEquivalente.getGrupo();

                    ObjectNode nodeEquivalente = new ObjectNode(JsonNodeFactory.instance);
                    nodeEquivalente.put("curso", cursoEquivalente.getCursoEquivalente().getNombre());
                    nodeEquivalente.put("codigo", cursoEquivalente.getCursoEquivalente().getCodigo());
                    nodeEquivalente.put("tpc", cursoEquivalente.getCursoEquivalente().getTpc());
                    nodeEquivalente.put("tipoDictadoCurso", cursoEquivalente.getCursoEquivalente().getTipoCursoEnum().getValue());
                    if (!grupos.containsKey(grupo)) {
                        grupos.put(cursoEquivalente.getGrupo(), new ArrayNode(JsonNodeFactory.instance));
                    }
                    grupos.get(grupo).add(nodeEquivalente);
                }
                for (Map.Entry<Integer, ArrayNode> entry : grupos.entrySet()) {
                    Integer numeroGrupo = entry.getKey();
                    ArrayNode arrCursosEquivalantes = entry.getValue();

                    ObjectNode nodeGrupoEquivalente = new ObjectNode(JsonNodeFactory.instance);

                    nodeGrupoEquivalente.put("numeroGrupo", numeroGrupo);
                    nodeGrupoEquivalente.set("arrCursosEquivalantes", arrCursosEquivalantes);
                    arrayGruposEquivalentes.add(nodeGrupoEquivalente);
                }
                node.put("numEquivalentes", arrayGruposEquivalentes.size());
                node.set("arrayGruposEquivalentes", arrayGruposEquivalentes);

                array.add(node);
                total += cursoCurricula.getCreditos();
            }
            {
                ObjectNode node = new ObjectNode(JsonNodeFactory.instance);
                node.put("curso", "TOTAL");
                node.put("creditos", total);
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
    @RequestMapping("resumenCurricula")
    public DynatableResponse resumenCurricula(DynatableFilter filter, HttpSession session) {
        DynatableResponse json = new DynatableResponse();
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);

        try {
            List<ResumenPlanCurricular> resumenes = service.allResPlanCurByDynatable(filter);

            Integer totalCreditos = 0, totalMinimo = 0, totalCursos = 0;
            ArrayNode array = new ArrayNode(JsonNodeFactory.instance);
            for (ResumenPlanCurricular resumen : resumenes) {
                ObjectNode node = new ObjectNode(JsonNodeFactory.instance);
                node.put("id", resumen.getId());
                node.put("tipoCurso", resumen.getTipoCursoCurricula().getNombre());
                node.put("tipoCursoCodigo", resumen.getTipoCursoCurricula().getCodigo());
                node.put("creditos", resumen.getCreditos());
                node.put("minimoCreditos", resumen.getMinimoCreditos());
                node.put("cursos", resumen.getCursos());

                array.add(node);
                if (Arrays.asList(GEN, OBL, ELC, DEP).contains(resumen.getTipoCursoCurricula().getCodigoEnum())) {

                    totalCreditos += resumen.getCreditos();
                    totalCursos += resumen.getCursos();
                    totalMinimo += resumen.getMinimoCreditos();
                }
                if (Arrays.asList(PROD, CULT, TECIND, ELE).contains(resumen.getTipoCursoCurricula().getCodigoEnum())) {
                    totalMinimo += resumen.getMinimoCreditos();
                }
            }

            {
                ObjectNode node = new ObjectNode(JsonNodeFactory.instance);
                node.put("tipoCurso", "TOTAL");
                node.put("creditos", totalCreditos);
                node.put("minimoCreditos", totalMinimo);
                node.put("cursos", totalCursos);
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

    @ResponseBody
    @RequestMapping("cursosAdicionales")
    public DynatableResponse cursosAdicionales(DynatableFilter filter, HttpSession session) {
        DynatableResponse json = new DynatableResponse();

        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
            List<CursoAdicionalCurricula> cursosAdicionales = service.allCursosAdcByDynatable(filter);

            ArrayNode array = new ArrayNode(JsonNodeFactory.instance);

            for (CursoAdicionalCurricula cursoAdicional : cursosAdicionales) {
                ObjectNode node = new ObjectNode(JsonNodeFactory.instance);
                node.put("id", cursoAdicional.getId());
                node.put("codigo", cursoAdicional.getCurso().getCodigo());
                node.put("codigo2", cursoAdicional.getCurso().getCodigoAnterior1());
                node.put("curso", cursoAdicional.getCurso().getNombre());
                node.put("creditos", cursoAdicional.getCurso().getCreditos());
                node.put("cicloInicio", (String) ObjectUtil.getParentTree(cursoAdicional, "cicloInicio.descripcion"));
                node.put("cicloFin", (String) ObjectUtil.getParentTree(cursoAdicional, "cicloFin.descripcion"));
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
    @RequestMapping("cursosElectivos")
    public DynatableResponse cursosElectivos(DynatableFilter filter, HttpSession session) {
        DynatableResponse json = new DynatableResponse();

        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
            List<CursoOpcionalCurricula> cursosOpcionales = service.allCursosElecByDynatable(filter);

            ArrayNode array = new ArrayNode(JsonNodeFactory.instance);
            for (CursoOpcionalCurricula cursoOpcional : cursosOpcionales) {
                ObjectNode node = new ObjectNode(JsonNodeFactory.instance);
                node.put("id", cursoOpcional.getId());
                node.put("codigo", cursoOpcional.getCurso().getCodigo());
                node.put("codigo2", cursoOpcional.getCurso().getCodigoAnterior1());
                node.put("curso", cursoOpcional.getCurso().getNombre());
                node.put("creditos", cursoOpcional.getCurso().getCreditos());
                node.put("tpc", cursoOpcional.getCurso().getTpc());
                node.put("tipoDictadoCurso", cursoOpcional.getCurso().getTipoCursoEnum().getValue());
                node.put("tipoCurso", cursoOpcional.getTipoCursoCurricula().getNombre());
                node.put("tipoCursoCodigo", cursoOpcional.getTipoCursoCurricula().getCodigo());
                node.put("creditosRequisito", cursoOpcional.getCreditosRequisito());
                node.put("cursosRequisito", cursoOpcional.getCursosOpcionales().size());
                node.put("esRequisitoDe", cursoOpcional.getRequisitosCursoOpcionales().size());
                node.put("requisitosOr", cursoOpcional.getRequisitosOr());

                ArrayNode arrayPreRequisitos = new ArrayNode(JsonNodeFactory.instance);
                List<RequisitoCursoOpcional> cursosRequisitos = cursoOpcional.getCursosOpcionales();
                for (RequisitoCursoOpcional requisito : cursosRequisitos) {
                    ObjectNode nodeRequisito = new ObjectNode(JsonNodeFactory.instance);
                    nodeRequisito.put("curso", requisito.getCursoRequisito().getNombre());
                    nodeRequisito.put("codigo", requisito.getCursoRequisito().getCodigo());
                    nodeRequisito.put("codigo2", requisito.getCursoRequisito().getCodigoAnterior1());
                    nodeRequisito.put("simultaneo", requisito.getSimultaneo());
                    nodeRequisito.put("tipoCurso", requisito.getTipoCursoCurricula().getNombre());
                    nodeRequisito.put("numeroRomano", requisito.getNumeroRomano());
                    nodeRequisito.put("tpc", requisito.getCursoRequisito().getTpc());
                    nodeRequisito.put("tipoDictadoCurso", requisito.getCursoRequisito().getTipoCursoEnum().getValue());

                    arrayPreRequisitos.add(nodeRequisito);
                }
                node.set("prerrequisitos", arrayPreRequisitos);

                ArrayNode arrayPostRequisitos = new ArrayNode(JsonNodeFactory.instance);
                List<RequisitoCursoOpcional> cursosPostRequisitos = cursoOpcional.getRequisitosCursoOpcionales();
                for (RequisitoCursoOpcional postrequisito : cursosPostRequisitos) {
                    ObjectNode nodePostRequisito = new ObjectNode(JsonNodeFactory.instance);
                    nodePostRequisito.put("curso", postrequisito.getCursoOpcional().getCurso().getNombre());
                    nodePostRequisito.put("codigo", postrequisito.getCursoOpcional().getCurso().getCodigo());
                    nodePostRequisito.put("codigo2", postrequisito.getCursoOpcional().getCurso().getCodigoAnterior1());
                    nodePostRequisito.put("simultaneo", postrequisito.getSimultaneo());
                    nodePostRequisito.put("tipoCurso", postrequisito.getCursoOpcional().getTipoCursoCurricula().getNombre());
                    nodePostRequisito.put("tpc", postrequisito.getCursoOpcional().getCurso().getTpc());
                    nodePostRequisito.put("tipoDictadoCurso", postrequisito.getCursoOpcional().getCurso().getTipoCursoEnum().getValue());

                    arrayPostRequisitos.add(nodePostRequisito);
                }
                node.set("postrrequisitos", arrayPostRequisitos);

                ArrayNode arrayGruposEquivalentesElectivos = new ArrayNode(JsonNodeFactory.instance);
                List<CursoEquivalenteElectivo> cursosEquivalentesElectivos = cursoOpcional.getCursoEquivalenteElectivo();
                HashMap<Integer, ArrayNode> grupos = new HashMap<>();

                for (CursoEquivalenteElectivo cursoEquivalente : cursosEquivalentesElectivos) {
                    Integer grupo = cursoEquivalente.getGrupo();

                    ObjectNode nodeEquivalente = new ObjectNode(JsonNodeFactory.instance);
                    nodeEquivalente.put("curso", cursoEquivalente.getCursoEquivalente().getNombre());
                    nodeEquivalente.put("codigo", cursoEquivalente.getCursoEquivalente().getCodigo());
                    nodeEquivalente.put("tpc", cursoEquivalente.getCursoEquivalente().getTpc());
                    nodeEquivalente.put("tipoDictadoCurso", cursoEquivalente.getCursoEquivalente().getTipoCursoEnum().getValue());
                    if (!grupos.containsKey(grupo)) {
                        grupos.put(cursoEquivalente.getGrupo(), new ArrayNode(JsonNodeFactory.instance));
                    }
                    grupos.get(grupo).add(nodeEquivalente);
                }
                for (Map.Entry<Integer, ArrayNode> entry : grupos.entrySet()) {
                    Integer numeroGrupo = entry.getKey();
                    ArrayNode arrCursosEquivalantes = entry.getValue();

                    ObjectNode nodeGrupoEquivalente = new ObjectNode(JsonNodeFactory.instance);

                    nodeGrupoEquivalente.put("numeroGrupo", numeroGrupo);
                    nodeGrupoEquivalente.set("arrCursosEquivalantes", arrCursosEquivalantes);
                    arrayGruposEquivalentesElectivos.add(nodeGrupoEquivalente);
                }
                node.put("numEquivalentes", arrayGruposEquivalentesElectivos.size());
                node.set("arrayGruposEquivalentesElectivos", arrayGruposEquivalentesElectivos);

                ArrayNode arrayPostRequisitosOpc = new ArrayNode(JsonNodeFactory.instance);
                node.set("postrrequisitosOpc", arrayPostRequisitosOpc);

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

    @RequestMapping("nuevo")
    public String nuevo(@RequestParam("origen") String origen, Model model, HttpSession session, HttpServletRequest request) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);

        PlanCurricular planCurricular = new PlanCurricular();
        planCurricular.init();

        List<CicloAcademico> ciclos = service.allUltimosCiclos(40);
//        List<Carrera> carreras = service.allCarreras(ds.getCarreras());
        List<Carrera> carreras = service.allCarreras(ds, request);

        model.addAttribute("ciclos", ciclos);
        model.addAttribute("planCurricular", planCurricular);
        model.addAttribute("carreras", carreras);
        model.addAttribute("editor", verificadorService.isEditorCurriculas(ds));
        model.addAttribute("editorAll", verificadorService.isEditorCurriculasAll(ds));
        model.addAttribute("editorEpg", verificadorService.isEditorCurriculasEpg(ds));
        model.addAttribute("origen", verificadorService.getOrigen(origen, "/academico/planCurricular"));
        model.addAttribute("origenBase", origen);

        return "academico/plancurricular/planCurricularForm";
    }

    @RequestMapping("{plancurricular}/addCursoObligatorio")
    public String addCursoObligatorio(@PathVariable("plancurricular") Long plancurricularId, Model model, HttpSession session) {

        PlanCurricular planCurricular = service.findPlanCurricularById(new PlanCurricular(plancurricularId));
        List<TipoCursoCurricula> tiposCursoCurriculas = service.allTiposCursoCurriculaByPlan(planCurricular);
        CursoCurricula cursoCurricula = new CursoCurricula();
        cursoCurricula.setTipoCursoCurricula(new TipoCursoCurricula());
        cursoCurricula.setCursosCurricula(new ArrayList());
        cursoCurricula.setPlanCurricular(planCurricular);
        cursoCurricula.setCreditosRequisito(0);

        model.addAttribute("modalidad", planCurricular.getCarrera().getModalidadEstudio());
        model.addAttribute("cursoCurricula", cursoCurricula);
        model.addAttribute("tiposCursoCurriculas", tiposCursoCurriculas);
        model.addAttribute("format", new NumberFormat());

        return "academico/plancurricular/agregarCursoObli";
    }

    @RequestMapping("{cursoCurricula}/editarCursoObligatorio")
    public String editarCursoObligatorio(@PathVariable("cursoCurricula") Long cursoCurriculaId, Model model, HttpSession session) {
        CursoCurricula cursoCurricula = service.findCursoCurricula(cursoCurriculaId);

        model.addAttribute("cursoCurricula", cursoCurricula);
        model.addAttribute("format", new NumberFormat());
        return "academico/plancurricular/agregarCursoObli";
    }

    @RequestMapping("{cursoCurricula}/editarCursosEquivalentes")
    public String editarCursosEquivalentes(@PathVariable("cursoCurricula") Long cursoCurriculaId, Model model, HttpSession session) {
        CursoCurricula cursoCurricula = service.findCursoCurricula(cursoCurriculaId);
        HashMap<Integer, ArrayList<CursoEquivalente>> mapGrupos = new HashMap<>();
        List<CursoEquivalente> equivalentes = cursoCurricula.getCursosEquivalentes();
        List<GrupoCursoEquivalente> grupos = new ArrayList<>();
        for (CursoEquivalente equivalente : equivalentes) {
            if (!mapGrupos.containsKey(equivalente.getGrupo())) {
                mapGrupos.put(equivalente.getGrupo(), new ArrayList<>());
            }
            mapGrupos.get(equivalente.getGrupo()).add(equivalente);
        }
        for (Map.Entry<Integer, ArrayList<CursoEquivalente>> entry : mapGrupos.entrySet()) {
            GrupoCursoEquivalente grupo = new GrupoCursoEquivalente();
            grupo.setCursoEquivalente(entry.getValue());
            grupo.setNumeroGrupo(entry.getKey());
            grupos.add(grupo);
        }
        model.addAttribute("gruposEquivalentes", grupos);
        model.addAttribute("cursoCurricula", cursoCurricula);
        model.addAttribute("format", new NumberFormat());

        return "academico/plancurricular/agregarCursoEqui";
    }

    @RequestMapping("{cursoOpcionalCurricula}/editarCursosEquivalentesElectivos")
    public String editarCursosEquivalentesElectivos(@PathVariable("cursoOpcionalCurricula") Long cursoOpcionalCurriculaId, Model model, HttpSession session) {
        CursoOpcionalCurricula cursoOpcionalCurricula = service.findCursoOpcionalCurricula(cursoOpcionalCurriculaId);
        HashMap<Integer, ArrayList<CursoEquivalenteElectivo>> mapGrupos = new HashMap<>();
        List<CursoEquivalenteElectivo> equivalentes = cursoOpcionalCurricula.getCursoEquivalenteElectivo();
        List<GrupoCursoEquivalenteElectivo> grupos = new ArrayList<>();
        for (CursoEquivalenteElectivo equivalente : equivalentes) {
            if (!mapGrupos.containsKey(equivalente.getGrupo())) {
                mapGrupos.put(equivalente.getGrupo(), new ArrayList<>());
            }
            mapGrupos.get(equivalente.getGrupo()).add(equivalente);
        }
        for (Map.Entry<Integer, ArrayList<CursoEquivalenteElectivo>> entry : mapGrupos.entrySet()) {
            GrupoCursoEquivalenteElectivo grupo = new GrupoCursoEquivalenteElectivo();
            grupo.setCursoEquivalenteElectivo(entry.getValue());
            grupo.setNumeroGrupo(entry.getKey());
            grupos.add(grupo);
        }

        model.addAttribute("gruposEquivalentes", grupos);
        model.addAttribute("cursoOpcionalCurricula", cursoOpcionalCurricula);
        model.addAttribute("format", new NumberFormat());

        return "academico/plancurricular/agregarCursoEquiElec";
    }

    @RequestMapping("{plancurricular}/agregarCursoElectivo")
    public String agregarCursoElectivo(@PathVariable("plancurricular") Long plancurricularId, Model model, HttpSession session) {
        PlanCurricular planCurricular = service.findPlanCurricularById(new PlanCurricular(plancurricularId));
        List<TipoCursoCurricula> tiposCursoCurriculas = service.allTiposCursoCurriculasElectivosByPlan(planCurricular);

        CursoOpcionalCurricula cursoOpcional = new CursoOpcionalCurricula();
        cursoOpcional.setCreditosRequisito(0);
        cursoOpcional.setPlanCurricular(planCurricular);
        cursoOpcional.setCursosOpcionales(new ArrayList());

        model.addAttribute("cursoOpcional", cursoOpcional);
        model.addAttribute("planCurricular", planCurricular);
        model.addAttribute("tiposCursoCurriculas", tiposCursoCurriculas);
        return "academico/plancurricular/agregarCursoElec";
    }

    @RequestMapping("{cursoElectivo}/editarCursoElectivo")
    public String editarCursoElectivo(@PathVariable("cursoElectivo") Long cursoElectivoId, Model model, HttpSession session) {
        CursoOpcionalCurricula cursoElectivo = service.findCursoElectivo(cursoElectivoId);

        model.addAttribute("cursoOpcional", cursoElectivo);
        model.addAttribute("planCurricular", cursoElectivo.getPlanCurricular());
        model.addAttribute("format", new NumberFormat());
        return "academico/plancurricular/agregarCursoElec";
    }

    @RequestMapping("{cursoAdicional}/editarCursoAdicional")
    public String editarCursoAdicional(@PathVariable("cursoAdicional") Long cursoAdicionalId, Model model, HttpSession session) {
        CursoAdicionalCurricula cursoAdicional = service.findCursoAdicional(cursoAdicionalId);

        model.addAttribute("cursoAdicional", cursoAdicional);
        model.addAttribute("planCurricular", cursoAdicional.getPlanCurricular());
        model.addAttribute("format", new NumberFormat());
        model.addAttribute("edita", true);
        return "academico/plancurricular/agregarCursoAdc";
    }

    @RequestMapping("{plancurricular}/agregarCursoAdicional")
    public String agregarCursoAdicional(@PathVariable("plancurricular") Long plancurricularId, Model model, HttpSession session) {
        PlanCurricular planCurricular = service.findPlanCurricularById(new PlanCurricular(plancurricularId));
        model.addAttribute("cursoAdicional", new CursoAdicionalCurricula());
        model.addAttribute("planCurricular", planCurricular);
        model.addAttribute("edita", false);

        return "academico/plancurricular/agregarCursoAdc";
    }

    @ResponseBody
    @RequestMapping("{carrera}/orientacionCarrera")
    public JsonResponse orientacionCarrera(@PathVariable("carrera") Long idCarrera, HttpSession session) {
        JsonResponse response = new JsonResponse();
        try {
            ObjectNode node = new ObjectNode(JsonNodeFactory.instance);
            Carrera carrera = service.findCarrera(new Carrera(idCarrera));
            ModalidadEstudio modalidad = carrera.getModalidadEstudio();
            List<OrientacionCarrera> orientaciones = service.allOrientacionByCarreraEstado(carrera, EstadoEnum.ACT);
            List<CicloAcademico> ciclos = service.allUltimosCiclosByModalidad(modalidad, 20);

            String templateOrienta = "<option value=\"%s\">%s</option>";
            String templateCiclos = "<option value=\"%s\">%s</option>";
            StringBuilder optionsOrienta = new StringBuilder();
            StringBuilder optionsCiclos = new StringBuilder();

            optionsOrienta.append(String.format(templateOrienta, "", ""));
            for (OrientacionCarrera orientacion : orientaciones) {
                optionsOrienta.append(String.format(templateOrienta, orientacion.getId().toString(), orientacion.getNombre()));
            }
            for (CicloAcademico ciclo : ciclos) {
                optionsCiclos.append(String.format(templateCiclos, ciclo.getId().toString(), ciclo.getDescripcion() + " - " + ciclo.getModalidadEstudio().getNombre()));
            }

            node.put("orientaciones", optionsOrienta.toString());
            node.put("ciclos", optionsCiclos.toString());
            node.put("cantidad", carrera.getCantidadCiclos());

            response.setData(node);
            response.setSuccess(Boolean.TRUE);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (RuntimeException e) {
            ExceptionHandler.handleSpecial(e, response, Messages.FK_ERROR);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("{tipoCursoCurricula}/cambiarTipoCursoCurricula")
    public JsonResponse cambiarTipoCursoCurricula(@PathVariable("tipoCursoCurricula") Long tipoCursoCurriculaId, HttpSession session) {

        JsonResponse response = new JsonResponse();
        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
            TipoCursoCurricula tipoCursoCurricula = service.findTipoCurricula(tipoCursoCurriculaId);
            List<Curso> cursos = service.allCursosByCodigo(tipoCursoCurricula.getCodigo());

            ObjectNode node = new ObjectNode(JsonNodeFactory.instance);
            node.put("tieneRequisitos", tipoCursoCurricula.isTieneRequisitos());
            node.put("tieneCreditoManual", tipoCursoCurricula.isTieneCreditoManual());
            if (cursos != null && !cursos.isEmpty()) {
                ObjectNode nodeCurso = new ObjectNode(JsonNodeFactory.instance);
                nodeCurso.put("id", cursos.get(0).getId());
                nodeCurso.put("codigo", cursos.get(0).getCodigo());
                nodeCurso.put("curso", cursos.get(0).getNombre());
                nodeCurso.put("creditos", cursos.get(0).getCreditos());
                node.putPOJO("cursoDefault", nodeCurso);
            }
            response.setData(node);
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

    @ResponseBody
    @RequestMapping("saveGrupoEquivalente")
    public JsonResponse saveGrupoEquivalente(GrupoCursoEquivalente grupoCursoEquivalente, HttpSession session) {

        JsonResponse response = new JsonResponse();
        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
            ObjectNode node = new ObjectNode(JsonNodeFactory.instance);
            String message = "Creado exitosamente";

            service.saveGrupoEquivalente(grupoCursoEquivalente, ds);

            ArrayNode array = new ArrayNode(JsonNodeFactory.instance);
            for (CursoEquivalente curso : grupoCursoEquivalente.getCursoEquivalente()) {
                ObjectNode arrnode = new ObjectNode(JsonNodeFactory.instance);
                arrnode.put("codigo", curso.getCursoEquivalente().getCodigo());
                arrnode.put("nombre", curso.getCursoEquivalente().getNombre());
                arrnode.put("tpc", curso.getCursoEquivalente().getTpc());
                array.add(arrnode);
            }
            if (grupoCursoEquivalente.getCursoEquivalente().size() > 0) {
                node.put("cursoCurricula", grupoCursoEquivalente.getCursoEquivalente().get(0).getCursoCurricula().getId());
                node.put("grupo", grupoCursoEquivalente.getCursoEquivalente().get(0).getGrupo());
                node.set("array", array);
            }
            response.setData(node);
            response.setSuccess(true);
            response.setMessage(message);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (RuntimeException e) {
            ExceptionHandler.handleSpecial(e, response, Messages.FK_ERROR);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("saveGrupoEquivalenteElectivo")
    public JsonResponse saveGrupoEquivalenteElectivo(GrupoCursoEquivalenteElectivo grupoCursoEquivalente, HttpSession session) {

        JsonResponse response = new JsonResponse();
        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
            ObjectNode node = new ObjectNode(JsonNodeFactory.instance);
            String message = "Creado exitosamente";
            service.saveGrupoEquivalenteElectivo(grupoCursoEquivalente, ds);

            ArrayNode array = new ArrayNode(JsonNodeFactory.instance);
            for (CursoEquivalenteElectivo curso : grupoCursoEquivalente.getCursoEquivalenteElectivo()) {
                ObjectNode arrnode = new ObjectNode(JsonNodeFactory.instance);
                arrnode.put("codigo", curso.getCursoEquivalente().getCodigo());
                arrnode.put("nombre", curso.getCursoEquivalente().getNombre());
                arrnode.put("tpc", curso.getCursoEquivalente().getTpc());
                array.add(arrnode);
            }
            if (grupoCursoEquivalente.getCursoEquivalenteElectivo().size() > 0) {
                node.put("cursoCurricula", grupoCursoEquivalente.getCursoEquivalenteElectivo().get(0).getCursoOpcionalCurricula().getId());
                node.put("grupo", grupoCursoEquivalente.getCursoEquivalenteElectivo().get(0).getGrupo());
                node.set("array", array);
            }
            response.setData(node);
            response.setSuccess(true);
            response.setMessage(message);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (RuntimeException e) {
            ExceptionHandler.handleSpecial(e, response, Messages.FK_ERROR);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("savePlanCurricular")
    public JsonResponse savePlanCurricular(
            PlanCurricular planCurricular, HttpSession session) {

        JsonResponse response = new JsonResponse();
        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
            ObjectNode node = new ObjectNode(JsonNodeFactory.instance);
            String message = "Creado exitosamente.";

            if (planCurricular.getId() == null) {
                service.savePlanCurricular(planCurricular);
                node.put("operation", "s");
                node.put("planCurricular", planCurricular.getId());

            } else {
                node.put("operation", "u");
                message = "Actualizado exitosamente.";
                service.updatePlanCurricular(planCurricular);
            }

            response.setData(node);
            response.setSuccess(true);
            response.setMessage(message);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (RuntimeException e) {
            ExceptionHandler.handleSpecial(e, response, Messages.FK_ERROR);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("saveCursoObligatorio")
    public JsonResponse saveCursoObligatorio(CursoCurricula cursoCurricula, HttpSession session) {

        JsonResponse response = new JsonResponse();
        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
            String message = "Curso agregado exitosamente.";
            ObjectNode node = new ObjectNode(JsonNodeFactory.instance);

            if (cursoCurricula.getId() == null) {
                service.saveCursoCurricula(cursoCurricula, ds);
            } else {
                message = "Curso actualizado exitosamente.";
                service.updateCursoCurricula(cursoCurricula, ds);
            }

            response.setData(node);
            response.setSuccess(true);
            response.setMessage(message);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (RuntimeException e) {
            ExceptionHandler.handleSpecial(e, response, Messages.FK_ERROR);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("deleteCursoObligatorio")
    public JsonResponse deleteCursoObligatorio(CursoCurricula cursoCurricula, HttpSession session) {

        JsonResponse response = new JsonResponse();
        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
            service.deleteCursoCurricula(cursoCurricula, ds);

            response.setSuccess(true);
            response.setMessage("Curso eliminado exitosamente");

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (RuntimeException e) {
            ExceptionHandler.handleSpecial(e, response, Messages.FK_ERROR);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("deleteGrupoEquivalente")
    public JsonResponse deleteGrupoEquivalente(@RequestParam("grupo") Integer grupo, @RequestParam("curso") Long idCurso, HttpSession session) {

        JsonResponse response = new JsonResponse();
        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
            service.deleteCursoEquivalenteByGrupoCursoCurricula(grupo, new CursoCurricula(idCurso));
            response.setSuccess(true);
            response.setMessage("Grupo eliminado exitosamente");

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (RuntimeException e) {
            ExceptionHandler.handleSpecial(e, response, Messages.FK_ERROR);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("deleteGrupoEquivalenteElectivo")
    public JsonResponse deleteGrupoEquivalenteElectivo(@RequestParam("grupo") Integer grupo, @RequestParam("curso") Long idCurso, HttpSession session) {

        JsonResponse response = new JsonResponse();
        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
            service.deleteCursoEquivalenteElectivoByGrupoCursoCurricula(grupo, new CursoOpcionalCurricula(idCurso));
            response.setSuccess(true);
            response.setMessage("Grupo eliminado exitosamente");

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (RuntimeException e) {
            ExceptionHandler.handleSpecial(e, response, Messages.FK_ERROR);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("trasladarCiclo")
    public JsonResponse trasladarCiclo(CursoCurricula cursoCurricula, HttpSession session) {

        JsonResponse response = new JsonResponse();
        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
            service.trasladarCiclo(cursoCurricula, ds);

            response.setSuccess(true);
            response.setMessage("Curso trasladado a otro ciclo exitosamente");

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (RuntimeException e) {
            ExceptionHandler.handleSpecial(e, response, Messages.FK_ERROR);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("trasladarToElectivos")
    public JsonResponse trasladarToElectivos(CursoCurricula cursoCurricula, HttpSession session) {

        JsonResponse response = new JsonResponse();
        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
            service.trasladarToElectivos(cursoCurricula, ds);

            response.setSuccess(true);
            response.setMessage("Curso traslado al grupo de electivos exitosamente");

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (RuntimeException e) {
            ExceptionHandler.handleSpecial(e, response, Messages.FK_ERROR);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("trasladarToObligatorios")
    public JsonResponse trasladarToObligatorios(CursoCurricula cursoCurricula, HttpSession session) {

        JsonResponse response = new JsonResponse();
        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
            service.trasladarToObligatorios(cursoCurricula, ds);

            response.setSuccess(true);
            response.setMessage("Curso traslado al grupo de Obligatorios/Generales exitosamente");

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (RuntimeException e) {
            ExceptionHandler.handleSpecial(e, response, Messages.FK_ERROR);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("saveCursoAdicional")
    public JsonResponse saveCursoAdicional(CursoAdicionalCurricula cursoAdicionalCurricula, HttpSession session) {

        JsonResponse response = new JsonResponse();
        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
            String message = "Curso agregado exitosamente.";
            ObjectNode node = new ObjectNode(JsonNodeFactory.instance);
            ObjectUtil.eliminarAttrSinId(cursoAdicionalCurricula);

            if (cursoAdicionalCurricula.getId() != null) {
                service.updateCursoAdicional(cursoAdicionalCurricula, ds);
                message = "Curso actualizado exitosamente";
            } else {
                service.saveCursoAdicional(cursoAdicionalCurricula, ds);
            }

            response.setData(node);
            response.setSuccess(true);
            response.setMessage(message);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (RuntimeException e) {
            ExceptionHandler.handleSpecial(e, response, Messages.FK_ERROR);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("findCursoAdicional/{id}")
    public JsonResponse findCursoAdicional(@PathVariable Long id, HttpSession session) {

        JsonResponse response = new JsonResponse();
        try {
            CursoAdicionalCurricula curso = service.findCursoAdicional(id);
            response.setData(JsonHelper.createJson(curso, JsonNodeFactory.instance, new String[]{
                "cicloInicio.id",
                "cicloInicio.descripcion",
                "cicloFin.id",
                "cicloFin.descripcion"
            }));
            response.setSuccess(true);
        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("saveCursoElectivo")
    public JsonResponse saveCursoElectivo(CursoOpcionalCurricula cursoOpcional, HttpSession session) {

        JsonResponse response = new JsonResponse();
        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
            String message = "Curso agregado exitosamente.";
            ObjectNode node = new ObjectNode(JsonNodeFactory.instance);

            if (cursoOpcional.getId() == null) {
                service.saveCursoOpcional(cursoOpcional, ds);
            } else {
                message = "Curso actualizado exitosamente.";
                service.updateCursoOpcional(cursoOpcional, ds);
            }

            response.setData(node);
            response.setSuccess(true);
            response.setMessage(message);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (RuntimeException e) {
            ExceptionHandler.handleSpecial(e, response, Messages.FK_ERROR);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @RequestMapping("{planCurricular}/succesSave")
    public String succesSave(
            @RequestParam("origen") String origen,
            @PathVariable("planCurricular") Long planCurricularId, RedirectAttributes redirectAttr, HttpSession session) {
        Notificaciones.crearMsg(Messages.CREATED, redirectAttr);
        return "redirect:/academico/planCurricular/" + planCurricularId + "/editarPlanCurricular?origen=" + origen;
    }

    @RequestMapping("{planCurricular}/editarPlanCurricular")
    public String editarPlanCurricular(
            @PathVariable("planCurricular") Long planCurricularId,
            @RequestParam("origen") String origen, Model model, HttpSession session) {

        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);

        PlanCurricular planCurricular = service.findPlanCurricularById(new PlanCurricular(planCurricularId));
        Carrera carrera = planCurricular.getCarrera();
        List<CicloAcademico> ciclos = service.allUltimosCiclosByModalidad(carrera.getModalidadEstudio(), 20);
        List<OrientacionCarrera> orientaciones = service.allOrientacionByCarreraEstado(carrera, EstadoEnum.ACT);
        //List<TipoCursoCurricula> tiposCursoCurriculas = service.allTiposCursoCurriculasElectivosByPlan();
        List<TipoCursoCurricula> tiposCursoCurriculasObli = service.allTiposCursoCurriculasObligatorios();
        Integer cantAlumnos = service.countAlumnosByPlanCurricular(planCurricular).intValue();
        model.addAttribute("ciclos", ciclos);
        model.addAttribute("planCurricular", planCurricular);
        model.addAttribute("orientaciones", orientaciones);
        model.addAttribute("format", new NumberFormat());
        // model.addAttribute("tiposCursoCurriculas", tiposCursoCurriculas);
        model.addAttribute("tiposCursoCurriculasObli", tiposCursoCurriculasObli);
        model.addAttribute("cantAlumnos", cantAlumnos);

        model.addAttribute("editor", verificadorService.isEditorCurriculas(ds));
        model.addAttribute("editorAll", verificadorService.isEditorCurriculasAll(ds));
        model.addAttribute("editorEpg", verificadorService.isEditorCurriculasEpg(ds));
        model.addAttribute("revisor", verificadorService.isRevisorCurriculas(ds));
        model.addAttribute("origen", verificadorService.getOrigen(origen, "/academico/planCurricular"));

        return "academico/plancurricular/planCurricularForm";
    }

    @ResponseBody
    @RequestMapping("{tipoCursoCurricula}/cursosCurricula")
    public JsonResponse cursosCurricula(@PathVariable("tipoCursoCurricula") Long tipoCursoCurricula, Model model, HttpSession session) {
        JsonResponse response = new JsonResponse();

        try {
            List<CursoCurricula> cursosCurricula = service.allCursosCurriculaByFilter(new TipoCursoCurricula(tipoCursoCurricula));
            String template = "<option value=\"%d\">%s<option>";
            StringBuilder select = new StringBuilder();
            if (!cursosCurricula.isEmpty()) {
                for (CursoCurricula cursoCurriculaEach : cursosCurricula) {
                    select.append(String.format(template, cursoCurriculaEach.getId(), cursoCurriculaEach.getCurso().getNombre()));
                }
            }

            ObjectNode node = new ObjectNode(JsonNodeFactory.instance);
            node.put("cursosCurricula", select.toString());
            response.setData(node);
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

    @ResponseBody
    @RequestMapping("buscarCursos")
    public JsonResponse buscarCursos(
            @RequestParam("nombre") String nombre,
            @RequestParam(name = "tipoCurricula", required = false) String tipoCurricula,
            @RequestParam(name = "tipoCursoCurricula", required = false) Long idTipoCursoCurricula, HttpSession session) {

        JsonNodeFactory jsonFactory = JsonNodeFactory.instance;
        JsonResponse response = new JsonResponse();

        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);

            ArrayNode jsonList = new ArrayNode(jsonFactory);
            List<TipoCurriculaEnum> tiposCurricula = null;
            TipoCursoCurricula tipoCursoCurricula = null;

            if (tipoCurricula != null) {
                tiposCurricula = new ArrayList();
                tiposCurricula.add(TipoCurriculaEnum.valueOf(tipoCurricula));
            }
            if (idTipoCursoCurricula != null) {
                tipoCursoCurricula = service.findTipoCurricula(idTipoCursoCurricula);
                tiposCurricula = tipoCursoCurricula.getTiposCursoCurricula();
            }

            List<Curso> cursos = service.allCursoByNombreTipoCurricula(nombre, tiposCurricula);

            for (Curso curso : cursos) {
                ObjectNode json = new ObjectNode(jsonFactory);
                if (tipoCursoCurricula != null && tipoCursoCurricula.isTieneCreditoManual()) {
                    if (!curso.getCodigo().equals(tipoCursoCurricula.getCodigo())) {
                        continue;
                    }
                }
                json.put("id", curso.getId());
                json.put("curso", curso.getNombre());
                json.put("codigo", curso.getCodigo());
                json.put("tpc", curso.getTpc());
                json.put("creditos", curso.getCreditos());
                json.put("creditosVariables", curso.getCreditosVariables());
                json.put("tipoCredito", curso.getTipoCredito());
                json.put("departamento", (String) ObjectUtil.getParentTree(curso, "departamentoAcademico.nombre"));
                json.put("facultad", (String) ObjectUtil.getParentTree(curso, "departamentoAcademico.facultad.nombre"));
                json.put("especialidad", (String) ObjectUtil.getParentTree(curso, "carrera.nombre"));
                json.put("tipoEspecialidad", (String) ObjectUtil.getParentTree(curso, "carrera.tipoEnum.value"));
                jsonList.add(json);
            }

            response.setData(jsonList);
            response.setTotal(jsonList.size());
            response.setSuccess(true);

        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }

        return response;
    }

    @ResponseBody
    @RequestMapping("buscarCursosCurricula")
    public JsonResponse buscarCursosCurricula(CursoCurricula cursoCurriculaForm, HttpSession session) {

        JsonNodeFactory jsonFactory = JsonNodeFactory.instance;
        JsonResponse response = new JsonResponse();

        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);

            ArrayNode jsonList = new ArrayNode(jsonFactory);
            List<CursoCurricula> cursosCurricula = service.allCursoCurriculaByNombre(cursoCurriculaForm);

            for (CursoCurricula cursoCurricula : cursosCurricula) {
                ObjectNode json = new ObjectNode(jsonFactory);
                Curso curso = cursoCurricula.getCurso();

                json.put("id", cursoCurricula.getId());
                json.put("numeroCiclo", cursoCurricula.getNumeroCiclo());
                json.put("numeroRomano", NumberFormat.roman(cursoCurricula.getNumeroCiclo()));
                json.put("curso", curso.getNombre());
                json.put("codigo", curso.getCodigo());
                json.put("codigo2", curso.getCodigoAnterior1());
                json.put("tpc", curso.getTpc());
                json.put("creditos", curso.getCreditos());
                json.put("departamento", curso.getDepartamentoAcademico().getNombre());
                json.put("facultad", curso.getDepartamentoAcademico().getFacultad().getNombre());
                json.put("especialidad", (String) ObjectUtil.getParentTree(curso, "carrera.nombre"));
                json.put("tipoEspecialidad", (String) ObjectUtil.getParentTree(curso, "carrera.tipoEnum.value"));

                jsonList.add(json);
            }

            response.setData(jsonList);
            response.setTotal(jsonList.size());
            response.setSuccess(true);

        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }

        return response;
    }

    @ResponseBody
    @RequestMapping("buscarCursosTodos")
    public JsonResponse buscarCursosTodos(Curso cursoForm, HttpSession session) {

        JsonNodeFactory jsonFactory = JsonNodeFactory.instance;
        JsonResponse response = new JsonResponse();

        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);

            ArrayNode jsonList = new ArrayNode(jsonFactory);
            List<Curso> cursos = service.allCursoByNombre(cursoForm);

            for (Curso curso : cursos) {
                ObjectNode json = new ObjectNode(jsonFactory);

                json.put("id", curso.getId());
                json.put("curso", curso.getNombre());
                json.put("codigo", curso.getCodigo());
                json.put("codigo2", curso.getCodigoAnterior1());
                json.put("tpc", curso.getTpc());
                json.put("creditos", curso.getCreditos());
                json.put("departamento", curso.getDepartamentoAcademico().getNombre());
                json.put("facultad", curso.getDepartamentoAcademico().getFacultad().getNombre());
                json.put("especialidad", (String) ObjectUtil.getParentTree(curso, "carrera.nombre"));
                json.put("tipoEspecialidad", (String) ObjectUtil.getParentTree(curso, "carrera.tipoEnum.value"));

                jsonList.add(json);
            }

            response.setData(jsonList);
            response.setTotal(jsonList.size());
            response.setSuccess(true);

        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }

        return response;
    }

    @ResponseBody
    @RequestMapping("buscarCursosOpcionales")
    public JsonResponse buscarCursosOpcionales(CursoCurricula cursoCurriculaForm, HttpSession session) {

        JsonNodeFactory jsonFactory = JsonNodeFactory.instance;
        JsonResponse response = new JsonResponse();

        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);

            cursoCurriculaForm.setNumeroCiclo(2000);
            List<RequisitoCursoOpcional> cursosComodines = service.allCursosObligatoriosAndElectivosByNombre(cursoCurriculaForm);

            ArrayNode jsonList = new ArrayNode(jsonFactory);
            for (RequisitoCursoOpcional cursoComodin : cursosComodines) {
                ObjectNode json = new ObjectNode(jsonFactory);
                Curso curso = cursoComodin.getCursoRequisito();

                json.put("id", RandomStringUtils.randomNumeric(4));
                json.put("idCursoCurricula", (Long) ObjectUtil.getParentTree(cursoComodin, "cursoRequisitoCurricula.id"));
                json.put("idCursoOpcional", (Long) ObjectUtil.getParentTree(cursoComodin, "cursoRequisitoOpcional.id"));
                json.put("numeroCiclo", cursoComodin.getNumeroCiclo());
                json.put("numeroRomano", cursoComodin.getNumeroRomano());
                json.put("tipoCursoCurricula", cursoComodin.getTipoCursoCurricula().getNombre());
                json.put("curso", curso.getNombre());
                json.put("codigo", curso.getCodigo());
                json.put("codigo2", curso.getCodigoAnterior1());
                json.put("tpc", curso.getTpc());
                json.put("creditos", curso.getCreditos());
                json.put("departamento", curso.getDepartamentoAcademico().getNombre());
                json.put("facultad", curso.getDepartamentoAcademico().getFacultad().getNombre());
                json.put("especialidad", (String) ObjectUtil.getParentTree(curso, "carrera.nombre"));
                json.put("tipoEspecialidad", (String) ObjectUtil.getParentTree(curso, "carrera.tipoEnum.value"));

                jsonList.add(json);
            }

            response.setData(jsonList);
            response.setTotal(jsonList.size());
            response.setSuccess(true);

        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }

        return response;
    }

    @ResponseBody
    @RequestMapping("deleteCursoAdicional")
    public JsonResponse deleteCursoAdicional(@RequestParam("id") Long cursoAdicionalId, HttpSession session) {

        JsonResponse response = new JsonResponse();
        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
            ObjectNode node = new ObjectNode(JsonNodeFactory.instance);
            service.deleteCursoAdicional(cursoAdicionalId);

            response.setData(node);
            response.setMessage("Curso adicional eliminado.");
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

    @ResponseBody
    @RequestMapping("deleteCursoElectivo")
    public JsonResponse deleteCursoElectivo(CursoOpcionalCurricula cursoElectivo, HttpSession session) {

        JsonResponse response = new JsonResponse();
        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
            ObjectNode node = new ObjectNode(JsonNodeFactory.instance);
            service.deleteCursoOpcional(cursoElectivo);

            response.setData(node);
            response.setMessage("Curso electivo eliminado.");
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

    @ResponseBody
    @RequestMapping("cursoPorTipoCurricula")
    public JsonResponse cursoPorTipoCurricula(@RequestParam("tipoCurricula") String tipoCurricula, Model model, HttpSession session) {

        List<TipoCurriculaEnum> tiposCurricula = new ArrayList<>();
        tiposCurricula.add(TipoCurriculaEnum.ADIC);
        List<Curso> cursos = service.allCursoByNombreTipoCurricula(null, tiposCurricula);

        JsonResponse response = new JsonResponse();
        response.setSuccess(Boolean.FALSE);

        ArrayNode arr = new ArrayNode(JsonNodeFactory.instance);
        if (cursos != null) {
            ObjectNode node = new ObjectNode(JsonNodeFactory.instance);
            for (Curso curso : cursos) {
                node.put("id", curso.getId());
                node.put("codigo", curso.getCodigo());
                node.put("curso", curso.getNombre());
            }
            arr.add(node);
            response.setSuccess(Boolean.TRUE);
        }
        response.setData(arr);
        return response;
    }

    @ResponseBody
    @RequestMapping("eliminarPlan")
    public JsonResponse eliminarPlan(@RequestBody PlanCurricular plan, Model model, HttpSession session) {
        JsonResponse response = new JsonResponse();
        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
            service.deletePlanCurricular(plan);

            response.setMessage("Plan curricular eliminado satisfactoriamente");
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

    @ResponseBody
    @RequestMapping("desactivarPlan")
    public JsonResponse desactivarPlan(@RequestBody PlanCurricular plan, Model model, HttpSession session) {
        JsonResponse response = new JsonResponse();
        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
            service.desactivarPlanCurricular(plan);

            response.setMessage("Plan curricular eliminado satisfactoriamente");
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

    @ResponseBody
    @RequestMapping("activarPlan")
    public JsonResponse activarPlan(@RequestBody PlanCurricular plan, Model model, HttpSession session) {
        JsonResponse response = new JsonResponse();
        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
            service.activarPlanCurricular(plan);

            response.setMessage("Plan curricular eliminado satisfactoriamente");
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

    @ResponseBody
    @RequestMapping("clonarPlan")
    public JsonResponse clonarPlan(@RequestBody PlanCurricular plan, Model model, HttpSession session) {
        JsonResponse response = new JsonResponse();
        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
            PlanCurricular planBD = service.clonarPlanCurricular(plan, ds.getCicloAcademico(), ds);

            response.setData(planBD.getId());
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

    @ResponseBody
    @RequestMapping("dataCurricula")
    public JsonResponse dataCurricula(PlanCurricular plan, Model model, HttpSession session) {
        JsonResponse response = new JsonResponse();
        try {
            ArrayNode array = new ArrayNode(JsonNodeFactory.instance);
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);

            if (plan.getId() != null) {
                PlanCurricular planBD = service.findPlanCurricularById(plan);

                List<CursoCurricula> cursosCurr = planBD.getCursoCurricula();
                Map<Integer, List<CursoCurricula>> mapCursosCurr = TypesUtil.convertListToMapList("numeroCiclo", cursosCurr);

                for (Map.Entry<Integer, List<CursoCurricula>> entry : mapCursosCurr.entrySet()) {
                    Integer nroCiclo = entry.getKey();
                    if (nroCiclo == 0) {
                        continue;
                    }

                    ObjectNode node = new ObjectNode(JsonNodeFactory.instance);

                    node.put("numeroCiclo", nroCiclo);
                    node.put("numeroRomano", NumberFormat.roman(nroCiclo));

                    ArrayNode arrayCursos = new ArrayNode(JsonNodeFactory.instance);
                    List<CursoCurricula> cursosCiclo = entry.getValue();
                    for (CursoCurricula cursoCurr : cursosCiclo) {
                        Curso curso = cursoCurr.getCurso();
                        ObjectNode nodeCurso = new ObjectNode(JsonNodeFactory.instance);
                        nodeCurso.put("id", cursoCurr.getId());
                        nodeCurso.put("tipo", cursoCurr.getTipoCursoCurricula().getCodigo());
                        nodeCurso.put("curso", curso.getNombre());
                        nodeCurso.put("codigo", curso.getCodigo());
                        nodeCurso.put("creditos", cursoCurr.getCreditos());
                        nodeCurso.put("numeroCurso", cursoCurr.getNumeroCurso());
                        nodeCurso.put("creditosRequisito", cursoCurr.getCreditosRequisito());
                        nodeCurso.put("estado", cursoCurr.getEstado());

                        ArrayNode arrayRequisitos = new ArrayNode(JsonNodeFactory.instance);
                        List<RequisitoCursoCurricula> requisitos = cursoCurr.getRequisitosCursoCurricula();
                        for (RequisitoCursoCurricula requisito : requisitos) {
                            CursoCurricula cursoReq = requisito.getCursoRequisito();
                            ObjectNode nodeReq = new ObjectNode(JsonNodeFactory.instance);
                            nodeReq.put("idReq", cursoReq.getId());
                            nodeReq.put("simultaneo", requisito.getSimultaneo());
                            arrayRequisitos.add(nodeReq);
                        }

                        nodeCurso.set("requisitos", arrayRequisitos);
                        arrayCursos.add(nodeCurso);
                    }

                    node.set("cursos", arrayCursos);
                    array.add(node);
                }
            }
            response.setData(array);
            response.setSuccess(true);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }

        return response;
    }

    @ResponseBody
    @RequestMapping("moveCurso")
    public JsonResponse moveCurso(
            CursoCurricula cursoCurricula,
            @RequestParam("direccion") String direccion, Model model, HttpSession session) {
        JsonResponse response = new JsonResponse();
        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
            service.moveCurso(cursoCurricula, direccion, ds);

            response.setSuccess(true);
            response.setMessage("El curso se ha movido satisfactoriamente");

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }

        return response;
    }

    @ResponseBody
    @RequestMapping("procesaralumnos")
    public JsonResponse generarAvanceCurricular(PlanCurricular planCurricular, HttpSession session) {
        JsonResponse response = new JsonResponse();
        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
            service.generarAvanceCurricular(planCurricular, ds);
            response.setSuccess(true);
            response.setMessage("Avances curricular generados");

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }

        return response;
    }

    @ResponseBody
    @RequestMapping("desvincularCursos")
    public JsonResponse desvincularCursos(PlanCurricular planCurricular, HttpSession session) {
        JsonResponse response = new JsonResponse();
        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
            service.desvincularCursoCurricula(planCurricular, ds);
            response.setSuccess(true);
            response.setMessage("Plan curricular desvinculado satisfactoriamente");

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }

        return response;
    }

    private ArrayNode createCarrerasJson(List<Carrera> carreras) {

        JsonNodeFactory jFactory = JsonNodeFactory.instance;
        ArrayNode array = new ArrayNode(jFactory);

        for (Carrera carr : carreras) {
            ObjectNode node = JsonHelper.createJson(carr, jFactory, true, new String[]{
                "id", "codigo", "nombre", "tipoEnum",
                "facultad.id",
                "facultad.codigo",
                "facultad.nombre",
                "modalidadEstudio.codigo",
                "modalidadEstudio.nombre"
            });
            array.add(node);
        }
        return array;
    }

    @ResponseBody
    @RequestMapping("asignacionmasiva")
    public JsonResponse asignacionmasiva(Carrera carrera, HttpSession session) {

        JsonResponse response = new JsonResponse();

        try {

            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
            service.verificarAsignacion(carrera);
//            service.desvincularMasivaCursoCurricula(carrera, ds);

            response.setSuccess(true);
            response.setMessage("Asignación masiva en proceso");

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }

        return response;
    }

    @ResponseBody
    @RequestMapping("existeAsignacionMasiva")
    public JsonResponse existeAsignacionMasiva(Carrera carrera, HttpSession session) {

        JsonResponse response = new JsonResponse();
        try {
            response.setSuccess(visorAsignaCurricula.existeCarrera(carrera));

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }

        return response;
    }

    @ResponseBody
    @RequestMapping("avanceAsignacionMasiva")
    public JsonResponse avanceAsignacionMasiva(Carrera carrera, HttpSession session) {

        JsonResponse response = new JsonResponse();
        try {
            if (visorAsignaCurricula.procesoMitadCarrera(carrera)) {
                DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
                service.asignacionMasivaCursoCurricula(carrera, ds);
            }

            boolean existe = visorAsignaCurricula.existeCarrera(carrera);
            response.setMessage(visorAsignaCurricula.reporte(carrera));
            response.setTotal(visorAsignaCurricula.porcentajeAvance(carrera));
            response.setSuccess(existe);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }

        return response;
    }

    @ResponseBody
    @RequestMapping("updateResumen")
    public JsonResponse updateResumen(@RequestParam("idResumen") Integer resumen,
            @RequestParam(value = "minCreditos", required = false) Integer minCreditos,
            @RequestParam(value = "totalCreditos", required = false) Integer totalCreditos, HttpSession session) {

        JsonResponse response = new JsonResponse();
        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
            service.updateResumen(minCreditos, totalCreditos, new ResumenPlanCurricular(resumen));
            response.setSuccess(true);
            response.setMessage("Se modifico el resumen exitosamente");

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (RuntimeException e) {
            ExceptionHandler.handleSpecial(e, response, Messages.FK_ERROR);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("allUpdateResumen")
    public JsonResponse allUpdateResumen(HttpSession session) {

        JsonResponse response = new JsonResponse();
        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
            service.allUpdateResumen();
            response.setSuccess(true);
            response.setMessage("Se modifico el resumen exitosamente");

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (RuntimeException e) {
            ExceptionHandler.handleSpecial(e, response, Messages.FK_ERROR);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("allUpdateResumenPost")
    public JsonResponse allUpdateResumenPost(HttpSession session) {

        JsonResponse response = new JsonResponse();
        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
            service.allUpdateResumenPost();
            response.setSuccess(true);
            response.setMessage("Se modifico el resumen exitosamente");

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (RuntimeException e) {
            ExceptionHandler.handleSpecial(e, response, Messages.FK_ERROR);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("caducar")
    public JsonResponse caducar(@RequestParam("idCursoCurricula") Long idCursoCurricula, HttpSession session) {

        JsonResponse response = new JsonResponse();
        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
            service.caducar(idCursoCurricula, ds);
            response.setSuccess(true);
            response.setMessage("Se modifico exitosamente");

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (RuntimeException e) {
            ExceptionHandler.handleSpecial(e, response, Messages.FK_ERROR);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }
}
