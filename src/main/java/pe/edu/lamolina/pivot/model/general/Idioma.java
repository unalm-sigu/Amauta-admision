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
import pe.edu.lamolina.pivot.model.academico.NombreCurso;

@Entity
@Table(name = "gen_idioma")
public class Idioma implements Serializable {

    @Id
    @GeneratedValue
    @Column(name = "id")
    private Long id;

    @Column(name = "nombre")
    private String nombre;

    @Column(name = "codigo")
    private String codigo;

    @OneToMany(mappedBy = "idioma", fetch = FetchType.LAZY)
    private List<NombreCurso> nombreCurso;

    public Idioma() {
    }

    public Idioma(Object id) {
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

    public List<NombreCurso> getNombreCurso() {
        return nombreCurso;
    }

    public void setNombreCurso(List<NombreCurso> nombreCurso) {
        this.nombreCurso = nombreCurso;
    }

}

