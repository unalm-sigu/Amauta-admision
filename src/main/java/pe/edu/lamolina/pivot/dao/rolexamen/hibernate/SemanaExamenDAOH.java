package pe.edu.lamolina.pivot.dao.rolexamen.hibernate;

import java.util.List;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.rolexamen.RolExamenes;
import pe.edu.lamolina.model.rolexamen.SemanaExamen;
import pe.edu.lamolina.pivot.dao.rolexamen.SemanaExamenDAO;

@Repository
public class SemanaExamenDAOH extends AbstractEasyDAO<SemanaExamen> implements SemanaExamenDAO {

    public SemanaExamenDAOH() {
        super();
        setClazz(SemanaExamen.class);
    }

    @Override
    public SemanaExamen find(long id) {
        Octavia sql = Octavia.query()
                .from(SemanaExamen.class, "se")
                .join("horaInicio hi", "horaFin hf", "rolExamenes rex")
                .filter("se.id", id);
        return find(sql);
    }

    @Override
    public List<SemanaExamen> allByRolExamenes(RolExamenes rolExamenes) {
        Octavia sql = Octavia.query()
                .from(SemanaExamen.class, "se")
                .join("horaInicio hi", "horaFin hf", "rolExamenes rex")
                .filter("rex.id", rolExamenes);
        return all(sql);
    }

}
