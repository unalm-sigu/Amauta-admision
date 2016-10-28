package pe.edu.lamolina.pivot.model.inscripcion;

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
import pe.edu.lamolina.pivot.model.finanzas.AbonoPostulante;
import pe.edu.lamolina.pivot.model.finanzas.ItemCargaAbono;
import pe.edu.lamolina.pivot.model.general.Colegio;
import pe.edu.lamolina.pivot.model.general.Pais;
import pe.edu.lamolina.pivot.model.general.Persona;
import pe.edu.lamolina.pivot.model.general.Universidad;

@Entity
@Table(name = "sip_postulante")
public class Postulante implements Serializable {

    @Id
    @GeneratedValue
    @Column(name = "id")
    private Long id;

    @Column(name = "codigo")
    private String codigo;

    @Column(name = "email")
    private String email;

    @Column(name = "clave")
    private String clave;

    @Column(name = "colegio_extranjero")
    private String colegioExtranjero;

    @Column(name = "year_egreso_colegio")
    private Integer yearEgresoColegio;

    @Column(name = "universidad_extranjera")
    private String universidadExtranjera;

    @Column(name = "estado")
    private String estado;

    @Column(name = "importe_abonado")
    private BigDecimal importeAbonado;

    @Column(name = "importe_descuento")
    private BigDecimal importeDescuento;

    @Column(name = "importe_total")
    private BigDecimal importeTotal;

    @Column(name = "importe_utilizado")
    private BigDecimal importeUtilizado;

    @Column(name = "numero_asiento")
    private Integer numeroAsiento;

    @Column(name = "orden_atencion")
    private String ordenAtencion;

    @Column(name = "fecha_registro")
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date fechaRegistro;

    @Column(name = "id_user_registro")
    private Long idUserRegistro;

    @Column(name = "fecha_modificacion")
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date fechaModificacion;

    @Column(name = "id_user_modificacion")
    private Long idUserModificacion;

    @Column(name = "fecha_descuento")
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date fechaDescuento;

    @Column(name = "id_user_descuento")
    private Long idUserDescuento;

    @Column(name = "fecha_inscripcion")
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date fechaInscripcion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_persona")
    private Persona persona;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_modalidad_ingreso")
    private ModalidadIngreso modalidadIngreso;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_ciclo_postula")
    private CicloPostula cicloPostula;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_colegio_procedencia")
    private Colegio colegioProcedencia;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_pais_colegio")
    private Pais paisColegio;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_universidad_procedencia")
    private Universidad universidadProcedencia;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_pais_universidad")
    private Pais paisUniversidad;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_prospecto")
    private Prospecto prospecto;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_descuento_examen")
    private DescuentoExamen descuentoExamen;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_aula_examen")
    private AulaExamen aulaExamen;

    @OneToMany(mappedBy = "postulante", fetch = FetchType.LAZY)
    private List<AbonoPostulante> abonoPostulante;

    @OneToMany(mappedBy = "postulante", fetch = FetchType.LAZY)
    private List<ItemCargaAbono> itemCargaAbono;

    @OneToMany(mappedBy = "postulante", fetch = FetchType.LAZY)
    private List<Evaluado> evaluado;

    @OneToMany(mappedBy = "postulante", fetch = FetchType.LAZY)
    private List<Ingresante> ingresante;

    @OneToMany(mappedBy = "postulante", fetch = FetchType.LAZY)
    private List<OpcionCarrera> opcionCarrera;

    @OneToMany(mappedBy = "postulante", fetch = FetchType.LAZY)
    private List<PostulanteDocumento> postulanteDocumento;

    @OneToMany(mappedBy = "postulante", fetch = FetchType.LAZY)
    private List<Prelamolina> prelamolina;

    public Postulante() {
    }

    public Postulante(Object id) {
        this.id = TypesUtil.getLong(id);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getClave() {
        return clave;
    }

    public void setClave(String clave) {
        this.clave = clave;
    }

    public Persona getPersona() {
        return persona;
    }

    public void setPersona(Persona persona) {
        this.persona = persona;
    }

    public ModalidadIngreso getModalidadIngreso() {
        return modalidadIngreso;
    }

    public void setModalidadIngreso(ModalidadIngreso modalidadIngreso) {
        this.modalidadIngreso = modalidadIngreso;
    }

    public CicloPostula getCicloPostula() {
        return cicloPostula;
    }

    public void setCicloPostula(CicloPostula cicloPostula) {
        this.cicloPostula = cicloPostula;
    }

    public Colegio getColegioProcedencia() {
        return colegioProcedencia;
    }

    public void setColegioProcedencia(Colegio colegioProcedencia) {
        this.colegioProcedencia = colegioProcedencia;
    }

    public Pais getPaisColegio() {
        return paisColegio;
    }

    public void setPaisColegio(Pais paisColegio) {
        this.paisColegio = paisColegio;
    }

    public String getColegioExtranjero() {
        return colegioExtranjero;
    }

    public void setColegioExtranjero(String colegioExtranjero) {
        this.colegioExtranjero = colegioExtranjero;
    }

    public Integer getYearEgresoColegio() {
        return yearEgresoColegio;
    }

    public void setYearEgresoColegio(Integer yearEgresoColegio) {
        this.yearEgresoColegio = yearEgresoColegio;
    }

    public Universidad getUniversidadProcedencia() {
        return universidadProcedencia;
    }

    public void setUniversidadProcedencia(Universidad universidadProcedencia) {
        this.universidadProcedencia = universidadProcedencia;
    }

    public Pais getPaisUniversidad() {
        return paisUniversidad;
    }

    public void setPaisUniversidad(Pais paisUniversidad) {
        this.paisUniversidad = paisUniversidad;
    }

    public String getUniversidadExtranjera() {
        return universidadExtranjera;
    }

    public void setUniversidadExtranjera(String universidadExtranjera) {
        this.universidadExtranjera = universidadExtranjera;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Prospecto getProspecto() {
        return prospecto;
    }

    public void setProspecto(Prospecto prospecto) {
        this.prospecto = prospecto;
    }

    public DescuentoExamen getDescuentoExamen() {
        return descuentoExamen;
    }

    public void setDescuentoExamen(DescuentoExamen descuentoExamen) {
        this.descuentoExamen = descuentoExamen;
    }

    public BigDecimal getImporteAbonado() {
        return importeAbonado;
    }

    public void setImporteAbonado(BigDecimal importeAbonado) {
        this.importeAbonado = importeAbonado;
    }

    public BigDecimal getImporteDescuento() {
        return importeDescuento;
    }

    public void setImporteDescuento(BigDecimal importeDescuento) {
        this.importeDescuento = importeDescuento;
    }

    public BigDecimal getImporteTotal() {
        return importeTotal;
    }

    public void setImporteTotal(BigDecimal importeTotal) {
        this.importeTotal = importeTotal;
    }

    public BigDecimal getImporteUtilizado() {
        return importeUtilizado;
    }

    public void setImporteUtilizado(BigDecimal importeUtilizado) {
        this.importeUtilizado = importeUtilizado;
    }

    public AulaExamen getAulaExamen() {
        return aulaExamen;
    }

    public void setAulaExamen(AulaExamen aulaExamen) {
        this.aulaExamen = aulaExamen;
    }

    public Integer getNumeroAsiento() {
        return numeroAsiento;
    }

    public void setNumeroAsiento(Integer numeroAsiento) {
        this.numeroAsiento = numeroAsiento;
    }

    public String getOrdenAtencion() {
        return ordenAtencion;
    }

    public void setOrdenAtencion(String ordenAtencion) {
        this.ordenAtencion = ordenAtencion;
    }

    public Date getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(Date fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    public Long getIdUserRegistro() {
        return idUserRegistro;
    }

    public void setIdUserRegistro(Long idUserRegistro) {
        this.idUserRegistro = idUserRegistro;
    }

    public Date getFechaModificacion() {
        return fechaModificacion;
    }

    public void setFechaModificacion(Date fechaModificacion) {
        this.fechaModificacion = fechaModificacion;
    }

    public Long getIdUserModificacion() {
        return idUserModificacion;
    }

    public void setIdUserModificacion(Long idUserModificacion) {
        this.idUserModificacion = idUserModificacion;
    }

    public Date getFechaDescuento() {
        return fechaDescuento;
    }

    public void setFechaDescuento(Date fechaDescuento) {
        this.fechaDescuento = fechaDescuento;
    }

    public Long getIdUserDescuento() {
        return idUserDescuento;
    }

    public void setIdUserDescuento(Long idUserDescuento) {
        this.idUserDescuento = idUserDescuento;
    }

    public Date getFechaInscripcion() {
        return fechaInscripcion;
    }

    public void setFechaInscripcion(Date fechaInscripcion) {
        this.fechaInscripcion = fechaInscripcion;
    }

    public List<AbonoPostulante> getAbonoPostulante() {
        return abonoPostulante;
    }

    public void setAbonoPostulante(List<AbonoPostulante> abonoPostulante) {
        this.abonoPostulante = abonoPostulante;
    }

    public List<ItemCargaAbono> getItemCargaAbono() {
        return itemCargaAbono;
    }

    public void setItemCargaAbono(List<ItemCargaAbono> itemCargaAbono) {
        this.itemCargaAbono = itemCargaAbono;
    }

    public List<Evaluado> getEvaluado() {
        return evaluado;
    }

    public void setEvaluado(List<Evaluado> evaluado) {
        this.evaluado = evaluado;
    }

    public List<Ingresante> getIngresante() {
        return ingresante;
    }

    public void setIngresante(List<Ingresante> ingresante) {
        this.ingresante = ingresante;
    }

    public List<OpcionCarrera> getOpcionCarrera() {
        return opcionCarrera;
    }

    public void setOpcionCarrera(List<OpcionCarrera> opcionCarrera) {
        this.opcionCarrera = opcionCarrera;
    }

    public List<PostulanteDocumento> getPostulanteDocumento() {
        return postulanteDocumento;
    }

    public void setPostulanteDocumento(List<PostulanteDocumento> postulanteDocumento) {
        this.postulanteDocumento = postulanteDocumento;
    }

    public List<Prelamolina> getPrelamolina() {
        return prelamolina;
    }

    public void setPrelamolina(List<Prelamolina> prelamolina) {
        this.prelamolina = prelamolina;
    }

}

