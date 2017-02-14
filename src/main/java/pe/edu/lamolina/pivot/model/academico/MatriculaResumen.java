package pe.edu.lamolina.pivot.model.academico;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.pivot.zelper.enums.EstadoMatriculaCursoEnum;

@Entity
@Table(name = "aca_matricula_resumen")
public class MatriculaResumen implements Serializable {

    @Id
    @GeneratedValue
    @Column(name = "id")
    private Long id;

    @Column(name = "estado")
    private String estado;

    @Column(name = "cursos_matriculados")
    private Integer cursosMatriculados;

    @Column(name = "cursos_retirados")
    private Integer cursosRetirados;

    @Column(name = "creditos_matriculados")
    private Integer creditosMatriculados;

    @Column(name = "creditos_retirados")
    private Integer creditosRetirados;

    @Column(name = "nota_avance")
    private String notaAvance;

    @Column(name = "nota_acumulada")
    private String notaAcumulada;

    @Column(name = "nota_final")
    private String notaFinal;

    @Column(name = "porcentaje_avance")
    private Integer porcentajeAvance;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_ciclo_academico")
    private CicloAcademico cicloAcademico;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_alumno")
    private Alumno alumno;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_situacion_inicio")
    private SituacionAcademica situacionInicio;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_situacion_final")
    private SituacionAcademica situacionFinal;

    @OneToMany(mappedBy = "matriculaResumen", fetch = FetchType.LAZY)
    private List<MatriculaCurso> matriculaCurso;

    @OneToMany(mappedBy = "matriculaResumen", fetch = FetchType.LAZY)
    private List<MatriculaSeccion> matriculaSeccion;

    public MatriculaResumen() {
    }

    public MatriculaResumen(Object id) {
        this.id = TypesUtil.getLong(id);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public CicloAcademico getCicloAcademico() {
        return cicloAcademico;
    }

    public void setCicloAcademico(CicloAcademico cicloAcademico) {
        this.cicloAcademico = cicloAcademico;
    }

    public Alumno getAlumno() {
        return alumno;
    }

    public void setAlumno(Alumno alumno) {
        this.alumno = alumno;
    }

    public String getEstado() {
        return estado;
    }

    public EstadoMatriculaCursoEnum getEstadoEnum() {
        if (estado == null) {
            return null;
        }
        return EstadoMatriculaCursoEnum.valueOf(estado);
    }

    public void setEstadoEnum(EstadoMatriculaCursoEnum estado) {
        this.estado = estado.name();
    }

    public Integer getCursosMatriculados() {
        return cursosMatriculados;
    }

    public void setCursosMatriculados(Integer cursosMatriculados) {
        this.cursosMatriculados = cursosMatriculados;
    }

    public Integer getCursosRetirados() {
        return cursosRetirados;
    }

    public void setCursosRetirados(Integer cursosRetirados) {
        this.cursosRetirados = cursosRetirados;
    }

    public Integer getCreditosMatriculados() {
        return creditosMatriculados;
    }

    public void setCreditosMatriculados(Integer creditosMatriculados) {
        this.creditosMatriculados = creditosMatriculados;
    }

    public Integer getCreditosRetirados() {
        return creditosRetirados;
    }

    public void setCreditosRetirados(Integer creditosRetirados) {
        this.creditosRetirados = creditosRetirados;
    }

    public SituacionAcademica getSituacionInicio() {
        return situacionInicio;
    }

    public void setSituacionInicio(SituacionAcademica situacionInicio) {
        this.situacionInicio = situacionInicio;
    }

    public SituacionAcademica getSituacionFinal() {
        return situacionFinal;
    }

    public void setSituacionFinal(SituacionAcademica situacionFinal) {
        this.situacionFinal = situacionFinal;
    }

    public String getNotaAvance() {
        return notaAvance;
    }

    public void setNotaAvance(String notaAvance) {
        this.notaAvance = notaAvance;
    }

    public String getNotaAcumulada() {
        return notaAcumulada;
    }

    public void setNotaAcumulada(String notaAcumulada) {
        this.notaAcumulada = notaAcumulada;
    }

    public String getNotaFinal() {
        return notaFinal;
    }

    public void setNotaFinal(String notaFinal) {
        this.notaFinal = notaFinal;
    }

    public Integer getPorcentajeAvance() {
        return porcentajeAvance;
    }

    public void setPorcentajeAvance(Integer porcentajeAvance) {
        this.porcentajeAvance = porcentajeAvance;
    }

    public List<MatriculaCurso> getMatriculaCurso() {
        return matriculaCurso;
    }

    public void setMatriculaCurso(List<MatriculaCurso> matriculaCurso) {
        this.matriculaCurso = matriculaCurso;
    }

    public List<MatriculaSeccion> getMatriculaSeccion() {
        return matriculaSeccion;
    }

    public void setMatriculaSeccion(List<MatriculaSeccion> matriculaSeccion) {
        this.matriculaSeccion = matriculaSeccion;
    }

}
