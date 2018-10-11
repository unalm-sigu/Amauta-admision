package pe.edu.lamolina.pivot.controller.academico.gposeccion.clonarciclo;

import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.pivot.controller.academico.gposeccion.GpoSeccionResumen;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

public interface ClonGpoSeccionService {

    void clonarCiclo(CicloAcademico cicloOrigen, CicloAcademico cicloDestino, DataSessionPivot ds);

    Long contarGpoSecc(CicloAcademico ciclo);

    GpoSeccionResumen resumenByCiclo(CicloAcademico ciclo);

    void reordenar(CicloAcademico ciclo, DataSessionPivot ds);

    public void limpiarCodigo2(CicloAcademico ciclo, DataSessionPivot ds);

}
