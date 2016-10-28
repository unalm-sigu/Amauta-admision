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
import pe.edu.lamolina.pivot.model.general.Compania;

@Entity
@Table(name = "fin_cuenta_bancaria")
public class CuentaBancaria implements Serializable {

    @Id
    @GeneratedValue
    @Column(name = "id")
    private Long id;

    @Column(name = "numero")
    private String numero;

    @Column(name = "nombre")
    private String nombre;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_compania")
    private Compania compania;

    @OneToMany(mappedBy = "cuentaBancaria", fetch = FetchType.LAZY)
    private List<CargaAbonos> cargaAbonos;

    @OneToMany(mappedBy = "cuentaBancaria", fetch = FetchType.LAZY)
    private List<ConceptoPago> conceptoPago;

    public CuentaBancaria() {
    }

    public CuentaBancaria(Object id) {
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

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public List<CargaAbonos> getCargaAbonos() {
        return cargaAbonos;
    }

    public void setCargaAbonos(List<CargaAbonos> cargaAbonos) {
        this.cargaAbonos = cargaAbonos;
    }

    public List<ConceptoPago> getConceptoPago() {
        return conceptoPago;
    }

    public void setConceptoPago(List<ConceptoPago> conceptoPago) {
        this.conceptoPago = conceptoPago;
    }

}

