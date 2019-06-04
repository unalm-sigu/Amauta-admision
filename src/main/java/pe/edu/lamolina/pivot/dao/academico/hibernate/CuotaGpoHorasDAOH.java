package pe.edu.lamolina.pivot.dao.academico.hibernate;

import java.util.List;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableSql;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.model.academico.AnexoBoletin;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.CuotasGrupoHoras;
import pe.edu.lamolina.model.academico.Seccion;
import pe.edu.lamolina.model.horario.GrupoHoras;
import pe.edu.lamolina.model.horario.HorarioSeccion;
import pe.edu.lamolina.pivot.controller.academico.cuotadpto.AnexoCuotaUtilizadaBean;
import pe.edu.lamolina.pivot.controller.academico.cuotagpohoras.LetraCuotaUtilizadaBean;
import pe.edu.lamolina.pivot.dao.academico.CuotaGpoHorasDAO;

@Repository
public class CuotaGpoHorasDAOH extends AbstractEasyDAO<CuotasGrupoHoras> implements CuotaGpoHorasDAO {

    public CuotaGpoHorasDAOH() {
        super();
        setClazz(CuotasGrupoHoras.class);
    }

    @Override
    public List<CuotasGrupoHoras> allByDynatable(DynatableFilter filter, AnexoBoletin anexoBoletin, CicloAcademico cicloAcademico) {
        DynatableSql sql = new DynatableSql(filter)
                .from(CuotasGrupoHoras.class, "cgpo")
                .join("anexoBoletin ab", "grupoHoras gh", "cicloAcademico ca")
                .filter("ca.id", cicloAcademico)
                .searchFields("ab.nombre", "gh.codigo", "ca.descripcion")
                .filter("ab.id", anexoBoletin)
                .orderBy("gh.letra");
        return all(sql);
    }

    @Override
    public List<CuotasGrupoHoras> allCuotasByAnexo(AnexoBoletin anexoBoletin, CicloAcademico cicloAcademico) {
        Octavia sql = Octavia.query()
                .from(CuotasGrupoHoras.class, "cgh")
                .join("anexoBoletin ab", "grupoHoras gh", "cicloAcademico ca")
                .filter("ca.id", cicloAcademico)
                .filter("ab.id", anexoBoletin)
                .orderBy("cgh.id desc");

        return all(sql);
    }

    @Override
    public CuotasGrupoHoras findByAnexoAndCicloAndGpoHoras(AnexoBoletin anexoBoletin, CicloAcademico cicloAcademico, String codigoGrupoHoras) {
        Octavia sql = Octavia.query()
                .from(CuotasGrupoHoras.class, "cgh")
                .join("anexoBoletin ab", "grupoHoras gh", "cicloAcademico ca")
                .filter("ca.id", cicloAcademico)
                .filter("ab.id", anexoBoletin)
                .filter("gh.codigo", codigoGrupoHoras);
        return find(sql);
    }

    @Override
    public List<LetraCuotaUtilizadaBean> allByAnexoBoletinAcademico(AnexoBoletin anexoBoletine, CicloAcademico cicloAcademico) {
        Octavia sql = Octavia.query()
                .select(" grho.letra", " count(*) ")
                .into(LetraCuotaUtilizadaBean.class)
                .from(Seccion.class, "secc")
                .join("grupoHoras grho", "grupoSeccion grse", "grse.anexoBoletin anbo", "grse.cicloAcademico ca")
                .filter("ca.id", cicloAcademico)
                .filter("anbo.id", anexoBoletine)
                .groupBy("grho.letra");

        return (List<LetraCuotaUtilizadaBean>) sql.all(getCurrentSession());
    }

    @Override
    public List<LetraCuotaUtilizadaBean> allByAnexoBoletinHoras(AnexoBoletin anexoBoletin, CicloAcademico cicloAcademico) {
        Octavia sql = Octavia.query()
                .select(" grho.letra", " count(*) ")
                .into(LetraCuotaUtilizadaBean.class)
                .from(HorarioSeccion.class, "hsecc")
                .join("seccion secc", "secc.grupoHoras grho", "secc.grupoSeccion grse", "grse.anexoBoletin anbo", "grse.cicloAcademico ca")
                .filter("ca.id", cicloAcademico)
                .filter("anbo.id", anexoBoletin)
                .groupBy("grho.letra");

        return (List<LetraCuotaUtilizadaBean>) sql.all(getCurrentSession());
    }

    @Override
    public List<LetraCuotaUtilizadaBean> allByAnexoBoletinGrupo(AnexoBoletin anexoBoletin, CicloAcademico cicloAcademico) {
        Octavia sql = Octavia.query()
                .select("grho.letra", "grho.codigo", " count(*) ")
                .into(LetraCuotaUtilizadaBean.class)
                .from(Seccion.class, "secc")
                .join("grupoHoras grho", "grupoSeccion grse", "grse.anexoBoletin anbo", "grse.cicloAcademico ca")
                .filter("ca.id", cicloAcademico)
                .filter("anbo.id", anexoBoletin)
                .groupBy("grho.letra", "grho.codigo");

        return (List<LetraCuotaUtilizadaBean>) sql.all(getCurrentSession());
    }

    @Override
    public List<CuotasGrupoHoras> allByDynatableGpoHoras(DynatableFilter filter, GrupoHoras grupoHoras, CicloAcademico cicloAcademico) {
        DynatableSql sql = new DynatableSql(filter)
                .from(CuotasGrupoHoras.class, "cgpo")
                .join("anexoBoletin ab", "grupoHoras gh", "cicloAcademico ca")
                .filter("ca.id", cicloAcademico)
                .searchFields("ab.nombre", "gh.codigo", "ca.descripcion")
                .filter("gh.id", grupoHoras)
                .orderBy("gh.letra");

        return all(sql);
    }

    @Override
    public List<AnexoCuotaUtilizadaBean> allByGpoHorasCiclo(GrupoHoras grupoHoras, CicloAcademico cicloAcademico) {
        Octavia sql = Octavia.query()
                .select("anbo.id", " count(*) ")
                .into(AnexoCuotaUtilizadaBean.class)
                .from(Seccion.class, "secc")
                .join("grupoHoras grho", "grupoSeccion grse", "grse.anexoBoletin anbo", "grse.cicloAcademico ca")
                .filter("ca.id", cicloAcademico)
                .filter("grho.id", grupoHoras)
                .groupBy("anbo.id");

        return (List<AnexoCuotaUtilizadaBean>) sql.all(getCurrentSession());
    }

    @Override
    public void updateColumns(CuotasGrupoHoras cuotasGrupoHoras, String... columns) {
        Octavia sql = Octavia.update(CuotasGrupoHoras.class, "cgh");
        for (String column : columns) {
            sql.set(cuotasGrupoHoras, column);
        }
        this.update(sql);
    }

    @Override
    public Integer countByAnexoAndCicloAndLetraHoras(AnexoBoletin anexoBoletin, CicloAcademico cicloAcademico, String letraHoras) {
        Octavia sql = Octavia.query()
                .selectCount()
                .from(Seccion.class, "sec")
                .join("grupoSeccion gSec", "gSec.cicloAcademico ca", "grupoHoras gHor")
                .join("gSec.anexoBoletin bol")
                .filter("gHor.letra", letraHoras)
                .filter("bol.id", anexoBoletin)
                .filter("ca.id", cicloAcademico)
                .groupBy("gHor.letra");
        return TypesUtil.getInt(sql.find(getCurrentSession()));
    }
}
