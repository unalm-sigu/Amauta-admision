package pe.edu.lamolina.pivot.model.inscripcion;

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
import pe.edu.lamolina.pivot.model.academico.Carrera;

@Entity
@Table(name = "sip_evaluado")
public class Evaluado implements Serializable {

    @Id
    @GeneratedValue
    @Column(name = "id")
    private Long id;

    @Column(name = "estado")
    private String estado;

    @Column(name = "puntaje_final")
    private BigDecimal puntajeFinal;

    @Column(name = "puntaje_rm")
    private BigDecimal puntajeRm;

    @Column(name = "puntaje_rv")
    private BigDecimal puntajeRv;

    @Column(name = "puntaje_matematicas")
    private BigDecimal puntajeMatematicas;

    @Column(name = "puntaje_fisica")
    private BigDecimal puntajeFisica;

    @Column(name = "puntaje_quimica")
    private BigDecimal puntajeQuimica;

    @Column(name = "puntaje_biologia")
    private BigDecimal puntajeBiologia;

    @Column(name = "orden_merito")
    private Integer ordenMerito;

    @Column(name = "id_user_registro")
    private Long idUserRegistro;

    @Column(name = "fecha_registro")
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date fechaRegistro;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_postulante")
    private Postulante postulante;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_carrera_ingreso")
    private Carrera carreraIngreso;

    @OneToMany(mappedBy = "evaluado", fetch = FetchType.LAZY)
    private List<Ingresante> ingresante;

    public Evaluado() {
    }

    public Evaluado(Object id) {
        this.id = TypesUtil.getLong(id);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Postulante getPostulante() {
        return postulante;
    }

    public void setPostulante(Postulante postulante) {
        this.postulante = postulante;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Carrera getCarreraIngreso() {
        return carreraIngreso;
    }

    public void setCarreraIngreso(Carrera carreraIngreso) {
        this.carreraIngreso = carreraIngreso;
    }

    public BigDecimal getPuntajeFinal() {
        return puntajeFinal;
    }

    public void setPuntajeFinal(BigDecimal puntajeFinal) {
        this.puntajeFinal = puntajeFinal;
    }

    public BigDecimal getPuntajeRm() {
        return puntajeRm;
    }

    public void setPuntajeRm(BigDecimal puntajeRm) {
        this.puntajeRm = puntajeRm;
    }

    public BigDecimal getPuntajeRv() {
        return puntajeRv;
    }

    public void setPuntajeRv(BigDecimal puntajeRv) {
        this.puntajeRv = puntajeRv;
    }

    public BigDecimal getPuntajeMatematicas() {
        return puntajeMatematicas;
    }

    public void setPuntajeMatematicas(BigDecimal puntajeMatematicas) {
        this.puntajeMatematicas = puntajeMatematicas;
    }

    public BigDecimal getPuntajeFisica() {
        return puntajeFisica;
    }

    public void setPuntajeFisica(BigDecimal puntajeFisica) {
        this.puntajeFisica = puntajeFisica;
    }

    public BigDecimal getPuntajeQuimica() {
        return puntajeQuimica;
    }

    public void setPuntajeQuimica(BigDecimal puntajeQuimica) {
        this.puntajeQuimica = puntajeQuimica;
    }

    public BigDecimal getPuntajeBiologia() {
        return puntajeBiologia;
    }

    public void setPuntajeBiologia(BigDecimal puntajeBiologia) {
        this.puntajeBiologia = puntajeBiologia;
    }

    public Integer getOrdenMerito() {
        return ordenMerito;
    }

    public void setOrdenMerito(Integer ordenMerito) {
        this.ordenMerito = ordenMerito;
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

    public List<Ingresante> getIngresante() {
        return ingresante;
    }

    public void setIngresante(List<Ingresante> ingresante) {
        this.ingresante = ingresante;
    }

}

