package pe.edu.lamolina.pivot.zelper.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import pe.edu.lamolina.model.academico.Carrera;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.DepartamentoAcademico;
import pe.edu.lamolina.model.academico.Docente;
import pe.edu.lamolina.model.academico.Facultad;
import pe.edu.lamolina.model.academico.ModalidadEstudio;
import pe.edu.lamolina.model.general.Colaborador;
import pe.edu.lamolina.model.general.Compania;
import pe.edu.lamolina.model.general.Oficina;
import pe.edu.lamolina.model.general.Persona;
import pe.edu.lamolina.model.seguridad.Menu;
import pe.edu.lamolina.model.seguridad.Rol;
import pe.edu.lamolina.model.seguridad.Usuario;

public class DataSessionPivot implements Serializable {

    private Compania compania;

    private CicloAcademico cicloAcademico;

    private String email;

    private Persona persona;

    private Usuario usuario;

    private Docente docente;

    private DepartamentoAcademico departamentoAcademico;

    private List<Rol> roles;

    private List<Rol> rolesMain;

    private Rol rolActivo;

    private List<Menu> menu;

    private List<Facultad> facultades;

    private List<Carrera> carreras;

    private List<ModalidadEstudio> modalidades;

    private List<DepartamentoAcademico> departamentos;

    private List<Oficina> oficinas;

    private List<Colaborador> colaborador;

    private Oficina oficinaMain;

    private String browser;

    private String direccionIp;

    private String sistemaOperativo;

    private Date fechaAccionAudit;

    public List<Colaborador> getColaborador() {
        return colaborador;
    }

    public void setColaborador(List<Colaborador> colaborador) {
        this.colaborador = colaborador;
    }

    public String getBrowser() {
        return browser;
    }

    public void setBrowser(String browser) {
        this.browser = browser;
    }

    public String getDireccionIp() {
        return direccionIp;
    }

    public void setDireccionIp(String direccionIp) {
        this.direccionIp = direccionIp;
    }

    public String getSistemaOperativo() {
        return sistemaOperativo;
    }

    public void setSistemaOperativo(String sistemaOperativo) {
        this.sistemaOperativo = sistemaOperativo;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public CicloAcademico getCicloAcademico() {
        return cicloAcademico;
    }

    public void setCicloAcademico(CicloAcademico cicloAcademico) {
        this.cicloAcademico = cicloAcademico;
    }

    public Persona getPersona() {
        return persona;
    }

    public void setPersona(Persona persona) {
        this.persona = persona;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public DepartamentoAcademico getDepartamentoAcademico() {
        return departamentoAcademico;
    }

    public void setDepartamentoAcademico(DepartamentoAcademico departamentoAcademico) {
        this.departamentoAcademico = departamentoAcademico;
    }

    public Docente getDocente() {
        return docente;
    }

    public void setDocente(Docente docente) {
        this.docente = docente;
    }

    public List<Rol> getRoles() {
        return roles;
    }

    public Map<Long, Rol> getMapRoles() {

        return roles.stream()
                .collect(Collectors.toMap(x -> x.getId(), x -> x));
    }

    public void setRoles(List<Rol> roles) {
        this.roles = roles;
    }

    public Rol getRolActivo() {
        return rolActivo;
    }

    public void setRolActivo(Rol rolActivo) {
        this.rolActivo = rolActivo;
    }

    public Compania getCompania() {
        return compania;
    }

    public void setCompania(Compania compania) {
        this.compania = compania;
    }

    public List<Facultad> getFacultades() {
        return facultades;
    }

    public void setFacultades(List<Facultad> facultades) {
        this.facultades = facultades;
    }

    public List<Carrera> getCarreras() {
        return carreras;
    }

    public void setCarreras(List<Carrera> carreras) {
        this.carreras = carreras;
    }

    public List<ModalidadEstudio> getModalidades() {
        return modalidades;
    }

    public void setModalidades(List<ModalidadEstudio> modalidades) {
        this.modalidades = modalidades;
    }

    public List<DepartamentoAcademico> getDepartamentos() {
        return departamentos;
    }

    public void setDepartamentos(List<DepartamentoAcademico> departamentos) {
        this.departamentos = departamentos;
    }

    public List<Menu> getMenu() {
        return menu;
    }

    public void setMenu(List<Menu> menu) {
        this.menu = menu;
    }

    public List<Oficina> getOficinas() {
        return oficinas;
    }

    public void setOficinas(List<Oficina> oficinas) {
        this.oficinas = oficinas;
    }

    public List<Rol> getRolesMain() {
        return rolesMain;
    }

    public void setRolesMain(List<Rol> rolesMain) {
        this.rolesMain = rolesMain;
    }

    public Oficina getOficinaMain() {
        return oficinaMain;
    }

    public void setOficinaMain(Oficina oficinaMain) {
        this.oficinaMain = oficinaMain;
    }

    public Date getFechaAccionAudit() {
        return fechaAccionAudit;
    }

    public void setFechaAccionAudit(Date fechaAccionAudit) {
        this.fechaAccionAudit = fechaAccionAudit;
    }

}
