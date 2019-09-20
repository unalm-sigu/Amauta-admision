package pe.edu.lamolina.pivot.controller.general.view;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfWriter;
import java.util.Date;
import pe.albatross.zelpers.miscelanea.TypesUtil;

public class HeaderReportePdf extends PdfPageEventHelper {

    private String fecha;
    private boolean pagina;

    public HeaderReportePdf() {
    }

    public HeaderReportePdf(boolean pagina) {
        this.pagina = pagina;
    }

    @Override
    public void onEndPage(PdfWriter writer, Document document) {
        PdfContentByte cb = writer.getDirectContent();
        Font sub = new Font(Font.FontFamily.HELVETICA, 10, Font.NORMAL);
        if (this.pagina) {
            sub = new Font(Font.FontFamily.HELVETICA, 8, Font.ITALIC, BaseColor.BLACK);
            this.fecha = "La Molina, " + TypesUtil.getStringDate(new Date(), "EEEE dd 'de' MMMM 'del' yyyy", "es");
            ColumnText.showTextAligned(writer.getDirectContent(), Element.ALIGN_LEFT, new Phrase(fecha, sub), 30, 20, 0);
            ColumnText.showTextAligned(writer.getDirectContent(), Element.ALIGN_CENTER, new Phrase("Página " + document.getPageNumber(), sub), 550, 20, 0);
        }
    }

    @Override
    public void onStartPage(PdfWriter writer, Document document) {
    }
}
