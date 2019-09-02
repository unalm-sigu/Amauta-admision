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
import java.util.ArrayList;
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
import pe.albatross.zelpers.pdf.document.PdfDocumentGenerator;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Curso;
import pe.edu.lamolina.model.academico.Docente;
import pe.edu.lamolina.model.academico.DocenteSeccion;
import pe.edu.lamolina.model.academico.GrupoSeccion;
import pe.edu.lamolina.model.academico.Seccion;
import pe.edu.lamolina.model.bienestar.ReservaAula;
import pe.edu.lamolina.model.enums.TipoResponsableEnum;
import pe.edu.lamolina.model.enums.TurnoAtencionEnum;
import pe.edu.lamolina.model.general.Aula;
import pe.edu.lamolina.model.general.Dia;
import pe.edu.lamolina.model.general.Persona;
import pe.edu.lamolina.model.general.ResponsableAula;
import pe.edu.lamolina.model.general.ResponsableAulaAsignacion;
import pe.edu.lamolina.model.horario.DiaHoraGrupo;
import pe.edu.lamolina.model.horario.Hora;
import pe.edu.lamolina.model.horario.HorarioAula;
import pe.edu.lamolina.model.tramite.Tramite;
import pe.edu.lamolina.pivot.zelper.pdf.AbstractOnlyPdfView;

@Component
public class HorarioAulaCicloPDF extends AbstractOnlyPdfView {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final String title = "Horarios Aula";

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
        document.setMargins(10, 10, 10, 10);
    }

    @Override
    protected void buildPdfDocument(Map<String, Object> model, Document document, PdfWriter writer, HttpServletRequest request, HttpServletResponse response) throws Exception {

        List<HorarioAula> horariosAulas = (List<HorarioAula>) model.get("horariosAulas");
        List<Aula> aulas = (List<Aula>) model.get("aulas");
        CicloAcademico ciclo = (CicloAcademico) model.get("cicloAcademico");
        List<Dia> dias = (List<Dia>) model.get("dias");
        List<Hora> horas = (List<Hora>) model.get("horas");
        List<DiaHoraGrupo> diasHorasGrupos = (List<DiaHoraGrupo>) model.get("diasHorasGruposByCiclo");
        Map<String, List<DiaHoraGrupo>> mapDiasHorasGrupos = TypesUtil.convertListToMapList("idDiaHora", diasHorasGrupos);
        List<ResponsableAulaAsignacion> responsablesAulasAsignadas = (List<ResponsableAulaAsignacion>) model.get("responsablesAulasAsignadas");

        Map<Long, List<HorarioAula>> mapHorariosByAula = TypesUtil.convertListToMapList("aula.id", horariosAulas);

        for (Aula aula : aulas) {
            List<HorarioAula> horariosAulasByAula = (List<HorarioAula>) mapHorariosByAula.get(aula.getId());
            List<ResponsableAulaAsignacion> responsablesByAula = responsablesAulasAsignadas.stream()
                    .filter(x -> x.getAula().equals(aula) || x.getAula().equals(aula.getAulaSuperior()))
                    .collect(Collectors.toList());
            PdfPTable table = this.createTable();
            this.documentHeader(table, aula, ciclo, dias, responsablesByAula);
            this.generateTable(table, dias, horas, horariosAulasByAula, mapDiasHorasGrupos);
            this.documentFooter(table, aula, ciclo);

            try {
                document.add(table);
                document.add(new Chunk("shot invisible", invisible));
            } catch (Exception ex) {
                ex.printStackTrace();
                logger.debug("Error Shot", ex);
            }
            document.newPage();

        }

        String filename = "horarios-aulas";

        response.setHeader("Content-Disposition", "attachment; filename=\"" + filename + ".pdf\"");
        response.setHeader("Set-Cookie", "fileDownload=true; path=/");
    }

    private PdfPTable createTable() throws DocumentException {
        PdfPTable table = new PdfPTable(7);
        table.setWidths(new int[]{1, 3, 3, 3, 3, 3, 3});
        table.setTotalWidth(800);
        table.setLockedWidth(true);
        table.setSpacingAfter(0f);
        table.setSpacingBefore(0f);
        table.setPaddingTop(0f);
        return table;
    }

    private void documentHeader(PdfPTable table, Aula aula, CicloAcademico ciclo, List<Dia> dias, List<ResponsableAulaAsignacion> responsablesAulasAsignadas) throws DocumentException {
        String titulo = "AULA " + aula.getCodigo();
        if (ObjectUtil.getParentTree(aula, "aulaSuperior.nombre") != null) {
            titulo = "MÓDULO " + ObjectUtil.getParentTree(aula, "aulaSuperior.nombre") + " " + titulo;
        }
        this.generateTitulo(titulo, table);
        this.generateSubTitulo(responsablesAulasAsignadas, table);
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

    private void generateTable(PdfPTable table, List<Dia> dias, List<Hora> horas, List<HorarioAula> horariosAulas, Map<String, List<DiaHoraGrupo>> mapDiasHorasGrupos) {

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
                List<DiaHoraGrupo> diasHoraGrupo = mapDiasHorasGrupos.get(key);
                try {
                    construccionCeldas(table, bodyFont, letterFont, horariosAulasByDiaHora, diasHoraGrupo);
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new PhobosException("Error al generar");
                }

            }
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

    public void generateSubTitulo(List<ResponsableAulaAsignacion> responsablesAulasAsignadas, PdfPTable table) throws DocumentException {

        PdfPTable innerTable = new PdfPTable(12);
        innerTable.getDefaultCell().setBorder(Rectangle.NO_BORDER);
        innerTable.setWidths(new int[]{2, 3, 1, 2,2, 3, 1, 2,2, 3, 1, 2});
        innerTable.setWidthPercentage(100);
        innerTable.setSpacingBefore(0f);
        innerTable.setSpacingAfter(0f);
        innerTable.setPaddingTop(0f);

        PdfPCell cell = new PdfPCell(innerTable);
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setColspan(7);

        Persona responsableAulaMNA = this.getResponsable(responsablesAulasAsignadas, TurnoAtencionEnum.MNA, TipoResponsableEnum.RES).getPersona();
        Persona responsableAulaTAR = this.getResponsable(responsablesAulasAsignadas, TurnoAtencionEnum.TAR, TipoResponsableEnum.RES).getPersona();
        Persona supervisorAulaMNA = this.getResponsable(responsablesAulasAsignadas, TurnoAtencionEnum.MNA, TipoResponsableEnum.SUP).getPersona();
        Persona supervisorAulaTAR = this.getResponsable(responsablesAulasAsignadas, TurnoAtencionEnum.TAR, TipoResponsableEnum.SUP).getPersona();
        Persona soportAulaMNA = this.getResponsable(responsablesAulasAsignadas, TurnoAtencionEnum.MNA, TipoResponsableEnum.SOP).getPersona();
        Persona soportAulaTAR = this.getResponsable(responsablesAulasAsignadas, TurnoAtencionEnum.TAR, TipoResponsableEnum.SOP).getPersona();

        PdfDocumentGenerator uDocumentoPdf = new PdfDocumentGenerator();
        uDocumentoPdf.addBodyCellTable(TipoResponsableEnum.RES.getValue().toUpperCase(), innerTable, 4, Element.ALIGN_LEFT, PdfDocumentGenerator.FUENTE_7_NEGRITA);
        uDocumentoPdf.addBodyCellTable(TipoResponsableEnum.SUP.getValue().toUpperCase(), innerTable, 4, Element.ALIGN_LEFT, PdfDocumentGenerator.FUENTE_7_NEGRITA);
        uDocumentoPdf.addBodyCellTable(TipoResponsableEnum.SOP.getValue().toUpperCase(), innerTable, 4, Element.ALIGN_LEFT, PdfDocumentGenerator.FUENTE_7_NEGRITA);

        //MAÑANA
        uDocumentoPdf.addBodyCellTable("MAÑANA", innerTable, 1, Element.ALIGN_LEFT, PdfDocumentGenerator.FUENTE_7_NEGRITA);
        uDocumentoPdf.addBodyCellTable(responsableAulaMNA.getPaternoNombre(), innerTable, 1, Element.ALIGN_LEFT);
        uDocumentoPdf.addBodyCellTable("CEL", innerTable, 1, Element.ALIGN_LEFT, PdfDocumentGenerator.FUENTE_7_NEGRITA);
        uDocumentoPdf.addBodyCellTable(responsableAulaMNA.getCelular(), innerTable, 1, Element.ALIGN_LEFT);

        uDocumentoPdf.addBodyCellTable("MAÑANA", innerTable, 1, Element.ALIGN_LEFT, PdfDocumentGenerator.FUENTE_7_NEGRITA);
        uDocumentoPdf.addBodyCellTable(supervisorAulaMNA.getPaternoNombre(), innerTable, 1, Element.ALIGN_LEFT);
        uDocumentoPdf.addBodyCellTable("CEL", innerTable, 1, Element.ALIGN_LEFT, PdfDocumentGenerator.FUENTE_7_NEGRITA);
        uDocumentoPdf.addBodyCellTable(supervisorAulaMNA.getCelular(), innerTable, 1, Element.ALIGN_LEFT);

        uDocumentoPdf.addBodyCellTable("MAÑANA", innerTable, 1, Element.ALIGN_LEFT, PdfDocumentGenerator.FUENTE_7_NEGRITA);
        uDocumentoPdf.addBodyCellTable(soportAulaMNA.getPaternoNombre(), innerTable, 1, Element.ALIGN_LEFT);
        uDocumentoPdf.addBodyCellTable("CEL", innerTable, 1, Element.ALIGN_LEFT, PdfDocumentGenerator.FUENTE_7_NEGRITA);
        uDocumentoPdf.addBodyCellTable(soportAulaMNA.getCelular(), innerTable, 1, Element.ALIGN_LEFT);

        //TARDE
        uDocumentoPdf.addBodyCellTable("TARDE", innerTable, 1, Element.ALIGN_LEFT, PdfDocumentGenerator.FUENTE_7_NEGRITA);
        uDocumentoPdf.addBodyCellTable(responsableAulaTAR.getPaternoNombre(), innerTable, 1, Element.ALIGN_LEFT);
        uDocumentoPdf.addBodyCellTable("CEL", innerTable, 1, Element.ALIGN_LEFT, PdfDocumentGenerator.FUENTE_7_NEGRITA);
        uDocumentoPdf.addBodyCellTable(responsableAulaTAR.getCelular(), innerTable, 1, Element.ALIGN_LEFT);

        uDocumentoPdf.addBodyCellTable("TARDE", innerTable, 1, Element.ALIGN_LEFT, PdfDocumentGenerator.FUENTE_7_NEGRITA);
        uDocumentoPdf.addBodyCellTable(supervisorAulaTAR.getPaternoNombre(), innerTable, 1, Element.ALIGN_LEFT);
        uDocumentoPdf.addBodyCellTable("CEL", innerTable, 1, Element.ALIGN_LEFT, PdfDocumentGenerator.FUENTE_7_NEGRITA);
        uDocumentoPdf.addBodyCellTable(supervisorAulaTAR.getCelular(), innerTable, 1, Element.ALIGN_LEFT);

        uDocumentoPdf.addBodyCellTable("TARDE", innerTable, 1, Element.ALIGN_LEFT, PdfDocumentGenerator.FUENTE_7_NEGRITA);
        uDocumentoPdf.addBodyCellTable(soportAulaTAR.getPaternoNombre(), innerTable, 1, Element.ALIGN_LEFT);
        uDocumentoPdf.addBodyCellTable("CEL", innerTable, 1, Element.ALIGN_LEFT, PdfDocumentGenerator.FUENTE_7_NEGRITA);
        uDocumentoPdf.addBodyCellTable(soportAulaTAR.getCelular(), innerTable, 1, Element.ALIGN_LEFT);

        table.addCell(cell);
    }

    public ResponsableAula getResponsable(List<ResponsableAulaAsignacion> responsablesAulasAsignadas, TurnoAtencionEnum turnoAtencionEnum, TipoResponsableEnum tipoResponsableEnum) {
        ResponsableAulaAsignacion responsablesAulasMOD = responsablesAulasAsignadas.stream()
                .filter(x -> x.getResponsableAula().getTipoEnum() == tipoResponsableEnum)
                .filter(x -> x.getAula().getTipoAula().isTipoAulaMOD())
                .filter(x -> x.getTurnoAtencionAula().getCodigo().equals(turnoAtencionEnum.name()))
                .findFirst().orElse(null);
        ResponsableAulaAsignacion responsablesAulasAUL = responsablesAulasAsignadas.stream()
                .filter(x -> x.getResponsableAula().getTipoEnum() == tipoResponsableEnum)
                .filter(x -> x.getAula().getTipoAula().isTipoAulaAUL())
                .filter(x -> x.getTurnoAtencionAula().getCodigo().equals(turnoAtencionEnum.name()))
                .findFirst().orElse(null);
        if (responsablesAulasAUL != null) {
            return responsablesAulasAUL.getResponsableAula();
        }
        if (responsablesAulasMOD == null) {
            responsablesAulasMOD = new ResponsableAulaAsignacion();
            ResponsableAula responsableAula = new ResponsableAula();
            responsableAula.setPersona(new Persona());
            responsablesAulasMOD.setResponsableAula(responsableAula);
        }
        return responsablesAulasMOD.getResponsableAula();
    }

    private void documentFooter(PdfPTable table, Aula aula, CicloAcademico cicloAcademico) {
        Font fontFooterPDF = new Font(Font.FontFamily.HELVETICA, 7, Font.NORMAL, BaseColor.BLACK);

        String cicloStr = "CICLO " + cicloAcademico.getDescripcion();
        String capacidadStr = "CAPACIDAD " + aula.getCapacidadAula();
        PdfPCell cellFooter = new PdfPCell(new Phrase(cicloStr + "      " + capacidadStr, fontFooterPDF));
        cellFooter.setVerticalAlignment(Element.ALIGN_LEFT);
        cellFooter.setHorizontalAlignment(Element.ALIGN_LEFT);
        cellFooter.setColspan(2);
        cellFooter.setPaddingLeft(0f);
        cellFooter.setPaddingRight(0f);
        cellFooter.setPaddingTop(1f);
        cellFooter.setPaddingBottom(0f);
        cellFooter.setBorder(Rectangle.NO_BORDER);
        table.addCell(cellFooter);

        PdfPCell cellFooter2 = new PdfPCell(new Phrase("OFICINA DE ESTUDIOS Y REGISTROS ACADÉMICOS", fontFooterPDF));
        cellFooter2.setVerticalAlignment(Element.ALIGN_LEFT);
        cellFooter2.setHorizontalAlignment(Element.ALIGN_CENTER);
        cellFooter2.setColspan(3);
        cellFooter2.setPaddingLeft(0f);
        cellFooter2.setPaddingRight(0f);
        cellFooter2.setPaddingTop(1f);
        cellFooter2.setPaddingBottom(0f);
        cellFooter2.setBorder(Rectangle.NO_BORDER);
        table.addCell(cellFooter2);

//        String fechaActual = TypesUtil.getStringDate(now, " dd 'de' MMMM 'de' yyyy", "es");
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

    private PdfPTable construccionCeldas(
            PdfPTable table, Font bodyFont, Font letterFont,
            List<HorarioAula> horariosAulasByDiaHora,
            List<DiaHoraGrupo> diasHoraGrupo) throws DocumentException {
        PdfPTable innerTable = new PdfPTable(1);
        innerTable.getDefaultCell().setBorder(0);
        innerTable.setWidths(new int[]{1});
        innerTable.setWidthPercentage(100);
        innerTable.setSpacingBefore(0f);
        innerTable.setSpacingAfter(0f);
        innerTable.setPaddingTop(0f);
        if (diasHoraGrupo != null) {
            for (DiaHoraGrupo diaHoraGrupo : diasHoraGrupo) {
                addCeldaCenterBody(diaHoraGrupo.getGrupoHorario().getCodigo(), innerTable, bodyFont); //letterFont
            }
        }
        if (horariosAulasByDiaHora == null || horariosAulasByDiaHora.isEmpty()) {
            table.addCell(innerTable);
            return table;
        }
        List<HorarioAula> horariosConSeccion = horariosAulasByDiaHora.stream()
                .filter(x -> x.getSeccion() != null)
                .collect(Collectors.toList());
        List<HorarioAula> horariosConReserva = horariosAulasByDiaHora.stream()
                .filter(x -> x.getReservaAula() != null)
                .collect(Collectors.toList());
        if (horariosAulasByDiaHora.size() == 1) {
            this.agregarHorarioConSeccion(innerTable, horariosConSeccion, bodyFont);
            this.agregarHorarioConReserva(innerTable, horariosConReserva, bodyFont);
        } else {
            //cruce
        }
        table.addCell(innerTable);
        return table;
    }

    public void agregarCruce(PdfPTable innerTable, List<HorarioAula> horariosConSeccion, Font bodyFont) {
        List<String> cruces = new ArrayList<>();
        for (HorarioAula horarioAula : horariosConSeccion) {
            Seccion seccion = horarioAula.getSeccion();
            ReservaAula reservaAula = horarioAula.getReservaAula();
            if (seccion != null) {
                GrupoSeccion grupoSeccion = seccion.getGrupoSeccion();
                Curso curso = grupoSeccion.getCurso();
                String cruce = curso.getCodigo() + " / " + seccion.getCodigo2();
                cruces.add(cruce);
            }
            if (reservaAula != null) {
                String tipoReserva = "Reserva; " + reservaAula.getTipoReservaEnum().getValue();
                String solicitante = "Solicitante; " + reservaAula.getTramite().getTipoSolicitanteEnum().getValue();
                cruces.add(tipoReserva + " / " + solicitante);
            }
        }
        for (String cruce : cruces) {
            Phrase phr = new Phrase(cruce, bodyFont);
            PdfPCell cell = this.getCellLeftBody(phr);
            cell.setBackgroundColor(BaseColor.RED);
            innerTable.addCell(cell);
        }
    }

    public void agregarHorarioConSeccion(PdfPTable innerTable, List<HorarioAula> horariosConSeccion, Font bodyFont) {
        if (horariosConSeccion == null || horariosConSeccion.isEmpty()) {
            return;
        }
        List<Seccion> secciones = horariosConSeccion.stream().map(x -> x.getSeccion()).distinct().collect(Collectors.toList());

        for (Seccion seccion : secciones) {
            GrupoSeccion grupoSeccion = seccion.getGrupoSeccion();
            Curso curso = grupoSeccion.getCurso();
            String cursoString = curso.getCodigo() + " " + curso.getTpc();
            String seccionString = seccion.getCodigo2() + " " + seccion.getGrupoHoras().getCodigo();
            String cursoNombre = curso.getNombre();
            addCeldaLeftBody(cursoString + " / " + seccionString, innerTable, bodyFont);
            if (cursoNombre.length() >= 35) {
                cursoNombre = cursoNombre.substring(0, 35);
            }
            addCeldaLeftBody(cursoNombre, innerTable, bodyFont);
            for (DocenteSeccion docenteSeccion : seccion.getDocenteSeccion()) {
                Docente docente = docenteSeccion.getDocente();
                String docenteStr = docente.getCodigo() + " Desconocido";
                if (docente.getPersona() != null) {
                    docenteStr = docente.getCodigo() + " " + docente.getPersona().getNombrePaternoMat();
                }
                addCeldaLeftBody(docenteStr, innerTable, bodyFont);
            }
        }
    }

    public void agregarHorarioConReserva(PdfPTable innerTable, List<HorarioAula> horariosConSeccion, Font bodyFont) {
        if (horariosConSeccion == null || horariosConSeccion.isEmpty()) {
            return;
        }
        List<ReservaAula> reservaAulas = horariosConSeccion.stream().map(x -> x.getReservaAula()).distinct().collect(Collectors.toList());

        for (ReservaAula reservaAula : reservaAulas) {
            Tramite tramite = reservaAula.getTramite();
            String tipoReserva = "Reserva Aula: " + reservaAula.getTipoReservaEnum().getValue();
            String solicitante = "Solicitante: ";
            if (tramite.isSolicitanteAlumno()) {
                solicitante = "Alumno " + tramite.getAlumno().getCodigo();
            } else if (tramite.isSolicitanteDocente()) {
                solicitante = "Docente " + tramite.getDocente().getCodigo();
            } else if (tramite.isSolicitanteEmpresa()) {
                solicitante = "Empresa " + tramite.getEmpresa().getDescripcion();
            } else if (tramite.isSolicitanteOficina()) {
                solicitante = "Oficina " + tramite.getOficina().getCodigo();
            } else if (tramite.isSolicitantePersona()) {
                solicitante = "Persona " + tramite.getPersona().getApellidosNombres();
            }
            addCeldaLeftBody(tipoReserva, innerTable, bodyFont);
            addCeldaLeftBody(solicitante, innerTable, bodyFont);
        }

    }

    private void addCeldaLeftBody(String contenido, PdfPTable table, Font bodyFont) {
        Phrase phr = new Phrase(contenido, bodyFont);
        PdfPCell cell = this.getCellLeftBody(phr);
        table.addCell(cell);
    }

    PdfPCell getCellLeftBody(Phrase phr) {
        PdfPCell cell = new PdfPCell(phr);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setPaddingLeft(0f);
        cell.setPaddingRight(0f);
        cell.setPaddingTop(0f);
        cell.setPaddingBottom(0f);
        return cell;
    }

    private void addCeldaCenterBody(String contenido, PdfPTable table, Font bodyFont) {
        this.addCeldaCenterBody(contenido, table, bodyFont, 1);
    }

    private void addCeldaCenterBody(String contenido, PdfPTable table, Font bodyFont, int coldspan) {
        Phrase phr = new Phrase(contenido, bodyFont);
        PdfPCell cell = new PdfPCell(phr);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setPaddingLeft(0f);
        cell.setPaddingRight(0f);
        cell.setPaddingTop(0f);
        cell.setPaddingBottom(0f);
        cell.setColspan(coldspan);
        table.addCell(cell);
    }

}
