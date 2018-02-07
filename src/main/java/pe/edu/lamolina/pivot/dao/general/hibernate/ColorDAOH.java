package pe.edu.lamolina.pivot.dao.general.hibernate;

import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.general.Color;
import pe.edu.lamolina.pivot.dao.general.ColorDAO;

@Repository
public class ColorDAOH extends AbstractEasyDAO<Color> implements ColorDAO {

    public ColorDAOH() {
        super();
        setClazz(Color.class);
    }

    @Override
    public Color findLastColor() {

        Octavia sql = Octavia.query().
                from(Color.class, "co").
                orderBy("co.ordenEvento").
                limit(1);
        return (Color) sql.find(getCurrentSession());

    }

}
