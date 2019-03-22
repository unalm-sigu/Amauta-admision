package pe.edu.lamolina.pivot.controller.general.oficina;

public class ResumenColaborador {

    Long activos;
    Long vacaciones;
    Long retirado;
    Long descanso;
    Long permiso;
    Long despedido;

    public ResumenColaborador(Long activos, Long vacaciones, Long retirado, Long descanso, Long permiso, Long despedido) {
        this.activos = activos;
        this.vacaciones = vacaciones;
        this.retirado = retirado;
        this.descanso = descanso;
        this.permiso = permiso;
        this.despedido = despedido;
    }

    public Long getActivos() {

        return activos != null ? activos : 0l;

    }

    public void setActivos(Long activos) {
        this.activos = activos;
    }

    public Long getVacaciones() {
        return vacaciones != null ? vacaciones : 0l;
    }

    public void setVacaciones(Long vacaciones) {
        this.vacaciones = vacaciones;
    }

    public Long getRetirado() {
        return retirado != null ? retirado : 0l;
    }

    public void setRetirado(Long retirado) {
        this.retirado = retirado;
    }

    public Long getDescanso() {
        return descanso != null ? descanso : 0l;
    }

    public void setDescanso(Long descanso) {
        this.descanso = descanso;
    }

    public Long getPermiso() {
        return permiso != null ? permiso : 0l;
    }

    public void setPermiso(Long permiso) {
        this.permiso = permiso;
    }

    public Long getDespedido() {
        return despedido != null ? despedido : 0l;
    }

    public void setDespedido(Long despedido) {
        this.despedido = despedido;
    }

}
