package pe.edu.lamolina.pivot.controller.general.aula;

import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfWriter;

public class HeaderTableDiario extends PdfPageEventHelper {

    protected PdfPTable table;
    protected float tableHeight;
    private Font fontHeader;

    public HeaderTableDiario() {
    }

    public float getTableHeight() {
        return tableHeight;
    }

    public void onEndPage(PdfWriter writer, Document document) {

        table.writeSelectedRows(0, -1, document.left(),
                document.top() + ((document.topMargin() + tableHeight) / 2) - 10,
                writer.getDirectContent());

        PdfPTable tblPage = new PdfPTable(1);
        tblPage.setTotalWidth(table.getTotalWidth());
        tblPage.setLockedWidth(true);
        PdfPCell cell = new PdfPCell(new Phrase(String.format("Pag. %d ", writer.getPageNumber()), fontHeader));
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        tblPage.addCell(cell);

        tblPage.writeSelectedRows(0, -1, document.left(),
                document.top() + ((document.topMargin() + tableHeight) / 2) - 10,
                writer.getDirectContent());

    }

    public PdfPTable getTable() {
        return table;
    }

    public void setTable(PdfPTable table) {
        this.table = table;
        this.tableHeight = table.getTotalHeight();
    }

    public Font getFontHeader() {
        return fontHeader;
    }

    public void setFontHeader(Font fontHeader) {
        this.fontHeader = fontHeader;
    }

}
