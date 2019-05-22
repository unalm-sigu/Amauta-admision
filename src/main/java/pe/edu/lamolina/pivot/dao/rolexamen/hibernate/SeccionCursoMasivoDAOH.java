package pe.edu.lamolina.pivot.dao.rolexamen.hibernate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.hibernate.Query;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableSql;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.model.academico.Docente;
import pe.edu.lamolina.model.academico.Seccion;
import pe.edu.lamolina.model.enums.SeccionRolExamenEstadoEnum;
import pe.edu.lamolina.model.rolexamen.CursoMasivoExamen;
import pe.edu.lamolina.model.rolexamen.GrupoHorasExamen;
import pe.edu.lamolina.model.rolexamen.RolExamenes;
import pe.edu.lamolina.model.rolexamen.SeccionCursoMasivo;
import pe.edu.lamolina.pivot.dao.rolexamen.*;

@Repository
public class SeccionCursoMasivoDAOH extends AbstractEasyDAO<SeccionCursoMasivo> implements SeccionCursoMasivoDAO {

    public SeccionCursoMasivoDAOH() {
        super();
        setClazz(SeccionCursoMasivo.class);
    }

    @Override
    public void deleteByCursoMasivo(CursoMasivoExamen cursoMasivoExamen) {
        StringBuilder strb = new StringBuilder();
        strb.append(" delete from  SeccionCursoMasivo scm where scm.cursoMasivoExamen.id = :CURSO_MASIVO ");

        Query query = getCurrentSession().createQuery(strb.toString());
        query.setParameter("CURSO_MASIVO", cursoMasivoExamen.getId());
        query.executeUpdate();
    }

    @Override
    public SeccionCursoMasivo find(long id) {
        Octavia sql = Octavia.query()
                .from(SeccionCursoMasivo.class, "scm")
                .join("cursoMasivoExamen cme", "cme.rolExamenes rexa", "scm.docente doc", "userRegistro ur", "seccion se")
                .join("cme.curso cur")
                .left("cme.grupoHorasExamen ghe")
                .filter("scm.id", id);
        return find(sql);
    }

    @Override
    public List<SeccionCursoMasivo> allByCursosMasivos(List<CursoMasivoExamen> cursosMasivosExamenes) {
        Octavia sql = Octavia.query()
                .from(SeccionCursoMasivo.class, "scm")
                .join("cursoMasivoExamen cme", "userRegistro ur", "seccion se")
                .join("se.grupoSeccion gs", "gs.curso")
                .in("cme.id", cursosMasivosExamenes);
        return all(sql);
    }

    @Override
    public List<SeccionCursoMasivo> allByCursosMasivos(List<CursoMasivoExamen> cursosMasivosExamenes, SeccionRolExamenEstadoEnum... estados) {
        Octavia sql = Octavia.query()
                .from(SeccionCursoMasivo.class, "scm")
                .join("cursoMasivoExamen cme", "userRegistro ur", "seccion se")
                .in("cme.id", cursosMasivosExamenes)
                .in("scm.estado", estados);
        return all(sql);
    }

    @Override
    public List<SeccionCursoMasivo> allSeccionByCursoMasivo(CursoMasivoExamen cursoMasivo) {
        Octavia sql = Octavia.query()
                .from(SeccionCursoMasivo.class, "scm")
                .join("cursoMasivoExamen cme")
                .join("userRegistro ureg", "ureg.persona pureg")
                .left("usuarioExclusion uexl", "uexl.persona puexl")
                .filter("cme.id", cursoMasivo);
        return all(sql);
    }

    @Override
    public List<SeccionCursoMasivo> allByCursoMasivo(CursoMasivoExamen cursoMasivo, SeccionRolExamenEstadoEnum... estados) {
        Octavia sql = Octavia.query()
                .from(SeccionCursoMasivo.class, "scm")
                .join("cursoMasivoExamen cme")
                .join("userRegistro ureg", "ureg.persona pureg")
                .left("usuarioExclusion uexl", "uexl.persona puexl")
                .in("scm.estado", estados)
                .filter("cme.id", cursoMasivo);
        return all(sql);
    }

    @Override
    public List<SeccionCursoMasivo> allByDocenteAndEstados(Docente docente, SeccionRolExamenEstadoEnum... estados) {
        Octavia sql = Octavia.query()
                .from(SeccionCursoMasivo.class, "scm")
                .join("cursoMasivoExamen cme")
                .join("userRegistro ureg", "ureg.persona pureg", "docente doc")
                .left("usuarioExclusion uexl", "uexl.persona puexl")
                .in("scm.estado", estados)
                .filter("doc.id", docente);
        return all(sql);
    }

    @Override
    public List<SeccionCursoMasivo> allByRolExamenes(RolExamenes rolExamenes, SeccionRolExamenEstadoEnum... estados) {
        Octavia sql = Octavia.query()
                .from(SeccionCursoMasivo.class, "scm")
                .join("cursoMasivoExamen cme")
                .join("userRegistro ureg", "ureg.persona pureg", "cme.rolExamenes rex")
                .left("usuarioExclusion uexl", "uexl.persona puexl")
                .in("scm.estado", estados)
                .filter("rex.id", rolExamenes);
        return all(sql);
    }

    @Override
    public void updateEstadoExcluido(SeccionCursoMasivo seccionCursoMasivo) {
        StringBuilder strBuilder = new StringBuilder();
        strBuilder.append(" update SeccionCursoMasivo scm set scm.estado=:ESTADO, scm.usuarioExclusion.id=:USUARIO, scm.fechaExclusion=:FECHA_EXC ");
        strBuilder.append(" where scm.id=:PRM_ID ");
        Query query = getCurrentSession().createQuery(strBuilder.toString());
        query.setParameter("PRM_ID", seccionCursoMasivo.getId());
        query.setParameter("ESTADO", SeccionRolExamenEstadoEnum.EXC.name());
        query.setParameter("USUARIO", seccionCursoMasivo.getUsuarioExclusion().getId());
        query.setParameter("FECHA_EXC", seccionCursoMasivo.getFechaExclusion());
        query.executeUpdate();
    }

    @Override
    public void updateEstado(SeccionCursoMasivo cursoMasivoExamen) {
        Octavia octavia = Octavia.update(SeccionCursoMasivo.class);
        octavia.set(cursoMasivoExamen, "estado");
        this.update(octavia);
    }

    @Override
    public Map<Long, Integer> countByCursosMasivos(List<CursoMasivoExamen> cursosMasivosExamen, SeccionRolExamenEstadoEnum... estados) {
        Octavia sql = Octavia.query()
                .select("cme.id", "count(scm)")
                .from(SeccionCursoMasivo.class, "scm")
                .join("cursoMasivoExamen cme")
                .in("scm.estado", estados)
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
    public List<SeccionCursoMasivo> allByDynatableAndCursoMasivo(DynatableFilter filter, CursoMasivoExamen cursoMasivoExamen) {
        DynatableSql sql = new DynatableSql(filter)
                .from(SeccionCursoMasivo.class, "scm")
                .join("seccion sec", "cursoMasivoExamen cm", "cm.rolExamenes re", "sec.grupoHoras gh")
                .left("sec.aula au")
                .filter("cm.id", cursoMasivoExamen.getId())
                .searchFields("sec.codigo");

        return all(sql);
    }

    @Override
    public Integer countDocenteByCursoMasivo(Docente docente, CursoMasivoExamen cursoMasivoExamen, SeccionRolExamenEstadoEnum... estados) {
        Octavia sql = Octavia.query()
                .select("count(scm)")
                .from(SeccionCursoMasivo.class, "scm")
                .join("cursoMasivoExamen cme", "docente doc")
                .in("scm.estado", estados)
                .filter("doc.id", docente)
                .filter("cme.id", cursoMasivoExamen);
        return TypesUtil.getInt(find(sql));
    }

    @Override
    public Integer countByCursoMasivo(CursoMasivoExamen cursoMasivoExamen, SeccionRolExamenEstadoEnum... estados) {
        Octavia sql = Octavia.query()
                .select("count(scm)")
                .from(SeccionCursoMasivo.class, "scm")
                .join("cursoMasivoExamen cme", "docente doc")
                .filter("cme.id", cursoMasivoExamen)
                .in("scm.estado", estados);
        return TypesUtil.getInt(find(sql));
    }

    @Override
    public List<SeccionCursoMasivo> allByGrupoHorasExamen(List<GrupoHorasExamen> grupoHorasExamenes) {
        Octavia sql = Octavia.query()
                .from(SeccionCursoMasivo.class, "scm")
                .join("cursoMasivoExamen cme", "cme.rolExamenes rexa", "userRegistro ur", "seccion se", "cme.curso cur")
                .join("cme.grupoHorasExamen ghe", "ghe.grupoHoras gh", "ghe.horaInicio hi", "ghe.horaFin hf")
                .in("ghe.id", grupoHorasExamenes);
        return all(sql);
    }

    @Override
    public SeccionCursoMasivo findByRolExamenesSeccion(RolExamenes rol, Seccion seccion, SeccionRolExamenEstadoEnum... estado) {
        Octavia sql = Octavia.query()
                .from(SeccionCursoMasivo.class, "scm")
                .join("cursoMasivoExamen cme", "cme.rolExamenes rexa", "userRegistro ur", "seccion se", "cme.curso cur")
                .left("cme.grupoHorasExamen ghe", "ghe.grupoHoras gh", "ghe.horaInicio hi", "ghe.horaFin hf")
                .filter("se.id", seccion)
                .in("scm.estado", estado)
                .filter("rexa.id", rol);

        return find(sql);
    }

}
