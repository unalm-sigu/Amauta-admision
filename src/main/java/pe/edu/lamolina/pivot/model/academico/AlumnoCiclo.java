package pe.edu.lamolina.pivot.model.academico;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
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
import javax.persistence.Temporal;
import pe.albatross.zelpers.miscelanea.TypesUtil;

@Entity
@Table(name = "aca_alumno_ciclo")
public class AlumnoCiclo implements Serializable {

    @Id
    @GeneratedValue
    @Column(name = "id")
    private Long id;

    @Column(name = "estado")
    private String estado;

    @Column(name = "creditos_cursados_ciclo")
    private Integer creditosCursadosCiclo;

    @Column(name = "creditos_acumulados")
    private Integer creditosAcumulados;

    @Column(name = "creditos_aprobados_ciclo")
    private Integer creditosAprobadosCiclo;

    @Column(name = "creditos_aprobados_acumulados")
    private Integer creditosAprobadosAcumulados;

    @Column(name = "promedio_ciclo")
    private BigDecimal promedioCiclo;

    @Column(name = "promedio_acumulado")
    private BigDecimal promedioAcumulado;

    @Column(name = "cursos_inscritos")
    private Integer cursosInscritos;

    @Column(name = "cursos_aprobados")
    private Integer cursosAprobados;

    @Column(name = "id_user_registro")
    private Long idUserRegistro;

    @Column(name = "fecha_registro")
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date fechaRegistro;

    @Column(name = "id_user_modificacion")
    private Long idUserModificacion;

    @Column(name = "fecha_modificacion")
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date fechaModificacion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_alumno")
    private Alumno alumno;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_ciclo_academico")
    private CicloAcademico cicloAcademico;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_carrera")
    private Carrera carrera;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_orientacion_carrera")
    private OrientacionCarrera orientacionCarrera;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_situacion_inicio")
    private SituacionAcademica situacionInicio;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_situacion_final")
    private SituacionAcademica situacionFinal;

    @OneToMany(mappedBy = "alumnoCiclo", fetch = FetchType.LAZY)
    private List<AlumnoCicloCurso> alumnoCicloCurso;

    public AlumnoCiclo() {
    }

    public AlumnoCiclo(Object id) {
        this.id = TypesUtil.getLong(id);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Alumno getAlumno() {
        return alumno;
    }

    public void setAlumno(Alumno alumno) {
        this.alumno = alumno;
    }

    public CicloAcademico getCicloAcademico() {
        return cicloAcademico;
    }

    public void setCicloAcademico(CicloAcademico cicloAcademico) {
        this.cicloAcademico = cicloAcademico;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Carrera getCarrera() {
        return carrera;
    }

    public void setCarrera(Carrera carrera) {
        this.carrera = carrera;
    }

    public OrientacionCarrera getOrientacionCarrera() {
        return orientacionCarrera;
    }

    public void setOrientacionCarrera(OrientacionCarrera orientacionCarrera) {
        this.orientacionCarrera = orientacionCarrera;
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

    public Integer getCreditosCursadosCiclo() {
        return creditosCursadosCiclo;
    }

    public void setCreditosCursadosCiclo(Integer creditosCursadosCiclo) {
        this.creditosCursadosCiclo = creditosCursadosCiclo;
    }

    public Integer getCreditosAcumulados() {
        return creditosAcumulados;
    }

    public void setCreditosAcumulados(Integer creditosAcumulados) {
        this.creditosAcumulados = creditosAcumulados;
    }

    public Integer getCreditosAprobadosCiclo() {
        return creditosAprobadosCiclo;
    }

    public void setCreditosAprobadosCiclo(Integer creditosAprobadosCiclo) {
        this.creditosAprobadosCiclo = creditosAprobadosCiclo;
    }

    public Integer getCreditosAprobadosAcumulados() {
        return creditosAprobadosAcumulados;
    }

    public void setCreditosAprobadosAcumulados(Integer creditosAprobadosAcumulados) {
        this.creditosAprobadosAcumulados = creditosAprobadosAcumulados;
    }

    public BigDecimal getPromedioCiclo() {
        return promedioCiclo;
    }

    public void setPromedioCiclo(BigDecimal promedioCiclo) {
        this.promedioCiclo = promedioCiclo;
    }

    public BigDecimal getPromedioAcumulado() {
        return promedioAcumulado;
    }

    public void setPromedioAcumulado(BigDecimal promedioAcumulado) {
        this.promedioAcumulado = promedioAcumulado;
    }

    public Integer getCursosInscritos() {
        return cursosInscritos;
    }

    public void setCursosInscritos(Integer cursosInscritos) {
        this.cursosInscritos = cursosInscritos;
    }

    public Integer getCursosAprobados() {
        return cursosAprobados;
    }

    public void setCursosAprobados(Integer cursosAprobados) {
        this.cursosAprobados = cursosAprobados;
    }

    public Long getIdUserRegistro() {
        return idUserRegistro;
    }

    public void setIdUserRegistro(Long idUserRegistro) {
        this.idUserRegistro = idUserRegistro;
    }

    public Date getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(Date fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    public Long getIdUserModificacion() {
        return idUserModificacion;
    }

    public void setIdUserModificacion(Long idUserModificacion) {
        this.idUserModificacion = idUserModificacion;
    }

    public Date getFechaModificacion() {
        return fechaModificacion;
    }

    public void setFechaModificacion(Date fechaModificacion) {
        this.fechaModificacion = fechaModificacion;
    }

    public List<AlumnoCicloCurso> getAlumnoCicloCurso() {
        return alumnoCicloCurso;
    }

    public void setAlumnoCicloCurso(List<AlumnoCicloCurso> alumnoCicloCurso) {
        this.alumnoCicloCurso = alumnoCicloCurso;
    }

}

