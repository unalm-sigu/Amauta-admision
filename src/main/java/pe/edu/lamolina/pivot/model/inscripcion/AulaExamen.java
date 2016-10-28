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
import pe.edu.lamolina.pivot.model.general.Aula;

@Entity
@Table(name = "sip_aula_examen")
public class AulaExamen implements Serializable {

    @Id
    @GeneratedValue
    @Column(name = "id")
    private Long id;

    @Column(name = "aforo")
    private Integer aforo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_pabellon_examen")
    private PabellonExamen pabellonExamen;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_aula")
    private Aula aula;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_agrupacion_modalidades")
    private AgrupacionModalidades agrupacionModalidades;

    @OneToMany(mappedBy = "aulaExamen", fetch = FetchType.LAZY)
    private List<Postulante> postulante;

    public AulaExamen() {
    }

    public AulaExamen(Object id) {
        this.id = TypesUtil.getLong(id);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public PabellonExamen getPabellonExamen() {
        return pabellonExamen;
    }

    public void setPabellonExamen(PabellonExamen pabellonExamen) {
        this.pabellonExamen = pabellonExamen;
    }

    public Aula getAula() {
        return aula;
    }

    public void setAula(Aula aula) {
        this.aula = aula;
    }

    public AgrupacionModalidades getAgrupacionModalidades() {
        return agrupacionModalidades;
    }

    public void setAgrupacionModalidades(AgrupacionModalidades agrupacionModalidades) {
        this.agrupacionModalidades = agrupacionModalidades;
    }

    public Integer getAforo() {
        return aforo;
    }

    public void setAforo(Integer aforo) {
        this.aforo = aforo;
    }

    public List<Postulante> getPostulante() {
        return postulante;
    }

    public void setPostulante(List<Postulante> postulante) {
        this.postulante = postulante;
    }

}

