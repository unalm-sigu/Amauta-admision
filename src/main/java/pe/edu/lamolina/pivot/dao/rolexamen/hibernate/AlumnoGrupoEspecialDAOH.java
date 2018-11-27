package pe.edu.lamolina.pivot.dao.rolexamen.hibernate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.model.enums.AlumnoRolExamenEstadoEnum;
import pe.edu.lamolina.model.rolexamen.AlumnoGrupoEspecial;
import pe.edu.lamolina.model.rolexamen.AlumnoGrupoRegular;
import pe.edu.lamolina.model.rolexamen.LetraGrupoRegular;
import pe.edu.lamolina.model.rolexamen.SeccionGrupoEspecial;
import pe.edu.lamolina.model.rolexamen.SeccionGrupoRegular;
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

}
