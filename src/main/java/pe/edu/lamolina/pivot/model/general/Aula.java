package pe.edu.lamolina.pivot.model.general;

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
import javax.persistence.Transient;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.pivot.model.academico.Seccion;
import pe.edu.lamolina.pivot.model.inscripcion.AulaExamen;

@Entity
@Table(name = "gen_aula")
public class Aula implements Serializable {

    @Id
    @GeneratedValue
    @Column(name = "id")
    private Long id;

    @Column(name = "codigo")
    private String codigo;

    @Column(name = "nombre")
    private String nombre;

    @Column(name = "aforo")
    private Integer aforo;

    @Column(name = "aforo_examen")
    private Integer aforoExamen;

    @Column(name = "capacidad_aula")
    private Integer capacidadAula;

    @Column(name = "permite_cruce")
    private Integer permiteCruce;

    @Column(name = "tipo_ambiente")
    private String tipoAmbiente;

    @Column(name = "piso")
    private Integer piso;

    @Column(name = "pisos")
    private Integer pisos;

    @Column(name = "estado")
    private String estado;

    @Column(name = "motivo_anulacion")
    private String motivoAnulacion;

    @Column(name = "fecha_anulacion")
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date fechaAnulacion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_aula_superior")
    private Aula aulaSuperior;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_sede")
    private Sede sede;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_tipo_aula")
    private TipoAula tipoAula;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_oficina_supervisora")
    private Oficina oficinaSupervisora;

    @OneToMany(mappedBy = "aula", fetch = FetchType.LAZY)
    private List<Seccion> seccion;

    @OneToMany(mappedBy = "aula", fetch = FetchType.LAZY)
    private List<AulaExamen> aulaExamen;

    @OneToMany(mappedBy = "aulaSuperior", fetch = FetchType.LAZY)
    private List<Aula> aula;

    @Transient
    private Boolean disponible;

    public Aula() {
    }

    public Aula(Object id) {
        this.id = TypesUtil.getLong(id);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TipoAula getTipoAula() {
        return tipoAula;
    }

    public void setTipoAula(TipoAula tipoAula) {
        this.tipoAula = tipoAula;
    }

    public Oficina getOficinaSupervisora() {
        return oficinaSupervisora;
    }

    public void setOficinaSupervisora(Oficina oficinaSupervisora) {
        this.oficinaSupervisora = oficinaSupervisora;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Integer getAforo() {
        return aforo;
    }

    public void setAforo(Integer aforo) {
        this.aforo = aforo;
    }

    public Integer getAforoExamen() {
        return aforoExamen;
    }

    public void setAforoExamen(Integer aforoExamen) {
        this.aforoExamen = aforoExamen;
    }

    public List<Seccion> getSeccion() {
        return seccion;
    }

    public void setSeccion(List<Seccion> seccion) {
        this.seccion = seccion;
    }

    public List<AulaExamen> getAulaExamen() {
        return aulaExamen;
    }

    public void setAulaExamen(List<AulaExamen> aulaExamen) {
        this.aulaExamen = aulaExamen;
    }

    public Integer getCapacidadAula() {
        return capacidadAula;
    }

    public void setCapacidadAula(Integer capacidadAula) {
        this.capacidadAula = capacidadAula;
    }

    public Integer getPermiteCruce() {
        return permiteCruce;
    }

    public void setPermiteCruce(Integer permiteCruce) {
        this.permiteCruce = permiteCruce;
    }

    public String getTipoAmbiente() {
        return tipoAmbiente;
    }

    public void setTipoAmbiente(String tipoAmbiente) {
        this.tipoAmbiente = tipoAmbiente;
    }

    public Integer getPiso() {
        return piso;
    }

    public void setPiso(Integer piso) {
        this.piso = piso;
    }

    public Integer getPisos() {
        return pisos;
    }

    public void setPisos(Integer pisos) {
        this.pisos = pisos;
    }

    public Aula getAulaSuperior() {
        return aulaSuperior;
    }

    public void setAulaSuperior(Aula aulaSuperior) {
        this.aulaSuperior = aulaSuperior;
    }

    public List<Aula> getAula() {
        return aula;
    }

    public void setAula(List<Aula> aula) {
        this.aula = aula;
    }

    public Sede getSede() {
        return sede;
    }

    public void setSede(Sede sede) {
        this.sede = sede;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getMotivoAnulacion() {
        return motivoAnulacion;
    }

    public void setMotivoAnulacion(String motivoAnulacion) {
        this.motivoAnulacion = motivoAnulacion;
    }

    public Date getFechaAnulacion() {
        return fechaAnulacion;
    }

    public void setFechaAnulacion(Date fechaAnulacion) {
        this.fechaAnulacion = fechaAnulacion;
    }

    public Boolean isDisponible() {
        return disponible;
    }

    public void setDisponible(Boolean disponible) {
        this.disponible = disponible;
    }

}
