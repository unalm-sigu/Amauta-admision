package pe.edu.lamolina.pivot.controller.horariocachimbo.carrera;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.model.academico.CarreraCachimbos;
import pe.edu.lamolina.model.academico.CicloAcademico;

public interface HorarioCachimboCarreraService {

    List<CarreraCachimbos> allCarreraCachimbos(DynatableFilter filter, CicloAcademico cicloAcademico);

}
