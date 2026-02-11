package pe.edu.lamolina.amauta.controller.nivelacioneegg.reportes.ExcelData;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.view.AbstractView;
import pe.edu.lamolina.amauta.controller.nivelacioneegg.reportes.ExcelData.Bean.*;
import pe.edu.lamolina.amauta.zelper.reportes.ExcelHelper;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.misc.Acumulador;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
public class ExcelReporteNivelacionCarrera extends AbstractView {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private static final String CONTENT_TYPE_XLSX = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
    private static final String CONTENT_TYPE_XLS = "application/vnd.ms-excel";

    @Override
    protected void renderMergedOutputModel(Map<String, Object> map, HttpServletRequest request, HttpServletResponse response) throws Exception {
        InputStream formato = (InputStream) map.get("formato");

        Workbook workbook = new XSSFWorkbook(formato);
        if (workbook instanceof XSSFWorkbook) {
            setContentType(CONTENT_TYPE_XLSX);
        } else {
            setContentType(CONTENT_TYPE_XLS);
        }

        this.buildExcelDocument(map, workbook, request, response);
    }

    private void buildExcelDocument(Map<String, Object> model, Workbook workbook, HttpServletRequest request, HttpServletResponse response) throws Exception {
        ResultadoReporteView resultado = (ResultadoReporteView) model.get("resultado");
        CicloAcademico ciclo = (CicloAcademico) model.get("cicloAcademico");

        String nombreReporte = "Reporte Ingresantes por Carrera " + ciclo.getDescripcion();

        Sheet sheet = workbook.getSheet("Hoja1");

        this.setHeaderExcel(sheet, resultado, workbook, ciclo);
        this.setFirstTabla(sheet, resultado, workbook);
        this.setSecondTabla(sheet, resultado, workbook);
        this.setThirdTabla(sheet, resultado, workbook);

        StringBuilder nomReporte = new StringBuilder();
        nomReporte.append(nombreReporte).append(new DateTime().toString("dd-MM-yyyy hhmmss"));
        logger.info(nomReporte.toString());

        workbook.setForceFormulaRecalculation(true);

        response.setHeader("Content-Disposition", "attachment; filename=\"" + nomReporte.toString() + ".xlsx\"");
        response.setContentType(getContentType());
        ServletOutputStream out = response.getOutputStream();
        out.flush();
        workbook.write(out);
        out.flush();

    }

    private void setHeaderExcel(Sheet sheet, ResultadoReporteView resultado, Workbook workbook, CicloAcademico ciclo) {
        Acumulador rowCounter = new Acumulador();
        ExcelHelper excelUtil = new ExcelHelper(sheet, workbook);
        CellStyle estiloLeft = workbook.createCellStyle();
        estiloLeft.setAlignment(HorizontalAlignment.LEFT);

        rowCounter.incrementar(4);
        excelUtil.replaceStyle(rowCounter.getValor(), 1, estiloLeft);
        excelUtil.replaceVal(rowCounter.getValor(), 1, ciclo.getDescripcion());
        rowCounter.incrementar();

        excelUtil.replaceStyle(rowCounter.getValor(), 1, estiloLeft);
        excelUtil.replaceVal(rowCounter.getValor(), 1, resultado.getFacultad());
        rowCounter.incrementar();

        excelUtil.replaceStyle(rowCounter.getValor(), 1, estiloLeft);
        excelUtil.replaceVal(rowCounter.getValor(), 1, resultado.getCarrera());
        rowCounter.incrementar();

    }

    private void setFirstTabla(Sheet sheet, ResultadoReporteView resultado, Workbook workbook) {

        Acumulador rowCounter = new Acumulador();
        ExcelHelper excelUtil = new ExcelHelper(sheet, workbook);
        CellStyle estiloLeft = excelUtil.getConBordes(HorizontalAlignment.LEFT);
        CellStyle estiloRight = excelUtil.getConBordes(HorizontalAlignment.RIGHT);

        List<IngresantesNivelacionCarreraDTO> ingresantes = resultado.getIngresantesNivelacionCarrera();

        rowCounter.incrementar(11);
        for (IngresantesNivelacionCarreraDTO data : ingresantes) {
            int col = 0;

            excelUtil.replaceStyle(rowCounter.getValor(), col, estiloLeft);
            excelUtil.replaceVal(rowCounter.getValor(), col, data.getMatricula());
            col++;

            excelUtil.replaceStyle(rowCounter.getValor(), col, estiloLeft);
            excelUtil.replaceVal(rowCounter.getValor(), col, data.getIngresante());
            col++;

            excelUtil.replaceStyle(rowCounter.getValor(), col, estiloLeft);
            excelUtil.replaceVal(rowCounter.getValor(), col, data.getCurso());
            col++;

            excelUtil.replaceStyle(rowCounter.getValor(), col, estiloRight);
            excelUtil.replaceVal(rowCounter.getValor(), col, data.getNotaInicial());
            col++;

            excelUtil.replaceStyle(rowCounter.getValor(), col, estiloRight);
            excelUtil.replaceVal(rowCounter.getValor(), col, data.getNotaFinal());
            col++;

            excelUtil.replaceStyle(rowCounter.getValor(), col, estiloLeft);
            excelUtil.replaceVal(rowCounter.getValor(), col, data.getEstadoCurso());
            col++;

            excelUtil.replaceStyle(rowCounter.getValor(), col, estiloLeft);
            excelUtil.replaceVal(rowCounter.getValor(), col, data.getPorcentajeAsistencia());
            col++;

            excelUtil.replaceStyle(rowCounter.getValor(), col, estiloLeft);
            excelUtil.replaceVal(rowCounter.getValor(), col, data.getDocente());
            col++;
            rowCounter.incrementar();
        }

    }

    private void setSecondTabla(Sheet sheet, ResultadoReporteView resultado, Workbook workbook) {
        Acumulador rowCounter = new Acumulador();
        ExcelHelper excelUtil = new ExcelHelper(sheet, workbook);
        CellStyle estiloLeft = excelUtil.getConBordes(HorizontalAlignment.LEFT);
        CellStyle estiloCenter = excelUtil.getConBordes(HorizontalAlignment.CENTER);

        List<IngresantesInscritosNivelacionDTO> inscritosNivelacion = resultado.getIngresantesInscritos();

        rowCounter.incrementar(11);

        for (IngresantesInscritosNivelacionDTO data : inscritosNivelacion) {
            int col = 8;

            excelUtil.replaceStyle(rowCounter.getValor(), col, estiloLeft);
            excelUtil.replaceVal(rowCounter.getValor(), col, data.getCurso());
            col++;

            excelUtil.replaceStyle(rowCounter.getValor(), col, estiloCenter);
            excelUtil.replaceVal(rowCounter.getValor(), col, data.getTotal());
            col++;

            excelUtil.replaceStyle(rowCounter.getValor(), col, estiloCenter);
            excelUtil.replaceVal(rowCounter.getValor(), col, data.getAprobados());
            col++;

            excelUtil.replaceStyle(rowCounter.getValor(), col, estiloCenter);
            excelUtil.replaceVal(rowCounter.getValor(), col, data.getDesaprobados());
            col++;
            
            rowCounter.incrementar();

        }
    }

    private void setThirdTabla(Sheet sheet, ResultadoReporteView resultado, Workbook workbook) {
        Acumulador rowCounter = new Acumulador();
        ExcelHelper excelUtil = new ExcelHelper(sheet, workbook);
        CellStyle estiloLeft = excelUtil.getConBordes(HorizontalAlignment.LEFT);
        CellStyle estiloCenter = excelUtil.getConBordes(HorizontalAlignment.CENTER);

        List<IngresantesAsistenciaInscritosDTO> asistencias = resultado.getIngresantesAsistencia();

        rowCounter.incrementar(47);
        for (IngresantesAsistenciaInscritosDTO data : asistencias) {

            int col = 8;

            excelUtil.replaceStyle(rowCounter.getValor(), col, estiloLeft);
            excelUtil.replaceVal(rowCounter.getValor(), col, data.getCurso());
            col++;

            excelUtil.replaceStyle(rowCounter.getValor(), col, estiloCenter);
            excelUtil.replaceVal(rowCounter.getValor(), col, data.getTotal());
            col++;

            excelUtil.replaceStyle(rowCounter.getValor(), col, estiloCenter);
            excelUtil.replaceVal(rowCounter.getValor(), col, data.getMayorIgual50Asistencia());
            col++;

            excelUtil.replaceStyle(rowCounter.getValor(), col, estiloCenter);
            excelUtil.replaceVal(rowCounter.getValor(), col, data.getMenora50Asistencia());
            col++;

            excelUtil.replaceStyle(rowCounter.getValor(), col, estiloCenter);
            excelUtil.replaceVal(rowCounter.getValor(), col, data.getZeroAsistencia());
            col++;
            
            rowCounter.incrementar();

        }

    }

}
