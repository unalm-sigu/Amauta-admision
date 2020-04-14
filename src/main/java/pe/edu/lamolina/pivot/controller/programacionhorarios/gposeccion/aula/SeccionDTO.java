package pe.edu.lamolina.pivot.controller.programacionhorarios.gposeccion.aula;

import java.util.List;
import pe.edu.lamolina.model.academico.AnexoBoletin;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Seccion;
import pe.edu.lamolina.model.enums.ModalidadEstudioEnum;
import pe.edu.lamolina.model.general.Dia;

public class SeccionDTO {

    private String tituloReporte;
    private Boolean conHorario;
    private Boolean conAula;

    List<Dia> dias;

    private List<Seccion> secciones;
    private List<AnexoBoletin> anexosBoletin;
    //deprecated
    private List<ModalidadEstudioEnum> modalidadesEstudioEnum;
    private CicloAcademico cicloAcademico;

    private List<ModalidadEstudioEnum> modalidadesEstudioAluEnum;
    private List<ModalidadEstudioEnum> modalidadesEstudioCurEnum;

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

    public List<Seccion> getSecciones() {
        return secciones;
    }

    public void setSecciones(List<Seccion> secciones) {
        this.secciones = secciones;
    }

    public List<Dia> getDias() {
        return dias;
    }

    public void setDias(List<Dia> dias) {
        this.dias = dias;
    }

    public CicloAcademico getCicloAcademico() {
        return cicloAcademico;
    }

    public void setCicloAcademico(CicloAcademico cicloAcademico) {
        this.cicloAcademico = cicloAcademico;
    }

    public List<ModalidadEstudioEnum> getModalidadesEstudioAluEnum() {
        return modalidadesEstudioAluEnum;
    }

    public void setModalidadesEstudioAluEnum(List<ModalidadEstudioEnum> modalidadesEstudioAluEnum) {
        this.modalidadesEstudioAluEnum = modalidadesEstudioAluEnum;
    }

    public List<ModalidadEstudioEnum> getModalidadesEstudioCurEnum() {
        return modalidadesEstudioCurEnum;
    }

    public void setModalidadesEstudioCurEnum(List<ModalidadEstudioEnum> modalidadesEstudioCurEnum) {
        this.modalidadesEstudioCurEnum = modalidadesEstudioCurEnum;
    }

    public List<AnexoBoletin> getAnexosBoletin() {
        return anexosBoletin;
    }

    public void setAnexosBoletin(List<AnexoBoletin> anexosBoletin) {
        this.anexosBoletin = anexosBoletin;
    }

}
