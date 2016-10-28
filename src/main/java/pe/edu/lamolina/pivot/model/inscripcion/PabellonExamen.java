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
import pe.edu.lamolina.pivot.model.general.Pabellon;

@Entity
@Table(name = "sip_pabellon_examen")
public class PabellonExamen implements Serializable {

    @Id
    @GeneratedValue
    @Column(name = "id")
    private Long id;

    @Column(name = "aforo")
    private Integer aforo;

    @Column(name = "aulas")
    private Integer aulas;

    @Column(name = "id_user_registro")
    private Long idUserRegistro;

    @Column(name = "fecha_registro")
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date fechaRegistro;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_ciclo_postula")
    private CicloPostula cicloPostula;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_agrupacion_modalidades")
    private AgrupacionModalidades agrupacionModalidades;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_pabellon")
    private Pabellon pabellon;

    @OneToMany(mappedBy = "pabellonExamen", fetch = FetchType.LAZY)
    private List<AulaExamen> aulaExamen;

    public PabellonExamen() {
    }

    public PabellonExamen(Object id) {
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

    public AgrupacionModalidades getAgrupacionModalidades() {
        return agrupacionModalidades;
    }

    public void setAgrupacionModalidades(AgrupacionModalidades agrupacionModalidades) {
        this.agrupacionModalidades = agrupacionModalidades;
    }

    public Pabellon getPabellon() {
        return pabellon;
    }

    public void setPabellon(Pabellon pabellon) {
        this.pabellon = pabellon;
    }

    public Integer getAforo() {
        return aforo;
    }

    public void setAforo(Integer aforo) {
        this.aforo = aforo;
    }

    public Integer getAulas() {
        return aulas;
    }

    public void setAulas(Integer aulas) {
        this.aulas = aulas;
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

    public List<AulaExamen> getAulaExamen() {
        return aulaExamen;
    }

    public void setAulaExamen(List<AulaExamen> aulaExamen) {
        this.aulaExamen = aulaExamen;
    }

}

