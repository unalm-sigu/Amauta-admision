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
import pe.albatross.zelpers.miscelanea.TypesUtil;
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
        document.setMargins(36, 36, 40, 36);

    }

    @Override
    protected void buildPdfDocument(Map<String, Object> model, Document document, PdfWriter writer, HttpServletRequest request, HttpServletResponse response) throws Exception {

        Aula aula = (Aula) model.get("aula");
        Aula aulaSuperior = (Aula) model.get("aulaSuperior");
        List<Dia> dias = (List<Dia>) model.get("dias");
        List<Hora> horas = (List<Hora>) model.get("horas");
        List<Hora> horasBase = (List<Hora>) model.get("horasBase");

        PdfPTable table = this.createTable();
        this.documentHeader(aula, aulaSuperior, dias, table);
        this.documentBody(horas, horasBase, document, table);
        document.newPage();

        String nombre = this.getUnTitle(aulaSuperior, aula);
        response.setHeader("Content-Disposition", "attachment; filename=\"" + nombre + ".pdf\"");
        response.setHeader("Set-Cookie", "fileDownload=true; path=/");
    }

    private String getUnTitle(Aula aulaSuperior, Aula aula) {
        String namedate = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        return this.title + " " + aulaSuperior.getNombre() + "-" + aula.getCodigo() + " " + namedate;
    }

    private void documentHeader(Aula aula, Aula aulaSuperior, List<Dia> dias, PdfPTable table) throws DocumentException {

        Font fontHeaderPDF = new Font(Font.FontFamily.HELVETICA, 9, Font.NORMAL, BaseColor.BLACK);
        Font fontHeaderTable = new Font(Font.FontFamily.HELVETICA, 8, Font.BOLD, BaseColor.WHITE);

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

            PdfPCell cell = new PdfPCell(new Phrase(st.nextToken(), fontHeaderPDF));
            cell.setColspan(2);
            cell.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell);

            cell = new PdfPCell(new Phrase(st.nextToken(), fontHeaderPDF));
            cell.setColspan(20);
            cell.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell);

        }

        Phrase phr = null;
        PdfPCell cell = null;

        phr = new Phrase("Hora", fontHeaderTable);
        cell = new PdfPCell(phr);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setBackgroundColor(BaseColor.BLACK);
        table.addCell(cell);

        for (Dia dia : dias) {
            phr = new Phrase(dia.getNombre().toUpperCase(), fontHeaderTable);
            cell = new PdfPCell(phr);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setBackgroundColor(BaseColor.BLACK);
            table.addCell(cell);
        }
    }

    private void documentBody(List<Hora> horas, List<Hora> horasBase, Document document, PdfPTable table) throws DocumentException {

        int totalColumna = 7;
        int columnaHoraEspacio = 1;
        int totalColumnaContenido = totalColumna - columnaHoraEspacio;

        Map<Long, Hora> mapHoraEncontrado = TypesUtil.convertListToMap("id", horas);

        Font bodyText = new Font(FontFamily.HELVETICA, 7, Font.NORMAL, BaseColor.BLACK);
        Font timeText = new Font(FontFamily.HELVETICA, 8, Font.NORMAL, BaseColor.BLACK);

        for (Hora horaBase : horasBase) {

            // primera celda de hora
            PdfPCell cell = new PdfPCell(new Phrase(horaBase.getDescripcion2(), timeText));
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setFixedHeight(34);
            table.addCell(cell);

            Hora hora = mapHoraEncontrado.get(horaBase.getId());

            if (hora != null) {
                table = this.construccionCeldas(table, hora, bodyText, totalColumnaContenido);
            } else {
                table = this.construccionCeldasVacias(table, totalColumnaContenido);
            }
        }

        document.add(table);
        document.add(new Chunk("shot invisible", new Font(FontFamily.COURIER, 10, Font.NORMAL, BaseColor.WHITE)));
    }

    private PdfPTable createTable() throws DocumentException {

        PdfPTable table = new PdfPTable(7);
        table.setWidths(new int[]{1, 3, 3, 3, 3, 3, 3});
        table.setTotalWidth(770);
        table.setLockedWidth(true);
        table.setSpacingAfter(0f);
        table.setSpacingBefore(0f);
        table.setPaddingTop(0f);

        return table;
    }

    private PdfPTable construccionCeldas(PdfPTable table, Hora hora, Font bodyText, int totalColumnaContenido) throws DocumentException {

        for (int i = 0; i < totalColumnaContenido; i++) {

            // creacion tabla compuesta 
            PdfPTable innerTable = new PdfPTable(1);
            innerTable.getDefaultCell().setBorder(0);
            innerTable.setWidths(new int[]{1});
            innerTable.setWidthPercentage(100);
            innerTable.setSpacingBefore(0f);
            innerTable.setSpacingAfter(0f);
            innerTable.setPaddingTop(0f);

            String gpoCodigo = hora.getDias().get(i).getGrupohoras() == null ? "" : hora.getDias().get(i).getGrupohoras().getCodigo();

            // fila
            String line = gpoCodigo;
            PdfPCell cellInner = new PdfPCell(new Phrase(line, bodyText));
            cellInner.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cellInner.setHorizontalAlignment(Element.ALIGN_CENTER);
            cellInner.setBorder(Rectangle.NO_BORDER);
            cellInner.setPaddingLeft(0f);
            cellInner.setPaddingRight(0f);
            cellInner.setPaddingTop(0f);
            cellInner.setPaddingBottom(0f);

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
            cellInner1.setPaddingLeft(0f);
            cellInner1.setPaddingRight(0f);
            cellInner1.setPaddingTop(0f);
            cellInner1.setPaddingBottom(0f);

            // segunda fila //35 caracteres para la celda
            String nombreCurso = hora.getDias().get(i).getMainHorarioAula() == null ? "" : hora.getDias().get(i).getMainHorarioAula().getSeccion().getGrupoSeccion().getCurso().getNombre();
            String secondLine = (nombreCurso.length() > 35 ? nombreCurso.substring(0, 35) : nombreCurso);
            PdfPCell cellInner2 = new PdfPCell(new Phrase(secondLine, bodyText));
            cellInner2.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cellInner2.setHorizontalAlignment(Element.ALIGN_LEFT);
            cellInner2.setBorder(Rectangle.NO_BORDER);
            cellInner2.setPaddingLeft(0f);
            cellInner2.setPaddingRight(0f);
            cellInner2.setPaddingTop(0f);
            cellInner2.setPaddingBottom(0f);
            cellInner2.setNoWrap(true);

            innerTable.addCell(cellInner);
            innerTable.addCell(cellInner1);
            innerTable.addCell(cellInner2);

            // tercera fila
                if (hora.getDias().get(i).getMainHorarioAula() != null && hora.getDias().get(i).getMainHorarioAula().getSeccion().getDocenteSeccion() != null && !hora.getDias().get(i).getMainHorarioAula().getSeccion().getDocenteSeccion().isEmpty()) {

                String docenteCodigo = hora.getDias().get(i).getMainHorarioAula().getSeccion().getDocenteSeccion().get(0).getDocente().getCodigo();
                String docenteApNombres = hora.getDias().get(i).getMainHorarioAula().getSeccion().getDocenteSeccion().get(0).getDocente().getPersona() == null ? "Desconocido" : hora.getDias().get(i).getMainHorarioAula().getSeccion().getDocenteSeccion().get(0).getDocente().getPersona().getNombrePaternoMat();
                String triLine = docenteCodigo + " " + docenteApNombres;

                PdfPCell cellInner3 = new PdfPCell(new Phrase(triLine, bodyText));
                cellInner3.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cellInner3.setHorizontalAlignment(Element.ALIGN_LEFT);
                cellInner3.setBorder(Rectangle.NO_BORDER);
                cellInner3.setPaddingLeft(0f);
                cellInner3.setPaddingRight(0f);
                cellInner3.setPaddingTop(0f);
                cellInner3.setPaddingBottom(0f);

                innerTable.addCell(cellInner3);

            } else {

                PdfPCell cellInner3 = new PdfPCell(new Phrase("", bodyText));
                cellInner3.setBorder(Rectangle.NO_BORDER);
                innerTable.addCell(cellInner3);
            }

            table.addCell(innerTable);
        }
        return table;
    }

    private PdfPTable construccionCeldasVacias(PdfPTable table, int totalColumnaContenido) {
        Font font = new Font(FontFamily.HELVETICA, 8, Font.NORMAL, BaseColor.BLACK);

        for (int i = 0; i < totalColumnaContenido; i++) {
            PdfPCell cell = new PdfPCell(new Phrase("", font));
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setFixedHeight(34);

            table.addCell(cell);
        }

        return table;
    }

}
