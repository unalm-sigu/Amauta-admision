package pe.edu.lamolina.pivot.dao.rolexamen.hibernate;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.hibernate.Query;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableSql;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Docente;
import pe.edu.lamolina.model.enums.DocenteRolExamenEstadoEnum;
import static pe.edu.lamolina.model.enums.TipoGestionEnum.PUB;
import pe.edu.lamolina.model.rolexamen.CursoMasivoExamen;
import pe.edu.lamolina.model.rolexamen.DocenteCursoMasivo;
import pe.edu.lamolina.model.rolexamen.GrupoHorasExamen;
import pe.edu.lamolina.model.rolexamen.RolExamenes;
import pe.edu.lamolina.model.rolexamen.SeccionCursoMasivo;
import pe.edu.lamolina.model.seguridad.Usuario;
import pe.edu.lamolina.pivot.dao.rolexamen.DocenteCursoMasivoDAO;

@Repository
public class DocenteCursoMasivoDAOH extends AbstractEasyDAO<DocenteCursoMasivo> implements DocenteCursoMasivoDAO {

    public DocenteCursoMasivoDAOH() {
        super();
        setClazz(DocenteCursoMasivo.class);
    }

    @Override
    public DocenteCursoMasivo find(long id) {
        Octavia sql = Octavia.query()
                .from(DocenteCursoMasivo.class, "dcm")
                .join("cursoMasivoExamen cm", "docente d", "userRegistro ur")
                .join("cm.rolExamenes re")
                .left("ur.persona urPer", "cm.grupoHorasExamen ghe")
                .filter("dcm.id", id);
        return find(sql);
    }

    @Override
    public void deleteByCursoMasivo(CursoMasivoExamen cursoMasivoExamen) {
        StringBuilder strb = new StringBuilder();
        strb.append(" delete from  DocenteCursoMasivo dcm where dcm.cursoMasivoExamen.id = :CURSO_MASIVO ");

        Query query = getCurrentSession().createQuery(strb.toString());
        query.setParameter("CURSO_MASIVO", cursoMasivoExamen.getId());
        query.executeUpdate();
    }

    @Override
    public List<DocenteCursoMasivo> allByRolExamenes(RolExamenes rolExamenes, DocenteRolExamenEstadoEnum... estados) {
        Octavia sql = Octavia.query()
                .from(DocenteCursoMasivo.class, "dcm")
                .join("cursoMasivoExamen cm", "docente d", "userRegistro ur")
                .join("cm.rolExamenes re")
                .left("ur.persona urPer")
                .filter("re.id", rolExamenes)
                .in("dcm.estado", estados)
                .orderBy("cm.id desc");
        return all(sql);
    }

    @Override
    public List<DocenteCursoMasivo> allByCursoMasivo(CursoMasivoExamen cursoMasivoExamen, DocenteRolExamenEstadoEnum... estados) {
        Octavia sql = Octavia.query()
                .from(DocenteCursoMasivo.class, "dcm")
                .join("cursoMasivoExamen cm", "docente d", "userRegistro ur")
                .join("cm.rolExamenes re")
                .left("ur.persona urPer")
                .filter("cm.id", cursoMasivoExamen)
                .in("dcm.estado", estados)
                .orderBy("cm.id desc");
        return all(sql);
    }

    @Override
    public List<DocenteCursoMasivo> allByGrupoHorasExamenAndEstados(GrupoHorasExamen grupoHorasExamen, DocenteRolExamenEstadoEnum... estados) {
        Octavia sql = Octavia.query()
                .from(DocenteCursoMasivo.class, "dcm")
                .join("cursoMasivoExamen cm", "docente d", "userRegistro ur")
                .join("cm.rolExamenes re")
                .left("ur.persona urPer", "cm.grupoHorasExamen ghe")
                .filter("ghe.id", grupoHorasExamen)
                .in("dcm.estado", estados)
                .orderBy("cm.id desc");
        return all(sql);
    }

    @Override
    public List<DocenteCursoMasivo> allByCursosMasivos(List<CursoMasivoExamen> cursosMasivos, DocenteRolExamenEstadoEnum... estados) {
        Octavia sql = Octavia.query()
                .from(DocenteCursoMasivo.class, "dcm")
                .join("cursoMasivoExamen cm", "docente d", "d.persona dper", "userRegistro ur", "ur.persona urPer", "cm.rolExamenes re")
                .in("cm.id", cursosMasivos)
                .in("dcm.estado", estados)
                .orderBy("cm.id desc");
        return all(sql);
    }

    @Override
    public Map<Long, Integer> countByCursosMasivos(List<CursoMasivoExamen> cursosMasivosExamen, DocenteRolExamenEstadoEnum... estados) {
        Octavia sql = Octavia.query()
                .select("cme.id", "count(dcm)")
                .from(DocenteCursoMasivo.class, "dcm")
                .join("cursoMasivoExamen cme")
                .in("dcm.estado", estados)
                .in("cme.id", cursosMasivosExamen)
                .groupBy("cme.id");

        List<Object[]> resultado = sql.all(getCurrentSession());
        Map<Long, Integer> result = new HashMap<>();
        for (Object[] objects : resultado) {
            result.put(TypesUtil.getLong(objects[0]), TypesUtil.getInt(objects[1]));
        }
        return result;
    }

    @Override
    public void updateEstadoExclusion(DocenteCursoMasivo docenteCursoMasivo) {
        docenteCursoMasivo.setEstadoEnum(DocenteRolExamenEstadoEnum.EXC);
        Octavia octavia = Octavia.update(DocenteCursoMasivo.class);
        octavia.set(docenteCursoMasivo, "estado");
        // octavia.set(docenteCursoMasivo, "usuarioExclusion");
        // octavia.set(docenteCursoMasivo, "fechaExclusion");
        this.update(octavia);
    }

    @Override
    public void updateEstado(DocenteCursoMasivo docenteCursoMasivo) {
        Octavia octavia = Octavia.update(DocenteCursoMasivo.class);
        octavia.set(docenteCursoMasivo, "estado");
        // octavia.set(docenteCursoMasivo, "usuarioExclusion");
        // octavia.set(docenteCursoMasivo, "fechaExclusion");
        this.update(octavia);
    }

    @Override
    public List<DocenteCursoMasivo> allByDynatableAndCursoMasivo(DynatableFilter filter, CursoMasivoExamen cursoMasivoExamen) {
        DynatableSql sql = new DynatableSql(filter)
                .from(DocenteCursoMasivo.class, "sge")
                .join("cursoMasivoExamen cm", "docente d", "d.persona per")
                .join("cm.rolExamenes re", "userRegistro ur", "ur.persona urPer")
                .filter("cm.id", cursoMasivoExamen.getId())
                .searchFields("d.codigo");

        sql.searchComplexField("concat(coalesce(per.paterno,''),' ',coalesce(per.materno,''),' ',coalesce(per.nombres,''))")
                .searchComplexField("concat(coalesce(per.nombres,''),' ',coalesce(per.paterno,''),' ',coalesce(per.materno,''))");

        return all(sql);
    }

    @Override
    public List<DocenteCursoMasivo> allByDocenteAndCiclo(Docente docente, CicloAcademico cicloAcademico) {

        Octavia sql = new Octavia()
                .from(DocenteCursoMasivo.class, "dcm")
                .join("docente dc", "cursoMasivoExamen cme", "cme.rolExamenes re", "re.eventoCicloAcademico eca")
                .join("eca.cicloAcademico ca", "cme.grupoHorasExamen ghe")
                .join("ghe.dia", "ghe.horaInicio", "ghe.horaFin", "ghe.grupoHoras")
                .filter("ca.id", cicloAcademico)
                .filter("re.estado", PUB)
                .filter("dc.id", docente);

        return all(sql);
    }

    @Override
    public List<DocenteCursoMasivo> allByCursoMasivoAndDocenteAndEstados(CursoMasivoExamen cursoMasivoExamen, Docente docente, DocenteRolExamenEstadoEnum... estados) {
        Octavia sql = new Octavia()
                .from(DocenteCursoMasivo.class, "dcm")
                .join("docente dc", "cursoMasivoExamen cme", "cme.rolExamenes re", "re.eventoCicloAcademico eca")
                .join("eca.cicloAcademico ca", "cme.grupoHorasExamen ghe")
                .join("ghe.dia", "ghe.horaInicio", "ghe.horaFin", "ghe.grupoHoras")
                .filter("cme.id", cursoMasivoExamen)
                .in("re.estado", estados)
                .filter("dc.id", docente);
        return all(sql);
    }

    @Override
    public void createDocentesCursoMasivo(
            List<DocenteCursoMasivo> docentesCursoMasivo,
            CursoMasivoExamen cursoMasivo,
            Usuario user) {

        if (docentesCursoMasivo.isEmpty()) {
            return;
        }
        List<Long> ids = docentesCursoMasivo.stream().map(x -> x.getDocente().getId()).collect(Collectors.toList());

        StringBuilder sql = new StringBuilder();
        sql.append("insert into ").append(tb(DocenteCursoMasivo.class));
        sql.append("  (estado,secciones,fechaRegistro,cursoMasivoExamen,docente,userRegistro) ");
        sql.append(" select :ESTADO, :SECCIONES, :FECHA, cm, doc, usr ");
        sql.append("   from ").append(tb(CursoMasivoExamen.class)).append(" cm, ");
        sql.append("        ").append(tb(Docente.class)).append(" doc, ");
        sql.append("        ").append(tb(Usuario.class)).append(" usr ");
        sql.append("  where doc.id in (:DOCENTES) ");
        sql.append("    and cm.id = :CURSO ");
        sql.append("    and usr.id = :USER ");

        Query query = getCurrentSession().createQuery(sql.toString());
        query.setParameterList("DOCENTES", ids);
        query.setParameter("CURSO", cursoMasivo.getId());
        query.setParameter("USER", user.getId());
        query.setParameter("ESTADO", DocenteRolExamenEstadoEnum.ACT.name());
        query.setParameter("FECHA", new Date());
        query.setParameter("SECCIONES", BigDecimal.ZERO.intValue());

        query.executeUpdate();

    }

    private String tb(Class clazz) {
        return clazz.getSimpleName();
    }

}
