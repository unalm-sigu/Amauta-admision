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
import pe.edu.lamolina.pivot.model.general.Compania;

@Entity
@Table(name = "fin_factura")
public class Factura implements Serializable {

    @Id
    @GeneratedValue
    @Column(name = "id")
    private Long id;

    @Column(name = "serie")
    private Long serie;

    @Column(name = "numero")
    private Long numero;

    @Column(name = "fecha")
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date fecha;

    @Column(name = "estado")
    private String estado;

    @Column(name = "codigo")
    private String codigo;

    @Column(name = "nombre")
    private String nombre;

    @Column(name = "importe")
    private BigDecimal importe;

    @Column(name = "comentario")
    private String comentario;

    @Column(name = "id_user_impresion")
    private Long idUserImpresion;

    @Column(name = "fecha_impresion")
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date fechaImpresion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_compania")
    private Compania compania;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_item_carga_abono")
    private ItemCargaAbono itemCargaAbono;

    @OneToMany(mappedBy = "factura", fetch = FetchType.LAZY)
    private List<ItemFactura> itemFactura;

    public Factura() {
    }

    public Factura(Object id) {
        this.id = TypesUtil.getLong(id);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Compania getCompania() {
        return compania;
    }

    public void setCompania(Compania compania) {
        this.compania = compania;
    }

    public Long getSerie() {
        return serie;
    }

    public void setSerie(Long serie) {
        this.serie = serie;
    }

    public Long getNumero() {
        return numero;
    }

    public void setNumero(Long numero) {
        this.numero = numero;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public ItemCargaAbono getItemCargaAbono() {
        return itemCargaAbono;
    }

    public void setItemCargaAbono(ItemCargaAbono itemCargaAbono) {
        this.itemCargaAbono = itemCargaAbono;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public BigDecimal getImporte() {
        return importe;
    }

    public void setImporte(BigDecimal importe) {
        this.importe = importe;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }

    public Long getIdUserImpresion() {
        return idUserImpresion;
    }

    public void setIdUserImpresion(Long idUserImpresion) {
        this.idUserImpresion = idUserImpresion;
    }

    public Date getFechaImpresion() {
        return fechaImpresion;
    }

    public void setFechaImpresion(Date fechaImpresion) {
        this.fechaImpresion = fechaImpresion;
    }

    public List<ItemFactura> getItemFactura() {
        return itemFactura;
    }

    public void setItemFactura(List<ItemFactura> itemFactura) {
        this.itemFactura = itemFactura;
    }

}

