package pe.edu.lamolina.pivot.controller.academico.profesor.view;

import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.html.simpleparser.HTMLWorker;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import java.io.ByteArrayOutputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.zelpers.enums.FooterTypeEnum;
import pe.albatross.zelpers.enums.HeaderTypeEnum;
import pe.albatross.zelpers.pdf.document.PdfDocumentGenerator;
import pe.albatross.zelpers.pdf.document.UEventoPaginaPdf;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.DepartamentoAcademico;
import pe.edu.lamolina.model.academico.Docente;
import pe.edu.lamolina.model.academico.Facultad;
import pe.edu.lamolina.model.enums.ContenidoCartaEnum;
import pe.edu.lamolina.model.inscripcion.ContenidoCarta;
import pe.edu.lamolina.pivot.controller.academico.profesor.ProfesorService;
import pe.edu.lamolina.pivot.zelper.pdf.AbstractOnlyPdfView;

@Component
public class ProfesoresPDF extends AbstractOnlyPdfView {

    @Autowired
    ProfesorService service;

    protected void renderMergedOutputModel(Map<String, Object> model, HttpServletRequest request, HttpServletResponse response) throws Exception {
        // IE workaround: write into byte array first.
        ByteArrayOutputStream baos = createTemporaryOutputStream();

        // Apply preferences and build metadata.
        Document document = new Document();
        PdfWriter writer = PdfWriter.getInstance(document, baos);
        prepareWriter(model, writer, request);
        buildPdfMetadata(model, document, request);

        // Build PDF document.
        writer.setInitialLeading(16);
        CicloAcademico ciclo = (CicloAcademico) model.get("cicloAcademico");
        UEventoPaginaPdf eventoPagina = new UEventoPaginaPdf(HeaderTypeEnum.HEADER2, FooterTypeEnum.FOOTER3);
        eventoPagina.setTitulo1("ENTREGA DE MATERIALES");
        eventoPagina.setTitulo2(String.format("CICLO ACADEMICO %s", ciclo.getDescripcion()));
        this.documentPageVertical(document, writer, eventoPagina);

        document.open();
        buildPdfDocument(model, document, writer, request, response);
        document.close();

        // Flush to HTTP response.
        writeToResponse(response, baos);

    }

    @Override
    protected void buildPdfMetadata(Map<String, Object> model, Document document, HttpServletRequest request) {
        document.addAuthor("La Molina");
        document.addCreationDate();
        document.addCreator("Amauta");
        document.addTitle("Entrega de materiales");
        document.addSubject("");
        document.setPageSize(PageSize.A4);
    }

    @Override
    protected void buildPdfDocument(Map<String, Object> model, Document document, PdfWriter writer, HttpServletRequest request, HttpServletResponse response) throws Exception {
        CicloAcademico ciclo = (CicloAcademico) model.get("cicloAcademico");
        ContenidoCarta contenidoCarta = service.findContenidoCartaByEnum(ContenidoCartaEnum.AMAUTA_FOOTER_INVENTARIO_DOCENTE);

        List<DepartamentoAcademico> departamentos = (List<DepartamentoAcademico>) model.get("departamentos");
        Facultad facultad = (Facultad) model.get("facultad");
        List<Facultad> facultades = departamentos.stream().map(x -> x.getFacultad()).distinct().collect(Collectors.toList());
        if (facultad.getId() != null) {
            facultades.removeIf(x -> !x.equals(facultad));
        }

        int idxFac = 0;
        for (Facultad facultadEach : facultades) {
            idxFac++;
            boolean lastFacu = (idxFac == facultades.size());

            List<DepartamentoAcademico> departamentosByFacu = departamentos.stream().filter(x -> x.getFacultad().getId().compareTo(facultadEach.getId()) == 0)
                    .distinct().collect(Collectors.toList());

            DynatableFilter filter = new DynatableFilter();
            filter.setPage(1);
            filter.setOffset(0);
            filter.setPerPage(10000);
            List<Docente> docentes = service.allByDepartamentoDynatable(filter, departamentosByFacu, ciclo);
            Collections.sort(docentes, (x1, x2) -> x1.getPersona().getApellidosNombres().compareTo(x2.getPersona().getApellidosNombres()));
            int idxDep = 0;
            for (DepartamentoAcademico departamento : departamentosByFacu) {
                idxDep++;
                List<Docente> docentesByDepartamento = docentes.stream()
                        .filter(x -> x.getDepartamentoAcademico().equals(departamento))
                        .collect(Collectors.toList());

                this.builByDepartamento(document, departamento, contenidoCarta, docentesByDepartamento, ciclo);
                if (departamentosByFacu.size() != idxDep) {
                    document.newPage();
                }
            }
            if (!lastFacu) {
                document.newPage();
            }
        }

        String filename = "entrega-materiales";
        response.setHeader("Content-Disposition", "attachment; filename=\"" + filename + ".pdf\"");
        response.setHeader("Set-Cookie", "fileDownload=true; path=/");
    }

    public void builByDepartamento(Document document, DepartamentoAcademico departamentoAcademico,
            ContenidoCarta contenidoCarta,
            List<Docente> docentes,
            CicloAcademico cicloAcademico) throws Exception {
        PdfDocumentGenerator uDocumentoPdf = new PdfDocumentGenerator();

        Facultad facultad = departamentoAcademico.getFacultad();

        PdfPTable tableSubs = new PdfPTable(new float[]{1});
        tableSubs.getDefaultCell().setBorder(0);
        tableSubs.getDefaultCell().setPaddingTop(5);
        tableSubs.getDefaultCell().setPaddingBottom(5);
        tableSubs.setWidthPercentage(100);
        uDocumentoPdf.addBodyCellTable("Facultad " + facultad.getNombre(), tableSubs, 1, Element.ALIGN_LEFT, PdfDocumentGenerator.FUENTE_10_NEGRITA);
        uDocumentoPdf.addBodyCellTable("Departamento " + departamentoAcademico.getNombreLargo(), tableSubs, 1, Element.ALIGN_LEFT, PdfDocumentGenerator.FUENTE_10_NEGRITA);
        document.add(tableSubs);

        document.add(new Chunk(""));
        document.add(new Chunk(""));
        if (docentes.isEmpty()) {
            document.add(new Chunk("No hay docentes par la opción seleccionada"));
        }

        float[] columnWidths = new float[]{5f, 55f, 80f};

        PdfPTable table = new PdfPTable(columnWidths);
        table.setHeaderRows(1);
        table.getDefaultCell().setBorder(PdfPCell.RECTANGLE);
        table.setWidthPercentage(100);
        uDocumentoPdf.addTitleCellTable("Nº", table, 1, Element.ALIGN_CENTER);
        uDocumentoPdf.addTitleCellTable("APELLIDOS Y NOMBRES", table, 1, Element.ALIGN_CENTER);
        uDocumentoPdf.addTitleCellTable("FIRMA", table, 1, Element.ALIGN_CENTER);
        int ind = 0;
        for (Docente docente : docentes) {
//            PdfPCell celda = uDocumentoPdf.addBodyCellTable(++ind + "", table, 1, Element.ALIGN_LEFT);
//            celda.setFixedHeight(25f);
//            celda = uDocumentoPdf.addBodyCellTable(docente.getPersona().getApellidosNombres(), table, 1, Element.ALIGN_LEFT);
//            celda.setFixedHeight(25f);
//            celda = uDocumentoPdf.addBodyCellTable("", table, 1, Element.ALIGN_LEFT);
//            celda.setFixedHeight(25f);
            this.addBodyCellTable(++ind + "", table, Element.ALIGN_LEFT);
            this.addBodyCellTable(docente.getPersona().getApellidosNombres(), table, Element.ALIGN_LEFT);
            this.addBodyCellTable("", table, Element.ALIGN_LEFT);
        }

        document.add(table);

        table = new PdfPTable(new float[]{100f});
        table.getDefaultCell().setBorder(PdfPCell.NO_BORDER);
        table.setWidthPercentage(100);
        uDocumentoPdf.addBodyCellTable(" ", table, 1, Element.ALIGN_LEFT);
        String contenido = contenidoCarta.getContenido();

        List listHtmlContent = new ArrayList();
        StringReader strReader = new StringReader(contenido);
        listHtmlContent = HTMLWorker.parseToList(strReader, null);

        for (int k = 0; k < listHtmlContent.size(); ++k) {
            Paragraph paragraph = new Paragraph();
            paragraph.add((Element) listHtmlContent.get(k));

            PdfPCell celdaTablaReporte = new PdfPCell(paragraph);
            celdaTablaReporte.setBorder(table.getDefaultCell().getBorder());
            celdaTablaReporte.setVerticalAlignment(Element.ALIGN_MIDDLE);
            celdaTablaReporte.setHorizontalAlignment(Element.ALIGN_LEFT);
            celdaTablaReporte.setColspan(1);
            table.addCell(celdaTablaReporte);
        }

        //  uDocumentoPdf.addBodyCellTable(contenido, table, 1, Element.ALIGN_LEFT);
        document.add(table);
    }

    public PdfPCell addBodyCellTable(String strTituloCabecera, PdfPTable table, int align) {
        Paragraph parrafoCeldaReporte = new Paragraph(strTituloCabecera, PdfDocumentGenerator.FUENTE_8);
        PdfPCell celdaTablaReporte = new PdfPCell(parrafoCeldaReporte);
        celdaTablaReporte.setVerticalAlignment(Element.ALIGN_MIDDLE);
        celdaTablaReporte.setHorizontalAlignment(Element.ALIGN_CENTER);
        celdaTablaReporte.setFixedHeight(25f);
        celdaTablaReporte.setBorder(table.getDefaultCell().getBorder());

        celdaTablaReporte.setHorizontalAlignment(align);
        celdaTablaReporte.setColspan(1);
        table.addCell(celdaTablaReporte);

        return celdaTablaReporte;
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

}
