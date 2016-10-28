package pe.edu.lamolina.pivot.model.academico;

import java.io.Serializable;
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
import pe.edu.lamolina.pivot.model.inscripcion.CicloPostula;
import pe.edu.lamolina.pivot.model.tramite.RetiroCiclo;
import pe.edu.lamolina.pivot.model.tramite.RetiroCurso;
import pe.edu.lamolina.pivot.model.tramite.Tramite;

@Entity
@Table(name = "aca_ciclo_academico")
public class CicloAcademico implements Serializable {

    @Id
    @GeneratedValue
    @Column(name = "id")
    private Long id;

    @Column(name = "year")
    private Integer year;

    @Column(name = "numero_ciclo")
    private Integer numeroCiclo;

    @Column(name = "codigo")
    private String codigo;

    @Column(name = "tipo")
    private String tipo;

    @Column(name = "estado")
    private String estado;

    @Column(name = "descripcion")
    private String descripcion;

    @Column(name = "id_user_registro")
    private Long idUserRegistro;

    @Column(name = "fecha_registro")
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date fechaRegistro;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_modalidad_estudio")
    private ModalidadEstudio modalidadEstudio;

    @OneToMany(mappedBy = "cicloIngreso", fetch = FetchType.LAZY)
    private List<Alumno> alumno;

    @OneToMany(mappedBy = "cicloActivo", fetch = FetchType.LAZY)
    private List<Alumno> alumno1;

    @OneToMany(mappedBy = "cicloAcademico", fetch = FetchType.LAZY)
    private List<AlumnoCiclo> alumnoCiclo;

    @OneToMany(mappedBy = "cicloAcademico", fetch = FetchType.LAZY)
    private List<ConfiguracionReclamoNota> configuracionReclamoNota;

    @OneToMany(mappedBy = "cicloAcademico", fetch = FetchType.LAZY)
    private List<LoggerMatricula> loggerMatricula;

    @OneToMany(mappedBy = "cicloAcademico", fetch = FetchType.LAZY)
    private List<MatriculaResumen> matriculaResumen;

    @OneToMany(mappedBy = "ciclo", fetch = FetchType.LAZY)
    private List<Seccion> seccion;

    @OneToMany(mappedBy = "cicloAcademico", fetch = FetchType.LAZY)
    private List<CicloPostula> cicloPostula;

    @OneToMany(mappedBy = "cicloAcademico", fetch = FetchType.LAZY)
    private List<RetiroCiclo> retiroCiclo;

    @OneToMany(mappedBy = "cicloAcademico", fetch = FetchType.LAZY)
    private List<RetiroCurso> retiroCurso;

    @OneToMany(mappedBy = "cicloAcademico", fetch = FetchType.LAZY)
    private List<Tramite> tramite;

    public CicloAcademico() {
    }

    public CicloAcademico(Object id) {
        this.id = TypesUtil.getLong(id);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ModalidadEstudio getModalidadEstudio() {
        return modalidadEstudio;
    }

    public void setModalidadEstudio(ModalidadEstudio modalidadEstudio) {
        this.modalidadEstudio = modalidadEstudio;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public Integer getNumeroCiclo() {
        return numeroCiclo;
    }

    public void setNumeroCiclo(Integer numeroCiclo) {
        this.numeroCiclo = numeroCiclo;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
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

    public List<Alumno> getAlumno() {
        return alumno;
    }

    public void setAlumno(List<Alumno> alumno) {
        this.alumno = alumno;
    }

    public List<Alumno> getAlumno1() {
        return alumno1;
    }

    public void setAlumno1(List<Alumno> alumno1) {
        this.alumno1 = alumno1;
    }

    public List<AlumnoCiclo> getAlumnoCiclo() {
        return alumnoCiclo;
    }

    public void setAlumnoCiclo(List<AlumnoCiclo> alumnoCiclo) {
        this.alumnoCiclo = alumnoCiclo;
    }

    public List<ConfiguracionReclamoNota> getConfiguracionReclamoNota() {
        return configuracionReclamoNota;
    }

    public void setConfiguracionReclamoNota(List<ConfiguracionReclamoNota> configuracionReclamoNota) {
        this.configuracionReclamoNota = configuracionReclamoNota;
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

    public List<Seccion> getSeccion() {
        return seccion;
    }

    public void setSeccion(List<Seccion> seccion) {
        this.seccion = seccion;
    }

    public List<CicloPostula> getCicloPostula() {
        return cicloPostula;
    }

    public void setCicloPostula(List<CicloPostula> cicloPostula) {
        this.cicloPostula = cicloPostula;
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

}

