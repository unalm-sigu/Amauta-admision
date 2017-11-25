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

@Entity
@Table(name = "aca_carrera_cachimbos")
public class CarreraCachimbos implements Serializable {

    @Id
    @GeneratedValue
    @Column(name = "id")
    private Long id;

    @Column(name = "ingresantes")
    private Integer ingresantes;

    @Column(name = "con_horario")
    private Integer conHorario;

    @Column(name = "sin_horario")
    private Integer sinHorario;

    @Column(name = "suspendidos")
    private Integer suspendidos;

    @Column(name = "matriculados")
    private Integer matriculados;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_ciclo_academico")
    private CicloAcademico cicloAcademico;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_carrera")
    private Carrera carrera;

    public CarreraCachimbos() {
    }

    public CarreraCachimbos(Object id) {
        this.id = TypesUtil.getLong(id);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public CicloAcademico getCicloAcademico() {
        return cicloAcademico;
    }

    public void setCicloAcademico(CicloAcademico cicloAcademico) {
        this.cicloAcademico = cicloAcademico;
    }

    public Carrera getCarrera() {
        return carrera;
    }

    public void setCarrera(Carrera carrera) {
        this.carrera = carrera;
    }

    public Integer getIngresantes() {
        return ingresantes;
    }

    public void setIngresantes(Integer ingresantes) {
        this.ingresantes = ingresantes;
    }

    public Integer getConHorario() {
        return conHorario;
    }

    public void setConHorario(Integer conHorario) {
        this.conHorario = conHorario;
    }

    public Integer getSinHorario() {
        return sinHorario;
    }

    public void setSinHorario(Integer sinHorario) {
        this.sinHorario = sinHorario;
    }

    public Integer getSuspendidos() {
        return suspendidos;
    }

    public void setSuspendidos(Integer suspendidos) {
        this.suspendidos = suspendidos;
    }

    public Integer getMatriculados() {
        return matriculados;
    }

    public void setMatriculados(Integer matriculados) {
        this.matriculados = matriculados;
    }

}

