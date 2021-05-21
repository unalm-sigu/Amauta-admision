package pe.edu.lamolina.amauta.dao.consejeria.hibernate;

import java.util.List;
import org.springframework.stereotype.Service;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.amauta.dao.consejeria.AgendaConsejeroDAO;
import pe.edu.lamolina.model.consejeria.AgendaConsejero;
import pe.edu.lamolina.model.consejeria.Consejero;

@Service
public class AgendaConsejeroDAOH extends AbstractEasyDAO<AgendaConsejero> implements AgendaConsejeroDAO {

    public AgendaConsejeroDAOH() {
        super();
        setClazz(AgendaConsejero.class);
    }

    @Override
    public List<AgendaConsejero> allByConsejero(Consejero consejero) {
       Octavia sql = new Octavia()
               .from(AgendaConsejero.class, "ac")
               .join("consejero con", "hora hor")
               .filter("con.id", consejero);
       
       return all(sql);
    }

}
