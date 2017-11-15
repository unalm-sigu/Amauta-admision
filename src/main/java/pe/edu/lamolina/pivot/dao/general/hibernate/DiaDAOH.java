package pe.edu.lamolina.pivot.dao.general.hibernate;

import java.util.List;
import pe.albatross.zelpers.dao.AbstractDAO;
import pe.edu.lamolina.pivot.dao.general.DiaDAO;
import pe.edu.lamolina.pivot.model.general.Dia;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;

@Repository
public class DiaDAOH extends AbstractDAO<Dia> implements DiaDAO {

    public DiaDAOH() {
        super();
        setClazz(Dia.class);
    }

    @Override
    public List<Dia> allDia() {
        Octavia sql = Octavia.query()
                .from(Dia.class, "di")
                .orderBy("di.numeroDia");
        return sql.all(getCurrentSession());
    }
}
