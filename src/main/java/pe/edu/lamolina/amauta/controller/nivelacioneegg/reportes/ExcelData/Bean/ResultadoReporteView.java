package pe.edu.lamolina.amauta.controller.nivelacioneegg.reportes.ExcelData.Bean;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import pe.edu.lamolina.model.nivelacioneegg.AsistenciaNivelacion;

@Setter
@Getter
public class ResultadoReporteView {

    private String correlativo;
    private String dni;
    private String codCurso;
    private String curso;
    private String docente;
    private String seccion;
    private String ciclo;
    private String matricula;
    private String apellidosNombre;
    private BigDecimal evaluacionParcial1;
    private BigDecimal evaluacionParcial2;
    private BigDecimal examenFinal;
    private BigDecimal promedioFinal;
    private String condicion;
    private String modalidadIngreso;
    private String carrera;
    private String facultad;
    private String correoPersonal;
    private String correoOutlook;
    private String correoGmail;
    private String correoDocente;
    private String celular;
    private String telefono;
    private String temaCurso;
    private String moduloAula;
    private String aula;
    private String usuario;
    private BigDecimal porcentajeAsistencia;
    private BigDecimal puntajeCurso;
    private List<AsistenciaNivelacion> asistencias;
    private List<IngresantesExamenAdmisionDTO> ingresantesExamene = new ArrayList<>();
    private List<IngresantesAsistenciaInscritosDTO> ingresantesAsistencia = new ArrayList<>();
    private List<IngresantesMateriasNivelacionDTO> ingresantesMateria = new ArrayList<>();
    private List<IngresantesInscritosNivelacionDTO> ingresantesInscritos = new ArrayList<>();

}
