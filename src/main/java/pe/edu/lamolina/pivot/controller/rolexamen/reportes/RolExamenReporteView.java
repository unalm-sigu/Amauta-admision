package pe.edu.lamolina.pivot.controller.rolexamen.reportes;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pe.albatross.zelpers.file.excel.AbstractPOIExcelView;
import pe.albatross.zelpers.file.excel.ExcelStyles;
import pe.albatross.zelpers.miscelanea.ObjectUtil;
import pe.edu.lamolina.model.rolexamen.CursoMasivoExamen;
import pe.edu.lamolina.model.rolexamen.LetraGrupoRegular;
import pe.edu.lamolina.model.rolexamen.RolExamenes;
import pe.edu.lamolina.model.rolexamen.SeccionGrupoEspecial;
import pe.edu.lamolina.pivot.dao.academico.DocenteSeccionDAO;
import pe.edu.lamolina.pivot.dao.academico.GrupoSeccionDAO;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.util.CellUtil;
import pe.albatross.zelpers.file.excel.ExcelHelper;

@Component
public class RolExamenReporteView extends AbstractPOIExcelView {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    GrupoSeccionDAO grupoSeccionDAO;

    @Autowired
    DocenteSeccionDAO docenteSeccionDAO;

    @Override
    protected Workbook createWorkbook() {
        return new SXSSFWorkbook();
    }

    @Override
    protected void buildExcelDocument(Map<String, Object> model, Workbook workbook, HttpServletRequest request, HttpServletResponse response) throws Exception {

        RolExamenes rol = (RolExamenes) model.get("rol");
        List<CursoMasivoExamen> masivos = (List<CursoMasivoExamen>) model.get("masivos");
        List<LetraGrupoRegular> regulares = (List<LetraGrupoRegular>) model.get("regulares");
        List<SeccionGrupoEspecial> especiales = (List<SeccionGrupoEspecial>) model.get("especiales");

        this.crearHojaRegulares(workbook, regulares);
        this.crearHojaEspeciales(workbook, especiales);
        this.crearHojaMasivos(workbook, masivos);

        String fechaRep = " - " + new DateTime().toString("dd/MM/yyyy H:mm");

        String nombreReporte = "RolExamenes ";

        response.setHeader("Content-Disposition", "attachment; filename=\"" + nombreReporte + fechaRep + ".xlsx\"");
    }

    private void createHeader(String headers, Integer row, Sheet sheet, Workbook wb) {
        StringTokenizer st = new StringTokenizer(headers, "|");

        Integer col = 0;

        while (st.hasMoreTokens()) {
            String token = st.nextToken();
            this.createCell(row, col, token, sheet, ExcelStyles.getStyleHeader(wb));
            col++;
        }
    }

    private void crearHojaRegulares(Workbook wb, List<LetraGrupoRegular> regulares) {
        Sheet sheet = wb.createSheet("REGULARES");
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE dd 'de' MMMMM", new Locale("es", "ES"));

        this.createHeader("GRUPO|FECHA|HORA", 2, sheet, wb);
        Integer cont = 3;

        for (LetraGrupoRegular regular : regulares) {
            List<String> grupos = regular.getGruposRegularesExamenes().stream().map(grupoRegular -> grupoRegular.getGrupoHoras().getCodigo()).collect(Collectors.toList());
            ExcelHelper.replaceVal(sheet, cont, 0, String.join(", ", grupos));
            ExcelHelper.replaceVal(sheet, cont, 1, StringUtils.capitalize(sdf.format(regular.getGrupoHorasExamen().getFecha())));
            ExcelHelper.replaceVal(sheet, cont, 2, regular.getGrupoHorasExamen().getHoraInicio().getDescripcion());

            for (int i = 0; i < 3; i++) {
                CellUtil.setCellStyleProperty(sheet.getRow(cont).getCell(i), wb, CellUtil.ALIGNMENT, CellStyle.ALIGN_CENTER);
                CellUtil.setCellStyleProperty(sheet.getRow(cont).getCell(i), wb, CellUtil.VERTICAL_ALIGNMENT, CellStyle.VERTICAL_CENTER);
            }

            cont++;
        }

        for (int i = 0; i < 3; i++) {
            sheet.autoSizeColumn(i);
        }

    }

    private void crearHojaEspeciales(Workbook wb, List<SeccionGrupoEspecial> especiales) {
        Sheet sheet = wb.createSheet("ESPECIALES");
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE dd 'de' MMMMM", new Locale("es", "ES"));

        this.createHeader("CURSO|CLAVE|GR.|AULA|FECHA|HORA|PROFESOR", 2, sheet, wb);
        Integer cont = 3;

        for (SeccionGrupoEspecial masivo : especiales) {
            ExcelHelper.replaceVal(sheet, cont, 0, (String) ObjectUtil.getParentTree(masivo, "seccion.grupoSeccion.curso.nombre"));
            ExcelHelper.replaceVal(sheet, cont, 1, (String) ObjectUtil.getParentTree(masivo, "seccion.codigo2"));
            ExcelHelper.replaceVal(sheet, cont, 2, (String) ObjectUtil.getParentTree(masivo, "seccion.grupoHoras.codigo"));
            ExcelHelper.replaceVal(sheet, cont, 3, (String) ObjectUtil.getParentTree(masivo, "aula.codigo"));

            CellUtil.setCellStyleProperty(sheet.getRow(cont).getCell(3), wb, CellUtil.ALIGNMENT, CellStyle.ALIGN_CENTER);

            if (masivo.getGrupoHorasExamen() != null && masivo.getGrupoHorasExamen().getFecha() != null) {
                ExcelHelper.replaceVal(sheet, cont, 4, StringUtils.capitalize(sdf.format(masivo.getGrupoHorasExamen().getFecha())));
            }

            ExcelHelper.replaceVal(sheet, cont, 5, (String) ObjectUtil.getParentTree(masivo, "grupoHorasExamen.horaInicio.descripcion"));
            ExcelHelper.replaceVal(sheet, cont, 6, (String) ObjectUtil.getParentTree(masivo, "docente.persona.paterno"));

            cont++;
        }

        for (int i = 0; i < 7; i++) {
            sheet.autoSizeColumn(i);
        }
    }

    private void crearHojaMasivos(Workbook wb, List<CursoMasivoExamen> masivos) {
        Sheet sheet = wb.createSheet("MASIVOS");
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE dd 'de' MMMMM", new Locale("es", "ES"));

        this.createHeader("CODIGO|CURSO|FECHA Y HORA|AULAS", 2, sheet, wb);
        Integer cont = 3;

        for (CursoMasivoExamen masivo : masivos) {
            List<String> aulas = masivo.getAulasCursosMasivos().stream().map(aulaExamen -> aulaExamen.getAula().getCodigo()).collect(Collectors.toList());
            ExcelHelper.replaceVal(sheet, cont, 0, (String) ObjectUtil.getParentTree(masivo, "curso.codigo"));
            ExcelHelper.replaceVal(sheet, cont, 1, (String) ObjectUtil.getParentTree(masivo, "curso.nombre"));

            CellUtil.setCellStyleProperty(sheet.getRow(cont).getCell(0), wb, CellUtil.ALIGNMENT, CellStyle.ALIGN_CENTER);
            CellUtil.setCellStyleProperty(sheet.getRow(cont).getCell(0), wb, CellUtil.VERTICAL_ALIGNMENT, CellStyle.VERTICAL_CENTER);

            CellUtil.setCellStyleProperty(sheet.getRow(cont).getCell(1), wb, CellUtil.VERTICAL_ALIGNMENT, CellStyle.VERTICAL_CENTER);

            if (masivo.getGrupoHorasExamen() != null) {
                if (masivo.getGrupoHorasExamen().getHoraInicio() != null) {
                    ExcelHelper.replaceVal(sheet, cont, 2, String.format("%s\n%s", StringUtils.capitalize(sdf.format(masivo.getGrupoHorasExamen().getFecha())), masivo.getGrupoHorasExamen().getHoraInicio().getDescripcion()));
                } else {
                    ExcelHelper.replaceVal(sheet, cont, 2, StringUtils.capitalize(sdf.format(masivo.getGrupoHorasExamen().getFecha())));
                }
            }

            CellUtil.setCellStyleProperty(sheet.getRow(cont).getCell(2), wb, CellUtil.ALIGNMENT, CellStyle.ALIGN_CENTER);
            CellUtil.setCellStyleProperty(sheet.getRow(cont).getCell(2), wb, CellUtil.VERTICAL_ALIGNMENT, CellStyle.VERTICAL_CENTER);

            ExcelHelper.replaceVal(sheet, cont, 3, String.join(", ", aulas));
            CellUtil.setCellStyleProperty(sheet.getRow(cont).getCell(3), wb, CellUtil.ALIGNMENT, CellStyle.ALIGN_CENTER);
            CellUtil.setCellStyleProperty(sheet.getRow(cont).getCell(3), wb, CellUtil.VERTICAL_ALIGNMENT, CellStyle.VERTICAL_CENTER);

            cont++;
        }

        for (int i = 0; i < 4; i++) {
            sheet.autoSizeColumn(i);
        }

    }

    private void createCell(Integer cellRow, int cellNumber, String value, Sheet sheet, CellStyle style) {
        Row row = sheet.getRow(cellRow);
        if (row == null) {
            row = sheet.createRow(cellRow);
        }
        this.createCell(row, cellNumber, value, style);
    }

    private void createCell(Row row, int cellNumber, String value, CellStyle style) {
        Cell cell = row.createCell(cellNumber);
        cell.setCellValue(value + "");
        if (style != null) {
            cell.setCellStyle(style);
        }
    }

}
