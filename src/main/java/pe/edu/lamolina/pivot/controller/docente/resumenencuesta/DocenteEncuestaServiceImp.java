package pe.edu.lamolina.pivot.controller.docente.resumenencuesta;

import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.PRStream;
import com.itextpdf.text.pdf.PdfDictionary;
import com.itextpdf.text.pdf.PdfImage;
import com.itextpdf.text.pdf.PdfName;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import java.awt.Color;
import java.awt.Font;
import java.awt.Paint;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
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
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Docente;
import pe.edu.lamolina.model.academico.DocenteSeccion;
import pe.edu.lamolina.model.academico.GrupoSeccion;
import pe.edu.lamolina.model.academico.Seccion;
import pe.edu.lamolina.model.encuestaestudiantil.ConfiguraEncuesta;
import pe.edu.lamolina.model.encuestaestudiantil.EncuestaDocente;
import pe.edu.lamolina.model.encuestaestudiantil.EncuestaDocenteModalidad;
import pe.edu.lamolina.model.encuestaestudiantil.EncuestaEstudiantil;
import pe.edu.lamolina.model.encuestaestudiantil.PuntajeEncuestaDocente;
import pe.edu.lamolina.model.encuestaestudiantil.PuntajeEncuestaDocenteModalidad;
import pe.edu.lamolina.model.enums.EncuestaEstadoEnum;
import pe.edu.lamolina.model.enums.TipoExamenVirtualEnum;
import pe.edu.lamolina.model.examen.ExamenVirtual;
import pe.edu.lamolina.model.examen.TemaExamenVirtual;
import pe.edu.lamolina.model.examen.TipoExamenVirtual;
import static pe.edu.lamolina.pivot.controller.academico.encuestaestudiantil.docentemodalidad.EncuestaDocenteModalidadServiceImp.replaceStream;
import pe.edu.lamolina.pivot.dao.academico.DocenteSeccionDAO;
import pe.edu.lamolina.pivot.dao.encuesta.ConfiguraEncuestaDAO;
import pe.edu.lamolina.pivot.dao.encuesta.CursoSinEncuestaDAO;
import pe.edu.lamolina.pivot.dao.encuesta.EncuestaDocenteDAO;
import pe.edu.lamolina.pivot.dao.encuesta.EncuestaDocenteModalidadDAO;
import pe.edu.lamolina.pivot.dao.encuesta.EncuestaEstudiantilDAO;
import pe.edu.lamolina.pivot.dao.encuesta.ExamenVirtualDAO;
import pe.edu.lamolina.pivot.dao.encuesta.PeriodoEncuestaDAO;
import pe.edu.lamolina.pivot.dao.encuesta.PuntajeEncuestaDocenteDAO;
import pe.edu.lamolina.pivot.dao.encuesta.PuntajeEncuestaDocenteModalidadDAO;
import pe.edu.lamolina.pivot.dao.encuesta.TipoExamenVirtualDAO;
import pe.edu.lamolina.pivot.zelper.CustomRenderer;
import pe.edu.lamolina.pivot.zelper.constant.Constantine;
import pe.edu.lamolina.pivot.zelper.pdf.PdfContent;
import pe.edu.lamolina.pivot.zelper.pdf.PdfGenerator;
import pe.edu.lamolina.pivot.zelper.pdf.TipoPdfEnum;

@Service
@Transactional(readOnly = true)
public class DocenteEncuestaServiceImp implements DocenteEncuestaService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    EncuestaDocenteModalidadDAO encuestaDocenteModalidadDAO;
    @Autowired
    PuntajeEncuestaDocenteModalidadDAO puntajeEncuestaDocenteModalidadDAO;
    @Autowired
    PuntajeEncuestaDocenteDAO puntajeEncuestaDocenteDAO;
    @Autowired
    EncuestaDocenteDAO encuestaDocenteDAO;
    @Autowired
    PdfGenerator pdfGenerator;
    @Autowired
    DocenteSeccionDAO docenteSeccionDAO;
    @Autowired
    PeriodoEncuestaDAO periodoEncuestaDAO;
    @Autowired
    ConfiguraEncuestaDAO configuraEncuestaDAO;
    @Autowired
    CursoSinEncuestaDAO cursoSinEncuestaDAO;
    @Autowired
    TipoExamenVirtualDAO tipoExamenVirtualDAO;
    @Autowired
    ExamenVirtualDAO examenVirtualDAO;
    @Autowired
    EncuestaEstudiantilDAO encuestaEstudiantilDAO;

    @Override
    public List<EncuestaDocenteModalidad> allByDynatableCicloAcademicoDocente(DynatableFilter filter, CicloAcademico ciclo, Docente docente) {
        return encuestaDocenteModalidadDAO.allByDynatableCicloAcademicoDocente(filter, ciclo, docente);
    }

    @Override
    public List<PuntajeEncuestaDocenteModalidad> resumenTemas(EncuestaDocenteModalidad encuestaDocenteModalidad) {
        return puntajeEncuestaDocenteModalidadDAO.allByEncuestaDocenteModalidad(encuestaDocenteModalidad);
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

        Map<GrupoSeccion, List<PuntajeEncuestaDocente>> mapCursos = peds.stream().collect(Collectors.groupingBy(x -> x.getEncuestaDocente().getDocenteSeccion().getSeccion().getGrupoSeccion()));

        List<TemaExamenVirtual> temas = peds
                .stream()
                .map(PuntajeEncuestaDocente::getTemaEncuesta)
                .distinct()
                .sorted(Comparator.comparing(TemaExamenVirtual::getNombre))
                .collect(Collectors.toList());

        Map<GrupoSeccion, Long> mapEncuestados = new HashMap<>();
        Map<GrupoSeccion, Long> mapMatriculados = new HashMap<>();

        Set<EncuestaDocente> encuestas = new HashSet<>();

        for (Map.Entry<GrupoSeccion, List<PuntajeEncuestaDocente>> entry : mapCursos.entrySet()) {
            for (PuntajeEncuestaDocente ped : entry.getValue()) {

                EncuestaDocente encuesta = ped.getEncuestaDocente();
                if (encuestas.contains(encuesta)) {
                    continue;
                } else {
                    encuestas.add(encuesta);
                }
                GrupoSeccion key = entry.getKey();
                if (!mapEncuestados.containsKey(key)) {
                    mapEncuestados.put(key, 0L);
                    mapMatriculados.put(key, 0L);
                }
                mapEncuestados.replace(key, mapEncuestados.get(key) + encuesta.getAlumnosEncuestados());
                mapMatriculados.replace(key, mapEncuestados.get(key) + encuesta.getAlumnosInicio());
            }
        }

        SimpleDateFormat formateador = new SimpleDateFormat("EEEE d 'de' MMMM 'de' yyyy", Locale.forLanguageTag("es-ES"));

        Context ctx = new Context();
        ctx.setVariable("edm", edm);
        ctx.setVariable("docente", edm.getDocente());
        ctx.setVariable("modalidad", edm.getModalidadEstudio());
        ctx.setVariable("cicloAcademico", edm.getCicloAcademico());
        ctx.setVariable("mapCursos", mapCursos);
        ctx.setVariable("mapEncuestados", mapEncuestados);
        ctx.setVariable("mapMatriculados", mapMatriculados);
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
            dest = String.format("%s%d.pdf", Constantine.TMP_DIR, TypesUtil.getUnixTime());

            PdfStamper stamper = new PdfStamper(reader, new FileOutputStream(dest));
            stamper.close();
            reader.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return dest;
    }

    private String buildPlot(List<PuntajeEncuestaDocenteModalidad> puntajes) {
        Font fontBold = new Font("Arial", Font.BOLD, 16);
        Font fontNormal = new Font("Arial", Font.PLAIN, 12);
        try {
            DefaultCategoryDataset data = new DefaultCategoryDataset();

            for (PuntajeEncuestaDocenteModalidad puntaje : puntajes) {
                data.setValue(puntaje.getPuntaje(), "CATEGORÍA", puntaje.getTemaEncuesta().getNombre());
            }

            JFreeChart chart = ChartFactory.createBarChart3D(
                    "ENCUESTA ESTUDIANTIL\n(ESCALA 1 - 5)",
                    "CATEGORÍA",
                    "PUNTAJE",
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

            String fileName = String.format("%s%d.png", Constantine.TMP_DIR, TypesUtil.getUnixTime());
            ChartUtilities.writeChartAsPNG(new FileOutputStream(fileName), chart, width, height);
            return fileName;

        } catch (Exception i) {
            System.out.println(i);
        }
        return null;
    }

    @Override
    public List<EncuestaDocente> allEncuestaDocente(DynatableFilter filter, CicloAcademico ciclo, Docente docente) {
        List<EncuestaDocente> encuestas = encuestaDocenteDAO.allByDynatableDocente(filter, ciclo, docente);
        List<Seccion> secciones = new ArrayList();
        for (EncuestaDocente encuesta : encuestas) {
            Seccion seccion = encuesta.getDocenteSeccion().getSeccion();
            secciones.add(seccion);
        }
        List<DocenteSeccion> profesSecciones = docenteSeccionDAO.allPersonasActivasBySecciones(secciones);
        Map<Long, List<DocenteSeccion>> mapProfesBySeccion = TypesUtil.convertListToMapList("seccion.id", profesSecciones);
        for (EncuestaDocente encuesta : encuestas) {
            Seccion seccion = encuesta.getDocenteSeccion().getSeccion();
            List<DocenteSeccion> profesSecc = mapProfesBySeccion.get(seccion.getId());
            profesSecc = (profesSecc == null) ? new ArrayList() : profesSecc;
            seccion.setDocenteSeccion(profesSecc);
        }

        return encuestas;
    }

    @Override
    public EncuestaEstudiantil findEncuestaDocente(CicloAcademico cicloAcademico) {
        TipoExamenVirtual tipoEncuesta = tipoExamenVirtualDAO.findByEnum(TipoExamenVirtualEnum.ENC_DOC);
        ExamenVirtual encuestaModelo = examenVirtualDAO.findEncuestaActivaByTipo(tipoEncuesta);
        EncuestaEstudiantil encuesta = null;
        if (encuestaModelo != null) {
            encuesta = encuestaEstudiantilDAO.findByCicloEncuesta(cicloAcademico, encuestaModelo);
        }
        if (encuesta == null) {
            encuesta = new EncuestaEstudiantil();
            encuesta.setEstadoEnum(EncuestaEstadoEnum.NCRE);
        }

        encuesta.setPeriodosEncuesta(new ArrayList());
        encuesta.setConfiguraEncuesta(new ArrayList());
        encuesta.setCursosNoEncuestar(new ArrayList());
        if (encuesta.getId() != null) {
            encuesta.setPeriodosEncuesta(periodoEncuestaDAO.allByEncuesta(encuesta));
            ConfiguraEncuesta cfg = configuraEncuestaDAO.findByEncuesta(encuesta);
            if (cfg != null) {
                encuesta.getConfiguraEncuesta().add(cfg);
            }
            encuesta.setCursosNoEncuestar(cursoSinEncuestaDAO.allByEncuestaEstudiantil(encuesta));
            List<EncuestaDocente> encDocentes = encuestaDocenteDAO.allByEncuestaEstudiantil(encuesta, new ArrayList());
            int activos = 0;
            int anulados = 0;
            int innecesa = 0;
            int cerrados = 0;
            int sinperio = 0;
            for (EncuestaDocente encDocente : encDocentes) {
                switch (encDocente.getEstadoEnum()) {
                    case ACT:
                        activos++;
                        break;
                    case ANU:
                        anulados++;
                        break;
                    case TEO:
                        innecesa++;
                        break;
                    case CER:
                        cerrados++;
                        break;
                    case FECH:
                        sinperio++;
                        break;
                }
            }
            encuesta.setEncuestasActivas(activos);
            encuesta.setEncuestasAnuladas(anulados);
            encuesta.setEncuestasCerradas(cerrados);
            encuesta.setEncuestasInnecesarias(innecesa);
            encuesta.setEncuestasSinPeriodo(sinperio);
        }

        return encuesta;
    }
}
