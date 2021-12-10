package pe.edu.lamolina.amauta.controller.soporte;

import java.text.SimpleDateFormat;
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
import pe.albatross.zelpers.file.excel.ExcelStyles;
import pe.edu.lamolina.model.academico.Soporte;

@Component
public class SoporteExcelView extends AbstractView {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private static final String CONTENT_TYPE_XLSX = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";

    @Override
    protected void renderMergedOutputModel(Map<String, Object> map, HttpServletRequest request, HttpServletResponse response) throws Exception {

        Workbook workbook = new XSSFWorkbook();
        setContentType(CONTENT_TYPE_XLSX);
        this.buildExcelDocument(map, workbook, request, response);

    }

    protected void buildExcelDocument(Map<String, Object> model, Workbook wb, HttpServletRequest request, HttpServletResponse response) throws Exception {

        List<Soporte> soportes = (List<Soporte>) model.get("soportes");

        this.generateSheet(wb, soportes);

        String fecha = new DateTime().toString("yyyMMdd_Hmm");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + "Soporte_" + fecha + ".xlsx\"");
        response.setContentType(getContentType());

        ServletOutputStream out = response.getOutputStream();
        out.flush();
        wb.write(out);
        out.flush();
    }

    private void generateSheet(Workbook wb, List<Soporte> soportes) {
        Sheet sheet = wb.createSheet("Hoja1");
        this.createBody(wb, sheet, soportes);
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

    private void createBody(Workbook wb, Sheet sheet, List<Soporte> soportes) {
        ExcelHelper excelUtil = new ExcelHelper(sheet, wb);

        CellStyle headerCell = ExcelStyles.getStyleCellHeaderGrey(wb);
        CellStyle estiloNumero = getStyleNumero(wb);
        CellStyle estiloGeneral = getStyleGeneral(wb);

        int irow = 0;

        int column = 0;
        excelUtil.replaceVal(irow, column++, " ", headerCell);
        excelUtil.replaceVal(irow, column++, "DNI", headerCell);
        excelUtil.replaceVal(irow, column++, "CÓDIGO", headerCell);
        excelUtil.replaceVal(irow, column++, "NOMBRE", headerCell);
        excelUtil.replaceVal(irow, column++, "TÍTULO", headerCell);
        excelUtil.replaceVal(irow, column++, "DESCRIPCIÓN", headerCell);
        excelUtil.replaceVal(irow, column++, "FECHA REGISTRÓ", headerCell);
        excelUtil.replaceVal(irow, column++, "FECHA RESPUESTA", headerCell);
        excelUtil.replaceVal(irow, column++, "RESPONDIO", headerCell);
        excelUtil.replaceVal(irow, column++, "RESPUESTA", headerCell);
        excelUtil.replaceVal(irow, column++, "ESTADO", headerCell);
        column = 0;
        irow++;
        int num = 1;
        for (Soporte soporte : soportes) {
            excelUtil.replaceVal(irow, column++, num++, estiloNumero);
            excelUtil.replaceVal(irow, column++, soporte.getAlumno().getPersona().getNumeroDocIdentidad(), estiloGeneral);
            excelUtil.replaceVal(irow, column++, soporte.getAlumno().getCodigo(), estiloGeneral);
            excelUtil.replaceVal(irow, column++, soporte.getAlumno().getPersona().getNombreCompleto(), estiloGeneral);
            excelUtil.replaceVal(irow, column++, soporte.getTitulo(), estiloGeneral);
            excelUtil.replaceVal(irow, column++, soporte.getComentario(), estiloGeneral);
            excelUtil.replaceVal(irow, column++, this.getFecha(soporte.getFechaRegistro()), estiloGeneral);
            excelUtil.replaceVal(irow, column++, this.getFecha(soporte.getFechaAtencion()), estiloGeneral);
            excelUtil.replaceVal(irow, column++, soporte.getUserAtencion() != null ? soporte.getUserAtencion().getPersona().getNombreCompleto() : "", estiloGeneral);
            excelUtil.replaceVal(irow, column++, soporte.getRespuesta(), estiloGeneral);
            excelUtil.replaceVal(irow, column++, soporte.getEstadoEnum().getValue(), estiloGeneral);
            irow++;
            column = 0;
        }
    }

    public int tamanio(int width) {
        return width * 256;
    }

    private String getFecha(Date fecha) {
        if (fecha == null) {
            return "";
        }
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        return sdf.format(fecha);
    }

}
