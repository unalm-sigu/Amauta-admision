package pe.edu.lamolina.amauta.controller.academico.encuestaestudiantil.docentemodalidad;

import com.itextpdf.text.pdf.PRStream;
import com.itextpdf.text.pdf.PdfName;
import com.itextpdf.text.pdf.PdfStream;
import java.awt.Color;
import java.awt.Font;
import java.awt.Paint;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import static java.math.BigDecimal.ZERO;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.context.Context;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.albatross.zelpers.miscelanea.math.Fraxtion;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Curso;
import pe.edu.lamolina.model.academico.DepartamentoAcademico;
import pe.edu.lamolina.model.academico.Docente;
import pe.edu.lamolina.model.academico.DocenteSeccion;
import pe.edu.lamolina.model.academico.Facultad;
import pe.edu.lamolina.model.academico.ModalidadEstudio;
import pe.edu.lamolina.model.academico.Seccion;
import pe.edu.lamolina.model.encuestaestudiantil.EncuestaDocente;
import pe.edu.lamolina.model.encuestaestudiantil.EncuestaDocenteModalidad;
import pe.edu.lamolina.model.encuestaestudiantil.PuntajeEncuestaDocente;
import pe.edu.lamolina.model.encuestaestudiantil.PuntajeEncuestaDocenteModalidad;
import pe.edu.lamolina.model.enums.ModalidadEstudioEnum;
import static pe.edu.lamolina.model.enums.RolEnum.DOC;
import pe.edu.lamolina.model.enums.TipoOficinaEnum;
import static pe.edu.lamolina.model.enums.TipoSeccionEnum.PCUR;
import static pe.edu.lamolina.model.enums.TipoSeccionEnum.PRA;
import static pe.edu.lamolina.model.enums.TipoSeccionEnum.TCUR;
import static pe.edu.lamolina.model.enums.TipoSeccionEnum.TEO;
import pe.edu.lamolina.model.examen.TemaExamenVirtual;
import pe.edu.lamolina.amauta.controller.seguridad.verificador.VerificadorService;
import pe.edu.lamolina.amauta.dao.academico.CicloAcademicoDAO;
import pe.edu.lamolina.amauta.dao.academico.DepartamentoAcademicoDAO;
import pe.edu.lamolina.amauta.dao.academico.ModalidadEstudioDAO;
import pe.edu.lamolina.amauta.dao.encuesta.EncuestaDocenteDAO;
import pe.edu.lamolina.amauta.dao.encuesta.EncuestaDocenteModalidadDAO;
import pe.edu.lamolina.amauta.dao.encuesta.PuntajeEncuestaDocenteDAO;
import pe.edu.lamolina.amauta.dao.encuesta.PuntajeEncuestaDocenteModalidadDAO;
import pe.edu.lamolina.amauta.zelper.CustomRenderer;
import pe.edu.lamolina.model.constantines.GlobalConstantine;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;
import pe.edu.lamolina.amauta.zelper.pdf.PdfImageProvider;

@Service
@Transactional(readOnly = true)
public class EncuestaDocenteModalidadServiceImp implements EncuestaDocenteModalidadService {

    @Autowired
    EncuestaDocenteModalidadDAO encuestaDocenteModalidadDAO;

    @Autowired
    PuntajeEncuestaDocenteModalidadDAO puntajeEncuestaDocenteModalidadDAO;

    @Autowired
    PuntajeEncuestaDocenteDAO puntajeEncuestaDocenteDAO;

    @Autowired
    EncuestaDocenteDAO encuestaDocenteDAO;

    @Autowired
    ModalidadEstudioDAO modalidadEstudioDAO;

    @Autowired
    DepartamentoAcademicoDAO departamentoAcademicoDAO;

    @Autowired
    VerificadorService verificadorService;

    @Autowired
    CicloAcademicoDAO cicloAcademicoDAO;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public List<EncuestaDocenteModalidad> allByDynatableCicloAcademico(DynatableFilter filter, CicloAcademico ciclo, List<DepartamentoAcademico> departamentos, DataSessionPivot ds) {
        Docente docente = null;
        if (ds.getRolActivo().getCodigoEnum() == DOC) {
            docente = ds.getDocente();
        }
        return encuestaDocenteModalidadDAO.allByDynatableCicloAcademico(filter, ciclo, departamentos, docente);
    }

    @Override
    public Context reporte(EncuestaDocenteModalidad encuestaDocenteModalidad) {
        EncuestaDocenteModalidad edm = encuestaDocenteModalidadDAO.find(encuestaDocenteModalidad.getId());

        List<PuntajeEncuestaDocente> peds = puntajeEncuestaDocenteDAO.allByDocenteModalidadCicloAcademicoActivo(edm.getDocente(), edm.getModalidadEstudio(), edm.getCicloAcademico());

        List<PuntajeEncuestaDocenteModalidad> puntajes = puntajeEncuestaDocenteModalidadDAO.allByEncuestaDocenteModalidad(encuestaDocenteModalidad);

        List<EncuestaDocente> anuladas = encuestaDocenteDAO.allAnuladaByModalidadEstudioDocenteCicloAcademico(edm.getModalidadEstudio(), edm.getDocente(), edm.getCicloAcademico());

        return buildReport(edm, peds, puntajes, anuladas);
    }

    private Context buildReport(
            EncuestaDocenteModalidad edm,
            List<PuntajeEncuestaDocente> peds,
            List<PuntajeEncuestaDocenteModalidad> puntajes,
            List<EncuestaDocente> anuladas) {

        Collections.sort(puntajes, new PuntajeEncuestaDocenteModalidad.CompareOrdenEncuesta());

        Map<Seccion, List<PuntajeEncuestaDocente>> mapCursos = TypesUtil.convertListToMapList("encuestaDocente.docenteSeccion.seccion", peds);

        List<TemaExamenVirtual> temas = peds
                .stream()
                .map(PuntajeEncuestaDocente::getTemaEncuesta)
                .distinct()
                .sorted(Comparator.comparing(TemaExamenVirtual::getNombre))
                .collect(Collectors.toList());

        Map<Seccion, Long> mapEncuestados = new HashMap();
        Map<Seccion, Long> mapMatriculados = new HashMap();
        Map<Seccion, BigDecimal> mapPorcentaje = new HashMap();
        Map<Seccion, BigDecimal> mapHorasTeo = new HashMap();
        Map<Seccion, BigDecimal> mapHorasPra = new HashMap();
        Map<Seccion, EncuestaDocente> mapDocenteSeccion = new HashMap();

        Set<EncuestaDocente> encuestas = new HashSet();
        BigDecimal CIEN = new BigDecimal("100");

        for (Map.Entry<Seccion, List<PuntajeEncuestaDocente>> entry : mapCursos.entrySet()) {
            Collections.sort(entry.getValue(), new PuntajeEncuestaDocente.CompareOrdenEncuesta());
            for (PuntajeEncuestaDocente ped : entry.getValue()) {

                EncuestaDocente encuesta = ped.getEncuestaDocente();
                if (encuestas.contains(encuesta)) {
                    continue;
                } else {
                    encuestas.add(encuesta);
                }

                Seccion key = entry.getKey();
                Curso curso = key.getGrupoSeccion().getCurso();
                DocenteSeccion docenteSeccion = encuesta.getDocenteSeccion();
                if (docenteSeccion.getPorcentajeCargaFraccion() == null) {
                    docenteSeccion.setPorcentajeCargaFraccion("0");
                }
                BigDecimal horasTeo = new Fraxtion("0/1").getValue(2);
                BigDecimal horasPra = new Fraxtion("0/1").getValue(2);

                if (Arrays.asList(TEO, TCUR).contains(key.getTipoSeccionEnum())) {
                    Fraxtion frax = new Fraxtion(docenteSeccion.getPorcentajeCargaFraccion());
                    frax = frax.multiply(new BigDecimal(curso.getHorasTeoria())).divide(CIEN);
                    horasTeo = frax.getValue(2);
                    if (Arrays.asList(TCUR).contains(key.getTipoSeccionEnum())) {
                        boolean tieneCursoPractico = false;
                        Fraxtion frax1 = new Fraxtion(0);
                        for (Seccion seccione : docenteSeccion.getSeccion().getGrupoSeccion().getSecciones()) {
                            if (PCUR == seccione.getTipoSeccionEnum()) {
                                for (DocenteSeccion docenteSeccion1 : seccione.getDocenteSeccion()) {
                                    if (docenteSeccion1.getDocente().getId() == docenteSeccion.getDocente().getId()) {
                                        tieneCursoPractico = true;
                                        frax1 = new Fraxtion(docenteSeccion1.getPorcentajeCarga());
                                        frax1 = frax1.multiply(new BigDecimal(curso.getHorasPractica())).divide(CIEN);
                                    }
                                }
                            }
                        }
                        if (tieneCursoPractico) {
                            horasPra = frax1.getValue(2);
                        }
                    }
                }

                if (Arrays.asList(PRA, PCUR).contains(key.getTipoSeccionEnum())) {
                    Fraxtion frax = new Fraxtion(docenteSeccion.getPorcentajeCargaFraccion());
                    frax = frax.multiply(new BigDecimal(curso.getHorasPractica())).divide(CIEN);
                    horasPra = frax.getValue(2);
                }

                if (!mapEncuestados.containsKey(key)) {
                    mapEncuestados.put(key, encuesta.getAlumnosEncuestados());
                    mapMatriculados.put(key, encuesta.getAlumnosInicio());
                    mapDocenteSeccion.put(key, encuesta);
                    mapHorasTeo.put(key, horasTeo);
                    mapHorasPra.put(key, horasPra);

                    BigDecimal encuestados = new BigDecimal(encuesta.getAlumnosEncuestados());
                    BigDecimal matriculados = new BigDecimal(encuesta.getAlumnosInicio());
                    BigDecimal porc = encuestados.multiply(CIEN).divide(matriculados, 2, RoundingMode.HALF_UP);
                    mapPorcentaje.put(key, porc);
                }
            }
        }

        SimpleDateFormat formateador = new SimpleDateFormat("EEEE d 'de' MMMM 'de' yyyy", Locale.forLanguageTag("es-ES"));

        String imgBuilt = buildPlot(puntajes, edm.getCicloAcademico());

        Context ctx = new Context();
        ctx.setVariable("edm", edm);
        ctx.setVariable("docente", edm.getDocente());
        ctx.setVariable("modalidad", edm.getModalidadEstudio());
        ctx.setVariable("cicloAcademico", edm.getCicloAcademico());
        ctx.setVariable("mapCursos", mapCursos);
        ctx.setVariable("mapEncuestados", mapEncuestados);
        ctx.setVariable("mapMatriculados", mapMatriculados);
        ctx.setVariable("mapDocenteSeccion", mapDocenteSeccion);
        ctx.setVariable("mapPorcentaje", mapPorcentaje);
        ctx.setVariable("mapHorasTeo", mapHorasTeo);
        ctx.setVariable("mapHorasPra", mapHorasPra);
        ctx.setVariable("puntajes", puntajes);
        ctx.setVariable("anuladas", anuladas);
        ctx.setVariable("temas", temas);
        ctx.setVariable("fecha", String.format("La Molina, %s", formateador.format(new Date())));
        ctx.setVariable("imagenChart", imgBuilt);
        logger.debug("imagenChart {}", imgBuilt);

        ctx.setVariable("nombrePdf", System.currentTimeMillis() + "_ResultadoEncuesta");
        ctx.setVariable("templatePdf", "resultadoencuesta");

        return ctx;
    }

    public static void replaceStream(PRStream orig, PdfStream stream) throws IOException {
        orig.clear();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        stream.writeContent(baos);
        orig.setData(baos.toByteArray(), false);
        for (PdfName name : stream.getKeys()) {
            orig.put(name, stream.get(name));
        }
    }

    @Override
    public List<Context> reporteTodos(CicloAcademico cicloAcademico, ModalidadEstudioEnum modalidadEstudioEnum, List<DepartamentoAcademico> departamentos) {

        ModalidadEstudio modalidadEstudio = modalidadEstudioDAO.findByCodigo(modalidadEstudioEnum);

        List<EncuestaDocenteModalidad> encuestas = encuestaDocenteModalidadDAO.allConEncuestadosByCiclo(cicloAcademico, modalidadEstudio, departamentos);

        List<PuntajeEncuestaDocente> peds = puntajeEncuestaDocenteDAO.allByModalidadEncuestaCicloAcademico(modalidadEstudio, cicloAcademico);

        Map<Long, List<PuntajeEncuestaDocente>> pedsXdocente = peds.stream()
                .collect(Collectors.groupingBy(x -> x.getEncuestaDocente().getDocenteSeccion().getDocente().getId()));

        List<PuntajeEncuestaDocenteModalidad> puntajes = puntajeEncuestaDocenteModalidadDAO.allByEncuestasDocenteModalidad(encuestas);

        Map<Long, List<PuntajeEncuestaDocenteModalidad>> puntajesXencuesta = puntajes.stream()
                .collect(Collectors.groupingBy(x -> x.getEncuestaDocenteModalidad().getDocente().getId()));

        Map<Long, List<EncuestaDocente>> mapAnuladas = encuestaDocenteDAO.allAnuladaByModalidadEstudioCicloAcademico(modalidadEstudio, cicloAcademico)
                .stream()
                .collect(Collectors.groupingBy(x -> x.getDocenteSeccion().getDocente().getId()));

        List<Context> multipleContext = new ArrayList();
        List dfault = new ArrayList();
        Long key = null;

        for (EncuestaDocenteModalidad encuesta : encuestas) {
            key = encuesta.getDocente().getId();
            multipleContext.add(buildReport(encuesta, pedsXdocente.getOrDefault(key, dfault), puntajesXencuesta.getOrDefault(key, dfault), mapAnuladas.getOrDefault(key, dfault)));
        }

        return multipleContext;

    }

    @Override
    public List<Context> reporteUnicoDocenteMultipleCiclo(List<CicloAcademico> cicloAcademicos, ModalidadEstudioEnum modalidadEstudioEnum, List<DepartamentoAcademico> departamentos, Long idDocente) {

        List<Context> multipleContext = new ArrayList();

        ModalidadEstudio modalidadEstudio = modalidadEstudioDAO.findByCodigo(modalidadEstudioEnum);

        for (CicloAcademico cicloAcademico : cicloAcademicos) {

            List<EncuestaDocenteModalidad> encuestas = encuestaDocenteModalidadDAO.allConEncuestadosByCicloDocente(cicloAcademico, modalidadEstudio, departamentos, new Docente(idDocente));

            List<PuntajeEncuestaDocente> peds = puntajeEncuestaDocenteDAO.allByDocenteModalidadCicloAcademico(new Docente(idDocente), modalidadEstudio, cicloAcademico);

            Map<Long, List<PuntajeEncuestaDocente>> pedsXdocente = peds.stream()
                    .collect(Collectors.groupingBy(x -> x.getEncuestaDocente().getDocenteSeccion().getDocente().getId()));

            List<PuntajeEncuestaDocenteModalidad> puntajes = puntajeEncuestaDocenteModalidadDAO.allByEncuestasDocenteModalidad(encuestas);

            Map<Long, List<PuntajeEncuestaDocenteModalidad>> puntajesXencuesta = puntajes.stream()
                    .collect(Collectors.groupingBy(x -> x.getEncuestaDocenteModalidad().getDocente().getId()));

            List<EncuestaDocente> anuladas = encuestaDocenteDAO.allAnuladaByModalidadEstudioDocenteCicloAcademico(modalidadEstudio, new Docente(idDocente), cicloAcademico);

            List dfault = new ArrayList();
            Long key = null;

            for (EncuestaDocenteModalidad encuesta : encuestas) {
                key = encuesta.getDocente().getId();
                multipleContext.add(buildReport(encuesta, pedsXdocente.getOrDefault(key, dfault), puntajesXencuesta.getOrDefault(key, dfault), anuladas));
            }
        }

        return multipleContext;

    }

    private String buildPlot(List<PuntajeEncuestaDocenteModalidad> puntajes, CicloAcademico ciclo) {
        Font fontBold = new Font("Arial", Font.BOLD, 16);
        Font fontNormal = new Font("Arial", Font.PLAIN, 12);
        try {
            DefaultCategoryDataset data = new DefaultCategoryDataset();
            BigDecimal DOS = new BigDecimal("2");

            for (PuntajeEncuestaDocenteModalidad puntaje : puntajes) {
                if (ciclo.getCodigoInt() < 202220) {//solución temporal hasta definir la escala
                    data.setValue(puntaje.getPuntaje().divide(DOS, 6, RoundingMode.HALF_UP), "CATEGORÍA", puntaje.getTemaEncuesta().getNombre());
                } else {
                    data.setValue(puntaje.getPuntaje().setScale(0, RoundingMode.HALF_UP), "CATEGORÍA", puntaje.getTemaEncuesta().getNombre());
                }
            }

            JFreeChart chart = ChartFactory.createBarChart3D(
                    "ENCUESTA ESTUDIANTIL\n(Escala 1 - 5)",
                    "Categoría",
                    "Puntaje",
                    data,
                    PlotOrientation.VERTICAL,
                    false, true, false);

            CategoryPlot plot = chart.getCategoryPlot();

            plot.setRenderer((CategoryItemRenderer) new CustomRenderer(
                    new Paint[]{Color.red, Color.blue, Color.green,
                        Color.yellow, Color.orange}));

            plot.setRangeGridlinePaint(new Color(0));
            plot.setBackgroundPaint(Color.white);
            plot.getDomainAxis().setTickLabelFont(fontNormal);
            plot.getDomainAxis().setLabelFont(fontBold);
            plot.getRangeAxis().setTickLabelFont(fontNormal);
            plot.getRangeAxis().setLabelFont(fontBold);

            plot.getDomainAxis().setMaximumCategoryLabelWidthRatio(5.0f);
            chart.getTitle().setFont(fontBold);

            BarRenderer br = (BarRenderer) plot.getRenderer();
            br.setMaximumBarWidth(.05);

            NumberAxis range = (NumberAxis) plot.getRangeAxis();
            range.setTickUnit(new NumberTickUnit(0.5));
            plot.getRangeAxis().setRange(0, 5);

            int width = 1300;
            int height = 600;
            String fileName = String.format("%s%d.png", PdfImageProvider.ONLY_CHART_ENCUESTA, TypesUtil.getUnixTime());
            ChartUtilities.writeChartAsPNG(new FileOutputStream(GlobalConstantine.TMP_DIR + fileName), chart, width, height);
            return fileName;

        } catch (Exception i) {
            System.out.println(i);
        }
        return null;
    }

    @Override
    public List<PuntajeEncuestaDocenteModalidad> resumenTemas(EncuestaDocenteModalidad encuestaDocenteModalidad) {
        return puntajeEncuestaDocenteModalidadDAO.allByEncuestaDocenteModalidad(encuestaDocenteModalidad);
    }

    @Override
    public List<Facultad> allAccesoFacultades(DataSessionPivot ds, HttpServletRequest request, String codeRequest) {
        if (verificadorService.puedeVerAllFacultades(ds, "ENCUESTA_ESTUDIANTIL")) {
            return new ArrayList();
        }

        List<Facultad> facultades = verificadorService.allInstanciasByMenuRol(TipoOficinaEnum.FAC, request, ds, codeRequest);
        if (facultades.isEmpty()) {
            facultades.add(new Facultad(99999L));
        }
        return facultades;
    }

    @Override
    public List<DepartamentoAcademico> allAccesoDepartamentos(DataSessionPivot ds, List<Facultad> facultades, CicloAcademico ciclo, HttpServletRequest request, String codeRequest) {
        if (facultades.isEmpty()) {
            return new ArrayList();
        }
        if (verificadorService.puedeVerAllDepartamentos(ds, "ENCUESTA_ESTUDIANTIL")) {
            return new ArrayList();
        }

        Facultad comodin = null;
        for (Facultad fac : facultades) {
            if (fac.getId() == 99999L) {
                comodin = fac;
            }
        }
        if (comodin != null) {
            facultades.remove(comodin);
        }

        List<DepartamentoAcademico> departamentos = verificadorService.allInstanciasByMenuRol(TipoOficinaEnum.DPTO, request, ds, codeRequest);
        if (departamentos.isEmpty() && facultades.isEmpty()) {
            departamentos.add(new DepartamentoAcademico(99999L));
            return departamentos;
        }

        if (!departamentos.isEmpty() && facultades.isEmpty()) {
            return departamentos;
        }

        List<DepartamentoAcademico> dptosAll = departamentoAcademicoDAO.allFromDocentesByCiclo(ciclo);
        Map<Long, Facultad> mapFacultad = TypesUtil.convertListToMap("id", facultades);
        for (DepartamentoAcademico dpto : dptosAll) {
            Facultad fac = mapFacultad.get(dpto.getFacultad().getId());
            if (fac != null) {
                departamentos.add(dpto);
            }
        }
        if (departamentos.isEmpty()) {
            departamentos.add(new DepartamentoAcademico(99999L));
        }
        return departamentos;
    }

    @Override
    public List<CicloAcademico> allCicloAcademico() {
        return cicloAcademicoDAO.allPregradoByRangeCode(201920, 220000);
    }

}
