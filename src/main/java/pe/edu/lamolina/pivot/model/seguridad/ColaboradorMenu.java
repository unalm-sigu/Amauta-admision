package pe.edu.lamolina.pivot.model.seguridad;

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
import pe.edu.lamolina.pivot.model.general.Colaborador;


@Entity
@Table(name = "seg_colaborador_menu")
public class ColaboradorMenu implements Serializable {

    @Id
    @GeneratedValue
    @Column(name = "id")
    private Long id;

    @Column(name = "estado")
    private String estado;

    @Column(name = "id_user_acceso")
    private Long idUserAcceso;

    @Column(name = "id_user_revocar")
    private Long idUserRevocar;

    @Column(name = "fecha_acceso")
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date fechaAcceso;

    @Column(name = "fecha_revocar")
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date fechaRevocar;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_menu")
    private Menu menu;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_colaborador")
    private Colaborador colaborador;

    public ColaboradorMenu() {
    }

    public ColaboradorMenu(Object id) {
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

    public Long getIdUserAcceso() {
        return idUserAcceso;
    }

    public void setIdUserAcceso(Long idUserAcceso) {
        this.idUserAcceso = idUserAcceso;
    }

    public Long getIdUserRevocar() {
        return idUserRevocar;
    }

    public void setIdUserRevocar(Long idUserRevocar) {
        this.idUserRevocar = idUserRevocar;
    }

    public Date getFechaAcceso() {
        return fechaAcceso;
    }

    public void setFechaAcceso(Date fechaAcceso) {
        this.fechaAcceso = fechaAcceso;
    }

    public Date getFechaRevocar() {
        return fechaRevocar;
    }

    public void setFechaRevocar(Date fechaRevocar) {
        this.fechaRevocar = fechaRevocar;
    }

    public Menu getMenu() {
        return menu;
    }

    public void setMenu(Menu menu) {
        this.menu = menu;
    }

    public Colaborador getColaborador() {
        return colaborador;
    }

    public void setColaborador(Colaborador colaborador) {
        this.colaborador = colaborador;
    }

}
