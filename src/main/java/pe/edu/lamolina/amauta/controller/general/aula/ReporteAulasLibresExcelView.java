package pe.edu.lamolina.amauta.controller.general.aula;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.view.AbstractView;
import pe.edu.lamolina.amauta.zelper.reportes.ExcelHelper;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.general.Aula;

@Component
public class ReporteAulasLibresExcelView extends AbstractView {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private static final String CONTENT_TYPE_XLSX = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";

    @Override
    protected void renderMergedOutputModel(Map<String, Object> map, HttpServletRequest request, HttpServletResponse response) throws Exception {

        Workbook workbook = new XSSFWorkbook();
        setContentType(CONTENT_TYPE_XLSX);
        this.buildExcelDocument(map, workbook, request, response);

    }

    protected void buildExcelDocument(Map<String, Object> model, Workbook wb, HttpServletRequest request, HttpServletResponse response) throws Exception {

        List<Aula> aulas = (List<Aula>) model.get("aulas");
        CicloAcademico ciclo = (CicloAcademico) model.get("ciclo");
        Integer totalHorasPosibles = (Integer) model.get("totalHorasPosibles");
        String filtroOficina = (String) model.get("filtroOficina");
        String filtroTipoAula = (String) model.get("filtroTipoAula");
        String filtroModulo = (String) model.get("filtroModulo");
        String filtroAula = (String) model.get("filtroAula");
        String usuarioGenerador = (String) model.get("usuarioGenerador");

        this.generateSheet(wb, aulas, ciclo, totalHorasPosibles, filtroOficina, filtroTipoAula, filtroModulo, filtroAula, usuarioGenerador);

        String fecha = new DateTime().toString("yyyyMMdd_HHmm");
        String nombreArchivo = "Reporte_Detalle_Aulas_" + (ciclo != null ? ciclo.getDescripcion().replace("/", "-") : "") + "_" + fecha + ".xlsx";
        response.setHeader("Content-Disposition", "attachment; filename=\"" + nombreArchivo + "\"");
        response.setContentType(getContentType());

        ServletOutputStream out = response.getOutputStream();
        out.flush();
        wb.write(out);
        out.flush();
    }

    private void generateSheet(Workbook wb, List<Aula> aulas, CicloAcademico ciclo, Integer totalHorasPosibles,
                                String filtroOficina, String filtroTipoAula, String filtroModulo, String filtroAula,
                                String usuarioGenerador) {
        Sheet sheet = wb.createSheet("Detalle de Aulas");
        this.createBody(wb, sheet, aulas, ciclo, totalHorasPosibles, filtroOficina, filtroTipoAula, filtroModulo, filtroAula, usuarioGenerador);
    }

    private CellStyle getStyleHeader(Workbook workBook) {
        Font font = workBook.createFont();
        font.setFontName("Arial");
        font.setBold(true);
        font.setColor(IndexedColors.WHITE.getIndex());

        CellStyle cell = workBook.createCellStyle();
        cell.setAlignment(HorizontalAlignment.CENTER);
        cell.setFont(font);
        cell.setBorderTop(BorderStyle.THIN);
        cell.setBorderBottom(BorderStyle.THIN);
        cell.setBorderRight(BorderStyle.THIN);
        cell.setBorderLeft(BorderStyle.THIN);
        cell.setFillForegroundColor(IndexedColors.DARK_GREEN.getIndex());
        cell.setFillPattern(FillPatternType.SOLID_FOREGROUND);

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

    private CellStyle getStylePorcentaje(Workbook workBook, int porcentaje) {
        Font font = workBook.createFont();
        font.setFontName("Arial");

        if (porcentaje >= 80) {
            font.setBold(true);
        }

        CellStyle cell = workBook.createCellStyle();
        cell.setAlignment(HorizontalAlignment.CENTER);
        cell.setFont(font);
        cell.setBorderTop(BorderStyle.THIN);
        cell.setBorderBottom(BorderStyle.THIN);
        cell.setBorderRight(BorderStyle.THIN);
        cell.setBorderLeft(BorderStyle.THIN);

        // Color de fondo según porcentaje
        if (porcentaje < 30) {
            cell.setFillForegroundColor(IndexedColors.LIGHT_GREEN.getIndex());
        } else if (porcentaje < 60) {
            cell.setFillForegroundColor(IndexedColors.LIGHT_BLUE.getIndex());
        } else if (porcentaje < 80) {
            cell.setFillForegroundColor(IndexedColors.LIGHT_YELLOW.getIndex());
        } else {
            cell.setFillForegroundColor(IndexedColors.CORAL.getIndex());
        }
        cell.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        return cell;
    }

    private CellStyle getStyleSubtitle(Workbook workBook) {
        Font font = workBook.createFont();
        font.setFontName("Arial");
        font.setBold(true);
        font.setFontHeightInPoints((short) 11);

        CellStyle cell = workBook.createCellStyle();
        cell.setFont(font);
        cell.setAlignment(HorizontalAlignment.LEFT);

        return cell;
    }

    private CellStyle getStyleInfoLabel(Workbook workBook) {
        Font font = workBook.createFont();
        font.setFontName("Arial");
        font.setBold(true);

        CellStyle cell = workBook.createCellStyle();
        cell.setFont(font);
        cell.setAlignment(HorizontalAlignment.LEFT);
        cell.setBorderTop(BorderStyle.THIN);
        cell.setBorderBottom(BorderStyle.THIN);
        cell.setBorderRight(BorderStyle.THIN);
        cell.setBorderLeft(BorderStyle.THIN);
        cell.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        cell.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        return cell;
    }

    private CellStyle getStyleInfoValue(Workbook workBook) {
        Font font = workBook.createFont();
        font.setFontName("Arial");

        CellStyle cell = workBook.createCellStyle();
        cell.setFont(font);
        cell.setAlignment(HorizontalAlignment.LEFT);
        cell.setBorderTop(BorderStyle.THIN);
        cell.setBorderBottom(BorderStyle.THIN);
        cell.setBorderRight(BorderStyle.THIN);
        cell.setBorderLeft(BorderStyle.THIN);

        return cell;
    }

    private void createBody(Workbook wb, Sheet sheet, List<Aula> aulas, CicloAcademico ciclo, Integer totalHorasPosibles,
                            String filtroOficina, String filtroTipoAula, String filtroModulo, String filtroAula,
                            String usuarioGenerador) {
        ExcelHelper excelUtil = new ExcelHelper(sheet, wb);

        CellStyle headerCell = getStyleHeader(wb);
        CellStyle estiloNumero = getStyleNumero(wb);
        CellStyle estiloGeneral = getStyleGeneral(wb);
        CellStyle estiloSubtitle = getStyleSubtitle(wb);
        CellStyle estiloInfoLabel = getStyleInfoLabel(wb);
        CellStyle estiloInfoValue = getStyleInfoValue(wb);

        int irow = 0;

        // ===== TÍTULO PRINCIPAL =====
        Font fontTitulo = wb.createFont();
        fontTitulo.setFontName("Arial");
        fontTitulo.setBold(true);
        fontTitulo.setFontHeightInPoints((short) 16);
        fontTitulo.setColor(IndexedColors.DARK_BLUE.getIndex());

        CellStyle estiloTitulo = wb.createCellStyle();
        estiloTitulo.setFont(fontTitulo);
        estiloTitulo.setAlignment(HorizontalAlignment.CENTER);
        estiloTitulo.setVerticalAlignment(VerticalAlignment.CENTER);

        String titulo = "REPORTE DE DETALLE DE AULAS";
        excelUtil.replaceVal(irow, 0, titulo, estiloTitulo);
        ExcelHelper.mergeCell(sheet, irow, irow, 0, 7);
        sheet.getRow(irow).setHeightInPoints(25);
        irow++;

        // Subtítulo con ciclo
        Font fontSubtitulo = wb.createFont();
        fontSubtitulo.setFontName("Arial");
        fontSubtitulo.setBold(true);
        fontSubtitulo.setFontHeightInPoints((short) 12);

        CellStyle estiloSubtitulo = wb.createCellStyle();
        estiloSubtitulo.setFont(fontSubtitulo);
        estiloSubtitulo.setAlignment(HorizontalAlignment.CENTER);

        String subtitulo = ciclo != null ? ciclo.getDescripcion() : "";
        excelUtil.replaceVal(irow, 0, subtitulo, estiloSubtitulo);
        ExcelHelper.mergeCell(sheet, irow, irow, 0, 7);
        irow++;
        irow++;

        // ===== INFORMACIÓN DEL REPORTE =====
        excelUtil.replaceVal(irow, 0, "INFORMACIÓN DEL REPORTE", estiloSubtitle);
        ExcelHelper.mergeCell(sheet, irow, irow, 0, 7);
        irow++;

        // Fecha de generación
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        String fechaGeneracion = sdf.format(new Date());
        excelUtil.replaceVal(irow, 0, "Fecha de Generación:", estiloInfoLabel);
        excelUtil.replaceVal(irow, 1, fechaGeneracion, estiloInfoValue);
        ExcelHelper.mergeCell(sheet, irow, irow, 1, 3);
        irow++;

        // Usuario generador
        if (usuarioGenerador != null && !usuarioGenerador.isEmpty()) {
            excelUtil.replaceVal(irow, 0, "Generado por:", estiloInfoLabel);
            excelUtil.replaceVal(irow, 1, usuarioGenerador, estiloInfoValue);
            ExcelHelper.mergeCell(sheet, irow, irow, 1, 3);
            irow++;
        }
        irow++;

        // ===== FILTROS APLICADOS =====
        boolean hayFiltros = filtroOficina != null || filtroTipoAula != null || filtroModulo != null || filtroAula != null;

        if (hayFiltros) {
            excelUtil.replaceVal(irow, 0, "FILTROS APLICADOS", estiloSubtitle);
            ExcelHelper.mergeCell(sheet, irow, irow, 0, 7);
            irow++;

            if (filtroOficina != null) {
                excelUtil.replaceVal(irow, 0, "Oficina:", estiloInfoLabel);
                excelUtil.replaceVal(irow, 1, filtroOficina, estiloInfoValue);
                ExcelHelper.mergeCell(sheet, irow, irow, 1, 3);
                irow++;
            }

            if (filtroTipoAula != null) {
                excelUtil.replaceVal(irow, 0, "Tipo de Aula:", estiloInfoLabel);
                excelUtil.replaceVal(irow, 1, filtroTipoAula, estiloInfoValue);
                ExcelHelper.mergeCell(sheet, irow, irow, 1, 3);
                irow++;
            }

            if (filtroModulo != null) {
                excelUtil.replaceVal(irow, 0, "Módulo:", estiloInfoLabel);
                excelUtil.replaceVal(irow, 1, filtroModulo, estiloInfoValue);
                ExcelHelper.mergeCell(sheet, irow, irow, 1, 3);
                irow++;
            }

            if (filtroAula != null) {
                excelUtil.replaceVal(irow, 0, "Aula:", estiloInfoLabel);
                excelUtil.replaceVal(irow, 1, filtroAula, estiloInfoValue);
                ExcelHelper.mergeCell(sheet, irow, irow, 1, 3);
                irow++;
            }
            irow++;
        } else {
            excelUtil.replaceVal(irow, 0, "FILTROS APLICADOS", estiloSubtitle);
            ExcelHelper.mergeCell(sheet, irow, irow, 0, 7);
            irow++;
            excelUtil.replaceVal(irow, 0, "Sin filtros aplicados (mostrando todas las aulas activas)", estiloInfoValue);
            ExcelHelper.mergeCell(sheet, irow, irow, 0, 7);
            irow++;
            irow++;
        }

        // ===== RESUMEN EJECUTIVO =====
        excelUtil.replaceVal(irow, 0, "RESUMEN EJECUTIVO", estiloSubtitle);
        ExcelHelper.mergeCell(sheet, irow, irow, 0, 7);
        irow++;

        // Calcular estadísticas
        int totalAulas = aulas.size();
        double promedioOcupacion = 0.0;
        Aula aulaMayorOcupacion = null;
        Aula aulaMenorOcupacion = null;
        int maxOcupacion = -1;
        int minOcupacion = Integer.MAX_VALUE;

        int contDisponible = 0;
        int contModerado = 0;
        int contOcupado = 0;
        int contMuyOcupado = 0;

        double sumaOcupacion = 0.0;

        for (Aula aula : aulas) {
            int horasOcupadas = aula.getHorariosAula() != null ? aula.getHorariosAula().size() : 0;
            int porcentajeOcupacion = 0;
            if (totalHorasPosibles != null && totalHorasPosibles > 0) {
                porcentajeOcupacion = Math.round((horasOcupadas * 100.0f) / totalHorasPosibles);
            }

            sumaOcupacion += porcentajeOcupacion;

            // Buscar mayor y menor ocupación
            if (porcentajeOcupacion > maxOcupacion) {
                maxOcupacion = porcentajeOcupacion;
                aulaMayorOcupacion = aula;
            }
            if (porcentajeOcupacion < minOcupacion) {
                minOcupacion = porcentajeOcupacion;
                aulaMenorOcupacion = aula;
            }

            // Contar por estado
            if (porcentajeOcupacion < 30) {
                contDisponible++;
            } else if (porcentajeOcupacion < 60) {
                contModerado++;
            } else if (porcentajeOcupacion < 80) {
                contOcupado++;
            } else {
                contMuyOcupado++;
            }
        }

        if (totalAulas > 0) {
            promedioOcupacion = sumaOcupacion / totalAulas;
        }

        // Mostrar estadísticas principales
        excelUtil.replaceVal(irow, 0, "Total de Aulas Analizadas:", estiloInfoLabel);
        excelUtil.replaceVal(irow, 1, totalAulas, estiloInfoValue);
        irow++;

        excelUtil.replaceVal(irow, 0, "Promedio de Ocupación:", estiloInfoLabel);
        excelUtil.replaceVal(irow, 1, String.format("%.2f%%", promedioOcupacion), estiloInfoValue);
        irow++;

        if (aulaMayorOcupacion != null) {
            excelUtil.replaceVal(irow, 0, "Aula con Mayor Ocupación:", estiloInfoLabel);
            String infoMayor = aulaMayorOcupacion.getCodigo() + " (" + maxOcupacion + "%)";
            excelUtil.replaceVal(irow, 1, infoMayor, estiloInfoValue);
            ExcelHelper.mergeCell(sheet, irow, irow, 1, 3);
            irow++;
        }

        if (aulaMenorOcupacion != null) {
            excelUtil.replaceVal(irow, 0, "Aula con Menor Ocupación:", estiloInfoLabel);
            String infoMenor = aulaMenorOcupacion.getCodigo() + " (" + minOcupacion + "%)";
            excelUtil.replaceVal(irow, 1, infoMenor, estiloInfoValue);
            ExcelHelper.mergeCell(sheet, irow, irow, 1, 3);
            irow++;
        }
        irow++;

        // ===== DISTRIBUCIÓN POR ESTADO =====
        excelUtil.replaceVal(irow, 0, "DISTRIBUCIÓN POR ESTADO", estiloSubtitle);
        ExcelHelper.mergeCell(sheet, irow, irow, 0, 7);
        irow++;

        // Cabecera de distribución
        CellStyle estiloDistHeader = wb.createCellStyle();
        Font fontDistHeader = wb.createFont();
        fontDistHeader.setFontName("Arial");
        fontDistHeader.setBold(true);
        estiloDistHeader.setFont(fontDistHeader);
        estiloDistHeader.setAlignment(HorizontalAlignment.CENTER);
        estiloDistHeader.setBorderTop(BorderStyle.THIN);
        estiloDistHeader.setBorderBottom(BorderStyle.THIN);
        estiloDistHeader.setBorderRight(BorderStyle.THIN);
        estiloDistHeader.setBorderLeft(BorderStyle.THIN);
        estiloDistHeader.setFillForegroundColor(IndexedColors.GREY_40_PERCENT.getIndex());
        estiloDistHeader.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        excelUtil.replaceVal(irow, 0, "Estado", estiloDistHeader);
        excelUtil.replaceVal(irow, 1, "Cantidad", estiloDistHeader);
        excelUtil.replaceVal(irow, 2, "Porcentaje", estiloDistHeader);
        irow++;

        // Disponible (Verde)
        CellStyle estiloDisponible = getStylePorcentaje(wb, 15);
        excelUtil.replaceVal(irow, 0, "Disponible (< 30%)", estiloDisponible);
        excelUtil.replaceVal(irow, 1, contDisponible, estiloNumero);
        String porcDisponible = totalAulas > 0 ? String.format("%.1f%%", (contDisponible * 100.0 / totalAulas)) : "0%";
        excelUtil.replaceVal(irow, 2, porcDisponible, estiloNumero);
        irow++;

        // Moderado (Azul)
        CellStyle estiloModerado = getStylePorcentaje(wb, 45);
        excelUtil.replaceVal(irow, 0, "Moderado (30% - 60%)", estiloModerado);
        excelUtil.replaceVal(irow, 1, contModerado, estiloNumero);
        String porcModerado = totalAulas > 0 ? String.format("%.1f%%", (contModerado * 100.0 / totalAulas)) : "0%";
        excelUtil.replaceVal(irow, 2, porcModerado, estiloNumero);
        irow++;

        // Ocupado (Amarillo)
        CellStyle estiloOcupado = getStylePorcentaje(wb, 70);
        excelUtil.replaceVal(irow, 0, "Ocupado (60% - 80%)", estiloOcupado);
        excelUtil.replaceVal(irow, 1, contOcupado, estiloNumero);
        String porcOcupado = totalAulas > 0 ? String.format("%.1f%%", (contOcupado * 100.0 / totalAulas)) : "0%";
        excelUtil.replaceVal(irow, 2, porcOcupado, estiloNumero);
        irow++;

        // Muy Ocupado (Coral)
        CellStyle estiloMuyOcupado = getStylePorcentaje(wb, 90);
        excelUtil.replaceVal(irow, 0, "Muy Ocupado (>= 80%)", estiloMuyOcupado);
        excelUtil.replaceVal(irow, 1, contMuyOcupado, estiloNumero);
        String porcMuyOcupado = totalAulas > 0 ? String.format("%.1f%%", (contMuyOcupado * 100.0 / totalAulas)) : "0%";
        excelUtil.replaceVal(irow, 2, porcMuyOcupado, estiloNumero);
        irow++;
        irow++;
        irow++;

        // ===== DETALLE DE AULAS =====
        excelUtil.replaceVal(irow, 0, "DETALLE DE TODAS LAS AULAS", estiloSubtitle);
        ExcelHelper.mergeCell(sheet, irow, irow, 0, 7);
        irow++;
        irow++;

        // Encabezados
        int column = 0;
        excelUtil.replaceVal(irow, column++, "#", headerCell);
        excelUtil.replaceVal(irow, column++, "CÓDIGO", headerCell);
        excelUtil.replaceVal(irow, column++, "NOMBRE", headerCell);
        excelUtil.replaceVal(irow, column++, "TIPO", headerCell);
        excelUtil.replaceVal(irow, column++, "CAPACIDAD", headerCell);
        excelUtil.replaceVal(irow, column++, "HORAS OCUPADAS", headerCell);
        excelUtil.replaceVal(irow, column++, "% OCUPACIÓN", headerCell);
        excelUtil.replaceVal(irow, column++, "ESTADO", headerCell);

        // Ajustar ancho de columnas
        sheet.setColumnWidth(0, 2000);  // #
        sheet.setColumnWidth(1, 4000);  // Código
        sheet.setColumnWidth(2, 8000);  // Nombre
        sheet.setColumnWidth(3, 6000);  // Tipo
        sheet.setColumnWidth(4, 3500);  // Capacidad
        sheet.setColumnWidth(5, 5000);  // Horas Ocupadas
        sheet.setColumnWidth(6, 4000);  // % Ocupación
        sheet.setColumnWidth(7, 4500);  // Estado

        column = 0;
        irow++;
        int num = 1;

        for (Aula aula : aulas) {
            // Calcular estadísticas del aula
            int horasOcupadas = 0;
            if (aula.getHorariosAula() != null) {
                horasOcupadas = aula.getHorariosAula().size();
            }

            int porcentajeOcupacion = 0;
            if (totalHorasPosibles != null && totalHorasPosibles > 0) {
                porcentajeOcupacion = Math.round((horasOcupadas * 100.0f) / totalHorasPosibles);
            }

            String estadoTexto = "";
            if (porcentajeOcupacion < 30) {
                estadoTexto = "Disponible";
            } else if (porcentajeOcupacion < 60) {
                estadoTexto = "Moderado";
            } else if (porcentajeOcupacion < 80) {
                estadoTexto = "Ocupado";
            } else {
                estadoTexto = "Muy Ocupado";
            }

            // Llenar datos
            excelUtil.replaceVal(irow, column++, num++, estiloNumero);
            excelUtil.replaceVal(irow, column++, aula.getCodigo() != null ? aula.getCodigo() : "", estiloGeneral);
            excelUtil.replaceVal(irow, column++, aula.getNombre() != null ? aula.getNombre() : "", estiloGeneral);
            excelUtil.replaceVal(irow, column++,
                aula.getTipoAula() != null ? aula.getTipoAula().getNombre() : "", estiloGeneral);
            excelUtil.replaceVal(irow, column++,
                aula.getCapacidadAula() != null ? aula.getCapacidadAula() : 0, estiloNumero);
            excelUtil.replaceVal(irow, column++, horasOcupadas, estiloNumero);

            CellStyle estiloPorcentaje = getStylePorcentaje(wb, porcentajeOcupacion);
            excelUtil.replaceVal(irow, column++, porcentajeOcupacion + "%", estiloPorcentaje);
            excelUtil.replaceVal(irow, column++, estadoTexto, estiloGeneral);

            irow++;
            column = 0;
        }

        // Habilitar autofiltro en el detalle
        if (aulas.size() > 0) {
            sheet.setAutoFilter(new org.apache.poi.ss.util.CellRangeAddress(
                irow - aulas.size() - 1,
                irow - 1,
                0,
                7
            ));
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
