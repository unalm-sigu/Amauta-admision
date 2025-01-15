package pe.edu.lamolina.amauta.controller.academico.pronabec.reporte;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.joda.time.DateTime;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.view.AbstractView;
import pe.albatross.zelpers.file.excel.ExcelHelper;
import pe.edu.lamolina.amauta.controller.academico.pronabec.MatriculadosBecadosBean;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@Component
public class RecordNotasBecadosExcelView extends AbstractView {

    private static final String CONTENT_TYPE_XLSX = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";

    @Override
    protected void renderMergedOutputModel(Map<String, Object> map, HttpServletRequest request, HttpServletResponse response) throws Exception {
        HSSFWorkbook workbook = new HSSFWorkbook();
        setContentType(CONTENT_TYPE_XLSX);
        this.buildExcelDocument(map, workbook, request, response);
    }

    private void buildExcelDocument(Map<String, Object> model, Workbook workbook, HttpServletRequest request, HttpServletResponse response) throws IOException {
        List<MatriculadosBecadosBean> listMatriculadosBecadosBean = (List<MatriculadosBecadosBean>) model.get("listMatriculadosBecadosBean");
        this.generateCelda(workbook, listMatriculadosBecadosBean);
        String fecha = new DateTime().toString("yyyMMdd_Hmm");
        String header = "Reporte_Situacion_y_Carrera_UNALM_";

        response.setHeader("Content-Disposition", "attachment; filename=\"" + header + fecha + ".xls\"");
        response.setContentType(getContentType());
        response.setStatus(200);
        ServletOutputStream out = response.getOutputStream();
        out.flush();
        workbook.write(out);
        out.flush();
    }

    private void generateCelda(Workbook workbook, List<MatriculadosBecadosBean> listMatriculadosBecadosBean){
        Sheet sheet = workbook.createSheet("Hoja1");
        ExcelHelper excelUtil = new ExcelHelper(sheet, workbook);
        int irow = 2;
        this.createHeader(excelUtil, workbook);
        this.createBody(excelUtil, irow, listMatriculadosBecadosBean, workbook);
    }

    private void createHeader(ExcelHelper excelUtil, Workbook workbook) {
        CellStyle estiloCabecera = getStyleCabecera(workbook, HorizontalAlignment.CENTER);
        CellStyle estiloCabeceraLeft = getStyleCabecera(workbook, HorizontalAlignment.LEFT);
        this.setWidthColumn(excelUtil.getSheet(), 1, 7000);
        this.setWidthColumn(excelUtil.getSheet(), 2, 7000);
        this.setWidthColumn(excelUtil.getSheet(), 3, 7000);
        this.setWidthColumn(excelUtil.getSheet(), 4, 7000);
        this.setWidthColumn(excelUtil.getSheet(), 5, 7000);

        excelUtil.replaceStyle(0, 0, estiloCabecera);
        excelUtil.replaceStyle(0, 1, estiloCabeceraLeft);
        excelUtil.replaceStyle(0, 2, estiloCabeceraLeft);
        excelUtil.replaceStyle(0, 3, estiloCabeceraLeft);
        excelUtil.replaceStyle(0, 4, estiloCabecera);
        excelUtil.replaceStyle(0, 5, estiloCabecera);

        excelUtil.replaceVal(0, 0, "DNI");
        excelUtil.replaceVal(0, 1, "CODIGO ESTUDIANTE");
        excelUtil.replaceVal(0, 2, "SITUACION UNALM");
        excelUtil.replaceVal(0, 3, "SITUACION PRONABEC");
        excelUtil.replaceVal(0, 4, "CARRERA UNALM");
        excelUtil.replaceVal(0, 5, "CARRERA PRONABEC");

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

    private void setWidthColumn(Sheet sheet, int numberColumn, int width) {
        sheet.setColumnWidth(numberColumn, width);
    }

    private void createBody(ExcelHelper excelUtil, int irow, List<MatriculadosBecadosBean> listMatriculadosBecadosBean, Workbook workbook) {
        CellStyle estiloGeneral = getStyleGeneral(workbook, HorizontalAlignment.CENTER);
        CellStyle estiloLeft = getStyleGeneral(workbook, HorizontalAlignment.LEFT);

        for (MatriculadosBecadosBean item : listMatriculadosBecadosBean){
            excelUtil.replaceStyle(irow - 1, 0, estiloGeneral);
            excelUtil.replaceStyle(irow - 1, 1, estiloLeft);
            excelUtil.replaceStyle(irow - 1, 2, estiloLeft);
            excelUtil.replaceStyle(irow - 1, 3, estiloLeft);
            excelUtil.replaceStyle(irow - 1, 4, estiloGeneral);
            excelUtil.replaceStyle(irow - 1, 5, estiloGeneral);

            excelUtil.replaceVal(irow - 1, 0, item.getDni());
            excelUtil.replaceVal(irow - 1, 1, item.getCodigo_estudiante());
            excelUtil.replaceVal(irow - 1, 2, item.getSituacion_unalm());
            excelUtil.replaceVal(irow - 1, 3, item.getSituacion_pronabec());
            excelUtil.replaceVal(irow - 1, 4, item.getCarrera_unalm());
            excelUtil.replaceVal(irow - 1, 5, item.getCarrera_pronabec());

            irow++;

        }

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

    private String retornVacio(Long parametro) {
        if (parametro == null) {
            return "-";
        }
        return parametro.toString();
    }


}
