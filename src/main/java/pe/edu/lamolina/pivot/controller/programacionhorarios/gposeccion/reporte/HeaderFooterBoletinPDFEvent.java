package pe.edu.lamolina.pivot.controller.programacionhorarios.gposeccion.reporte;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfWriter;

public class HeaderFooterBoletinPDFEvent extends PdfPageEventHelper {

    private String ciclo;
    private String anexo;

    public HeaderFooterBoletinPDFEvent(String ciclo, String anexo) {
        this.ciclo = ciclo;
        this.anexo = anexo;
    }

    public void onStartPage(PdfWriter writer, Document document) {
        Font font = new Font(Font.FontFamily.HELVETICA, 8, Font.NORMAL, BaseColor.BLACK);
        ColumnText.showTextAligned(writer.getDirectContent(), Element.ALIGN_LEFT, new Phrase(this.anexo, font), 30, 820, 0);
    }

    public void onEndPage(PdfWriter writer, Document document) {
        Font font = new Font(Font.FontFamily.HELVETICA, 8, Font.NORMAL, BaseColor.BLACK);
        ColumnText.showTextAligned(writer.getDirectContent(), Element.ALIGN_LEFT, new Phrase("Programación de horarios " + this.ciclo, font), 30, 20, 0);
        ColumnText.showTextAligned(writer.getDirectContent(), Element.ALIGN_CENTER, new Phrase("Página " + document.getPageNumber(), font), 550, 20, 0);
    }

    public void setAnexo(String anexo) {
        this.anexo = anexo;
    }

}
