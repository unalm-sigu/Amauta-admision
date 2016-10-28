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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_pabellon")
    private Pabellon pabellon;

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

    public Pabellon getPabellon() {
        return pabellon;
    }

    public void setPabellon(Pabellon pabellon) {
        this.pabellon = pabellon;
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

}

