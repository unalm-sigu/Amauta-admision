package pe.edu.lamolina.pivot.controller.academico.horario.grupo;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.pivot.model.horario.GrupoHoras;

public interface GrupoHorasService {

    public List<GrupoHoras> allGrupoHoras(DynatableFilter filter);

}
