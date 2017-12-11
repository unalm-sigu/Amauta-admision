package pe.edu.lamolina.pivot.controller.academico.horariocachimbo.carrera;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.pivot.model.academico.CarreraCachimbos;
import pe.edu.lamolina.pivot.model.academico.CicloAcademico;

public interface HorarioCarreraService {

    public List<CarreraCachimbos> allCarreraCachimbos(DynatableFilter filter, CicloAcademico cicloAcademico);

}
