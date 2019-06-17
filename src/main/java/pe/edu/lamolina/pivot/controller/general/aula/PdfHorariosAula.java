package pe.edu.lamolina.pivot.controller.general.aula;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Font.FontFamily;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pe.edu.lamolina.model.academico.DocenteSeccion;
import pe.edu.lamolina.model.general.Aula;
import pe.edu.lamolina.model.general.Dia;
import pe.edu.lamolina.model.horario.Hora;
import pe.edu.lamolina.pivot.zelper.pdf.AbstractOnlyPdfView;

@Component
public class PdfHorariosAula extends AbstractOnlyPdfView {

    @Autowired
    AulaService service;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final String title = "Horarios Aula";
    private final int totalColumna = 7;

    @Override
    protected void buildPdfMetadata(Map<String, Object> model, Document document, HttpServletRequest request) {

        document.addAuthor("AlbatrossCloud");
        document.addCreationDate();
        document.addCreator("AlbatrossCloud");
        document.addTitle(this.title);
        document.addSubject("subject cualquiera");
        document.setPageSize(PageSize.A4.rotate());
        document.setMargins(36, 36, 130, 36);

    }

    @Override
    protected void buildPdfDocument(Map<String, Object> model, Document document, PdfWriter writer, HttpServletRequest request, HttpServletResponse response) throws Exception {

        Aula aula = (Aula) model.get("aula");
        Aula aulaSuperior = (Aula) model.get("aulaSuperior");
        List<Dia> dias = (List<Dia>) model.get("dias");
        List<Hora> horas = (List<Hora>) model.get("horas");

        PdfPTable table = new PdfPTable(totalColumna);

        this.documentHeader(writer, aula, aulaSuperior, dias, table);

        List<String> rows = new ArrayList();
        this.generateContent(rows, dias);

        this.documentBody(horas, document);
        document.newPage();

        String nombre = this.getUnTitle(aulaSuperior, aula);
        response.setHeader("Content-Disposition", "attachment; filename=\"" + nombre + ".pdf\"");
        response.setHeader("Set-Cookie", "fileDownload=true; path=/");
    }

    private String getUnTitle(Aula aulaSuperior, Aula aula) {
        String namedate = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        return this.title + " " + aulaSuperior.getNombre() + " " + aula.getCodigo() + " " + namedate;
    }

    private void documentHeader(PdfWriter writer, Aula aula, Aula aulaSuperior, List<Dia> dias, PdfPTable table) throws DocumentException {

//        PdfPTable table = new PdfPTable(7);
        table.setWidths(new int[]{1, 3, 3, 3, 3, 3, 3});
        table.setTotalWidth(770);
        table.setLockedWidth(true);

        Font fontHeader = new Font(Font.FontFamily.HELVETICA, 10, Font.NORMAL, BaseColor.BLACK);

        List<String> rows = new ArrayList();

        StringBuilder str = new StringBuilder();
        str.append(this.title);
        str.append("|").append(aulaSuperior.getNombre());
        rows.add(str.toString());

        str = new StringBuilder();
        str.append("Aula");
        str.append("|").append(aula.getCodigo());
        rows.add(str.toString());

        for (int i = 0; i < rows.size(); i++) {

            String fila = (String) rows.get(i);
            StringTokenizer st = new StringTokenizer(fila, "|");

            PdfPCell cell = new PdfPCell(new Phrase(st.nextToken(), fontHeader));
            cell.setColspan(2);
            cell.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell);

            cell = new PdfPCell(new Phrase(st.nextToken(), fontHeader));
            cell.setColspan(20);
            cell.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell);

        }

        Phrase phr = null;
        PdfPCell cell = null;

        logger.debug("******* cantidad dias {}", dias.size());

        phr = new Phrase("Hora", fontHeader);
        cell = new PdfPCell(phr);
        cell.setColspan(1);
        cell.setRowspan(2);
         cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(cell);

        for (Dia dia : dias) {
            phr = new Phrase(dia.getNombre(), fontHeader);
            cell = new PdfPCell(phr);
            cell.setColspan(1);
            cell.setRowspan(2);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);
        }

        HeaderTableDiario event = new HeaderTableDiario();
        event.setFontHeader(fontHeader);
        event.setTable(table);
        writer.setPageEvent(event);

    }

    private void documentBody(List<Hora> horas, Document document) throws DocumentException {
//        PdfPTable table1 = new PdfPTable(1);
//        table1.setWidthPercentage(100);
//        table1.addCell("HOLA");
//        document.add(table1);

        List<String> rows = new ArrayList();

        //    this.generateContent(rows, dias);
        PdfPTable table = new PdfPTable(7);
        table.setWidthPercentage(100);

        Font bodyText = new Font(FontFamily.HELVETICA, 12, Font.NORMAL, BaseColor.BLACK);

        for (Hora hora : horas) {
            PdfPCell cell = new PdfPCell(new Phrase(hora.getDescripcion(), bodyText));
            table.addCell(cell);
            for (Dia dia : hora.getDias()) {

                String firstLine = "z";
                cell = new PdfPCell(new Phrase(firstLine, bodyText));
                table.addCell(cell);

            }
            table.addCell(cell);

        }

//        Map<Integer, Integer> indxMerge = new LinkedHashMap<>();
//        indxMerge.put(2, 2);
//        indxMerge.put(4, 4);
//        for (int i = 0; i < rows.size(); i++) {
//
//            String fila = (String) rows.get(i);
//            StringTokenizer st = new StringTokenizer(fila, "|");
//
//            int j = 0;
//
//            while (st.hasMoreTokens()) {
//
//                String token = st.nextToken();
//                PdfPCell cell = new PdfPCell(new Phrase(token, bodyText));
//                cell.setColspan(7);
//                table.addCell(cell);
//
//            }
//        }
        table.addCell("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
        document.add(table);
        document.add(new Chunk(".333333333333333", new Font(FontFamily.COURIER, 10, Font.NORMAL, BaseColor.BLACK)));
    }

    private void generateContent(List<String> rows, List<Dia> dias) {

        logger.debug("******** detro del generateContent dias cantidad {}", dias.size());

        StringBuilder sb = new StringBuilder();
        sb.append("TEST");
        sb.append("-");
        sb.append("testing");
        rows.add(sb.toString());

    }

}
