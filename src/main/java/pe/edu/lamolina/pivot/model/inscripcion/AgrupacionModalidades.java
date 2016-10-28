package pe.edu.lamolina.pivot.model.inscripcion;

import java.io.Serializable;
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
import pe.albatross.zelpers.miscelanea.TypesUtil;

@Entity
@Table(name = "sip_agrupacion_modalidades")
public class AgrupacionModalidades implements Serializable {

    @Id
    @GeneratedValue
    @Column(name = "id")
    private Long id;

    @Column(name = "nombre")
    private String nombre;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_ciclo_postula")
    private CicloPostula cicloPostula;

    @OneToMany(mappedBy = "agrupacionModalidades", fetch = FetchType.LAZY)
    private List<AulaExamen> aulaExamen;

    @OneToMany(mappedBy = "agrupacionModalidades", fetch = FetchType.LAZY)
    private List<ModalidadGrupo> modalidadGrupo;

    @OneToMany(mappedBy = "agrupacionModalidades", fetch = FetchType.LAZY)
    private List<PabellonExamen> pabellonExamen;

    public AgrupacionModalidades() {
    }

    public AgrupacionModalidades(Object id) {
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

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public List<AulaExamen> getAulaExamen() {
        return aulaExamen;
    }

    public void setAulaExamen(List<AulaExamen> aulaExamen) {
        this.aulaExamen = aulaExamen;
    }

    public List<ModalidadGrupo> getModalidadGrupo() {
        return modalidadGrupo;
    }

    public void setModalidadGrupo(List<ModalidadGrupo> modalidadGrupo) {
        this.modalidadGrupo = modalidadGrupo;
    }

    public List<PabellonExamen> getPabellonExamen() {
        return pabellonExamen;
    }

    public void setPabellonExamen(List<PabellonExamen> pabellonExamen) {
        this.pabellonExamen = pabellonExamen;
    }

}

