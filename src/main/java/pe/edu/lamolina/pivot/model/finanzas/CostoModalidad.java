package pe.edu.lamolina.pivot.model.finanzas;

import java.io.Serializable;
import java.math.BigDecimal;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.pivot.model.inscripcion.CicloPostula;
import pe.edu.lamolina.pivot.model.inscripcion.ModalidadIngreso;

@Entity
@Table(name = "fin_costo_modalidad")
public class CostoModalidad implements Serializable {

    @Id
    @GeneratedValue
    @Column(name = "id")
    private Long id;

    @Column(name = "tipo_gestion")
    private String tipoGestion;

    @Column(name = "costo_regular")
    private BigDecimal costoRegular;

    @Column(name = "costo_adicional")
    private BigDecimal costoAdicional;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_ciclo_postula")
    private CicloPostula cicloPostula;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_modalidad_ingreso")
    private ModalidadIngreso modalidadIngreso;

    public CostoModalidad() {
    }

    public CostoModalidad(Object id) {
        this.id = TypesUtil.getLong(id);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public CicloPostula getCicloPostula() {
        return cicloPostula;
    }

    public void setCicloPostula(CicloPostula cicloPostula) {
        this.cicloPostula = cicloPostula;
    }

    public ModalidadIngreso getModalidadIngreso() {
        return modalidadIngreso;
    }

    public void setModalidadIngreso(ModalidadIngreso modalidadIngreso) {
        this.modalidadIngreso = modalidadIngreso;
    }

    public String getTipoGestion() {
        return tipoGestion;
    }

    public void setTipoGestion(String tipoGestion) {
        this.tipoGestion = tipoGestion;
    }

    public BigDecimal getCostoRegular() {
        return costoRegular;
    }

    public void setCostoRegular(BigDecimal costoRegular) {
        this.costoRegular = costoRegular;
    }

    public BigDecimal getCostoAdicional() {
        return costoAdicional;
    }

    public void setCostoAdicional(BigDecimal costoAdicional) {
        this.costoAdicional = costoAdicional;
    }

}

