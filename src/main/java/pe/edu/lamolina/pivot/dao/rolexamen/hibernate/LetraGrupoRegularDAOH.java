package pe.edu.lamolina.pivot.dao.rolexamen.hibernate;

import org.springframework.stereotype.Repository;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.rolexamen.LetraGrupoRegular;
import pe.edu.lamolina.pivot.dao.rolexamen.LetraGrupoRegularDAO;

@Repository
public class LetraGrupoRegularDAOH extends AbstractEasyDAO<LetraGrupoRegular> implements LetraGrupoRegularDAO {

    public LetraGrupoRegularDAOH() {
        super();
        setClazz(LetraGrupoRegular.class);
    }
}
