package pe.edu.lamolina.amauta.controller.academico.profesor.view;

import com.itextpdf.text.BadElementException;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.HeaderFooter;
import static pe.edu.lamolina.model.constantines.GlobalConstantine.PDF_LOGO_UNALM;

public class HeaderReportePDF1 extends PdfPageEventHelper {

    private String titulo1;
    private String titulo2;
    private String codigo;

    public HeaderReportePDF1() {
    }

    public HeaderReportePDF1(String titulo1) {
        this.titulo1 = titulo1;
    }

    public HeaderReportePDF1(String titulo1, String titulo2) {
        this.titulo1 = titulo1;
        this.titulo2 = titulo2;
    }

    @Override
    public void onEndPage(PdfWriter writer, Document document) {

        PdfContentByte cb = writer.getDirectContent();

        try {

            PdfPTable tableHeader;
            PdfPCell cell;
            float[] columnWidths = new float[]{20f, 65f, 20f};
            tableHeader = new PdfPTable(columnWidths);

            tableHeader.setWidths(columnWidths);
            tableHeader.setHorizontalAlignment(PdfPTable.ALIGN_CENTER);

            Image img = Image.getInstance(this.getClass().getResource(PDF_LOGO_UNALM));
            img.scalePercent(40F);

            cell = new PdfPCell(img);
            cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
            cell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
            cell.setPaddingBottom(5f);
            cell.setPaddingTop(5f);
            cell.setRowspan(4);
            cell.setBorder(PdfPCell.NO_BORDER);
            tableHeader.addCell(cell);

            Font font = new Font(Font.FontFamily.HELVETICA, 9, Font.BOLD);

            Paragraph parrafo = new Paragraph();
            parrafo.setFont(font);
            parrafo.add(this.titulo1);
            if (!StringUtils.isBlank(this.titulo2)) {
                parrafo.add(Chunk.NEWLINE);
                parrafo.add(Chunk.NEWLINE);
                parrafo.add(this.titulo2);
            }

            cell = new PdfPCell(parrafo);
            cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
            cell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
            cell.setRowspan(4);
            cell.setBorder(PdfPCell.NO_BORDER);
            tableHeader.addCell(cell);

            //Image image = Image.getInstance(this.getClass().getResource(PDF_LOGO_EPG_ALFA));
            //image.scalePercent(20F);
            cell = new PdfPCell();
            cell.setPaddingBottom(5f);
            cell.setPaddingTop(5f);
            cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
            cell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
            cell.setBorder(PdfPCell.TOP | PdfPCell.RIGHT);
            cell.setRowspan(3);
            cell.setBorder(PdfPCell.NO_BORDER);
            tableHeader.addCell(cell);

            font = new Font(Font.FontFamily.HELVETICA, 9, Font.BOLD);
            cell = new PdfPCell(new Phrase(this.codigo, font));
            cell.setBorder(PdfPCell.BOTTOM | PdfPCell.RIGHT);
            cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
            cell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
            cell.setRowspan(1);
            cell.setBorder(PdfPCell.NO_BORDER);
            tableHeader.addCell(cell);

            ColumnText ct = new ColumnText(cb);
            ct.addElement(tableHeader);
            ct.setSimpleColumn(-30, 0, 620, 820);
            ct.go();

            font = new Font(Font.FontFamily.HELVETICA, 9, Font.ITALIC, BaseColor.BLACK);
            ColumnText.showTextAligned(writer.getDirectContent(), Element.ALIGN_CENTER, new Phrase("Página " + document.getPageNumber(), font), 520, 20, 0);

        } catch (BadElementException ex) {
            Logger.getLogger(HeaderFooter.class.getName()).log(Level.SEVERE, null, ex);
        } catch (DocumentException ex) {
            Logger.getLogger(HeaderFooter.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(HeaderReportePDF1.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void onStartPage(PdfWriter writer, Document documento) {

    }

}
