package pe.edu.lamolina.amauta.controller.academico.profesor.view;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.Sheet;

import org.joda.time.DateTime;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.view.AbstractView;
import pe.albatross.zelpers.file.excel.ExcelHelper;
import pe.edu.lamolina.amauta.controller.academico.profesor.DocenteCicloBean;

@Component
public class ReporteDocenteCicloView extends AbstractView {

    private static final String CONTENT_TYPE_XLSX = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";

    @Override
    protected void renderMergedOutputModel(Map<String, Object> map, HttpServletRequest request, HttpServletResponse response) throws Exception {
        HSSFWorkbook workbook = new HSSFWorkbook();
        setContentType(CONTENT_TYPE_XLSX);
        this.buildExcelDocument(map, workbook, request, response);
    }

    private void buildExcelDocument(Map<String, Object> model, Workbook workbook, HttpServletRequest request, HttpServletResponse response) throws IOException {
        List<DocenteCicloBean> listaDocenteCicloBean = (List<DocenteCicloBean>) model.get("listdocenteCicloBean");

        String listaDocente = (String) model.get("listaDocente");
        this.generateCelda(workbook, listaDocenteCicloBean, listaDocente);
        String fecha = new DateTime().toString("yyyMMdd_Hmm");

        String header = "";

        header = "Programacion_Harario_";

        response.setHeader("Content-Disposition", "attachment; filename=\"" + header + fecha + ".xls\"");
        response.setContentType(getContentType());
        response.setStatus(200);
        ServletOutputStream out = response.getOutputStream();
        out.flush();
        workbook.write(out);
        out.flush();
    }

    private void generateCelda(Workbook workbook, List<DocenteCicloBean> listaDocenteCicloBean, String listaDocente) {
        Sheet sheet = workbook.createSheet("Hoja1");
        ExcelHelper excelUtil = new ExcelHelper(sheet, workbook);
        int irow = 2; // fila de inicio para la data
        this.createHeader(excelUtil, workbook, listaDocente);
        this.createBody(excelUtil, irow, listaDocenteCicloBean, workbook, listaDocente);
    }

    private void createHeader(ExcelHelper excelUtil, Workbook workbook, String listaDocente) {
        CellStyle estiloCabecera = getStyleCabecera(workbook, HorizontalAlignment.CENTER);
        CellStyle estiloCabeceraLeft = getStyleCabecera(workbook, HorizontalAlignment.LEFT);
        this.setWidthColumn(excelUtil.getSheet(), 0, 4000);
        this.setWidthColumn(excelUtil.getSheet(), 1, 3500);
        this.setWidthColumn(excelUtil.getSheet(), 2, 9500);
        this.setWidthColumn(excelUtil.getSheet(), 3, 3000);
        this.setWidthColumn(excelUtil.getSheet(), 4, 7000);
        this.setWidthColumn(excelUtil.getSheet(), 5, 2500);
        this.setWidthColumn(excelUtil.getSheet(), 6, 3500);
        this.setWidthColumn(excelUtil.getSheet(), 7, 6500);
        this.setWidthColumn(excelUtil.getSheet(), 8, 8500);
        this.setWidthColumn(excelUtil.getSheet(), 9, 5500);
        this.setWidthColumn(excelUtil.getSheet(), 10, 5500);

        excelUtil.replaceStyle(0, 0, estiloCabecera);
        excelUtil.replaceStyle(0, 1, estiloCabecera);
        excelUtil.replaceStyle(0, 2, estiloCabeceraLeft);
        excelUtil.replaceStyle(0, 3, estiloCabecera);
        excelUtil.replaceStyle(0, 4, estiloCabeceraLeft);
        excelUtil.replaceStyle(0, 5, estiloCabecera);
        excelUtil.replaceStyle(0, 6, estiloCabecera);
        excelUtil.replaceStyle(0, 7, estiloCabecera);
        excelUtil.replaceStyle(0, 8, estiloCabeceraLeft);
        excelUtil.replaceStyle(0, 9, estiloCabecera);
        excelUtil.replaceStyle(0, 10, estiloCabecera);

        excelUtil.replaceVal(0, 0, "Ciclo");
        excelUtil.replaceVal(0, 1, "Código");
        excelUtil.replaceVal(0, 2, "Nombres");
        excelUtil.replaceVal(0, 3, "Categoría");
        excelUtil.replaceVal(0, 4, "Detalle Categoría");
        excelUtil.replaceVal(0, 5, "Situación");
        excelUtil.replaceVal(0, 6, "Dedicación");
        excelUtil.replaceVal(0, 7, "Detalle DE");
        excelUtil.replaceVal(0, 8, "Departamento");
        excelUtil.replaceVal(0, 9, "Pre");
        excelUtil.replaceVal(0, 10, "Post");

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

    private void createBody(ExcelHelper excelUtil, int irow, List<DocenteCicloBean> listaDocenteCicloBean, Workbook workbook, String listaDocente) {
        CellStyle estiloGeneral = getStyleGeneral(workbook, HorizontalAlignment.CENTER);
        CellStyle estiloLeft = getStyleGeneral(workbook, HorizontalAlignment.LEFT);
        for (DocenteCicloBean item : listaDocenteCicloBean) {
            excelUtil.replaceStyle(irow - 1, 0, estiloGeneral);
            excelUtil.replaceStyle(irow - 1, 1, estiloGeneral);
            excelUtil.replaceStyle(irow - 1, 2, estiloLeft);
            excelUtil.replaceStyle(irow - 1, 3, estiloGeneral);
            excelUtil.replaceStyle(irow - 1, 4, estiloLeft);
            excelUtil.replaceStyle(irow - 1, 5, estiloGeneral);
            excelUtil.replaceStyle(irow - 1, 6, estiloGeneral);
            excelUtil.replaceStyle(irow - 1, 7, estiloGeneral);
            excelUtil.replaceStyle(irow - 1, 8, estiloLeft);
            excelUtil.replaceStyle(irow - 1, 9, estiloGeneral);
            excelUtil.replaceStyle(irow - 1, 10, estiloGeneral);

            excelUtil.replaceVal(irow - 1, 0, item.getDescripcion());
            excelUtil.replaceVal(irow - 1, 1, item.getCod_docente2());
            excelUtil.replaceVal(irow - 1, 2, item.getNombreDocente());
            excelUtil.replaceVal(irow - 1, 3, item.getPro_cad2());
            excelUtil.replaceVal(irow - 1, 4, item.getCateg_nombre2());
            excelUtil.replaceVal(irow - 1, 5, item.getPro_situac2());
            excelUtil.replaceVal(irow - 1, 6, item.getCod_dedica2());
            excelUtil.replaceVal(irow - 1, 7, item.getNombre());
            excelUtil.replaceVal(irow - 1, 8, item.getNombre_largo());
            excelUtil.replaceVal(irow - 1, 9, item.getCargaPregrado());
            excelUtil.replaceVal(irow - 1, 10, item.getCargaPost());
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
}
