package pe.edu.lamolina.amauta.dao.escalafon.hibernate;

import java.util.List;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.amauta.dao.escalafon.InvestigacionEscalafonDAO;
import pe.edu.lamolina.model.escalafon.Escalafon;
import pe.edu.lamolina.model.escalafon.InvestigacionEscalafon;

@Repository
public class InvestigacionEscalafonDAOH extends AbstractEasyDAO<InvestigacionEscalafon> implements InvestigacionEscalafonDAO {

    public InvestigacionEscalafonDAOH() {
        super();
        setClazz(InvestigacionEscalafon.class);
    }

    @Override
    public List<InvestigacionEscalafon> allByEscalafon(Escalafon escalafon) {
        Octavia sql = new Octavia()
                .from(InvestigacionEscalafon.class, "ie")
                .join("escalafon es", "area pa")
                .filter("es.id", escalafon)
                .orderBy("ie.id desc");

        return all(sql);
    }
}
