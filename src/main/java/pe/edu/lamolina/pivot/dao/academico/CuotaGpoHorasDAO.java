package pe.edu.lamolina.pivot.dao.academico;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.academico.AnexoBoletin;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.CuotasGrupoHoras;
import pe.edu.lamolina.model.horario.GrupoHoras;
import pe.edu.lamolina.pivot.controller.academico.cuotadpto.AnexoCuotaUtilizadaBean;
import pe.edu.lamolina.pivot.controller.academico.cuotagpohoras.LetraCuotaUtilizadaBean;

public interface CuotaGpoHorasDAO extends EasyDAO<CuotasGrupoHoras> {

    List<CuotasGrupoHoras> allByDynatable(DynatableFilter filter, AnexoBoletin anexoBoletin, CicloAcademico cicloAcademico);

    List<CuotasGrupoHoras> allCuotasByAnexo(AnexoBoletin anexoBoletin, CicloAcademico cicloAcademico);

    List<LetraCuotaUtilizadaBean> allByAnexoBoletinAcademico(AnexoBoletin anexoBoletin, CicloAcademico cicloAcademico);

    List<LetraCuotaUtilizadaBean> allByAnexoBoletinHoras(AnexoBoletin anexoBoletin, CicloAcademico cicloAcademico);

    List<LetraCuotaUtilizadaBean> allByAnexoBoletinGrupo(AnexoBoletin anexoBoletin, CicloAcademico cicloAcademico);

    List<CuotasGrupoHoras> allByDynatableGpoHoras(DynatableFilter filter, GrupoHoras grupoHoras, CicloAcademico cicloAcademico);

    List<AnexoCuotaUtilizadaBean> allByGpoHorasCiclo(GrupoHoras grupoHoras, CicloAcademico cicloAcademico);
}
