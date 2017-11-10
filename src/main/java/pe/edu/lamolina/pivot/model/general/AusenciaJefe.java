package pe.edu.lamolina.pivot.model.general;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.pivot.model.seguridad.Usuario;

@Entity
@Table(name = "gen_ausencia_jefe")
public class AusenciaJefe implements Serializable {

    @Id
    @GeneratedValue
    @Column(name = "id")
    private Long id;

    @Column(name = "motivo")
    private String motivo;

    @Column(name = "fecha")
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date fecha;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_oficina")
    private Oficina oficina;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_jefe")
    private Persona jefe;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_encargado")
    private Persona encargado;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;

    public AusenciaJefe() {
    }

    public AusenciaJefe(Object id) {
        this.id = TypesUtil.getLong(id);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public Oficina getOficina() {
        return oficina;
    }

    public void setOficina(Oficina oficina) {
        this.oficina = oficina;
    }

    public Persona getJefe() {
        return jefe;
    }

    public void setJefe(Persona jefe) {
        this.jefe = jefe;
    }

    public Persona getEncargado() {
        return encargado;
    }

    public void setEncargado(Persona encargado) {
        this.encargado = encargado;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

}
