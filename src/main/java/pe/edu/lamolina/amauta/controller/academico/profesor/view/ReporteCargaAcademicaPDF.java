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
import static java.math.BigDecimal.ZERO;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import pe.albatross.zelpers.file.pdf.AbstractOnlyPdfView;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.model.academico.AnexoBoletin;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.DepartamentoAcademico;
import pe.edu.lamolina.model.academico.Docente;
import pe.edu.lamolina.model.academico.DocenteSeccion;
import pe.edu.lamolina.model.academico.Facultad;
import pe.edu.lamolina.model.academico.GrupoSeccion;
import pe.edu.lamolina.model.academico.Seccion;
import pe.edu.lamolina.model.enums.ModalidadEstudioEnum;
import pe.edu.lamolina.model.horario.HorarioSeccion;

@Component
public class ReporteCargaAcademicaPDF extends AbstractOnlyPdfView {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final String title = "Carga Académica";
    private final String autor = "UNIVERSIDAD NACIONAL AGRARIA LA MOLINA";
    private final String creator = "Universidad Nacional Agraria La Molina";
    private final String oficina = "Dirección de Estudios y Registros Académicos";
    private final BaseColor GRAY_LIGHT = new BaseColor(219, 219, 219);

    private final List<String> tituloItems = Arrays.asList(
            "Curso",
            "Sección",
            "Horario",
            "Gpo",
            "Aula",
            "% Carga",
            "Créd.",
            "Periodo clases",
            "Mat."
    );

    @Override
    protected void buildPdfMetadata(Map<String, Object> model, Document document, HttpServletRequest request) {

        document.addAuthor(this.autor);
        document.addCreationDate();
        document.addCreator(this.title);
        document.addTitle(this.title);
        document.addSubject(this.title);
        document.setPageSize(PageSize.A4);
        // marginLeft, marginRight, marginTop, marginBottom
        document.setMargins(30, 30, 75, 20);

    }

    @Override
    protected void buildPdfDocument(Map<String, Object> model, Document document, PdfWriter writer, HttpServletRequest request, HttpServletResponse response) throws Exception {

        HeaderReportePDF1 event = new HeaderReportePDF1(autor, oficina);
        writer.setPageEvent(event);

        this.createBody(model, document, writer);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd HHmm");
        String nombre = "Carga Académica " + sdf.format(new Date());
        response.setHeader("Content-Disposition", "attachment; filename=\"" + nombre + ".pdf\"");
    }

    private void createBody(Map<String, Object> model, Document document, PdfWriter writer) throws DocumentException {

        Font font;
        PdfPCell cell;
        PdfPTable tableBody;

        String tipoGrado = (String) model.get("tipoGrado");

        List<Docente> docentes = (List<Docente>) model.get("docentes");

        List<CicloAcademico> ciclos = (List<CicloAcademico>) model.get("cicloAcademicos");
        List<DocenteSeccion> docentesSecciones = (List<DocenteSeccion>) model.get("docentesSecciones");

        Map<Long, List<DocenteSeccion>> docentesSeccionesXciclo = docentesSecciones.stream()
                .collect(groupingBy(x -> x.getSeccion().getGrupoSeccion().getCicloAcademico().getId(), toList()));

        int countCiclosConInformacion = 0;

        for (CicloAcademico ciclo : ciclos) {

            List<DocenteSeccion> misDocentesSecciones = docentesSeccionesXciclo.get(ciclo.getId());

            if (misDocentesSecciones == null) {
                continue;
            }

            countCiclosConInformacion++;

            Map<Long, List<DocenteSeccion>> docentesSeccionesXdocente = misDocentesSecciones.stream().collect(groupingBy(x -> x.getDocente().getId(), toList()));

            List<HorarioSeccion> horarioSecciones = (List<HorarioSeccion>) model.get("horarioSecciones");

            Map<Long, List<HorarioSeccion>> horarioSeccionesXseccion = horarioSecciones.stream().collect(groupingBy(x -> x.getSeccion().getId(), toList()));

            int indice = 0;

            int size = docentes.size();

            for (Docente docente : docentes) {

                float[] columnWidths = new float[]{40f, 15f, 20f, 10f, 10f, 10f, 6f, 17f, 6f};
                tableBody = new PdfPTable(columnWidths);
                tableBody.setWidths(columnWidths);
                tableBody.setHorizontalAlignment(PdfPTable.ALIGN_LEFT);
                tableBody.setWidthPercentage(100);

                indice++;

                List<DocenteSeccion> profeSecciones = docentesSeccionesXdocente.getOrDefault(docente.getId(), new ArrayList());

                List<GrupoSeccion> grupoSecciones = allGpoSecciones(docente, profeSecciones, horarioSeccionesXseccion);
                if (grupoSecciones.isEmpty()) {
                    continue;
                }

                Total total = new Total();

                this.fillGrupos(grupoSecciones, total);

                if (!StringUtils.isEmpty(tipoGrado)) {
                    ModalidadEstudioEnum modalidadEstudioEnum = ModalidadEstudioEnum.valueOf(tipoGrado);
                    if (modalidadEstudioEnum == ModalidadEstudioEnum.EPG) {
                        total.grupoPregrado = new ArrayList();
                    }
                    if (modalidadEstudioEnum == ModalidadEstudioEnum.PRE) {
                        total.grupoPosgrado = new ArrayList();
                    }
                }

                if (total.grupoPosgrado.size() < 1 && total.grupoPregrado.size() < 1) {
                    continue;
                }

                DepartamentoAcademico departamentoAcademico = docente.getDepartamentoAcademico();
                Facultad facultad = departamentoAcademico.getFacultad();

                this.textCenter(tableBody, "CARGA ACADÉMICA  " + ciclo.getDescripcion());
                this.addSpace(tableBody);

                this.cellFullColumn(tableBody, "Facultad de " + facultad.getNombre());
                this.cellFullColumn(tableBody, "Departamento de " + departamentoAcademico.getNombre());
                this.cellFullColumn(tableBody, docente.getPersona().getApellidosNombres() + " - " + docente.getCodigo());

                if (total.grupoPregrado.size() > 0) {

                    this.cellSubtitleColumn(tableBody, "Cursos de Pregrado");

                    this.tituloItem(tableBody, 1, tituloItems);

                }

                for (GrupoSeccion grupoSeccion : total.grupoPregrado) {

                    this.fillSecciones(grupoSeccion, tableBody);

                }

                if (total.grupoPregrado.size() > 0) {

                    this.rowTotal(tableBody, total.creditosPregrado);

                }

                if (total.grupoPosgrado.size() > 0) {

                    this.cellSubtitleColumn(tableBody, "Cursos de Posgrado");

                    this.tituloItem(tableBody, 1, tituloItems);

                }

                for (GrupoSeccion grupoSeccion : total.grupoPosgrado) {

                    this.fillSecciones(grupoSeccion, tableBody);

                }

                if (total.grupoPosgrado.size() > 0) {

                    this.rowTotal(tableBody, total.creditosPosgrado);

                }

                if (total.grupoPregrado.size() <= 0 && total.grupoPosgrado.size() <= 0) {
                    this.cellFullColumn(tableBody, "Docente sin carga académica");
                }

                document.add(tableBody);

                document.newPage();

            }

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
        cell.setColspan(9);
        cell.setBorder(PdfPCell.NO_BORDER);
        tableBody.addCell(cell);

    }

    private void cellSubtitleColumn(PdfPTable tableBody, String str) {

        Font font = new Font(Font.FontFamily.TIMES_ROMAN, 8, Font.BOLD);
        PdfPCell cell = new PdfPCell(new Phrase(str, font));
        cell.setPaddingBottom(10f);
        cell.setPaddingTop(10f);
        cell.setColspan(9);
        cell.setBorder(PdfPCell.NO_BORDER);
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
        cell.setColspan(8);
        cell.setPaddingBottom(10f);
        cell.setPaddingTop(10f);
        cell.setBorder(PdfPCell.NO_BORDER);
        tableBody.addCell(cell);

    }

    private void fillSecciones(GrupoSeccion grupoSeccion, PdfPTable tableBody) {

        for (Seccion seccion : grupoSeccion.getSecciones()) {

            StringJoiner sj = new StringJoiner(" ");
            sj.add(grupoSeccion.getCurso().getNombre());
            sj.add("\n");
            sj.add(grupoSeccion.getCurso().getCodigo());
            sj.add(" ");
            sj.add(grupoSeccion.getCurso().getTpc());
            if (grupoSeccion.getCursoDirigido()) {
                sj.add(" ");
                sj.add("Curso Dirigido");
            }

            this.textCell(tableBody, sj.toString(), false);

            sj = new StringJoiner("\n");
            sj.add(seccion.getCodigo2());
            sj.add(tipoSeccion(seccion));

            this.textCellCenter(tableBody, sj.toString());

            this.textCell(tableBody, seccion.getHorarioTexto(), false);
            this.textCellCenter(tableBody, (seccion.getGrupoHoras() != null ? seccion.getGrupoHoras().getCodigo() : ""));
            this.textCellCenter(tableBody, (seccion.getAula() != null ? seccion.getAula().getCodigo() : ""));

            for (DocenteSeccion docenteSeccion : seccion.getDocenteSeccion()) {

                String porcentajeCarga = docenteSeccion.getPorcentajeCarga() != null ? docenteSeccion.getPorcentajeCarga().setScale(2, BigDecimal.ROUND_HALF_EVEN).toString() : "";
                BigDecimal creditosCarga = docenteSeccion.getCreditosCarga() != null ? docenteSeccion.getCreditosCarga().setScale(2, BigDecimal.ROUND_HALF_EVEN) : ZERO;
                //String creditosCarga = docenteSeccion.getCreditosCarga() != null ? docenteSeccion.getCreditosCarga().setScale(2, BigDecimal.ROUND_HALF_EVEN).toString() : "";
                String credito;
                if (grupoSeccion.getCursoDirigido()) {
                    if (creditosCarga.compareTo(ZERO) == 0) {
                        credito = "";
                    } else {
                        credito = creditosCarga.multiply(new BigDecimal(0.33)).setScale(2, BigDecimal.ROUND_HALF_EVEN).toString();
                    }
                } else {
                    credito = creditosCarga.toString();
                }

                this.textCell(tableBody, porcentajeCarga, true);
                this.textCell(tableBody, credito, true);
                //this.textCell(tableBody, creditosCarga, true);

                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy");

                sj = new StringJoiner(" ");
                if (docenteSeccion.getFechaInicio() != null) {
                    sj.add("Desde el ");
                    sj.add(sdf.format(docenteSeccion.getFechaInicio()));
                }
                sj.add("\n");
                if (docenteSeccion.getFechaFin() != null) {
                    sj.add("Hasta el ");
                    sj.add(sdf.format(docenteSeccion.getFechaFin()));
                }

                this.textCellCenter(tableBody, sj.toString());

            }

            this.textCellCenter(tableBody, seccion.getMatriculados().toString());

        }
    }

    private void rowTotal(PdfPTable tableBody, BigDecimal creditos) {

        Font font = new Font(Font.FontFamily.TIMES_ROMAN, 8, Font.NORMAL);
        PdfPCell cell = new PdfPCell(new Phrase("Total créditos carga", font));
        cell.setColspan(4);
        cell.setBorder(PdfPCell.NO_BORDER);
        cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
        tableBody.addCell(cell);

        String creditosStr = creditos != null ? creditos.setScale(2, BigDecimal.ROUND_HALF_EVEN).toString() : "";

        font = new Font(Font.FontFamily.TIMES_ROMAN, 8, Font.NORMAL);
        cell = new PdfPCell(new Phrase(creditosStr, font));
        cell.setColspan(1);
        cell.setBorder(PdfPCell.NO_BORDER);
        cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
        tableBody.addCell(cell);

        font = new Font(Font.FontFamily.TIMES_ROMAN, 8, Font.NORMAL);
        cell = new PdfPCell(new Phrase("", font));
        cell.setColspan(2);
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

    private void textCellCenter(PdfPTable tableBody, String txt) {
        Font font = new Font(Font.FontFamily.TIMES_ROMAN, 8, Font.NORMAL);
        PdfPCell cell = new PdfPCell(new Phrase(txt, font));
        cell.setPaddingBottom(5f);
        cell.setPaddingTop(5f);
        cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
        cell.setBorderColor(GRAY_LIGHT);
        cell.setRowspan(1);
        tableBody.addCell(cell);
    }

    public class Total {

        public BigDecimal creditosPregrado = ZERO;
        public BigDecimal creditosPosgrado = ZERO;

        public List<GrupoSeccion> grupoPregrado = new ArrayList();
        public List<GrupoSeccion> grupoPosgrado = new ArrayList();

        public Total() {
            creditosPregrado = ZERO;
            creditosPosgrado = ZERO;
            grupoPregrado = new ArrayList();
            grupoPosgrado = new ArrayList();
        }
    }

    private String tipoSeccion(Seccion seccion) {
        if (seccion.getTipoSeccionEnum().getValue().indexOf(" ") < 0) {
            return seccion.getTipoSeccionEnum().getValue();
        }
        return seccion.getTipoSeccionEnum().getValue().split(" ")[0];

    }

    private List<HorarioSeccion> allBySecciones(List<Seccion> secciones, Map<Long, List<HorarioSeccion>> horarioSeccionesXseccion) {
        List<HorarioSeccion> horarioSecciones = new ArrayList();
        for (Seccion seccion : secciones) {
            horarioSecciones.addAll(horarioSeccionesXseccion.getOrDefault(seccion.getId(), new ArrayList()));
        }
        return horarioSecciones;
    }

    private void fillGrupos(List<GrupoSeccion> grupoSecciones, Total total) {
        for (GrupoSeccion grupoSeccion : grupoSecciones) {

            AnexoBoletin anexoSup = grupoSeccion.getAnexoBoletin().getAnexoSuperior();

            List<Seccion> secciones = grupoSeccion.getSecciones();
            for (Seccion seccion : secciones) {
                List<DocenteSeccion> profesSeccion = seccion.getDocenteSeccion();
                for (DocenteSeccion profeSecc : profesSeccion) {
                    BigDecimal creditos;
                    if (grupoSeccion.getCursoDirigido()) {
                        creditos = profeSecc.getCreditosCarga().multiply(new BigDecimal(0.33));
                    } else {
                        creditos = profeSecc.getCreditosCarga();
                    }
                    if (profeSecc.getCreditosCarga() == null) {
                        creditos = ZERO;
                    }
                    if (anexoSup.isAnexoCursosPostgrado()) {
                        total.creditosPosgrado = total.creditosPosgrado.add(creditos);
                    } else {
                        total.creditosPregrado = total.creditosPregrado.add(creditos);
                    }
                }
            }

            if (anexoSup.isAnexoCursosPostgrado()) {
                total.grupoPosgrado.add(grupoSeccion);
            } else {
                total.grupoPregrado.add(grupoSeccion);
            }
        }
    }

    public List<GrupoSeccion> allGpoSecciones(Docente docente, List<DocenteSeccion> profeSecciones, Map<Long, List<HorarioSeccion>> horarioSeccionesXseccion) {
        Map<Long, GrupoSeccion> mapGpoSecc = new LinkedHashMap();
        List<Seccion> secciones = new ArrayList();

        for (DocenteSeccion profeSecc : profeSecciones) {
            Seccion secc = profeSecc.getSeccion();
            secc.setDocenteSeccion(new ArrayList());
            secc.getDocenteSeccion().add(profeSecc);
            GrupoSeccion gpoSeccBD = secc.getGrupoSeccion();
            GrupoSeccion gpoSecc = mapGpoSecc.get(gpoSeccBD.getId());
            if (gpoSecc == null) {
                mapGpoSecc.put(gpoSeccBD.getId(), gpoSeccBD);
                gpoSecc = gpoSeccBD;
                gpoSecc.setSecciones(new ArrayList());
            }
            gpoSecc.getSecciones().add(secc);
            secc.setGrupoSeccion(gpoSecc);
            secciones.add(secc);
        }

        List<HorarioSeccion> horarios = allBySecciones(secciones, horarioSeccionesXseccion);

        Map<Long, List<HorarioSeccion>> mapHorarios = TypesUtil.convertListToMapList("seccion.id", horarios);

        for (Seccion secc : secciones) {
            secc.setHorarioSeccion(mapHorarios.getOrDefault(secc.getId(), new ArrayList()));
        }

        return new ArrayList(mapGpoSecc.values());
    }

}
