package pe.edu.lamolina.pivot.model.finanzas;

import java.io.Serializable;
import java.math.BigDecimal;
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
import pe.edu.lamolina.pivot.model.inscripcion.CicloPostula;

@Entity
@Table(name = "fin_concepto_precio")
public class ConceptoPrecio implements Serializable {

    @Id
    @GeneratedValue
    @Column(name = "id")
    private Long id;

    @Column(name = "monto")
    private BigDecimal monto;

    @Column(name = "id_user_registra")
    private Long idUserRegistra;

    @Column(name = "fecha_registra")
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date fechaRegistra;

    @Column(name = "id_user_modifica")
    private Long idUserModifica;

    @Column(name = "fecha_modifica")
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date fechaModifica;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_ciclo_postula")
    private CicloPostula cicloPostula;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_concepto_pago")
    private ConceptoPago conceptoPago;

    public ConceptoPrecio() {
    }

    public ConceptoPrecio(Object id) {
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

    public ConceptoPago getConceptoPago() {
        return conceptoPago;
    }

    public void setConceptoPago(ConceptoPago conceptoPago) {
        this.conceptoPago = conceptoPago;
    }

    public BigDecimal getMonto() {
        return monto;
    }

    public void setMonto(BigDecimal monto) {
        this.monto = monto;
    }

    public Long getIdUserRegistra() {
        return idUserRegistra;
    }

    public void setIdUserRegistra(Long idUserRegistra) {
        this.idUserRegistra = idUserRegistra;
    }

    public Date getFechaRegistra() {
        return fechaRegistra;
    }

    public void setFechaRegistra(Date fechaRegistra) {
        this.fechaRegistra = fechaRegistra;
    }

    public Long getIdUserModifica() {
        return idUserModifica;
    }

    public void setIdUserModifica(Long idUserModifica) {
        this.idUserModifica = idUserModifica;
    }

    public Date getFechaModifica() {
        return fechaModifica;
    }

    public void setFechaModifica(Date fechaModifica) {
        this.fechaModifica = fechaModifica;
    }

}

