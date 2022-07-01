/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.edu.lamolina.amauta.controller.reporte.dto.plancurricular;

/**
 *
 * @author Carlos Buitron
 */
public class PlanEstudiosCursoRegularDTO {

    private Long idPlanCurricular;
    private Long idCurriculaCurso;
    private String facultad;
    private String especialidad;
    private String nivel;
    private String codigoCurso;
    private String nombreCurso;
    private String tipoCurso;
    private Long horasTeoria;
    private Long horasPractica;
    private Long creditos;
    private String cursoRequisito;
    private String creditosRequisito;
    private Long creditosOtros;
    private Long year;

    public PlanEstudiosCursoRegularDTO() {

    }

    public PlanEstudiosCursoRegularDTO(Long idPlanCurricular, Long idCurriculaCurso, String facultad, String especialidad, String nivel, String codigoCurso, String nombreCurso, String tipoCurso, Long horasTeoria, Long horasPractica, Long creditos, String cursoRequisito, String creditosRequisito, Long creditosOtros, Long year) {
        this.idPlanCurricular = idPlanCurricular;
        this.idCurriculaCurso = idCurriculaCurso;
        this.facultad = facultad;
        this.especialidad = especialidad;
        this.nivel = nivel;
        this.codigoCurso = codigoCurso;
        this.nombreCurso = nombreCurso;
        this.tipoCurso = tipoCurso;
        this.horasTeoria = horasTeoria;
        this.horasPractica = horasPractica;
        this.creditos = creditos;
        this.cursoRequisito = cursoRequisito;
        this.creditosRequisito = creditosRequisito;
        this.creditosOtros = creditosOtros;
        this.year = year;
    }

    public Long getIdPlanCurricular() {
        return idPlanCurricular;
    }

    public void setIdPlanCurricular(Long idPlanCurricular) {
        this.idPlanCurricular = idPlanCurricular;
    }

    public Long getIdCurriculaCurso() {
        return idCurriculaCurso;
    }

    public void setIdCurriculaCurso(Long idCurriculaCurso) {
        this.idCurriculaCurso = idCurriculaCurso;
    }

    public String getFacultad() {
        return facultad;
    }

    public void setFacultad(String facultad) {
        this.facultad = facultad;
    }

    public String getEspecialidad() {
        return especialidad;
    }

    public void setEspecialidad(String especialidad) {
        this.especialidad = especialidad;
    }

    public String getNivel() {
        return nivel;
    }

    public void setNivel(String nivel) {
        this.nivel = nivel;
    }

    public String getCodigoCurso() {
        return codigoCurso;
    }

    public void setCodigoCurso(String codigoCurso) {
        this.codigoCurso = codigoCurso;
    }

    public String getNombreCurso() {
        return nombreCurso;
    }

    public void setNombreCurso(String nombreCurso) {
        this.nombreCurso = nombreCurso;
    }

    public String getTipoCurso() {
        return tipoCurso;
    }

    public void setTipoCurso(String tipoCurso) {
        this.tipoCurso = tipoCurso;
    }

    public Long getHorasTeoria() {
        return horasTeoria;
    }

    public void setHorasTeoria(Long horasTeoria) {
        this.horasTeoria = horasTeoria;
    }

    public Long getHorasPractica() {
        return horasPractica;
    }

    public void setHorasPractica(Long horasPractica) {
        this.horasPractica = horasPractica;
    }

    public Long getCreditos() {
        return creditos;
    }

    public void setCreditos(Long creditos) {
        this.creditos = creditos;
    }

    public String getCursoRequisito() {
        return cursoRequisito;
    }

    public void setCursoRequisito(String cursoRequisito) {
        this.cursoRequisito = cursoRequisito;
    }

    public String getCreditosRequisito() {
        return creditosRequisito;
    }

    public void setCreditosRequisito(String creditosRequisito) {
        this.creditosRequisito = creditosRequisito;
    }

    public Long getCreditosOtros() {
        return creditosOtros;
    }

    public void setCreditosOtros(Long creditosOtros) {
        this.creditosOtros = creditosOtros;
    }

    public Long getYear() {
        return year;
    }

    public void setYear(Long year) {
        this.year = year;
    }

}
