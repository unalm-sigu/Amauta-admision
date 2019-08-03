package pe.edu.lamolina.pivot.controller.academico.alumno;

import java.util.List;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.CursoConvalidado;
import pe.edu.lamolina.model.tramite.TramiteTraslado;

public class TrasladoBean {

    List<CursoConvalidado> listCursoConvalidado;
    Integer total;
    Alumno alumno;
    TramiteTraslado tramiteTraslado;

    public TrasladoBean() {
    }

    public List<CursoConvalidado> getListCursoConvalidado() {
        return listCursoConvalidado;
    }

    public void setListCursoConvalidado(List<CursoConvalidado> listCursoConvalidado) {
        this.listCursoConvalidado = listCursoConvalidado;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public Alumno getAlumno() {
        return alumno;
    }

    public void setAlumno(Alumno alumno) {
        this.alumno = alumno;
    }

    public TramiteTraslado getTramiteTraslado() {
        return tramiteTraslado;
    }

    public void setTramiteTraslado(TramiteTraslado tramiteTraslado) {
        this.tramiteTraslado = tramiteTraslado;
    }
}
