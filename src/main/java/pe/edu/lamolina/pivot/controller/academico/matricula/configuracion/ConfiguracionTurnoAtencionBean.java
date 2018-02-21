/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.edu.lamolina.pivot.controller.academico.matricula.configuracion;

import pe.edu.lamolina.model.academico.EventoCicloAcademico;

/**
 *
 * @author AlbatrossCloud
 */
public class ConfiguracionTurnoAtencionBean {

    private Long id;

    private String fechaInicio;

    private String fechaFin;

    private Integer turnosDia;

    private String tipo;

    private Integer duracion;

    private Integer espera;

    private Integer alumnos;

    private String horaInicio;

    private EventoCicloAcademico eventoCicloAcademico;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(String fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public String getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(String fechaFin) {
        this.fechaFin = fechaFin;
    }

    public Integer getTurnosDia() {
        return turnosDia;
    }

    public void setTurnosDia(Integer turnosDia) {
        this.turnosDia = turnosDia;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public Integer getDuracion() {
        return duracion;
    }

    public void setDuracion(Integer duracion) {
        this.duracion = duracion;
    }

    public Integer getEspera() {
        return espera;
    }

    public void setEspera(Integer espera) {
        this.espera = espera;
    }

    public Integer getAlumnos() {
        return alumnos;
    }

    public void setAlumnos(Integer alumnos) {
        this.alumnos = alumnos;
    }

    public String getHoraInicio() {
        return horaInicio;
    }

    public void setHoraInicio(String horaInicio) {
        this.horaInicio = horaInicio;
    }

    public EventoCicloAcademico getEventoCicloAcademico() {
        return eventoCicloAcademico;
    }

    public void setEventoCicloAcademico(EventoCicloAcademico eventoCicloAcademico) {
        this.eventoCicloAcademico = eventoCicloAcademico;
    }

}
