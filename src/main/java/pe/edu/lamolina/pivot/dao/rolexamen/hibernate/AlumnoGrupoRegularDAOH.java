package pe.edu.lamolina.pivot.dao.rolexamen.hibernate;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.hibernate.Query;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.enums.AlumnoRolExamenEstadoEnum;
import pe.edu.lamolina.model.rolexamen.AlumnoGrupoRegular;
import pe.edu.lamolina.model.rolexamen.LetraGrupoRegular;
import pe.edu.lamolina.model.rolexamen.SeccionGrupoRegular;
import pe.edu.lamolina.model.seguridad.Usuario;
import pe.edu.lamolina.pivot.dao.rolexamen.AlumnoGrupoRegularDAO;

@Repository
public class AlumnoGrupoRegularDAOH extends AbstractEasyDAO<AlumnoGrupoRegular> implements AlumnoGrupoRegularDAO {

    public AlumnoGrupoRegularDAOH() {
        super();
        setClazz(AlumnoGrupoRegular.class);
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
    public List<AlumnoGrupoRegular> allByLetraGrupoRegularAndEstados(LetraGrupoRegular letrasGruposRegular,
            List<AlumnoRolExamenEstadoEnum> estados) {
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
    public void updateEstado(AlumnoGrupoRegular alumnoGrupoRegular) {
        Octavia octavia = Octavia.update(AlumnoGrupoRegular.class);
        octavia.set(alumnoGrupoRegular, "estado");
        octavia.set(alumnoGrupoRegular, "usuarioExclusion");
        octavia.set(alumnoGrupoRegular, "fechaExclusion");
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

    @Override
    public void deleteByLetraGrupoRegular(LetraGrupoRegular letraGrupoRegular) {
        StringBuilder strb = new StringBuilder();
        strb.append(" delete from  AlumnoGrupoRegular agr where agr.seccionGrupoRegular.id in ");
        strb.append(" (Select ssgr.id from SeccionGrupoRegular ssgr where  ssgr.letraGrupoRegular.id=:LETRA_ID) ");

        Query query = getCurrentSession().createQuery(strb.toString());
        query.setParameter("LETRA_ID", letraGrupoRegular.getId());
        query.executeUpdate();
    }

}
