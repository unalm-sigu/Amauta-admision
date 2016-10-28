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

@Entity
@Table(name = "gen_ubicacion")
public class Ubicacion implements Serializable {

    @Id
    @GeneratedValue
    @Column(name = "id")
    private Long id;

    @Column(name = "nombre")
    private String nombre;

    @Column(name = "codigo")
    private String codigo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_ubicacion_superior")
    private Ubicacion ubicacionSuperior;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_tipo_ubicacion")
    private TipoUbicacion tipoUbicacion;

    @OneToMany(mappedBy = "ubicacionNacer", fetch = FetchType.LAZY)
    private List<Persona> persona;

    @OneToMany(mappedBy = "ubicacionDomicilio", fetch = FetchType.LAZY)
    private List<Persona> persona1;

    @OneToMany(mappedBy = "ubicacionSuperior", fetch = FetchType.LAZY)
    private List<Ubicacion> ubicacion;

    public Ubicacion() {
    }

    public Ubicacion(Object id) {
        this.id = TypesUtil.getLong(id);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Ubicacion getUbicacionSuperior() {
        return ubicacionSuperior;
    }

    public void setUbicacionSuperior(Ubicacion ubicacionSuperior) {
        this.ubicacionSuperior = ubicacionSuperior;
    }

    public TipoUbicacion getTipoUbicacion() {
        return tipoUbicacion;
    }

    public void setTipoUbicacion(TipoUbicacion tipoUbicacion) {
        this.tipoUbicacion = tipoUbicacion;
    }

    public List<Persona> getPersona() {
        return persona;
    }

    public void setPersona(List<Persona> persona) {
        this.persona = persona;
    }

    public List<Persona> getPersona1() {
        return persona1;
    }

    public void setPersona1(List<Persona> persona1) {
        this.persona1 = persona1;
    }

    public List<Ubicacion> getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(List<Ubicacion> ubicacion) {
        this.ubicacion = ubicacion;
    }

}

