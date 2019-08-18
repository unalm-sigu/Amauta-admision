package pe.edu.lamolina.pivot.controller.academico.cuotadpto;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.CuotasGrupoHoras;
import pe.edu.lamolina.model.horario.GrupoHoras;

public interface CuotaDptoService {

    List<GrupoHoras> allGrupos();

    List<CuotasGrupoHoras> allCuotasGpoHoras(DynatableFilter filter, GrupoHoras grupoHoras, CicloAcademico cicloAcademico);

    GrupoHoras findGrupo(GrupoHoras grupoHoras);

    String grupos(CuotasGrupoHoras cuotasGrupoHoras, String tipoSeccion);

}
