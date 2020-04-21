package pe.edu.lamolina.pivot.controller.academico.curso.view;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
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
import pe.albatross.zelpers.miscelanea.ObjectUtil;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.Carrera;
import pe.edu.lamolina.model.academico.Curso;
import pe.edu.lamolina.model.academico.Docente;
import pe.edu.lamolina.model.academico.DocenteSeccion;
import pe.edu.lamolina.model.academico.MatriculaSeccion;
import pe.edu.lamolina.model.academico.Seccion;
import pe.edu.lamolina.model.constantines.AcademicoConstantine;
import pe.edu.lamolina.model.constantines.GlobalConstantine;
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
        DataSessionPivot ds = (DataSessionPivot) request.getSession().getAttribute(GlobalConstantine.SESSION_USUARIO);
        List<MatriculaSeccion> matriculasSecciones = (List<MatriculaSeccion>) model.get("alumnosPorCurso");
        List<DocenteSeccion> docenteSecciones = (List<DocenteSeccion>) model.get("docenteSecciones");
        Map<Long, Docente> mapDocenteXseccion = TypesUtil.convertListToMap("seccion.id", "docente", docenteSecciones);
        this.generateSheet(wb, matriculasSecciones, ds, mapDocenteXseccion);
        String fecha = new DateTime().toString("yyyMMdd_Hmm");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + "Alumnos_Curso" + fecha + ".xlsx\"");
        response.setContentType(getContentType());

        ServletOutputStream out = response.getOutputStream();
        out.flush();
        wb.write(out);
        out.flush();
    }

    private void generateSheet(Workbook wb, List<MatriculaSeccion> matriculasSecciones, DataSessionPivot ds, Map<Long, Docente> mapDocenteXseccion) {
        Sheet sheet = wb.createSheet("Hoja1");
        this.createBody(wb, sheet, matriculasSecciones, ds, mapDocenteXseccion);
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

    private void createBody(Workbook wb, Sheet sheet, List<MatriculaSeccion> matriculasSecciones, DataSessionPivot ds, Map<Long, Docente> mapDocenteXseccion) {
        ExcelHelper excelUtil = new ExcelHelper(sheet, wb);

        //  CellStyle headerCell = getStyleCellHeader(wb);
        CellStyle headerCell = ExcelStyles.getStyleCellHeaderGrey(wb);
        CellStyle estiloNumero = getStyleNumero(wb);
        CellStyle estiloGeneral = getStyleGeneral(wb);

        int irow = 1;
        CellRangeAddress region = CellRangeAddress.valueOf("A" + irow + ":M" + irow);
        sheet.addMergedRegion(region);
        Row row = sheet.createRow(region.getFirstRow());
        Cell cell = row.createCell(region.getFirstColumn());
        cell.setCellValue("UNIVERSIDAD NACIONAL AGRARIA LA MOLINA");
        cell.setCellStyle(ExcelStyles.getCellTitle1Green(wb));
        irow++;

        Curso cursoBase = null;
        if (!matriculasSecciones.isEmpty()) {
            cursoBase = matriculasSecciones.get(0).getSeccion().getGrupoSeccion().getCurso();
        }
        region = CellRangeAddress.valueOf("A" + irow + ":M" + irow);
        sheet.addMergedRegion(region);
        row = sheet.createRow(region.getFirstRow());
        cell = row.createCell(region.getFirstColumn());
        cell.setCellValue("CURSO " + (cursoBase == null ? "SIN DATOS" : cursoBase.getNombre().toUpperCase() + " " + cursoBase.getCodigo()));
        cell.setCellStyle(ExcelStyles.getCellTitle2Green(wb));
        irow++;

        region = CellRangeAddress.valueOf("A" + irow + ":M" + irow);
        sheet.addMergedRegion(region);
        row = sheet.createRow(region.getFirstRow());
        cell = row.createCell(region.getFirstColumn());
        cell.setCellValue("LISTA DE ALUMNOS MATRICULADOS");
        cell.setCellStyle(ExcelStyles.getCellTitle3Green(wb));
        irow++;

        String ciclo = "Ciclo Académico " + ds.getCicloAcademico().getDescripcion();
        String fecha = TypesUtil.getStringDate(new Date(), "dd/MM/yyyy H:mm:ss");
        region = CellRangeAddress.valueOf("A" + irow + ":M" + irow);
        sheet.addMergedRegion(region);
        row = sheet.createRow(region.getFirstRow());
        cell = row.createCell(region.getFirstColumn());
        cell.setCellValue(ciclo + " - " + fecha);
        cell.setCellStyle(ExcelStyles.getStyleBody(wb));
        cell.getCellStyle().setAlignment(CellStyle.ALIGN_RIGHT);
        irow++;

        int column = 0;
        excelUtil.replaceVal(irow, column++, "N", headerCell);
        excelUtil.replaceVal(irow, column++, "CURSO COD", headerCell);
        sheet.setColumnWidth((column - 1), tamanio(15));
        excelUtil.replaceVal(irow, column++, "CURSO", headerCell);
        sheet.setColumnWidth((column - 1), tamanio(20));
        excelUtil.replaceVal(irow, column++, "SECCIÓN", headerCell);
        sheet.setColumnWidth((column - 1), tamanio(15));
        excelUtil.replaceVal(irow, column++, "GRUPO", headerCell);
        sheet.setColumnWidth((column - 1), tamanio(15));
        excelUtil.replaceVal(irow, column++, "DOCENTE", headerCell);
        sheet.setColumnWidth((column - 1), tamanio(15));
        excelUtil.replaceVal(irow, column++, "MATRICULA", headerCell);
        sheet.setColumnWidth((column - 1), tamanio(15));
        excelUtil.replaceVal(irow, column++, "ALUMNO", headerCell);
        sheet.setColumnWidth((column - 1), tamanio(30));
        excelUtil.replaceVal(irow, column++, "CORREO", headerCell);
        sheet.setColumnWidth((column - 1), tamanio(30));
        excelUtil.replaceVal(irow, column++, "PRIORIDAD", headerCell);
        sheet.setColumnWidth((column - 1), tamanio(15));
        excelUtil.replaceVal(irow, column++, "ESPECIALIDAD", headerCell);
        sheet.setColumnWidth((column - 1), tamanio(30));
        excelUtil.replaceVal(irow, column++, "FAC", headerCell);
        sheet.setColumnWidth((column - 1), tamanio(10));
        excelUtil.replaceVal(irow, column++, "ESP", headerCell);
        sheet.setColumnWidth((column - 1), tamanio(10));

        irow++;
        int num = 1;
        for (MatriculaSeccion matriculaSeccion : matriculasSecciones) {
            Alumno alumno = matriculaSeccion.getMatriculaResumen().getAlumno();
            Carrera carrera = alumno.getCarrera();
            Seccion seccion = matriculaSeccion.getSeccion();
            Docente docente = mapDocenteXseccion.get(seccion.getId());
            String docStr = (String) ObjectUtil.getParentTree(docente, "persona.nombreCompleto");
            docStr = AcademicoConstantine.DOCENTE_INDETERMINADO.equalsIgnoreCase(docente.getCodigo()) ? "Docente Indeterminado" : docStr ;
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
            excelUtil.replaceVal(irow, column++, (String) ObjectUtil.getParentTree(seccion, "grupoHoras.codigo"), estiloGeneral);
            excelUtil.replaceVal(irow, column++, docStr , estiloGeneral);
            excelUtil.replaceVal(irow, column++, alumno.getCodigo(), estiloGeneral);
            excelUtil.replaceVal(irow, column++, alumno.getPersona().getApellidosNombres(), estiloGeneral);
            excelUtil.replaceVal(irow, column++, alumno.getPersona().getEmailCompania(), estiloGeneral);
            excelUtil.replaceVal(irow, column++, prioridad == null ? "" : prioridad.toString(), estiloGeneral);
            excelUtil.replaceVal(irow, column++, carrera.getNombre(), estiloGeneral);
            excelUtil.replaceVal(irow, column++, carrera.getFacultad().getCodigo(), estiloGeneral);
            excelUtil.replaceVal(irow, column++, carrera.getCodigo(), estiloGeneral);
            irow++;

        }
    }

    public int tamanio(int width) {
        return width * 256;
    }

}
