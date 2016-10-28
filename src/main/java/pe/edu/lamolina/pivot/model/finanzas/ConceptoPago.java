package pe.edu.lamolina.pivot.model.finanzas;

import java.io.Serializable;
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
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.pivot.model.inscripcion.ModalidadIngreso;

@Entity
@Table(name = "fin_concepto_pago")
public class ConceptoPago implements Serializable {

    @Id
    @GeneratedValue
    @Column(name = "id")
    private Long id;

    @Column(name = "codigo")
    private String codigo;

    @Column(name = "estado")
    private String estado;

    @Column(name = "descripcion")
    private String descripcion;

    @Column(name = "tipo")
    private String tipo;

    @Column(name = "descuento")
    private Integer descuento;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_modalidad_ingreso")
    private ModalidadIngreso modalidadIngreso;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_concepto_origen")
    private ConceptoPago conceptoOrigen;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_cuenta_bancaria")
    private CuentaBancaria cuentaBancaria;

    @OneToMany(mappedBy = "concepto", fetch = FetchType.LAZY)
    private List<AbonoPostulante> abonoPostulante;

    @OneToMany(mappedBy = "conceptoOrigen", fetch = FetchType.LAZY)
    private List<ConceptoPago> conceptoPago;

    @OneToMany(mappedBy = "conceptoPago", fetch = FetchType.LAZY)
    private List<ConceptoPrecio> conceptoPrecio;

    @OneToMany(mappedBy = "conceptoPago", fetch = FetchType.LAZY)
    private List<ItemCargaAbono> itemCargaAbono;

    public ConceptoPago() {
    }

    public ConceptoPago(Object id) {
        this.id = TypesUtil.getLong(id);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ModalidadIngreso getModalidadIngreso() {
        return modalidadIngreso;
    }

    public void setModalidadIngreso(ModalidadIngreso modalidadIngreso) {
        this.modalidadIngreso = modalidadIngreso;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public Integer getDescuento() {
        return descuento;
    }

    public void setDescuento(Integer descuento) {
        this.descuento = descuento;
    }

    public ConceptoPago getConceptoOrigen() {
        return conceptoOrigen;
    }

    public void setConceptoOrigen(ConceptoPago conceptoOrigen) {
        this.conceptoOrigen = conceptoOrigen;
    }

    public CuentaBancaria getCuentaBancaria() {
        return cuentaBancaria;
    }

    public void setCuentaBancaria(CuentaBancaria cuentaBancaria) {
        this.cuentaBancaria = cuentaBancaria;
    }

    public List<AbonoPostulante> getAbonoPostulante() {
        return abonoPostulante;
    }

    public void setAbonoPostulante(List<AbonoPostulante> abonoPostulante) {
        this.abonoPostulante = abonoPostulante;
    }

    public List<ConceptoPago> getConceptoPago() {
        return conceptoPago;
    }

    public void setConceptoPago(List<ConceptoPago> conceptoPago) {
        this.conceptoPago = conceptoPago;
    }

    public List<ConceptoPrecio> getConceptoPrecio() {
        return conceptoPrecio;
    }

    public void setConceptoPrecio(List<ConceptoPrecio> conceptoPrecio) {
        this.conceptoPrecio = conceptoPrecio;
    }

    public List<ItemCargaAbono> getItemCargaAbono() {
        return itemCargaAbono;
    }

    public void setItemCargaAbono(List<ItemCargaAbono> itemCargaAbono) {
        this.itemCargaAbono = itemCargaAbono;
    }

}

