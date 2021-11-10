package pe.edu.lamolina.amauta.controller.academico.profesor.view;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;
import java.util.function.Function;
import java.util.stream.Collectors;
import static java.util.stream.Collectors.groupingBy;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import pe.albatross.zelpers.file.pdf.AbstractOnlyPdfView;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.DepartamentoAcademico;
import pe.edu.lamolina.model.academico.DocenteSeccion;
import pe.edu.lamolina.model.academico.Facultad;
import pe.edu.lamolina.model.academico.GrupoSeccion;
import pe.edu.lamolina.model.academico.Seccion;
import pe.edu.lamolina.model.rrhh.ContratoDocente;

@Component
public class ReporteContratoLaboralPDF extends AbstractOnlyPdfView {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final String title = "Reporte Contrato Laboral";
    private final String autor = "UNIVERSIDAD NACIONAL AGRARIA LA MOLINA";
    private final String creator = "Universidad Nacional Agraria La Molina";
    private final String oficina = "Oficina de Estudios y Registros Académicos";
    private final BaseColor GRAY_LIGHT = new BaseColor(219, 219, 219);

    private final List<String> tituloItems = Arrays.asList(
            "Docente",
            "Código",
            "Categoría",
            "Dedicación",
            "Situación"
    );

    @Override
    protected void buildPdfMetadata(Map<String, Object> model, Document document, HttpServletRequest request) {

        document.addAuthor(this.autor);
        document.addCreationDate();
        document.addCreator(this.title);
        document.addTitle(this.title);
        document.addSubject(this.title);
        document.setPageSize(PageSize.A4);
        document.setMargins(30, 30, 75, 20);

    }

    @Override
    protected void buildPdfDocument(Map<String, Object> model, Document document, PdfWriter writer, HttpServletRequest request, HttpServletResponse response) throws Exception {

        HeaderReportePDF1 event = new HeaderReportePDF1(autor, oficina);
        writer.setPageEvent(event);

        this.createBody(model, document, writer);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd HHmm");
        String nombre = "Reporte Contrato Laboral " + sdf.format(new Date());
        response.setHeader("Content-Disposition", "attachment; filename=\"" + nombre + ".pdf\"");
    }

    private void createBody(Map<String, Object> model, Document document, PdfWriter writer) throws DocumentException {

        PdfPTable tableBody;

        CicloAcademico ciclo = (CicloAcademico) model.get("cicloAcademico");

        List<ContratoDocente> contratoDocentes = (List<ContratoDocente>) model.get("contratoDocentes");

        int countCiclosConInformacion = 0;

        countCiclosConInformacion++;

        Map<Long, List<ContratoDocente>> contratoDocentesXdepartamento = contratoDocentes.stream()
                .collect(groupingBy(x -> x.getDocente().getDepartamentoAcademico().getId()));

        Map<Long, DepartamentoAcademico> departamentoAcademicos = contratoDocentes.stream()
                .collect(Collectors.toMap(x -> x.getDocente().getDepartamentoAcademico().getId(),
                        y -> y.getDocente().getDepartamentoAcademico(), (z, w) -> w));

        for (Map.Entry<Long, List<ContratoDocente>> entry : contratoDocentesXdepartamento.entrySet()) {

            float[] columnWidths = new float[]{45f, 7f, 20f, 20f, 20f};
            tableBody = new PdfPTable(columnWidths);
            tableBody.setWidths(columnWidths);
            tableBody.setHorizontalAlignment(PdfPTable.ALIGN_LEFT);
            tableBody.setWidthPercentage(100);

            DepartamentoAcademico departamentoAcademico = departamentoAcademicos.get(entry.getKey());

            Facultad facultad = departamentoAcademico.getFacultad();

            this.textCenter(tableBody, "CONTRATO LABORAL " + ciclo.getDescripcion());
            this.addSpace(tableBody);

            this.cellFullColumn(tableBody, "Facultad de " + facultad.getNombre());
            this.cellFullColumn(tableBody, "Departamento de " + departamentoAcademico.getNombre());

            this.tituloItem(tableBody, 1, tituloItems);

            for (ContratoDocente contratoDocente : entry.getValue()) {

                this.textCell(tableBody, contratoDocente.getDocente().getPersona().getApellidosNombres(), false);
                this.textCell(tableBody, contratoDocente.getDocente().getCodigo(), false);
                this.textCell(tableBody, contratoDocente.getCategoria().getNombre(), false);
                this.textCell(tableBody, contratoDocente.getDedicacion().getNombre(), false);
                this.textCell(tableBody, contratoDocente.getSituacion().getNombre(), false);

            }

            document.add(tableBody);
            document.newPage();

        }

        if (countCiclosConInformacion < 1) {
            document.add(new Chunk("No hay resultados para mostrar.", new Font(Font.FontFamily.TIMES_ROMAN, 1, Font.NORMAL, BaseColor.WHITE)));
            document.newPage();
        }

    }

    private void tituloItem(PdfPTable tableBody, int colSpan, List<String> nombreItems) {

        for (String nombreItem : nombreItems) {

            Font font = new Font(Font.FontFamily.TIMES_ROMAN, 8, Font.BOLD);
            PdfPCell cell = new PdfPCell(new Phrase(nombreItem, font));
            cell.setRowspan(1);
            cell.setBackgroundColor(GRAY_LIGHT);
            cell.setVerticalAlignment(PdfPCell.ALIGN_CENTER);
            cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
            cell.setBorderColor(GRAY_LIGHT);
            tableBody.addCell(cell);

        }

    }

    private void cellFullColumn(PdfPTable tableBody, String str) {

        Font font = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.BOLD);
        PdfPCell cell = new PdfPCell(new Phrase(str, font));
        cell.setColspan(7);
        cell.setBorder(PdfPCell.NO_BORDER);
        cell.setPaddingBottom(10f);
        tableBody.addCell(cell);

    }

    private void textCenter(PdfPTable tableBody, String str) {

        Font font = new Font(Font.FontFamily.TIMES_ROMAN, 8, Font.NORMAL);
        PdfPCell cell = new PdfPCell(new Phrase(str, font));
        cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
        cell.setColspan(7);
        cell.setBorder(PdfPCell.NO_BORDER);
        tableBody.addCell(cell);

    }

    private void addSpace(PdfPTable tableBody) {

        PdfPCell cell = new PdfPCell(new Phrase(".", new Font(Font.FontFamily.TIMES_ROMAN, 1, Font.NORMAL, BaseColor.WHITE)));
        cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
        cell.setColspan(7);
        cell.setPaddingBottom(10f);
        cell.setPaddingTop(10f);
        cell.setBorder(PdfPCell.NO_BORDER);
        tableBody.addCell(cell);

    }

    private void textCell(PdfPTable tableBody, String txt, boolean right) {
        Font font = new Font(Font.FontFamily.TIMES_ROMAN, 8, Font.NORMAL);
        PdfPCell cell = new PdfPCell(new Phrase(txt, font));
        cell.setPaddingBottom(5f);
        cell.setPaddingTop(5f);
        if (right) {
            cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
        }
        cell.setBorderColor(GRAY_LIGHT);
        cell.setRowspan(1);
        tableBody.addCell(cell);
    }

}
