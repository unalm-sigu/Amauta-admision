package pe.edu.lamolina.amauta.controller.academico.profesor.view;

import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import static java.math.BigDecimal.ZERO;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import pe.albatross.zelpers.file.pdf.AbstractOnlyPdfView;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.model.academico.AnexoBoletin;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.DepartamentoAcademico;
import pe.edu.lamolina.model.academico.Docente;
import pe.edu.lamolina.model.academico.DocenteSeccion;
import pe.edu.lamolina.model.academico.Facultad;
import pe.edu.lamolina.model.academico.GrupoSeccion;
import pe.edu.lamolina.model.academico.Seccion;
import pe.edu.lamolina.model.general.Oficina;
import pe.edu.lamolina.model.horario.HorarioSeccion;
import pe.edu.lamolina.model.zelper.pdfgenerator.FooterTypeEnum;
import pe.edu.lamolina.model.zelper.pdfgenerator.HeaderTypeEnum;
import pe.edu.lamolina.model.zelper.pdfgenerator.PdfDocumentGenerator;
import pe.edu.lamolina.model.zelper.pdfgenerator.UEventoPaginaPdf;

@Component
public class DocenteCargaAcademicaPDF extends AbstractOnlyPdfView {

    @Override
    protected void renderMergedOutputModel(Map<String, Object> model, HttpServletRequest request, HttpServletResponse response) throws Exception {

        ByteArrayOutputStream baos = createTemporaryOutputStream();

        Document document = new Document();
        PdfWriter writer = PdfWriter.getInstance(document, baos);
        prepareWriter(model, writer, request);
        buildPdfMetadata(model, document, request);

        writer.setInitialLeading(16);
        CicloAcademico ciclo = (CicloAcademico) model.get("cicloAcademico");
        Oficina oficina = (Oficina) model.get("oficina");

        UEventoPaginaPdf eventoPagina = new UEventoPaginaPdf(HeaderTypeEnum.HEADER1, FooterTypeEnum.FOOTER3);
        eventoPagina.setOficina(oficina);
        eventoPagina.setTitulo1("Carga Académica " + ciclo.getDescripcion());
        this.documentPageVertical(document, writer, eventoPagina);

        document.open();
        buildPdfDocument(model, document, writer, request, response);
        document.close();

        writeToResponse(response, baos);

    }

    @Override
    protected void buildPdfMetadata(Map<String, Object> model, Document document, HttpServletRequest request) {
        document.addAuthor("La Molina");
        document.addCreationDate();
        document.addCreator("Amauta");
        document.addTitle("Carga Académica");
        document.addSubject("");
        document.setPageSize(PageSize.A4);
    }

    @Override
    protected void buildPdfDocument(Map<String, Object> model, Document document, PdfWriter writer, HttpServletRequest request, HttpServletResponse response) throws Exception {

        List<Docente> docentes = (List<Docente>) model.get("docentes");

        List<DocenteSeccion> docentesSecciones = (List<DocenteSeccion>) model.get("docentesSecciones");

        Map<Long, List<DocenteSeccion>> docentesSeccionesXdocente = docentesSecciones.stream().collect(groupingBy(x -> x.getDocente().getId(), toList()));

        List<HorarioSeccion> horarioSecciones = (List<HorarioSeccion>) model.get("horarioSecciones");

        Map<Long, List<HorarioSeccion>> horarioSeccionesXseccion = horarioSecciones.stream().collect(groupingBy(x -> x.getSeccion().getId(), toList()));

        PdfDocumentGenerator uDocumentoPdf = new PdfDocumentGenerator();

        int indice = 0;
        int size = docentes.size();
        for (Docente docente : docentes) {

            indice++;

            List<DocenteSeccion> profeSecciones = docentesSeccionesXdocente.getOrDefault(docente.getId(), new ArrayList());

            List<GrupoSeccion> grupoSecciones = allGpoSecciones(docente, profeSecciones, horarioSeccionesXseccion);
            if (grupoSecciones.isEmpty()) {
                continue;
            }

            this.builContent(document, docente, uDocumentoPdf, grupoSecciones, indice, size);

        }

        if (docentes.isEmpty()) {
            document.add(new Chunk("No hay docentes para la opción seleccionada"));
        }

        String filename = "Carga academica";
        response.setHeader("Content-Disposition", "attachment; filename=\"" + filename + ".pdf\"");
        response.setHeader("Set-Cookie", "fileDownload=true; path=/");
    }

    public void builContent(Document document, Docente docente, PdfDocumentGenerator uDocumentoPdf, List<GrupoSeccion> grupoSecciones, int indice, int size) throws Exception {

        DepartamentoAcademico departamentoAcademico = docente.getDepartamentoAcademico();
        Facultad facultad = departamentoAcademico.getFacultad();

        PdfPTable tableSubs = new PdfPTable(new float[]{1});
        tableSubs.getDefaultCell().setBorder(0);
        tableSubs.setWidthPercentage(100);

        uDocumentoPdf.addBodyCellTable("Facultad " + facultad.getNombre(), tableSubs, 1, Element.ALIGN_LEFT, PdfDocumentGenerator.FUENTE_10_NEGRITA);
        uDocumentoPdf.addBodyCellTable("Departamento " + departamentoAcademico.getNombreLargo(), tableSubs, 1, Element.ALIGN_LEFT, PdfDocumentGenerator.FUENTE_10_NEGRITA);
        uDocumentoPdf.addBodyCellTable(docente.getPersona().getApellidosNombres() + " - " + docente.getCodigo(), tableSubs, 1, Element.ALIGN_LEFT, PdfDocumentGenerator.FUENTE_10_NEGRITA);

        document.add(tableSubs);
        document.add(uDocumentoPdf.agregarEnter(1));
        document.add(new Chunk(""));

        Total total = new Total();

        this.fillGrupos(grupoSecciones, total);

        float[] columns = new float[]{4f, 1f, 2f, 1f, 1f, 1f, 2f};

        if (total.grupoPregrado.size() > 0) {
            this.titleTable(document, columns, uDocumentoPdf, "Cursos de Pregrado");
        }

        PdfPTable tableBody = new PdfPTable(columns);
        tableBody.setWidthPercentage(100);
        for (GrupoSeccion grupoSeccion : total.grupoPregrado) {

            this.fillSecciones(grupoSeccion, uDocumentoPdf, tableBody);

        }

        if (total.grupoPregrado.size() > 0) {
            this.fillTotal(total.creditosPregrado, uDocumentoPdf, tableBody);
        }

        document.add(tableBody);

        if (total.grupoPosgrado.size() > 0) {
            this.titleTable(document, columns, uDocumentoPdf, "Cursos de Posgrado");
        }

        tableBody = new PdfPTable(columns);
        tableBody.setWidthPercentage(100);
        for (GrupoSeccion grupoSeccion : total.grupoPosgrado) {

            this.fillSecciones(grupoSeccion, uDocumentoPdf, tableBody);

        }

        if (total.grupoPosgrado.size() > 0) {
            this.fillTotal(total.creditosPosgrado, uDocumentoPdf, tableBody);
        }

        if (total.grupoPregrado.size() <= 0 && total.grupoPosgrado.size() <= 0) {
            this.titleSinTable(document, columns, uDocumentoPdf, "Docente sin carga académica");
        }

        document.add(tableBody);

        if (indice < size) {
            document.newPage();
        }
    }

    public Document documentPageVertical(Document document, PdfWriter writer, UEventoPaginaPdf eventoPaginaPdf) throws Exception {
        float relativeAditionalMargin = 0;
        HeaderTypeEnum headerTypeEnum = eventoPaginaPdf.getHeaderTypeEnum();

        if (headerTypeEnum != null) {
            if (headerTypeEnum.equals(HeaderTypeEnum.HEADER2)) {
                int cont = 0;
                if (StringUtils.isNotBlank(eventoPaginaPdf.getTitulo1())) {
                    cont++;
                }
                if (StringUtils.isNotBlank(eventoPaginaPdf.getTitulo2())) {
                    cont++;
                }
                if (cont > 0) {
                    relativeAditionalMargin += headerTypeEnum.getRelativeMarginTop() * cont;
                }
            } else {
                relativeAditionalMargin += headerTypeEnum.getRelativeMarginTop();
            }
        }
        document.setMargins(36, 36, 20 + relativeAditionalMargin, 36);
        this.generarPlantillaAgrariaPdf(document, writer, eventoPaginaPdf);
        return document;
    }

    public Document generarPlantillaAgrariaPdf(Document documentoPDF, PdfWriter escritor, UEventoPaginaPdf eventoPagina) throws Exception {
        Rectangle rct = new Rectangle(36, 54, 559, 788);
        escritor.setBoxSize("art", rct);
        escritor.setPageEvent(eventoPagina);
        return documentoPDF;
    }

    public List<GrupoSeccion> allGpoSecciones(Docente docente, List<DocenteSeccion> profeSecciones, Map<Long, List<HorarioSeccion>> horarioSeccionesXseccion) {
        Map<Long, GrupoSeccion> mapGpoSecc = new LinkedHashMap();
        List<Seccion> secciones = new ArrayList();

        for (DocenteSeccion profeSecc : profeSecciones) {
            Seccion secc = profeSecc.getSeccion();
            secc.setDocenteSeccion(new ArrayList());
            secc.getDocenteSeccion().add(profeSecc);
            GrupoSeccion gpoSeccBD = secc.getGrupoSeccion();
            GrupoSeccion gpoSecc = mapGpoSecc.get(gpoSeccBD.getId());
            if (gpoSecc == null) {
                mapGpoSecc.put(gpoSeccBD.getId(), gpoSeccBD);
                gpoSecc = gpoSeccBD;
                gpoSecc.setSecciones(new ArrayList());
            }
            gpoSecc.getSecciones().add(secc);
            secc.setGrupoSeccion(gpoSecc);
            secciones.add(secc);
        }

        List<HorarioSeccion> horarios = allBySecciones(secciones, horarioSeccionesXseccion);

        Map<Long, List<HorarioSeccion>> mapHorarios = TypesUtil.convertListToMapList("seccion.id", horarios);

        for (Seccion secc : secciones) {
            secc.setHorarioSeccion(mapHorarios.getOrDefault(secc.getId(), new ArrayList()));
        }

        return new ArrayList(mapGpoSecc.values());
    }

    private List<HorarioSeccion> allBySecciones(List<Seccion> secciones, Map<Long, List<HorarioSeccion>> horarioSeccionesXseccion) {
        List<HorarioSeccion> horarioSecciones = new ArrayList();
        for (Seccion seccion : secciones) {
            horarioSecciones.addAll(horarioSeccionesXseccion.getOrDefault(seccion.getId(), new ArrayList()));
        }
        return horarioSecciones;
    }

    private void fillGrupos(List<GrupoSeccion> grupoSecciones, Total total) {
        for (GrupoSeccion grupoSeccion : grupoSecciones) {

            AnexoBoletin anexoSup = grupoSeccion.getAnexoBoletin().getAnexoSuperior();

            List<Seccion> secciones = grupoSeccion.getSecciones();
            for (Seccion seccion : secciones) {
                List<DocenteSeccion> profesSeccion = seccion.getDocenteSeccion();
                for (DocenteSeccion profeSecc : profesSeccion) {
                    BigDecimal creaditos = profeSecc.getCreditosCarga();
                    if (profeSecc.getCreditosCarga() == null) {
                        creaditos = ZERO;
                    }
                    if (anexoSup.isAnexoCursosPostgrado()) {
                        total.creditosPosgrado = total.creditosPosgrado.add(creaditos);
                    } else {
                        total.creditosPregrado = total.creditosPregrado.add(creaditos);
                    }
                }
            }

            if (anexoSup.isAnexoCursosPostgrado()) {
                total.grupoPosgrado.add(grupoSeccion);
            } else {
                total.grupoPregrado.add(grupoSeccion);
            }
        }
    }

    private void titleTable(Document document, float[] columns, PdfDocumentGenerator uDocumentoPdf, String titulo) throws DocumentException {

        PdfPTable tableBody = new PdfPTable(new float[]{1});
        tableBody.getDefaultCell().setBorder(0);
        tableBody.setWidthPercentage(100);
        tableBody.setSpacingBefore(10);
        uDocumentoPdf.addBodyCellTable(titulo, tableBody, 10, Element.ALIGN_LEFT);
        document.add(tableBody);

        tableBody = new PdfPTable(columns);
        tableBody.getDefaultCell().setBorder(PdfPCell.RECTANGLE);
        tableBody.setWidthPercentage(100);

        uDocumentoPdf.addTitleCellTable("Curso", tableBody, 1, Element.ALIGN_CENTER);
        uDocumentoPdf.addTitleCellTable("Sección", tableBody, 1, Element.ALIGN_CENTER);
        uDocumentoPdf.addTitleCellTable("Horario", tableBody, 1, Element.ALIGN_CENTER);
        uDocumentoPdf.addTitleCellTable("Matriculados", tableBody, 1, Element.ALIGN_CENTER);
        uDocumentoPdf.addTitleCellTable("% Carga ", tableBody, 1, Element.ALIGN_CENTER);
        uDocumentoPdf.addTitleCellTable("Créditos", tableBody, 1, Element.ALIGN_CENTER);
        uDocumentoPdf.addTitleCellTable("Periodo clases", tableBody, 1, Element.ALIGN_CENTER);
        document.add(tableBody);

    }

    private void titleSinTable(Document document, float[] columns, PdfDocumentGenerator uDocumentoPdf, String titulo) throws DocumentException {

        PdfPTable tableBody = new PdfPTable(new float[]{1});
        tableBody.getDefaultCell().setBorder(0);
        tableBody.setWidthPercentage(100);
        tableBody.setSpacingBefore(10);
        uDocumentoPdf.addBodyCellTable(titulo, tableBody, 10, Element.ALIGN_LEFT);
        document.add(tableBody);
    }

    private void fillSecciones(GrupoSeccion grupoSeccion, PdfDocumentGenerator uDocumentoPdf, PdfPTable tableBody) {

        for (Seccion seccion : grupoSeccion.getSecciones()) {

            StringJoiner sj = new StringJoiner(" ");
            sj.add(grupoSeccion.getCurso().getNombre());
            sj.add("\n");
            sj.add(grupoSeccion.getCurso().getCodigo());
            sj.add(" ");
            sj.add(grupoSeccion.getCurso().getTpc());

            uDocumentoPdf.addBodyCellTable(sj.toString(), tableBody, 1, Element.ALIGN_LEFT);

            sj = new StringJoiner("\n");
            sj.add(seccion.getCodigo2());
            sj.add(tipoSeccion(seccion));
            uDocumentoPdf.addBodyCellTable(sj.toString(), tableBody, 1, Element.ALIGN_CENTER);
            uDocumentoPdf.addBodyCellTable(seccion.getHorarioTexto(), tableBody, 1, Element.ALIGN_CENTER);
            uDocumentoPdf.addBodyCellTable(seccion.getMatriculados().toString(), tableBody, 1, Element.ALIGN_RIGHT);

            for (DocenteSeccion docenteSeccion : seccion.getDocenteSeccion()) {

                String porcentajeCarga = docenteSeccion.getPorcentajeCarga() != null ? docenteSeccion.getPorcentajeCarga().setScale(2, BigDecimal.ROUND_HALF_EVEN).toString() : "";
                String creditosCarga = docenteSeccion.getCreditosCarga() != null ? docenteSeccion.getCreditosCarga().setScale(2, BigDecimal.ROUND_HALF_EVEN).toString() : "";

                uDocumentoPdf.addBodyCellTable(porcentajeCarga, tableBody, 1, Element.ALIGN_RIGHT);

                uDocumentoPdf.addBodyCellTable(creditosCarga, tableBody, 1, Element.ALIGN_RIGHT);

                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy");

                sj = new StringJoiner(" ");
                if (docenteSeccion.getFechaInicio() != null) {
                    sj.add("Desde el ");
                    sj.add(sdf.format(docenteSeccion.getFechaInicio()));
                }
                sj.add("\n");
                if (docenteSeccion.getFechaFin() != null) {
                    sj.add("Hasta el ");
                    sj.add(sdf.format(docenteSeccion.getFechaFin()));
                }
                uDocumentoPdf.addBodyCellTable(sj.toString(), tableBody, 1, Element.ALIGN_CENTER);
            }
        }
    }

    private void fillTotal(BigDecimal creditos, PdfDocumentGenerator uDocumentoPdf, PdfPTable tableBody) {

        uDocumentoPdf.addBodyCellTable("Total créditos carga", tableBody, 5, Element.ALIGN_RIGHT);
        String creditosStr = creditos != null ? creditos.setScale(2, BigDecimal.ROUND_HALF_EVEN).toString() : "";
        uDocumentoPdf.addBodyCellTable(creditosStr, tableBody, 1, Element.ALIGN_RIGHT);
        uDocumentoPdf.addBodyCellTable("", tableBody, 2, Element.ALIGN_RIGHT);
    }

    public class Total {

        public BigDecimal creditosPregrado = ZERO;
        public BigDecimal creditosPosgrado = ZERO;

        public List<GrupoSeccion> grupoPregrado = new ArrayList();
        public List<GrupoSeccion> grupoPosgrado = new ArrayList();

        public Total() {
            creditosPregrado = ZERO;
            creditosPosgrado = ZERO;
            grupoPregrado = new ArrayList();
            grupoPosgrado = new ArrayList();
        }
    }

    private String tipoSeccion(Seccion seccion) {
        if (seccion.getTipoSeccionEnum().getValue().indexOf(" ") < 0) {
            return seccion.getTipoSeccionEnum().getValue();
        }
        return seccion.getTipoSeccionEnum().getValue().split(" ")[0];

    }
}
