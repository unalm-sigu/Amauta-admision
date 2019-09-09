package pe.edu.lamolina.pivot.controller.consejeria.consejeros.view;

import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.view.AbstractView;
import pe.albatross.zelpers.file.excel.ExcelHelper;
import pe.albatross.zelpers.file.excel.ExcelStyles;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.Carrera;
import pe.edu.lamolina.model.consejeria.AlumnoConsejero;
import pe.edu.lamolina.pivot.zelper.constant.Constantine;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

@Component
public class TutoradosPorCondicionExcelView extends AbstractView {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private static final String CONTENT_TYPE_XLSX = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
    private static final String CONTENT_TYPE_XLS = "application/vnd.ms-excel";

    @Override
    protected void renderMergedOutputModel(Map<String, Object> map, HttpServletRequest request, HttpServletResponse response) throws Exception {

        //InputStream formato = (InputStream) map.get("formato");
// new XSSFWorkbook(formato);
        Workbook workbook = new XSSFWorkbook();
        if (workbook instanceof XSSFWorkbook) {
            setContentType(CONTENT_TYPE_XLSX);
        } else {
            setContentType(CONTENT_TYPE_XLS);
        }

        this.buildExcelDocument(map, workbook, request, response);

    }

    protected void buildExcelDocument(Map<String, Object> model, Workbook wb, HttpServletRequest request, HttpServletResponse response) throws Exception {
        DataSessionPivot ds = (DataSessionPivot) request.getSession().getAttribute(Constantine.SESSION_USUARIO);

        this.generateSheet(wb, model, ds);
        String fecha = new DateTime().toString("yyyMMdd_Hmm");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + "Alumnos_Aconsejados" + fecha + ".xlsx\"");
        response.setContentType(getContentType());

        ServletOutputStream out = response.getOutputStream();
        out.flush();
        wb.write(out);
        out.flush();
    }

    private void generateSheet(Workbook wb, Map<String, Object> model, DataSessionPivot ds) {
        //Sheet sheet = wb.getSheet("Hoja1");
        Sheet sheet = wb.createSheet("Hoja1");
        //sheet.setAutobreaks(true);
        this.createBody(wb, sheet, model, ds);
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

    private void createBody(Workbook wb, Sheet sheet, Map<String, Object> model, DataSessionPivot ds) {
        List<AlumnoConsejero> alumnosConsejero = (List<AlumnoConsejero>) model.get("alumnosConsejero");
        String condicion = (String) model.get("condicion");
        if (condicion.equals("conConsejero")) {
            condicion = "Con Consejero";
        } else if (condicion.equals("sinConsejero")) {
            condicion = "Sin Consejero";
        }
        Carrera carrera = null;
        if (!alumnosConsejero.isEmpty()) {
            carrera = alumnosConsejero.get(0).getAlumno().getCarrera();
        }

        ExcelHelper excelUtil = new ExcelHelper(sheet, wb);

        CellStyle headerCell = ExcelStyles.getStyleCellHeaderGrey(wb);
        CellStyle estiloNumero = getStyleNumero(wb);
        CellStyle estiloGeneral = getStyleGeneral(wb);

        String letterToMerge = ":E";
        int irow = 1;
        CellRangeAddress region = CellRangeAddress.valueOf("A" + irow + letterToMerge + irow);
        sheet.addMergedRegion(region);
        Row row = sheet.createRow(region.getFirstRow());
        Cell cell = row.createCell(region.getFirstColumn());
        cell.setCellValue("UNIVERSIDAD NACIONAL AGRARIA LA MOLINA");
        cell.setCellStyle(ExcelStyles.getCellTitle1Green(wb));
        irow++;

        region = CellRangeAddress.valueOf("A" + irow + letterToMerge + irow);
        sheet.addMergedRegion(region);
        row = sheet.createRow(region.getFirstRow());
        cell = row.createCell(region.getFirstColumn());
        cell.setCellValue("CARRERA " + (carrera != null ? carrera.getNombre().toUpperCase() : "SIN DATA"));
        cell.setCellStyle(ExcelStyles.getCellTitle3Green(wb));
        irow++;

        region = CellRangeAddress.valueOf("A" + irow + letterToMerge + irow);
        sheet.addMergedRegion(region);
        row = sheet.createRow(region.getFirstRow());
        cell = row.createCell(region.getFirstColumn());
        cell.setCellValue("TUTORADOS " + condicion.toUpperCase(Locale.ITALY));
        cell.setCellStyle(ExcelStyles.getCellTitle3Green(wb));
        irow++;

        String ciclo = "Ciclo Académico " + ds.getCicloAcademico().getDescripcion();
        String fecha = TypesUtil.getStringDate(new Date(), "dd/MM/yyyy H:mm:ss");
        region = CellRangeAddress.valueOf("A" + irow + letterToMerge + irow);
        sheet.addMergedRegion(region);
        row = sheet.createRow(region.getFirstRow());
        cell = row.createCell(region.getFirstColumn());
        cell.setCellValue(ciclo + " - " + fecha);
        cell.setCellStyle(ExcelStyles.getStyleBody(wb));
        cell.getCellStyle().setAlignment(CellStyle.ALIGN_RIGHT);
        irow++;

        int column = 0;
        excelUtil.replaceVal(irow - 1, column++, "N", headerCell);
        sheet.setColumnWidth((column - 1), 10 * 256);
        excelUtil.replaceVal(irow - 1, column++, "CÓDIGO ALUMNO", headerCell);
        sheet.setColumnWidth((column - 1), 20 * 256);
        excelUtil.replaceVal(irow - 1, column++, "NOMBRE ALUMNO", headerCell);
        sheet.setColumnWidth((column - 1), 35 * 256);
        excelUtil.replaceVal(irow - 1, column++, "CARRERA ALUMNO", headerCell);
        sheet.setColumnWidth((column - 1), 50 * 256);
        excelUtil.replaceVal(irow - 1, column++, "SITUACIÓN ACADEMICA", headerCell);
        sheet.setColumnWidth((column - 1), 30 * 256);

        int num = 1;
        for (AlumnoConsejero alumnoConsejero : alumnosConsejero) {
            column = 0;
            Alumno alumno = alumnoConsejero.getAlumno();
            excelUtil.replaceVal(irow, column++, num, estiloNumero);
            excelUtil.replaceVal(irow, column++, alumno.getCodigo());
            excelUtil.replaceVal(irow, column++, alumno.getPersona().getApellidosNombres());
            excelUtil.replaceVal(irow, column++, alumno.getCarrera().getNombre(), estiloGeneral);
            excelUtil.replaceVal(irow, column++, alumno.getSituacionAcademica().getNombre(), estiloGeneral);
            irow++;
            num++;
        }

    }

}
