package pe.edu.lamolina.pivot.controller.programacionhorarios.boletinacademico;

import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pe.albatross.zelpers.file.excel.AbstractPOIExcelView;
import pe.albatross.zelpers.file.excel.ExcelHelper;
import pe.albatross.zelpers.file.excel.ExcelStyles;
import pe.edu.lamolina.model.academico.AnexoBoletin;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Curso;
import pe.edu.lamolina.model.academico.GrupoSeccion;
import pe.edu.lamolina.model.academico.Seccion;

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
        Sheet sheet = workBook.createSheet("reporte boletin");
        this.createSheet(workBook, sheet);

        String fechaRep = " - " + new DateTime().toString("dd/MM/yyyy H:mm");

        String nombreReporte = "Boletin";

        response.setHeader("Content-Disposition", "attachment; filename=\"" + nombreReporte + fechaRep + ".xlsx\"");
    }

    private void createSheet(Workbook workBook, Sheet sheet) {

        CicloAcademico ciclo = service.findCicloAcademicoActivo();
        List<AnexoBoletin> anexosBoletin = service.allAnexosByCiclo(ciclo);

        int rowIndice = 0;
        for (AnexoBoletin anexoBoletin : anexosBoletin) {
            logger.debug("Anexo Boletin Padre {} id {}", anexoBoletin.getNombre(), anexoBoletin.getId());
            Row row = sheet.createRow(rowIndice++);
            this.createHeader(workBook, sheet, row, 0, "Anexo " + anexoBoletin.getNombre());
            sheet.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 0, 10));
            for (AnexoBoletin anexosBoletinHijo : anexoBoletin.getAnexosBoletinHijos()) {
                logger.debug("          Anexo Boletin Hijo {} id {}", anexosBoletinHijo.getNombre(), anexosBoletinHijo.getId());
                row = sheet.createRow(rowIndice++);
                this.createHeader(workBook, sheet, row, 0, "Anexo " + anexosBoletinHijo.getNombre());
                sheet.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 0, 10));
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
                this.createHeader(workBook, sheet, row, col++, "DIA/HORA");
                this.createHeader(workBook, sheet, row, col, "VAC");
                for (Curso curso : anexosBoletinHijo.getCursos()) {
                    logger.debug("                     Curso {}", curso.getNombre());
                    rowIndice++;
                    row = sheet.createRow(rowIndice++);
                    this.createHeader(workBook, sheet, row, 0, curso.getCodigo() + " " + curso.getNombre());
                    sheet.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 0, 10));
                    for (GrupoSeccion grupoSeccion : curso.getGrupoSeccion()) {
                        for (Seccion seccion : grupoSeccion.getSecciones()) {
                            row = sheet.createRow(rowIndice++);
                            logger.debug("                      Seccion {} tipo {}", seccion.getCodigo2(), seccion.getTipoSeccion());
                            col = 0;
                            String tpc = !seccion.isTipoSeccionPCUR() ? curso.getTpc() : "";
                            this.createBody(workBook, row, col++, tpc);
                            sheet.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 0, 1));
                            col++;
                            boolean isTeoria = seccion.isTipoSeccionTCUR() || seccion.isTipoSeccionTEO();
                            String horarioTeoria = isTeoria ? seccion.getGrupoHoras().getCodigo() : "";
                            String aulaTeoria = isTeoria ? seccion.getGrupoHoras().getCodigo() : "";
                            this.createBody(workBook, row, col++, horarioTeoria);
                            this.createBody(workBook, row, col++, aulaTeoria);
                            this.createBody(workBook, row, col++, seccion.getCodigo2());
                            boolean isPractica = seccion.isTipoSeccionPCUR() || seccion.isTipoSeccionPRA();
                            String horarioPractica = isPractica ? seccion.getGrupoHoras().getCodigo() : "";
                            String aulaPractica = isPractica ? seccion.getGrupoHoras().getCodigo() : "";
                            this.createBody(workBook, row, col++, horarioPractica);
                            this.createBody(workBook, row, col++, aulaPractica);
                            this.createBody(workBook, row, col++, "");
                            this.createBody(workBook, row, col++, "");
                            this.createBody(workBook, row, col++, seccion.getHorarioTexto());
                            ExcelHelper.replaceVal(sheet, row.getRowNum(), col, seccion.getVacantes());
                        }
                    }
                }
            }

            for (int i = 0; i <= 10; i++) {
                // sheet.setColumnWidth(i, 1024);
                sheet.autoSizeColumn(i);
            }
            break;
        }
    }

    private void createHeader(Workbook wb, Sheet sheet, Row row, int column, String title) {
        // Row row = sheet.createRow(0);
        CellStyle fontTitle = ExcelStyles.getStyleHeader(wb);
        ExcelHelper.createCell(row, column, title, fontTitle);
        //  CellRangeAddress mergedRegion = new CellRangeAddress(0, 0, 0, 10);
        //   sheet.addMergedRegion(mergedRegion);
    }

    private void createBody(Workbook wb, Row row, int column, String value) {
        CellStyle fontBody = ExcelStyles.getStyleBody(wb);
        ExcelHelper.createCell(row, column, value, fontBody);
    }

}
