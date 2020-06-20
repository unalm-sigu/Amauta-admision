package pe.edu.lamolina.amauta.dao.escalafon.hibernate;

import java.util.List;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.amauta.dao.escalafon.DistincionEscalfonDAO;
import pe.edu.lamolina.model.escalafon.DistincionEscalafon;
import pe.edu.lamolina.model.escalafon.Escalafon;

@Repository
public class DistincionEscalfonDAOH extends AbstractEasyDAO<DistincionEscalafon> implements DistincionEscalfonDAO {

    public DistincionEscalfonDAOH() {
        super();
        setClazz(DistincionEscalafon.class);
    }

    @Override
    public List<DistincionEscalafon> allByEscalafon(Escalafon escalafon) {
        Octavia sql = new Octavia()
                .from(DistincionEscalafon.class, "ie")
                .join("escalafon es", "pais pa")
                .filter("es.id", escalafon)
                .orderBy("ie.id desc");

        return all(sql);
    }
}
