
package pe.edu.lamolina.amauta.controller.reporte.view;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import java.util.List; // Falta esta importación
import java.util.Map;
import java.util.stream.Stream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import pe.albatross.zelpers.file.pdf.AbstractOnlyPdfView;
import pe.edu.lamolina.amauta.controller.programacionhorarios.gposeccion.reporte.dto.HorarioDocenteDTO;

@Component
public class HorarioDocentePDF extends AbstractOnlyPdfView {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final String title = "HORARIO DOCENTE CICLO :";

    private final Font tituloFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD, BaseColor.BLACK);
    private final Font infoFont = new Font(Font.FontFamily.HELVETICA, 9, Font.BOLD, BaseColor.BLACK);
    private final Font headerTableFont = new Font(Font.FontFamily.HELVETICA, 8, Font.BOLD, BaseColor.WHITE);
    private final Font bodyTableFont = new Font(Font.FontFamily.HELVETICA, 7, Font.NORMAL, BaseColor.BLACK);
    private final Font invisible = new Font(Font.FontFamily.COURIER, 1, Font.NORMAL, BaseColor.WHITE);

    int columnsMainTable = 7;
    int lengthMaxDescription = 25;

    @Override
    protected void buildPdfMetadata(Map<String, Object> model, Document document, HttpServletRequest request) {
        document.addAuthor("DERA");
        document.addCreationDate();
        document.addCreator("DERA");
        document.addTitle(this.title);
        document.addSubject("");
        document.setPageSize(PageSize.A4.rotate());
        document.setMargins(20, 20, 20, 20);
    }

    @Override
    protected void buildPdfDocument(Map<String, Object> model, Document document, PdfWriter writer, HttpServletRequest request, HttpServletResponse response) throws Exception {
        List<HorarioDocenteDTO> horarioDocente = (List<HorarioDocenteDTO>) model.get("horarioDocente");

        if (horarioDocente != null && !horarioDocente.isEmpty()) {
            // Crear y añadir la tabla de cabecera una vez
            PdfPTable headerTable = createHeaderTable();
            documentHeader(headerTable, horarioDocente.get(0)); // Usar el primer elemento para la cabecera
            document.add(headerTable);

            // Crear y añadir la cabecera de la tabla principal una vez
            PdfPTable table = createTable();
            addTableHeader(table);

            // Añadir las filas a la tabla principal para cada registro
            addRows(table, horarioDocente);
            document.add(table);
        }

        response.setHeader("Content-Disposition", "attachment; filename=\"horario-docente.pdf\"");
        response.setHeader("Set-Cookie", "fileDownload=true; path=/");
    }

    private PdfPTable createTable() throws DocumentException {
        PdfPTable table = new PdfPTable(columnsMainTable); // Número de columnas
        table.setWidthPercentage(80);
        table.setWidths(new int[]{1, 2, 2, 2, 2, 2, 2});
        table.setTotalWidth(770);
        table.setLockedWidth(true);
        table.setSpacingAfter(0f);
        table.setSpacingBefore(1f);
        table.setPaddingTop(0f);        
        return table;
    }

    private PdfPTable createHeaderTable() throws DocumentException {
        PdfPTable table = new PdfPTable(4);
        table.setWidthPercentage(90);
        table.setWidths(new int[]{1, 3, 2, 3});
        table.setTotalWidth(770);
        table.setLockedWidth(true);
        table.setSpacingAfter(0f);
        table.setSpacingBefore(3f);
        table.setPaddingTop(0f);         
        return table;
    }

    private void documentHeader(PdfPTable table, HorarioDocenteDTO horarioDocente) {
        String titulo = title + " " + horarioDocente.getCiclo().toUpperCase();
        this.generateTitulo(titulo, table);
        addHeaderCell(table, "Ciclo Académico:", horarioDocente.getCiclo().toUpperCase());
        addHeaderCell(table, "Apellidos y Nombres:", horarioDocente.getNombres().toUpperCase());
        addHeaderCell(table, "Código Docente:", horarioDocente.getCodigoDocente().toUpperCase());
        addHeaderCell(table, "Departamento:", horarioDocente.getNombreDepartamento().toUpperCase());
    }

    private void addHeaderCell(PdfPTable table, String header, String value) {
        PdfPCell headerCell = new PdfPCell(new Phrase(header, infoFont));
        headerCell.setHorizontalAlignment(Element.ALIGN_LEFT);
        headerCell.setBorder(PdfPCell.NO_BORDER);
        table.addCell(headerCell);

        PdfPCell valueCell = new PdfPCell(new Phrase(value, bodyTableFont));
        valueCell.setHorizontalAlignment(Element.ALIGN_LEFT);
        valueCell.setBorder(PdfPCell.NO_BORDER);
        table.addCell(valueCell);
    }

    private void addTableHeader(PdfPTable table) {
        Stream.of("Horario", "Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado")
              .forEach(columnTitle -> {
                  PdfPCell header = new PdfPCell();
                  header.setBackgroundColor(BaseColor.BLACK);
                  header.setBorderWidth(2);
                  header.setPhrase(new Phrase(columnTitle, headerTableFont));
                  header.setHorizontalAlignment(Element.ALIGN_CENTER);
                  table.addCell(header);
              });
    }

    private void addRows(PdfPTable table, List<HorarioDocenteDTO> horarioDocente) {
        for (HorarioDocenteDTO dto : horarioDocente) {
            PdfPCell cHora = new PdfPCell(new Phrase(dto.getHora2() == null ? "" : dto.getHora2(), bodyTableFont));
            cHora.setMinimumHeight(40f);
            table.addCell(cHora);
            table.addCell(celdaDia(dto.getCurso_lunes(),   dto.getAula_lunes(),   dto.getGrupo_lunes(),   dto.getDescansoLunes()));
            table.addCell(celdaDia(dto.getCurso_martes(),  dto.getAula_martes(),  dto.getGrupo_martes(),  dto.getDescansoMartes()));
            table.addCell(celdaDia(dto.getCurso_miercoles(),dto.getAula_miercoles(),dto.getGrupo_miercoles(),dto.getDescansoMiercoles()));
            table.addCell(celdaDia(dto.getCurso_jueves(),  dto.getAula_jueves(),  dto.getGrupo_jueves(),  dto.getDescansoJueves()));
            table.addCell(celdaDia(dto.getCurso_viernes(), dto.getAula_viernes(), dto.getGrupo_viernes(), dto.getDescansoViernes()));
            table.addCell(celdaDia(dto.getCurso_sabado(),  dto.getAula_sabado(),  dto.getGrupo_sabado(),  dto.getDescansoSabado()));
        }
    }

    private PdfPCell celdaDia(String curso, String aula, String grupo, String descanso) {
        PdfPTable inner = new PdfPTable(1);
        inner.getDefaultCell().setBorder(Rectangle.NO_BORDER);
        try { inner.setWidths(new int[]{1}); } catch (DocumentException e) { /* ignore */ }
        inner.setWidthPercentage(100);

        String contenido =
                (curso == null ? "" : curso) + "\n" +
                        (aula  == null ? "" : aula)  + "\n" +
                        (grupo == null ? "" : grupo);
        PdfPCell cCont = new PdfPCell(new Phrase(contenido, bodyTableFont));
        cCont.setBorder(Rectangle.NO_BORDER);
        cCont.setPadding(2f);
        cCont.setVerticalAlignment(Element.ALIGN_TOP);
        inner.addCell(cCont);

        if (descanso != null && !descanso.trim().isEmpty()) {
            Font fontDescanso = new Font(Font.FontFamily.HELVETICA, 7, Font.BOLD, BaseColor.WHITE);
            PdfPCell cDesc = new PdfPCell(new Phrase(descanso, fontDescanso));
            cDesc.setBorder(Rectangle.NO_BORDER);
            cDesc.setPadding(1f);
            cDesc.setBackgroundColor(new BaseColor(13, 95, 44));
            cDesc.setHorizontalAlignment(Element.ALIGN_CENTER);
            inner.addCell(cDesc);
        }

        PdfPCell celda = new PdfPCell(inner);
        celda.setPadding(0f);
        return celda;
    }
    
    private void generateTitulo(String titulo, PdfPTable table) {
        Phrase phr = new Phrase(titulo, tituloFont);

        PdfPCell cell = new PdfPCell(phr);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setColspan(7);
        cell.setBorder(Rectangle.NO_BORDER);

        table.addCell(cell);
    }
}