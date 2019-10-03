package pe.edu.lamolina.pivot.controller.matricula.matriculable;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.joda.time.DateTime;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.view.AbstractView;
import pe.albatross.zelpers.file.excel.ExcelHelper;

@Component
public class ActosPregradoView extends AbstractView {

    private static final String CONTENT_TYPE_XLSX = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";

    @Override
    protected void renderMergedOutputModel(Map<String, Object> map, HttpServletRequest request, HttpServletResponse response) throws Exception {
        HSSFWorkbook workbook = new HSSFWorkbook();
        setContentType(CONTENT_TYPE_XLSX);
        this.buildExcelDocument(map, workbook, request, response);
    }

    private void buildExcelDocument(Map<String, Object> model, Workbook workbook, HttpServletRequest request, HttpServletResponse response) throws IOException {
        List<ActoPreBean> listActoPreBean = (List<ActoPreBean>) model.get("listActoPreBean");
        String tipoReporte = (String) model.get("tipoReporte");
        this.generateCelda(workbook, listActoPreBean, tipoReporte);
        String fecha = new DateTime().toString("yyyMMdd_Hmm");

        String header = "";
        if ("candidatosActPre".equals(tipoReporte)) {
            header = "Candidatos_Actos_Pregrado_";
        }

        if ("votantesActPre".equals(tipoReporte)) {
            header = "Votantes_Actos_Pregrado_";
        }

        response.setHeader("Content-Disposition", "attachment; filename=\"" + header + fecha + ".xlsx\"");
        response.setContentType(getContentType());
        ServletOutputStream out = response.getOutputStream();
        out.flush();
        workbook.write(out);
        out.flush();
    }

    private void generateCelda(Workbook workbook, List<ActoPreBean> listActoPreBean, String tipoReporte) {
        Sheet sheet = workbook.createSheet("Hoja1");
        ExcelHelper excelUtil = new ExcelHelper(sheet, workbook);
        int irow = 2; // fila de inicio para la data
        this.createHeader(excelUtil, workbook, tipoReporte);
        this.createBody(excelUtil, irow, listActoPreBean, workbook, tipoReporte);
    }

    private void createHeader(ExcelHelper excelUtil, Workbook workbook, String tipoReporte) {
        CellStyle estiloCabecera = getStyleCabecera(workbook, CellStyle.ALIGN_CENTER);
        CellStyle estiloCabeceraLeft = getStyleCabecera(workbook, CellStyle.ALIGN_LEFT);
        this.setWidthColumn(excelUtil.getSheet(), 1, 12500);
        this.setWidthColumn(excelUtil.getSheet(), 2, 10000);
        this.setWidthColumn(excelUtil.getSheet(), 3, 10000);
        this.setWidthColumn(excelUtil.getSheet(), 4, 7000);
        this.setWidthColumn(excelUtil.getSheet(), 5, 7000);
        this.setWidthColumn(excelUtil.getSheet(), 6, 7000);
        this.setWidthColumn(excelUtil.getSheet(), 7, 7000);
        excelUtil.replaceStyle(0, 0, estiloCabecera);
        excelUtil.replaceStyle(0, 1, estiloCabeceraLeft);
        excelUtil.replaceStyle(0, 2, estiloCabeceraLeft);
        excelUtil.replaceStyle(0, 3, estiloCabeceraLeft);
        excelUtil.replaceStyle(0, 4, estiloCabecera);
        excelUtil.replaceStyle(0, 5, estiloCabecera);
        excelUtil.replaceStyle(0, 6, estiloCabecera);
        excelUtil.replaceStyle(0, 7, estiloCabecera);
        excelUtil.replaceStyle(0, 8, estiloCabecera);
        excelUtil.replaceVal(0, 0, "Matrícula");
        excelUtil.replaceVal(0, 1, "Apellidos y Nombres");
        excelUtil.replaceVal(0, 2, "Especialidad");
        excelUtil.replaceVal(0, 3, "Facultad");
        excelUtil.replaceVal(0, 4, "Créditos Matriculados");
        excelUtil.replaceVal(0, 5, "Créditos Aprobados");
        excelUtil.replaceVal(0, 6, "Ciclos Estudiados");
        excelUtil.replaceVal(0, 7, "Código de Facultad");
        excelUtil.replaceVal(0, 8, "Nivel");

        if ("candidatosActPre".equals(tipoReporte)) {
            this.setWidthColumn(excelUtil.getSheet(), 9, 8000);
            excelUtil.replaceStyle(0, 9, estiloCabecera);
            excelUtil.replaceVal(0, 9, "Tercio Superior");
        }
    }

    private CellStyle getStyleCabecera(Workbook workbook, short posicion) {
        Font font = workbook.createFont();
        font.setFontName("Arial");
        font.setBoldweight(Font.BOLDWEIGHT_BOLD);
        CellStyle cell = workbook.createCellStyle();
        cell.setAlignment(posicion);
        cell.setFont(font);
        cell.setBorderTop(HSSFCellStyle.BORDER_MEDIUM);
        cell.setBorderBottom(HSSFCellStyle.BORDER_MEDIUM);
        cell.setBorderRight(HSSFCellStyle.BORDER_MEDIUM);
        cell.setBorderLeft(HSSFCellStyle.BORDER_MEDIUM);
        return cell;
    }

    private CellStyle getStyleGeneral(Workbook workbook, short posicion) {
        Font font = workbook.createFont();
        font.setFontName("Arial");
        CellStyle cell = workbook.createCellStyle();
        cell.setAlignment(posicion);
        cell.setFont(font);
        cell.setBorderTop(HSSFCellStyle.BORDER_THIN);
        cell.setBorderBottom(HSSFCellStyle.BORDER_THIN);
        cell.setBorderRight(HSSFCellStyle.BORDER_THIN);
        cell.setBorderLeft(HSSFCellStyle.BORDER_THIN);
        return cell;
    }

    private void setWidthColumn(Sheet sheet, int numberColumn, int width) {
        sheet.setColumnWidth(numberColumn, width);
    }

    private void createBody(ExcelHelper excelUtil, int irow, List<ActoPreBean> listActoPreBean, Workbook workbook, String tipoReporte) {
        CellStyle estiloGeneral = getStyleGeneral(workbook, CellStyle.ALIGN_CENTER);
        CellStyle estiloLeft = getStyleGeneral(workbook, CellStyle.ALIGN_LEFT);
        for (ActoPreBean item : listActoPreBean) {
            excelUtil.replaceStyle(irow - 1, 0, estiloGeneral);
            excelUtil.replaceStyle(irow - 1, 1, estiloLeft);
            excelUtil.replaceStyle(irow - 1, 2, estiloLeft);
            excelUtil.replaceStyle(irow - 1, 3, estiloLeft);
            excelUtil.replaceStyle(irow - 1, 4, estiloGeneral);
            excelUtil.replaceStyle(irow - 1, 5, estiloGeneral);
            excelUtil.replaceStyle(irow - 1, 6, estiloGeneral);
            excelUtil.replaceStyle(irow - 1, 7, estiloGeneral);
            excelUtil.replaceStyle(irow - 1, 8, estiloGeneral);
            excelUtil.replaceVal(irow - 1, 0, item.getMatricula());
            excelUtil.replaceVal(irow - 1, 1, item.getApellidos_nombres());
            excelUtil.replaceVal(irow - 1, 2, item.getEspecialidad());
            excelUtil.replaceVal(irow - 1, 3, item.getFacultad());
            excelUtil.replaceVal(irow - 1, 4, item.getCreditos_matriculados());
            excelUtil.replaceVal(irow - 1, 5, item.getCreditos_aprobados());
            excelUtil.replaceVal(irow - 1, 6, item.getCiclos_estudiados());
            excelUtil.replaceVal(irow - 1, 7, item.getCodigo_facultad());
            excelUtil.replaceVal(irow - 1, 8, this.retornVacio(item.getNivel()));
            if ("candidatosActPre".equals(tipoReporte)) {
                excelUtil.replaceStyle(irow - 1, 9, estiloGeneral);
                excelUtil.replaceVal(irow - 1, 9, item.getEs_3cio_super());
            }
            irow++;
        }
    }

    private String retornVacio(Long parametro) {
        if (parametro == null) {
            return "-";
        }
        return parametro.toString();
    }

}
