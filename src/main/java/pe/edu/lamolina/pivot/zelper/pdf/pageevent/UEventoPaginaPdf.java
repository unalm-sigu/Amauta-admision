package pe.edu.lamolina.pivot.zelper.pdf.pageevent;

import com.itextpdf.text.BadElementException;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
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
import pe.albatross.zelpers.pdf.document.PdfDocumentGenerator;
import pe.edu.lamolina.model.general.Oficina;

public class UEventoPaginaPdf extends PdfPageEventHelper {

    public final static String PATH = "/public/pdf/img/";

    int pageNum;
    private HeaderTypeEnum headerTypeEnum;
    private FooterTypeEnum footerTypeEnum;

    private String titulo1;
    private String titulo2;

    private String escudoUnalmPng = "escudoUnalm.png";
    private String escudoUnalmSm = "escudoUnalmSm.jpg";
    private String backgroundPage = "bgConstanciaIngreso.png";

    private Oficina oficina;

    public UEventoPaginaPdf(HeaderTypeEnum headerTypeEnum, FooterTypeEnum footerTypeEnum) {
        this.headerTypeEnum = headerTypeEnum;
        this.footerTypeEnum = footerTypeEnum;
    }

    public UEventoPaginaPdf() {
        this.headerTypeEnum = null;
        this.footerTypeEnum = null;
    }

    @Override
    public void onStartPage(PdfWriter writer, Document document) {
        if (headerTypeEnum == null) {
            return;
        }

        pageNum++;
        PdfContentByte cb = writer.getDirectContent();

        try {
            PdfPTable tableHeader;
            PdfPCell cell;

            if (HeaderTypeEnum.HEADER1.equals(headerTypeEnum)) {
                float[] columnWidths = new float[]{10f, 90f};
                tableHeader = new PdfPTable(columnWidths);

                //  tableHeader.setWidths(columnWidths);
                tableHeader.setHorizontalAlignment(PdfPTable.ALIGN_CENTER);
                Image img = Image.getInstance(this.getClass().getResource(PATH + escudoUnalmSm));
                img.scalePercent(30F);

                cell = new PdfPCell(img);
                cell.setRowspan(3);
                cell.setBorder(PdfPCell.NO_BORDER);
                cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
                tableHeader.addCell(cell);

                Font font = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD);
                cell = new PdfPCell(new Phrase("UNIVERSIDAD NACIONAL AGRARIA LA MOLINA", font));
                cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
                cell.setBorder(PdfPCell.NO_BORDER);
                tableHeader.addCell(cell);

                font = new Font(Font.FontFamily.HELVETICA, 8, Font.NORMAL);
                cell = new PdfPCell(new Phrase(oficina.getNombre(), font));
                cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
                cell.setBorder(PdfPCell.NO_BORDER);
                tableHeader.addCell(cell);

                font = new Font(Font.FontFamily.HELVETICA, 8, Font.NORMAL);
                cell = new PdfPCell(new Phrase(this.titulo1.toUpperCase(), font));
                cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
                cell.setBorder(PdfPCell.NO_BORDER);
                tableHeader.addCell(cell);

                ColumnText ct = new ColumnText(cb);
                ct.addElement(tableHeader);
                ct.setSimpleColumn(0, 0, 559, 806); //Position goes here
                ct.go();

                Rectangle rect = writer.getPageSize();
                Image image = Image.getInstance(this.getClass().getResource(PATH + backgroundPage));
                image.setAbsolutePosition(rect.getWidth() - 367, rect.getHeight() - 263);
                PdfContentByte canvas = writer.getDirectContentUnder();
                canvas.addImage(image);
            } else if (HeaderTypeEnum.HEADER2.equals(headerTypeEnum)) {
                tableHeader = new PdfPTable(1);
                tableHeader.setHorizontalAlignment(PdfPTable.ALIGN_CENTER);

                if (StringUtils.isNotBlank(titulo1)) {
                    cell = new PdfPCell(new Phrase(titulo1, PdfDocumentGenerator.FUENTE_TITULO));
                    cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
                    cell.setBorder(PdfPCell.NO_BORDER);
                    tableHeader.addCell(cell);
                }
                if (StringUtils.isNotBlank(titulo2)) {
                    cell = new PdfPCell(new Phrase(titulo2, PdfDocumentGenerator.FUENTE_TITULO));
                    cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
                    cell.setBorder(PdfPCell.NO_BORDER);
                    tableHeader.addCell(cell);
                }
                if (StringUtils.isNotBlank(titulo1) || StringUtils.isNotBlank(titulo2)) {
                    ColumnText ct = new ColumnText(cb);
                    ct.addElement(tableHeader);
                    ct.setSimpleColumn(0, 0, 559, 806); //Position goes here
                    ct.go();
                }
            }

        } catch (BadElementException ex) {
            Logger.getLogger(HeaderFooter.class.getName()).log(Level.SEVERE, null, ex);
        } catch (DocumentException ex) {
            Logger.getLogger(HeaderFooter.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(UEventoPaginaPdf.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    @Override
    public void onEndPage(PdfWriter writer, Document documento) {
        if (footerTypeEnum == null) {
            return;
        }

        PdfContentByte cb = writer.getDirectContent();
        Rectangle rectangulo = writer.getBoxSize("art");

        if (FooterTypeEnum.FOOTER3.equals(footerTypeEnum)) {
            Font fuentePiePagina = new Font();
            fuentePiePagina.setSize(7f);
            fuentePiePagina.setFamily("Arial");
            fuentePiePagina.setColor(BaseColor.BLACK);
            fuentePiePagina.setStyle(Font.BOLD);

            ColumnText.showTextAligned(writer.getDirectContent(), Element.ALIGN_CENTER, new Phrase("" + String.valueOf(writer.getPageNumber()), fuentePiePagina), (rectangulo.getLeft() + rectangulo.getRight()) / 2, rectangulo.getBottom() - 30, 0);

        } else if (FooterTypeEnum.FOOTER1.equals(footerTypeEnum) || FooterTypeEnum.FOOTER2.equals(footerTypeEnum)) {
            try {

                PdfPTable table = new PdfPTable(1);
                table.setTotalWidth(523);
                table.setHorizontalAlignment(PdfPTable.ALIGN_CENTER);

                Font font = new Font(Font.FontFamily.HELVETICA, 8, Font.NORMAL);

                PdfPCell cell = new PdfPCell();
                Paragraph p = new Paragraph("Av. La Molina s/n La Molina - Lima - Lima - Perú     Telf: 6147800 anexo 137    Directo: 6147117 / 6147118 / 6147119", font);
                p.setAlignment(Element.ALIGN_CENTER);
                cell.addElement(p);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setBorder(Rectangle.NO_BORDER);

                cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
                if (FooterTypeEnum.FOOTER1.equals(footerTypeEnum)) {
                    cell.setBorder(PdfPCell.TOP);
                }
                table.addCell(cell);

                cell = new PdfPCell(new Phrase("admision@lamolina.edu.pe    sip.lamolina.edu.pe    admision.lamolina.edu.pe", font));
                cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
                cell.setBorder(PdfPCell.NO_BORDER);
                table.addCell(cell);
                float xPos = (rectangulo.getLeft() + rectangulo.getRight()) / 2;
                float yPos = rectangulo.getBottom() - 20;
                table.writeSelectedRows(0, -1, 10, yPos, writer.getDirectContent());
            } catch (Exception e) {
                Logger.getLogger(HeaderFooter.class.getName()).log(Level.SEVERE, null, e);
            }
        }
    }

    public String getTitulo1() {
        return titulo1;
    }

    public void setTitulo1(String titulo1) {
        this.titulo1 = titulo1;
    }

    public String getTitulo2() {
        return titulo2;
    }

    public void setTitulo2(String titulo2) {
        this.titulo2 = titulo2;
    }

    public HeaderTypeEnum getHeaderTypeEnum() {
        return headerTypeEnum;
    }

    public void setHeaderTypeEnum(HeaderTypeEnum headerTypeEnum) {
        this.headerTypeEnum = headerTypeEnum;
    }

    public FooterTypeEnum getFooterTypeEnum() {
        return footerTypeEnum;
    }

    public void setFooterTypeEnum(FooterTypeEnum footerTypeEnum) {
        this.footerTypeEnum = footerTypeEnum;
    }

    public Oficina getOficina() {
        return oficina;
    }

    public void setOficina(Oficina oficina) {
        this.oficina = oficina;
    }

}
