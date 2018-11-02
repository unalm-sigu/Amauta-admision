package pe.edu.lamolina.pivot.controller.academico.gposeccion.clonarciclo;

import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.pivot.controller.academico.gposeccion.GpoSeccionResumen;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

public interface ClonarCicloService {

    void clonarCiclo(CicloAcademico cicloOrigen, CicloAcademico cicloDestino, DataSessionPivot ds);

    //Long contarGpoSecc(CicloAcademico ciclo);

    GpoSeccionResumen resumenByCiclo(CicloAcademico ciclo);

    void reordenar(CicloAcademico ciclo, DataSessionPivot ds);

    void limpiarCodigo2(CicloAcademico ciclo, DataSessionPivot ds);

    void limpiarCiclo(CicloAcademico ciclo);

    CicloAcademico findCiclo(CicloAcademico ciclo);

    void cerrarClonacion(CicloAcademico cicloBD);

    void cerrarOrden(CicloAcademico cicloBD);

}
