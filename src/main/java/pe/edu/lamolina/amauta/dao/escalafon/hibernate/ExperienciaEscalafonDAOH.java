package pe.edu.lamolina.amauta.dao.escalafon.hibernate;

import java.util.List;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.amauta.dao.escalafon.ExperienciaEscalafonDAO;
import pe.edu.lamolina.model.escalafon.Escalafon;
import pe.edu.lamolina.model.escalafon.ExperienciaEscalafon;

@Repository
public class ExperienciaEscalafonDAOH extends AbstractEasyDAO<ExperienciaEscalafon> implements ExperienciaEscalafonDAO {

    public ExperienciaEscalafonDAOH() {
        super();
        setClazz(ExperienciaEscalafon.class);
    }

    @Override
    public List<ExperienciaEscalafon> allByEscalafon(Escalafon escalafon) {
        Octavia sql = new Octavia()
                .from(ExperienciaEscalafon.class, "ee")
                .join("escalafon es")
                .leftJoin("universidad uni")
                .filter("es.id", escalafon)
                .orderBy("ee.id desc");

        return all(sql);
    }
}
