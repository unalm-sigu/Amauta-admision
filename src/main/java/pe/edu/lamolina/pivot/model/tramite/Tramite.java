package pe.edu.lamolina.pivot.model.tramite;

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
import pe.edu.lamolina.pivot.model.academico.Alumno;
import pe.edu.lamolina.pivot.model.academico.CicloAcademico;
import pe.edu.lamolina.pivot.model.general.Persona;

@Entity
@Table(name = "tram_tramite")
public class Tramite implements Serializable {

    @Id
    @GeneratedValue
    @Column(name = "id")
    private Long id;

    @Column(name = "id_compania")
    private Long idCompania;

    @Column(name = "serie")
    private Integer serie;

    @Column(name = "numero")
    private Integer numero;

    @Column(name = "estado")
    private String estado;

    @Column(name = "id_user_registro")
    private Long idUserRegistro;

    @Column(name = "fecha_registro")
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date fechaRegistro;

    @Column(name = "id_user_respuesta")
    private Long idUserRespuesta;

    @Column(name = "fecha_respuesta")
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date fechaRespuesta;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_persona")
    private Persona persona;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_alumno")
    private Alumno alumno;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_tipo_tramite")
    private TipoTramite tipoTramite;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_ciclo_academico")
    private CicloAcademico cicloAcademico;

    @OneToMany(mappedBy = "tramite", fetch = FetchType.LAZY)
    private List<RetiroCiclo> retiroCiclo;

    @OneToMany(mappedBy = "tramite", fetch = FetchType.LAZY)
    private List<RetiroCurso> retiroCurso;

    @OneToMany(mappedBy = "tramiteAnulacion", fetch = FetchType.LAZY)
    private List<RetiroCurso> retiroCurso1;

    public Tramite() {
    }

    public Tramite(Object id) {
        this.id = TypesUtil.getLong(id);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getIdCompania() {
        return idCompania;
    }

    public void setIdCompania(Long idCompania) {
        this.idCompania = idCompania;
    }

    public Integer getSerie() {
        return serie;
    }

    public void setSerie(Integer serie) {
        this.serie = serie;
    }

    public Integer getNumero() {
        return numero;
    }

    public void setNumero(Integer numero) {
        this.numero = numero;
    }

    public Persona getPersona() {
        return persona;
    }

    public void setPersona(Persona persona) {
        this.persona = persona;
    }

    public Alumno getAlumno() {
        return alumno;
    }

    public void setAlumno(Alumno alumno) {
        this.alumno = alumno;
    }

    public TipoTramite getTipoTramite() {
        return tipoTramite;
    }

    public void setTipoTramite(TipoTramite tipoTramite) {
        this.tipoTramite = tipoTramite;
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

    public Long getIdUserRespuesta() {
        return idUserRespuesta;
    }

    public void setIdUserRespuesta(Long idUserRespuesta) {
        this.idUserRespuesta = idUserRespuesta;
    }

    public Date getFechaRespuesta() {
        return fechaRespuesta;
    }

    public void setFechaRespuesta(Date fechaRespuesta) {
        this.fechaRespuesta = fechaRespuesta;
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

    public List<RetiroCurso> getRetiroCurso1() {
        return retiroCurso1;
    }

    public void setRetiroCurso1(List<RetiroCurso> retiroCurso1) {
        this.retiroCurso1 = retiroCurso1;
    }

}

