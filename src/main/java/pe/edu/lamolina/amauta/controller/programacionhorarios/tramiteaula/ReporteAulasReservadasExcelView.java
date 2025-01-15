package pe.edu.lamolina.amauta.controller.programacionhorarios.tramiteaula;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.joda.time.DateTime;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.view.AbstractView;
import pe.albatross.zelpers.file.excel.ExcelHelper;
import pe.edu.lamolina.amauta.controller.academico.pronabec.MatriculadosBecadosBean;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@Component
public class ReporteAulasReservadasExcelView extends AbstractView {

    private static final String CONTENT_TYPE_XLSX = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";

    @Override
    protected void renderMergedOutputModel(Map<String, Object> map, HttpServletRequest request, HttpServletResponse response) throws Exception {
        HSSFWorkbook workbook = new HSSFWorkbook();
        setContentType(CONTENT_TYPE_XLSX);
        this.buildExcelDocument(map, workbook, request, response);
    }

    private void buildExcelDocument(Map<String, Object> model, Workbook workbook, HttpServletRequest request, HttpServletResponse response) throws IOException {
        List<ReservaAulaBean> listMatriculadosBecadosBean = (List<ReservaAulaBean>) model.get("listReservas");
        this.generateCelda(workbook, listMatriculadosBecadosBean);
        String fecha = new DateTime().toString("yyyMMdd_Hmm");
        String header = "Reporte_Aulas_Reservadas_";

        response.setHeader("Content-Disposition", "attachment; filename=\"" + header + fecha + ".xls\"");
        response.setContentType(getContentType());
        response.setStatus(200);
        ServletOutputStream out = response.getOutputStream();
        out.flush();
        workbook.write(out);
        out.flush();
    }

    private void generateCelda(Workbook workbook, List<ReservaAulaBean> listMatriculadosBecadosBean){
        Sheet sheet = workbook.createSheet("Hoja1");
        ExcelHelper excelUtil = new ExcelHelper(sheet, workbook);
        int irow = 2;
        this.createHeader(excelUtil, workbook);
        this.createBody(excelUtil, irow, listMatriculadosBecadosBean, workbook);
    }

    private void createHeader(ExcelHelper excelUtil, Workbook workbook) {
        CellStyle estiloCabecera = getStyleCabecera(workbook, HorizontalAlignment.CENTER);
        CellStyle estiloCabeceraLeft = getStyleCabecera(workbook, HorizontalAlignment.LEFT);
        this.setWidthColumn(excelUtil.getSheet(), 0, 5000);
        this.setWidthColumn(excelUtil.getSheet(), 1, 6000);
        this.setWidthColumn(excelUtil.getSheet(), 2, 6000);
        this.setWidthColumn(excelUtil.getSheet(), 3, 7000);
        this.setWidthColumn(excelUtil.getSheet(), 4, 7000);
        this.setWidthColumn(excelUtil.getSheet(), 5, 10000);
        this.setWidthColumn(excelUtil.getSheet(), 6, 10000);
        this.setWidthColumn(excelUtil.getSheet(), 7, 5000);
        this.setWidthColumn(excelUtil.getSheet(), 8, 5000);
        this.setWidthColumn(excelUtil.getSheet(), 9, 7000);

        excelUtil.replaceStyle(0, 0, estiloCabeceraLeft);
        excelUtil.replaceStyle(0, 1, estiloCabeceraLeft);
        excelUtil.replaceStyle(0, 2, estiloCabeceraLeft);
        excelUtil.replaceStyle(0, 3, estiloCabeceraLeft);
        excelUtil.replaceStyle(0, 4, estiloCabeceraLeft);
        excelUtil.replaceStyle(0, 5, estiloCabecera);
        excelUtil.replaceStyle(0, 6, estiloCabecera);
        excelUtil.replaceStyle(0, 7, estiloCabecera);
        excelUtil.replaceStyle(0, 8, estiloCabecera);
        excelUtil.replaceStyle(0, 9, estiloCabecera);

        excelUtil.replaceVal(0, 0, "CICLO");
        excelUtil.replaceVal(0, 1, "FECHA INICIO");
        excelUtil.replaceVal(0, 2, "FECHA FIN");
        excelUtil.replaceVal(0, 3, "AULA (CAPACIDAD) ");
        excelUtil.replaceVal(0, 4, "SOLICITANTE");
        excelUtil.replaceVal(0, 5, "MOTIVO");
        excelUtil.replaceVal(0, 6, "COMENTARIO");
        excelUtil.replaceVal(0, 7, "ESTADO");
        excelUtil.replaceVal(0, 8, "TIPO RESERVA");
        excelUtil.replaceVal(0, 9, "SOLICITUD");

    }

    private CellStyle getStyleCabecera(Workbook workbook, HorizontalAlignment posicion) {
        Font font = workbook.createFont();
        font.setFontName("Arial");
        font.setBold(true);
        CellStyle cell = workbook.createCellStyle();
        cell.setAlignment(posicion);
        cell.setFont(font);
        cell.setBorderTop(BorderStyle.MEDIUM);
        cell.setBorderBottom(BorderStyle.MEDIUM);
        cell.setBorderRight(BorderStyle.MEDIUM);
        cell.setBorderLeft(BorderStyle.MEDIUM);
        return cell;
    }

    private void setWidthColumn(Sheet sheet, int numberColumn, int width) {
        sheet.setColumnWidth(numberColumn, width);
    }

    private void createBody(ExcelHelper excelUtil, int irow, List<ReservaAulaBean> listMatriculadosBecadosBean, Workbook workbook) {
        CellStyle estiloGeneral = getStyleGeneral(workbook, HorizontalAlignment.CENTER);
        CellStyle estiloLeft = getStyleGeneral(workbook, HorizontalAlignment.LEFT);

        for (ReservaAulaBean item : listMatriculadosBecadosBean){
            excelUtil.replaceStyle(irow - 1, 0, estiloLeft);
            excelUtil.replaceStyle(irow - 1, 1, estiloLeft);
            excelUtil.replaceStyle(irow - 1, 2, estiloLeft);
            excelUtil.replaceStyle(irow - 1, 3, estiloLeft);
            excelUtil.replaceStyle(irow - 1, 4, estiloLeft);
            excelUtil.replaceStyle(irow - 1, 5, estiloGeneral);
            excelUtil.replaceStyle(irow - 1, 6, estiloGeneral);
            excelUtil.replaceStyle(irow - 1, 7, estiloGeneral);
            excelUtil.replaceStyle(irow - 1, 8, estiloGeneral);
            excelUtil.replaceStyle(irow - 1, 9, estiloGeneral);

            excelUtil.replaceVal(irow - 1, 0, item.getCiclo());
            excelUtil.replaceVal(irow - 1, 1, item.getFecha_inicio());
            excelUtil.replaceVal(irow - 1, 2, item.getFecha_fin());
            excelUtil.replaceVal(irow - 1, 3, item.getAula());
            excelUtil.replaceVal(irow - 1, 4, item.getSolicitante());
            excelUtil.replaceVal(irow - 1, 5, item.getMotivo());
            excelUtil.replaceVal(irow - 1, 6, item.getComentario());
            excelUtil.replaceVal(irow - 1, 7, item.getEstado());
            excelUtil.replaceVal(irow - 1, 8, item.getTipo_reserva());
            excelUtil.replaceVal(irow - 1, 9, item.getCodigo());

            irow++;

        }

    }

    private CellStyle getStyleGeneral(Workbook workbook, HorizontalAlignment posicion) {
        Font font = workbook.createFont();
        font.setFontName("Arial");
        CellStyle cell = workbook.createCellStyle();
        cell.setAlignment(posicion);
        cell.setFont(font);
        cell.setBorderTop(BorderStyle.THIN);
        cell.setBorderBottom(BorderStyle.THIN);
        cell.setBorderRight(BorderStyle.THIN);
        cell.setBorderLeft(BorderStyle.THIN);
        return cell;
    }

    private String retornVacio(Long parametro) {
        if (parametro == null) {
            return "-";
        }
        return parametro.toString();
    }
}
