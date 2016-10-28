package pe.edu.lamolina.pivot.model.calificacion;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import pe.albatross.zelpers.miscelanea.TypesUtil;

@Entity
@Table(name = "sce_tema_examen")
public class TemaExamen implements Serializable {

    @Id
    @GeneratedValue
    @Column(name = "id")
    private Long id;

    @Column(name = "estado")
    private String estado;

    @Column(name = "nombre")
    private String nombre;

    @Column(name = "codigo")
    private String codigo;

    @OneToMany(mappedBy = "temaExamen", fetch = FetchType.LAZY)
    private List<TemaCiclo> temaCiclo;

    @OneToMany(mappedBy = "temaExamen", fetch = FetchType.LAZY)
    private List<TemaExamenModalidad> temaExamenModalidad;

    public TemaExamen() {
    }

    public TemaExamen(Object id) {
        this.id = TypesUtil.getLong(id);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
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

    public List<TemaCiclo> getTemaCiclo() {
        return temaCiclo;
    }

    public void setTemaCiclo(List<TemaCiclo> temaCiclo) {
        this.temaCiclo = temaCiclo;
    }

    public List<TemaExamenModalidad> getTemaExamenModalidad() {
        return temaExamenModalidad;
    }

    public void setTemaExamenModalidad(List<TemaExamenModalidad> temaExamenModalidad) {
        this.temaExamenModalidad = temaExamenModalidad;
    }

}

