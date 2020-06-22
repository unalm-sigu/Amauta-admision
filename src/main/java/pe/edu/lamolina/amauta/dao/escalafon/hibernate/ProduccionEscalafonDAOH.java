package pe.edu.lamolina.amauta.dao.escalafon.hibernate;

import java.util.List;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.amauta.dao.escalafon.ProduccionEscalafonDAO;
import pe.edu.lamolina.model.escalafon.Escalafon;
import pe.edu.lamolina.model.escalafon.ProduccionEscalafon;

@Repository
public class ProduccionEscalafonDAOH extends AbstractEasyDAO<ProduccionEscalafon> implements ProduccionEscalafonDAO {

    public ProduccionEscalafonDAOH() {
        super();
        setClazz(ProduccionEscalafon.class);
    }

    @Override
    public List<ProduccionEscalafon> allByEscalafon(Escalafon escalafon) {
        Octavia sql = new Octavia()
                .from(ProduccionEscalafon.class, "pe")
                .join("escalafon es")
                .filter("es.id", escalafon)
                .orderBy("pe.id desc");

        return all(sql);
    }
}
