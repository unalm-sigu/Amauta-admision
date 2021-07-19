package pe.edu.lamolina.amauta.controller.fotoCarne;

import java.math.BigDecimal;
import static java.math.BigDecimal.ZERO;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;
import pe.edu.lamolina.model.academico.MatriculaResumen;

@Component
public class FotosCarneUploadComponent {

    private Integer total;

    private Integer avance;

    private boolean iniciado;

    List<MatriculaResumen> matriculaResumens;

    BigDecimal perAvance;

    public FotosCarneUploadComponent() {
        this.total = 0;
        this.avance = 0;
        this.iniciado = false;
        this.perAvance = ZERO;
    }

    public Integer getCantidadTotal() {
        return total;
    }

    public void setCantidadTotal(Integer cantidadTotal) {
        this.total = cantidadTotal;
    }

    public Integer getAvance() {
        return avance;
    }

    public void setAvance(Integer avance) {
        this.avance = avance;
    }

    public boolean isIniciado() {
        return iniciado;
    }

    public void setIniciado(boolean iniciado) {
        this.iniciado = iniciado;
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
        if (!this.iniciado) {
            return ZERO;
        }
        if (this.total < 1) {
            return ZERO;
        }
        if (this.avance < 1) {
            return ZERO;
        }

        return ((new BigDecimal(this.avance)).multiply(new BigDecimal("90")))
                .divide(new BigDecimal(this.total), 2, RoundingMode.HALF_UP);

    }

    public void setPerAvance(BigDecimal perAvance) {

        this.perAvance = perAvance;
    }

    public void iniciarProceso(List<MatriculaResumen> matriculaResumens) {
        if (this.iniciado) {
            return;
        }
        this.iniciado = true;
        this.matriculaResumens = matriculaResumens;
        this.total = matriculaResumens.size();
        this.avance = 0;
        this.perAvance = ZERO;
    }

    public void finalizarProceso() {
        this.iniciado = false;
        this.matriculaResumens = null;
        this.total = 0;
        this.avance = 0;
        this.perAvance = ZERO;
    }

    @Override
    public String toString() {
        return "FotosCarneComponent{" + "total=" + total + ", avance=" + avance + ", iniciado=" + iniciado + ", perAvance=" + perAvance + "}";
    }

}
