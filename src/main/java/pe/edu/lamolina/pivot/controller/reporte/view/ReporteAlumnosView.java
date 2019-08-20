package pe.edu.lamolina.pivot.controller.reporte.view;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pe.albatross.zelpers.file.excel.AbstractPOIExcelView;
import pe.albatross.zelpers.file.excel.ExcelHelper;
import pe.albatross.zelpers.file.excel.ExcelStyles;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Curso;
import pe.edu.lamolina.model.academico.Docente;
import pe.edu.lamolina.model.academico.Evaluacion;
import pe.edu.lamolina.model.academico.GrupoSeccion;
import pe.edu.lamolina.model.academico.MatriculaSeccion;
import pe.edu.lamolina.model.academico.Seccion;
import pe.edu.lamolina.pivot.controller.docente.notasacademicas.NotaAcademicaService;

@Component
public class ReporteAlumnosView extends AbstractPOIExcelView {

    @Autowired
    NotaAcademicaService cargaAcademicaService;

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
        Docente docente = (Docente) model.get("docente");
        Curso curso = (Curso) model.get("curso");

        List<MatriculaSeccion> matriculasSeccionByFilter = cargaAcademicaService.allMatriculaSeccionBySeccion(seccion);

        List<String> rows = new ArrayList();
        int totalColumns = 5;

        String head = "item|Código|Nombres Completos|Prioridad|FAC|ESP";

        rows.add(head);

        StringBuilder sb;
        int count = 1;
        for (MatriculaSeccion matriculasSeccion : matriculasSeccionByFilter) {
            sb = new StringBuilder();
            Alumno alumno = matriculasSeccion.getMatriculaResumen().getAlumno();

            sb.append(count).append("|");
            sb.append(alumno.getCodigo()).append("|");
            sb.append(alumno.getPersona().getApellidosNombres()).append("|");
            sb.append(matriculasSeccion.getMatriculaResumen().getPrioridad().setScale(2, BigDecimal.ROUND_HALF_UP)).append("|");
            sb.append(alumno.getCarrera().getFacultad().getCodigo()).append("|");
            sb.append(alumno.getCarrera().getCodigo()).append("|");
            count++;

            rows.add(sb.toString());
        }

        CellStyle cellHeader = ExcelStyles.getStyleHeader(wb);
        CellStyle cellBody = ExcelStyles.getStyleBody(wb);
        Sheet sheet = wb.createSheet("ReporteAlumnos");
        boolean autosize = false;
        CellRangeAddress region = CellRangeAddress.valueOf("B" + 2 + ":C" + 2);
        sheet.addMergedRegion(region);
        CellStyle cellStyle = getStyleBody(wb);

        Row row1 = sheet.createRow(1);
        Cell cell = row1.createCell(1);
        cell.setCellValue(curso.getNombre());
        cell.setCellStyle(cellStyle);
        Row row2 = sheet.createRow(3);
        Row row3 = sheet.createRow(4);
        Row row4 = sheet.createRow(5);
        Row row5 = sheet.createRow(6);
        Row row6 = sheet.createRow(7);

        Cell cell2 = row2.createCell(0);
        Cell cell3 = row2.createCell(1);
        cell2.setCellValue("Codigo Curso: ");
        cell3.setCellValue(curso.getCodigo());

        Cell cell4 = row3.createCell(0);
        Cell cell5 = row3.createCell(1);
        cell4.setCellValue("Docente: ");
        cell5.setCellValue(docente.getPersona().getApellidosNombres());

        Cell cell8 = row5.createCell(0);
        Cell cell9 = row5.createCell(1);
        cell8.setCellValue("Clave: ");
        cell9.setCellValue(seccion.getCodigo2());

        Cell cell10 = row6.createCell(0);
        Cell cell11 = row6.createCell(1);
        cell10.setCellValue("Grupo: ");
        cell11.setCellValue(seccion.getGrupoHoras().getCodigo());

        if (seccion.getAula() != null) {
            Cell cell6 = row4.createCell(0);
            Cell cell7 = row4.createCell(1);
            cell6.setCellValue("Aula: ");
            cell7.setCellValue(seccion.getAula().getCodigo());
        }

        this.createSheet(sheet, rows, totalColumns, cellHeader, cellBody);

        String nombreReporte = "ReporteAlumnos ";

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

    private void createSheet(Sheet sheet, List<String> rows, int columnas, CellStyle cellHeader, CellStyle cellBody) {
        boolean autosize = false;
        int rw = 9;
        cellBody.setAlignment(CellStyle.ALIGN_CENTER);
        for (int i = 0; i < rows.size(); i++) {
            String fila = (String) rows.get(i);

            String[] argHeader = fila.split("\\|");

            StringTokenizer st = new StringTokenizer(fila, "|");
            Row row = sheet.createRow(rw);
            int j = 0;

            boolean isHeader = false;
            boolean isHeaderTotal = false;
            boolean isHeaderSede = false;

            if (rw == 9) {
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
            if (rw > 9) {
                for (int ii = 0; ii < columnas; ii++) {
                    sheet.autoSizeColumn((short) ii);
                }
                autosize = true;
            }
            rw++;
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
        fontBody.setBold(true);
        fontBody.setFontHeight((short) 300);
//        short border = CellStyle.BORDER_THIN;

        CellStyle cellBody = workBook.createCellStyle();
        cellBody.setFont(fontBody);
        cellBody.setWrapText(true);
        cellBody.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
        cellBody.setAlignment(CellStyle.ALIGN_CENTER);

        return cellBody;
    }

}
