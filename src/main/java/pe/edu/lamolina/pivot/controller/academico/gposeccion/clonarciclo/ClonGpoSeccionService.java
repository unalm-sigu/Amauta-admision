package pe.edu.lamolina.pivot.controller.academico.gposeccion.clonarciclo;

import java.util.List;
import pe.edu.lamolina.model.academico.AmpliacionVacantes;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Seccion;
import pe.edu.lamolina.model.general.Oficina;
import pe.edu.lamolina.model.general.Persona;
import pe.edu.lamolina.pivot.controller.academico.gposeccion.GpoSeccionResumen;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

public interface ClonGpoSeccionService {

    void clonarCiclo(CicloAcademico cicloOrigen, CicloAcademico cicloDestino, DataSessionPivot ds);

    Long contarGpoSecc(CicloAcademico ciclo);

    GpoSeccionResumen resumenByCiclo(CicloAcademico ciclo);

    void reordenar(CicloAcademico ciclo, DataSessionPivot ds);

    void limpiarCodigo2(CicloAcademico ciclo, DataSessionPivot ds);

    List<AmpliacionVacantes> allAmpliacionVacante(Seccion seccion);

    AmpliacionVacantes findAmpliacionVacante(AmpliacionVacantes ampliacionVacanteForm);

    void saveAmpliacionVacante(AmpliacionVacantes ampliacionVacante, DataSessionPivot ds);

    void deleteAmpliacionVacante(AmpliacionVacantes ampliacionVacante, DataSessionPivot ds);

    List<Oficina> allOficinaByPersona(Persona persona);

    void rechazarAmpliacionVacante(AmpliacionVacantes ampliacionVacante, DataSessionPivot ds);

    void aceptarAmpliacionVacante(AmpliacionVacantes ampliacionVacante, DataSessionPivot ds);

}
