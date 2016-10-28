package pe.edu.lamolina.pivot.model.academico;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.pivot.model.general.Pabellon;

@Entity
@Table(name = "aca_distancia_pabellon")
public class DistanciaPabellon implements Serializable {

    @Id
    @GeneratedValue
    @Column(name = "id")
    private Long id;

    @Column(name = "distancia")
    private Integer distancia;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_pabellon")
    private Pabellon pabellon;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_departamento_academico")
    private DepartamentoAcademico departamentoAcademico;

    public DistanciaPabellon() {
    }

    public DistanciaPabellon(Object id) {
        this.id = TypesUtil.getLong(id);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Pabellon getPabellon() {
        return pabellon;
    }

    public void setPabellon(Pabellon pabellon) {
        this.pabellon = pabellon;
    }

    public DepartamentoAcademico getDepartamentoAcademico() {
        return departamentoAcademico;
    }

    public void setDepartamentoAcademico(DepartamentoAcademico departamentoAcademico) {
        this.departamentoAcademico = departamentoAcademico;
    }

    public Integer getDistancia() {
        return distancia;
    }

    public void setDistancia(Integer distancia) {
        this.distancia = distancia;
    }

}

