package pe.edu.lamolina.pivot.dao.rolexamen.hibernate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableSql;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.model.enums.AlumnoRolExamenEstadoEnum;
import pe.edu.lamolina.model.rolexamen.AlumnoCursoMasivo;
import pe.edu.lamolina.model.rolexamen.CursoMasivoExamen;
import pe.edu.lamolina.model.rolexamen.RolExamenes;
import pe.edu.lamolina.model.rolexamen.SeccionCursoMasivo;
import pe.edu.lamolina.pivot.dao.rolexamen.AlumnoCursoMasivoDAO;

@Repository
public class AlumnoCursoMasivoDAOH extends AbstractEasyDAO<AlumnoCursoMasivo> implements AlumnoCursoMasivoDAO {

    public AlumnoCursoMasivoDAOH() {
        super();
        setClazz(AlumnoCursoMasivo.class);
    }

    @Override
    public List<AlumnoCursoMasivo> allAlumnoByCursoMasivo(CursoMasivoExamen cursoMasivo) {
        Octavia sql = Octavia.query()
                .from(AlumnoCursoMasivo.class, "acm")
                .join("cursoMasivoExamen cme", "userRegistro ur")
                .filter("cme.id", cursoMasivo);
        return all(sql);
    }

    @Override
    public List<AlumnoCursoMasivo> allByCursoMasivo(CursoMasivoExamen cursoMasivo, AlumnoRolExamenEstadoEnum... estados) {
        Octavia sql = Octavia.query()
                .from(AlumnoCursoMasivo.class, "acm")
                .join("cursoMasivoExamen cme", "userRegistro ur")
                .filter("cme.id", cursoMasivo)
                .in("acm.estado", estados);
        return all(sql);
    }

    @Override
    public List<AlumnoCursoMasivo> allAlumnoByRolExamenes(RolExamenes rolExamenes, AlumnoRolExamenEstadoEnum... estados) {
        Octavia sql = Octavia.query()
                .from(AlumnoCursoMasivo.class, "acm")
                .join("cursoMasivoExamen cme", "userRegistro ur")
                .join("cme.rolExamenes rex")
                .filter("rex.id", rolExamenes)
                .in("acm.estado", estados);
        return all(sql);
    }

    @Override
    public List<AlumnoCursoMasivo> allByCursosMasivos(List<CursoMasivoExamen> cursosMasivoExamenes, AlumnoRolExamenEstadoEnum... estados) {
        Octavia sql = Octavia.query()
                .from(AlumnoCursoMasivo.class, "acm")
                .join("cursoMasivoExamen cme", "userRegistro ur", "alumno alu", "alu.persona aluper", "cme.rolExamenes rex")
                .in("cme.id", cursosMasivoExamenes)
                .in("acm.estado", estados);
        return all(sql);
    }

    @Override
    public List<AlumnoCursoMasivo> allBySeccionCursosMasivos(List<SeccionCursoMasivo> seccionesCursoMasivo, AlumnoRolExamenEstadoEnum... estados) {
        Octavia sql = Octavia.query()
                .from(AlumnoCursoMasivo.class, "acm")
                .join("cursoMasivoExamen cme", "seccionCursoMasivo scm", "userRegistro ur", "alumno alu", "alu.persona aluper", "cme.rolExamenes rex")
                .in("scm.id", seccionesCursoMasivo)
                .in("acm.estado", estados);
        return all(sql);
    }

    @Override
    public List<AlumnoCursoMasivo> allBySeccionCursosMasivos(SeccionCursoMasivo seccionCursoMasivo, AlumnoRolExamenEstadoEnum... estados) {
        Octavia sql = Octavia.query()
                .from(AlumnoCursoMasivo.class, "acm")
                .join("cursoMasivoExamen cme", "seccionCursoMasivo scm", "userRegistro ur", "alumno alu", "alu.persona aluper", "cme.rolExamenes rex")
                .filter("scm.id", seccionCursoMasivo)
                .in("acm.estado", estados);
        return all(sql);
    }

    @Override
    public List<AlumnoCursoMasivo> allByDynatableAndCursoMasivo(DynatableFilter filter, CursoMasivoExamen cursoMasivoExamen) {
        DynatableSql sql = new DynatableSql(filter)
                .from(AlumnoCursoMasivo.class, "acm")
                .join("cursoMasivoExamen cm", "alumno alu", "alu.persona per")
                .join("cm.rolExamenes re", "userRegistro ur", "ur.persona urPer")
                .filter("cm.id", cursoMasivoExamen.getId())
                .searchFields("alu.codigo");

        sql.searchComplexField("concat(coalesce(per.paterno,''),' ',coalesce(per.materno,''),' ',coalesce(per.nombres,''))")
                .searchComplexField("concat(coalesce(per.nombres,''),' ',coalesce(per.paterno,''),' ',coalesce(per.materno,''))");

        return all(sql);
    }

    @Override
    public void updateEstadoExclusion(AlumnoCursoMasivo alumnoCursoMasivo) {
        alumnoCursoMasivo.setEstadoEnum(AlumnoRolExamenEstadoEnum.EXC);
        Octavia octavia = Octavia.update(AlumnoCursoMasivo.class);
        octavia.set(alumnoCursoMasivo, "estado");
        // octavia.set(docenteCursoMasivo, "usuarioExclusion");
        // octavia.set(docenteCursoMasivo, "fechaExclusion");
        this.update(octavia);
    }

    @Override
    public void updateEstado(AlumnoCursoMasivo alumnoCursoMasivo) {
        Octavia octavia = Octavia.update(AlumnoCursoMasivo.class);
        octavia.set(alumnoCursoMasivo, "estado");
        this.update(octavia);
    }

    @Override
    public Map<Long, Integer> countByCursosMasivos(List<CursoMasivoExamen> cursosMasivosExamen, AlumnoRolExamenEstadoEnum... estados) {
        Octavia sql = Octavia.query()
                .select("cme.id", "count(acm)")
                .from(AlumnoCursoMasivo.class, "acm")
                .join("cursoMasivoExamen cme")
                .in("acm.estado", estados)
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
    public Map<Long, Integer> countBySeccionCursosMasivos(List<SeccionCursoMasivo> seccionesCursoMasivo, AlumnoRolExamenEstadoEnum... estados) {
        Octavia sql = Octavia.query()
                .select("scm.id", "count(acm)")
                .from(AlumnoCursoMasivo.class, "acm")
                .join("cursoMasivoExamen cme", "seccionCursoMasivo scm")
                .in("acm.estado", estados)
                .in("scm.id", seccionesCursoMasivo)
                .groupBy("scm.id");

        List<Object[]> resultado = sql.all(getCurrentSession());
        Map<Long, Integer> result = new HashMap<>();
        for (Object[] objects : resultado) {
            result.put(TypesUtil.getLong(objects[0]), TypesUtil.getInt(objects[1]));
        }
        return result;
    }

}
