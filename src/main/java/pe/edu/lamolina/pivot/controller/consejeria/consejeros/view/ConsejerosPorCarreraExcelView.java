package pe.edu.lamolina.pivot.controller.consejeria.consejeros.view;

import java.util.List;
import java.util.Map;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
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
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.model.academico.Carrera;
import pe.edu.lamolina.model.consejeria.Consejero;
import pe.edu.lamolina.pivot.zelper.constant.Constantine;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

@Component
public class ConsejerosPorCarreraExcelView extends AbstractView {

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
        DataSessionPivot ds = (DataSessionPivot) request.getSession().getAttribute(Constantine.SESSION_USUARIO);
        List<Consejero> consejeros = (List<Consejero>) model.get("consejeros");

        this.generateSheet(wb, consejeros, ds);
        String fecha = new DateTime().toString("yyyMMdd_Hmm");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + "Consejeros_por_especialidad" + fecha + ".xlsx\"");
        response.setContentType(getContentType());

        ServletOutputStream out = response.getOutputStream();
        out.flush();
        wb.write(out);
        out.flush();
    }

    private void generateSheet(Workbook wb, List<Consejero> consejero, DataSessionPivot ds) {
        //Sheet sheet = wb.getSheet("Hoja1");
        Sheet sheet = wb.createSheet("Hoja1");
        //sheet.setAutobreaks(true);
        this.createBody(wb, sheet, consejero, ds);
    }

    private CellStyle getStyleCabecera(Workbook workBook) {

        Font font = workBook.createFont();
        font.setFontName("Arial");
        font.setBoldweight(Font.BOLDWEIGHT_BOLD);

        CellStyle cell = workBook.createCellStyle();
        cell.setAlignment(CellStyle.ALIGN_CENTER);
        cell.setFont(font);
//        cell.setBorderTop(HSSFCellStyle.BORDER_NONE);
//        cell.setBorderBottom(HSSFCellStyle.BORDER_NONE);
//        cell.setBorderRight(HSSFCellStyle.BORDER_NONE);
//        cell.setBorderLeft(HSSFCellStyle.BORDER_NONE);
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

    private void createBody(Workbook wb, Sheet sheet, List<Consejero> consejeros, DataSessionPivot ds) {
        ExcelHelper excelUtil = new ExcelHelper(sheet, wb);

        CellStyle estiloCabecera = getStyleCabecera(wb);
        CellStyle estiloCabeceraNombre = getStyleCabecera(wb);
        estiloCabeceraNombre.setAlignment(CellStyle.ALIGN_LEFT);

        CellStyle estiloNumero = getStyleNumero(wb);
        CellStyle estiloGeneral = getStyleGeneral(wb);

        int irow = 1;
        CellRangeAddress region = CellRangeAddress.valueOf("A" + irow + ":E" + irow);
        sheet.addMergedRegion(region);
        //  CellStyle cellStyle = getStyleBodyDavid(wb);
        Row row = sheet.createRow(region.getFirstRow());
        Cell cell = row.createCell(region.getFirstColumn());
        cell.setCellValue("UNIVERSIDAD NACIONAL AGRARIA LA MOLINA");
        cell.setCellStyle(estiloCabecera);
        irow++;

        Carrera carrera = null;
        if (!consejeros.isEmpty()) {
            carrera = consejeros.get(0).getCarrera();
        }
        region = CellRangeAddress.valueOf("A" + irow + ":E" + irow);
        sheet.addMergedRegion(region);
        row = sheet.createRow(region.getFirstRow());
        cell = row.createCell(region.getFirstColumn());
        cell.setCellValue("ESPECIALIDAD DE " + (carrera == null ? "SIN DATOS" : carrera.getNombre()));
        cell.setCellStyle(estiloCabecera);
        irow++;

        region = CellRangeAddress.valueOf("A" + irow + ":E" + irow);
        sheet.addMergedRegion(region);
        row = sheet.createRow(region.getFirstRow());
        cell = row.createCell(region.getFirstColumn());
        cell.setCellValue("");
        cell.setCellStyle(estiloCabecera);
        irow++;

        region = CellRangeAddress.valueOf("A" + irow + ":E" + irow);
        sheet.addMergedRegion(region);
        row = sheet.createRow(region.getFirstRow());
        cell = row.createCell(region.getFirstColumn());
        cell.setCellValue("LISTA DE DOCENTES CONSEJEROS DE LA ESPECIALIDAD");
        cell.setCellStyle(estiloCabecera);
        irow++;

        region = CellRangeAddress.valueOf("A" + irow + ":E" + irow);
        sheet.addMergedRegion(region);
        row = sheet.createRow(region.getFirstRow());
        cell = row.createCell(region.getFirstColumn());
        cell.setCellValue("");
        cell.setCellStyle(estiloGeneral);
        irow++;

        region = CellRangeAddress.valueOf("A" + irow + ":E" + irow);
        sheet.addMergedRegion(region);
        row = sheet.createRow(region.getFirstRow());
        cell = row.createCell(region.getFirstColumn());
        cell.setCellValue("La Molina " + TypesUtil.getStringDateTimeLongFormat(ds.getFechaAccionAudit()));
        cell.setCellStyle(estiloGeneral);
        irow++;

//        excelUtil.mergeCell(sheet, irow, irow, 0, 4);
//        excelUtil.replaceVal(irow, 1, "fecha", estiloCabecera);
//        irow++;
        int column = 0;
        excelUtil.replaceVal(irow, column++, "N", estiloCabecera);
        sheet.setColumnWidth((column - 1), 10 * 256);
        excelUtil.replaceVal(irow, column++, "CODIGO", estiloCabecera);
        sheet.setColumnWidth((column - 1), 20 * 256);
        excelUtil.replaceVal(irow, column++, "NOMBRE DEL PROFESOR CONSEJERO", estiloCabecera);
        sheet.setColumnWidth((column - 1), 50 * 256);
        excelUtil.replaceVal(irow, column++, "DPTO. ACADÉMICO", estiloCabecera);
        sheet.setColumnWidth((column - 1), 20 * 256);
        excelUtil.replaceVal(irow, column++, "NRO. ACONSEJADOS", estiloCabecera);
        sheet.setColumnWidth((column - 1), 35 * 256);

        irow++;

        int num = 1;
        for (Consejero consejero : consejeros) {
            column = 0;
            excelUtil.replaceVal(irow, column++, num++, estiloNumero);
            excelUtil.replaceVal(irow, column++, consejero.getDocente().getCodigo(), estiloGeneral);
            excelUtil.replaceVal(irow, column++, consejero.getColaborador().getPersona().getApellidosNombres(), estiloGeneral);
            excelUtil.replaceVal(irow, column++, consejero.getDocente().getDepartamentoAcademico().getNombre(), estiloGeneral);
            excelUtil.replaceVal(irow, column++, "", estiloGeneral);
            irow++;

        }

        // sheet.setForceFormulaRecalculation(true);
    }

    private CellStyle getStyleBodyDavid(Workbook workBook) {
        Font fontBody = workBook.createFont();
        fontBody.setFontName("Arial");
        fontBody.setColor(IndexedColors.BLACK.getIndex());
        fontBody.setBold(true);
        fontBody.setFontHeight((short) 300);
//        short border = CellStyle.BORDER_THIN;

        CellStyle cellBody = workBook.createCellStyle();
        cellBody.setFont(fontBody);
        cellBody.setWrapText(true);
        cellBody.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
        cellBody.setAlignment(CellStyle.ALIGN_CENTER);

        return cellBody;
    }

}
