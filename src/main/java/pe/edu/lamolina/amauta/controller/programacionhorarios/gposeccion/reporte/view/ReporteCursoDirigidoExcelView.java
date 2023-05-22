package pe.edu.lamolina.amauta.controller.programacionhorarios.gposeccion.reporte.view;

import java.io.InputStream;
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
import pe.edu.lamolina.amauta.controller.programacionhorarios.gposeccion.reporte.dto.CursoDirigidoDTO;
import pe.edu.lamolina.model.academico.CicloAcademico;

@Component
public class ReporteCursoDirigidoExcelView extends AbstractView {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private static final String CONTENT_TYPE_XLSX = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
    private static final String CONTENT_TYPE_XLS = "application/vnd.ms-excel";

    @Override
    protected void renderMergedOutputModel(Map<String, Object> map, HttpServletRequest request, HttpServletResponse response) throws Exception {

        InputStream formato = (InputStream) map.get("formato");
//
        Workbook workbook = new XSSFWorkbook(formato);
        if (workbook instanceof XSSFWorkbook) {
            setContentType(CONTENT_TYPE_XLSX);
        } else {
            setContentType(CONTENT_TYPE_XLS);
        }

        this.buildExcelDocument(map, workbook, request, response);

    }

    protected void buildExcelDocument(Map<String, Object> model, Workbook wb, HttpServletRequest request, HttpServletResponse response) throws Exception {

        List<CursoDirigidoDTO> cursoDirigidos = (List<CursoDirigidoDTO>) model.get("cursosDirigidos");
        CicloAcademico ciclo = (CicloAcademico) model.get("ciclo");

        this.generateSheet(wb, cursoDirigidos, ciclo);
        String fecha = new DateTime().toString("yyyMMdd_Hmm");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + "Cursos_Dirigidos_" + fecha + ".xlsx\"");
        response.setContentType(getContentType());

        ServletOutputStream out = response.getOutputStream();
        out.flush();
        wb.write(out);
        out.flush();
    }

    private void generateSheet(Workbook wb, List<CursoDirigidoDTO> cursoDirigidos, CicloAcademico ciclo) {
        Sheet sheet = wb.getSheet("Hoja1");
        this.createBody(wb, sheet, cursoDirigidos, ciclo);
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

    private void createBody(Workbook wb, Sheet sheet, List<CursoDirigidoDTO> cursoDirigidos, CicloAcademico ciclo) {
        ExcelHelper excelUtil = new ExcelHelper(sheet, wb);

        CellStyle estiloCabecera = getStyleCabecera(wb);
        CellStyle estiloCabeceraNombre = getStyleCabecera(wb);
        estiloCabeceraNombre.setAlignment(HorizontalAlignment.LEFT);
        CellStyle estiloCodigo = getStyleNumero(wb);
        CellStyle estiloNumero = getStyleNumero(wb);
        CellStyle estiloGeneral = getStyleGeneral(wb);

        int irow = 8;

        excelUtil.replaceVal(5, 6, "Ciclo Académico " + ciclo.getDescripcion());
        excelUtil.replaceVal(6, 6, "Fecha " + TypesUtil.getStringDate(new Date(), "dd/MM/yyyy H:mm:ss"));

        //datos
        irow = 9;
        int column = 0;
        for (CursoDirigidoDTO cursoDirigido : cursoDirigidos) {
            column = 0;
            excelUtil.replaceStyle(irow, column++, estiloGeneral);
            excelUtil.replaceStyle(irow, column++, estiloGeneral);
            excelUtil.replaceStyle(irow, column++, estiloGeneral);
            excelUtil.replaceStyle(irow, column++, estiloGeneral);
            excelUtil.replaceStyle(irow, column++, estiloGeneral);
            excelUtil.replaceStyle(irow, column++, estiloGeneral);
            excelUtil.replaceStyle(irow, column++, estiloGeneral);
            excelUtil.replaceStyle(irow, column++, estiloGeneral);
            excelUtil.replaceStyle(irow, column++, estiloGeneral);
            excelUtil.replaceStyle(irow, column++, estiloGeneral);
            excelUtil.replaceStyle(irow, column++, estiloGeneral);
            excelUtil.replaceStyle(irow, column, estiloGeneral);
            column = 0;
            excelUtil.replaceVal(irow, column++, cursoDirigido.getCodCurso());
            excelUtil.replaceVal(irow, column++, cursoDirigido.getNomCurso());
            excelUtil.replaceVal(irow, column++, cursoDirigido.getSeccion());
            excelUtil.replaceVal(irow, column++, cursoDirigido.getTipo());
            excelUtil.replaceVal(irow, column++, cursoDirigido.getGrupo());
            excelUtil.replaceVal(irow, column++, cursoDirigido.getCarga());
            excelUtil.replaceVal(irow, column++, cursoDirigido.getAula());
            excelUtil.replaceVal(irow, column++, cursoDirigido.getMatriculados());
            excelUtil.replaceVal(irow, column++, cursoDirigido.getCodDocente());
            excelUtil.replaceVal(irow, column++, cursoDirigido.getNomDocente());
            excelUtil.replaceVal(irow, column++, cursoDirigido.getDepartamento());
            excelUtil.replaceVal(irow, column, cursoDirigido.getFacultad());
            irow ++;
        }
        sheet.setForceFormulaRecalculation(true);

    }

}

