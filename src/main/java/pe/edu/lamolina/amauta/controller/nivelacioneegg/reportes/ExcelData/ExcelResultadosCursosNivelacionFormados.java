package pe.edu.lamolina.amauta.controller.nivelacioneegg.reportes.ExcelData;

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
import pe.edu.lamolina.model.misc.Acumulador;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@Component
public class ExcelResultadosCursosNivelacionFormados extends AbstractView {

    @Override
    protected void renderMergedOutputModel(Map<String, Object> map, HttpServletRequest request, HttpServletResponse response) throws Exception {
        Workbook workbook = new XSSFWorkbook();
        setContentType(ExcelHelper.CONTENT_TYPE_XLSX);
        this.buildExcelDocument(map, workbook, response);
    }

    protected void buildExcelDocument(Map<String, Object> model, Workbook workbook, HttpServletResponse response) throws Exception {

        this.crearDetalle(workbook, model);
        this.publicarExcel(workbook, response);

    }

    private void crearDetalle(Workbook workbook, Map<String, Object> model) {
        Acumulador rowCounterSheet = new Acumulador();
        Sheet sheetDetalle = workbook.createSheet("Data");
        ExcelHelper excelUtil = new ExcelHelper(sheetDetalle, workbook);

        List<ResultadoReporteView> resultado = (List<ResultadoReporteView>) model.get("resultado");

        this.crearHeaderDetalle(excelUtil, rowCounterSheet);

        rowCounterSheet.incrementar();
        this.crearItemsDetalle(excelUtil, resultado, rowCounterSheet);
    }

    private void crearItemsDetalle(ExcelHelper excelUtil, List<ResultadoReporteView> resultado, Acumulador rowCounter) {
        CellStyle estiloCenter = excelUtil.getConBordes(HorizontalAlignment.CENTER);
        CellStyle estiloLeft = excelUtil.getConBordes(HorizontalAlignment.LEFT);
        CellStyle estiloRight = excelUtil.getConBordes(HorizontalAlignment.RIGHT);
        CellStyle estiloHead = excelUtil.getBgGreenLetraBlanca(HorizontalAlignment.CENTER);

        {

            int col = 0;

            excelUtil.setWidthColumn(col, 9000);
            excelUtil.replaceStyle(rowCounter.getValor(), col, estiloHead);
            excelUtil.replaceVal(rowCounter.getValor(), col, "Curso");
            col++;

            excelUtil.setWidthColumn(col, 2500);
            excelUtil.replaceStyle(rowCounter.getValor(), col, estiloHead);
            excelUtil.replaceVal(rowCounter.getValor(), col, "Sección");
            col++;
            excelUtil.setWidthColumn(col, 12000);
            excelUtil.replaceStyle(rowCounter.getValor(), col, estiloHead);
            excelUtil.replaceVal(rowCounter.getValor(), col, "Docente");
            col++;
            excelUtil.setWidthColumn(col, 8000);
            excelUtil.replaceStyle(rowCounter.getValor(), col, estiloHead);
            excelUtil.replaceVal(rowCounter.getValor(), col, "Correo Docente");
            col++;

            excelUtil.setWidthColumn(col, 5000);
            excelUtil.replaceStyle(rowCounter.getValor(), col, estiloHead);
            excelUtil.replaceVal(rowCounter.getValor(), col, "Código Docente");
            col++;

            excelUtil.setWidthColumn(col, 2500);
            excelUtil.replaceStyle(rowCounter.getValor(), col, estiloHead);
            excelUtil.replaceVal(rowCounter.getValor(), col, "Aula");
            col++;

            excelUtil.setWidthColumn(col, 8000);
            excelUtil.replaceStyle(rowCounter.getValor(), col, estiloHead);
            excelUtil.replaceVal(rowCounter.getValor(), col, "Modulo Aula");
            col++;

            excelUtil.setWidthColumn(col, 2500);
            excelUtil.replaceStyle(rowCounter.getValor(), col, estiloHead);
            excelUtil.replaceVal(rowCounter.getValor(), col, "Plantilla");
            col++;

            excelUtil.setWidthColumn(col, 5000);
            excelUtil.replaceStyle(rowCounter.getValor(), col, estiloHead);
            excelUtil.replaceVal(rowCounter.getValor(), col, "Semana");
            col++;

            excelUtil.setWidthColumn(col, 5000);
            excelUtil.replaceStyle(rowCounter.getValor(), col, estiloHead);
            excelUtil.replaceVal(rowCounter.getValor(), col, "Día");
            col++;

            excelUtil.setWidthColumn(col, 6000);
            excelUtil.replaceStyle(rowCounter.getValor(), col, estiloHead);
            excelUtil.replaceVal(rowCounter.getValor(), col, "Hora Dictado");
            col++;

            excelUtil.setWidthColumn(col, 5000);
            excelUtil.replaceStyle(rowCounter.getValor(), col, estiloHead);
            excelUtil.replaceVal(rowCounter.getValor(), col, "Asistencia");
            col++;

            excelUtil.setWidthColumn(col, 5000);
            excelUtil.replaceStyle(rowCounter.getValor(), col, estiloHead);
            excelUtil.replaceVal(rowCounter.getValor(), col, "Evaluciones");
            col++;

            excelUtil.setWidthColumn(col, 5000);
            excelUtil.replaceStyle(rowCounter.getValor(), col, estiloHead);
            excelUtil.replaceVal(rowCounter.getValor(), col, "Vac / Mat");
            col++;

            excelUtil.setWidthColumn(col, 5000);
            excelUtil.replaceStyle(rowCounter.getValor(), col, estiloHead);
            excelUtil.replaceVal(rowCounter.getValor(), col, "Estado");
            col++;

            rowCounter.incrementar();
        }

        resultado.forEach(data -> {

            int col = 0;

            excelUtil.replaceStyle(rowCounter.getValor(), col, estiloLeft);
            excelUtil.replaceVal(rowCounter.getValor(), col, data.getCurso());
            col++;

            excelUtil.replaceStyle(rowCounter.getValor(), col, estiloCenter);
            excelUtil.replaceVal(rowCounter.getValor(), col, data.getSeccion());
            col++;

            excelUtil.replaceStyle(rowCounter.getValor(), col, estiloLeft);
            excelUtil.replaceVal(rowCounter.getValor(), col, data.getDocente());
            col++;

            excelUtil.replaceStyle(rowCounter.getValor(), col, estiloLeft);
            excelUtil.replaceVal(rowCounter.getValor(), col, data.getCorreoDocente());
            col++;

            excelUtil.replaceStyle(rowCounter.getValor(), col, estiloLeft);
            excelUtil.replaceVal(rowCounter.getValor(), col, data.getCodDocente());
            col++;

            excelUtil.replaceStyle(rowCounter.getValor(), col, estiloCenter);
            excelUtil.replaceVal(rowCounter.getValor(), col, data.getAula());
            col++;

            excelUtil.replaceStyle(rowCounter.getValor(), col, estiloCenter);
            excelUtil.replaceVal(rowCounter.getValor(), col, data.getModuloAula());
            col++;

            excelUtil.replaceStyle(rowCounter.getValor(), col, estiloCenter);
            excelUtil.replaceVal(rowCounter.getValor(), col, data.getPlantilla());
            col++;

            excelUtil.replaceStyle(rowCounter.getValor(), col, estiloCenter);
            excelUtil.replaceVal(rowCounter.getValor(), col, TypesUtil.getStringDate(data.getSemana(), "dd/MM/yyyy"));
            col++;

            excelUtil.replaceStyle(rowCounter.getValor(), col, estiloCenter);
            excelUtil.replaceVal(rowCounter.getValor(), col, data.getDia());
            col++;

            excelUtil.replaceStyle(rowCounter.getValor(), col, estiloCenter);
            excelUtil.replaceVal(rowCounter.getValor(), col, data.getHoraDictado());
            col++;

            excelUtil.replaceStyle(rowCounter.getValor(), col, estiloCenter);
            excelUtil.replaceVal(rowCounter.getValor(), col, data.getControlAsistencia());
            col++;

            excelUtil.replaceStyle(rowCounter.getValor(), col, estiloCenter);
            excelUtil.replaceVal(rowCounter.getValor(), col, data.getActasEntregadas());
            col++;

            excelUtil.replaceStyle(rowCounter.getValor(), col, estiloCenter);
            excelUtil.replaceVal(rowCounter.getValor(), col, data.getVacMat());
            col++;

            excelUtil.replaceStyle(rowCounter.getValor(), col, estiloCenter);
            excelUtil.replaceVal(rowCounter.getValor(), col, data.getEstado());
            col++;

            rowCounter.incrementar();
        });
    }

    private void crearHeaderDetalle(ExcelHelper excelUtil, Acumulador rowCounterSheet) {

        DateTime today = new DateTime();
        ExcelHelper.mergeCell(excelUtil.getSheet(), rowCounterSheet.getValor(), rowCounterSheet.getValor(), 0, 6);
        excelUtil.replaceStyle(rowCounterSheet.getValor(), 0, excelUtil.getConLetraBoldSize14(HorizontalAlignment.CENTER));
        excelUtil.replaceVal(rowCounterSheet.getValor(), 0, "CURSOS NIVELACIÓN FORMADOS");
        rowCounterSheet.incrementar();

        excelUtil.replaceStyle(rowCounterSheet.getValor(), 0, excelUtil.getConLetraBold(HorizontalAlignment.LEFT));
//        excelUtil.replaceVal(rowCounterSheet.getValor(), 0, "Fecha reporte");
        excelUtil.replaceVal(rowCounterSheet.getValor(), 0, capitaliza(TypesUtil.getStringDate(today.toDate(), "EEEE dd 'de' MMMM 'de' yyyy", "es")));
        rowCounterSheet.incrementar();

        excelUtil.replaceStyle(rowCounterSheet.getValor(), 0, excelUtil.getConLetraBold(HorizontalAlignment.LEFT));
//        excelUtil.replaceVal(rowCounterSheet.getValor(), 1, "Hora reporte");
        excelUtil.replaceVal(rowCounterSheet.getValor(), 0, "Hora reporte: ".concat(capitaliza(TypesUtil.getStringDate(today.toDate(), "HH:mm"))));
        rowCounterSheet.incrementar();
    }

    private void publicarExcel(Workbook workbook, HttpServletResponse response) throws IOException {

        String fechaCreacion = new DateTime().toString("yyyMMdd_HHmm");

        response.setHeader("Content-Disposition", "attachment; filename=\"" + "ReporteCursosNivelacionFormadas_" + fechaCreacion + ".xlsx\"");
        response.setContentType(getContentType());
        ServletOutputStream out = response.getOutputStream();
        workbook.write(out);
        out.flush();
    }

    private String capitaliza(String texto) {
        return texto.substring(0, 1).toUpperCase() + texto.substring(1);
    }

}
