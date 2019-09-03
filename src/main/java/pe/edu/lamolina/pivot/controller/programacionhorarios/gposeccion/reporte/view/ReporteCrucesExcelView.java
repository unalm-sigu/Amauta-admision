package pe.edu.lamolina.pivot.controller.programacionhorarios.gposeccion.reporte.view;

import java.io.InputStream;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.view.AbstractView;
import pe.albatross.zelpers.file.excel.ExcelHelper;
import pe.albatross.zelpers.miscelanea.ObjectUtil;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Seccion;

@Component
public class ReporteCrucesExcelView extends AbstractView {

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

        List<Seccion> seccionesConCruce = (List<Seccion>) model.get("secciones");
        CicloAcademico ciclo = (CicloAcademico) model.get("ciclo");

        this.generateSheet(wb, seccionesConCruce, ciclo);
        String fecha = new DateTime().toString("yyyMMdd_Hmm");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + "Secciones_con_Cruce" + fecha + ".xlsx\"");
        response.setContentType(getContentType());

        ServletOutputStream out = response.getOutputStream();
        out.flush();
        wb.write(out);
        out.flush();
    }

    private void generateSheet(Workbook wb, List<Seccion> seccionesConCruce, CicloAcademico ciclo) {
        Sheet sheet = wb.getSheet("Hoja1");
        //  Sheet sheet = wb.createSheet("Hoja1");
        //sheet.setAutobreaks(true);
        this.createBody(wb, sheet, seccionesConCruce, ciclo);
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

        excelUtil.replaceVal(3, 6, "Ciclo Académico " + ciclo.getDescripcion());
        excelUtil.replaceVal(4, 6, "Fecha " + TypesUtil.getStringDate(new Date(), "dd/MM/yyyy H:mm:ss"));

        excelUtil.replaceStyle(irow - 1, 0, estiloCabecera);
        excelUtil.replaceStyle(irow - 1, 1, estiloCabecera);
        excelUtil.replaceStyle(irow - 1, 2, estiloCabecera);
        excelUtil.replaceStyle(irow - 1, 3, estiloCabecera);
        excelUtil.replaceStyle(irow - 1, 4, estiloCabecera);
        excelUtil.replaceStyle(irow - 1, 5, estiloCabecera);
        excelUtil.replaceStyle(irow - 1, 6, estiloCabecera);
        excelUtil.replaceStyle(irow - 1, 7, estiloCabecera);
        excelUtil.replaceStyle(irow - 1, 8, estiloCabecera);
        excelUtil.replaceStyle(irow - 1, 9, estiloCabecera);
        excelUtil.replaceStyle(irow - 1, 10, estiloCabecera);

        int column = 0;
        excelUtil.replaceVal(irow - 1, column++, "CURCOD");
        excelUtil.replaceVal(irow - 1, column++, "NOMBRE CURSO");
        excelUtil.replaceVal(irow - 1, column++, "SECCIÓN");
        excelUtil.replaceVal(irow - 1, column++, "VACANTES");
        excelUtil.replaceVal(irow - 1, column++, "MATRICULADOS");
        excelUtil.replaceVal(irow - 1, column++, "TIPO");
        excelUtil.replaceVal(irow - 1, column++, "DOCENTE");
        excelUtil.replaceVal(irow - 1, column++, "AULA");
        excelUtil.replaceVal(irow - 1, column++, "GRUPO HORAS");
        excelUtil.replaceVal(irow - 1, column++, "HORARIO SECCIÓN");
        excelUtil.replaceVal(irow - 1, column++, "HORARIO AULA");
        //datos
        int num = 1;
        for (Seccion seccionCruzada : seccionesConCruce) {
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
            column = 0;
            excelUtil.replaceVal(irow, column++, seccionCruzada.getGrupoSeccion().getCurso().getCodigo());
            excelUtil.replaceVal(irow, column++, seccionCruzada.getGrupoSeccion().getCurso().getNombre());
            excelUtil.replaceVal(irow, column++, seccionCruzada.getCodigo2());
            excelUtil.replaceVal(irow, column++, seccionCruzada.getVacantes());
            excelUtil.replaceVal(irow, column++, seccionCruzada.getMatriculados());
            excelUtil.replaceVal(irow, column++, seccionCruzada.getTipoSeccion());
            String docente = (String) ObjectUtil.getParentTree(seccionCruzada.getDocentePrincipal(), "persona.apellidosNombres");
            excelUtil.replaceVal(irow, column++, docente);
            excelUtil.replaceVal(irow, column++, seccionCruzada.getAula().getCodigo());
            excelUtil.replaceVal(irow, column++, seccionCruzada.getGrupoHoras().getCodigo());
            CellStyle cs = wb.createCellStyle();
            cs.setWrapText(true);
            List<String> horarioSeccion = seccionCruzada.getHorarioSeccion().stream()
                    .map(x -> x.getHoraDiaDescripcion())
                    .collect(Collectors.toList());
            excelUtil.replaceVal(irow, column++, String.join("\n", horarioSeccion), cs);
            List<String> horarioAula = seccionCruzada.getHorariosAula().stream()
                    .map(x -> x.getHoraDiaDescripcion())
                    .collect(Collectors.toList());
            excelUtil.replaceVal(irow, column++, String.join("\n", horarioAula), cs);

            int max = horarioSeccion.size();
            max = horarioAula.size() > max ? horarioAula.size() : max;
            Row row = excelUtil.getSheet().getRow(irow);
            row.setHeightInPoints(max * sheet.getDefaultRowHeightInPoints());

            irow++;
        }

        sheet.setForceFormulaRecalculation(true);

    }

}
