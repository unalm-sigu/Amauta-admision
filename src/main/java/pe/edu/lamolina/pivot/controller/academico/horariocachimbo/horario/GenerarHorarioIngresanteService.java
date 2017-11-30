package pe.edu.lamolina.pivot.controller.academico.horariocachimbo.horario;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.pivot.model.academico.CicloAcademico;
import pe.edu.lamolina.pivot.model.horario.HorarioCachimbos;

public interface GenerarHorarioIngresanteService {

    public List<HorarioCachimbos> allHorarioCachimbos(DynatableFilter filter, CicloAcademico cicloAcademico);

    public void delete(HorarioCachimbos horarioCachimbos);

}
