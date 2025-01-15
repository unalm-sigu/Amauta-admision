package pe.edu.lamolina.amauta.controller.academico.profesor.view;

import com.itextpdf.text.BadElementException;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfWriter;
import java.awt.Desktop;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import pe.albatross.zelpers.file.pdf.AbstractOnlyPdfView;
import pe.edu.lamolina.amauta.controller.academico.profesor.HistoricoCargaAcademicoBean;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.Phaser;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import pe.edu.lamolina.amauta.controller.academico.profesor.ProfesorService;
import static pe.edu.lamolina.model.constantines.GlobalConstantine.PDF_LOGO_UNALM;
import pe.edu.lamolina.model.enums.oficina.OficinaEnum;
import pe.edu.lamolina.model.general.Oficina;
import pe.edu.lamolina.model.zelper.pdfgenerator.FooterTypeEnum;
import pe.edu.lamolina.model.zelper.pdfgenerator.HeaderTypeEnum;
import pe.edu.lamolina.model.zelper.pdfgenerator.UEventoPaginaPdf;

@Component
public class ReporteHistoricoCargaAcademicoView extends AbstractOnlyPdfView {

    private final String TITULO = "UNIVERSIDAD AGRARIA LA MOLINA";
    private final String SUB_TITULO = "DIRECCIÓN DE ESTUDIOS Y REGISTROS ACADÉMICOS";
    private final String SUB_TITULO_2 = "Historico de carga académica de créditos pre grado";

    @Autowired
    ProfesorService service;

    @Override
    protected void renderMergedOutputModel(Map<String, Object> model, HttpServletRequest request, HttpServletResponse response) throws Exception {

        List<HistoricoCargaAcademicoBean> historicos = (List<HistoricoCargaAcademicoBean>) model.get("historicos");

        // IE workaround: write into byte array first.
        ByteArrayOutputStream baos = createTemporaryOutputStream();

        // Apply preferences and build metadata.
        Document document = new Document(PageSize.A4.rotate());

        File tempFile = new File("TablaConFiltros.pdf");
        PdfWriter writer = PdfWriter.getInstance(document, baos);
//        prepareWriter(model, writer, request);
//        buildPdfMetadata(model, document, request);

        // Crear un nuevo evento de página para manejar los pies de página
        FooterPageEvent footer = new FooterPageEvent();

        // Establecer el evento de página en el escritor
        writer.setPageEvent(footer);

        // Build PDF document.
//        writer.setInitialLeading(100);
        document.open();
        this.headerPDF(document, writer);
        this.buildPdfDocument(model, document, writer, request, response);


        String filename = "historico-carga-academica";
        response.setHeader("Content-Disposition", "attachment; filename=\"" + filename + ".pdf\"");
        response.setHeader("Set-Cookie", "fileDownload=true; path=/");
        document.close();

        // Flush to HTTP response.
        writeToResponse(response, baos);

        try {
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().open(tempFile);
            } else {
                System.out.println("Abrir archivos no está soportado en este sistema.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void buildPdfMetadata(Map<String, Object> model, Document document, HttpServletRequest request) {
        document.addAuthor("La Molina");
        document.addCreationDate();
        document.addCreator("Amauta");
        document.addTitle("Reporte Historico Carga Académica");
        document.addSubject("");
        document.setPageSize(PageSize.A4.rotate());
    }

    protected void headerPDF(Document document, PdfWriter writer) throws DocumentException, BadElementException, IOException {

        float[] columnWidths = new float[]{20f, 65f, 20f};
        PdfPTable tabla = new PdfPTable(columnWidths); // Tabla de una sola columna
        tabla.setWidths(columnWidths);       // Ancho de la tabla al 50% de la página

        Image img = Image.getInstance(this.getClass().getResource(PDF_LOGO_UNALM));
        img.scalePercent(40F);

        PdfPCell cell = new PdfPCell(img);
        cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
        cell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
        cell.setPaddingBottom(5f);
        cell.setPaddingTop(5f);
        cell.setBorder(PdfPCell.NO_BORDER);
        tabla.addCell(cell);

        Font font = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
        Font fontSubtitulo = new Font(Font.FontFamily.HELVETICA, 10, Font.NORMAL);
        Paragraph parrafo = new Paragraph();
        parrafo.setFont(font);
        parrafo.add(this.TITULO);
        parrafo.add(Chunk.NEWLINE);
        parrafo.add(Chunk.NEWLINE);
        parrafo.add(this.SUB_TITULO);

        Paragraph parrafosub = new Paragraph();
        parrafosub.setFont(fontSubtitulo);
        parrafo.add(Chunk.NEWLINE);
        parrafo.add(Chunk.NEWLINE);
        parrafo.add(this.SUB_TITULO_2);

        PdfPCell cell2 = new PdfPCell(parrafo);
        cell2.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
        cell2.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
        cell2.setBorder(PdfPCell.NO_BORDER);
        tabla.addCell(cell2);

        PdfPCell cell3 = new PdfPCell();
        cell3.setBorder(PdfPCell.NO_BORDER);
        tabla.addCell(cell3);

        // Agregar la tabla al documento
        document.add(tabla);

        Font fontFecha = new Font(Font.FontFamily.HELVETICA, 8, Font.NORMAL);

        Paragraph fecha = new Paragraph();
        fecha.setFont(fontFecha);
        fecha.setAlignment(Element.ALIGN_RIGHT);
        fecha.add(this.getFecha());
        document.add(fecha);

    }

    @Override
    protected void buildPdfDocument(Map<String, Object> model, Document document, PdfWriter writer, HttpServletRequest request,
            HttpServletResponse response) throws Exception {

        List<HistoricoCargaAcademicoBean> historicos = (List<HistoricoCargaAcademicoBean>) model.get("historicos");
        List<String> ciclosTotal = historicos.stream().map(x -> x.getCiclo()).distinct().collect(Collectors.toList());
        List<String> ciclos = ciclosTotal.stream()
                .sorted((c1, c2) -> {
                    // Extraer año y semestre de cada string
                    int año1 = Integer.parseInt(c1.split("-")[0]);
                    int semestre1 = c1.split("-")[1].equals("I") ? 2 : 1; // Invertir: II = 1, I = 2

                    int año2 = Integer.parseInt(c2.split("-")[0]);
                    int semestre2 = c2.split("-")[1].equals("I") ? 2 : 1; // Invertir: II = 1, I = 2

                    // Primero comparamos por año (en orden descendente)
                    if (año1 != año2) {
                        return Integer.compare(año2, año1); // Orden descendente por año
                    }

                    // Si los años son iguales, comparamos por semestre (II antes de I)
                    return Integer.compare(semestre1, semestre2);
                })
                .collect(Collectors.toList());

        Map<String, List<String>> mapDptosListDocente = historicos.stream()
                .collect(Collectors.groupingBy(
                        HistoricoCargaAcademicoBean::getDepartamento,
                        Collectors.mapping(HistoricoCargaAcademicoBean::getNombreDocente, Collectors.toSet())
                ))
                .entrySet()
                .stream()
                // Ordena por clave del departamento
                .sorted(Map.Entry.comparingByKey())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        // Convierte el Set a una lista ordenada de docentes
                        e -> e.getValue().stream()
                                .sorted() // Ordena los nombres de los docentes alfabéticamente
                                .collect(Collectors.toList()),
                        (oldValue, newValue) -> oldValue, // En caso de colisión (no debe ocurrir), conserva el valor antiguo
                        LinkedHashMap::new // Asegura el orden de inserción
                ));

        Map<String, String> mapCreditoByCicloDocente = historicos.stream().
                collect(Collectors.toMap(
                        x -> x.getCiclo() + "-" + x.getNombreDocente(),
                        HistoricoCargaAcademicoBean::getCreditosPre)
                );

        Font fontTitulo = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD);
        Font fontceldaTabla = new Font(Font.FontFamily.HELVETICA, 10, Font.NORMAL);

        mapDptosListDocente.forEach((departamento, docentes) -> {

            Paragraph textoDPTO = new Paragraph();
            textoDPTO.setFont(fontTitulo);
            textoDPTO.add("DPTO ACADEMICO " + departamento.toUpperCase());

            try {
                document.add(textoDPTO);
                document.add(new Paragraph(new Chunk(Chunk.NEWLINE)));

                int docenteCol = 1;
                int numColumnas = docenteCol + ciclos.size();

                PdfPTable tabla = new PdfPTable(numColumnas);
                tabla.setWidthPercentage(100);  // Ancho de la tabla al 100% de la página

                float[] anchosColumnas = new float[numColumnas];
                anchosColumnas[0] = 80; // Ancho mayor para la primera columna
                for (int i = 1; i < numColumnas; i++) {
                    anchosColumnas[i] = 8; // Anchos iguales para las demás columnas
                }
                tabla.setWidths(anchosColumnas);

                // Encabezados de columna
                PdfPCell celdaDocente = new PdfPCell(new Paragraph("NOMBRE DEL PROFESOR", fontceldaTabla));
                celdaDocente.setBackgroundColor(new BaseColor(238, 238, 238));
                celdaDocente.setRowspan(2); // Fusionar la celda en las 3 columnas
                celdaDocente.setHorizontalAlignment(Element.ALIGN_LEFT); // Centrar el texto dentro de la celda
                celdaDocente.setVerticalAlignment(Element.ALIGN_MIDDLE);
//        celdaDocente.setPadding(20);

                tabla.addCell(celdaDocente);

                ciclos.stream().forEach(ciclo -> {
                    PdfPCell cellCiclo = new PdfPCell(new Paragraph(ciclo, fontceldaTabla));
                    cellCiclo.setBackgroundColor(new BaseColor(238, 238, 238));
                    cellCiclo.setHorizontalAlignment(Element.ALIGN_CENTER); // Centrar el texto dentro de la celda
                    cellCiclo.setVerticalAlignment(Element.ALIGN_MIDDLE);
                    tabla.addCell(cellCiclo);

                });

                ciclos.stream().forEach(ciclo -> {
                    PdfPCell cellCred = new PdfPCell(new Paragraph("Cred.", fontceldaTabla));
                    cellCred.setBackgroundColor(new BaseColor(238, 238, 238));
                    cellCred.setHorizontalAlignment(Element.ALIGN_CENTER); // Centrar el texto dentro de la celda
                    cellCred.setVerticalAlignment(Element.ALIGN_MIDDLE);
                    tabla.addCell(cellCred);

                });

                docentes.stream().forEach(docente -> {

                    PdfPCell cellDocente = new PdfPCell(new Paragraph(docente, fontceldaTabla));
                    cellDocente.setHorizontalAlignment(Element.ALIGN_LEFT); // Centrar el texto dentro de la celda
                    cellDocente.setVerticalAlignment(Element.ALIGN_MIDDLE);
                    tabla.addCell(cellDocente);

                    ciclos.stream().forEach(ciclo -> {
                        String creditoCiclo = mapCreditoByCicloDocente.get(ciclo + "-" + docente);
                        PdfPCell cellCred = new PdfPCell(new Paragraph(creditoCiclo, fontceldaTabla));
                        cellCred.setHorizontalAlignment(Element.ALIGN_CENTER); // Centrar el texto dentro de la celda
                        cellCred.setVerticalAlignment(Element.ALIGN_MIDDLE);
                        tabla.addCell(cellCred);
                    });

                });

                document.add(tabla);
                document.newPage();
            } catch (DocumentException ex) {
                Logger.getLogger(ReporteHistoricoCargaAcademicoView.class.getName()).log(Level.SEVERE, null, ex);
            }

        });
    }

    class FooterPageEvent extends PdfPageEventHelper {

        private int totalPages;  // Variable para almacenar el número total de páginas

        // Este método se ejecuta al final de cada página
        @Override
        public void onEndPage(PdfWriter writer, Document document) {
            // Obtener el número de página actual
            int pageNumber = writer.getPageNumber();

            // Crear un objeto Font para el pie de página (puedes personalizar el estilo aquí)
            Font font = new Font(Font.FontFamily.HELVETICA, 8, Font.NORMAL);

            // Establecer el texto del pie de página (por ahora solo con el número de página actual)
            String footerText = "pág. " + pageNumber;  // XX será reemplazado por el total de páginas

            // Centrar el pie de página en la parte inferior de la página
            float x = (document.getPageSize().getWidth() - 100) / 2; // Centrando el pie de página horizontalmente
            float y = document.bottom(); // Posición vertical del pie de página (a 30 puntos desde el borde inferior)

            // Posicionar el pie de página en la parte inferior central de la página
            ColumnText.showTextAligned(writer.getDirectContent(), Element.ALIGN_CENTER,
                    new Phrase(footerText, font), x, y, 0);
        }

    }

    private String getFecha() {
        SimpleDateFormat sdf = new SimpleDateFormat("'La Molina,' d 'de' MMMM yyyy", Locale.forLanguageTag("es"));
        Date date = new Date();
        return sdf.format(date);
    }
}
