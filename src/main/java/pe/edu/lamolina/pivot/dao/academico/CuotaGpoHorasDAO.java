package pe.edu.lamolina.pivot.dao.academico;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.CuotasGrupoHoras;


public interface CuotaGpoHorasDAO extends EasyDAO<CuotasGrupoHoras> {
    public List<CuotasGrupoHoras> allByDynatable(DynatableFilter filter, CicloAcademico cicloAcademico);
}
