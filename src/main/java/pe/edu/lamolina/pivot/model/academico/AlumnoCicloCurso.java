package pe.edu.lamolina.pivot.model.academico;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.pivot.model.tramite.AutorizacionRegistro;

@Entity
@Table(name = "aca_alumno_ciclo_curso")
public class AlumnoCicloCurso implements Serializable {

    @Id
    @GeneratedValue
    @Column(name = "id")
    private Long id;

    @Column(name = "estado")
    private String estado;

    @Column(name = "creditos")
    private Integer creditos;

    @Column(name = "nota")
    private String nota;

    @Column(name = "esta_aprobado")
    private Integer estaAprobado;

    @Column(name = "origen_data")
    private String origenData;

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
    @JoinColumn(name = "id_alumno_ciclo")
    private AlumnoCiclo alumnoCiclo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_curso")
    private Curso curso;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_autorizacion_registro")
    private AutorizacionRegistro autorizacionRegistro;

    public AlumnoCicloCurso() {
    }

    public AlumnoCicloCurso(Object id) {
        this.id = TypesUtil.getLong(id);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public AlumnoCiclo getAlumnoCiclo() {
        return alumnoCiclo;
    }

    public void setAlumnoCiclo(AlumnoCiclo alumnoCiclo) {
        this.alumnoCiclo = alumnoCiclo;
    }

    public Curso getCurso() {
        return curso;
    }

    public void setCurso(Curso curso) {
        this.curso = curso;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Integer getCreditos() {
        return creditos;
    }

    public void setCreditos(Integer creditos) {
        this.creditos = creditos;
    }

    public String getNota() {
        return nota;
    }

    public void setNota(String nota) {
        this.nota = nota;
    }

    public Integer getEstaAprobado() {
        return estaAprobado;
    }

    public void setEstaAprobado(Integer estaAprobado) {
        this.estaAprobado = estaAprobado;
    }

    public String getOrigenData() {
        return origenData;
    }

    public void setOrigenData(String origenData) {
        this.origenData = origenData;
    }

    public AutorizacionRegistro getAutorizacionRegistro() {
        return autorizacionRegistro;
    }

    public void setAutorizacionRegistro(AutorizacionRegistro autorizacionRegistro) {
        this.autorizacionRegistro = autorizacionRegistro;
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

}

