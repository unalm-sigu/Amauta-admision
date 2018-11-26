package pe.edu.lamolina.pivot.dao.rolexamen.hibernate;

import java.util.List;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.academico.Seccion;
import pe.edu.lamolina.model.rolexamen.LetraGrupoRegular;
import pe.edu.lamolina.model.rolexamen.RolExamenes;
import pe.edu.lamolina.pivot.dao.rolexamen.LetraGrupoRegularDAO;

@Repository
public class LetraGrupoRegularDAOH extends AbstractEasyDAO<LetraGrupoRegular> implements LetraGrupoRegularDAO {

    public LetraGrupoRegularDAOH() {
        super();
        setClazz(LetraGrupoRegular.class);
    }

    @Override
    public LetraGrupoRegular find(long id) {
        Octavia sql = Octavia.query()
                .from(LetraGrupoRegular.class, "lgr")
                .join("rolExamenes re", "userRegistro ur")
                .leftJoin("dia d", "hora h")
                .left("ur.persona urPer")
                .filter("lgr.id", id);
        return find(sql);
    }

    @Override
    public List<LetraGrupoRegular> allByRolExamenes(RolExamenes rolExamenes) {
        Octavia sql = Octavia.query()
                .from(LetraGrupoRegular.class, "lgr")
                .join("rolExamenes re", "userRegistro ur")
                .leftJoin("dia d", "hora h")
                .left("ur.persona urPer")
                .filter("re.id", rolExamenes);
        return all(sql);
    }

}
