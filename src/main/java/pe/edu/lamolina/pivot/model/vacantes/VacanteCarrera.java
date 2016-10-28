package pe.edu.lamolina.pivot.model.vacantes;

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
import pe.edu.lamolina.pivot.model.inscripcion.CarreraPostula;
import pe.edu.lamolina.pivot.model.inscripcion.CicloPostula;
import pe.edu.lamolina.pivot.model.inscripcion.ModalidadIngreso;

@Entity
@Table(name = "vac_vacante_carrera")
public class VacanteCarrera implements Serializable {

    @Id
    @GeneratedValue
    @Column(name = "id")
    private Long id;

    @Column(name = "cupos")
    private Integer cupos;

    @Column(name = "tipo_ciclo")
    private String tipoCiclo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_ciclo_postula")
    private CicloPostula cicloPostula;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_carrera_postula")
    private CarreraPostula carreraPostula;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_modalidad_ingreso")
    private ModalidadIngreso modalidadIngreso;

    public VacanteCarrera() {
    }

    public VacanteCarrera(Object id) {
        this.id = TypesUtil.getLong(id);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public CicloPostula getCicloPostula() {
        return cicloPostula;
    }

    public void setCicloPostula(CicloPostula cicloPostula) {
        this.cicloPostula = cicloPostula;
    }

    public CarreraPostula getCarreraPostula() {
        return carreraPostula;
    }

    public void setCarreraPostula(CarreraPostula carreraPostula) {
        this.carreraPostula = carreraPostula;
    }

    public ModalidadIngreso getModalidadIngreso() {
        return modalidadIngreso;
    }

    public void setModalidadIngreso(ModalidadIngreso modalidadIngreso) {
        this.modalidadIngreso = modalidadIngreso;
    }

    public Integer getCupos() {
        return cupos;
    }

    public void setCupos(Integer cupos) {
        this.cupos = cupos;
    }

    public String getTipoCiclo() {
        return tipoCiclo;
    }

    public void setTipoCiclo(String tipoCiclo) {
        this.tipoCiclo = tipoCiclo;
    }

}

