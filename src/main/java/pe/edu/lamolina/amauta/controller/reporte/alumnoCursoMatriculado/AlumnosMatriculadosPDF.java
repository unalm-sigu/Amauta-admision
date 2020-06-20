package pe.edu.lamolina.amauta.controller.reporte.alumnoCursoMatriculado;

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
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import static org.springframework.web.servlet.mvc.method.annotation.SseEmitter.event;
import pe.albatross.zelpers.file.pdf.AbstractOnlyPdfView;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Curso;
import pe.edu.lamolina.model.academico.DocenteSeccion;
import pe.edu.lamolina.model.academico.MatriculaSeccion;
import pe.edu.lamolina.model.academico.Seccion;
import static pe.edu.lamolina.model.constantines.GlobalConstantine.PDF_LOGO_EPG_ALFA;
import static pe.edu.lamolina.model.constantines.GlobalConstantine.PDF_LOGO_UNALM;
import pe.edu.lamolina.model.enums.ModalidadEstudioEnum;

@Component
public class AlumnosMatriculadosPDF extends AbstractOnlyPdfView {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final String SUBJT_PREGRADO = "OFICINA DE ESTUDIOS Y REGISTROS ACADÉMICOS";
    private final String title = "REPORTE DE ALUMNO MATRICULADOS ";

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

        List<MatriculaSeccion> matriculaSeccions = (List<MatriculaSeccion>) model.get("matriculaSeccion");
        CicloAcademico ciclo = matriculaSeccions.get(0).getMatriculaResumen().getCicloAcademico();

        buildHeaderPaginaPrincipal(document);
        createAnexos(matriculaSeccions, document);

        document.newPage();
        DateTime today = new DateTime();

        String nombre = "Alumno Matriculados " + ciclo.getDescripcion() + "_" + today.toString("yyyyMMdd_HHmm");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + nombre + ".pdf\"");
        response.setHeader("Set-Cookie", "fileDownload=true; path=/");
    }

    private void buildHeaderPaginaPrincipal(Document document) throws DocumentException, DocumentException, BadElementException, IOException {

        Font font = new Font(Font.FontFamily.HELVETICA, 10, Font.NORMAL, BaseColor.BLACK);
        PdfPTable tablePdf;
        PdfPCell cell;
        float[] columnWidths = null;
        columnWidths = new float[]{20f, 80f};
        tablePdf = new PdfPTable(columnWidths);
        tablePdf.setWidths(columnWidths);
        tablePdf.setHorizontalAlignment(PdfPTable.ALIGN_CENTER);
        tablePdf.setSpacingAfter(0);
        tablePdf.setSpacingBefore(0);
        tablePdf.setPaddingTop(0);

        Image img = Image.getInstance(this.getClass().getResource(PDF_LOGO_UNALM));
        img.scalePercent(40F);
        cell = new PdfPCell(img);
        cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
        cell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
        cell.setPaddingBottom(0f);
        cell.setPaddingTop(0f);
        cell.setRowspan(4);
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setBorderColor(BaseColor.WHITE);

        tablePdf.addCell(cell);

        Paragraph parrafo = new Paragraph();
        parrafo.setFont(new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD));
        parrafo.add(this.title);
        parrafo.add(Chunk.NEWLINE);
        parrafo.setFont(new Font(Font.FontFamily.HELVETICA, 10, Font.NORMAL));
        parrafo.add(this.SUBJT_PREGRADO);

        cell = new PdfPCell(parrafo);
        cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
        cell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
        cell.setRowspan(4);
        cell.setBorder(0);
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setBorderColor(BaseColor.WHITE);

        tablePdf.addCell(cell);

        document.add(tablePdf);

        document.add(new Chunk(".", new Font(Font.FontFamily.COURIER, 1, Font.NORMAL, BaseColor.WHITE)));
    }

    private void tituloData(int posision, String texto, int colspan, PdfPTable tableBody, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(texto, font));
        cell.setBorder(PdfPCell.BOTTOM);
        cell.setHorizontalAlignment(posision);
        cell.setColspan(colspan);
        cell.setPaddingLeft(0f);
        cell.setPaddingRight(0f);
        cell.setPaddingTop(0f);
        cell.setBottom(PdfPCell.BOTTOM);
        cell.setPaddingBottom(4f);
        cell.setPaddingTop(4f);
        tableBody.addCell(cell);
    }

    private void createAnexos(List<MatriculaSeccion> matriculaSeccions, Document document) throws DocumentException, Exception {
        Font font = new Font(Font.FontFamily.HELVETICA, 10, Font.NORMAL, BaseColor.BLACK);
        Font fontAnexo = new Font(Font.FontFamily.HELVETICA, 8, Font.BOLD, BaseColor.BLACK);

        Paragraph paragraph = new Paragraph();
        paragraph.setFont(getTitle());
        paragraph.add("");
        paragraph.setAlignment(Element.ALIGN_CENTER);
        document.add(paragraph);

        PdfPTable table = new PdfPTable(3);
        float[] medidaCeldas = {1.55f, 1.70f, 8f};// tamañan por columna

        Map<Long, List<MatriculaSeccion>> mapReporte = TypesUtil.convertListToMapList("seccion.grupoSeccion.curso.id", matriculaSeccions);

        for (Long idCurso : mapReporte.keySet()) {
            List<MatriculaSeccion> matSecciones = mapReporte.get(idCurso);
            Curso curso = matSecciones.get(0).getSeccion().getGrupoSeccion().getCurso();

            PdfPTable tableTitulo = new PdfPTable(6);
            tableTitulo.setHorizontalAlignment(PdfPTable.ALIGN_CENTER);
            tableTitulo.setTotalWidth(220);
            tableTitulo.setLockedWidth(true);
            this.tituloData(PdfPCell.ALIGN_CENTER, "Curso:", 2, tableTitulo, font);
            this.tituloData(PdfPCell.ALIGN_LEFT, curso.getNombre(), 4, tableTitulo, font);

            Seccion seccion = matSecciones.get(0).getSeccion();

            this.tituloData(PdfPCell.ALIGN_CENTER, "Sección:", 2, tableTitulo, font);
            this.tituloData(PdfPCell.ALIGN_LEFT, seccion.getCodigo2(), 4, tableTitulo, font);

            int i = 1;
            for (DocenteSeccion docenteSeccion : seccion.getDocenteSeccion()) {

                this.tituloData(PdfPCell.ALIGN_CENTER, "Docente " + i + " :", 2, tableTitulo, font);
                if (docenteSeccion.getDocente().getPersona() != null) {
                    this.tituloData(PdfPCell.ALIGN_LEFT, docenteSeccion.getDocente().getPersona().getApellidosNombres(), 4, tableTitulo, font);
                } else {
                    this.tituloData(PdfPCell.ALIGN_LEFT, docenteSeccion.getDocente().getCodigo(), 4, tableTitulo, font);
                }
            }
            tableTitulo.setSpacingBefore(2f);
            tableTitulo.setSpacingAfter(2f);
            document.add(tableTitulo);
            document.add(new Chunk(".", new Font(Font.FontFamily.COURIER, 1, Font.NORMAL, BaseColor.WHITE)));

            table.setWidths(medidaCeldas);
            table.setWidthPercentage(80.00f);//todo el ancho de la pagina
            table.setSpacingBefore(2f);
            table.setSpacingAfter(2f);
            table.addCell(getStyleTitulo("FOTO", 8, "C"));
            table.addCell(getStyleTitulo("CÓDIGO", 8, "C"));
            table.addCell(getStyleTitulo("NOMBRE ALUMNO", 8, "C"));
            table.setHeaderRows(1);

            for (MatriculaSeccion matriculaSeccion : matSecciones) {
                Alumno alumno = matriculaSeccion.getMatriculaResumen().getAlumno();

                table.addCell(getImagenUrl(25, 20, alumno.getPersona().getFoto()));
                addCelda(alumno.getCodigo(), 1, table, fontAnexo, 1);
                StringBuilder st = new StringBuilder();
                st.append(alumno.getPersona().getApellidosNombres() + "\n");
                st.append(alumno.getCarrera().getNombre() + "\n");
                if (!alumno.getCarrera().getCodigo().equals(alumno.getCarrera().getFacultad().getCodigo())) {
                    st.append(alumno.getCarrera().getFacultad().getNombre() + "\n");
                }
                st.append(alumno.getPersona().getEmailCompania() + "\n");
                addCelda(st.toString(), 1, table, fontAnexo, 1);
            }
        }
        document.add(table);

    }

    private PdfPCell getImagenUrl(int scaleWidth, int scaleHeight, String url) throws Exception {
        Image image = Image.getInstance(new URL(url));
        image.scaleToFit(scaleWidth, scaleHeight);
        PdfPCell pdfPCell = new PdfPCell(image, true);
        pdfPCell.setBorderColor(BaseColor.BLACK);
        return pdfPCell;
    }

    public Font getTitle() {
        Font font = new Font();
        font.setFamily("TimesRoman");
        font.setSize(12);
        font.setColor(BaseColor.BLACK);
        font.setStyle(Font.BOLD);
        font.setStyle(Font.UNDERLINE);
        return font;
    }

    private void addCelda(String contenido, int colspan, PdfPTable table, Font bodyFont, int rolSpan) {
        Phrase phr = new Phrase(contenido, bodyFont);
        PdfPCell cell = new PdfPCell(phr);

        cell.setVerticalAlignment(Element.ALIGN_CENTER);
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
//        cell.setBackgroundColor(baseColor);
//        cell.setBorderColor(BaseColor.DARK_GRAY);
        cell.setPaddingLeft(2f);
        cell.setPaddingRight(0f);
        cell.setPaddingTop(10f);
        cell.setPaddingBottom(0f);
        cell.setColspan(colspan);
        cell.setRowspan(rolSpan);
        table.addCell(cell);
    }

    public PdfPCell getStyleTitulo(String data, float numero, String posicion) {
        Font font = new Font();
        font.setSize(numero);
        font.setColor(BaseColor.WHITE);
        font.setStyle(Font.BOLD);

        PdfPCell cell = new PdfPCell(new Paragraph(data, font));
        cell.setMinimumHeight(numero);
        cell.setBackgroundColor(BaseColor.DARK_GRAY);
        cell.getEffectivePaddingRight();
        //cell.setPaddingBottom(6.0f);
        //cell.setPaddingTop(6.0f);
        //cell.setBorder(1);
        if (posicion.equals("C")) {
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        }
        if (posicion.equals("R")) {
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        }
        if (posicion.equals("L")) {
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        }

        return cell;
    }

}
