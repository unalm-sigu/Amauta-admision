package pe.edu.lamolina.pivot.controller.reporte.view;

import com.google.common.base.Strings;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.groovy.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import pe.albatross.zelpers.miscelanea.ObjectUtil;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.AlumnoHorario;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.general.Aula;
import pe.edu.lamolina.model.general.Dia;
import pe.edu.lamolina.model.general.Oficina;
import pe.edu.lamolina.model.horario.Hora;
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

        List<Hora> horas = (List<Hora>) model.get("horas");
        List<Dia> dias = (List<Dia>) model.get("dias");

        Map<Long, Oficina> mapOficinas = (Map<Long, Oficina>) model.get("mapOficinas");

        CicloAcademico cicloAcademico = (CicloAcademico) model.get("cicloAcademico");
        List<AlumnoHorario> alumnosHorario = (List<AlumnoHorario>) model.get("alumnosHorario");

        Map<Long, List<Hora>> mapHorasConHorarios = (Map<Long, List<Hora>>) model.get("mapHorasConHorarios");

        for (AlumnoHorario alumnoHorario : alumnosHorario) {

            List<Hora> horasConHorarios = mapHorasConHorarios.get(alumnoHorario.getAlumno().getId());

            int totalColumn = 7;
            PdfPTable table = this.createTable(totalColumn);
            this.documentHeader(alumnoHorario.getAlumno(), dias, cicloAcademico, table, mapOficinas);
            this.documentBody(horasConHorarios, horas, document, table, cicloAcademico, totalColumn);

            document.newPage();

        }

        String nombre = this.getUnTitle(cicloAcademico);
        response.setHeader("Content-Disposition", "attachment; filename=\"" + nombre + ".pdf\"");
        response.setHeader("Set-Cookie", "fileDownload=true; path=/");
    }

    private void documentHeader(Alumno alumno, List<Dia> dias, CicloAcademico cicloAcademico, PdfPTable table, Map<Long, Oficina> mapOficinas) throws DocumentException, ParseException {
        Font headerFont = new Font(Font.FontFamily.HELVETICA, 8, Font.BOLD, BaseColor.WHITE);
        Phrase phr = null;
        PdfPCell cell = null;

        this.addCeldaHeader(this.title + " " + cicloAcademico.getDescripcion2().toUpperCase(), table);

        Oficina oficina = mapOficinas.get(alumno.getCarrera().getId());

        String facultad = (String) ObjectUtil.getParentTree(alumno, "carrera.facultad.nombre");

        String consejero = "";
        if (oficina != null) {
            consejero = (String) ObjectUtil.getParentTree(oficina, "personaJefe.nombreCompleto");
        }

        this.addCeldaLeftHeader("FACULTAD DE " + facultad.toUpperCase(), table);

        String codigoAlumno = alumno.getCodigo();
        String nombres = alumno.getPersona().getNombreCompleto().toUpperCase();
        String infoAlumno = codigoAlumno + " " + nombres;

        this.addCeldaLeftHeaderPersonalizado(infoAlumno, table);

        this.addCeldaLeftHeaderPersonalizadoRight(consejero, table);

        this.addCeldaLeftHeader(alumno.getEmailIngresante() + " -  " + alumno.getClaveEmailIngresante(), table);

        // pediente column
        phr = new Phrase("HORA", headerFont);
        cell = new PdfPCell(phr);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setBackgroundColor(BaseColor.BLACK);
        table.addCell(cell);

        for (Dia dia : dias) {
            phr = new Phrase(dia.getNombre().toUpperCase(), headerFont);
            cell = new PdfPCell(phr);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setBackgroundColor(BaseColor.BLACK);
            table.addCell(cell);
        }
    }

    private String getUnTitle(CicloAcademico cicloAcademico) {
        String namedate = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        return this.title + " " + cicloAcademico.getDescripcion() + " " + namedate;
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

    private void documentBody(List<Hora> horas, List<Hora> horasBase, Document document, PdfPTable table, CicloAcademico cicloAcademico, int totalColumn) throws DocumentException {

        int columnaHoraEspacio = 1;
        int totalColumnaContenido = totalColumn - columnaHoraEspacio;

        Map<Long, Hora> mapHoraEncontrado = TypesUtil.convertListToMap("id", horas);

        Font bodyFont = new Font(FontFamily.HELVETICA, 7, Font.NORMAL, BaseColor.BLACK);
        Font timeFont = new Font(FontFamily.HELVETICA, 10, Font.NORMAL, BaseColor.BLACK);
        Font letterFont = new Font(FontFamily.HELVETICA, 9, Font.BOLD, BaseColor.BLACK);

        for (Hora horaBase : horasBase) {

            // primera celda de hora
            PdfPCell cell = new PdfPCell(new Phrase(horaBase.getDescripcion(), timeFont));
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setFixedHeight(42);
            table.addCell(cell);

            Hora hora = mapHoraEncontrado.get(horaBase.getId());

            if (hora != null) {
                table = this.construccionCeldas(table, hora, bodyFont, letterFont, totalColumnaContenido);
            } else {
                table = this.construccionCeldasVacias(table, totalColumnaContenido);
            }
        }
        document.add(table);
        document.add(new Chunk("shot invisible", new Font(FontFamily.COURIER, 1, Font.NORMAL, BaseColor.WHITE)));
    }

    private PdfPTable construccionCeldasVacias(PdfPTable table, int totalColumnaContenido) {
        Font font = new Font(FontFamily.HELVETICA, 8, Font.NORMAL, BaseColor.BLACK);

        for (int i = 0; i < totalColumnaContenido; i++) {
            PdfPCell cell = new PdfPCell(new Phrase("", font));
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setFixedHeight(42);

            table.addCell(cell);
        }

        return table;
    }

    private PdfPTable construccionCeldas(PdfPTable table, Hora hora, Font bodyFont, Font letterFont, int totalColumnaContenido) throws DocumentException {

        for (int i = 0; i < totalColumnaContenido; i++) {

            // creacion tabla compuesta 
            PdfPTable innerTable = new PdfPTable(1);
            innerTable.getDefaultCell().setBorder(0);
            innerTable.setWidths(new int[]{1});
            innerTable.setWidthPercentage(100);
            innerTable.setSpacingBefore(0f);
            innerTable.setSpacingAfter(0f);
            innerTable.setPaddingTop(0f);

            String cursoCodigo = hora.getDias().get(i).getHorarioSeccion().isEmpty() ? "" : hora.getDias().get(i).getHorarioSeccion().get(0).getSeccion().getGrupoSeccion().getCurso().getCodigo();
            String curso = hora.getDias().get(i).getHorarioSeccion().isEmpty() ? "" : hora.getDias().get(i).getHorarioSeccion().get(0).getSeccion().getGrupoSeccion().getCurso().getNombre();

            // primera fila
            String firstLine = cursoCodigo + " " + (curso.length() > 17 ? curso.substring(0, 17).toUpperCase() : curso.toUpperCase());
            this.addCeldaLeftBody(firstLine, innerTable, bodyFont);

            // segunda fila //35 caracteres para la celda
            String docente = (hora.getDias().get(i).getHorarioSeccion().isEmpty() ? null
                    : (hora.getDias().get(i).getHorarioSeccion().get(0).getSeccion().getDocenteSeccion().get(0).getDocente().getPersona() != null
                    ? hora.getDias().get(i).getHorarioSeccion().get(0).getSeccion().getDocenteSeccion().get(0).getDocente().getPersona().getPaterno() : ""));
            String docenteCodigo = hora.getDias().get(i).getHorarioSeccion().isEmpty() ? null : hora.getDias().get(i).getHorarioSeccion().get(0).getSeccion().getDocenteSeccion().get(0).getDocente().getCodigo();

            if (docente != null) {
                String secondLine = "Prof. " + docente + " " + "(" + docenteCodigo + ")";
                this.addCeldaLeftBody(secondLine, innerTable, bodyFont);
            }

            // tercera fila
            String clave = (hora.getDias().get(i).getHorarioSeccion().isEmpty() ? null : hora.getDias().get(i).getHorarioSeccion().get(0).getSeccion().getCodigo2());
            if (clave != null) {
                String thirdLine = "Clave: " + clave;
                this.addCeldaLeftBody(thirdLine, innerTable, bodyFont);
            }

            Aula aula = (hora.getDias().get(i).getHorarioSeccion().isEmpty() ? null : hora.getDias().get(i).getHorarioSeccion().get(0).getSeccion().getAula());
            String quartLine = "";
            if (aula != null) {
                String abrev = (aula.getAulaSuperior().getNombre().length() > 20 ? " (" + aula.getAulaSuperior().getNombre().substring(0, 20) : " (" + aula.getAulaSuperior().getNombre() + ")");
                quartLine = "Aula: " + (aula.getCodigo() + abrev);
            }
            this.addCeldaLeftBody(quartLine, innerTable, bodyFont);
            table.addCell(innerTable);
        }

        return table;
    }

    private void addCeldaLeftBody(String contenido, PdfPTable table, Font bodyFont) {
        Phrase phr = new Phrase(contenido, bodyFont);
        PdfPCell cell = new PdfPCell(phr);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setPaddingLeft(0f);
        cell.setPaddingRight(0f);
        cell.setPaddingTop(0f);
        cell.setPaddingBottom(0f);
        table.addCell(cell);
    }

//    private void addCeldaLBody(String contenido, PdfPTable table, Font bodyFont) {
//        Phrase phr = new Phrase(contenido, bodyFont);
//        PdfPCell cell = new PdfPCell(phr);
//        cell.setVerticalAlignment(Element.ALIGN_LEFT);
//        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
//        cell.setBorder(Rectangle.NO_BORDER);
//        cell.setPaddingLeft(0f);
//        cell.setPaddingRight(0f);
//        cell.setPaddingTop(0f);
//        cell.setPaddingBottom(0f);
//        table.addCell(cell);
//    }
}
