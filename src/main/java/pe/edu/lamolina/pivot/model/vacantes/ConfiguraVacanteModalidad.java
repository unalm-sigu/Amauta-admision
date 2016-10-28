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
import pe.edu.lamolina.pivot.model.inscripcion.CicloPostula;
import pe.edu.lamolina.pivot.model.inscripcion.ModalidadIngreso;

@Entity
@Table(name = "vac_configura_vacante_modalidad")
public class ConfiguraVacanteModalidad implements Serializable {

    @Id
    @GeneratedValue
    @Column(name = "id")
    private Long id;

    @Column(name = "vacantes_fija")
    private Integer vacantesFija;

    @Column(name = "porcentaje")
    private Integer porcentaje;

    @Column(name = "referencia_porcentaje")
    private String referenciaPorcentaje;

    @Column(name = "supernumerario")
    private Integer supernumerario;

    @Column(name = "por_carrera")
    private Integer porCarrera;

    @Column(name = "tipo_ciclo")
    private String tipoCiclo;

    @Column(name = "vacantes_total")
    private Integer vacantesTotal;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_ciclo_postula")
    private CicloPostula cicloPostula;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_modalidad_ingreso")
    private ModalidadIngreso modalidadIngreso;

    public ConfiguraVacanteModalidad() {
    }

    public ConfiguraVacanteModalidad(Object id) {
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

    public Integer getVacantesFija() {
        return vacantesFija;
    }

    public void setVacantesFija(Integer vacantesFija) {
        this.vacantesFija = vacantesFija;
    }

    public Integer getPorcentaje() {
        return porcentaje;
    }

    public void setPorcentaje(Integer porcentaje) {
        this.porcentaje = porcentaje;
    }

    public String getReferenciaPorcentaje() {
        return referenciaPorcentaje;
    }

    public void setReferenciaPorcentaje(String referenciaPorcentaje) {
        this.referenciaPorcentaje = referenciaPorcentaje;
    }

    public Integer getSupernumerario() {
        return supernumerario;
    }

    public void setSupernumerario(Integer supernumerario) {
        this.supernumerario = supernumerario;
    }

    public Integer getPorCarrera() {
        return porCarrera;
    }

    public void setPorCarrera(Integer porCarrera) {
        this.porCarrera = porCarrera;
    }

    public String getTipoCiclo() {
        return tipoCiclo;
    }

    public void setTipoCiclo(String tipoCiclo) {
        this.tipoCiclo = tipoCiclo;
    }

    public Integer getVacantesTotal() {
        return vacantesTotal;
    }

    public void setVacantesTotal(Integer vacantesTotal) {
        this.vacantesTotal = vacantesTotal;
    }

}

