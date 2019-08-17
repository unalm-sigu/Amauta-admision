package pe.edu.lamolina.pivot.controller.programacionhorarios.gposeccion.reporte.seccion;

import java.util.List;
import pe.edu.lamolina.model.enums.ModalidadEstudioEnum;

public class SeccionDTO {

    private String tituloReporte;
    private Boolean conHorario;
    private Boolean conAula;

    List<ModalidadEstudioEnum> modalidadesEstudioEnum;

    public Boolean getConHorario() {
        return conHorario;
    }

    public void setConHorario(Boolean conHorario) {
        this.conHorario = conHorario;
    }

    public Boolean getConAula() {
        return conAula;
    }

    public void setConAula(Boolean conAula) {
        this.conAula = conAula;
    }

    public String getTituloReporte() {
        return tituloReporte;
    }

    public void setTituloReporte(String tituloReporte) {
        this.tituloReporte = tituloReporte;
    }

    public List<ModalidadEstudioEnum> getModalidadesEstudioEnum() {
        return modalidadesEstudioEnum;
    }

    public void setModalidadesEstudioEnum(List<ModalidadEstudioEnum> modalidadesEstudioEnum) {
        this.modalidadesEstudioEnum = modalidadesEstudioEnum;
    }

}
