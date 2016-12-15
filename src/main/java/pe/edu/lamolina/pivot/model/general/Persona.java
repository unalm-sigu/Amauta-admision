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

    @Column(name = "fecha_nacer")
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date fechaNacer;

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

    public Persona() {
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

    public String getNombreCompleto() {
        return this.nombres + (StringUtils.isEmpty(this.paterno) ? "" : (" " + this.paterno)) + (StringUtils.isEmpty(this.materno) ? "" : (" " + this.materno));
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
