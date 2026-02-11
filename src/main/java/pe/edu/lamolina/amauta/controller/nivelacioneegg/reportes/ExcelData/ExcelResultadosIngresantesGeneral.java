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
public class ExcelResultadosIngresantesGeneral extends AbstractView {

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
            excelUtil.setWidthColumn(col, 1100);
            excelUtil.replaceStyle(rowCounter.getValor(), col, estiloHead);
            excelUtil.replaceVal(rowCounter.getValor(), col, "N°");
            col++;

            excelUtil.setWidthColumn(col, 4000);
            excelUtil.replaceStyle(rowCounter.getValor(), col, estiloHead);
            excelUtil.replaceVal(rowCounter.getValor(), col, "Matrícula");
            col++;

            excelUtil.setWidthColumn(col, 4000);
            excelUtil.replaceStyle(rowCounter.getValor(), col, estiloHead);
            excelUtil.replaceVal(rowCounter.getValor(), col, "DNI");
            col++;
            excelUtil.setWidthColumn(col, 12000);
            excelUtil.replaceStyle(rowCounter.getValor(), col, estiloHead);
            excelUtil.replaceVal(rowCounter.getValor(), col, "Apellidos Nombre");
            col++;
            excelUtil.setWidthColumn(col, 12000);
            excelUtil.replaceStyle(rowCounter.getValor(), col, estiloHead);
            excelUtil.replaceVal(rowCounter.getValor(), col, "Modalidad Ingreso");
            col++;

            excelUtil.setWidthColumn(col, 8000);
            excelUtil.replaceStyle(rowCounter.getValor(), col, estiloHead);
            excelUtil.replaceVal(rowCounter.getValor(), col, "Carrera");
            col++;

            excelUtil.setWidthColumn(col, 7000);
            excelUtil.replaceStyle(rowCounter.getValor(), col, estiloHead);
            excelUtil.replaceVal(rowCounter.getValor(), col, "Facultad");
            col++;

            excelUtil.setWidthColumn(col, 10000);
            excelUtil.replaceStyle(rowCounter.getValor(), col, estiloHead);
            excelUtil.replaceVal(rowCounter.getValor(), col, "Correo Personal");
            col++;

            excelUtil.setWidthColumn(col, 7000);
            excelUtil.replaceStyle(rowCounter.getValor(), col, estiloHead);
            excelUtil.replaceVal(rowCounter.getValor(), col, "Correo Microsoft");
            col++;

            excelUtil.setWidthColumn(col, 5000);
            excelUtil.replaceStyle(rowCounter.getValor(), col, estiloHead);
            excelUtil.replaceVal(rowCounter.getValor(), col, "Telefono");
            col++;

            excelUtil.setWidthColumn(col, 5000);
            excelUtil.replaceStyle(rowCounter.getValor(), col, estiloHead);
            excelUtil.replaceVal(rowCounter.getValor(), col, "Celular");
            col++;

            excelUtil.setWidthColumn(col, 8000);
            excelUtil.replaceStyle(rowCounter.getValor(), col, estiloHead);
            excelUtil.replaceVal(rowCounter.getValor(), col, "Curso Nivelación");
            col++;

            excelUtil.setWidthColumn(col, 7000);
            excelUtil.replaceStyle(rowCounter.getValor(), col, estiloHead);
            excelUtil.replaceVal(rowCounter.getValor(), col, "Materia");
            col++;

            excelUtil.setWidthColumn(col, 4000);
            excelUtil.replaceStyle(rowCounter.getValor(), col, estiloHead);
            excelUtil.replaceVal(rowCounter.getValor(), col, "Puntaje Materia");
            col++;

            excelUtil.setWidthColumn(col, 6500);
            excelUtil.replaceStyle(rowCounter.getValor(), col, estiloHead);
            excelUtil.replaceVal(rowCounter.getValor(), col, "Estado Curso Nivelación");
            col++;

            excelUtil.setWidthColumn(col, 5000);
            excelUtil.replaceStyle(rowCounter.getValor(), col, estiloHead);
            excelUtil.replaceVal(rowCounter.getValor(), col, "Tema Aprobado");
            col++;

            excelUtil.setWidthColumn(col, 6500);
            excelUtil.replaceStyle(rowCounter.getValor(), col, estiloHead);
            excelUtil.replaceVal(rowCounter.getValor(), col, "Estado Alumno Nivelación");
            col++;

            excelUtil.setWidthColumn(col, 6000);
            excelUtil.replaceStyle(rowCounter.getValor(), col, estiloHead);
            excelUtil.replaceVal(rowCounter.getValor(), col, "Motivo Reserva");
            col++;

            excelUtil.setWidthColumn(col, 6000);
            excelUtil.replaceStyle(rowCounter.getValor(), col, estiloHead);
            excelUtil.replaceVal(rowCounter.getValor(), col, "Usuario");
            col++;


            rowCounter.incrementar();
        }

        resultado.forEach(data -> {

            int col = 0;

            excelUtil.replaceStyle(rowCounter.getValor(), col, estiloRight);
            excelUtil.replaceVal(rowCounter.getValor(), col, data.getCorrelativo());
            col++;

            excelUtil.replaceStyle(rowCounter.getValor(), col, estiloLeft);
            excelUtil.replaceVal(rowCounter.getValor(), col, data.getMatricula());
            col++;

            excelUtil.replaceStyle(rowCounter.getValor(), col, estiloLeft);
            excelUtil.replaceVal(rowCounter.getValor(), col, data.getDni());
            col++;

            excelUtil.replaceStyle(rowCounter.getValor(), col, estiloLeft);
            excelUtil.replaceVal(rowCounter.getValor(), col, data.getApellidosNombre());
            col++;

            excelUtil.replaceStyle(rowCounter.getValor(), col, estiloLeft);
            excelUtil.replaceVal(rowCounter.getValor(), col, data.getModalidadIngreso());
            col++;

            excelUtil.replaceStyle(rowCounter.getValor(), col, estiloLeft);
            excelUtil.replaceVal(rowCounter.getValor(), col, data.getCarrera());
            col++;

            excelUtil.replaceStyle(rowCounter.getValor(), col, estiloCenter);
            excelUtil.replaceVal(rowCounter.getValor(), col, data.getFacultad());
            col++;

            excelUtil.replaceStyle(rowCounter.getValor(), col, estiloCenter);
            excelUtil.replaceVal(rowCounter.getValor(), col, data.getCorreoPersonal());
            col++;

            excelUtil.replaceStyle(rowCounter.getValor(), col, estiloCenter);
            excelUtil.replaceVal(rowCounter.getValor(), col, data.getCorreoOutlook());
            col++;

            excelUtil.replaceStyle(rowCounter.getValor(), col, estiloCenter);
            excelUtil.replaceVal(rowCounter.getValor(), col, data.getTelefono());
            col++;

            excelUtil.replaceStyle(rowCounter.getValor(), col, estiloCenter);
            excelUtil.replaceVal(rowCounter.getValor(), col, data.getCelular());
            col++;

            excelUtil.replaceStyle(rowCounter.getValor(), col, estiloCenter);
            excelUtil.replaceVal(rowCounter.getValor(), col, data.getCurso());
            col++;

            excelUtil.replaceStyle(rowCounter.getValor(), col, estiloCenter);
            excelUtil.replaceVal(rowCounter.getValor(), col, data.getTemaCurso());
            col++;

            excelUtil.replaceStyle(rowCounter.getValor(), col, estiloCenter);
            excelUtil.replaceVal(rowCounter.getValor(), col, data.getPuntajeCurso());
            col++;

            excelUtil.replaceStyle(rowCounter.getValor(), col, estiloCenter);
            excelUtil.replaceVal(rowCounter.getValor(), col, data.getEstadoCursoNivelacion());
            col++;

            excelUtil.replaceStyle(rowCounter.getValor(), col, estiloCenter);
            excelUtil.replaceVal(rowCounter.getValor(), col, data.getTemaAprobado());
            col++;

            excelUtil.replaceStyle(rowCounter.getValor(), col, estiloCenter);
            excelUtil.replaceVal(rowCounter.getValor(), col, data.getEstado());
            col++;

            excelUtil.replaceStyle(rowCounter.getValor(), col, estiloCenter);
            excelUtil.replaceVal(rowCounter.getValor(), col, data.getMotivoReserva());
            col++;

            excelUtil.replaceStyle(rowCounter.getValor(), col, estiloCenter);
            excelUtil.replaceVal(rowCounter.getValor(), col, data.getUsuario());
            col++;

            rowCounter.incrementar();
        });
    }

    private void crearHeaderDetalle(ExcelHelper excelUtil, Acumulador rowCounterSheet) {

        DateTime today = new DateTime();
        ExcelHelper.mergeCell(excelUtil.getSheet(), rowCounterSheet.getValor(), rowCounterSheet.getValor(), 0, 6);
        excelUtil.replaceStyle(rowCounterSheet.getValor(), 0, excelUtil.getConLetraBoldSize14(HorizontalAlignment.CENTER));
        excelUtil.replaceVal(rowCounterSheet.getValor(), 0, "INGRESANTES GENERAL (APROBADOS/DESAPROBADOS/RESERVA)");
        rowCounterSheet.incrementar();

        excelUtil.replaceStyle(rowCounterSheet.getValor(), 1, excelUtil.getConLetraBold(HorizontalAlignment.LEFT));
        excelUtil.replaceVal(rowCounterSheet.getValor(), 1, "Fecha reporte");
        excelUtil.replaceVal(rowCounterSheet.getValor(), 2, capitaliza(TypesUtil.getStringDate(today.toDate(), "EEEE dd 'de' MMMM 'de' yyyy", "es")));
        rowCounterSheet.incrementar();

        excelUtil.replaceStyle(rowCounterSheet.getValor(), 1, excelUtil.getConLetraBold(HorizontalAlignment.LEFT));
        excelUtil.replaceVal(rowCounterSheet.getValor(), 1, "Hora reporte");
        excelUtil.replaceVal(rowCounterSheet.getValor(), 2, capitaliza(TypesUtil.getStringDate(today.toDate(), "HH:mm")));
        rowCounterSheet.incrementar();
    }

    private void publicarExcel(Workbook workbook, HttpServletResponse response) throws IOException {

        String fechaCreacion = new DateTime().toString("yyyMMdd_HHmm");

        response.setHeader("Content-Disposition", "attachment; filename=\"" + "ReporteIngresantesGeneral_" + fechaCreacion + ".xlsx\"");
        response.setContentType(getContentType());
        ServletOutputStream out = response.getOutputStream();
        workbook.write(out);
        out.flush();
    }

    private String capitaliza(String texto) {
        return texto.substring(0, 1).toUpperCase() + texto.substring(1);
    }

}
