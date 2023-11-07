package pe.edu.lamolina.amauta.controller.programacionhorarios.reporte;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.joda.time.DateTime;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.view.AbstractView;
import pe.albatross.zelpers.file.excel.ExcelHelper;

@Component
public class ReporteHorarioView extends AbstractView {

    private static final String CONTENT_TYPE_XLSX = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";

    @Override
    protected void renderMergedOutputModel(Map<String, Object> map, HttpServletRequest request, HttpServletResponse response) throws Exception {
        HSSFWorkbook workbook = new HSSFWorkbook();
        setContentType(CONTENT_TYPE_XLSX);
        this.buildExcelDocument(map, workbook, request, response);
    }

    private void buildExcelDocument(Map<String, Object> model, Workbook workbook, HttpServletRequest request, HttpServletResponse response) throws IOException {
        List<MatriculaPreBean> listMatriculaPreBean = (List<MatriculaPreBean>) model.get("listMatriculaPreBean");

        String tipoReporte = (String) model.get("tipoReporte");
        this.generateCelda(workbook, listMatriculaPreBean, tipoReporte);
        String fecha = new DateTime().toString("yyyMMdd_Hmm");

        String header = "";

        header = "Programacion_Harario_";

        response.setHeader("Content-Disposition", "attachment; filename=\"" + header + fecha + ".xls\"");
        response.setContentType(getContentType());
        response.setStatus(200);
        ServletOutputStream out = response.getOutputStream();
        out.flush();
        workbook.write(out);
        out.flush();
    }

    private void generateCelda(Workbook workbook, List<MatriculaPreBean> listMatriculaPreBean, String tipoReporte) {
        Sheet sheet = workbook.createSheet("Hoja1");
        ExcelHelper excelUtil = new ExcelHelper(sheet, workbook);
        int irow = 2; // fila de inicio para la data
        this.createHeader(excelUtil, workbook, tipoReporte);
        this.createBody(excelUtil, irow, listMatriculaPreBean, workbook, tipoReporte);
    }

    private void createHeader(ExcelHelper excelUtil, Workbook workbook, String tipoReporte) {
        CellStyle estiloCabecera = getStyleCabecera(workbook, HorizontalAlignment.CENTER);
        CellStyle estiloCabeceraLeft = getStyleCabecera(workbook, HorizontalAlignment.LEFT);
        this.setWidthColumn(excelUtil.getSheet(), 0, 7500);
        this.setWidthColumn(excelUtil.getSheet(), 1, 10500);
        this.setWidthColumn(excelUtil.getSheet(), 2, 10000);
        this.setWidthColumn(excelUtil.getSheet(), 3, 10000);
        this.setWidthColumn(excelUtil.getSheet(), 4, 4000);
        this.setWidthColumn(excelUtil.getSheet(), 5, 7000);
        this.setWidthColumn(excelUtil.getSheet(), 6, 5000);
        this.setWidthColumn(excelUtil.getSheet(), 7, 4000);
        this.setWidthColumn(excelUtil.getSheet(), 8, 6000);
        this.setWidthColumn(excelUtil.getSheet(), 9, 4000);
        this.setWidthColumn(excelUtil.getSheet(), 10, 6000);
        this.setWidthColumn(excelUtil.getSheet(), 11, 6000);

        excelUtil.replaceStyle(0, 0, estiloCabecera);
        excelUtil.replaceStyle(0, 1, estiloCabeceraLeft);
        excelUtil.replaceStyle(0, 2, estiloCabeceraLeft);
        excelUtil.replaceStyle(0, 3, estiloCabeceraLeft);
        excelUtil.replaceStyle(0, 4, estiloCabecera);
        excelUtil.replaceStyle(0, 5, estiloCabecera);
        excelUtil.replaceStyle(0, 6, estiloCabecera);
        excelUtil.replaceStyle(0, 7, estiloCabecera);
        excelUtil.replaceStyle(0, 8, estiloCabecera);
        excelUtil.replaceStyle(0, 9, estiloCabecera);
        excelUtil.replaceStyle(0, 10, estiloCabecera);
        excelUtil.replaceStyle(0, 11, estiloCabecera);

        excelUtil.replaceVal(0, 0, "Facultad");
        excelUtil.replaceVal(0, 1, "Programa");
        excelUtil.replaceVal(0, 2, "Apellidos y Nombres");
        excelUtil.replaceVal(0, 3, "Correo");
        excelUtil.replaceVal(0, 4, "codigo");
        excelUtil.replaceVal(0, 5, "Curso");
        excelUtil.replaceVal(0, 6, "Grupo");
        excelUtil.replaceVal(0, 7, "Dia");
        excelUtil.replaceVal(0, 8, "Hora Dictado");
        excelUtil.replaceVal(0, 9, "vacantes");
        excelUtil.replaceVal(0, 10, "matriculados");
        excelUtil.replaceVal(0, 11, "Modo");
    }

    private CellStyle getStyleCabecera(Workbook workbook, HorizontalAlignment posicion) {
        Font font = workbook.createFont();
        font.setFontName("Arial");
        font.setBold(true);
        CellStyle cell = workbook.createCellStyle();
        cell.setAlignment(posicion);
        cell.setFont(font);
        cell.setBorderTop(BorderStyle.MEDIUM);
        cell.setBorderBottom(BorderStyle.MEDIUM);
        cell.setBorderRight(BorderStyle.MEDIUM);
        cell.setBorderLeft(BorderStyle.MEDIUM);
        return cell;
    }

    private CellStyle getStyleGeneral(Workbook workbook, HorizontalAlignment posicion) {
        Font font = workbook.createFont();
        font.setFontName("Arial");
        CellStyle cell = workbook.createCellStyle();
        cell.setAlignment(posicion);
        cell.setFont(font);
        cell.setBorderTop(BorderStyle.THIN);
        cell.setBorderBottom(BorderStyle.THIN);
        cell.setBorderRight(BorderStyle.THIN);
        cell.setBorderLeft(BorderStyle.THIN);
        return cell;
    }

    private void setWidthColumn(Sheet sheet, int numberColumn, int width) {
        sheet.setColumnWidth(numberColumn, width);
    }

    private void createBody(ExcelHelper excelUtil, int irow, List<MatriculaPreBean> listMatriculaPreBean, Workbook workbook, String tipoReporte) {
        CellStyle estiloGeneral = getStyleGeneral(workbook, HorizontalAlignment.CENTER);
        CellStyle estiloLeft = getStyleGeneral(workbook, HorizontalAlignment.LEFT);
        for (MatriculaPreBean item : listMatriculaPreBean) {
            excelUtil.replaceStyle(irow - 1, 0, estiloGeneral);
            excelUtil.replaceStyle(irow - 1, 1, estiloLeft);
            excelUtil.replaceStyle(irow - 1, 2, estiloLeft);
            excelUtil.replaceStyle(irow - 1, 3, estiloLeft);
            excelUtil.replaceStyle(irow - 1, 4, estiloGeneral);
            excelUtil.replaceStyle(irow - 1, 5, estiloLeft);
            excelUtil.replaceStyle(irow - 1, 6, estiloGeneral);
            excelUtil.replaceStyle(irow - 1, 7, estiloGeneral);
            excelUtil.replaceStyle(irow - 1, 8, estiloGeneral);
            excelUtil.replaceStyle(irow - 1, 9, estiloGeneral);
            excelUtil.replaceStyle(irow - 1, 10, estiloGeneral);
            excelUtil.replaceStyle(irow - 1, 11, estiloGeneral);

            excelUtil.replaceVal(irow - 1, 0, item.getFacultad());
            excelUtil.replaceVal(irow - 1, 1, item.getPrograma());
            excelUtil.replaceVal(irow - 1, 2, item.getPaterno() + " " + item.getMaterno() + " " + item.getNombres());
            excelUtil.replaceVal(irow - 1, 3, item.getCorreo());
            excelUtil.replaceVal(irow - 1, 4, item.getCodigo());
            excelUtil.replaceVal(irow - 1, 5, item.getCurso());
            excelUtil.replaceVal(irow - 1, 6, item.getClave());
            excelUtil.replaceVal(irow - 1, 7, item.getDia());
            excelUtil.replaceVal(irow - 1, 8, item.getHora_dictado());
            excelUtil.replaceVal(irow - 1, 9, item.getVacantes());
            excelUtil.replaceVal(irow - 1, 10, item.getMatriculados());
            excelUtil.replaceVal(irow - 1, 11, item.getModo_dictado());
            irow++;
        }
    }

    private String retornVacio(Long parametro) {
        if (parametro == null) {
            return "-";
        }
        return parametro.toString();
    }
}
