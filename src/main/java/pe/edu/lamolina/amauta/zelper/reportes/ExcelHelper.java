package pe.edu.lamolina.amauta.zelper.reportes;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.util.CellReference;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;

@Slf4j
@Getter
@Setter
@NoArgsConstructor
public class ExcelHelper {

    public static final String CONTENT_TYPE_XLSX = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
    public static final String CONTENT_TYPE_XLS = "application/vnd.ms-excel";

    private static enum StyleEnum {
        // formatos para tipo de datos
        DATA_FORMAT_IMPORTE,
        DATA_FORMAT_FECHA,
        // Styles para celdas
        BORDES_BgGREEN_LETRABLANCA,
        LETRABOLD_SIZE14,
        LETRABOLD,
        BORDES,
        BORDES_LETRABOLD,
        NUMERICO_SOLO,
        MONTO_SOLO,
        MONTO_BORDES,
        MONTO_BORDES_LETRAROJA,
        FECHA_BORDES
    }

    private Sheet sheet;
    private Workbook workBook;

    private Map<String, DataFormat> mapDataFormat;
    private Map<String, CellStyle> mapCellStyle;

    public ExcelHelper(Sheet sheet, Workbook workBook) {
        this.sheet = sheet;
        this.workBook = workBook;
    }

    private Map<String, CellStyle> createMapStyle() {
        if (this.mapCellStyle == null) {
            this.mapCellStyle = new HashMap();
        }
        return this.mapCellStyle;
    }

    public static String getCellStringValue(Row row, int nroCol) {
        Cell cell = row.getCell(nroCol);
        String dato = cell.getStringCellValue();
        if (dato == null) {
            return null;
        }
        return dato;
    }

    public static Cell findCell(Sheet sheet, int nroRow, int nroCell) {
        Row row = sheet.getRow(nroRow);
        if (row == null) {
            row = sheet.createRow(nroRow);
        }

        Cell cell = row.getCell(nroCell);
        if (cell == null) {
            cell = row.createCell(nroCell);
        }

        return cell;
    }

    public static void formatCell(Sheet sheet, int nroRow, int nroCell, String formato) {
        Cell cell = findCell(sheet, nroRow, nroCell);
        CellStyle cellStyle = cell.getCellStyle();
        DataFormat format = sheet.getWorkbook().createDataFormat();
        cellStyle.setDataFormat(format.getFormat(formato));
    }

    public static void replaceVal(Sheet sheet, int nroRow, int nroCell, Date valor) {
        Cell cell = findCell(sheet, nroRow, nroCell);

        if (valor != null) {
            cell.setCellValue(valor);
        } else {
            cell.setBlank();
        }

        CellStyle cellStyle = sheet.getWorkbook().createCellStyle();
        DataFormat format = sheet.getWorkbook().createDataFormat();
        cellStyle.setDataFormat(format.getFormat("dd/MM/yyyy"));

    }

    public static void replaceVal(Sheet sheet, int nroRow, int nroCell, Integer valor) {
        Cell cell = findCell(sheet, nroRow, nroCell);
        cell.setBlank();
        if (valor != null) {
            cell.setCellValue(valor);
        }
    }

    public static void replaceVal(Sheet sheet, int nroRow, int nroCell, Long valor) {
        Cell cell = findCell(sheet, nroRow, nroCell);
        cell.setBlank();
        if (valor != null) {
            cell.setCellValue(valor);
        }
    }

    public static void replaceVal(Sheet sheet, int nroRow, int nroCell, String valor) {
        Cell cell = findCell(sheet, nroRow, nroCell);
        cell.setBlank();
        if (valor != null) {
            cell.setCellValue(valor);
        }
    }

    public static void replaceVal(Sheet sheet, int nroRow, int nroCell, Double valor) {
        Cell cell = findCell(sheet, nroRow, nroCell);
        cell.setBlank();
        if (valor != null) {
            cell.setCellValue(valor);
        }
    }

    public static void replaceVal(Sheet sheet, int nroRow, int nroCell, BigDecimal valor) {
        Cell cell = findCell(sheet, nroRow, nroCell);

        cell.setBlank();
        if (valor != null) {
            cell.setCellValue(valor.doubleValue());
        }

    }

    private String createTipo(Class clazz, HorizontalAlignment align) {
        String tipo;
        if (clazz == null) {
            tipo = "NULL";
        } else {
            tipo = clazz.getSimpleName();
        }

        if (align == null) {
            tipo += "|NULL";
        } else {
            tipo += "|" + align.name();
        }
        return tipo;
    }

    private DataFormat createDataFormat(String tipo) {
        if (this.mapDataFormat == null) {
            this.mapDataFormat = new HashMap();
        }

        DataFormat dataFormat = this.mapDataFormat.get(tipo);
        if (dataFormat == null) {
            dataFormat = workBook.createDataFormat();
            this.mapDataFormat.put(tipo, dataFormat);
        }

        return dataFormat;
    }

    private CellStyle createCellStyle(String tipo) {
        if (this.mapCellStyle == null) {
            this.mapCellStyle = new HashMap();
        }

        CellStyle cellStyle = this.mapCellStyle.get(tipo);
        if (cellStyle == null) {
            cellStyle = workBook.createCellStyle();
            this.mapCellStyle.put(tipo, cellStyle);
        }

        return cellStyle;
    }

    public Cell findCell(int nroRow, int nroCell) {
        Row row = sheet.getRow(nroRow);
        if (row == null) {
            row = sheet.createRow(nroRow);
        }

        Cell cell = row.getCell(nroCell);
        if (cell == null) {
            cell = row.createCell(nroCell);
        }

        return cell;
    }

    public CellStyle getCellStyle(int nroRow, int nroCell) {
        Cell cell = findCell(nroRow, nroCell);
        CellStyle cellStyle = cell.getCellStyle();

        String tipo = createTipo(null, null);
        CellStyle newCellStyle = this.createCellStyle(tipo);
        newCellStyle.cloneStyleFrom(cellStyle);
        return newCellStyle;
    }

    public void replaceStyle(int nroRow, int nroCell, CellStyle style) {
        Cell cell = findCell(nroRow, nroCell);
        cell.setCellStyle(style);
    }

    public void replaceVal(int nroRow, int nroCell, Integer valor) {
        Cell cell = findCell(nroRow, nroCell);
        cell.setBlank();
        if (valor != null) {
            cell.setCellValue(valor);
        }
    }

    public void replaceVal(int nroRow, int nroCell, Long valor) {
        Cell cell = findCell(nroRow, nroCell);
        cell.setBlank();
        if (valor != null) {
            cell.setCellValue(valor);
        }
    }

    public void replaceVal(int nroRow, int nroCell, String valor) {
        Cell cell = findCell(nroRow, nroCell);
        cell.setBlank();
        if (valor != null) {
            cell.setCellValue(valor);
        }
    }

    public void replaceVal(int nroRow, int nroCell, BigDecimal valor) {
        Cell cell = findCell(nroRow, nroCell);
        cell.setBlank();
        if (valor != null) {
            cell.setCellValue(valor.doubleValue());
        }
    }

    public void replaceVal(int nroRow, int nroCell, Date valor) {
        Cell cell = findCell(nroRow, nroCell);
        cell.setBlank();
        if (valor != null) {
            cell.setCellValue(valor);
        }
    }

    public void replaceVal(int nroRow, int nroCell, Date valor, HorizontalAlignment align) {
        Cell cell = findCell(nroRow, nroCell);
        cell.setCellValue(valor);

        String tipo = createTipo(Date.class, align);
        CellStyle cellStyle = this.createCellStyle(tipo);
        DataFormat dataFormat = this.createDataFormat(tipo);
        cellStyle.setDataFormat(dataFormat.getFormat("dd/mm/yyyy"));
        cell.setCellStyle(cellStyle);
    }

    public void replaceValHora(int nroRow, int nroCell, Date valor, HorizontalAlignment align) {
        Cell cell = findCell(nroRow, nroCell);
        cell.setCellValue(valor);

        String tipo = createTipo(Date.class, align) + "|HORA";
        CellStyle cellStyle = this.createCellStyle(tipo);
        DataFormat dataFormat = this.createDataFormat(tipo);
        cellStyle.setDataFormat(dataFormat.getFormat("HH:mm:ss"));
        cell.setCellStyle(cellStyle);
    }

    public void replaceVal(int nroRow, int nroCell, Date valor, String formato, CellStyle cellStyle) {
        Cell cell = findCell(nroRow, nroCell);
        if (valor != null) {
            replaceVal(nroRow, nroCell, valor);
        }

        String tipo = createTipo(Date.class, null);
        DataFormat df = this.createDataFormat(tipo);
        cell.setCellStyle(cellStyle);
        cellStyle.setDataFormat(df.getFormat(formato));
        cell.setCellStyle(cellStyle);
    }

    public void replaceVal(int nroRow, int nroCell, Date valor, String formato) {
        Cell cell = findCell(nroRow, nroCell);
        if (valor != null) {
            replaceVal(nroRow, nroCell, valor);
        }

        String tipo = createTipo(Date.class, null);
        DataFormat df = this.createDataFormat(tipo);
        CellStyle cellStyle = this.createCellStyle(tipo);
        cell.setCellStyle(cellStyle);
        cellStyle.setDataFormat(df.getFormat(formato));
        cell.setCellStyle(cellStyle);
    }

    public void replaceVal(int nroRow, int nroCell, Date valor, CellStyle cellStyle) {
        Cell cell = findCell(nroRow, nroCell);
        if (valor != null) {
            replaceVal(nroRow, nroCell, valor);
        }
        cell.setCellStyle(cellStyle);
    }

    public void replaceVal(int nroRow, int nroCell, Integer valor, String formato) {
        Cell cell = findCell(nroRow, nroCell);
        cell.setBlank();
        if (valor != null) {
            cell.setCellValue(valor);
        }

        String tipo = createTipo(Integer.class, null);
        DataFormat df = this.createDataFormat(tipo);
        CellStyle cellStyle = this.createCellStyle(tipo);
        cell.setCellStyle(cellStyle);
        cellStyle.setDataFormat(df.getFormat(formato));
        cell.setCellStyle(cellStyle);

    }

    public void replaceVal(int nroRow, int nroCell, Integer valor, CellStyle cellStyle) {
        Cell cell = findCell(nroRow, nroCell);
        cell.setBlank();
        if (valor != null) {
            cell.setCellValue(valor);
        }

        cell.setCellStyle(cellStyle);
    }

    public void replaceVal(int nroRow, int nroCell, Long valor, CellStyle cellStyle) {
        Cell cell = findCell(nroRow, nroCell);
        cell.setBlank();

        if (valor != null) {
            cell.setCellValue(valor);
        }
        cell.setCellStyle(cellStyle);
    }

    public void replaceVal(int nroRow, int nroCell, String valor, HorizontalAlignment align) {
        Cell cell = findCell(nroRow, nroCell);
        cell.setBlank();
        if (valor != null) {
            cell.setCellValue(valor);
        }

        String tipo = createTipo(String.class, align);
        CellStyle cs = this.createCellStyle(tipo);
        cs.setAlignment(align);
        cell.setCellStyle(cs);
    }

    public void replaceValWrapText(int nroRow, int nroCell, String valor) {
        Cell cell = findCell(nroRow, nroCell);
        cell.setBlank();
        if (valor != null) {
            cell.setCellValue(valor);
        }

        CellStyle cellStyle = getCellStyle(nroRow, nroCell);
        cellStyle.setWrapText(true);
        cell.setCellStyle(cellStyle);
    }

    public void replaceVal(int nroRow, int nroCell, String valor, String formato) {
        Cell cell = findCell(nroRow, nroCell);
        replaceVal(nroRow, nroCell, valor);

        CellStyle cellStyle = getCellStyle(nroRow, nroCell);
        String tipo = this.createTipo(String.class, null);
        DataFormat df = this.createDataFormat(tipo);
        cellStyle.setDataFormat(df.getFormat(formato));
        cell.setCellStyle(cellStyle);
    }

    public void replaceVal(int nroRow, int nroCell, String valor, CellStyle cellStyle) {
        Cell cell = findCell(nroRow, nroCell);
        replaceVal(nroRow, nroCell, valor);
        cell.setCellStyle(cellStyle);
    }

    public void replaceVal(int nroRow, int nroCell, Double valor) {
        Cell cell = findCell(nroRow, nroCell);
        cell.setCellValue(valor);
    }

    public void replaceVal(int nroRow, int nroCell, BigDecimal valor, String formato) {
        Cell cell = findCell(nroRow, nroCell);
        replaceVal(nroRow, nroCell, valor);

        CellStyle cellStyle = getCellStyle(nroRow, nroCell);
        String tipo = this.createTipo(BigDecimal.class, null);
        DataFormat df = this.createDataFormat(tipo);
        cellStyle.setDataFormat(df.getFormat(formato));

        cell.setCellStyle(cellStyle);
    }

    public void replaceVal(int nroRow, int nroCell, BigDecimal valor, CellStyle cellStyle) {
        Cell cell = findCell(nroRow, nroCell);
        replaceVal(nroRow, nroCell, valor);
        cell.setCellStyle(cellStyle);
    }

    public static void mergeCell(Sheet sheet, int firstRow, int lastRow, int firstCol, int lastCol) {

        sheet.addMergedRegion(new CellRangeAddress(firstRow, lastRow, firstCol, lastCol));
    }

    public static void createCell(Row row, int cellNumber, String value, CellStyle style) {
        Cell cell = row.createCell(cellNumber);
        cell.setCellValue(value + "");
        if (style != null) {
            cell.setCellStyle(style);
        }
    }

    public void setWidthColumn(int numberColumn, int width) {
        sheet.setColumnWidth(numberColumn, width);
    }

    public static String getColLetterByColNum(int col) {
        String columnLetter = "";
        try {
            columnLetter = CellReference.convertNumToColString(col);
        } catch (Exception e) {
        }
        try {
            columnLetter = org.apache.poi.ss.util.CellReference.convertNumToColString(col);
        } catch (Exception e) {
        }
        return columnLetter;
    }

    public CellStyle getConLetraBoldSize14(HorizontalAlignment posicion) {
        String tipo = StyleEnum.LETRABOLD_SIZE14.name();
        tipo += "_" + posicion.name();

        CellStyle cellStyle = this.createMapStyle().get(tipo);
        if (cellStyle != null) {
            return cellStyle;
        }

        Font font = this.workBook.createFont();
        font.setFontName("Arial");
        font.setFontHeightInPoints((short) 14);
        font.setBold(true);

        cellStyle = this.workBook.createCellStyle();
        cellStyle.setAlignment(posicion);
        cellStyle.setFont(font);

        this.mapCellStyle.put(tipo, cellStyle);

        return cellStyle;
    }

    public CellStyle getConLetraBold(HorizontalAlignment posicion) {
        String tipo = StyleEnum.LETRABOLD.name();
        tipo += "_" + posicion.name();

        CellStyle cellStyle = this.createMapStyle().get(tipo);
        if (cellStyle != null) {
            return cellStyle;
        }

        Font font = this.workBook.createFont();
        font.setFontName("Arial");
        font.setBold(true);

        cellStyle = this.workBook.createCellStyle();
        cellStyle.setAlignment(posicion);
        cellStyle.setFont(font);

        this.mapCellStyle.put(tipo, cellStyle);

        return cellStyle;
    }

    public CellStyle getConBordes(HorizontalAlignment posicion) {
        String tipo = StyleEnum.BORDES.name();
        tipo += "_" + posicion.name();

        CellStyle cellStyle = this.createMapStyle().get(tipo);
        if (cellStyle != null) {
            return cellStyle;
        }

        Font font = this.workBook.createFont();
        font.setFontName("Arial");

        cellStyle = this.workBook.createCellStyle();
        cellStyle.setAlignment(posicion);
        cellStyle.setFont(font);
        cellStyle.setBorderTop(BorderStyle.THIN);
        cellStyle.setBorderBottom(BorderStyle.THIN);
        cellStyle.setBorderRight(BorderStyle.THIN);
        cellStyle.setBorderLeft(BorderStyle.THIN);

        this.mapCellStyle.put(tipo, cellStyle);

        return cellStyle;
    }

    public CellStyle getBgGreenLetraBlanca(HorizontalAlignment posicion) {
        String tipo = StyleEnum.BORDES_BgGREEN_LETRABLANCA.name();
        tipo += "_" + posicion.name();

        CellStyle cellStyle = this.createMapStyle().get(tipo);
        if (cellStyle != null) {
            return cellStyle;
        }

        Font font = this.workBook.createFont();
        font.setFontName("Arial");
        font.setBold(true);
        font.setColor(IndexedColors.WHITE.getIndex());

        cellStyle = this.workBook.createCellStyle();
        cellStyle.setAlignment(posicion);
        cellStyle.setFont(font);
        cellStyle.setBorderTop(BorderStyle.THIN);
        cellStyle.setBorderBottom(BorderStyle.THIN);
        cellStyle.setBorderRight(BorderStyle.THIN);
        cellStyle.setBorderLeft(BorderStyle.THIN);

        cellStyle.setFillForegroundColor(IndexedColors.GREEN.getIndex());
        cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        this.mapCellStyle.put(tipo, cellStyle);

        return cellStyle;
    }

    public CellStyle getFechaConBordes(HorizontalAlignment posicion) {
        String tipo = StyleEnum.FECHA_BORDES.name();
        tipo += "_" + posicion.name();

        CellStyle cellStyle = this.createMapStyle().get(tipo);
        if (cellStyle != null) {
            return cellStyle;
        }

        Font font = this.workBook.createFont();
        font.setFontName("Arial");

        DataFormat format = this.createDataFormat(StyleEnum.DATA_FORMAT_FECHA.name());

        cellStyle = this.workBook.createCellStyle();
        cellStyle.setAlignment(posicion);
        cellStyle.setFont(font);
        cellStyle.setBorderTop(BorderStyle.THIN);
        cellStyle.setBorderBottom(BorderStyle.THIN);
        cellStyle.setBorderRight(BorderStyle.THIN);
        cellStyle.setBorderLeft(BorderStyle.THIN);
        cellStyle.setDataFormat(format.getFormat("dd/MM/yyyy"));

        this.mapCellStyle.put(tipo, cellStyle);

        return cellStyle;
    }

    public CellStyle getNumerico(HorizontalAlignment posicion) {
        String tipo = StyleEnum.NUMERICO_SOLO.name();
        tipo += "_" + posicion.name();

        CellStyle cellStyle = this.createMapStyle().get(tipo);
        if (cellStyle != null) {
            return cellStyle;
        }

        Font font = this.workBook.createFont();
        font.setFontName("Arial");

        DataFormat format = this.createDataFormat(StyleEnum.DATA_FORMAT_IMPORTE.name());

        cellStyle = this.workBook.createCellStyle();
        cellStyle.setAlignment(posicion);
        cellStyle.setFont(font);
        cellStyle.setDataFormat(format.getFormat("#,##0"));

        this.mapCellStyle.put(tipo, cellStyle);

        return cellStyle;
    }

    public CellStyle getImporte(HorizontalAlignment posicion) {
        String tipo = StyleEnum.MONTO_SOLO.name();
        tipo += "_" + posicion.name();

        CellStyle cellStyle = this.createMapStyle().get(tipo);
        if (cellStyle != null) {
            return cellStyle;
        }

        Font font = this.workBook.createFont();
        font.setFontName("Arial");

        DataFormat format = this.createDataFormat(StyleEnum.DATA_FORMAT_IMPORTE.name());

        cellStyle = this.workBook.createCellStyle();
        cellStyle.setAlignment(posicion);
        cellStyle.setFont(font);
        cellStyle.setDataFormat(format.getFormat("#,##0.00"));

        this.mapCellStyle.put(tipo, cellStyle);

        return cellStyle;
    }

    public CellStyle getImporteConBordes(HorizontalAlignment posicion) {
        String tipo = StyleEnum.MONTO_BORDES.name();
        tipo += "_" + posicion.name();

        CellStyle cellStyle = this.createMapStyle().get(tipo);
        if (cellStyle != null) {
            return cellStyle;
        }

        Font font = this.workBook.createFont();
        font.setFontName("Arial");

        DataFormat format = this.createDataFormat(StyleEnum.DATA_FORMAT_IMPORTE.name());

        cellStyle = this.workBook.createCellStyle();
        cellStyle.setAlignment(posicion);
        cellStyle.setFont(font);
        cellStyle.setBorderTop(BorderStyle.THIN);
        cellStyle.setBorderBottom(BorderStyle.THIN);
        cellStyle.setBorderRight(BorderStyle.THIN);
        cellStyle.setBorderLeft(BorderStyle.THIN);
        cellStyle.setDataFormat(format.getFormat("#,##0.00"));

        this.mapCellStyle.put(tipo, cellStyle);

        return cellStyle;
    }

}
