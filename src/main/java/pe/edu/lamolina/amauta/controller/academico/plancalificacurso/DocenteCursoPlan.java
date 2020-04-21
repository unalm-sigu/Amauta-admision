package pe.edu.lamolina.amauta.controller.academico.plancalificacurso;

public class DocenteCursoPlan {

    private Long idCurso;
    private Long idDocente;
    private Long idPlanCalifica;
    private Long cantidadPlanes;

    public DocenteCursoPlan(Long idDocente, Long idCurso, Long cantidadGrupos, Long idPlanCalifica) {
        this.idCurso = idCurso;
        this.idDocente = idDocente;
        this.idPlanCalifica = idPlanCalifica;
        this.cantidadPlanes = cantidadGrupos;
    }

    public Long getIdCurso() {
        return idCurso;
    }

    public void setIdCurso(Long idCurso) {
        this.idCurso = idCurso;
    }

    public Long getIdDocente() {
        return idDocente;
    }

    public void setIdDocente(Long idDocente) {
        this.idDocente = idDocente;
    }

    public Long getIdPlanCalifica() {
        return idPlanCalifica;
    }

    public void setIdPlanCalifica(Long idPlanCalifica) {
        this.idPlanCalifica = idPlanCalifica;
    }

    public Long getCantidadPlanes() {
        return cantidadPlanes;
    }

    public void setCantidadPlanes(Long cantidadPlanes) {
        this.cantidadPlanes = cantidadPlanes;
    }

}
