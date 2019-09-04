package pe.edu.lamolina.pivot.controller.reporte.view;

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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.view.AbstractView;
import pe.albatross.zelpers.file.excel.ExcelHelper;
import pe.albatross.zelpers.file.excel.ExcelStyles;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Curso;
import pe.edu.lamolina.model.academico.Docente;
import pe.edu.lamolina.model.academico.GrupoSeccion;
import pe.edu.lamolina.model.academico.MatriculaSeccion;
import pe.edu.lamolina.model.academico.Seccion;
import pe.edu.lamolina.pivot.controller.docente.notasacademicas.NotaAcademicaService;
import pe.edu.lamolina.pivot.zelper.constant.Constantine;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

@Component
public class ReporteAlumnosExcel extends AbstractView {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private static final String CONTENT_TYPE_XLSX = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
    private static final String CONTENT_TYPE_XLS = "application/vnd.ms-excel";

    @Autowired
    NotaAcademicaService cargaAcademicaService;

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
        ds.setFechaAccionAudit(new Date());
        Seccion seccion = (Seccion) model.get("seccion");

        List<MatriculaSeccion> matriculasSeccionByFilter = cargaAcademicaService.allMatriculaSeccionBySeccion(seccion);

        this.generateSheet(wb, model, matriculasSeccionByFilter, ds);
        String fecha = new DateTime().toString("yyyMMdd_Hmm");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + "Consejeros_por_especialidad" + fecha + ".xlsx\"");
        response.setContentType(getContentType());

        ServletOutputStream out = response.getOutputStream();
        out.flush();
        wb.write(out);
        out.flush();
    }

    private void generateSheet(Workbook wb, Map<String, Object> model, List<MatriculaSeccion> matriculasSecciones, DataSessionPivot ds) {
        Sheet sheet = wb.createSheet("Hoja1");
        this.createBody(wb, sheet, model, matriculasSecciones, ds);
    }

    private void createBody(Workbook wb, Sheet sheet, Map<String, Object> model, List<MatriculaSeccion> matriculasSecciones, DataSessionPivot ds) {
        Seccion seccion = (Seccion) model.get("seccion");
        GrupoSeccion grupoSeccion = (GrupoSeccion) model.get("grupoSeccion");
        CicloAcademico cicloAcademico = (CicloAcademico) model.get("cicloAcademico");
        Docente docente = (Docente) model.get("docente");
        if (docente == null) {
            docente = seccion.getDocentePrincipal();
        }
        Curso curso = (Curso) model.get("curso");

        ExcelHelper excelUtil = new ExcelHelper(sheet, wb);

        CellStyle headerCell = ExcelStyles.getStyleCellHeaderGrey(wb);
        CellStyle estiloNumero = getStyleNumero(wb);
        CellStyle estiloGeneral = getStyleGeneral(wb);
        CellStyle titulo3Left = ExcelStyles.getStyleBody(wb);
        titulo3Left.setAlignment(CellStyle.ALIGN_LEFT);

        int irow = 1;
        String letterToMerge = ":H";
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
        cell.setCellValue("LISTA DE ALUMNOS " + cicloAcademico.getDescripcion());
        cell.setCellStyle(ExcelStyles.getCellTitle2Green(wb));
        irow++;

        region = CellRangeAddress.valueOf("A" + irow + letterToMerge + irow);
        sheet.addMergedRegion(region);
        row = sheet.createRow(region.getFirstRow());
        cell = row.createCell(region.getFirstColumn());
        cell.setCellValue("CURSO: " + curso.getCodigo() + " " + curso.getNombre().toUpperCase());
        cell.setCellStyle(titulo3Left);
        irow++;

        region = CellRangeAddress.valueOf("A" + irow + letterToMerge + irow);
        sheet.addMergedRegion(region);
        row = sheet.createRow(region.getFirstRow());
        cell = row.createCell(region.getFirstColumn());
        cell.setCellValue("DOCENTE: " + docente.getPersona().getApellidosNombres());
        cell.setCellStyle(titulo3Left);
        irow++;

        region = CellRangeAddress.valueOf("A" + irow + letterToMerge + irow);
        sheet.addMergedRegion(region);
        row = sheet.createRow(region.getFirstRow());
        cell = row.createCell(region.getFirstColumn());
        cell.setCellValue("CLAVE: " + seccion.getCodigo2());
        cell.setCellStyle(titulo3Left);
        irow++;

        if (seccion.getAula() != null) {
            region = CellRangeAddress.valueOf("A" + irow + letterToMerge + irow);
            sheet.addMergedRegion(region);
            row = sheet.createRow(region.getFirstRow());
            cell = row.createCell(region.getFirstColumn());
            cell.setCellValue("AULA: " + seccion.getAula().getCodigo());
            cell.setCellStyle(titulo3Left);
            irow++;
        }
        region = CellRangeAddress.valueOf("A" + irow + letterToMerge + irow);
        sheet.addMergedRegion(region);
        row = sheet.createRow(region.getFirstRow());
        cell = row.createCell(region.getFirstColumn());
        cell.setCellValue("GRUPO: " + seccion.getGrupoHoras().getCodigo());
        cell.setCellStyle(titulo3Left);
        irow++;

        String ciclo = "Ciclo Académico " + ds.getCicloAcademico().getDescripcion();
        String fecha = TypesUtil.getStringDateTimeLongFormat(new Date());
        region = CellRangeAddress.valueOf("A" + irow + letterToMerge + irow);
        sheet.addMergedRegion(region);
        row = sheet.createRow(region.getFirstRow());
        cell = row.createCell(region.getFirstColumn());
        cell.setCellValue(ciclo + " - " + fecha);
        cell.setCellStyle(ExcelStyles.getStyleBody(wb));
        cell.getCellStyle().setAlignment(CellStyle.ALIGN_RIGHT);
        irow++;

        int column = 0;
        excelUtil.replaceVal(irow, column++, "N", headerCell);
        sheet.setColumnWidth((column - 1), 5 * 256);
        excelUtil.replaceVal(irow, column++, "MATRICULA", headerCell);
        sheet.setColumnWidth((column - 1), 20 * 256);
        excelUtil.replaceVal(irow, column++, "ALUMNO", headerCell);
        sheet.setColumnWidth((column - 1), 50 * 256);
        excelUtil.replaceVal(irow, column++, "CORREO", headerCell);
        sheet.setColumnWidth((column - 1), 30 * 256);
        excelUtil.replaceVal(irow, column++, "PRIORIDAD", headerCell);
        sheet.setColumnWidth((column - 1), 20 * 256);
        excelUtil.replaceVal(irow, column++, "ESPECIALIDAD", headerCell);
        sheet.setColumnWidth((column - 1), 50 * 256);
        excelUtil.replaceVal(irow, column++, "FAC", headerCell);
        sheet.setColumnWidth((column - 1), 5 * 256);
        excelUtil.replaceVal(irow, column++, "ESP", headerCell);
        sheet.setColumnWidth((column - 1), 5 * 256);
        irow++;

        int num = 1;
        for (MatriculaSeccion matriculasSeccion : matriculasSecciones) {
            column = 0;
            Alumno alumno = matriculasSeccion.getMatriculaResumen().getAlumno();
            excelUtil.replaceVal(irow, column++, num++, estiloNumero);
            excelUtil.replaceVal(irow, column++, alumno.getCodigo(), estiloGeneral);
            excelUtil.replaceVal(irow, column++, alumno.getPersona().getApellidosNombres(), estiloGeneral);
            excelUtil.replaceVal(irow, column++, alumno.getPersona().getEmailCompania(), estiloGeneral);
            String prioridad = "";
            if (matriculasSeccion.getMatriculaResumen().getPrioridad() != null) {
                prioridad = matriculasSeccion.getMatriculaResumen().getPrioridad().setScale(2, BigDecimal.ROUND_HALF_UP).toString();
            }
            excelUtil.replaceVal(irow, column++, prioridad, estiloNumero);
            excelUtil.replaceVal(irow, column++, alumno.getCarrera().getNombre(), estiloGeneral);
            excelUtil.replaceVal(irow, column++, alumno.getCarrera().getFacultad().getCodigo(), estiloGeneral);
            excelUtil.replaceVal(irow, column++, alumno.getCarrera().getCodigo(), estiloGeneral);
            irow++;
        }

    }

    private CellStyle getStyleNumero(Workbook workBook) {
        CellStyle cell = workBook.createCellStyle();
        cell.setAlignment(CellStyle.ALIGN_RIGHT);
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

}
