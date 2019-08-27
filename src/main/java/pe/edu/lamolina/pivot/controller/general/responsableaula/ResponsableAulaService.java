package pe.edu.lamolina.pivot.controller.general.responsableaula;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.general.Aula;
import pe.edu.lamolina.model.general.Persona;
import pe.edu.lamolina.model.general.TurnoAtencionAula;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

interface ResponsableAulaService {

    List<Persona> allResponsablesByRaptor(DynatableFilter filter, CicloAcademico cicloAcademico);

    List<Persona> allPersonasByName(String nombre);

    List<Aula> allAulasByName(String nombre);

    List<TurnoAtencionAula> allTurnoAtenconAula();

    void saveResponsableAula(Persona personaResponsable, DataSessionPivot ds);

    Persona findResponsableAula(Persona personaResponsable, DataSessionPivot ds);

}
