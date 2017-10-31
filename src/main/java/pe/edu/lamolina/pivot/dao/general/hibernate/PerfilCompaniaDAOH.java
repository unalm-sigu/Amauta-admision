package pe.edu.lamolina.pivot.dao.general.hibernate;

import java.util.List;
import org.hibernate.Query;
import pe.albatross.zelpers.dao.AbstractDAO;
import pe.edu.lamolina.pivot.dao.general.PerfilCompaniaDAO;
import pe.edu.lamolina.pivot.model.general.PerfilCompania;
import org.springframework.stereotype.Repository;

@Repository
public class PerfilCompaniaDAOH extends AbstractDAO<PerfilCompania> implements PerfilCompaniaDAO {

    public PerfilCompaniaDAOH() {
        super();
        setClazz(PerfilCompania.class);
    }

    @Override
    public List<PerfilCompania> allByNombre(String nombre) {
        nombre = "%" + nombre.replaceAll(" ", "%") + "%";
        StringBuilder sql = new StringBuilder();
        sql.append("  from ").append(PerfilCompania.class.getName()).append(" as pc ");
        sql.append("  where pc.nombreDocumento like :BUSQUEDA ");
        sql.append("  order by pc.nombreDocumento ");

        Query query = getCurrentSession().createQuery(sql.toString());
        query.setString("BUSQUEDA", nombre);
        query.setMaxResults(15);

        return query.list();
    }
}
