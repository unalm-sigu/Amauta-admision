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

@Entity
@Table(name = "aca_configuracion_reclamo_nota")
public class ConfiguracionReclamoNota implements Serializable {

    @Id
    @GeneratedValue
    @Column(name = "id")
    private Long id;

    @Column(name = "dias_despues_ef")
    private Integer diasDespuesEf;

    @Column(name = "dias_despues_ep")
    private Integer diasDespuesEp;

    @Column(name = "dias_despues_otros")
    private Integer diasDespuesOtros;

    @Column(name = "veces_reclamo")
    private Integer vecesReclamo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_ciclo_academico")
    private CicloAcademico cicloAcademico;

    public ConfiguracionReclamoNota() {
    }

    public ConfiguracionReclamoNota(Object id) {
        this.id = TypesUtil.getLong(id);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public CicloAcademico getCicloAcademico() {
        return cicloAcademico;
    }

    public void setCicloAcademico(CicloAcademico cicloAcademico) {
        this.cicloAcademico = cicloAcademico;
    }

    public Integer getDiasDespuesEf() {
        return diasDespuesEf;
    }

    public void setDiasDespuesEf(Integer diasDespuesEf) {
        this.diasDespuesEf = diasDespuesEf;
    }

    public Integer getDiasDespuesEp() {
        return diasDespuesEp;
    }

    public void setDiasDespuesEp(Integer diasDespuesEp) {
        this.diasDespuesEp = diasDespuesEp;
    }

    public Integer getDiasDespuesOtros() {
        return diasDespuesOtros;
    }

    public void setDiasDespuesOtros(Integer diasDespuesOtros) {
        this.diasDespuesOtros = diasDespuesOtros;
    }

    public Integer getVecesReclamo() {
        return vecesReclamo;
    }

    public void setVecesReclamo(Integer vecesReclamo) {
        this.vecesReclamo = vecesReclamo;
    }

}

