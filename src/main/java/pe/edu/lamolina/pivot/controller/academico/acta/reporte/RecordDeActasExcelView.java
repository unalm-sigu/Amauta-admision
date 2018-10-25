package pe.edu.lamolina.pivot.controller.academico.acta.reporte;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pe.albatross.zelpers.file.excel.AbstractPOIExcelView;
import pe.albatross.zelpers.file.excel.ExcelStyles;
import pe.albatross.zelpers.miscelanea.ObjectUtil;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Curso;
import pe.edu.lamolina.model.academico.DepartamentoAcademico;
import pe.edu.lamolina.model.academico.Docente;
import pe.edu.lamolina.model.academico.DocenteSeccion;
import pe.edu.lamolina.model.academico.GrupoSeccion;
import pe.edu.lamolina.model.academico.Seccion;
import pe.edu.lamolina.model.enums.EstadoEnum;
import pe.edu.lamolina.pivot.dao.academico.DocenteSeccionDAO;
import pe.edu.lamolina.pivot.dao.academico.GrupoSeccionDAO;

@Component
public class RecordDeActasExcelView extends AbstractPOIExcelView {
    
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    
    public final static String TIPO = "tipo";
    public final static String PRE_GRADO = "PRE";
    public final static String POST_GRADO = "POST";
    
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
        
        CicloAcademico cicloAcademico = (CicloAcademico) model.get("cicloAcademico");
        String tipo = (String) model.get(TIPO);
        
        List<GrupoSeccion> allGruposSeccion = grupoSeccionDAO.allByFilter(null, cicloAcademico, null, EstadoEnum.ACT);
        allGruposSeccion = filtrarByType(allGruposSeccion, tipo);
        logger.debug("Cantidad de grupos {}", allGruposSeccion.size());
        
        Map cantidadAlumnosByGrupo = grupoSeccionDAO.allCountAlumnos(allGruposSeccion);
        logger.debug("cantidadAlumnosByGrupo {}", cantidadAlumnosByGrupo.size());
        Map cantidadAlumnosByGrupoNF = grupoSeccionDAO.allCountAlumnosWithNf(allGruposSeccion);
        logger.debug("cantidadAlumnosByGrupoNF {}", cantidadAlumnosByGrupoNF.size());
        /*
        List<DocenteSeccion> responsables = docenteSeccionDAO.allResponsablesByGpoSecciones(allGruposSeccion, cicloAcademico);
        Map<Long, DocenteSeccion> mapResponsables = MapUtil.storeItems("seccion.grupoSeccion.id", responsables);
        for (GrupoSeccion grupoSeccion : allGruposSeccion) {
            grupoSeccion.setSecciones(new ArrayList());
            DocenteSeccion responsable = mapResponsables.get(grupoSeccion.getId());
            grupoSeccion.setDocenteResponsable(responsable.getDocente());
        }
         */
        CellStyle cellHeader = ExcelStyles.getStyleHeader(workbook);
        CellStyle cellBody = ExcelStyles.getStyleBody(workbook);
        
        List<String> rows = new ArrayList();
        
        String head = "Curso|Grupo|Departamento|Docente Principal|Email Doc. Principal|Versión Acta|Estado Sistema Calificación|Estado Acta|Fecha Cierre Acta|Alumnos Total|Alumnos NF";
        rows.add(head);
        StringBuilder sb;
        for (GrupoSeccion grupoSeccion : allGruposSeccion) {
            sb = new StringBuilder();
            Curso curso = grupoSeccion.getCurso();
            DepartamentoAcademico departamento = curso.getDepartamentoAcademico();
            
            List<DocenteSeccion> docentesSeccion = null;
            List<Docente> docentesPrincipal = new ArrayList<>();
            
            String docentes = "";
            String emails = "";
            String secciones = "";
            
            for (Seccion sec : grupoSeccion.getSecciones()) {
                
                if (sec.isTipoSeccionPRA() || sec.isTipoSeccionTCUR() || sec.isTipoSeccionTEO()) {
                    secciones += sec.getCodigo();
                    if (ObjectUtil.getParentTree(sec, "grupoHoras.id") != null) {
                        secciones += " - " + sec.getGrupoHoras().getCodigo();
                    }
                    secciones += ",";
                    docentesSeccion = docenteSeccionDAO.allByFilter(null, sec);
                    for (DocenteSeccion docentesSeccionEach : docentesSeccion) {
                        if (docentesSeccionEach.getEstadoEnum().equals(EstadoEnum.ACT)) {
                            if (docentesSeccionEach.esDocentePrincipal()) {
                                docentesPrincipal.add(docentesSeccionEach.getDocente());
                            }
                        }
                    }
                    
                }
                
            }
            if (!docentesPrincipal.isEmpty()) {
                docentes = "";
                emails = "";
                for (Docente doc : docentesPrincipal) {
                    docentes += doc.getPersona().getApellidosNombres() + " - ";
                    emails += doc.getPersona().getEmailCompania() + " - ";
                }
                if (!StringUtils.isEmpty(docentes)) {
                    docentes = docentes.substring(0, docentes.length() - 3);
                    emails = emails.substring(0, emails.length() - 3);
                }
            }
            String estadoPlan = "";
            if (grupoSeccion.getEstadoPlanEnum() != null) {
                estadoPlan = grupoSeccion.getEstadoPlanEnum().getValue();
            }
            String estadoGrupo = "";
            if (grupoSeccion.getEstadoGrupoEnum() != null) {
                estadoGrupo = grupoSeccion.getEstadoGrupoEnum().getValue();
            }
            if (!StringUtils.isEmpty(secciones)) {
                secciones = secciones.substring(0, secciones.length() - 1);
            }
            if (docentes.isEmpty()) {
                docentes = "-";
                emails = "-";
            }
            sb.append(curso.getNombre()).append("|").append(secciones.substring(0, secciones.length())).append("|").append(departamento.getNombre()).append("|").append(docentes).append("|").append(emails).append("|").append(grupoSeccion.getVersion()).append("|").append(estadoPlan).append("|").append(estadoGrupo);
            sb.append("|").append(grupoSeccion.getFechaCierreActa() == null ? "-" : TypesUtil.getStringDate(grupoSeccion.getFechaCierreActa(), "dd/MM/yyyy"));
            
            Object cantidadAlumnos = cantidadAlumnosByGrupo.get(grupoSeccion.getId());
            sb.append("|").append(cantidadAlumnos != null ? TypesUtil.getInt(cantidadAlumnos) : "");
            
            Object cantidadAlumnosNF = cantidadAlumnosByGrupoNF.get(grupoSeccion.getId());
            sb.append("|").append(cantidadAlumnosNF != null ? TypesUtil.getInt(cantidadAlumnosNF) : "");
            
            rows.add(sb.toString());
        }
        
        int totalColumns = 9;
        
        this.createSheet(workbook, rows, totalColumns, "RecordActas", cellHeader, cellBody);
        String fechaRep = " - " + new DateTime().toString("dd/MM/yyyy H:mm");
        
        String nombreReporte = "RecordActas ";
        
        response.setHeader("Content-Disposition", "attachment; filename=\"" + nombreReporte + fechaRep + ".xlsx\"");
    }
    
    public List<GrupoSeccion> filtrarByType(List<GrupoSeccion> allGruposSeccion, String tipo) {
        List<GrupoSeccion> result = new ArrayList<>();
        for (GrupoSeccion grupoSeccion : allGruposSeccion) {
            if (PRE_GRADO.equals(tipo)) {
                if (!grupoSeccion.getCurso().isPostgrado()) {
                    result.add(grupoSeccion);
                }
            } else if (POST_GRADO.equals(tipo)) {
                if (grupoSeccion.getCurso().isPostgrado()) {
                    result.add(grupoSeccion);
                }
            }
        }
        return result;
    }
    
    private void createSheet(Workbook workBook, List<String> rows, int columnas, String sheetName, CellStyle cellHeader, CellStyle cellBody) {
        Sheet sheet = workBook.createSheet(sheetName);
        boolean autosize = false;
        
        for (int i = 0; i < rows.size(); i++) {
            String fila = (String) rows.get(i);
            
            String[] argHeader = fila.split("\\|");
            
            StringTokenizer st = new StringTokenizer(fila, "|");
            Row row = sheet.createRow(i);
            int j = 0;
            
            boolean isHeader = false;
            boolean isHeaderTotal = false;
            boolean isHeaderSede = false;
            
            if (i == 0) {
                isHeader = true;
            }
            
            while (st.hasMoreTokens()) {
                String token = st.nextToken();
                
                if (isHeader) {
                    this.createCell(row, j, token, cellHeader);
                    if (isHeaderTotal) {
                        isHeader = false;
                    }
                } else {
                    if (TypesUtil.getInt(token) != null) {
                        this.createCellNumber(row, j, token, cellBody);
                    } else {
                        this.createCell(row, j, token, cellBody);
                    }
                }
                j++;
            }
            if (i == 20) {
                for (int ii = 0; ii < columnas; ii++) {
                    sheet.autoSizeColumn((short) ii);
                }
                autosize = true;
            }
        }
        
        if (!autosize) {
            for (int i = 0; i < columnas; i++) {
                sheet.autoSizeColumn((short) i);
            }
        }
        
    }
    
    private void createCell(Row row, int cellNumber, String value, CellStyle style) {
        Cell cell = row.createCell(cellNumber);
        cell.setCellValue(value + "");
        if (style != null) {
            cell.setCellStyle(style);
        }
    }
    
    private void createCellNumber(Row row, int cellNumber, String value, CellStyle style) {
        Cell cell = row.createCell(cellNumber);
        cell.setCellValue(Integer.parseInt(value));
        cell.setCellType(Cell.CELL_TYPE_NUMERIC);
        if (style != null) {
            cell.setCellStyle(style);
        }
    }
    
    private void createCellNumber(Row row, int cellNumber, String value) {
        Cell cell = row.createCell(cellNumber);
        CellStyle cellStyle = cell.getCellStyle();
        cellStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("#,##0.00"));
        cell.setCellStyle(cellStyle);
        cell.setCellValue(new BigDecimal(value).doubleValue());
    }
    
    private String getValor(String val) {
        if (StringUtils.isEmpty(val)) {
            return " ";
        }
        val = StringUtils.trim(val);
        val = StringUtils.remove(val, '\t');
        val = StringUtils.remove(val, '\r');
        val = StringUtils.remove(val, '\n');
        val = StringUtils.remove(val, '|');
        
        if (StringUtils.isEmpty(val)) {
            return " ";
        }
        return val;
    }
    
    public boolean isTipoPreGrado() {
        if (TIPO.equals(PRE_GRADO)) {
            return true;
        }
        return false;
    }
    
    public boolean isTipoPostGrado() {
        if (TIPO.equals(POST_GRADO)) {
            return true;
        }
        return false;
    }
    
}
