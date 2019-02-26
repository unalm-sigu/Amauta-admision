package pe.edu.lamolina.pivot.controller.reporte.view;

import java.io.InputStream;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.view.AbstractView;
import pe.albatross.zelpers.file.excel.ExcelHelper;
import pe.edu.lamolina.model.academico.RecorridoIngresante;

@Component
public class IngresanteMuestraLabView extends AbstractView {

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

    protected void buildExcelDocument(Map<String, Object> model, Workbook wb, HttpServletRequest request, HttpServletResponse response) throws Exception {

        List<RecorridoIngresante> ingresantes = (List<RecorridoIngresante>) model.get("ingresantes");

        this.generateSheet(wb, ingresantes);
        String fecha = new DateTime().toString("dd/MM/yyyy_H:mm");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + "Alumnos_Muestra_Laboratorio_" + fecha + ".xlsx\"");
        response.setContentType(getContentType());

        ServletOutputStream out = response.getOutputStream();
        out.flush();
        wb.write(out);
        out.flush();
    }

    private void generateSheet(Workbook wb, List<RecorridoIngresante> ingresantes) {
        Sheet sheet = wb.getSheet("Hoja1");
        this.createBody(wb, sheet, ingresantes);
    }

    private CellStyle getStyleCabecera(Workbook workBook) {

        Font font = workBook.createFont();
        font.setFontName("Arial");
        font.setBoldweight(Font.BOLDWEIGHT_BOLD);

        CellStyle cell = workBook.createCellStyle();
        cell.setAlignment(CellStyle.ALIGN_CENTER);
        cell.setFont(font);
        cell.setBorderTop(HSSFCellStyle.BORDER_MEDIUM);
        cell.setBorderBottom(HSSFCellStyle.BORDER_MEDIUM);
        cell.setBorderRight(HSSFCellStyle.BORDER_MEDIUM);
        cell.setBorderLeft(HSSFCellStyle.BORDER_MEDIUM);

        return cell;
    }

    private CellStyle getStyleNumero(Workbook workBook) {

        Font font = workBook.createFont();
        font.setFontName("Arial");

        CellStyle cell = workBook.createCellStyle();
        cell.setAlignment(CellStyle.ALIGN_CENTER);
        cell.setFont(font);
        cell.setBorderTop(HSSFCellStyle.BORDER_THIN);
        cell.setBorderBottom(HSSFCellStyle.BORDER_THIN);
        cell.setBorderRight(HSSFCellStyle.BORDER_THIN);
        cell.setBorderLeft(HSSFCellStyle.BORDER_THIN);

        return cell;
    }

    private CellStyle getStyleGeneral(Workbook workBook) {

        CellStyle cell = workBook.createCellStyle();
        cell.setBorderTop(HSSFCellStyle.BORDER_THIN);
        cell.setBorderBottom(HSSFCellStyle.BORDER_THIN);
        cell.setBorderRight(HSSFCellStyle.BORDER_THIN);
        cell.setBorderLeft(HSSFCellStyle.BORDER_THIN);

        return cell;
    }

    private void createBody(Workbook wb, Sheet sheet, List<RecorridoIngresante> ingresantes) {
        ExcelHelper excelUtil = new ExcelHelper(sheet, wb);

        CellStyle estiloCabecera = getStyleCabecera(wb);
        CellStyle estiloCabeceraNombre = getStyleCabecera(wb);
        estiloCabeceraNombre.setAlignment(CellStyle.ALIGN_LEFT);
        CellStyle estiloCodigo = getStyleNumero(wb);
        CellStyle estiloNumero = getStyleNumero(wb);
        CellStyle estiloGeneral = getStyleGeneral(wb);

        int irow = 5;
        int firsRow = irow;
        int num = 1;
        int nameCol = 1;
        int codigoCol = nameCol + 8;
        int firmaCol = codigoCol + 3;

        //cabeceras
        excelUtil.mergeCell(sheet, irow - 1, irow - 1, nameCol, nameCol + 7);
        excelUtil.mergeCell(sheet, irow - 1, irow - 1, codigoCol, codigoCol + 2);
        excelUtil.mergeCell(sheet, irow - 1, irow - 1, firmaCol, firmaCol + 2);

        excelUtil.replaceStyle(irow - 1, 0, estiloCabecera);
        for (int i = codigoCol; i < firmaCol; i++) {
            excelUtil.replaceStyle(irow - 1, i, estiloCabecera);
        }
        for (int i = firmaCol; i <= firmaCol + 2; i++) {
            excelUtil.replaceStyle(irow - 1, i, estiloCabecera);
        }
        for (int i = nameCol; i < codigoCol; i++) {
            excelUtil.replaceStyle(irow - 1, i, estiloCabeceraNombre);
        }

        excelUtil.replaceVal(irow - 1, 0, "N°");
        excelUtil.replaceVal(irow - 1, nameCol, "ALUMNO");
        excelUtil.replaceVal(irow - 1, codigoCol, "CÓDIGO");
        excelUtil.replaceVal(irow - 1, firmaCol, "FIRMA");

        //datos
        for (RecorridoIngresante ingresante : ingresantes) {

            excelUtil.replaceStyle(irow, 0, estiloNumero);
            excelUtil.replaceVal(irow, 0, num++);

            excelUtil.mergeCell(sheet, irow, irow, nameCol, nameCol + 7);
            excelUtil.mergeCell(sheet, irow, irow, codigoCol, codigoCol + 2);
            excelUtil.mergeCell(sheet, irow, irow, firmaCol, firmaCol + 2);

            for (int i = codigoCol; i < firmaCol; i++) {
                excelUtil.replaceStyle(irow, i, estiloCodigo);
            }
            for (int i = nameCol; i < codigoCol; i++) {
                excelUtil.replaceStyle(irow, i, estiloGeneral);
            }
            for (int i = firmaCol; i <= firmaCol + 2; i++) {
                excelUtil.replaceStyle(irow, i, estiloGeneral);
            }

            excelUtil.replaceVal(irow, nameCol, ingresante.getAlumno().getPersona().getApellidosNombres());
            excelUtil.replaceVal(irow, codigoCol, ingresante.getAlumno().getCodigo());
            irow++;
        }

        sheet.setForceFormulaRecalculation(true);

    }

}
