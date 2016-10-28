package pe.edu.lamolina.pivot.model.general;

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
import pe.edu.lamolina.pivot.model.academico.Facultad;
import pe.edu.lamolina.pivot.model.academico.ModalidadEstudio;
import pe.edu.lamolina.pivot.model.finanzas.CuentaBancaria;
import pe.edu.lamolina.pivot.model.finanzas.Factura;

@Entity
@Table(name = "gen_compania")
public class Compania implements Serializable {

    @Id
    @GeneratedValue
    @Column(name = "id")
    private Long id;

    @Column(name = "nombre")
    private String nombre;

    @Column(name = "codigo")
    private String codigo;

    @OneToMany(mappedBy = "compania", fetch = FetchType.LAZY)
    private List<Facultad> facultad;

    @OneToMany(mappedBy = "compania", fetch = FetchType.LAZY)
    private List<ModalidadEstudio> modalidadEstudio;

    @OneToMany(mappedBy = "compania", fetch = FetchType.LAZY)
    private List<CuentaBancaria> cuentaBancaria;

    @OneToMany(mappedBy = "compania", fetch = FetchType.LAZY)
    private List<Factura> factura;

    @OneToMany(mappedBy = "compania", fetch = FetchType.LAZY)
    private List<Oficina> oficina;

    @OneToMany(mappedBy = "compania", fetch = FetchType.LAZY)
    private List<PersonaPerfil> personaPerfil;

    @OneToMany(mappedBy = "compania", fetch = FetchType.LAZY)
    private List<Sede> sede;

    public Compania() {
    }

    public Compania(Object id) {
        this.id = TypesUtil.getLong(id);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public List<Facultad> getFacultad() {
        return facultad;
    }

    public void setFacultad(List<Facultad> facultad) {
        this.facultad = facultad;
    }

    public List<ModalidadEstudio> getModalidadEstudio() {
        return modalidadEstudio;
    }

    public void setModalidadEstudio(List<ModalidadEstudio> modalidadEstudio) {
        this.modalidadEstudio = modalidadEstudio;
    }

    public List<CuentaBancaria> getCuentaBancaria() {
        return cuentaBancaria;
    }

    public void setCuentaBancaria(List<CuentaBancaria> cuentaBancaria) {
        this.cuentaBancaria = cuentaBancaria;
    }

    public List<Factura> getFactura() {
        return factura;
    }

    public void setFactura(List<Factura> factura) {
        this.factura = factura;
    }

    public List<Oficina> getOficina() {
        return oficina;
    }

    public void setOficina(List<Oficina> oficina) {
        this.oficina = oficina;
    }

    public List<PersonaPerfil> getPersonaPerfil() {
        return personaPerfil;
    }

    public void setPersonaPerfil(List<PersonaPerfil> personaPerfil) {
        this.personaPerfil = personaPerfil;
    }

    public List<Sede> getSede() {
        return sede;
    }

    public void setSede(List<Sede> sede) {
        this.sede = sede;
    }

}

