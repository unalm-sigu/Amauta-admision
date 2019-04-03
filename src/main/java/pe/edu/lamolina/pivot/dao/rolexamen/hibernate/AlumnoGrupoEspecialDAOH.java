package pe.edu.lamolina.pivot.dao.rolexamen.hibernate;

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
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.enums.AlumnoRolExamenEstadoEnum;
import pe.edu.lamolina.model.rolexamen.AlumnoGrupoEspecial;
import pe.edu.lamolina.model.rolexamen.GrupoHorasExamen;
import pe.edu.lamolina.model.rolexamen.RolExamenes;
import pe.edu.lamolina.model.rolexamen.SeccionGrupoEspecial;
import pe.edu.lamolina.model.seguridad.Usuario;
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

    @Override
    public void createForSeccionGrupoEspecial(
            List<AlumnoGrupoEspecial> alumnosGpoEspecial,
            SeccionGrupoEspecial seccionGpoEspecial,
            Date fecha,
            Usuario user) {

        if (alumnosGpoEspecial.isEmpty()) {
            return;
        }

        List<Long> ids = alumnosGpoEspecial.stream().map(x -> x.getAlumno().getId()).collect(Collectors.toList());

        StringBuilder sql = new StringBuilder();
        sql.append("insert into ").append(tb(AlumnoGrupoEspecial.class));
        sql.append("  (estado,fechaRegistro,seccionGrupoEspecial,alumno,userRegistro) ");
        sql.append(" select :ESTADO, :FECHA, sge, alu, usr ");
        sql.append("   from ").append(tb(Alumno.class)).append(" alu, ");
        sql.append("        ").append(tb(SeccionGrupoEspecial.class)).append(" sge, ");
        sql.append("        ").append(tb(Usuario.class)).append(" usr ");
        sql.append("  where alu.id in (:ALUMNOS) ");
        sql.append("    and sge.id = :SECCION ");
        sql.append("    and usr.id = :USER ");

        Query query = getCurrentSession().createQuery(sql.toString());
        query.setParameterList("ALUMNOS", ids);
        query.setParameter("SECCION", seccionGpoEspecial.getId());
        query.setParameter("USER", user.getId());
        query.setParameter("ESTADO", AlumnoRolExamenEstadoEnum.ACT.name());
        query.setParameter("FECHA", fecha);

        query.executeUpdate();

    }

    private String tb(Class clazz) {
        return clazz.getSimpleName();
    }

}
