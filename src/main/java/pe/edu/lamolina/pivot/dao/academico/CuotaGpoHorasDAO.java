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

    List<CuotasGrupoHoras> allByAnexoCiclo(AnexoBoletin anexoBoletin, CicloAcademico cicloAcademico);

    List<LetraCuotaUtilizadaBean> allLetrasUtilizadasByAnexoCiclo(AnexoBoletin anexoBoletin, CicloAcademico cicloAcademico);

    List<LetraCuotaUtilizadaBean> allHorasUtilizadasByAnexoCiclo(AnexoBoletin anexoBoletin, CicloAcademico cicloAcademico);

    List<LetraCuotaUtilizadaBean> allGposUtilizadosByAnexoCiclo(AnexoBoletin anexoBoletin, CicloAcademico cicloAcademico);

    List<CuotasGrupoHoras> allByDynatableGpoHoras(DynatableFilter filter, GrupoHoras grupoHoras, CicloAcademico cicloAcademico);

    CuotasGrupoHoras findByAnexoAndCicloAndGpoHoras(AnexoBoletin anexoBoletin, CicloAcademico cicloAcademico, String codigoGrupoHoras);

    void updateColumns(CuotasGrupoHoras cuotasGrupoHoras, String... columns);

    Integer countSeccionesByAnexoCicloLetraGpo(AnexoBoletin anexoBoletin, CicloAcademico cicloAcademico, String letraHoras);

    List<AnexoCuotaUtilizadaBean> allCuotasAnexosByLetraCiclo(GrupoHoras grupoHoras, CicloAcademico cicloAcademico);

    List<AnexoCuotaUtilizadaBean> allGposAnexosByLetraCiclo(GrupoHoras grupoHoras, CicloAcademico cicloAcademico);

}
