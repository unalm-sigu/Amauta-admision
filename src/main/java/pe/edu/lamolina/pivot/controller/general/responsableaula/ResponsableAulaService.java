package pe.edu.lamolina.pivot.controller.general.responsableaula;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.general.Aula;
import pe.edu.lamolina.model.general.Persona;
import pe.edu.lamolina.model.general.ResponsableAula;
import pe.edu.lamolina.model.general.TurnoAtencionAula;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

interface ResponsableAulaService {

    List<ResponsableAula> allResponsablesByRaptor(DynatableFilter filter, CicloAcademico cicloAcademico);

    List<Persona> allPersonasByName(String nombre);

    List<Aula> allAulasByName(String nombre);

    List<TurnoAtencionAula> allTurnoAtenconAula();

    void saveResponsableAula(ResponsableAula responsableAula, DataSessionPivot ds);

    ResponsableAula findResponsableAula(ResponsableAula responsableAula, DataSessionPivot ds);

}
