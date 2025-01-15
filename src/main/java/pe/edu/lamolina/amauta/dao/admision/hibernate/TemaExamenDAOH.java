package pe.edu.lamolina.amauta.dao.admision.hibernate;

import java.util.List;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.amauta.dao.admision.TemaExamenDAO;
import pe.edu.lamolina.model.calificacion.TemaExamen;

@Repository
public class TemaExamenDAOH extends AbstractEasyDAO<TemaExamen> implements TemaExamenDAO {

    public TemaExamenDAOH() {
        super();
        setClazz(TemaExamen.class);
    }

    @Override
    public List<TemaExamen> allNotTemaSuperior() {
       Octavia sql =Octavia.query()
               .from(TemaExamen.class,"t")
               .left("temaSuperior ts")
               .isNull("ts.id");
       return all(sql);
    }

}
