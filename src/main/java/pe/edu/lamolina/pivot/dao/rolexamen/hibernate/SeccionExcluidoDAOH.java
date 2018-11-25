package pe.edu.lamolina.pivot.dao.rolexamen.hibernate;

import java.util.List;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.rolexamen.RolExamenes;
import pe.edu.lamolina.model.rolexamen.SeccionExcluido;
import pe.edu.lamolina.pivot.dao.rolexamen.SeccionExcluidoDAO;

@Repository
public class SeccionExcluidoDAOH extends AbstractEasyDAO<SeccionExcluido> implements SeccionExcluidoDAO {

    public SeccionExcluidoDAOH() {
        super();
        setClazz(SeccionExcluido.class);
    }

    @Override
    public List<SeccionExcluido> allByRolExamenes(RolExamenes rolExamenes) {
        Octavia sql = Octavia.query()
                .from(SeccionExcluido.class, "se")
                .join("rolExamenes re", "seccion sec")
                .filter("re.id", rolExamenes);
        return all(sql);
    }

}
