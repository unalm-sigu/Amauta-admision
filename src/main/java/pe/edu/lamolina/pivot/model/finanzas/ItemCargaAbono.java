package pe.edu.lamolina.pivot.model.finanzas;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
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
import javax.persistence.Temporal;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.pivot.model.inscripcion.Postulante;

@Entity
@Table(name = "fin_item_carga_abono")
public class ItemCargaAbono implements Serializable {

    @Id
    @GeneratedValue
    @Column(name = "id")
    private Long id;

    @Column(name = "fecha_abono")
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date fechaAbono;

    @Column(name = "importe")
    private BigDecimal importe;

    @Column(name = "descripcion")
    private String descripcion;

    @Column(name = "numero_operacion")
    private String numeroOperacion;

    @Column(name = "sucursal")
    private String sucursal;

    @Column(name = "usuario_banco")
    private String usuarioBanco;

    @Column(name = "redundante")
    private Integer redundante;

    @Column(name = "extornado")
    private Integer extornado;

    @Column(name = "fecha_extornado")
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date fechaExtornado;

    @Column(name = "id_user_extornado")
    private Long idUserExtornado;

    @Column(name = "utilizado")
    private Integer utilizado;

    @Column(name = "id_user_utilizado")
    private Long idUserUtilizado;

    @Column(name = "fecha_utilizado")
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date fechaUtilizado;

    @Column(name = "fecha_impresion")
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date fechaImpresion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_carga_abonos")
    private CargaAbonos cargaAbonos;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_postulante")
    private Postulante postulante;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_concepto_pago")
    private ConceptoPago conceptoPago;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_no_redundante")
    private ItemCargaAbono noRedundante;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_extornador")
    private ItemCargaAbono extornador;

    @OneToMany(mappedBy = "abono", fetch = FetchType.LAZY)
    private List<AbonoPostulante> abonoPostulante;

    @OneToMany(mappedBy = "itemCargaAbono", fetch = FetchType.LAZY)
    private List<Factura> factura;

    @OneToMany(mappedBy = "noRedundante", fetch = FetchType.LAZY)
    private List<ItemCargaAbono> itemCargaAbono;

    @OneToMany(mappedBy = "extornador", fetch = FetchType.LAZY)
    private List<ItemCargaAbono> itemCargaAbono1;

    public ItemCargaAbono() {
    }

    public ItemCargaAbono(Object id) {
        this.id = TypesUtil.getLong(id);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public CargaAbonos getCargaAbonos() {
        return cargaAbonos;
    }

    public void setCargaAbonos(CargaAbonos cargaAbonos) {
        this.cargaAbonos = cargaAbonos;
    }

    public Date getFechaAbono() {
        return fechaAbono;
    }

    public void setFechaAbono(Date fechaAbono) {
        this.fechaAbono = fechaAbono;
    }

    public BigDecimal getImporte() {
        return importe;
    }

    public void setImporte(BigDecimal importe) {
        this.importe = importe;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getNumeroOperacion() {
        return numeroOperacion;
    }

    public void setNumeroOperacion(String numeroOperacion) {
        this.numeroOperacion = numeroOperacion;
    }

    public String getSucursal() {
        return sucursal;
    }

    public void setSucursal(String sucursal) {
        this.sucursal = sucursal;
    }

    public String getUsuarioBanco() {
        return usuarioBanco;
    }

    public void setUsuarioBanco(String usuarioBanco) {
        this.usuarioBanco = usuarioBanco;
    }

    public Postulante getPostulante() {
        return postulante;
    }

    public void setPostulante(Postulante postulante) {
        this.postulante = postulante;
    }

    public ConceptoPago getConceptoPago() {
        return conceptoPago;
    }

    public void setConceptoPago(ConceptoPago conceptoPago) {
        this.conceptoPago = conceptoPago;
    }

    public Integer getRedundante() {
        return redundante;
    }

    public void setRedundante(Integer redundante) {
        this.redundante = redundante;
    }

    public ItemCargaAbono getNoRedundante() {
        return noRedundante;
    }

    public void setNoRedundante(ItemCargaAbono noRedundante) {
        this.noRedundante = noRedundante;
    }

    public Integer getExtornado() {
        return extornado;
    }

    public void setExtornado(Integer extornado) {
        this.extornado = extornado;
    }

    public ItemCargaAbono getExtornador() {
        return extornador;
    }

    public void setExtornador(ItemCargaAbono extornador) {
        this.extornador = extornador;
    }

    public Date getFechaExtornado() {
        return fechaExtornado;
    }

    public void setFechaExtornado(Date fechaExtornado) {
        this.fechaExtornado = fechaExtornado;
    }

    public Long getIdUserExtornado() {
        return idUserExtornado;
    }

    public void setIdUserExtornado(Long idUserExtornado) {
        this.idUserExtornado = idUserExtornado;
    }

    public Integer getUtilizado() {
        return utilizado;
    }

    public void setUtilizado(Integer utilizado) {
        this.utilizado = utilizado;
    }

    public Long getIdUserUtilizado() {
        return idUserUtilizado;
    }

    public void setIdUserUtilizado(Long idUserUtilizado) {
        this.idUserUtilizado = idUserUtilizado;
    }

    public Date getFechaUtilizado() {
        return fechaUtilizado;
    }

    public void setFechaUtilizado(Date fechaUtilizado) {
        this.fechaUtilizado = fechaUtilizado;
    }

    public Date getFechaImpresion() {
        return fechaImpresion;
    }

    public void setFechaImpresion(Date fechaImpresion) {
        this.fechaImpresion = fechaImpresion;
    }

    public List<AbonoPostulante> getAbonoPostulante() {
        return abonoPostulante;
    }

    public void setAbonoPostulante(List<AbonoPostulante> abonoPostulante) {
        this.abonoPostulante = abonoPostulante;
    }

    public List<Factura> getFactura() {
        return factura;
    }

    public void setFactura(List<Factura> factura) {
        this.factura = factura;
    }

    public List<ItemCargaAbono> getItemCargaAbono() {
        return itemCargaAbono;
    }

    public void setItemCargaAbono(List<ItemCargaAbono> itemCargaAbono) {
        this.itemCargaAbono = itemCargaAbono;
    }

    public List<ItemCargaAbono> getItemCargaAbono1() {
        return itemCargaAbono1;
    }

    public void setItemCargaAbono1(List<ItemCargaAbono> itemCargaAbono1) {
        this.itemCargaAbono1 = itemCargaAbono1;
    }

}

