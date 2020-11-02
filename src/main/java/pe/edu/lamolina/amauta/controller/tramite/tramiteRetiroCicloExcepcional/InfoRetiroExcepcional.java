package pe.edu.lamolina.amauta.controller.tramite.tramiteRetiroCicloExcepcional;

import java.math.BigDecimal;

public class InfoRetiroExcepcional {

    BigDecimal pps;
    Integer cca;
    Integer caa;
    BigDecimal ppa;
    String situacion;
    BigDecimal relacionEficiencia;
    Integer vecesSuspendido;
    Integer vecesObservado;
    Integer vecesNormal;
    Integer ciclosRegularesApro;
    Integer ciclosRegularesDesap;
    Integer ciclosVeranoApro;
    Integer ciclosVeranoDesap;

    public BigDecimal getPps() {
        return pps;
    }

    public void setPps(BigDecimal pps) {
        this.pps = pps;
    }

    public Integer getCca() {
        return cca;
    }

    public void setCca(Integer cca) {
        this.cca = cca;
    }

    public Integer getCaa() {
        return caa;
    }

    public void setCaa(Integer caa) {
        this.caa = caa;
    }

    public BigDecimal getPpa() {
        return ppa;
    }

    public void setPpa(BigDecimal ppa) {
        this.ppa = ppa;
    }

    public String getSituacion() {
        return situacion;
    }

    public void setSituacion(String situacion) {
        this.situacion = situacion;
    }

    public BigDecimal getRelacionEficiencia() {
        return relacionEficiencia;
    }

    public void setRelacionEficiencia(BigDecimal relacionEficiencia) {
        this.relacionEficiencia = relacionEficiencia;
    }

    public Integer getVecesSuspendido() {
        if (vecesSuspendido == null) {
            return 0;
        }
        return vecesSuspendido;
    }

    public void setVecesSuspendido(Integer vecesSuspendido) {
        this.vecesSuspendido = vecesSuspendido;
    }

    public Integer getVecesObservado() {
        if (vecesObservado == null) {
            return 0;
        }
        return vecesObservado;
    }

    public void setVecesObservado(Integer vecesObservado) {

        this.vecesObservado = vecesObservado;
    }

    public Integer getVecesNormal() {
        if (vecesNormal == null) {
            return 0;
        }
        return vecesNormal;
    }

    public void setVecesNormal(Integer vecesNormal) {
        this.vecesNormal = vecesNormal;
    }

    public Integer getCiclosRegularesApro() {
        if (ciclosRegularesApro == null) {
            return 0;
        }
        return ciclosRegularesApro;
    }

    public void setCiclosRegularesApro(Integer ciclosRegularesApro) {
        this.ciclosRegularesApro = ciclosRegularesApro;
    }

    public Integer getCiclosRegularesDesap() {
        if (ciclosRegularesDesap == null) {
            return 0;
        }
        return ciclosRegularesDesap;
    }

    public void setCiclosRegularesDesap(Integer ciclosRegularesDesap) {
        this.ciclosRegularesDesap = ciclosRegularesDesap;
    }

    public Integer getCiclosVeranoApro() {
        if (ciclosVeranoApro == null) {
            return 0;
        }
        return ciclosVeranoApro;
    }

    public void setCiclosVeranoApro(Integer ciclosVeranoApro) {
        this.ciclosVeranoApro = ciclosVeranoApro;
    }

    public Integer getCiclosVeranoDesap() {
        if (ciclosVeranoDesap == null) {
            return 0;
        }
        return ciclosVeranoDesap;
    }

    public void setCiclosVeranoDesap(Integer ciclosVeranoDesap) {
        this.ciclosVeranoDesap = ciclosVeranoDesap;
    }

}
