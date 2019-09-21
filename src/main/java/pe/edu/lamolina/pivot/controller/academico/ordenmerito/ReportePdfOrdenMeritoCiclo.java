package pe.edu.lamolina.pivot.controller.academico.ordenmerito;

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
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.joda.time.DateTime;
import org.springframework.stereotype.Component;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.AlumnoCiclo;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.pivot.controller.general.view.HeaderReportePdf;
import pe.edu.lamolina.pivot.zelper.constant.Constantine;
import pe.edu.lamolina.pivot.zelper.pdf.AbstractOnlyPdfView;

@Component
public class ReportePdfOrdenMeritoCiclo extends AbstractOnlyPdfView {

    private final String logoUNALM = Constantine.LOGOUNALM;
    private final Font fontGenearal = new Font(Font.FontFamily.HELVETICA, 8, Font.NORMAL, BaseColor.BLACK);
    private final String header1 = "UNIVERSIDAD NACIONAL AGRARIA LA MOLINA";
    private final String header2 = "OFICINA DE ESTUDIOS Y REGISTROS ACADÉMICOS";
    private final String title = "Orden de Mérito General";
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
        List<AlumnoCiclo> listAlumnoCiclo = (List<AlumnoCiclo>) model.get("listAlumnoCiclo");
        CicloAcademico cicloAcademico = (CicloAcademico) model.get("cicloAcademico");

        this.buildFooter(writer);
        this.buildHeaderPaginaPrincipal(document, cicloAcademico, listAlumnoCiclo.size());
        this.buildBody(listAlumnoCiclo, cicloAcademico, document);

        DateTime today = new DateTime();
        String nombre = this.header1 + today.toString("yyyyMMdd_HHmm");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + nombre + ".pdf\"");
        response.setHeader("Set-Cookie", "fileDownload=true; path=/");
    }

    private void buildFooter(PdfWriter writer) {
        HeaderReportePdf event = new HeaderReportePdf(this.numeroPagina);
        writer.setPageEvent(event);
    }

    private void buildHeaderPaginaPrincipal(Document document, CicloAcademico cicloAcademico, int cantidadAlumno) throws DocumentException, BadElementException, IOException {
        Font font = new Font(Font.FontFamily.HELVETICA, 10, Font.NORMAL, BaseColor.BLACK);
        PdfPTable tablePdf;
        PdfPCell cell;
        tablePdf = new PdfPTable(new float[]{20f, 80f});
        tablePdf.setWidths(new float[]{20f, 80f});
        tablePdf.setHorizontalAlignment(PdfPTable.ALIGN_CENTER);
        tablePdf.setSpacingAfter(0);
        tablePdf.setSpacingBefore(0);
        tablePdf.setPaddingTop(0);

        Image img = Image.getInstance(this.getClass().getResource(this.logoUNALM));
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
        tableAlumno.setHorizontalAlignment(PdfPTable.ALIGN_CENTER);
        tableAlumno.setTotalWidth(220);
        tableAlumno.setLockedWidth(true);
        this.addCeld(0, PdfPCell.ALIGN_CENTER, cicloAcademico.getDescripcion2(), 4, tableAlumno, font, BaseColor.WHITE);
        this.addCeld(0, PdfPCell.ALIGN_CENTER, cantidadAlumno + " alumnos", 4, tableAlumno, font, BaseColor.WHITE);
        document.add(tableAlumno);
        document.add(new Chunk(".", new Font(Font.FontFamily.COURIER, 1, Font.NORMAL, BaseColor.WHITE)));
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
        } else {
            cell.setBorder(Rectangle.UNDEFINED);
        }
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setBackgroundColor(base);
        tableBody.addCell(cell);
    }

    private void buildBody(List<AlumnoCiclo> listAlumnoCiclo, CicloAcademico cicloAcademico, Document document) throws DocumentException {
        Font fontTableBody = new Font(Font.FontFamily.HELVETICA, 7, Font.NORMAL, BaseColor.BLACK);
        Map<Integer, List<AlumnoCiclo>> mapListAlumnoCiclo = TypesUtil.convertListToMapList("nivel", listAlumnoCiclo);
        int numberPageActual = 0;
        int contador = 0;
        PdfPTable tableBody = null;
        int index = 0;
        for (index = 1; index < 6; index++) {
            PdfPTable tableDataAlumnoBody = this.createTableHeaderDescripcion(cicloAcademico, index);
            document.add(tableDataAlumnoBody);
            tableBody = this.createTableBody();
            List<AlumnoCiclo> list = mapListAlumnoCiclo.get(index);
            List<AlumnoCiclo> list2 = list.stream().filter(x -> x.getOrdenMeritoCicloNivel() != null).collect(Collectors.toList());
            List<AlumnoCiclo> listSinNivel3 = list.stream().filter(x -> x.getOrdenMeritoCicloNivel() == null).collect(Collectors.toList());
            Collections.sort(listSinNivel3, (AlumnoCiclo p1, AlumnoCiclo p2) -> p2.getPromedioAcumulado().compareTo(p1.getPromedioAcumulado()));
            list2.addAll(listSinNivel3);
            this.addCeldList(list2, tableBody, fontTableBody, numberPageActual, contador, document, index, cicloAcademico);
        }
        PdfPTable tableDescripcion = this.createTableHeaderDescripcion(cicloAcademico, index);
        document.add(tableDescripcion);
        document.add(tableBody);
    }

    private PdfPTable createTableBody() {
        PdfPTable tableBody = new PdfPTable(12);
        tableBody.setHorizontalAlignment(PdfPTable.ALIGN_CENTER);
        tableBody.setTotalWidth(510);
        tableBody.setWidthPercentage(100);
        tableBody.setLockedWidth(true);
        tableBody.setPaddingTop(10f);
        tableBody.setSpacingAfter(10f);

        this.addCeldHead("Matrícula", 1, tableBody, Element.ALIGN_CENTER);
        this.addCeldHead("Apellidos y Nombres", 4, tableBody, Element.ALIGN_LEFT);
        this.addCeldHead("Especialidad", 3, tableBody, Element.ALIGN_LEFT);
        this.addCeldHead("Orden Mérito", 1, tableBody, Element.ALIGN_CENTER);
        this.addCeldHead("PPA", 1, tableBody, Element.ALIGN_CENTER);
        this.addCeldHead("Mérito Alcanzado", 2, tableBody, Element.ALIGN_CENTER);
        return tableBody;
    }

    private PdfPTable createTableHeaderDescripcion(CicloAcademico cicloAcademico, Integer nivel) {
        Font font = new Font(Font.FontFamily.HELVETICA, 9, Font.NORMAL, BaseColor.BLACK);
        PdfPTable table = new PdfPTable(8);
        table.setHorizontalAlignment(PdfPTable.ALIGN_CENTER);
        table.setTotalWidth(510);
        table.setWidthPercentage(100);
        table.setLockedWidth(true);
        table.setPaddingTop(5f);
        table.setSpacingBefore(2f);
        table.setSpacingAfter(2f);
        this.addCeld(0, PdfPCell.ALIGN_LEFT, cicloAcademico.getDescripcion2() + " - Nivel " + nivel.toString(), 8, table, font, BaseColor.WHITE);
        return table;
    }

    public String returnCicloMerito(AlumnoCiclo alumnoCiclo) {
        if (alumnoCiclo.getCuadroHonorCicloNivel() != null) {
            return "C.Honor";
        } else if (alumnoCiclo.getQuintoSuperiorCicloNivel() != null) {
            return "5to.Super.";

        } else if (alumnoCiclo.getTercioSuperiorCicloNivel() != null) {
            return "3cio.Super.";
        }
        return "";
    }

    public String returnFacultadMerito(AlumnoCiclo alumnoCiclo) {
        if (alumnoCiclo.getCuadroHonorFacultadNivel() != null) {
            return "C.Honor";
        } else if (alumnoCiclo.getQuintoSuperiorFacultadNivel() != null) {
            return "5to.Super.";

        } else if (alumnoCiclo.getTercioSuperiorFacultadNivel() != null) {
            return "3cio.Super.";
        }
        return "";
    }

    public String returnCarreraMerito(AlumnoCiclo alumnoCiclo) {
        if (alumnoCiclo.getCuadroHonorCarreraNivel() != null) {
            return "C.Honor";
        } else if (alumnoCiclo.getQuintoSuperiorCarreraNivel() != null) {
            return "5to.Super.";

        } else if (alumnoCiclo.getTercioSuperiorCarreraNivel() != null) {
            return "3cio.Super.";
        }
        return "";
    }

    BigDecimal returnValorDecimal(BigDecimal valor) {
        if (valor == null) {
            return new BigDecimal(BigInteger.ZERO);
        }
        return valor.setScale(2, RoundingMode.FLOOR);
    }

    private void addCeldList(List<AlumnoCiclo> list, PdfPTable tableBody, Font fontTableBody, int numberPageActual, int contadorColumn, Document document, int indexAsnivel, CicloAcademico cicloAcademico) throws DocumentException {
        for (AlumnoCiclo alumnoCiclo : list) {
            contadorColumn++;
            if (numberPageActual == 0 && contadorColumn > 47) {
                contadorColumn = 0;
                numberPageActual++;
                document.add(tableBody);
                document.newPage();
                tableBody = this.createTableBody();
            } else if (numberPageActual > 0 && contadorColumn >= 57) {
                contadorColumn = 0;
                numberPageActual++;
                PdfPTable tableDescripcion = this.createTableHeaderDescripcion(cicloAcademico, indexAsnivel);
                document.add(tableDescripcion);
                document.add(tableBody);
                tableBody = this.createTableBody();
                document.newPage();
            }

            Alumno alumno = alumnoCiclo.getAlumno();
            String matricula = alumno.getCodigo();
            String apeNombres = alumno.getPersona().getApellidosNombres();
            String especialidad = alumnoCiclo.getCarrera().getNombre();
            String nivel = alumnoCiclo.getNivel() != null ? alumnoCiclo.getNivel().toString() : ""; //****
            String ordenMeritoCicloNivel = alumnoCiclo.getOrdenMeritoCicloNivel() != null ? alumnoCiclo.getOrdenMeritoCicloNivel().toString() : "";
            String ppa = this.returnValorDecimal(alumnoCiclo.getPromedioAcumulado()).toString();
            String meritoAlcanzadoCicloNivel = this.returnCicloMerito(alumnoCiclo);
            this.addCeld(Rectangle.LEFT, PdfPCell.ALIGN_CENTER, matricula, 1, tableBody, fontTableBody, BaseColor.WHITE);
            this.addCeld(-1, PdfPCell.ALIGN_LEFT, apeNombres, 4, tableBody, fontTableBody, BaseColor.WHITE);
            this.addCeld(-1, PdfPCell.ALIGN_LEFT, especialidad + "-" + nivel, 3, tableBody, fontTableBody, BaseColor.WHITE);
            this.addCeld(-1, PdfPCell.ALIGN_CENTER, ordenMeritoCicloNivel, 1, tableBody, fontTableBody, BaseColor.WHITE);
            this.addCeld(-1, PdfPCell.ALIGN_CENTER, ppa, 1, tableBody, fontTableBody, BaseColor.WHITE);
            this.addCeld(Rectangle.RIGHT, PdfPCell.ALIGN_CENTER, meritoAlcanzadoCicloNivel, 2, tableBody, fontTableBody, BaseColor.WHITE);
        }
    }

}
