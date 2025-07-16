package pe.edu.lamolina.amauta.controller.nivelacioneegg.reportes.ExcelData;

import java.io.InputStream;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.view.AbstractView;
import pe.edu.lamolina.amauta.controller.nivelacioneegg.reportes.ExcelData.Bean.IngresantesAsistenciaInscritosDTO;
import pe.edu.lamolina.amauta.controller.nivelacioneegg.reportes.ExcelData.Bean.IngresantesExamenAdmisionDTO;
import pe.edu.lamolina.amauta.controller.nivelacioneegg.reportes.ExcelData.Bean.IngresantesInscritosNivelacionDTO;
import pe.edu.lamolina.amauta.controller.nivelacioneegg.reportes.ExcelData.Bean.IngresantesMateriasNivelacionDTO;
import pe.edu.lamolina.amauta.controller.nivelacioneegg.reportes.ExcelData.Bean.ResultadoReporteView;
import pe.edu.lamolina.amauta.zelper.reportes.ExcelHelper;
import pe.edu.lamolina.model.academico.CicloAcademico;

@Slf4j
@Component
public class ExcelReporteGeneralNivelacion extends AbstractView {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private static final String CONTENT_TYPE_XLSX = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
    private static final String CONTENT_TYPE_XLS = "application/vnd.ms-excel";

    @Override
    protected void renderMergedOutputModel(Map<String, Object> map, HttpServletRequest request, HttpServletResponse response) throws Exception {
        InputStream formato = (InputStream) map.get("formato");

        Workbook workbook = new XSSFWorkbook(formato);
        if (workbook instanceof XSSFWorkbook) {
            setContentType(CONTENT_TYPE_XLSX);
        } else {
            setContentType(CONTENT_TYPE_XLS);
        }

        this.buildExcelDocument(map, workbook, request, response);
    }

    private void buildExcelDocument(Map<String, Object> model, Workbook workbook, HttpServletRequest request, HttpServletResponse response) throws Exception {
        ResultadoReporteView resultado = (ResultadoReporteView) model.get("resultado");
        CicloAcademico ciclo = (CicloAcademico) model.get("cicloAcademico");

        String nombreReporte = "Reporte General de Nivelación " + ciclo.getDescripcion();

        Sheet sheetGenero = workbook.getSheet("Hoja1");

        this.llenarCuadros(sheetGenero, resultado, workbook, ciclo);

        StringBuilder nomReporte = new StringBuilder();
        nomReporte.append(nombreReporte).append(new DateTime().toString("dd-MM-yyyy hhmmss"));
        logger.info(nomReporte.toString());

        workbook.setForceFormulaRecalculation(true);

        response.setHeader("Content-Disposition", "attachment; filename=\"" + nomReporte.toString() + ".xlsx\"");
        response.setContentType(getContentType());
        ServletOutputStream out = response.getOutputStream();
        out.flush();
        workbook.write(out);
        out.flush();

    }

    private void llenarCuadros(Sheet sheet, ResultadoReporteView resultado, Workbook workbook, CicloAcademico ciclo) {
        ExcelHelper excelUtil = new ExcelHelper(sheet, workbook);

        List<IngresantesExamenAdmisionDTO> examenes = resultado.getIngresantesExamene();
        List<IngresantesExamenAdmisionDTO> examenesFormateado = this.formateoExamenes(examenes);

        List<IngresantesInscritosNivelacionDTO> inscritos = resultado.getIngresantesInscritos();
        List<IngresantesMateriasNivelacionDTO> materias = resultado.getIngresantesMateria();
        List<IngresantesAsistenciaInscritosDTO> asistencias = resultado.getIngresantesAsistencia();

        log.debug("setIngresantesExamene {}", examenes.size());
        log.debug("inscritosNivelacion {}", inscritos.size());
        log.debug("materiasNivelacion {}", materias.size());
        log.debug("asistencias {}", asistencias.size());

        if (!examenes.isEmpty()) {
            int irowExamen = 11;
            for (IngresantesExamenAdmisionDTO data : examenesFormateado) {
                excelUtil.replaceVal(irowExamen, 1, data.getMateria());
                excelUtil.replaceVal(irowExamen, 2, data.getAprobados());
                excelUtil.replaceVal(irowExamen, 3, data.getPorcAprobados(), "#,##0.00");
                excelUtil.replaceVal(irowExamen, 4, data.getDesaprobados());
                excelUtil.replaceVal(irowExamen, 5, data.getPorcDesaprobados(), "#,##0.00");
                irowExamen++;
            }

        }
        if (!inscritos.isEmpty()) {
            int irow = 26;
            for (IngresantesInscritosNivelacionDTO data : inscritos) {
                excelUtil.replaceVal(irow, 1, data.getCarrera());
                excelUtil.replaceVal(irow, 2, data.getAprobados());
                excelUtil.replaceVal(irow, 3, data.getDesaprobados());
                excelUtil.replaceVal(irow, 4, data.getTotal());
                irow++;
            }

        }

        if (!materias.isEmpty()) {
            int irow = 44;
            for (IngresantesMateriasNivelacionDTO data : materias) {
                excelUtil.replaceVal(irow, 1, data.getCurso());
                excelUtil.replaceVal(irow, 2, data.getInscritos());
                excelUtil.replaceVal(irow, 3, data.getAprobados());
                excelUtil.replaceVal(irow, 4, data.getDesaprobados());
                excelUtil.replaceVal(irow, 5, data.getSinNota());
                irow++;
            }
        }

        if (!asistencias.isEmpty()) {
            int irow = 76;
            for (IngresantesAsistenciaInscritosDTO data : asistencias) {
                excelUtil.replaceVal(irow, 1, data.getCurso());
                excelUtil.replaceVal(irow, 2, data.getTotal());
                excelUtil.replaceVal(irow, 3, data.getMayorIgual50Asistencia());
                excelUtil.replaceVal(irow, 4, data.getMenora50Asistencia());
                excelUtil.replaceVal(irow, 5, data.getZeroAsistencia());
                irow++;
            }
        }
    }

    private List<IngresantesExamenAdmisionDTO> formateoExamenes(List<IngresantesExamenAdmisionDTO> examenes) {
        List<String> ordenMaterias = Arrays.asList("Razonamiento Matemático", "Matemática", "Razonamiento Verbal", "Física", "Química", "Biología", "Economía", "Historia del Perú", "Geografía");

        List<IngresantesExamenAdmisionDTO> examenesFormatMateria = examenes.stream().map(x -> {
            if (x.getMateria().equalsIgnoreCase("puntaje_matematicas")) {
                x.setMateria("Matemática");
            } else if (x.getMateria().equalsIgnoreCase("puntaje_biologia")) {
                x.setMateria("Biología");
            } else if (x.getMateria().equalsIgnoreCase("puntaje_economia")) {
                x.setMateria("Economía");
            } else if (x.getMateria().equalsIgnoreCase("puntaje_fisica")) {
                x.setMateria("Física");
            } else if (x.getMateria().equalsIgnoreCase("puntaje_geografia")) {
                x.setMateria("Geografía");
            } else if (x.getMateria().equalsIgnoreCase("puntaje_historia")) {
                x.setMateria("Historia del Perú");
            } else if (x.getMateria().equalsIgnoreCase("puntaje_quimica")) {
                x.setMateria("Química");
            } else if (x.getMateria().equalsIgnoreCase("puntaje_rm")) {
                x.setMateria("Razonamiento Matemático");
            } else if (x.getMateria().equalsIgnoreCase("puntaje_rv")) {
                x.setMateria("Razonamiento Verbal");
            }
            return x;
        }).collect(Collectors.toList());

        return examenesFormatMateria.stream().sorted(Comparator.comparingInt(
                data -> ordenMaterias.indexOf(data.getMateria()) // Ordena según la posición en la lista
        )).collect(Collectors.toList());

    }

}
