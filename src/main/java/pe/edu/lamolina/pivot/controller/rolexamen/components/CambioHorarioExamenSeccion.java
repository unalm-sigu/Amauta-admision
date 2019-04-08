package pe.edu.lamolina.pivot.controller.rolexamen.components;

import pe.edu.lamolina.model.enums.TipoGrupoRolExamenesEnum;
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

    public boolean isTipoGrupMasivooOrigen() {
        if (this.tipoGrupoRolExamenOrigen == null) {
            return false;
        }
        return TipoGrupoRolExamenesEnum.CUR_MAS.equals(this.getTipoGrupoRolExamenOrigenEnum());
    }

    public boolean isTipoGrupoRegularOrigen() {
        if (this.tipoGrupoRolExamenOrigen == null) {
            return false;
        }
        return TipoGrupoRolExamenesEnum.GRU_REG.equals(this.getTipoGrupoRolExamenOrigenEnum());
    }

    public boolean isTipoGrupoEspecialOrigen() {
        if (this.tipoGrupoRolExamenOrigen == null) {
            return false;
        }
        return TipoGrupoRolExamenesEnum.GRU_ESP.equals(this.getTipoGrupoRolExamenOrigenEnum());
    }

    public boolean isTipoGrupoMasivoDestino() {
        if (this.tipoGrupoRolExamenDestino == null) {
            return false;
        }
        return TipoGrupoRolExamenesEnum.CUR_MAS.equals(this.getTipoGrupoRolExamenDestinoEnum());
    }

    public boolean isTipoGrupRegularoDestino() {
        if (this.tipoGrupoRolExamenDestino == null) {
            return false;
        }
        return TipoGrupoRolExamenesEnum.GRU_REG.equals(this.getTipoGrupoRolExamenDestinoEnum());
    }

    public boolean isTipoGrupoEspecialDestino() {
        if (this.tipoGrupoRolExamenDestino == null) {
            return false;
        }
        return TipoGrupoRolExamenesEnum.GRU_ESP.equals(this.getTipoGrupoRolExamenDestinoEnum());
    }

}
