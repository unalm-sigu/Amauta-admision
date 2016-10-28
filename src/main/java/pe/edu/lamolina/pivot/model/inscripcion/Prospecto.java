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
@Table(name = "sip_prospecto")
public class Prospecto implements Serializable {

    @Id
    @GeneratedValue
    @Column(name = "id")
    private Long id;

    @Column(name = "codigo")
    private String codigo;

    @Column(name = "estado")
    private String estado;

    @Column(name = "es_prelamolina")
    private Integer esPrelamolina;

    @Column(name = "fecha_registro")
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date fechaRegistro;

    @Column(name = "fecha_utilizado")
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date fechaUtilizado;

    @Column(name = "id_user_registro")
    private Long idUserRegistro;

    @Column(name = "id_user_utilizado")
    private Long idUserUtilizado;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_ciclo_postula")
    private CicloPostula cicloPostula;

    @OneToMany(mappedBy = "prospecto", fetch = FetchType.LAZY)
    private List<Postulante> postulante;

    public Prospecto() {
    }

    public Prospecto(Object id) {
        this.id = TypesUtil.getLong(id);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public CicloPostula getCicloPostula() {
        return cicloPostula;
    }

    public void setCicloPostula(CicloPostula cicloPostula) {
        this.cicloPostula = cicloPostula;
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

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Integer getEsPrelamolina() {
        return esPrelamolina;
    }

    public void setEsPrelamolina(Integer esPrelamolina) {
        this.esPrelamolina = esPrelamolina;
    }

    public Date getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(Date fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    public Date getFechaUtilizado() {
        return fechaUtilizado;
    }

    public void setFechaUtilizado(Date fechaUtilizado) {
        this.fechaUtilizado = fechaUtilizado;
    }

    public Long getIdUserRegistro() {
        return idUserRegistro;
    }

    public void setIdUserRegistro(Long idUserRegistro) {
        this.idUserRegistro = idUserRegistro;
    }

    public Long getIdUserUtilizado() {
        return idUserUtilizado;
    }

    public void setIdUserUtilizado(Long idUserUtilizado) {
        this.idUserUtilizado = idUserUtilizado;
    }

    public List<Postulante> getPostulante() {
        return postulante;
    }

    public void setPostulante(List<Postulante> postulante) {
        this.postulante = postulante;
    }

}

