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
import pe.edu.lamolina.pivot.model.academico.AlumnoCicloCurso;

@Entity
@Table(name = "tram_autorizacion_registro")
public class AutorizacionRegistro implements Serializable {

    @Id
    @GeneratedValue
    @Column(name = "id")
    private Long id;

    @Column(name = "motivo")
    private String motivo;

    @Column(name = "estado")
    private String estado;

    @Column(name = "descripcion")
    private String descripcion;

    @Column(name = "id_user_autoriza")
    private Long idUserAutoriza;

    @Column(name = "fecha_autorizacion")
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date fechaAutorizacion;

    @Column(name = "id_user_cierre")
    private Long idUserCierre;

    @Column(name = "fecha_cierre")
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date fechaCierre;

    @Column(name = "id_user_registro")
    private Long idUserRegistro;

    @Column(name = "fecha_registro")
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date fechaRegistro;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_alumno")
    private Alumno alumno;

    @OneToMany(mappedBy = "autorizacionRegistro", fetch = FetchType.LAZY)
    private List<AlumnoCicloCurso> alumnoCicloCurso;

    public AutorizacionRegistro() {
    }

    public AutorizacionRegistro(Object id) {
        this.id = TypesUtil.getLong(id);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Alumno getAlumno() {
        return alumno;
    }

    public void setAlumno(Alumno alumno) {
        this.alumno = alumno;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Long getIdUserAutoriza() {
        return idUserAutoriza;
    }

    public void setIdUserAutoriza(Long idUserAutoriza) {
        this.idUserAutoriza = idUserAutoriza;
    }

    public Date getFechaAutorizacion() {
        return fechaAutorizacion;
    }

    public void setFechaAutorizacion(Date fechaAutorizacion) {
        this.fechaAutorizacion = fechaAutorizacion;
    }

    public Long getIdUserCierre() {
        return idUserCierre;
    }

    public void setIdUserCierre(Long idUserCierre) {
        this.idUserCierre = idUserCierre;
    }

    public Date getFechaCierre() {
        return fechaCierre;
    }

    public void setFechaCierre(Date fechaCierre) {
        this.fechaCierre = fechaCierre;
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

    public List<AlumnoCicloCurso> getAlumnoCicloCurso() {
        return alumnoCicloCurso;
    }

    public void setAlumnoCicloCurso(List<AlumnoCicloCurso> alumnoCicloCurso) {
        this.alumnoCicloCurso = alumnoCicloCurso;
    }

}

