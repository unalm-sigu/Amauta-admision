package pe.edu.lamolina.pivot.model.horario;

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
import pe.edu.lamolina.pivot.model.academico.Seccion;

@Entity
@Table(name = "hor_seccion_horario_cachimbos")
public class SeccionHorarioCachimbos implements Serializable {

    @Id
    @GeneratedValue
    @Column(name = "id")
    private Long id;

    @Column(name = "id_user_creacion")
    private Long idUserCreacion;

    @Column(name = "fecha_creacion")
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date fechaCreacion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_seccion")
    private Seccion seccion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_horario_cachimbos")
    private HorarioCachimbos horarioCachimbos;

    public SeccionHorarioCachimbos() {
    }

    public SeccionHorarioCachimbos(Object id) {
        this.id = TypesUtil.getLong(id);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Seccion getSeccion() {
        return seccion;
    }

    public void setSeccion(Seccion seccion) {
        this.seccion = seccion;
    }

    public HorarioCachimbos getHorarioCachimbos() {
        return horarioCachimbos;
    }

    public void setHorarioCachimbos(HorarioCachimbos horarioCachimbos) {
        this.horarioCachimbos = horarioCachimbos;
    }

    public Long getIdUserCreacion() {
        return idUserCreacion;
    }

    public void setIdUserCreacion(Long idUserCreacion) {
        this.idUserCreacion = idUserCreacion;
    }

    public Date getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(Date fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

}

