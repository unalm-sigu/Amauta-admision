package pe.edu.lamolina.pivot.dao.mensajeria.hibernate;

import java.util.List;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableSql;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.academico.GrupoAlumno;
import pe.edu.lamolina.pivot.dao.mensajeria.GrupoAlumnoDAO;

@Repository
public class GrupoAlumnoDAOH extends AbstractEasyDAO<GrupoAlumno> implements GrupoAlumnoDAO {

    public GrupoAlumnoDAOH() {
        super();
        setClazz(GrupoAlumno.class);
    }

    @Override
    public List<GrupoAlumno> allByDynatble(DynatableFilter filter) {
         DynatableSql sql = new DynatableSql(filter)
                .from(GrupoAlumno.class, "ga")
                .searchFields("ga.codigo", "ga.nombre")
                .orderBy("ga.id desc");
        return sql.all(getCurrentSession());
    }
}
