package pe.edu.lamolina.pivot.controller.general.responsableaula;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.general.Persona;

interface ResponsableAulaService {

    List<Persona> allResponsablesByRaptor(DynatableFilter filter, CicloAcademico cicloAcademico);

}
