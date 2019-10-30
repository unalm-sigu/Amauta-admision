package pe.edu.lamolina.pivot.controller.rolexamen.reportes;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pe.albatross.zelpers.file.excel.AbstractPOIExcelView;
import pe.albatross.zelpers.file.excel.ExcelStyles;
import pe.edu.lamolina.model.rolexamen.RolExamenes;
import pe.edu.lamolina.pivot.dao.academico.DocenteSeccionDAO;
import pe.edu.lamolina.pivot.dao.academico.GrupoSeccionDAO;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.StringTokenizer;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.openxml4j.util.ZipSecureFile;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellUtil;
import pe.albatross.zelpers.file.excel.ExcelHelper;
import pe.albatross.zelpers.miscelanea.NumberFormat;
import pe.edu.lamolina.model.enums.EventoAcademicoEnum;
import pe.edu.lamolina.model.general.Aula;
import pe.edu.lamolina.model.rolexamen.CursoMasivoExamen;
import pe.edu.lamolina.model.rolexamen.SeccionGrupoEspecial;
import pe.edu.lamolina.model.rolexamen.SeccionGrupoRegular;

@Component
public class RolExamenReporteAulasView extends AbstractPOIExcelView {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public final static Integer FLAG_ACTIVO = 1;
    public final static Integer ROW_DIAS = 4;
    public final static Integer ROW_HORAS = 5;
    public final static Integer COL_INICIO_MATRIZ = 3;
    public final static List<IndexedColors> COLORES = Arrays.asList(IndexedColors.LIGHT_YELLOW, IndexedColors.LIGHT_GREEN, IndexedColors.LIGHT_TURQUOISE, IndexedColors.LIGHT_CORNFLOWER_BLUE);
    public final static Short border = CellStyle.BORDER_THIN;

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
        ZipSecureFile.setMinInflateRatio(0);
        RolExamenes rol = (RolExamenes) model.get("rol");
        List<Aula> modulos = (List<Aula>) model.get("modulos");
        Map<Aula, List<Aula>> aulasPorModulo = (Map<Aula, List<Aula>>) model.get("aulasPorModulo");
        List<Date> dias = (List<Date>) model.get("dias");
        Map<Date, List<Integer>> horasPorDia = (Map<Date, List<Integer>>) model.get("horasPorDia");
        Map<Aula, Map<Date, Map<Integer, List>>> mapOcupacion = (Map<Aula, Map<Date, Map<Integer, List>>>) model.get("mapOcupacion");

        Sheet sheet = workbook.createSheet("REPORTE");
        this.createSheet(workbook, sheet, rol, modulos, aulasPorModulo, dias, horasPorDia, mapOcupacion);

        String fechaRep = " - " + new DateTime().toString("dd/MM/yyyy H:mm");

        String nombreReporte = "RolExamenes - Aulas";

        response.setHeader("Content-Disposition", "attachment; filename=\"" + nombreReporte + fechaRep + ".xlsx\"");
    }

    private void createHeader(String headers, Integer row, Font headerFont, Sheet sheet, Workbook wb) {
        StringTokenizer st = new StringTokenizer(headers, "|");

        Integer col = 0;

        while (st.hasMoreTokens()) {
            String token = st.nextToken();
            this.createCell(row, col, token, sheet, null);
            this.setHeaderStyle(row, col, sheet, wb, headerFont);
            col++;
        }
    }

    private void createSheet(
            Workbook wb,
            Sheet sheet,
            RolExamenes rol,
            List<Aula> modulos,
            Map<Aula, List<Aula>> aulasPorModulo,
            List<Date> fechas,
            Map<Date, List<Integer>> horasPorDia,
            Map<Aula, Map<Date, Map<Integer, List>>> mapOcupacion) {

        SimpleDateFormat sdf = new SimpleDateFormat("EEEE dd", new Locale("es", "ES"));
        SimpleDateFormat sdf2 = new SimpleDateFormat("EEEE dd 'de' MMMM 'del' yyyy", new Locale("es", "ES"));

        String titulo;
        if (rol.getEventoCicloAcademico().getEventoAcademico().getTipo().equals(EventoAcademicoEnum.EXAMEN_PARC.name())) {
            titulo = "ROL DE EXAMENES PARCIALES";
        } else {
            titulo = "ROL DE EXAMENES FINALES";
        }

        Font headerFont = wb.createFont();
        headerFont.setFontHeightInPoints((short) 11);
        headerFont.setFontName("Arial");
        headerFont.setColor(IndexedColors.BLACK.getIndex());
        headerFont.setBold(true);
        headerFont.setItalic(true);

        String fechaInicio = sdf.format(fechas.get(0));
        String fechaFin = sdf2.format(fechas.get(fechas.size() - 1));

        titulo = String.format("%s %s", titulo, rol.getEventoCicloAcademico().getCicloAcademico().getDescripcion());
        String subtitulo = String.format("Del %s al %s", fechaInicio, fechaFin);

        ExcelHelper.replaceVal(sheet, 1, 4, titulo);
        ExcelHelper.replaceVal(sheet, 2, 4, subtitulo);

        Integer currentColumn = COL_INICIO_MATRIZ;

        Row row = sheet.createRow(ROW_DIAS);

        for (Date fecha : fechas) {
            Integer cantidadHoras = horasPorDia.get(fecha).size();
            CellRangeAddress mergedRegion = new CellRangeAddress(ROW_DIAS, ROW_DIAS, currentColumn, currentColumn + cantidadHoras - 1);
            sheet.addMergedRegion(mergedRegion);
            Cell cell = row.createCell(currentColumn);
            cell.setCellValue(StringUtils.capitalize(sdf.format(fecha)));
            CellStyle style = (ExcelStyles.getStyleHeader(wb));
            style.setAlignment(CellStyle.ALIGN_LEFT);
            cell.setCellStyle(style);
            currentColumn = currentColumn + cantidadHoras;
        }

        Integer cols = this.buildHeader(fechas, horasPorDia, ROW_HORAS, headerFont, sheet, wb);

        Integer rowNum = ROW_HORAS + 1;

        for (Aula modulo : modulos) {
            List<Aula> aulas = aulasPorModulo.get(modulo);

            CellRangeAddress mergedRegion = new CellRangeAddress(rowNum, rowNum + aulas.size() - 1, 0, 0);
            sheet.addMergedRegion(mergedRegion);

            ExcelHelper.replaceVal(sheet, rowNum, 0, modulo.getNombre());
            setBorder(rowNum, 0, sheet, wb);

            CellUtil.setCellStyleProperty(sheet.getRow(rowNum).getCell(0), wb, CellUtil.ALIGNMENT, CellStyle.ALIGN_CENTER);
            CellUtil.setCellStyleProperty(sheet.getRow(rowNum).getCell(0), wb, CellUtil.VERTICAL_ALIGNMENT, CellStyle.ALIGN_CENTER);

            for (Aula aula : aulas) {
                ExcelHelper.replaceVal(sheet, rowNum, 1, aula.getCodigo());
                CellUtil.setCellStyleProperty(sheet.getRow(rowNum).getCell(1), wb, CellUtil.ALIGNMENT, CellStyle.ALIGN_CENTER);

                if (aula.getAforo() != null) {
                    ExcelHelper.replaceVal(sheet, rowNum, 2, aula.getAforo());
                    CellUtil.setCellStyleProperty(sheet.getRow(rowNum).getCell(2), wb, CellUtil.ALIGNMENT, CellStyle.ALIGN_CENTER);
                }

                setBorder(rowNum, 1, sheet, wb);
                setBorder(rowNum, 2, sheet, wb);

                Integer col = 3;
                Integer numeroDia = 0;
                for (Date fecha : fechas) {
                    List<Integer> horas = horasPorDia.get(fecha);
                    for (Integer nroHora : horas) {
                        List ocupantes = getOcupantes(aula, fecha, nroHora, mapOcupacion);
                        if (ocupantes.size() > 1) {
                            ExcelHelper.replaceVal(sheet, rowNum, col, ocupantes.size());
                            CellUtil.setCellStyleProperty(sheet.getRow(rowNum).getCell(col), wb, CellUtil.ALIGNMENT, CellStyle.ALIGN_CENTER);
                        } else if (ocupantes.size() == 1) {
                            Object obj = ocupantes.get(0);
                            if (obj instanceof CursoMasivoExamen) {
                                ExcelHelper.replaceVal(sheet, rowNum, col, "M");
                                CellUtil.setCellStyleProperty(sheet.getRow(rowNum).getCell(col), wb, CellUtil.ALIGNMENT, CellStyle.ALIGN_CENTER);
                            } else if (obj instanceof SeccionGrupoEspecial) {
                                ExcelHelper.replaceVal(sheet, rowNum, col, "E");
                                CellUtil.setCellStyleProperty(sheet.getRow(rowNum).getCell(col), wb, CellUtil.ALIGNMENT, CellStyle.ALIGN_CENTER);
                            } else if (obj instanceof SeccionGrupoRegular) {
                                ExcelHelper.replaceVal(sheet, rowNum, col, "R");
                                CellUtil.setCellStyleProperty(sheet.getRow(rowNum).getCell(col), wb, CellUtil.ALIGNMENT, CellStyle.ALIGN_CENTER);
                            } else {
                                ExcelHelper.replaceVal(sheet, rowNum, col, "");
                            }
                        } else {
                            ExcelHelper.replaceVal(sheet, rowNum, col, "");
                        }

                        Integer indexColor = numeroDia % COLORES.size();
                        IndexedColors color = COLORES.get(indexColor);
                        CellUtil.setCellStyleProperty(sheet.getRow(rowNum).getCell(col), wb, CellUtil.FILL_FOREGROUND_COLOR, color.getIndex());
                        CellUtil.setCellStyleProperty(sheet.getRow(rowNum).getCell(col), wb, CellUtil.FILL_PATTERN, CellStyle.SOLID_FOREGROUND);
                        setBorder(rowNum, col, sheet, wb);
                        col++;
                    }
                    numeroDia++;
                }
                rowNum++;
            }

        }

        sheet.autoSizeColumn(0);
        sheet.autoSizeColumn(1);
        sheet.autoSizeColumn(2);

        for (int i = 3; i < cols + 3; i++) {
            sheet.setColumnWidth(i, 1024);
        }

    }

    private List getOcupantes(Aula aula, Date fecha, Integer nroHora, Map<Aula, Map<Date, Map<Integer, List>>> mapOcupacion) {
        if (!mapOcupacion.containsKey(aula)) {
            return new ArrayList();
        }
        if (!mapOcupacion.get(aula).containsKey(fecha)) {
            return new ArrayList();
        }
        if (!mapOcupacion.get(aula).get(fecha).containsKey(nroHora)) {
            return new ArrayList();
        }
        return mapOcupacion.get(aula).get(fecha).get(nroHora);
    }

    private void setBorder(Integer row, Integer col, Sheet sheet, Workbook wb) {
        if (sheet.getRow(row) == null) {
            sheet.createRow(row);
        }

        if (sheet.getRow(row).getCell(col) == null) {
            sheet.getRow(row).createCell(col);
        }

        CellUtil.setCellStyleProperty(sheet.getRow(row).getCell(col), wb, CellUtil.BORDER_BOTTOM, border);
        CellUtil.setCellStyleProperty(sheet.getRow(row).getCell(col), wb, CellUtil.BORDER_LEFT, border);
        CellUtil.setCellStyleProperty(sheet.getRow(row).getCell(col), wb, CellUtil.BORDER_RIGHT, border);
        CellUtil.setCellStyleProperty(sheet.getRow(row).getCell(col), wb, CellUtil.BORDER_TOP, border);
    }

    private void setHeaderStyle(Integer row, Integer col, Sheet sheet, Workbook wb, Font font) {
        if (sheet.getRow(row) == null) {
            sheet.createRow(row);
        }

        if (sheet.getRow(row).getCell(col) == null) {
            sheet.getRow(row).createCell(col);
        }

        this.setBorder(row, col, sheet, wb);
        Cell cell = sheet.getRow(row).getCell(col);
        cell.getCellStyle().setFont(font);
        CellUtil.setCellStyleProperty(sheet.getRow(row).getCell(col), wb, CellUtil.FILL_FOREGROUND_COLOR, IndexedColors.GREY_25_PERCENT.getIndex());
        CellUtil.setCellStyleProperty(sheet.getRow(row).getCell(col), wb, CellUtil.FILL_PATTERN, CellStyle.SOLID_FOREGROUND);
    }

    private Integer buildHeader(List<Date> dias, Map<Date, List<Integer>> horasPorDia, Integer row, Font headerFont, Sheet sheet, Workbook wb) {
        Integer cont = 0;
        StringBuilder sb = new StringBuilder();
        sb = sb.append("Modulo|Aula|Cap");
        for (Date dia : dias) {
            List<Integer> horas = horasPorDia.get(dia);
            for (Integer hora : horas) {
                sb = sb.append("|");
                sb = sb.append(NumberFormat.codigo(hora, 2));
                cont++;
            }
        }
        createHeader(sb.toString(), row, headerFont, sheet, wb);
        return cont;
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
