package pe.edu.lamolina.amauta.controller.nivelacioneegg.reportes.ExcelData.Bean;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
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
    private String codDocente;
    private String docente;
    private String seccion;
    private String grupo;
    private Date semana;
    private String dia;
    private String horaDictado;
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
    private String controlAsistencia;
    private String actasEntregadas;
    private BigDecimal porcentajeAsistencia;
    private BigDecimal puntajeCurso;

    private BigDecimal puntajeAlgebra;
    private BigDecimal puntajeAritmetica;
    private BigDecimal puntajeGeometrica;
    private BigDecimal puntajeTrigonometria;
    private BigDecimal puntajeMatematica;
    private BigDecimal puntajeQuimica;
    private BigDecimal puntajeRm;
    private BigDecimal puntajeRv;
    private BigDecimal puntajeBiologia;
    private BigDecimal puntajeEconomia;
    private BigDecimal puntajeFisica;
    private BigDecimal puntajeHistoria;
    private BigDecimal puntajeGeografia;
    private BigDecimal puntajeFinal;
    private Date fechaIngreso;
    private String cicloIngresoAdmision;
    private String estado;
    private String estadoCursoNivelacion;
    private String temaAprobado;
    private List<AsistenciaNivelacion> asistencias;
    private List<IngresantesExamenAdmisionDTO> ingresantesExamene = new ArrayList<>();
    private List<IngresantesAsistenciaInscritosDTO> ingresantesAsistencia = new ArrayList<>();
    private List<IngresantesMateriasNivelacionDTO> ingresantesMateria = new ArrayList<>();
    private List<IngresantesInscritosNivelacionDTO> ingresantesInscritos = new ArrayList<>();

}
