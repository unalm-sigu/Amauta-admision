package pe.edu.lamolina.pivot.model.inscripcion;

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
@Table(name = "sip_modalidad_ingreso_ciclo")
public class ModalidadIngresoCiclo implements Serializable {

    @Id
    @GeneratedValue
    @Column(name = "id")
    private Long id;

    @Column(name = "estado")
    private String estado;

    @Column(name = "presentar_documentos")
    private Integer presentarDocumentos;

    @Column(name = "requiere_colegio")
    private Integer requiereColegio;

    @Column(name = "requiere_universidad")
    private Integer requiereUniversidad;

    @Column(name = "solo_colegio_peruano")
    private Integer soloColegioPeruano;

    @Column(name = "solo_colegio_extranjero")
    private Integer soloColegioExtranjero;

    @Column(name = "exonerado_pago")
    private Integer exoneradoPago;

    @Column(name = "rinde_examen_admision")
    private Integer rindeExamenAdmision;

    @Column(name = "opciones")
    private Integer opciones;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_ciclo_postula")
    private CicloPostula cicloPostula;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_modalidad_ingreso")
    private ModalidadIngreso modalidadIngreso;

    public ModalidadIngresoCiclo() {
    }

    public ModalidadIngresoCiclo(Object id) {
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

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Integer getPresentarDocumentos() {
        return presentarDocumentos;
    }

    public void setPresentarDocumentos(Integer presentarDocumentos) {
        this.presentarDocumentos = presentarDocumentos;
    }

    public Integer getRequiereColegio() {
        return requiereColegio;
    }

    public void setRequiereColegio(Integer requiereColegio) {
        this.requiereColegio = requiereColegio;
    }

    public Integer getRequiereUniversidad() {
        return requiereUniversidad;
    }

    public void setRequiereUniversidad(Integer requiereUniversidad) {
        this.requiereUniversidad = requiereUniversidad;
    }

    public Integer getSoloColegioPeruano() {
        return soloColegioPeruano;
    }

    public void setSoloColegioPeruano(Integer soloColegioPeruano) {
        this.soloColegioPeruano = soloColegioPeruano;
    }

    public Integer getSoloColegioExtranjero() {
        return soloColegioExtranjero;
    }

    public void setSoloColegioExtranjero(Integer soloColegioExtranjero) {
        this.soloColegioExtranjero = soloColegioExtranjero;
    }

    public Integer getExoneradoPago() {
        return exoneradoPago;
    }

    public void setExoneradoPago(Integer exoneradoPago) {
        this.exoneradoPago = exoneradoPago;
    }

    public Integer getRindeExamenAdmision() {
        return rindeExamenAdmision;
    }

    public void setRindeExamenAdmision(Integer rindeExamenAdmision) {
        this.rindeExamenAdmision = rindeExamenAdmision;
    }

    public Integer getOpciones() {
        return opciones;
    }

    public void setOpciones(Integer opciones) {
        this.opciones = opciones;
    }

}

