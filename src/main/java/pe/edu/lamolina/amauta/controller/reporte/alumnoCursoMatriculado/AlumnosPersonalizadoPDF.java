package pe.edu.lamolina.amauta.controller.reporte.alumnoCursoMatriculado;

import com.google.common.base.Strings;
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
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import pe.albatross.zelpers.file.pdf.AbstractOnlyPdfView;
import static pe.edu.lamolina.model.constantines.GlobalConstantine.PDF_LOGO_UNALM;

@Component
public class AlumnosPersonalizadoPDF extends AbstractOnlyPdfView {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final String SUBJT_PREGRADO = "DIRECCIÓN DE ESTUDIOS Y REGISTROS ACADÉMICOS";
    private final String title = "REPORTE DE ALUMNOS MATRICULADOS AMNISTÍA 2025-I";

    @Override
    protected void buildPdfMetadata(Map<String, Object> model, Document document, HttpServletRequest request) {

        document.addAuthor("Rogelio Orihuela C.");
        document.addCreationDate();
        document.addCreator("Rogelio Orihuela C.");
        document.addTitle(this.title);
        document.addSubject(this.title);
        document.setPageSize(PageSize.A4);
        document.setMargins(10, 10, 30, 30);

    }

    @Override
    protected void buildPdfDocument(Map<String, Object> model, Document document, PdfWriter writer, HttpServletRequest request, HttpServletResponse response) throws Exception {

        List<AlumnoPersonalizadoDTO> lista = (List<AlumnoPersonalizadoDTO>) model.get("alumnoDTO");

        buildHeaderPaginaPrincipal(document);
        createBody(lista, document);
        this.onEndPage(writer, document);
        document.newPage();
        DateTime today = new DateTime();

        String nombre = "Alumno Matriculados imnastiado" + lista.get(0).getCiclo() + "_" + today.toString("yyyyMMdd_HHmm");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + nombre + ".pdf\"");
        response.setHeader("Set-Cookie", "fileDownload=true; path=/");
    }

    private void buildHeaderPaginaPrincipal(Document document) throws DocumentException, DocumentException, BadElementException, IOException {

        Font font = new Font(Font.FontFamily.HELVETICA, 10, Font.NORMAL, BaseColor.BLACK);
        PdfPTable tablePdf;
        PdfPCell cell;
        float[] columnWidths = null;
        columnWidths = new float[]{20f, 80f};
        tablePdf = new PdfPTable(columnWidths);
        tablePdf.setWidths(columnWidths);
        tablePdf.setHorizontalAlignment(PdfPTable.ALIGN_CENTER);
        tablePdf.setSpacingAfter(0);
        tablePdf.setSpacingBefore(0);
        tablePdf.setPaddingTop(0);

        Image img = Image.getInstance(this.getClass().getResource(PDF_LOGO_UNALM));
        img.scalePercent(40F);
        cell = new PdfPCell(img);
        cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
        cell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
        cell.setPaddingBottom(0f);
        cell.setPaddingTop(0f);
        cell.setRowspan(4);
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setBorderColor(BaseColor.WHITE);

        tablePdf.addCell(cell);

        Paragraph parrafo = new Paragraph();
        parrafo.setFont(new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD));
        parrafo.add(this.title);
        parrafo.add(Chunk.NEWLINE);
        parrafo.add(Chunk.NEWLINE);
        parrafo.setFont(new Font(Font.FontFamily.HELVETICA, 10, Font.NORMAL));
        parrafo.add(this.SUBJT_PREGRADO);

        cell = new PdfPCell(parrafo);
        cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
        cell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
        cell.setRowspan(4);
        cell.setBorder(0);
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setBorderColor(BaseColor.WHITE);

        tablePdf.addCell(cell);

        document.add(tablePdf);

        document.add(new Chunk(".", new Font(Font.FontFamily.COURIER, 1, Font.NORMAL, BaseColor.WHITE)));
    }

    private void tituloData(int posision, String texto, int colspan, PdfPTable tableBody, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(texto, font));
        cell.setBorder(PdfPCell.BOTTOM);
        cell.setHorizontalAlignment(posision);
        cell.setColspan(colspan);
        cell.setPaddingLeft(0f);
        cell.setPaddingRight(0f);
        cell.setPaddingTop(0f);
        cell.setBottom(PdfPCell.BOTTOM);
        cell.setPaddingBottom(4f);
        cell.setPaddingTop(4f);
        tableBody.addCell(cell);
    }

    private void createBody(List<AlumnoPersonalizadoDTO> lista, Document document) throws DocumentException, Exception {
        Font font = new Font(Font.FontFamily.HELVETICA, 10, Font.NORMAL, BaseColor.BLACK);
        Font fontAnexo = new Font(Font.FontFamily.HELVETICA, 8, Font.BOLD, BaseColor.BLACK);

        Paragraph paragraph = new Paragraph();
        paragraph.setFont(getTitle());
        paragraph.add("");
        paragraph.setAlignment(Element.ALIGN_CENTER);
        document.add(paragraph);

        PdfPTable table = new PdfPTable(2);
        float[] medidaCeldas = {1.55f, 8f};// tamañan por columna

        PdfPTable tableTitulo = new PdfPTable(6);
        tableTitulo.setHorizontalAlignment(PdfPTable.ALIGN_CENTER);
        tableTitulo.setTotalWidth(220);
        tableTitulo.setLockedWidth(true);

        int i = 1;
        tableTitulo.setSpacingBefore(2f);
        tableTitulo.setSpacingAfter(2f);
        document.add(tableTitulo);
        document.add(new Chunk(".", new Font(Font.FontFamily.COURIER, 1, Font.NORMAL, BaseColor.WHITE)));

        table.setWidths(medidaCeldas);
        table.setWidthPercentage(80.00f);//todo el ancho de la pagina
        table.setSpacingBefore(2f);
        table.setSpacingAfter(2f);
        table.addCell(getStyleTitulo("FOTO", 8, "C"));
//        table.addCell(getStyleTitulo("CÓDIGO", 8, "C"));
        table.addCell(getStyleTitulo("ESTUDIANTE", 8, "C"));
        table.setHeaderRows(1);

        for (AlumnoPersonalizadoDTO data : lista) {
            String url = "";
            if (!Strings.isNullOrEmpty(data.getFoto())) {
                url = data.getFoto();
                table.addCell(getImagenUrl(25, 20, url));
            } else {
                table.addCell(getImagenUrl(25, 20, url));
            }
//            addCelda(data.getMatricula(), "C", table, fontAnexo, 1);
            StringBuilder st = new StringBuilder();
            st.append(" Alumno: " + data.getAlumno() + "\n");
            st.append(" \n");
            st.append(" Dni: " + data.getDni() + "\n");
            st.append(" \n");
            st.append(" Matricula: " + data.getMatricula() + "\n");
            st.append(" \n");
            st.append(" Carrera: " + data.getCarrera() + "\n");
            st.append(" \n");
            st.append(" Situación Académica: " + data.getSituacion() + "\n");
            st.append(" \n");
            addCelda(st.toString(), "L", table, fontAnexo, 1);
        }
//        }
        document.add(table);

    }

    private PdfPCell getImagenUrl(int scaleWidth, int scaleHeight, String url) throws Exception {

        TrustManager[] trustAllCerts = new TrustManager[]{
            new X509TrustManager() {
                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                    return null;
                }

                public void checkClientTrusted(
                        java.security.cert.X509Certificate[] certs, String authType) {
                }

                public void checkServerTrusted(
                        java.security.cert.X509Certificate[] certs, String authType) {
                }
            }
        };

        try {
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        } catch (Exception e) {
            e.printStackTrace();
            logger.debug("error 1");
        }

        PdfPCell pdfPCell = new PdfPCell();
        if (!url.equals("")) {
            Image image = Image.getInstance(new URL(url));
            image.scaleToFit(scaleWidth, scaleHeight);
            pdfPCell.setImage(image);
        }

        pdfPCell.setMinimumHeight(70);
        return pdfPCell;
    }

    public Font getTitle() {
        Font font = new Font();
        font.setFamily("TimesRoman");
        font.setSize(12);
        font.setColor(BaseColor.BLACK);
        font.setStyle(Font.BOLD);
        font.setStyle(Font.UNDERLINE);
        return font;
    }

    private void addCelda(String contenido, String posicion, PdfPTable table, Font bodyFont, int rolSpan) {
        Phrase phr = new Phrase(contenido, bodyFont);
        PdfPCell cell = new PdfPCell(phr);

        if (posicion.equals("C")) {
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        } else if (posicion.equals("L")) {
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        }
        cell.setVerticalAlignment(Element.ALIGN_CENTER);
        cell.setMinimumHeight(75);
        table.addCell(cell);
    }

    public PdfPCell getStyleTitulo(String data, float numero, String posicion) {
        Font font = new Font();
        font.setSize(numero);
        font.setColor(BaseColor.WHITE);
        font.setStyle(Font.BOLD);

        PdfPCell cell = new PdfPCell(new Paragraph(data, font));
        cell.setMinimumHeight(numero);
        cell.setBackgroundColor(BaseColor.DARK_GRAY);
        cell.getEffectivePaddingRight();
        if (posicion.equals("C")) {
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        }
        if (posicion.equals("R")) {
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        }
        if (posicion.equals("L")) {
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        }

        return cell;
    }

    public void onEndPage(PdfWriter writer, Document document) {
        // Obtener el área de la página
        Rectangle pageSize = document.getPageSize();

        // Texto del pie de página
        Phrase footer = new Phrase(
                "© DERA - Rogelio Orihuela C.",
                new Font(Font.FontFamily.HELVETICA, 5, Font.NORMAL)
        );

        // Posicionar el texto en la parte inferior centrada
        ColumnText.showTextAligned(
                writer.getDirectContent(),
                Element.ALIGN_CENTER,
                footer,
                pageSize.getWidth() / 2,
                pageSize.getBottom(30), // Ajusta el margen inferior (30 unidades)
                0
        );

    }

}
