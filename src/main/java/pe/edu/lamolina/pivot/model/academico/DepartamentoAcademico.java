package pe.edu.lamolina.pivot.model.academico;

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
import javax.persistence.Transient;
import pe.albatross.zelpers.miscelanea.TypesUtil;

@Entity
@Table(name = "aca_departamento_academico")
public class DepartamentoAcademico implements Serializable {

    @Id
    @GeneratedValue
    @Column(name = "id")
    private Long id;

    @Column(name = "codigo")
    private String codigo;

    @Column(name = "estado")
    private String estado;

    @Column(name = "nombre")
    private String nombre;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_facultad")
    private Facultad facultad;

    @OneToMany(mappedBy = "departamentoAcademico", fetch = FetchType.LAZY)
    private List<Curso> curso;

    @OneToMany(mappedBy = "departamentoAcademico", fetch = FetchType.LAZY)
    private List<DistanciaPabellon> distanciaPabellon;

    @OneToMany(mappedBy = "departamentoAcademico", fetch = FetchType.LAZY)
    private List<Docente> docente;

    @OneToMany(mappedBy = "departamentoAcademico", fetch = FetchType.LAZY)
    private List<PlanCalificacion> planCalificacion;

    @Transient
    Long cantidadGruposAbiertos;

    @Transient
    Long cantidadGruposCerrados;

    @Transient
    Long totalGrupos;

    public DepartamentoAcademico() {
    }

    public DepartamentoAcademico(Object id) {
        this.id = TypesUtil.getLong(id);
    }

    public DepartamentoAcademico(Object id, Object cantidadGruposCerrados, Object cantidadGruposAbiertos, Object totalGrupos) {
        this.id = TypesUtil.getLong(id);
        this.cantidadGruposAbiertos = TypesUtil.getLong(cantidadGruposAbiertos);
        this.cantidadGruposCerrados = TypesUtil.getLong(cantidadGruposCerrados);
        this.totalGrupos = TypesUtil.getLong(totalGrupos);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Facultad getFacultad() {
        return facultad;
    }

    public void setFacultad(Facultad facultad) {
        this.facultad = facultad;
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

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public List<Curso> getCurso() {
        return curso;
    }

    public void setCurso(List<Curso> curso) {
        this.curso = curso;
    }

    public List<DistanciaPabellon> getDistanciaPabellon() {
        return distanciaPabellon;
    }

    public void setDistanciaPabellon(List<DistanciaPabellon> distanciaPabellon) {
        this.distanciaPabellon = distanciaPabellon;
    }

    public List<Docente> getDocente() {
        return docente;
    }

    public void setDocente(List<Docente> docente) {
        this.docente = docente;
    }

    public List<PlanCalificacion> getPlanCalificacion() {
        return planCalificacion;
    }

    public void setPlanCalificacion(List<PlanCalificacion> planCalificacion) {
        this.planCalificacion = planCalificacion;
    }

    public Long getCantidadGruposAbiertos() {
        return cantidadGruposAbiertos;
    }

    public void setCantidadGruposAbiertos(Long cantidadGruposAbiertos) {
        this.cantidadGruposAbiertos = cantidadGruposAbiertos;
    }

    public Long getCantidadGruposCerrados() {
        return cantidadGruposCerrados;
    }

    public void setCantidadGruposCerrados(Long cantidadGruposCerrados) {
        this.cantidadGruposCerrados = cantidadGruposCerrados;
    }

    public Long getTotalGrupos() {
        return totalGrupos;
    }

    public void setTotalGrupos(Long totalGrupos) {
        this.totalGrupos = totalGrupos;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof DepartamentoAcademico)) {
            return false;
        }
        DepartamentoAcademico other = (DepartamentoAcademico) obj;
        if (id != other.id) {
            return false;
        }
        return true;
    }

}
