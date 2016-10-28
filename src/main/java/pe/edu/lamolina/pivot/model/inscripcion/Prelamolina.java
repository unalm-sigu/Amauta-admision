package pe.edu.lamolina.pivot.model.inscripcion;

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

@Entity
@Table(name = "sip_prelamolina")
public class Prelamolina implements Serializable {

    @Id
    @GeneratedValue
    @Column(name = "id")
    private Long id;

    @Column(name = "id_carrera_postula")
    private Long idCarreraPostula;

    @Column(name = "estado")
    private String estado;

    @Column(name = "codigo")
    private String codigo;

    @Column(name = "paterno")
    private String paterno;

    @Column(name = "materno")
    private String materno;

    @Column(name = "nombres")
    private String nombres;

    @Column(name = "sexo")
    private String sexo;

    @Column(name = "email")
    private String email;

    @Column(name = "carrera")
    private String carrera;

    @Column(name = "fecha_inscripcion")
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date fechaInscripcion;

    @Column(name = "fecha_registro")
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date fechaRegistro;

    @Column(name = "id_user_registro")
    private Long idUserRegistro;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_ciclo_postula")
    private CarreraPostula cicloPostula;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_postulante")
    private Postulante postulante;

    @OneToMany(mappedBy = "prelamolina", fetch = FetchType.LAZY)
    private List<Ingresante> ingresante;

    public Prelamolina() {
    }

    public Prelamolina(Object id) {
        this.id = TypesUtil.getLong(id);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public CarreraPostula getCicloPostula() {
        return cicloPostula;
    }

    public void setCicloPostula(CarreraPostula cicloPostula) {
        this.cicloPostula = cicloPostula;
    }

    public Postulante getPostulante() {
        return postulante;
    }

    public void setPostulante(Postulante postulante) {
        this.postulante = postulante;
    }

    public Long getIdCarreraPostula() {
        return idCarreraPostula;
    }

    public void setIdCarreraPostula(Long idCarreraPostula) {
        this.idCarreraPostula = idCarreraPostula;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getPaterno() {
        return paterno;
    }

    public void setPaterno(String paterno) {
        this.paterno = paterno;
    }

    public String getMaterno() {
        return materno;
    }

    public void setMaterno(String materno) {
        this.materno = materno;
    }

    public String getNombres() {
        return nombres;
    }

    public void setNombres(String nombres) {
        this.nombres = nombres;
    }

    public String getSexo() {
        return sexo;
    }

    public void setSexo(String sexo) {
        this.sexo = sexo;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCarrera() {
        return carrera;
    }

    public void setCarrera(String carrera) {
        this.carrera = carrera;
    }

    public Date getFechaInscripcion() {
        return fechaInscripcion;
    }

    public void setFechaInscripcion(Date fechaInscripcion) {
        this.fechaInscripcion = fechaInscripcion;
    }

    public Date getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(Date fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    public Long getIdUserRegistro() {
        return idUserRegistro;
    }

    public void setIdUserRegistro(Long idUserRegistro) {
        this.idUserRegistro = idUserRegistro;
    }

    public List<Ingresante> getIngresante() {
        return ingresante;
    }

    public void setIngresante(List<Ingresante> ingresante) {
        this.ingresante = ingresante;
    }

}

