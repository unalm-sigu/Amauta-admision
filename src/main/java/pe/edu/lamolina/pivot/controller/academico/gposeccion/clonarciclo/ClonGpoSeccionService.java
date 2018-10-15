package pe.edu.lamolina.pivot.controller.academico.gposeccion.clonarciclo;

import java.util.List;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.AmpliacionVacante;
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

    List<AmpliacionVacante> allAmpliacionVacante(Seccion seccion);

    AmpliacionVacante findAmpliacionVacante(AmpliacionVacante ampliacionVacanteForm);

    void saveAmpliacionVacante(AmpliacionVacante ampliacionVacante, DataSessionPivot ds);

    void updateAmpliacionVacante(AmpliacionVacante ampliacionVacante, DataSessionPivot ds);

    void deleteAmpliacionVacante(AmpliacionVacante ampliacionVacante);

    List<Oficina> allOficinaByPersona(Persona persona);

    void rechazarAmpliacionVacante(AmpliacionVacante ampliacionVacante, DataSessionPivot ds);

    void aceptarAmpliacionVacante(AmpliacionVacante ampliacionVacante, DataSessionPivot ds);

    List<Alumno> allAlumnoBySeccion(Seccion seccion);

    void trasladar(Fusion trasladoForm);

}
