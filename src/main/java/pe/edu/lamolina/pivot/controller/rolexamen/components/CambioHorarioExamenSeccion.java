package pe.edu.lamolina.pivot.controller.rolexamen.components;

import java.io.Serializable;

public class CambioHorarioExamenSeccion implements Serializable {

    private String tipoGrupoRolExamenOrigen;
    private Long idSeccionRolExamenesOrigen;

    private String tipoGrupoRolExamenDestino;
    private Long idTipoGrupoExamenDestino;

    public CambioHorarioExamenSeccion() {
    }

    public String getTipoGrupoRolExamenOrigen() {
        return tipoGrupoRolExamenOrigen;
    }

    public void setTipoGrupoRolExamenOrigen(String tipoGrupoRolExamenOrigen) {
        this.tipoGrupoRolExamenOrigen = tipoGrupoRolExamenOrigen;
    }

    public Long getIdSeccionRolExamenesOrigen() {
        return idSeccionRolExamenesOrigen;
    }

    public void setIdSeccionRolExamenesOrigen(Long idSeccionRolExamenesOrigen) {
        this.idSeccionRolExamenesOrigen = idSeccionRolExamenesOrigen;
    }

    public String getTipoGrupoRolExamenDestino() {
        return tipoGrupoRolExamenDestino;
    }

    public void setTipoGrupoRolExamenDestino(String tipoGrupoRolExamenDestino) {
        this.tipoGrupoRolExamenDestino = tipoGrupoRolExamenDestino;
    }

    public Long getIdTipoGrupoExamenDestino() {
        return idTipoGrupoExamenDestino;
    }

    public void setIdTipoGrupoExamenDestino(Long idTipoGrupoExamenDestino) {
        this.idTipoGrupoExamenDestino = idTipoGrupoExamenDestino;
    }

    public TipoGrupoRolExamenesEnum getTipoGrupoRolExamenOrigenEnum() {
        if (this.getTipoGrupoRolExamenOrigen() == null) {
            return null;
        }
        return TipoGrupoRolExamenesEnum.valueOf(this.getTipoGrupoRolExamenOrigen());
    }

    public TipoGrupoRolExamenesEnum getTipoGrupoRolExamenDestinoEnum() {
        if (this.getTipoGrupoRolExamenDestino() == null) {
            return null;
        }
        return TipoGrupoRolExamenesEnum.valueOf(this.getTipoGrupoRolExamenDestino());
    }

    public boolean isTipoGrupoOrigenMasivo() {
        if (this.tipoGrupoRolExamenOrigen == null) {
            return false;
        }
        return TipoGrupoRolExamenesEnum.CUR_MAS.equals(this.getTipoGrupoRolExamenOrigenEnum());
    }

    public boolean isTipoGrupoOrigenRegular() {
        if (this.tipoGrupoRolExamenOrigen == null) {
            return false;
        }
        return TipoGrupoRolExamenesEnum.GRU_REG.equals(this.getTipoGrupoRolExamenOrigenEnum());
    }

    public boolean isTipoGrupoOrigenEspecial() {
        if (this.tipoGrupoRolExamenOrigen == null) {
            return false;
        }
        return TipoGrupoRolExamenesEnum.GRU_ESP.equals(this.getTipoGrupoRolExamenOrigenEnum());
    }

    public boolean isTipoGrupoDestinoMasivo() {
        if (this.tipoGrupoRolExamenDestino == null) {
            return false;
        }
        return TipoGrupoRolExamenesEnum.CUR_MAS.equals(this.getTipoGrupoRolExamenDestinoEnum());
    }

    public boolean isTipoGrupoDestinoRegular() {
        if (this.tipoGrupoRolExamenDestino == null) {
            return false;
        }
        return TipoGrupoRolExamenesEnum.GRU_REG.equals(this.getTipoGrupoRolExamenDestinoEnum());
    }

    public boolean isTipoGrupoDestinoEspecial() {
        if (this.tipoGrupoRolExamenDestino == null) {
            return false;
        }
        return TipoGrupoRolExamenesEnum.GRU_ESP.equals(this.getTipoGrupoRolExamenDestinoEnum());
    }

}
