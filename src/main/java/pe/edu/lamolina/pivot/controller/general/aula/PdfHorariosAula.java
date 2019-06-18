package pe.edu.lamolina.pivot.controller.general.aula;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Font.FontFamily;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
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

    @Override
    protected void buildPdfMetadata(Map<String, Object> model, Document document, HttpServletRequest request) {

        document.addAuthor("AlbatrossCloud");
        document.addCreationDate();
        document.addCreator("AlbatrossCloud");
        document.addTitle(this.title);
        document.addSubject("subject cualquiera");
        document.setPageSize(PageSize.A4.rotate());
        document.setMargins(36, 36, 38, 36);

    }

    @Override
    protected void buildPdfDocument(Map<String, Object> model, Document document, PdfWriter writer, HttpServletRequest request, HttpServletResponse response) throws Exception {

        Aula aula = (Aula) model.get("aula");
        Aula aulaSuperior = (Aula) model.get("aulaSuperior");
        List<Dia> dias = (List<Dia>) model.get("dias");
        List<Hora> horas = (List<Hora>) model.get("horas");

        PdfPTable table = new PdfPTable(7);
        table.setWidths(new int[]{1, 3, 3, 3, 3, 3, 3});
        table.setTotalWidth(770);
        table.setLockedWidth(true);
        table.setSpacingAfter(0f);
        table.setSpacingBefore(0f);
        table.setPaddingTop(0f);

        this.documentHeader(writer, aula, aulaSuperior, dias, table, document);
        this.documentBody(horas, document, table);
        document.newPage();

        String nombre = this.getUnTitle(aulaSuperior, aula);
        response.setHeader("Content-Disposition", "attachment; filename=\"" + nombre + ".pdf\"");
        response.setHeader("Set-Cookie", "fileDownload=true; path=/");
    }

    private String getUnTitle(Aula aulaSuperior, Aula aula) {
        String namedate = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        return this.title + " " + aulaSuperior.getNombre() + "-" + aula.getCodigo() + " " + namedate;
    }

    private void documentHeader(PdfWriter writer, Aula aula, Aula aulaSuperior, List<Dia> dias, PdfPTable table, Document document) throws DocumentException {

        Font fontHeader = new Font(Font.FontFamily.HELVETICA, 9, Font.NORMAL, BaseColor.BLACK);

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

        phr = new Phrase("Hora", fontHeader);
        cell = new PdfPCell(phr);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(cell);

        for (Dia dia : dias) {
            phr = new Phrase(dia.getNombre(), fontHeader);
            cell = new PdfPCell(phr);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);
        }
    }

    private void documentBody(List<Hora> horas, Document document, PdfPTable table) throws DocumentException {

        int totalColumna = 6;
        int totalColumnaContenido = totalColumna;

        Font bodyText = new Font(FontFamily.HELVETICA, 5, Font.NORMAL, BaseColor.BLACK);
        Font timeText = new Font(FontFamily.HELVETICA, 8, Font.NORMAL, BaseColor.BLACK);

        for (Hora hora : horas) {

            PdfPCell cell = new PdfPCell(new Phrase(hora.getDescripcion2(), timeText));
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);

            for (int i = 0; i < totalColumnaContenido; i++) {

                // tabla compuesta 
                PdfPTable innerTable = new PdfPTable(1);
                innerTable.getDefaultCell().setBorder(0);
                innerTable.setWidths(new int[]{1});
                innerTable.setWidthPercentage(100);
                innerTable.setSpacingBefore(0f);
                innerTable.setSpacingAfter(0f);
                innerTable.setPaddingTop(0f);

                String codigo = hora.getDias().get(i).getGrupohoras() == null ? "" : hora.getDias().get(i).getGrupohoras().getCodigo();

                // fila
                String line = codigo;
                PdfPCell cellInner = new PdfPCell(new Phrase(line, bodyText));
                cellInner.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cellInner.setHorizontalAlignment(Element.ALIGN_CENTER);
                cellInner.setBorder(Rectangle.NO_BORDER);
                cellInner.setPaddingLeft(0);
                cellInner.setPaddingRight(0);
                cellInner.setPaddingTop(0);
                cellInner.setPaddingBottom(0);

                String codigoCurso = hora.getDias().get(i).getMainHorarioAula() == null ? "" : hora.getDias().get(i).getMainHorarioAula().getSeccion().getGrupoSeccion().getCurso().getCodigo();
                String tcp = hora.getDias().get(i).getMainHorarioAula() == null ? "" : hora.getDias().get(i).getMainHorarioAula().getSeccion().getGrupoSeccion().getCurso().getTpc();
                String seccionCodigo2 = hora.getDias().get(i).getMainHorarioAula() == null ? "" : hora.getDias().get(i).getMainHorarioAula().getSeccion().getCodigo2();
                String codigoGpoHoras = hora.getDias().get(i).getMainHorarioAula() == null ? "" : hora.getDias().get(i).getMainHorarioAula().getSeccion().getGrupoHoras().getCodigo();

                // primera fila
                String firstLine = (codigoCurso == "" ? "" : codigoCurso + " " + tcp + " / " + seccionCodigo2 + " " + codigoGpoHoras);
                PdfPCell cellInner1 = new PdfPCell(new Phrase(firstLine, bodyText));
                cellInner1.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cellInner1.setHorizontalAlignment(Element.ALIGN_LEFT);
                cellInner1.setBorder(Rectangle.NO_BORDER);
                cellInner1.setPaddingLeft(0);
                cellInner1.setPaddingRight(0);
                cellInner1.setPaddingTop(0);
                cellInner1.setPaddingBottom(0);

                // segunda fila
                String secondLine = hora.getDias().get(i).getMainHorarioAula() == null ? "" : hora.getDias().get(i).getMainHorarioAula().getSeccion().getGrupoSeccion().getCurso().getNombre();
                PdfPCell cellInner2 = new PdfPCell(new Phrase(secondLine, bodyText));
                cellInner2.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cellInner2.setHorizontalAlignment(Element.ALIGN_LEFT);
                cellInner2.setBorder(Rectangle.NO_BORDER);
                cellInner2.setPaddingLeft(0);
                cellInner2.setPaddingRight(0);
                cellInner2.setPaddingTop(0);
                cellInner2.setPaddingBottom(0);

                innerTable.addCell(cellInner);
                innerTable.addCell(cellInner1);
                innerTable.addCell(cellInner2);

                // tercera fila
                if (hora.getDias().get(i).getMainHorarioAula() != null && !hora.getDias().get(i).getMainHorarioAula().getSeccion().getDocenteSeccion().isEmpty()) {

                    String docenteCodigo = hora.getDias().get(i).getMainHorarioAula().getSeccion().getDocenteSeccion().get(0).getDocente().getCodigo();
                    String docenteApNombres = hora.getDias().get(i).getMainHorarioAula().getSeccion().getDocenteSeccion().get(0).getDocente().getPersona() == null ? "Desconocido" : hora.getDias().get(i).getMainHorarioAula().getSeccion().getDocenteSeccion().get(0).getDocente().getPersona().getNomPaternoMat();
                    String triLine = docenteCodigo + " " + docenteApNombres;

                    PdfPCell cellInner3 = new PdfPCell(new Phrase(triLine, bodyText));
                    cellInner3.setVerticalAlignment(Element.ALIGN_MIDDLE);
                    cellInner3.setHorizontalAlignment(Element.ALIGN_LEFT);
                    cellInner3.setBorder(Rectangle.NO_BORDER);
                    cellInner3.setPaddingLeft(0);
                    cellInner3.setPaddingRight(0);
                    cellInner3.setPaddingTop(0);
                    cellInner3.setPaddingBottom(0);

                    innerTable.addCell(cellInner3);

                } else {

                    PdfPCell cellInner3 = new PdfPCell(new Phrase("", bodyText));
                    cellInner3.setBorder(Rectangle.NO_BORDER);
                    innerTable.addCell(cellInner3);
                }

                table.addCell(innerTable);
            }

        }

        document.add(table);
        document.add(new Chunk("shot invisible", new Font(FontFamily.COURIER, 10, Font.NORMAL, BaseColor.WHITE)));
    }

}
