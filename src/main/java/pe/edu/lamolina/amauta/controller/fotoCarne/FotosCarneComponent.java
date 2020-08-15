package pe.edu.lamolina.amauta.controller.fotoCarne;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;
import pe.edu.lamolina.model.academico.MatriculaResumen;

@Component
public class FotosCarneComponent {

    Integer cantidadTotal = 0;
    Integer avance = 0;
    String estado = "INA";
    List<MatriculaResumen> matriculaResumens;
    BigDecimal perAvance;

    public Integer getCantidadTotal() {
        return cantidadTotal;
    }

    public void setCantidadTotal(Integer cantidadTotal) {
        this.cantidadTotal = cantidadTotal;
    }

    public Integer getAvance() {
        return avance;
    }

    public void setAvance(Integer avance) {
        this.avance = avance;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public List<MatriculaResumen> getMatriculaResumens() {
        if (matriculaResumens == null) {
            return new ArrayList();
        }
        return matriculaResumens;
    }

    public void setMatriculaResumens(List<MatriculaResumen> matriculaResumens) {
        this.matriculaResumens = matriculaResumens;
    }

    public BigDecimal getPerAvance() {
        return this.perAvance;
    }

    public void setPerAvance(BigDecimal perAvance) {

        this.perAvance = perAvance;
    }

}
