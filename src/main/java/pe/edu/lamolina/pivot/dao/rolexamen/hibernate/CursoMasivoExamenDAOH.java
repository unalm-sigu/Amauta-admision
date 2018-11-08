package pe.edu.lamolina.pivot.dao.rolexamen.hibernate;

import java.util.List;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.rolexamen.CursoMasivoExamen;
import pe.edu.lamolina.model.rolexamen.RolExamenes;
import pe.edu.lamolina.pivot.dao.rolexamen.CursoMasivoExamenDAO;

@Repository
public class CursoMasivoExamenDAOH extends AbstractEasyDAO<CursoMasivoExamen> implements CursoMasivoExamenDAO {

    public CursoMasivoExamenDAOH() {
        super();
        setClazz(CursoMasivoExamen.class);
    }

    @Override
    public List<CursoMasivoExamen> allActiveByRolExamen(RolExamenes rolExamenes) {
        Octavia sql = Octavia.query()
                .from(CursoMasivoExamen.class, "cme")
                .join("rolExamen re", "curso cur", "dia dia", "hora hora")
                .filter("re.id", rolExamenes);
        return all(sql);
    }

}
