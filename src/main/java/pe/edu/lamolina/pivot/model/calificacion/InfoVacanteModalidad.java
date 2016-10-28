package pe.edu.lamolina.pivot.model.calificacion;

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
import pe.edu.lamolina.pivot.model.inscripcion.CicloPostula;
import pe.edu.lamolina.pivot.model.inscripcion.ModalidadIngreso;

@Entity
@Table(name = "sce_info_vacante_modalidad")
public class InfoVacanteModalidad implements Serializable {

    @Id
    @GeneratedValue
    @Column(name = "id")
    private Long id;

    @Column(name = "devuelve_vacantes")
    private Integer devuelveVacantes;

    @Column(name = "porcentaje_aumento_nota")
    private Integer porcentajeAumentoNota;

    @Column(name = "control_nota_minima_ordinario")
    private Integer controlNotaMinimaOrdinario;

    @Column(name = "nota_minima")
    private Integer notaMinima;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_ciclo_postula")
    private CicloPostula cicloPostula;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_modalidad_ingreso")
    private ModalidadIngreso modalidadIngreso;

    public InfoVacanteModalidad() {
    }

    public InfoVacanteModalidad(Object id) {
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

    public Integer getDevuelveVacantes() {
        return devuelveVacantes;
    }

    public void setDevuelveVacantes(Integer devuelveVacantes) {
        this.devuelveVacantes = devuelveVacantes;
    }

    public Integer getPorcentajeAumentoNota() {
        return porcentajeAumentoNota;
    }

    public void setPorcentajeAumentoNota(Integer porcentajeAumentoNota) {
        this.porcentajeAumentoNota = porcentajeAumentoNota;
    }

    public Integer getControlNotaMinimaOrdinario() {
        return controlNotaMinimaOrdinario;
    }

    public void setControlNotaMinimaOrdinario(Integer controlNotaMinimaOrdinario) {
        this.controlNotaMinimaOrdinario = controlNotaMinimaOrdinario;
    }

    public Integer getNotaMinima() {
        return notaMinima;
    }

    public void setNotaMinima(Integer notaMinima) {
        this.notaMinima = notaMinima;
    }

}

