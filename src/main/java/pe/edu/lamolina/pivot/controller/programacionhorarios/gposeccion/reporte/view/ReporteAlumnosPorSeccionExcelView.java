package pe.edu.lamolina.pivot.controller.programacionhorarios.gposeccion.reporte.view;

import java.io.InputStream;
import java.util.Date;
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
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.Curso;
import pe.edu.lamolina.model.academico.GrupoSeccion;
import pe.edu.lamolina.model.academico.MatriculaSeccion;
import pe.edu.lamolina.model.academico.Seccion;
import pe.edu.lamolina.pivot.controller.programacionhorarios.gposeccion.aula.SeccionDTO;

@Component
public class ReporteAlumnosPorSeccionExcelView extends AbstractView {

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

        List<MatriculaSeccion> matriculados = (List<MatriculaSeccion>) model.get("matriculasSecciones");
        SeccionDTO seccionDTO = (SeccionDTO) model.get("seccionDTO");

        this.generateSheet(wb, matriculados, seccionDTO);
        String fecha = new DateTime().toString("yyyMMdd_Hmm");
        String titulo = seccionDTO.getTituloReporte().replace(" ", "_");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + titulo + fecha + ".xlsx\"");
        response.setContentType(getContentType());

        ServletOutputStream out = response.getOutputStream();
        out.flush();
        wb.write(out);
        out.flush();
    }

    private void generateSheet(Workbook wb, List<MatriculaSeccion> matriculados, SeccionDTO seccionDTO) {
        Sheet sheet = wb.getSheet("Hoja1");
        // Sheet sheet = wb.createSheet("Hoja1");
        sheet.setAutobreaks(true);
        this.createBody(wb, sheet, seccionDTO, matriculados);
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

    private void createBody(Workbook wb, Sheet sheet, SeccionDTO seccionDTO, List<MatriculaSeccion> matriculados) {
        ExcelHelper excelUtil = new ExcelHelper(sheet, wb);

        CellStyle estiloCabecera = getStyleCabecera(wb);
        CellStyle estiloCabeceraNombre = getStyleCabecera(wb);
        estiloCabeceraNombre.setAlignment(CellStyle.ALIGN_LEFT);
        CellStyle estiloCodigo = getStyleNumero(wb);
        CellStyle estiloNumero = getStyleNumero(wb);
        CellStyle estiloGeneral = getStyleGeneral(wb);

        int irow = 7;
        excelUtil.replaceVal(2, 1, seccionDTO.getTituloReporte());
        excelUtil.replaceVal(3, 1, "Ciclo Académico " + seccionDTO.getCicloAcademico().getDescripcion());
        excelUtil.replaceVal(4, 2, "Fecha " + TypesUtil.getStringDate(new Date(), "dd/MM/yyyy H:mm:ss"));

        int column = 0;
        excelUtil.replaceVal(irow - 1, column++, "ANEXO SUPERIOR", estiloCabecera);
        sheet.setColumnWidth((column - 1), this.tamaño(25));
//        excelUtil.replaceVal(irow - 1, column++, "ANEXO", estiloCabecera);
//        sheet.setColumnWidth((column - 1), this.tamaño(25));
        excelUtil.replaceVal(irow - 1, column++, "DEPARTAMENTO CURSO", estiloCabecera);
        sheet.setColumnWidth((column - 1), this.tamaño(25));
        excelUtil.replaceVal(irow - 1, column++, "CURSO COD", estiloCabecera);
        sheet.setColumnWidth((column - 1), this.tamaño(15));
        excelUtil.replaceVal(irow - 1, column++, "CURSO", estiloCabecera);
        sheet.setColumnWidth((column - 1), this.tamaño(25));
        excelUtil.replaceVal(irow - 1, column++, "SECCIÓN", estiloCabecera);
        sheet.setColumnWidth((column - 1), this.tamaño(15));
        excelUtil.replaceVal(irow - 1, column++, "MATRICULA", estiloCabecera);
        sheet.setColumnWidth((column - 1), this.tamaño(15));
        excelUtil.replaceVal(irow - 1, column++, "ALUMNO", estiloCabecera);
        sheet.setColumnWidth((column - 1), this.tamaño(40));
        excelUtil.replaceVal(irow - 1, column++, "CARRERA ALUMNO", estiloCabecera);
        sheet.setColumnWidth((column - 1), this.tamaño(40));
        //datos
        int num = 1;
        for (MatriculaSeccion matriculado : matriculados) {
            column = 0;
            Seccion seccion = matriculado.getSeccion();
            GrupoSeccion grupoSeccion = seccion.getGrupoSeccion();
            Curso curso = grupoSeccion.getCurso();
            Alumno alumno = matriculado.getMatriculaResumen().getAlumno();
            excelUtil.replaceVal(irow, column++, grupoSeccion.getAnexoBoletin().getAnexoSuperior().getNombre(), estiloGeneral);
//            excelUtil.replaceVal(irow, column++, grupoSeccion.getAnexoBoletin().getNombre(), estiloGeneral);
            excelUtil.replaceVal(irow, column++, curso.getDepartamentoAcademico().getNombre(), estiloGeneral);
            excelUtil.replaceVal(irow, column++, curso.getCodigo(), estiloGeneral);
            excelUtil.replaceVal(irow, column++, curso.getNombre(), estiloGeneral);
            excelUtil.replaceVal(irow, column++, seccion.getCodigo2(), estiloGeneral);
            excelUtil.replaceVal(irow, column++, alumno.getCodigo(), estiloGeneral);
            excelUtil.replaceVal(irow, column++, alumno.getPersona().getApellidosNombres(), estiloGeneral);
            excelUtil.replaceVal(irow, column++, alumno.getCarrera().getNombre(), estiloGeneral);
            irow++;
        }

    }

    public int tamaño(int caracteres) {
        return caracteres * 256;
    }

}
