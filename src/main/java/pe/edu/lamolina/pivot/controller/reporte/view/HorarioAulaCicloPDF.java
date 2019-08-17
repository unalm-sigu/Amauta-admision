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
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import pe.albatross.zelpers.miscelanea.ObjectUtil;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Curso;
import pe.edu.lamolina.model.academico.GrupoSeccion;
import pe.edu.lamolina.model.academico.Seccion;
import pe.edu.lamolina.model.general.Aula;
import pe.edu.lamolina.model.general.Dia;
import pe.edu.lamolina.model.horario.Hora;
import pe.edu.lamolina.model.horario.HorarioAula;
import pe.edu.lamolina.pivot.controller.reporte.dto.HoraDTO;
import pe.edu.lamolina.pivot.zelper.pdf.AbstractOnlyPdfView;

@Component
public class HorarioAulaCicloPDF extends AbstractOnlyPdfView {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final String title = "HORARIO DE INGRESANTES";

    private final Font tituloFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD, BaseColor.BLACK);
    private final Font infoFont = new Font(Font.FontFamily.HELVETICA, 9, Font.BOLD, BaseColor.BLACK);

    private final Font headerTableFont = new Font(Font.FontFamily.HELVETICA, 8, Font.BOLD, BaseColor.WHITE);
    private final Font bodyTableFont = new Font(FontFamily.HELVETICA, 7, Font.NORMAL, BaseColor.BLACK);
    private final Font invisible = new Font(FontFamily.COURIER, 1, Font.NORMAL, BaseColor.WHITE);

    @Override
    protected void buildPdfMetadata(Map<String, Object> model, Document document, HttpServletRequest request) {

        document.addAuthor("La Molina");
        document.addCreationDate();
        document.addCreator("Amauta");
        document.addTitle(this.title);
        document.addSubject("");
        document.setPageSize(PageSize.A4.rotate());
        document.setMargins(36, 36, 15, 35);
    }

    @Override
    protected void buildPdfDocument(Map<String, Object> model, Document document, PdfWriter writer, HttpServletRequest request, HttpServletResponse response) throws Exception {

        List<HorarioAula> horariosAulas = (List<HorarioAula>) model.get("horariosAulas");
        List<Aula> aulas = (List<Aula>) model.get("aulas");
        CicloAcademico ciclo = (CicloAcademico) model.get("cicloAcademico");
        List<Dia> dias = (List<Dia>) model.get("dias");
        List<Hora> horas = (List<Hora>) model.get("horas");

        Map<Long, List<HorarioAula>> mapHorariosByAula = TypesUtil.convertListToMapList("aula.id", horariosAulas);

        for (Aula aula : aulas) {
            List<HorarioAula> horariosAulasByAula = (List<HorarioAula>) mapHorariosByAula.get(aula.getId());

            PdfPTable table = this.createTable();
            this.documentHeader(table, aula.getAulaSuperior(), ciclo, dias);
            this.generateTable(table, dias, horas, horariosAulasByAula);
            this.documentFooter(table, ciclo);
            try {
                document.add(table);
                document.add(new Chunk("shot invisible", invisible));
            } catch (Exception ex) {
                logger.debug("Error Shot", ex);
            }
            document.newPage();

        }

        String filename = "horarios-cachimbos";

        response.setHeader("Content-Disposition", "attachment; filename=\"" + filename + ".pdf\"");
        response.setHeader("Set-Cookie", "fileDownload=true; path=/");
    }

    private PdfPTable createTable() throws DocumentException {
        PdfPTable table = new PdfPTable(7);
        table.setWidths(new int[]{2, 3, 3, 3, 3, 3, 3});
        table.setTotalWidth(770);
        table.setLockedWidth(true);
        table.setSpacingAfter(0f);
        table.setSpacingBefore(0f);
        table.setPaddingTop(0f);
        return table;
    }

    private void documentHeader(PdfPTable table, Aula aula, CicloAcademico ciclo, List<Dia> dias) {
        String titulo = "AULA " + aula.getCodigo();
        if (ObjectUtil.getParentTree(aula, "aulaSuperior.nombre") != null) {
            titulo = ObjectUtil.getParentTree(aula, "aulaSuperior.nombre") + " " + titulo;
        }
        this.generateTitulo(titulo, table);
//
//        {
//            String facultad = (String) ObjectUtil.getParentTree(alumno, "carrera.facultad.nombre");
//            String carrera = (String) ObjectUtil.getParentTree(alumno, "carrera.nombre");
//            this.addHeaderLeft("FACULTAD DE " + facultad.toUpperCase() + " - " + carrera.toUpperCase(), table);
//
//            this.addHeaderRight("HORARIO: " + horario.getCodigo(), table);
//
//        }
//        {
//            String infoAlumno = alumno.getCodigo() + " " + alumno.getPersona()
//                    .getNombreCompleto().toUpperCase();
//            this.addHeaderLeft(infoAlumno, table);
//
//            String consejero = "";
//            if (oficina != null) {
//                if (oficina.getPersonaJefe() != null) {
//                    consejero = oficina.getPersonaJefe().getNombreCompleto();
//                }
//            }
//            this.addHeaderRight("CONSEJERO: " + consejero, table);
//        }
//        {
//            String credencial = alumno.getEmailIngresante() + " -  " + alumno.getClaveEmailIngresante();
//            this.addHeaderFull("CORREO INSTITUCIONAL: " + credencial, table);
//        }
        Font headerFont = new Font(Font.FontFamily.HELVETICA, 8, Font.BOLD, BaseColor.WHITE);

        Phrase phr = null;
        PdfPCell cell = null;

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

    private void addHeaderFull(String titulo, PdfPTable table) {
        Phrase phr = new Phrase(titulo, infoFont);
        PdfPCell cell = new PdfPCell(phr);
        cell.setVerticalAlignment(Element.ALIGN_LEFT);
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell.setColspan(7);
        cell.setBorder(Rectangle.NO_BORDER);
        table.addCell(cell);
    }

    private void addHeaderLeft(String titulo, PdfPTable table) {

        Phrase phr = new Phrase(titulo, infoFont);
        PdfPCell cell = new PdfPCell(phr);
        cell.setVerticalAlignment(Element.ALIGN_LEFT);
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell.setColspan(4);
        cell.setBorder(Rectangle.NO_BORDER);
        table.addCell(cell);
    }

    private void addHeaderRight(String titulo, PdfPTable table) {

        Phrase phr = new Phrase(titulo, infoFont);
        PdfPCell cell = new PdfPCell(phr);
        cell.setVerticalAlignment(Element.ALIGN_RIGHT);
        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        cell.setColspan(3);
        cell.setBorder(Rectangle.NO_BORDER);
        table.addCell(cell);
    }

    private void generateTable(PdfPTable table, List<Dia> dias, List<Hora> horas, List<HorarioAula> horariosAulas) {

        Font bodyFont = new Font(FontFamily.HELVETICA, 7, Font.NORMAL, BaseColor.BLACK);
        Font timeFont = new Font(FontFamily.HELVETICA, 10, Font.NORMAL, BaseColor.BLACK);
        Font letterFont = new Font(FontFamily.HELVETICA, 9, Font.BOLD, BaseColor.BLACK);
        Map<String, List<HorarioAula>> mapHorariosAulas = TypesUtil.convertListToMapList("idDiaHora", horariosAulas);
        for (Hora hora : horas) {

            // primera celda de hora
            PdfPCell cell = new PdfPCell(new Phrase(hora.getDescripcion2(), timeFont));
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setFixedHeight(35);
            table.addCell(cell);

            for (Dia dia : dias) {
                String key = dia.getId() + "_" + hora.getId();
                List<HorarioAula> horariosAulasByDiaHora = mapHorariosAulas.get(key);
                try {
                    construccionCeldas(table, bodyFont, letterFont, horariosAulasByDiaHora);
                } catch (Exception e) {
                    throw new PhobosException("Error al generar");
                }

            }

//            Hora hora = mapHoraEncontrado.get(horaBase.getId());
//
//            if (hora != null) {
//                table = this.construccionCeldas(table, hora, bodyFont, letterFont, totalColumnaContenido);
//            } else {
//                table = this.construccionCeldasVacias(table, totalColumnaContenido);
//            }
        }

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

    private void documentFooter(PdfPTable table, CicloAcademico cicloAcademico) {
        Font fontFooterPDF = new Font(Font.FontFamily.HELVETICA, 7, Font.NORMAL, BaseColor.BLACK);

        PdfPCell cellFooter = new PdfPCell(new Phrase("CICLO " + cicloAcademico.getDescripcion(), fontFooterPDF));
        cellFooter.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cellFooter.setHorizontalAlignment(Element.ALIGN_CENTER);
        // cellFooter.setColspan(1);
        cellFooter.setBorder(Rectangle.NO_BORDER);
        table.addCell(cellFooter);

        cellFooter = new PdfPCell(new Phrase("OFICINA DE ESTUDIOS Y REGISTROS ACADÉMICOS", fontFooterPDF));
        cellFooter.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cellFooter.setHorizontalAlignment(Element.ALIGN_CENTER);
        // cellFooter.setColspan(1);
        cellFooter.setBorder(Rectangle.NO_BORDER);
        table.addCell(cellFooter);

        Date now = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE dd 'de' MMMMM 'del' yyyy ", new Locale("es", "ES"));
        String fechaActual = sdf.format(now);

        PdfPCell cellFooter3 = new PdfPCell(new Phrase("La Molina, " + fechaActual, fontFooterPDF));
        cellFooter3.setHorizontalAlignment(Element.ALIGN_RIGHT);
        cellFooter3.setColspan(2);
        cellFooter3.setPaddingLeft(0f);
        cellFooter3.setPaddingRight(0f);
        cellFooter3.setPaddingTop(1f);
        cellFooter3.setPaddingBottom(0f);
        cellFooter3.setBorder(Rectangle.NO_BORDER);
        table.addCell(cellFooter3);
    }

    private PdfPTable construccionCeldas(PdfPTable table, Font bodyFont, Font letterFont, List<HorarioAula> horariosAulasByDiaHora) throws DocumentException {
        PdfPTable innerTable = new PdfPTable(1);
        innerTable.getDefaultCell().setBorder(0);
        innerTable.setWidths(new int[]{1});
        innerTable.setWidthPercentage(100);
        innerTable.setSpacingBefore(0f);
        innerTable.setSpacingAfter(0f);
        innerTable.setPaddingTop(0f);
        if (horariosAulasByDiaHora == null || horariosAulasByDiaHora.isEmpty()) {
            table.addCell(innerTable);
            return table;
        }
        List<Seccion> secciones = horariosAulasByDiaHora.stream().map(x -> x.getSeccion()).distinct().collect(Collectors.toList());
        for (Seccion seccion : secciones) {
            GrupoSeccion grupoSeccion = seccion.getGrupoSeccion();
            Curso curso = grupoSeccion.getCurso();
            String cursoString = curso.getCodigo() + " " + curso.getTpc();
            String seccionString = seccion.getCodigo2() + " " + seccion.getGrupoHoras().getCodigo();
            String cursoNombre = curso.getNombre();
            addCeldaCenterBody(seccion.getGrupoHoras().getCodigo(), innerTable, letterFont);
            addCeldaLeftBody(cursoString + " / " + seccionString, innerTable, bodyFont);
            addCeldaLeftBody(cursoNombre, innerTable, bodyFont);
            table.addCell(innerTable);
//            break;
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

    private void addCeldaCenterBody(String contenido, PdfPTable table, Font bodyFont) {
        Phrase phr = new Phrase(contenido, bodyFont);
        PdfPCell cell = new PdfPCell(phr);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setPaddingLeft(0f);
        cell.setPaddingRight(0f);
        cell.setPaddingTop(0f);
        cell.setPaddingBottom(0f);
        table.addCell(cell);
    }

}
