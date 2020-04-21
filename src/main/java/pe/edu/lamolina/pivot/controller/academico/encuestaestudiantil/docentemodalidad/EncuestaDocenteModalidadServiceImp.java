package pe.edu.lamolina.pivot.controller.academico.encuestaestudiantil.docentemodalidad;

import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.PRStream;
import com.itextpdf.text.pdf.PdfDictionary;
import com.itextpdf.text.pdf.PdfImage;
import com.itextpdf.text.pdf.PdfName;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.PdfStream;
import java.awt.Color;
import java.awt.Font;
import java.awt.Paint;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
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
import pe.edu.lamolina.pivot.controller.seguridad.verificador.VerificadorService;
import pe.edu.lamolina.pivot.dao.academico.DepartamentoAcademicoDAO;
import pe.edu.lamolina.pivot.dao.academico.ModalidadEstudioDAO;
import pe.edu.lamolina.pivot.dao.encuesta.EncuestaDocenteDAO;
import pe.edu.lamolina.pivot.dao.encuesta.EncuestaDocenteModalidadDAO;
import pe.edu.lamolina.pivot.dao.encuesta.PuntajeEncuestaDocenteDAO;
import pe.edu.lamolina.pivot.dao.encuesta.PuntajeEncuestaDocenteModalidadDAO;
import pe.edu.lamolina.pivot.zelper.CustomRenderer;
import pe.edu.lamolina.model.constantines.AcademicoConstantine;
import pe.edu.lamolina.model.constantines.GlobalConstantine;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;
import pe.edu.lamolina.pivot.zelper.pdf.PdfContent;
import pe.edu.lamolina.pivot.zelper.pdf.PdfGenerator;
import pe.edu.lamolina.pivot.zelper.pdf.TipoPdfEnum;

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
    PdfGenerator pdfGenerator;

    @Autowired
    VerificadorService verificadorService;

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
    public String reporte(EncuestaDocenteModalidad encuestaDocenteModalidad) {
        EncuestaDocenteModalidad edm = encuestaDocenteModalidadDAO.find(encuestaDocenteModalidad.getId());

        List<PuntajeEncuestaDocente> peds = puntajeEncuestaDocenteDAO.allByDocenteModalidadCicloAcademico(edm.getDocente(), edm.getModalidadEstudio(), edm.getCicloAcademico());

        List<PuntajeEncuestaDocenteModalidad> puntajes = puntajeEncuestaDocenteModalidadDAO.allByEncuestaDocenteModalidad(encuestaDocenteModalidad);

        List<EncuestaDocente> anuladas = encuestaDocenteDAO.allAnuladaByModalidadEstudioDocenteCicloAcademico(edm.getModalidadEstudio(), edm.getDocente(), edm.getCicloAcademico());

        return buildReport(edm, peds, puntajes, anuladas);
    }

    private String buildReport(
            EncuestaDocenteModalidad edm,
            List<PuntajeEncuestaDocente> peds,
            List<PuntajeEncuestaDocenteModalidad> puntajes,
            List<EncuestaDocente> anuladas) {

//        Map<GrupoSeccion, List<PuntajeEncuestaDocente>> mapCursos = peds.stream().collect(Collectors.groupingBy(x -> x.getEncuestaDocente().getDocenteSeccion().getSeccion().getGrupoSeccion()));
        //Collections.sort(peds, new PuntajeEncuestaDocente.CompareOrdenEncuesta());
        Collections.sort(puntajes, new PuntajeEncuestaDocenteModalidad.CompareOrdenEncuesta());
        System.out.println("peds.size=" + peds.size());

        Map<Seccion, List<PuntajeEncuestaDocente>> mapCursos = TypesUtil.convertListToMapList("encuestaDocente.docenteSeccion.seccion", peds);
        System.out.println("mapCursos.size=" + mapCursos.size());
        //Map<Seccion, EncuestaDocente> mapDocenteSeccion = TypesUtil.convertListToMapList("encuestaDocente.docenteSeccion.seccion", "encuestaDocente", peds);

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

//        for (Map.Entry<GrupoSeccion, List<PuntajeEncuestaDocente>> entry : mapCursos.entrySet()) {
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
                BigDecimal horasTeo = new Fraxtion("0/1").getValue(2);
                BigDecimal horasPra = new Fraxtion("0/1").getValue(2);

                if (Arrays.asList(TEO, TCUR).contains(key.getTipoSeccionEnum())) {
                    Fraxtion frax = new Fraxtion(docenteSeccion.getPorcentajeCargaFraccion());
                    frax = frax.multiply(new BigDecimal(curso.getHorasTeoria())).divide(CIEN);
                    horasTeo = frax.getValue(2);
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
        System.out.println("mapDocenteSeccion.size=" + mapDocenteSeccion.size());

        SimpleDateFormat formateador = new SimpleDateFormat("EEEE d 'de' MMMM 'de' yyyy", Locale.forLanguageTag("es-ES"));

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
        ctx.setVariable("plot", buildPlot(puntajes));

        PdfContent pdfContent = new PdfContent();
        pdfContent.setContext(ctx);
        pdfContent.setTipoPdfEnum(TipoPdfEnum.RESULTADO_ENCUESTA);

        String src = pdfGenerator.generateDocument(pdfContent, "tmp");
        String dest = src;
        try {
            PdfReader reader = new PdfReader(src);
            PdfDictionary page = reader.getPageN(1);
            PdfDictionary resources = page.getAsDict(PdfName.RESOURCES);
            PdfDictionary xobjects = resources.getAsDict(PdfName.XOBJECT);
            PdfName imgRef = xobjects.getKeys().iterator().next();
            PRStream stream = (PRStream) xobjects.getAsStream(imgRef);
            PdfImage image = new PdfImage(Image.getInstance(buildPlot(puntajes)), "", null);
            replaceStream(stream, image);
            dest = String.format("%s%d.pdf", GlobalConstantine.TMP_DIR, TypesUtil.getUnixTime());

            PdfStamper stamper = new PdfStamper(reader, new FileOutputStream(dest));
            stamper.close();
            reader.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return dest;
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
    public String reporteTodos(CicloAcademico cicloAcademico) {

        ModalidadEstudio modalidadEstudio = modalidadEstudioDAO.findByCodigo(ModalidadEstudioEnum.PRE);
        Map<Long, List<EncuestaDocente>> mapAnuladas = encuestaDocenteDAO.allAnuladaByModalidadEstudioCicloAcademico(modalidadEstudio, cicloAcademico)
                .stream()
                .collect(Collectors.groupingBy(x -> x.getDocenteSeccion().getDocente().getId()));

        List<EncuestaDocenteModalidad> encuestas = encuestaDocenteModalidadDAO.allConEncuestadosByCiclo(cicloAcademico);
        for (EncuestaDocenteModalidad encuesta : encuestas) {
            encuesta.setPuntajeEncuestaDocente(new ArrayList<>());
            encuesta.setPuntajeEncuestaDocenteModalidad(new ArrayList<>());
        }
        Map<Long, EncuestaDocenteModalidad> encuestasPorDocente = encuestas.stream().collect(Collectors.toMap(x -> x.getDocente().getId(), x -> x));
        Map<Long, EncuestaDocenteModalidad> encuestasPorId = encuestas.stream().collect(Collectors.toMap(x -> x.getId(), x -> x));
        List<PuntajeEncuestaDocente> peds = puntajeEncuestaDocenteDAO.allByCicloAcademico(cicloAcademico);

        for (PuntajeEncuestaDocente ped : peds) {
            EncuestaDocenteModalidad edm = encuestasPorDocente.get(ped.getEncuestaDocente().getDocenteSeccion().getDocente().getId());
            if (edm != null && edm.getPuntajeEncuestaDocente() != null) {
                edm.getPuntajeEncuestaDocente().add(ped);
            } else {
                logger.error("not found");
            }
        }

        List<PuntajeEncuestaDocenteModalidad> puntajes = puntajeEncuestaDocenteModalidadDAO.allByEncuestasDocenteModalidad(encuestas);
        for (PuntajeEncuestaDocenteModalidad puntaje : puntajes) {
            EncuestaDocenteModalidad edm = encuestasPorId.get(puntaje.getEncuestaDocenteModalidad().getId());
            if (edm != null && edm.getPuntajeEncuestaDocenteModalidad() != null) {
                edm.getPuntajeEncuestaDocenteModalidad().add(puntaje);
            } else {
                logger.error("not found 2");
            }
        }

        List<String> pdfs = new ArrayList<>();

        for (EncuestaDocenteModalidad encuesta : encuestas) {
            pdfs.add(buildReport(encuesta, encuesta.getPuntajeEncuestaDocente(), encuesta.getPuntajeEncuestaDocenteModalidad(), mapAnuladas.get(encuesta.getDocente().getId())));
        }

        return pdfGenerator.concatPDFs(pdfs, "resumen", true);
    }

    private String buildPlot(List<PuntajeEncuestaDocenteModalidad> puntajes) {
        Font fontBold = new Font("Arial", Font.BOLD, 16);
        Font fontNormal = new Font("Arial", Font.PLAIN, 12);
        try {
            DefaultCategoryDataset data = new DefaultCategoryDataset();
            BigDecimal DOS = new BigDecimal("2");

            for (PuntajeEncuestaDocenteModalidad puntaje : puntajes) {
                data.setValue(puntaje.getPuntaje().divide(DOS, 6, RoundingMode.HALF_UP), "CATEGORÍA", puntaje.getTemaEncuesta().getNombre());
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

            int width = 1200;
            int height = 600;

            String fileName = String.format("%s%d.png", GlobalConstantine.TMP_DIR, TypesUtil.getUnixTime());
            ChartUtilities.writeChartAsPNG(new FileOutputStream(fileName), chart, width, height);
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
    public List<Facultad> allAccesoFacultades(DataSessionPivot ds, HttpServletRequest request) {
        if (verificadorService.puedeVerAllFacultades(ds, "ENCUESTA_ESTUDIANTIL")) {
            return new ArrayList();
        }

        List<Facultad> facultades = verificadorService.allInstanciasByMenuRol(TipoOficinaEnum.FAC, request, ds);
        if (facultades.isEmpty()) {
            facultades.add(new Facultad(99999L));
        }
        return facultades;
    }

    @Override
    public List<DepartamentoAcademico> allAccesoDepartamentos(DataSessionPivot ds, List<Facultad> facultades, CicloAcademico ciclo, HttpServletRequest request) {
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

        List<DepartamentoAcademico> departamentos = verificadorService.allInstanciasByMenuRol(TipoOficinaEnum.DPTO, request, ds);
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

}
