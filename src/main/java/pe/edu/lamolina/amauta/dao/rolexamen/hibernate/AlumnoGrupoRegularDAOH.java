package pe.edu.lamolina.amauta.dao.rolexamen.hibernate;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.hibernate.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Insecto;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableSql;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.enums.AlumnoRolExamenEstadoEnum;
import pe.edu.lamolina.model.rolexamen.AlumnoGrupoRegular;
import pe.edu.lamolina.model.rolexamen.GrupoHorasExamen;
import pe.edu.lamolina.model.rolexamen.LetraGrupoRegular;
import pe.edu.lamolina.model.rolexamen.SeccionGrupoRegular;
import pe.edu.lamolina.model.seguridad.Usuario;
import pe.edu.lamolina.amauta.dao.rolexamen.AlumnoGrupoRegularDAO;

@Repository
public class AlumnoGrupoRegularDAOH extends AbstractEasyDAO<AlumnoGrupoRegular> implements AlumnoGrupoRegularDAO {

    public AlumnoGrupoRegularDAOH() {
        super();
        setClazz(AlumnoGrupoRegular.class);
    }

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public AlumnoGrupoRegular find(long id) {
        Octavia sql = Octavia.query()
                .from(AlumnoGrupoRegular.class, "agr")
                .join("userRegistro ur", "seccionGrupoRegular sgr", "alumno alu")
                .join("sgr.letraGrupoRegular lgr", "alu.persona per")
                .filter("agr.id", id);

        return find(sql);
    }

    @Override
    public List<AlumnoGrupoRegular> allByLetraGrupoActives(LetraGrupoRegular letraGrupoRegular) {
        return this.allByLetraGrupoAndEstado(letraGrupoRegular, AlumnoRolExamenEstadoEnum.ACT);
    }

    @Override
    public List<AlumnoGrupoRegular> allByLetraGrupoAndEstado(LetraGrupoRegular letraGrupoRegular, AlumnoRolExamenEstadoEnum estadoEnum) {
        Octavia sql = Octavia.query()
                .from(AlumnoGrupoRegular.class, "agr")
                .join("agr.seccionGrupoRegular sgr", "sgr.letraGruposRegulares gs", "userRegistro cur")
                .filter("agr.estado", estadoEnum)
                .filter("gs.id", letraGrupoRegular);
        return all(sql);
    }

    @Override
    public List<AlumnoGrupoRegular> allByGrupoHorasExamenAndEstados(GrupoHorasExamen grupoHorasExamen, AlumnoRolExamenEstadoEnum estadoEnum) {
        Octavia sql = Octavia.query()
                .from(AlumnoGrupoRegular.class, "agr")
                .join("agr.seccionGrupoRegular sgr", "sgr.letraGrupoRegular lgr", "lgr.grupoHorasExamen ghe", "alumno")
                .filter("agr.estado", estadoEnum)
                .filter("ghe.id", grupoHorasExamen);
        return all(sql);
    }

    @Override
    public List<AlumnoGrupoRegular> allByFechaEstados(Date fecha, AlumnoRolExamenEstadoEnum estadoEnum) {
        Octavia sql = Octavia.query()
                .from(AlumnoGrupoRegular.class, "agr")
                .join("agr.seccionGrupoRegular sgr", "sgr.letraGrupoRegular lgr", "lgr.grupoHorasExamen ghe", "alumno")
                .filter("agr.estado", estadoEnum)
                .filter("ghe.fecha", fecha);
        return all(sql);
    }

    @Override
    public List<AlumnoGrupoRegular> allByLetraGrupoRegularAndEstados(LetraGrupoRegular letrasGruposRegular,
            AlumnoRolExamenEstadoEnum... estados) {
        Octavia sql = Octavia.query()
                .from(AlumnoGrupoRegular.class, "agr")
                .join("agr.seccionGrupoRegular sgr", "sgr.letraGrupoRegular lgr", "userRegistro cur", "agr.alumno alu")
                .join("alu.persona per")
                .in("agr.estado", estados)
                .filter("lgr.id", letrasGruposRegular);
        return all(sql);
    }

    @Override
    public List<AlumnoGrupoRegular> allBySeccionGrupoRegularAndEstados(SeccionGrupoRegular seccionGrupoRegular,
            AlumnoRolExamenEstadoEnum... estados) {
        Octavia sql = Octavia.query()
                .from(AlumnoGrupoRegular.class, "agr")
                .join("agr.seccionGrupoRegular sgr", "sgr.letraGrupoRegular lgr", "userRegistro cur", "agr.alumno alu")
                .join("alu.persona per")
                .in("agr.estado", estados)
                .filter("sgr.id", seccionGrupoRegular);
        return all(sql);
    }

    @Override
    public List<AlumnoGrupoRegular> allBySeccionGrupoRegularAndEstados(List<SeccionGrupoRegular> seccionGrupoRegular,
            AlumnoRolExamenEstadoEnum... estados) {
        Octavia sql = Octavia.query()
                .from(AlumnoGrupoRegular.class, "agr")
                .join("agr.seccionGrupoRegular sgr", "sgr.letraGrupoRegular lgr", "userRegistro cur", "agr.alumno alu", "alu.persona per")
                .in("agr.estado", estados)
                .in("sgr.id", seccionGrupoRegular);
        return all(sql);
    }

    @Override
    public void updateEstadoExclusion(AlumnoGrupoRegular alumnoGrupoRegular) {
        alumnoGrupoRegular.setEstadoEnum(AlumnoRolExamenEstadoEnum.EXC);
        Octavia octavia = Octavia.update(AlumnoGrupoRegular.class);
        octavia.set(alumnoGrupoRegular, "estado");
        octavia.set(alumnoGrupoRegular, "usuarioExclusion");
        octavia.set(alumnoGrupoRegular, "fechaExclusion");
        this.update(octavia);
    }

    @Override
    public void updateEstado(AlumnoGrupoRegular alumnoGrupoRegular) {
        Octavia octavia = Octavia.update(AlumnoGrupoRegular.class);
        octavia.set(alumnoGrupoRegular, "estado");
        this.update(octavia);
    }

    @Override
    public void updateEstado(List<Alumno> alumnos, AlumnoRolExamenEstadoEnum estadoEnum, Usuario usuario, Date fecha) {
        List<Long> alumnosIds = alumnos.stream().map(x -> x.getId()).collect(Collectors.toList());

        StringBuilder strb = new StringBuilder();
        strb.append(" UPDATE AlumnoGrupoRegular agr ");
        strb.append(" SET agr.estado=:ESTADO");
        strb.append(" ,agr.usuarioExclusion=:USUARIO_EXCLUSION  ");
        strb.append(" ,agr.fechaExclusion=:FECHA_EXCLUSION ");
        strb.append(" where ");
        strb.append(" agr.alumno.id in (:ALUMNOS) ");

        Query query = getCurrentSession().createQuery(strb.toString());
        query.setParameter("ESTADO", estadoEnum.name());
        query.setParameter("USUARIO_EXCLUSION", usuario);
        query.setParameter("FECHA_EXCLUSION", fecha);
        query.setParameterList("ALUMNOS", alumnosIds);
        query.executeUpdate();
    }

    @Override
    public Map<Long, Integer> countByLetrasGruposRegulares(List<LetraGrupoRegular> letraGrupoRegulars, AlumnoRolExamenEstadoEnum... estados) {
        Octavia sql = Octavia.query()
                .select("lgr.id", "count(agr)")
                .from(AlumnoGrupoRegular.class, "agr")
                .join("seccionGrupoRegular sgr", "sgr.letraGrupoRegular lgr")
                .in("agr.estado", estados)
                .in("lgr.id", letraGrupoRegulars)
                .groupBy("lgr.id");

        List<Object[]> resultado = sql.all(getCurrentSession());
        Map<Long, Integer> result = new HashMap<>();
        for (Object[] objects : resultado) {
            result.put(TypesUtil.getLong(objects[0]), TypesUtil.getInt(objects[1]));
        }
        return result;
    }

    public Map<Long, Integer> countBySeccionesGruposRegulares(List<SeccionGrupoRegular> seccionesGrupoRegular, AlumnoRolExamenEstadoEnum... estados) {
        Octavia sql = Octavia.query()
                .select("sgr.id", "count(agr)")
                .from(AlumnoGrupoRegular.class, "agr")
                .join("seccionGrupoRegular sgr", "sgr.letraGrupoRegular lgr")
                .in("agr.estado", estados)
                .in("sgr.id", seccionesGrupoRegular)
                .groupBy("sgr.id");

        List<Object[]> resultado = sql.all(getCurrentSession());
        Map<Long, Integer> result = new HashMap<>();
        for (Object[] objects : resultado) {
            result.put(TypesUtil.getLong(objects[0]), TypesUtil.getInt(objects[1]));
        }
        return result;
    }

    @Override
    public void deleteByLetraGrupoRegular(LetraGrupoRegular letraGrupoRegular) {
        StringBuilder strb = new StringBuilder();
        strb.append(" delete from  AlumnoGrupoRegular agr where agr.seccionGrupoRegular.id in ");
        strb.append(" (Select ssgr.id from SeccionGrupoRegular ssgr where  ssgr.letraGrupoRegular.id=:LETRA_ID) ");

        Query query = getCurrentSession().createQuery(strb.toString());
        query.setParameter("LETRA_ID", letraGrupoRegular.getId());
        query.executeUpdate();
    }

    @Override
    public List<AlumnoGrupoRegular> allByDynatableAndLetraGrupoRegular(DynatableFilter filter, LetraGrupoRegular letraGrupoRegular) {
        DynatableSql sql = new DynatableSql(filter)
                .from(AlumnoGrupoRegular.class, "agr")
                .join("alumno alu", "alu.persona per", "seccionGrupoRegular sgr", "sgr.letraGrupoRegular lgr")
                .join("userRegistro ur", "ur.persona urPer")
                .filter("lgr.id", letraGrupoRegular)
                .searchFields("alu.codigo");

        sql.searchComplexField("concat(coalesce(per.paterno,''),' ',coalesce(per.materno,''),' ',coalesce(per.nombres,''))")
                .searchComplexField("concat(coalesce(per.nombres,''),' ',coalesce(per.paterno,''),' ',coalesce(per.materno,''))");

        return all(sql);
    }

    @Override
    public void createForSeccionGrupoRegular(
            List<AlumnoGrupoRegular> alumnosGpoRegular,
            SeccionGrupoRegular seccionGpoRegular,
            Date fecha,
            Usuario user) {

        if (alumnosGpoRegular.isEmpty()) {
            return;
        }

        List<Long> ids = alumnosGpoRegular.stream().map(x -> x.getAlumno().getId()).collect(Collectors.toList());

        StringBuilder sql = new StringBuilder();
        sql.append("insert into ").append(tb(AlumnoGrupoRegular.class));
        sql.append("  (estado,fechaRegistro,seccionGrupoRegular,alumno,userRegistro) ");
        sql.append(" select :ESTADO, :FECHA, sgr, alu, usr ");
        sql.append("   from ").append(tb(Alumno.class)).append(" alu, ");
        sql.append("        ").append(tb(SeccionGrupoRegular.class)).append(" sgr, ");
        sql.append("        ").append(tb(Usuario.class)).append(" usr ");
        sql.append("  where alu.id in (:ALUMNOS) ");
        sql.append("    and sgr.id = :SECCION ");
        sql.append("    and usr.id = :USER ");

        Query query = getCurrentSession().createQuery(sql.toString());
        query.setParameterList("ALUMNOS", ids);
        query.setParameter("SECCION", seccionGpoRegular.getId());
        query.setParameter("USER", user.getId());
        query.setParameter("ESTADO", AlumnoRolExamenEstadoEnum.ACT.name());
        query.setParameter("FECHA", fecha);

        query.executeUpdate();

    }

    private String tb(Class clazz) {
        return clazz.getSimpleName();
    }

    @Override
    public int saveAll(List<AlumnoGrupoRegular> alumnosSeccGpoReg) {
        if (alumnosSeccGpoReg.isEmpty()) {
            return 0;
        }

        long t1 = System.currentTimeMillis();
        Insecto sql = Insecto.createInsert()
                .into(AlumnoGrupoRegular.class)
                .columns("estado", "fechaRegistro", "fechaExclusion", "seccionGrupoRegular", "alumno", "userRegistro", "usuarioExclusion")
                .values(alumnosSeccGpoReg);

        Query query = getCurrentSession().createSQLQuery(sql.toString());
        int rows = query.executeUpdate();

        long t2 = System.currentTimeMillis();
        logger.info("{} AlumnoGrupoRegular's insertados en {} mseg....", rows, (t2 - t1));
        return rows;
    }

}
