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
import javax.persistence.Transient;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.pivot.model.seguridad.Usuario;
import pe.edu.lamolina.pivot.zelper.enums.EstadoMatriculaCursoEnum;

@Entity
@Table(name = "aca_matricula_seccion")
public class MatriculaSeccion implements Serializable {

    @Id
    @GeneratedValue
    @Column(name = "id")
    private Long id;

    @Column(name = "estado")
    private String estado;

    @Column(name = "id_user_registro")
    private Usuario userRegistro;

    @Column(name = "fecha_registro")
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date fechaRegistro;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_matricula_resumen")
    private MatriculaResumen matriculaResumen;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_seccion")
    private Seccion seccion;

    @Transient
    private String codigoAlumno;
    @Transient
    private String codigoSeccion;

    public MatriculaSeccion() {
    }

    public MatriculaSeccion(String codigoAlumno, String codigoSeccion) {
        this.codigoAlumno = codigoAlumno;
        this.codigoSeccion = codigoSeccion;
    }

    public MatriculaSeccion(Object id) {
        this.id = TypesUtil.getLong(id);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public MatriculaResumen getMatriculaResumen() {
        return matriculaResumen;
    }

    public void setMatriculaResumen(MatriculaResumen matriculaResumen) {
        this.matriculaResumen = matriculaResumen;
    }

    public Seccion getSeccion() {
        return seccion;
    }

    public void setSeccion(Seccion seccion) {
        this.seccion = seccion;
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

    public Usuario getUserRegistro() {
        return userRegistro;
    }

    public void setUserRegistro(Usuario userRegistro) {
        this.userRegistro = userRegistro;
    }

    public Date getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(Date fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    public String getCodigoAlumno() {
        return codigoAlumno;
    }

    public void setCodigoAlumno(String codigoAlumno) {
        this.codigoAlumno = codigoAlumno;
    }

    public String getCodigoSeccion() {
        return codigoSeccion;
    }

    public void setCodigoSeccion(String codigoSeccion) {
        this.codigoSeccion = codigoSeccion;
    }

}
