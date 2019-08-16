package pe.edu.lamolina.pivot.controller.reporte.view;

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
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import pe.albatross.zelpers.miscelanea.ObjectUtil;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.AlumnoHorario;
import pe.edu.lamolina.pivot.controller.reporte.dto.HoraDTO;
import pe.edu.lamolina.pivot.controller.reporte.dto.HorarioDTO;
import pe.edu.lamolina.pivot.zelper.pdf.AbstractOnlyPdfView;

@Component
public class HorarioAlumnoCicloPDF extends AbstractOnlyPdfView {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final String title = "HORARIO DE INGRESANTES";

    @Override
    protected void buildPdfMetadata(Map<String, Object> model, Document document, HttpServletRequest request) {

        document.addAuthor("AlbatrossCloud");
        document.addCreationDate();
        document.addCreator("AlbatrossCloud");
        document.addTitle(this.title);
        document.addSubject("");
        document.setPageSize(PageSize.A4.rotate());
        document.setMargins(36, 36, 15, 35);
    }

    @Override
    protected void buildPdfDocument(Map<String, Object> model, Document document, PdfWriter writer, HttpServletRequest request, HttpServletResponse response) throws Exception {

        List<AlumnoHorario> alumnosHorario = (List<AlumnoHorario>) model.get("alumnosHorario");
        Map<Long, HorarioDTO> horarios = (Map<Long, HorarioDTO>) model.get("horariosCachimbo");

        for (AlumnoHorario alumnoHorario : alumnosHorario) {

            Alumno alumno = alumnoHorario.getAlumno();
            HorarioDTO horario = horarios.get(alumnoHorario.getHorarioCachimbos().getId());

            int totalColumn = 7;

            PdfPTable table = this.createTable(totalColumn);

            this.documentHeader(table, alumno, horario);
            this.generateTable(document, table, horario);

            document.newPage();

        }

        String filename = "horarios-cachimbos";

        response.setHeader("Content-Disposition", "attachment; filename=\"" + filename + ".pdf\"");
        response.setHeader("Set-Cookie", "fileDownload=true; path=/");
    }

    private PdfPTable createTable(int columnTotal) throws DocumentException {
        PdfPTable table = new PdfPTable(columnTotal);
        table.setWidths(new int[]{2, 3, 3, 3, 3, 3, 3});
        table.setTotalWidth(770);
        table.setLockedWidth(true);
        table.setSpacingAfter(0f);
        table.setSpacingBefore(0f);
        table.setPaddingTop(0f);

        return table;
    }

    private void documentHeader(PdfPTable table, Alumno alumno, HorarioDTO horario) {
       

        this.addCeldaHeader(horario.getTitulo(), table);

        String facultad = (String) ObjectUtil.getParentTree(alumno, "carrera.facultad.nombre");
        this.addCeldaLeftHeader("FACULTAD DE " + facultad.toUpperCase(), table);

        String infoAlumno = alumno.getCodigo() + " " + alumno.getPersona()
                .getNombreCompleto().toUpperCase();
        this.addCeldaLeftHeaderPersonalizado(infoAlumno, table);

        String consejero = "";
        this.addCeldaLeftHeaderPersonalizadoRight(consejero, table);

        String credencial = alumno.getEmailIngresante() + " -  " + alumno.getClaveEmailIngresante();
        this.addCeldaLeftHeader(credencial, table);
    }

    private void addCeldaHeader(String titulo, PdfPTable table) {
        Font fontHeaderPDF = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD, BaseColor.BLACK);
        Phrase phr = new Phrase(titulo, fontHeaderPDF);
        PdfPCell cell = new PdfPCell(phr);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setColspan(7);
        cell.setBorder(Rectangle.NO_BORDER);
        table.addCell(cell);
    }

    private void addCeldaLeftHeader(String titulo, PdfPTable table) {
        Font fontHeaderPDF = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD, BaseColor.BLACK);
        Phrase phr = new Phrase(titulo, fontHeaderPDF);
        PdfPCell cell = new PdfPCell(phr);
        cell.setVerticalAlignment(Element.ALIGN_LEFT);
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell.setColspan(7);
        cell.setBorder(Rectangle.NO_BORDER);
        table.addCell(cell);
    }

    private void addCeldaLeftHeaderPersonalizado(String titulo, PdfPTable table) {
        Font fontHeaderPDF = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD, BaseColor.BLACK);
        Phrase phr = new Phrase(titulo, fontHeaderPDF);
        PdfPCell cell = new PdfPCell(phr);
        cell.setVerticalAlignment(Element.ALIGN_LEFT);
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell.setColspan(4);
        cell.setBorder(Rectangle.NO_BORDER);
        table.addCell(cell);
    }

    private void addCeldaLeftHeaderPersonalizadoRight(String titulo, PdfPTable table) {
        Font fontHeaderPDF = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD, BaseColor.BLACK);
        Phrase phr = new Phrase(titulo, fontHeaderPDF);
        PdfPCell cell = new PdfPCell(phr);
        cell.setVerticalAlignment(Element.ALIGN_RIGHT);
        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        cell.setColspan(3);
        cell.setBorder(Rectangle.NO_BORDER);
        table.addCell(cell);
    }

    private void generateTable(Document document, PdfPTable table, HorarioDTO horario) {

        List<List<HoraDTO>> horas = horario.getHorarios();

        for (List<HoraDTO> filas : horas) {
            for (HoraDTO celda : filas) {
                table = this.generateCelda(table, celda);
            }
        }

        try {
            document.add(table);
            document.add(new Chunk("shot invisible", invisible));
        } catch (Exception ex) {
            logger.debug("Error Shot", ex);
        }

    }

    private final Font timeFont = new Font(FontFamily.HELVETICA, 10, Font.NORMAL, BaseColor.BLACK);
    private final Font letterFont = new Font(FontFamily.HELVETICA, 9, Font.BOLD, BaseColor.BLACK);
    
    private final Font headerTableFont = new Font(Font.FontFamily.HELVETICA, 8, Font.BOLD, BaseColor.WHITE);
    private final Font bodyTableFont = new Font(FontFamily.HELVETICA, 7, Font.NORMAL, BaseColor.BLACK);
    private final Font invisible = new Font(FontFamily.COURIER, 1, Font.NORMAL, BaseColor.WHITE);

    private PdfPTable generateCelda(PdfPTable table, HoraDTO celda) {

        Phrase ppp = new Phrase(celda.getContenido());
        ppp.setFont(headerTableFont);

        PdfPCell cell = new PdfPCell(ppp);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setHorizontalAlignment(celda.getAlineacion());
        cell.setBackgroundColor(BaseColor.BLACK);
        
        if (celda.getTipoCelda() == HoraDTO.TipoCeldaDTO.BODY) {
            ppp.setFont(bodyTableFont);
            cell.setFixedHeight(42);
            cell.setBackgroundColor(BaseColor.WHITE);
        }

        table.addCell(cell);
        return table; 
    }

}
