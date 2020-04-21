package pe.edu.lamolina.amauta.controller.programacionhorarios.gposeccion.reporte;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import pe.albatross.zelpers.miscelanea.ObjectUtil;
import pe.edu.lamolina.model.academico.AnexoBoletin;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Curso;
import pe.edu.lamolina.model.academico.DocenteSeccion;
import pe.edu.lamolina.model.academico.GrupoSeccion;
import pe.edu.lamolina.model.academico.Seccion;
import pe.edu.lamolina.model.enums.SeccionEstadoEnum;
import static pe.edu.lamolina.model.enums.TipoSeccionEnum.TCUR;
import static pe.edu.lamolina.model.enums.TipoSeccionEnum.TEO;
import pe.edu.lamolina.model.misc.Acumulador;
import pe.edu.lamolina.amauta.zelper.pdf.AbstractOnlyPdfView;

@Component
public class BoletinPDF extends AbstractOnlyPdfView {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final String title = "Programación de Horarios";

    @Override
    protected void buildPdfMetadata(Map<String, Object> model, Document document, HttpServletRequest request) {

        document.addAuthor("AlbatrossCloud");
        document.addCreationDate();
        document.addCreator("AlbatrossCloud");
        document.addTitle(this.title);
        document.addSubject(this.title);
        document.setPageSize(PageSize.A4);
        document.setMargins(10, 10, 30, 30);

    }

    @Override
    protected void buildPdfDocument(Map<String, Object> model, Document document, PdfWriter writer, HttpServletRequest request, HttpServletResponse response) throws Exception {

        CicloAcademico ciclo = (CicloAcademico) model.get("ciclo");
        List<AnexoBoletin> anexosSuper = (List<AnexoBoletin>) model.get("anexosSuper");

        createTitleMain(ciclo, document);

        HeaderFooterBoletinPDFEvent event = new HeaderFooterBoletinPDFEvent(ciclo.getDescripcion(), "");
        writer.setPageEvent(event);

        Acumulador acumulador = new Acumulador(4);
        createAnexos(anexosSuper, acumulador, document, event);

        document.newPage();
        DateTime today = new DateTime();

        String nombre = "Boletin" + ciclo.getDescripcion() + "_" + today.toString("yyyyMMdd_HHmm");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + nombre + ".pdf\"");
        response.setHeader("Set-Cookie", "fileDownload=true; path=/");
    }

    private void createAnexos(List<AnexoBoletin> anexosSuper, Acumulador acumulador, Document document, HeaderFooterBoletinPDFEvent event) throws DocumentException {
        Font fontSuper = new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD, BaseColor.BLUE);
        Font fontAnexo = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD, BaseColor.BLACK);

        int loopPadre = 0;
        for (AnexoBoletin anexoPadre : anexosSuper) {
            if (loopPadre > 0) {
                document.newPage();
                acumulador = new Acumulador(0);
            }

            Paragraph parrafo = new Paragraph(anexoPadre.getNombre(), fontSuper);
            parrafo.setAlignment(Element.ALIGN_CENTER);
            parrafo.setPaddingTop(0f);
            document.add(parrafo);
            acumulador.incrementar();

            int loopHijo = 0;
            List<AnexoBoletin> anexosHijos = anexoPadre.getAnexosBoletinHijos();
            for (AnexoBoletin anexoHijo : anexosHijos) {
                event.setAnexo(anexoHijo.getNombre());
                if (loopHijo > 0) {
                    int nuevosRows = nuevosRegistrosAnexo(anexoHijo);
                    if (acumulador.getValor() + nuevosRows > 35) {
                        document.newPage();
                        acumulador = new Acumulador(0);
                    }
                }

                PdfPTable table = createTable(10);
                addCeldaAnexo(anexoHijo.getNombre(), 10, table, fontAnexo);
                document.add(table);
                acumulador.incrementar();

                List<GrupoSeccion> gposSecciones = anexoHijo.getGruposSecciones();
                createCursos(gposSecciones, acumulador, document);
                loopHijo++;
            }

            loopPadre++;
        }

    }

    private void createCursos(List<GrupoSeccion> gposSeccionesAnexo, Acumulador acumulador, Document document) throws DocumentException {
        Font fontCurso = new Font(Font.FontFamily.HELVETICA, 10, Font.NORMAL, BaseColor.BLACK);
        Font fontHeader = new Font(Font.FontFamily.HELVETICA, 8, Font.BOLD, BaseColor.WHITE);
        Font fontConte = new Font(Font.FontFamily.HELVETICA, 8, Font.NORMAL, BaseColor.BLACK);
        Font fontHorario = new Font(Font.FontFamily.HELVETICA, 6, Font.NORMAL, BaseColor.BLACK);

        List<Curso> cursos = gposSeccionesAnexo.stream().map(x -> x.getCurso()).distinct().collect(Collectors.toList());
        Collections.sort(cursos, (c1, c2) -> c1.getNombre().compareTo(c2.getNombre()));

        PdfPTable table = createTable(10);
        createHeaderTable(table, fontHeader);
        acumulador.incrementar();

        for (Curso curso : cursos) {
            String infoCurso = curso.getCodigo() + " ";
            infoCurso += curso.getTpc() == null ? "" : curso.getTpc() + "   ";
            infoCurso += curso.getNombre();

            List<GrupoSeccion> gposSecciones = curso.getGrupoSeccion();
            {
                GrupoSeccion gpoSecc = gposSecciones.get(0);
                int nuevosRows = nuevosRegistrosGpoSecc(gpoSecc) + 1;
                if (nuevosRows + acumulador.getValor() > 35) {
                    document.add(table);

                    document.newPage();
                    acumulador = new Acumulador(0);

                    table = createTable(10);
                    createHeaderTable(table, fontHeader);
                    acumulador.incrementar();
                }
            }

            addCeldaCurso(infoCurso, 10, table, fontCurso);
            acumulador.incrementar();

            Collections.sort(gposSecciones, (gs1, gs2) -> gs1.getCodigo2().compareTo(gs2.getCodigo2()));

            for (GrupoSeccion gpoSecc : gposSecciones) {
                int nuevosRows = nuevosRegistrosGpoSecc(gpoSecc);
                if (nuevosRows + acumulador.getValor() > 35) {
                    document.add(table);

                    document.newPage();
                    acumulador = new Acumulador(0);

                    table = createTable(10);
                    createHeaderTable(table, fontHeader);
                    acumulador.incrementar();

                    addCeldaCurso(infoCurso, 10, table, fontCurso);
                    acumulador.incrementar();
                }

                acumulador.incrementar(nuevosRows);

                List<Seccion> secciones = gpoSecc.getSecciones();
                Collections.sort(secciones, (sec1, sec2) -> sec1.getCodigo2().compareTo(sec2.getCodigo2()));

                int loopSecc = 0;
                for (Seccion secc : secciones) {
                    loopSecc++;
                    BaseColor colorBordeBlack = BaseColor.BLACK;
                    BaseColor colorBordeGray = BaseColor.LIGHT_GRAY;
                    BaseColor colorBordeTop = (loopSecc == 1) ? BaseColor.BLACK : BaseColor.LIGHT_GRAY;
                    BaseColor colorBordeBottom = (loopSecc == secciones.size()) ? BaseColor.BLACK : BaseColor.LIGHT_GRAY;

                    String gpo = (String) ObjectUtil.getParentTree(secc, "grupoHoras.codigo");
                    String aula = (String) ObjectUtil.getParentTree(secc, "aula.codigo");
                    boolean esTeoria = secc.getTipoSeccionEnum() == TEO || secc.getTipoSeccionEnum() == TCUR;
                    boolean hayVacantes = secc.getVacantes() != null && secc.getMatriculados() != null;

                    String gpoTeo = esTeoria ? gpo : "";
                    String gpoPra = esTeoria ? "" : gpo;
                    String aulaTeo = esTeoria ? aula : "";
                    String aulaPra = esTeoria ? "" : aula;
                    String horario = secc.getHorarioTexto();
                    String vac = hayVacantes ? (secc.getVacantes() + "/" + secc.getMatriculados()) : "";

                    List<DocenteSeccion> profesSecc = secc.getDocenteSeccion();
                    int rowspan = profesSecc.size() > 1 ? profesSecc.size() : 1;

                    addCeldaConte(gpoTeo, colorBordeTop, colorBordeBottom, colorBordeBlack, colorBordeGray, Element.ALIGN_CENTER, rowspan, table, fontConte);
                    addCeldaConte(aulaTeo, colorBordeTop, colorBordeBottom, colorBordeGray, colorBordeGray, Element.ALIGN_CENTER, rowspan, table, fontConte);
                    addCeldaSeccion(secc, colorBordeTop, colorBordeBottom, colorBordeGray, colorBordeGray, rowspan, table, fontConte);
                    addCeldaConte(gpoPra, colorBordeTop, colorBordeBottom, colorBordeGray, colorBordeGray, Element.ALIGN_CENTER, rowspan, table, fontConte);
                    addCeldaConte(aulaPra, colorBordeTop, colorBordeBottom, colorBordeGray, colorBordeGray, Element.ALIGN_CENTER, rowspan, table, fontConte);

                    if (profesSecc.isEmpty()) {
                        addCeldaConte("", colorBordeTop, colorBordeBottom, colorBordeGray, colorBordeGray, Element.ALIGN_LEFT, 1, table, fontConte);
                        addCeldaConte("", colorBordeTop, colorBordeBottom, colorBordeGray, colorBordeGray, Element.ALIGN_CENTER, 1, table, fontConte);
                        addCeldaConte("", colorBordeTop, colorBordeBottom, colorBordeGray, colorBordeGray, Element.ALIGN_CENTER, 1, table, fontConte);

                    } else {
                        for (DocenteSeccion profeSecc : profesSecc) {
                            String profe = getProfesor(profeSecc);
                            String periodo = getPeriodoProfe(profeSecc);
                            String carga = profeSecc.getPorcentajeCarga() == null ? "" : (profeSecc.getPorcentajeCarga() + "%");

                            BaseColor colorBordeTopProfe = colorBordeTop;
                            BaseColor colorBordeBottomProfe = profesSecc.size() > 1 ? BaseColor.LIGHT_GRAY : colorBordeBottom;

                            addCeldaConte(profe, colorBordeTopProfe, colorBordeBottomProfe, colorBordeGray, colorBordeGray, Element.ALIGN_LEFT, 1, table, fontConte);
                            addCeldaConte(carga, colorBordeTopProfe, colorBordeBottomProfe, colorBordeGray, colorBordeGray, Element.ALIGN_CENTER, 1, table, fontConte);
                            addCeldaConte(periodo, colorBordeTopProfe, colorBordeBottomProfe, colorBordeGray, colorBordeGray, Element.ALIGN_CENTER, 1, table, fontHorario);
                            break;
                        }
                    }

                    addCeldaHorario(horario, colorBordeTop, colorBordeBottom, colorBordeGray, colorBordeGray, Element.ALIGN_LEFT, rowspan, table, fontHorario);
                    addCeldaConte(vac, colorBordeTop, colorBordeBottom, colorBordeGray, colorBordeBlack, Element.ALIGN_CENTER, rowspan, table, fontConte);

                    if (profesSecc.size() > 1) {
                        int loopProfe = 0;
                        for (DocenteSeccion profeSecc : profesSecc) {
                            loopProfe++;
                            if (loopProfe == 1) {
                                continue;
                            }

                            String profe = getProfesor(profeSecc);
                            String periodo = getPeriodoProfe(profeSecc);
                            String carga = profeSecc.getPorcentajeCarga() == null ? "" : (profeSecc.getPorcentajeCarga() + "%");

                            BaseColor colorBordeTopProfe = BaseColor.LIGHT_GRAY;
                            BaseColor colorBordeBottomProfe = loopProfe == profesSecc.size() ? colorBordeBottom : BaseColor.LIGHT_GRAY;

                            addCeldaConte(profe, colorBordeTopProfe, colorBordeBottomProfe, colorBordeGray, colorBordeGray, Element.ALIGN_LEFT, 1, table, fontConte);
                            addCeldaConte(carga, colorBordeTopProfe, colorBordeBottomProfe, colorBordeGray, colorBordeGray, Element.ALIGN_CENTER, 1, table, fontConte);
                            addCeldaConte(periodo, colorBordeTopProfe, colorBordeBottomProfe, colorBordeGray, colorBordeGray, Element.ALIGN_CENTER, 1, table, fontHorario);
                        }

                    }

                }
            }

        }

        document.add(table);

    }

    private String getPeriodoProfe(DocenteSeccion profeSecc) {
        if (profeSecc.getFechaInicio() == null) {
            return "";
        }
        if (profeSecc.getFechaFin() == null) {
            return "";
        }

        String periodo = new DateTime(profeSecc.getFechaInicio()).toString("dd/MM/yyyy");
        periodo += " al ";
        periodo += new DateTime(profeSecc.getFechaFin()).toString("dd/MM/yyyy");
        return periodo;
    }

    private String getProfesor(DocenteSeccion profeSecc) {
        String codigo = profeSecc.getDocente().getCodigo() + " ";
        String persona = (String) ObjectUtil.getParentTree(profeSecc, "docente.persona.nomPaternoMat");
        persona = (persona == null) ? "Desconocido" : persona;
        return codigo + " " + persona;
    }

    private void createTitleMain(CicloAcademico ciclo, Document document) throws DocumentException {
        Font fontUni = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD, BaseColor.BLUE);
        Font fontTitle = new Font(Font.FontFamily.HELVETICA, 16, Font.BOLD, BaseColor.BLUE);
        Paragraph parrafo = new Paragraph("Universidad Nacional Agraria La Molina", fontUni);
        parrafo.setAlignment(Element.ALIGN_CENTER);
        parrafo.setPaddingTop(0f);
        parrafo.setSpacingAfter(0f);
        parrafo.setSpacingBefore(0f);
        document.add(parrafo);

        parrafo = new Paragraph("Programación de Horarios " + ciclo.getDescripcion(), fontTitle);
        parrafo.setAlignment(Element.ALIGN_CENTER);
        parrafo.setPaddingTop(0f);
        parrafo.setSpacingAfter(0f);
        parrafo.setSpacingBefore(0f);
        document.add(parrafo);
    }

    private PdfPTable createTable(int columnTotal) throws DocumentException {

        PdfPTable table = new PdfPTable(columnTotal);
        table.setWidths(new int[]{3, 3, 3, 3, 3, 6, 3, 4, 5, 3});
        table.setTotalWidth(500);
        table.setLockedWidth(true);
        table.setSpacingAfter(0f);
        table.setSpacingBefore(0f);
        table.setPaddingTop(0f);

        return table;
    }

    private void addCeldaHorario(
            String contenido,
            BaseColor colorBordeTop,
            BaseColor colorBordeBottom,
            BaseColor colorBordeLeft,
            BaseColor colorBordeRight,
            int align,
            int rowspan, PdfPTable table, Font bodyFont) {

        PdfPCell cell = new PdfPCell();
        float padding = 4f;

        String[] prafes = contenido.split(" y ");
        for (int i = 0; i < prafes.length; i++) {
            Paragraph p = new Paragraph(prafes[i].replaceAll(" de ", " "), bodyFont);
            p.setAlignment(Element.ALIGN_CENTER);
            p.setPaddingTop(0f);
            if (i == prafes.length - 1) {
                p.setSpacingAfter(padding);
            }
            cell.addElement(p);
        }

        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setUseVariableBorders(true);
        cell.setBorderColorTop(colorBordeTop);
        cell.setBorderColorBottom(colorBordeBottom);
        cell.setBorderColorLeft(colorBordeLeft);
        cell.setBorderColorRight(colorBordeRight);
        cell.setRowspan(rowspan);
        table.addCell(cell);
    }

    private void addCeldaSeccion(
            Seccion seccion,
            BaseColor colorBordeTop,
            BaseColor colorBordeBottom,
            BaseColor colorBordeLeft,
            BaseColor colorBordeRight,
            int rowspan, PdfPTable table, Font bodyFont) {

        Font fontDirigido = new Font(Font.FontFamily.HELVETICA, 6, Font.BOLD, BaseColor.BLUE);
        Font fontDanger = new Font(Font.FontFamily.HELVETICA, 6, Font.BOLD, BaseColor.RED);
        Font fontWarning = new Font(Font.FontFamily.HELVETICA, 6, Font.BOLD, BaseColor.ORANGE);
        float padding = 4f;

        PdfPCell cell = new PdfPCell();
        {
            Paragraph p = new Paragraph(seccion.getCodigo2(), bodyFont);
            p.setAlignment(Element.ALIGN_CENTER);
            p.setPaddingTop(0f);

            if (seccion.getEstadoEnum() == SeccionEstadoEnum.ACT
                    && !seccion.getGrupoSeccion().getCursoDirigido()) {
                p.setSpacingAfter(padding);
            }
            cell.addElement(p);
        }

        if (seccion.getEstadoEnum() == SeccionEstadoEnum.ACT) {
        } else if (seccion.getEstadoEnum() == SeccionEstadoEnum.BLO) {
            Paragraph p = new Paragraph(seccion.getEstadoEnum().getValue(), fontWarning);
            p.setAlignment(Element.ALIGN_CENTER);
            p.setPaddingTop(0f);

            if (!seccion.getGrupoSeccion().getCursoDirigido()) {
                p.setSpacingAfter(padding);
            }
            cell.addElement(p);

        } else {
            Paragraph p = new Paragraph(seccion.getEstadoEnum().getValue(), fontDanger);
            p.setAlignment(Element.ALIGN_CENTER);
            p.setPaddingTop(0f);

            if (!seccion.getGrupoSeccion().getCursoDirigido()) {
                p.setSpacingAfter(padding);
            }
            cell.addElement(p);
        }

        if (seccion.getGrupoSeccion().getCursoDirigido()) {
            Paragraph p = new Paragraph("Dirigido", fontDirigido);
            p.setAlignment(Element.ALIGN_CENTER);
            p.setPaddingTop(0f);
            p.setSpacingAfter(padding);
            cell.addElement(p);
        }

        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setUseVariableBorders(true);
        cell.setBorderColorTop(colorBordeTop);
        cell.setBorderColorBottom(colorBordeBottom);
        cell.setBorderColorLeft(colorBordeLeft);
        cell.setBorderColorRight(colorBordeRight);
        cell.setPaddingTop(0f);
        cell.setRowspan(rowspan);
        table.addCell(cell);
    }

    private void addCeldaConte(
            String contenido,
            BaseColor colorBordeTop,
            BaseColor colorBordeBottom,
            BaseColor colorBordeLeft,
            BaseColor colorBordeRight,
            int align,
            int rowspan, PdfPTable table, Font bodyFont) {

        Phrase phr = new Phrase(contenido, bodyFont);
        PdfPCell cell = new PdfPCell(phr);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setHorizontalAlignment(align);
        cell.setUseVariableBorders(true);
        cell.setBorderColorTop(colorBordeTop);
        cell.setBorderColorBottom(colorBordeBottom);
        cell.setBorderColorLeft(colorBordeLeft);
        cell.setBorderColorRight(colorBordeRight);
        cell.setRowspan(rowspan);
        table.addCell(cell);
    }

    private void addCeldaHeader(String contenido, PdfPTable table, Font bodyFont) {
        Phrase phr = new Phrase(contenido, bodyFont);
        PdfPCell cell = new PdfPCell(phr);
        BaseColor baseColor = new BaseColor(46, 64, 83);

        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setBackgroundColor(baseColor);
        cell.setBorderColor(baseColor);
        cell.setPaddingLeft(0f);
        cell.setPaddingRight(0f);
        cell.setPaddingTop(1f);
        cell.setPaddingBottom(4f);
        table.addCell(cell);
    }

    private void addCeldaCurso(String contenido, int colspan, PdfPTable table, Font bodyFont) {
        Phrase phr = new Phrase(contenido, bodyFont);
        PdfPCell cell = new PdfPCell(phr);
        BaseColor baseColor = new BaseColor(204, 209, 209);

        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setBackgroundColor(baseColor);
        cell.setBorderColor(BaseColor.DARK_GRAY);
        cell.setPaddingLeft(0f);
        cell.setPaddingRight(0f);
        cell.setPaddingTop(2f);
        cell.setPaddingBottom(6f);
        cell.setColspan(colspan);
        table.addCell(cell);
    }

    private void addCeldaAnexo(String contenido, int colspan, PdfPTable table, Font bodyFont) {
        Phrase phr = new Phrase(contenido, bodyFont);
        PdfPCell cell = new PdfPCell(phr);

        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell.setBorderWidth(0f);
        cell.setPaddingLeft(0f);
        cell.setPaddingRight(0f);
        cell.setPaddingTop(20f);
        cell.setPaddingBottom(6f);
        cell.setColspan(colspan);
        table.addCell(cell);
    }

    private void createHeaderTable(PdfPTable table, Font fontHeader) {
        addCeldaHeader("Teoría", table, fontHeader);
        addCeldaHeader("Aula", table, fontHeader);
        addCeldaHeader("Sección", table, fontHeader);
        addCeldaHeader("Práct.", table, fontHeader);
        addCeldaHeader("Aula", table, fontHeader);
        addCeldaHeader("Profesor", table, fontHeader);
        addCeldaHeader("%", table, fontHeader);
        addCeldaHeader("Periodo", table, fontHeader);
        addCeldaHeader("Horario", table, fontHeader);
        addCeldaHeader("Vac/Mat", table, fontHeader);
    }

    private int nuevosRegistrosGpoSecc(GrupoSeccion gpoSecc) {
        List<Seccion> secciones = gpoSecc.getSecciones();
        int cant = secciones.size();
        for (Seccion seccion : secciones) {
            List<DocenteSeccion> profesSecc = seccion.getDocenteSeccion();
            cant += profesSecc.size() > 1 ? profesSecc.size() - 1 : 0;
        }
        return cant;
    }

    private int nuevosRegistrosAnexo(AnexoBoletin anexo) {
        int cant = 2;
        List<GrupoSeccion> gposSecciones = anexo.getGruposSecciones();
        List<Curso> cursos = gposSecciones.stream().map(x -> x.getCurso()).distinct().collect(Collectors.toList());
        cant += cursos.size();
        for (GrupoSeccion gpoSecc : gposSecciones) {
            cant += nuevosRegistrosGpoSecc(gpoSecc);
        }
        return cant;
    }

}
