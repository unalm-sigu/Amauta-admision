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
import pe.edu.lamolina.model.enums.AlumnoRolExamenEstadoEnum;
import pe.edu.lamolina.model.rolexamen.AlumnoGrupoEspecial;
import pe.edu.lamolina.model.rolexamen.GrupoHorasExamen;
import pe.edu.lamolina.model.rolexamen.RolExamenes;
import pe.edu.lamolina.model.rolexamen.SeccionGrupoEspecial;
import pe.edu.lamolina.pivot.dao.rolexamen.AlumnoGrupoEspecialDAO;

@Repository
public class AlumnoGrupoEspecialDAOH extends AbstractEasyDAO<AlumnoGrupoEspecial> implements AlumnoGrupoEspecialDAO {

    public AlumnoGrupoEspecialDAOH() {
        super();
        setClazz(AlumnoGrupoEspecial.class);
    }

    //userRegistro alumno seccionGrupoEspecial rolExamenes
    @Override
    public AlumnoGrupoEspecial find(long id) {
        Octavia sql = Octavia.query()
                .from(AlumnoGrupoEspecial.class, "age")
                .join("userRegistro ur", "seccionGrupoEspecial sge", "alumno alu")
                .join("sge.rolExamenes rex", "alu.persona per", "ur.persona uregper")
                .filter("age.id", id);

        return find(sql);
    }

    @Override
    public Map<Long, Integer> countBySeccionesGrupoEspecial(List<SeccionGrupoEspecial> seccionesGrupoEspecial, AlumnoRolExamenEstadoEnum... estados) {
        Octavia sql = Octavia.query()
                .select("sge.id", "count(age)")
                .from(AlumnoGrupoEspecial.class, "age")
                .join("seccionGrupoEspecial sge")
                .in("age.estado", estados)
                .in("sge.id", seccionesGrupoEspecial)
                .groupBy("sge.id");

        List<Object[]> resultado = sql.all(getCurrentSession());
        Map<Long, Integer> result = new HashMap<>();
        for (Object[] objects : resultado) {
            result.put(TypesUtil.getLong(objects[0]), TypesUtil.getInt(objects[1]));
        }
        return result;
    }

    @Override
    public List<AlumnoGrupoEspecial> allBySeccionGrupoEspecialAndEstados(SeccionGrupoEspecial seccionGrupoEspecial, AlumnoRolExamenEstadoEnum... estados) {
        Octavia sql = Octavia.query()
                .from(AlumnoGrupoEspecial.class, "age")
                .join("alumno alu", "seccionGrupoEspecial sge")
                .join("userRegistro ureg", "ureg.persona pureg")
                .filter("sge.id", seccionGrupoEspecial)
                .in("age.estado", estados);
        return all(sql);
    }

    @Override
    public List<AlumnoGrupoEspecial> allByGrupoHorasExamenAndEstados(GrupoHorasExamen grupoHorasExamen, AlumnoRolExamenEstadoEnum... estados) {
        Octavia sql = Octavia.query()
                .from(AlumnoGrupoEspecial.class, "age")
                .join("alumno alu", "seccionGrupoEspecial sge", "sge.grupoHorasExamen ghe")
                .join("userRegistro ureg", "ureg.persona pureg")
                .filter("ghe.id", grupoHorasExamen)
                .in("age.estado", estados);
        return all(sql);
    }

    @Override
    public List<AlumnoGrupoEspecial> allBySeccionGrupoEspecialAndEstados(List<SeccionGrupoEspecial> seccionesGrupoEspecial, AlumnoRolExamenEstadoEnum... estados) {
        Octavia sql = Octavia.query()
                .from(AlumnoGrupoEspecial.class, "age")
                .join("alumno alu", "seccionGrupoEspecial sge")
                .join("userRegistro ureg", "ureg.persona pureg")
                .in("sge.id", seccionesGrupoEspecial)
                .in("age.estado", estados);
        return all(sql);
    }

    @Override
    public void deleteByRolExamenes(RolExamenes rolExamenes) {
        StringBuilder strb = new StringBuilder();
        strb.append(" delete from  AlumnoGrupoEspecial age where age.seccionGrupoEspecial.id in ");
        strb.append(" (Select ssge.id from SeccionGrupoEspecial ssge where  ssge.rolExamenes.id=:ROL_EXAMENES) ");

        Query query = getCurrentSession().createQuery(strb.toString());
        query.setParameter("ROL_EXAMENES", rolExamenes.getId());
        query.executeUpdate();
    }

    @Override
    public List<AlumnoGrupoEspecial> allByDynatableAndSeccionGrupoEsp(DynatableFilter filter, SeccionGrupoEspecial seccionGrupoEspecial) {
        DynatableSql sql = new DynatableSql(filter)
                .from(AlumnoGrupoEspecial.class, "age")
                .join("alumno alu", "alu.persona per", "seccionGrupoEspecial sge")
                .join("userRegistro ur", "ur.persona urPer")
                .filter("sge.id", seccionGrupoEspecial)
                .searchFields("alu.codigo");

        sql.searchComplexField("concat(coalesce(per.paterno,''),' ',coalesce(per.materno,''),' ',coalesce(per.nombres,''))")
                .searchComplexField("concat(coalesce(per.nombres,''),' ',coalesce(per.paterno,''),' ',coalesce(per.materno,''))");

        return all(sql);
    }

    @Override
    public void updateEstadoExclusion(AlumnoGrupoEspecial alumnoGrupoEspecial) {
        alumnoGrupoEspecial.setEstadoEnum(AlumnoRolExamenEstadoEnum.EXC);
        Octavia octavia = Octavia.update(AlumnoGrupoEspecial.class);
        octavia.set(alumnoGrupoEspecial, "estado");
        this.update(octavia);
    }

    @Override
    public void updateEstado(AlumnoGrupoEspecial alumnoGrupoEspecial) {
        Octavia octavia = Octavia.update(AlumnoGrupoEspecial.class);
        octavia.set(alumnoGrupoEspecial, "estado");
        this.update(octavia);
    }

}
