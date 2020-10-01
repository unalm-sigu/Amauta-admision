package pe.edu.lamolina.amauta.controller.academico.cursoPropedeutico;

import java.math.BigDecimal;
import java.util.List;
import pe.edu.lamolina.model.academico.MatriculaResumen;
import pe.edu.lamolina.model.academico.Seccion;

public class AlumnoCursoPropedeuticoBean {

    Seccion seccion;
    BigDecimal precio;
    List<MatriculaResumen> matriculaResumens;

    public Seccion getSeccion() {
        return seccion;
    }

    public void setSeccion(Seccion seccion) {
        this.seccion = seccion;
    }

    public List<MatriculaResumen> getMatriculaResumens() {
        return matriculaResumens;
    }

    public void setMatriculaResumens(List<MatriculaResumen> matriculaResumens) {
        this.matriculaResumens = matriculaResumens;
    }

    public BigDecimal getPrecio() {
        return precio;
    }

    public void setPrecio(BigDecimal precio) {
        this.precio = precio;
    }

}
