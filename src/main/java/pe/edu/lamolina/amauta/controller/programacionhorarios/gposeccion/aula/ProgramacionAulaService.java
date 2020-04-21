package pe.edu.lamolina.amauta.controller.programacionhorarios.gposeccion.aula;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.model.general.Aula;

public interface ProgramacionAulaService {

    List<Aula> allAulasSinHorarioDyna(DynatableFilter filter);

}
