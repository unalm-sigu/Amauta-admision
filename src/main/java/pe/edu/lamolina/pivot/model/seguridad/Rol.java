package pe.edu.lamolina.pivot.model.seguridad;

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
@Table(name = "seg_rol")
public class Rol implements Serializable {

    @Id
    @GeneratedValue
    @Column(name = "id")
    private Long id;

    @Column(name = "nombre")
    private String nombre;

    @Column(name = "codigo")
    private String codigo;

    @OneToMany(mappedBy = "rol", fetch = FetchType.LAZY)
    private List<UsuarioRol> usuarioRol;

    public Rol() {
    }

    public Rol(Object id) {
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

    public List<UsuarioRol> getUsuarioRol() {
        return usuarioRol;
    }

    public void setUsuarioRol(List<UsuarioRol> usuarioRol) {
        this.usuarioRol = usuarioRol;
    }

}

