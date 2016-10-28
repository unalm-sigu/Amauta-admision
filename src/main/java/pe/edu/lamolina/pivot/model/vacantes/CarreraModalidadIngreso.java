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
@Table(name = "vac_carrera_modalidad_ingreso")
public class CarreraModalidadIngreso implements Serializable {

    @Id
    @GeneratedValue
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_ciclo_postula")
    private CicloPostula cicloPostula;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_modalidad_ingreso")
    private ModalidadIngreso modalidadIngreso;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_carrera_postula")
    private CarreraPostula carreraPostula;

    public CarreraModalidadIngreso() {
    }

    public CarreraModalidadIngreso(Object id) {
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

    public ModalidadIngreso getModalidadIngreso() {
        return modalidadIngreso;
    }

    public void setModalidadIngreso(ModalidadIngreso modalidadIngreso) {
        this.modalidadIngreso = modalidadIngreso;
    }

    public CarreraPostula getCarreraPostula() {
        return carreraPostula;
    }

    public void setCarreraPostula(CarreraPostula carreraPostula) {
        this.carreraPostula = carreraPostula;
    }

}

