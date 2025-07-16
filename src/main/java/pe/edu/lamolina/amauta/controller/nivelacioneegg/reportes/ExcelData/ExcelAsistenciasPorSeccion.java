package pe.edu.lamolina.amauta.controller.nivelacioneegg.reportes.ExcelData;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.joda.time.DateTime;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.view.AbstractView;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.amauta.controller.nivelacioneegg.reportes.ExcelData.Bean.ResultadoReporteView;
import pe.edu.lamolina.amauta.zelper.reportes.ExcelHelper;
import static pe.edu.lamolina.model.enums.dictadoclases.AsistenciaClasesEstadoEnum.ASISTIO;
import pe.edu.lamolina.model.misc.Acumulador;
import pe.edu.lamolina.model.nivelacioneegg.AsistenciaNivelacion;

@Component
public class ExcelAsistenciasPorSeccion extends AbstractView {

    @Override
    protected void renderMergedOutputModel(Map<String, Object> map, HttpServletRequest request, HttpServletResponse response) throws Exception {
        Workbook workbook = new XSSFWorkbook();
        setContentType(ExcelHelper.CONTENT_TYPE_XLSX);
        this.buildExcelDocument(map, workbook, response);
    }

    protected void buildExcelDocument(
            Map<String, Object> model,
            Workbook workbook,
            HttpServletResponse response) throws Exception {

        this.crearDetalle(workbook, model);
        this.publicarExcel(workbook, response);

    }

    private void crearDetalle(Workbook workbook, Map<String, Object> model) {
        Acumulador rowCounterSheet = new Acumulador();
        Sheet sheetDetalle = workbook.createSheet("Data");
        ExcelHelper excelUtil = new ExcelHelper(sheetDetalle, workbook);

        List<ResultadoReporteView> resultados = (List<ResultadoReporteView>) model.get("resultado");

        this.crearHeaderDetalle(excelUtil, resultados, rowCounterSheet);

        rowCounterSheet.incrementar();
        this.crearItemsDetalle(excelUtil, resultados, rowCounterSheet);
    }

    private void crearItemsDetalle(ExcelHelper excelUtil, List<ResultadoReporteView> resultado, Acumulador rowCounter) {
        CellStyle estiloCenter = excelUtil.getConBordes(HorizontalAlignment.CENTER);
        CellStyle estiloLeft = excelUtil.getConBordes(HorizontalAlignment.LEFT);
        CellStyle estiloHead = excelUtil.getBgGreenLetraBlanca(HorizontalAlignment.CENTER);

        {

            int col = 0;

            ExcelHelper.mergeCell(excelUtil.getSheet(), rowCounter.getValor(), rowCounter.getValor() + 1, col, col);
            excelUtil.setWidthColumn(col, 1000);
            excelUtil.replaceStyle(rowCounter.getValor(), col, estiloHead);
            excelUtil.replaceVal(rowCounter.getValor(), col, "N°");
            col++;

            ExcelHelper.mergeCell(excelUtil.getSheet(), rowCounter.getValor(), rowCounter.getValor() + 1, col, col);
            excelUtil.setWidthColumn(col, 6000);
            excelUtil.replaceStyle(rowCounter.getValor(), col, estiloHead);
            excelUtil.replaceVal(rowCounter.getValor(), col, "Matrícula");
            col++;

            ExcelHelper.mergeCell(excelUtil.getSheet(), rowCounter.getValor(), rowCounter.getValor() + 1, col, col);
            excelUtil.setWidthColumn(col, 12000);
            excelUtil.replaceStyle(rowCounter.getValor(), col, estiloHead);
            excelUtil.replaceVal(rowCounter.getValor(), col, "Apellidos Nombres");
            col++;

            List<AsistenciaNivelacion> asistencias = resultado.get(col).getAsistencias();

            ExcelHelper.mergeCell(excelUtil.getSheet(), rowCounter.getValor(), rowCounter.getValor(), col, (col + asistencias.size() - 1));
            excelUtil.setWidthColumn(col, 12000);
            excelUtil.replaceStyle(rowCounter.getValor(), col, estiloHead);
            excelUtil.replaceVal(rowCounter.getValor(), col, "Clases");

            Acumulador numeroAsistencia = new Acumulador(1);

            for (AsistenciaNivelacion asistencia : asistencias) {
                excelUtil.setWidthColumn(col, 1000);
                excelUtil.replaceStyle(rowCounter.getValor() + 1, col, estiloHead);
                excelUtil.replaceVal(rowCounter.getValor() + 1, col, numeroAsistencia.getValor());
                numeroAsistencia.incrementar();
                col++;
            }

            ExcelHelper.mergeCell(excelUtil.getSheet(), rowCounter.getValor(), rowCounter.getValor() + 1, col, col);
            excelUtil.setWidthColumn(col, 3500);
            excelUtil.replaceStyle(rowCounter.getValor(), col, estiloHead);
            excelUtil.replaceVal(rowCounter.getValor(), col, "% Asistencia");
            col++;

            rowCounter.incrementar(2);
        }

        Acumulador numeroAlumno = new Acumulador(1);
        resultado.forEach(data -> {

            int col = 0;

            excelUtil.replaceStyle(rowCounter.getValor(), col, estiloLeft);
            excelUtil.replaceVal(rowCounter.getValor(), col, numeroAlumno.getValor());
            numeroAlumno.incrementar();
            col++;

            excelUtil.replaceStyle(rowCounter.getValor(), col, estiloLeft);
            excelUtil.replaceVal(rowCounter.getValor(), col, data.getMatricula());
            col++;

            excelUtil.replaceStyle(rowCounter.getValor(), col, estiloLeft);
            excelUtil.replaceVal(rowCounter.getValor(), col, data.getApellidosNombre());
            col++;
            List<AsistenciaNivelacion> asistencias = data.getAsistencias();
            for (AsistenciaNivelacion asistencia : asistencias) {
                excelUtil.replaceStyle(rowCounter.getValor(), col, estiloCenter);
                excelUtil.replaceVal(rowCounter.getValor(), col, asistencia.getEstadoEnum() == ASISTIO ? "✓" : "");
                col++;
            }

            excelUtil.replaceStyle(rowCounter.getValor(), col, estiloCenter);
            excelUtil.replaceVal(rowCounter.getValor(), col, data.getPorcentajeAsistencia());
            col++;

            rowCounter.incrementar();
        });
    }

    private void crearHeaderDetalle(ExcelHelper excelUtil, List<ResultadoReporteView> resultado, Acumulador rowCounterSheet) {

        DateTime today = new DateTime();
        ExcelHelper.mergeCell(excelUtil.getSheet(), rowCounterSheet.getValor(), rowCounterSheet.getValor(), 0, 14);
        excelUtil.replaceStyle(rowCounterSheet.getValor(), 0, excelUtil.getConLetraBoldSize14(HorizontalAlignment.CENTER));
        excelUtil.replaceVal(rowCounterSheet.getValor(), 0, "LISTA DE ASISTENCIA DE NIVELACIÓN DE INGRESANTES");
        rowCounterSheet.incrementar();

        ExcelHelper.mergeCell(excelUtil.getSheet(), rowCounterSheet.getValor(), rowCounterSheet.getValor(), 0, 1);
        excelUtil.replaceStyle(rowCounterSheet.getValor(), 0, excelUtil.getConLetraBold(HorizontalAlignment.LEFT));
        excelUtil.replaceVal(rowCounterSheet.getValor(), 0, "Materia de Nivelación");
        excelUtil.replaceVal(rowCounterSheet.getValor(), 2, capitaliza(resultado.get(0).getCurso()));
        rowCounterSheet.incrementar();

        ExcelHelper.mergeCell(excelUtil.getSheet(), rowCounterSheet.getValor(), rowCounterSheet.getValor(), 0, 1);
        excelUtil.replaceStyle(rowCounterSheet.getValor(), 0, excelUtil.getConLetraBold(HorizontalAlignment.LEFT));
        excelUtil.replaceVal(rowCounterSheet.getValor(), 0, "Profesor");
        excelUtil.replaceVal(rowCounterSheet.getValor(), 2, capitaliza(resultado.get(0).getDocente()));
        rowCounterSheet.incrementar();

        ExcelHelper.mergeCell(excelUtil.getSheet(), rowCounterSheet.getValor(), rowCounterSheet.getValor(), 0, 1);
        excelUtil.replaceStyle(rowCounterSheet.getValor(), 0, excelUtil.getConLetraBold(HorizontalAlignment.LEFT));
        excelUtil.replaceVal(rowCounterSheet.getValor(), 0, "Ciclo");
        excelUtil.replaceVal(rowCounterSheet.getValor(), 2, capitaliza(resultado.get(0).getCiclo()));
        rowCounterSheet.incrementar();

        ExcelHelper.mergeCell(excelUtil.getSheet(), rowCounterSheet.getValor(), rowCounterSheet.getValor(), 0, 1);
        excelUtil.replaceStyle(rowCounterSheet.getValor(), 0, excelUtil.getConLetraBold(HorizontalAlignment.LEFT));
        excelUtil.replaceVal(rowCounterSheet.getValor(), 0, "Sección");
        excelUtil.replaceVal(rowCounterSheet.getValor(), 2, capitaliza(resultado.get(0).getSeccion()));
        rowCounterSheet.incrementar();

        ExcelHelper.mergeCell(excelUtil.getSheet(), rowCounterSheet.getValor(), rowCounterSheet.getValor(), 0, 1);
        excelUtil.replaceStyle(rowCounterSheet.getValor(), 0, excelUtil.getConLetraBold(HorizontalAlignment.LEFT));
        excelUtil.replaceVal(rowCounterSheet.getValor(), 0, "Fecha reporte");
        excelUtil.replaceVal(rowCounterSheet.getValor(), 2, capitaliza(TypesUtil.getStringDate(today.toDate(), "EEEE dd 'de' MMMM 'de' yyyy", "es")));
        rowCounterSheet.incrementar();

        ExcelHelper.mergeCell(excelUtil.getSheet(), rowCounterSheet.getValor(), rowCounterSheet.getValor(), 0, 1);
        excelUtil.replaceStyle(rowCounterSheet.getValor(), 0, excelUtil.getConLetraBold(HorizontalAlignment.LEFT));
        excelUtil.replaceVal(rowCounterSheet.getValor(), 0, "Hora reporte");
        excelUtil.replaceVal(rowCounterSheet.getValor(), 2, capitaliza(TypesUtil.getStringDate(today.toDate(), "HH:mm")));
        rowCounterSheet.incrementar();
    }

    private void publicarExcel(Workbook workbook, HttpServletResponse response) throws IOException {

        String fechaCreacion = new DateTime().toString("yyyMMdd_HHmm");

        response.setHeader("Content-Disposition", "attachment; filename=\"" + "ReporteAsistenciaPorSeccion_" + fechaCreacion + ".xlsx\"");
        response.setContentType(getContentType());
        ServletOutputStream out = response.getOutputStream();
        workbook.write(out);
        out.flush();
    }

    private String capitaliza(String texto) {
        return texto.substring(0, 1).toUpperCase() + texto.substring(1);
    }

}
