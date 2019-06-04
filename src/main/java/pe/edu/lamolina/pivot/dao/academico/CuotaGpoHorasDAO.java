package pe.edu.lamolina.pivot.dao.academico;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.academico.AnexoBoletin;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.CuotasGrupoHoras;
import pe.edu.lamolina.model.academico.Seccion;
import pe.edu.lamolina.model.horario.GrupoHoras;
import pe.edu.lamolina.pivot.controller.academico.cuotagpohoras.LetraCuotaUtilizadaBean;

public interface CuotaGpoHorasDAO extends EasyDAO<CuotasGrupoHoras> {

    List<CuotasGrupoHoras> allByDynatable(DynatableFilter filter, AnexoBoletin anexoBoletin, CicloAcademico cicloAcademico);

    List<CuotasGrupoHoras> allCuotasByAnexo(AnexoBoletin anexoBoletin, CicloAcademico cicloAcademico);

    CuotasGrupoHoras findByAnexoAndCicloAndGpoHoras(AnexoBoletin anexoBoletin, CicloAcademico cicloAcademico, String codigoGrupoHoras);

    List<LetraCuotaUtilizadaBean> allInAnexoBoletines(AnexoBoletin anexoBoletin, CicloAcademico cicloAcademico);

    List<LetraCuotaUtilizadaBean> allInAnexoBoletinesHoras(AnexoBoletin anexoBoletin, CicloAcademico cicloAcademico);

    List<LetraCuotaUtilizadaBean> allInAnexoBoletinesGrupos(AnexoBoletin anexoBoletin, CicloAcademico cicloAcademico);

    void updateColumns(CuotasGrupoHoras cuotasGrupoHoras, String... columns);

//    List<LetraCuotaUtilizadaBean> allInAnexoBoletinesGruposGroupByLetra(AnexoBoletin anexoBoletin, CicloAcademico cicloAcademico);

    Integer countByAnexoAndCicloAndLetraHoras(AnexoBoletin anexoBoletin, CicloAcademico cicloAcademico, String letraHoras);
}
