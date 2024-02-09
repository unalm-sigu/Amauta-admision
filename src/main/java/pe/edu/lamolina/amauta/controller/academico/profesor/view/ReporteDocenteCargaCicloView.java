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
import pe.edu.lamolina.amauta.controller.academico.profesor.DocenteCicloCargaBean;

@Component
public class ReporteDocenteCargaCicloView extends AbstractView {

    private static final String CONTENT_TYPE_XLSX = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";

    @Override
    protected void renderMergedOutputModel(Map<String, Object> map, HttpServletRequest request, HttpServletResponse response) throws Exception {
        HSSFWorkbook workbook = new HSSFWorkbook();
        setContentType(CONTENT_TYPE_XLSX);
        this.buildExcelDocument(map, workbook, request, response);
    }

    private void buildExcelDocument(Map<String, Object> model, Workbook workbook, HttpServletRequest request, HttpServletResponse response) throws IOException {
        List<DocenteCicloCargaBean> listaDocenteCicloBean = (List<DocenteCicloCargaBean>) model.get("listdocenteCicloCargaBean");

        String listaDocente = (String) model.get("listaDocente");
        String codDocente = listaDocenteCicloBean.get(0).getCodDocente();

        this.generateCelda(workbook, listaDocenteCicloBean, listaDocente);
        String fecha = new DateTime().toString("yyyMMdd_Hmm");

        String header = "";

        header = "Carga_Academica_" + codDocente + "_";

        response.setHeader("Content-Disposition", "attachment; filename=\"" + header + fecha + ".xls\"");
        response.setContentType(getContentType());
        response.setStatus(200);
        ServletOutputStream out = response.getOutputStream();
        out.flush();
        workbook.write(out);
        out.flush();
    }

    private void generateCelda(Workbook workbook, List<DocenteCicloCargaBean> listaDocenteCicloBean, String listaDocente) {
        Sheet sheet = workbook.createSheet("Hoja1");
        ExcelHelper excelUtil = new ExcelHelper(sheet, workbook);
        int irow = 2; // fila de inicio para la data
        this.createHeader(excelUtil, workbook, listaDocente);
        this.createBody(excelUtil, irow, listaDocenteCicloBean, workbook, listaDocente);
    }

    private void createHeader(ExcelHelper excelUtil, Workbook workbook, String listaDocente) {
        CellStyle estiloCabecera = getStyleCabecera(workbook, HorizontalAlignment.CENTER);
        CellStyle estiloCabeceraLeft = getStyleCabecera(workbook, HorizontalAlignment.LEFT);
        this.setWidthColumn(excelUtil.getSheet(), 0, 3000);
        this.setWidthColumn(excelUtil.getSheet(), 1, 6000);
        this.setWidthColumn(excelUtil.getSheet(), 2, 2500);
        this.setWidthColumn(excelUtil.getSheet(), 3, 7500);
        this.setWidthColumn(excelUtil.getSheet(), 4, 2000);
        this.setWidthColumn(excelUtil.getSheet(), 5, 2000);
        this.setWidthColumn(excelUtil.getSheet(), 6, 2000);
        this.setWidthColumn(excelUtil.getSheet(), 7, 2000);
        this.setWidthColumn(excelUtil.getSheet(), 8, 3500);
        this.setWidthColumn(excelUtil.getSheet(), 9, 2500);
        this.setWidthColumn(excelUtil.getSheet(), 10, 3500);

        this.setWidthColumn(excelUtil.getSheet(), 11, 3500);
        this.setWidthColumn(excelUtil.getSheet(), 12, 3500);
        this.setWidthColumn(excelUtil.getSheet(), 13, 2500);
        this.setWidthColumn(excelUtil.getSheet(), 14, 2500);
        this.setWidthColumn(excelUtil.getSheet(), 15, 11500);

        excelUtil.replaceStyle(0, 0, estiloCabeceraLeft);
        excelUtil.replaceStyle(0, 1, estiloCabeceraLeft);
        excelUtil.replaceStyle(0, 2, estiloCabeceraLeft);
        excelUtil.replaceStyle(0, 3, estiloCabeceraLeft);
        excelUtil.replaceStyle(0, 4, estiloCabeceraLeft);
        excelUtil.replaceStyle(0, 5, estiloCabecera);
        excelUtil.replaceStyle(0, 6, estiloCabecera);
        excelUtil.replaceStyle(0, 7, estiloCabecera);
        excelUtil.replaceStyle(0, 8, estiloCabeceraLeft);
        excelUtil.replaceStyle(0, 9, estiloCabecera);
        excelUtil.replaceStyle(0, 10, estiloCabecera);
        excelUtil.replaceStyle(0, 11, estiloCabecera);
        excelUtil.replaceStyle(0, 12, estiloCabecera);
        excelUtil.replaceStyle(0, 13, estiloCabecera);
        excelUtil.replaceStyle(0, 14, estiloCabecera);
        excelUtil.replaceStyle(0, 15, estiloCabeceraLeft);

        excelUtil.replaceVal(0, 0, "Ciclo");
        excelUtil.replaceVal(0, 1, "Anexo");
        excelUtil.replaceVal(0, 2, "Codigo");
        excelUtil.replaceVal(0, 3, "curso");
        excelUtil.replaceVal(0, 4, "H.Teoria");
        excelUtil.replaceVal(0, 5, "H.Practicas");
        excelUtil.replaceVal(0, 6, "Creditos");
        excelUtil.replaceVal(0, 7, "Seccion");
        excelUtil.replaceVal(0, 8, "tipo Seccion");
        excelUtil.replaceVal(0, 9, "Por.Carga");
        excelUtil.replaceVal(0, 10, "Fecha Inicio");

        excelUtil.replaceVal(0, 11, "Fecha Fin");
        excelUtil.replaceVal(0, 12, "Día");
        excelUtil.replaceVal(0, 13, "Créditos");
        excelUtil.replaceVal(0, 14, "Matric.");
        excelUtil.replaceVal(0, 15, "Horario");

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

    private void createBody(ExcelHelper excelUtil, int irow, List<DocenteCicloCargaBean> listaDocenteCicloBean, Workbook workbook, String listaDocente) {
        CellStyle estiloGeneral = getStyleGeneral(workbook, HorizontalAlignment.CENTER);
        CellStyle estiloLeft = getStyleGeneral(workbook, HorizontalAlignment.LEFT);
        for (DocenteCicloCargaBean item : listaDocenteCicloBean) {
            excelUtil.replaceStyle(irow - 1, 0, estiloGeneral);
            excelUtil.replaceStyle(irow - 1, 1, estiloLeft);
            excelUtil.replaceStyle(irow - 1, 2, estiloGeneral);
            excelUtil.replaceStyle(irow - 1, 3, estiloLeft);
            excelUtil.replaceStyle(irow - 1, 4, estiloLeft);
            excelUtil.replaceStyle(irow - 1, 5, estiloGeneral);
            excelUtil.replaceStyle(irow - 1, 6, estiloGeneral);
            excelUtil.replaceStyle(irow - 1, 7, estiloGeneral);
            excelUtil.replaceStyle(irow - 1, 8, estiloLeft);
            excelUtil.replaceStyle(irow - 1, 9, estiloGeneral);
            excelUtil.replaceStyle(irow - 1, 10, estiloGeneral);

            excelUtil.replaceStyle(irow - 1, 11, estiloGeneral);
            excelUtil.replaceStyle(irow - 1, 12, estiloGeneral);
            excelUtil.replaceStyle(irow - 1, 13, estiloGeneral);
            excelUtil.replaceStyle(irow - 1, 14, estiloGeneral);
            excelUtil.replaceStyle(irow - 1, 15, estiloLeft);

            excelUtil.replaceVal(irow - 1, 0, item.getDescripcion());
            excelUtil.replaceVal(irow - 1, 1, item.getAnexo());
            excelUtil.replaceVal(irow - 1, 2, item.getCodigoCurso());
            excelUtil.replaceVal(irow - 1, 3, item.getCurso());
            excelUtil.replaceVal(irow - 1, 4, item.getHorasTeoria());
            excelUtil.replaceVal(irow - 1, 5, item.getHorasPractica());
            excelUtil.replaceVal(irow - 1, 6, item.getCreditos());
            excelUtil.replaceVal(irow - 1, 7, item.getSeccion());
            excelUtil.replaceVal(irow - 1, 8, item.getTiposeccion());
            excelUtil.replaceVal(irow - 1, 9, item.getPorcentajeCarga());
            excelUtil.replaceVal(irow - 1, 10, item.getFechaInicio());

            excelUtil.replaceVal(irow - 1, 11, item.getFechaFin());
            excelUtil.replaceVal(irow - 1, 12, item.getDia());
            excelUtil.replaceVal(irow - 1, 13, item.getCredProfe());
            excelUtil.replaceVal(irow - 1, 14, item.getMatriculados());
            excelUtil.replaceVal(irow - 1, 15, item.getHorario());
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
