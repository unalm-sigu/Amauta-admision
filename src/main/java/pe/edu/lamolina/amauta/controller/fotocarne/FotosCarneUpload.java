package pe.edu.lamolina.amauta.controller.fotocarne;

import java.math.BigDecimal;
import static java.math.BigDecimal.ZERO;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class FotosCarneUpload {

    private Integer total;

    private Integer avance;

    private boolean iniciado;

    private BigDecimal perAvance;

    private List<MsjError> errores;

    public FotosCarneUpload() {
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

    public void iniciarProceso() {
        if (this.iniciado) {
            return;
        }
        this.iniciado = true;
        this.avance = 0;
        this.perAvance = ZERO;
        this.total = 0;
        this.errores = new ArrayList();
    }

    public void finalizarProceso() {
        this.iniciado = false;
    }

}
