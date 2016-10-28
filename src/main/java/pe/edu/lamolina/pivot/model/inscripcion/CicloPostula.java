package pe.edu.lamolina.pivot.model.inscripcion;

import java.io.Serializable;
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
import pe.edu.lamolina.pivot.model.academico.CicloAcademico;
import pe.edu.lamolina.pivot.model.calificacion.InfoExamen;
import pe.edu.lamolina.pivot.model.calificacion.InfoVacanteModalidad;
import pe.edu.lamolina.pivot.model.calificacion.TemaCiclo;
import pe.edu.lamolina.pivot.model.calificacion.TemaExamenModalidad;
import pe.edu.lamolina.pivot.model.finanzas.CargaAbonos;
import pe.edu.lamolina.pivot.model.finanzas.ConceptoPrecio;
import pe.edu.lamolina.pivot.model.finanzas.CostoModalidad;
import pe.edu.lamolina.pivot.model.vacantes.CarreraModalidadIngreso;
import pe.edu.lamolina.pivot.model.vacantes.ConfiguraVacanteModalidad;
import pe.edu.lamolina.pivot.model.vacantes.VacanteCarrera;

@Entity
@Table(name = "sip_ciclo_postula")
public class CicloPostula implements Serializable {

    @Id
    @GeneratedValue
    @Column(name = "id")
    private Long id;

    @Column(name = "estado")
    private String estado;

    @Column(name = "fecha_inicio")
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date fechaInicio;

    @Column(name = "fecha_fin")
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date fechaFin;

    @Column(name = "vacantes_base")
    private Integer vacantesBase;

    @Column(name = "vacantes_total")
    private Integer vacantesTotal;

    @Column(name = "vacantes_supernumerario")
    private Integer vacantesSupernumerario;

    @Column(name = "estado_vacantes")
    private String estadoVacantes;

    @Column(name = "id_user_vacantes")
    private Long idUserVacantes;

    @Column(name = "fecha_asigna_vacantes")
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date fechaAsignaVacantes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_ciclo_academico")
    private CicloAcademico cicloAcademico;

    @OneToMany(mappedBy = "cicloPostula", fetch = FetchType.LAZY)
    private List<CargaAbonos> cargaAbonos;

    @OneToMany(mappedBy = "cicloPostula", fetch = FetchType.LAZY)
    private List<ConceptoPrecio> conceptoPrecio;

    @OneToMany(mappedBy = "cicloPostula", fetch = FetchType.LAZY)
    private List<CostoModalidad> costoModalidad;

    @OneToMany(mappedBy = "cicloPostula", fetch = FetchType.LAZY)
    private List<InfoExamen> infoExamen;

    @OneToMany(mappedBy = "cicloPostula", fetch = FetchType.LAZY)
    private List<InfoVacanteModalidad> infoVacanteModalidad;

    @OneToMany(mappedBy = "cicloPostula", fetch = FetchType.LAZY)
    private List<TemaCiclo> temaCiclo;

    @OneToMany(mappedBy = "cicloPostula", fetch = FetchType.LAZY)
    private List<TemaExamenModalidad> temaExamenModalidad;

    @OneToMany(mappedBy = "cicloPostula", fetch = FetchType.LAZY)
    private List<AgrupacionModalidades> agrupacionModalidades;

    @OneToMany(mappedBy = "cicloPostula", fetch = FetchType.LAZY)
    private List<CarreraPostula> carreraPostula;

    @OneToMany(mappedBy = "cicloPostula", fetch = FetchType.LAZY)
    private List<DocumentoModalidad> documentoModalidad;

    @OneToMany(mappedBy = "cicloPostula", fetch = FetchType.LAZY)
    private List<EventoCiclo> eventoCiclo;

    @OneToMany(mappedBy = "cicloPostula", fetch = FetchType.LAZY)
    private List<ModalidadIngresoCiclo> modalidadIngresoCiclo;

    @OneToMany(mappedBy = "cicloPostula", fetch = FetchType.LAZY)
    private List<PabellonExamen> pabellonExamen;

    @OneToMany(mappedBy = "cicloPostula", fetch = FetchType.LAZY)
    private List<Postulante> postulante;

    @OneToMany(mappedBy = "cicloPostula", fetch = FetchType.LAZY)
    private List<Prospecto> prospecto;

    @OneToMany(mappedBy = "cicloPostula", fetch = FetchType.LAZY)
    private List<CarreraModalidadIngreso> carreraModalidadIngreso;

    @OneToMany(mappedBy = "cicloPostula", fetch = FetchType.LAZY)
    private List<ConfiguraVacanteModalidad> configuraVacanteModalidad;

    @OneToMany(mappedBy = "cicloPostula", fetch = FetchType.LAZY)
    private List<VacanteCarrera> vacanteCarrera;

    public CicloPostula() {
    }

    public CicloPostula(Object id) {
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

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Date getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(Date fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public Date getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(Date fechaFin) {
        this.fechaFin = fechaFin;
    }

    public Integer getVacantesBase() {
        return vacantesBase;
    }

    public void setVacantesBase(Integer vacantesBase) {
        this.vacantesBase = vacantesBase;
    }

    public Integer getVacantesTotal() {
        return vacantesTotal;
    }

    public void setVacantesTotal(Integer vacantesTotal) {
        this.vacantesTotal = vacantesTotal;
    }

    public Integer getVacantesSupernumerario() {
        return vacantesSupernumerario;
    }

    public void setVacantesSupernumerario(Integer vacantesSupernumerario) {
        this.vacantesSupernumerario = vacantesSupernumerario;
    }

    public String getEstadoVacantes() {
        return estadoVacantes;
    }

    public void setEstadoVacantes(String estadoVacantes) {
        this.estadoVacantes = estadoVacantes;
    }

    public Long getIdUserVacantes() {
        return idUserVacantes;
    }

    public void setIdUserVacantes(Long idUserVacantes) {
        this.idUserVacantes = idUserVacantes;
    }

    public Date getFechaAsignaVacantes() {
        return fechaAsignaVacantes;
    }

    public void setFechaAsignaVacantes(Date fechaAsignaVacantes) {
        this.fechaAsignaVacantes = fechaAsignaVacantes;
    }

    public List<CargaAbonos> getCargaAbonos() {
        return cargaAbonos;
    }

    public void setCargaAbonos(List<CargaAbonos> cargaAbonos) {
        this.cargaAbonos = cargaAbonos;
    }

    public List<ConceptoPrecio> getConceptoPrecio() {
        return conceptoPrecio;
    }

    public void setConceptoPrecio(List<ConceptoPrecio> conceptoPrecio) {
        this.conceptoPrecio = conceptoPrecio;
    }

    public List<CostoModalidad> getCostoModalidad() {
        return costoModalidad;
    }

    public void setCostoModalidad(List<CostoModalidad> costoModalidad) {
        this.costoModalidad = costoModalidad;
    }

    public List<InfoExamen> getInfoExamen() {
        return infoExamen;
    }

    public void setInfoExamen(List<InfoExamen> infoExamen) {
        this.infoExamen = infoExamen;
    }

    public List<InfoVacanteModalidad> getInfoVacanteModalidad() {
        return infoVacanteModalidad;
    }

    public void setInfoVacanteModalidad(List<InfoVacanteModalidad> infoVacanteModalidad) {
        this.infoVacanteModalidad = infoVacanteModalidad;
    }

    public List<TemaCiclo> getTemaCiclo() {
        return temaCiclo;
    }

    public void setTemaCiclo(List<TemaCiclo> temaCiclo) {
        this.temaCiclo = temaCiclo;
    }

    public List<TemaExamenModalidad> getTemaExamenModalidad() {
        return temaExamenModalidad;
    }

    public void setTemaExamenModalidad(List<TemaExamenModalidad> temaExamenModalidad) {
        this.temaExamenModalidad = temaExamenModalidad;
    }

    public List<AgrupacionModalidades> getAgrupacionModalidades() {
        return agrupacionModalidades;
    }

    public void setAgrupacionModalidades(List<AgrupacionModalidades> agrupacionModalidades) {
        this.agrupacionModalidades = agrupacionModalidades;
    }

    public List<CarreraPostula> getCarreraPostula() {
        return carreraPostula;
    }

    public void setCarreraPostula(List<CarreraPostula> carreraPostula) {
        this.carreraPostula = carreraPostula;
    }

    public List<DocumentoModalidad> getDocumentoModalidad() {
        return documentoModalidad;
    }

    public void setDocumentoModalidad(List<DocumentoModalidad> documentoModalidad) {
        this.documentoModalidad = documentoModalidad;
    }

    public List<EventoCiclo> getEventoCiclo() {
        return eventoCiclo;
    }

    public void setEventoCiclo(List<EventoCiclo> eventoCiclo) {
        this.eventoCiclo = eventoCiclo;
    }

    public List<ModalidadIngresoCiclo> getModalidadIngresoCiclo() {
        return modalidadIngresoCiclo;
    }

    public void setModalidadIngresoCiclo(List<ModalidadIngresoCiclo> modalidadIngresoCiclo) {
        this.modalidadIngresoCiclo = modalidadIngresoCiclo;
    }

    public List<PabellonExamen> getPabellonExamen() {
        return pabellonExamen;
    }

    public void setPabellonExamen(List<PabellonExamen> pabellonExamen) {
        this.pabellonExamen = pabellonExamen;
    }

    public List<Postulante> getPostulante() {
        return postulante;
    }

    public void setPostulante(List<Postulante> postulante) {
        this.postulante = postulante;
    }

    public List<Prospecto> getProspecto() {
        return prospecto;
    }

    public void setProspecto(List<Prospecto> prospecto) {
        this.prospecto = prospecto;
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

