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
import pe.edu.lamolina.pivot.model.inscripcion.CarreraPostula;
import pe.edu.lamolina.pivot.model.inscripcion.Evaluado;
import pe.edu.lamolina.pivot.model.inscripcion.Ingresante;
import pe.edu.lamolina.pivot.zelper.enums.EstadoEnum;
import pe.edu.lamolina.pivot.zelper.enums.TipoCarreraEnum;

@Entity
@Table(name = "aca_carrera")
public class Carrera implements Serializable {

    @Id
    @GeneratedValue
    @Column(name = "id")
    private Long id;

    @Column(name = "nombre")
    private String nombre;

    @Column(name = "codigo")
    private String codigo;

    @Column(name = "estado")
    private String estado;

    @Column(name = "tipo")
    private String tipo;

    @Column(name = "nombre_corto")
    private String nombreCorto;

    @Column(name = "nombre_resultado")
    private String nombreResultado;

    @Column(name = "motivo")
    private String motivo;

    @Column(name = "id_user_registro")
    private Long idUserRegistro;

    @Column(name = "fecha_registro")
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date fechaRegistro;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_modalidad_estudio")
    private ModalidadEstudio modalidadEstudio;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_facultad")
    private Facultad facultad;

    @OneToMany(mappedBy = "carrera", fetch = FetchType.LAZY)
    private List<Alumno> alumno;

    @OneToMany(mappedBy = "carrera", fetch = FetchType.LAZY)
    private List<AlumnoCiclo> alumnoCiclo;

    @OneToMany(mappedBy = "carrera", fetch = FetchType.LAZY)
    private List<OrientacionCarrera> orientacionCarrera;

    @OneToMany(mappedBy = "carrera", fetch = FetchType.LAZY)
    private List<PlanCurricular> planCurricular;

    @OneToMany(mappedBy = "carrera", fetch = FetchType.LAZY)
    private List<CarreraPostula> carreraPostula;

    @OneToMany(mappedBy = "carreraIngreso", fetch = FetchType.LAZY)
    private List<Evaluado> evaluado;

    @OneToMany(mappedBy = "carrera", fetch = FetchType.LAZY)
    private List<Ingresante> ingresante;

    public Carrera() {
    }

    public Carrera(Object id) {
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

    public Facultad getFacultad() {
        return facultad;
    }

    public void setFacultad(Facultad facultad) {
        this.facultad = facultad;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getEstado() {
        return estado;
    }

    public EstadoEnum getEstadoEnum() {
        return estado != null ? EstadoEnum.valueOf(estado) : null;
    }

    public void setEstado(EstadoEnum estado) {
        this.estado = estado.name();
    }

    public String getTipo() {
        return tipo;
    }

    public TipoCarreraEnum getTipoEnum() {
        return tipo != null ? TipoCarreraEnum.valueOf(tipo) : null;
    }

    public void setTipo(TipoCarreraEnum tipo) {
        this.tipo = tipo.name();
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

    public List<AlumnoCiclo> getAlumnoCiclo() {
        return alumnoCiclo;
    }

    public void setAlumnoCiclo(List<AlumnoCiclo> alumnoCiclo) {
        this.alumnoCiclo = alumnoCiclo;
    }

    public List<OrientacionCarrera> getOrientacionCarrera() {
        return orientacionCarrera;
    }

    public void setOrientacionCarrera(List<OrientacionCarrera> orientacionCarrera) {
        this.orientacionCarrera = orientacionCarrera;
    }

    public List<PlanCurricular> getPlanCurricular() {
        return planCurricular;
    }

    public void setPlanCurricular(List<PlanCurricular> planCurricular) {
        this.planCurricular = planCurricular;
    }

    public List<CarreraPostula> getCarreraPostula() {
        return carreraPostula;
    }

    public void setCarreraPostula(List<CarreraPostula> carreraPostula) {
        this.carreraPostula = carreraPostula;
    }

    public List<Evaluado> getEvaluado() {
        return evaluado;
    }

    public void setEvaluado(List<Evaluado> evaluado) {
        this.evaluado = evaluado;
    }

    public List<Ingresante> getIngresante() {
        return ingresante;
    }

    public void setIngresante(List<Ingresante> ingresante) {
        this.ingresante = ingresante;
    }

    public String getNombreCorto() {
        return nombreCorto;
    }

    public void setNombreCorto(String nombreCorto) {
        this.nombreCorto = nombreCorto;
    }

    public String getNombreResultado() {
        return nombreResultado;
    }

    public void setNombreResultado(String nombreResultado) {
        this.nombreResultado = nombreResultado;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

}
