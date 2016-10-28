package pe.edu.lamolina.pivot.model.inscripcion;

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
@Table(name = "sip_evento")
public class Evento implements Serializable {

    @Id
    @GeneratedValue
    @Column(name = "id")
    private Long id;

    @Column(name = "codigo")
    private String codigo;

    @Column(name = "descripcion")
    private String descripcion;

    @OneToMany(mappedBy = "evento", fetch = FetchType.LAZY)
    private List<EventoCiclo> eventoCiclo;

    public Evento() {
    }

    public Evento(Object id) {
        this.id = TypesUtil.getLong(id);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public List<EventoCiclo> getEventoCiclo() {
        return eventoCiclo;
    }

    public void setEventoCiclo(List<EventoCiclo> eventoCiclo) {
        this.eventoCiclo = eventoCiclo;
    }

}

