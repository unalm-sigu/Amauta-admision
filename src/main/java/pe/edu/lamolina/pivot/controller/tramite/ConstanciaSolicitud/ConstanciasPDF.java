package pe.edu.lamolina.pivot.controller.tramite.ConstanciaSolicitud;

import pe.edu.lamolina.pivot.controller.programacionhorarios.gposeccion.reporte.*;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPCellEvent;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import pe.edu.lamolina.model.academico.AlumnoCiclo;
import pe.edu.lamolina.model.academico.AlumnoCicloCurso;
import pe.edu.lamolina.model.misc.Acumulador;
import pe.edu.lamolina.model.tramite.TramiteDocumentoAcademico;
import pe.edu.lamolina.pivot.zelper.pdf.AbstractOnlyPdfView;

@Component
public class ConstanciasPDF extends AbstractOnlyPdfView {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    protected void buildPdfMetadata(Map<String, Object> model, Document document, HttpServletRequest request) {
        Rectangle pageSize = new Rectangle(793.7007874f, 1145.1968504f); //ancho y alto
        document.setPageSize(pageSize);
        document.addAuthor("AlbatrossCloud");
        document.addCreationDate();
        document.addCreator("AlbatrossCloud");
        document.setMargins(5, 5, 5, 5);

    }

    @Override
    protected void buildPdfDocument(Map<String, Object> model, Document document, PdfWriter writer, HttpServletRequest request, HttpServletResponse response) throws Exception {

        TramiteDocumentoAcademico documentoAcademico = (TramiteDocumentoAcademico) model.get("documentoAcademico");
        List<AlumnoCiclo> alumnoCiclo = (List<AlumnoCiclo>) model.get("alumnoCiclo");

        Acumulador acumulador = new Acumulador(4);

        createAnexos(alumnoCiclo, acumulador, document, null);

//        document.newPage();
        DateTime today = new DateTime();

        String nombre = documentoAcademico.getTipoDocumentoAcademico().getNombre() + "_" + today.toString("yyyyMMdd_HHmm");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + nombre + ".pdf\"");
        response.setHeader("Set-Cookie", "fileDownload=true; path=/");
    }

    private void createAnexos(List<AlumnoCiclo> alumnoCiclos, Acumulador acumulador, Document document, HeaderFooterBoletinPDFEvent event) throws DocumentException {

        int index = 1;
        for (AlumnoCiclo alumnoCiclo : alumnoCiclos) {
            index = index + alumnoCiclo.getAlumnoCicloCurso().size();
            if (index <= 33) {
                alumnoCiclo.setNumeroOrden(1);
            } else if (index > 33 && index < 66) {
                alumnoCiclo.setNumeroOrden(2);
            } else if (index > 66 && index < 103) {
                alumnoCiclo.setNumeroOrden(3);
            } else if (index > 103) {
                alumnoCiclo.setNumeroOrden(4);
            }
        }
        PdfPCell cell;

        PdfPTable outertable = new PdfPTable(1);
        outertable.setTotalWidth(760F);
        outertable.setLockedWidth(true);
        outertable.getDefaultCell().setBorder(0);
        // inner table 1
        PdfPTable innertable = new PdfPTable(5);
        innertable.setWidths(new int[]{8, 12, 1, 4, 12});
        // first row
        // column 1
        cell = new PdfPCell(new Phrase("Record Ref:"));
        cell.setBorder(Rectangle.NO_BORDER);
        innertable.addCell(cell);
        // column 2
        cell = new PdfPCell(new Phrase("GN Staff"));
        cell.setPaddingLeft(2);
        innertable.addCell(cell);
        // column 3
        cell = new PdfPCell();
        cell.setBorder(Rectangle.NO_BORDER);
        innertable.addCell(cell);
        // column 4
        cell = new PdfPCell(new Phrase("Date: "));
        cell.setBorder(Rectangle.NO_BORDER);
        innertable.addCell(cell);
        // column 5
        cell = new PdfPCell(new Phrase("30/4/2015"));
        cell.setPaddingLeft(2);
        innertable.addCell(cell);
        // spacing
        cell = new PdfPCell();
        cell.setColspan(5);
        cell.setFixedHeight(3);
        cell.setBorder(Rectangle.NO_BORDER);
        innertable.addCell(cell);
        // second row
        // column 1
        cell = new PdfPCell(new Phrase("Hospital:"));
        cell.setBorder(Rectangle.NO_BORDER);
        innertable.addCell(cell);
        // column 2
        cell = new PdfPCell(new Phrase("Derby Royal"));
        cell.setPaddingLeft(2);
        innertable.addCell(cell);
        // column 3
        cell = new PdfPCell();
        cell.setBorder(Rectangle.NO_BORDER);
        innertable.addCell(cell);
        // column 4
        cell = new PdfPCell(new Phrase("Ward: "));
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setPaddingLeft(5);
        innertable.addCell(cell);
        // column 5
        cell = new PdfPCell(new Phrase("21"));
        cell.setPaddingLeft(2);
        innertable.addCell(cell);
        // spacing
        cell = new PdfPCell();
        cell.setColspan(5);
        cell.setFixedHeight(3);
        cell.setBorder(Rectangle.NO_BORDER);
        innertable.addCell(cell);
        // first nested table
        cell = new PdfPCell(innertable);
//        cell.setCellEvent(roundRectangle);
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setPadding(8);
        outertable.addCell(cell);

        // inner table 2
        innertable = new PdfPTable(4);
        innertable.setWidths(new int[]{3, 17, 1, 16});
        // first row
        // column 1
        cell = new PdfPCell();
        cell.setBorder(Rectangle.NO_BORDER);
        innertable.addCell(cell);
        // column 2
        cell = new PdfPCell(new Phrase("Name"));
        cell.setBorder(Rectangle.NO_BORDER);
        innertable.addCell(cell);
        // column 3
        cell = new PdfPCell();
        cell.setBorder(Rectangle.NO_BORDER);
        innertable.addCell(cell);
        // column 4
        cell = new PdfPCell(new Phrase("Signature: "));
        cell.setBorder(Rectangle.NO_BORDER);
        innertable.addCell(cell);
        // spacing
        cell = new PdfPCell();
        cell.setColspan(4);
        cell.setFixedHeight(3);
        cell.setBorder(Rectangle.NO_BORDER);
        innertable.addCell(cell);
        // subsequent rows
        for (int i = 1; i < 4; i++) {
            // column 1
            cell = new PdfPCell(new Phrase(String.format("%s:", i)));
            cell.setBorder(Rectangle.NO_BORDER);
            innertable.addCell(cell);
            // column 2
            cell = new PdfPCell();
            innertable.addCell(cell);
            // column 3
            cell = new PdfPCell();
            cell.setBorder(Rectangle.NO_BORDER);
            innertable.addCell(cell);
            // column 4
            cell = new PdfPCell();
            innertable.addCell(cell);
            // spacing
            cell = new PdfPCell();
            cell.setColspan(4);
            cell.setFixedHeight(3);
            cell.setBorder(Rectangle.NO_BORDER);
            innertable.addCell(cell);
        }
        // second nested table
        cell = new PdfPCell(innertable);
//        cell.setCellEvent(roundRectangle);
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setPadding(8);
        outertable.addCell(cell);
        // add the table

        float[] columnWidths = {1, 1};
        innertable = new PdfPTable(2);
        innertable.setWidths(columnWidths);
        addTablePage(innertable, alumnoCiclos, 1, document);

        cell = new PdfPCell(innertable);
        cell.setBorder(Rectangle.NO_BORDER);

        cell.setPadding(8);
        outertable.addCell(cell);

        document.add(outertable);

//        document.add(new Chunk());
    }

    private void addTablePage(PdfPTable pageTable, List<AlumnoCiclo> alumnoCiclos, int colum, Document document) throws DocumentException {
        Font fontCiclo = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD, BaseColor.BLACK);

        PdfPTable ladoTableFull = new PdfPTable(1);
        Font font = new Font(Font.FontFamily.HELVETICA, 10, Font.NORMAL, BaseColor.BLACK);
        if (colum % 2 == 0) {
            if (colum > 2) {
                return;
            }
            document.newPage();
        }
//        ladoTableFull.getDefaultCell().setBorder(0);
        for (AlumnoCiclo alumnoCiclo : alumnoCiclos) {

            if (alumnoCiclo.getNumeroOrden() == colum) {

                PdfPTable cicloTable = new PdfPTable(3);
                cicloTable.getDefaultCell().setBorder(0);
                cicloTable.setWidths(new int[]{9, 1, 1});

                System.out.println(alumnoCiclo.getCicloAcademico().getDescripcion2() + "");

                Phrase phr = new Phrase(alumnoCiclo.getCicloAcademico().getDescripcion2(), fontCiclo);
                PdfPCell cell = new PdfPCell();
                cell.setBorder(Rectangle.NO_BORDER);
                cell.setColspan(3);
                cell.setPadding(5f);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setPhrase(phr);
                cicloTable.addCell(cell);
                for (AlumnoCicloCurso alumnoCicloCurso : alumnoCiclo.getAlumnoCicloCurso()) {
                    phr = new Phrase(alumnoCicloCurso.getCurso().getNombre(), font);
                    cell = new PdfPCell();
                    cell.setBorder(Rectangle.NO_BORDER);
                    cell.setPhrase(phr);
                    cicloTable.addCell(cell);
                    phr = new Phrase(alumnoCicloCurso.getNota(), font);
                    cell = new PdfPCell();
                    cell.setBorder(Rectangle.NO_BORDER);
                    cell.setPhrase(phr);
                    cicloTable.addCell(cell);
                    phr = new Phrase(alumnoCicloCurso.getCreditos() + "", font);
                    cell = new PdfPCell();
//                    cell.setBorder(Rectangle.NO_BORDER);
                    cell.setPhrase(phr);
                    cicloTable.addCell(cell);
                    System.out.println(alumnoCicloCurso.getCurso().getNombre() + "");
                }

                ladoTableFull.addCell(cicloTable);
//                PdfPCell cellPadding = new PdfPCell();
//                cellPadding.setColspan(3);
//                cellPadding.setFixedHeight(6);
////                cell.setBorder(Rectangle.NO_BORDER);
//                innerTableFull.addCell(cellPadding);
            } 
        }
        pageTable.addCell(ladoTableFull);

        colum = colum + 1;
        addTablePage(pageTable, alumnoCiclos, colum, document);

    }

}
