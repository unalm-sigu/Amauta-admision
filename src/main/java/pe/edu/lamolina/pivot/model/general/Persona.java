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
import org.apache.commons.lang3.StringUtils;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.pivot.model.academico.Alumno;
import pe.edu.lamolina.pivot.model.academico.Docente;
import pe.edu.lamolina.pivot.model.inscripcion.Postulante;
import pe.edu.lamolina.pivot.model.seguridad.Usuario;
import pe.edu.lamolina.pivot.model.tramite.Tramite;

@Entity
@Table(name = "gen_persona")
public class Persona implements Serializable {

    @Id
    @GeneratedValue
    @Column(name = "id")
    private Long id;

    @Column(name = "paterno")
    private String paterno;

    @Column(name = "materno")
    private String materno;

    @Column(name = "nombres")
    private String nombres;

    @Column(name = "sexo")
    private String sexo;

    @Column(name = "email")
    private String email;

    @Column(name = "email_compania")
    private String emailCompania;

    @Column(name = "celular")
    private String celular;

    @Column(name = "telefono")
    private String telefono;

    @Column(name = "numero_doc_identidad")
    private String numeroDocIdentidad;

    @Column(name = "direccion")
    private String direccion;

    @Column(name = "titulo_academico")
    private String tituloAcademico;

    @Column(name = "foto")
    private String foto;

    @Column(name = "estado")
    private String estado;

    @Column(name = "fecha_nacer")
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date fechaNacer;

    @Column(name = "fecha_traslado")
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date fechaTraslado;

    @Column(name = "fecha_registro")
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date fechaRegistro;

    @Column(name = "fecha_validacion")
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date fechaValidacion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_ubicacion_nacer")
    private Ubicacion ubicacionNacer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_tipo_documento")
    private TipoDocIdentidad tipoDocumento;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_pais_nacer")
    private Pais paisNacer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_nacionalidad")
    private Pais nacionalidad;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_ubicacion_domicilio")
    private Ubicacion ubicacionDomicilio;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_persona_traslado")
    private Persona personaTraslado;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_user_validacion")
    private Usuario userValidacion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_user_traslado")
    private Usuario userTraslado;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_user_registro")
    private Usuario userRegistro;

    @OneToMany(mappedBy = "persona", fetch = FetchType.LAZY)
    private List<Alumno> alumno;

    @OneToMany(mappedBy = "persona", fetch = FetchType.LAZY)
    private List<Docente> docente;

    @OneToMany(mappedBy = "persona", fetch = FetchType.LAZY)
    private List<Colaborador> colaborador;

    @OneToMany(mappedBy = "coordinador", fetch = FetchType.LAZY)
    private List<CoordinadorAmbientes> coordinadorAmbientes;

    @OneToMany(mappedBy = "personaJefe", fetch = FetchType.LAZY)
    private List<Oficina> oficina;

    @OneToMany(mappedBy = "persona", fetch = FetchType.LAZY)
    private List<PersonaPerfil> personaPerfil;

    @OneToMany(mappedBy = "persona", fetch = FetchType.LAZY)
    private List<Usuario> usuario;

    @OneToMany(mappedBy = "persona", fetch = FetchType.LAZY)
    private List<Postulante> postulante;

    @OneToMany(mappedBy = "persona", fetch = FetchType.LAZY)
    private List<Tramite> tramite;

    @Transient
    private String codigoTipoDocumento;

    public Persona() {
    }

    public Persona(Persona tempo) {
        paterno = tempo.getPaterno();
        materno = tempo.getMaterno();
        nombres = tempo.getNombres();
        sexo = tempo.getSexo();
        email = tempo.getEmail();
        emailCompania = tempo.getEmailCompania();
        telefono = tempo.getTelefono();
        celular = tempo.getCelular();
        tipoDocumento = tempo.getTipoDocumento();
        numeroDocIdentidad = tempo.getNumeroDocIdentidad();
        direccion = tempo.getDireccion();
        tituloAcademico = tempo.getTituloAcademico();
        foto = tempo.getFoto();
        fechaNacer = tempo.getFechaNacer();
        ubicacionNacer = tempo.getUbicacionNacer();
        paisNacer = tempo.getPaisNacer();
        nacionalidad = tempo.getNacionalidad();
        ubicacionDomicilio = tempo.getUbicacionDomicilio();
    }

    public Persona(String paterno, String materno, String nombres, String numeroDocIdentidad, String codigoTipoDocumento) {
        this.paterno = paterno;
        this.materno = materno;
        this.nombres = nombres;
        this.numeroDocIdentidad = numeroDocIdentidad;
        this.codigoTipoDocumento = codigoTipoDocumento;
    }

    public Persona(Object id) {
        this.id = TypesUtil.getLong(id);
    }

    public Persona(TipoDocIdentidad tipo, String numeroDocIdentidad) {
        this.tipoDocumento = tipo;
        this.numeroDocIdentidad = numeroDocIdentidad;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPaterno() {
        return paterno;
    }

    public void setPaterno(String paterno) {
        this.paterno = paterno;
    }

    public String getMaterno() {
        return materno;
    }

    public void setMaterno(String materno) {
        this.materno = materno;
    }

    public String getNombres() {
        return nombres;
    }

    public void setNombres(String nombres) {
        this.nombres = nombres;
    }

    public String getSexo() {
        return sexo;
    }

    public void setSexo(String sexo) {
        this.sexo = sexo;
    }

    public Date getFechaNacer() {
        return fechaNacer;
    }

    public void setFechaNacer(Date fechaNacer) {
        this.fechaNacer = fechaNacer;
    }

    public Ubicacion getUbicacionNacer() {
        return ubicacionNacer;
    }

    public void setUbicacionNacer(Ubicacion ubicacionNacer) {
        this.ubicacionNacer = ubicacionNacer;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCelular() {
        return celular;
    }

    public void setCelular(String celular) {
        this.celular = celular;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public TipoDocIdentidad getTipoDocumento() {
        return tipoDocumento;
    }

    public void setTipoDocumento(TipoDocIdentidad tipoDocumento) {
        this.tipoDocumento = tipoDocumento;
    }

    public String getNumeroDocIdentidad() {
        return numeroDocIdentidad;
    }

    public void setNumeroDocIdentidad(String numeroDocIdentidad) {
        this.numeroDocIdentidad = numeroDocIdentidad;
    }

    public Pais getPaisNacer() {
        return paisNacer;
    }

    public void setPaisNacer(Pais paisNacer) {
        this.paisNacer = paisNacer;
    }

    public Pais getNacionalidad() {
        return nacionalidad;
    }

    public void setNacionalidad(Pais nacionalidad) {
        this.nacionalidad = nacionalidad;
    }

    public Ubicacion getUbicacionDomicilio() {
        return ubicacionDomicilio;
    }

    public void setUbicacionDomicilio(Ubicacion ubicacionDomicilio) {
        this.ubicacionDomicilio = ubicacionDomicilio;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public List<Alumno> getAlumno() {
        return alumno;
    }

    public void setAlumno(List<Alumno> alumno) {
        this.alumno = alumno;
    }

    public List<Docente> getDocente() {
        return docente;
    }

    public void setDocente(List<Docente> docente) {
        this.docente = docente;
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

    public List<Usuario> getUsuario() {
        return usuario;
    }

    public void setUsuario(List<Usuario> usuario) {
        this.usuario = usuario;
    }

    public List<Postulante> getPostulante() {
        return postulante;
    }

    public void setPostulante(List<Postulante> postulante) {
        this.postulante = postulante;
    }

    public List<Tramite> getTramite() {
        return tramite;
    }

    public void setTramite(List<Tramite> tramite) {
        this.tramite = tramite;
    }

    public String getTituloAcademico() {
        return tituloAcademico;
    }

    public void setTituloAcademico(String tituloAcademico) {
        this.tituloAcademico = tituloAcademico;
    }

    public String getEmailCompania() {
        return emailCompania;
    }

    public void setEmailCompania(String emailCompania) {
        this.emailCompania = emailCompania;
    }

    public String getCodigoTipoDocumento() {
        return codigoTipoDocumento;
    }

    public void setCodigoTipoDocumento(String codigoTipoDocumento) {
        this.codigoTipoDocumento = codigoTipoDocumento;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Date getFechaTraslado() {
        return fechaTraslado;
    }

    public void setFechaTraslado(Date fechaTraslado) {
        this.fechaTraslado = fechaTraslado;
    }

    public Date getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(Date fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    public Date getFechaValidacion() {
        return fechaValidacion;
    }

    public void setFechaValidacion(Date fechaValidacion) {
        this.fechaValidacion = fechaValidacion;
    }

    public Persona getPersonaTraslado() {
        return personaTraslado;
    }

    public void setPersonaTraslado(Persona personaTraslado) {
        this.personaTraslado = personaTraslado;
    }

    public Usuario getUserValidacion() {
        return userValidacion;
    }

    public void setUserValidacion(Usuario userValidacion) {
        this.userValidacion = userValidacion;
    }

    public Usuario getUserTraslado() {
        return userTraslado;
    }

    public void setUserTraslado(Usuario userTraslado) {
        this.userTraslado = userTraslado;
    }

    public Usuario getUserRegistro() {
        return userRegistro;
    }

    public void setUserRegistro(Usuario userRegistro) {
        this.userRegistro = userRegistro;
    }

    public String getNombreCompleto() {
        return this.nombres
                + (StringUtils.isEmpty(this.paterno) ? "" : (" " + this.paterno))
                + (StringUtils.isEmpty(this.materno) ? "" : (" " + this.materno));
    }

    public String getNombreConTitulo() {
        return (StringUtils.isEmpty(this.tituloAcademico) ? "" : (this.tituloAcademico + " ")) + getNombreCompleto();
    }

    public String getApellidosNombres() {
        return (StringUtils.isEmpty(this.paterno) ? "" : this.paterno) + (StringUtils.isEmpty(this.materno) ? "" : (" " + this.materno)) + ", " + this.nombres;
    }

    public String getPaternoNombre() {
        String nom = (StringUtils.isEmpty(this.paterno) ? "" : this.paterno);
        if (StringUtils.isEmpty(nom)) {
            nom = (StringUtils.isEmpty(this.materno) ? "" : this.materno);
        }

        return this.nombres.split(" ")[0] + " " + nom;
    }

    public String getNombrePaterno() {
        String pat = (StringUtils.isEmpty(this.paterno) ? "" : this.paterno);
        if (StringUtils.isEmpty(pat)) {
            pat = (StringUtils.isEmpty(this.materno) ? "" : this.materno);
        }

        return this.nombres.split(" ")[0] + " " + pat;
    }

    public String getApellidos() {
        return (StringUtils.isEmpty(this.paterno) ? "" : this.paterno) + (StringUtils.isEmpty(this.materno) ? "" : " " + this.materno);
    }

    public String getAvatar() {

        String avatar = "";

        if (this.nombres != null) {
            avatar += this.nombres.substring(0, 1).toUpperCase();
        }

        if (this.paterno != null) {
            avatar += this.paterno.substring(0, 1).toUpperCase();
        }

        return avatar;
    }

}
