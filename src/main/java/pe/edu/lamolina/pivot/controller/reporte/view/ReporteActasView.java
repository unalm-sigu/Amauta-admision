package pe.edu.lamolina.pivot.controller.reporte.view;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.view.AbstractView;
import pe.albatross.zelpers.file.excel.AbstractPOIExcelView;
import pe.albatross.zelpers.file.excel.ExcelHelper;
import pe.albatross.zelpers.file.excel.ExcelStyles;
import pe.edu.lamolina.pivot.controller.academico.cargaacademica.CargaAcademicaService;
import pe.edu.lamolina.pivot.model.academico.Alumno;
import pe.edu.lamolina.pivot.model.academico.AlumnoEvaluacion;
import pe.edu.lamolina.pivot.model.academico.CicloAcademico;
import pe.edu.lamolina.pivot.model.academico.Curso;
import pe.edu.lamolina.pivot.model.academico.Evaluacion;
import pe.edu.lamolina.pivot.model.academico.GrupoSeccion;
import pe.edu.lamolina.pivot.model.academico.MatriculaCurso;
import pe.edu.lamolina.pivot.model.academico.MatriculaSeccion;
import pe.edu.lamolina.pivot.model.academico.Seccion;
import pe.edu.lamolina.pivot.model.academico.SistemaNotas;

@Component
public class ReporteActasView extends AbstractPOIExcelView {

    @Autowired
    CargaAcademicaService cargaAcademicaService;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private static final String CONTENT_TYPE_XLSX = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
    private static final String CONTENT_TYPE_XLS = "application/vnd.ms-excel";

    @Override
    protected Workbook createWorkbook() {
        return new XSSFWorkbook();
    }

    @Override
    protected void buildExcelDocument(Map<String, Object> model, Workbook wb, HttpServletRequest request, HttpServletResponse response) throws Exception {
        Seccion seccion = (Seccion) model.get("seccion");
        GrupoSeccion grupoSeccion = (GrupoSeccion) model.get("grupoSeccion");
        CicloAcademico cicloAcademico = (CicloAcademico) model.get("cicloAcademico");
        Curso curso = (Curso) model.get("curso");

        List<MatriculaSeccion> matriculasSeccionByFilter = cargaAcademicaService.allMatriculaSeccionBySeccion(seccion);
        List<Evaluacion> evaluacionesBySeccionFinal = cargaAcademicaService.allEvaluacionesByTipoSeccion(seccion);
        Map<String, AlumnoEvaluacion> mapNotas = cargaAcademicaService.allAlumnoEvaluacionBySeccion(seccion.getId());
        Map matriculaCursoMap = cargaAcademicaService.getMapMatriculasCursoByCicloCurso(cicloAcademico, curso);
        SistemaNotas sistemaNotas = grupoSeccion.getPlanCalificacion().getSistemaNotas();

        List<String> rows = new ArrayList();
        int totalColumns = 1;

        String head = "Alumno";
        for (Evaluacion evaluacion : evaluacionesBySeccionFinal) {
            head += "|" + evaluacion.getTipoEvaluacion().getCodigo() + evaluacion.getNumero();
            totalColumns++;
        }
        if (sistemaNotas.isLetras()) {
            head += "|Creditos";
            totalColumns++;
        }
        if (sistemaNotas.isNumerico()) {
            totalColumns += 3;
            head += "|Avance NF|Acumulada NF|NotaFinal";
        }
        rows.add(head);

        StringBuilder sb;
        for (MatriculaSeccion matriculasSeccion : matriculasSeccionByFilter) {
            sb = new StringBuilder();
            Alumno alumno = matriculasSeccion.getMatriculaResumen().getAlumno();

            sb.append(alumno.getPersona().getApellidosNombres()).append("|");
            for (Evaluacion evaluacionFinal : evaluacionesBySeccionFinal) {
                StringBuilder key = new StringBuilder();
                key.append(alumno.getId());
                key.append("-");
                key.append(evaluacionFinal.getId());
                AlumnoEvaluacion alumnoEvaluacion = null;

                if (mapNotas.get(key.toString()) != null) {
                    alumnoEvaluacion = mapNotas.get(key.toString());
                }
                if (alumnoEvaluacion != null) {

                    StringBuilder nota = new StringBuilder();
                    if (StringUtils.isNotBlank(alumnoEvaluacion.getValorLetra())) {
                        nota.append(alumnoEvaluacion.getValorLetra()).append(" ");
                    }
                    if (!curso.isCreditosZero()) {
                        nota.append(alumnoEvaluacion.getNota());
                    }
                    sb.append(nota.toString()).append("|");
                } else {
                    sb.append("-").append("|");
                }
            }
            MatriculaCurso matriculaCurso = (MatriculaCurso) matriculaCursoMap.get(alumno.getId());
            if (sistemaNotas.isLetras()) {
                sb.append(matriculaCurso.getCreditos() != null ? matriculaCurso.getCreditos() : "").append("|");
            }
            if (sistemaNotas.isNumerico()) {
                sb.append(matriculaCurso.getNotaAvanceFull() != null ? matriculaCurso.getNotaAvanceFull() : "").append("|");
                sb.append(matriculaCurso.getNotaAcumuladaFull() != null ? matriculaCurso.getNotaAcumuladaFull() : "").append("|");
                sb.append(matriculaCurso.getNotaFinal() != null ? matriculaCurso.getNotaFinal() : "");
            }
            rows.add(sb.toString());
        }

        CellStyle cellHeader = ExcelStyles.getStyleHeader(wb);
        CellStyle cellBody = ExcelStyles.getStyleBody(wb);
        this.createSheet(wb, rows, totalColumns, "RecordActas", cellHeader, cellBody);

        String nombreReporte = "RecordActas ";

        response.setHeader("Content-Disposition", "attachment; filename=\"" + nombreReporte + ".xlsx\"");
        /*
        List<IngCarreColeSexo> ingresantes = (List<IngCarreColeSexo>) model.get("ingresantes");
        List<Carrera> carreras = (List<Carrera>) model.get("carreras");
        CicloPostula cicloInfo = (CicloPostula) model.get("cicloInfo");

        this.generateSheet(wb, ingresantes, carreras, cicloInfo);
        String fecha = new DateTime().toString("dd/MM/yyyy_H:mm");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + "Ingresantes_Carrera_Colegio_Genero_" + fecha + ".xlsx\"");
        response.setContentType(getContentType());

        ServletOutputStream out = response.getOutputStream();
        out.flush();
        wb.write(out);
        out.flush();*/
    }

    private void createSheet(Workbook workBook, List<String> rows, int columnas, String sheetName, CellStyle cellHeader, CellStyle cellBody) {
        Sheet sheet = workBook.createSheet(sheetName);
        boolean autosize = false;

        for (int i = 0; i < rows.size(); i++) {
            String fila = (String) rows.get(i);

            String[] argHeader = fila.split("\\|");

            StringTokenizer st = new StringTokenizer(fila, "|");
            Row row = sheet.createRow(i);
            int j = 0;

            boolean isHeader = false;
            boolean isHeaderTotal = false;
            boolean isHeaderSede = false;

            if (i == 0) {
                isHeader = true;
            }

            while (st.hasMoreTokens()) {
                String token = st.nextToken();

                if (isHeader) {
                    this.createCell(row, j, token, cellHeader);
                    if (isHeaderTotal) {
                        isHeader = false;
                    }
                } else {
                    this.createCell(row, j, token, cellBody);
                }
                j++;
            }
            if (i == 20) {
                for (int ii = 0; ii < columnas; ii++) {
                    sheet.autoSizeColumn((short) ii);
                }
                autosize = true;
            }
        }

        if (!autosize) {
            for (int i = 0; i < columnas; i++) {
                sheet.autoSizeColumn((short) i);
            }
        }

    }

    private void createCell(Row row, int cellNumber, String value, CellStyle style) {
        Cell cell = row.createCell(cellNumber);
        cell.setCellValue(value + "");
        if (style != null) {
            cell.setCellStyle(style);
        }
    }

    private void createHeader(ExcelHelper helper, CellStyle cellHeader, List<Evaluacion> evaluacionesBySeccionFinal) {
        String head = "Alumno|Carrera|Vacantes";

        for (Evaluacion evaluacion : evaluacionesBySeccionFinal) {
            head += evaluacion.getTipoEvaluacion().getCodigo() + evaluacion.getNumero() + "|";
        }

        head += "Avance NF|Acumulada NF|NotaFinal";
        StringTokenizer st = new StringTokenizer(head, "|");

        int i = 0;
        while (st.hasMoreTokens()) {
            String token = st.nextToken();
            helper.replaceStyle(2, i, cellHeader);
            helper.replaceVal(2, i, token);
            i++;
        }
    }

    private CellStyle getStyleHeader(Workbook workBook) {
        Font fontTitle = workBook.createFont();
        fontTitle.setFontName("Arial");
        fontTitle.setBoldweight(Font.BOLDWEIGHT_BOLD);
        fontTitle.setColor(IndexedColors.WHITE.getIndex());

        CellStyle cellHeader = workBook.createCellStyle();
        cellHeader.setFillForegroundColor(IndexedColors.GREY_50_PERCENT.getIndex());
        cellHeader.setFillPattern(CellStyle.SOLID_FOREGROUND);
        cellHeader.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
        cellHeader.setAlignment(CellStyle.ALIGN_CENTER);
        cellHeader.setFont(fontTitle);

        return cellHeader;

    }

    private CellStyle getStyleBody(Workbook workBook) {
        Font fontBody = workBook.createFont();
        fontBody.setFontName("Arial");
        fontBody.setColor(IndexedColors.BLACK.getIndex());
        short border = CellStyle.BORDER_THIN;

        CellStyle cellBody = workBook.createCellStyle();
        cellBody.setFont(fontBody);
        cellBody.setWrapText(true);
        cellBody.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
        cellBody.setBorderBottom(border);
        cellBody.setBorderLeft(border);
        cellBody.setBorderRight(border);
        cellBody.setBorderTop(border);

        return cellBody;
    }

}
