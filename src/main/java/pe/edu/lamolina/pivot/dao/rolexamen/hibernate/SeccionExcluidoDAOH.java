package pe.edu.lamolina.pivot.dao.rolexamen.hibernate;

import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import org.hibernate.Query;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.academico.Seccion;
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

    @Override
    public void deleteBySecciones(List<Seccion> secciones) {
        List<Long> seccionesIds = secciones.stream().map(x -> x.getId()).collect(Collectors.toList());
        StringBuilder sql = new StringBuilder();
        sql.append(" DELETE ").append(SeccionExcluido.class.getName()).append(" sex ")
                .append(" WHERE sex.seccion.id in :SECCIONES ");

        Query query = getCurrentSession().createQuery(sql.toString());
        query.setParameterList("SECCIONES", seccionesIds);
        query.executeUpdate();

    }

}
