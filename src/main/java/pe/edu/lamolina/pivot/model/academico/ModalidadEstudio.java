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
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.pivot.model.general.Compania;
import pe.edu.lamolina.pivot.model.inscripcion.ModalidadIngreso;

@Entity
@Table(name = "aca_modalidad_estudio")
public class ModalidadEstudio implements Serializable {

    @Id
    @GeneratedValue
    @Column(name = "id")
    private Long id;

    @Column(name = "nombre")
    private String nombre;

    @Column(name = "estado")
    private String estado;

    @Column(name = "codigo")
    private String codigo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_compania")
    private Compania compania;

    @OneToMany(mappedBy = "modalidadEstudio", fetch = FetchType.LAZY)
    private List<Carrera> carrera;

    @OneToMany(mappedBy = "modalidadEstudio", fetch = FetchType.LAZY)
    private List<CicloAcademico> cicloAcademico;

    @OneToMany(mappedBy = "modalidadEstudio", fetch = FetchType.LAZY)
    private List<Docente> docente;

    @OneToMany(mappedBy = "modalidadEstudio", fetch = FetchType.LAZY)
    private List<ModalidadIngreso> modalidadIngreso;

    public ModalidadEstudio() {
    }

    public ModalidadEstudio(Object id) {
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

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public List<Carrera> getCarrera() {
        return carrera;
    }

    public void setCarrera(List<Carrera> carrera) {
        this.carrera = carrera;
    }

    public List<CicloAcademico> getCicloAcademico() {
        return cicloAcademico;
    }

    public void setCicloAcademico(List<CicloAcademico> cicloAcademico) {
        this.cicloAcademico = cicloAcademico;
    }

    public List<Docente> getDocente() {
        return docente;
    }

    public void setDocente(List<Docente> docente) {
        this.docente = docente;
    }

    public List<ModalidadIngreso> getModalidadIngreso() {
        return modalidadIngreso;
    }

    public void setModalidadIngreso(List<ModalidadIngreso> modalidadIngreso) {
        this.modalidadIngreso = modalidadIngreso;
    }

}

