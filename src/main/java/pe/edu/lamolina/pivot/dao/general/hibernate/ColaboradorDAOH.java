package pe.edu.lamolina.pivot.dao.general.hibernate;

import java.util.List;
import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.criterion.Restrictions;
import pe.albatross.zelpers.dao.AbstractDAO;
import pe.edu.lamolina.pivot.dao.general.ColaboradorDAO;
import pe.edu.lamolina.pivot.model.general.Colaborador;
import org.springframework.stereotype.Repository;
import pe.edu.lamolina.pivot.model.general.Oficina;

@Repository
public class ColaboradorDAOH extends AbstractDAO<Colaborador> implements ColaboradorDAO {

    public ColaboradorDAOH() {
        super();
        setClazz(Colaborador.class);
    }

    @Override
    public List<Colaborador> allColaborador(List<Oficina> oficinas) {
        Criteria criteria = getCurrentSession().createCriteria(Colaborador.class);
        criteria.add(Restrictions.in("oficina", oficinas));
        criteria.setFetchMode("cargo", FetchMode.JOIN);
        criteria.setFetchMode("persona", FetchMode.JOIN);
        return criteria.list();
    }
}
