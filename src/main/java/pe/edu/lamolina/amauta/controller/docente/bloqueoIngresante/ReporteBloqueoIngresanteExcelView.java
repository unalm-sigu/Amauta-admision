package pe.edu.lamolina.amauta.controller.docente.bloqueoIngresante;

import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
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
import pe.edu.lamolina.model.constantines.GlobalConstantine;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;
import pe.edu.lamolina.model.academico.MatriculaBloqueoIngresante;

@Component
public class ReporteBloqueoIngresanteExcelView extends AbstractView {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private static final String CONTENT_TYPE_XLSX = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
    private static final String CONTENT_TYPE_XLS = "application/vnd.ms-excel";

    @Override
    protected void renderMergedOutputModel(Map<String, Object> map, HttpServletRequest request, HttpServletResponse response) throws Exception {

        Workbook workbook = new XSSFWorkbook();
        if (workbook instanceof XSSFWorkbook) {
            setContentType(CONTENT_TYPE_XLSX);
        } else {
            setContentType(CONTENT_TYPE_XLS);
        }

        this.buildExcelDocument(map, workbook, request, response);

    }

    protected void buildExcelDocument(Map<String, Object> model, Workbook wb, HttpServletRequest request, HttpServletResponse response) throws Exception {
        DataSessionPivot ds = (DataSessionPivot) request.getSession().getAttribute(GlobalConstantine.SESSION_USUARIO);

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
        font.setBold(true);

        CellStyle cell = workBook.createCellStyle();
        cell.setAlignment(HorizontalAlignment.CENTER);
        cell.setFont(font);
        cell.setBorderTop(BorderStyle.MEDIUM);
        cell.setBorderBottom(BorderStyle.MEDIUM);
        cell.setBorderRight(BorderStyle.MEDIUM);
        cell.setBorderLeft(BorderStyle.MEDIUM);

        return cell;
    }

    private CellStyle getStyleNumero(Workbook workBook) {

        Font font = workBook.createFont();
        font.setFontName("Arial");

        CellStyle cell = workBook.createCellStyle();
        cell.setAlignment(HorizontalAlignment.CENTER);
        cell.setFont(font);
        cell.setBorderTop(BorderStyle.THIN);
        cell.setBorderBottom(BorderStyle.THIN);
        cell.setBorderRight(BorderStyle.THIN);
        cell.setBorderLeft(BorderStyle.THIN);

        return cell;
    }

    private CellStyle getStyleGeneral(Workbook workBook) {

        CellStyle cell = workBook.createCellStyle();
        cell.setBorderTop(BorderStyle.THIN);
        cell.setBorderBottom(BorderStyle.THIN);
        cell.setBorderRight(BorderStyle.THIN);
        cell.setBorderLeft(BorderStyle.THIN);

        return cell;
    }

    private void createBody(Workbook wb, Sheet sheet, Map<String, Object> model, DataSessionPivot ds) {
        List<MatriculaBloqueoIngresante> lista = (List<MatriculaBloqueoIngresante>) model.get("matriculaBloqueoIngresantes");

        ExcelHelper excelUtil = new ExcelHelper(sheet, wb);

        CellStyle headerCell = ExcelStyles.getStyleCellHeaderGrey(wb);
        CellStyle estiloCodigo = getStyleNumero(wb);
        CellStyle estiloNumero = getStyleNumero(wb);
        CellStyle estiloGeneral = getStyleGeneral(wb);

        String letterToMerge = ":H";
        int irow = 1;
        CellRangeAddress region = CellRangeAddress.valueOf("A" + irow + letterToMerge + irow);
        sheet.addMergedRegion(region);
        Row row = sheet.createRow(region.getFirstRow());
        Cell cell = row.createCell(region.getFirstColumn());
        cell.setCellValue("UNIVERSIDAD NACIONAL AGRARIA LA MOLINA");
        cell.setCellStyle(ExcelStyles.getCellTitle1Green(wb));
        irow++;

        String ciclo = "Ciclo Académico " + ds.getCicloAcademico().getDescripcion();
        String fecha = TypesUtil.getStringDate(new Date(), "dd/MM/yyyy H:mm:ss");
        region = CellRangeAddress.valueOf("A" + irow + letterToMerge + irow);
        sheet.addMergedRegion(region);
        row = sheet.createRow(region.getFirstRow());
        cell = row.createCell(region.getFirstColumn());
        cell.setCellValue(ciclo + " - " + fecha);
        cell.setCellStyle(ExcelStyles.getStyleBody(wb));
        cell.getCellStyle().setAlignment(HorizontalAlignment.LEFT);
        irow++;

        int column = 0;
        excelUtil.replaceVal(irow - 1, column++, "N", headerCell);
        sheet.setColumnWidth((column - 1), 8 * 256);
        excelUtil.replaceVal(irow - 1, column++, "MODALIDAD INGRESO", headerCell);
        sheet.setColumnWidth((column - 1), 50 * 256);
        excelUtil.replaceVal(irow - 1, column++, "CARRERA", headerCell);
        sheet.setColumnWidth((column - 1), 50 * 256);
        excelUtil.replaceVal(irow - 1, column++, "ALUMNO", headerCell);
        sheet.setColumnWidth((column - 1), 40 * 256);
        excelUtil.replaceVal(irow - 1, column++, "CÓDIGO ALUMNO", headerCell);
        sheet.setColumnWidth((column - 1), 20 * 256);
        excelUtil.replaceVal(irow - 1, column++, "EMAIL PERSONAL", headerCell);
        sheet.setColumnWidth((column - 1), 35 * 256);
        excelUtil.replaceVal(irow - 1, column++, "EMAIL INSTITUCIONAL", headerCell);
        sheet.setColumnWidth((column - 1), 35 * 256);
        excelUtil.replaceVal(irow - 1, column++, "TELEFONO", headerCell);
        sheet.setColumnWidth((column - 1), 20 * 256);
        excelUtil.replaceVal(irow - 1, column++, "CELULAR", headerCell);
        sheet.setColumnWidth((column - 1), 20 * 256);
        excelUtil.replaceVal(irow - 1, column++, "RM", headerCell);
        sheet.setColumnWidth((column - 1), 15 * 256);
        excelUtil.replaceVal(irow - 1, column++, "RV", headerCell);
        sheet.setColumnWidth((column - 1), 15 * 256);
        excelUtil.replaceVal(irow - 1, column++, "MATEMATICA", headerCell);
        sheet.setColumnWidth((column - 1), 15 * 256);
        excelUtil.replaceVal(irow - 1, column++, "FISICA", headerCell);
        sheet.setColumnWidth((column - 1), 15 * 256);
        excelUtil.replaceVal(irow - 1, column++, "QUIMICA", headerCell);
        sheet.setColumnWidth((column - 1), 15 * 256);
        excelUtil.replaceVal(irow - 1, column++, "BIOLOGIA", headerCell);
        sheet.setColumnWidth((column - 1), 15 * 256);
        excelUtil.replaceVal(irow - 1, column++, "MATRICULAR", headerCell);
        sheet.setColumnWidth((column - 1), 15 * 256);

        int num = 1;

        for (MatriculaBloqueoIngresante item : lista) {

            column = 0;
            excelUtil.replaceVal(irow, column++, num, estiloNumero);
            excelUtil.replaceVal(irow, column++, item.getIngresante().getPostulante().getModalidadIngreso().getNombre(), estiloGeneral);
            excelUtil.replaceVal(irow, column++, item.getIngresante().getCarrera().getNombre(), estiloGeneral);
            excelUtil.replaceVal(irow, column++, item.getIngresante().getPostulante().getPersona().getApellidosNombres(), estiloGeneral);
            excelUtil.replaceVal(irow, column++, item.getIngresante().getCodigo(), estiloGeneral);
            excelUtil.replaceVal(irow, column++, item.getIngresante().getPostulante().getPersona().getEmail() != null ? item.getIngresante().getPostulante().getPersona().getEmail() : "", estiloGeneral);
            excelUtil.replaceVal(irow, column++, item.getIngresante().getPostulante().getPersona().getEmailCompania() != null ? item.getIngresante().getPostulante().getPersona().getEmailCompania() : "", estiloGeneral);
            excelUtil.replaceVal(irow, column++, item.getIngresante().getPostulante().getPersona().getTelefono() != null ? item.getIngresante().getPostulante().getPersona().getTelefono() : "", estiloGeneral);
            excelUtil.replaceVal(irow, column++, item.getIngresante().getPostulante().getPersona().getCelular() != null ? item.getIngresante().getPostulante().getPersona().getCelular() : "", estiloGeneral);
            excelUtil.replaceVal(irow, column++, item.getRm(), estiloGeneral);
            excelUtil.replaceVal(irow, column++, item.getRv(), estiloGeneral);
            excelUtil.replaceVal(irow, column++, item.getMatematica(), estiloGeneral);
            excelUtil.replaceVal(irow, column++, item.getFisica(), estiloGeneral);
            excelUtil.replaceVal(irow, column++, item.getQuimica(), estiloGeneral);
            excelUtil.replaceVal(irow, column++, item.getBiologia(), estiloGeneral);
            excelUtil.replaceVal(irow, column++, item.getMatricula() ? "SI" : "NO", estiloGeneral);
            irow++;
            num++;
        }

    }

}
