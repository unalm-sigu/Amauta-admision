package pe.edu.lamolina.pivot.model.general;

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
import pe.edu.lamolina.pivot.model.academico.DistanciaPabellon;
import pe.edu.lamolina.pivot.model.inscripcion.PabellonExamen;

@Entity
@Table(name = "gen_pabellon")
public class Pabellon implements Serializable {

    @Id
    @GeneratedValue
    @Column(name = "id")
    private Long id;

    @Column(name = "codigo")
    private String codigo;

    @Column(name = "nombre")
    private String nombre;

    @Column(name = "contiene_aulas")
    private Integer contieneAulas;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_sede")
    private Sede sede;

    @OneToMany(mappedBy = "pabellon", fetch = FetchType.LAZY)
    private List<DistanciaPabellon> distanciaPabellon;

    @OneToMany(mappedBy = "pabellon", fetch = FetchType.LAZY)
    private List<PabellonExamen> pabellonExamen;

    public Pabellon() {
    }

    public Pabellon(Object id) {
        this.id = TypesUtil.getLong(id);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Sede getSede() {
        return sede;
    }

    public void setSede(Sede sede) {
        this.sede = sede;
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

    public Integer getContieneAulas() {
        return contieneAulas;
    }

    public void setContieneAulas(Integer contieneAulas) {
        this.contieneAulas = contieneAulas;
    }

    public List<DistanciaPabellon> getDistanciaPabellon() {
        return distanciaPabellon;
    }

    public void setDistanciaPabellon(List<DistanciaPabellon> distanciaPabellon) {
        this.distanciaPabellon = distanciaPabellon;
    }

    public List<PabellonExamen> getPabellonExamen() {
        return pabellonExamen;
    }

    public void setPabellonExamen(List<PabellonExamen> pabellonExamen) {
        this.pabellonExamen = pabellonExamen;
    }

}
