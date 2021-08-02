package pe.edu.lamolina.amauta.controller.fotocarne;

import java.math.BigDecimal;
import static java.math.BigDecimal.ZERO;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import org.springframework.stereotype.Component;
import pe.edu.lamolina.model.academico.MatriculaResumen;

@Component
@Data
public class FotosCarneDown {

    private Integer total;

    private Integer avance;

    private boolean iniciado;

    private List<MatriculaResumen> matriculaResumens;

    private List<MsjError> errores;

    private BigDecimal perAvance;

    private String pathFile;

    public FotosCarneDown() {
        this.total = 0;
        this.avance = 0;
        this.iniciado = false;
        this.perAvance = ZERO;
        this.errores = new ArrayList();
    }

    public BigDecimal getPerAvance() {

        if (this.total < 1) {
            return ZERO;
        }
        if (this.avance < 1) {
            return ZERO;
        }

        return ((new BigDecimal(this.avance)).multiply(new BigDecimal("100")))
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
        this.pathFile = "";
        this.errores = new ArrayList();
    }

    public void finalizarProceso() {
        this.iniciado = false;
        this.matriculaResumens = null;
    }

}
