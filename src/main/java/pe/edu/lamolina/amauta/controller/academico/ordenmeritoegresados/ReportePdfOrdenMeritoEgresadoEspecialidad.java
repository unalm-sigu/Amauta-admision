package pe.edu.lamolina.amauta.controller.academico.ordenmeritoegresados;

import com.itextpdf.text.BadElementException;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.joda.time.DateTime;
import org.springframework.stereotype.Component;
import pe.albatross.zelpers.file.pdf.AbstractOnlyPdfView;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.Carrera;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Egresado;
import pe.edu.lamolina.model.academico.Facultad;
import pe.edu.lamolina.amauta.controller.general.view.HeaderReportePdf;
import pe.edu.lamolina.model.constantines.GlobalConstantine;
import pe.edu.lamolina.amauta.zelper.misc.Acumulador;

@Component
public class ReportePdfOrdenMeritoEgresadoEspecialidad extends AbstractOnlyPdfView {

    private final String header1 = "UNIVERSIDAD NACIONAL AGRARIA LA MOLINA";
    private final String header2 = "OFICINA DE ESTUDIOS Y REGISTROS ACADÉMICOS";
    private final String title = "Orden de Mérito Egresados por Especialidad";
    private final boolean numeroPagina = true;

    @Override
    protected void buildPdfMetadata(Map<String, Object> model, Document document, HttpServletRequest request) {
        CicloAcademico cicloAcademico = (CicloAcademico) model.get("cicloAcademico");
        document.addAuthor("AlbatrossCloud");
        document.addCreationDate();
        document.addCreator("AlbatrossCloud");
        document.addTitle(this.header1 + " - " + this.header2);
        document.addSubject(this.title + " - " + cicloAcademico.getDescripcion());
        document.setPageSize(PageSize.A4);
        document.setMargins(20, 5, 35, 20);
    }

    @Override
    protected void buildPdfDocument(Map<String, Object> model, Document document, PdfWriter writer, HttpServletRequest request, HttpServletResponse response) throws Exception {
        List<Egresado> egresados = (List<Egresado>) model.get("egresados");
        CicloAcademico cicloAcademico = (CicloAcademico) model.get("cicloAcademico");
        List<Facultad> facultades = (List<Facultad>) model.get("facultades");
        List<Facultad> noFacultadUnica = facultades.stream().filter(fac -> fac.getCarrera().size() > 1).collect(Collectors.toList());
        Collections.sort(noFacultadUnica, new Facultad.CompareCodigo());
        List<Long> idFacs = noFacultadUnica.stream().map(f -> f.getId()).collect(Collectors.toList());
        this.buildFooter(writer);
        this.buildHeader(document, cicloAcademico, egresados.size());
        this.buildBody(egresados, cicloAcademico, idFacs, document);
        DateTime today = new DateTime();
        String nombre = this.header1 + today.toString("yyyyMMdd_HHmm");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + nombre + ".pdf\"");
        response.setHeader("Set-Cookie", "fileDownload=true; path=/");
    }

    private void buildFooter(PdfWriter writer) {
        HeaderReportePdf event = new HeaderReportePdf(this.numeroPagina, true);
        writer.setPageEvent(event);
    }

    private void buildHeader(Document document, CicloAcademico cicloAcademico, int cantidadAlumno) throws DocumentException, BadElementException, IOException {
        Font fontCursivo = new Font(Font.FontFamily.HELVETICA, 9, Font.BOLDITALIC, BaseColor.BLACK);
        Font fontBold = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD, BaseColor.BLACK);

        PdfPTable tablePdf;
        PdfPCell cell;
        tablePdf = new PdfPTable(new float[]{20f, 80f});
        tablePdf.setWidths(new float[]{20f, 80f});
        tablePdf.setHorizontalAlignment(PdfPTable.ALIGN_CENTER);
        tablePdf.setSpacingAfter(0);
        tablePdf.setSpacingBefore(0);
        tablePdf.setPaddingTop(0);

        Image img = Image.getInstance(this.getClass().getResource(GlobalConstantine.PDF_LOGO_UNALM));
        img.scalePercent(50F);
        cell = new PdfPCell(img);
        cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
        cell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
        cell.setPaddingBottom(0f);
        cell.setPaddingTop(0f);
        cell.setRowspan(2);
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setBorderColor(BaseColor.WHITE);

        tablePdf.addCell(cell);

        Paragraph parrafo = new Paragraph();
        parrafo.setFont(new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD));
        parrafo.add(this.header1);
        parrafo.add(Chunk.NEWLINE);
        parrafo.setFont(new Font(Font.FontFamily.HELVETICA, 11, Font.NORMAL));
        parrafo.add(this.header2);
        parrafo.add(Chunk.NEWLINE);
        parrafo.setFont(new Font(Font.FontFamily.HELVETICA, 10, Font.NORMAL));
        parrafo.add(this.title.toUpperCase());

        cell = new PdfPCell(parrafo);
        cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
        cell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
        cell.setRowspan(4);
        cell.setBorder(0);
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setBorderColor(BaseColor.WHITE);

        tablePdf.addCell(cell);
        document.add(tablePdf);

        PdfPTable tableAlumno = new PdfPTable(4);
        tableAlumno.setTotalWidth(510);
        tableAlumno.setWidthPercentage(100);
        tableAlumno.setLockedWidth(true);
        tablePdf.setSpacingAfter(1f);
        this.addCeld(0, PdfPCell.ALIGN_CENTER, cicloAcademico.getDescripcion2(), 4, tableAlumno, fontBold, BaseColor.WHITE);
//        this.addCeld(-2, PdfPCell.ALIGN_CENTER, cantidadAlumno + " alumnos", 4, tableAlumno, fontBold, BaseColor.WHITE);
        String fecha = "La Molina, " + TypesUtil.getStringDate(new Date(), "EEEE dd 'de' MMMM 'del' yyyy", "es");
        this.addCeld(0, PdfPCell.ALIGN_RIGHT, fecha, 4, tableAlumno, fontCursivo, BaseColor.WHITE);
        document.add(tableAlumno);
    }

    private void addCeld(int lado, int posision, String texto, int colspan, PdfPTable tableBody, Font font, BaseColor base) {
        PdfPCell cell = new PdfPCell(new Phrase(texto, font));
        cell.setHorizontalAlignment(posision);
        cell.setColspan(colspan);
        cell.setPaddingLeft(2f);
        cell.setPaddingRight(2f);
        cell.setPaddingTop(0f);
        cell.setPaddingBottom(5f);
        if (lado > 0) {
            cell.setBorder(Rectangle.TOP | Rectangle.BOTTOM | lado);
        } else if (lado == -1) {
            cell.setBorder(Rectangle.TOP | Rectangle.BOTTOM);
        } else if (lado == -2) {
            cell.setBorder(Rectangle.BOTTOM);
        } else {
            cell.setBorder(Rectangle.UNDEFINED);
        }
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setBackgroundColor(base);
        tableBody.addCell(cell);
    }

    private void buildBody(List<Egresado> egresados, CicloAcademico cicloAcademico, List<Long> facultades, Document document)
            throws DocumentException, BadElementException, IOException {
        Font fontTableBody = new Font(Font.FontFamily.HELVETICA, 7, Font.NORMAL, BaseColor.BLACK);
        Acumulador contadorRow = new Acumulador();
        PdfPTable tableBody = null;
        PdfPTable tableHeader = null;

        Map<String, Carrera> mapCarrera = TypesUtil.convertListToMap("alumno.carrera", "alumno.carrera", egresados);
        Map<Long, List<Egresado>> mapBeanCarrera = TypesUtil.convertListToMapList("alumno.carrera.id", egresados);
        List<Carrera> listCarrera = new ArrayList(mapCarrera.values());
        int i = 0;
        for (Carrera carrera : listCarrera) {
            i++;
            if (!facultades.contains(carrera.getFacultad().getId())) {
                continue;
            }
            List<Egresado> listMapperByCarrera = mapBeanCarrera.get(carrera.getId());
            listMapperByCarrera = this.orderList(listMapperByCarrera);
            tableBody = this.createTableBody();
            tableHeader = this.createTableHeaderDescripcion(carrera.getNombre(), cicloAcademico);
            contadorRow.incrementar(5); // espacio del table header
            contadorRow = this.addCeldList(listMapperByCarrera, tableBody, tableHeader, fontTableBody, contadorRow, document, cicloAcademico, egresados.size());
            if (i != listCarrera.size()) {
                document.newPage();
                this.buildHeader(document, cicloAcademico, listMapperByCarrera.size());
                contadorRow = new Acumulador();
            }
        }
    }

    private List<Egresado> orderList(List<Egresado> listMapperByCarrera) { // oderna la lista y separa con campos nulos
        List<Egresado> listConOMC = listMapperByCarrera.stream().filter(x -> x.getOrdenMeritoCiclo() != null).collect(Collectors.toList());
        List<Egresado> listSinOMC = listMapperByCarrera.stream().filter(x -> x.getOrdenMeritoCiclo() == null).collect(Collectors.toList());
        Collections.sort(listSinOMC, (Egresado p1, Egresado p2) -> p2.getPromedioAcumulado().compareTo(p1.getPromedioAcumulado()));
        listConOMC.addAll(listSinOMC);
        return listConOMC;
    }

    private PdfPTable createTableBody() {
        PdfPTable tableBody = new PdfPTable(12);
        tableBody.setHorizontalAlignment(PdfPTable.ALIGN_CENTER);
        tableBody.setTotalWidth(510);
        tableBody.setWidthPercentage(100);
        tableBody.setLockedWidth(true);
        tableBody.setPaddingTop(5f);
        tableBody.setSpacingAfter(5f);

        this.addCeldHead("Matrícula", 2, tableBody, Element.ALIGN_CENTER);
        this.addCeldHead("Apellidos y Nombres", 4, tableBody, Element.ALIGN_LEFT);
        this.addCeldHead("Orden Mérito", 2, tableBody, Element.ALIGN_CENTER);
        this.addCeldHead("PPA", 2, tableBody, Element.ALIGN_CENTER);
        this.addCeldHead("Mérito Alcanzado", 2, tableBody, Element.ALIGN_CENTER);
        return tableBody;
    }

    private void addCeldHead(String texto, int colspan, PdfPTable tableBody, int posicion) {
        PdfPCell cell = new PdfPCell(new Phrase(texto, new Font(Font.FontFamily.HELVETICA, 8, Font.BOLD, BaseColor.BLACK)));
        cell.setHorizontalAlignment(posicion);
        cell.setColspan(colspan);
        cell.setPaddingLeft(2f);
        cell.setPaddingRight(2f);
        cell.setPaddingTop(0f);
        cell.setPaddingBottom(5f);
        cell.setBorder(Rectangle.TOP | Rectangle.BOTTOM | Rectangle.RIGHT | Rectangle.LEFT);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setBackgroundColor(BaseColor.GRAY);
        tableBody.addCell(cell);
    }

    private PdfPTable createTableHeaderDescripcion(String especialidad, CicloAcademico cicloAcademico) {
        Font font = new Font(Font.FontFamily.HELVETICA, 9, Font.BOLD, BaseColor.BLACK);
        PdfPTable table = new PdfPTable(8);
        table.setHorizontalAlignment(PdfPTable.ALIGN_CENTER);
        table.setTotalWidth(510);
        table.setWidthPercentage(100);
        table.setLockedWidth(true);
        table.setSpacingBefore(12f);
        table.setSpacingAfter(1f);
        table.setPaddingTop(10);
        String texto = "Especialidad de " + especialidad.toUpperCase();
        this.addCeld(0, PdfPCell.ALIGN_LEFT, cicloAcademico.getDescripcion2() + " " + texto, 8, table, font, BaseColor.WHITE);
        return table;
    }

    public String returnCarreraMerito(Egresado egresado) {
        if (egresado.getCuadroHonorCarrera() != null) {
            return "C.Honor";
        } else if (egresado.getQuintoSuperiorCarrera() != null) {
            return "5to.Super.";

        } else if (egresado.getTercioSuperiorCarrera() != null) {
            return "3cio.Super.";
        }
        return "-";
    }

    private Acumulador addCeldList(List<Egresado> list, PdfPTable tableBody, PdfPTable tableHeader, Font fontTableBody,
            Acumulador contadorRows, Document document, CicloAcademico cicloAcademico, int cantidad)
            throws DocumentException, BadElementException, IOException {
        boolean nuevoNivel = true;
        for (Egresado egresado : list) {
            this.addRowForData(egresado, contadorRows);
            if (contadorRows.getValor() > 54) {
                if (!nuevoNivel) {
                    document.add(tableHeader);
                    document.add(tableBody);
                }
                document.newPage();
                contadorRows = new Acumulador();
                this.buildHeader(document, cicloAcademico, cantidad);
                this.addRowForData(egresado, contadorRows);
                contadorRows.incrementar(5); // espacio del table header
                tableBody = this.createTableBody();
            }

            Alumno alumno = egresado.getAlumno();
            String matricula = alumno.getCodigo();
            String apeNombres = alumno.getPersona().getApellidosNombres();
            String ordenMeritoCarrera = egresado.getOrdenMeritoCarrera() != null ? egresado.getOrdenMeritoCarrera().toString() : "-";
            String ppa = this.returnValorDecimal(egresado.getPromedioAcumulado());
            String meritoAlcanzadoTipo = this.returnCarreraMerito(egresado);
            this.addCeld(Rectangle.LEFT, PdfPCell.ALIGN_CENTER, matricula, 2, tableBody, fontTableBody, BaseColor.WHITE);
            this.addCeld(-1, PdfPCell.ALIGN_LEFT, apeNombres, 4, tableBody, fontTableBody, BaseColor.WHITE);
            this.addCeld(-1, PdfPCell.ALIGN_CENTER, ordenMeritoCarrera, 2, tableBody, fontTableBody, BaseColor.WHITE);
            this.addCeld(-1, PdfPCell.ALIGN_CENTER, ppa, 2, tableBody, fontTableBody, BaseColor.WHITE);
            this.addCeld(Rectangle.RIGHT, PdfPCell.ALIGN_CENTER, meritoAlcanzadoTipo, 2, tableBody, fontTableBody, BaseColor.WHITE);

            if (list.indexOf(egresado) == 0) {
                nuevoNivel = false;
            }
        }

        document.add(tableHeader);
        document.add(tableBody);
        return contadorRows;
    }

    String returnValorDecimal(BigDecimal valor) {
        if (valor != null) {
            return valor.setScale(2, RoundingMode.FLOOR).toString();
        }
        return "-";
    }

    private void addRowForData(Egresado egresado, Acumulador contadorRows) {
        if (egresado.getAlumno().getPersona().getApellidosNombres().length() > 44) {
            contadorRows.incrementar(2);
        } else {
            contadorRows.incrementar();
        }
    }

}
