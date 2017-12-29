package pe.edu.lamolina.pivot.zelper.model;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import pe.edu.lamolina.model.academico.Carrera;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.DepartamentoAcademico;
import pe.edu.lamolina.model.academico.Docente;
import pe.edu.lamolina.model.academico.Facultad;
import pe.edu.lamolina.model.academico.ModalidadEstudio;
import pe.edu.lamolina.model.general.Compania;
import pe.edu.lamolina.model.general.Persona;
import pe.edu.lamolina.model.seguridad.Menu;
import pe.edu.lamolina.model.seguridad.Rol;
import pe.edu.lamolina.model.seguridad.Usuario;

public class DataSessionPivot implements Serializable {

    private String email;

    private CicloAcademico cicloAcademico;

    private Persona persona;

    private Usuario usuario;

    private Docente docente;

    private DepartamentoAcademico departamentoAcademico;

    private List<Rol> roles;

    private Rol rolActivo;

    private Compania compania;

    private List<Facultad> facultados;

    private List<Carrera> carreras;

    private List<ModalidadEstudio> modalidades;

    private List<DepartamentoAcademico> departamentos;

    private List<Menu> menu;

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

    public List<Facultad> getFacultados() {
        return facultados;
    }

    public void setFacultados(List<Facultad> facultados) {
        this.facultados = facultados;
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

}
