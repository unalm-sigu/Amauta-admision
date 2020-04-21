package pe.edu.lamolina.amauta.dao.academico.hibernate;

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
import pe.edu.lamolina.model.enums.OficinaEnum;
import pe.edu.lamolina.model.enums.SeccionEstadoEnum;
import pe.edu.lamolina.model.horario.GrupoHoras;
import pe.edu.lamolina.model.horario.HorarioSeccion;
import pe.edu.lamolina.amauta.controller.academico.cuotadpto.AnexoCuotaUtilizadaBean;
import pe.edu.lamolina.amauta.controller.academico.cuotagpohoras.LetraCuotaUtilizadaBean;
import pe.edu.lamolina.amauta.dao.academico.CuotaGpoHorasDAO;

@Repository
public class CuotaGpoHorasDAOH extends AbstractEasyDAO<CuotasGrupoHoras> implements CuotaGpoHorasDAO {

    public CuotaGpoHorasDAOH() {
        super();
        setClazz(CuotasGrupoHoras.class);
    }

    public CuotasGrupoHoras find(Long id) {
        Octavia sql = Octavia.query()
                .from(CuotasGrupoHoras.class, "cgh")
                .join("anexoBoletin ab", "grupoHoras gh", "cicloAcademico ca")
                .filter("cgh.id", id);

        return find(sql);
    }

    @Override
    public List<CuotasGrupoHoras> allByDynatable(DynatableFilter filter, AnexoBoletin anexo, CicloAcademico ciclo) {
        DynatableSql sql = new DynatableSql(filter)
                .from(CuotasGrupoHoras.class, "cgpo")
                .join("anexoBoletin ab", "grupoHoras gh", "cicloAcademico ca")
                .filter("ca.id", ciclo)
                .searchFields("ab.nombre", "gh.codigo", "ca.descripcion")
                .filter("ab.id", anexo)
                .orderBy("gh.letra");
        return all(sql);
    }

    @Override
    public List<CuotasGrupoHoras> allByAnexoCiclo(AnexoBoletin anexo, CicloAcademico ciclo) {
        Octavia sql = Octavia.query()
                .from(CuotasGrupoHoras.class, "cgh")
                .join("anexoBoletin ab", "grupoHoras gh", "cicloAcademico ca")
                .filter("ca.id", ciclo)
                .filter("ab.id", anexo)
                .orderBy("cgh.id desc");

        return all(sql);
    }

    @Override
    public List<CuotasGrupoHoras> allByCiclo(CicloAcademico ciclo) {
        Octavia sql = Octavia.query()
                .from(CuotasGrupoHoras.class, "cgh")
                .join("anexoBoletin ab", "grupoHoras gh", "cicloAcademico ca")
                .filter("ca.id", ciclo);

        return all(sql);
    }

    @Override
    public List<CuotasGrupoHoras> allByDynatableGpoHoras(DynatableFilter filter, GrupoHoras grupoHoras, CicloAcademico cicloAcademico) {
        DynatableSql sql = new DynatableSql(filter)
                .from(CuotasGrupoHoras.class, "cgpo")
                .join("anexoBoletin ab", "grupoHoras gh", "cicloAcademico ca")
                .filter("ca.id", cicloAcademico)
                .searchFields("ab.nombre", "gh.codigo", "ca.descripcion")
                .filter("gh.id", grupoHoras)
                .orderBy("ab.nombre");

        return all(sql);
    }

    @Override
    public CuotasGrupoHoras findByAnexoAndCicloAndGpoHoras(AnexoBoletin anexoBoletin, CicloAcademico cicloAcademico, String letraGpoHoras) {
        Octavia sql = Octavia.query()
                .from(CuotasGrupoHoras.class, "cgh")
                .join("anexoBoletin ab", "grupoHoras gh", "cicloAcademico ca")
                .filter("ca.id", cicloAcademico)
                .filter("ab.id", anexoBoletin)
                .filter("gh.codigo", letraGpoHoras);
        return find(sql);
    }

    @Override
    public List<LetraCuotaUtilizadaBean> allLetrasUtilizadasByAnexoCiclo(AnexoBoletin anexoBoletine, CicloAcademico cicloAcademico) {
        Octavia sql = Octavia.query()
                .select("grho.letra", "count(case when grho.tipoSeccion='TEO' then grho.id end)", "count(case when grho.tipoSeccion='PRA' then grho.id end)")
                .into(LetraCuotaUtilizadaBean.class)
                .from(Seccion.class, "secc")
                .join("grupoHoras grho", "grupoSeccion grse", "grse.anexoBoletin anbo", "grse.cicloAcademico ca")
                .leftJoin("secc.aula au", "au.oficinaSupervisora ofi")
                .filter("secc.estado", SeccionEstadoEnum.ACT)
                .filter("grse.estado", SeccionEstadoEnum.ACT)
                .filter("ca.id", cicloAcademico)
                .filter("anbo.id", anexoBoletine)
                .beginBlock()
                .__().filter("ofi.codigo", OficinaEnum.OERA)
                .__().isNull("au.id")
                .endBlock()
                .groupBy("grho.letra");

        return (List<LetraCuotaUtilizadaBean>) sql.all(getCurrentSession());
    }

    @Override
    public List<LetraCuotaUtilizadaBean> allLetrasUtilizadasByCiclo(CicloAcademico cicloAcademico) {
        Octavia sql = Octavia.query()
                .select("anbo.id", "grho.letra", "count(case when grho.tipoSeccion='TEO' then grho.id end)", "count(case when grho.tipoSeccion='PRA' then grho.id end)")
                .into(LetraCuotaUtilizadaBean.class)
                .from(Seccion.class, "secc")
                .join("grupoHoras grho", "grupoSeccion grse", "grse.anexoBoletin anbo", "grse.cicloAcademico ca")
                .leftJoin("secc.aula au", "au.oficinaSupervisora ofi")
                .filter("secc.estado", SeccionEstadoEnum.ACT)
                .filter("grse.estado", SeccionEstadoEnum.ACT)
                .filter("ca.id", cicloAcademico)
                .beginBlock()
                .__().filter("ofi.codigo", OficinaEnum.OERA)
                .__().isNull("au.id")
                .endBlock()
                .groupBy("anbo.id", "grho.letra");

        return (List<LetraCuotaUtilizadaBean>) sql.all(getCurrentSession());
    }

    @Override
    public List<LetraCuotaUtilizadaBean> allHorasUtilizadasByAnexoCiclo(AnexoBoletin anexoBoletin, CicloAcademico cicloAcademico) {
        Octavia sql = Octavia.query()
                .select("grho.letra", "count(case when grho.tipoSeccion='TEO' then grho.id end)", "count(case when grho.tipoSeccion='PRA' then grho.id end)")
                .into(LetraCuotaUtilizadaBean.class)
                .from(HorarioSeccion.class, "hsecc")
                .join("seccion secc", "secc.grupoHoras grho", "secc.grupoSeccion grse", "grse.anexoBoletin anbo", "grse.cicloAcademico ca")
                .leftJoin("secc.aula au", "au.oficinaSupervisora ofi")
                .filter("secc.estado", SeccionEstadoEnum.ACT)
                .filter("grse.estado", SeccionEstadoEnum.ACT)
                .filter("ca.id", cicloAcademico)
                .filter("anbo.id", anexoBoletin)
                .beginBlock()
                .__().filter("ofi.codigo", OficinaEnum.OERA)
                .__().isNull("au.id")
                .endBlock()
                .groupBy("grho.letra");

        return (List<LetraCuotaUtilizadaBean>) sql.all(getCurrentSession());
    }

    @Override
    public List<LetraCuotaUtilizadaBean> allGposUtilizadosByAnexoCiclo(AnexoBoletin anexoBoletin, CicloAcademico cicloAcademico) {
        Octavia sql = Octavia.query()
                .select("grho.letra", "grho.codigo",
                        "count(case when grho.tipoSeccion='TEO' then grho.id end)",
                        "count(case when grho.tipoSeccion='PRA' then grho.id end)")
                .into(LetraCuotaUtilizadaBean.class)
                .from(Seccion.class, "secc")
                .join("grupoHoras grho", "grupoSeccion grse", "grse.anexoBoletin anbo", "grse.cicloAcademico ca")
                .leftJoin("secc.aula au", "au.oficinaSupervisora ofi")
                .filter("secc.estado", SeccionEstadoEnum.ACT)
                .filter("grse.estado", SeccionEstadoEnum.ACT)
                .filter("ca.id", cicloAcademico)
                .filter("anbo.id", anexoBoletin)
                .beginBlock()
                .__().filter("ofi.codigo", OficinaEnum.OERA)
                .__().isNull("au.id")
                .endBlock()
                .groupBy("grho.letra", "grho.codigo");

        return (List<LetraCuotaUtilizadaBean>) sql.all(getCurrentSession());
    }

    @Override
    public List<AnexoCuotaUtilizadaBean> allCuotasAnexosByLetraCiclo(GrupoHoras grupoHoras, CicloAcademico cicloAcademico) {
        Octavia sql = Octavia.query()
                .select("anbo.id",
                        "count(case when grho.tipoSeccion='TEO' then grho.id end)",
                        "count(case when grho.tipoSeccion='PRA' then grho.id end)")
                .into(AnexoCuotaUtilizadaBean.class)
                .from(Seccion.class, "secc")
                .join("grupoHoras grho", "grupoSeccion grse", "grse.anexoBoletin anbo", "grse.cicloAcademico ca")
                .leftJoin("aula au", "au.oficinaSupervisora ofi")
                .filter("secc.estado", SeccionEstadoEnum.ACT)
                .filter("grse.estado", SeccionEstadoEnum.ACT)
                .filter("ca.id", cicloAcademico)
                .filter("grho.letra", grupoHoras.getLetra())
                .beginBlock()
                .__().filter("ofi.codigo", OficinaEnum.OERA)
                .__().isNull("au.id")
                .endBlock()
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
    public Integer countSeccionesByAnexoCicloLetraGpo(AnexoBoletin anexoBoletin, CicloAcademico cicloAcademico, String letra, String tipoSeccion) {
        Octavia sql = Octavia.query()
                .selectCount()
                .from(Seccion.class, "sec")
                .join("grupoSeccion gSec", "gSec.cicloAcademico ca", "grupoHoras gHor")
                .join("gSec.anexoBoletin bol")
                .leftJoin("sec.aula au", "au.oficinaSupervisora ofi")
                .filter("sec.estado", SeccionEstadoEnum.ACT)
                .filter("gSec.estado", SeccionEstadoEnum.ACT)
                .filter("gHor.letra", letra)
                .filter("gHor.tipoSeccion", tipoSeccion)
                .filter("bol.id", anexoBoletin)
                .filter("ca.id", cicloAcademico)
                .beginBlock()
                .__().filter("ofi.codigo", OficinaEnum.OERA)
                .__().isNull("au.id")
                .endBlock()
                .groupBy("gHor.letra");
        Integer result = TypesUtil.getInt(sql.find(getCurrentSession()));
        return result == null ? 0 : result;
    }

    @Override
    public List<AnexoCuotaUtilizadaBean> allGposAnexosByLetraCiclo(GrupoHoras grupoHoras, CicloAcademico cicloAcademico) {
        Octavia sql = Octavia.query()
                .select("anbo.id", "grho.codigo",
                        "count(case when grho.tipoSeccion='TEO' then grho.id end)",
                        "count(case when grho.tipoSeccion='PRA' then grho.id end)")
                .into(AnexoCuotaUtilizadaBean.class)
                .from(Seccion.class, "secc")
                .join("grupoHoras grho", "grupoSeccion grse", "grse.anexoBoletin anbo", "grse.cicloAcademico ca")
                .leftJoin("aula au", "au.oficinaSupervisora ofi")
                .filter("secc.estado", SeccionEstadoEnum.ACT)
                .filter("grse.estado", SeccionEstadoEnum.ACT)
                .filter("ca.id", cicloAcademico)
                .filter("grho.letra", grupoHoras.getLetra())
                .beginBlock()
                .__().filter("ofi.codigo", OficinaEnum.OERA)
                .__().isNull("au.id")
                .endBlock()
                .groupBy("anbo.id", "grho.codigo");

        return (List<AnexoCuotaUtilizadaBean>) sql.all(getCurrentSession());
    }

}
