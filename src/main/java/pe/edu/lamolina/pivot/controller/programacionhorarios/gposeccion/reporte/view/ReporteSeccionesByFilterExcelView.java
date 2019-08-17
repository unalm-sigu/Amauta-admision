package pe.edu.lamolina.pivot.controller.programacionhorarios.gposeccion.reporte.view;

import java.io.InputStream;
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
import pe.edu.lamolina.model.academico.Curso;
import pe.edu.lamolina.model.academico.GrupoSeccion;
import pe.edu.lamolina.model.academico.Seccion;
import pe.edu.lamolina.model.general.Aula;

@Component
public class ReporteSeccionesByFilterExcelView extends AbstractView {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private static final String CONTENT_TYPE_XLSX = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
    private static final String CONTENT_TYPE_XLS = "application/vnd.ms-excel";

    @Override
    protected void renderMergedOutputModel(Map<String, Object> map, HttpServletRequest request, HttpServletResponse response) throws Exception {

        InputStream formato = (InputStream) map.get("formato");
//
        Workbook workbook = new XSSFWorkbook();
        if (workbook instanceof XSSFWorkbook) {
            setContentType(CONTENT_TYPE_XLSX);
        } else {
            setContentType(CONTENT_TYPE_XLS);
        }

        this.buildExcelDocument(map, workbook, request, response);

    }

    protected void buildExcelDocument(Map<String, Object> model, Workbook wb, HttpServletRequest request, HttpServletResponse response) throws Exception {

        List<Seccion> seccionesConCruce = (List<Seccion>) model.get("secciones");
        CicloAcademico ciclo = (CicloAcademico) model.get("ciclo");

        this.generateSheet(wb, seccionesConCruce, ciclo);
        String fecha = new DateTime().toString("yyyMMdd_Hmm");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + "Secciones_sin_aula_" + fecha + ".xlsx\"");
        response.setContentType(getContentType());

        ServletOutputStream out = response.getOutputStream();
        out.flush();
        wb.write(out);
        out.flush();
    }

    private void generateSheet(Workbook wb, List<Seccion> secciones, CicloAcademico ciclo) {
        //  Sheet sheet = wb.getSheet("Hoja1");
        Sheet sheet = wb.createSheet("Hoja1");
        sheet.setAutobreaks(true);
        this.createBody(wb, sheet, secciones, ciclo);
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

    private void createBody(Workbook wb, Sheet sheet, List<Seccion> seccionesConCruce, CicloAcademico ciclo) {
        ExcelHelper excelUtil = new ExcelHelper(sheet, wb);

        CellStyle estiloCabecera = getStyleCabecera(wb);
        CellStyle estiloCabeceraNombre = getStyleCabecera(wb);
        estiloCabeceraNombre.setAlignment(CellStyle.ALIGN_LEFT);
        CellStyle estiloCodigo = getStyleNumero(wb);
        CellStyle estiloNumero = getStyleNumero(wb);
        CellStyle estiloGeneral = getStyleGeneral(wb);

        int irow = 7;
        excelUtil.replaceVal(2, 6, "Secciones sin Aula ");
        excelUtil.replaceVal(3, 6, "Ciclo Académico " + ciclo.getDescripcion());
        excelUtil.replaceVal(4, 6, "Fecha " + TypesUtil.getStringDate(new Date(), "dd/MM/yyyy H:mm:ss"));

//        excelUtil.replaceStyle(irow - 1, 0, estiloCabecera);
//        excelUtil.replaceStyle(irow - 1, 1, estiloCabecera);
//        excelUtil.replaceStyle(irow - 1, 2, estiloCabecera);
//        excelUtil.replaceStyle(irow - 1, 3, estiloCabecera);
//        excelUtil.replaceStyle(irow - 1, 4, estiloCabecera);
//        excelUtil.replaceStyle(irow - 1, 5, estiloCabecera);
//        excelUtil.replaceStyle(irow - 1, 6, estiloCabecera);
//        excelUtil.replaceStyle(irow - 1, 7, estiloCabecera);
//        excelUtil.replaceStyle(irow - 1, 8, estiloCabecera);
//        excelUtil.replaceStyle(irow - 1, 9, estiloCabecera);
//        excelUtil.replaceStyle(irow - 1, 10, estiloCabecera);
        int column = 0;
        excelUtil.replaceVal(irow - 1, column++, "ANEXO SUPERIOR", estiloCabecera);
        excelUtil.replaceVal(irow - 1, column++, "ANEXO", estiloCabecera);
        excelUtil.replaceVal(irow - 1, column++, "CURSO", estiloCabecera);
        excelUtil.replaceVal(irow - 1, column++, "SECCIÓN", estiloCabecera);
        excelUtil.replaceVal(irow - 1, column++, "TIPO SECCIÓN", estiloCabecera);
        excelUtil.replaceVal(irow - 1, column++, "HORARIO", estiloCabecera);
        excelUtil.replaceVal(irow - 1, column++, "VACANTES", estiloCabecera);
        excelUtil.replaceVal(irow - 1, column++, "MATRICULADOS", estiloCabecera);
        excelUtil.replaceVal(irow - 1, column++, "AULA", estiloCabecera);
        excelUtil.replaceVal(irow - 1, column++, "ESTADO", estiloCabecera);
        //datos
        int num = 1;
        for (Seccion seccion : seccionesConCruce) {
//            column = 0;
//            excelUtil.replaceStyle(irow, column++, estiloGeneral);
//            excelUtil.replaceStyle(irow, column++, estiloGeneral);
//            excelUtil.replaceStyle(irow, column++, estiloGeneral);
//            excelUtil.replaceStyle(irow, column++, estiloGeneral);
//            excelUtil.replaceStyle(irow, column++, estiloGeneral);
//            excelUtil.replaceStyle(irow, column++, estiloGeneral);
//            excelUtil.replaceStyle(irow, column++, estiloGeneral);
//            excelUtil.replaceStyle(irow, column++, estiloGeneral);
//            excelUtil.replaceStyle(irow, column++, estiloGeneral);
            column = 0;

            GrupoSeccion grupoSeccion = seccion.getGrupoSeccion();
            Curso curso = grupoSeccion.getCurso();
            excelUtil.replaceVal(irow, column++, grupoSeccion.getAnexoBoletin().getAnexoSuperior().getNombre());
            excelUtil.replaceVal(irow, column++, grupoSeccion.getAnexoBoletin().getNombre());
            excelUtil.replaceVal(irow, column++, curso.getNombre());
            excelUtil.replaceVal(irow, column++, seccion.getCodigo2());
            excelUtil.replaceVal(irow, column++, seccion.getTipoSeccion());
            excelUtil.replaceVal(irow, column++, seccion.getGrupoHoras() != null ? seccion.getGrupoHoras().getCodigo() : "");
            excelUtil.replaceVal(irow, column++, seccion.getVacantes(), estiloNumero);
            excelUtil.replaceVal(irow, column++, seccion.getMatriculados(), estiloNumero);
            Aula aula = seccion.getAula();
            aula = aula == null ? new Aula() : aula;
            excelUtil.replaceVal(irow, column++, aula.getCodigo());
            excelUtil.replaceVal(irow, column++, seccion.getEstado());
            Cell cell = excelUtil.findCell(irow, irow);

            irow++;

//            for (int i = 0; i < column; i++) {
//                sheet.autoSizeColumn(i);
//            }
        }
        sheet.autoSizeColumn(2);
        sheet.autoSizeColumn(column);
//        sheet.setColumnWidth(0, 100);
//        sheet.setColumnWidth(1, 100);
//        sheet.setColumnWidth(1, 100);
        //  sheet.autoSizeColumn(1);
        // sheet.setForceFormulaRecalculation(true);

    }

}
