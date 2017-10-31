package pe.edu.lamolina.pivot.dao.general.hibernate;

import java.util.List;
import pe.albatross.zelpers.dao.AbstractDAO;
import pe.edu.lamolina.pivot.dao.general.OficinaDAO;
import pe.edu.lamolina.pivot.model.general.Oficina;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.zelpers.dao.SqlUtil;
import pe.edu.lamolina.pivot.model.general.Persona;

@Repository
public class OficinaDAOH extends AbstractDAO<Oficina> implements OficinaDAO {

    public OficinaDAOH() {
        super();
        setClazz(Oficina.class);
    }

    @Override
    public List<Oficina> allByJefe(Persona persona) {
        SqlUtil sqlUtil = SqlUtil.creaSqlUtil("ofi")
                .parents("personaJefe pj")
                .filter("pj.id", persona);
        return all(sqlUtil);
    }

    @Override
    public List<Oficina> allOficinasByName(String nombre) {
         Octavia sql = Octavia.query()
                .from(Oficina.class, "se")
                .filter("se.nombre", "like", nombre);
        return sql.all(getCurrentSession());
    }
}
