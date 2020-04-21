package pe.edu.lamolina.amauta.controller.rolexamen.components;

import pe.edu.lamolina.model.enums.TipoGrupoRolExamenesEnum;
import java.io.Serializable;

public class CambiarAula implements Serializable {

    private String tipoGrupoRolExamenOrigen;
    private Long idSeccionRolExamenesOrigen;

    private Long idAulaDestino;

    public CambiarAula() {
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

    public TipoGrupoRolExamenesEnum getTipoGrupoRolExamenOrigenEnum() {
        if (this.getTipoGrupoRolExamenOrigen() == null) {
            return null;
        }
        return TipoGrupoRolExamenesEnum.valueOf(this.getTipoGrupoRolExamenOrigen());
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

    public Long getIdAulaDestino() {
        return idAulaDestino;
    }

    public void setIdAulaDestino(Long idAulaDestino) {
        this.idAulaDestino = idAulaDestino;
    }

}
