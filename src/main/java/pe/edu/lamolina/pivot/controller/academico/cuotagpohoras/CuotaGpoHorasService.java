package pe.edu.lamolina.pivot.controller.academico.cuotagpohoras;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.model.academico.AnexoBoletin;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.CuotasGrupoHoras;
import pe.edu.lamolina.model.horario.GrupoHoras;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

public interface CuotaGpoHorasService {

    List<CuotasGrupoHoras> allCuotasGpoHoras(DynatableFilter filter, AnexoBoletin anexoBoletin, CicloAcademico cicloAcademico);

    List<AnexoBoletin> allAnexos();

    List<GrupoHoras> allGrupos();

    void save(List<CuotasGrupoHoras> cuotas, CicloAcademico cicloAcademico, DataSessionPivot ds);

    List<CuotasGrupoHoras> allCuotasByAnexo(AnexoBoletin anexoBoletin, CicloAcademico cicloAcademico);

}
