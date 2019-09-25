package pe.edu.lamolina.pivot.controller.general.view;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfWriter;
import java.util.Date;
import pe.albatross.zelpers.miscelanea.TypesUtil;

public class HeaderReportePdf extends PdfPageEventHelper {

    private final Font fontGenearal = new Font(Font.FontFamily.HELVETICA, 8, Font.ITALIC, BaseColor.BLACK);

    private String fecha;
    private boolean totalPagina;
    private boolean pagina = false;

    public HeaderReportePdf() {
    }

    public HeaderReportePdf(boolean pagina, boolean totalPagina) {
        this.pagina = pagina;
        this.totalPagina = totalPagina;
    }

    @Override
    public void onEndPage(PdfWriter writer, Document document) {
        if (this.pagina) {
            String footerPagina = "Página " + document.getPageNumber();
//            String footerPagina = this.totalPagina ? "Página " + document.getPageNumber() + " de " + document.getPageSize() : "Página " + document.getPageNumber();
            ColumnText.showTextAligned(writer.getDirectContent(), Element.ALIGN_CENTER, new Phrase(footerPagina, this.fontGenearal), 550, 20, 0);
        }
    }

    @Override
    public void onStartPage(PdfWriter writer, Document document) {
//        this.fecha = "La Molina, " + TypesUtil.getStringDate(new Date(), "EEEE dd 'de' MMMM 'del' yyyy", "es");
//        ColumnText.showTextAligned(writer.getDirectContent(), Element.ALIGN_RIGHT, new Phrase(fecha, this.fontGenearal), 30, 20, 0);
    }
}
