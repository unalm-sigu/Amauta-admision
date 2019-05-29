package pe.edu.lamolina.pivot.controller.programacionhorarios.boletinacademico;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pe.albatross.zelpers.file.excel.AbstractPOIExcelView;
import pe.albatross.zelpers.file.excel.ExcelHelper;
import pe.albatross.zelpers.file.excel.ExcelStyles;
import pe.albatross.zelpers.miscelanea.ObjectUtil;
import pe.edu.lamolina.model.academico.AnexoBoletin;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Curso;
import pe.edu.lamolina.model.academico.DocenteSeccion;
import pe.edu.lamolina.model.academico.GrupoSeccion;
import pe.edu.lamolina.model.academico.Seccion;
import pe.edu.lamolina.model.general.Aula;

@Component
public class BoletinAcademicoExcelView extends AbstractPOIExcelView {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    String SECCION = "Sección";

    @Autowired
    BoletinAcademicoService service;

    @Override
    protected Workbook createWorkbook() {
        return new SXSSFWorkbook();
    }

    @Override
    protected void buildExcelDocument(Map<String, Object> map, Workbook workBook, HttpServletRequest request, HttpServletResponse response) throws Exception {
        String fechaRep = " - " + new DateTime().toString("dd/MM/yyyy H:mm");

        CicloAcademico ciclo = service.findCicloAcademicoActivo();
        List<AnexoBoletin> anexosBoletin = service.allAnexosByCiclo(ciclo);
        for (AnexoBoletin anexoBoletin : anexosBoletin) {
            Sheet sheet = workBook.createSheet(anexoBoletin.getNombre());
            this.createSheet(workBook, anexoBoletin, sheet);
        }

        String nombreReporte = "Boletin";
        response.setHeader("Content-Disposition", "attachment; filename=\"" + nombreReporte + fechaRep + ".xlsx\"");
    }

    private void createSheet(Workbook workBook, AnexoBoletin anexoBoletin, Sheet sheet) {

        CicloAcademico ciclo = service.findCicloAcademicoActivo();

        int totalColumns = 11;
        int rowIndice = 0;

//        logger.debug("Anexo Boletin Padre {} id {}", anexoBoletin.getNombre(), anexoBoletin.getId());
//        Row row = sheet.createRow(rowIndice++);
//        this.createHeader(workBook, sheet, row, 0, "Anexo " + anexoBoletin.getNombre());
//        sheet.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 0, 10));
        for (AnexoBoletin anexosBoletinHijo : anexoBoletin.getAnexosBoletinHijos()) {
            logger.debug("          Anexo Boletin Hijo {} id {}", anexosBoletinHijo.getNombre(), anexosBoletinHijo.getId());
            Row row = sheet.createRow(rowIndice++);
            this.createHeader1(workBook, sheet, row, 0, "Anexo " + anexosBoletinHijo.getNombre());
            sheet.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 0, totalColumns));
            //
            row = sheet.createRow(rowIndice++);
            int col = 0;
            this.createHeader(workBook, sheet, row, col++, "CÓDIGO");
            this.createHeader(workBook, sheet, row, col++, "CURSO");
            this.createHeader(workBook, sheet, row, col++, "TEORÍA");
            this.createHeader(workBook, sheet, row, col++, "AULA");
            this.createHeader(workBook, sheet, row, col++, SECCION.toUpperCase());
            this.createHeader(workBook, sheet, row, col++, "PRACT.");
            this.createHeader(workBook, sheet, row, col++, "AULA");
            this.createHeader(workBook, sheet, row, col++, "PROFESOR");
            this.createHeader(workBook, sheet, row, col++, "%");
            this.createHeader(workBook, sheet, row, col++, "HORARIO");
            this.createHeader(workBook, sheet, row, col++, "PERIODO");
            this.createHeader(workBook, sheet, row, col, "VAC");
            for (Curso curso : anexosBoletinHijo.getCursos()) {
                logger.debug("                     Curso {}", curso.getNombre());
                rowIndice++;
                row = sheet.createRow(rowIndice++);
                this.createHeader3(workBook, sheet, row, 0, curso.getCodigo() + " " + curso.getNombre());
                sheet.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 0, totalColumns));
                for (GrupoSeccion grupoSeccion : curso.getGrupoSeccion()) {
                    int indiceSeccion = 0;
                    for (Seccion seccion : grupoSeccion.getSecciones()) {
                        String horario = seccion.getGrupoHoras().getCodigo();
                        Optional<Aula> aulaOpt = Optional.ofNullable(seccion.getAula());
                        String aula = aulaOpt.isPresent() ? aulaOpt.get().getCodigo() : "";
                        row = sheet.createRow(rowIndice++);
                        logger.debug("                      Seccion {} tipo {}", seccion.getCodigo2(), seccion.getTipoSeccion());
                        col = 0;
                        String tpc = !seccion.isTipoSeccionPCUR() ? curso.getTpc() : "";

                        ExcelHelper.replaceVal(sheet, row.getRowNum(), col++, tpc);
                        sheet.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 0, 1));
                        col++;
                        boolean isTeoria = seccion.isTipoSeccionTCUR() || seccion.isTipoSeccionTEO();

                        ExcelHelper.replaceVal(sheet, row.getRowNum(), col++, isTeoria ? horario : "");
                        ExcelHelper.replaceVal(sheet, row.getRowNum(), col++, isTeoria ? aula : "");
                        ExcelHelper.replaceVal(sheet, row.getRowNum(), col++, seccion.getCodigo2());
                        boolean isPractica = seccion.isTipoSeccionPCUR() || seccion.isTipoSeccionPRA();
                        ExcelHelper.replaceVal(sheet, row.getRowNum(), col++, isPractica ? horario : "");
                        ExcelHelper.replaceVal(sheet, row.getRowNum(), col++, isPractica ? aula : "");
                        String docentesSeccion = "";
                        String porcentaje = "";
                        String fechas = "";
                        for (DocenteSeccion doc : seccion.getDocenteSeccion()) {
                            docentesSeccion += ObjectUtil.getParentTree(doc, "docente.codigo") + "  " + ObjectUtil.getParentTree(doc, "docente.persona.nomPaternoMat") + "\n";
                            porcentaje += ObjectUtil.getParentTree(doc, "porcentajeCarga") + "\n";
                            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", new Locale("es", "ES"));

                            String fechaIni = "";
                            String fechaFin = "";
                            if (ObjectUtil.getParentTree(doc, "fechaInicio") != null) {
                                fechaIni = sdf.format(doc.getFechaInicio());
                            }
                            if (ObjectUtil.getParentTree(doc, "fechaFin") != null) {
                                fechaFin = sdf.format(doc.getFechaFin());
                            }
                            fechas += String.format("%s al %s", fechaIni, fechaFin) + "\n";;
                        }

//                            this.createBody(workBook, row, col++, "");//profesores
//                            this.createBody(workBook, row, col++, "");//porcentajes
                        CellStyle cs = workBook.createCellStyle();
                        cs.setWrapText(true);

                        XSSFRichTextString richStringDocentes = new XSSFRichTextString(docentesSeccion);
                        Cell docentesCell = ExcelHelper.findCell(sheet, row.getRowNum(), col++);
                        docentesCell.setCellValue(richStringDocentes);
                        docentesCell.setCellStyle(cs);

                        XSSFRichTextString richStringPorcentajes = new XSSFRichTextString(porcentaje);
                        Cell procentajeCell = ExcelHelper.findCell(sheet, row.getRowNum(), col++);
                        procentajeCell.setCellValue(richStringPorcentajes);
                        procentajeCell.setCellStyle(cs);

                        ExcelHelper.replaceVal(sheet, row.getRowNum(), col++, seccion.getHorarioTexto());

                        XSSFRichTextString richStringFechas = new XSSFRichTextString(fechas);
                        Cell fechasCell = ExcelHelper.findCell(sheet, row.getRowNum(), col++);
                        fechasCell.setCellValue(richStringFechas);
                        fechasCell.setCellStyle(cs);

                        //    ExcelHelper.replaceVal(sheet, row.getRowNum(), col++, seccion.getHorarioTexto());
                        ExcelHelper.replaceVal(sheet, row.getRowNum(), col, seccion.getVacantes());
                        if (indiceSeccion == (grupoSeccion.getSecciones().size() - 1)) {
                            for (int i = row.getFirstCellNum(); i < row.getLastCellNum(); i++) {
                                Cell cell = row.getCell(i);
                                if (cell == null) {
                                    cell = row.createCell(i);
                                }
                                CellStyle cellStyle = cell.getCellStyle();
                                cellStyle.setBorderBottom((short) 1);
                                if (cell.getColumnIndex() == (row.getLastCellNum() - 1)) {
                                    cellStyle.setBorderRight((short) 1);
                                }
                                cell.setCellStyle(cs);
                            }
                        }
                        indiceSeccion++;
                    }
                }
            }
        }

        for (int i = 0; i <= 10; i++) {
            // sheet.setColumnWidth(i, 1024);
            sheet.autoSizeColumn(i);
        }

    }

    private void createHeader1(Workbook wb, Sheet sheet, Row row, int column, String title) {
        // Row row = sheet.createRow(0);
        Font fontTitle = wb.createFont();
        fontTitle.setFontName("Arial");
        fontTitle.setBoldweight(Font.BOLDWEIGHT_BOLD);
        fontTitle.setColor(IndexedColors.BLACK.getIndex());

        CellStyle cellStyle = ExcelStyles.getStyleHeader(wb);
        cellStyle.setFillForegroundColor(IndexedColors.WHITE.getIndex());
        cellStyle.setFont(fontTitle);

        ExcelHelper.replaceVal(sheet, row.getRowNum(), column, title.toUpperCase());
        ExcelHelper.findCell(sheet, row.getRowNum(), column).setCellStyle(cellStyle);
    }

    private void createHeader(Workbook wb, Sheet sheet, Row row, int column, String title) {
        // Row row = sheet.createRow(0);
        CellStyle fontTitle = ExcelStyles.getStyleHeader(wb);
//        ExcelHelper.createCell(row, column, title, fontTitle);
        //  CellRangeAddress mergedRegion = new CellRangeAddress(0, 0, 0, 10);
        //   sheet.addMergedRegion(mergedRegion);
        ExcelHelper.replaceVal(sheet, row.getRowNum(), column, title);
        ExcelHelper.findCell(sheet, row.getRowNum(), column).setCellStyle(fontTitle);
    }

    private void createHeader3(Workbook wb, Sheet sheet, Row row, int column, String title) {
        // Row row = sheet.createRow(0);
        Font fontTitle = wb.createFont();
        fontTitle.setFontName("Arial");
        fontTitle.setBoldweight(Font.BOLDWEIGHT_BOLD);
        fontTitle.setColor(IndexedColors.BLACK.getIndex());

        CellStyle cellStyle = ExcelStyles.getStyleHeader(wb);
        cellStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        cellStyle.setFont(fontTitle);

        ExcelHelper.replaceVal(sheet, row.getRowNum(), column, title);
        ExcelHelper.findCell(sheet, row.getRowNum(), column).setCellStyle(cellStyle);
    }

    private void createBody(Workbook wb, Row row, int column, String value) {
        CellStyle fontBody = ExcelStyles.getStyleBody(wb);
        ExcelHelper.createCell(row, column, value, fontBody);
    }

}
