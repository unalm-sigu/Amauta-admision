package pe.edu.lamolina.pivot.model.academico;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import pe.albatross.zelpers.miscelanea.TypesUtil;

@Entity
@Table(name = "aca_situacion_academica")
public class SituacionAcademica implements Serializable {

    @Id
    @GeneratedValue
    @Column(name = "id")
    private Long id;

    @Column(name = "codigo")
    private String codigo;

    @Column(name = "nombre")
    private String nombre;

    @OneToMany(mappedBy = "situacionAcademica", fetch = FetchType.LAZY)
    private List<Alumno> alumno;

    @OneToMany(mappedBy = "situacionInicio", fetch = FetchType.LAZY)
    private List<AlumnoCiclo> alumnoCiclo;

    @OneToMany(mappedBy = "situacionFinal", fetch = FetchType.LAZY)
    private List<AlumnoCiclo> alumnoCiclo1;

    @OneToMany(mappedBy = "situacionInicio", fetch = FetchType.LAZY)
    private List<MatriculaResumen> matriculaResumen;

    @OneToMany(mappedBy = "situacionFinal", fetch = FetchType.LAZY)
    private List<MatriculaResumen> matriculaResumen1;

    public SituacionAcademica() {
    }

    public SituacionAcademica(Object id) {
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

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public List<Alumno> getAlumno() {
        return alumno;
    }

    public void setAlumno(List<Alumno> alumno) {
        this.alumno = alumno;
    }

    public List<AlumnoCiclo> getAlumnoCiclo() {
        return alumnoCiclo;
    }

    public void setAlumnoCiclo(List<AlumnoCiclo> alumnoCiclo) {
        this.alumnoCiclo = alumnoCiclo;
    }

    public List<AlumnoCiclo> getAlumnoCiclo1() {
        return alumnoCiclo1;
    }

    public void setAlumnoCiclo1(List<AlumnoCiclo> alumnoCiclo1) {
        this.alumnoCiclo1 = alumnoCiclo1;
    }

    public List<MatriculaResumen> getMatriculaResumen() {
        return matriculaResumen;
    }

    public void setMatriculaResumen(List<MatriculaResumen> matriculaResumen) {
        this.matriculaResumen = matriculaResumen;
    }

    public List<MatriculaResumen> getMatriculaResumen1() {
        return matriculaResumen1;
    }

    public void setMatriculaResumen1(List<MatriculaResumen> matriculaResumen1) {
        this.matriculaResumen1 = matriculaResumen1;
    }

}

