package pe.edu.lamolina.amauta.controller.programacionhorarios.resumen.reporte;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.joda.time.DateTime;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.view.AbstractView;
import pe.albatross.zelpers.file.excel.ExcelHelper;
import pe.edu.lamolina.amauta.controller.programacionhorarios.resumen.DepartamentoCursosProgramadosDTO;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Component
public class ResumenProgramacionExcelView extends AbstractView {

    private static final String CONTENT_TYPE_XLSX = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
    private static final String SHEET_NAME = "Resumen Programación";
    private static final String REPORT_PREFIX = "Reporte_resumen_programacion_UNALM_";
    private static final String DATE_PATTERN = "yyyyMMdd_HHmm";

    // Configuración de columnas
    private static final String[] HEADERS = {
            "Anexo Superior", "Departamento", "Total Secciones", "Activos", "Anulados",
            "Fusionados", "Bloqueados", "Cancelados"
    };

    private static final int[] COLUMN_WIDTHS = {8000, 8000, 4000, 3000, 3000, 3000, 3000, 3000};

    @Override
    protected void renderMergedOutputModel(Map<String, Object> model,
                                           HttpServletRequest request,
                                           HttpServletResponse response) throws Exception {
        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            buildExcelDocument(model, workbook, response);
        }
    }

    private void buildExcelDocument(Map<String, Object> model,
                                    XSSFWorkbook workbook,
                                    HttpServletResponse response) throws IOException {

        @SuppressWarnings("unchecked")
        List<DepartamentoCursosProgramadosDTO> data =
                (List<DepartamentoCursosProgramadosDTO>) model.get("counts");

        @SuppressWarnings("unchecked")
        Map<Long, String> nombresDepartamentos =
                (Map<Long, String>) model.get("nombresDepartamentos");

        @SuppressWarnings("unchecked")
        Map<Long, String> nombresAnexosSuperiores =
                (Map<Long, String>) model.get("nombresAnexosSuperiores");

        if (data == null || data.isEmpty()) {
            throw new IllegalArgumentException("No hay datos para generar el reporte");
        }

        Sheet sheet = createSheet(workbook);
        ExcelStyleHelper styleHelper = new ExcelStyleHelper(workbook);

        createHeader(sheet, styleHelper);
        createBody(sheet, styleHelper, data, nombresDepartamentos, nombresAnexosSuperiores);
        createSummaryRow(sheet, styleHelper, data);

        configureResponse(response);
        writeWorkbookToResponse(workbook, response);
    }

    private Sheet createSheet(XSSFWorkbook workbook) {
        Sheet sheet = workbook.createSheet(SHEET_NAME);

        // Configurar anchos de columna
        for (int i = 0; i < COLUMN_WIDTHS.length; i++) {
            sheet.setColumnWidth(i, COLUMN_WIDTHS[i]);
        }

        // Configurar impresión
        sheet.setFitToPage(true);
        sheet.getPrintSetup().setFitWidth((short) 1);
        sheet.getPrintSetup().setFitHeight((short) 0);

        return sheet;
    }

    private void createHeader(Sheet sheet, ExcelStyleHelper styleHelper) {
        Row headerRow = sheet.createRow(0);

        for (int i = 0; i < HEADERS.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(HEADERS[i]);
            cell.setCellStyle(styleHelper.getHeaderStyle());
        }

        // Congelar la fila de encabezado
        sheet.createFreezePane(0, 1);
    }

    private void createBody(Sheet sheet, ExcelStyleHelper styleHelper,
                            List<DepartamentoCursosProgramadosDTO> data,
                            Map<Long, String> nombresDepartamentos,
                            Map<Long, String> nombresAnexosSuperiores) {

        int rowIndex = 1;

        for (DepartamentoCursosProgramadosDTO item : data) {
            Row row = sheet.createRow(rowIndex++);

            String nombreDep = nombresDepartamentos.getOrDefault(
                    item.getIdDepartamento(), "Departamento sin nombre");
            String nombreAnexoSuperior = nombresAnexosSuperiores != null ?
                    nombresAnexosSuperiores.getOrDefault(item.getIdDepartamento(), "Sin anexo superior") :
                    "Sin anexo superior";

            // Crear celdas con datos
            createCell(row, 0, nombreAnexoSuperior, styleHelper.getTextStyle());
            createCell(row, 1, nombreDep, styleHelper.getTextStyle());
            createCell(row, 2, safeGetValue(item.getTotalSecciones()), styleHelper.getNumberStyle());
            createCell(row, 3, safeGetValue(item.getActivos()), styleHelper.getNumberStyle());
            createCell(row, 4, safeGetValue(item.getAnulados()), styleHelper.getNumberStyle());
            createCell(row, 5, safeGetValue(item.getFusionados()), styleHelper.getNumberStyle());
            createCell(row, 6, safeGetValue(item.getBloqueados()), styleHelper.getNumberStyle());
            createCell(row, 7, safeGetValue(item.getCancelados()), styleHelper.getNumberStyle());
        }
    }

    private void createSummaryRow(Sheet sheet, ExcelStyleHelper styleHelper,
                                  List<DepartamentoCursosProgramadosDTO> data) {

        int summaryRowIndex = data.size() + 2; // Saltar una fila
        Row summaryRow = sheet.createRow(summaryRowIndex);

        // Calcular totales - manejando Long values
        long totalSecciones = data.stream()
                .mapToLong(item -> item.getTotalSecciones() != null ? item.getTotalSecciones() : 0L)
                .sum();
        long totalActivos = data.stream()
                .mapToLong(item -> item.getActivos() != null ? item.getActivos() : 0L)
                .sum();
        long totalAnulados = data.stream()
                .mapToLong(item -> item.getAnulados() != null ? item.getAnulados() : 0L)
                .sum();
        long totalFusionados = data.stream()
                .mapToLong(item -> item.getFusionados() != null ? item.getFusionados() : 0L)
                .sum();
        long totalBloqueados = data.stream()
                .mapToLong(item -> item.getBloqueados() != null ? item.getBloqueados() : 0L)
                .sum();
        long totalCancelados = data.stream()
                .mapToLong(item -> item.getCancelados() != null ? item.getCancelados() : 0L)
                .sum();

        // Crear fila de totales
        createCell(summaryRow, 0, "", styleHelper.getSummaryStyle());
        createCell(summaryRow, 1, "TOTAL GENERAL", styleHelper.getSummaryStyle());
        createCell(summaryRow, 2, totalSecciones, styleHelper.getSummaryStyle());
        createCell(summaryRow, 3, totalActivos, styleHelper.getSummaryStyle());
        createCell(summaryRow, 4, totalAnulados, styleHelper.getSummaryStyle());
        createCell(summaryRow, 5, totalFusionados, styleHelper.getSummaryStyle());
        createCell(summaryRow, 6, totalBloqueados, styleHelper.getSummaryStyle());
        createCell(summaryRow, 7, totalCancelados, styleHelper.getSummaryStyle());
    }

    private void createCell(Row row, int columnIndex, Object value, CellStyle style) {
        Cell cell = row.createCell(columnIndex);

        if (value instanceof String) {
            cell.setCellValue((String) value);
        } else if (value instanceof Number) {
            cell.setCellValue(((Number) value).doubleValue());
        } else if (value != null) {
            cell.setCellValue(value.toString());
        }

        cell.setCellStyle(style);
    }

    private void configureResponse(HttpServletResponse response) {
        String fileName = generateFileName();

        response.setContentType(CONTENT_TYPE_XLSX);
        response.setHeader("Content-Disposition",
                "attachment; filename=\"" + fileName + "\"");
        response.setStatus(HttpServletResponse.SC_OK);

        // Headers adicionales para evitar cacheo
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Expires", "0");
    }

    private String generateFileName() {
        return REPORT_PREFIX +
                LocalDateTime.now().format(DateTimeFormatter.ofPattern(DATE_PATTERN)) +
                ".xlsx";
    }

    private void writeWorkbookToResponse(XSSFWorkbook workbook, HttpServletResponse response)
            throws IOException {
        try (ServletOutputStream out = response.getOutputStream()) {
            workbook.write(out);
            out.flush();
        }
    }

    /**
     * Método helper para manejar valores Long que pueden ser null
     */
    private Long safeGetValue(Long value) {
        return value != null ? value : 0L;
    }

    /**
     * Clase helper para manejar estilos de Excel
     */
    private static class ExcelStyleHelper {
        private final XSSFWorkbook workbook;
        private CellStyle headerStyle;
        private CellStyle textStyle;
        private CellStyle numberStyle;
        private CellStyle summaryStyle;

        public ExcelStyleHelper(XSSFWorkbook workbook) {
            this.workbook = workbook;
            initializeStyles();
        }

        private void initializeStyles() {
            // Fuente base
            Font defaultFont = workbook.createFont();
            defaultFont.setFontName("Arial");
            defaultFont.setFontHeightInPoints((short) 10);

            // Fuente para encabezados
            Font headerFont = workbook.createFont();
            headerFont.setFontName("Arial");
            headerFont.setFontHeightInPoints((short) 11);
            headerFont.setBold(true);
            headerFont.setColor(IndexedColors.WHITE.getIndex());

            // Fuente para totales
            Font summaryFont = workbook.createFont();
            summaryFont.setFontName("Arial");
            summaryFont.setFontHeightInPoints((short) 10);
            summaryFont.setBold(true);

            // Estilo para encabezados
            headerStyle = workbook.createCellStyle();
            headerStyle.setFont(headerFont);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);
            headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            headerStyle.setFillForegroundColor(IndexedColors.SEA_GREEN.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            setBorders(headerStyle, BorderStyle.MEDIUM);

            // Estilo para texto
            textStyle = workbook.createCellStyle();
            textStyle.setFont(defaultFont);
            textStyle.setAlignment(HorizontalAlignment.LEFT);
            textStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            setBorders(textStyle, BorderStyle.THIN);

            // Estilo para números
            numberStyle = workbook.createCellStyle();
            numberStyle.setFont(defaultFont);
            numberStyle.setAlignment(HorizontalAlignment.CENTER);
            numberStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            numberStyle.setDataFormat(workbook.createDataFormat().getFormat("#,##0"));
            setBorders(numberStyle, BorderStyle.THIN);

            // Estilo para totales
            summaryStyle = workbook.createCellStyle();
            summaryStyle.setFont(summaryFont);
            summaryStyle.setAlignment(HorizontalAlignment.CENTER);
            summaryStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            summaryStyle.setFillForegroundColor(IndexedColors.LIGHT_YELLOW.getIndex());
            summaryStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            summaryStyle.setDataFormat(workbook.createDataFormat().getFormat("#,##0"));
            setBorders(summaryStyle, BorderStyle.MEDIUM);
        }

        private void setBorders(CellStyle style, BorderStyle borderStyle) {
            style.setBorderTop(borderStyle);
            style.setBorderBottom(borderStyle);
            style.setBorderLeft(borderStyle);
            style.setBorderRight(borderStyle);
        }

        public CellStyle getHeaderStyle() { return headerStyle; }
        public CellStyle getTextStyle() { return textStyle; }
        public CellStyle getNumberStyle() { return numberStyle; }
        public CellStyle getSummaryStyle() { return summaryStyle; }
    }
}