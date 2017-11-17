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

    @Column(name = "fecha_inicio_jefatura")
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date fechaInicioJefatura;

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
    @JoinColumn(name = "id_cargo_jefe")
    private PerfilCompania cargoJefe;

    @Column(name = "estado")
    private String estado;

    @OneToMany(mappedBy = "oficinaSupervisora", fetch = FetchType.LAZY)
    private List<Aula> aula;

    @OneToMany(mappedBy = "oficina", fetch = FetchType.LAZY)
    private List<Colaborador> colaborador;

    @OneToMany(mappedBy = "oficina", fetch = FetchType.LAZY)
    private List<CoordinadorAmbientes> coordinadorAmbientes;

    @OneToMany(mappedBy = "oficinaSuperior", fetch = FetchType.LAZY)
    private List<Oficina> oficina;

    @OneToMany(mappedBy = "oficina", fetch = FetchType.LAZY)
    private List<PersonaPerfil> personaPerfil;

    @Transient
    private String instanciaOficinaNombre;

    @Transient
    private String instanciaOficinaCodigo;

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

    public void setTipoOficina(String tipoOficina) {
        this.tipoOficina = tipoOficina;
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

    public void setEstado(String estado) {
        this.estado = estado;
    }

}
