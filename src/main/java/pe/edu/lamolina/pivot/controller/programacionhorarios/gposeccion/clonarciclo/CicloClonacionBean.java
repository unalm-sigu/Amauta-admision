package pe.edu.lamolina.pivot.controller.programacionhorarios.gposeccion.clonarciclo;

import pe.edu.lamolina.model.academico.CicloAcademico;

public class CicloClonacionBean {

    private CicloAcademico cicloOrigen;
    private CicloAcademico cicloDestino;
    private Boolean copiarAulasOera;
    private Boolean copiarAulasDptos;
    private Boolean copiarAulasPosgrado;

    public CicloAcademico getCicloOrigen() {
        return cicloOrigen;
    }

    public void setCicloOrigen(CicloAcademico cicloOrigen) {
        this.cicloOrigen = cicloOrigen;
    }

    public Boolean getCopiarAulasOera() {
        return copiarAulasOera;
    }

    public void setCopiarAulasOera(Boolean copiarAulasOera) {
        this.copiarAulasOera = copiarAulasOera;
    }

    public Boolean getCopiarAulasDptos() {
        return copiarAulasDptos;
    }

    public void setCopiarAulasDptos(Boolean copiarAulasDptos) {
        this.copiarAulasDptos = copiarAulasDptos;
    }

    public Boolean getCopiarAulasPosgrado() {
        return copiarAulasPosgrado;
    }

    public void setCopiarAulasPosgrado(Boolean copiarAulasPosgrado) {
        this.copiarAulasPosgrado = copiarAulasPosgrado;
    }

    public CicloAcademico getCicloDestino() {
        return cicloDestino;
    }

    public void setCicloDestino(CicloAcademico cicloDestino) {
        this.cicloDestino = cicloDestino;
    }

}
