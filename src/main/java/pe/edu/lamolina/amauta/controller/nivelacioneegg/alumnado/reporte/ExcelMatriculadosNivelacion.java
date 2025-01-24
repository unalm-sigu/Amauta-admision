package pe.edu.lamolina.amauta.controller.nivelacioneegg.alumnado.reporte;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.joda.time.DateTime;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.view.AbstractView;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.amauta.zelper.reportes.ExcelHelper;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.Carrera;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Curso;
import pe.edu.lamolina.model.academico.Docente;
import pe.edu.lamolina.model.general.Persona;
import pe.edu.lamolina.model.misc.Acumulador;
import pe.edu.lamolina.model.nivelacioneegg.CursoNivelacion;
import pe.edu.lamolina.model.nivelacioneegg.NotaAlumnoNivelacion;

@Slf4j
@Component
public class ExcelMatriculadosNivelacion extends AbstractView {

    @Override
    protected void renderMergedOutputModel(Map<String, Object> map, HttpServletRequest request, HttpServletResponse response) throws Exception {
        Workbook workbook = new XSSFWorkbook();
        setContentType(ExcelHelper.CONTENT_TYPE_XLSX);
        this.buildExcelDocument(map, workbook, response);
    }

    protected void buildExcelDocument(
            Map<String, Object> model,
            Workbook workbook,
            HttpServletResponse response) throws Exception {

        this.crearDetalle(workbook, model);
        this.publicarExcel(workbook, model, response);

    }

    private void crearDetalle(Workbook workbook, Map<String, Object> model) {
        Acumulador rowCounterDetalle = new Acumulador();
        Sheet sheetDetalle = workbook.createSheet("Detalle");
        ExcelHelper excelUtil = new ExcelHelper(sheetDetalle, workbook);

        CicloAcademico ciclo = (CicloAcademico) model.get("ciclo");
        CursoNivelacion seccion = (CursoNivelacion) model.get("seccion");
        List<NotaAlumnoNivelacion> alumnado = (List<NotaAlumnoNivelacion>) model.get("alumnado");

        this.crearHeaderDetalle(excelUtil, ciclo, seccion, alumnado, rowCounterDetalle);
        rowCounterDetalle.incrementar();
        this.crearItemsDetalle(excelUtil, alumnado, rowCounterDetalle);
    }

    private void crearItemsDetalle(ExcelHelper excelUtil, List<NotaAlumnoNivelacion> matriculados, Acumulador rowCounter) {
        CellStyle estiloCenter = excelUtil.getConBordes(HorizontalAlignment.CENTER);
        CellStyle estiloLeft = excelUtil.getConBordes(HorizontalAlignment.LEFT);
        CellStyle estiloHead = excelUtil.getBgGreenLetraBlanca(HorizontalAlignment.CENTER);

        {

            int col = 0;
            excelUtil.setWidthColumn(col, 1200);
            excelUtil.replaceStyle(rowCounter.getValor(), col, estiloHead);
            excelUtil.replaceVal(rowCounter.getValor(), col, "Nº");
            col++;

            excelUtil.setWidthColumn(col, 4500);
            excelUtil.replaceStyle(rowCounter.getValor(), col, estiloHead);
            excelUtil.replaceVal(rowCounter.getValor(), col, "Matrícula");
            col++;

            excelUtil.setWidthColumn(col, 12000);
            excelUtil.replaceStyle(rowCounter.getValor(), col, estiloHead);
            excelUtil.replaceVal(rowCounter.getValor(), col, "Alumno");
            col++;

            excelUtil.setWidthColumn(col, 10000);
            excelUtil.replaceStyle(rowCounter.getValor(), col, estiloHead);
            excelUtil.replaceVal(rowCounter.getValor(), col, "Especialidad");
            col++;

            excelUtil.setWidthColumn(col, 9000);
            excelUtil.replaceStyle(rowCounter.getValor(), col, estiloHead);
            excelUtil.replaceVal(rowCounter.getValor(), col, "Correo");
            col++;

            excelUtil.setWidthColumn(col, 4000);
            excelUtil.replaceStyle(rowCounter.getValor(), col, estiloHead);
            excelUtil.replaceVal(rowCounter.getValor(), col, "Celular");
            col++;

            excelUtil.setWidthColumn(col, 4000);
            excelUtil.replaceStyle(rowCounter.getValor(), col, estiloHead);
            excelUtil.replaceVal(rowCounter.getValor(), col, "Ciclo ingreso");
            col++;

            rowCounter.incrementar();
        }

        Acumulador numeroAlumno = new Acumulador(1);
        matriculados.forEach(inscrito -> {
            Alumno alumno = inscrito.getAlumnoNivelacion().getAlumno();
            Persona persona = alumno.getPersona();
            Carrera carrera = alumno.getCarrera();
            CicloAcademico cicloIngreso = alumno.getCicloIngreso();

            int col = 0;
            excelUtil.replaceStyle(rowCounter.getValor(), col, estiloCenter);
            excelUtil.replaceVal(rowCounter.getValor(), col, numeroAlumno.getValor());
            numeroAlumno.incrementar();
            col++;

            excelUtil.replaceStyle(rowCounter.getValor(), col, estiloCenter);
            excelUtil.replaceVal(rowCounter.getValor(), col, alumno.getCodigo());
            col++;

            excelUtil.replaceStyle(rowCounter.getValor(), col, estiloLeft);
            excelUtil.replaceVal(rowCounter.getValor(), col, persona.getApellidosNombres());
            col++;

            excelUtil.replaceStyle(rowCounter.getValor(), col, estiloLeft);
            excelUtil.replaceVal(rowCounter.getValor(), col, carrera.getNombre());
            col++;

            excelUtil.replaceStyle(rowCounter.getValor(), col, estiloLeft);
            excelUtil.replaceVal(rowCounter.getValor(), col, persona.getEmail());
            col++;

            excelUtil.replaceStyle(rowCounter.getValor(), col, estiloLeft);
            excelUtil.replaceVal(rowCounter.getValor(), col, persona.getCelular());
            col++;

            excelUtil.replaceStyle(rowCounter.getValor(), col, estiloCenter);
            excelUtil.replaceVal(rowCounter.getValor(), col, cicloIngreso.getDescripcion());
            col++;

            rowCounter.incrementar();
        });
    }

    private void crearHeaderDetalle(ExcelHelper excelUtil, CicloAcademico ciclo, CursoNivelacion seccion, List<NotaAlumnoNivelacion> inscritos, Acumulador rowCounter) {
        DateTime today = new DateTime();
        ExcelHelper.mergeCell(excelUtil.getSheet(), rowCounter.getValor(), rowCounter.getValor(), 0, 6);

        excelUtil.replaceStyle(rowCounter.getValor(), 0, excelUtil.getConLetraBoldSize14(HorizontalAlignment.LEFT));
        excelUtil.replaceVal(rowCounter.getValor(), 0, "Lista de alumnos matriculados en nivelación de ingresantes");
        rowCounter.incrementar();

        ExcelHelper.mergeCell(excelUtil.getSheet(), rowCounter.getValor(), rowCounter.getValor(), 0, 1);
        excelUtil.replaceStyle(rowCounter.getValor(), 0, excelUtil.getConLetraBold(HorizontalAlignment.LEFT));
        excelUtil.replaceVal(rowCounter.getValor(), 0, "Ciclo");
        excelUtil.replaceVal(rowCounter.getValor(), 2, ciclo.getDescripcion2());
        rowCounter.incrementar();

        Curso curso = seccion.getCursoCiclo().getCurso();
        String datoCurso = curso.getCodigo() + " - " + curso.getNombre();
        ExcelHelper.mergeCell(excelUtil.getSheet(), rowCounter.getValor(), rowCounter.getValor(), 0, 1);
        excelUtil.replaceStyle(rowCounter.getValor(), 0, excelUtil.getConLetraBold(HorizontalAlignment.LEFT));
        excelUtil.replaceVal(rowCounter.getValor(), 0, "Curso");
        excelUtil.replaceVal(rowCounter.getValor(), 2, datoCurso);
        rowCounter.incrementar();

        Docente docente = seccion.getDocente();
        Persona persona = docente == null ? null : docente.getPersona();
        String datoDocente = docente == null ? "Desconocido" : docente.getCodigo();
        if (persona != null) {
            datoDocente += " - " + persona.getApellidosNombres();
        }
        ExcelHelper.mergeCell(excelUtil.getSheet(), rowCounter.getValor(), rowCounter.getValor(), 0, 1);
        excelUtil.replaceStyle(rowCounter.getValor(), 0, excelUtil.getConLetraBold(HorizontalAlignment.LEFT));
        excelUtil.replaceVal(rowCounter.getValor(), 0, "Docente");
        excelUtil.replaceVal(rowCounter.getValor(), 2, datoDocente);
        rowCounter.incrementar();

        ExcelHelper.mergeCell(excelUtil.getSheet(), rowCounter.getValor(), rowCounter.getValor(), 0, 1);
        excelUtil.replaceStyle(rowCounter.getValor(), 0, excelUtil.getConLetraBold(HorizontalAlignment.LEFT));
        excelUtil.replaceVal(rowCounter.getValor(), 0, "Sección");
        excelUtil.replaceVal(rowCounter.getValor(), 2, seccion.getCodigo());
        rowCounter.incrementar();

        ExcelHelper.mergeCell(excelUtil.getSheet(), rowCounter.getValor(), rowCounter.getValor(), 0, 1);
        excelUtil.replaceStyle(rowCounter.getValor(), 0, excelUtil.getConLetraBold(HorizontalAlignment.LEFT));
        excelUtil.replaceVal(rowCounter.getValor(), 0, "Total alumnos");
        excelUtil.replaceStyle(rowCounter.getValor(), 2, excelUtil.getNumerico(HorizontalAlignment.LEFT));
        excelUtil.replaceVal(rowCounter.getValor(), 2, inscritos.size());
        rowCounter.incrementar();

        ExcelHelper.mergeCell(excelUtil.getSheet(), rowCounter.getValor(), rowCounter.getValor(), 0, 1);
        excelUtil.replaceStyle(rowCounter.getValor(), 0, excelUtil.getConLetraBold(HorizontalAlignment.LEFT));
        excelUtil.replaceVal(rowCounter.getValor(), 0, "Fecha reporte");
        excelUtil.replaceVal(rowCounter.getValor(), 2, capitaliza(TypesUtil.getStringDate(today.toDate(), "EEEE dd 'de' MMMM 'de' yyyy", "es")));
        rowCounter.incrementar();

        ExcelHelper.mergeCell(excelUtil.getSheet(), rowCounter.getValor(), rowCounter.getValor(), 0, 1);
        excelUtil.replaceStyle(rowCounter.getValor(), 0, excelUtil.getConLetraBold(HorizontalAlignment.LEFT));
        excelUtil.replaceVal(rowCounter.getValor(), 0, "Hora reporte");
        excelUtil.replaceVal(rowCounter.getValor(), 2, capitaliza(TypesUtil.getStringDate(today.toDate(), "HH:mm")));
        rowCounter.incrementar();
    }

    private void publicarExcel(Workbook workbook, Map<String, Object> model, HttpServletResponse response) throws IOException {
        CursoNivelacion seccion = (CursoNivelacion) model.get("seccion");

        String cursoSeccion = seccion.getCursoCiclo().getCurso().getCodigo() + "-" + seccion.getCodigo();
        String fechaCreacion = new DateTime().toString("yyyMMdd_HHmm");

        response.setHeader("Content-Disposition", "attachment; filename=\"" + "ReporteAlumnosMatriculados_" + cursoSeccion + "_" + fechaCreacion + ".xlsx\"");
        response.setContentType(getContentType());
        ServletOutputStream out = response.getOutputStream();
        workbook.write(out);
        out.flush();
    }

    private String capitaliza(String texto) {
        return texto.substring(0, 1).toUpperCase() + texto.substring(1);
    }

}
