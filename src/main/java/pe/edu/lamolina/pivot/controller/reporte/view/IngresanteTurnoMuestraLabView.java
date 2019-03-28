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
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.RecorridoIngresante;
import pe.edu.lamolina.model.inscripcion.TurnoEntrevistaObuae;

@Component
public class IngresanteTurnoMuestraLabView extends AbstractView {

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
        CicloAcademico ciclo = (CicloAcademico) model.get("ciclo");
        TurnoEntrevistaObuae turno = (TurnoEntrevistaObuae) model.get("turno");

        this.generateSheet(wb, ingresantes, turno, ciclo);
        String fecha = new DateTime().toString("dd/MM/yyyy_H:mm");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + "Alumnos_Muestra_Laboratorio_" + fecha + ".xlsx\"");
        response.setContentType(getContentType());

        ServletOutputStream out = response.getOutputStream();
        out.flush();
        wb.write(out);
        out.flush();
    }

    private void generateSheet(Workbook wb, List<RecorridoIngresante> ingresantes, TurnoEntrevistaObuae turno, CicloAcademico ciclo) {
        Sheet sheet = wb.getSheet("Hoja1");
        this.createBody(wb, sheet, ingresantes, turno, ciclo);
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

    private void createBody(Workbook wb, Sheet sheet, List<RecorridoIngresante> ingresantes, TurnoEntrevistaObuae turno, CicloAcademico ciclo) {
        ExcelHelper excelUtil = new ExcelHelper(sheet, wb);

        CellStyle estiloCabeceraNombre = getStyleCabecera(wb);
        estiloCabeceraNombre.setAlignment(CellStyle.ALIGN_LEFT);
        CellStyle estiloCodigo = getStyleNumero(wb);
        CellStyle estiloNumero = getStyleNumero(wb);
        CellStyle estiloGeneral = getStyleGeneral(wb);

        int irow = 7;

        excelUtil.replaceVal(3, 0, "Ciclo Académico " + ciclo.getDescripcion());
        excelUtil.replaceVal(4, 2, "Fecha " + TypesUtil.getStringDate(turno.getFecha(), "dd/MM/yyyy"));

        //datos
        int num = 1;
        for (RecorridoIngresante ingresante : ingresantes) {

            excelUtil.replaceStyle(irow, 0, estiloNumero);
            excelUtil.replaceVal(irow, 0, num++);

            excelUtil.replaceStyle(irow, 1, estiloCodigo);
            excelUtil.replaceStyle(irow, 2, estiloGeneral);
            excelUtil.replaceStyle(irow, 3, estiloGeneral);

            excelUtil.replaceVal(irow, 1, ingresante.getAlumno().getCodigo());
            excelUtil.replaceVal(irow, 2, ingresante.getAlumno().getPersona().getApellidosNombres());
            irow++;
        }

        sheet.setForceFormulaRecalculation(true);

    }

}
