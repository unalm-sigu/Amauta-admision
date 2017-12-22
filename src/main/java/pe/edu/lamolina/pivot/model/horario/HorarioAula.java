package pe.edu.lamolina.pivot.model.horario;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.Serializable;
import java.math.BigDecimal;
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
import pe.albatross.zelpers.miscelanea.JsonHelper;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.pivot.model.academico.Seccion;
import pe.edu.lamolina.pivot.model.general.Aula;
import pe.edu.lamolina.pivot.model.general.Dia;

@Entity
@Table(name = "hor_horario_aula")
public class HorarioAula implements Serializable {

    @Id
    @GeneratedValue
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_aula")
    private Aula aula;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_dia")
    private Dia dia;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_hora")
    private Hora hora;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_seccion")
    private Seccion seccion;

    public HorarioAula() {
    }

    public HorarioAula(Object id) {
        this.id = TypesUtil.getLong(id);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Aula getAula() {
        return aula;
    }

    public void setAula(Aula aula) {
        this.aula = aula;
    }

    public Dia getDia() {
        return dia;
    }

    public void setDia(Dia dia) {
        this.dia = dia;
    }

    public Hora getHora() {
        return hora;
    }

    public void setHora(Hora hora) {
        this.hora = hora;
    }

    public Seccion getSeccion() {
        return seccion;
    }

    public void setSeccion(Seccion seccion) {
        this.seccion = seccion;
    }

    public ObjectNode toJson() {
        JsonNodeFactory nf = JsonNodeFactory.instance;
        ObjectNode node = new ObjectNode(nf);
        node.put("id", this.getId());
        node.putPOJO("dia", JsonHelper.createJson(this.getDia(), JsonNodeFactory.instance));
        node.putPOJO("hora", JsonHelper.createJson(this.getHora(), JsonNodeFactory.instance));
        node.putPOJO("seccion", JsonHelper.createJson(this.getSeccion(), JsonNodeFactory.instance));
        node.putPOJO("aula", JsonHelper.createJson(this.getAula(), JsonNodeFactory.instance));
        return node;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof HorarioAula)) {
            return false;
        }
        HorarioAula other = (HorarioAula) obj;
        if (id != other.id) {
            return false;
        }
        return true;
    }

}
