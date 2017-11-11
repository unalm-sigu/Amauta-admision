package pe.edu.lamolina.pivot.controller.academico.horario.grupo;

import java.util.List;
import pe.albatross.zelpers.dynatable.DynatableFilter;
import pe.edu.lamolina.pivot.model.horario.GrupoHoras;

public interface HorarioGrupoService {

    public List<GrupoHoras> allGrupoHoras(DynatableFilter filter);

}
