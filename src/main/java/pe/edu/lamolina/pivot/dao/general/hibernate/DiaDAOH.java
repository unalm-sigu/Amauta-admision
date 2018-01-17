package pe.edu.lamolina.pivot.dao.general.hibernate;

import java.util.List;
import pe.edu.lamolina.pivot.dao.general.DiaDAO;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.general.Dia;

@Repository
public class DiaDAOH extends AbstractEasyDAO<Dia> implements DiaDAO {

    public DiaDAOH() {
        super();
        setClazz(Dia.class);
    }

    @Override
    public List<Dia> allDia() {
        Octavia sql = Octavia.query()
                .from(Dia.class, "di")
                .orderBy("di.numeroDia");

        return all(sql);
    }
}
