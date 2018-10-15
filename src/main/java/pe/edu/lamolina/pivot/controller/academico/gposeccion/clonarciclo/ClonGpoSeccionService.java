package pe.edu.lamolina.pivot.controller.academico.gposeccion.clonarciclo;

import java.util.List;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Seccion;
import pe.edu.lamolina.pivot.controller.academico.gposeccion.GpoSeccionResumen;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

public interface ClonGpoSeccionService {

    void clonarCiclo(CicloAcademico cicloOrigen, CicloAcademico cicloDestino, DataSessionPivot ds);

    Long contarGpoSecc(CicloAcademico ciclo);

    GpoSeccionResumen resumenByCiclo(CicloAcademico ciclo);

    void reordenar(CicloAcademico ciclo, DataSessionPivot ds);

    void limpiarCodigo2(CicloAcademico ciclo, DataSessionPivot ds);

    List<Alumno> allAlumnoBySeccion(Seccion seccion);

    void trasladar(Fusion trasladoForm);

}
