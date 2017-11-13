package pe.edu.lamolina.pivot.model.general;

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
import javax.persistence.Transient;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.pivot.model.seguridad.Usuario;
import pe.edu.lamolina.pivot.zelper.enums.OficinaEstadoEnum;
import pe.edu.lamolina.pivot.zelper.enums.TipoOficinaEnum;

@Entity
@Table(name = "gen_oficina")
public class Oficina implements Serializable {

    @Id
    @GeneratedValue
    @Column(name = "id")
    private Long id;

    @Column(name = "codigo")
    private String codigo;

    @Column(name = "nombre")
    private String nombre;

    @Column(name = "tipo_oficina")
    private String tipoOficina;

    @Column(name = "instancia_oficina")
    private Long instanciaOficina;

    @Column(name = "motivo_ausencia_jefe")
    private String motivoAusenciaJefe;

    @Column(name = "estado")
    private String estado;

    @Column(name = "fecha_inicio_jefatura")
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date fechaInicioJefatura;

    @Column(name = "fecha_encargatura")
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date fechaEncargatura;

    @Column(name = "fecha_registro")
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date fechaRegistro;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_compania")
    private Compania compania;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_oficina_superior")
    private Oficina oficinaSuperior;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_persona_jefe")
    private Persona personaJefe;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_jefe_encargado")
    private Persona jefeEncargado;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_cargo_jefe")
    private PerfilCompania cargoJefe;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_user_registro")
    private Usuario userRegistro;

    @OneToMany(mappedBy = "oficinaSupervisora", fetch = FetchType.LAZY)
    private List<Aula> aula;

    @OneToMany(mappedBy = "oficina", fetch = FetchType.LAZY)
    private List<Colaborador> colaborador;

    @OneToMany(mappedBy = "oficina", fetch = FetchType.LAZY)
    private List<CoordinadorAmbientes> coordinadorAmbientes;

    @OneToMany(mappedBy = "oficinaSuperior", fetch = FetchType.LAZY)
    private List<Oficina> oficinasDependientes;

    @OneToMany(mappedBy = "oficina", fetch = FetchType.LAZY)
    private List<PersonaPerfil> personaPerfil;

    @Transient
    private String instanciaOficinaNombre;

    @Transient
    private String instanciaOficinaCodigo;

    @Transient
    private Date fechaFinJefatura;

    public Oficina() {
    }

    public Oficina(Object id) {
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

    public String getTipoOficina() {
        return tipoOficina;
    }

    public TipoOficinaEnum getTipoOficinaEnum() {
        if (tipoOficina == null) {
            return null;
        }
        return TipoOficinaEnum.valueOf(tipoOficina);
    }

    public void setTipoOficina(TipoOficinaEnum tipoOficina) {
        this.tipoOficina = tipoOficina.name();
    }

    public Long getInstanciaOficina() {
        return instanciaOficina;
    }

    public void setInstanciaOficina(Long instanciaOficina) {
        this.instanciaOficina = instanciaOficina;
    }

    public Oficina getOficinaSuperior() {
        return oficinaSuperior;
    }

    public void setOficinaSuperior(Oficina oficinaSuperior) {
        this.oficinaSuperior = oficinaSuperior;
    }

    public Persona getPersonaJefe() {
        return personaJefe;
    }

    public void setPersonaJefe(Persona personaJefe) {
        this.personaJefe = personaJefe;
    }

    public PerfilCompania getCargoJefe() {
        return cargoJefe;
    }

    public void setCargoJefe(PerfilCompania cargoJefe) {
        this.cargoJefe = cargoJefe;
    }

    public Date getFechaInicioJefatura() {
        return fechaInicioJefatura;
    }

    public void setFechaInicioJefatura(Date fechaInicioJefatura) {
        this.fechaInicioJefatura = fechaInicioJefatura;
    }

    public Persona getJefeEncargado() {
        return jefeEncargado;
    }

    public void setJefeEncargado(Persona jefeEncargado) {
        this.jefeEncargado = jefeEncargado;
    }

    public List<Aula> getAula() {
        return aula;
    }

    public void setAula(List<Aula> aula) {
        this.aula = aula;
    }

    public List<Colaborador> getColaborador() {
        return colaborador;
    }

    public void setColaborador(List<Colaborador> colaborador) {
        this.colaborador = colaborador;
    }

    public List<CoordinadorAmbientes> getCoordinadorAmbientes() {
        return coordinadorAmbientes;
    }

    public void setCoordinadorAmbientes(List<CoordinadorAmbientes> coordinadorAmbientes) {
        this.coordinadorAmbientes = coordinadorAmbientes;
    }

    public List<Oficina> getOficinasDependientes() {
        return oficinasDependientes;
    }

    public void setOficinasDependientes(List<Oficina> oficinasDependientes) {
        this.oficinasDependientes = oficinasDependientes;
    }

    public List<PersonaPerfil> getPersonaPerfil() {
        return personaPerfil;
    }

    public void setPersonaPerfil(List<PersonaPerfil> personaPerfil) {
        this.personaPerfil = personaPerfil;
    }

    public String getInstanciaOficinaNombre() {
        return instanciaOficinaNombre;
    }

    public void setInstanciaOficinaNombre(String instanciaOficinaNombre) {
        this.instanciaOficinaNombre = instanciaOficinaNombre;
    }

    public String getInstanciaOficinaCodigo() {
        return instanciaOficinaCodigo;
    }

    public void setInstanciaOficinaCodigo(String instanciaOficinaCodigo) {
        this.instanciaOficinaCodigo = instanciaOficinaCodigo;
    }

    public String getEstado() {
        return estado;
    }

    public OficinaEstadoEnum getEstadoEnum() {
        if (estado == null) {
            return null;
        }
        return OficinaEstadoEnum.valueOf(estado);
    }

    public void setEstado(OficinaEstadoEnum estado) {
        this.estado = estado.name();
    }

    public String getMotivoAusenciaJefe() {
        return motivoAusenciaJefe;
    }

    public void setMotivoAusenciaJefe(String motivoAusenciaJefe) {
        this.motivoAusenciaJefe = motivoAusenciaJefe;
    }

    public Date getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(Date fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    public Usuario getUserRegistro() {
        return userRegistro;
    }

    public void setUserRegistro(Usuario userRegistro) {
        this.userRegistro = userRegistro;
    }

    public Date getFechaEncargatura() {
        return fechaEncargatura;
    }

    public void setFechaEncargatura(Date fechaEncargatura) {
        this.fechaEncargatura = fechaEncargatura;
    }

    public Date getFechaFinJefatura() {
        return fechaFinJefatura;
    }

    public void setFechaFinJefatura(Date fechaFinJefatura) {
        this.fechaFinJefatura = fechaFinJefatura;
    }

}
