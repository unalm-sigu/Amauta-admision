package pe.edu.lamolina.pivot.dao.rolexamen.hibernate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.hibernate.Query;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.model.enums.AlumnoRolExamenEstadoEnum;
import pe.edu.lamolina.model.rolexamen.AlumnoGrupoEspecial;
import pe.edu.lamolina.model.rolexamen.LetraGrupoRegular;
import pe.edu.lamolina.model.rolexamen.RolExamenes;
import pe.edu.lamolina.model.rolexamen.SeccionGrupoEspecial;
import pe.edu.lamolina.pivot.dao.rolexamen.AlumnoGrupoEspecialDAO;

@Repository
public class AlumnoGrupoEspecialDAOH extends AbstractEasyDAO<AlumnoGrupoEspecial> implements AlumnoGrupoEspecialDAO {

    public AlumnoGrupoEspecialDAOH() {
        super();
        setClazz(AlumnoGrupoEspecial.class);
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
    public void deleteByRolExamenes(RolExamenes rolExamenes) {
        StringBuilder strb = new StringBuilder();
        strb.append(" delete from  AlumnoGrupoEspecial age where age.seccionGrupoEspecial.id in ");
        strb.append(" (Select ssge.id from SeccionGrupoEspecial ssge where  ssge.rolExamenes.id=:ROL_EXAMENES) ");

        Query query = getCurrentSession().createQuery(strb.toString());
        query.setParameter("ROL_EXAMENES", rolExamenes.getId());
        query.executeUpdate();
    }

}
