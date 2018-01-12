package pe.edu.lamolina.pivot.dao.horario.hibernate;

import java.util.List;
import org.hibernate.Query;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.edu.lamolina.model.academico.CursoCachimbos;
import pe.edu.lamolina.model.horario.SeccionCursoCachimbos;
import pe.edu.lamolina.pivot.dao.horario.SeccionCursoCachimbosDAO;

@Repository
public class SeccionCursoCachimbosDAOH extends AbstractEasyDAO<SeccionCursoCachimbos> implements SeccionCursoCachimbosDAO {

    public SeccionCursoCachimbosDAOH() {
        super();
        setClazz(SeccionCursoCachimbos.class);
    }

    @Override
    public List<SeccionCursoCachimbos> allByCursoCachimbos(CursoCachimbos curso) {
        Octavia sql = Octavia.query()
                .from(SeccionCursoCachimbos.class, "hs")
                .join("seccion se", "cursoCachimbos ch", "userCreacion uc")
                .filter("ch.id", curso);
        return all(sql);
    }

    @Override
    public void deleteByCursoCachimbos(CursoCachimbos curso) {

        StringBuilder sql = new StringBuilder();
        sql.append(" delete from SeccionCursoCachimbos ");
        sql.append(" where  cursoCachimbos.id = :CURSO");

        Query query = getCurrentSession().createQuery(sql.toString());
        query.setLong("CURSO", curso.getId());
        query.executeUpdate();

    }

    @Override
    public List<SeccionCursoCachimbos> allByCursoCachimbos(List<CursoCachimbos> cursoCachimbos) {
        Octavia sql = Octavia.query()
                .from(SeccionCursoCachimbos.class, "hs")
                .join("seccion se", "cursoCachimbos ch")
                .in("ch.id", cursoCachimbos);
        return all(sql);
    }

}
