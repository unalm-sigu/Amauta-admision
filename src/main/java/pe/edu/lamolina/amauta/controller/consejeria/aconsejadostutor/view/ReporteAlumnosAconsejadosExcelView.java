package pe.edu.lamolina.amauta.controller.consejeria.aconsejadostutor.view;

import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.view.AbstractView;
import pe.albatross.zelpers.file.excel.ExcelHelper;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.consejeria.Consejero;
import pe.edu.lamolina.model.constantines.GlobalConstantine;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;

@Component
public class ReporteAlumnosAconsejadosExcelView extends AbstractView {

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
        DataSessionPivot ds = (DataSessionPivot) request.getSession().getAttribute(GlobalConstantine.SESSION_USUARIO);
        List<Consejero> consejeros = (List<Consejero>) model.get("consejeros");
        List<Alumno> alumnos = (List<Alumno>) model.get("alumnosConsejero");

        this.generateSheet(wb, consejeros, alumnos, ds);
        String fecha = new DateTime().toString("yyyMMdd_Hmm");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + "Alumnos_Aconsejados" + fecha + ".xlsx\"");
        response.setContentType(getContentType());

        ServletOutputStream out = response.getOutputStream();
        out.flush();
        wb.write(out);
        out.flush();
    }

    private void generateSheet(Workbook wb, List<Consejero> consejero, List<Alumno> alumnos, DataSessionPivot ds) {
        //Sheet sheet = wb.getSheet("Hoja1");
        Sheet sheet = wb.createSheet("Hoja1");
        //sheet.setAutobreaks(true);
        this.createBody(wb, sheet, consejero, alumnos, ds);
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

    private void createBody(Workbook wb, Sheet sheet, List<Consejero> consejeros, List<Alumno> alumnos, DataSessionPivot ds) {
        ExcelHelper excelUtil = new ExcelHelper(sheet, wb);

        CellStyle estiloCabecera = getStyleCabecera(wb);
        CellStyle estiloCabeceraNombre = getStyleCabecera(wb);
        estiloCabeceraNombre.setAlignment(HorizontalAlignment.LEFT);
        CellStyle estiloCodigo = getStyleNumero(wb);
        CellStyle estiloNumero = getStyleNumero(wb);
        CellStyle estiloGeneral = getStyleGeneral(wb);

        int irow = 7;

        excelUtil.replaceVal(3, 2, "Ciclo Académico " + ds.getCicloAcademico().getDescripcion());
        excelUtil.replaceVal(4, 2, "Fecha " + TypesUtil.getStringDate(new Date(), "dd/MM/yyyy H:mm:ss"));
//ponderado, estado academico
        int column = 0;
        excelUtil.replaceVal(irow - 1, column++, "CODIGO TUTOR", estiloCabecera);
        sheet.setColumnWidth((column - 1), 20 * 256);
        excelUtil.replaceVal(irow - 1, column++, "TUTOR", estiloCabecera);
        sheet.setColumnWidth((column - 1), 50 * 256);
        excelUtil.replaceVal(irow - 1, column++, "CÓDIGO ALUMNO", estiloCabecera);
        sheet.setColumnWidth((column - 1), 20 * 256);
        excelUtil.replaceVal(irow - 1, column++, "NOMBRE ALUMNO", estiloCabecera);
        sheet.setColumnWidth((column - 1), 50 * 256);
        excelUtil.replaceVal(irow - 1, column++, "CARRERA ALUMNO", estiloCabecera);
        sheet.setColumnWidth((column - 1), 50 * 256);
        excelUtil.replaceVal(irow - 1, column++, "SITUACIÓN ACADEMICA", estiloCabecera);
        sheet.setColumnWidth((column - 1), 50 * 256);

//        for (int i = 1; i < column; i++) {
//            sheet.setColumnWidth(i, 20 * 256);
//        }
        //datos
        int num = 1;
        for (Consejero consejero : consejeros) {
            for (Alumno alumno : consejero.getAlumno()) {
                column = 0;
                excelUtil.replaceVal(irow, column++, consejero.getDocente().getCodigo());
                sheet.setColumnWidth((column - 1), 20 * 256);
                excelUtil.replaceVal(irow, column++, consejero.getColaborador().getPersona().getApellidosNombres(), estiloGeneral);
                sheet.setColumnWidth((column - 1), 50 * 256);
                excelUtil.replaceVal(irow, column++, alumno.getCodigo(), estiloGeneral);
                sheet.setColumnWidth((column - 1), 20 * 256);
                excelUtil.replaceVal(irow, column++, alumno.getPersona().getApellidosNombres(), estiloGeneral);
                sheet.setColumnWidth((column - 1), 50 * 256);
                excelUtil.replaceVal(irow, column++, alumno.getCarrera().getNombre(), estiloGeneral);
                sheet.setColumnWidth((column - 1), 50 * 256);
                excelUtil.replaceVal(irow, column++, alumno.getSituacionAcademica().getNombre(), estiloGeneral);
                sheet.setColumnWidth((column - 1), 50 * 256);
                irow++;
            }
        }

        sheet.setForceFormulaRecalculation(true);

    }

}
