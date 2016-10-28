package pe.edu.lamolina.pivot.model.general;

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
import pe.edu.lamolina.pivot.model.inscripcion.Postulante;

@Entity
@Table(name = "gen_pais")
public class Pais implements Serializable {

    @Id
    @GeneratedValue
    @Column(name = "id")
    private Long id;

    @Column(name = "nombre")
    private String nombre;

    @Column(name = "codigo")
    private String codigo;

    @OneToMany(mappedBy = "paisNacer", fetch = FetchType.LAZY)
    private List<Persona> persona;

    @OneToMany(mappedBy = "nacionalidad", fetch = FetchType.LAZY)
    private List<Persona> persona1;

    @OneToMany(mappedBy = "pais", fetch = FetchType.LAZY)
    private List<Universidad> universidad;

    @OneToMany(mappedBy = "paisColegio", fetch = FetchType.LAZY)
    private List<Postulante> postulante;

    @OneToMany(mappedBy = "paisUniversidad", fetch = FetchType.LAZY)
    private List<Postulante> postulante1;

    public Pais() {
    }

    public Pais(Object id) {
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

    public List<Universidad> getUniversidad() {
        return universidad;
    }

    public void setUniversidad(List<Universidad> universidad) {
        this.universidad = universidad;
    }

    public List<Postulante> getPostulante() {
        return postulante;
    }

    public void setPostulante(List<Postulante> postulante) {
        this.postulante = postulante;
    }

    public List<Postulante> getPostulante1() {
        return postulante1;
    }

    public void setPostulante1(List<Postulante> postulante1) {
        this.postulante1 = postulante1;
    }

}

