package pe.edu.lamolina.pivot.model.academico;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.pivot.zelper.enums.EstadoMatriculaCursoEnum;

@Entity
@Table(name = "aca_matricula_curso")
public class MatriculaCurso implements Serializable {

    @Id
    @GeneratedValue
    @Column(name = "id")
    private Long id;

    @Column(name = "creditos")
    private Integer creditos;

    @Column(name = "estado")
    private String estado;

    @Column(name = "nota_avance")
    private String notaAvance;

    @Column(name = "nota_acumulada")
    private String notaAcumulada;

    @Column(name = "nota_final")
    private String notaFinal;

    @Column(name = "porcentaje_avance_nota")
    private Integer porcentajeAvanceNota;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_matricula_resumen")
    private MatriculaResumen matriculaResumen;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_curso")
    private Curso curso;

    public MatriculaCurso() {
    }

    public MatriculaCurso(Object id) {
        this.id = TypesUtil.getLong(id);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public MatriculaResumen getMatriculaResumen() {
        return matriculaResumen;
    }

    public void setMatriculaResumen(MatriculaResumen matriculaResumen) {
        this.matriculaResumen = matriculaResumen;
    }

    public Curso getCurso() {
        return curso;
    }

    public void setCurso(Curso curso) {
        this.curso = curso;
    }

    public Integer getCreditos() {
        return creditos;
    }

    public void setCreditos(Integer creditos) {
        this.creditos = creditos;
    }

    public String getEstado() {
        return estado;
    }

    public EstadoMatriculaCursoEnum getEstadoEnum() {
        if (estado == null) {
            return null;
        }
        return EstadoMatriculaCursoEnum.valueOf(estado);
    }

    public void setEstadoEnum(EstadoMatriculaCursoEnum estado) {
        this.estado = estado.name();
    }

    public String getNotaAvance() {
        return notaAvance;
    }

    public void setNotaAvance(String notaAvance) {
        this.notaAvance = notaAvance;
    }

    public String getNotaAcumulada() {
        return notaAcumulada;
    }

    public void setNotaAcumulada(String notaAcumulada) {
        this.notaAcumulada = notaAcumulada;
    }

    public String getNotaFinal() {
        return notaFinal;
    }

    public void setNotaFinal(String notaFinal) {
        this.notaFinal = notaFinal;
    }

    public Integer getPorcentajeAvanceNota() {
        return porcentajeAvanceNota;
    }

    public void setPorcentajeAvanceNota(Integer porcentajeAvanceNota) {
        this.porcentajeAvanceNota = porcentajeAvanceNota;
    }

}
