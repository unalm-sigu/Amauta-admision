package pe.edu.lamolina.pivot.model.seguridad;

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
import pe.edu.lamolina.pivot.model.general.Persona;
import pe.edu.lamolina.pivot.zelper.enums.EstadoEnum;

@Entity
@Table(name = "seg_usuario")
public class Usuario implements Serializable {

    @Id
    @GeneratedValue
    @Column(name = "id")
    private Long id;

    @Column(name = "usuario")
    private String usuario;

    @Column(name = "estado")
    private String estado;

    @Column(name = "fecha_registro")
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date fechaRegistro;

    @Column(name = "fecha_modifica")
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date fechaMofica;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_persona")
    private Persona persona;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_user_registro")
    private Usuario userRegistro;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_user_modifica")
    private Usuario userModifica;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_user_activo")
    private Usuario userActivo;

    @OneToMany(mappedBy = "usuario", fetch = FetchType.LAZY)
    private List<UsuarioRol> usuarioRol;

    @OneToMany(mappedBy = "userActivo", fetch = FetchType.LAZY)
    private List<Usuario> usuariosNoActivos;

    public Usuario() {
    }

    public Usuario(Object id) {
        this.id = TypesUtil.getLong(id);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Persona getPersona() {
        return persona;
    }

    public void setPersona(Persona persona) {
        this.persona = persona;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public List<UsuarioRol> getUsuarioRol() {
        return usuarioRol;
    }

    public void setUsuarioRol(List<UsuarioRol> usuarioRol) {
        this.usuarioRol = usuarioRol;
    }

    public EstadoEnum getEstadoEnum() {
        return EstadoEnum.valueOf(estado);
    }

    public void setEstadoEnum(EstadoEnum estadoEnum) {
        this.estado = estadoEnum.name();
    }

    public Date getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(Date fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    public Date getFechaMofica() {
        return fechaMofica;
    }

    public void setFechaMofica(Date fechaMofica) {
        this.fechaMofica = fechaMofica;
    }

    public Usuario getUserRegistro() {
        return userRegistro;
    }

    public void setUserRegistro(Usuario userRegistro) {
        this.userRegistro = userRegistro;
    }

    public Usuario getUserModifica() {
        return userModifica;
    }

    public void setUserModifica(Usuario userModifica) {
        this.userModifica = userModifica;
    }

    public Usuario getUserActivo() {
        return userActivo;
    }

    public void setUserActivo(Usuario userActivo) {
        this.userActivo = userActivo;
    }

    public List<Usuario> getUsuariosNoActivos() {
        return usuariosNoActivos;
    }

    public void setUsuariosNoActivos(List<Usuario> usuariosNoActivos) {
        this.usuariosNoActivos = usuariosNoActivos;
    }

    public boolean isEstadoLike(EstadoEnum estado) {
        if (this.getEstadoEnum() == estado) {
            return true;
        }
        return false;
    }

}
