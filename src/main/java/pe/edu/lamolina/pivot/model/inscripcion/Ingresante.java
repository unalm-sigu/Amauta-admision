package pe.edu.lamolina.pivot.model.inscripcion;

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
import pe.edu.lamolina.pivot.model.academico.Carrera;

@Entity
@Table(name = "sip_ingresante")
public class Ingresante implements Serializable {

    @Id
    @GeneratedValue
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_postulante")
    private Postulante postulante;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_prelamolina")
    private Prelamolina prelamolina;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_evaluado")
    private Evaluado evaluado;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_carrera")
    private Carrera carrera;

    public Ingresante() {
    }

    public Ingresante(Object id) {
        this.id = TypesUtil.getLong(id);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Postulante getPostulante() {
        return postulante;
    }

    public void setPostulante(Postulante postulante) {
        this.postulante = postulante;
    }

    public Prelamolina getPrelamolina() {
        return prelamolina;
    }

    public void setPrelamolina(Prelamolina prelamolina) {
        this.prelamolina = prelamolina;
    }

    public Evaluado getEvaluado() {
        return evaluado;
    }

    public void setEvaluado(Evaluado evaluado) {
        this.evaluado = evaluado;
    }

    public Carrera getCarrera() {
        return carrera;
    }

    public void setCarrera(Carrera carrera) {
        this.carrera = carrera;
    }

}

