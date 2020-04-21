package pe.edu.lamolina.amauta.controller.programacionhorarios.gposeccion.ampliavacantes;

import java.util.List;
import pe.edu.lamolina.model.academico.AmpliacionVacantes;
import pe.edu.lamolina.model.academico.Seccion;
import pe.edu.lamolina.model.general.Oficina;
import pe.edu.lamolina.model.general.Persona;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;

public interface AmpliaVacantesService {

    List<AmpliacionVacantes> allAmpliacionVacante(Seccion seccion);

    AmpliacionVacantes findAmpliacionVacante(AmpliacionVacantes ampliacionVacanteForm);

    void saveAmpliacionVacante(AmpliacionVacantes ampliacionVacante, DataSessionPivot ds);

    void deleteAmpliacionVacante(AmpliacionVacantes ampliacionVacante, DataSessionPivot ds);

    List<Oficina> allOficinaByPersona(Persona persona);

    void rechazarAmpliacionVacante(AmpliacionVacantes ampliacionVacante, DataSessionPivot ds);

    void aceptarAmpliacionVacante(AmpliacionVacantes ampliacionVacante, DataSessionPivot ds);

}
