package pe.edu.lamolina.pivot.controller.programacionhorarios.gposeccion.reporte.seccion;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.model.general.Aula;

public interface SeccionService {

    List<Aula> allAulasSinHorarioDyna(DynatableFilter filter);

}
