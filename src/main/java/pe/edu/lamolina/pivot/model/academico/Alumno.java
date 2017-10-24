package pe.edu.lamolina.pivot.model.academico;

import java.io.Serializable;
import java.math.BigDecimal;
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
import javax.persistence.Transient;
import org.apache.commons.lang3.StringUtils;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.pivot.model.general.Persona;
import pe.edu.lamolina.pivot.model.tramite.AutorizacionRegistro;
import pe.edu.lamolina.pivot.model.tramite.RetiroCiclo;
import pe.edu.lamolina.pivot.model.tramite.RetiroCurso;
import pe.edu.lamolina.pivot.model.tramite.Tramite;
import pe.edu.lamolina.pivot.zelper.enums.AlumnoEstadoEnum;

@Entity
@Table(name = "aca_alumno")
public class Alumno implements Serializable {

    @Id
    @GeneratedValue
    @Column(name = "id")
    private Long id;

    @Column(name = "codigo")
    private String codigo;

    @Column(name = "retiros_cursos")
    private Integer retirosCursos;

    @Column(name = "retiros_ciclos")
    private Integer retirosCiclos;

    @Column(name = "retiros_extemporaneos")
    private Integer retirosExtemporaneos;

    @Column(name = "creditos_cursados")
    private Integer creditosCursados;

    @Column(name = "creditos_aprobados")
    private Integer creditosAprobados;

    @Column(name = "cursos_inscritos")
    private Integer cursosInscritos;

    @Column(name = "cursos_aprobados")
    private Integer cursosAprobados;

    @Column(name = "promedio_acumulado")
    private BigDecimal promedioAcumulado;

    @Column(name = "creditos_carrera_cursados")
    private Integer creditosCarreraCursados;

    @Column(name = "creditos_carrera_aprobados")
    private Integer creditosCarreraAprobados;

    @Column(name = "cursos_carrera_inscritos")
    private Integer cursosCarreraInscritos;

    @Column(name = "cursos_carrera_aprobados")
    private Integer cursosCarreraAprobados;

    @Column(name = "promedio_carrera_acumulado")
    private BigDecimal promedioCarreraAcumulado;

    @Column(name = "estado")
    private String estado;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_persona")
    private Persona persona;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_carrera")
    private Carrera carrera;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_orientacion_carrera")
    private OrientacionCarrera orientacionCarrera;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_ciclo_ingreso")
    private CicloAcademico cicloIngreso;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_ciclo_activo")
    private CicloAcademico cicloActivo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_situacion_academica")
    private SituacionAcademica situacionAcademica;

    @OneToMany(mappedBy = "alumno", fetch = FetchType.LAZY)
    private List<AlumnoCiclo> alumnoCiclo;

    @OneToMany(mappedBy = "alumno", fetch = FetchType.LAZY)
    private List<AlumnoEvaluacion> alumnoEvaluacion;

    @OneToMany(mappedBy = "alumno", fetch = FetchType.LAZY)
    private List<LoggerMatricula> loggerMatricula;

    @OneToMany(mappedBy = "alumno", fetch = FetchType.LAZY)
    private List<MatriculaResumen> matriculaResumen;

    @OneToMany(mappedBy = "alumno", fetch = FetchType.LAZY)
    private List<ReclamoNota> reclamoNota;

    @OneToMany(mappedBy = "alumno", fetch = FetchType.LAZY)
    private List<AutorizacionRegistro> autorizacionRegistro;

    @OneToMany(mappedBy = "alumno", fetch = FetchType.LAZY)
    private List<RetiroCiclo> retiroCiclo;

    @OneToMany(mappedBy = "alumno", fetch = FetchType.LAZY)
    private List<RetiroCurso> retiroCurso;

    @OneToMany(mappedBy = "alumno", fetch = FetchType.LAZY)
    private List<Tramite> tramite;

    @Transient
    private String codigoEspecialidad;
    @Transient
    private String codigoPostgrado;
    @Transient
    private String situacion;
    @Transient
    private String email;

    public Alumno(String codigo, String codigoEspecialidad, String codigoPostgrado, String situacion, String email) {
        this.codigo = codigo;
        this.codigoEspecialidad = codigoEspecialidad;
        this.codigoPostgrado = codigoPostgrado;
        this.situacion = situacion;
        this.email = email;
    }

    public Alumno() {
    }

    public Alumno(Object id) {
        this.id = TypesUtil.getLong(id);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public Persona getPersona() {
        return persona;
    }

    public void setPersona(Persona persona) {
        this.persona = persona;
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

    public CicloAcademico getCicloIngreso() {
        return cicloIngreso;
    }

    public void setCicloIngreso(CicloAcademico cicloIngreso) {
        this.cicloIngreso = cicloIngreso;
    }

    public CicloAcademico getCicloActivo() {
        return cicloActivo;
    }

    public void setCicloActivo(CicloAcademico cicloActivo) {
        this.cicloActivo = cicloActivo;
    }

    public SituacionAcademica getSituacionAcademica() {
        return situacionAcademica;
    }

    public void setSituacionAcademica(SituacionAcademica situacionAcademica) {
        this.situacionAcademica = situacionAcademica;
    }

    public Integer getRetirosCursos() {
        return retirosCursos;
    }

    public void setRetirosCursos(Integer retirosCursos) {
        this.retirosCursos = retirosCursos;
    }

    public Integer getRetirosCiclos() {
        return retirosCiclos;
    }

    public void setRetirosCiclos(Integer retirosCiclos) {
        this.retirosCiclos = retirosCiclos;
    }

    public Integer getRetirosExtemporaneos() {
        return retirosExtemporaneos;
    }

    public void setRetirosExtemporaneos(Integer retirosExtemporaneos) {
        this.retirosExtemporaneos = retirosExtemporaneos;
    }

    public Integer getCreditosCursados() {
        return creditosCursados;
    }

    public void setCreditosCursados(Integer creditosCursados) {
        this.creditosCursados = creditosCursados;
    }

    public Integer getCreditosAprobados() {
        return creditosAprobados;
    }

    public void setCreditosAprobados(Integer creditosAprobados) {
        this.creditosAprobados = creditosAprobados;
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

    public BigDecimal getPromedioAcumulado() {
        return promedioAcumulado;
    }

    public void setPromedioAcumulado(BigDecimal promedioAcumulado) {
        this.promedioAcumulado = promedioAcumulado;
    }

    public Integer getCreditosCarreraCursados() {
        return creditosCarreraCursados;
    }

    public void setCreditosCarreraCursados(Integer creditosCarreraCursados) {
        this.creditosCarreraCursados = creditosCarreraCursados;
    }

    public Integer getCreditosCarreraAprobados() {
        return creditosCarreraAprobados;
    }

    public void setCreditosCarreraAprobados(Integer creditosCarreraAprobados) {
        this.creditosCarreraAprobados = creditosCarreraAprobados;
    }

    public Integer getCursosCarreraInscritos() {
        return cursosCarreraInscritos;
    }

    public void setCursosCarreraInscritos(Integer cursosCarreraInscritos) {
        this.cursosCarreraInscritos = cursosCarreraInscritos;
    }

    public Integer getCursosCarreraAprobados() {
        return cursosCarreraAprobados;
    }

    public void setCursosCarreraAprobados(Integer cursosCarreraAprobados) {
        this.cursosCarreraAprobados = cursosCarreraAprobados;
    }

    public BigDecimal getPromedioCarreraAcumulado() {
        return promedioCarreraAcumulado;
    }

    public void setPromedioCarreraAcumulado(BigDecimal promedioCarreraAcumulado) {
        this.promedioCarreraAcumulado = promedioCarreraAcumulado;
    }

    public List<AlumnoCiclo> getAlumnoCiclo() {
        return alumnoCiclo;
    }

    public void setAlumnoCiclo(List<AlumnoCiclo> alumnoCiclo) {
        this.alumnoCiclo = alumnoCiclo;
    }

    public List<AlumnoEvaluacion> getAlumnoEvaluacion() {
        return alumnoEvaluacion;
    }

    public void setAlumnoEvaluacion(List<AlumnoEvaluacion> alumnoEvaluacion) {
        this.alumnoEvaluacion = alumnoEvaluacion;
    }

    public List<LoggerMatricula> getLoggerMatricula() {
        return loggerMatricula;
    }

    public void setLoggerMatricula(List<LoggerMatricula> loggerMatricula) {
        this.loggerMatricula = loggerMatricula;
    }

    public List<MatriculaResumen> getMatriculaResumen() {
        return matriculaResumen;
    }

    public void setMatriculaResumen(List<MatriculaResumen> matriculaResumen) {
        this.matriculaResumen = matriculaResumen;
    }

    public List<ReclamoNota> getReclamoNota() {
        return reclamoNota;
    }

    public void setReclamoNota(List<ReclamoNota> reclamoNota) {
        this.reclamoNota = reclamoNota;
    }

    public List<AutorizacionRegistro> getAutorizacionRegistro() {
        return autorizacionRegistro;
    }

    public void setAutorizacionRegistro(List<AutorizacionRegistro> autorizacionRegistro) {
        this.autorizacionRegistro = autorizacionRegistro;
    }

    public List<RetiroCiclo> getRetiroCiclo() {
        return retiroCiclo;
    }

    public void setRetiroCiclo(List<RetiroCiclo> retiroCiclo) {
        this.retiroCiclo = retiroCiclo;
    }

    public List<RetiroCurso> getRetiroCurso() {
        return retiroCurso;
    }

    public void setRetiroCurso(List<RetiroCurso> retiroCurso) {
        this.retiroCurso = retiroCurso;
    }

    public List<Tramite> getTramite() {
        return tramite;
    }

    public void setTramite(List<Tramite> tramite) {
        this.tramite = tramite;
    }

    public String getCodigoEspecialidad() {
        return codigoEspecialidad;
    }

    public void setCodigoEspecialidad(String codigoEspecialidad) {
        this.codigoEspecialidad = codigoEspecialidad;
    }

    public String getCodigoPostgrado() {
        return codigoPostgrado;
    }

    public void setCodigoPostgrado(String codigoPostgrado) {
        this.codigoPostgrado = codigoPostgrado;
    }

    public String getSituacion() {
        return situacion;
    }

    public void setSituacion(String situacion) {
        this.situacion = situacion;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public AlumnoEstadoEnum getEstadoEnum() {
        if (StringUtils.isBlank(estado)) {
            return null;
        }
        return AlumnoEstadoEnum.valueOf(estado);
    }

}
