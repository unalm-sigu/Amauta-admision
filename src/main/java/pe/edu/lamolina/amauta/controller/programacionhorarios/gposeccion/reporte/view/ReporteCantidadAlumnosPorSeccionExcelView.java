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
import pe.edu.lamolina.amauta.controller.programacionhorarios.gposeccion.aula.SeccionDTO;
import pe.edu.lamolina.amauta.controller.programacionhorarios.gposeccion.reporte.dto.CantidadMatriculadosDTO;

@Component
public class ReporteCantidadAlumnosPorSeccionExcelView extends AbstractView {

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

        List<CantidadMatriculadosDTO> matriculados = (List<CantidadMatriculadosDTO>) model.get("cantidadMatriculados");
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

    private void generateSheet(Workbook wb, List<CantidadMatriculadosDTO> matriculados, SeccionDTO seccionDTO) {
        Sheet sheet = wb.getSheet("Hoja1");
        // Sheet sheet = wb.createSheet("Hoja1");
        sheet.setAutobreaks(true);
        this.createBody(wb, sheet, seccionDTO, matriculados);
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

    private void createBody(Workbook wb, Sheet sheet, SeccionDTO seccionDTO, List<CantidadMatriculadosDTO> matriculados) {
        ExcelHelper excelUtil = new ExcelHelper(sheet, wb);

        CellStyle estiloCabecera = getStyleCabecera(wb);
        CellStyle estiloCabeceraNombre = getStyleCabecera(wb);
        estiloCabeceraNombre.setAlignment(HorizontalAlignment.LEFT);

        CellStyle estiloNumero = getStyleNumero(wb);
        CellStyle estiloGeneral = getStyleGeneral(wb);

        int irow = 7;
        excelUtil.replaceVal(2, 1, seccionDTO.getTituloReporte());
        excelUtil.replaceVal(3, 1, "Ciclo Académico " + seccionDTO.getCicloAcademico().getDescripcion());
        excelUtil.replaceVal(4, 2, "Fecha " + TypesUtil.getStringDate(new Date(), "dd/MM/yyyy H:mm:ss"));

        int column = 0;
        excelUtil.replaceVal(irow - 1, column++, "CICLO", estiloCabecera);
        sheet.setColumnWidth((column - 1), this.tamagno(10));
        excelUtil.replaceVal(irow - 1, column++, "ANEXO SUPERIOR", estiloCabecera);
        sheet.setColumnWidth((column - 1), this.tamagno(25));
        excelUtil.replaceVal(irow - 1, column++, "ANEXO", estiloCabecera);
        sheet.setColumnWidth((column - 1), this.tamagno(35));
        excelUtil.replaceVal(irow - 1, column++, "DEPARTAMENTO CURSO", estiloCabecera);
        sheet.setColumnWidth((column - 1), this.tamagno(35));
        excelUtil.replaceVal(irow - 1, column++, "CÓDIGO CURSO", estiloCabecera);
        sheet.setColumnWidth((column - 1), this.tamagno(18));
        excelUtil.replaceVal(irow - 1, column++, "CURSO", estiloCabecera);
        sheet.setColumnWidth((column - 1), this.tamagno(50));
        excelUtil.replaceVal(irow - 1, column++, "SECCIÓN", estiloCabecera);
        sheet.setColumnWidth((column - 1), this.tamagno(20));
        excelUtil.replaceVal(irow - 1, column++, "CÓDIGO DOCENTE", estiloCabecera);
        sheet.setColumnWidth((column - 1), this.tamagno(18));
        excelUtil.replaceVal(irow - 1, column++, "DOCENTE", estiloCabecera);
        sheet.setColumnWidth((column - 1), this.tamagno(35));
        excelUtil.replaceVal(irow - 1, column++, "MATRICULADOS", estiloCabecera);
        sheet.setColumnWidth((column - 1), this.tamagno(20));

        for (CantidadMatriculadosDTO matriculado : matriculados) {
            column = 0;
            excelUtil.replaceVal(irow, column++, matriculado.getCiclo());
            excelUtil.replaceVal(irow, column++, matriculado.getAnexoSuperior(), estiloGeneral);
            excelUtil.replaceVal(irow, column++, matriculado.getAnexo(), estiloGeneral);
            excelUtil.replaceVal(irow, column++, matriculado.getDepartamentoCurso(), estiloGeneral);
            excelUtil.replaceVal(irow, column++, matriculado.getCodigoCurso(), estiloGeneral);
            excelUtil.replaceVal(irow, column++, matriculado.getNombreCurso(), estiloGeneral);
            excelUtil.replaceVal(irow, column++, matriculado.getNombreSeccion(), estiloGeneral);
            excelUtil.replaceVal(irow, column++, matriculado.getCodigoDocente(), estiloGeneral);
            excelUtil.replaceVal(irow, column++, matriculado.getNombreDocente(), estiloGeneral);
            excelUtil.replaceVal(irow, column++, matriculado.getCantidad(), estiloNumero);
            irow++;
        }

    }

    public int tamagno(int caracteres) {
        return caracteres * 256;
    }

}
