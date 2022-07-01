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
public class PlanEstudiosCursoElectivoDTO {
    
    private Long idPlanCurricular;
    private Long idCursoOpcionalCurricula;
    private String tipo;
    private String codigoTipo;
    private String codigoCurso;
    private String codigoAnteriorCurso;
    private String nombreCurso;    
    private Long horasTeoria;
    private Long horasPractica;
    private Long creditos;
    private String tipoCurso;
    private Long creditosCursoOpcionalCurricula;
    private String cursosEquivalente;
    private Long creditosRequisito;
    private String cursosRequisito;
    private String cursosPreRequisito;
    private Long year;

    public PlanEstudiosCursoElectivoDTO(){
        
    }

    public PlanEstudiosCursoElectivoDTO(Long idPlanCurricular, Long idCursoOpcionalCurricula, String tipo, String codigoTipo, String codigoCurso, String codigoAnteriorCurso, String nombreCurso, Long horasTeoria, Long horasPractica, Long creditos, String tipoCurso, Long creditosCursoOpcionalCurricula, String cursosEquivalente, String cursoRequisito, Long creditosRequisito, String cursosRequisito, String cursosPreRequisito, Long year) {
        this.idPlanCurricular = idPlanCurricular;
        this.idCursoOpcionalCurricula = idCursoOpcionalCurricula;
        this.tipo = tipo;
        this.codigoTipo = codigoTipo;
        this.codigoCurso = codigoCurso;
        this.codigoAnteriorCurso = codigoAnteriorCurso;
        this.nombreCurso = nombreCurso;        
        this.horasTeoria = horasTeoria;
        this.horasPractica = horasPractica;
        this.creditos = creditos;
        this.tipoCurso = tipoCurso;        
        this.creditosCursoOpcionalCurricula = creditosCursoOpcionalCurricula;
        this.cursosEquivalente = cursosEquivalente;
        this.creditosRequisito = creditosRequisito;
        this.cursosRequisito = cursosRequisito;
        this.cursosPreRequisito = cursosPreRequisito;
        this.year = year;
    }

    public Long getIdPlanCurricular() {
        return idPlanCurricular;
    }

    public void setIdPlanCurricular(Long idPlanCurricular) {
        this.idPlanCurricular = idPlanCurricular;
    }

    public Long getIdCursoOpcionalCurricula() {
        return idCursoOpcionalCurricula;
    }

    public void setIdCursoOpcionalCurricula(Long idCursoOpcionalCurricula) {
        this.idCursoOpcionalCurricula = idCursoOpcionalCurricula;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getCodigoTipo() {
        return codigoTipo;
    }

    public void setCodigoTipo(String codigoTipo) {
        this.codigoTipo = codigoTipo;
    }

    public String getCodigoCurso() {
        return codigoCurso;
    }

    public void setCodigoCurso(String codigoCurso) {
        this.codigoCurso = codigoCurso;
    }

    public String getCodigoAnteriorCurso() {
        return codigoAnteriorCurso;
    }

    public void setCodigoAnteriorCurso(String codigoAnteriorCurso) {
        this.codigoAnteriorCurso = codigoAnteriorCurso;
    }

    public String getNombreCurso() {
        return nombreCurso;
    }

    public void setNombreCurso(String nombreCurso) {
        this.nombreCurso = nombreCurso;
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

    public String getTipoCurso() {
        return tipoCurso;
    }

    public void setTipoCurso(String tipoCurso) {
        this.tipoCurso = tipoCurso;
    }

    public Long getCreditosCursoOpcionalCurricula() {
        return creditosCursoOpcionalCurricula;
    }

    public void setCreditosCursoOpcionalCurricula(Long creditosCursoOpcionalCurricula) {
        this.creditosCursoOpcionalCurricula = creditosCursoOpcionalCurricula;
    }

    public String getCursosEquivalente() {
        return cursosEquivalente;
    }

    public void setCursosEquivalente(String cursosEquivalente) {
        this.cursosEquivalente = cursosEquivalente;
    }

    public Long getCreditosRequisito() {
        return creditosRequisito;
    }

    public void setCreditosRequisito(Long creditosRequisito) {
        this.creditosRequisito = creditosRequisito;
    }

    public String getCursosRequisito() {
        return cursosRequisito;
    }

    public void setCursosRequisito(String cursosRequisito) {
        this.cursosRequisito = cursosRequisito;
    }

    public String getCursosPreRequisito() {
        return cursosPreRequisito;
    }

    public void setCursosPreRequisito(String cursosPreRequisito) {
        this.cursosPreRequisito = cursosPreRequisito;
    }

    public Long getYear() {
        return year;
    }

    public void setYear(Long year) {
        this.year = year;
    }   
    
}
