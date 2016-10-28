package pe.edu.lamolina.pivot.model.inscripcion;

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
import pe.edu.lamolina.pivot.model.academico.ModalidadEstudio;
import pe.edu.lamolina.pivot.model.calificacion.InfoVacanteModalidad;
import pe.edu.lamolina.pivot.model.calificacion.TemaExamenModalidad;
import pe.edu.lamolina.pivot.model.finanzas.ConceptoPago;
import pe.edu.lamolina.pivot.model.finanzas.CostoModalidad;
import pe.edu.lamolina.pivot.model.vacantes.CarreraModalidadIngreso;
import pe.edu.lamolina.pivot.model.vacantes.ConfiguraVacanteModalidad;
import pe.edu.lamolina.pivot.model.vacantes.VacanteCarrera;

@Entity
@Table(name = "sip_modalidad_ingreso")
public class ModalidadIngreso implements Serializable {

    @Id
    @GeneratedValue
    @Column(name = "id")
    private Long id;

    @Column(name = "nombre")
    private String nombre;

    @Column(name = "nombre_corto")
    private String nombreCorto;

    @Column(name = "codigo")
    private String codigo;

    @Column(name = "estado")
    private String estado;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_modalidad_estudio")
    private ModalidadEstudio modalidadEstudio;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_modalidad_superior")
    private ModalidadIngreso modalidadSuperior;

    @OneToMany(mappedBy = "modalidadIngreso", fetch = FetchType.LAZY)
    private List<ConceptoPago> conceptoPago;

    @OneToMany(mappedBy = "modalidadIngreso", fetch = FetchType.LAZY)
    private List<CostoModalidad> costoModalidad;

    @OneToMany(mappedBy = "modalidadIngreso", fetch = FetchType.LAZY)
    private List<InfoVacanteModalidad> infoVacanteModalidad;

    @OneToMany(mappedBy = "modalidadIngreso", fetch = FetchType.LAZY)
    private List<TemaExamenModalidad> temaExamenModalidad;

    @OneToMany(mappedBy = "modalidadIngreso", fetch = FetchType.LAZY)
    private List<DescuentoExamen> descuentoExamen;

    @OneToMany(mappedBy = "modalidad", fetch = FetchType.LAZY)
    private List<DocumentoModalidad> documentoModalidad;

    @OneToMany(mappedBy = "modalidadIngreso", fetch = FetchType.LAZY)
    private List<ModalidadGrupo> modalidadGrupo;

    @OneToMany(mappedBy = "modalidadSuperior", fetch = FetchType.LAZY)
    private List<ModalidadIngreso> modalidadIngreso;

    @OneToMany(mappedBy = "modalidadIngreso", fetch = FetchType.LAZY)
    private List<ModalidadIngresoCiclo> modalidadIngresoCiclo;

    @OneToMany(mappedBy = "modalidadIngreso", fetch = FetchType.LAZY)
    private List<Postulante> postulante;

    @OneToMany(mappedBy = "modalidadIngreso", fetch = FetchType.LAZY)
    private List<CarreraModalidadIngreso> carreraModalidadIngreso;

    @OneToMany(mappedBy = "modalidadIngreso", fetch = FetchType.LAZY)
    private List<ConfiguraVacanteModalidad> configuraVacanteModalidad;

    @OneToMany(mappedBy = "modalidadIngreso", fetch = FetchType.LAZY)
    private List<VacanteCarrera> vacanteCarrera;

    public ModalidadIngreso() {
    }

    public ModalidadIngreso(Object id) {
        this.id = TypesUtil.getLong(id);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ModalidadEstudio getModalidadEstudio() {
        return modalidadEstudio;
    }

    public void setModalidadEstudio(ModalidadEstudio modalidadEstudio) {
        this.modalidadEstudio = modalidadEstudio;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getNombreCorto() {
        return nombreCorto;
    }

    public void setNombreCorto(String nombreCorto) {
        this.nombreCorto = nombreCorto;
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

    public ModalidadIngreso getModalidadSuperior() {
        return modalidadSuperior;
    }

    public void setModalidadSuperior(ModalidadIngreso modalidadSuperior) {
        this.modalidadSuperior = modalidadSuperior;
    }

    public List<ConceptoPago> getConceptoPago() {
        return conceptoPago;
    }

    public void setConceptoPago(List<ConceptoPago> conceptoPago) {
        this.conceptoPago = conceptoPago;
    }

    public List<CostoModalidad> getCostoModalidad() {
        return costoModalidad;
    }

    public void setCostoModalidad(List<CostoModalidad> costoModalidad) {
        this.costoModalidad = costoModalidad;
    }

    public List<InfoVacanteModalidad> getInfoVacanteModalidad() {
        return infoVacanteModalidad;
    }

    public void setInfoVacanteModalidad(List<InfoVacanteModalidad> infoVacanteModalidad) {
        this.infoVacanteModalidad = infoVacanteModalidad;
    }

    public List<TemaExamenModalidad> getTemaExamenModalidad() {
        return temaExamenModalidad;
    }

    public void setTemaExamenModalidad(List<TemaExamenModalidad> temaExamenModalidad) {
        this.temaExamenModalidad = temaExamenModalidad;
    }

    public List<DescuentoExamen> getDescuentoExamen() {
        return descuentoExamen;
    }

    public void setDescuentoExamen(List<DescuentoExamen> descuentoExamen) {
        this.descuentoExamen = descuentoExamen;
    }

    public List<DocumentoModalidad> getDocumentoModalidad() {
        return documentoModalidad;
    }

    public void setDocumentoModalidad(List<DocumentoModalidad> documentoModalidad) {
        this.documentoModalidad = documentoModalidad;
    }

    public List<ModalidadGrupo> getModalidadGrupo() {
        return modalidadGrupo;
    }

    public void setModalidadGrupo(List<ModalidadGrupo> modalidadGrupo) {
        this.modalidadGrupo = modalidadGrupo;
    }

    public List<ModalidadIngreso> getModalidadIngreso() {
        return modalidadIngreso;
    }

    public void setModalidadIngreso(List<ModalidadIngreso> modalidadIngreso) {
        this.modalidadIngreso = modalidadIngreso;
    }

    public List<ModalidadIngresoCiclo> getModalidadIngresoCiclo() {
        return modalidadIngresoCiclo;
    }

    public void setModalidadIngresoCiclo(List<ModalidadIngresoCiclo> modalidadIngresoCiclo) {
        this.modalidadIngresoCiclo = modalidadIngresoCiclo;
    }

    public List<Postulante> getPostulante() {
        return postulante;
    }

    public void setPostulante(List<Postulante> postulante) {
        this.postulante = postulante;
    }

    public List<CarreraModalidadIngreso> getCarreraModalidadIngreso() {
        return carreraModalidadIngreso;
    }

    public void setCarreraModalidadIngreso(List<CarreraModalidadIngreso> carreraModalidadIngreso) {
        this.carreraModalidadIngreso = carreraModalidadIngreso;
    }

    public List<ConfiguraVacanteModalidad> getConfiguraVacanteModalidad() {
        return configuraVacanteModalidad;
    }

    public void setConfiguraVacanteModalidad(List<ConfiguraVacanteModalidad> configuraVacanteModalidad) {
        this.configuraVacanteModalidad = configuraVacanteModalidad;
    }

    public List<VacanteCarrera> getVacanteCarrera() {
        return vacanteCarrera;
    }

    public void setVacanteCarrera(List<VacanteCarrera> vacanteCarrera) {
        this.vacanteCarrera = vacanteCarrera;
    }

}

