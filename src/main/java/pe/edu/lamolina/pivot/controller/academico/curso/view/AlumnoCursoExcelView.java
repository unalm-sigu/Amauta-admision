package pe.edu.lamolina.pivot.controller.academico.curso.view;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.PatternFormatting;
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
import pe.edu.lamolina.model.academico.Carrera;
import pe.edu.lamolina.model.academico.Curso;
import pe.edu.lamolina.model.academico.MatriculaSeccion;
import pe.edu.lamolina.model.academico.Seccion;
import pe.edu.lamolina.model.consejeria.Consejero;
import pe.edu.lamolina.pivot.zelper.constant.Constantine;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

@Component
public class AlumnoCursoExcelView extends AbstractView {

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
        List<MatriculaSeccion> matriculasSecciones = (List<MatriculaSeccion>) model.get("alumnosPorCurso");
        this.generateSheet(wb, matriculasSecciones, ds);
        String fecha = new DateTime().toString("yyyMMdd_Hmm");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + "Alumnos_Curso" + fecha + ".xlsx\"");
        response.setContentType(getContentType());

        ServletOutputStream out = response.getOutputStream();
        out.flush();
        wb.write(out);
        out.flush();
    }

    private void generateSheet(Workbook wb, List<MatriculaSeccion> matriculasSecciones, DataSessionPivot ds) {
        Sheet sheet = wb.createSheet("Hoja1");
        this.createBody(wb, sheet, matriculasSecciones, ds);
    }

    private CellStyle getStyleCabecera(Workbook workBook) {

        Font font = workBook.createFont();
        font.setFontName("Arial");
        font.setBoldweight(Font.BOLDWEIGHT_BOLD);
        font.setColor(HSSFColor.WHITE.index);

        CellStyle cell = workBook.createCellStyle();
        cell.setAlignment(CellStyle.ALIGN_CENTER);
        cell.setFont(font);
        cell.setBorderTop(HSSFCellStyle.BORDER_THIN);
        cell.setBorderBottom(HSSFCellStyle.BORDER_THIN);
        cell.setBorderRight(HSSFCellStyle.BORDER_THIN);
        cell.setBorderLeft(HSSFCellStyle.BORDER_THIN);
        
        
        
        cell.setFillForegroundColor(IndexedColors.GREY_50_PERCENT.getIndex());
        cell.setFillPattern(PatternFormatting.SOLID_FOREGROUND);
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

    private void createBody(Workbook wb, Sheet sheet, List<MatriculaSeccion> matriculasSecciones, DataSessionPivot ds) {
        ExcelHelper excelUtil = new ExcelHelper(sheet, wb);

        CellStyle estiloCabecera = getStyleCabecera(wb);
        CellStyle estiloCabeceraNombre = getStyleCabecera(wb);
        estiloCabeceraNombre.setAlignment(CellStyle.ALIGN_LEFT);
        CellStyle estiloNumero = getStyleNumero(wb);
        CellStyle estiloGeneral = getStyleGeneral(wb);

        int irow = 7;

        excelUtil.replaceVal(3, 2, "Ciclo Académico " + ds.getCicloAcademico().getDescripcion());
        excelUtil.replaceVal(4, 2, "Fecha " + TypesUtil.getStringDate(new Date(), "dd/MM/yyyy H:mm:ss"));
//ponderado, estado academico
        int column = 0;
        excelUtil.replaceVal(irow - 1, column++, "N", estiloCabecera);
        excelUtil.replaceVal(irow - 1, column++, "CURSO COD", estiloCabecera);
        sheet.setColumnWidth((column - 1), tamanio(15));
        excelUtil.replaceVal(irow - 1, column++, "CURSO", estiloCabecera);
        sheet.setColumnWidth((column - 1), tamanio(20));
        excelUtil.replaceVal(irow - 1, column++, "SECCIÓN", estiloCabecera);
        sheet.setColumnWidth((column - 1), tamanio(15));
        excelUtil.replaceVal(irow - 1, column++, "MATRICULA", estiloCabecera);
        sheet.setColumnWidth((column - 1), tamanio(15));
        excelUtil.replaceVal(irow - 1, column++, "ALUMNO", estiloCabecera);
        sheet.setColumnWidth((column - 1), tamanio(30));
        excelUtil.replaceVal(irow - 1, column++, "CORREO", estiloCabecera);
        sheet.setColumnWidth((column - 1), tamanio(30));
        excelUtil.replaceVal(irow - 1, column++, "PRIORIDAD", estiloCabecera);
        sheet.setColumnWidth((column - 1), tamanio(15));
        excelUtil.replaceVal(irow - 1, column++, "ESPECIALIDAD", estiloCabecera);
        sheet.setColumnWidth((column - 1), tamanio(30));
        excelUtil.replaceVal(irow - 1, column++, "FAC", estiloCabecera);
        sheet.setColumnWidth((column - 1), tamanio(10));
        excelUtil.replaceVal(irow - 1, column++, "ESP", estiloCabecera);
        sheet.setColumnWidth((column - 1), tamanio(10));

//        for (int i = 1; i < column; i++) {
//            sheet.setColumnWidth(i, 20 * 256);
//        }
        //datos
        int num = 1;
        for (MatriculaSeccion matriculaSeccion : matriculasSecciones) {
            Alumno alumno = matriculaSeccion.getMatriculaResumen().getAlumno();
            Carrera carrera = alumno.getCarrera();
            Seccion seccion = matriculaSeccion.getSeccion();
            Curso curso = seccion.getGrupoSeccion().getCurso();
            column = 0;
            BigDecimal prioridad = null;
            if (matriculaSeccion.getMatriculaResumen().getPrioridad() != null) {
                prioridad = matriculaSeccion.getMatriculaResumen().getPrioridad().setScale(2, BigDecimal.ROUND_HALF_UP);
            }
            excelUtil.replaceVal(irow, column++, num++, estiloNumero);
            excelUtil.replaceVal(irow, column++, curso.getCodigo(), estiloGeneral);
            excelUtil.replaceVal(irow, column++, curso.getNombre(), estiloGeneral);
            excelUtil.replaceVal(irow, column++, seccion.getCodigo2(), estiloGeneral);
            excelUtil.replaceVal(irow, column++, alumno.getCodigo(), estiloGeneral);
            excelUtil.replaceVal(irow, column++, alumno.getPersona().getApellidosNombres(), estiloGeneral);
            excelUtil.replaceVal(irow, column++, alumno.getPersona().getEmailCompania(), estiloGeneral);
            excelUtil.replaceVal(irow, column++, prioridad == null ? "" : prioridad.toString(), estiloGeneral);
            excelUtil.replaceVal(irow, column++, carrera.getNombre(), estiloGeneral);
            excelUtil.replaceVal(irow, column++, carrera.getFacultad().getCodigo(), estiloGeneral);
            excelUtil.replaceVal(irow, column++, carrera.getCodigo(), estiloGeneral);
            irow++;

        }

        sheet.setForceFormulaRecalculation(true);

    }

    public int tamanio(int width) {
        return width * 256;
    }

}
