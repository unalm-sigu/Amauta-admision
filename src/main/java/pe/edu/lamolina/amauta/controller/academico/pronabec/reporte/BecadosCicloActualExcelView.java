package pe.edu.lamolina.amauta.controller.academico.pronabec.reporte;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.joda.time.DateTime;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.view.AbstractView;
import pe.albatross.zelpers.file.excel.ExcelHelper;
import pe.albatross.zelpers.miscelanea.ObjectUtil;
import pe.edu.lamolina.amauta.controller.academico.pronabec.BecadosFilterBean;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@Component
public class BecadosCicloActualExcelView extends AbstractView {
    private static final String CONTENT_TYPE_XLSX = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";

    @Override
    protected void renderMergedOutputModel(Map<String, Object> map, HttpServletRequest request, HttpServletResponse response) throws Exception {
        HSSFWorkbook workbook = new HSSFWorkbook();
        setContentType(CONTENT_TYPE_XLSX);
        this.buildExcelDocument(map, workbook, request, response);
    }

    private void buildExcelDocument(Map<String, Object> model, Workbook workbook, HttpServletRequest request, HttpServletResponse response) throws IOException {
        List<BecadosFilterBean> listAptoPreBean = (List<BecadosFilterBean>) model.get("listBecadosFilter");
        //String tipoReporte = (String) model.get("tipoReporte");
        BecadosFilterBean becadosFilterBeanX = (BecadosFilterBean) model.get("objtBecados");

        this.generateCelda(workbook, listAptoPreBean, becadosFilterBeanX);
        String fecha = new DateTime().toString("yyyMMdd_Hmm");

        String header = "Reporte-Becado-Ciclo-Actual";

        response.setHeader("Content-Disposition", "attachment; filename=\"" + header + fecha + ".xls\"");
        response.setContentType(getContentType());
        response.setStatus(200);
        ServletOutputStream out = response.getOutputStream();
        out.flush();
        workbook.write(out);
        out.flush();
    }

    private void generateCelda(Workbook workbook, List<BecadosFilterBean> listAptoPreBean, BecadosFilterBean becadosFilterBeanX ) {
        Sheet sheet = workbook.createSheet("Hoja1");
        //ObjectUtil.printAttr(becadosFilterBeanX);
        ExcelHelper excelUtil = new ExcelHelper(sheet, workbook);
        int irow = 2; // fila de inicio para la data
        this.createHeader(excelUtil, workbook,becadosFilterBeanX);
        this.createBody(excelUtil, irow, listAptoPreBean, workbook, becadosFilterBeanX);
    }

    private void createHeader(ExcelHelper excelUtil, Workbook workbook, BecadosFilterBean becadosFilterBeanX) {
        CellStyle estiloCabecera = getStyleCabecera(workbook, HorizontalAlignment.CENTER);
        CellStyle estiloCabeceraLeft = getStyleCabecera(workbook, HorizontalAlignment.LEFT);
        this.setWidthColumn(excelUtil.getSheet(), 1, 7000);
        this.setWidthColumn(excelUtil.getSheet(), 2, 12000);
        this.setWidthColumn(excelUtil.getSheet(), 3, 10000);
        this.setWidthColumn(excelUtil.getSheet(), 4, 5000);
        this.setWidthColumn(excelUtil.getSheet(), 5, 10000);
        this.setWidthColumn(excelUtil.getSheet(), 6, 10000);
        this.setWidthColumn(excelUtil.getSheet(), 7, 7000);
        this.setWidthColumn(excelUtil.getSheet(), 8, 7000);
        this.setWidthColumn(excelUtil.getSheet(), 9, 10000);
        this.setWidthColumn(excelUtil.getSheet(), 10, 5000);

        excelUtil.replaceStyle(0, 0, estiloCabecera);
        excelUtil.replaceStyle(0, 1, estiloCabeceraLeft);
        excelUtil.replaceStyle(0, 2, estiloCabeceraLeft);
        excelUtil.replaceStyle(0, 3, estiloCabeceraLeft);
        excelUtil.replaceStyle(0, 4, estiloCabecera);
        excelUtil.replaceStyle(0, 5, estiloCabecera);
        excelUtil.replaceStyle(0, 6, estiloCabeceraLeft);
        excelUtil.replaceStyle(0, 7, estiloCabecera);
        excelUtil.replaceStyle(0, 8, estiloCabecera);
        excelUtil.replaceStyle(0, 9, estiloCabecera);
        excelUtil.replaceStyle(0, 10, estiloCabecera);

        excelUtil.replaceVal(0, 0, "DNI");
        excelUtil.replaceVal(0, 1, "APELLIDOS Y NOMBRES");
        excelUtil.replaceVal(0, 2, "NOMBRE DE LA INSTITUCION");
        excelUtil.replaceVal(0, 3, "CARRERA");
        excelUtil.replaceVal(0, 4, "REALIZO MATRICULA EL SEMESTRE ACTUAL");
        excelUtil.replaceVal(0, 5, "CICLO EN EL SEMESTRE");
        excelUtil.replaceVal(0, 6, "NRO CURSOS MATRICULADOS");
        excelUtil.replaceVal(0, 7, "CREDITO");
        excelUtil.replaceVal(0, 8, "NRO ELECTIVOS MATRICULADOS");
        excelUtil.replaceVal(0, 9, "REALIZO CAMBIO CARRERA");
        excelUtil.replaceVal(0, 10, "CURSO MATRICULADO POR TERCERA VEZ");


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

    private void createBody(ExcelHelper excelUtil, int irow, List<BecadosFilterBean> listBecadosFilter, Workbook workbook, BecadosFilterBean becadosFilterBeanX) {
        CellStyle estiloGeneral = getStyleGeneral(workbook, HorizontalAlignment.CENTER);
        CellStyle estiloLeft = getStyleGeneral(workbook, HorizontalAlignment.LEFT);
        ObjectUtil.printAttr(listBecadosFilter);
        for (BecadosFilterBean item : listBecadosFilter) {
            excelUtil.replaceStyle(irow - 1, 0, estiloGeneral);
            excelUtil.replaceStyle(irow - 1, 1, estiloLeft);
            excelUtil.replaceStyle(irow - 1, 2, estiloLeft);
            excelUtil.replaceStyle(irow - 1, 3, estiloLeft);
            excelUtil.replaceStyle(irow - 1, 4, estiloGeneral);
            excelUtil.replaceStyle(irow - 1, 5, estiloGeneral);
            excelUtil.replaceStyle(irow - 1, 6, estiloGeneral);
            excelUtil.replaceStyle(irow - 1, 7, estiloGeneral);
            excelUtil.replaceStyle(irow - 1, 8, estiloLeft);
            excelUtil.replaceStyle(irow - 1, 9, estiloGeneral);
            excelUtil.replaceStyle(irow - 1, 10, estiloGeneral);

            excelUtil.replaceVal(irow - 1, 0, item.getDni());
            excelUtil.replaceVal(irow - 1, 1, item.getApellidos_nombres());
            excelUtil.replaceVal(irow - 1, 2, item.getNombre_institucion());
            excelUtil.replaceVal(irow - 1, 3, item.getCarrera());
            excelUtil.replaceVal(irow - 1, 4, item.getSe_matriculo());
            excelUtil.replaceVal(irow - 1, 5, item.getPeriodo_academico());
            excelUtil.replaceVal(irow - 1, 6, item.getCurso_matriculado());
            excelUtil.replaceVal(irow - 1, 7, item.getCreditos());
            excelUtil.replaceVal(irow - 1, 8, item.getElectivo_matriculado());
            excelUtil.replaceVal(irow - 1, 9, item.getCambio_carrera());
            excelUtil.replaceVal(irow - 1, 10, item.getTercera_vez());

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
