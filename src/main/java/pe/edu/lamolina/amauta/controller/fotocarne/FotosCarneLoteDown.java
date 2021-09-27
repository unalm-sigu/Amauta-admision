package pe.edu.lamolina.amauta.controller.fotocarne;

import java.math.BigDecimal;
import static java.math.BigDecimal.ZERO;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.MatriculaResumen;

@Component
public class FotosCarneLoteDown {

    private Integer total;

    private Integer avance;

    private boolean iniciado;

    private List<MatriculaResumen> matriculaResumens;

    private List<MsjError> errores;

    private BigDecimal perAvance;

    private String pathFile;

    public FotosCarneLoteDown() {
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

    public void iniciarProceso(List<Alumno> alumnos) {
        if (this.iniciado) {
            return;
        }
        this.iniciado = true;
        this.total = alumnos.size();
        this.avance = 0;
        this.perAvance = ZERO;
        this.pathFile = "";
        this.errores = new ArrayList();
    }
    
    

    public void finalizarProceso() {
        this.iniciado = false;
        this.matriculaResumens = null;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
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
        return matriculaResumens;
    }

    public void setMatriculaResumens(List<MatriculaResumen> matriculaResumens) {
        this.matriculaResumens = matriculaResumens;
    }

    public List<MsjError> getErrores() {
        return errores;
    }

    public void setErrores(List<MsjError> errores) {
        this.errores = errores;
    }

    public String getPathFile() {
        return pathFile;
    }

    public void setPathFile(String pathFile) {
        this.pathFile = pathFile;
    }

}
